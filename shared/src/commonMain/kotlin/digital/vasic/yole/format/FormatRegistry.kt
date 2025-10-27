/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform Format Registry
 * Central registry for all supported text formats
 *
 *########################################################*/
package digital.vasic.yole.format

/**
 * Registry for all supported text formats in Yole.
 * Provides format detection, lookup, and management.
 */
object FormatRegistry {
    /**
     * All supported text formats
     */
    val formats: List<TextFormat> = listOf(
        // Core formats
        TextFormat(
            id = ID_PLAINTEXT,
            name = "Plain Text",
            extensions = listOf("txt", "text", "log"),
            mimeTypes = listOf("text/plain"),
            description = "Plain text without formatting"
        ),
        TextFormat(
            id = ID_MARKDOWN,
            name = "Markdown",
            extensions = listOf("md", "markdown", "mdown"),
            mimeTypes = listOf("text/markdown"),
            description = "Lightweight markup language"
        ),
        TextFormat(
            id = ID_TODOTXT,
            name = "Todo.txt",
            extensions = listOf("txt"),
            mimeTypes = listOf("text/plain"),
            description = "Simple todo list format"
        ),
        TextFormat(
            id = ID_CSV,
            name = "CSV",
            extensions = listOf("csv"),
            mimeTypes = listOf("text/csv"),
            description = "Comma-separated values"
        ),
        
        // Wiki formats
        TextFormat(
            id = ID_WIKITEXT,
            name = "WikiText",
            extensions = listOf("wiki", "wikitext"),
            mimeTypes = listOf("text/plain"),
            description = "MediaWiki markup language"
        ),
        TextFormat(
            id = ID_CREOLE,
            name = "Creole",
            extensions = listOf("creole"),
            mimeTypes = listOf("text/plain"),
            description = "Universal wiki markup"
        ),
        TextFormat(
            id = ID_TIDDLYWIKI,
            name = "TiddlyWiki",
            extensions = listOf("tid", "tiddly"),
            mimeTypes = listOf("text/plain"),
            description = "TiddlyWiki markup"
        ),
        
        // Technical formats
        TextFormat(
            id = ID_LATEX,
            name = "LaTeX",
            extensions = listOf("tex", "latex"),
            mimeTypes = listOf("text/x-tex"),
            description = "Document preparation system"
        ),
        TextFormat(
            id = ID_ASCIIDOC,
            name = "AsciiDoc",
            extensions = listOf("adoc", "asciidoc"),
            mimeTypes = listOf("text/asciidoc"),
            description = "Lightweight markup for technical docs"
        ),
        TextFormat(
            id = ID_ORGMODE,
            name = "Org Mode",
            extensions = listOf("org"),
            mimeTypes = listOf("text/org"),
            description = "Emacs Org Mode format"
        ),
        TextFormat(
            id = ID_RESTRUCTUREDTEXT,
            name = "reStructuredText",
            extensions = listOf("rst", "rest"),
            mimeTypes = listOf("text/x-rst"),
            description = "Python documentation format"
        ),
        
        // Specialized formats
        TextFormat(
            id = ID_KEYVALUE,
            name = "Key-Value",
            extensions = listOf("keyvalue", "properties", "ini"),
            mimeTypes = listOf("text/plain"),
            description = "Key-value configuration format"
        ),
        TextFormat(
            id = ID_TASKPAPER,
            name = "TaskPaper",
            extensions = listOf("taskpaper"),
            mimeTypes = listOf("text/plain"),
            description = "Simple task management format"
        ),
        TextFormat(
            id = ID_TEXTILE,
            name = "Textile",
            extensions = listOf("textile"),
            mimeTypes = listOf("text/plain"),
            description = "Lightweight markup language"
        ),
        
        // Data science formats
        TextFormat(
            id = ID_JUPYTER,
            name = "Jupyter Notebook",
            extensions = listOf("ipynb"),
            mimeTypes = listOf("application/x-ipynb+json"),
            description = "Jupyter notebook format"
        ),
        TextFormat(
            id = ID_RMARKDOWN,
            name = "R Markdown",
            extensions = listOf("rmd", "rmarkdown"),
            mimeTypes = listOf("text/markdown"),
            description = "R statistical computing with Markdown"
        ),
        
        // Binary format
        TextFormat(
            id = ID_BINARY,
            name = "Binary",
            extensions = emptyList(),
            mimeTypes = listOf("application/octet-stream"),
            description = "Binary file format"
        )
    )

