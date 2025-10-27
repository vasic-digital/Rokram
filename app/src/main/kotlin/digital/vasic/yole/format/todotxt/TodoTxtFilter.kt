package digital.vasic.yole.format.todotxt

import android.content.Context
import android.content.SharedPreferences
import android.util.Pair
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import digital.vasic.yole.R
import digital.vasic.yole.format.todotxt.TodoTxtTask.TodoDueState
import digital.vasic.opoc.model.GsSharedPreferencesPropertyBackend
import digital.vasic.opoc.wrapper.GsCallback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object TodoTxtFilter {

    // Special query keywords
    const val QUERY_PRIORITY_ANY = "pri"
    const val QUERY_DUE_TODAY = "due="
    const val QUERY_DUE_OVERDUE = "due<"
    const val QUERY_DUE_FUTURE = "due>"
    const val QUERY_DUE_ANY = "due"
    const val QUERY_DONE = "done"

    const val SAVED_TODO_VIEWS = "todo_txt__saved_todo_views"
    const val STRING_NONE = "-"
    private const val TITLE = "TITLE"
    private const val QUERY = "QUERY"
    private const val NULL_SENTINEL = "NULL SENTINEL"

    const val MAX_RECENT_VIEWS = 10

    enum class TYPE {
        PROJECT, CONTEXT, PRIORITY, DUE
    }

    data class SttFilterKey(
        @JvmField val key: String,    // Name
        @JvmField val count: Int,     // How many exist
        @JvmField val query: String?  // What to stick in the query
    )

    @JvmStatic
    fun getKeys(context: Context, tasks: List<TodoTxtTask>, type: TYPE): List<SttFilterKey> {
        return when (type) {
            TYPE.PROJECT -> getStringListKeys(tasks) { it.getProjects() }
            TYPE.CONTEXT -> getStringListKeys(tasks) { it.getContexts() }
            TYPE.PRIORITY -> getStringListKeys(tasks) { task ->
                if (task.getPriority() == TodoTxtTask.PRIORITY_NONE) {
                    null
                } else {
                    listOf(task.getPriority().uppercaseChar().toString())
                }
            }
            TYPE.DUE -> getDueKeys(context, tasks)
        }
    }

    private fun getStringListKeys(
        tasks: List<TodoTxtTask>,
        keyGetter: GsCallback.r1<List<String>?, TodoTxtTask>
    ): List<SttFilterKey> {
        val all = mutableListOf<String>()
        for (task in tasks) {
            if (!task.isDone()) {
                val tKeys = keyGetter.callback(task)
                all.addAll(if (tKeys.isNullOrEmpty()) listOf(NULL_SENTINEL) else tKeys)
            }
        }

        val keys = mutableListOf<SttFilterKey>()
        val unique = TreeSet(all)
        if (unique.remove(NULL_SENTINEL)) {
            keys.add(SttFilterKey(STRING_NONE, Collections.frequency(all, NULL_SENTINEL), null))
        }
        for (key in unique) {
            keys.add(SttFilterKey(key, Collections.frequency(all, key), key))
        }

        return keys
    }

    @JvmStatic
    fun getDueKeys(context: Context, tasks: List<TodoTxtTask>): List<SttFilterKey> {
        val all = mutableListOf<TodoDueState>()
        for (task in tasks) {
            all.add(task.getDueStatus())
        }
        val keys = mutableListOf<SttFilterKey>()
        keys.add(SttFilterKey(context.getString(R.string.due_future), Collections.frequency(all, TodoDueState.FUTURE), QUERY_DUE_FUTURE))
        keys.add(SttFilterKey(context.getString(R.string.due_today), Collections.frequency(all, TodoDueState.TODAY), QUERY_DUE_TODAY))
        keys.add(SttFilterKey(context.getString(R.string.due_overdue), Collections.frequency(all, TodoDueState.OVERDUE), QUERY_DUE_OVERDUE))
        keys.add(SttFilterKey(STRING_NONE, Collections.frequency(all, TodoDueState.NONE), null))

        return keys
    }

    // Convert a set of query keys into a formatted query
    @JvmStatic
    fun makeQuery(keys: Collection<String?>, isAnd: Boolean, type: TYPE): String {
        val prefix: String
        val nullKey: String
        when (type) {
            TYPE.CONTEXT -> {
                nullKey = "!@"
                prefix = "@"
            }
            TYPE.PROJECT -> {
                nullKey = "!+"
                prefix = "+"
            }
            TYPE.PRIORITY -> {
                nullKey = "!$QUERY_PRIORITY_ANY"
                prefix = "pri:"
            }
            TYPE.DUE -> {
                nullKey = "!$QUERY_DUE_ANY"
                prefix = ""
            }
        }

        val adjusted = mutableListOf<String>()
        for (key in keys) {
            if (key != null) {
                adjusted.add(prefix + key)
            } else {
                adjusted.add(nullKey)
            }
        }

        // We don't include done tasks by default
        return adjusted.joinToString(if (isAnd) " & " else " | ") + " & !$QUERY_DONE"
    }

    @JvmStatic
    fun saveFilter(context: Context, title: String, query: String) {
        try {
            // Create the view dict
            val obj = JSONObject()
            obj.put(TITLE, title)
            obj.put(QUERY, query)

            // This oldArray / newArray approach needed as array.remove is api 19+
            val newArray = JSONArray()
            newArray.put(obj)

            // Load the existing list of views and append the required number to the newArray
            val pref = context.getSharedPreferences(GsSharedPreferencesPropertyBackend.SHARED_PREF_APP, Context.MODE_PRIVATE)
            val oldArray = JSONArray(pref.getString(SAVED_TODO_VIEWS, "[]"))
            val addCount = minOf(MAX_RECENT_VIEWS - 1, oldArray.length())
            for (i in 0 until addCount) {
                newArray.put(oldArray.get(i))
            }

            // Save
            pref.edit().putString(SAVED_TODO_VIEWS, newArray.toString()).apply()

        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(context, "𐄂", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(context, String.format("✔ %s️", title), Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun deleteFilterIndex(context: Context, index: Int): Boolean {
        try {
            val pref = context.getSharedPreferences(GsSharedPreferencesPropertyBackend.SHARED_PREF_APP, Context.MODE_PRIVATE)
            // Load the existing list of views

            val oldArray = JSONArray(pref.getString(SAVED_TODO_VIEWS, "[]"))
            if (index < 0 || index >= oldArray.length()) {
                return false
            }

            val newArray = JSONArray()
            for (i in 0 until oldArray.length()) {
                if (i != index) {
                    newArray.put(oldArray.get(i))
                }
            }

            pref.edit().putString(SAVED_TODO_VIEWS, newArray.toString()).apply()

            return true
        } catch (e: JSONException) {
            e.printStackTrace()
            return false
        }
    }

    @JvmStatic
    fun loadSavedFilters(context: Context): List<Pair<String, String>> {
        val pref = context.getSharedPreferences(GsSharedPreferencesPropertyBackend.SHARED_PREF_APP, Context.MODE_PRIVATE)
        try {
            val loadedViews = mutableListOf<Pair<String, String>>()
            val jsonString = pref.getString(SAVED_TODO_VIEWS, "[]")
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                loadedViews.add(Pair.create(obj.getString(TITLE), obj.getString(QUERY)))
            }
            return loadedViews
        } catch (e: JSONException) {
            e.printStackTrace()
            pref.edit().remove(SAVED_TODO_VIEWS).apply()
        }
        return emptyList()
    }

    // Query matching

    @JvmStatic
    fun isMatchQuery(task: TodoTxtTask, query: CharSequence): Boolean {
        return try {
            val expression = parseQuery(task, query)
            evaluateExpression(expression)
        } catch (e: EmptyStackException) {
            // TODO - display a useful message somehow
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    // Pre-process the query to simplify the syntax
    private fun preProcess(query: CharSequence): String {
        return " $query "                  // Leading and trailing spaces
            .replace(" !", " ! ")    // Add space after exclamation mark
            .replace(" (", " ( ")    // Add space before opening paren
            .replace(") ", " ) ")    // Add space after closing paren
    }

    private fun isSyntax(c: Char): Boolean =
        c == '!' || c == '|' || c == '&' || c == '(' || c == ')'

    // Parse a query into an expression.
    // i.e. evaluate the elements in the query to true or false
    @VisibleForTesting
    @JvmStatic
    fun parseQuery(task: TodoTxtTask, query: CharSequence): String {
        val expression = StringBuilder()
        val parts = preProcess(query).split(" ")
        for (part in parts) {
            if (part.length == 1 && isSyntax(part[0])) {
                expression.append(part)
            } else if (part.isNotEmpty()) {
                expression.append(evalElement(task, part))
            }
        }
        return expression.toString()
    }

    // Evaluate a word (element) for truthyness or falsiness
    // Step through all the possible conditions
    private fun evalElement(task: TodoTxtTask, element: String): Char {
        val result: Boolean = when {
            element.startsWith(QUERY_PRIORITY_ANY) -> {
                when {
                    QUERY_PRIORITY_ANY == element -> task.getPriority() != TodoTxtTask.PRIORITY_NONE
                    element.length == 5 && element[3] == ':' -> task.getPriority() == element[4]
                    else -> false
                }
            }
            QUERY_DUE_TODAY == element -> task.getDueStatus() == TodoDueState.TODAY
            QUERY_DUE_OVERDUE == element -> task.getDueStatus() == TodoDueState.OVERDUE
            QUERY_DUE_FUTURE == element -> task.getDueStatus() == TodoDueState.FUTURE
            QUERY_DUE_ANY == element -> task.getDueStatus() != TodoDueState.NONE
            QUERY_DONE == element -> task.isDone()
            element == "@" -> task.getContexts().isNotEmpty()
            element == "+" -> task.getProjects().isNotEmpty()
            element.startsWith("@") -> task.getContexts().contains(element.substring(1))
            element.startsWith("+") -> task.getProjects().contains(element.substring(1))
            else -> {
                // Default to string match
                task.getLine().lowercase().contains(element.lowercase())
            }
        }

        return if (result) 'T' else 'F'
    }

    // Expression evaluator

    private fun isStart(stack: Stack<Char>): Boolean =
        stack.isEmpty() || stack.peek() == '('

    @JvmStatic
    fun isValue(stack: Stack<Char>): Boolean {
        if (!stack.isEmpty()) {
            val top = stack.peek()
            return top == 'T' || top == 'F'
        }
        return false
    }

    private fun toChar(v: Boolean): Char = if (v) 'T' else 'F'

    @JvmStatic
    fun evaluateOperations(stack: Stack<Char>) {
        while (!isStart(stack) && isValue(stack)) {
            val rhs = stack.pop()
            if (isStart(stack)) {
                stack.push(rhs)
                return
            }
            val op = stack.pop()
            when (op) {
                '|' -> stack.push(toChar(stack.pop() == 'T' || rhs == 'T'))
                '&' -> stack.push(toChar(stack.pop() == 'T' && rhs == 'T'))
                '!' -> stack.push(toChar(rhs == 'F'))
                else -> throw IllegalArgumentException("Unexpected character")
            }
        }
    }

    @JvmStatic
    fun evaluateExpression(expression: CharSequence): Boolean {
        val stack = Stack<Char>()
        for (i in expression.indices) {
            val c = expression[i]
            if (c == ')') {
                val value = stack.pop()
                if (stack.pop() != '(') {
                    throw IllegalArgumentException("Mismatched parenthesis")
                }
                stack.push(value)
            } else {
                stack.push(c)
            }
            evaluateOperations(stack)
        }
        if (stack.size == 1 && isValue(stack)) {
            return stack.pop() == 'T'
        }
        throw IllegalArgumentException("Malformed expression")
    }
}
