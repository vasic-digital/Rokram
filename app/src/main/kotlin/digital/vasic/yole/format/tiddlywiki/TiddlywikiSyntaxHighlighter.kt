/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.tiddlywiki

import android.graphics.Paint
import android.graphics.Typeface
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

/**
 * TiddlyWiki syntax highlighter.
 *
 * Highlights TiddlyWiki wikitext markup.
 */
class TiddlywikiSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)

        // Highlight headers
        createColorSpanForMatches(TIDDLYWIKI_HEADING, HEADING_COLOR)

        // Highlight bold
        createStyleSpanForMatches(TIDDLYWIKI_BOLD, Typeface.BOLD)

        // Highlight italic
        createStyleSpanForMatches(TIDDLYWIKI_ITALIC, Typeface.ITALIC)

        // Highlight code
        createColorSpanForMatches(TIDDLYWIKI_CODE, CODE_COLOR)

        // Highlight lists
        createColorSpanForMatches(TIDDLYWIKI_LIST, LIST_COLOR)

        // Highlight links
        createColorSpanForMatches(TIDDLYWIKI_LINK, LINK_COLOR)
    }

    companion object {
        @JvmField
        val TIDDLYWIKI_HEADING: Pattern = Pattern.compile("(?m)^!+ .*$")

        @JvmField
        val TIDDLYWIKI_BOLD: Pattern = Pattern.compile("''(.*?)''")

        @JvmField
        val TIDDLYWIKI_ITALIC: Pattern = Pattern.compile("//(.*?)//")

        @JvmField
        val TIDDLYWIKI_CODE: Pattern = Pattern.compile("`(.*?)`")

        @JvmField
        val TIDDLYWIKI_LIST: Pattern = Pattern.compile("(?m)^[*#]+ ")

        @JvmField
        val TIDDLYWIKI_LINK: Pattern = Pattern.compile("\\[\\[([^]]+)\\]\\]")

        private const val HEADING_COLOR = 0xFFEF6D00.toInt()   // Orange
        private const val CODE_COLOR = 0xFF009900.toInt()      // Green
        private const val LIST_COLOR = 0xFFDAA521.toInt()      // Gold
        private const val LINK_COLOR = 0xFF1EA3FE.toInt()      // Blue
    }
}
