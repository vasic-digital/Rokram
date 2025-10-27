/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.restructuredtext

import android.graphics.Paint
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

/**
 * reStructuredText syntax highlighter.
 *
 * Highlights RST markup including headers, inline markup, directives, and code blocks.
 */
class RestructuredtextSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)

        // Highlight inline literal
        createColorSpanForMatches(RST_INLINE_LITERAL, CODE_COLOR)

        // Highlight bold
        createStyleSpanForMatches(RST_BOLD, android.graphics.Typeface.BOLD)

        // Highlight italic
        createStyleSpanForMatches(RST_ITALIC, android.graphics.Typeface.ITALIC)

        // Highlight directives
        createColorSpanForMatches(RST_DIRECTIVE, DIRECTIVE_COLOR)

        // Highlight code blocks
        createColorBackgroundSpan(RST_CODE_BLOCK, CODE_BLOCK_COLOR)

        // Highlight section underlines
        createColorSpanForMatches(RST_UNDERLINE, HEADER_COLOR)

        // Highlight list markers
        createColorSpanForMatches(RST_LIST, LIST_COLOR)
    }

    companion object {
        @JvmField
        val RST_INLINE_LITERAL: Pattern = Pattern.compile("``([^`]+)``")

        @JvmField
        val RST_BOLD: Pattern = Pattern.compile("\\*\\*([^\\*]+)\\*\\*")

        @JvmField
        val RST_ITALIC: Pattern = Pattern.compile("\\*([^\\*]+)\\*")

        @JvmField
        val RST_DIRECTIVE: Pattern = Pattern.compile("(?m)^\\.\\. [a-z-]+::")

        @JvmField
        val RST_CODE_BLOCK: Pattern = Pattern.compile("::\\s*\\n\\n.*?(?=\\n\\n|\\Z)", Pattern.DOTALL)

        @JvmField
        val RST_UNDERLINE: Pattern = Pattern.compile("(?m)^(=+|-+|~+|`+|:+|\\.+|\\'+|\"+|\\^+|_+|\\*+)$")

        @JvmField
        val RST_LIST: Pattern = Pattern.compile("(?m)^(\\*|\\d+\\.|#\\.) ")

        private const val CODE_COLOR = 0xFF009900.toInt()          // Green for code
        private const val DIRECTIVE_COLOR = 0xFFCC6600.toInt()     // Orange for directives
        private const val CODE_BLOCK_COLOR = 0x2bafafaf            // Light gray background
        private const val HEADER_COLOR = 0xFFEF6D00.toInt()        // Orange for headers
        private const val LIST_COLOR = 0xFFDAA521.toInt()          // Gold for lists
    }
}
