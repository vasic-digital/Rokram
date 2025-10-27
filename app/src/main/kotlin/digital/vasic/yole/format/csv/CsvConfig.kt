/*#######################################################
 *
 *   Maintained 2023 by k3b
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.csv

/**
 * Configuration for CSV file format.
 *
 * Implementation detail for csv support. This file should be not have dependencies to
 * android and to Markor-Architecture.
 */
data class CsvConfig(
    val fieldDelimiterChar: Char,
    val quoteChar: Char
) {
    companion object {
        @JvmField
        val DEFAULT = CsvConfig(',', '"')

        @JvmField
        val CSV_DELIMITER_CANDIDATES = charArrayOf(DEFAULT.fieldDelimiterChar, ';', '\t', ':', '|')

        @JvmField
        val CSV_QUOTE_CANDIDATES = charArrayOf(DEFAULT.quoteChar, '\'')

        @JvmStatic
        fun infer(line: String): CsvConfig {
            val csvFieldDelimiterChar = findChar(line, *CSV_DELIMITER_CANDIDATES)
            val csvQuoteChar = findChar(line, *CSV_QUOTE_CANDIDATES)
            return CsvConfig(csvFieldDelimiterChar, csvQuoteChar)
        }

        private fun findChar(line: String, vararg candidates: Char): Char {
            val pos = CsvSyntaxHighlighter.indexOfAny(line, 0, line.length, *candidates)
            return if (pos == -1) candidates[0] else line[pos]
        }
    }
}
