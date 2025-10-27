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

import digital.vasic.yole.format.asciidoc.registerAsciidocParser
import digital.vasic.yole.format.binary.registerBinaryParser
import digital.vasic.yole.format.creole.registerCreoleParser
import digital.vasic.yole.format.csv.registerCsvParser
import digital.vasic.yole.format.jupyter.registerJupyterParser
import digital.vasic.yole.format.keyvalue.registerKeyValueParser
import digital.vasic.yole.format.latex.registerLatexParser
import digital.vasic.yole.format.markdown.registerMarkdownParser
import digital.vasic.yole.format.orgmode.registerOrgModeParser
import digital.vasic.yole.format.plaintext.registerPlaintextParser
import digital.vasic.yole.format.restructuredtext.registerRestructuredTextParser
import digital.vasic.yole.format.rmarkdown.registerRMarkdownParser
import digital.vasic.yole.format.taskpaper.registerTaskpaperParser
import digital.vasic.yole.format.textile.registerTextileParser
import digital.vasic.yole.format.tiddlywiki.registerTiddlyWikiParser
import digital.vasic.yole.format.todotxt.registerTodoTxtParser
import digital.vasic.yole.format.wikitext.registerWikitextParser

/**
 * Initializes and registers all format parsers
 */
object ParserInitializer {
    
    /**
     * Register all available parsers
     */
    fun registerAllParsers() {
        // Core formats
        registerPlaintextParser()
        registerMarkdownParser()
        registerTodoTxtParser()
        registerCsvParser()
        
        // Wiki formats
        registerWikitextParser()
        registerCreoleParser()
        registerTiddlyWikiParser()
        
        // Technical formats
        registerLatexParser()
        registerAsciidocParser()
        registerOrgModeParser()
        registerRestructuredTextParser()
        
        // Specialized formats
        registerKeyValueParser()
        registerTaskpaperParser()
        registerTextileParser()
        
        // Data science formats
        registerJupyterParser()
        registerRMarkdownParser()
        
        // Binary format
        registerBinaryParser()
    }
    
    /**
     * Get initialization status
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
     * Get parser statistics
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