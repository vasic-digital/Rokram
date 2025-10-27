package digital.vasic.yole.format.todotxt

import android.graphics.Typeface
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings

open class TodoTxtBasicSyntaxHighlighter(appSettings: AppSettings) : SyntaxHighlighterBase(appSettings) {

    override fun generateSpans() {
        createSmallBlueLinkSpans()
        createColorSpanForMatches(TodoTxtTask.PATTERN_CONTEXTS, COLOR_CONTEXT)
        createColorSpanForMatches(TodoTxtTask.PATTERN_PROJECTS, COLOR_CATEGORY)
        createStyleSpanForMatches(TodoTxtTask.PATTERN_KEY_VALUE_PAIRS, Typeface.ITALIC)

        // Priorities
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_A, HighlightSpan().setForeColor(COLOR_PRIORITY_A).setBold(true))
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_B, HighlightSpan().setForeColor(COLOR_PRIORITY_B).setBold(true))
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_C, HighlightSpan().setForeColor(COLOR_PRIORITY_C).setBold(true))
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_D, HighlightSpan().setForeColor(COLOR_PRIORITY_D).setBold(true))
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_E, HighlightSpan().setForeColor(COLOR_PRIORITY_E).setBold(true))
        createSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_F, HighlightSpan().setForeColor(COLOR_PRIORITY_F).setBold(true))
        createStyleSpanForMatches(TodoTxtTask.PATTERN_PRIORITY_G_TO_Z, Typeface.BOLD)

        createColorSpanForMatches(TodoTxtTask.PATTERN_CREATION_DATE, if (_isDarkMode) COLOR_DATE_DARK else COLOR_DATE_LIGHT, 1)
        createColorSpanForMatches(TodoTxtTask.PATTERN_DUE_DATE, COLOR_PRIORITY_A, 2, 3)

        // Strike out done tasks
        // Note - as we now sort by start, projects, contexts, tags and due date will be highlighted for done tasks
        createSpanForMatches(TodoTxtTask.PATTERN_DONE, HighlightSpan().setForeColor(if (_isDarkMode) COLOR_DONE_DARK else COLOR_DONE_LIGHT).setStrike(true))
    }

    companion object {
        private const val COLOR_CATEGORY = 0xffef6C00.toInt()
        private const val COLOR_CONTEXT = 0xff88b04b.toInt()

        private const val COLOR_PRIORITY_A = 0xffEF2929.toInt()
        private const val COLOR_PRIORITY_B = 0xffd16900.toInt()
        private const val COLOR_PRIORITY_C = 0xff59a112.toInt()
        private const val COLOR_PRIORITY_D = 0xff0091c2.toInt()
        private const val COLOR_PRIORITY_E = 0xffa952cb.toInt()
        private const val COLOR_PRIORITY_F = 0xff878986.toInt()

        private const val COLOR_DONE_DARK = 0x999d9d9d.toInt()
        private const val COLOR_DONE_LIGHT = 0x993d3d3d.toInt()
        private const val COLOR_DATE_DARK = COLOR_DONE_DARK
        private const val COLOR_DATE_LIGHT = 0xcc6d6d6d.toInt()
    }
}
