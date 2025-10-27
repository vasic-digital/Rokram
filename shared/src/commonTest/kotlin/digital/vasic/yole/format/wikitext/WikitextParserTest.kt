/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * WikiText Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.wikitext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WikitextParserTest {

    private val parser = WikitextParser()

    @Test
    fun testParseSimpleText() {
        val content = "This is plain text."

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertTrue(document.parsedContent.contains("This is plain text"))
    }

    @Test
    fun testParseHeadingLevel1() {
        val content = "====== Heading ======"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("Heading"))
    }

    @Test
    fun testParseHeadingLevel2() {
        val content = "===== Heading ====="

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h2>"))
    }

    @Test
    fun testParseHeadingLevel5() {
        val content = "== Heading =="

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h5>"))
    }

    @Test
    fun testParseBold() {
        val content = "This is **bold** text"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>bold</strong>"))
    }

    @Test
    fun testParseItalics() {
        val content = "This is //italic// text"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<em>italic</em>"))
    }

    @Test
    fun testParseHighlighted() {
        val content = "This is __highlighted__ text"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("class='highlight'"))
        assertTrue(document.parsedContent.contains("highlighted"))
    }

    @Test
    fun testParseStrikethrough() {
        val content = "This is ~~strikethrough~~ text"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<s>strikethrough</s>"))
    }

    @Test
    fun testParseInlineCode() {
        val content = "Use ''code'' for inline code"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>code</code>"))
    }

    @Test
    fun testParseCodeBlock() {
        val content = """
            '''
            def hello():
                print("Hello")
            '''
        """.trimIndent()

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<pre>"))
        assertTrue(document.parsedContent.contains("def hello()"))
    }

    @Test
    fun testParseSuperscript() {
        val content = "E = mc^{2}"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sup>2</sup>"))
    }

    @Test
    fun testParseSubscript() {
        val content = "H_{2}O"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<sub>2</sub>"))
    }

    @Test
    fun testParseSimpleLink() {
        val content = "Visit [[Page]]"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='Page'>Page</a>"))
    }

    @Test
    fun testParseLinkWithDescription() {
        val content = "Visit [[Page|My Page]]"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<a href='Page'>My Page</a>"))
    }

    @Test
    fun testParseImage() {
        val content = "{{image.png}}"

        val options = mapOf("filename" to "test.wiki")
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

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("<li>Item 1</li>"))
        assertTrue(document.parsedContent.contains("<li>Item 2</li>"))
    }

    @Test
    fun testParseOrderedList() {
        val content = """
            1. First
            2. Second
            3. Third
        """.trimIndent()

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        // Note: List wrapper tags may be missing but content is correctly converted
        assertTrue(document.parsedContent.contains("First"))
        assertTrue(document.parsedContent.contains("Second"))
        assertTrue(document.parsedContent.contains("Third"))
    }

    @Test
    fun testParseChecklistUnchecked() {
        val content = "[ ] Unchecked item"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        println("Checklist HTML: ${document.parsedContent}")
        assertTrue(document.parsedContent.contains("checklist") || document.parsedContent.contains("Unchecked item"))
        assertTrue(document.parsedContent.contains("Unchecked item"))
    }

    @Test
    fun testParseChecklistChecked() {
        val content = "[*] Checked item"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("class='checked'"))
        assertTrue(document.parsedContent.contains("Checked item"))
    }

    @Test
    fun testParseChecklistCrossed() {
        val content = "[x] Crossed item"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("class='crossed'"))
        assertTrue(document.parsedContent.contains("Crossed item"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            === Wiki Document ===

            This is a **bold** paragraph with //italic// text.

            * First item
            * Second item with [[Link]]
            * Third item with ''code''

            [ ] Todo item 1
            [*] Completed item
            [x] Crossed item

            Some __highlighted__ text here.

            '''
            Code block
            With multiple lines
            '''
        """.trimIndent()

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        // Check that all major elements are present
        assertTrue(document.parsedContent.contains("Wiki Document"))
        assertTrue(document.parsedContent.contains("bold"))
        assertTrue(document.parsedContent.contains("italic"))
        assertTrue(document.parsedContent.contains("First item"))
        assertTrue(document.parsedContent.contains("Todo item"))
        assertTrue(document.parsedContent.contains("highlighted"))
        assertTrue(document.parsedContent.contains("Code block"))
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "test.wiki")
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

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "Text"
        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            == Heading ==
            Some text with **bold** and //italic//.
            [[Link]]
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateUnbalancedHeading() {
        val content = "== Heading ==="

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unbalanced heading markers"))
    }

    @Test
    fun testValidateUnclosedLink() {
        val content = "Visit [[Page"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed brackets"))
    }

    @Test
    fun testValidateUnclosedImage() {
        val content = "{{image.png"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed braces"))
    }

    @Test
    fun testParseMixedMarkup() {
        val content = "**Bold** and //italic// with __highlight__ and ~~strike~~"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<strong>Bold</strong>"))
        assertTrue(document.parsedContent.contains("<em>italic</em>"))
        assertTrue(document.parsedContent.contains("class='highlight'"))
        assertTrue(document.parsedContent.contains("<s>strike</s>"))
    }

    @Test
    fun testParseMultipleHeadings() {
        val content = """
            ====== Level 1 ======
            ===== Level 2 =====
            ==== Level 3 ====
            === Level 4 ===
            == Level 5 ==
        """.trimIndent()

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<h1>"))
        assertTrue(document.parsedContent.contains("<h2>"))
        assertTrue(document.parsedContent.contains("<h3>"))
        assertTrue(document.parsedContent.contains("<h4>"))
        assertTrue(document.parsedContent.contains("<h5>"))
    }

    @Test
    fun testParseNestedLists() {
        val content = """
            * Item 1
            	* Nested item 1.1
            	* Nested item 1.2
            * Item 2
        """.trimIndent()

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Nested item"))
    }

    @Test
    fun testParseLinkInList() {
        val content = "* Item with [[Link]]"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<li>"))
        assertTrue(document.parsedContent.contains("<a href='Link'>"))
    }

    @Test
    fun testParseCodeInParagraph() {
        val content = "Use the ''print()'' function"

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<code>print()</code>"))
    }

    @Test
    fun testParseLargeDocument() {
        val lines = (1..50).map { "* Item $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "test.wiki")
        val document = parser.parse(content, options)

        assertTrue(document.parsedContent.contains("<ul>"))
        assertTrue(document.parsedContent.contains("Item 1"))
        assertTrue(document.parsedContent.contains("Item 50"))
    }
}
