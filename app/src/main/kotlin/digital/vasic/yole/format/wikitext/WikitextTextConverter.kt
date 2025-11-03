/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.wikitext

import android.content.Context
import androidx.arch.core.util.Function
import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.TextConverterBase
import digital.vasic.yole.model.AppSettings
import digital.vasic.opoc.format.GsTextUtils
import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Make use of MarkdownConverter by transpiling Wikitext syntax to Markdown
 */
class WikitextTextConverter : TextConverterBase() {
    /**
     * First, convert Wikitext to regular Yole markdown. Then, calls the regular converter.
     *
     * @param markup    Markup text
     * @param context   Android Context
     * @param lightMode True if the light theme is to apply.
     * @param enableLineNumbers
     * @param file      The file to convert.
     * @return HTML text
     */
    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        val contentWithoutHeader = markup.replaceFirst(WikitextSyntaxHighlighter.ZIMHEADER.toString().toRegex(), "")
        val markdownContent = StringBuilder()

        for (line in contentWithoutHeader.split("\\r\\n|\\r|\\n".toRegex())) {
            val markdownEquivalentLine = getMarkdownEquivalentLine(context, file, line, lightMode)
            markdownContent.append(markdownEquivalentLine)
            markdownContent.append("  ") // line breaks must be made explicit in markdown by two spaces
            markdownContent.append(String.format("%n"))
        }

