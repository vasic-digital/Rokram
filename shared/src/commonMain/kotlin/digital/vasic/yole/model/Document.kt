/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform Document Model
 * Platform-agnostic document representation
 *
 *########################################################*/
package digital.vasic.yole.model

import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.TextFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Platform-agnostic document model for Yole.
 * Represents a text document with associated metadata.
 *
 * This is the KMP version of the Document class, containing only
 * platform-independent properties. Platform-specific operations
 * are delegated to expect/actual functions.
 */
@Serializable
data class Document(
    /**
     * Absolute path to the document file
     */
    val path: String,

    /**
     * Document title (filename without extension)
     */
    val title: String,

    /**
     * File extension (e.g., "md", "txt", "tex")
     */
    val extension: String,

    /**
     * Format identifier (e.g., "markdown", "plaintext", "latex")
     * Maps to FormatRegistry format constants
     */
    var format: String = FORMAT_UNKNOWN,

    /**
     * Last modification time in milliseconds since epoch
     */
    @Transient
    var modTime: Long = -1,

    /**
     * Last touch time (when document was last accessed) in milliseconds since epoch
     */
    @Transient
    var touchTime: Long = -1
) {

    companion object {
        // Format constants (delegated to TextFormat for consistency)
        const val FORMAT_UNKNOWN = TextFormat.ID_UNKNOWN
        const val FORMAT_PLAINTEXT = TextFormat.ID_PLAINTEXT
        const val FORMAT_MARKDOWN = TextFormat.ID_MARKDOWN
        const val FORMAT_TODOTXT = TextFormat.ID_TODOTXT
        const val FORMAT_CSV = TextFormat.ID_CSV
        const val FORMAT_WIKITEXT = TextFormat.ID_WIKITEXT
        const val FORMAT_KEYVALUE = TextFormat.ID_KEYVALUE
        const val FORMAT_ASCIIDOC = TextFormat.ID_ASCIIDOC
        const val FORMAT_ORGMODE = TextFormat.ID_ORGMODE
        const val FORMAT_LATEX = TextFormat.ID_LATEX
        const val FORMAT_RESTRUCTUREDTEXT = TextFormat.ID_RESTRUCTUREDTEXT
        const val FORMAT_TASKPAPER = TextFormat.ID_TASKPAPER
        const val FORMAT_TEXTILE = TextFormat.ID_TEXTILE
        const val FORMAT_CREOLE = TextFormat.ID_CREOLE
        const val FORMAT_TIDDLYWIKI = TextFormat.ID_TIDDLYWIKI
        const val FORMAT_JUPYTER = TextFormat.ID_JUPYTER
        const val FORMAT_RMARKDOWN = TextFormat.ID_RMARKDOWN
        const val FORMAT_BINARY = TextFormat.ID_BINARY
    }

    /**
     * Get the filename with extension
     */
    val filename: String
        get() = if (extension.isNotEmpty()) "$title.$extension" else title

    /**
     * Check if document has been modified
     */
    fun hasChanged(): Boolean {
        return modTime < 0 || touchTime < 0 || getFileModTime() > modTime
    }

    /**
     * Reset change tracking
     */
    fun resetChangeTracking() {
        modTime = -1
        touchTime = -1
    }

    /**
     * Update touch time to current time
     */
    fun touch() {
        touchTime = currentTimeMillis()
    }

    /**
     * Get the TextFormat for this document
     */
    fun getTextFormat(): TextFormat {
        return FormatRegistry.getById(format) ?: FormatRegistry.formats.last()
    }

    /**
     * Update format based on file extension
     */
    fun detectFormatByExtension() {
        val detectedFormat = FormatRegistry.detectByExtension(extension)
        format = detectedFormat.id
    }

    /**
     * Update format based on file content
     * Returns true if format was detected, false otherwise
     */
    fun detectFormatByContent(content: String): Boolean {
        val detectedFormat = FormatRegistry.detectByContent(content)
        if (detectedFormat != null) {
            format = detectedFormat.id
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Document

        if (path != other.path) return false
        if (title != other.title) return false
        if (format != other.format) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + format.hashCode()
        return result
    }
}

/**
 * Get current time in milliseconds since epoch
 * Platform-specific implementation via expect/actual
 */
expect fun currentTimeMillis(): Long

/**
 * Get file modification time for the given path
 * Platform-specific implementation via expect/actual
 */
expect fun Document.getFileModTime(): Long

/**
 * Get file size in bytes for the given path
 * Platform-specific implementation via expect/actual
 */
expect fun Document.getFileSize(): Long

/**
 * Check if file exists at the given path
 * Platform-specific implementation via expect/actual
 */
expect fun Document.fileExists(): Boolean

/**
 * Create a Document from a file path
 * Platform-specific implementation via expect/actual
 */
expect fun createDocument(path: String): Document?
