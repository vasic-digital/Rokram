/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * TodoTxt Parser Tests
 *
 *########################################################*/
package digital.vasic.yole.format.todotxt

import kotlin.test.*

class TodoTxtParserTest {

    private val parser = TodoTxtParser()

    @Test
    fun testParseSimpleTask() {
        val task = parser.parseTask("Buy milk")

        assertEquals("Buy milk", task.line)
        assertEquals("Buy milk", task.description)
        assertFalse(task.done)
        assertEquals(null, task.priority)
        assertTrue(task.projects.isEmpty())
        assertTrue(task.contexts.isEmpty())
    }

    @Test
    fun testParseTaskWithPriority() {
        val task = parser.parseTask("(A) Important task")

        assertEquals("(A) Important task", task.line)
        assertEquals("Important task", task.description)
        assertEquals('A', task.priority)
        assertFalse(task.done)
    }

    @Test
    fun testParseCompletedTask() {
        val task = parser.parseTask("x 2025-01-15 Completed task")

        assertTrue(task.done)
        assertEquals("2025-01-15", task.completionDate)
        assertEquals("Completed task", task.description)
    }

    @Test
    fun testParseTaskWithProject() {
        val task = parser.parseTask("Write documentation +yole")

        assertEquals("Write documentation", task.description.trim())
        assertEquals(listOf("yole"), task.projects)
    }

    @Test
    fun testParseTaskWithContext() {
        val task = parser.parseTask("Call Mom @phone")

        assertEquals("Call Mom", task.description.trim())
        assertEquals(listOf("phone"), task.contexts)
    }

    @Test
    fun testParseTaskWithMultipleProjects() {
        val task = parser.parseTask("Write docs +yole +documentation +kmp")

        assertEquals(listOf("yole", "documentation", "kmp"), task.projects)
    }

    @Test
    fun testParseTaskWithMultipleContexts() {
        val task = parser.parseTask("Buy groceries @home @errands")

        assertEquals(listOf("home", "errands"), task.contexts)
    }

    @Test
    fun testParseTaskWithDueDate() {
        val task = parser.parseTask("Submit report due:2025-12-31")

        assertEquals("2025-12-31", task.dueDate)
        assertEquals("Submit report", task.description.trim())
    }

    @Test
    fun testParseTaskWithCreationDate() {
        val task = parser.parseTask("(B) 2025-01-01 New year task")

        assertEquals('B', task.priority)
        assertEquals("2025-01-01", task.creationDate)
        assertEquals("New year task", task.description)
    }

    @Test
    fun testParseComplexTask() {
        val task = parser.parseTask("(A) 2025-01-15 Write KMP documentation +yole +kmp @computer due:2025-02-01")

        assertEquals('A', task.priority)
        assertEquals("2025-01-15", task.creationDate)
        assertEquals("Write KMP documentation", task.description.trim())
        assertEquals(listOf("yole", "kmp"), task.projects)
        assertEquals(listOf("computer"), task.contexts)
        assertEquals("2025-02-01", task.dueDate)
        assertFalse(task.done)
    }

    @Test
    fun testParseCompletedTaskWithAllFields() {
        val task = parser.parseTask("x 2025-01-20 (B) 2025-01-15 Finished task +project @context")

        assertTrue(task.done)
        assertEquals("2025-01-20", task.completionDate)
        assertEquals('B', task.priority)
        assertEquals("2025-01-15", task.creationDate)
        assertEquals(listOf("project"), task.projects)
        assertEquals(listOf("context"), task.contexts)
    }

    @Test
    fun testParseTaskWithKeyValuePairs() {
        val task = parser.parseTask("Task with metadata key1:value1 key2:value2")

        assertEquals("value1", task.keyValues["key1"])
        assertEquals("value2", task.keyValues["key2"])
    }

    @Test
    fun testParseAllTasks() {
        val content = """
            (A) Important task +work
            Buy milk @home
            x 2025-01-15 Completed task
            (B) Another task due:2025-02-01
        """.trimIndent()

        val tasks = parser.parseAllTasks(content)

        assertEquals(4, tasks.size)
        assertEquals('A', tasks[0].priority)
        assertEquals("Buy milk", tasks[1].description)
        assertTrue(tasks[2].done)
        assertEquals("2025-02-01", tasks[3].dueDate)
    }

    @Test
    fun testParseEmptyLines() {
        val content = """
            Task 1

            Task 2


            Task 3
        """.trimIndent()

        val tasks = parser.parseAllTasks(content)

        assertEquals(3, tasks.size)
        assertEquals("Task 1", tasks[0].description)
        assertEquals("Task 2", tasks[1].description)
        assertEquals("Task 3", tasks[2].description)
    }

    @Test
    fun testParseLowercasePriority() {
        val task = parser.parseTask("(a) Task with lowercase priority")

        assertEquals('A', task.priority) // Should be uppercased
    }

    @Test
    fun testParseXUppercaseDone() {
        val task = parser.parseTask("X Task completed with uppercase X")

        assertTrue(task.done)
    }

    @Test
    fun testParseDocument() {
        val content = """
            (A) Important task +work @office
            Buy groceries @home
            x 2025-01-15 Completed task
        """.trimIndent()

        val document = parser.parse(content)

        assertNotNull(document)
        assertEquals("3", document.metadata["totalTasks"])
        assertEquals("1", document.metadata["completedTasks"])
        assertEquals("2", document.metadata["pendingTasks"])
        assertTrue(document.parsedContent.contains("todotxt"))
    }

    @Test
    fun testHtmlGeneration() {
        val content = "(A) Test task +project @context"
        val document = parser.parse(content)

        val html = document.parsedContent

        assertTrue(html.contains("class='todotxt'"))
        assertTrue(html.contains("class='task"))
        assertTrue(html.contains("class='priority'"))
        assertTrue(html.contains("class='description'"))
        assertTrue(html.contains("class='project'"))
        assertTrue(html.contains("class='context'"))
    }

    @Test
    fun testCompletedTaskHtml() {
        val content = "x 2025-01-15 Completed task"
        val document = parser.parse(content)

        val html = document.parsedContent

        // Check for done class
        assertTrue(html.contains("done"), "HTML should contain 'done' class: $html")
        // Check for checkbox (either checked or unchecked should be present)
        assertTrue(html.contains("checkbox"), "HTML should contain 'checkbox' class: $html")
    }

    @Test
    fun testPendingTaskHtml() {
        val content = "Pending task"
        val document = parser.parse(content)

        val html = document.parsedContent

        assertFalse(html.contains("done"))
        assertTrue(html.contains("checkbox"))
    }

    @Test
    fun testTaskWithDueDateHtml() {
        val content = "Task with due date due:2025-12-31"
        val document = parser.parse(content)

        val html = document.parsedContent

        assertTrue(html.contains("class='due-date'"))
        assertTrue(html.contains("due:2025-12-31"))
    }

    @Test
    fun testGetCurrentDate() {
        val date = getCurrentDate()

        // Should match YYYY-MM-DD format
        assertTrue(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun testTaskIsDueToday() {
        val today = getCurrentDate()
        val task = parser.parseTask("Task due today due:$today")

        assertTrue(task.isDueToday())
        assertFalse(task.isOverdue())
    }

    @Test
    fun testTaskIsNotOverdue() {
        val task = parser.parseTask("Task due:2099-12-31")

        assertFalse(task.isOverdue())
        assertFalse(task.isDueToday())
    }
}
