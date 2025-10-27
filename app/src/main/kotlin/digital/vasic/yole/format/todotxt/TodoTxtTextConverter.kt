/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.todotxt

import android.content.Context
import androidx.core.text.TextUtilsCompat
import digital.vasic.yole.format.TextConverterBase
import java.io.File
import java.util.regex.Pattern

@Suppress("WeakerAccess")
class TodoTxtTextConverter : TextConverterBase() {

    override fun convertMarkup(
        markup: String,
        context: Context,
        lightMode: Boolean,
        lineNum: Boolean,
        file: File?
    ): String {
        val converted = HTML100_BODY_PRE_BEGIN +
                parse(TextUtilsCompat.htmlEncode(markup)) +
                HTML101_BODY_PRE_END
        return putContentIntoTemplate(context, converted, lightMode, file, "", "")
    }

    private fun parse(str: String): String {
        return str.replace("\n", "</br><span style='margin-bottom=20px;'/><hr/><span style='margin-bottom=20px;'/>")
    }

    override fun getContentType(): String = CONTENT_TYPE_HTML

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return name == "todo.txt" ||
                (TODOTXT_FILE_PATTERN.matcher(name).matches() && (name.endsWith(".txt") || name.endsWith(".text")))
    }

    companion object {
        private const val HTML100_BODY_PRE_BEGIN = "<pre style='white-space: pre-wrap;font-family: $TOKEN_FONT' >"
        private const val HTML101_BODY_PRE_END = "</pre>"

        @JvmField
        val TODOTXT_FILE_PATTERN: Pattern = Pattern.compile("(?i)(^todo[-.]?.*)|(.*[-.]todo\\.((txt)|(text))$)")
    }
}
