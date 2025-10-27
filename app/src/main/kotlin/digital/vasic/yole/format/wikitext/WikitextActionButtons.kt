/*#######################################################
 *
 *   Maintained 2017-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.wikitext

import android.content.Context
import android.os.Build
import android.view.KeyEvent
import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.activity.DocumentActivity
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.YoleDialogFactory
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.frontend.textview.TextViewUtils
import digital.vasic.yole.model.Document
import digital.vasic.opoc.util.GsIntentUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WikitextActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__wikitext_action_keys
    }

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_common_checkbox_list, R.drawable.ic_check_box_black_24dp, R.string.check_list),
            ActionItem(R.string.abid_common_unordered_list_char, R.drawable.ic_list_black_24dp, R.string.unordered_list),
            ActionItem(R.string.abid_wikitext_bold, R.drawable.ic_format_bold_black_24dp, R.string.bold),
            ActionItem(R.string.abid_wikitext_strikeout, R.drawable.ic_format_strikethrough_black_24dp, R.string.strikeout),
            ActionItem(R.string.abid_wikitext_italic, R.drawable.ic_format_italic_black_24dp, R.string.italic),
            ActionItem(R.string.abid_wikitext_highlight, R.drawable.ic_format_underlined_black_24dp, R.string.highlighted),
            ActionItem(R.string.abid_wikitext_code_inline, R.drawable.ic_code_black_24dp, R.string.inline_code),
            ActionItem(R.string.abid_wikitext_h1, R.drawable.format_header_1, R.string.heading_1),
            ActionItem(R.string.abid_wikitext_h2, R.drawable.format_header_2, R.string.heading_2),
            ActionItem(R.string.abid_wikitext_h3, R.drawable.format_header_3, R.string.heading_3),
            ActionItem(R.string.abid_common_ordered_list_number, R.drawable.ic_format_list_numbered_black_24dp, R.string.ordered_list),
            ActionItem(R.string.abid_common_accordion, R.drawable.ic_arrow_drop_down_black_24dp, R.string.accordion),
            ActionItem(R.string.abid_common_indent, R.drawable.ic_format_indent_increase_black_24dp, R.string.indent),
            ActionItem(R.string.abid_common_deindent, R.drawable.ic_format_indent_decrease_black_24dp, R.string.deindent),
            ActionItem(R.string.abid_wikitext_h4, R.drawable.format_header_4, R.string.heading_4),
            ActionItem(R.string.abid_wikitext_h5, R.drawable.format_header_5, R.string.heading_5),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link)
        )
    }

    override fun onActionClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_wikitext_h1 -> {
                toggleHeading(1)
                true
            }
            R.string.abid_wikitext_h2 -> {
                toggleHeading(2)
                true
            }
            R.string.abid_wikitext_h3 -> {
                toggleHeading(3)
                true
            }
            R.string.abid_wikitext_h4 -> {
                toggleHeading(4)
                true
            }
            R.string.abid_wikitext_h5 -> {
                toggleHeading(5)
                true
            }
            R.string.abid_common_unordered_list_char -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.replaceWithUnorderedListPrefixOrRemovePrefix())
                true
            }
            R.string.abid_common_checkbox_list -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.replaceWithNextStateCheckbox())
                true
            }
            R.string.abid_common_ordered_list_number -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.replaceWithOrderedListPrefixOrRemovePrefix())
                // TODO: adapt to zim wiki
                runRenumberOrderedListIfRequired()
                true
            }
            R.string.abid_wikitext_bold -> {
                runSurroundAction("**")
                true
            }
            R.string.abid_wikitext_italic -> {
                runSurroundAction("//")
                true
            }
            R.string.abid_wikitext_highlight -> {
                runSurroundAction("__")
                true
            }
            R.string.abid_wikitext_strikeout -> {
                runSurroundAction("~~")
                true
            }
            R.string.abid_wikitext_code_inline -> {
                runSurroundAction("''")
                true
            }
            R.string.abid_common_indent -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.indentOneTab())
                runRenumberOrderedListIfRequired()
                true
            }
            R.string.abid_common_deindent -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.deindentOneTab())
                runRenumberOrderedListIfRequired()
                true
            }
            R.string.abid_common_open_link_browser -> {
                openLink()
                true
            }
            else -> runCommonAction(action)
        }
    }

    override fun onActionLongClick(@StringRes action: Int): Boolean {
        return when (action) {
            R.string.abid_common_checkbox_list -> {
                runRegexReplaceAction(WikitextReplacePatternGenerator.removeCheckbox())
                true
            }
            R.string.abid_common_insert_link -> {
                val pos = _hlEditor.selectionStart
                _hlEditor.text?.insert(pos, "[[]]")
                _hlEditor.setSelection(pos + 2)
                true
            }
            R.string.abid_common_insert_image -> {
                val pos = _hlEditor.selectionStart
                _hlEditor.text?.insert(pos, "{{}}")
                _hlEditor.setSelection(pos + 2)
                true
            }
            R.string.abid_wikitext_code_inline -> {
                _hlEditor.withAutoFormatDisabled { _hlEditor.text?.let { surroundBlock(it, "'''") } }
                true
            }
            else -> runCommonLongPressAction(action)
        }
    }

    private fun openLink() {
        val fullWikitextLink = tryExtractWikitextLink()

        if (fullWikitextLink == null) {
            // the link under the cursor is not a wikitext link, probably just a plain url
            runCommonAction(R.string.abid_common_open_link_browser)
            return
        }

        val resolver = WikitextLinkResolver.resolve(fullWikitextLink, _appSettings.notebookDirectory, _document.file, _appSettings.isWikitextDynamicNotebookRootEnabled)
        val resolvedLink = resolver.resolvedLink ?: return

        if (resolver.isWebLink()) {
            GsIntentUtils.openWebpageInExternalBrowser(context, resolvedLink)
        } else {
            DocumentActivity.launch(activity, File(resolvedLink), false, null)
        }
    }

    private fun tryExtractWikitextLink(): String? {
        val cursorPos = TextViewUtils.getSelection(_hlEditor)[0]
        val text = _hlEditor.text ?: return null
        val lineStart = TextViewUtils.getLineStart(text, cursorPos)
        val lineEnd = TextViewUtils.getLineEnd(text, cursorPos)
        val line = text.subSequence(lineStart, lineEnd)
        val cursorPosInLine = cursorPos - lineStart

        val m = WikitextSyntaxHighlighter.LINK.matcher(line)
        while (m.find()) {
            if (m.start() < cursorPosInLine && m.end() > cursorPosInLine) {
                return m.group()
            }
        }
        return null
    }

    private fun toggleHeading(headingLevel: Int) {
        val text = _hlEditor.text
        runRegexReplaceAction(WikitextReplacePatternGenerator.setOrUnsetHeadingWithLevel(headingLevel))

        text?.let {
            val lineSelection = TextViewUtils.getLineSelection(_hlEditor)
            val m = WikitextSyntaxHighlighter.HEADING.matcher(it.subSequence(lineSelection[0], lineSelection[1]))
            if (m.find()) {
                val afterHeadingTextOffset = m.end(3)
                val lineStart = TextViewUtils.getLineStart(it, TextViewUtils.getSelection(_hlEditor)[0])
                _hlEditor.setSelection(lineStart + afterHeadingTextOffset)
            }
        }
    }

    private val _headlineDialogState = HeadlineState()

    override fun runTitleClick(): Boolean {
        val m = WikitextSyntaxHighlighter.HEADING.matcher("")
        YoleDialogFactory.showHeadlineDialog(activity, _hlEditor, _webView, _headlineDialogState) { text, start, end ->
            if (m.reset(text.subSequence(start, end)).find()) {
                return@showHeadlineDialog 7 - (m.end(2) - m.start(2))
            }
            -1
        }
        return true
    }

    override fun renumberOrderedList() {
        _hlEditor.text?.let { AutoTextFormatter.renumberOrderedList(it, WikitextReplacePatternGenerator.formatPatterns) }
    }

    override fun onReceiveKeyPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_TAB && _appSettings.isIndentWithTabKey) {
            if (event.isShiftPressed) {
                runRegexReplaceAction(WikitextReplacePatternGenerator.deindentOneTab())
            } else {
                runRegexReplaceAction(WikitextReplacePatternGenerator.indentOneTab())
            }
            runRenumberOrderedListIfRequired()
            return true
        }

        return super.onReceiveKeyPress(keyCode, event)
    }

    companion object {
        @JvmStatic
        fun createWikitextHeaderAndTitleContents(fileNameWithoutExtension: String, creationDate: Date, creationDateLinePrefix: String): String {
            val creationDateFormatted = try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    throw Exception()
                }
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT).format(creationDate)
            } catch (e: Exception) {
                val formatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.ROOT).format(creationDate)
                if (!formatted.contains("+")) formatted else (formatted.substring(0, 22) + ":" + formatted.substring(22))
            }

            val headerContentTypeLine = "Content-Type: text/x-zim-wiki"
            val headerWikiFormatLine = "Wiki-Format: zim 0.6"
            val headerCreationDateLine = "Creation-Date: $creationDateFormatted"
            val title = fileNameWithoutExtension.trim().replace("_", " ")
            val titleLine = "====== $title ======"
            val creationDateLineFormat = SimpleDateFormat("'$creationDateLinePrefix' EEEE dd MMMM yyyy", Locale.getDefault())
            val creationDateLine = creationDateLineFormat.format(creationDate)

            return """$headerContentTypeLine
$headerWikiFormatLine
$headerCreationDateLine

$titleLine
$creationDateLine
"""
        }
    }
}
