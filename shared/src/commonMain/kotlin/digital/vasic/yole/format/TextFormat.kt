/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform Text Format System
 * Platform-agnostic format definitions
 *
 *########################################################*/
package digital.vasic.yole.format

/**
 * Represents a text format supported by Yole.
 * This is the KMP version of FormatRegistry.Format.
 */
data class TextFormat(
    /**
     * Unique format identifier (e.g., "markdown", "todotxt", "latex")
     */
    val id: String,

    /**
     * Human-readable format name (e.g., "Markdown", "Todo.txt", "LaTeX")
     */
    val name: String,

    /**
     * Default file extension with dot (e.g., ".md", ".txt", ".tex")
     */
    val defaultExtension: String,

    /**
     * Supported file extensions for this format
     */
    val extensions: List<String> = listOf(defaultExtension),

    /**
     * Format detection patterns (e.g., file content patterns)
     */
    val detectionPatterns: List<String> = emptyList()
) {
    companion object {
        // Standard format identifiers
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
}

/**
 * Registry of all supported text formats.
 * Platform-agnostic format metadata.
 */
object FormatRegistry {

    /**
     * All supported formats in order of detection priority.
     * Order matters: more specific formats should come before more general ones.
     */
    val formats = listOf(
        TextFormat(
            id = TextFormat.ID_MARKDOWN,
            name = "Markdown",
            defaultExtension = ".md",
            extensions = listOf(".md", ".markdown", ".mkd", ".mdown", ".mkdn", ".mdwn"),
            detectionPatterns = listOf("^#+ ", "^\\[.*\\]\\(.*\\)", "^\\*\\*.*\\*\\*")
        ),
        TextFormat(
            id = TextFormat.ID_TODOTXT,
            name = "Todo.txt",
            defaultExtension = ".txt",
            extensions = listOf(".txt", ".todo.txt"),
            detectionPatterns = listOf("^\\(([A-Z])\\) ", "^x \\d{4}-\\d{2}-\\d{2}")
        ),
        TextFormat(
            id = TextFormat.ID_CSV,
            name = "CSV",
            defaultExtension = ".csv",
            extensions = listOf(".csv", ".tsv"),
            detectionPatterns = listOf("^.*,.*,.*$")
        ),
        TextFormat(
            id = TextFormat.ID_WIKITEXT,
            name = "WikiText",
            defaultExtension = ".wiki",
            extensions = listOf(".wiki", ".zim", ".txt"),
            detectionPatterns = listOf("^==+ .* ==+$", "^\\[\\[.*\\]\\]")
        ),
        TextFormat(
            id = TextFormat.ID_KEYVALUE,
            name = "Key-Value",
            defaultExtension = ".ini",
            extensions = listOf(".ini", ".conf", ".config", ".properties", ".json", ".yml", ".yaml", ".toml", ".vcf", ".ics", ".zim"),
            detectionPatterns = listOf("^[a-zA-Z_]+\\s*=", "^\\[.*\\]$")
        ),
        TextFormat(
            id = TextFormat.ID_ASCIIDOC,
            name = "AsciiDoc",
            defaultExtension = ".adoc",
            extensions = listOf(".adoc", ".asciidoc", ".asc"),
            detectionPatterns = listOf("^= ", "^== ")
        ),
        TextFormat(
            id = TextFormat.ID_ORGMODE,
            name = "Org Mode",
            defaultExtension = ".org",
            extensions = listOf(".org"),
            detectionPatterns = listOf("^\\* ", "^#\\+")
        ),
        TextFormat(
            id = TextFormat.ID_LATEX,
            name = "LaTeX",
            defaultExtension = ".tex",
            extensions = listOf(".tex", ".latex"),
            detectionPatterns = listOf("\\\\documentclass", "\\\\begin\\{document\\}")
        ),
        TextFormat(
            id = TextFormat.ID_RESTRUCTUREDTEXT,
            name = "reStructuredText",
            defaultExtension = ".rst",
            extensions = listOf(".rst", ".rest"),
            detectionPatterns = listOf("^=+$", "^-+$", "^\\.\\. ")
        ),
        TextFormat(
            id = TextFormat.ID_TASKPAPER,
            name = "TaskPaper",
            defaultExtension = ".taskpaper",
            extensions = listOf(".taskpaper"),
            detectionPatterns = listOf("^\\t- ", "^.*:$")
        ),
        TextFormat(
            id = TextFormat.ID_TEXTILE,
            name = "Textile",
            defaultExtension = ".textile",
            extensions = listOf(".textile"),
            detectionPatterns = listOf("^h[1-6]\\. ", "^\\*+ ")
        ),
        TextFormat(
            id = TextFormat.ID_CREOLE,
            name = "Creole",
            defaultExtension = ".creole",
            extensions = listOf(".creole"),
            detectionPatterns = listOf("^=+ ", "^\\*\\* ")
        ),
        TextFormat(
            id = TextFormat.ID_TIDDLYWIKI,
            name = "TiddlyWiki",
            defaultExtension = ".tid",
            extensions = listOf(".tid"),
            detectionPatterns = listOf("^!+ ", "^title: ")
        ),
        TextFormat(
            id = TextFormat.ID_JUPYTER,
            name = "Jupyter Notebook",
            defaultExtension = ".ipynb",
            extensions = listOf(".ipynb"),
            detectionPatterns = listOf("\"nbformat\":", "\"cell_type\":")
        ),
        TextFormat(
            id = TextFormat.ID_RMARKDOWN,
            name = "R Markdown",
            defaultExtension = ".rmd",
            extensions = listOf(".rmd", ".rmarkdown"),
            detectionPatterns = listOf("```\\{r", "^---$")
        ),
        TextFormat(
            id = TextFormat.ID_BINARY,
            name = "Binary/Embed",
            defaultExtension = ".bin",
            extensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".pdf", ".mp3", ".mp4", ".avi")
        ),
        TextFormat(
            id = TextFormat.ID_PLAINTEXT,
            name = "Plain Text",
            defaultExtension = ".txt",
            extensions = listOf(".txt", ".text", ".log")
        ),
        TextFormat(
            id = TextFormat.ID_UNKNOWN,
            name = "Unknown",
            defaultExtension = "",
            extensions = emptyList()
        )
    )

    /**
     * Detect format by file extension
     */
    fun detectByExtension(extension: String): TextFormat {
        val ext = extension.lowercase().let { if (it.startsWith(".")) it else ".$it" }
        return formats.firstOrNull { format ->
            format.extensions.any { it.equals(ext, ignoreCase = true) }
        } ?: formats.last() // Return UNKNOWN as fallback
    }

    /**
     * Detect format by filename
     */
    fun detectByFilename(filename: String): TextFormat {
        val extension = filename.substringAfterLast('.', "")
        return if (extension.isNotEmpty()) {
            detectByExtension(extension)
        } else {
            formats.last() // Return UNKNOWN
        }
    }

    /**
     * Detect format by file content (first few lines)
     */
    fun detectByContent(content: String, maxLines: Int = 10): TextFormat? {
        val lines = content.lines().take(maxLines)
        val sampleText = lines.joinToString("\n")

        return formats.firstOrNull { format ->
            format.detectionPatterns.any { pattern ->
                Regex(pattern, RegexOption.MULTILINE).containsMatchIn(sampleText)
            }
        }
    }

    /**
     * Get format by ID
     */
    fun getById(id: String): TextFormat? {
        return formats.firstOrNull { it.id == id }
    }

    /**
     * Check if file extension is supported
     */
    fun isExtensionSupported(extension: String): Boolean {
        val ext = extension.lowercase().let { if (it.startsWith(".")) it else ".$it" }
        return formats.any { format ->
            format.extensions.any { it.equals(ext, ignoreCase = true) }
        }
    }
}
