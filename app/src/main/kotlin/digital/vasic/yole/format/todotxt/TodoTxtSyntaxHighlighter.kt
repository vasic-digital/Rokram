/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.todotxt

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spannable
import android.text.style.LineBackgroundSpan
import android.text.style.LineHeightSpan
import androidx.annotation.ColorInt
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings
import java.util.regex.Pattern

class TodoTxtSyntaxHighlighter(appSettings: AppSettings) : TodoTxtBasicSyntaxHighlighter(appSettings) {

    private var paragraphSpan: ParagraphDividerSpan? = null

    override fun configure(paint: Paint?): SyntaxHighlighterBase {
        super.configure(paint)

        _delay = _appSettings.highlightingDelayTodoTxt
        paragraphSpan = ParagraphDividerSpan(paint!!, _textColor)

        return this
    }

    override fun generateSpans() {
        // Single span for the whole text - highly performant
        addSpanGroup(paragraphSpan, 0, _spannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        super.generateSpans()
    }

    // Adds spacing and divider line between paragraphs
    class ParagraphDividerSpan(
        paint: Paint,
        @ColorInt private val lineColor: Int
    ) : LineBackgroundSpan, LineHeightSpan, SyntaxHighlighterBase.StaticSpan {

        private val top: Int
        private val ascent: Int
        private val descent: Int
        private val bottom: Int
        private val offset: Int

        init {
            val fm = paint.fontMetricsInt
            offset = Math.abs(fm.ascent) / 2
            top = fm.top
            ascent = fm.ascent
            descent = fm.descent
            bottom = fm.bottom
        }

        override fun drawBackground(
            canvas: Canvas,
            paint: Paint,
            left: Int,
            right: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence,
            start: Int,
            end: Int,
            lineNumber: Int
        ) {
            if (end > 0 && text[end - 1] == '\n') {
                paint.color = lineColor
                paint.strokeWidth = 0f
                canvas.drawLine(left.toFloat(), bottom.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }

        override fun chooseHeight(
            text: CharSequence,
            start: Int,
            end: Int,
            spanstartv: Int,
            v: Int,
            fm: Paint.FontMetricsInt
        ) {
            fm.top = top
            fm.ascent = ascent
            fm.descent = descent
            fm.bottom = bottom

            if (start > 0 && text[start - 1] == '\n') {
                fm.top = top - offset
                fm.ascent = ascent - offset
            }

            if (end > 0 && text[end - 1] == '\n') {
                fm.descent = descent + offset
                fm.bottom = bottom + offset
            }
        }
    }

    companion object {
        private val LINE_OF_TEXT = Pattern.compile("(?m)(.*)?")
    }
}
