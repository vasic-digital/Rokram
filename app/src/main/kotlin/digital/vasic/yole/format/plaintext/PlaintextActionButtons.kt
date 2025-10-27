/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   Maintained 2025 by Milos Vasic
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   SPDX-FileCopyrightText: 2025 Milos Vasic
 *   SPDX-License-Identifier: Apache-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.plaintext

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.format.markdown.MarkdownReplacePatternGenerator
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.model.Document

/**
 * Action buttons for plain text format.
 *
 * Provides toolbar quick-insert actions:
 * - Checkbox lists
 * - Ordered/unordered lists
 * - Special keys
 * - Indentation
 * - Links and media insertion
 *
 * Uses Markdown formatting patterns for list numbering.
 */
class PlaintextActionButtons(context: Context, document: Document?) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_common_checkbox_list, R.drawable.ic_check_box_black_24dp, R.string.check_list),
            ActionItem(R.string.abid_common_unordered_list_char, R.drawable.ic_list_black_24dp, R.string.unordered_list),
            ActionItem(R.string.abid_common_ordered_list_number, R.drawable.ic_format_list_numbered_black_24dp, R.string.ordered_list),
            ActionItem(R.string.abid_common_special_key, R.drawable.ic_keyboard_black_24dp, R.string.special_key),
            ActionItem(R.string.abid_common_indent, R.drawable.ic_format_indent_increase_black_24dp, R.string.indent),
            ActionItem(R.string.abid_common_deindent, R.drawable.ic_format_indent_decrease_black_24dp, R.string.deindent),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio)
        )
    }

    @StringRes
    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__plaintext__action_keys
    }

    override fun renumberOrderedList() {
        // Use markdown format for plain text too
        _hlEditor?.text?.let { text ->
            AutoTextFormatter.renumberOrderedList(text, MarkdownReplacePatternGenerator.formatPatterns)
        }
    }
}
