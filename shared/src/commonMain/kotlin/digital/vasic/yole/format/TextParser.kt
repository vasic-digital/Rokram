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
 * 
 * This is the result of parsing markup text into a structured format. It contains
 * both the original raw content and the processed/parsed version, along with
 * metadata and any errors encountered during parsing.
 *
 * @property format The format that was used to parse this document
 * @property rawContent Raw text content (original markup)
 * @property parsedContent Parsed content (typically HTML for display)
 * @property metadata Document metadata extracted during parsing (e.g., title, author)
 * @property errors Any parsing errors or warnings encountered
 *
 * @example
 * ```kotlin
 * val parser = MarkdownParser()
 * val document = parser.parse("# Hello World\n\nThis is **markdown**.")
 * 
 * println(document.format.name) // "Markdown"
 * println(document.metadata["lines"]) // "2"
 * println(document.errors.isEmpty()) // true
 * ```
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
 * 
 * Each format (Markdown, TodoTxt, LaTeX, etc.) implements this interface to provide
 * parsing capabilities for their specific markup language. The interface defines the
 * contract for parsing text content, converting to HTML, and validating syntax.
 *
 * @property supportedFormat The format this parser handles
 *
 * @example
 * ```kotlin
 * class MyCustomParser : TextParser {
 *     override val supportedFormat = TextFormat("custom", "Custom Format", ".custom")
 *     
 *     override fun parse(content: String, options: Map<String, Any>): ParsedDocument {
 *         // Implementation here
 *     }
 * }
 * ```
 */
interface TextParser {
    /**
     * The format this parser handles
     */
    val supportedFormat: TextFormat

    /**
     * Check if this parser can handle the given format.
     * 
     * Default implementation checks format ID equality. Override for more complex
     * format compatibility checking.
     *
     * @param format The format to check
     * @return true if this parser can handle the format, false otherwise
     */
    fun canParse(format: TextFormat): Boolean {
        return supportedFormat.id == format.id
    }

    /**
     * Parse markup text into structured content.
     * 
     * This is the main parsing method that converts raw markup text into a
     * structured ParsedDocument. The implementation should extract metadata,
     * perform syntax validation, and generate appropriate parsed content.
     *
     * @param content The raw markup text to parse
     * @param options Optional parsing options (format-specific). Common options include:
     *                - "filename": String - source filename for context
     *                - "lineNumbers": Boolean - enable line numbering
     *                - "baseUrl": String - base URL for relative links
     * @return Parsed document with structured content, metadata, and any errors
     *
     * @example
     * ```kotlin
     * val parser = MarkdownParser()
     * val options = mapOf(
     *     "filename" to "README.md",
     *     "lineNumbers" to true
     * )
     * val document = parser.parse("# Title\n\nContent", options)
     * ```
     */
    fun parse(content: String, options: Map<String, Any> = emptyMap()): ParsedDocument

    /**
     * Convert parsed document to HTML (for preview/export).
     * 
     * This method generates HTML representation suitable for display in web views
     * or export to HTML files. The default implementation provides a simple
     * pre-formatted text display, but parsers should override to provide rich
     * formatting.
     *
     * @param document The parsed document to convert
     * @param lightMode Whether to use light theme (true) or dark theme (false)
     * @return HTML representation of the document with appropriate styling
     *
     * @example
     * ```kotlin
     * val parser = MarkdownParser()
     * val document = parser.parse("# Title")
     * val html = parser.toHtml(document, lightMode = true)
     * // Returns: "<div class='markdown'><h1>Title</h1></div>"
     * ```
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
     * Validate document content for syntax errors.
     * 
     * This method performs syntax validation on the raw content and returns
     * a list of validation errors. The default implementation returns no errors,
     * but parsers should override to provide format-specific validation.
     *
     * @param content The content to validate
     * @return List of validation errors (empty if valid). Each error should include
     *         line number information when applicable.
     *
     * @example
     * ```kotlin
     * val parser = MarkdownParser()
     * val errors = parser.validate("[Unclosed link](missing-end")
     * // Returns: ["Line 1: Unclosed parentheses in link"]
     * ```
     */
    fun validate(content: String): List<String> {
        return emptyList()
    }
}

/**
 * Registry for text parsers.
 * 
 * Manages all available parsers and provides lookup by format. This is a singleton
 * object that maintains a registry of all TextParser implementations. Parsers are
 * typically registered during application initialization.
 *
 * @example
 * ```kotlin
 * // Register a parser
 * ParserRegistry.register(MarkdownParser())
 * 
 * // Get a parser for a format
 * val format = FormatRegistry.getById("markdown")!!
 * val parser = ParserRegistry.getParser(format)
 * 
 * // Parse content
 * val document = parser?.parse("# Hello World")
 * ```
 */
