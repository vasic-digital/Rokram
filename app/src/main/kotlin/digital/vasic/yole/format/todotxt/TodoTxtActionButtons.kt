/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.todotxt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.frontend.YoleDialogFactory
import digital.vasic.yole.frontend.textview.TextViewUtils
import digital.vasic.yole.model.Document
import digital.vasic.opoc.util.GsCollectionUtils
import digital.vasic.opoc.util.GsFileUtils
import digital.vasic.opoc.wrapper.GsCallback
import java.io.File
import java.text.ParseException
import java.util.*

class TodoTxtActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            ActionItem(R.string.abid_todotxt_toggle_done, R.drawable.ic_check_box_black_24dp, R.string.toggle_done),
            ActionItem(R.string.abid_todotxt_add_context, R.drawable.gs_email_sign_black_24dp, R.string.add_context),
            ActionItem(R.string.abid_todotxt_add_project, R.drawable.ic_new_label_black_24dp, R.string.add_project),
            ActionItem(R.string.abid_todotxt_priority, R.drawable.ic_star_border_black_24dp, R.string.priority),
            ActionItem(R.string.abid_todotxt_archive_done_tasks, R.drawable.ic_archive_black_24dp, R.string.archive_completed_tasks),
            ActionItem(R.string.abid_todotxt_due_date, R.drawable.ic_date_range_black_24dp, R.string.due_date),
            ActionItem(R.string.abid_todotxt_sort_todo, R.drawable.ic_sort_by_alpha_black_24dp, R.string.sort_by),
            ActionItem(R.string.abid_common_insert_link, R.drawable.ic_link_black_24dp, R.string.insert_link),
            ActionItem(R.string.abid_common_insert_image, R.drawable.ic_image_black_24dp, R.string.insert_image),
            ActionItem(R.string.abid_common_insert_audio, R.drawable.ic_keyboard_voice_black_24dp, R.string.audio)
        )
    }

    @StringRes
    override fun getFormatActionsKey(): Int = R.string.pref_key__todotxt__action_keys

    @SuppressLint("NonConstantResourceId")
    override fun onActionClick(@StringRes action: Int): Boolean {
        val selTasks = TodoTxtTask.getSelectedTasks(_hlEditor)

        when (action) {
            R.string.abid_todotxt_toggle_done -> {
                val doneMark = "x" + (if (_appSettings.isTodoAddCompletionDateEnabled) " ${TodoTxtTask.getToday()}" else "") + " "
                val bodyWithPri = "(.*)(\\spri:([A-Z])(?=\\s|$))(.*)" // +1 = pre, +2 = full tag, +3 = pri, +4 = post
                val doneWithDate = "^([Xx]\\s(?:${TodoTxtTask.PT_DATE}\\s)?)"
                val startingPriority = "^\\(([A-Z])\\)\\s"
                runRegexReplaceAction(
                    // If task not done and starts with a priority and contains a pri tag
                    ReplacePattern("$startingPriority$bodyWithPri", "$doneMark\$2 pri:\$1\$5"),
                    // else if task not done and starts with a priority and does not contain a pri tag
                    ReplacePattern("$startingPriority(.*)(\\s*)", "$doneMark\$2 pri:\$1"),
                    // else if task is done and contains a pri tag
                    ReplacePattern("$doneWithDate$bodyWithPri", "(\$4) \$2\$5"),
                    // else if task is done and does not contain a pri tag
                    ReplacePattern(doneWithDate, ""),
                    // else replace task start with 'x ...'
                    ReplacePattern("^", doneMark)
                )
                return true
            }
            R.string.abid_todotxt_add_context -> {
                addRemoveItems("@", R.string.insert_context) { tasks -> TodoTxtTask.getContexts(tasks) }
                return true
            }
            R.string.abid_todotxt_add_project -> {
                addRemoveItems("+", R.string.insert_project) { tasks -> TodoTxtTask.getProjects(tasks) }
                return true
            }
            R.string.abid_todotxt_priority -> {
                YoleDialogFactory.showPriorityDialog(activity, selTasks[0].getPriority()) { priority ->
                    setPriority(if (priority.length == 1) priority[0] else TodoTxtTask.PRIORITY_NONE)
                }
                return true
            }
            R.string.abid_todotxt_due_date -> {
                setDueDate(_appSettings.dueDateOffset)
                return true
            }
            R.string.abid_todotxt_archive_done_tasks -> {
                archiveDoneTasks()
                return true
            }
            R.string.abid_todotxt_sort_todo -> {
                YoleDialogFactory.showSttSortDialogue(activity) { orderBy, descending ->
                    Thread {
                        val tasks = TodoTxtTask.getAllTasks(_hlEditor.text!!)
                        TodoTxtTask.sortTasks(tasks.toMutableList(), orderBy, descending)
                        setEditorTextAsync(TodoTxtTask.tasksToString(tasks))
                        _appSettings.setStringList(LAST_SORT_ORDER_KEY, listOf(orderBy, descending.toString()))
                    }.start()
                }
                return true
            }
            else -> return runCommonAction(action)
        }
    }

    override fun onActionLongClick(@StringRes action: Int): Boolean {
        when (action) {
            R.string.abid_todotxt_add_context -> {
                YoleDialogFactory.showSttKeySearchDialog(activity, _hlEditor, R.string.browse_by_context, true, true, TodoTxtFilter.TYPE.CONTEXT)
                return true
            }
            R.string.abid_todotxt_add_project -> {
                YoleDialogFactory.showSttKeySearchDialog(activity, _hlEditor, R.string.browse_by_project, true, true, TodoTxtFilter.TYPE.PROJECT)
                return true
            }
            R.string.abid_todotxt_sort_todo -> {
                val last = _appSettings.getStringList(LAST_SORT_ORDER_KEY)
                if (last != null && last.size == 2) {
                    val tasks = TodoTxtTask.getAllTasks(_hlEditor.text!!)
                    TodoTxtTask.sortTasks(tasks.toMutableList(), last[0], last[1].toBoolean())
                    setEditorTextAsync(TodoTxtTask.tasksToString(tasks))
                }
                return true
            }
            R.string.abid_todotxt_priority -> {
                val text = _hlEditor.text!!
                val sel = TextViewUtils.getSelection(_hlEditor)
                val lineStart = TextViewUtils.getLineStart(text, sel[0])
                val lineEnd = TextViewUtils.getLineEnd(text, sel[1])
                val tasks = TodoTxtTask.getTasks(text, sel)
                var prevPriority = '\u0000'
                var nextPriority = '\u0000'
                var areAllSamePriority = true

                if (lineStart != 0) {
                    val prevLineStart = TextViewUtils.getLineStart(text, lineStart - 1)
                    val prevLineEnd = TextViewUtils.getLineEnd(text, prevLineStart)
                    val prevLine = text.subSequence(prevLineStart, prevLineEnd).toString()
                    prevPriority = TodoTxtTask(prevLine).getPriority()
                }
                if (lineEnd != text.length) {
                    val nextLineStart = TextViewUtils.getLineStart(text, lineEnd + 1)
                    val nextLineEnd = TextViewUtils.getLineEnd(text, nextLineStart)
                    val nextLine = text.subSequence(nextLineStart, nextLineEnd).toString()
                    nextPriority = TodoTxtTask(nextLine).getPriority()
                }
                for (task in tasks) {
                    if (task.getPriority() != tasks[0].getPriority()) {
                        areAllSamePriority = false
                        break
                    }
                }
                if (areAllSamePriority) {
                    when {
                        prevPriority != tasks[0].getPriority() && prevPriority != '\u0000' ->
                            setPriority(prevPriority)
                        nextPriority != tasks[tasks.size - 1].getPriority() && nextPriority != '\u0000' ->
                            setPriority(nextPriority)
                        else ->
                            setPriority(TodoTxtTask.PRIORITY_NONE)
                    }
                } else {
                    if (prevPriority != '\u0000') {
                        setPriority(prevPriority)
                    } else {
                        setPriority(tasks[0].getPriority())
                    }
                }
                return true
            }
            R.string.abid_todotxt_due_date -> {
                setDate()
                return true
            }
            else -> return runCommonLongPressAction(action)
        }
    }

    fun archiveDoneTasks() {
        val lastDoneName = _appSettings.getLastTodoDoneName(_document.path)
        YoleDialogFactory.showSttArchiveDialog(activity, lastDoneName) { callbackPayload ->
            val doneName = Document.normalizeFilename(callbackPayload)
            val text = _hlEditor.text!!
            val sel = TextViewUtils.getSelection(text)
            val offsets = TextViewUtils.getLineOffsetFromIndex(text, *sel)

            val keep = ArrayList<TodoTxtTask>()
            val move = ArrayList<TodoTxtTask>()
            val allTasks = TodoTxtTask.getAllTasks(text)

            for (i in allTasks.indices) {
                val task = allTasks[i]
                if (task.isDone()) {
                    move.add(task)
                    if (i <= offsets[0][0]) offsets[0][0]--
                    if (i <= offsets[1][0]) offsets[1][0]--
                } else {
                    keep.add(task)
                }
            }

            if (move.isNotEmpty() && _document.testCreateParent()) {
                val doneFile = File(_document.file.parentFile, doneName)
                val doneContents = StringBuilder()
                if (doneFile.exists() && doneFile.canRead()) {
                    doneContents.append(GsFileUtils.readTextFileFast(doneFile).first.trim()).append("\n")
                }
                doneContents.append(TodoTxtTask.tasksToString(move)).append("\n")

                // Write to done file
                if (Document(doneFile).saveContent(activity, doneContents.toString())) {
                    val tasksString = TodoTxtTask.tasksToString(keep)
                    _hlEditor.setText(tasksString)
                    TextViewUtils.setSelectionFromOffsets(_hlEditor, offsets)
                }
            }
            _appSettings.setLastTodoDoneName(_document.path, doneName)
        }
    }

    override fun runTitleClick(): Boolean {
        YoleDialogFactory.showSttFilteringDialog(activity, _hlEditor)
        return true
    }

    override fun onSearch(): Boolean {
        YoleDialogFactory.showSttSearchDialog(activity, _hlEditor)
        return true
    }

    private fun addRemoveItems(
        prefix: String,
        @StringRes titleResId: Int,
        keyGetter: GsCallback.r1<Collection<String>, List<TodoTxtTask>>
    ) {
        val all = TreeSet(keyGetter.callback(TodoTxtTask.getAllTasks(_hlEditor.text!!)))
        val additional = TodoTxtTask(_appSettings.todotxtAdditionalContextsAndProjects)
        all.addAll(keyGetter.callback(listOf(additional)))

        val current = HashSet(keyGetter.callback(TodoTxtTask.getSelectedTasks(_hlEditor)))

        val append = _appSettings.isTodoAppendProConOnEndEnabled

        YoleDialogFactory.showUpdateItemsDialog(activity, titleResId, all, current) { updated ->
            val chunk = TextViewUtils.ChunkedEditable.wrap(_hlEditor.text!!)
            for (item in GsCollectionUtils.setDiff(current, updated)) {
                removeItem(chunk, prefix + item)
            }
            for (item in GsCollectionUtils.setDiff(updated, current)) {
                insertUniqueItem(chunk, prefix + item, append)
            }
            chunk.applyChanges()
        }
    }

    private fun trimLeadingWhiteSpace() {
        runRegexReplaceAction("^\\s*", "")
    }

    private fun setPriority(priority: Char) {
        val patterns = ArrayList<ReplacePattern>()
        if (priority == TodoTxtTask.PRIORITY_NONE) {
            patterns.add(ReplacePattern(TodoTxtTask.PATTERN_PRIORITY_ANY, ""))
        } else {
            val priorityStr = String.format("(%c) ", priority)
            patterns.add(ReplacePattern(TodoTxtTask.PATTERN_PRIORITY_ANY, priorityStr))
            patterns.add(ReplacePattern("^\\s*", priorityStr))
        }
        runRegexReplaceAction(patterns)
        trimLeadingWhiteSpace()
    }

    private fun setDate() {
        val text = _hlEditor.text
        val sel = TextViewUtils.getSelection(text)
        if (text == null || sel[0] < 0) {
            return
        }
        val selStr = text.subSequence(sel[0], sel[1]).toString()
        val initDate = parseDateString(selStr, Calendar.getInstance())

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val fmtCal = Calendar.getInstance()
            fmtCal.set(year, month, day)
            val newDate = TodoTxtTask.DATEF_YYYY_MM_DD.format(fmtCal.time)
            text.replace(sel[0], sel[1], newDate)
        }

        DateFragment()
            .setListener(listener)
            .setCalendar(initDate)
            .show((activity as FragmentActivity).supportFragmentManager, "date")
    }

    private fun setDueDate(offset: Int) {
        val dueString = TodoTxtTask.getSelectedTasks(_hlEditor)[0].getDueDate()
        var initDate = parseDateString(dueString, Calendar.getInstance())
        initDate.add(Calendar.DAY_OF_MONTH, if (dueString.isEmpty()) offset else 0)

        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val fmtCal = Calendar.getInstance()
            fmtCal.set(year, month, day)
            val newDue = "due:" + TodoTxtTask.DATEF_YYYY_MM_DD.format(fmtCal.time)
            runRegexReplaceAction(
                // Replace due date
                ReplacePattern(TodoTxtTask.PATTERN_DUE_DATE, "\$1$newDue\$4"),
                // Add due date to end if none already exists. Will correctly handle trailing whitespace.
                ReplacePattern("\\s*$", " $newDue")
            )
        }

        val clear = DialogInterface.OnClickListener { _, _ ->
            runRegexReplaceAction(ReplacePattern(TodoTxtTask.PATTERN_DUE_DATE, "\$4"))
        }

        DateFragment()
            .setListener(listener)
            .setCalendar(initDate)
            .setMessage(context.getString(R.string.due_date))
            .setExtraLabel(context.getString(R.string.clear))
            .setExtraListener(clear)
            .show((activity as FragmentActivity).supportFragmentManager, "date")
    }

    /**
     * A DialogFragment to manage showing a DatePicker
     * Must be public and have default constructor.
     */
    class DateFragment : DialogFragment() {

        private var listener: DatePickerDialog.OnDateSetListener? = null
        private var extraListener: DialogInterface.OnClickListener? = null
        private var extraLabel: String? = null

        private var year: Int = 0
        private var month: Int = 0
        private var day: Int = 0
        private var message: String? = null

        init {
            setCalendar(Calendar.getInstance())
        }

        fun setListener(listener: DatePickerDialog.OnDateSetListener): DateFragment {
            this.listener = listener
            return this
        }

        fun setExtraListener(listener: DialogInterface.OnClickListener): DateFragment {
            this.extraListener = listener
            return this
        }

        fun setExtraLabel(label: String): DateFragment {
            this.extraLabel = label
            return this
        }

        fun setYear(year: Int): DateFragment {
            this.year = year
            return this
        }

        fun setMonth(month: Int): DateFragment {
            this.month = month
            return this
        }

        fun setDay(day: Int): DateFragment {
            this.day = day
            return this
        }

        fun setMessage(message: String): DateFragment {
            this.message = message
            return this
        }

        fun setCalendar(calendar: Calendar): DateFragment {
            setYear(calendar.get(Calendar.YEAR))
            setMonth(calendar.get(Calendar.MONTH))
            setDay(calendar.get(Calendar.DAY_OF_MONTH))
            return this
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): DatePickerDialog {
            super.onCreateDialog(savedInstanceState)

            val dialog = DatePickerDialog(requireContext(), listener, year, month, day)

            if (!message.isNullOrEmpty()) {
                dialog.setMessage(message)
            }

            if (extraListener != null && !extraLabel.isNullOrEmpty()) {
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, extraLabel, extraListener)
            }

            return dialog
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Do not auto-recreate
            if (savedInstanceState != null) {
                dismiss()
            }
        }
    }

    companion object {
        private val LAST_SORT_ORDER_KEY = TodoTxtActionButtons::class.java.canonicalName + "_last_sort_order_key"

        private fun removeItem(editable: Editable, item: String) {
            runRegexReplaceAction(
                editable,
                // In the middle - replace with space
                ReplacePattern(String.format("\\s\\Q%s\\E\\s", item), " "),
                // In the end - remove
                ReplacePattern(String.format("\\s\\Q%s\\E$", item), "")
            )
        }

        private fun insertUniqueItem(editable: Editable, item: String, append: Boolean) {
            // Pattern to match <space><literal string><space OR end of line>
            // i.e. to check if a word is present in the line
            val pattern = java.util.regex.Pattern.compile(String.format("\\s\\Q%s\\E(:?\\s|$)", item))
            val lines = TextViewUtils.getSelectedLines(editable)
            // Multiline or setting
            if (append || lines.contains("\n")) {
                runRegexReplaceAction(
                    editable,
                    // Replace existing item with itself. i.e. do nothing
                    ReplacePattern(pattern, "\$0"),
                    // Append to end
                    ReplacePattern("\\s*$", " $item")
                )
            } else if (!pattern.matcher(lines).find()) {
                insertInline(editable, item)
            }
        }

        private fun insertInline(editable: Editable, thing: String) {
            val sel = TextViewUtils.getSelection(editable)
            if (sel[0] < 0) {
                return
            }

            var modifiedThing = thing
            if (sel[0] > 0) {
                val before = editable[sel[0] - 1]
                if (before != ' ' && before != '\n') {
                    modifiedThing = " $modifiedThing"
                }
            }
            if (sel[1] < editable.length) {
                val after = editable[sel[1]]
                if (after != ' ' && after != '\n') {
                    modifiedThing = "$modifiedThing "
                }
            }
            editable.replace(sel[0], sel[1], modifiedThing)
        }

        private fun parseDateString(dateString: String?, fallback: Calendar): Calendar {
            if (dateString == null || dateString.length != TodoTxtTask.DATEF_YYYY_MM_DD_LEN) {
                return fallback
            }

            return try {
                val calendar = Calendar.getInstance()
                calendar.time = TodoTxtTask.DATEF_YYYY_MM_DD.parse(dateString)!!
                calendar
            } catch (e: ParseException) {
                fallback
            }
        }
    }
}
