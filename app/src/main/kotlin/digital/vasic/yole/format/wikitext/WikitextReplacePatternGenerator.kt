/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.wikitext

import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.textview.AutoTextFormatter
import digital.vasic.yole.frontend.textview.ReplacePatternGeneratorHelper
import digital.vasic.opoc.format.GsTextUtils
import java.util.regex.Pattern

object WikitextReplacePatternGenerator {
    @JvmField
    val PREFIX_UNORDERED_LIST: Pattern = Pattern.compile("^(\\s*)((\\*)\\s)")

    @JvmField
    val PREFIX_ORDERED_LIST: Pattern = Pattern.compile("^(\\s*)((\\d+|[a-zA-z])(\\.)(\\s+))")

    @JvmField
    val PREFIX_UNCHECKED_LIST: Pattern = Pattern.compile("^(\\s*)(\\[\\s]\\s)")

    @JvmField
    val PREFIX_CHECKED_LIST: Pattern = Pattern.compile("^(\\s*)(\\[(\\*)]\\s)")

    @JvmField
    val PREFIX_CROSSED_LIST: Pattern = Pattern.compile("^(\\s*)(\\[(x)]\\s)")

    @JvmField
    val PREFIX_RIGHT_ARROW_LIST: Pattern = Pattern.compile("^(\\s*)(\\[(>)]\\s)")

    @JvmField
    val PREFIX_LEFT_ARROW_LIST: Pattern = Pattern.compile("^(\\s*)(\\[(<)]\\s)")

    @JvmField
    val PREFIX_LEADING_SPACE: Pattern = Pattern.compile("^(\\s*)")

    @JvmField
    val PREFIX_CHECKBOX_LIST: Pattern = Pattern.compile("^(\\s*)((\\[)[\\sx*>](]\\s))")

    @JvmField
    val formatPatterns = AutoTextFormatter.FormatPatterns(
        PREFIX_UNORDERED_LIST,
        PREFIX_CHECKBOX_LIST,
        PREFIX_ORDERED_LIST,
        0
    )

    @JvmField
    val PREFIX_PATTERNS = arrayOf(
        PREFIX_ORDERED_LIST,
        PREFIX_CHECKED_LIST,
        PREFIX_UNCHECKED_LIST,
        PREFIX_CROSSED_LIST,
        PREFIX_RIGHT_ARROW_LIST,
        PREFIX_LEFT_ARROW_LIST,
        PREFIX_UNORDERED_LIST,
        PREFIX_LEADING_SPACE
    )

    private const val uncheckedReplacement = "$1[ ] "
    private const val checkedReplacement = "$1[*] "
    private const val crossedReplacement = "$1[x] "
    private const val rightArrowReplacement = "$1[>] "
    private const val leftArrowReplacement = "$1[<] "
    private const val unorderedListReplacement = "$1* "
    private const val orderedListReplacement = "$11. "

    /**
     * Set/unset heading level for the line (the lower the level, the higher the count of equal signs)
     * <p>
     * This routine will make the following conditional changes
     * <p>
     * Line is heading of same level as requested -> remove heading
     * Line is heading of different level that that requested -> replace with requested heading
     * Line is not heading -> add heading of specified level
     *
     * @param level heading level
     */
    @JvmStatic
    fun setOrUnsetHeadingWithLevel(level: Int): List<ActionButtonBase.ReplacePattern> {
        val patterns = mutableListOf<ActionButtonBase.ReplacePattern>()

        val numberOfEqualSigns = 7 - level

        val isValidWikitextHeading = numberOfEqualSigns in 2..6
        if (!isValidWikitextHeading) {
            return patterns
        }

        val headingChars = GsTextUtils.repeatChars('=', numberOfEqualSigns)

        patterns.add(removeHeadingCharsForExactHeadingLevel(headingChars))
        patterns.add(replaceDifferentHeadingLevelWithThisLevel(headingChars))
        patterns.add(createHeadingIfNoneThere(headingChars))

        return patterns
    }

