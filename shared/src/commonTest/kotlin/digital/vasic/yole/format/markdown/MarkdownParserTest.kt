/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Markdown Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MarkdownParserTest {

    private val parser = MarkdownParser()

    @Test
    fun testParseSimpleText() {
        val content = "This is plain text."

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("This is plain text"))
    }

    @Test
    fun testParseHeading1() {
        val content = "# Heading 1"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading 1"))
    }

    @Test
    fun testParseHeading2() {
        val content = "## Heading 2"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("Heading 2"))
    }

    @Test
    fun testParseHeading6() {
        val content = "###### Heading 6"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h6>"))
    }

    @Test
    fun testParseBoldAsterisk() {
        val content = "This is **bold** text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseBoldUnderscore() {
        val content = "This is __bold__ text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseItalicAsterisk() {
        val content = "This is *italic* text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseItalicUnderscore() {
        val content = "This is _italic_ text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseStrikethrough() {
        val content = "This is ~~deleted~~ text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<s>deleted</s>"))
    }

    @Test
    fun testParseInlineCode() {
        val content = "Use `code` for inline code"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>code</code>"))
    }

    @Test
    fun testParseCodeBlock() {
        val content = """
            ```
            def hello():
                print("Hello")
            ```
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("<code>"))
        assertTrue(document.parsedContent.contains("def hello()"))
    }

    @Test
    fun testParseCodeBlockWithLanguage() {
        val content = """
            ```python
            print("Hello")
            ```
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("print"))
    }

    @Test
    fun testParseLink() {
        val content = "Visit [Google](https://google.com) for search"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://google.com'>Google</a>"))
    }

    @Test
    fun testParseImage() {
        val content = "![Alt text](image.png)"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<img src='image.png'"))
        assertTrue(document.parsedContent.contains("alt='Alt text'"))
    }

    @Test
    fun testParseUnorderedListAsterisk() {
        val content = """
            * Item 1
            * Item 2
            * Item 3
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<li>Item 1</li>"))
        assertTrue(document.parsedContent.contains("<li>Item 2</li>"))
    }

    @Test
    fun testParseUnorderedListDash() {
        val content = """
            - Item 1
            - Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
    }

    @Test
    fun testParseUnorderedListPlus() {
        val content = """
            + Item 1
            + Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
    }

    @Test
    fun testParseOrderedList() {
        val content = """
            1. First
            2. Second
            3. Third
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<li>First</li>"))
        assertTrue(document.parsedContent.contains("<li>Second</li>"))
    }

    @Test
    fun testParseBlockQuote() {
        val content = "> This is a quote"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("This is a quote"))
    }

    @Test
    fun testParseHorizontalRuleDash() {
        val content = """
            Text above
            ---
            Text below
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<hr>"))
    }

    @Test
    fun testParseHorizontalRuleAsterisk() {
        val content = """
            Text above
            ***
            Text below
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<hr>"))
    }

    @Test
    fun testParseHorizontalRuleUnderscore() {
        val content = """
            Text above
            ___
            Text below
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<hr>"))
    }

    @Test
    fun testParseSimpleTable() {
        val content = """
            | Header 1 | Header 2 |
            |----------|----------|
            | Cell 1   | Cell 2   |
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<th>Header 1</th>"))
        assertTrue(document.parsedContent.contains("<td>Cell 1</td>"))
    }

    @Test
    fun testParseTaskListUnchecked() {
        val content = "- [ ] Unchecked task"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<input type='checkbox'"))
        assertTrue(document.parsedContent.contains("disabled"))
        assertTrue(document.parsedContent.contains("Unchecked task"))
    }

    @Test
    fun testParseTaskListChecked() {
        val content = "- [x] Checked task"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<input type='checkbox'"))
        assertTrue(document.parsedContent.contains("checked"))
        assertTrue(document.parsedContent.contains("Checked task"))
    }

    @Test
    fun testParseBoldAndItalic() {
        val content = "**bold** and *italic*"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            # Main Title

            This is a paragraph with **bold** and *italic* text.

            ## Lists

            * Unordered item 1
            * Unordered item 2

            1. Ordered item 1
            2. Ordered item 2

            ## Links and Images

            Visit [Google](https://google.com) for search.

            ![Sample](image.png)

            ## Code

            Inline `code` example.

            ```python
            print("Hello")
            ```

            ## Table

            | Column 1 | Column 2 |
            |----------|----------|
            | Data 1   | Data 2   |

            > Blockquote example

            ---

            ~~Strikethrough~~ text
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<a href='https://google.com'>"))
        assertTrue(document.parsedContent.contains("<img src='image.png'"))
        assertTrue(document.parsedContent.contains("<code>code</code>"))
        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("<hr>"))
        assertTrue(document.parsedContent.contains("<s>Strikethrough</s>"))
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertNotNull(document)
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "Text"

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("", document.metadata["extension"])
    }

    @Test
    fun testHtmlEscaping() {
        val content = "Text with <html> & special \"characters\""

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "Text"
        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            # Heading
            Some text with **bold** and *italic*.
            [Link](url)
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateUnclosedBrackets() {
        val content = "Visit [Page"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed brackets"))
    }

    @Test
    fun testValidateUnclosedParentheses() {
        val content = "Link [text](url"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed parentheses"))
    }

    @Test
    fun testParseMultipleHeadings() {
        val content = """
            # Heading 1
            ## Heading 2
            ### Heading 3
            #### Heading 4
            ##### Heading 5
            ###### Heading 6
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<h3>"))
        assertTrue(document.parsedContent.contains("<h4>"))
        assertTrue(document.parsedContent.contains("<h5>"))
        assertTrue(document.parsedContent.contains("<h6>"))
    }

    @Test
    fun testParseLinkInParagraph() {
        val content = "Visit [Google](https://google.com) for more info"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://google.com'>Google</a>"))
    }

    @Test
    fun testParseCodeInParagraph() {
        val content = "Use the `print()` function"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>print()</code>"))
    }

    @Test
    fun testParseLargeDocument() {
        val lines = (1..50).map { "* Item $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 50"))
    }

    @Test
    fun testParseHeadingWithFormatting() {
        val content = "# **Bold** Heading"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
    }

    @Test
    fun testParseListWithFormatting() {
        val content = "* Item with **bold** text"

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<li>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseMixedLists() {
        val content = """
            * Unordered 1
            * Unordered 2

            1. Ordered 1
            2. Ordered 2
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("Unordered"))
        assertTrue(document.parsedContent.contains("Ordered"))
    }

    @Test
    fun testParseBlockQuoteMultiLine() {
        val content = """
            > Line 1
            > Line 2
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("Line 1"))
        assertTrue(document.parsedContent.contains("Line 2"))
    }

    @Test
    fun testParseTableWithFormatting() {
        val content = """
            | **Bold** | *Italic* |
            |----------|----------|
            | Cell 1   | Cell 2   |
        """.trimIndent()

        val options = mapOf("filename" to "test.md")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>Italic</em>"))
    }
}
