/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * reStructuredText Parser for Kotlin Multiplatform
 * Supports parsing .rst and .rest files
 *
 *########################################################*/
package digital.vasic.yole.format.restructuredtext

import digital.vasic.yole.format.*

/**
 * Parser for reStructuredText (.rst, .rest) files
 */
class RestructuredTextParser : TextParser {
    
    override val supportedFormat = FormatRegistry.formats.first { it.id == TextFormat.ID_RESTRUCTUREDTEXT }
    
    override fun parse(content: String, options: Map<String, Any>): ParsedDocument {
        val sections = extractSections(content)
        val directives = extractDirectives(content)
        
        return ParsedDocument(
            format = supportedFormat,
            rawContent = content,
            parsedContent = generateRstHtml(content, true),
            metadata = buildMap {
                put("sections", sections.size.toString())
                put("directives", directives.size.toString())
                put("max_level", sections.maxOfOrNull { it.level }?.toString() ?: "0")
            }
        )
    }
    
    override fun toHtml(document: ParsedDocument, lightMode: Boolean): String {
        val themeClass = if (lightMode) "light" else "dark"
        
        return """
            |<div class="rst-document $themeClass">
            |${generateRstHtml(document.rawContent, lightMode)}
            |</div>
            |<style>
            |.rst-document { font-family: sans-serif; line-height: 1.6; }
            |.rst-document.light { background: white; color: black; }
            |.rst-document.dark { background: #1e1e1e; color: #d4d4d4; }
            |.rst-section { margin: 1rem 0; }
            |.rst-section-1 { font-size: 2em; color: #4e9a06; font-weight: bold; border-bottom: 2px solid #4e9a06; }
            |.rst-section-2 { font-size: 1.8em; color: #4e9a06; font-weight: bold; border-bottom: 1px solid #4e9a06; }
            |.rst-section-3 { font-size: 1.6em; color: #4e9a06; font-weight: bold; }
            |.rst-section-4 { font-size: 1.4em; color: #4e9a06; font-weight: bold; }
            |.rst-section-5 { font-size: 1.2em; color: #4e9a06; font-weight: bold; }
            |.rst-section-6 { font-size: 1em; color: #4e9a06; font-weight: bold; }
            |.rst-directive { background: #f5f5f5; border: 1px solid #ddd; border-radius: 4px; padding: 1rem; margin: 1rem 0; }
            |.rst-directive .dark { background: #2d2d2d; border-color: #444; }
            |.rst-directive-header { font-family: monospace; color: #75507b; margin-bottom: 0.5rem; }
            |.rst-directive-header .dark { color: #ad7fa8; }
            |.rst-directive-content { font-family: monospace; white-space: pre-wrap; }
            |.rst-code { font-family: monospace; background: #f8f8f8; padding: 0.2rem 0.4rem; border-radius: 3px; }
            |.rst-code .dark { background: #2d2d2d; }
            |.rst-block { background: #f8f8f8; border: 1px solid #ddd; border-radius: 4px; padding: 1rem; margin: 1rem 0; }
            |.rst-block .dark { background: #2d2d2d; border-color: #444; }
            |.rst-link { color: #1ea3fd; text-decoration: none; }
            |.rst-link:hover { text-decoration: underline; }
            |.rst-bold { font-weight: bold; }
            |.rst-italic { font-style: italic; }
            |.rst-literal { font-family: monospace; background: #f0f0f0; padding: 0.1rem 0.2rem; }
            |.rst-literal .dark { background: #3d3d3d; }
            |.rst-admonition { background: #e7f2fa; border: 1px solid #6ab0de; border-radius: 4px; padding: 1rem; margin: 1rem 0; }
            |.rst-admonition .dark { background: #1e3a5f; border-color: #4e9a06; }
            |.rst-admonition-title { font-weight: bold; color: #4e9a06; margin-bottom: 0.5rem; }
            |.rst-admonition.note { background: #e7f2fa; border-color: #6ab0de; }
            |.rst-admonition.warning { background: #fff2cc; border-color: #f0b37e; }
            |.rst-admonition.danger { background: #f2dede; border-color: #d9534f; }
            |.rst-admonition.tip { background: #dff0d8; border-color: #5cb85c; }
            |</style>
        """.trimMargin()
    }
    
    override fun canParse(format: TextFormat): Boolean {
        return supportedFormat.id == format.id
    }
    
