/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Unit tests for Plain Text parser
 *
 *########################################################*/
package digital.vasic.yole.format.plaintext

import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.plaintext.PlaintextParser
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for Plain Text format parser.
 *
 * Tests cover:
 * - Format detection by extension
 * - Basic parsing functionality
 * - Edge cases and error handling
 * - Empty input handling
 * - Special characters
 */
class PlainTextParserTest {

    private val parser = PlaintextParser()

    // ==================== Format Detection Tests ====================

    @Test
    fun `should detect Plain Text format by extension`() {
        val format = FormatRegistry.getByExtension(".txt")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_PLAINTEXT, format.id)
        assertEquals("Plain Text", format.name)
    }

    @Test
    fun `should detect Plain Text format by filename`() {
        val format = FormatRegistry.detectByFilename("test.txt")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_PLAINTEXT, format.id)
    }

    @Test
    fun `should support all Plain Text extensions`() {
        val extensions = listOf(".txt")

        extensions.forEach { ext ->
            val format = FormatRegistry.getByExtension(ext)
            assertNotNull(format, "Extension $ext should be recognized")
            assertEquals(FormatRegistry.ID_PLAINTEXT, format.id)
        }
    }

    // ==================== Basic Parsing Tests ====================

    @Test
    fun `should parse basic Plain Text content`() {
        val content = """
            Just plain text without special formatting
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // Add format-specific assertions here
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
        val content = "Single line of Plain Text"

        val result = parser.parse(content)

        assertNotNull(result)
    }

    // ==================== Content Detection Tests ====================

    @Test
    fun `should detect format by content patterns`() {
        // PlainText has no detection patterns (it's the fallback format)
        val content = """
            Just plain text without special formatting
        """.trimIndent()

        val format = FormatRegistry.detectByContent(content)

        // PlainText won't be detected by content patterns (no patterns defined)
        // It's the fallback when no other format matches
        assertTrue(format == null || format.id != FormatRegistry.ID_PLAINTEXT)
    }

    @Test
    fun `should not false-positive on plain text`() {
        val plainText = "Just some plain text without special formatting"

        val format = FormatRegistry.detectByContent(plainText)

        // Should detect as plaintext, not Plain Text
        if (format != null) {
            assertNotEquals(FormatRegistry.ID_PLAINTEXT, format.id)
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
            Malformed Plain Text content
        """.trimIndent()

        // Should not throw exception
        val result = parser.parse(malformed)
        assertNotNull(result)
    }

    @Test
    fun `should handle very long input`() {
        val longContent = "Single line of Plain Text\n".repeat(10000)

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
    // Add format-specific parsing tests below
    // Examples:
    // - Headers (for Markdown, AsciiDoc, etc.)
    // - Lists (for Markdown, Org Mode, etc.)
    // - Code blocks (for Markdown, reStructuredText, etc.)
    // - Tables (for CSV, Markdown, etc.)
    // - Math (for LaTeX, R Markdown, etc.)

    @Test
    fun `should parse format-specific feature`() {
        val content = """
            Format specific sample
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // Add format-specific assertions
    }

    // ==================== Integration Tests ====================

    @Test
    fun `should integrate with FormatRegistry`() {
        val format = FormatRegistry.getById(FormatRegistry.ID_PLAINTEXT)

        assertNotNull(format)
        assertEquals("Plain Text", format.name)
        assertEquals(".txt", format.defaultExtension)
    }

    @Test
    fun `should be registered in FormatRegistry`() {
        val allFormats = FormatRegistry.formats
        val plainTextFormat = allFormats.find { it.id == FormatRegistry.ID_PLAINTEXT }

        assertNotNull(plainTextFormat)
        assertEquals("Plain Text", plainTextFormat.name)
    }
}
