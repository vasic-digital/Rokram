/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.keyvalue

import android.graphics.Typeface
import digital.vasic.yole.format.markdown.MarkdownSyntaxHighlighter
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

class KeyValueSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()
        createSmallBlueLinkSpans()

        createStyleSpanForMatches(PATTERN_KEY_VALUE, Typeface.BOLD)
        createStyleSpanForMatches(PATTERN_KEY_VALUE_QUOTED, Typeface.BOLD)
        createColorSpanForMatches(MarkdownSyntaxHighlighter.LIST_UNORDERED, 0xffef6D00.toInt())
        createStyleSpanForMatches(PATTERN_VCARD_KEY, Typeface.BOLD)
        createStyleSpanForMatches(PATTERN_INI_KEY, Typeface.BOLD)
        createRelativeSizeSpanForMatches(PATTERN_INI_HEADER, 1.25f)
        createColorSpanForMatches(PATTERN_INI_HEADER, 0xffef6D00.toInt())
        createColorSpanForMatches(PATTERN_INI_COMMENT, 0xff88b04b.toInt())
        createColorSpanForMatches(PATTERN_COMMENT, 0xff88b04b.toInt())

        /*
        // Too expensive
        if (getFilepath().toLowerCase().endsWith(".csv")) {
            _profiler.restart("KeyValue: csv");
            createStyleSpanForMatches(spannable, KeyValueHighlighterPattern.PATTERN_CSV.getPattern(), Typeface.BOLD);
        }
        */
    }

    companion object {
        @JvmField
        val PATTERN_KEY_VALUE: Pattern = Pattern.compile("(?im)^([a-z_0-9]+)[-:=]")

        @JvmField
        val PATTERN_KEY_VALUE_QUOTED: Pattern = Pattern.compile("(?i)([\"'][a-z_0-9\\- ]+[\"']\\s*[-:=])")

        @JvmField
        val PATTERN_VCARD_KEY: Pattern = Pattern.compile("(?im)^(?<FIELD>[^\\s:;]+)(;(?<PARAM>[^=:;]+)=\"?(?<VALUE>[^:;]+)\"?)*:")

        @JvmField
        val PATTERN_INI_HEADER: Pattern = Pattern.compile("(?im)^(\\[.*\\])$")

        @JvmField
        val PATTERN_INI_KEY: Pattern = Pattern.compile("(?im)^([a-z_0-9]+)\\s*[=]")

        @JvmField
        val PATTERN_INI_COMMENT: Pattern = Pattern.compile("(?im)^(;.*)$")

        @JvmField
        val PATTERN_COMMENT: Pattern = Pattern.compile("(?im)^((#|//)\\s+.*)$")

        @JvmField
        val PATTERN_CSV: Pattern = Pattern.compile("[,;:]")
    }
}
