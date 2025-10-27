/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.keyvalue

import digital.vasic.yole.format.plaintext.PlaintextTextConverter
import java.io.File

open class KeyValueTextConverter : PlaintextTextConverter() {

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT = listOf(".yml", ".yaml", ".toml", ".vcf", ".ics", ".ini", ".json", ".zim")
    }
}
