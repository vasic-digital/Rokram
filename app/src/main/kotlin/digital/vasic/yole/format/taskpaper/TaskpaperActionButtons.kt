package digital.vasic.yole.format.taskpaper

import androidx.annotation.StringRes
import digital.vasic.yole.R
import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.model.Document
import android.content.Context

class TaskpaperActionButtons(context: Context, document: Document?) : ActionButtonBase(context, document) {

    override fun getFormatActionList(): List<ActionItem> {
        return listOf(
            // TODO: Add Taskpaper-specific actions
        )
    }

    @StringRes
    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__taskpaper__action_keys
    }
}
