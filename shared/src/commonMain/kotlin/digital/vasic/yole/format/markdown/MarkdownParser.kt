/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Markdown Format Parser - Platform Agnostic
 * Handles CommonMark + GitHub Flavored Markdown
 *
 *########################################################*/
package digital.vasic.yole.format.markdown

import digital.vasic.yole.format.*

/**
 * Markdown format parser
 * Supports CommonMark + GitHub Flavored Markdown (GFM) extensions
 */
class MarkdownParser : TextParser {
    override val supportedFormat: TextFormat
        get() = FormatRegistry.getById(TextFormat.ID_MARKDOWN) ?: FormatRegistry.formats.last()

    override fun parse(content: String, options: Map<String, Any>): ParsedDocument {
        val filename = options["filename"] as? String ?: ""
        val extension = getExtension(filename)

        // Convert to HTML
        val html = convertToHtml(content)

        val metadata = buildMap {
            put("extension", extension)
            put("lines", content.lines().size.toString())
        }

        return ParsedDocument(
            format = supportedFormat,
            rawContent = content,
            parsedContent = html,
            metadata = metadata
        )
    }

    override fun toHtml(document: ParsedDocument, lightMode: Boolean): String {
        return document.parsedContent
    }

    /**
     * Convert Markdown to HTML
     */
    private fun convertToHtml(content: String): String {
        val lines = content.lines()
        val html = StringBuilder()

        html.append("<div class='markdown'>")
        html.append("<style>")
        html.append(".markdown { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; }")
        html.append(".markdown h1 { font-size: 2em; font-weight: 600; border-bottom: 1px solid #eee; padding-bottom: 0.3em; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown h2 { font-size: 1.5em; font-weight: 600; border-bottom: 1px solid #eee; padding-bottom: 0.3em; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown h3 { font-size: 1.25em; font-weight: 600; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown h4 { font-size: 1em; font-weight: 600; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown h5 { font-size: 0.875em; font-weight: 600; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown h6 { font-size: 0.85em; font-weight: 600; color: #666; margin-top: 24px; margin-bottom: 16px; }")
        html.append(".markdown p { margin-top: 0; margin-bottom: 16px; }")
        html.append(".markdown blockquote { border-left: 4px solid #ddd; padding: 0 1em; color: #666; margin: 0 0 16px 0; }")
        html.append(".markdown ul, .markdown ol { margin-top: 0; margin-bottom: 16px; padding-left: 2em; }")
        html.append(".markdown li { margin-bottom: 0.25em; }")
        html.append(".markdown code { background-color: rgba(27,31,35,0.05); padding: 0.2em 0.4em; margin: 0; font-size: 85%; font-family: 'SF Mono', Monaco, Consolas, 'Courier New', monospace; border-radius: 3px; }")
        html.append(".markdown pre { background-color: #f6f8fa; padding: 16px; overflow-x: auto; font-size: 85%; line-height: 1.45; border-radius: 6px; margin-bottom: 16px; }")
        html.append(".markdown pre code { background-color: transparent; padding: 0; margin: 0; border-radius: 0; }")
        html.append(".markdown hr { height: 0.25em; padding: 0; margin: 24px 0; background-color: #e1e4e8; border: 0; }")
        html.append(".markdown table { border-collapse: collapse; border-spacing: 0; margin-bottom: 16px; }")
        html.append(".markdown table th { font-weight: 600; padding: 6px 13px; border: 1px solid #ddd; background-color: #f6f8fa; }")
        html.append(".markdown table td { padding: 6px 13px; border: 1px solid #ddd; }")
        html.append(".markdown a { color: #0366d6; text-decoration: none; }")
        html.append(".markdown a:hover { text-decoration: underline; }")
        html.append(".markdown img { max-width: 100%; }")
        html.append(".markdown input[type='checkbox'] { margin-right: 0.5em; }")
        html.append("</style>")

        var inCodeBlock = false
        var inBlockQuote = false
        var inUnorderedList = false
        var inOrderedList = false
        var inTable = false
        var inParagraph = false
        var codeBlockLanguage = ""

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            val trimmed = line.trim()

            // Handle code blocks (fenced: ```)
            if (trimmed.startsWith("```")) {
                if (!inCodeBlock) {
                    codeBlockLanguage = trimmed.substring(3).trim()
                    html.append("<pre><code>")
                    inCodeBlock = true
                } else {
                    html.append("</code></pre>")
                    inCodeBlock = false
                    codeBlockLanguage = ""
                }
                i++
                continue
            } else if (inCodeBlock) {
                html.append(line.escapeHtml())
                html.append("\n")
                i++
                continue
            }

            // Empty line handling
            if (trimmed.isEmpty()) {
                if (inParagraph) {
                    html.append("</p>")
                    inParagraph = false
                }
                if (inBlockQuote) {
                    html.append("</blockquote>")
                    inBlockQuote = false
                }
                if (inUnorderedList) {
                    html.append("</ul>")
                    inUnorderedList = false
                }
                if (inOrderedList) {
                    html.append("</ol>")
                    inOrderedList = false
                }
                i++
                continue
            }

            // Detect line types
            val isHeading = trimmed.startsWith("#")
            val isBlockQuote = trimmed.startsWith(">")
            val isUnorderedList = trimmed.matches(Regex("^[-*+]\\s+.*"))
            val isOrderedList = trimmed.matches(Regex("^\\d+\\.\\s+.*"))
            val isHorizontalRule = trimmed.matches(Regex("^([-*_]\\s*){3,}$"))
            val isTableRow = trimmed.startsWith("|") && trimmed.endsWith("|")

            // Handle table separator detection
            var isTableSeparator = false
            if (isTableRow && trimmed.matches(Regex("^\\|\\s*([-:]+\\s*\\|)+\\s*$"))) {
                isTableSeparator = true
            }

            // Close incompatible block elements
            if (isHeading || isHorizontalRule) {
                if (inParagraph) { html.append("</p>"); inParagraph = false }
                if (inBlockQuote) { html.append("</blockquote>"); inBlockQuote = false }
                if (inUnorderedList) { html.append("</ul>"); inUnorderedList = false }
                if (inOrderedList) { html.append("</ol>"); inOrderedList = false }
            }

            when {
                isHeading -> {
                    val headingMatch = Regex("^(#{1,6})\\s+(.+)$").find(trimmed)
                    if (headingMatch != null) {
                        val level = headingMatch.groupValues[1].length
                        val text = headingMatch.groupValues[2]
                        html.append("<h$level>${convertInlineMarkup(text)}</h$level>")
                    }
                }

                isHorizontalRule -> {
                    html.append("<hr>")
                }

                isTableRow && !isTableSeparator -> {
                    if (!inTable) {
                        html.append("<table>")
                        inTable = true
                    }
                    // Check if next line is a separator (makes this row a header)
                    val isHeaderRow = i + 1 < lines.size && lines[i + 1].trim().matches(Regex("^\\|\\s*([-:]+\\s*\\|)+\\s*$"))
                    html.append(convertTableRow(trimmed, isHeaderRow))
                }

                isTableSeparator -> {
                    // Just skip the separator line, table already started
                }

                isBlockQuote -> {
                    if (!inBlockQuote) {
                        html.append("<blockquote>")
                        inBlockQuote = true
                    }
                    val text = trimmed.substring(1).trim()
                    html.append(convertInlineMarkup(text))
                    html.append("<br>")
                }

                isUnorderedList -> {
                    if (!inUnorderedList) {
                        html.append("<ul>")
                        inUnorderedList = true
                    }
                    val text = trimmed.substring(trimmed.indexOfFirst { it.isWhitespace() }).trim()
                    html.append("<li>${convertInlineMarkup(text)}</li>")
                }

                isOrderedList -> {
                    if (!inOrderedList) {
                        html.append("<ol>")
                        inOrderedList = true
                    }
                    val text = trimmed.substring(trimmed.indexOf('.') + 1).trim()
                    html.append("<li>${convertInlineMarkup(text)}</li>")
                }

                else -> {
                    if (inTable) {
                        html.append("</table>")
                        inTable = false
                    }
                    if (!inParagraph) {
                        html.append("<p>")
                        inParagraph = true
                    }
                    html.append(convertInlineMarkup(trimmed))
                    html.append(" ")
                }
            }

            i++
        }

