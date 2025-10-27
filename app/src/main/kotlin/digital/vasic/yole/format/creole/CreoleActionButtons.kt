package digital.vasic.yole.format.creole

import digital.vasic.yole.format.ActionButtonBase
import digital.vasic.yole.model.Document
import android.content.Context
import digital.vasic.yole.R

class CreoleActionButtons(context: Context, document: Document) : ActionButtonBase(context, document) {
    override fun getFormatActionsKey(): Int {
        return R.string.pref_key__creole__action_keys
    }

    override fun getFormatActionList(): List<ActionItem> {
        return emptyList()
    }
}
