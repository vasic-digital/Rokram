/*#######################################################
 *
 *   Maintained 2017-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.view.KeyEvent
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.activity.DocumentActivity
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.YoleDialogFactory
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.frontend.textview.TextViewUtils
import digital.vasic.yole.model.Document
import digital.vasic.opoc.util.GsFileUtils
import digital.vasic.opoc.util.GsIntentUtils
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

open class MarkdownActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    @StringRes
    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__markdown__action_keys
    }

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_common_checkbox_list, R.drawable.ic_check_box_black_24dp, R.string.check_list),
            ActionItem(R.string.abid_common_unordered_list_char, R.drawable.ic_list_black_24dp, R.string.unordered_list),
            ActionItem(R.string.abid_common_ordered_list_number, R.drawable.ic_format_list_numbered_black_24dp, R.string.ordered_list),
            ActionItem(R.string.abid_markdown_bold, R.drawable.ic_format_bold_black_24dp, R.string.bold),
            ActionItem(R.string.abid_markdown_italic, R.drawable.ic_format_italic_black_24dp, R.string.italic),
            ActionItem(R.string.abid_markdown_strikeout, R.drawable.ic_format_strikethrough_black_24dp, R.string.strikeout),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio),
            ActionItem(R.string.abid_markdown_code_inline, R.drawable.ic_code_black_24dp, R.string.inline_code),
            ActionItem(R.string.abid_markdown_table_insert_columns, R.drawable.ic_view_module_black_24dp, R.string.table),
            ActionItem(R.string.abid_markdown_quote, R.drawable.ic_format_quote_black_24dp, R.string.quote),
            ActionItem(R.string.abid_markdown_h1, R.drawable.format_header_1, R.string.heading_1),
            ActionItem(R.string.abid_markdown_h2, R.drawable.format_header_2, R.string.heading_2),
            ActionItem(R.string.abid_markdown_h3, R.drawable.format_header_3, R.string.heading_3),
            ActionItem(R.string.abid_markdown_horizontal_line, R.drawable.ic_more_horiz_black_24dp, R.string.horizontal_line),
            ActionItem(R.string.abid_common_indent, R.drawable.ic_format_indent_increase_black_24dp, R.string.indent),
            ActionItem(R.string.abid_common_deindent, R.drawable.ic_format_indent_decrease_black_24dp, R.string.deindent),
            ActionItem(R.string.abid_common_accordion, R.drawable.ic_arrow_drop_down_black_24dp, R.string.accordion)
        )
    }

    override fun onActionClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_markdown_quote -> {
                runRegexReplaceAction(MarkdownReplacePatternGenerator.toggleQuote())
                true
            }
            R.string.abid_markdown_h1 -> {
                runRegexReplaceAction(MarkdownReplacePatternGenerator.setOrUnsetHeadingWithLevel(1))
                true
            }
            R.string.abid_markdown_h2 -> {
                runRegexReplaceAction(MarkdownReplacePatternGenerator.setOrUnsetHeadingWithLevel(2))
                true
            }
            R.string.abid_markdown_h3 -> {
                runRegexReplaceAction(MarkdownReplacePatternGenerator.setOrUnsetHeadingWithLevel(3))
                true
            }
            R.string.abid_common_unordered_list_char -> {
                val listChar = _appSettings.unorderedListCharacter
                runRegexReplaceAction(MarkdownReplacePatternGenerator.replaceWithUnorderedListPrefixOrRemovePrefix(listChar))
                true
            }
            R.string.abid_common_checkbox_list -> {
                val listChar = _appSettings.unorderedListCharacter
                runRegexReplaceAction(MarkdownReplacePatternGenerator.toggleToCheckedOrUncheckedListPrefix(listChar))
                true
            }
            R.string.abid_common_ordered_list_number -> {
                runRegexReplaceAction(MarkdownReplacePatternGenerator.replaceWithOrderedListPrefixOrRemovePrefix())
                runRenumberOrderedListIfRequired()
                true
            }
            R.string.abid_markdown_bold -> {
                runSurroundAction("**")
                true
            }
            R.string.abid_markdown_italic -> {
                runSurroundAction("_")
                true
            }
            R.string.abid_markdown_strikeout -> {
                runSurroundAction("~~")
                true
            }
            R.string.abid_markdown_code_inline -> {
                runSurroundAction("`")
                true
            }
            R.string.abid_markdown_horizontal_line -> {
                _hlEditor.insertOrReplaceTextOnCursor("----\n")
                true
            }
            R.string.abid_markdown_table_insert_columns -> {
                YoleDialogFactory.showInsertTableRowDialog(activity, false) { cols, isHeaderEnabled ->
                    insertTableRow(cols, isHeaderEnabled)
                }
                true
            }
            R.string.abid_common_open_link_browser -> {
                if (followLinkUnderCursor()) {
                    true
                } else {
                    runCommonAction(action)
                }
            }
            else -> runCommonAction(action)
        }
    }

    /**
     * Used to surround selected text with a given delimiter (and remove it if present)
     *
     * Not super intelligent about how patterns can be combined.
     * Current regexes just look for the literal delimiters.
     *
     * @param pattern - Pattern to match if delimiter is present
     * @param delim   - Delimiter to surround text with
     */
    private fun runLineSurroundAction(pattern: Pattern, delim: String) {
        val sel = TextViewUtils.getSelection(_hlEditor)
        if (sel[0] < 0) {
            return
        }

        val lineBefore = if (sel[0] == sel[1]) TextViewUtils.getSelectedLines(_hlEditor, sel[0]) else null
        runRegexReplaceAction(
            ReplacePattern(pattern, "\$1\$2\$4\$6"),
            ReplacePattern(LINE_NONE, "\$1\$2$delim\$3$delim\$4")
        )

        // This logic sets the cursor to the inside of the delimiters if the delimiters were empty
        if (lineBefore != null) {
            val lineAfter = TextViewUtils.getSelectedLines(_hlEditor, sel[0])
            val pair = delim + delim
            if (lineAfter.length - lineBefore.length == pair.length && lineAfter.trim().endsWith(pair)) {
                val text = _hlEditor.text
                val end = TextViewUtils.getLineEnd(text, sel[0])
                val ns = TextViewUtils.getLastNonWhitespace(text, end) - delim.length
                _hlEditor.setSelection(ns)
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onActionLongClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_markdown_table_insert_columns -> {
                YoleDialogFactory.showInsertTableRowDialog(activity, true) { cols, isHeaderEnabled ->
                    insertTableRow(cols, isHeaderEnabled)
                }
                true
            }
            R.string.abid_markdown_code_inline -> {
                _hlEditor.withAutoFormatDisabled {
                    surroundBlock(_hlEditor.text, "```")
                }
                true
            }
            R.string.abid_markdown_bold -> {
                runLineSurroundAction(LINE_BOLD, "**")
                true
            }
            R.string.abid_markdown_italic -> {
                runLineSurroundAction(LINE_ITALIC, "_")
                true
            }
            R.string.abid_markdown_strikeout -> {
                runLineSurroundAction(LINE_STRIKEOUT, "~~")
                true
            }
            R.string.abid_common_checkbox_list -> {
                YoleDialogFactory.showDocumentChecklistDialog(
                    activity, _hlEditor.text, CHECKED_LIST_LINE, 4, "xX", " "
                ) { pos -> TextViewUtils.setSelectionAndShow(_hlEditor, pos) }
                true
            }
            else -> runCommonLongPressAction(action)
        }
    }

    class Link(
        @JvmField val title: String,
        @JvmField val link: String,
        @JvmField val isImage: Boolean,
        @JvmField val start: Int,
        @JvmField val end: Int
    ) {

        fun isValid(): Boolean {
            return link.isNotEmpty() && start >= 0 && end >= 0
        }

        companion object {
            @JvmStatic
            fun extract(text: CharSequence, pos: Int): Link {
                val sel = TextViewUtils.getLineSelection(text, pos)
                if (sel[0] != -1 && sel[1] != -1) {
                    val line = text.subSequence(sel[0], sel[1]).toString()
                    val m = MarkdownSyntaxHighlighter.LINK.matcher(line)

                    while (m.find()) {
                        val start = m.start() + sel[0]
                        val end = m.end() + sel[0]
                        if (start <= pos && end >= pos) {
                            val isImage = m.group(1) != null
                            val link = m.group(3)
                            return Link(m.group(2) ?: "", link?.trim() ?: "", isImage, start, end)
                        }
                    }
                }

                return Link("", "", false, -1, -1)
            }
        }
    }

    private fun followLinkUnderCursor(): Boolean {
        val sel = TextViewUtils.getSelection(_hlEditor)[0]
        if (sel < 0) {
            return false
        }

        val link = Link.extract(_hlEditor.text ?: "", sel)
        if (link.isValid()) {
            if (WEB_URL.matcher(link.link).matches()) {
                GsIntentUtils.openWebpageInExternalBrowser(activity, link.link)
                return true
            } else {
                val parentFile = _document.file?.parentFile
                if (parentFile != null) {
                    val f = GsFileUtils.makeAbsolute(link.link, parentFile)
                    if (f != null && (GsFileUtils.isDirectory(f) || f.isFile || GsFileUtils.canCreate(f))) {
                        DocumentActivity.launch(activity, f, null, null)
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun insertTableRow(cols: Int, isHeaderEnabled: Boolean) {
        val sb = StringBuilder()
        _hlEditor.requestFocus()

        // Append if current line empty
        val sel = TextViewUtils.getLineSelection(_hlEditor)
        if (sel[0] != -1 && sel[0] == sel[1]) {
            sb.append("\n")
        }

        for (i in 0 until cols - 1) {
            sb.append("  | ")
        }
        if (isHeaderEnabled) {
            sb.append("\n")
            for (i in 0 until cols) {
                sb.append("---")
                if (i < cols - 1) {
                    sb.append("|")
                }
            }
        }
        _hlEditor.moveCursorToEndOfLine(0)
        _hlEditor.insertOrReplaceTextOnCursor(sb.toString())
        _hlEditor.moveCursorToBeginOfLine(0)
        if (isHeaderEnabled) {
            _hlEditor.simulateKeyPress(KeyEvent.KEYCODE_DPAD_UP)
        }
    }

    private val _headlineDialogState = HeadlineState()

    override fun runTitleClick(): Boolean {
        val m = MarkdownReplacePatternGenerator.PREFIX_ATX_HEADING.matcher("")
        YoleDialogFactory.showHeadlineDialog(activity, _hlEditor, _webView, _headlineDialogState) { text, start, end ->
            if (m.reset(text.subSequence(start, end)).find()) {
                m.end(2) - m.start(2) - 1
            } else {
                -1
            }
        }
        return true
    }

    override fun renumberOrderedList() {
        AutoTextFormatter.renumberOrderedList(_hlEditor.text, MarkdownReplacePatternGenerator.formatPatterns)
    }

    companion object {
        private val WEB_URL: Pattern = Pattern.compile("https?://[^\\s/$.?#].[^\\s]*")

        const val LINE_PREFIX = "^(>\\s|#{1,6}\\s|\\s*[-*+](?:\\s\\[[ xX]\\])?\\s|\\s*\\d+[.)]\\s)?"

        // Patterns used for surrounding entire lines
        // ----------------------------------------------------------------------------
        // TODO - make these more intelligent. Should work with combined delimiters.
        // Goup 1: Prefix, Group 2: Pre-space, Group 3: Open delim, Group 4: Text, Group 5: Close delim, Group 6: Post-space
        @JvmField
        val LINE_BOLD: Pattern = Pattern.compile(LINE_PREFIX + "(\\s*)(\\*\\*)(\\S.*\\S)(\\3)(\\s*)$")

        @JvmField
        val LINE_ITALIC: Pattern = Pattern.compile(LINE_PREFIX + "(\\s*)(_)(\\S.*\\S)(\\3)(\\s*)$")

        @JvmField
        val LINE_STRIKEOUT: Pattern = Pattern.compile(LINE_PREFIX + "(\\s*)(~~)(\\S.*\\S)(\\3)(\\s*)$")

        // Group 1: Prefix, Group 2: Pre-space, Group 3: Text, Group 4: Post-space
        @JvmField
        val LINE_NONE: Pattern = Pattern.compile(LINE_PREFIX + "(\\s*)(.*?)(\\s*)$")

        @JvmField
        val CHECKED_LIST_LINE: Pattern = Pattern.compile("^(\\s*)(([-*+])\\s\\[([xX ])\\]\\s)")
    }
}
