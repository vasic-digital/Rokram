/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * FormatRegistry tests
 *
 *########################################################*/
package digital.vasic.yole.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormatRegistryTest {

    @Test
    fun testFormatCount() {
        // Should have all 17 supported formats
        assertEquals(17, FormatRegistry.formats.size, "Expected exactly 17 formats")
    }

    @Test
    fun testDetectMarkdownByExtension() {
        val format = FormatRegistry.detectByExtension(".md")
        assertEquals(TextFormat.ID_MARKDOWN, format.id)
        assertEquals("Markdown", format.name)
    }

    @Test
    fun testDetectMarkdownByVariousExtensions() {
        val extensions = listOf(".md", ".markdown", ".mkd", ".mdown")
        extensions.forEach { ext ->
            val format = FormatRegistry.detectByExtension(ext)
            assertEquals(TextFormat.ID_MARKDOWN, format.id, "Failed for extension: $ext")
        }
    }

    @Test
    fun testDetectTodoTxtByExtension() {
        val format = FormatRegistry.detectByExtension(".txt")
        // Note: .txt might match todotxt or plaintext depending on order
        assertTrue(
            format.id == TextFormat.ID_TODOTXT || format.id == TextFormat.ID_PLAINTEXT,
            "Expected todotxt or plaintext for .txt"
        )
    }

    @Test
    fun testDetectLatexByExtension() {
        val format = FormatRegistry.detectByExtension(".tex")
        assertEquals(TextFormat.ID_LATEX, format.id)
    }

    @Test
    fun testDetectByFilename() {
        val format = FormatRegistry.detectByFilename("document.md")
        assertEquals(TextFormat.ID_MARKDOWN, format.id)
    }

    @Test
    fun testDetectByFilenameWithoutExtension() {
        val format = FormatRegistry.detectByFilename("README")
        assertEquals(TextFormat.ID_PLAINTEXT, format.id)
    }

    @Test
    fun testDetectMarkdownByContent() {
        val markdownContent = """
            # Heading 1
            ## Heading 2
            This is a paragraph.
        """.trimIndent()

        val format = FormatRegistry.detectByContent(markdownContent)
        assertNotNull(format)
        assertEquals(TextFormat.ID_MARKDOWN, format.id)
    }

    @Test
    fun testDetectTodoTxtByContent() {
        val todoContent = """
            (A) Call Mom @phone +family
            (B) Write documentation @work
            x 2025-01-15 Finished task
        """.trimIndent()

        val format = FormatRegistry.detectByContent(todoContent)
        assertNotNull(format)
        assertEquals(TextFormat.ID_TODOTXT, format.id)
    }

    @Test
    fun testDetectLatexByContent() {
        val latexContent = """
            \documentclass{article}
            \begin{document}
            Hello world
            \end{document}
        """.trimIndent()

        val format = FormatRegistry.detectByContent(latexContent)
        assertNotNull(format)
        assertEquals(TextFormat.ID_LATEX, format.id)
    }

    @Test
    fun testDetectOrgModeByContent() {
        val orgContent = """
            * TODO Task 1
            ** DONE Subtask
            #+BEGIN_SRC python
            print("hello")
            #+END_SRC
        """.trimIndent()

        val format = FormatRegistry.detectByContent(orgContent)
        assertNotNull(format)
        assertEquals(TextFormat.ID_ORGMODE, format.id)
    }

    @Test
    fun testDetectNoMatch() {
        val plainContent = "Just some plain text without any formatting markers."
        val format = FormatRegistry.detectByContent(plainContent)
        assertNull(format, "Should return null when no pattern matches")
    }

    @Test
    fun testGetById() {
        val markdown = FormatRegistry.getById(TextFormat.ID_MARKDOWN)
        assertNotNull(markdown)
        assertEquals("Markdown", markdown.name)

        val latex = FormatRegistry.getById(TextFormat.ID_LATEX)
        assertNotNull(latex)
        assertEquals("LaTeX", latex.name)
    }

    @Test
    fun testGetByIdNonExistent() {
        val format = FormatRegistry.getById("nonexistent")
        assertNull(format)
    }

    @Test
    fun testIsExtensionSupported() {
        assertTrue(FormatRegistry.isExtensionSupported(".md"))
        assertTrue(FormatRegistry.isExtensionSupported(".tex"))
        assertTrue(FormatRegistry.isExtensionSupported(".org"))
        assertTrue(FormatRegistry.isExtensionSupported(".rst"))
    }

    @Test
    fun testIsExtensionSupportedWithoutDot() {
        assertTrue(FormatRegistry.isExtensionSupported("md"))
        assertTrue(FormatRegistry.isExtensionSupported("tex"))
    }

    @Test
    fun testIsExtensionNotSupported() {
        assertTrue(!FormatRegistry.isExtensionSupported(".xyz"))
        assertTrue(!FormatRegistry.isExtensionSupported(".nonexistent"))
    }

    @Test
    fun testExtensionCaseInsensitive() {
        val format1 = FormatRegistry.detectByExtension(".MD")
        val format2 = FormatRegistry.detectByExtension(".md")
        assertEquals(format1.id, format2.id)
    }

    @Test
    fun testAllFormatsHaveUniqueIds() {
        val ids = FormatRegistry.formats.map { it.id }.toSet()
        assertEquals(FormatRegistry.formats.size, ids.size, "All formats should have unique IDs")
    }

    @Test
    fun testAllFormatsHaveNames() {
        FormatRegistry.formats.forEach { format ->
            assertTrue(format.name.isNotEmpty(), "Format ${format.id} should have a name")
        }
    }

    @Test
    fun testTextFormatEquality() {
        val format1 = TextFormat(
            id = "test",
            name = "Test",
            defaultExtension = ".test"
        )
        val format2 = TextFormat(
            id = "test",
            name = "Test",
            defaultExtension = ".test"
        )
        assertEquals(format1, format2)
    }

    @Test
    fun testFormatDetectionOrder() {
        // More specific formats should come before more general ones
        val markdownIndex = FormatRegistry.formats.indexOfFirst { it.id == TextFormat.ID_MARKDOWN }
        val plaintextIndex = FormatRegistry.formats.indexOfFirst { it.id == TextFormat.ID_PLAINTEXT }

        assertTrue(markdownIndex < plaintextIndex, "Markdown should come before plaintext")
    }
}