        // Close any open elements
        if (inCodeBlock) html.append("</code></pre>")
        if (inParagraph) html.append("</p>")
        if (inBlockQuote) html.append("</blockquote>")
        if (inUnorderedList) html.append("</ul>")
        if (inOrderedList) html.append("</ol>")
        if (inTable) html.append("</table>")

        html.append("</div>")
        return html.toString()
    }

    /**
     * Convert a table row
     */
    private fun convertTableRow(line: String, isHeaderRow: Boolean): String {
        val cells = line.trim().substring(1, line.trim().length - 1).split("|")
        val tag = if (isHeaderRow) "th" else "td"

        val html = StringBuilder("<tr>")
        for (cell in cells) {
            html.append("<$tag>${convertInlineMarkup(cell.trim())}</$tag>")
        }
        html.append("</tr>")
        return html.toString()
    }

    /**
     * Convert inline Markdown markup to HTML
     */
    private fun convertInlineMarkup(text: String): String {
        var result = text

        // Process inline code FIRST (before escaping)
        result = result.replace(Regex("""`([^`]+)`""")) { match ->
            val code = match.groupValues[1].escapeHtml()
            "\u0000CODE\u0000$code\u0000/CODE\u0000"
        }

        // Process images and links BEFORE escaping
        // Images: ![alt](url)
        result = result.replace(Regex("""!\[([^\]]*)\]\(([^)]+)\)""")) { match ->
            val alt = match.groupValues[1]
            val url = match.groupValues[2]
            "\u0000IMG\u0000$url\u0000ALT\u0000$alt\u0000/IMG\u0000"
        }

        // Links: [text](url)
        result = result.replace(Regex("""\[([^\]]+)\]\(([^)]+)\)""")) { match ->
            val text = match.groupValues[1]
            val url = match.groupValues[2]
            "\u0000LINK\u0000$url\u0000TEXT\u0000$text\u0000/LINK\u0000"
        }

        // Task list checkboxes: [ ] and [x]
        result = result.replace(Regex("""\[ \]""")) { "\u0000CHECKBOX\u0000unchecked\u0000/CHECKBOX\u0000" }
        result = result.replace(Regex("""\[x\]""")) { "\u0000CHECKBOX\u0000checked\u0000/CHECKBOX\u0000" }

        // Escape HTML
        result = result.escapeHtml()

        // Bold and italic (need to handle carefully to avoid conflicts)
        // Process bold before italic

        // Bold: **text** or __text__
        result = result.replace(Regex("""\*\*(.+?)\*\*""")) { match ->
            "\u0000BOLD\u0000${match.groupValues[1]}\u0000/BOLD\u0000"
        }
        result = result.replace(Regex("""__(.+?)__""")) { match ->
            "\u0000BOLD\u0000${match.groupValues[1]}\u0000/BOLD\u0000"
        }

        // Strikethrough: ~~text~~ (GFM)
        result = result.replace(Regex("""~~(.+?)~~""")) { match ->
            "\u0000STRIKE\u0000${match.groupValues[1]}\u0000/STRIKE\u0000"
        }

        // Italic: *text* or _text_
        result = result.replace(Regex("""\*(.+?)\*""")) { match ->
            "\u0000ITALIC\u0000${match.groupValues[1]}\u0000/ITALIC\u0000"
        }
        result = result.replace(Regex("""_(.+?)_""")) { match ->
            "\u0000ITALIC\u0000${match.groupValues[1]}\u0000/ITALIC\u0000"
        }

        // Restore all placeholders with HTML tags
        result = result.replace(Regex("""\u0000CODE\u0000(.+?)\u0000/CODE\u0000""")) { "<code>${it.groupValues[1]}</code>" }
        result = result.replace(Regex("""\u0000BOLD\u0000(.+?)\u0000/BOLD\u0000""")) { "<strong>${it.groupValues[1]}</strong>" }
        result = result.replace(Regex("""\u0000ITALIC\u0000(.+?)\u0000/ITALIC\u0000""")) { "<em>${it.groupValues[1]}</em>" }
        result = result.replace(Regex("""\u0000STRIKE\u0000(.+?)\u0000/STRIKE\u0000""")) { "<s>${it.groupValues[1]}</s>" }

        result = result.replace(Regex("""\u0000LINK\u0000(.+?)\u0000TEXT\u0000(.+?)\u0000/LINK\u0000""")) { match ->
            "<a href='${match.groupValues[1]}'>${match.groupValues[2]}</a>"
        }

        result = result.replace(Regex("""\u0000IMG\u0000(.+?)\u0000ALT\u0000(.+?)\u0000/IMG\u0000""")) { match ->
            "<img src='${match.groupValues[1]}' alt='${match.groupValues[2]}'/>"
        }

        result = result.replace(Regex("""\u0000CHECKBOX\u0000unchecked\u0000/CHECKBOX\u0000""")) {
            "<input type='checkbox' disabled>"
        }
        result = result.replace(Regex("""\u0000CHECKBOX\u0000checked\u0000/CHECKBOX\u0000""")) {
            "<input type='checkbox' disabled checked>"
        }

        return result
    }

    /**
     * Extract file extension from filename
     */
    private fun getExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        return if (lastDot >= 0) {
            filename.substring(lastDot).lowercase()
        } else {
            ""
        }
    }

    override fun validate(content: String): List<String> {
        val errors = mutableListOf<String>()

        val lines = content.lines()
        var inCodeBlock = false

        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()

            // Track code blocks
            if (trimmed.startsWith("```")) {
                inCodeBlock = !inCodeBlock
            }

            // Skip validation inside code blocks
            if (inCodeBlock) return@forEachIndexed

            // Check for unclosed brackets in links
            val linkPattern = Regex("""\[([^\]]+)\]\(([^)]+)\)""")
            val imageLinkPattern = Regex("""!\[([^\]]*)\]\(([^)]+)\)""")

            // Remove valid links/images, then check for orphaned brackets
            var testLine = trimmed
            testLine = testLine.replace(linkPattern, "")
            testLine = testLine.replace(imageLinkPattern, "")

            val openBrackets = testLine.count { it == '[' }
            val closeBrackets = testLine.count { it == ']' }
            if (openBrackets != closeBrackets) {
                errors.add("Line ${index + 1}: Unclosed brackets")
            }

            val openParens = testLine.count { it == '(' }
            val closeParens = testLine.count { it == ')' }
            if (openParens != closeParens) {
                errors.add("Line ${index + 1}: Unclosed parentheses")
            }
        }

        return errors
    }

    companion object {
        // Supported extensions
        val EXTENSIONS = setOf(".md", ".markdown", ".mdown", ".mkd")
    }
}
