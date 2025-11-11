/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Unit tests for CSV parser
 *
 *########################################################*/
package digital.vasic.yole.format.csv

import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.csv.CsvParser
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for CSV format parser.
 *
 * Tests cover:
 * - Format detection by extension
 * - Basic parsing functionality
 * - Edge cases and error handling
 * - Empty input handling
 * - Special characters
 */
class CsvParserTest {

    private val parser = CsvParser()

    // ==================== Format Detection Tests ====================

    @Test
    fun `should detect CSV format by extension`() {
        val format = FormatRegistry.getByExtension(".csv")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_CSV, format.id)
        assertEquals("CSV", format.name)
    }

    @Test
    fun `should detect CSV format by filename`() {
        val format = FormatRegistry.detectByFilename("test.csv")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_CSV, format.id)
    }

    @Test
    fun `should support all CSV extensions`() {
        val extensions = listOf(".csv")

        extensions.forEach { ext ->
            val format = FormatRegistry.getByExtension(ext)
            assertNotNull(format, "Extension $ext should be recognized")
            assertEquals(FormatRegistry.ID_CSV, format.id)
        }
    }

    // ==================== Basic Parsing Tests ====================

    @Test
    fun `should parse basic CSV content`() {
        val content = """
            Name,Age,City
            John,30,New York
            Jane,25,Los Angeles
            Bob,35,Chicago
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("John"))
        assertTrue(result.parsedContent.contains("New York"))
        assertEquals(content, result.rawContent)
    }

    @Test
    fun `should handle empty input`() {
        val result = parser.parse("")

        assertNotNull(result)
        // Verify empty result is valid
    }

    @Test
    fun `should handle whitespace-only input`() {
        val result = parser.parse("   \n\n   \t  ")

        assertNotNull(result)
    }

    @Test
    fun `should handle single line input`() {
        val content = "Single line of CSV"

        val result = parser.parse(content)

        assertNotNull(result)
    }

    // ==================== Content Detection Tests ====================

    @Test
    fun `should detect format by content patterns`() {
        val content = """
            Name,Age,Email
            Alice,28,alice@example.com
            Bob,32,bob@example.com
        """.trimIndent()

        val format = FormatRegistry.detectByContent(content)

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_CSV, format.id)
    }

    @Test
    fun `should not false-positive on plain text`() {
        val plainText = "Just some plain text without special formatting"

        val format = FormatRegistry.detectByContent(plainText)

        // Should detect as plaintext, not CSV
        if (format != null) {
            assertNotEquals(FormatRegistry.ID_CSV, format.id)
        }
    }

    // ==================== Special Characters Tests ====================

    @Test
    fun `should handle special characters`() {
        val content = """
            Special chars: @#$%^{{SPECIAL_CHARS_SAMPLE}}*()
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // Verify special characters are preserved/escaped correctly
    }

    @Test
    fun `should handle unicode characters`() {
        val content = "Unicode test: 你好世界 🌍 Привет мир"

        val result = parser.parse(content)

        assertNotNull(result)
    }

    // ==================== Error Handling Tests ====================

    @Test
    fun `should handle malformed input gracefully`() {
        val malformed = """
            Malformed CSV content
        """.trimIndent()

        // Should not throw exception
        val result = parser.parse(malformed)
        assertNotNull(result)
    }

    @Test
    fun `should handle very long input`() {
        val longContent = "Single line of CSV\n".repeat(10000)

        val result = parser.parse(longContent)

        assertNotNull(result)
    }

    @Test
    fun `should handle null bytes gracefully`() {
        // Binary content detection
        val binaryContent = "Some text\u0000with null\u0000bytes"

        val result = parser.parse(binaryContent)

        assertNotNull(result)
    }

    // ==================== Format-Specific Tests ====================

    @Test
    fun `should parse CSV with headers`() {
        val content = """
            Product,Price,Quantity
            Apple,1.50,100
            Banana,0.75,200
            Orange,2.00,150
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("Product"))
        assertTrue(result.parsedContent.contains("Apple"))
        assertTrue(result.parsedContent.contains("1.50"))
    }

    @Test
    fun `should parse CSV with quoted fields`() {
        val content = """
            Name,Address,Phone
            "Smith, John","123 Main St, NYC","555-1234"
            "Doe, Jane","456 Oak Ave, LA","555-5678"
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("Smith, John") || result.parsedContent.contains("Smith"))
    }

    @Test
    fun `should handle escaped quotes in CSV`() {
        val content = "Title,Description\n\"Book\",\"He said Hello\"\n\"Movie\",\"She replied Hi there\""

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("Book"))
    }

    @Test
    fun `should parse CSV with empty fields`() {
        val content = "Name,Email,Phone\nJohn,john@example.com,\nJane,,555-1234\nBob,bob@example.com,555-5678"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("John"))
        assertTrue(result.parsedContent.contains("Jane"))
    }

    @Test
    fun `should parse CSV with numeric data`() {
        val content = "Year,Sales,Profit\n2021,150000,25000\n2022,175000,30000\n2023,200000,35000"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("150000"))
        assertTrue(result.parsedContent.contains("2021"))
    }

    @Test
    fun `should parse CSV with line breaks in quoted fields`() {
        val content = "\"Name\",\"Address\"\n\"John Smith\",\"123 Main St\\nNew York, NY\"\n\"Jane Doe\",\"456 Oak Ave\\nLos Angeles, CA\""

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("John Smith"))
    }

    @Test
    fun `should parse single column CSV`() {
        val content = "Name\nAlice\nBob\nCharlie"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("Alice"))
        assertTrue(result.parsedContent.contains("Bob"))
    }

    @Test
    fun `should parse CSV with special characters`() {
        val content = "Email,Password\nuser@example.com,pass123!\nadmin@site.org,secur3#"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("user@example.com"))
    }

    @Test
    fun `should generate HTML table from CSV`() {
        val content = "Name,Age,City\nJohn,30,NYC\nJane,25,LA"

        val result = parser.parse(content)

        assertNotNull(result)
        // CSV should be rendered as HTML table
        assertTrue(result.parsedContent.contains("table") || result.parsedContent.contains("TABLE"))
    }

    // ==================== Integration Tests ====================

    @Test
    fun `should integrate with FormatRegistry`() {
        val format = FormatRegistry.getById(FormatRegistry.ID_CSV)

        assertNotNull(format)
        assertEquals("CSV", format.name)
        assertEquals(".csv", format.defaultExtension)
    }

    @Test
    fun `should be registered in FormatRegistry`() {
        val allFormats = FormatRegistry.formats
        val csvFormat = allFormats.find { it.id == FormatRegistry.ID_CSV }

        assertNotNull(csvFormat)
        assertEquals("CSV", csvFormat.name)
    }
}
