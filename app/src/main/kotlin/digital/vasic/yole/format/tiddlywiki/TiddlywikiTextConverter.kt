/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.tiddlywiki

import android.content.Context
import digital.vasic.yole.format.TextConverterBase
import java.io.File

/**
 * TiddlyWiki text converter.
 *
 * TiddlyWiki is a non-linear notebook wiki format.
 * Files use .tid extension and contain metadata followed by text content.
 */
class TiddlywikiTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        if (markup.isEmpty()) return ""

        var html = markup

        // TiddlyWiki uses wikitext markup similar to MediaWiki
        // Convert headers
        html = html.replace(Regex("^! (.*)$", RegexOption.MULTILINE), "<h1>$1</h1>")
        html = html.replace(Regex("^!! (.*)$", RegexOption.MULTILINE), "<h2>$1</h2>")
        html = html.replace(Regex("^!!! (.*)$", RegexOption.MULTILINE), "<h3>$1</h3>")

        // Convert bold and italic
        html = html.replace(Regex("''(.*?)''"), "<strong>$1</strong>")
        html = html.replace(Regex("//(.*?)//"), "<em>$1</em>")

        // Convert code
        html = html.replace(Regex("`(.*?)`"), "<code>$1</code>")

        // Convert lists
        html = html.replace(Regex("^\\* (.*)$", RegexOption.MULTILINE), "<li>$1</li>")
        html = html.replace(Regex("((<li>.*</li>\\s*)+)"), "<ul>$1</ul>")

        // Convert line breaks
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
        private val EXT = setOf(".tid", ".tiddler")
    }
}
