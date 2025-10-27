/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Plaintext Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.plaintext

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PlaintextParserTest {

    private val parser = PlaintextParser()

    @Test
    fun testParsePlainText() {
        val content = "This is plain text content.\nWith multiple lines."

        val options = mapOf("filename" to "test.txt")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertEquals("plain", document.metadata["type"])
        assertEquals(".txt", document.metadata["extension"])
        assertTrue(document.parsedContent.contains("<pre"))
    }

    @Test
    fun testParseHtmlFile() {
        val content = "<html><body><h1>Test</h1></body></html>"

        val options = mapOf("filename" to "test.html")
        val document = parser.parse(content, options)

        assertEquals("html", document.metadata["type"])
        assertEquals(content, document.parsedContent) // HTML displayed as-is
    }

    @Test
    fun testParseJsonFile() {
        val content = """{"name":"Alice","age":30}"""

        val options = mapOf("filename" to "data.json")
        val document = parser.parse(content, options)

        // JSON is treated as code type with JSON language
        assertTrue(document.metadata["type"] == "json" || document.metadata["type"] == "code")
        // Original content should have Alice
        assertTrue(document.rawContent.contains("Alice"))
    }

    @Test
    fun testParseJsonPrettyPrint() {
        val content = """{"name":"Alice","age":30,"city":"New York"}"""

        val options = mapOf("filename" to "data.json")
        val document = parser.parse(content, options)

        val processed = document.parsedContent
        // Pretty-printed JSON should be formatted
        assertTrue(processed.contains("language-json"))
    }

    @Test
    fun testParsePythonCode() {
        val content = """
            def hello():
                print("Hello, World!")
        """.trimIndent()

        val options = mapOf("filename" to "script.py")
        val document = parser.parse(content, options)

        assertEquals("code", document.metadata["type"])
        assertEquals(".py", document.metadata["extension"])
        assertTrue(document.parsedContent.contains("language-python"))
        assertTrue(document.parsedContent.contains("code-block"))
    }

    @Test
    fun testParseJavaScriptCode() {
        val content = """
            function hello() {
                console.log("Hello!");
            }
        """.trimIndent()

        val options = mapOf("filename" to "script.js")
        val document = parser.parse(content, options)

        assertEquals("code", document.metadata["type"])
        assertTrue(document.parsedContent.contains("language-javascript"))
    }

    @Test
    fun testParseKotlinCode() {
        val content = """
            fun main() {
                println("Hello, Kotlin!")
            }
        """.trimIndent()

        val options = mapOf("filename" to "Main.kt")
        val document = parser.parse(content, options)

        assertEquals("code", document.metadata["type"])
        assertTrue(document.parsedContent.contains("language-kotlin"))
    }

    @Test
    fun testParseXmlFile() {
        val content = """
            <?xml version="1.0"?>
            <root>
                <item>Value</item>
            </root>
        """.trimIndent()

        val options = mapOf("filename" to "data.xml")
        val document = parser.parse(content, options)

        // XML is treated as code type with XML language
        assertTrue(document.metadata["type"] == "xml" || document.metadata["type"] == "code")
        assertTrue(document.parsedContent.contains("language-xml"))
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "Plain text without filename"

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("", document.metadata["extension"])
    }

    @Test
    fun testParseEmptyContent() {
        val content = ""

        val options = mapOf("filename" to "empty.txt")
        val document = parser.parse(content, options)

        assertNotNull(document)
        // Empty string.lines() returns list with one empty string
        assertEquals("1", document.metadata["lines"])
    }

    @Test
    fun testParseLargeFile() {
        val lines = (1..1000).map { "Line $it" }
        val content = lines.joinToString("\n")

        val options = mapOf("filename" to "large.txt")
        val document = parser.parse(content, options)

        assertEquals("1000", document.metadata["lines"])
        assertTrue(document.parsedContent.contains("Line 1"))
    }

    @Test
    fun testHtmlEscaping() {
        val content = "Text with <html> & special \"characters\""

        val options = mapOf("filename" to "test.txt")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testMetadataExtraction() {
        val content = "Line 1\nLine 2\nLine 3"

        val options = mapOf("filename" to "test.txt")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["lines"])
        assertEquals(content.length.toString(), document.metadata["characters"])
        assertEquals(".txt", document.metadata["extension"])
        assertEquals("plain", document.metadata["type"])
    }

    @Test
    fun testCodeBlockHtmlStructure() {
        val content = "console.log('test');"

        val options = mapOf("filename" to "test.js")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("<div class='plaintext code-block'>"))
        assertTrue(html.contains("<pre>"))
        assertTrue(html.contains("<code class='language-javascript'>"))
    }

    @Test
    fun testPlainTextHtmlStructure() {
        val content = "Plain text"

        val options = mapOf("filename" to "test.txt")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("<div class='plaintext'>"))
        assertTrue(html.contains("<pre"))
        assertTrue(html.contains("monospace"))
    }

    @Test
    fun testLanguageMappings() {
        val testCases = mapOf(
            "test.py" to "python",
            "test.js" to "javascript",
            "test.ts" to "typescript",
            "test.java" to "java",
            "test.kt" to "kotlin",
            "test.cpp" to "cpp",
            "test.rs" to "rust",
            "test.go" to "go",
            "test.rb" to "ruby",
            "test.php" to "php",
            "test.swift" to "swift",
            "test.sh" to "bash"
        )

        for ((filename, expectedLang) in testCases) {
            val document = parser.parse("code", mapOf("filename" to filename))
            assertTrue(
                document.parsedContent.contains("language-$expectedLang"),
                "Expected language-$expectedLang for $filename"
            )
        }
    }

    @Test
    fun testCaseInsensitiveExtension() {
        val content = "code"

        val options1 = mapOf("filename" to "test.PY")
        val document1 = parser.parse(content, options1)
        assertTrue(document1.parsedContent.contains("language-python"))

        val options2 = mapOf("filename" to "test.Kt")
        val document2 = parser.parse(content, options2)
        assertTrue(document2.parsedContent.contains("language-kotlin"))
    }

    @Test
    fun testMultipleExtensions() {
        // Test various code extensions
        val codeExtensions = listOf(
            ".py", ".js", ".ts", ".java", ".kt", ".cpp", ".c", ".h",
            ".cs", ".rb", ".php", ".swift", ".rs", ".go", ".sh"
        )

        for (ext in codeExtensions) {
            val options = mapOf("filename" to "test$ext")
            val document = parser.parse("code", options)
            assertEquals("code", document.metadata["type"], "Failed for $ext")
        }
    }

    @Test
    fun testHtmlExtensions() {
        val htmlExtensions = listOf(".html", ".htm")

        for (ext in htmlExtensions) {
            val content = "<h1>Test</h1>"
            val options = mapOf("filename" to "test$ext")
            val document = parser.parse(content, options)
            assertEquals("html", document.metadata["type"])
            assertEquals(content, document.parsedContent)
        }
    }

    @Test
    fun testJsonArrayPrettyPrint() {
        val content = """[{"id":1,"name":"Alice"},{"id":2,"name":"Bob"}]"""

        val options = mapOf("filename" to "data.json")
        val document = parser.parse(content, options)

        assertTrue(document.metadata["type"] == "json" || document.metadata["type"] == "code")
    }

    @Test
    fun testJsonNestedObjects() {
        val content = """{"user":{"name":"Alice","address":{"city":"NYC"}}}"""

        val options = mapOf("filename" to "data.json")
        val document = parser.parse(content, options)

        assertTrue(document.metadata["type"] == "json" || document.metadata["type"] == "code")
    }

    @Test
    fun testSpecialCharactersInCode() {
        val content = """
            const str = "Hello <world> & 'friends'";
            // Comment with special chars: <>&
        """.trimIndent()

        val options = mapOf("filename" to "test.js")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;"))  // < escaped
        assertTrue(html.contains("&gt;"))  // > escaped
        assertTrue(html.contains("&amp;")) // & escaped
    }

    @Test
    fun testToHtml() {
        val content = "Test content"
        val options = mapOf("filename" to "test.txt")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testFileWithoutExtension() {
        val content = "Content without extension"
        val options = mapOf("filename" to "README")

        val document = parser.parse(content, options)

        assertEquals("", document.metadata["extension"])
        assertEquals("plain", document.metadata["type"])
    }

    @Test
    fun testYamlFile() {
        val content = """
            name: Yole
            version: 1.0
            dependencies:
              - kotlin
              - compose
        """.trimIndent()

        val options = mapOf("filename" to "config.yaml")
        val document = parser.parse(content, options)

        assertEquals("code", document.metadata["type"])
        assertTrue(document.parsedContent.contains("language-yaml"))
    }

    @Test
    fun testSqlFile() {
        val content = """
            SELECT * FROM users WHERE age > 18;
            INSERT INTO users (name) VALUES ('Alice');
        """.trimIndent()

        val options = mapOf("filename" to "query.sql")
        val document = parser.parse(content, options)

        assertEquals("code", document.metadata["type"])
        assertTrue(document.parsedContent.contains("language-sql"))
    }
}
