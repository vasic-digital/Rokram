/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * CSV Format Parser - Platform Agnostic
 * Simple CSV parser without external dependencies
 *
 *########################################################*/
package digital.vasic.yole.format.csv

import digital.vasic.yole.format.*

/**
 * CSV configuration
 */
data class CsvConfig(
    val delimiter: Char = ',',
    val quote: Char = '"',
    val hasHeader: Boolean = true
) {
    companion object {
        /**
         * Infer CSV configuration from first line
         */
        fun infer(firstLine: String): CsvConfig {
            // Try common delimiters
            val delimiter = when {
                '\t' in firstLine -> '\t'  // TSV
                ';' in firstLine -> ';'    // Semicolon-separated
                '|' in firstLine -> '|'    // Pipe-separated
                ',' in firstLine -> ','    // CSV
                else -> ','                // Default to comma
            }

            // Detect quote character
            val quote = when {
                '\'' in firstLine -> '\''
                else -> '"'
            }

            return CsvConfig(delimiter, quote, hasHeader = true)
        }
    }
}

/**
 * Represents a parsed CSV table
 */
data class CsvTable(
    val rows: List<List<String>>,
    val headers: List<String>? = null,
    val config: CsvConfig = CsvConfig()
) {
    val rowCount: Int get() = rows.size
    val columnCount: Int get() = headers?.size ?: rows.firstOrNull()?.size ?: 0
}

/**
 * CSV format parser
 */
class CsvParser : TextParser {
    override val supportedFormat: TextFormat
        get() = FormatRegistry.getById(TextFormat.ID_CSV) ?: FormatRegistry.formats.last()

    override fun parse(content: String, options: Map<String, Any>): ParsedDocument {
        // Infer configuration from first non-empty line
        val firstLine = content.lines().firstOrNull { it.isNotBlank() } ?: ""
        val config = CsvConfig.infer(firstLine)

        // Parse CSV
        val table = parseCsv(content, config)

        val metadata = buildMap {
            put("rows", table.rowCount.toString())
            put("columns", table.columnCount.toString())
            put("delimiter", config.delimiter.toString())
            put("hasHeader", config.hasHeader.toString())
        }

        // Convert to HTML
        val html = tableToHtml(table)

        return ParsedDocument(
            format = supportedFormat,
            rawContent = content,
            parsedContent = html,
            metadata = metadata
        )
    }

    override fun toHtml(document: ParsedDocument, lightMode: Boolean): String {
        return document.parsedContent
    }

    /**
     * Parse CSV content into a table
     */
    fun parseCsv(content: String, config: CsvConfig = CsvConfig()): CsvTable {
        val lines = content.lines().filter { it.isNotBlank() && !it.trim().startsWith("#") }
        if (lines.isEmpty()) {
            return CsvTable(emptyList(), null, config)
        }

        val rows = mutableListOf<List<String>>()
        for (line in lines) {
            val row = parseLine(line, config.delimiter, config.quote)
            if (row.isNotEmpty()) {
                rows.add(row)
            }
        }

        // Extract headers if configured
        val headers = if (config.hasHeader && rows.isNotEmpty()) {
            rows.removeAt(0)
        } else {
            null
        }

        return CsvTable(rows, headers, config)
    }

    /**
     * Parse a single CSV line into fields
     */
    fun parseLine(line: String, delimiter: Char = ',', quote: Char = '"'): List<String> {
        val fields = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                // Quote character
                char == quote -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == quote) {
                        // Escaped quote (two quotes in a row)
                        currentField.append(quote)
                        i++ // Skip next quote
                    } else {
                        // Toggle quote state
                        inQuotes = !inQuotes
                    }
                }

                // Delimiter (when not in quotes)
                char == delimiter && !inQuotes -> {
                    fields.add(currentField.toString())
                    currentField.clear()
                }

                // Regular character
                else -> {
                    currentField.append(char)
                }
            }

            i++
        }

        // Add last field
        fields.add(currentField.toString())

        return fields
    }

    /**
     * Convert CSV table to HTML
     */
    private fun tableToHtml(table: CsvTable): String {
        return buildString {
            append("<div class='csv-table'>")
            append("<table>")

            // Headers
            if (table.headers != null) {
                append("<thead><tr>")
                for (header in table.headers) {
                    append("<th>")
                    append(header.trim().escapeHtml())
                    append("</th>")
                }
                append("</tr></thead>")
            }

            // Rows
            append("<tbody>")
            for (row in table.rows) {
                append("<tr>")
                for (cell in row) {
                    append("<td>")
                    val trimmed = cell.trim()
                    if (trimmed.isEmpty()) {
                        append("&nbsp;")
                    } else {
                        // Handle newlines in cells
                        append(trimmed.replace("\n", "<br/>").escapeHtml())
                    }
                    append("</td>")
                }
                append("</tr>")
            }
            append("</tbody>")

            append("</table>")
            append("</div>")
        }
    }

    /**
     * Convert CSV to Markdown table format
     */
    fun toMarkdownTable(table: CsvTable): String {
        return buildString {
            // Headers
            if (table.headers != null) {
                append("| ")
                append(table.headers.joinToString(" | ") { it.trim() })
                append(" |\n")

                // Separator line
                append("|")
                append(table.headers.joinToString("|") { " --- " })
                append("|\n")
            }

            // Rows
            for (row in table.rows) {
                append("| ")
                append(row.joinToString(" | ") { it.trim().ifEmpty { "&nbsp;" } })
                append(" |\n")
            }
        }
    }
}
