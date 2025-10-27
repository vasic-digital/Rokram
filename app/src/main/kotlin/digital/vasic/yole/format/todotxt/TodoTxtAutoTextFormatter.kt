/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.todotxt

import android.text.InputFilter
import android.text.Spanned
import digital.vasic.opoc.format.GsTextUtils
import java.util.*

class TodoTxtAutoTextFormatter : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            if (start < source.length && dstart <= dest.length && GsTextUtils.isNewLine(source, start, end)) {
                return autoIndent(source)
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return source
    }

    private fun autoIndent(source: CharSequence): CharSequence {
        return source.toString() + TodoTxtTask.DATEF_YYYY_MM_DD.format(Date()) + " "
    }
}
