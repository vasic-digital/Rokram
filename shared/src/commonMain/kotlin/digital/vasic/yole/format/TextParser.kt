/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform Text Parser System
 * Platform-agnostic text parsing interface
 *
 *########################################################*/
package digital.vasic.yole.format

/**
 * Represents a parsed document with structured content.
 * This is the result of parsing markup text into a structured format.
 */
data class ParsedDocument(
    /**
     * The format that was used to parse this document
     */
    val format: TextFormat,

    /**
     * Raw text content (original markup)
     */
    val rawContent: String,

    /**
     * Parsed content (could be HTML, structured data, etc.)
     */
    val parsedContent: String,

    /**
     * Document metadata extracted during parsing
     */
    val metadata: Map<String, String> = emptyMap(),

    /**
     * Any parsing errors or warnings
     */
    val errors: List<String> = emptyList()
)

/**
 * Interface for text format parsers.
 * Each format (Markdown, TodoTxt, LaTeX, etc.) implements this interface.
 */
interface TextParser {
    /**
     * The format this parser handles
     */
    val supportedFormat: TextFormat

    /**
     * Check if this parser can handle the given format
     */
    fun canParse(format: TextFormat): Boolean {
        return supportedFormat.id == format.id
    }

    /**
     * Parse markup text into structured content
     *
     * @param content The raw markup text to parse
     * @param options Optional parsing options (format-specific)
     * @return Parsed document with structured content
     */
    fun parse(content: String, options: Map<String, Any> = emptyMap()): ParsedDocument

    /**
     * Convert parsed document to HTML (for preview/export)
     *
     * @param document The parsed document
     * @param lightMode Whether to use light theme (true) or dark theme (false)
     * @return HTML representation of the document
     */
    fun toHtml(document: ParsedDocument, lightMode: Boolean = true): String {
        // Default implementation: escape HTML and wrap in <pre>
        return buildString {
            append("<pre>")
            append(document.rawContent.escapeHtml())
            append("</pre>")
        }
    }

    /**
     * Validate document content
     *
     * @param content The content to validate
     * @return List of validation errors (empty if valid)
     */
    fun validate(content: String): List<String> {
        return emptyList()
    }
}

/**
 * Registry for text parsers.
 * Manages all available parsers and provides lookup by format.
 */
object ParserRegistry {
    private val parsers = mutableListOf<TextParser>()

    /**
     * Register a parser
     */
    fun register(parser: TextParser) {
        parsers.add(parser)
    }

    /**
     * Get parser for a specific format
     */
    fun getParser(format: TextFormat): TextParser? {
        return parsers.firstOrNull { it.canParse(format) }
    }

    /**
     * Get parser by format ID
     */
    fun getParser(formatId: String): TextParser? {
        val format = FormatRegistry.getById(formatId) ?: return null
        return getParser(format)
    }

    /**
     * Check if a parser exists for the given format
     */
    fun hasParser(format: TextFormat): Boolean {
        return getParser(format) != null
    }

    /**
     * Get all registered parsers
     */
    fun getAllParsers(): List<TextParser> {
        return parsers.toList()
    }

    /**
     * Clear all registered parsers (useful for testing)
     */
    fun clear() {
        parsers.clear()
    }
}

/**
 * Escape HTML special characters
 */
fun String.escapeHtml(): String {
    return this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}

/**
 * Parse options builder for common parsing configurations
 */
class ParseOptions {
    private val options = mutableMapOf<String, Any>()

    fun enableLineNumbers(enable: Boolean = true): ParseOptions {
        options["lineNumbers"] = enable
        return this
    }

    fun enableHighlighting(enable: Boolean = true): ParseOptions {
        options["highlighting"] = enable
        return this
    }

    fun setBaseUrl(url: String): ParseOptions {
        options["baseUrl"] = url
        return this
    }

    fun set(key: String, value: Any): ParseOptions {
        options[key] = value
        return this
    }

    fun build(): Map<String, Any> = options.toMap()

    companion object {
        fun create(): ParseOptions = ParseOptions()
    }
}
