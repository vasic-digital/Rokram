/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import digital.vasic.opoc.format.GsTextUtils
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.frontend.textview.ReplacePatternGeneratorHelper
import java.util.regex.Pattern

object AsciidocReplacePatternGenerator {

    // Check on https://regex101.com/
    @JvmField
    val PREFIX_HEADING: Pattern = Pattern.compile("^( {0})(=)(={0,5})( {1})")

    @JvmField
    val PREFIX_HEADING_GT1: Pattern = Pattern.compile("^( {0})(=)(={1,6})( {1})")

    @JvmField
    val PREFIX_UNORDERED_LIST: Pattern = Pattern.compile("^( {0})(\\*)(\\*{0,5})( {1,})")

    @JvmField
    val PREFIX_UNORDERED_LIST_GT1: Pattern = Pattern.compile("^( {0})(\\*)(\\*{1,6})( {1,})")

    @JvmField
    val PREFIX_ORDERED_LIST: Pattern = Pattern.compile("^( {0})(\\.)(\\.{0,5})( {1,})")

    @JvmField
    val PREFIX_ORDERED_LIST_GT1: Pattern = Pattern.compile("^( {0})(\\.)(\\.{1,6})( {1,})")

    @JvmField
    val PREFIX_CHECKBOX_LIST: Pattern = Pattern.compile("^( {0})(\\*{1,6})( \\[( |\\*|x|X)] {1,})")

    @JvmField
    val PREFIX_CHECKED_LIST: Pattern = Pattern.compile("^( {0})(\\*{1,6})( \\[(\\*|x|X)] {1,})")

    @JvmField
    val PREFIX_UNCHECKED_LIST: Pattern = Pattern.compile("^( {0})(\\*{1,6})( \\[( )] {1,})")

    @JvmField
    val PREFIX_LEADING_SPACE: Pattern = Pattern.compile("^( *)")

    @JvmField
    val PREFIX_PATTERNS: Array<Pattern> = arrayOf(
        PREFIX_ORDERED_LIST,
        PREFIX_HEADING,
        PREFIX_CHECKED_LIST,
        PREFIX_UNCHECKED_LIST,
        PREFIX_UNORDERED_LIST,
        PREFIX_LEADING_SPACE
    )

    @JvmField
    val formatPatterns = AutoTextFormatter.FormatPatterns(
        PREFIX_UNORDERED_LIST,
        PREFIX_CHECKBOX_LIST,
        PREFIX_ORDERED_LIST,
        2
    )

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
        val heading = GsTextUtils.repeatChars('=', level)

        // Pattern 1: Remove this exact heading level
        patterns.add(ActionButtonBase.ReplacePattern("^$heading ", ""))

        // Pattern 2: Replace other headings
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_HEADING, "$heading "))

        // Pattern 3+: Replace all other prefixes with heading
        for (pp in PREFIX_PATTERNS) {
            patterns.add(ActionButtonBase.ReplacePattern(pp, "$heading "))
        }

        return patterns
    }

    @JvmStatic
    fun indentLevel(): List<ActionButtonBase.ReplacePattern> {
        val patterns = mutableListOf<ActionButtonBase.ReplacePattern>()

        // Insert one (1) level: duplicate $2, which is "=" or "*" or "."
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_HEADING, "\$1\$2\$2\$3\$4"))
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_ORDERED_LIST, "\$1\$2\$2\$3\$4"))
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_UNORDERED_LIST, "\$1\$2\$2\$3\$4"))

        return patterns
    }

    @JvmStatic
    fun deindentLevel(): List<ActionButtonBase.ReplacePattern> {
        val patterns = mutableListOf<ActionButtonBase.ReplacePattern>()

        // Remove one (1) level: remove $2, which is the first "=" or "*" or "."
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_HEADING_GT1, "\$1\$3\$4"))
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_ORDERED_LIST_GT1, "\$1\$3\$4"))
        patterns.add(ActionButtonBase.ReplacePattern(PREFIX_UNORDERED_LIST_GT1, "\$1\$3\$4"))

        return patterns
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
    fun replaceWithUnorderedListPrefixOrRemovePrefix(listChar: String): List<ActionButtonBase.ReplacePattern> {
        val unorderedListReplacement = "\$1$listChar "
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_UNORDERED_LIST,
            unorderedListReplacement
        )
    }

    @JvmStatic
    fun replaceWithOrderedListPrefixOrRemovePrefix(listChar: String): List<ActionButtonBase.ReplacePattern> {
        val orderedListReplacement = "\$1$listChar "
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_ORDERED_LIST,
            orderedListReplacement
        )
    }
}