    private fun removeHeadingCharsForExactHeadingLevel(headingChars: String): ActionButtonBase.ReplacePattern {
        return ActionButtonBase.ReplacePattern(
            "^\\s{0,3}$headingChars[ \\t](.*)[ \\t]$headingChars\\w*",
            "$1"
        )
    }

    private fun replaceDifferentHeadingLevelWithThisLevel(headingChars: String): ActionButtonBase.ReplacePattern {
        return ActionButtonBase.ReplacePattern(
            "^\\s{0,3}={2,6}([ \\t].*[ \\t])={2,6}",
            "$headingChars$1$headingChars"
        )
    }

    private fun createHeadingIfNoneThere(headingChars: String): ActionButtonBase.ReplacePattern {
        return ActionButtonBase.ReplacePattern(
            "^\\s*?(\\S?.*)\\s*",
            "$headingChars $1 $headingChars"
        )
    }

    @JvmStatic
    fun replaceWithNextStateCheckbox(): List<ActionButtonBase.ReplacePattern> {
        val replacePatterns = mutableListOf<ActionButtonBase.ReplacePattern>()

        // toggle order: no checkbox -> unchecked -> checked -> crossed -> arrow -> unchecked -> ...
        replacePatterns.addAll(toggleCheckboxToNextState())
        replacePatterns.addAll(replaceOtherPrefixesWithUncheckedBox())
        return replacePatterns
    }

    @JvmStatic
    fun removeCheckbox(): List<ActionButtonBase.ReplacePattern> {
        val anyCheckboxItem = Pattern.compile("^(\\s*)(\\[([ x*><])]\\s)")
        return listOf(ActionButtonBase.ReplacePattern(anyCheckboxItem, "$1"))
    }

    private fun toggleCheckboxToNextState(): List<ActionButtonBase.ReplacePattern> {
        return listOf(
            ActionButtonBase.ReplacePattern(PREFIX_UNCHECKED_LIST, checkedReplacement),
            ActionButtonBase.ReplacePattern(PREFIX_CHECKED_LIST, crossedReplacement),
            ActionButtonBase.ReplacePattern(PREFIX_CROSSED_LIST, rightArrowReplacement),
            ActionButtonBase.ReplacePattern(PREFIX_RIGHT_ARROW_LIST, leftArrowReplacement),
            ActionButtonBase.ReplacePattern(PREFIX_LEFT_ARROW_LIST, uncheckedReplacement)
        )
    }

    private fun replaceOtherPrefixesWithUncheckedBox(): List<ActionButtonBase.ReplacePattern> {
        val replacePatterns = mutableListOf<ActionButtonBase.ReplacePattern>()
        for (otherPattern in PREFIX_PATTERNS) {
            replacePatterns.add(ActionButtonBase.ReplacePattern(otherPattern, uncheckedReplacement))
        }
        return replacePatterns
    }

    @JvmStatic
    fun replaceWithUnorderedListPrefixOrRemovePrefix(): List<ActionButtonBase.ReplacePattern> {
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_UNORDERED_LIST,
            unorderedListReplacement
        )
    }

    @JvmStatic
    fun replaceWithOrderedListPrefixOrRemovePrefix(): List<ActionButtonBase.ReplacePattern> {
        return ReplacePatternGeneratorHelper.replaceWithTargetPrefixOrRemove(
            PREFIX_PATTERNS,
            PREFIX_ORDERED_LIST,
            orderedListReplacement
        )
    }

    @JvmStatic
    fun deindentOneTab(): List<ActionButtonBase.ReplacePattern> {
        return listOf(ActionButtonBase.ReplacePattern("^\t", ""))
    }

    @JvmStatic
    fun indentOneTab(): List<ActionButtonBase.ReplacePattern> {
        return listOf(ActionButtonBase.ReplacePattern("^", "\t"))
    }
}