    /**
     * Get format by ID
     */
    fun getById(id: String): TextFormat? {
        return formats.firstOrNull { it.id == id }
    }

    /**
     * Get format by file extension
     */
    fun getByExtension(extension: String): TextFormat? {
        val cleanExtension = extension.trim().lowercase()
        return formats.firstOrNull { format ->
            format.extensions.any { it.equals(cleanExtension, ignoreCase = true) }
        }
    }

    /**
     * Get format by MIME type
     */
    fun getByMimeType(mimeType: String): TextFormat? {
        val cleanMimeType = mimeType.trim().lowercase()
        return formats.firstOrNull { format ->
            format.mimeTypes.any { it.equals(cleanMimeType, ignoreCase = true) }
        }
    }

    /**
     * Detect format by file extension
     */
    fun detectByExtension(extension: String): TextFormat {
        return getByExtension(extension) ?: formats.first { it.id == ID_PLAINTEXT }
    }

    /**
     * Detect format by file content analysis
     */
    fun detectByContent(content: String): TextFormat? {
        if (content.isEmpty()) return null

        // Check for format-specific signatures
        return when {
            // Markdown detection
            content.contains("```") || content.contains("# ") || 
            content.contains("**") || content.contains("* ") -> getById(ID_MARKDOWN)
            
            // Todo.txt detection
            content.lines().any { line ->
                line.matches(Regex("^[xX]\\s.*|^\\d{4}-\\d{2}-\\d{2}.*"))
            } -> getById(ID_TODOTXT)
            
            // CSV detection
            content.lines().take(5).any { line ->
                line.split(',').size > 1 && line.split(',').all { it.isNotBlank() }
            } -> getById(ID_CSV)
            
            // LaTeX detection
            content.contains("\\documentclass") || content.contains("\\begin{document}") -> 
                getById(ID_LATEX)
            
            // AsciiDoc detection
            content.contains("= ") && content.lines().first().startsWith("=") -> 
                getById(ID_ASCIIDOC)
            
            // Org Mode detection
            content.contains("* ") && content.lines().any { it.startsWith("* ") } -> 
                getById(ID_ORGMODE)
            
            // reStructuredText detection
            content.contains("====") || content.contains("----") -> 
                getById(ID_RESTRUCTUREDTEXT)
            
            // Key-Value detection
            content.lines().take(10).any { line ->
                line.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*.*"))
            } -> getById(ID_KEYVALUE)
            
            // TaskPaper detection
            content.contains("- ") && content.lines().any { it.startsWith("- ") } -> 
                getById(ID_TASKPAPER)
            
            // WikiText detection
            content.contains("[[") && content.contains("]]") -> 
                getById(ID_WIKITEXT)
            
            // Default to plain text
            else -> getById(ID_PLAINTEXT)
        }
    }

    /**
     * Get all formats that support a given extension
     */
    fun getFormatsByExtension(extension: String): List<TextFormat> {
        val cleanExtension = extension.trim().lowercase()
        return formats.filter { format ->
            format.extensions.any { it.equals(cleanExtension, ignoreCase = true) }
        }
    }

    /**
     * Check if a format is supported
     */
    fun isSupported(formatId: String): Boolean {
        return getById(formatId) != null
    }

    /**
     * Get readable format names
     */
    fun getFormatNames(): List<String> {
        return formats.map { it.name }
    }

    /**
     * Get all supported file extensions
     */
    fun getAllExtensions(): List<String> {
        return formats.flatMap { it.extensions }.distinct()
    }

    // Format ID constants
    const val ID_UNKNOWN = "unknown"
    const val ID_PLAINTEXT = "plaintext"
    const val ID_MARKDOWN = "markdown"
    const val ID_TODOTXT = "todotxt"
    const val ID_CSV = "csv"
    const val ID_WIKITEXT = "wikitext"
    const val ID_KEYVALUE = "keyvalue"
    const val ID_ASCIIDOC = "asciidoc"
    const val ID_ORGMODE = "orgmode"
    const val ID_LATEX = "latex"
    const val ID_RESTRUCTUREDTEXT = "restructuredtext"
    const val ID_TASKPAPER = "taskpaper"
    const val ID_TEXTILE = "textile"
    const val ID_CREOLE = "creole"
    const val ID_TIDDLYWIKI = "tiddlywiki"
    const val ID_JUPYTER = "jupyter"
    const val ID_RMARKDOWN = "rmarkdown"
    const val ID_BINARY = "binary"
}