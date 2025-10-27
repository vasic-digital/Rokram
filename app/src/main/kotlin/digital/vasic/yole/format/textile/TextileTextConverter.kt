/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   Maintained 2025 by Milos Vasic
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   SPDX-FileCopyrightText: 2025 Milos Vasic
 *   SPDX-License-Identifier: Apache-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.textile

import android.content.Context
import digital.vasic.yole.format.TextConverterBase
import java.io.File

class TextileTextConverter : TextConverterBase() {

    override fun convertMarkup(
        markup: String,
        context: Context,
        lightMode: Boolean,
        lineNum: Boolean,
        file: File?
    ): String {
        // TODO: Implement markup to HTML conversion
        return markup
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        // TODO: Implement file detection logic
        return false
    }
}
