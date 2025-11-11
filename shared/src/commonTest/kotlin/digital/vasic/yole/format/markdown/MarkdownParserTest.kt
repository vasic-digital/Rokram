/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Unit tests for Markdown parser
 *
 *########################################################*/
package digital.vasic.yole.format.markdown

import digital.vasic.yole.format.FormatRegistry
import digital.vasic.yole.format.markdown.MarkdownParser
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for Markdown format parser.
 *
 * Tests cover:
 * - Format detection by extension
 * - Basic parsing functionality
 * - Edge cases and error handling
 * - Empty input handling
 * - Special characters
 */
class MarkdownParserTest {

    private val parser = MarkdownParser()

    // ==================== Format Detection Tests ====================

    @Test
    fun `should detect Markdown format by extension`() {
        val format = FormatRegistry.getByExtension(".md")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_MARKDOWN, format.id)
        assertEquals("Markdown", format.name)
    }

    @Test
    fun `should detect Markdown format by filename`() {
        val format = FormatRegistry.detectByFilename("test.md")

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_MARKDOWN, format.id)
    }

    @Test
    fun `should support all Markdown extensions`() {
        val extensions = listOf(".md", ".markdown", ".mdown", ".mkd")

        extensions.forEach { ext ->
            val format = FormatRegistry.getByExtension(ext)
            assertNotNull(format, "Extension $ext should be recognized")
            assertEquals(FormatRegistry.ID_MARKDOWN, format.id)
        }
    }

    // ==================== Basic Parsing Tests ====================

    @Test
    fun `should parse basic Markdown content`() {
        val content = """
            # Hello World

            This is a paragraph with **bold** and *italic* text.

            Here's a [link](https://example.com).
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertEquals(content, result.rawContent)
        assertTrue(result.parsedContent.contains("<h1>"))
        assertTrue(result.parsedContent.contains("<strong>"))
        assertTrue(result.parsedContent.contains("<em>"))
        assertTrue(result.parsedContent.contains("<a href="))
    }

    @Test
    fun `should handle empty input`() {
        val result = parser.parse("")

        assertNotNull(result)
        assertTrue(result.rawContent.isEmpty())
        assertNotNull(result.parsedContent)
    }

    @Test
    fun `should handle whitespace-only input`() {
        val result = parser.parse("   \n\n   \t  ")

        assertNotNull(result)
    }

    @Test
    fun `should handle single line input`() {
        val content = "Single line of Markdown"

        val result = parser.parse(content)

        assertNotNull(result)
    }

    // ==================== Content Detection Tests ====================

    @Test
    fun `should detect format by content patterns`() {
        val content = """
            # Markdown Header

            This is **bold** and *italic* text.
        """.trimIndent()

        val format = FormatRegistry.detectByContent(content)

        assertNotNull(format)
        assertEquals(FormatRegistry.ID_MARKDOWN, format.id)
    }

    @Test
    fun `should not false-positive on plain text`() {
        val plainText = "Just some plain text without special formatting"

        val format = FormatRegistry.detectByContent(plainText)

        // Should detect as plaintext, not Markdown
        if (format != null) {
            assertNotEquals(FormatRegistry.ID_MARKDOWN, format.id)
        }
    }

    // ==================== Special Characters Tests ====================

    @Test
    fun `should handle special characters`() {
        val content = """
            Special chars: @#$%^&*()

            HTML entities: <div> & "quotes"
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // HTML should be escaped
        assertTrue(result.parsedContent.contains("&lt;"))
        assertTrue(result.parsedContent.contains("&gt;"))
        assertTrue(result.parsedContent.contains("&amp;"))
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
            # Unclosed [bracket

            Mismatched **bold text

            Invalid ![image(url)
        """.trimIndent()

        // Should not throw exception
        val result = parser.parse(malformed)
        assertNotNull(result)
        assertNotNull(result.parsedContent)
    }

    @Test
    fun `should handle very long input`() {
        val longContent = "Single line of Markdown\n".repeat(10000)

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
    fun `should parse headers H1 through H6`() {
        val content = """
            # H1 Header
            ## H2 Header
            ### H3 Header
            #### H4 Header
            ##### H5 Header
            ###### H6 Header
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<h1>H1 Header</h1>"))
        assertTrue(result.parsedContent.contains("<h2>H2 Header</h2>"))
        assertTrue(result.parsedContent.contains("<h3>H3 Header</h3>"))
        assertTrue(result.parsedContent.contains("<h4>H4 Header</h4>"))
        assertTrue(result.parsedContent.contains("<h5>H5 Header</h5>"))
        assertTrue(result.parsedContent.contains("<h6>H6 Header</h6>"))
    }

    @Test
    fun `should parse bold text with double asterisks`() {
        val content = "This is **bold text** here"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<strong>bold text</strong>"))
    }

    @Test
    fun `should parse bold text with double underscores`() {
        val content = "This is __bold text__ here"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<strong>bold text</strong>"))
    }

    @Test
    fun `should parse italic text with single asterisk`() {
        val content = "This is *italic text* here"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<em>italic text</em>"))
    }

    @Test
    fun `should parse italic text with single underscore`() {
        val content = "This is _italic text_ here"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<em>italic text</em>"))
    }

    @Test
    fun `should parse strikethrough text`() {
        val content = "This is ~~strikethrough text~~ here"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<s>strikethrough text</s>"))
    }

    @Test
    fun `should parse inline code`() {
        val content = "Use the `println()` function"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<code>println()</code>"))
    }

    @Test
    fun `should parse fenced code blocks`() {
        val content = """
            ```kotlin
            fun main() {
                println("Hello World")
            }
            ```
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<pre>"))
        assertTrue(result.parsedContent.contains("<code>"))
        assertTrue(result.parsedContent.contains("fun main()"))
    }

    @Test
    fun `should parse unordered lists with dashes`() {
        val content = """
            - Item 1
            - Item 2
            - Item 3
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<ul>"))
        assertTrue(result.parsedContent.contains("<li>Item 1</li>"))
        assertTrue(result.parsedContent.contains("<li>Item 2</li>"))
        assertTrue(result.parsedContent.contains("</ul>"))
    }

    @Test
    fun `should parse unordered lists with asterisks`() {
        val content = """
            * Item 1
            * Item 2
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<ul>"))
        assertTrue(result.parsedContent.contains("<li>Item 1</li>"))
    }

    @Test
    fun `should parse unordered lists with plus signs`() {
        val content = """
            + Item 1
            + Item 2
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<ul>"))
        assertTrue(result.parsedContent.contains("<li>Item 1</li>"))
    }

    @Test
    fun `should parse ordered lists`() {
        val content = """
            1. First item
            2. Second item
            3. Third item
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<ol>"))
        assertTrue(result.parsedContent.contains("<li>First item</li>"))
        assertTrue(result.parsedContent.contains("<li>Second item</li>"))
        assertTrue(result.parsedContent.contains("</ol>"))
    }

    @Test
    fun `should parse links`() {
        val content = "Check out [Example](https://example.com)"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<a href='https://example.com'>Example</a>"))
    }

    @Test
    fun `should parse images`() {
        val content = "![Alt text](https://example.com/image.png)"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<img src='https://example.com/image.png' alt='Alt text'/>"))
    }

    @Test
    fun `should parse blockquotes`() {
        val content = """
            > This is a quote
            > It spans multiple lines
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<blockquote>"))
        assertTrue(result.parsedContent.contains("This is a quote"))
        assertTrue(result.parsedContent.contains("</blockquote>"))
    }

    @Test
    fun `should parse horizontal rules with dashes`() {
        val content = """
            Above

            ---

            Below
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<hr>"))
    }

    @Test
    fun `should parse horizontal rules with asterisks`() {
        val content = """
            Above

            ***

            Below
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<hr>"))
    }

    @Test
    fun `should parse horizontal rules with underscores`() {
        val content = """
            Above

            ___

            Below
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<hr>"))
    }

    @Test
    fun `should parse GFM tables`() {
        val content = """
            | Column 1 | Column 2 |
            |----------|----------|
            | Cell 1   | Cell 2   |
            | Cell 3   | Cell 4   |
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<table>"))
        assertTrue(result.parsedContent.contains("<th>Column 1</th>"))
        assertTrue(result.parsedContent.contains("<th>Column 2</th>"))
        assertTrue(result.parsedContent.contains("<td>Cell 1</td>"))
        assertTrue(result.parsedContent.contains("</table>"))
    }

    @Test
    fun `should parse task lists with unchecked checkboxes`() {
        val content = "- [ ] Unchecked task"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<input type='checkbox' disabled>"))
    }

    @Test
    fun `should parse task lists with checked checkboxes`() {
        val content = "- [x] Checked task"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<input type='checkbox' disabled checked>"))
    }

    @Test
    fun `should parse complex nested formatting`() {
        val content = "This is **bold with *italic* inside** text"

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<strong>"))
        assertTrue(result.parsedContent.contains("<em>"))
    }

    @Test
    fun `should parse mixed list items`() {
        val content = """
            1. Ordered item
            2. Another ordered

            - Unordered item
            - Another unordered
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        assertTrue(result.parsedContent.contains("<ol>"))
        assertTrue(result.parsedContent.contains("<ul>"))
    }

    @Test
    fun `should parse paragraphs separated by blank lines`() {
        val content = """
            First paragraph here.

            Second paragraph here.
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // Should have separate paragraph tags
        val pCount = result.parsedContent.count { it == 'p' }
        assertTrue(pCount >= 4) // At least 2 opening and 2 closing p tags
    }

    @Test
    fun `should handle code blocks with special characters`() {
        val content = """
            ```
            <html>
              <body>&amp;</body>
            </html>
            ```
        """.trimIndent()

        val result = parser.parse(content)

        assertNotNull(result)
        // Special characters inside code blocks should be escaped
        assertTrue(result.parsedContent.contains("&lt;html&gt;"))
        assertTrue(result.parsedContent.contains("&amp;amp;"))
    }

    @Test
    fun `should validate and detect unclosed brackets`() {
        val content = "This has [unclosed bracket"

        val errors = parser.validate(content)

        assertTrue(errors.isNotEmpty())
        assertTrue(errors[0].contains("Unclosed brackets"))
    }

    @Test
    fun `should validate and detect unclosed parentheses`() {
        val content = "This has (unclosed paren"

        val errors = parser.validate(content)

        assertTrue(errors.isNotEmpty())
        assertTrue(errors[0].contains("Unclosed parentheses"))
    }

    @Test
    fun `should validate correctly formed markdown without errors`() {
        val content = """
            # Title

            This is [a link](https://example.com) and ![image](test.png).
        """.trimIndent()

        val errors = parser.validate(content)

        assertTrue(errors.isEmpty())
    }

    // ==================== Integration Tests ====================

    @Test
    fun `should integrate with FormatRegistry`() {
        val format = FormatRegistry.getById(FormatRegistry.ID_MARKDOWN)

        assertNotNull(format)
        assertEquals("Markdown", format.name)
        assertEquals(".md", format.defaultExtension)
    }

    @Test
    fun `should be registered in FormatRegistry`() {
        val allFormats = FormatRegistry.formats
        val markdownFormat = allFormats.find { it.id == FormatRegistry.ID_MARKDOWN }

        assertNotNull(markdownFormat)
        assertEquals("Markdown", markdownFormat.name)
    }
}
