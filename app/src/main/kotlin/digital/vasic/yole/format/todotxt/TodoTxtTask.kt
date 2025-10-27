/*#######################################################
 *
 * SPDX-FileCopyrightText: 2017-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 *
 * Written 2017-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
#########################################################*/
package digital.vasic.yole.format.todotxt

import android.text.TextUtils
import android.widget.TextView
import digital.vasic.yole.frontend.textview.TextViewUtils
import digital.vasic.opoc.format.GsTextUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class TodoTxtTask(line: CharSequence) {

    enum class TodoDueState {
        NONE, OVERDUE, TODAY, FUTURE
    }

    private val _line: String = line.toString()
    private var contexts: List<String>? = null
    private var projects: List<String>? = null
    private var priority: Char? = null
    private var done: Boolean? = null
    private var creationDate: String? = null
    private var completionDate: String? = null
    private var dueDate: String? = null
    private var description: String? = null
    private var dueStatus: TodoDueState? = null

    fun getLine(): String = _line

    fun isDone(): Boolean {
        if (done == null) {
            done = isPatternFindable(_line, PATTERN_DONE)
        }
        return done!!
    }

    fun getDescription(): String {
        if (description == null) {
            // The description is what is left when all structured parts of the task are removed
            description = getLine()
                .replace(PATTERN_COMPLETION_DATE.pattern().toRegex(), "")
                .replace(PATTERN_PRIORITY_ANY.pattern().toRegex(), "")
                .replace(PATTERN_CREATION_DATE.pattern().toRegex(), "")
                .replace(PATTERN_CONTEXTS.pattern().toRegex(), "")
                .replace(PATTERN_PROJECTS.pattern().toRegex(), "")
                .replace(PATTERN_KEY_VALUE_PAIRS.pattern().toRegex(), "")
        }
        return description!!
    }

    fun getPriority(): Char {
        if (priority == null) {
            val ret = parseOneValueOrDefault(_line, PATTERN_PRIORITY_ANY, "")
            priority = if (ret.isEmpty()) PRIORITY_NONE else ret[0].uppercaseChar()
        }
        return priority!!
    }

    fun getContexts(): List<String> {
        if (contexts == null) {
            contexts = parseAllMatches(_line, PATTERN_CONTEXTS)
        }
        return contexts!!
    }

    fun getProjects(): List<String> {
        if (projects == null) {
            projects = parseAllMatches(_line, PATTERN_PROJECTS)
        }
        return projects!!
    }

    fun getCreationDate(): String = getCreationDate("")

    fun getCreationDate(defaultValue: String): String {
        if (creationDate == null) {
            creationDate = parseOneValueOrDefault(_line, PATTERN_CREATION_DATE, defaultValue)
        }
        return creationDate!!
    }

    fun getDueDate(): String = getDueDate("")

    fun getDueDate(defaultValue: String): String {
        if (dueDate == null) {
            dueDate = parseOneValueOrDefault(_line, PATTERN_DUE_DATE, 3, defaultValue)
        }
        return dueDate!!
    }

    fun getDueStatus(): TodoDueState {
        if (dueStatus == null) {
            val date = getDueDate()
            dueStatus = if (GsTextUtils.isNullOrEmpty(date)) {
                TodoDueState.NONE
            } else {
                val comp = date.compareTo(getToday())
                when {
                    comp > 0 -> TodoDueState.FUTURE
                    comp < 0 -> TodoDueState.OVERDUE
                    else -> TodoDueState.TODAY
                }
            }
        }
        return dueStatus!!
    }

    fun getCompletionDate(): String = getCompletionDate("")

    fun getCompletionDate(defaultValue: String): String {
        if (completionDate == null) {
            completionDate = parseOneValueOrDefault(_line, PATTERN_COMPLETION_DATE, defaultValue)
        }
        return completionDate!!
    }

    class SttTaskSimpleComparator(
        private val orderBy: String,
        private val descending: Boolean
    ) : Comparator<TodoTxtTask> {

        override fun compare(x: TodoTxtTask, y: TodoTxtTask): Int {
            // Always push done tasks to the bottom. Note ascending is small -> big.
            val doneCompare = Integer.compare(if (x.isDone()) 1 else 0, if (y.isDone()) 1 else 0)
            if (doneCompare != 0) return doneCompare

            var difference = when (orderBy) {
                BY_PRIORITY -> compare(x.getPriority(), y.getPriority())
                BY_CONTEXT -> compare(x.getContexts(), y.getContexts())
                BY_PROJECT -> compare(x.getProjects(), y.getProjects())
                BY_CREATION_DATE -> compare(x.getCreationDate(), y.getCreationDate())
                BY_DUE_DATE -> compare(x.getDueDate(), y.getDueDate())
                BY_DESCRIPTION -> compare(x.getDescription(), y.getDescription())
                BY_LINE -> compare(x.getLine(), y.getLine())
                else -> 0
            }

            // Always resolve sorts by due date and then priority
            if (difference == 0) {
                difference = compare(x.getDueDate(), y.getDueDate())
            }
            if (difference == 0) {
                difference = compare(x.getPriority(), y.getPriority())
            }

            if (descending) {
                difference = -1 * difference
            }
            return difference
        }

        private fun compareNull(x: String?, y: String?): Int {
            val xi = if (GsTextUtils.isNullOrEmpty(x)) 1 else 0
            val yi = if (GsTextUtils.isNullOrEmpty(y)) 1 else 0
            return Integer.compare(xi, yi)
        }

        private fun compare(x: Char, y: Char): Int =
            compare(x.toString(), y.toString())

        private fun compare(x: Array<String>, y: Array<String>): Int =
            compare(x.toList(), y.toList())

        private fun compare(x: List<String>, y: List<String>): Int {
            val sortedX = x.sorted()
            val sortedY = y.sorted()
            return compare(TextUtils.join("", sortedX), TextUtils.join("", sortedY))
        }

        private fun compare(x: String, y: String): Int {
            val n = compareNull(x, y)
            return if (n != 0) {
                n
            } else {
                x.trim().lowercase().compareTo(y.trim().lowercase())
            }
        }

        companion object {
            const val BY_PRIORITY = "priority"
            const val BY_CONTEXT = "context"
            const val BY_PROJECT = "project"
            const val BY_CREATION_DATE = "creation_date"
            const val BY_DUE_DATE = "due_date"
            const val BY_DESCRIPTION = "description"
            const val BY_LINE = "line_natural"
        }
    }

    companion object {
        @JvmField
        val DATEF_YYYY_MM_DD = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

        const val DATEF_YYYY_MM_DD_LEN = 10 // "yyyy-MM-dd".length
        const val PT_DATE = "\\d{4}-\\d{2}-\\d{2}"

        @JvmField
        val PATTERN_PROJECTS: Pattern = Pattern.compile("(?:^|\\s)(?:\\++)(\\S+)")

        @JvmField
        val PATTERN_CONTEXTS: Pattern = Pattern.compile("(?:^|\\s)(?:\\@+)(\\S+)")

        @JvmField
        val PATTERN_DONE: Pattern = Pattern.compile("(?m)(^[Xx]) (.*)$")

        @JvmField
        val PATTERN_DATE: Pattern = Pattern.compile("(?:^|\\s|:)($PT_DATE)(?:$|\\s)")

        @JvmField
        val PATTERN_KEY_VALUE_PAIRS__TAG_ONLY: Pattern = Pattern.compile("(?i)([a-z]+):([a-z0-9_-]+)")

        @JvmField
        val PATTERN_KEY_VALUE_PAIRS: Pattern = Pattern.compile("(?i)((?:[a-z]+):(?:[a-z0-9_-]+))")

        @JvmField
        val PATTERN_DUE_DATE: Pattern = Pattern.compile("(^|\\s)(due:)($PT_DATE)(\\s|$)")

        @JvmField
        val PATTERN_PRIORITY_ANY: Pattern = Pattern.compile("(?:^|\\n)\\(([A-Za-z])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_A: Pattern = Pattern.compile("(?:^|\\n)\\(([Aa])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_B: Pattern = Pattern.compile("(?:^|\\n)\\(([Bb])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_C: Pattern = Pattern.compile("(?:^|\\n)\\(([Cc])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_D: Pattern = Pattern.compile("(?:^|\\n)\\(([Dd])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_E: Pattern = Pattern.compile("(?:^|\\n)\\(([Ee])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_F: Pattern = Pattern.compile("(?:^|\\n)\\(([Ff])\\)\\s")

        @JvmField
        val PATTERN_PRIORITY_G_TO_Z: Pattern = Pattern.compile("(?:^|\\n)\\(([g-zG-Z])\\)\\s")

        @JvmField
        val PATTERN_COMPLETION_DATE: Pattern = Pattern.compile("(?:^|\\n)(?:[Xx] )($PT_DATE)?")

        @JvmField
        val PATTERN_CREATION_DATE: Pattern = Pattern.compile("(?:^|\\n)(?:\\([A-Za-z]\\)\\s)?(?:[Xx] $PT_DATE )?($PT_DATE)")

        const val PRIORITY_NONE = '~'

        @JvmStatic
        fun getToday(): String = DATEF_YYYY_MM_DD.format(Date())

        @JvmStatic
        fun getTasks(text: CharSequence, sel: IntArray): List<TodoTxtTask> {
            val tasks = mutableListOf<TodoTxtTask>()
            if (GsTextUtils.isValidSelection(text, *sel)) {
                val lsel = TextViewUtils.getLineSelection(text, sel)
                val lines = text.subSequence(lsel[0], lsel[1]).toString().split("\n")

                for (line in lines) {
                    tasks.add(TodoTxtTask(line))
                }
            }
            return tasks
        }

        @JvmStatic
        fun getSelectedTasks(view: TextView): List<TodoTxtTask> =
            getTasks(view.text, TextViewUtils.getSelection(view))

        @JvmStatic
        fun getAllTasks(text: CharSequence): List<TodoTxtTask> =
            getTasks(text, intArrayOf(0, text.length))

        @JvmStatic
        fun getProjects(tasks: List<TodoTxtTask>): List<String> {
            val set = TreeSet<String>()
            for (task in tasks) {
                set.addAll(task.getProjects())
            }
            return ArrayList(set)
        }

        @JvmStatic
        fun getContexts(tasks: List<TodoTxtTask>): List<String> {
            val set = TreeSet<String>()
            for (task in tasks) {
                set.addAll(task.getContexts())
            }
            return ArrayList(set)
        }

        @JvmStatic
        fun getPriorities(tasks: List<TodoTxtTask>): List<Char> {
            val set = TreeSet<Char>()
            for (task in tasks) {
                set.add(task.getPriority())
            }
            return ArrayList(set)
        }

        @JvmStatic
        fun getDueStates(tasks: List<TodoTxtTask>): List<TodoDueState> {
            val set = TreeSet<TodoDueState>()
            for (task in tasks) {
                set.add(task.getDueStatus())
            }
            return ArrayList(set)
        }

        @JvmStatic
        fun tasksToString(tasks: List<TodoTxtTask>): String {
            val builder = StringBuilder()
            for (task in tasks) {
                builder.append(task.getLine())
                builder.append('\n')
            }
            if (builder.isNotEmpty()) {
                builder.deleteCharAt(builder.length - 1)
            }
            return builder.toString()
        }

        // Only captures the first group of each match
        private fun parseAllMatches(text: String, pattern: Pattern): List<String> {
            val ret = mutableListOf<String>()
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                if (matcher.groupCount() > 0) {
                    ret.add(matcher.group(1)!!)
                }
            }
            return ret
        }

        private fun parseOneValueOrDefault(
            text: String,
            pattern: Pattern,
            defaultValue: String
        ): String = parseOneValueOrDefault(text, pattern, 1, defaultValue)

        private fun parseOneValueOrDefault(
            text: String,
            pattern: Pattern,
            group: Int,
            defaultValue: String
        ): String {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                if (matcher.groupCount() >= group) {  // Groups are 1-indexed
                    return matcher.group(group) ?: defaultValue
                }
            }
            return defaultValue
        }

        private fun isPatternFindable(text: String, pattern: Pattern): Boolean =
            pattern.matcher(text).find()

        // Sort tasks array and return it. Changes input array.
        @JvmStatic
        fun sortTasks(
            tasks: MutableList<TodoTxtTask>,
            orderBy: String,
            descending: Boolean
        ): List<TodoTxtTask> {
            Collections.sort(tasks, SttTaskSimpleComparator(orderBy, descending))
            return tasks
        }
    }
}
