package digital.vasic.yole.format.taskpaper

import digital.vasic.yole.format.TextConverterBase
import android.content.Context
import java.io.File

class TaskpaperTextConverter : TextConverterBase() {
    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, lineNum: Boolean, file: File): String {
        // TODO: Implement markup to HTML conversion
        return markup
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        // TODO: Implement file detection logic
        return false
    }
}
