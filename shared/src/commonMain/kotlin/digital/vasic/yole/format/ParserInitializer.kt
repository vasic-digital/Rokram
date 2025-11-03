/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Parser Initializer
 * Registers all format parsers with the registry
 *
 *########################################################*/
package digital.vasic.yole.format

import digital.vasic.yole.format.*
import digital.vasic.yole.format.plaintext.PlaintextParser
import digital.vasic.yole.format.markdown.MarkdownParser
import digital.vasic.yole.format.todotxt.TodoTxtParser
import digital.vasic.yole.format.csv.CsvParser
import digital.vasic.yole.format.wikitext.WikitextParser
import digital.vasic.yole.format.creole.CreoleParser
import digital.vasic.yole.format.tiddlywiki.TiddlyWikiParser
import digital.vasic.yole.format.latex.LatexParser
import digital.vasic.yole.format.asciidoc.AsciidocParser
import digital.vasic.yole.format.orgmode.OrgModeParser
import digital.vasic.yole.format.restructuredtext.RestructuredTextParser
import digital.vasic.yole.format.keyvalue.KeyValueParser
import digital.vasic.yole.format.taskpaper.TaskpaperParser
import digital.vasic.yole.format.textile.TextileParser
import digital.vasic.yole.format.jupyter.JupyterParser
import digital.vasic.yole.format.rmarkdown.RMarkdownParser
import digital.vasic.yole.format.binary.BinaryParser

/**
 * Initializes and registers all format parsers.
 *
 * This object provides methods to register all available format parsers with the
 * ParserRegistry. It also provides utilities to check initialization status and
 * get statistics about registered parsers.
 *
 * @example
 * ```kotlin
 * // Initialize all parsers
 * ParserInitializer.registerAllParsers()
 *
 * // Check initialization status
 * val status = ParserInitializer.getInitializationStatus()
 * println(status["Markdown"]) // true
 *
 * // Get parser statistics
 * val stats = ParserInitializer.getParserStatistics()
 * println(stats["total_parsers"]) // 15
 * ```
 */
object ParserInitializer {

    /**
     * Register all available parsers with the ParserRegistry.
     *
     * This method should be called during application initialization to ensure
     * all format parsers are available for use. It registers parsers in a logical
     * order: core formats first, then wiki formats, technical formats, specialized
     * formats, data science formats, and finally the binary format.
     *
     * @example
     * ```kotlin
     * // In application startup
     * ParserInitializer.registerAllParsers()
     *
     * // Now parsers are available
     * val parser = ParserRegistry.getParser("markdown")
     * ```
     */
    fun registerAllParsers() {
        // Core formats
        ParserRegistry.register(PlaintextParser())
        ParserRegistry.register(MarkdownParser())
        ParserRegistry.register(TodoTxtParser())
        ParserRegistry.register(CsvParser())

        // Wiki formats
        ParserRegistry.register(WikitextParser())
        ParserRegistry.register(CreoleParser())
        ParserRegistry.register(TiddlyWikiParser())

        // Technical formats
        ParserRegistry.register(LatexParser())
        ParserRegistry.register(AsciidocParser())
        ParserRegistry.register(OrgModeParser())
        ParserRegistry.register(RestructuredTextParser())

        // Specialized formats
        ParserRegistry.register(KeyValueParser())
        ParserRegistry.register(TaskpaperParser())
        ParserRegistry.register(TextileParser())

        // Data science formats
        ParserRegistry.register(JupyterParser())
        ParserRegistry.register(RMarkdownParser())

        // Binary format
        ParserRegistry.register(BinaryParser())
    }
    
    /**
     * Get initialization status for all formats.
     * 
     * This method checks which formats have registered parsers and returns
     * a status map indicating whether each format is available.
     * 
     * @return A map where keys are format names and values indicate if a parser is registered
     *
     * @example
     * ```kotlin
     * val status = ParserInitializer.getInitializationStatus()
     * status.forEach { (format, isRegistered) ->
     *     println("$format: ${if (isRegistered) "✓" else "✗"}")
     * }
     * // Output:
     * // Plain Text: ✓
     * // Markdown: ✓
     * // Todo.txt: ✓
     * // ...
     * ```
     */
    fun getInitializationStatus(): Map<String, Boolean> {
        val status = mutableMapOf<String, Boolean>()
        
        // Check if all parsers are registered
        FormatRegistry.formats.forEach { format ->
            status[format.name] = ParserRegistry.hasParser(format)
        }
        
        return status
    }
    
    /**
     * Get comprehensive parser statistics.
     * 
     * This method returns detailed statistics about the current parser registration
     * state, including total count, supported formats, and any missing parsers.
     * 
     * @return A map containing parser statistics with the following keys:
     *         - "total_parsers": Int - Total number of registered parsers
     *         - "supported_formats": List<String> - Names of formats with parsers
     *         - "missing_formats": List<String> - Names of formats without parsers
     *
     * @example
     * ```kotlin
     * val stats = ParserInitializer.getParserStatistics()
     * println("Total parsers: ${stats["total_parsers"]}")
     * println("Supported formats: ${stats["supported_formats"]}")
     * println("Missing formats: ${stats["missing_formats"]}")
     * ```
     */
    fun getParserStatistics(): Map<String, Any> {
        val parsers = ParserRegistry.getAllParsers()
        
        return mapOf(
            "total_parsers" to parsers.size,
            "supported_formats" to parsers.map { it.supportedFormat.name },
            "missing_formats" to FormatRegistry.formats
                .filter { !ParserRegistry.hasParser(it) }
                .map { it.name }
        )
    }
}