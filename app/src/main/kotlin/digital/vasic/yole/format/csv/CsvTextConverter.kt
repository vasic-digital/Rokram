/*#######################################################
 *
 *   Maintained 2023 by k3b
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.csv

import android.content.Context
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.ICSVParser
import digital.vasic.yole.format.markdown.MarkdownTextConverter
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.Reader
import java.io.StringReader
import kotlin.math.max

/**
 * Part of Yole-Architecture implementing Preview/Export for csv.
 *
 * Converts csv to md and let
 * [TextConverterBase.convertMarkup]
 * do the rest.
 *
 * This way csv columns may contain md expressions like bold text.
 */
@Suppress("WeakerAccess")
class CsvTextConverter : MarkdownTextConverter() {

    private val ext = listOf(".csv", ".tsv", ".tab", ".psv")

    override fun convertMarkup(csvMarkup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        val mdMarkup = Csv2MdTable.toMdTable(csvMarkup)
        return super.convertMarkup(mdMarkup, context, lightMode, enableLineNumbers, file)
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return this.ext.contains(ext)
    }

    protected class Csv2MdTable private constructor(
        csvConfig: CsvConfig,
        csvDataReader: Reader
    ) : Closeable {

        private val csvReader: CSVReader
        private var lineNumber = 0

        init {
            val parser: ICSVParser = CSVParserBuilder()
                .withSeparator(csvConfig.fieldDelimiterChar)
                .withQuoteChar(csvConfig.quoteChar)
                .build()

            csvReader = CSVReaderBuilder(csvDataReader)
                .withSkipLines(0)
                .withCSVParser(parser)
                .withKeepCarriageReturn(true)
                .build()
        }

        private fun readNextCsvColumnLine(): Array<String>? {
            // openCsv-3.10 may throw IOException or RuntimeException,
            // openCsv-5.7 may throw IOException or CsvValidationException.
            var columns: Array<String>?
            do {
                lineNumber++
                columns = csvReader.readNext()
            } while (columns != null && isComment(columns))
            return columns
        }

        private fun isComment(columns: Array<String>): Boolean {
            if (columns.isEmpty()) {
                return true
            }

            // empty line without content
            if (columns.size == 1 && columns[0].trim().isEmpty()) {
                return true
            }

            // comments start with "#" char
            return columns[0].startsWith("#")
        }

        override fun close() {
            csvReader.close()
        }

        companion object {
            const val BUFFER_SIZE = 8096
            private const val MD_LINE_DELIMITER = "\n"
            private const val MD_COL_DELIMITER = "|"
            private const val MD_HEADER_LINE_DELIMITER = "$MD_COL_DELIMITER:---"

            @JvmStatic
            fun toMdTable(csvMarkup: String): String {
                // parser cannot handle empty lines if they are not "\r\n"
                return toMdTable(StringReader(csvMarkup.replace("\n", "\r\n")))
            }

            @JvmStatic
            fun toMdTable(csvMarkup: Reader): String {
                val mdMarkup = StringBuilder()
                try {
                    BufferedReader(csvMarkup, BUFFER_SIZE).use { bufferedReader ->
                        val csvConfig = inferCsvConfiguration(bufferedReader)
                        Csv2MdTable(csvConfig, bufferedReader).use { toMdTable ->
                            val headers = toMdTable.readNextCsvColumnLine()

                            if (headers != null && headers.isNotEmpty()) {
                                addColumnsLine(mdMarkup, headers, headers.size)

                                for (h in headers) {
                                    mdMarkup.append(MD_HEADER_LINE_DELIMITER)
                                }
                                mdMarkup.append(MD_COL_DELIMITER).append(MD_LINE_DELIMITER)

                                var lineColumns: Array<String>?
                                while (toMdTable.readNextCsvColumnLine().also { lineColumns = it } != null) {
                                    addColumnsLine(mdMarkup, lineColumns!!, headers.size)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // invalid csv format in editor should not crash the app when trying to display as html
                    // openCsv-3.10 may throw IOException or RuntimeException,
                    // openCsv-5.7 may throw IOException or CsvValidationException.
                    e.printStackTrace()
                }
                return mdMarkup.toString()
            }

            private fun inferCsvConfiguration(bufferedReader: BufferedReader): CsvConfig {
                // remember where we started.
                bufferedReader.mark(BUFFER_SIZE)
                try {
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        val trimmedLine = line!!.trim()
                        if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                            return CsvConfig.infer(trimmedLine)
                        }
                    }
                    return CsvConfig.DEFAULT
                } finally {
                    // go back to start of csv
                    bufferedReader.reset()
                }
            }

            private fun addColumnsLine(mdMarkup: StringBuilder, columns: Array<String>, headerLength: Int) {
                for (i in 0 until max(headerLength, columns.size)) {
                    addColumnContainingNL(mdMarkup.append(MD_COL_DELIMITER), getCol(columns, i))
                }
                mdMarkup.append(MD_COL_DELIMITER).append(MD_LINE_DELIMITER)
            }

            private fun getCol(columns: Array<String>, i: Int): String {
                return if (i >= 0 && i < columns.size) columns[i] else ""
            }

            private fun addColumnContainingNL(mdMarkup: StringBuilder, col: String) {
                // '|' is a reserved symbol and my not be content of a csv-column
                val sanitizedCol = col.replace('|', '!')

                val lines = sanitizedCol.split(Regex("\r?\n"))
                if (lines.size > 1) {
                    addColumn(mdMarkup, lines[0])
                    for (i in 1 until lines.size) {
                        addColumn(mdMarkup.append("<br/>"), lines[i])
                    }
                } else {
                    addColumn(mdMarkup, sanitizedCol)
                }
            }

            private fun addColumn(mdMarkup: StringBuilder, col: String?) {
                var trimmedCol = col?.trim()
                if (trimmedCol.isNullOrEmpty()) {
                    trimmedCol = "&nbsp;"
                }
                mdMarkup.append(trimmedCol)
            }
        }
    }
}
