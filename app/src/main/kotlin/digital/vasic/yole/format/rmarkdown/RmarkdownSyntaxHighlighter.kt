/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.rmarkdown

import android.graphics.Paint
import digital.vasic.yole.format.markdown.MarkdownSyntaxHighlighter
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

/**
 * RMarkdown syntax highlighter.
 *
 * Extends Markdown highlighter and adds support for R code chunks.
 * R code chunks are delimited by ```{r} ... ```
 */
class RmarkdownSyntaxHighlighter(appSettings: AppSettings) : MarkdownSyntaxHighlighter(appSettings) {

    override fun configure(paint: Paint?): RmarkdownSyntaxHighlighter {
        super.configure(paint)
        return this
    }

    override fun generateSpans() {
        // First apply standard Markdown highlighting
        super.generateSpans()

        // Add R-specific code chunk highlighting
        createColorSpanForMatches(R_CODE_CHUNK_START, R_CHUNK_COLOR)
    }

    companion object {
        // Pattern for R code chunk start: ```{r} or ```{r, options}
        @JvmField
        val R_CODE_CHUNK_START: Pattern = Pattern.compile("(?m)^```\\{r[^}]*\\}")

        private const val R_CHUNK_COLOR = 0xFFCC6600.toInt() // Orange for R chunks
    }
}
