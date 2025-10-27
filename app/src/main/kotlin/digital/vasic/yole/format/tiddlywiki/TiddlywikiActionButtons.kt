/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.tiddlywiki

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.model.Document

/**
 * TiddlyWiki action buttons.
 *
 * Provides toolbar actions for TiddlyWiki wikitext markup.
 */
class TiddlywikiActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        // TODO: Add TiddlyWiki-specific action buttons when string resources are defined
        // Common TiddlyWiki actions would include:
        // - Headers (!, !!, !!!)
        // - Bold ('')
        // - Italic (//)
        // - Links ([[text]])
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
