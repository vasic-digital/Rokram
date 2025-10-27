/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * TiddlyWiki Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.tiddlywiki

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TiddlyWikiParserTest {

    private val parser = TiddlyWikiParser()

    @Test
    fun testParseSimpleText() {
        val content = "This is plain text."

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("This is plain text"))
    }

    @Test
    fun testParseWithMetadata() {
        val content = """
            title: My Tiddler
            tags: tag1 tag2 tag3

            This is the content.
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertEquals("My Tiddler", document.metadata["title"])
        assertEquals("tag1, tag2, tag3", document.metadata["tags"])
        assertTrue(document.parsedContent.contains("This is the content"))
    }

    @Test
    fun testParseWithoutMetadata() {
        val content = "Just plain content without metadata"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("Just plain content"))
    }

    @Test
    fun testParseHeading1() {
        val content = "! Heading 1"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading 1"))
    }

    @Test
    fun testParseHeading2() {
        val content = "!! Heading 2"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("Heading 2"))
    }

    @Test
    fun testParseHeading6() {
        val content = "!!!!!! Heading 6"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h6>"))
    }

    @Test
    fun testParseBold() {
        val content = "This is ''bold'' text"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseItalics() {
        val content = "This is //italic// text"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseUnderline() {
        val content = "This is __underlined__ text"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<u>underlined</u>"))
    }

    @Test
    fun testParseStrikethrough() {
        val content = "This is ~~deleted~~ text"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<s>deleted</s>"))
    }

    @Test
    fun testParseSuperscript() {
        val content = "E = mc^^2^^"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sup>2</sup>"))
    }

    @Test
    fun testParseSubscript() {
        val content = "H,,2,,O"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sub>2</sub>"))
    }

    @Test
    fun testParseSimpleLink() {
        val content = "Visit [[MyPage]]"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='MyPage'>MyPage</a>"))
    }

    @Test
    fun testParseLinkWithDescription() {
        val content = "Visit [[MyPage|My Page]]"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='MyPage'>My Page</a>"))
    }

    @Test
    fun testParseExternalLink() {
        val content = "Visit [ext[https://example.com]]"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://example.com'"))
        assertTrue(document.parsedContent.contains("target='_blank'"))
    }

    @Test
    fun testParseExternalLinkWithDescription() {
        val content = "Visit [ext[https://example.com|Example Site]]"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='https://example.com'"))
        assertTrue(document.parsedContent.contains("Example Site"))
    }

    @Test
    fun testParseImage() {
        val content = "[img[image.png]]"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<img src='image.png'"))
    }

    @Test
    fun testParseUnorderedList() {
        val content = """
            * Item 1
            * Item 2
            * Item 3
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<li>Item 1</li>"))
        assertTrue(document.parsedContent.contains("<li>Item 2</li>"))
    }

    @Test
    fun testParseNestedUnorderedList() {
        val content = """
            * Item 1
            ** Nested 1.1
            ** Nested 1.2
            * Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Nested 1.1"))
    }

    @Test
    fun testParseOrderedList() {
        val content = """
            # First
            # Second
            # Third
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<li>First</li>"))
        assertTrue(document.parsedContent.contains("<li>Second</li>"))
    }

    @Test
    fun testParseNestedOrderedList() {
        val content = """
            # Item 1
            ## Nested 1.1
            ## Nested 1.2
            # Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Nested 1.1"))
    }

    @Test
    fun testParseInlineCode() {
        val content = "Use `code` for inline code"

        val options = mapOf("filename" to "test.tid")
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

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("def hello()"))
    }

    @Test
    fun testParseSingleLineBlockQuote() {
        val content = "> This is a quote"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("This is a quote"))
    }

    @Test
    fun testParseMultiLineBlockQuote() {
        val content = """
            <<<
            This is a quote
            that spans multiple lines
            <<<
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<blockquote>"))
        assertTrue(document.parsedContent.contains("multiple lines"))
    }

    @Test
    fun testParseHorizontalRule() {
        val content = """
            Text above
            ---
            Text below
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<hr>"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            title: Complex Document
            tags: test example

            ! Main Title

            This is a paragraph with ''bold'' and //italic// text.

            !! Lists

            * Unordered item 1
            * Unordered item 2

            # Ordered item 1
            # Ordered item 2

            !! Links and Images

            Visit [[MyPage|My Page]] for more.

            [img[image.png]]

            !! Code

            Inline `code` example.

            ```
            Code block
            ```
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertEquals("Complex Document", document.metadata["title"])
        assertEquals("test, example", document.metadata["tags"])
        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<a href='MyPage'>"))
        assertTrue(document.parsedContent.contains("<img src='image.png'"))
        assertTrue(document.parsedContent.contains("<code>code</code>"))
        assertTrue(document.parsedContent.contains("<pre>"))
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "test.tid")
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

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "Text"
        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            ! Heading
            Some text with ''bold'' and //italic//.
            [[Link]]
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateUnclosedBrackets() {
        val content = "Visit [[Page"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed brackets"))
    }

    @Test
    fun testValidateUnclosedQuotes() {
        val content = "This is ''bold text"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed quotes"))
    }

    @Test
    fun testParseMixedFormatting() {
        val content = "''Bold'' and //italic// and __underline__"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("<u>underline</u>"))
    }

    @Test
    fun testParseMultipleHeadings() {
        val content = """
            ! Level 1
            !! Level 2
            !!! Level 3
            !!!! Level 4
            !!!!! Level 5
            !!!!!! Level 6
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
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
        val content = "Visit [[MyPage|My Page]] for more info"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='MyPage'>My Page</a>"))
    }

    @Test
    fun testParseCodeInParagraph() {
        val content = "Use the `print()` function"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>print()</code>"))
    }

    @Test
    fun testParseLargeDocument() {
        val lines = (1..50).map { "* Item $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 50"))
    }

    @Test
    fun testParseHeadingWithFormatting() {
        val content = "! ''Bold'' Heading"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
    }

    @Test
    fun testParseListWithFormatting() {
        val content = "* Item with ''bold'' text"

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<li>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseMixedLists() {
        val content = """
            * Unordered 1
            * Unordered 2

            # Ordered 1
            # Ordered 2
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("Unordered"))
        assertTrue(document.parsedContent.contains("Ordered"))
    }

    @Test
    fun testParseMetadataWithoutContent() {
        val content = """
            title: Just Metadata
            tags: test
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertEquals("Just Metadata", document.metadata["title"])
        assertEquals("test", document.metadata["tags"])
    }

    @Test
    fun testParseMetadataWithCustomFields() {
        val content = """
            title: My Tiddler
            custom-field: custom value
            another-field: another value

            Content here
        """.trimIndent()

        val options = mapOf("filename" to "test.tid")
        val document = parser.parse(content, options)

        assertEquals("My Tiddler", document.metadata["title"])
        assertTrue(document.parsedContent.contains("Content here"))
    }
}
