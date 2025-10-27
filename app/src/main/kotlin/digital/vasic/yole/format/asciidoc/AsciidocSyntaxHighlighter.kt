/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import android.graphics.Color
import android.graphics.Paint
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import other.writeily.format.asciidoc.WrAsciidocHeaderSpanCreator
import other.writeily.format.markdown.WrMarkdownHeaderSpanCreator
import java.util.regex.Pattern

class AsciidocSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    private var _highlightLineEnding: Boolean = false
    private var _highlightCodeChangeFont: Boolean = false
    private var _highlightBiggerHeadings: Boolean = false
    private var _highlightCodeBlock: Boolean = false

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        _highlightLineEnding = _appSettings.isAsciidocHighlightLineEnding
        _highlightBiggerHeadings = _appSettings.isHighlightBiggerHeadings
        _highlightCodeChangeFont = _appSettings.isHighlightCodeMonospaceFont
        _highlightCodeBlock = _appSettings.isHighlightCodeBlock
        _delay = _appSettings.asciidocHighlightingDelay
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()
        createSmallBlueLinkSpans()

        if (_highlightBiggerHeadings) {
            createSpanForMatches(
                HEADING_ASCIIDOC, WrAsciidocHeaderSpanCreator(
                    _spannable,
                    if (_isDarkMode) AD_FORECOLOR_DARK_HEADING else AD_FORECOLOR_LIGHT_HEADING
                )
            )
            createSpanForMatches(
                HEADING_MD, WrMarkdownHeaderSpanCreator(
                    _spannable,
                    if (_isDarkMode) AD_FORECOLOR_DARK_HEADING else AD_FORECOLOR_LIGHT_HEADING
                )
            )
        } else {
            createSpanForMatches(
                HEADING, HighlightSpan().setForeColor(
                    if (_isDarkMode) AD_FORECOLOR_DARK_HEADING else AD_FORECOLOR_LIGHT_HEADING
                )
            )
        }

        if (_highlightCodeChangeFont) {
            createMonospaceSpanForMatches(MONOSPACE)
            createMonospaceSpanForMatches(BLOCK_DELIMITED_LISTING)
            createMonospaceSpanForMatches(BLOCK_DELIMITED_LITERAL)
            createMonospaceSpanForMatches(LIST_UNORDERED)
            createMonospaceSpanForMatches(LIST_ORDERED)
            createMonospaceSpanForMatches(LIST_DESCRIPTION)
            createMonospaceSpanForMatches(ATTRIBUTE_DEFINITION)
            createMonospaceSpanForMatches(ATTRIBUTE_REFERENCE)
            createMonospaceSpanForMatches(ADMONITION)
            createMonospaceSpanForMatches(SQUAREBRACKETS)
        }

        createSpanForMatches(BOLD, HighlightSpan().setBold(true))
        createSpanForMatches(ITALICS, HighlightSpan().setItalic(true))

        createSpanForMatches(
            LINK_PATTERN, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            XREF_PATTERN, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            IMAGE_PATTERN, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            INCLUDE_PATTERN, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            LIST_UNORDERED, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            LIST_ORDERED, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )
        createSpanForMatches(
            LIST_DESCRIPTION, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_LINK else AD_FORECOLOR_LIGHT_LINK
            )
        )

        createSpanForMatches(
            ADMONITION,
            HighlightSpan().setBold(true).setForeColor(AD_FORECOLOR_ADMONITION)
        )

        createSpanForMatches(
            SQUAREBRACKETS, HighlightSpan().setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_SQUAREBRACKETS
                else AD_BACKCOLOR_LIGHT_SQUAREBRACKETS
            )
        )
        createSpanForMatches(
            BLOCKTITLE, HighlightSpan().setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_BLOCKTITLE else AD_BACKCOLOR_LIGHT_BLOCKTITLE
            )
        )
        createSpanForMatches(
            MONOSPACE, HighlightSpan().setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_MONOSPACE else AD_BACKCOLOR_LIGHT_MONOSPACE
            )
        )

        if (_highlightLineEnding) {
            createSpanForMatches(
                HARD_LINE_BREAK, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_MONOSPACE else AD_BACKCOLOR_LIGHT_MONOSPACE
                )
            )
        }

        if (_highlightCodeBlock) {
            createSpanForMatches(
                BLOCK_DELIMITED_LISTING, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_MONOSPACE else AD_BACKCOLOR_LIGHT_MONOSPACE
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_LITERAL, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_MONOSPACE else AD_BACKCOLOR_LIGHT_MONOSPACE
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_QUOTATION, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_QUOTE else AD_BACKCOLOR_LIGHT_QUOTE
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_EXAMPLE, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_EXAMPLE else AD_BACKCOLOR_LIGHT_EXAMPLE
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_SIDEBAR, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_SIDEBAR else AD_BACKCOLOR_LIGHT_SIDEBAR
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_TABLE, HighlightSpan().setBackColor(
                    if (_isDarkMode) AD_BACKCOLOR_DARK_TABLE else AD_BACKCOLOR_LIGHT_TABLE
                )
            )
            createSpanForMatches(
                BLOCK_DELIMITED_COMMENT, HighlightSpan().setForeColor(
                    if (_isDarkMode) AD_FORECOLOR_DARK_COMMENT else AD_FORECOLOR_LIGHT_COMMENT
                )
            )
        }

        createSpanForMatches(
            LINE_COMMENT, HighlightSpan().setForeColor(
                if (_isDarkMode) AD_FORECOLOR_DARK_COMMENT else AD_FORECOLOR_LIGHT_COMMENT
            )
        )
        createSpanForMatches(
            HIGHLIGHT,
            HighlightSpan().setForeColor(AD_FORECOLOR_HIGHLIGHT).setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_HIGHLIGHT else AD_BACKCOLOR_LIGHT_HIGHLIGHT
            )
        )
        createSpanForMatches(
            ATTRIBUTE_DEFINITION, HighlightSpan().setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_ATTRIBUTE else AD_BACKCOLOR_LIGHT_ATTRIBUTE
            )
        )
        createSpanForMatches(
            ATTRIBUTE_REFERENCE, HighlightSpan().setBackColor(
                if (_isDarkMode) AD_BACKCOLOR_DARK_ATTRIBUTE else AD_BACKCOLOR_LIGHT_ATTRIBUTE
            )
        )

        createSubscriptStyleSpanForMatches(SUBSCRIPT)
        createSuperscriptStyleSpanForMatches(SUPERSCRIPT)
        createStrikeThroughSpanForMatches(ROLE_STRIKETHROUGH)
    }

    companion object {
        // check on https://regex101.com/
        // the syntax patterns are simplified
        // WARNING: wrong or invalid patterns causes the app to crash, when a file opens!

        // Monospace syntax (`) must be the outermost formatting pair (i.e., outside the
        // bold formatting pair).
        // Italic syntax (_) is always the innermost formatting pair.

        // simplified, OK for basic examples
        @JvmField
        val BOLD: Pattern = Pattern.compile("(?m)(\\*\\S(?!\\*)(.*?)\\S\\*(?!\\*))")

        // simplified, OK for basic examples
        @JvmField
        val ITALICS: Pattern = Pattern.compile("(?m)(_\\S(?!_)(.*?)\\S_(?!_))")

        // simplified, OK for basic examples, contains only inline code
        @JvmField
        val SUBSCRIPT: Pattern = Pattern.compile("(?m)(~(?!~)(.*?)~(?!~))")

        @JvmField
        val SUPERSCRIPT: Pattern = Pattern.compile("(?m)(\\^(?!\\^)(.*?)\\^(?!\\^))")

        @JvmField
        val MONOSPACE: Pattern = Pattern.compile("(?m)(`(?!`)(.*?)`(?!`))")

        @JvmField
        val HEADING: Pattern = Pattern.compile("(?m)(^(={1,6}|#{1,6})[^\\S\\n][^\\n]+)")

        @JvmField
        val HEADING_ASCIIDOC: Pattern = Pattern.compile("(?m)(^={1,6}[^\\S\\n][^\\n]+)")

        @JvmField
        val HEADING_MD: Pattern = Pattern.compile("(?m)(^#{1,6}[^\\S\\n][^\\n]+)")

        // simplified syntax: In fact, leading spaces are also possible
        @JvmField
        val LIST_ORDERED: Pattern = Pattern.compile("(?m)^(\\.{1,6})( {1})")

        @JvmField
        val LIST_UNORDERED: Pattern = Pattern.compile("(?m)^\\*{1,6} {1}")

        @JvmField
        val LIST_DESCRIPTION: Pattern = Pattern.compile("(?m)^(.+\\S(:{2,4}|;{2,2}))( {1}|[\\r\\n])")

        @JvmField
        val ATTRIBUTE_DEFINITION: Pattern = Pattern.compile("(?m)^:\\S+:")

        @JvmField
        val ATTRIBUTE_REFERENCE: Pattern = Pattern.compile("(?m)\\{\\S+\\}")

        @JvmField
        val LINE_COMMENT: Pattern = Pattern.compile("(?m)^\\/{2}(?!\\/).*$")

        @JvmField
        val ADMONITION: Pattern = Pattern.compile("(?m)^(NOTE: |TIP: |IMPORTANT: |CAUTION: |WARNING: )")

        @JvmField
        val SQUAREBRACKETS: Pattern = Pattern.compile("\\[([^\\[]*)\\]")

        @JvmField
        val HIGHLIGHT: Pattern = Pattern.compile("(?m)(?<!])((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))")

        @JvmField
        val ROLE_GENERAL: Pattern = Pattern.compile("(?m)\\[([^\\[]*)\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))")

        @JvmField
        val ROLE_UNDERLINE: Pattern = Pattern.compile("(?m)\\[\\.underline\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))")

        @JvmField
        val ROLE_STRIKETHROUGH: Pattern = Pattern.compile("(?m)\\[\\.line-through\\]((#(?!#)(.*?)#(?!#))|(##(?!#)(.*?)##))")

        @JvmField
        val HARD_LINE_BREAK: Pattern = Pattern.compile("(?m)(?<=\\S)([^\\S\\r\\n]{1})\\+[\\r\\n]")

        // simplified, OK for basic examples
        @JvmField
        val LINK_PATTERN: Pattern = Pattern.compile("link:\\S*?\\[([^\\[]*)\\]")

        @JvmField
        val XREF_PATTERN: Pattern = Pattern.compile("xref:\\S*?\\[([^\\[]*)\\]")

        @JvmField
        val IMAGE_PATTERN: Pattern = Pattern.compile("image:\\S*?\\[([^\\[]*)\\]")

        @JvmField
        val INCLUDE_PATTERN: Pattern = Pattern.compile("include:\\S*?\\[([^\\[]*)\\]")

        @JvmField
        val BLOCKTITLE: Pattern = Pattern.compile("(?m)^\\.[^(\\s|\\.)].*$")

        // block syntax
        @JvmField
        val BLOCK_DELIMITED_QUOTATION: Pattern = Pattern.compile("(?m)^\\_{4}[\\r\\n]([\\s\\S]+?(?=^\\_{4}[\\r\\n]))^\\_{4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_EXAMPLE: Pattern = Pattern.compile("(?m)^\\={4}[\\r\\n]([\\s\\S]+?(?=^\\={4}[\\r\\n]))^\\={4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_LISTING: Pattern = Pattern.compile("(?m)^\\-{4}[\\r\\n]([\\s\\S]+?(?=^\\-{4}[\\r\\n]))^\\-{4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_LITERAL: Pattern = Pattern.compile("(?m)^\\.{4}[\\r\\n]([\\s\\S]+?(?=^\\.{4}[\\r\\n]))^\\.{4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_SIDEBAR: Pattern = Pattern.compile("(?m)^\\*{4}[\\r\\n]([\\s\\S]+?(?=^\\*{4}[\\r\\n]))^\\*{4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_COMMENT: Pattern = Pattern.compile("(?m)^\\/{4}[\\r\\n]([\\s\\S]+?(?=^\\/{4}[\\r\\n]))^\\/{4}[\\r\\n]")

        @JvmField
        val BLOCK_DELIMITED_TABLE: Pattern = Pattern.compile("(?m)^\\|\\={3}[\\r\\n]([\\s\\S]+?(?=^\\|\\={3}[\\r\\n]))^\\|={3}[\\r\\n]")

        @JvmField
        val DOUBLESPACE_LINE_ENDING: Pattern = Pattern.compile("(?m)(?<=\\S)([^\\S\\r\\n]{2,})[\\r\\n]")

        /*
        https://personal.sron.nl/~pault/[Paul Tol's Notes, Colour schemes and templates, 18 August 2021]

        Colours in default order: '#4477AA', '#EE6677', '#228833', '#CCBB44', '#66CCEE', '#AA3377', '#BBBBBB'.

        BLUE, CYAN, GREEN, YELLOW, RED, PURPLE, GRAY
        */

        private val TOL_BLUE = Color.parseColor("#4477AA")
        private val TOL_CYAN = Color.parseColor("#66CCEE")
        private val TOL_GREEN = Color.parseColor("#228833")
        private val TOL_YELLOW = Color.parseColor("#CCBB44")
        private val TOL_RED = Color.parseColor("#EE6677")
        private val TOL_PURPLE = Color.parseColor("#AA3377")
        private val TOL_GRAY = Color.parseColor("#BBBBBB")

        private val TOL_PALE_BLUE = Color.parseColor("#BBCCEE")
        private val TOL_PALE_CYAN = Color.parseColor("#CCEEFF")
        private val TOL_PALE_GREEN = Color.parseColor("#CCDDAA")
        private val TOL_PALE_YELLOW = Color.parseColor("#EEEEBB")
        private val TOL_PALE_RED = Color.parseColor("#FFCCCC")
        private val TOL_PALE_GRAY = Color.parseColor("#DDDDDD")

        private val TOL_DARK_BLUE = Color.parseColor("#222255")
        private val TOL_DARK_CYAN = Color.parseColor("#225555")
        private val TOL_DARK_GREEN = Color.parseColor("#225522")
        private val TOL_DARK_YELLOW = Color.parseColor("#666633")
        private val TOL_DARK_RED = Color.parseColor("#663333")
        private val TOL_DARK_GRAY = Color.parseColor("#555555")

        // Concrete use of colors for AsciiDoc
        private val AD_FORECOLOR_LIGHT_HEADING = TOL_RED
        private val AD_FORECOLOR_DARK_HEADING = TOL_RED
        private val AD_FORECOLOR_LIGHT_LINK = TOL_DARK_CYAN
        private val AD_FORECOLOR_DARK_LINK = TOL_CYAN
        private val AD_FORECOLOR_LIGHT_LIST = TOL_DARK_YELLOW
        private val AD_FORECOLOR_DARK_LIST = TOL_YELLOW
        private val AD_FORECOLOR_ADMONITION = TOL_RED

        private val AD_BACKCOLOR_LIGHT_QUOTE = TOL_PALE_GREEN
        private val AD_BACKCOLOR_DARK_QUOTE = TOL_DARK_GREEN
        private val AD_BACKCOLOR_LIGHT_EXAMPLE = TOL_PALE_BLUE
        private val AD_BACKCOLOR_DARK_EXAMPLE = TOL_DARK_BLUE
        private val AD_BACKCOLOR_LIGHT_SIDEBAR = TOL_PALE_RED
        private val AD_BACKCOLOR_DARK_SIDEBAR = TOL_DARK_RED
        private val AD_BACKCOLOR_LIGHT_TABLE = TOL_PALE_YELLOW
        private val AD_BACKCOLOR_DARK_TABLE = TOL_DARK_YELLOW
        private val AD_BACKCOLOR_LIGHT_ATTRIBUTE = TOL_PALE_CYAN
        private val AD_BACKCOLOR_DARK_ATTRIBUTE = TOL_DARK_CYAN
        private val AD_BACKCOLOR_LIGHT_MONOSPACE = TOL_PALE_GRAY
        private val AD_BACKCOLOR_DARK_MONOSPACE = TOL_DARK_GRAY
        private val AD_BACKCOLOR_LIGHT_SQUAREBRACKETS = TOL_PALE_GRAY
        private val AD_BACKCOLOR_DARK_SQUAREBRACKETS = TOL_DARK_GRAY
        private val AD_BACKCOLOR_LIGHT_BLOCKTITLE = TOL_PALE_GRAY
        private val AD_BACKCOLOR_DARK_BLOCKTITLE = TOL_DARK_GRAY

        private val AD_BACKCOLOR_LIGHT_HIGHLIGHT = Color.YELLOW
        private val AD_BACKCOLOR_DARK_HIGHLIGHT = Color.YELLOW
        private val AD_FORECOLOR_HIGHLIGHT = Color.BLACK

        private val AD_FORECOLOR_LIGHT_COMMENT = Color.GRAY
        private val AD_FORECOLOR_DARK_COMMENT = Color.GRAY
    }
}
