/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.markdown

import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.frontend.textview.ReplacePatternGeneratorHelper
import digital.vasic.opoc.format.GsTextUtils
import java.util.regex.Pattern

object MarkdownReplacePatternGenerator {

    // TODO: write tests

    @JvmField
    val PREFIX_ORDERED_LIST: Pattern = Pattern.compile("^(\\s*)((\\d+)(\\.|\\))(\\s))")

    @JvmField
    val PREFIX_ATX_HEADING: Pattern = Pattern.compile("^(\\s{0,3})(#{1,6}\\s)")

    @JvmField
    val PREFIX_QUOTE: Pattern = Pattern.compile("^(>\\s)")

    @JvmField
    val PREFIX_CHECKED_LIST: Pattern = Pattern.compile("^(\\s*)((-|\\*|\\+)\\s\\[(x|X)]\\s)")

    @JvmField
    val PREFIX_CHECKBOX_LIST: Pattern = Pattern.compile("^(\\s*)(([-*+]\\s\\[)[\\sxX](]\\s))")

    @JvmField
    val PREFIX_UNCHECKED_LIST: Pattern = Pattern.compile("^(\\s*)((-|\\*|\\+)\\s\\[\\s]\\s)")

    @JvmField
    val PREFIX_UNORDERED_LIST: Pattern = Pattern.compile("^(\\s*)((-|\\*|\\+)\\s)")

    @JvmField
    val PREFIX_LEADING_SPACE: Pattern = Pattern.compile("^(\\s*)")

    @JvmField
    val formatPatterns: AutoTextFormatter.FormatPatterns = AutoTextFormatter.FormatPatterns(
        PREFIX_UNORDERED_LIST,
        PREFIX_CHECKBOX_LIST,
        PREFIX_ORDERED_LIST,
        2
    )

    @JvmField
    val PREFIX_PATTERNS: Array<Pattern> = arrayOf(
        PREFIX_ORDERED_LIST,
        PREFIX_ATX_HEADING,
        PREFIX_QUOTE,
        PREFIX_CHECKED_LIST,
        PREFIX_UNCHECKED_LIST,
        // Unordered has to be after checked list. Otherwise checklist will match as an unordered list.
        PREFIX_UNORDERED_LIST,
        PREFIX_LEADING_SPACE
    )

    private const val ORDERED_LIST_REPLACEMENT = "\$11. "

    /**
     * Set/unset ATX heading level on each selected line
     *
     * This routine will make the following conditional changes
     *
     * Line is heading of same level as requested -> remove heading
     * Line is heading of different level that that requested -> add heading of specified level
     * Line is not heading -> add heading of specified level
     *
     * @param level ATX heading level
     */
    @JvmStatic
    fun setOrUnsetHeadingWithLevel(level: Int): List<ActionButtonBase.ReplacePattern> {
        val patterns = mutableListOf<ActionButtonBase.ReplacePattern>()

        val heading = GsTextUtils.repeatChars('#', level)

        // Replace this exact heading level with nothing
        patterns.add(ActionButtonBase.ReplacePattern("^(\\s{0,3})$heading ", "\$1"))

        // Replace other headings with commonmark-compatible leading space
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_ATX_HEADING, "\$1$heading "))

        // Replace all other prefixes with heading
        for (pp in PREFIX_PATTERNS) {
            patterns.add(ActionButtonBase.ReplacePattern(pp, "$heading\$1 "))
        }

        return patterns
    }

    @JvmStatic
    fun replaceWithUnorderedListPrefixOrRemovePrefix(listChar: String): List<ActionButtonBase.ReplacePattern> {
        val unorderedListReplacement = "\$1$listChar "
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_UNORDERED_LIST,
            unorderedListReplacement
        )
    }

    @JvmStatic
    fun toggleToCheckedOrUncheckedListPrefix(listChar: String): List<ActionButtonBase.ReplacePattern> {
        val unchecked = "\$1$listChar [ ] "
        val checked = "\$1$listChar [x] "
        return ReplacePatternGeneratorHelper.replaceWithTargetPatternOrAlternative(
            PREFIX_PATTERNS,
            PREFIX_UNCHECKED_LIST,
            unchecked,
            checked
        )
    }

    @JvmStatic
    fun replaceWithOrderedListPrefixOrRemovePrefix(): List<ActionButtonBase.ReplacePattern> {
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_ORDERED_LIST,
            ORDERED_LIST_REPLACEMENT
        )
    }

    @JvmStatic
    fun toggleQuote(): List<ActionButtonBase.ReplacePattern> {
        return ReplacePatternGeneratorHelper.replaceWithTargetPatternOrAlternative(
            PREFIX_PATTERNS,
            PREFIX_QUOTE,
            ">\$1 ",
            ""
        )
    }
}
