/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.restructuredtext

import android.content.Context
import digital.vasic.yole.format.TextConverterBase
import java.io.File
import java.util.regex.Pattern

/**
 * reStructuredText (RST) text converter.
 *
 * Converts reStructuredText markup to HTML.
 * RST is commonly used in Python documentation (Sphinx, ReadTheDocs).
 */
class RestructuredtextTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        if (markup.isEmpty()) return ""

        var html = markup

        // Convert headers (simplified - RST uses underlines for headers)
        html = html.replace(Regex("^=+$"), "")
        html = html.replace(Regex("^-+$"), "")
        html = html.replace(Regex("^(.*?)\\n=+$", RegexOption.MULTILINE), "<h1>$1</h1>")
        html = html.replace(Regex("^(.*?)\\n-+$", RegexOption.MULTILINE), "<h2>$1</h2>")

        // Convert inline markup
        html = RST_INLINE_LITERAL.matcher(html).replaceAll("<code>$1</code>")
        html = RST_BOLD.matcher(html).replaceAll("<strong>$1</strong>")
        html = RST_ITALIC.matcher(html).replaceAll("<em>$1</em>")

        // Convert code blocks (:: marker)
        html = RST_CODE_BLOCK.matcher(html).replaceAll("<pre><code>$1</code></pre>")

        // Convert lists (basic)
        html = html.replace(Regex("^\\* (.*)$", RegexOption.MULTILINE), "<li>$1</li>")
        html = html.replace(Regex("((<li>.*</li>\\s*)+)"), "<ul>$1</ul>")

        // Convert paragraphs
        html = html.replace("\n\n", "</p><p>")
        if (!html.trim().startsWith("<")) {
            html = "<p>$html</p>"
        }

        return putContentIntoTemplate(context, html, lightMode, file, "", "")
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT = setOf(".rst", ".rest")

        private val RST_HEADERS: Pattern = Pattern.compile("^(=+|-+|`+|:+|\\.+|\\'+|\"+|~+|\\^+|_+|\\*+|=*)$", Pattern.MULTILINE)
        private val RST_INLINE_LITERAL: Pattern = Pattern.compile("``([^`]+)``")
        private val RST_BOLD: Pattern = Pattern.compile("\\*\\*([^\\*]+)\\*\\*")
        private val RST_ITALIC: Pattern = Pattern.compile("\\*([^\\*]+)\\*")
        private val RST_CODE_BLOCK: Pattern = Pattern.compile("::\\s*\\n\\n(.*?)(?=\\n\\n|\\Z)", Pattern.DOTALL)
    }
}
