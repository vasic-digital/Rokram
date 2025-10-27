/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.markdown

import android.graphics.Paint
import android.graphics.Typeface
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import other.writeily.format.markdown.WrMarkdownHeaderSpanCreator
import java.util.regex.Pattern

open class MarkdownSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    private var _highlightLineEnding: Boolean = false
    private var _highlightCodeChangeFont: Boolean = false
    private var _highlightBiggerHeadings: Boolean = false
    private var _highlightCodeBlock: Boolean = false

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        _highlightLineEnding = _appSettings.isMarkdownHighlightLineEnding
        _highlightCodeChangeFont = _appSettings.isHighlightCodeMonospaceFont
        _highlightBiggerHeadings = _appSettings.isHighlightBiggerHeadings
        _highlightCodeBlock = _appSettings.isHighlightCodeBlock
        _delay = _appSettings.markdownHighlightingDelay
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()
        createSmallBlueLinkSpans()

        if (_highlightBiggerHeadings) {
            createSpanForMatches(HEADING, WrMarkdownHeaderSpanCreator(_spannable, MD_COLOR_HEADING))
        } else {
            createColorSpanForMatches(HEADING, MD_COLOR_HEADING)
        }

        createColorSpanForMatches(LINK, MD_COLOR_LINK)
        createColorSpanForMatches(LIST_UNORDERED, MD_COLOR_LIST)
        createColorSpanForMatches(LIST_ORDERED, MD_COLOR_LIST)

        if (_highlightLineEnding) {
            createColorBackgroundSpan(DOUBLESPACE_LINE_ENDING, MD_COLOR_CODEBLOCK)
        }

        createStyleSpanForMatches(BOLD, Typeface.BOLD)
        createStyleSpanForMatches(ITALICS, Typeface.ITALIC)
        createColorSpanForMatches(QUOTATION, MD_COLOR_QUOTE)
        createStrikeThroughSpanForMatches(STRIKETHROUGH)

        if (_highlightCodeChangeFont) {
            createMonospaceSpanForMatches(CODE)
        }

        if (_highlightCodeBlock) {
            createColorBackgroundSpan(CODE, MD_COLOR_CODEBLOCK)
        }
    }

    companion object {
        @JvmField
        val BOLD: Pattern = Pattern.compile("(?<=(\\n|^|\\s|\\[|\\{|\\())(([*_]){2,3})(?=\\S)(.*?)\\S\\2(?=(\\n|$|\\s|\\.|,|:|;|-|\\]|\\}|\\)))")

        @JvmField
        val ITALICS: Pattern = Pattern.compile("(?<=(\\n|^|\\s|\\[|\\{|\\())([*_])(?=((?!\\2)|\\2{2,}))(?=\\S)(.*?)\\S\\2(?=(\\n|$|\\s|\\.|,|:|;|-|\\]|\\}|\\)))")

        @JvmField
        val HEADING: Pattern = Pattern.compile("(?m)((^#{1,6}[^\\S\\n][^\\n]+)|((\\n|^)[^\\s]+.*?\\n(-{2,}|={2,})[^\\S\\n]*$))")

        @JvmField
        val HEADING_SIMPLE: Pattern = Pattern.compile("(?m)^(#{1,6}\\s.*$)")

        // Group 1 matches image, Group 2 matches text, group 3 matches path
        @JvmField
        val LINK: Pattern = Pattern.compile("(?m)(!)?\\[([^]]*)]\\(([^()]*(?:\\([^()]*\\)[^()]*)*)\\)")

        @JvmField
        val LIST_UNORDERED: Pattern = Pattern.compile("(\\n|^)\\s{0,16}([*+-])( \\[[ xX]\\])?(?= )")

        @JvmField
        val LIST_ORDERED: Pattern = Pattern.compile("(?m)^\\s{0,16}(\\d+)(:?\\.|\\))\\s")

        @JvmField
        val QUOTATION: Pattern = Pattern.compile("(\\n|^)>")

        @JvmField
        val STRIKETHROUGH: Pattern = Pattern.compile("~{2}(.*?)\\S~{2}")

        @JvmField
        val CODE: Pattern = Pattern.compile("(?m)(`(?!`)(.*?)`)|(^[^\\S\\n]{4}(?![0-9\\-*+]).*$)")

        @JvmField
        val DOUBLESPACE_LINE_ENDING: Pattern = Pattern.compile("(?m)(?<=\\S)([^\\S\\n]{2,})\\n")

        private const val MD_COLOR_HEADING = 0xffef6D00.toInt()
        private const val MD_COLOR_LINK = 0xff1ea3fe.toInt()
        private const val MD_COLOR_LIST = 0xffdaa521.toInt()
        private const val MD_COLOR_QUOTE = 0xff88b04c.toInt()
        private const val MD_COLOR_CODEBLOCK = 0x2bafafaf
    }
}
