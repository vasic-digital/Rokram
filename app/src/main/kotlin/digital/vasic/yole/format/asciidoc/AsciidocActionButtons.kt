/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.YoleDialogFactory
import digital.vasic.yole.model.Document

class AsciidocActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_asciidoc_checkbox_list, R.drawable.ic_check_box_black_24dp, R.string.check_list),
            ActionItem(R.string.abid_asciidoc_unordered_list_char, R.drawable.ic_list_black_24dp, R.string.unordered_list),
            ActionItem(R.string.abid_asciidoc_ordered_list_char, R.drawable.ic_format_list_numbered_black_24dp, R.string.ordered_list),
            ActionItem(R.string.abid_asciidoc_indent_level, R.drawable.ic_baseline_keyboard_double_arrow_right_24, R.string.indent_level),
            ActionItem(R.string.abid_asciidoc_deindent_level, R.drawable.ic_baseline_keyboard_double_arrow_left_24, R.string.deindent_level),
            ActionItem(R.string.abid_common_indent, R.drawable.ic_format_indent_increase_black_24dp, R.string.indent),
            ActionItem(R.string.abid_common_deindent, R.drawable.ic_format_indent_decrease_black_24dp, R.string.deindent),
            ActionItem(R.string.abid_asciidoc_squarebrackets, R.drawable.ic_baseline_data_array_24, R.string.squarebrackets),
            ActionItem(R.string.abid_asciidoc_special_key, R.drawable.asciidoc_icon_black_24dp, R.string.asciidoc_special_key),
            ActionItem(R.string.abid_asciidoc_h1, R.drawable.format_header_1, R.string.heading_1),
            ActionItem(R.string.abid_asciidoc_h2, R.drawable.format_header_2, R.string.heading_2),
            ActionItem(R.string.abid_asciidoc_h3, R.drawable.format_header_3, R.string.heading_3),
            ActionItem(R.string.abid_asciidoc_bold, R.drawable.ic_format_bold_black_24dp, R.string.bold),
            ActionItem(R.string.abid_asciidoc_italic, R.drawable.ic_format_italic_black_24dp, R.string.italic),
            ActionItem(R.string.abid_asciidoc_monospace, R.drawable.ic_code_black_24dp, R.string.inline_code),
            ActionItem(R.string.abid_asciidoc_underline, R.drawable.ic_format_underlined_black_24dp, R.string.underline),
            ActionItem(R.string.abid_asciidoc_highlight, R.drawable.ic_highlight_black_24dp, R.string.highlighted),
            ActionItem(R.string.abid_asciidoc_linethrough, R.drawable.ic_format_strikethrough_black_24dp, R.string.strikeout),
            ActionItem(R.string.abid_asciidoc_overline, R.drawable.ic_baseline_format_overline_24, R.string.inline_code),
            ActionItem(R.string.abid_asciidoc_superscript, R.drawable.ic_baseline_superscript_24, R.string.inline_code),
            ActionItem(R.string.abid_asciidoc_subscript, R.drawable.ic_baseline_subscript_24, R.string.inline_code),
            ActionItem(R.string.abid_asciidoc_break_thematic, R.drawable.ic_more_horiz_black_24dp, R.string.horizontal_line),
            ActionItem(R.string.abid_asciidoc_block_quote, R.drawable.ic_format_quote_black_24dp, R.string.quote),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio),
            ActionItem(R.string.abid_common_view_file_in_other_app, R.drawable.ic_baseline_open_in_new_24, R.string.open_with)
                .setDisplayMode(ActionItem.DisplayMode.ANY)
        )
    }

    override fun onActionClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_asciidoc_h1 -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.setOrUnsetHeadingWithLevel(1))
                true
            }
            R.string.abid_asciidoc_h2 -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.setOrUnsetHeadingWithLevel(2))
                true
            }
            R.string.abid_asciidoc_h3 -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.setOrUnsetHeadingWithLevel(3))
                true
            }
            R.string.abid_asciidoc_h4 -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.setOrUnsetHeadingWithLevel(4))
                true
            }
            R.string.abid_asciidoc_h5 -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.setOrUnsetHeadingWithLevel(5))
                true
            }
            R.string.abid_asciidoc_indent_level -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.indentLevel())
                true
            }
            R.string.abid_asciidoc_deindent_level -> {
                runRegexReplaceAction(AsciidocReplacePatternGenerator.deindentLevel())
                true
            }
            R.string.abid_asciidoc_checkbox_list -> {
                val listChar = "*"
                runRegexReplaceAction(AsciidocReplacePatternGenerator.toggleToCheckedOrUncheckedListPrefix(listChar))
                true
            }
            R.string.abid_asciidoc_unordered_list_char -> {
                val listChar = "*"
                runRegexReplaceAction(AsciidocReplacePatternGenerator.replaceWithUnorderedListPrefixOrRemovePrefix(listChar))
                true
            }
            R.string.abid_asciidoc_ordered_list_char -> {
                val listChar = "."
                runRegexReplaceAction(AsciidocReplacePatternGenerator.replaceWithOrderedListPrefixOrRemovePrefix(listChar))
                true
            }
            R.string.abid_asciidoc_squarebrackets -> {
                runAsciidocInlineAction("", "[", "]")
                true
            }
            R.string.abid_asciidoc_bold -> {
                runAsciidocInlineAction("**", "", "")
                true
            }
            R.string.abid_asciidoc_italic -> {
                runAsciidocInlineAction("_", "", "")
                true
            }
            R.string.abid_asciidoc_monospace -> {
                runAsciidocInlineAction("`", "", "")
                true
            }
            R.string.abid_asciidoc_highlight -> {
                runAsciidocInlineAction("#", "", "")
                true
            }
            R.string.abid_asciidoc_underline -> {
                runAsciidocInlineAction("#", "[.underline]", "")
                true
            }
            R.string.abid_asciidoc_overline -> {
                runAsciidocInlineAction("#", "[.overline]", "")
                true
            }
            R.string.abid_asciidoc_linethrough -> {
                runAsciidocInlineAction("#", "[.line-through]", "")
                true
            }
            R.string.abid_asciidoc_nobreak -> {
                runAsciidocInlineAction("#", "[.nobreak]", "")
                true
            }
            R.string.abid_asciidoc_nowrap -> {
                runAsciidocInlineAction("#", "[.nowrap]", "")
                true
            }
            R.string.abid_asciidoc_prewrap -> {
                runAsciidocInlineAction("#", "[.pre-wrap]", "")
                true
            }
            R.string.abid_asciidoc_subscript -> {
                runAsciidocInlineAction("~", "", "")
                true
            }
            R.string.abid_asciidoc_superscript -> {
                runAsciidocInlineAction("^", "", "")
                true
            }
            R.string.abid_asciidoc_break_thematic -> {
                runAsciidocInlineAction("", "'''\n", "")
                true
            }
            R.string.abid_asciidoc_break_page -> {
                runAsciidocInlineAction("", "<<<\n", "")
                true
            }
            R.string.abid_asciidoc_block_quote -> {
                runAsciidocInlineAction("\n____\n", "", "")
                true
            }
            R.string.abid_asciidoc_special_key -> {
                runAsciidocSpecialKeyAction()
                true
            }
            else -> runCommonAction(action)
        }
    }

    private fun rstr(@StringRes resKey: Int): String {
        return context.getString(resKey)
    }

    private fun runAsciidocSpecialKeyAction() {
        YoleDialogFactory.showAsciidocSpecialKeyDialog(activity) { callbackPayload ->
            if (!_hlEditor.hasSelection() && _hlEditor.length() > 0) {
                _hlEditor.requestFocus()
            }
            when (callbackPayload) {
                rstr(R.string.asciidoc_block_comment) -> runAsciidocInlineAction("\n////\n", "", "")
                rstr(R.string.asciidoc_block_example) -> runAsciidocInlineAction("\n====\n", "", "")
                rstr(R.string.asciidoc_block_listing) -> runAsciidocInlineAction("\n----\n", "", "")
                rstr(R.string.asciidoc_block_literal) -> runAsciidocInlineAction("\n....\n", "", "")
                rstr(R.string.asciidoc_block_open) -> runAsciidocInlineAction("\n--\n", "", "")
                rstr(R.string.asciidoc_block_sidebar) -> runAsciidocInlineAction("\n****\n", "", "")
                rstr(R.string.asciidoc_block_table) -> runAsciidocInlineAction("\n|===\n", "", "")
                rstr(R.string.asciidoc_block_pass) -> runAsciidocInlineAction("\n++++\n", "", "")
                rstr(R.string.asciidoc_block_quote) -> runAsciidocInlineAction("\n____\n", "", "")
                rstr(R.string.asciidoc_block_code) -> runAsciidocInlineAction("\n----\n", "[source,sql]", "")
                rstr(R.string.asciidoc_block_collapsible) -> runAsciidocInlineAction("\n====\n", "[%collapsible]", "")
                rstr(R.string.asciidoc_highlight) -> runAsciidocInlineAction("#", "", "")
                rstr(R.string.asciidoc_underline) -> runAsciidocInlineAction("#", "[.underline]", "")
                rstr(R.string.asciidoc_linethrough) -> runAsciidocInlineAction("#", "[.line-through]", "")
                rstr(R.string.asciidoc_overline) -> runAsciidocInlineAction("#", "[.overline]", "")
                rstr(R.string.asciidoc_subscript) -> runAsciidocInlineAction("~", "", "")
                rstr(R.string.asciidoc_superscript) -> runAsciidocInlineAction("^", "", "")
                rstr(R.string.asciidoc_nobreak) -> runAsciidocInlineAction("#", "[.nobreak]", "")
                rstr(R.string.asciidoc_nowrap) -> runAsciidocInlineAction("#", "[.nowrap]", "")
                rstr(R.string.asciidoc_prewrap) -> runAsciidocInlineAction("#", "[.pre-wrap]", "")
                rstr(R.string.asciidoc_break_thematic) -> runAsciidocInlineAction("", "\n---\n", "")
                rstr(R.string.asciidoc_break_page) -> runAsciidocInlineAction("", "\n<<<\n", "")
            }
        }
    }

    private fun runAsciidocInlineAction(action: String, prefix: String, suffix: String) {
        val text = _hlEditor.text ?: return

        if (_hlEditor.hasSelection()) {
            val selectionStart = _hlEditor.selectionStart
            val selectionEnd = _hlEditor.selectionEnd
            val selectionLength = selectionEnd - selectionStart
            val selectedText = text.toString().substring(selectionStart, selectionEnd)
            val comparingText = prefix + action + selectedText + action + suffix

            var selectedTextWithSurrounding = ""
            if ((selectionStart >= prefix.length + action.length) &&
                (selectionEnd <= (_hlEditor.length() - suffix.length - action.length))
            ) {
                selectedTextWithSurrounding = text.toString().substring(
                    selectionStart - prefix.length - action.length,
                    selectionEnd + action.length + suffix.length
                )
            }

            when {
                selectedTextWithSurrounding == comparingText -> {
                    // Remove outer surrounding
                    _hlEditor.text?.replace(
                        selectionStart - prefix.length - action.length,
                        selectionEnd + action.length + suffix.length,
                        selectedText
                    )
                    _hlEditor.setSelection(
                        selectionStart - prefix.length - action.length,
                        selectionStart - prefix.length - action.length + selectionLength
                    )
                }
                selectionStart + prefix.length + action.length <= text.length &&
                text.toString().substring(
                    selectionStart,
                    selectionStart + prefix.length + action.length
                ) == prefix + action + suffix -> {
                    if ((prefix.length + action.length + action.length + suffix.length <= selectionLength) &&
                        (text.toString().substring(
                            selectionEnd - suffix.length - action.length,
                            selectionEnd
                        ) == action)
                    ) {
                        // Remove inner surrounding
                        _hlEditor.text?.replace(selectionStart, selectionStart + prefix.length + action.length, "")
                        _hlEditor.text?.replace(
                            selectionEnd - suffix.length - action.length,
                            selectionEnd,
                            ""
                        )
                    } else {
                        // Add surrounding
                        _hlEditor.text?.insert(selectionStart, prefix + action)
                        _hlEditor.text?.insert(_hlEditor.selectionEnd, action + suffix)
                        _hlEditor.setSelection(
                            selectionStart + action.length + prefix.length,
                            selectionEnd + action.length + prefix.length
                        )
                    }
                }
                else -> {
                    // Add surrounding
                    _hlEditor.text?.insert(selectionStart, prefix + action)
                    _hlEditor.text?.insert(_hlEditor.selectionEnd, action + suffix)
                    _hlEditor.setSelection(
                        selectionStart + action.length + prefix.length,
                        selectionEnd + action.length + prefix.length
                    )
                }
            }
        } else {
            // Empty selection
            _hlEditor.text?.insert(_hlEditor.selectionStart, prefix + action)
            _hlEditor.text?.insert(_hlEditor.selectionEnd, action + suffix)
            _hlEditor.setSelection(
                _hlEditor.selectionStart - prefix.length - action.length,
                _hlEditor.selectionStart - prefix.length - action.length
            )
        }
    }

    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__asciidoc__action_keys
    }
}
