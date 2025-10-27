/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.jupyter

import android.graphics.Paint
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

/**
 * Jupyter Notebook syntax highlighter.
 *
 * Highlights JSON structure of Jupyter notebooks.
 */
class JupyterSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)

        // Highlight JSON keys
        createColorSpanForMatches(JSON_KEY, KEY_COLOR)

        // Highlight JSON strings
        createColorSpanForMatches(JSON_STRING, STRING_COLOR)

        // Highlight JSON numbers
        createColorSpanForMatches(JSON_NUMBER, NUMBER_COLOR)

        // Highlight JSON booleans
        createColorSpanForMatches(JSON_BOOLEAN, BOOLEAN_COLOR)

        // Highlight cell types
        createColorSpanForMatches(CELL_TYPE, CELL_TYPE_COLOR)
    }

    companion object {
        @JvmField
        val JSON_KEY: Pattern = Pattern.compile("\"([^\"]+)\"\\s*:")

        @JvmField
        val JSON_STRING: Pattern = Pattern.compile(":\\s*\"([^\"]+)\"")

        @JvmField
        val JSON_NUMBER: Pattern = Pattern.compile(":\\s*(\\d+)")

        @JvmField
        val JSON_BOOLEAN: Pattern = Pattern.compile(":\\s*(true|false|null)")

        @JvmField
        val CELL_TYPE: Pattern = Pattern.compile("\"cell_type\"\\s*:\\s*\"(code|markdown|raw)\"")

        private const val KEY_COLOR = 0xFF0066CC.toInt()          // Blue for keys
        private const val STRING_COLOR = 0xFF009900.toInt()       // Green for strings
        private const val NUMBER_COLOR = 0xFFCC6600.toInt()       // Orange for numbers
        private const val BOOLEAN_COLOR = 0xFFAA3377.toInt()      // Purple for booleans
        private const val CELL_TYPE_COLOR = 0xFFEF6D00.toInt()    // Orange for cell types
    }
}
