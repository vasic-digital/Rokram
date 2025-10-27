/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * TaskPaper Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.taskpaper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TaskpaperParserTest {

    private val parser = TaskpaperParser()

    @Test
    fun testParseSimpleTask() {
        val content = "- Buy milk"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertEquals("1", document.metadata["tasks"])
        assertEquals("0", document.metadata["projects"])
        assertTrue(document.parsedContent.contains("Buy milk"))
    }

    @Test
    fun testParseProject() {
        val content = "Home:"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("0", document.metadata["tasks"])
        assertEquals("1", document.metadata["projects"])
        assertTrue(document.parsedContent.contains("Home:"))
    }

    @Test
    fun testParseProjectWithTasks() {
        val content = """
            Home:
            	- Clean kitchen
            	- Buy groceries
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["tasks"])
        assertEquals("1", document.metadata["projects"])
        assertTrue(document.parsedContent.contains("Home:"))
        assertTrue(document.parsedContent.contains("Clean kitchen"))
        assertTrue(document.parsedContent.contains("Buy groceries"))
    }

    @Test
    fun testParseTaskWithDoneTag() {
        val content = "- Buy milk @done"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertEquals("1", document.metadata["doneTasks"])
        assertTrue(document.parsedContent.contains("@done"))
    }

    @Test
    fun testParseTaskWithTodayTag() {
        val content = "- Call dentist @today"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertEquals("1", document.metadata["todayTasks"])
        assertTrue(document.parsedContent.contains("@today"))
    }

    @Test
    fun testParseTaskWithDueTag() {
        val content = "- Submit report @due(2025-10-31)"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("@due(2025-10-31)"))
    }

    @Test
    fun testParseNote() {
        val content = """
            Project:
            	- Task 1
            	This is a note about the task
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertEquals("1", document.metadata["notes"])
        assertTrue(document.parsedContent.contains("This is a note"))
    }

    @Test
    fun testParseMultipleProjects() {
        val content = """
            Work:
            	- Task 1
            Home:
            	- Task 2
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["projects"])
        assertEquals("2", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("Work:"))
        assertTrue(document.parsedContent.contains("Home:"))
    }

    @Test
    fun testParseNestedProjects() {
        val content = """
            Work:
            	Project A:
            		- Task 1
            		- Task 2
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["projects"])
        assertEquals("2", document.metadata["tasks"])
    }

    @Test
    fun testParseEmptyLines() {
        val content = """
            Project:

            	- Task 1

            	- Task 2
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["tasks"])
        assertEquals("1", document.metadata["projects"])
    }

    @Test
    fun testParseMultipleTags() {
        val content = "- Important task @today @priority(high) @due(2025-10-27)"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("@today"))
        assertTrue(document.parsedContent.contains("@priority(high)"))
        assertTrue(document.parsedContent.contains("@due(2025-10-27)"))
    }

    @Test
    fun testParseComplexDocument() {
        val content = """
            Work Tasks:
            	Project Alpha:
            		- Complete documentation @done
            		- Review code @today
            		- Write tests @due(2025-11-01)
            		Need to cover all edge cases
            	Project Beta:
            		- Initial setup
            		- Configure CI/CD @priority(high)

            Personal:
            	- Buy birthday gift @due(2025-10-30)
            	- Schedule dentist appointment @today
            	- Call mom
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("4", document.metadata["projects"])
        assertEquals("8", document.metadata["tasks"])
        assertEquals("1", document.metadata["doneTasks"])
        assertEquals("2", document.metadata["todayTasks"])
        assertEquals("1", document.metadata["notes"])
    }

    @Test
    fun testParseEmptyDocument() {
        val content = ""

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertNotNull(document)
        assertEquals("0", document.metadata["tasks"])
        assertEquals("0", document.metadata["projects"])
    }

    @Test
    fun testParseOnlyWhitespace() {
        val content = "   \n\t\n   "

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("0", document.metadata["tasks"])
        assertEquals("0", document.metadata["projects"])
    }

    @Test
    fun testParseTaskWithoutIndent() {
        val content = "- Task without project"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("Task without project"))
    }

    @Test
    fun testParseWithoutFilename() {
        val content = "- Task"

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("", document.metadata["extension"])
        assertEquals("1", document.metadata["tasks"])
    }

    @Test
    fun testHtmlGeneration() {
        val content = """
            Project:
            	- Task @done
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("<div class='taskpaper'>"))
        assertTrue(html.contains("taskpaper-project"))
        assertTrue(html.contains("taskpaper-task"))
        assertTrue(html.contains("@done"))
    }

    @Test
    fun testHtmlEscaping() {
        val content = "- Task with <html> & special \"characters\""

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        val html = document.parsedContent
        assertTrue(html.contains("&lt;html&gt;"))
        assertTrue(html.contains("&amp;"))
        assertTrue(html.contains("&quot;"))
    }

    @Test
    fun testToHtmlMethod() {
        val content = "- Task"
        val options = mapOf("filename" to "test.taskpaper")
        val document = parser.parse(content, options)

        val html = parser.toHtml(document, lightMode = true)
        assertEquals(document.parsedContent, html)
    }

    @Test
    fun testValidateValidContent() {
        val content = """
            Project:
            	- Task 1
            	- Task 2 @done
        """.trimIndent()

        val errors = parser.validate(content)
        assertTrue(errors.isEmpty())
    }

    @Test
    fun testValidateMalformedTask() {
        val content = "-Task without space"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Task marker should be '- '"))
    }

    @Test
    fun testValidateUnclosedTagParameter() {
        val content = "- Task @due(2025-10-27"

        val errors = parser.validate(content)
        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Unclosed tag parameter"))
    }

    @Test
    fun testParseTaskWithColonInContent() {
        val content = "- Task: do something important"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        // Should be parsed as task, not project (project must END with colon only)
        assertEquals("1", document.metadata["tasks"])
        assertEquals("0", document.metadata["projects"])
    }

    @Test
    fun testParseIndentationLevels() {
        val content = """
            Level 0:
            	Level 1:
            		- Level 2 task
            			Level 3 note
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("2", document.metadata["projects"])
        assertEquals("1", document.metadata["tasks"])
        assertEquals("1", document.metadata["notes"])
    }

    @Test
    fun testParseLargeDocument() {
        val tasks = (1..50).map { "- Task $it" }
        val content = "Project:\n\t" + tasks.joinToString("\n\t")

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("50", document.metadata["tasks"])
        assertEquals("1", document.metadata["projects"])
    }

    @Test
    fun testParseProjectOnly() {
        val content = """
            Project 1:
            Project 2:
            Project 3:
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("3", document.metadata["projects"])
        assertEquals("0", document.metadata["tasks"])
    }

    @Test
    fun testParseNotesOnly() {
        val content = """
            This is a note
            Another note
            Third note
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("0", document.metadata["projects"])
        assertEquals("0", document.metadata["tasks"])
        assertEquals("3", document.metadata["notes"])
    }

    @Test
    fun testParseCustomTags() {
        val content = """
            - Task @priority(high) @estimate(2h) @assigned(alice)
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("@priority(high)"))
        assertTrue(document.parsedContent.contains("@estimate(2h)"))
        assertTrue(document.parsedContent.contains("@assigned(alice)"))
    }

    @Test
    fun testParseTagsWithoutParameters() {
        val content = "- Task @important @urgent @reviewed"

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["tasks"])
        assertTrue(document.parsedContent.contains("@important"))
        assertTrue(document.parsedContent.contains("@urgent"))
        assertTrue(document.parsedContent.contains("@reviewed"))
    }

    @Test
    fun testParseMixedContent() {
        val content = """
            Random note at the top

            Project:
            	- Task 1 @done
            	Some notes here
            	- Task 2

            Another loose note
        """.trimIndent()

        val options = mapOf("filename" to "tasks.taskpaper")
        val document = parser.parse(content, options)

        assertEquals("1", document.metadata["projects"])
        assertEquals("2", document.metadata["tasks"])
        assertEquals("3", document.metadata["notes"])
        assertEquals("1", document.metadata["doneTasks"])
    }
}
