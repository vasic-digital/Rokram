/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.restructuredtext

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.model.Document

/**
 * reStructuredText action buttons.
 *
 * Provides toolbar actions for common RST markup.
 */
class RestructuredtextActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        // TODO: Add RST-specific action buttons when string resources are defined
        // Common RST actions would include:
        // - Headers (with underlines)
        // - Bold (**text**)
        // - Italic (*text*)
        // - Inline literal (``code``)
        // - Directives (.. directive::)
        // - Lists
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
