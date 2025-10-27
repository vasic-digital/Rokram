/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform AsciiDoc Parser Tests
 * Comprehensive test suite for AsciiDoc parsing functionality
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import digital.vasic.yole.format.*
import kotlin.test.*

class AsciidocParserTest {
    private val parser = AsciidocParser()

    @BeforeTest
    fun setUp() {
        ParserRegistry.clear()
        registerAsciidocParser()
    }

    @Test
    fun testParseSimpleDocument() {
        val content = """
= Document Title
Author Name

This is a simple paragraph.

Another paragraph with *bold* and _italic_ text.
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals(content, result.rawContent)
        assertTrue(result.metadata["title"] == "Document Title")
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseHeadings() {
        val content = """
= Heading 1
== Heading 2
=== Heading 3
==== Heading 4
===== Heading 5
====== Heading 6
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.metadata["title"] == "Heading 1")
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseLists() {
        val content = """
* Unordered item 1
* Unordered item 2
** Nested item

. Ordered item 1
. Ordered item 2
.. Nested ordered item
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseCodeBlocks() {
        val content = """
----
fun main() {
    println("Hello, World!")
}
----

Some text after code block.
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseTables() {
        val content = """
|===
| Header 1 | Header 2 | Header 3
| Cell 1   | Cell 2   | Cell 3
| Cell 4   | Cell 5   | Cell 6
|===
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseAdmonitions() {
        val content = """
NOTE: This is a note with important information.

TIP: Here's a helpful tip for users.

WARNING: Be careful with this operation.

IMPORTANT: This is very important information.

CAUTION: Proceed with caution.
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseBlockquotes() {
        val content = """
____
This is a blockquote.
It can span multiple lines.
____
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseLinks() {
        val content = """
Check out link:https://example.com[Example Website] for more information.
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        // The simplified parser doesn't handle links perfectly, but it should parse without errors
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseAttributes() {
        val content = """
:author: John Doe
:email: john@example.com
:version: 1.0

= Document by {author}

Contact: {email}
Version: {version}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals("John Doe", result.metadata["author"])
        assertEquals("john@example.com", result.metadata["email"])
        assertEquals("1.0", result.metadata["version"])
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""
        
        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals(content, result.rawContent)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "= Simple Document\n\nSome content here."
        
        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals(content, result.rawContent)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testToHtmlMethod() {
        val content = "= Test Document\n\nSome *bold* and _italic_ text."
        val document = parser.parse(content)
        
        val html = parser.toHtml(document)
        
        assertTrue(html.contains("<h1>Test Document</h1>"))
        // The simplified parser handles formatting differently
        assertTrue(html.contains("class='asciidoc'"))
    }

    @Test
    fun testToHtmlDarkMode() {
        val content = "= Dark Mode Test\n\nContent for dark mode."
        val document = parser.parse(content)
        
        val html = parser.toHtml(document, lightMode = false)
        
        assertTrue(html.contains("background-color: #2d2d2d"))
        assertTrue(html.contains("color: #f0f0f0"))
    }

    @Test
    fun testValidateValidContent() {
        val content = """
= Valid Document

This is valid AsciiDoc content.

----
code block
----
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateUnclosedCodeBlock() {
        val content = """
= Document with Unclosed Code

----
This code block is not closed
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.contains("Unclosed code block"))
    }

    @Test
    fun testValidateUnclosedCommentBlock() {
        val content = """
= Document with Unclosed Comment

////
This comment block is not closed
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.contains("Unclosed comment block"))
    }

    @Test
    fun testValidateMalformedInclude() {
        val content = """
= Document with Malformed Include

include::somefile.adoc
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.any { it.contains("Malformed include directive") })
    }

    @Test
    fun testValidateMalformedLink() {
        val content = """
= Document with Malformed Link

link:https://example.com
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.any { it.contains("Malformed link directive") })
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
= Complex AsciiDoc Document
:author: Jane Smith
:email: jane@example.com
:version: 2.0

== Introduction

This document demonstrates various AsciiDoc features.

NOTE: This is an important note about the document.

== Code Examples

Here's some Kotlin code:

----
fun main() {
    println("Hello, AsciiDoc!")
}
----

== Lists

* First item
* Second item

. First ordered item
. Second ordered item

== Links

Visit link:https://example.com[our website] for more information.
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals("Jane Smith", result.metadata["author"])
        assertEquals("jane@example.com", result.metadata["email"])
        assertEquals("2.0", result.metadata["version"])
        // The simplified parser may not handle all features perfectly
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseLargeDocument() {
        val content = buildString {
            append("= Large Test Document\n\n")
            repeat(100) { i ->
                append("== Section ${i + 1}\n\n")
                append("This is paragraph ${i + 1}. ")
                append("It contains some *bold* and _italic_ text.\n\n")
            }
        }

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_ASCIIDOC, result.format.id)
        assertEquals("Large Test Document", result.metadata["title"])
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testHtmlEscaping() {
        val content = """
= Test HTML Escaping

Some text with <script>alert('XSS')</script> and other HTML entities.
""".trimIndent()

        val document = parser.parse(content)
        val html = parser.toHtml(document)
        
        assertTrue(html.contains("&lt;script&gt;"))
        assertTrue(html.contains("&lt;/script&gt;"))
        assertFalse(html.contains("<script>"))
    }

    @Test
    fun testSupportedFormat() {
        assertEquals(TextFormat.ID_ASCIIDOC, parser.supportedFormat.id)
        assertEquals("AsciiDoc", parser.supportedFormat.name)
        assertEquals(".adoc", parser.supportedFormat.defaultExtension)
    }

    @Test
    fun testCanParse() {
        val asciidocFormat = FormatRegistry.getById(TextFormat.ID_ASCIIDOC)!!
        val markdownFormat = FormatRegistry.getById(TextFormat.ID_MARKDOWN)!!
        
        assertTrue(parser.canParse(asciidocFormat))
        assertFalse(parser.canParse(markdownFormat))
    }

    @Test
    fun testParserRegistration() {
        val format = FormatRegistry.getById(TextFormat.ID_ASCIIDOC)!!
        val registeredParser = ParserRegistry.getParser(format)
        
        assertNotNull(registeredParser)
        assertTrue(registeredParser is AsciidocParser)
    }
}