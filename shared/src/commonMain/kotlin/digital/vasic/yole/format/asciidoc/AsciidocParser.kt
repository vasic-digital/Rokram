/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform AsciiDoc Parser
 * Platform-agnostic AsciiDoc parsing and HTML conversion
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import digital.vasic.yole.format.*

/**
 * AsciiDoc parser implementation.
 * Supports AsciiDoc markup with basic document structure.
 */
class AsciidocParser : TextParser {
    override val supportedFormat = FormatRegistry.formats.first { it.id == TextFormat.ID_ASCIIDOC }

    override fun parse(content: String, options: Map<String, Any>): ParsedDocument {
        val metadata = extractMetadata(content)
        val errors = validate(content)
        
        return ParsedDocument(
            format = supportedFormat,
            rawContent = content,
            parsedContent = content, // Raw content for now, will be converted to HTML in toHtml
            metadata = metadata,
            errors = errors
        )
    }

    override fun toHtml(document: ParsedDocument, lightMode: Boolean): String {
        return convertAsciidocToHtml(document.rawContent, lightMode)
    }

    override fun validate(content: String): List<String> {
        val errors = mutableListOf<String>()
        
        // Check for unclosed blocks
        val lines = content.lines()
        var inCodeBlock = false
        var inCommentBlock = false
        
        lines.forEachIndexed { index, line ->
            val lineNumber = index + 1
            
            // Check for code block boundaries
            if (line.trim() == "----" || line.trim() == "....") {
                inCodeBlock = !inCodeBlock
            }
            
            // Check for comment block boundaries
            if (line.trim() == "////") {
                inCommentBlock = !inCommentBlock
            }
            
            // Check for malformed includes
            if (line.contains("include::") && !line.contains("[]")) {
                errors.add("Line $lineNumber: Malformed include directive")
            }
            
            // Check for malformed links
            if (line.contains("link:") && !line.contains("[]")) {
                errors.add("Line $lineNumber: Malformed link directive")
            }
        }
        
        // Check for unclosed blocks
        if (inCodeBlock) {
            errors.add("Unclosed code block")
        }
        if (inCommentBlock) {
            errors.add("Unclosed comment block")
        }
        
        return errors
    }

    /**
     * Extract metadata from AsciiDoc content
     */
    private fun extractMetadata(content: String): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        val lines = content.lines()
        
        lines.take(20).forEach { line ->
            when {
                line.startsWith(":") -> {
                    // Attribute definition: :name: value
                    val parts = line.substring(1).split(":", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        metadata[key] = value
                    }
                }
                line.startsWith("=") && line.count { it == '=' } >= 1 -> {
                    // Document title
                    val title = line.replace("=", "").trim()
                    if (metadata["title"].isNullOrEmpty()) {
                        metadata["title"] = title
                    }
                }
                line.startsWith("// ") -> {
                    // Comment - could contain metadata
                    val comment = line.substring(3).trim()
                    if (comment.startsWith("@")) {
                        val parts = comment.substring(1).split(":", limit = 2)
                        if (parts.size == 2) {
                            metadata[parts[0].trim()] = parts[1].trim()
                        }
                    }
                }
            }
        }
        
