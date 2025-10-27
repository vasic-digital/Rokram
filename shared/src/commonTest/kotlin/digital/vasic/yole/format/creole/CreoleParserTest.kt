/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Creole Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.creole

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreoleParserTest {

    private val parser = CreoleParser()

    @Test
    fun testParseSimpleText() {
        val content = "This is plain text."

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("This is plain text"))
    }

    @Test
    fun testParseHeading1() {
        val content = "= Heading 1 ="

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading 1"))
    }

    @Test
    fun testParseHeading2() {
        val content = "== Heading 2 =="

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("Heading 2"))
    }

    @Test
    fun testParseHeading6() {
        val content = "====== Heading 6 ======"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h6>"))
    }

    @Test
    fun testParseHeadingWithoutClosing() {
        val content = "= Heading 1"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading 1"))
    }

    @Test
    fun testParseBold() {
        val content = "This is **bold** text"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseItalics() {
        val content = "This is //italic// text"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseBoldAndItalic() {
        val content = "**bold** and //italic//"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseSimpleLink() {
        val content = "Visit [[Page]]"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='Page'>Page</a>"))
    }

    @Test
    fun testParseLinkWithDescription() {
        val content = "Visit [[Page|My Page]]"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='Page'>My Page</a>"))
    }

    @Test
    fun testParseSimpleImage() {
        val content = "{{image.png}}"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<img src='image.png'"))
    }

    @Test
    fun testParseImageWithAlt() {
        val content = "{{image.png|Alt text}}"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<img src='image.png' alt='Alt text'"))
    }

    @Test
    fun testParseUnorderedList() {
        val content = """
            * Item 1
            * Item 2
            * Item 3
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<li>Item 1</li>"))
        assertTrue(document.parsedContent.contains("<li>Item 2</li>"))
        assertTrue(document.parsedContent.contains("<li>Item 3</li>"))
    }

    @Test
    fun testParseNestedUnorderedList() {
        val content = """
            * Item 1
            ** Nested 1.1
            ** Nested 1.2
            * Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Nested 1.1"))
        assertTrue(document.parsedContent.contains("Nested 1.2"))
    }

    @Test
    fun testParseOrderedList() {
        val content = """
            # First
            # Second
            # Third
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<li>First</li>"))
        assertTrue(document.parsedContent.contains("<li>Second</li>"))
        assertTrue(document.parsedContent.contains("<li>Third</li>"))
    }

    @Test
    fun testParseNestedOrderedList() {
        val content = """
            # Item 1
            ## Nested 1.1
            ## Nested 1.2
            # Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Nested 1.1"))
    }

    @Test
    fun testParseHorizontalRule() {
        val content = """
            Text above
            ----
            Text below
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<hr>"))
    }

    @Test
    fun testParseInlineCode() {
        val content = "Use {{{code}}} for inline code"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>code</code>"))
    }

    @Test
    fun testParseCodeBlock() {
        val content = """
            {{{
            def hello():
                print("Hello")
            }}}
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("def hello()"))
    }

    @Test
    fun testParseLineBreak() {
        val content = "Line 1\\\\Line 2"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<br>"))
    }

    @Test
    fun testParseSimpleTable() {
        val content = """
            |=Header 1|=Header 2|
            |Cell 1|Cell 2|
            |Cell 3|Cell 4|
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<th>Header 1</th>"))
        assertTrue(document.parsedContent.contains("<td>Cell 1</td>"))
    }

    @Test
    fun testParseTableWithoutHeaders() {
        val content = """
            |Cell 1|Cell 2|
            |Cell 3|Cell 4|
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<td>Cell 1</td>"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            = Document Title =

            This is a paragraph with **bold** and //italic// text.

            == Lists ==

            * Unordered item 1
            * Unordered item 2

            # Ordered item 1
            # Ordered item 2

            == Links and Images ==

            Visit [[Page|My Page]] for more.

            {{image.png|Sample image}}

            == Code ==

            Inline {{{code}}} example.

            {{{
            Code block
            }}}
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("<a href='Page'>"))
        assertTrue(document.parsedContent.contains("<img src='image.png'"))
        assertTrue(document.parsedContent.contains("<code>code</code>"))
        assertTrue(document.parsedContent.contains("<pre>"))
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "test.creole")
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

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "Text"
        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            = Heading =
            Some text with **bold** and //italic//.
            [[Link]]
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateMalformedTable() {
        val content = "|Cell 1|Cell 2"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Malformed table row"))
    }

    @Test
    fun testValidateUnclosedBrackets() {
        val content = "Visit [[Page"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed brackets"))
    }

    @Test
    fun testValidateUnclosedBraces() {
        val content = "Image {{image.png"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed braces"))
    }

    @Test
    fun testParseMixedFormatting() {
        val content = "**Bold** and //italic// text"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseMultipleHeadings() {
        val content = """
            = Level 1 =
            == Level 2 ==
            === Level 3 ===
            ==== Level 4 ====
            ===== Level 5 =====
            ====== Level 6 ======
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
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
        val content = "Visit [[Page|My Page]] for more info"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='Page'>My Page</a>"))
    }

    @Test
    fun testParseCodeInParagraph() {
        val content = "Use the {{{print()}}} function"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>print()</code>"))
    }

    @Test
    fun testParseLargeDocument() {
        val lines = (1..50).map { "* Item $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 50"))
    }

    @Test
    fun testParseHeadingWithFormatting() {
        val content = "= **Bold** Heading ="

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
    }

    @Test
    fun testParseListWithFormatting() {
        val content = "* Item with **bold** text"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<li>"))
        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseTableWithFormatting() {
        val content = "|**Bold**|//Italic//|"

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<table>"))
        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>Italic</em>"))
    }

    @Test
    fun testParseMixedLists() {
        val content = """
            * Unordered 1
            * Unordered 2

            # Ordered 1
            # Ordered 2
        """.trimIndent()

        val options = mapOf("filename" to "test.creole")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<ol>"))
        assertTrue(document.parsedContent.contains("Unordered"))
        assertTrue(document.parsedContent.contains("Ordered"))
    }
}
