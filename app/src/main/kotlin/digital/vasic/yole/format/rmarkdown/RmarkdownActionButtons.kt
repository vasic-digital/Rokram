/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.rmarkdown

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.markdown.MarkdownActionButtons
import digital.vasic.yole.model.Document

/**
 * RMarkdown action buttons.
 *
 * Extends Markdown action buttons - RMarkdown uses standard Markdown syntax
 * with additional R code chunk support (```{r}).
 */
class RmarkdownActionButtons(context: Context, document: Document) : MarkdownActionButtons(context, document) {

    // RMarkdown uses standard Markdown actions
    // R code chunks can be inserted using the code block button with manual {r} addition

    override fun getFormatActionsKey(): Int {
        // Use markdown action keys as RMarkdown is Markdown-based
        return R.string.pref_key__markdown__action_keys
    }
}
