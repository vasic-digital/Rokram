/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * CSV Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.csv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CsvParserTest {

    private val parser = CsvParser()

    @Test
    fun testParseSimpleLine() {
        val fields = parser.parseLine("a,b,c")

        assertEquals(3, fields.size)
        assertEquals("a", fields[0])
        assertEquals("b", fields[1])
        assertEquals("c", fields[2])
    }

    @Test
    fun testParseLineWithSpaces() {
        val fields = parser.parseLine("  a  ,  b  ,  c  ")

        assertEquals(3, fields.size)
        assertEquals("  a  ", fields[0])
        assertEquals("  b  ", fields[1])
        assertEquals("  c  ", fields[2])
    }

    @Test
    fun testParseLineWithQuotes() {
        val fields = parser.parseLine("\"a\",\"b\",\"c\"")

        assertEquals(3, fields.size)
        assertEquals("a", fields[0])
        assertEquals("b", fields[1])
        assertEquals("c", fields[2])
    }

    @Test
    fun testParseLineWithQuotedDelimiter() {
        val fields = parser.parseLine("\"a,b\",c,d")

        assertEquals(3, fields.size)
        assertEquals("a,b", fields[0])
        assertEquals("c", fields[1])
        assertEquals("d", fields[2])
    }

    @Test
    fun testParseLineWithEscapedQuotes() {
        val fields = parser.parseLine("\"a\"\"b\",c")

        assertEquals(2, fields.size)
        assertEquals("a\"b", fields[0])
        assertEquals("c", fields[1])
    }

    @Test
    fun testParseLineWithEmptyFields() {
        val fields = parser.parseLine("a,,c")

        assertEquals(3, fields.size)
        assertEquals("a", fields[0])
        assertEquals("", fields[1])
        assertEquals("c", fields[2])
    }

    @Test
    fun testParseLineWithTabDelimiter() {
        val fields = parser.parseLine("a\tb\tc", delimiter = '\t')

        assertEquals(3, fields.size)
        assertEquals("a", fields[0])
        assertEquals("b", fields[1])
        assertEquals("c", fields[2])
    }

    @Test
    fun testParseLineWithSemicolonDelimiter() {
        val fields = parser.parseLine("a;b;c", delimiter = ';')

        assertEquals(3, fields.size)
        assertEquals("a", fields[0])
        assertEquals("b", fields[1])
        assertEquals("c", fields[2])
    }

    @Test
    fun testParseSimpleCsv() {
        val content = """
            Name,Age,City
            Alice,30,New York
            Bob,25,London
        """.trimIndent()

        val table = parser.parseCsv(content)

        assertEquals(3, table.headers?.size)
        assertEquals("Name", table.headers?.get(0))
        assertEquals("Age", table.headers?.get(1))
        assertEquals("City", table.headers?.get(2))

        assertEquals(2, table.rowCount)
        assertEquals(3, table.columnCount)

        assertEquals("Alice", table.rows[0][0])
        assertEquals("30", table.rows[0][1])
        assertEquals("New York", table.rows[0][2])

        assertEquals("Bob", table.rows[1][0])
        assertEquals("25", table.rows[1][1])
        assertEquals("London", table.rows[1][2])
    }

    @Test
    fun testParseCsvWithQuotes() {
        val content = """
            "Name","Age","City"
            "Alice Smith",30,"New York"
            "Bob Jones",25,"London, UK"
        """.trimIndent()

        val table = parser.parseCsv(content)

        assertEquals(3, table.headers?.size)
        assertEquals(2, table.rowCount)

        assertEquals("Alice Smith", table.rows[0][0])
        assertEquals("London, UK", table.rows[1][2])
    }

    @Test
    fun testParseCsvWithEmptyCells() {
        val content = """
            A,B,C
            1,,3
            ,5,
        """.trimIndent()

        val table = parser.parseCsv(content)

        assertEquals(2, table.rowCount)
        assertEquals("1", table.rows[0][0])
        assertEquals("", table.rows[0][1])
        assertEquals("3", table.rows[0][2])
        assertEquals("", table.rows[1][0])
        assertEquals("5", table.rows[1][1])
        assertEquals("", table.rows[1][2])
    }

    @Test
    fun testParseTsv() {
        val content = """
            Name${'\t'}Age${'\t'}City
            Alice${'\t'}30${'\t'}New York
            Bob${'\t'}25${'\t'}London
        """.trimIndent()

        val config = CsvConfig.infer(content.lines()[0])
        assertEquals('\t', config.delimiter)

        val table = parser.parseCsv(content, config)

        assertEquals(2, table.rowCount)
        assertEquals("Alice", table.rows[0][0])
        assertEquals("30", table.rows[0][1])
    }

    @Test
    fun testParseSemicolonSeparated() {
        val content = """
            Name;Age;City
            Alice;30;New York
            Bob;25;London
        """.trimIndent()

        val config = CsvConfig.infer(content.lines()[0])
        assertEquals(';', config.delimiter)

        val table = parser.parseCsv(content, config)

        assertEquals(2, table.rowCount)
        assertEquals("Alice", table.rows[0][0])
    }

    @Test
    fun testParseWithComments() {
        val content = """
            # This is a comment
            Name,Age,City
            # Another comment
            Alice,30,New York
            Bob,25,London
        """.trimIndent()

        val table = parser.parseCsv(content)

        assertEquals(2, table.rowCount)
        assertEquals("Name", table.headers?.get(0))
    }

    @Test
    fun testCsvConfigInfer() {
        val csvLine = "a,b,c"
        val config = CsvConfig.infer(csvLine)
        assertEquals(',', config.delimiter)
        assertEquals('"', config.quote)
    }

    @Test
    fun testCsvConfigInferTab() {
        val tsvLine = "a\tb\tc"
        val config = CsvConfig.infer(tsvLine)
        assertEquals('\t', config.delimiter)
    }

    @Test
    fun testCsvConfigInferSemicolon() {
        val line = "a;b;c"
        val config = CsvConfig.infer(line)
        assertEquals(';', config.delimiter)
    }

    @Test
    fun testCsvConfigInferPipe() {
        val line = "a|b|c"
        val config = CsvConfig.infer(line)
        assertEquals('|', config.delimiter)
    }

    @Test
    fun testParseDocument() {
        val content = """
            Name,Age,City
            Alice,30,New York
            Bob,25,London
        """.trimIndent()

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("2", document.metadata["rows"])
        assertEquals("3", document.metadata["columns"])
        assertEquals(",", document.metadata["delimiter"])
        assertTrue(document.parsedContent.contains("<table>"))
    }

    @Test
    fun testHtmlGeneration() {
        val content = """
            Name,Age
            Alice,30
            Bob,25
        """.trimIndent()

        val document = parser.parse(content)
        val html = document.parsedContent

        assertTrue(html.contains("<table>"))
        assertTrue(html.contains("<thead>"))
        assertTrue(html.contains("<tbody>"))
        assertTrue(html.contains("<th>"))
        assertTrue(html.contains("<td>"))
        assertTrue(html.contains("Name"))
        assertTrue(html.contains("Alice"))
        assertTrue(html.contains("30"))
    }

    @Test
    fun testHtmlWithEmptyCells() {
        val content = """
            A,B
            1,
            ,3
        """.trimIndent()

        val document = parser.parse(content)
        val html = document.parsedContent

        assertTrue(html.contains("&nbsp;")) // Empty cells should have &nbsp;
    }

    @Test
    fun testMarkdownTableGeneration() {
        val content = """
            Name,Age
            Alice,30
            Bob,25
        """.trimIndent()

        val table = parser.parseCsv(content)
        val markdown = parser.toMarkdownTable(table)

        assertTrue(markdown.contains("| Name | Age |"))
        assertTrue(markdown.contains("| --- | --- |"))
        assertTrue(markdown.contains("| Alice | 30 |"))
        assertTrue(markdown.contains("| Bob | 25 |"))
    }

    @Test
    fun testMarkdownTableWithEmptyCells() {
        val content = """
            A,B
            1,
            ,3
        """.trimIndent()

        val table = parser.parseCsv(content)
        val markdown = parser.toMarkdownTable(table)

        assertTrue(markdown.contains("&nbsp;"))
    }

    @Test
    fun testEmptyCsv() {
        val content = ""

        val table = parser.parseCsv(content)

        assertEquals(0, table.rowCount)
        assertEquals(0, table.columnCount)
    }

    @Test
    fun testCsvWithOnlyHeader() {
        val content = "Name,Age,City"

        val table = parser.parseCsv(content)

        assertEquals(0, table.rowCount)
        assertEquals(3, table.columnCount)
        assertEquals("Name", table.headers?.get(0))
    }

    @Test
    fun testCsvTableProperties() {
        val headers = listOf("A", "B", "C")
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6")
        )
        val table = CsvTable(rows, headers)

        assertEquals(2, table.rowCount)
        assertEquals(3, table.columnCount)
    }

    @Test
    fun testComplexCsvWithSpecialCharacters() {
        val content = """
            "Product","Price","Description"
            "Widget A","$10.99","A ""great"" widget"
            "Widget B","$15.99","Contains, comma"
        """.trimIndent()

        val table = parser.parseCsv(content)

        assertEquals(2, table.rowCount)
        assertEquals("Widget A", table.rows[0][0])
        assertEquals("$10.99", table.rows[0][1])
        assertEquals("A \"great\" widget", table.rows[0][2])
        assertEquals("Contains, comma", table.rows[1][2])
    }

    @Test
    fun testLargeTable() {
        val rows = (1..100).map { "row$it,value$it" }
        val content = "Header1,Header2\n" + rows.joinToString("\n")

        val table = parser.parseCsv(content)

        assertEquals(100, table.rowCount)
        assertEquals(2, table.columnCount)
        assertEquals("row1", table.rows[0][0])
        assertEquals("row100", table.rows[99][0])
    }
}
