/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.latex

import android.graphics.Paint
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

class LatexSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)

        // Highlight LaTeX commands
        createColorSpanForMatches(LATEX_COMMANDS, HIGHLIGHT_COLOR)

        // Highlight inline math
        createColorSpanForMatches(LATEX_INLINE_MATH, MATH_COLOR)

        // Highlight display math
        createColorSpanForMatches(LATEX_DISPLAY_MATH, MATH_COLOR)

        // Highlight comments
        createColorSpanForMatches(LATEX_COMMENTS, COMMENT_COLOR)

        // Highlight environments
        createColorSpanForMatches(LATEX_ENVIRONMENTS, ENVIRONMENT_COLOR)
    }

    companion object {
        @JvmField
        val LATEX_COMMANDS: Pattern = Pattern.compile("\\\\[a-zA-Z]+(\\{[^}]*\\})*")

        @JvmField
        val LATEX_INLINE_MATH: Pattern = Pattern.compile("\\$[^$]+\\$")

        @JvmField
        val LATEX_DISPLAY_MATH: Pattern = Pattern.compile("\\$\\$.*?\\$\\$")

        @JvmField
        val LATEX_COMMENTS: Pattern = Pattern.compile("%.*$")

        @JvmField
        val LATEX_ENVIRONMENTS: Pattern = Pattern.compile("\\\\(begin|end)\\{[^}]+\\}")

        private const val HIGHLIGHT_COLOR = 0xFF0066CC.toInt()  // Blue for commands
        private const val MATH_COLOR = 0xFF009900.toInt()        // Green for math
        private const val COMMENT_COLOR = 0xFF666666.toInt()     // Gray for comments
        private const val ENVIRONMENT_COLOR = 0xFFCC6600.toInt() // Orange for environments
    }
}
