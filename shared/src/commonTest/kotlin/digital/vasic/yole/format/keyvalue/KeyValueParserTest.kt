/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * KeyValue Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.keyvalue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KeyValueParserTest {

    private val parser = KeyValueParser()

    @Test
    fun testParseSimpleKeyValue() {
        val content = """
            name=Alice
            age=30
            city=New York
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertEquals("ini", document.metadata["type"])
        assertEquals("3", document.metadata["entries"])
        assertTrue(document.parsedContent.contains("name"))
        assertTrue(document.parsedContent.contains("Alice"))
    }

    @Test
    fun testParseIniWithSections() {
        val content = """
            [database]
            host=localhost
            port=5432

            [server]
            address=0.0.0.0
            port=8080
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("ini", document.metadata["type"])
        assertEquals("4", document.metadata["entries"]) // 4 key-value pairs
        assertEquals("2", document.metadata["sections"]) // 2 sections
        assertTrue(document.parsedContent.contains("[database]"))
        assertTrue(document.parsedContent.contains("[server]"))
    }

    @Test
    fun testParseYamlStyle() {
        val content = """
            name: Alice
            age: 30
            city: New York
            active: true
        """.trimIndent()

        val options = mapOf("filename" to "config.yaml")
        val document = parser.parse(content, options)

        assertEquals("yaml", document.metadata["type"])
        assertEquals(".yaml", document.metadata["extension"])
        assertEquals("4", document.metadata["entries"])
        assertTrue(document.parsedContent.contains("name"))
    }

    @Test
    fun testParseWithComments() {
        val content = """
            # This is a comment
            name=Alice
            ; INI-style comment
            age=30
            // C-style comment
            city=New York
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
        assertTrue(document.parsedContent.contains("# This is a comment"))
        assertTrue(document.parsedContent.contains("; INI-style comment"))
        assertTrue(document.parsedContent.contains("// C-style comment"))
    }

    @Test
    fun testParseQuotedKeys() {
        val content = """
            "name"=Alice
            "full name"=Alice Smith
            'city'=New York
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testParseQuotedValues() {
        val content = """
            name="Alice Smith"
            city="New York"
            description="A person who lives in NYC"
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
        assertTrue(document.parsedContent.contains("Alice Smith"))
    }

    @Test
    fun testParseEmptyValues() {
        val content = """
            name=
            age=30
            city=
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testParseColonSeparator() {
        val content = """
            name: Alice
            age: 30
            city: New York
        """.trimIndent()

        val options = mapOf("filename" to "config.yaml")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
        assertTrue(document.parsedContent.contains("Alice"))
    }

    @Test
    fun testParseJsonFile() {
        val content = """
            {
              "name": "Alice",
              "age": 30,
              "city": "New York"
            }
        """.trimIndent()

        val options = mapOf("filename" to "config.json")
        val document = parser.parse(content, options)

        assertEquals("json", document.metadata["type"])
        assertEquals(".json", document.metadata["extension"])
    }

    @Test
    fun testParseTomlFile() {
        val content = """
            [package]
            name = "myapp"
            version = "1.0.0"

            [dependencies]
            kotlin = "1.9.0"
        """.trimIndent()

        val options = mapOf("filename" to "config.toml")
        val document = parser.parse(content, options)

        assertEquals("toml", document.metadata["type"])
        assertEquals("2", document.metadata["sections"])
    }

    @Test
    fun testParsePropertiesFile() {
        val content = """
            app.name=Yole
            app.version=1.0
            app.author=Milos
        """.trimIndent()

        val options = mapOf("filename" to "app.properties")
        val document = parser.parse(content, options)

        assertEquals("properties", document.metadata["type"])
        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testParseEmptyFile() {
        val content = ""

        val options = mapOf("filename" to "empty.ini")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertEquals("0", document.metadata["entries"])
    }

    @Test
    fun testParseFileWithOnlyComments() {
        val content = """
            # Comment 1
            ; Comment 2
            // Comment 3
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("0", document.metadata["entries"])
    }

    @Test
    fun testParseFileWithOnlySections() {
        val content = """
            [section1]
            [section2]
            [section3]
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("0", document.metadata["entries"])
        assertEquals("3", document.metadata["sections"])
    }

    @Test
    fun testParseComplexIni() {
        val content = """
            ; Configuration file

            [server]
            # Server settings
            host=localhost
            port=8080
            debug=true

            [database]
            host=db.example.com
            port=5432
            name=mydb
            user=admin
            password=secret123
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["sections"])
        assertEquals("8", document.metadata["entries"]) // 8 key-value pairs
    }

    @Test
    fun testParseWithSpaces() {
        val content = """
            name = Alice
            age = 30
            city = New York
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testHtmlGeneration() {
        val content = """
            [section]
            key=value
            # comment
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("<div class='keyvalue'>"))
        assertTrue(html.contains("<pre"))
        assertTrue(html.contains("font-weight: bold")) // Keys should be bold
        assertTrue(html.contains("color: #ef6d00")) // Sections should be colored
        assertTrue(html.contains("color: #88b04b")) // Comments should be colored
    }

    @Test
    fun testHtmlEscaping() {
        val content = """
            html=<div>Test</div>
            ampersand=A & B
            quote=Say "hello"
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;div&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "key=value"
        val options = mapOf("filename" to "test.ini")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testParseVcfExtension() {
        val content = """
            BEGIN:VCARD
            VERSION:3.0
            FN:Alice Smith
            END:VCARD
        """.trimIndent()

        val options = mapOf("filename" to "contact.vcf")
        val document = parser.parse(content, options)

        assertEquals("vcard", document.metadata["type"])
        assertEquals(".vcf", document.metadata["extension"])
    }

    @Test
    fun testParseIcsExtension() {
        val content = """
            BEGIN:VCALENDAR
            VERSION:2.0
            PRODID:-//My App//EN
            END:VCALENDAR
        """.trimIndent()

        val options = mapOf("filename" to "event.ics")
        val document = parser.parse(content, options)

        assertEquals("icalendar", document.metadata["type"])
        assertEquals(".ics", document.metadata["extension"])
    }

    @Test
    fun testParseZimExtension() {
        val content = """
            notebook=MyNotes
            version=1.0
        """.trimIndent()

        val options = mapOf("filename" to "config.zim")
        val document = parser.parse(content, options)

        assertEquals("zim", document.metadata["type"])
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            name=Alice
            age=30
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateInvalidContent() {
        val content = """
            name=Alice
            invalid line without separator
            age=30
        """.trimIndent()

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Line 2"))
    }

    @Test
    fun testValidateIgnoresComments() {
        val content = """
            # This is a comment
            name=Alice
            ; Another comment
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateIgnoresSections() {
        val content = """
            [section]
            key=value
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testParseMultilineValues() {
        // Note: This parser doesn't support true multiline values
        // but it should handle lines gracefully
        val content = """
            name=Alice
            description=Line 1
            age=30
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "key=value"

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("", document.metadata["extension"])
        assertEquals("1", document.metadata["entries"])
    }

    @Test
    fun testParseLargeFile() {
        val lines = (1..100).map { "key$it=value$it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "large.ini")
        val document = parser.parse(content, options)

        assertEquals("100", document.metadata["entries"])
    }

    @Test
    fun testParseYmlExtension() {
        val content = """
            name: Alice
            age: 30
        """.trimIndent()

        val options = mapOf("filename" to "config.yml")
        val document = parser.parse(content, options)

        assertEquals("yaml", document.metadata["type"])
        assertEquals(".yml", document.metadata["extension"])
    }

    @Test
    fun testParseNestedSections() {
        val content = """
            [section1]
            key1=value1

            [section2]
            key2=value2

            [section3]
            key3=value3
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["sections"])
        assertEquals("3", document.metadata["entries"])
    }

    @Test
    fun testParseMixedSeparators() {
        val content = """
            name=Alice
            age:30
            city-New York
        """.trimIndent()

        val options = mapOf("filename" to "config.ini")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["entries"])
    }
}