    override fun validate(content: String): List<String> {
        val issues = mutableListOf<String>()
        
        // Check for invalid section underlines
        val sections = extractSections(content)
        sections.forEach { section ->
            if (section.underline.length < section.title.length) {
                issues.add("Section underline too short for '${section.title}'")
            }
        }
        
        return issues
    }
    
    private fun extractSections(content: String): List<RstSection> {
        val sections = mutableListOf<RstSection>()
        val lines = content.lines()
        var i = 0
        
        while (i < lines.size) {
            val line = lines[i]
            if (line.isNotEmpty() && i + 1 < lines.size) {
                val nextLine = lines[i + 1]
                if (isUnderline(nextLine)) {
                    val level = getSectionLevel(nextLine)
                    sections.add(RstSection(
                        level = level,
                        title = line.trim(),
                        underline = nextLine.trim()
                    ))
                    i += 2
                    continue
                }
            }
            i++
        }
        
        return sections
    }
    
    private fun extractDirectives(content: String): List<RstDirective> {
        val directives = mutableListOf<RstDirective>()
        
        val lines = content.lines()
        var i = 0
        
        while (i < lines.size) {
            val line = lines[i]
            if (line.startsWith(".. ") && line.contains("::")) {
                val directiveName = line.substringAfter(".. ").substringBefore("::").trim()
                val content = mutableListOf<String>()
                i++
                
                // Collect directive content (indented lines)
                while (i < lines.size && lines[i].startsWith("   ")) {
                    content.add(lines[i].trimStart())
                    i++
                }
                
                directives.add(RstDirective(
                    name = directiveName,
                    content = content.joinToString("\n")
                ))
            } else {
                i++
            }
        }
        
        return directives
    }
    
    private fun isUnderline(line: String): Boolean {
        val firstChar = line.firstOrNull()
        return firstChar != null && line.all { it == firstChar } && line.length >= 2
    }
    
    private fun getSectionLevel(underline: String): Int {
        val char = underline.first()
        return when (char) {
            '=' -> 1
            '-' -> 2
            '~' -> 3
            '^' -> 4
            '"' -> 5
            else -> 6
        }
    }
    
    private fun generateRstHtml(content: String, lightMode: Boolean): String {
        val lines = content.lines()
        val htmlLines = mutableListOf<String>()
        var inDirective = false
        var currentDirective = ""
        var directiveContent = mutableListOf<String>()
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            
            when {
                line.startsWith(".. ") && line.contains("::") -> {
                    inDirective = true
                    currentDirective = line.substringAfter(".. ").substringBefore("::").trim()
                    directiveContent.clear()
                    htmlLines.add("<div class=\"rst-directive\">")
                    htmlLines.add("<div class=\"rst-directive-header\">$line</div>")
                    i++
                }
                inDirective && line.startsWith("   ") -> {
                    directiveContent.add(line.trimStart())
                    i++
                }
                inDirective -> {
                    // End of directive
                    inDirective = false
                    htmlLines.add("<div class=\"rst-directive-content\">${directiveContent.joinToString("\n")}</div>")
                    htmlLines.add("</div>")
                }
                isSectionTitle(line, i, lines) -> {
                    // Section heading
                    val title = line
                    val underline = lines[i + 1]
                    val level = getSectionLevel(underline)
                    htmlLines.add("<div class=\"rst-section rst-section-$level\">${escapeHtml(title)}</div>")
                    i += 2
                }
                line.isNotEmpty() -> {
                    // Regular paragraph
                    htmlLines.add("<p>${escapeHtml(line)}</p>")
                    i++
                }
                else -> {
                    // Empty line
                    htmlLines.add("<br>")
                    i++
                }
            }
        }
        
        // Close any open directive
        if (inDirective) {
            htmlLines.add("<div class=\"rst-directive-content\">${directiveContent.joinToString("\n")}</div>")
            htmlLines.add("</div>")
        }
        
        return htmlLines.joinToString("\n")
    }
    
    private fun isSectionTitle(line: String, index: Int, lines: List<String>): Boolean {
        if (index + 1 >= lines.size) return false
        val nextLine = lines[index + 1]
        return isUnderline(nextLine) && line.isNotEmpty()
    }
    
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}

/**
 * Register the reStructuredText parser with the registry
 */
fun registerRestructuredTextParser() {
    ParserRegistry.register(RestructuredTextParser())
}

data class RstSection(
    val level: Int,
    val title: String,
    val underline: String
)

data class RstDirective(
    val name: String,
    val content: String
)