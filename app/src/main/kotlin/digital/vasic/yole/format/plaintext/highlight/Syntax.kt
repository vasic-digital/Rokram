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

import java.util.regex.Pattern

/**
 * Syntax highlighting definition for a programming language.
 *
 * Loaded from JSON configuration files.
 * Contains regex-based rules for identifying syntax elements.
 *
 * @property language Language name (e.g., "kotlin", "python", "java")
 * @property rules List of highlighting rules
 */
class Syntax {
    var language: String? = null
    var rules: ArrayList<Rule>? = null

    /**
     * Highlighting rule for a syntax element type.
     *
     * Lazily compiles regex pattern on first access.
     *
     * @property type Element type (e.g., "keyword", "string", "comment")
     * @property regex Regular expression to match this element type
     */
    class Rule {
        var type: String? = null
        var regex: String? = null
        private var m_pattern: Pattern? = null

        val pattern: Pattern
            get() {
                if (m_pattern == null) {
                    m_pattern = Pattern.compile(regex)
                }
                return m_pattern!!
            }
    }
}