object ParserRegistry {
    private val parsers = mutableListOf<TextParser>()

    /**
     * Register a parser with the registry.
     * 
     * @param parser The parser to register
     * @throws IllegalArgumentException if a parser for the same format is already registered
     *
     * @example
     * ```kotlin
     * ParserRegistry.register(MarkdownParser())
     * ```
     */
    fun register(parser: TextParser) {
        // Check for duplicate format registration
        val existing = parsers.find { it.supportedFormat.id == parser.supportedFormat.id }
        if (existing != null) {
            throw IllegalArgumentException("Parser for format '${parser.supportedFormat.id}' is already registered")
        }
        parsers.add(parser)
    }

    /**
     * Get parser for a specific format.
     * 
     * @param format The format to get a parser for
     * @return The parser if found, null otherwise
     *
     * @example
     * ```kotlin
     * val format = FormatRegistry.getById("markdown")!!
     * val parser = ParserRegistry.getParser(format)
     * ```
     */
    fun getParser(format: TextFormat): TextParser? {
        return parsers.firstOrNull { it.canParse(format) }
    }

    /**
     * Get parser by format ID.
     * 
     * @param formatId The format ID to get a parser for
     * @return The parser if found, null otherwise
     *
     * @example
     * ```kotlin
     * val parser = ParserRegistry.getParser("markdown")
     * ```
     */
    fun getParser(formatId: String): TextParser? {
        val format = FormatRegistry.getById(formatId) ?: return null
        return getParser(format)
    }

    /**
     * Check if a parser exists for the given format.
     * 
     * @param format The format to check
     * @return true if a parser exists, false otherwise
     */
    fun hasParser(format: TextFormat): Boolean {
        return getParser(format) != null
    }

    /**
     * Get all registered parsers.
     * 
     * @return A list of all registered parsers
     */
    fun getAllParsers(): List<TextParser> {
        return parsers.toList()
    }

    /**
     * Clear all registered parsers.
     * 
     * This method is primarily useful for testing scenarios where you need
     * to reset the registry to a clean state.
     */
    fun clear() {
        parsers.clear()
    }
}

/**
 * Escape HTML special characters in a string.
 * 
 * This extension function escapes the standard HTML special characters to prevent
 * XSS attacks and ensure proper HTML rendering. It should be used when inserting
 * user-provided text into HTML content.
 *
 * @receiver The string to escape
 * @return The escaped string safe for HTML insertion
 *
 * @example
 * ```kotlin
 * val unsafe = "<script>alert('xss')</script>"
 * val safe = unsafe.escapeHtml()
 * // Returns: "&lt;script&gt;alert('xss')&lt;/script&gt;"
 * ```
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
 * Builder for common parsing configuration options.
 * 
 * This class provides a fluent API for building parsing options that can be
 * passed to TextParser.parse() methods. It includes common options like line
 * numbering, syntax highlighting, and base URL configuration.
 *
 * @property options Internal map of option key-value pairs
 *
 * @example
 * ```kotlin
 * val options = ParseOptions.create()
 *     .enableLineNumbers(true)
 *     .enableHighlighting(true)
 *     .setBaseUrl("https://example.com")
 *     .set("customOption", "value")
 *     .build()
 * 
 * val parser = MarkdownParser()
 * val document = parser.parse(content, options)
 * ```
 */
class ParseOptions {
    private val options = mutableMapOf<String, Any>()

    /**
     * Enable or disable line numbering in the output.
     * 
     * @param enable Whether to enable line numbering (default: true)
     * @return This builder for method chaining
     */
    fun enableLineNumbers(enable: Boolean = true): ParseOptions {
        options["lineNumbers"] = enable
        return this
    }

    /**
     * Enable or disable syntax highlighting in the output.
     * 
     * @param enable Whether to enable syntax highlighting (default: true)
     * @return This builder for method chaining
     */
    fun enableHighlighting(enable: Boolean = true): ParseOptions {
        options["highlighting"] = enable
        return this
    }

    /**
     * Set the base URL for resolving relative links.
     * 
     * @param url The base URL to use for relative link resolution
     * @return This builder for method chaining
     */
    fun setBaseUrl(url: String): ParseOptions {
        options["baseUrl"] = url
        return this
    }

    /**
     * Set a custom option key-value pair.
     * 
     * @param key The option key
     * @param value The option value
     * @return This builder for method chaining
     */
    fun set(key: String, value: Any): ParseOptions {
        options[key] = value
        return this
    }

    /**
     * Build the options map.
     * 
     * @return An immutable map of all configured options
     */
    fun build(): Map<String, Any> = options.toMap()

    companion object {
        /**
         * Create a new ParseOptions builder.
         * 
         * @return A new ParseOptions instance
         */
        fun create(): ParseOptions = ParseOptions()
    }
}
