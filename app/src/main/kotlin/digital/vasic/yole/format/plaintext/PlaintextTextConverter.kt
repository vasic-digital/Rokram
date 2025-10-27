/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   Maintained 2025 by Milos Vasic
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   SPDX-FileCopyrightText: 2025 Milos Vasic
 *   SPDX-License-Identifier: Apache-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.plaintext

import android.content.Context
import androidx.core.text.TextUtilsCompat
import digital.vasic.opoc.format.GsTextUtils
import digital.vasic.opoc.util.GsFileUtils
import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.TextConverterBase
import digital.vasic.yole.format.binary.EmbedBinaryTextConverter
import digital.vasic.yole.format.keyvalue.KeyValueTextConverter
import digital.vasic.yole.model.AppSettings
import java.io.File
import java.util.Locale

/**
 * Plain text format converter.
 *
 * Handles:
 * - Plain text files (.txt)
 * - HTML files (.html, .htm)
 * - Source code files with syntax highlighting (.py, .js, .cpp, .java, .kt, etc.)
 * - Structured text formats (.taskpaper, .org, .ledger, .svg, .lrc, .fen)
 * - Playlist files (.m3u, .m3u8) - delegates to EmbedBinaryTextConverter
 *
 * Strategy:
 * - HTML files: display as-is
 * - Source code: delegate to Markdown converter with code block highlighting
 * - Playlists/images: delegate to EmbedBinaryTextConverter
 * - Everything else: display in <pre> block with HTML encoding
 */
open class PlaintextTextConverter : TextConverterBase() {

    companion object {
        private const val HTML100_BODY_PRE_BEGIN = "<pre style='white-space: pre-wrap;font-family: $TOKEN_FONT' >"
        private const val HTML101_BODY_PRE_END = "</pre>"

        // Plain text file extensions
        private val EXT_TEXT = listOf(
            ".txt", ".taskpaper", ".org", ".ldg", ".ledger",
            ".m3u", ".m3u8", ".svg", ".lrc", ".fen"
        )

        // HTML file extensions
        private val EXT_HTML = listOf(".html", ".htm")

        // Code file extensions (with syntax highlighting support)
        private val EXT_CODE_HL = listOf(
            ".py", ".cpp", ".h", ".c", ".js", ".mjs", ".css", ".cs",
            ".kt", ".lua", ".perl", ".java", ".qml", ".diff", ".php",
            ".r", ".patch", ".rs", ".swift", ".ts", ".mm", ".go",
            ".sh", ".rb", ".tex", ".xml", ".xlf"
        )

        // Combined list of all supported extensions
        private val EXT = buildList {
            addAll(EXT_TEXT)
            addAll(EXT_HTML)
            addAll(EXT_CODE_HL)
        }
    }

    override fun convertMarkup(
        markup: String,
        context: Context,
        lightMode: Boolean,
        enableLineNumbers: Boolean,
        file: File
    ): String {
        var converted = ""
        val onLoadJs = ""
        val head = ""
        val extWithDot = GsFileUtils.getFilenameExtension(file.name)
        var processedMarkup = markup

        // JSON: try to pretty-print
        if (extWithDot == ".json") {
            GsTextUtils.jsonPrettyPrint(markup)?.let { processedMarkup = it }
        }

        // Convert based on file type
        converted = when {
            // HTML: Display it as-is
            extWithDot in EXT_HTML -> {
                processedMarkup
            }

            // Playlist or textual image: Load in Embed-Binary view-mode
            extWithDot.matches(EmbedBinaryTextConverter.EXT_MATCHES_M3U_PLAYLIST.toRegex()) ||
                    extWithDot.matches(EmbedBinaryTextConverter.EXT_IMAGE_TEXTUAL.toRegex()) -> {
                return FormatRegistry.CONVERTER_EMBEDBINARY.convertMarkup(
                    processedMarkup,
                    context,
                    lightMode,
                    enableLineNumbers,
                    file
                )
            }

            // Source code: Load in Markdown view-mode & utilize code block highlighting
            extWithDot in EXT_CODE_HL || this is KeyValueTextConverter -> {
                val hlLang = extWithDot.replace(".sh", ".bash").replace(".", "")
                val codeBlock = String.format(Locale.ROOT, "```%s\n%s\n```", hlLang, processedMarkup)
                return FormatRegistry.CONVERTER_MARKDOWN.convertMarkup(
                    codeBlock,
                    context,
                    lightMode,
                    enableLineNumbers,
                    file
                )
            }

            // Default: show in plaintext <pre> block
            else -> {
                HTML100_BODY_PRE_BEGIN +
                        TextUtilsCompat.htmlEncode(processedMarkup) +
                        HTML101_BODY_PRE_END
            }
        }

        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, head)
    }

    override fun getContentType(): String {
        return CONTENT_TYPE_HTML
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT ||
                AppSettings.get(null).isExtOpenWithThisApp(ext) ||
                GsFileUtils.isTextFile(file)
    }
}
