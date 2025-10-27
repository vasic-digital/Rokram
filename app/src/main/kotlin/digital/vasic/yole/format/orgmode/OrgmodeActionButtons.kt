package digital.vasic.yole.format.orgmode

import android.content.Context
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.model.Document

class OrgmodeActionButtons(context: Context, document: Document) :
    ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_common_checkbox_list, R.drawable.ic_check_box_black_24dp, R.string.check_list),
            ActionItem(R.string.abid_common_unordered_list_char, R.drawable.ic_list_black_24dp, R.string.unordered_list),
            ActionItem(R.string.abid_common_ordered_list_number, R.drawable.ic_format_list_numbered_black_24dp, R.string.ordered_list),
            ActionItem(R.string.abid_common_indent, R.drawable.ic_format_indent_increase_black_24dp, R.string.indent),
            ActionItem(R.string.abid_common_deindent, R.drawable.ic_format_indent_decrease_black_24dp, R.string.deindent),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio),
            ActionItem(R.string.abid_orgmode_bold, R.drawable.ic_format_bold_black_24dp, R.string.bold),
            ActionItem(R.string.abid_orgmode_italic, R.drawable.ic_format_italic_black_24dp, R.string.italic),
            ActionItem(R.string.abid_orgmode_strikeout, R.drawable.ic_format_strikethrough_black_24dp, R.string.strikeout),
            ActionItem(R.string.abid_orgmode_underline, R.drawable.ic_format_underlined_black_24dp, R.string.underline),
            ActionItem(R.string.abid_orgmode_code_inline, R.drawable.ic_code_black_24dp, R.string.inline_code),
            ActionItem(R.string.abid_orgmode_h1, R.drawable.format_header_1, R.string.heading_1),
            ActionItem(R.string.abid_orgmode_h2, R.drawable.format_header_2, R.string.heading_2),
            ActionItem(R.string.abid_orgmode_h3, R.drawable.format_header_3, R.string.heading_3)
        )
    }

    @StringRes
    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__orgmode__action_keys
    }

    override fun renumberOrderedList() {
        AutoTextFormatter.renumberOrderedList(_hlEditor.text, OrgmodeReplacePatternGenerator.formatPatterns)
    }

    override fun onActionClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_orgmode_h1 -> {
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.setOrUnsetHeadingWithLevel(1))
                true
            }
            R.string.abid_orgmode_h2 -> {
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.setOrUnsetHeadingWithLevel(2))
                true
            }
            R.string.abid_orgmode_h3 -> {
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.setOrUnsetHeadingWithLevel(3))
                true
            }
            R.string.abid_common_unordered_list_char -> {
                val listChar = _appSettings.unorderedListCharacter
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.replaceWithUnorderedListPrefixOrRemovePrefix(listChar))
                true
            }
            R.string.abid_common_checkbox_list -> {
                val listChar = _appSettings.unorderedListCharacter
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.toggleToCheckedOrUncheckedListPrefix(listChar))
                true
            }
            R.string.abid_common_ordered_list_number -> {
                runRegexReplaceAction(OrgmodeReplacePatternGenerator.replaceWithOrderedListPrefixOrRemovePrefix())
                runRenumberOrderedListIfRequired()
                true
            }
            R.string.abid_orgmode_bold -> {
                runSurroundAction("*")
                true
            }
            R.string.abid_orgmode_italic -> {
                runSurroundAction("/")
                true
            }
            R.string.abid_orgmode_strikeout -> {
                runSurroundAction("+")
                true
            }
            R.string.abid_orgmode_underline -> {
                runSurroundAction("_")
                true
            }
            R.string.abid_orgmode_code_inline -> {
                runSurroundAction("=")
                true
            }
            else -> runCommonAction(action)
        }
    }
}
