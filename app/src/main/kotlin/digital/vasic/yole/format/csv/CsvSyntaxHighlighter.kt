/*#######################################################
 *
 *   Maintained 2023 by k3b
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.csv

import android.graphics.Color
import android.util.Log
import digital.vasic.yole.format.markdown.MarkdownSyntaxHighlighter
import digital.vasic.yole.model.AppSettings
import digital.vasic.opoc.format.GsTextUtils
import digital.vasic.opoc.util.GsResourceUtils
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import kotlin.math.abs

/**
 * Part of Markor-Architecture implementing SyntaxHighlighting for csv.
 */
class CsvSyntaxHighlighter(appSettings: AppSettings) : MarkdownSyntaxHighlighter(appSettings) {

    private var csvDelimiter: Char = ' ' // i.e. -;-
    private var csvQuote: Char = ' ' // i.e. -"-

    private var startOfCol: Int = 0
    private var isEndOfRow: Boolean = true

    override fun generateSpans() {
        super.generateSpans()
        inferCsvConfig()
        createSpanForColumns(COLUMN_COLORS)
    }

    private fun createSpanForColumns(colors: IntArray) {
        var colNumber = -1
        var from = startOfCol
        var to: Int
        while (from < _spannable.length) {
            if (isEndOfRow) colNumber = -1 // -1 == skip coloring
            to = nextDelimiterPos(from)

            createSpanForColumn(from, to, if (colNumber >= 0) colors[colNumber] else Color.BLACK, colNumber)
            colNumber++
            if (colNumber >= colors.size) {
                colNumber = 0
            }
            from = to + 1
        }
    }

    private fun createSpanForColumn(from: Int, to: Int, color: Int, colNumber: Int) {
        if (DEBUG_COLORING) {
            Log.d(TAG, String.format("#%d(%d,%d,%d) = %s", colNumber, from, to, color, _spannable.subSequence(from, to)))
        }
        if (colNumber >= 0 && from > 0 && abs(to - from) >= 0) {
            val span = HighlightSpan().setForeColor(color)

            if (span != null) {
                addSpanGroup(span, from - 1, to) // -1 : also mark delimiter
            }
        }
    }

    private fun inferCsvConfig() {
        val posDelimiter = indexOfAny(_spannable, 0, _spannable.length, *CsvConfig.CSV_DELIMITER_CANDIDATES)
        if (posDelimiter >= 0) {
            csvDelimiter = _spannable[posDelimiter]
            startOfCol = GsTextUtils.beginOfLine(_spannable.toString(), posDelimiter)

            val posEndOfHeader = GsTextUtils.endOfLine(_spannable.toString(), posDelimiter)

            val posQuote = indexOfAny(_spannable, startOfCol, posEndOfHeader, *CsvConfig.CSV_QUOTE_CANDIDATES)
            csvQuote = if (posQuote >= 0) _spannable[posQuote] else CsvConfig.CSV_QUOTE_CANDIDATES[0]
        }
    }

    private fun nextDelimiterPos(startOfColumnContent: Int): Int {
        val csvLen = _spannable.length

        var nextDelimiter = StringUtils.indexOf(_spannable, csvDelimiter.code, startOfColumnContent)
        val nextBeginQuote = StringUtils.indexOf(_spannable, csvQuote.code, startOfColumnContent)

        if (isEndOfRow) {
            val nextComment = StringUtils.indexOf(_spannable, '#'.code, startOfColumnContent)
            if (nextComment > INDEX_NOT_FOUND && nextComment < nextDelimiter && nextComment < nextBeginQuote) {
                return GsTextUtils.endOfLine(_spannable.toString(), nextComment) // return comment as uninterpreted comment until end of line
            }
        }

        isEndOfRow = true

        var nextNl = GsTextUtils.endOfLine(_spannable.toString(), startOfColumnContent)
        if (nextBeginQuote > INDEX_NOT_FOUND && nextBeginQuote < nextDelimiter && nextBeginQuote < nextNl) {
            // column surrounded by quotes
            val nextEndQuote = nextEndQuote(nextBeginQuote + 1)
            if (nextEndQuote == INDEX_NOT_FOUND) {
                return csvLen
            }

            nextDelimiter = StringUtils.indexOf(_spannable, csvDelimiter.code, nextEndQuote)
            nextNl = GsTextUtils.endOfLine(_spannable.toString(), nextEndQuote)
        }

        if (nextNl > INDEX_NOT_FOUND && nextNl < nextDelimiter) {
            // nl comes before next delimiter
            // endOfRow = true
            return nextNl
        }
        if (nextDelimiter > INDEX_NOT_FOUND) {
            // more columns exist in row
            isEndOfRow = false
            return nextDelimiter
        }

        return csvLen
    }

    // skip double quotes -""-
    private fun nextEndQuote(start: Int): Int {
        val csvLen = _spannable.length
        var currentStart = start
        var found: Int
        while (currentStart < csvLen) {
            found = StringUtils.indexOf(_spannable, csvQuote.code, currentStart)

            if (found == INDEX_NOT_FOUND) {
                return INDEX_NOT_FOUND
            }
            if (found + 1 < csvLen && _spannable[found + 1] != csvQuote) {
                return found
            }

            // found double quote -""- : ignore
            currentStart = found + 2
        }

        return INDEX_NOT_FOUND
    }

    companion object {
        // standard green, yellow, cyan is not readable on white background
        // dkgray is not much different from black and not readable with black background
        // blue is difficult to read on black background
        private val COLUMN_COLORS = intArrayOf(
            Color.RED, GsResourceUtils.rgb(150, 150, 255), Color.MAGENTA,
            0xff00b04c.toInt(), // dark green
            0xffdaa500.toInt()  // brown
        )
        private val TAG = CsvSyntaxHighlighter::class.java.simpleName
        private const val DEBUG_COLORING = false

        private const val INDEX_NOT_FOUND = StringUtils.INDEX_NOT_FOUND

        /**
         * Modified version of org.apache.commons.lang3.StringUtils#indexOfAny.
         *
         * Same as [StringUtils.indexOfAny]
         * where you can specify the search interval.
         * License of this function is Apache2
         */
        @JvmStatic
        fun indexOfAny(cs: CharSequence?, csFirst: Int, csLen: Int, vararg searchChars: Char): Int {
            if (StringUtils.isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
                return StringUtils.INDEX_NOT_FOUND
            }
            val csLast = csLen - 1
            val searchLen = searchChars.size
            val searchLast = searchLen - 1
            for (i in csFirst until csLen) {
                val ch = cs!![i]
                for (j in 0 until searchLen) {
                    if (searchChars[j] == ch) {
                        if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                            // ch is a supplementary character
                            if (searchChars[j + 1] == cs[i + 1]) {
                                return i
                            }
                        } else {
                            return i
                        }
                    }
                }
            }
            return StringUtils.INDEX_NOT_FOUND
        }
    }
}
