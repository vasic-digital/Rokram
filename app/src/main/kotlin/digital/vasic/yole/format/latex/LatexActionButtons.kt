/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.latex

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.model.Document

class LatexActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        // TODO: Add LaTeX-specific action buttons when string resources are defined
        // For now, return empty list (stub implementation)
        return emptyList()
    }

    override fun onActionClick(@StringRes action: Int): Boolean {
        return runCommonAction(action)
    }

    override fun getFormatActionsKey(): Int {
        // Use plaintext action keys as fallback
        return R.string.pref_key__plaintext__action_keys
    }
}
