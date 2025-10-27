package digital.vasic.yole.format.orgmode

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

class OrgmodeSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    companion object {
        const val COMMON_EMPHASIS_PATTERN = "(?<=(\\n|^|\\s|\\{|\\())([%s])(?=\\S)(?!\\2+\\2)(.*?)\\S\\2(?=(\\n|\$|\\s|\\.|,|:|;|-|\\}|\\)))"
        @JvmField
        val BOLD: Pattern = Pattern.compile(String.format(COMMON_EMPHASIS_PATTERN, "*"))
        @JvmField
        val ITALICS: Pattern = Pattern.compile(String.format(COMMON_EMPHASIS_PATTERN, "/"))
        @JvmField
        val STRIKETHROUGH: Pattern = Pattern.compile(String.format(COMMON_EMPHASIS_PATTERN, "+"))
        @JvmField
        val UNDERLINE: Pattern = Pattern.compile(String.format(COMMON_EMPHASIS_PATTERN, "_"))
        @JvmField
        val CODE_INLINE: Pattern = Pattern.compile(String.format(COMMON_EMPHASIS_PATTERN, "=~"))
        @JvmField
        val HEADING: Pattern = Pattern.compile("(?m)^(\\*+) (.*?)(?=\\n|\$)")
        @JvmField
        val BLOCK: Pattern = Pattern.compile("(?m)(?<=#\\+BEGIN_.{1,15}\$\\s)[\\s\\S]*?(?=#\\+END)")
        @JvmField
        val PREAMBLE: Pattern = Pattern.compile("(?m)^(#\\+)(.*?)(?=\\n|\$)")
        @JvmField
        val COMMENT: Pattern = Pattern.compile("(?m)^(#+) (.*?)(?=\\n|\$)")
        @JvmField
        val LIST_UNORDERED: Pattern = Pattern.compile("(\\n|^)\\s{0,16}([+-])( \\[[ X]\\])?(?= )")
        @JvmField
        val LIST_ORDERED: Pattern = Pattern.compile("(?m)^\\s{0,16}(\\d+)(:?\\.|\\))\\s")
        @JvmField
        val LINK: Pattern = Pattern.compile("\\[\\[.*?]]|<.*?>|https?://\\S+|\\[.*?]\\[.*?]|\\[.*?]\n")

        private const val ORG_COLOR_HEADING = 0xffef6D00.toInt()
        private const val ORG_COLOR_LINK = 0xff1ea3fe.toInt()
        private const val ORG_COLOR_LIST = 0xffdaa521.toInt()
        private const val ORG_COLOR_DIM = 0xff8c8c8c.toInt()
        private const val ORG_COLOR_BLOCK = 0xdddddddd.toInt()
    }

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        _delay = _appSettings.orgmodeHighlightingDelay
        return super.configure(paint)
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()
        createSmallBlueLinkSpans()
        createColorSpanForMatches(HEADING, ORG_COLOR_HEADING)
        createColorSpanForMatches(LINK, ORG_COLOR_LINK)
        createColorSpanForMatches(LIST_UNORDERED, ORG_COLOR_LIST)
        createColorSpanForMatches(LIST_ORDERED, ORG_COLOR_LIST)
        createColorSpanForMatches(PREAMBLE, ORG_COLOR_DIM)
        createColorSpanForMatches(COMMENT, ORG_COLOR_DIM)
        createColorBackgroundSpan(BLOCK, ORG_COLOR_BLOCK)

        createStyleSpanForMatches(BOLD, Typeface.BOLD)
        createStyleSpanForMatches(ITALICS, Typeface.ITALIC)
        createStrikeThroughSpanForMatches(STRIKETHROUGH)
        createColoredUnderlineSpanForMatches(UNDERLINE, Color.BLACK)
        createMonospaceSpanForMatches(CODE_INLINE)
    }
}
