/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.wikitext

import android.graphics.Paint
import android.graphics.Typeface
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import other.writeily.format.wikitext.WrWikitextHeaderSpanCreator
import java.util.regex.Pattern

class WikitextSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    private var _isWikitextBiggerHeadings = false
    private var _fontFamily = ""
    private var _isHighlightCodeBlock = false
    private var _isHighlightCodeMonospace = false

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        _delay = _appSettings.markdownHighlightingDelay
        _isWikitextBiggerHeadings = _appSettings.isHighlightBiggerHeadings
        _fontFamily = _appSettings.fontFamily
        _isHighlightCodeMonospace = _appSettings.isHighlightCodeMonospaceFont
        _isHighlightCodeBlock = _appSettings.isHighlightCodeBlock
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()

        if (_isWikitextBiggerHeadings) {
            createSpanForMatches(HEADING, WrWikitextHeaderSpanCreator(_spannable, Colors.COLOR_HEADING))
        } else {
            createColorSpanForMatches(HEADING, Colors.COLOR_HEADING)
        }

        createStyleSpanForMatches(BOLD, Typeface.BOLD)

        createStyleSpanForMatches(ITALICS, Typeface.ITALIC)

        createColorBackgroundSpan(HIGHLIGHTED, Colors.HIGHLIGHT_BACKGROUND_COLOR)

        createStrikeThroughSpanForMatches(STRIKETHROUGH)

        if (_isHighlightCodeMonospace) {
            createMonospaceSpanForMatches(PREFORMATTED_INLINE)
            createMonospaceSpanForMatches(PREFORMATTED_MULTILINE)
        }

        if (_isHighlightCodeBlock) {
            createColorBackgroundSpan(PREFORMATTED_INLINE, Colors.CODEBLOCK_COLOR)
            createColorBackgroundSpan(PREFORMATTED_MULTILINE, Colors.CODEBLOCK_COLOR)
        }

        createColorSpanForMatches(LIST_UNORDERED, Colors.UNORDERED_LIST_BULLET_COLOR)

        createColorSpanForMatches(LIST_ORDERED, Colors.ORDERED_LIST_NUMBER_COLOR)

        createSmallBlueLinkSpans()
        createColorSpanForMatches(LINK, Colors.LINK_COLOR)

        createSuperscriptStyleSpanForMatches(SUPERSCRIPT)

        createSubscriptStyleSpanForMatches(SUBSCRIPT)

        createCheckboxSpansForAllCheckStates()

        createColorSpanForMatches(ZIMHEADER, Colors.ZIMHEADER_COLOR)
    }

    private fun createCheckboxSpansForAllCheckStates() {
        createCheckboxSpanWithDifferentColors(CHECKLIST_UNCHECKED, 0xffffffff.toInt())
        createCheckboxSpanWithDifferentColors(CHECKLIST_CHECKED, Colors.CHECKLIST_CHECKED_COLOR)
        createCheckboxSpanWithDifferentColors(CHECKLIST_CROSSED, Colors.CHECKLIST_CROSSED_COLOR)
        createCheckboxSpanWithDifferentColors(CHECKLIST_RIGHT_ARROW, Colors.CHECKLIST_ARROW_COLOR)
        createCheckboxSpanWithDifferentColors(CHECKLIST_LEFT_ARROW, Colors.CHECKLIST_ARROW_COLOR)
    }

    private fun createCheckboxSpanWithDifferentColors(checkboxPattern: Pattern, symbolColor: Int) {
        createColorSpanForMatches(checkboxPattern, Colors.CHECKLIST_BASE_COLOR, CHECKBOX_LEFT_BRACKET_GROUP)
        createColorSpanForMatches(checkboxPattern, symbolColor, CHECKBOX_SYMBOL_GROUP)
        createColorSpanForMatches(checkboxPattern, Colors.CHECKLIST_BASE_COLOR, CHECKBOX_RIGHT_BRACKET_GROUP)
    }

    object Colors {
        const val COLOR_HEADING = 0xff4e9a06.toInt()
        const val HIGHLIGHT_BACKGROUND_COLOR = 0xffFFA062.toInt()   // zim original color: 0xffffff00
        const val UNORDERED_LIST_BULLET_COLOR = 0xffdaa521.toInt()
        const val CHECKLIST_BASE_COLOR = UNORDERED_LIST_BULLET_COLOR
        const val CHECKLIST_ARROW_COLOR = CHECKLIST_BASE_COLOR
        const val ORDERED_LIST_NUMBER_COLOR = 0xffdaa521.toInt()
        const val LINK_COLOR = 0xff1ea3fd.toInt() // zim original color: 0xff0000ff
        const val CHECKLIST_CHECKED_COLOR = 0xff54a309.toInt()
        const val CHECKLIST_CROSSED_COLOR = 0xffa90000.toInt()
        const val ZIMHEADER_COLOR = 0xff808080.toInt()
        const val CODEBLOCK_COLOR = 0xff8c8c8c.toInt()
    }

    companion object {
        @JvmField
        val BOLD: Pattern = Pattern.compile("(?<=(\\n|^|\\s|\\*))(\\*{2})[^*\\s](?=\\S)(.*?)[^*\\s]?\\2(?=(\\n|$|\\s|\\*))")

        @JvmField
        val ITALICS: Pattern = Pattern.compile("(?<=(\\n|^|\\s|/))(/{2})[^/\\s](.*?)[^/\\s]?\\2(?=(\\n|$|\\s|/))")

        @JvmField
        val HIGHLIGHTED: Pattern = Pattern.compile("(?<=(\\n|^|\\s|_))(_{2})[^_\\s](.*?)[^_\\s]?\\2(?=(\\n|$|\\s|_))")

        @JvmField
        val STRIKETHROUGH: Pattern = Pattern.compile("(?<=(\\n|^|\\s|~))(~{2})[^~\\s](.*?)[^~\\s]?\\2(?=(\\n|$|\\s|~))")

        @JvmField
        val HEADING: Pattern = Pattern.compile("(?<=(\\n|^|\\s))(==+)[ \\t]+(.*?)[ \\t]\\2(?=(\\n|$|\\s))")

        @JvmField
        val PREFORMATTED_INLINE: Pattern = Pattern.compile("''(?!')(.+?)''")

        @JvmField
        val PREFORMATTED_MULTILINE: Pattern = Pattern.compile("(?s)(?<=[\\n^])'''[\\n$](.*?)[\\n^]'''(?=[\\n$])")

        @JvmField
        val LIST_UNORDERED: Pattern = Pattern.compile("(?<=((\\n|^)\\s{0,10}))\\*(?= )")

        @JvmField
        val LIST_ORDERED: Pattern = Pattern.compile("(?<=((\\n|^)(\\s{0,10})))(\\d+|[a-zA-Z])(\\.)(?= )")

        @JvmField
        val LINK: Pattern = WikitextLinkResolver.Patterns.LINK.pattern

        @JvmField
        val IMAGE: Pattern = Pattern.compile("(\\{\\{(?!\\{)(.*?)\\}\\})")

        @JvmField
        val CHECKLIST: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)([ x*><])(])(?= )")

        @JvmField
        val CHECKLIST_UNCHECKED: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)( )(])(?= )")

        @JvmField
        val CHECKLIST_CHECKED: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)(\\*)(])(?= )")

        @JvmField
        val CHECKLIST_CROSSED: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)(x)(])(?= )")

        @JvmField
        val CHECKLIST_RIGHT_ARROW: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)(>)(])(?= )")

        @JvmField
        val CHECKLIST_LEFT_ARROW: Pattern = Pattern.compile("(?<=(\\n|^))\t*(\\[)(<)(])(?= )")

        @JvmField
        val SUBSCRIPT: Pattern = Pattern.compile("(_\\{(?!~)(.+?)\\})")

        @JvmField
        val SUPERSCRIPT: Pattern = Pattern.compile("(\\^\\{(?!~)(.+?)\\})")

        @JvmField
        val ZIMHEADER_CONTENT_TYPE_ONLY: Pattern = Pattern.compile("^\\s*Content-Type:\\s*text/x-zim-wiki")

        @JvmField
        val ZIMHEADER: Pattern = Pattern.compile(
            "^Content-Type: text/x-zim-wiki(\r\n|\r|\n)" +
                    "Wiki-Format: zim \\d+\\.\\d+(\r\n|\r|\n)" +
                    "Creation-Date: \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[.+:\\d]+"
        )

        // groups for matching individual parts of the checklist regex
        const val CHECKBOX_LEFT_BRACKET_GROUP = 2
        const val CHECKBOX_SYMBOL_GROUP = 3
        const val CHECKBOX_RIGHT_BRACKET_GROUP = 4
    }
}