        return FormatRegistry.CONVERTER_MARKDOWN.convertMarkup(markdownContent.toString(), context, lightMode, enableLineNumbers, file)
    }

    private fun getMarkdownEquivalentLine(context: Context, file: File, wikitextLine: String, isExportInLightMode: Boolean): String {
        val currentLine = AtomicReference(wikitextLine)

        // Headings
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.HEADING, ::convertHeading)

        // bold syntax is the same as for markdown
        replaceAllMatchesInLinePartially(currentLine, WikitextSyntaxHighlighter.ITALICS, "^/+|/+$", "*")
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.HIGHLIGHTED) { match -> convertHighlighted(match, isExportInLightMode) }
        // strikethrough syntax is the same as for markdown

        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.PREFORMATTED_INLINE) { "`$1`" }
        replaceAllMatchesInLine(currentLine, Pattern.compile("^'''$")) { "```" }  // preformatted multiline

        // unordered list syntax is compatible with markdown
        replaceAllMatchesInLinePartially(currentLine, WikitextSyntaxHighlighter.LIST_ORDERED, "[0-9a-zA-Z]+\\.", "1.")    // why does this work?
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.CHECKLIST, ::convertChecklist)

        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.SUPERSCRIPT) { fullMatch ->
            String.format("<sup>%s</sup>", fullMatch.replace("^\\^\\{|\\}$".toRegex(), ""))
        }
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.SUBSCRIPT) { fullMatch ->
            String.format("<sub>%s</sub>", fullMatch.replace("^_\\{|\\}$".toRegex(), ""))
        }
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.LINK) { fullMatch -> convertLink(fullMatch, context, file) }
        replaceAllMatchesInLine(currentLine, WikitextSyntaxHighlighter.IMAGE) { fullMatch -> convertImage(file, context, fullMatch) }

        return currentLine.getAndSet("")
    }

    private fun replaceAllMatchesInLinePartially(
        currentLine: AtomicReference<String>,
        wikitextPattern: Pattern,
        matchPartToBeReplaced: String,
        replacementForMatchPart: String
    ) {
        replaceAllMatchesInLine(currentLine, wikitextPattern) { fullMatch ->
            fullMatch.replace(matchPartToBeReplaced.toRegex(), replacementForMatchPart)
        }
    }

    private fun replaceAllMatchesInLine(
        currentLine: AtomicReference<String>,
        wikitextPattern: Pattern,
        replaceMatchWithMarkdown: Function<String, String>
    ) {
        val matcher = wikitextPattern.matcher(currentLine.get())
        val replacedLine = StringBuffer()
        while (matcher.find()) {
            val fullMatch = matcher.group()
            val replacementForMatch = replaceMatchWithMarkdown.apply(fullMatch)
            matcher.appendReplacement(replacedLine, replacementForMatch)
        }
        matcher.appendTail(replacedLine)
        currentLine.set(replacedLine.toString())
    }

    private fun convertHeading(group: String): String {
        // Header level 1 has 6 equal signs (=)x6; while MD's top level is one hash (#)
        var equalSignsCount = 0
        while (group[equalSignsCount] == '=')
            equalSignsCount++

        // Maximum header level is 5, and has two equal signs
        val markdownLevel = 7 - minOf(6, equalSignsCount)

        return String.format(
            "%s %s",
            GsTextUtils.repeatChars('#', markdownLevel),
            group.replace("^=+\\s*|\\s*=+$".toRegex(), "")
        )
    }

    private fun convertHighlighted(fullMatch: String, isExportInLightMode: Boolean): String {
        val content = fullMatch.substring(2, fullMatch.length - 2)
        return "<span style=\"background-color: " + (if (isExportInLightMode) "#ffff00" else "#FFA062") + "\">" + content + "</span>"
    }

    private fun convertChecklist(fullMatch: String): String {
        // TODO: convert to more than two checkstates
        // TODO: use a single global regex for zim checkbox expression
        val matcher = Pattern.compile("\\[([ *x><])]").matcher(fullMatch)
        matcher.find()
        val checkboxContent = matcher.group(1)
        return if ("*" == checkboxContent) {
            matcher.replaceFirst("- [x]")
        } else {
            matcher.replaceFirst("- [ ]")
        }
    }

    private fun convertLink(group: String, context: Context, file: File): String {
        val settings = AppSettings.get(context)
        val notebookDir = settings.notebookDirectory
        val resolver = WikitextLinkResolver.resolve(group, notebookDir, file, settings.isWikitextDynamicNotebookRootEnabled)

        val markdownLink = if (resolver.isWebLink()) {
            resolver.resolvedLink?.replace(" ", "%20") ?: ""
        } else {
            "file://${resolver.resolvedLink}"
        }

        val linkDescription = (resolver.linkDescription ?: resolver.wikitextPath)?.replace("\\+".toRegex(), "&#43;") ?: ""

        return String.format("[%s](%s)", linkDescription, markdownLink)
    }

    private fun convertImage(file: File, context: Context, fullMatch: String): String {
        val imagePathFromPageFolder = fullMatch.substring(2, fullMatch.length - 2)
        val currentPageFileName = file.name
        val currentPageFolderName = currentPageFileName.replaceFirst(".txt$".toRegex(), "")
        var markdownPathToImage = FilenameUtils.concat(currentPageFolderName, imagePathFromPageFolder)

        // Zim may insert in the image link, after a '?' character, the 'id', 'width',
        // 'height', 'type', and 'href' tags, separating them with a '&' character, so
        // you may not want to use '?' and '&' as directory or file name:
        // https://github.com/zim-desktop-wiki/zim-desktop-wiki/blob/c88cf3cb53896bf272e87704826b77e82eddb3ef/zim/formats/__init__.py#L903
        val pos = markdownPathToImage.indexOf("?")
        if (pos != -1) {
            val image = markdownPathToImage.substring(0, pos)
            val options = markdownPathToImage.substring(pos + 1).split("&")
            var link: String? = null // <a href="link"></a> or [![name](image)](link)
            val attributes = StringBuilder() // <img id="" width="" height="" />
            // The 'type' tag is for backward compatibility of image generators before
            // Zim version 0.70.  Here, it probably may be ignored:
            // https://github.com/zim-desktop-wiki/zim-desktop-wiki/blob/c88cf3cb53896bf272e87704826b77e82eddb3ef/zim/formats/wiki.py#586
            val tags = Pattern.compile("(id|width|height|href)=(.+)", Pattern.CASE_INSENSITIVE)
            for (item in options) {
                val data = tags.matcher(item)
                if (data.matches()) {
                    val key = data.group(1)?.lowercase() ?: ""
                    var value = data.group(2) ?: ""
                    try {
                        value = URLDecoder.decode(value, "UTF-8")
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    if (key == "href") {
                        link = value
                    } else {
                        attributes.append(String.format("%s=\"%s\" ", key, value))
                    }
                }
            }
            var html = String.format("<img src=\"%s\" alt=\"%s\" %s/>", image, currentPageFileName, attributes)
            if (link != null) {
                val settings = AppSettings.get(context)
                val notebookDir = settings.notebookDirectory
                link = WikitextLinkResolver.resolveAttachmentPath(link, notebookDir, file, settings.isWikitextDynamicNotebookRootEnabled)
                html = String.format("<a href=\"%s\">%s</a>", link, html)
            }
            return html
        }

        return String.format("![%s](%s)", currentPageFileName, markdownPathToImage)
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        if (ext == ".txt") {
            try {
                BufferedReader(FileReader(file)).use { reader ->
                    return WikitextSyntaxHighlighter.ZIMHEADER_CONTENT_TYPE_ONLY.matcher(reader.readLine()).find()
                }
            } catch (ignored: Exception) {
            }
        }
        return listOf(".wikitext").contains(ext)
    }

    /*
    public static boolean isWikitextFile(String filename, Document document) {
        return filename.endsWith(".txt") && containsZimWikiHeader(document);
    }

    private static boolean containsZimWikiHeader(Document document) {
        Pattern headerPattern = ZimWikiHighlighterPattern.ZIMHEADER.pattern;
        Matcher headerMatcher = headerPattern.matcher(document.getContent());
        return headerMatcher.find();
    }*/
}