        return metadata
    }

    /**
     * Convert AsciiDoc content to HTML
     */
    private fun convertAsciidocToHtml(content: String, lightMode: Boolean): String {
        val lines = content.lines()
        val html = StringBuilder()
        
        html.append("""
            <div class='asciidoc'>
            <style>
            .asciidoc { font-family: sans-serif; line-height: 1.6; }
            .asciidoc h1 { color: #4e9a06; font-size: 2em; border-bottom: 2px solid #4e9a06; padding-bottom: 0.3em; }
            .asciidoc h2 { color: #4e9a06; font-size: 1.8em; border-bottom: 1px solid #4e9a06; padding-bottom: 0.2em; }
            .asciidoc h3 { color: #4e9a06; font-size: 1.6em; }
            .asciidoc h4 { color: #4e9a06; font-size: 1.4em; }
            .asciidoc h5 { color: #4e9a06; font-size: 1.2em; }
            .asciidoc h6 { color: #4e9a06; font-size: 1em; }
            .asciidoc blockquote { border-left: 4px solid #4e9a06; padding-left: 1em; margin-left: 0; color: #666; }
            .asciidoc code { background-color: #f0f0f0; padding: 2px 4px; font-family: monospace; border-radius: 3px; }
            .asciidoc pre { background-color: #f0f0f0; padding: 1em; border-radius: 5px; overflow-x: auto; }
            .asciidoc table { border-collapse: collapse; width: 100%; }
            .asciidoc th, .asciidoc td { border: 1px solid #ddd; padding: 8px; text-align: left; }
            .asciidoc th { background-color: #4e9a06; color: white; }
            .asciidoc a { color: #1ea3fd; text-decoration: none; }
            .asciidoc a:hover { text-decoration: underline; }
            .asciidoc .admonition { border-left: 4px solid; padding: 1em; margin: 1em 0; }
            .asciidoc .admonition-note { border-color: #1ea3fd; background-color: #f0f8ff; }
            .asciidoc .admonition-tip { border-color: #4e9a06; background-color: #f8fff0; }
            .asciidoc .admonition-warning { border-color: #ffa500; background-color: #fff8f0; }
            .asciidoc .admonition-important { border-color: #ff4500; background-color: #fff0f0; }
            .asciidoc .admonition-caution { border-color: #dc143c; background-color: #fff0f0; }
            .asciidoc ul { list-style-type: disc; }
            .asciidoc ol { list-style-type: decimal; }
            .asciidoc li { margin: 0.5em 0; }
            """.trimIndent())
        
        if (!lightMode) {
            html.append("""
                .asciidoc { background-color: #2d2d2d; color: #f0f0f0; }
                .asciidoc code { background-color: #3d3d3d; color: #f0f0f0; }
                .asciidoc pre { background-color: #3d3d3d; color: #f0f0f0; }
                .asciidoc th, .asciidoc td { border-color: #555; }
                .asciidoc th { background-color: #4e9a06; }
                """.trimIndent())
        }
        
        html.append("</style>\n")
        
        var inCodeBlock = false
        var inCommentBlock = false
        
        lines.forEach { line ->
            when {
                // Code blocks
                line.trim() == "----" || line.trim() == "...." -> {
                    inCodeBlock = !inCodeBlock
                    if (inCodeBlock) {
                        html.append("<pre><code>")
                    } else {
                        html.append("</code></pre>\n")
                    }
                }
                
                // Comment blocks
                line.trim() == "////" -> {
                    inCommentBlock = !inCommentBlock
                }
                
                // Skip comment blocks
                inCommentBlock -> {
                    // Do nothing
                }
                
                // Code block content
                inCodeBlock -> {
                    html.append(line.escapeHtml())
                    html.append("\n")
                }
                
                // Headings
                line.startsWith("=") && line.count { it == '=' } >= 1 -> {
                    val level = line.takeWhile { it == '=' }.length
                    val title = line.substring(level).trim()
                    html.append("<h$level>${title.escapeHtml()}</h$level>\n")
                }
                
                // Admonitions
                line.startsWith("NOTE:") || line.startsWith("TIP:") || 
                line.startsWith("WARNING:") || line.startsWith("IMPORTANT:") || 
                line.startsWith("CAUTION:") -> {
                    val type = line.substringBefore(":").lowercase()
                    val content = line.substringAfter(":").trim()
                    
                    html.append("<div class='admonition admonition-$type'>")
                    html.append("<strong>${type.replaceFirstChar { it.uppercase() }}:</strong> ")
                    html.append(content.escapeHtml())
                    html.append("</div>\n")
                }
                
                // Lists
                line.startsWith("* ") -> {
                    html.append("<ul><li>${line.substring(2).trim().escapeHtml()}</li></ul>\n")
                }
                
                line.startsWith(". ") -> {
                    html.append("<ol><li>${line.substring(2).trim().escapeHtml()}</li></ol>\n")
                }
                
                // Links - simplified implementation
                line.contains("link:") -> {
                    // Basic link handling
                    val processed = line.replace("link:", "")
                    html.append("<p>${processed.escapeHtml()}</p>\n")
                }
                
                // Bold text
                line.contains("*") -> {
                    val processed = line.replace("*", "<strong>")
                    html.append("<p>${processed.escapeHtml()}</p>\n")
                }
                
                // Italic text
                line.contains("_") -> {
                    val processed = line.replace("_", "<em>")
                    html.append("<p>${processed.escapeHtml()}</p>\n")
                }
                
                // Regular paragraph
                line.isNotBlank() -> {
                    if (!inCodeBlock && !inCommentBlock) {
                        html.append("<p>${line.escapeHtml()}</p>\n")
                    }
                }
                
                // Empty line
                else -> {
                    if (!inCodeBlock && !inCommentBlock) {
                        html.append("<br>\n")
                    }
                }
            }
        }
        
        html.append("</div>")
        
        return html.toString()
    }
}

/**
 * Register the AsciiDoc parser with the registry
 */
fun registerAsciidocParser() {
    ParserRegistry.register(AsciidocParser())
}