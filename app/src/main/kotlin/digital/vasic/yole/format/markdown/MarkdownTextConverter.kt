/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.markdown

import android.content.Context
import android.text.TextUtils
import com.vladsch.flexmark.ext.admonition.AdmonitionExtension
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.emoji.EmojiExtension
import com.vladsch.flexmark.ext.emoji.EmojiImageType
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.ext.gitlab.GitLabExtension
import com.vladsch.flexmark.ext.ins.InsExtension
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterExtension
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.SimTocExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.ext.toc.internal.TocOptions
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension
import com.vladsch.flexmark.html.AttributeProvider
import com.vladsch.flexmark.html.AttributeProviderFactory
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.renderer.AttributablePart
import com.vladsch.flexmark.html.renderer.LinkResolverContext
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.superscript.SuperscriptExtension
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.builder.Extension
import com.vladsch.flexmark.util.html.Attributes
import com.vladsch.flexmark.util.options.MutableDataHolder
import com.vladsch.flexmark.util.options.MutableDataSet
import digital.vasic.yole.R
import digital.vasic.yole.format.TextConverterBase
import digital.vasic.yole.model.AppSettings
import digital.vasic.opoc.util.GsContextUtils
import other.com.vladsch.flexmark.ext.katex.FlexmarkKatexExtension
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("unchecked", "WeakerAccess")
open class MarkdownTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        val appSettings = AppSettings.get(context)
        var converted: String
        var onLoadJs = ""
        var head = ""
        val options = MutableDataSet()

        options.set(Parser.EXTENSIONS, flexmarkExtensions)
        options.set(Parser.SPACE_IN_LINK_URLS, true) // Allow links like [this](some filename with spaces.md)

        // options.set(HtmlRenderer.SOFT_BREAK, "<br />\n"); // Add linefeed to HTML break

        options.set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.UNICODE_ONLY) // Use unicode (OS/browser images)

        // GitLab extension
        options.set(GitLabExtension.RENDER_BLOCK_MATH, false)

        // GFM table parsing
        options.set(TablesExtension.WITH_CAPTION, false)
            .set(TablesExtension.COLUMN_SPANS, true)
            .set(TablesExtension.MIN_HEADER_ROWS, 0)
            .set(TablesExtension.MAX_HEADER_ROWS, 1)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, false)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(WikiLinkExtension.LINK_ESCAPE_CHARS, "")
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)

        // Add id to headers
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true)
            .set(HtmlRenderer.HEADER_ID_GENERATOR_RESOLVE_DUPES, true)
            .set(AnchorLinkExtension.ANCHORLINKS_SET_ID, false)
            .set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "header_no_underline")

        head += CSS_BODY
        // Prepare head and javascript calls
        head += CSS_HEADER_UNDERLINE + CSS_H1_H2_UNDERLINE + CSS_BLOCKQUOTE_VERTICAL_LINE + CSS_GITLAB_VIDEO_CAPTION + CSS_LIST_TASK_NO_BULLET + CSS_LINK_SOFT_WRAP_AUTOBREAK_LINES

        // Presentations
        val enablePresentationBeamer = markup.contains("\nclass:beamer") || markup.contains("\nclass: beamer")
        if (enablePresentationBeamer) {
            head += CSS_PRESENTATION_BEAMER
        }

        // Front matter
        var fmaText = ""
        val fmaAllowedAttributes = appSettings.markdownShownYamlFrontMatterKeys
        var fma: Map<String, List<String>> = emptyMap()
        var markupProcessed = markup

        if (!enablePresentationBeamer && markup.startsWith("---")) {
            val hasTokens = YAML_FRONTMATTER_TOKEN_PATTERN.matcher(markup)
            if (fmaAllowedAttributes.isNotEmpty() || hasTokens.find()) {
                // Read YAML attributes
                fma = extractYamlAttributes(markup)
            }

            // Assemble YAML front-matter block
            if (fmaAllowedAttributes.isNotEmpty()) {
                for ((attrName, _) in fma) {
                    if (!(fmaAllowedAttributes.contains(attrName) || fmaAllowedAttributes.contains("*"))) {
                        continue
                    }
                    fmaText += HTML_FRONTMATTER_ITEM_CONTAINER_S.replace("{{ attrName }}", attrName) + "{{ post.$attrName }}\n" + HTML_FRONTMATTER_ITEM_CONTAINER_E + "\n"
                }
                if (fmaText != "") {
                    head += CSS_FRONTMATTER
                }
            }
        }

        // Table of contents
        val parentFolderName = if (file.parentFile != null && !TextUtils.isEmpty(file.parentFile?.name)) file.parentFile?.name ?: "" else ""
        val isInBlogFolder = parentFolderName == "_posts" || parentFolderName == "blog" || parentFolderName == "post"
        if (!enablePresentationBeamer) {
            if (!markupProcessed.contains("[TOC]: #") && (isInBlogFolder || appSettings.isMarkdownTableOfContentsEnabled) && (markupProcessed.contains("#") || markupProcessed.contains("<h"))) {
                val tocToken = "[TOC]: # ''\n  \n"
                if (markupProcessed.startsWith("---") && !markupProcessed.contains("[TOC]")) {
                    // 1st group: match opening YAML block delimiter ('---'), optionally followed by whitespace, excluding newline
                    // 2nd group: match YAML block contents, excluding surrounding newlines
                    // 3rd group: match closing YAML block delimiter ('---' or '...'), excluding newline(s)
                    markupProcessed = markupProcessed.replaceFirst("(?ms)(^-{3}\\s*?$)\n+(.*?)\n+(^[.-]{3}\\s*?$)\n+".toRegex(), "$1\n$2\n$3\n\n$tocToken\n")
                }

                if (!markupProcessed.contains("[TOC]")) {
                    markupProcessed = tocToken + markupProcessed
                }
            }

            head += CSS_TOC_STYLE
            options.set(TocExtension.LEVELS, TocOptions.getLevels(*appSettings.markdownTableOfContentLevels))
                .set(TocExtension.TITLE, context.getString(R.string.table_of_contents))
                .set(TocExtension.DIV_CLASS, "markor-table-of-contents toc")
                .set(TocExtension.LIST_CLASS, "markor-table-of-contents-list")
                .set(TocExtension.BLANK_LINE_SPACER, false)
        }

        // Enable Math / KaTex
        if (markupProcessed.contains("$")) {
            if (appSettings.isMarkdownMathEnabled) {
                head += HTML_KATEX_INCLUDE
                head += CSS_KATEX
            } else {
                markupProcessed = markupProcessed.replace("$", "\\$")
            }
        }

        // Enable View (block) code syntax highlighting
        if (markupProcessed.contains("```")) {
            head += getViewHlPrismIncludes(if (GsContextUtils.instance.isDarkModeEnabled(context)) "-tomorrow" else "", enableLineNumbers)
            onLoadJs += "usePrismCodeBlock();"
            if (appSettings.getDocumentWrapState(file.absolutePath)) {
                onLoadJs += "wrapCodeBlockWords();"
            }
        }

        // Enable Mermaid
        if (markupProcessed.contains("```mermaid")) {
            head += HTML_MERMAID_INCLUDE +
                    "<script>mermaid.initialize({theme:'" +
                    (if (GsContextUtils.instance.isDarkModeEnabled(context)) "dark" else "default") +
                    "',logLevel:5,securityLevel:'loose'});</script>"
        }

        // Enable flexmark Admonition support
        if (markupProcessed.contains("!!!") || markupProcessed.contains("???")) {
            head += HTML_ADMONITION_INCLUDE
            head += CSS_ADMONITION
        }

        // Jekyll: Replace {{ site.baseurl }} with ..--> usually used in Jekyll blog _posts folder which is one folder below repository root, for reference to e.g. pictures in assets folder
        markupProcessed = markupProcessed.replace("{{ site.baseurl }}", "..").replace(TOKEN_SITE_DATE_JEKYLL, TOKEN_POST_TODAY_DATE)

        // Notable: They use a home brewed syntax for referencing attachments: @attachment/f.png = ../attachements/f.jpg -- https://github.com/vasic-digital/Yole/issues/1252
        markupProcessed = markupProcessed.replace("](@attachment/", "](../attachements/")

        if (appSettings.isMarkdownNewlineNewparagraphEnabled) {
            markupProcessed = markupProcessed.replace("\n", "  \n")
        }

        // Replace space in url with %20, see #1365
        markupProcessed = escapeSpacesInLink(markupProcessed)

        // Replace tokens in note with corresponding YAML attribute values
        markupProcessed = replaceTokens(markupProcessed, fma)
        if (!TextUtils.isEmpty(fmaText)) {
            fmaText = replaceTokens(fmaText, fma)
            fmaText = HTML_FRONTMATTER_CONTAINER_S + fmaText + HTML_FRONTMATTER_CONTAINER_E + "\n"
        }

        ////////////
        // Markup parsing - afterwards = HTML
        val document = flexmarkParser.parse(markupProcessed)
        converted = fmaText + flexmarkRenderer.withOptions(options).render(document)

        // After render changes: Fixes for Footnotes (converter creates footnote + <br> + ref#(click) --> remove line break)
        if (converted.contains("footnote-")) {
            converted = converted.replace("</p>\n<a href=\"#fnref-", "<a href=\"#fnref-").replace("class=\"footnote-backref\">&#8617;</a>", "class=\"footnote-backref\"> &#8617;</a></p>")
        }

        // After render changes: Presentations with Beamer
        if (enablePresentationBeamer) {
            var c = 1
            var ihtml = 0
            while (converted.indexOf("<hr />", ihtml).also { ihtml = it } >= 0 && ihtml < converted.length + 5) {
                val ins = HTML_PRESENTATION_BEAMER_SLIDE_START_DIV.replace("NO", c.toString())
                converted = converted.substring(0, ihtml) + (if (c > 1) "</div></div>" else "") + ins + converted.substring(ihtml + "<hr />".length)
                if (converted.contains(ins + "\n<h1 ")) {
                    converted = converted.replace(ins, ins.replace("slide_body", "slide_body slide_title").replace("slide'", "slide_type_title slide'"))
                }
                c++
            }
            // Final Slide
            if (c > 1) {
                converted = converted.replace(HTML_PRESENTATION_BEAMER_SLIDE_START_DIV.replace("NO", (c - 1).toString()), "</div></div> <!-- Final presentation slide -->")
            }
        }

        if (enableLineNumbers) {
            // For Prism line numbers plugin
            onLoadJs += "enableLineNumbers(); adjustLineNumbers();"
        }

        // Deliver result
        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, head)
    }

    private fun escapeSpacesInLink(markup: String): String {
        val matcher = linkPattern.matcher(markup)
        if (!matcher.find()) {
            return markup
        }
        // 1) Walk through the text till finding a link in markdown syntax
        // 2) Add all text-before-link to buffer
        // 3) Extract [title](link to somehere)
        // 4) Add [title](link%20to%20somewhere) to buffer
        // 5) Do till the end and add all text & links of original-text to buffer
        val sb = StringBuilder(markup.length + 64)
        var previousEnd = 0
        do {
            val url = matcher.group(2)
            val title = matcher.group(3)
            if (url == null) {
                return markup
            }
            sb.append(markup.substring(previousEnd, matcher.start())).append(
                String.format(
                    "[%s](%s%s)", matcher.group(1),
                    url.trim().replace(" ", "%20"),
                    title ?: ""
                )
            )
            previousEnd = matcher.end()
        } while (matcher.find())
        sb.append(markup.substring(previousEnd))

        return sb.toString()
    }

    @Suppress("StringConcatenationInsideStringBufferAppend")
    private fun getViewHlPrismIncludes(theme: String, isLineNumbersEnabled: Boolean): String {
        val sb = StringBuilder(1000)
        sb.append(CSS_PREFIX + "prism/themes/prism$theme.min.css" + CSS_POSTFIX)
        sb.append(CSS_PREFIX + "prism/prism-markor.css" + CSS_POSTFIX)
        sb.append(CSS_PREFIX + "prism/plugins/toolbar/prism-toolbar.css" + CSS_POSTFIX)

        sb.append(JS_PREFIX + "prism/prism.js" + JS_POSTFIX)
        sb.append(JS_PREFIX + "prism/components.js" + JS_POSTFIX)
        sb.append(JS_PREFIX + "prism/prism-markor.js" + JS_POSTFIX)
        sb.append(JS_PREFIX + "prism/plugins/autoloader/prism-autoloader.min.js" + JS_POSTFIX)
        sb.append(JS_PREFIX + "prism/plugins/toolbar/prism-toolbar.min.js" + JS_POSTFIX)
        sb.append(JS_PREFIX + "prism/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js" + JS_POSTFIX)

        if (isLineNumbersEnabled) {
            sb.append(CSS_PREFIX + "prism/plugins/line-numbers/prism-line-numbers-markor.css" + CSS_POSTFIX)
            sb.append(JS_PREFIX + "prism/plugins/line-numbers/prism-line-numbers.min.js" + JS_POSTFIX)
            sb.append(JS_PREFIX + "prism/plugins/line-numbers/prism-line-numbers-markor.js" + JS_POSTFIX)
        }

        return sb.toString()
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return (PATTERN_HAS_FILE_EXTENSION_FOR_THIS_FORMAT.matcher(name).matches() && !name.endsWith(".txt")) || name.endsWith(".md.txt")
    }

    private fun extractYamlAttributes(markup: String): Map<String, List<String>> {
        val parser = Parser.builder().extensions(listOf(YamlFrontMatterExtension.create())).build()
        val visitor = AbstractYamlFrontMatterVisitor()
        visitor.visit(parser.parse(markup))
        return visitor.data
    }

    private fun replaceTokens(markup: String, fma: Map<String, List<String>>): String {
        var markupReplaced = markup

        for ((attrName, attrValue) in fma) {
            var attrValueProcessed = attrValue
            val attrValueOut = mutableListOf<String>()

            if (attrName == "tags" && attrValueProcessed.size == 1) {
                // It's not a real tag list, but rather a string of comma-separated strings
                // replaceFirst: [tag1,tag2,tag3] -> "[tag1" "tag2" "tag3]" -> "tag1" "tag2" "tag3"
                // LinkedHashSet in between keeps order, but eliminates later duplicates
                attrValueProcessed = LinkedHashSet(
                    attrValueProcessed[0].replaceFirst("^\\[".toRegex(), "").replaceFirst("]$".toRegex(), "").split(",\\s*".toRegex())
                ).toList()
            }

            for (v in attrValueProcessed) {
                // Strip surrounding single or double quotes
                var vProcessed = v.replaceFirst("^(['\"])(.*)\\1".toRegex(), "$2")
                vProcessed = TextUtils.htmlEncode(vProcessed)
                    .replace("(?<!-)---(?!-)".toRegex(), "&mdash;")
                    .replace("(?<!-)--(?!-)".toRegex(), "&ndash;")
                    .trim()
                attrValueOut.add(HTML_TOKEN_ITEM_S + vProcessed + HTML_TOKEN_ITEM_E)
            }
            val tokenValue = TextUtils.join(HTML_TOKEN_DELIMITER, attrValueOut).replace("{{ attrName }}", attrName)

            // Replace "{{ <scope>>.<key> }}" tokens in note body
            for (scope in YAML_FRONTMATTER_SCOPES.split(",\\s*".toRegex())) {
                val token = "{{ $scope.$attrName }}"
                markupReplaced = markupReplaced.replace(token, tokenValue.replace("{{ scope }}", scope))
            }
        }

        return markupReplaced
    }

    // Extension to add line numbers to headings
    // ---------------------------------------------------------------------------------------------

    private class LineNumberIdProvider : AttributeProvider {
        override fun setAttributes(node: Node, part: AttributablePart, attributes: Attributes) {
            val document = node.document
            val lineNumber = document.getLineNumber(node.startOffset)
            attributes.addValue("line", "" + lineNumber)
        }
    }

    private class LineNumberIdProviderFactory : AttributeProviderFactory {

        override fun getAfterDependents(): Set<Class<out AttributeProviderFactory>>? {
            return null
        }

        override fun getBeforeDependents(): Set<Class<out AttributeProviderFactory>>? {
            return null
        }

        override fun affectsGlobalScope(): Boolean {
            return false
        }

        override fun create(context: LinkResolverContext): AttributeProvider {
            return LineNumberIdProvider()
        }
    }

    private class LineNumberIdExtension : HtmlRenderer.HtmlRendererExtension {
        override fun rendererOptions(options: MutableDataHolder) {
        }

        override fun extend(rendererBuilder: HtmlRenderer.Builder, rendererType: String) {
            rendererBuilder.attributeProviderFactory(LineNumberIdProviderFactory())
        }

        companion object {
            @JvmStatic
            fun create(): HtmlRenderer.HtmlRendererExtension {
                return LineNumberIdExtension()
            }
        }
    }

    companion object {
        //########################
        //## Extensions
        //########################
        const val EXT_MARKDOWN__TXT = ".txt"
        const val EXT_MARKDOWN__MD_TXT = ".md.txt"
        const val EXT_MARKDOWN__MD = ".md"
        const val EXT_MARKDOWN__MARKDOWN = ".markdown"
        const val EXT_MARKDOWN__MKD = ".mkd"
        const val EXT_MARKDOWN__MDOWN = ".mdown"
        const val EXT_MARKDOWN__MKDN = ".mkdn"
        const val EXT_MARKDOWN__MDWN = ".mdwn"
        const val EXT_MARKDOWN__MDX = ".mdx"
        const val EXT_MARKDOWN__TEXT = ".text"
        const val EXT_MARKDOWN__RMD = ".rmd"

        const val MD_EXTENSIONS_PATTERN_LIST = "((md)|(markdown)|(mkd)|(mdown)|(mkdn)|(txt)|(mdwn)|(mdx)|(text)|(rmd))"

        @JvmField
        val PATTERN_HAS_FILE_EXTENSION_FOR_THIS_FORMAT: Pattern = Pattern.compile("((?i).*\\.$MD_EXTENSIONS_PATTERN_LIST$)")

        @JvmField
        val MD_EXTENSION_PATTERN: Pattern = Pattern.compile("((?i)\\.$MD_EXTENSIONS_PATTERN_LIST$)")

        @JvmField
        val MD_EXTENSIONS = arrayOf(
            EXT_MARKDOWN__MD, EXT_MARKDOWN__MARKDOWN, EXT_MARKDOWN__MKD, EXT_MARKDOWN__MDOWN,
            EXT_MARKDOWN__MKDN, EXT_MARKDOWN__TXT, EXT_MARKDOWN__MDWN, EXT_MARKDOWN__TEXT,
            EXT_MARKDOWN__RMD, EXT_MARKDOWN__MD_TXT, EXT_MARKDOWN__MDX
        )

        //########################
        //## Injected CSS / JS / HTML
        //########################
        const val CSS_BODY = CSS_S + "body{margin:0;padding:0.5vh 3.5vw}" + CSS_E
        const val CSS_HEADER_UNDERLINE = CSS_S + " .header_no_underline { text-decoration: none; color: " + TOKEN_BW_INVERSE_OF_THEME + "; } h1 < a.header_no_underline { border-bottom: 2px solid #eaecef; } " + CSS_E
        const val CSS_H1_H2_UNDERLINE = CSS_S + " h1,h2 { border-bottom: 2px solid " + TOKEN_BW_INVERSE_OF_THEME_HEADER_UNDERLINE + "; } " + CSS_E
        const val CSS_BLOCKQUOTE_VERTICAL_LINE = CSS_S + "blockquote{padding:0px 14px;border-" + TOKEN_TEXT_DIRECTION + ":3.5px solid #dddddd;margin:4px 0}" + CSS_E
        const val CSS_LIST_TASK_NO_BULLET = CSS_S + ".task-list-item { list-style-type:none; text-indent: -1.4em; } li.task-list-item > pre, li.task-list-item > ul > li { text-indent: 0pt; }" + CSS_E
        const val CSS_GITLAB_VIDEO_CAPTION = CSS_S + ".video-container > p { margin: 0; }" + CSS_E
        const val CSS_LINK_SOFT_WRAP_AUTOBREAK_LINES = CSS_S + "p > a { word-break:break-all; }" + CSS_E

        const val CSS_TOC_STYLE = CSS_S + ".markor-table-of-contents { border: 1px solid " + TOKEN_BW_INVERSE_OF_THEME + "; border-radius: 2px; } .markor-table-of-contents > h1 { padding-left: 14px; margin-bottom: -8px; border-bottom: 1px solid " + TOKEN_BW_INVERSE_OF_THEME + "; } .markor-table-of-contents-list li { margin-left: -12px; } .markor-table-of-contents-list a { text-decoration: none; }" + CSS_E
        const val CSS_PRESENTATION_BEAMER = "<!-- " + TOKEN_TEXT_CONVERTER_MAX_ZOOM_OUT_BY_DEFAULT + " -->" + CSS_S + "img { max-width: 100%; } a:visited, a:link, a:hover, a:focus, a:active { color:inherit; } table { border-collapse: collapse; width: 100%; } table, th, td { padding: 5px; } body { font-family: Helvetica, Arial, Freesans, clean, sans-serif; padding:0 0 0 0; margin:auto; max-width:42em; } h1, h2, h3, h4, h5, h6 { font-weight: bold; } h1 { font-size: 28px; border-bottom: 2px solid; border-bottom-color: inherit; } h2 { font-size: 24px; border-bottom: 2px solid; border-bottom-color: inherit; } h3 { font-size: 18px; } h4 { font-size: 16px; } h5 { font-size: 14px; } h6 { font-size: 14px; } p, blockquote, ul, ol, dl, li, table, pre { margin: 15px 0; } code { margin: 0 2px; padding: 0 5px; } pre { line-height: 1.25em; overflow: auto; padding: 6px 10px; } pre > code { border: 0; margin: 0; padding: 0; } code { font-family: monospace; } img { max-width: 100%; } .slide { display: flex; width: 297mm; height: 166mm; margin: 0 auto 20px auto; padding: 0; align-items: center; border: 1px solid " + TOKEN_BW_INVERSE_OF_THEME + "; } .slide_body { display: block; width: 260mm; height: 155mm; margin: auto; overflow: hidden; } .slide_body:empty { display: none; } .slide:empty{ display: none; } @media print { body { margin: 0; padding: 0; } .slide { page-break-after: always; margin: 0; padding: 0; width: 297mm; min-height: 200mm; height: 200mm; max-height: 200mm; border: none; overflow: hidden; border: 0; } } *:not(span){ unicode-bidi: plaintext; } .slide_title > *{ text-align: center; border-bottom: 0px; font-size: 450%; } .slide_title > h1 { font-size: 550%; } .slide_body:not(.slide_title) > * { font-size: 200%; } .slide_body:not(.slide_title) > h1 { font-size: 350%; } .slide_body:not(.slide_title) > h2 { font-size: 310%; } img[alt*='imghcenter'] { display:block; margin-left: auto; margin-right: auto; } img[alt*='imgbig'] { object-fit: cover; min-height: 100%; min-width: 70%; } .slide_body:not(.slide_title) > h3 { font-size: 280%; }" + CSS_E
        const val HTML_PRESENTATION_BEAMER_SLIDE_START_DIV = "<!-- Presentation slide NO --> <div class='slide_pNO slide'><div class='slide_body'>"
        const val TOKEN_SITE_DATE_JEKYLL = "{{ site.time | date: '%x' }}"

        private const val CSS_PREFIX = "<link rel='stylesheet' href='file:///android_asset/"
        private const val CSS_POSTFIX = "'/>"
        private const val JS_PREFIX = "<script src='file:///android_asset/"
        private const val JS_POSTFIX = "'></script>"

        const val HTML_KATEX_INCLUDE = CSS_PREFIX + "katex/katex.min.css" + CSS_POSTFIX +
                JS_PREFIX + "katex/katex.min.js" + JS_POSTFIX +
                JS_PREFIX + "katex/katex-render.js" + JS_POSTFIX +
                JS_PREFIX + "katex/mhchem.min.js" + JS_POSTFIX
        const val CSS_KATEX = CSS_S + ".katex { font-size: inherit; }" + CSS_E

        const val HTML_MERMAID_INCLUDE = JS_PREFIX + "mermaid/mermaid.min.js" + JS_POSTFIX

        const val HTML_FRONTMATTER_CONTAINER_S = "<div class='front-matter-container'>"
        const val HTML_FRONTMATTER_CONTAINER_E = "</div>"
        const val HTML_FRONTMATTER_ITEM_CONTAINER_S = "<div class='front-matter-item front-matter-container-{{ attrName }}'>"
        const val HTML_FRONTMATTER_ITEM_CONTAINER_E = "</div>"
        const val HTML_TOKEN_ITEM_S = "<span class='{{ scope }}-item-{{ attrName }}'>"
        const val HTML_TOKEN_ITEM_E = "</span>"
        const val HTML_TOKEN_DELIMITER = "<span class='{{ scope }}-delimiter-{{ attrName }} delimiter'></span>"

        const val CSS_FRONTMATTER = CSS_S + "span.delimiter::before { content: ', '; } .front-matter-container { margin-bottom: 1.5em; border-bottom: 2px solid black; } .front-matter-item { text-align: right; margin-bottom: 0.25em; } .front-matter-container-title { font-weight: bold; font-size: 110%; } .front-matter-container-tags { white-space: pre; overflow: scroll; font-size: 80%; } div.front-matter-item > .post-item-tags { padding: 0.1em 0.4em; border-radius: 50rem; background-color: #dee2e6; } div.front-matter-item > span.post-item-tags:not(:first-child) { margin-left: 0.25em; } div.front-matter-item > span.post-delimiter-tags::before { content: ' '; }" + CSS_E
        const val YAML_FRONTMATTER_SCOPES = "post" //, page, site";

        @JvmField
        val YAML_FRONTMATTER_TOKEN_PATTERN: Pattern = Pattern.compile("\\{\\{\\s+(?:" + YAML_FRONTMATTER_SCOPES.replace(",\\s*".toRegex(), "|") + ")\\.[A-Za-z0-9]+\\s+\\}\\}")

        const val HTML_ADMONITION_INCLUDE = CSS_PREFIX + "flexmark/admonition.css" + CSS_POSTFIX +
                JS_PREFIX + "flexmark/admonition.js" + JS_POSTFIX
        const val CSS_ADMONITION = CSS_S + ".adm-block { width: initial; font-size: 90%; text-indent: 0em; } .adm-heading { height: auto; padding-top: 0.4em; padding-left: 2.2em; padding-bottom: 0.4em; } .adm-body { padding-top: 0.25em; padding-bottom: 0.25em; margin-left: 0.5em; margin-right: 0.5em; } .adm-icon { position: absolute; top: 50%; left: 0.5em; transform: translateY(-50%); } .adm-block > .adm-heading { position: relative; cursor: pointer; } .adm-block.adm-open > .adm-heading:after, .adm-block.adm-collapsed > .adm-heading:after { top: 50%; transform: translateY(-50%); content: '▼'; } .adm-block.adm-collapsed > .adm-heading:after { content: '◀'; } pre + div.adm-block, div.adm-block + pre { margin-top: 1.75em; }" + CSS_E

        //########################
        //## Converter library
        //########################
        // See https://github.com/vsch/flexmark-java/wiki/Extensions#tables
        private val flexmarkExtensions: List<Extension> = listOf(
            StrikethroughSubscriptExtension.create(),
            AutolinkExtension.create(),
            InsExtension.create(),
            FlexmarkKatexExtension.KatexExtension.create(),
            JekyllTagExtension.create(),
            JekyllFrontMatterExtension.create(),
            SuperscriptExtension.create(),        // https://github.com/vsch/flexmark-java/wiki/Extensions#superscript
            TablesExtension.create(),
            TaskListExtension.create(),
            EmojiExtension.create(),
            AnchorLinkExtension.create(),
            TocExtension.create(),                // https://github.com/vsch/flexmark-java/wiki/Table-of-Contents-Extension
            SimTocExtension.create(),             // https://github.com/vsch/flexmark-java/wiki/Table-of-Contents-Extension
            WikiLinkExtension.create(),           // https://github.com/vsch/flexmark-java/wiki/Extensions#wikilinks
            YamlFrontMatterExtension.create(),
            TypographicExtension.create(),        // https://github.com/vsch/flexmark-java/wiki/Typographic-Extension
            GitLabExtension.create(),             // https://github.com/vsch/flexmark-java/wiki/Extensions#gitlab-flavoured-markdown
            AdmonitionExtension.create(),         // https://github.com/vsch/flexmark-java/wiki/Extensions#admonition
            FootnoteExtension.create(),            // https://github.com/vsch/flexmark-java/wiki/Footnotes-Extension#overview
            LineNumberIdExtension.create()
        )

        @JvmField
        val flexmarkParser: Parser = Parser.builder().extensions(flexmarkExtensions).build()

        @JvmField
        val flexmarkRenderer: HtmlRenderer = HtmlRenderer.builder().extensions(flexmarkExtensions).build()

        //########################
        //## Others
        //########################
        private var toDashChars = " -_" // See HtmlRenderer.HEADER_ID_GENERATOR_TO_DASH_CHARS.getFrom(document)
        private val linkPattern: Pattern = Pattern.compile("\\[(.*?)\\]\\((.*?)(\\s+\".*\")?\\)")
    }
}
