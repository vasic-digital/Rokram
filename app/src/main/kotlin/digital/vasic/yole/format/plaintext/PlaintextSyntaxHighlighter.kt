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
package digital.vasic.yole.format.plaintext

import android.util.Log
import digital.vasic.yole.format.plaintext.highlight.CodeTheme
import digital.vasic.yole.format.plaintext.highlight.HighlightConfigLoader
import digital.vasic.yole.format.plaintext.highlight.Syntax
import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase
import digital.vasic.yole.model.AppSettings

/**
 * Syntax highlighter for plain text and source code files.
 *
 * Provides:
 * - Code syntax highlighting based on file extension
 * - Configurable themes (loads from highlight configs)
 * - Pattern-based rule matching
 * - Tab visualization, hex color underlining, link highlighting
 *
 * Uses external syntax definitions and color themes from highlight/ package.
 */
open class PlaintextSyntaxHighlighter : SyntaxHighlighterBase {

    private var rules: ArrayList<Syntax.Rule>? = null
    private var styles: HashMap<String, CodeTheme.ThemeValue>? = null

    companion object {
        @JvmField
        val configLoader = HighlightConfigLoader()
    }

    constructor(appSettings: AppSettings) : super(appSettings)

    constructor(appSettings: AppSettings, extension: String) : super(appSettings) {
        try {
            val syntax = configLoader.getSyntax(_appSettings.getAppContext(), extension)
            if (syntax != null) {
                rules = syntax.rules
                val codeTheme = configLoader.getTheme(_appSettings.getAppContext(), "default")
                if (codeTheme != null) {
                    styles = codeTheme.styles
                }
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, e.toString())
        }
    }

    override fun generateSpans() {
        createTabSpans(_tabSize)
        createUnderlineHexColorsSpans()
        createSmallBlueLinkSpans()

        val rulesLocal = rules
        val stylesLocal = styles

        if (rulesLocal == null || stylesLocal == null) {
            return
        }

        for (rule in rulesLocal) {
            val style = stylesLocal[rule.type]
            if (style != null) {
                createColorSpanForMatches(rule.pattern, style.colorInt)
            }
        }
    }
}
