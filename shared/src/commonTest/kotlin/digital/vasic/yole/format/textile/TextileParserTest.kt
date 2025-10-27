/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Textile Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.textile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TextileParserTest {

    private val parser = TextileParser()

    @Test
    fun testParseSimpleText() {
        val content = "This is plain text."

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("This is plain text"))
    }

    @Test
    fun testParseHeading1() {
        val content = "h1. Heading 1"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading 1"))
    }

    @Test
    fun testParseHeading2() {
        val content = "h2. Heading 2"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("Heading 2"))
    }

    @Test
    fun testParseHeading6() {
        val content = "h6. Heading 6"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h6>"))
    }

    @Test
    fun testParseBold() {
        val content = "This is *bold* text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<b>bold</b>"))
    }

    @Test
    fun testParseStrongEmphasis() {
        val content = "This is **strong** text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>strong</strong>"))
    }

    @Test
    fun testParseItalics() {
        val content = "This is _italic_ text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseDoubleEmphasis() {
        val content = "This is __emphasized__ text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("emphasized"))
    }

    @Test
    fun testParseStrikethrough() {
        val content = "This is -deleted- text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<s>deleted</s>"))
    }

    @Test
    fun testParseInlineCode() {
        val content = "Use @code@ for inline code"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>code</code>"))
    }

    @Test
    fun testParseSuperscript() {
        val content = "E = mc^2^"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sup>2</sup>"))
    }

    @Test
    fun testParseSubscript() {
        val content = "H~2~O"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sub>2</sub>"))
    }

    @Test
    fun testParseLink() {
        val content = """"Visit "Google":https://google.com for search""""

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://google.com'>Google</a>"))
    }

    @Test
    fun testParseImage() {
        val content = "!image.png!"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<img src='image.png'"))
    }

    @Test
    fun testParseBlockquote() {
        val content = "bq. This is a quote"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("This is a quote"))
    }

    @Test
    fun testParseUnorderedList() {
        val content = """
            * Item 1
            * Item 2
            * Item 3
        """.trimIndent()

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 2"))
        assertTrue(document.parsedContent.contains("Item 3"))
    }

    @Test
    fun testParseOrderedList() {
        val content = """
            # First
            # Second
            # Third
        """.trimIndent()

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("First"))
        assertTrue(document.parsedContent.contains("Second"))
        assertTrue(document.parsedContent.contains("Third"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            h1. Document Title

            This is a paragraph with *bold* and _italic_ text.

            h2. Lists

            * Unordered item 1
            * Unordered item 2

            # Ordered item 1
            # Ordered item 2

            h2. Code

            Inline @code@ example.

            bq. This is a blockquote.
        """.trimIndent()

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<b>bold</b>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("Unordered item"))
        assertTrue(document.parsedContent.contains("Ordered item"))
        assertTrue(document.parsedContent.contains("<code>code</code>"))
        assertTrue(document.parsedContent.contains("<blockquote>"))
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "test.textile")
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

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "Text"
        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            h1. Heading
            Some text with *bold* and _italic_.
            "Link":url
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateInvalidHeadingLevel() {
        val content = "h7. Invalid heading"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Invalid heading level"))
    }

    @Test
    fun testValidateUnclosedCode() {
        val content = "Text with @unclosed code"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed inline code marker"))
    }

    @Test
    fun testValidateUnclosedImage() {
        val content = "Text with !unclosed image"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed image marker"))
    }

    @Test
    fun testParseMixedFormatting() {
        val content = "*Bold* and _italic_ with -strikethrough- and @code@"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<b>Bold</b>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("<s>strikethrough</s>"))
        assertTrue(document.parsedContent.contains("<code>code</code>"))
    }

    @Test
    fun testParseMultipleHeadings() {
        val content = """
            h1. Level 1
            h2. Level 2
            h3. Level 3
            h4. Level 4
            h5. Level 5
            h6. Level 6
        """.trimIndent()

        val options = mapOf("filename" to "test.textile")
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
        val content = """Visit "Google":https://google.com for search"""

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://google.com'>Google</a>"))
    }

    @Test
    fun testParseCodeInParagraph() {
        val content = "Use the @print()@ function"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>print()</code>"))
    }

    @Test
    fun testParseLargeDocument() {
        val lines = (1..50).map { "* Item $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 50"))
    }

    @Test
    fun testParseHeadingWithFormatting() {
        val content = "h1. *Bold* Heading"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<b>Bold</b>"))
    }

    @Test
    fun testParseListWithFormatting() {
        val content = "* Item with *bold* text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<li>"))
        assertTrue(document.parsedContent.contains("<b>bold</b>"))
    }

    @Test
    fun testParseBlockquoteWithFormatting() {
        val content = "bq. Quote with *bold* text"

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("<b>bold</b>"))
    }

    @Test
    fun testParseMixedLists() {
        val content = """
            * Unordered 1
            * Unordered 2

            # Ordered 1
            # Ordered 2
        """.trimIndent()

        val options = mapOf("filename" to "test.textile")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("Unordered"))
        assertTrue(document.parsedContent.contains("Ordered"))
    }
}
