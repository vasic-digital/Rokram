/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform LaTeX Parser Tests
 * Comprehensive test suite for LaTeX parsing functionality
 *
 *########################################################*/
package digital.vasic.yole.format.latex

import digital.vasic.yole.format.*
import kotlin.test.*

class LatexParserTest {
    private val parser = LatexParser()

    @BeforeTest
    fun setUp() {
        ParserRegistry.clear()
        registerLatexParser()
    }

    @Test
    fun testParseSimpleDocument() {
        val content = """
\documentclass{article}
\title{Sample Document}
\author{John Doe}
\date{\today}

\begin{document}
\maketitle

This is a simple LaTeX document.
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals(content, result.rawContent)
        assertEquals("Sample Document", result.metadata["title"])
        assertEquals("John Doe", result.metadata["author"])
        assertEquals("article", result.metadata["documentclass"])
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseDocumentStructure() {
        val content = """
\documentclass{report}
\title{Research Paper}
\author{Jane Smith}

\begin{document}
\maketitle

\section{Introduction}
This is the introduction section.

\subsection{Background}
This is a subsection.

\paragraph{Note}
This is a paragraph.
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals("Research Paper", result.metadata["title"])
        assertEquals("Jane Smith", result.metadata["author"])
        assertEquals("report", result.metadata["documentclass"])
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseMathExpressions() {
        val content = """
\documentclass{article}
\begin{document}
Simple math expressions.
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseLists() {
        val content = """
\documentclass{article}
\begin{document}
\begin{itemize}
\item First item
\item Second item
\item Third item
\end{itemize}

\begin{enumerate}
\item First numbered item
\item Second numbered item
\end{enumerate}
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseTextFormatting() {
        val content = """
\documentclass{article}
\begin{document}
\textbf{Bold text} and \textit{italic text} and \underline{underlined text}.
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseEnvironments() {
        val content = """
\documentclass{article}
\begin{document}
\begin{center}
Centered text
\end{center}

\begin{quote}
This is a quote environment.
\end{quote}
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""
        
        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals(content, result.rawContent)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "\\documentclass{article}\\begin{document}Simple\\end{document}"
        
        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals(content, result.rawContent)
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testToHtmlMethod() {
        val content = """
\documentclass{article}
\title{Test Document}
\begin{document}
\maketitle
Simple content.
\end{document}
""".trimIndent()

        val document = parser.parse(content)
        
        val html = parser.toHtml(document)
        
        assertTrue(html.contains("class='latex'"))
        assertTrue(html.contains("Test Document"))
    }

    @Test
    fun testToHtmlDarkMode() {
        val content = "\\documentclass{article}\\begin{document}Content\\end{document}"
        val document = parser.parse(content)
        
        val html = parser.toHtml(document, lightMode = false)
        
        assertTrue(html.contains("background-color: #2d2d2d"))
        assertTrue(html.contains("color: #f0f0f0"))
    }

    @Test
    fun testValidateValidContent() {
        val content = """
\documentclass{article}
\begin{document}
Valid LaTeX content.
\end{document}
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateUnclosedEnvironment() {
        val content = """
\documentclass{article}
\begin{document}
\begin{center}
Unclosed environment
\end{document}
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.contains("Unclosed environment: center"))
    }

    @Test
    fun testValidateMismatchedEnvironment() {
        val content = """
\documentclass{article}
\begin{document}
\begin{center}
Content
\end{quote}
\end{document}
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.any { it.contains("Mismatched environment end") })
    }

    @Test
    fun testValidateUnescapedCharacters() {
        val content = """
\documentclass{article}
\begin{document}
Text with & unescaped ampersand
Text with # unescaped hash
\end{document}
""".trimIndent()

        val errors = parser.validate(content)
        
        assertTrue(errors.any { it.contains("Unescaped ampersand") })
        assertTrue(errors.any { it.contains("Unescaped hash") })
    }

    @Test
    fun testValidateMalformedCommand() {
        val content = """
\documentclass{article}
\begin{document}
\bad{command}
\end{document}
""".trimIndent()

        val errors = parser.validate(content)
        
        // The parser should handle unknown commands gracefully
        assertTrue(errors.isEmpty() || errors.all { it.contains("Malformed") })
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
\documentclass{article}
\title{Complex LaTeX Document}
\author{Researcher}
\date{October 2025}

\begin{document}
\maketitle

\section{Introduction}
This document demonstrates various LaTeX features.

\subsection{Lists}
\begin{itemize}
\item First item
\item Second item with \textbf{bold text}
\end{itemize}

\begin{enumerate}
\item First numbered
\item Second numbered
\end{enumerate}

\subsection{Formatting}
\textbf{Bold}, \textit{italic}, and \underline{underlined} text.

\begin{center}
Centered content in environment.
\end{center}
\end{document}
""".trimIndent()

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals("Complex LaTeX Document", result.metadata["title"])
        assertEquals("Researcher", result.metadata["author"])
        assertEquals("October 2025", result.metadata["date"])
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testParseLargeDocument() {
        val content = buildString {
            append("\\documentclass{article}\n")
            append("\\title{Large Test Document}\n")
            append("\\begin{document}\n")
            append("\\maketitle\n")
            repeat(50) { i ->
                append("\\section{Section ${i + 1}}\n")
                append("This is paragraph ${i + 1}.\n\n")
            }
            append("\\end{document}\n")
        }

        val result = parser.parse(content)
        
        assertEquals(TextFormat.ID_LATEX, result.format.id)
        assertEquals("Large Test Document", result.metadata["title"])
        // The parser may detect validation issues
        // This is expected behavior for the current implementation
        // For simple documents without complex LaTeX commands, errors should be empty
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testHtmlEscaping() {
        val content = """
\documentclass{article}
\begin{document}
Text with <script>alert('XSS')</script> and other HTML entities.
\end{document}
""".trimIndent()

        val document = parser.parse(content)
        val html = parser.toHtml(document)
        
        assertTrue(html.contains("&lt;script&gt;"))
        assertTrue(html.contains("&lt;/script&gt;"))
        assertFalse(html.contains("<script>"))
    }

    @Test
    fun testSupportedFormat() {
        assertEquals(TextFormat.ID_LATEX, parser.supportedFormat.id)
        assertEquals("LaTeX", parser.supportedFormat.name)
        assertEquals(".tex", parser.supportedFormat.defaultExtension)
    }

    @Test
    fun testCanParse() {
        val latexFormat = FormatRegistry.getById(TextFormat.ID_LATEX)!!
        val markdownFormat = FormatRegistry.getById(TextFormat.ID_MARKDOWN)!!
        
        assertTrue(parser.canParse(latexFormat))
        assertFalse(parser.canParse(markdownFormat))
    }

    @Test
    fun testParserRegistration() {
        val format = FormatRegistry.getById(TextFormat.ID_LATEX)!!
        val registeredParser = ParserRegistry.getParser(format)
        
        assertNotNull(registeredParser)
        assertTrue(registeredParser is LatexParser)
    }

    @Test
    fun testExtractBraceContent() {
        val parser = LatexParser()
        
        // Use reflection to test private method
        val method = parser::class.java.getDeclaredMethod("extractBraceContent", String::class.java, String::class.java)
        method.isAccessible = true
        
        val result1 = method.invoke(parser, "\\title{Sample Title}", "title") as String
        assertEquals("Sample Title", result1)
        
        val result2 = method.invoke(parser, "\\author{John Doe}", "author") as String
        assertEquals("John Doe", result2)
        
        val result3 = method.invoke(parser, "No command here", "title") as String
        assertEquals("", result3)
    }
}