/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.rmarkdown

import android.content.Context
import digital.vasic.yole.format.markdown.MarkdownTextConverter
import java.io.File

/**
 * RMarkdown (R Markdown) text converter.
 *
 * RMarkdown is Markdown with embedded R code chunks.
 * This converter delegates to MarkdownTextConverter since RMarkdown
 * uses standard Markdown syntax with additional R code block support.
 */
class RmarkdownTextConverter : MarkdownTextConverter() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        // RMarkdown is essentially Markdown, so delegate to parent
        return super.convertMarkup(markup, context, lightMode, enableLineNumbers, file)
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT = setOf(".rmd", ".rmarkdown")
    }
}
