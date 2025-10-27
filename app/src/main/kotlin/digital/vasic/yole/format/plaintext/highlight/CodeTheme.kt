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
package digital.vasic.yole.format.plaintext.highlight

import android.graphics.Color

/**
 * Code syntax highlighting theme.
 *
 * Loaded from JSON configuration files.
 * Maps syntax element types to color styles.
 *
 * @property name Theme name (e.g., "default", "dark")
 * @property styles Map of type names to color/style values
 */
class CodeTheme {
    var name: String? = null
    var styles: HashMap<String, ThemeValue>? = null

    /**
     * Theme color value for a syntax element type.
     *
     * Lazily parses color string to int on first access.
     *
     * @property color Color in string format (e.g., "#FF0000")
     */
    class ThemeValue {
        internal var color: String? = null
        private var m_colorInt: Int? = null

        val colorInt: Int
            get() {
                if (m_colorInt == null) {
                    m_colorInt = Color.parseColor(color)
                }
                return m_colorInt!!
            }

        // Legacy Java compatibility
        fun getColor(): Int = colorInt
    }
}
