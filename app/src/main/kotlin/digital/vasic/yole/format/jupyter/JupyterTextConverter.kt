/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.jupyter

import android.content.Context
import digital.vasic.yole.format.TextConverterBase
import org.json.JSONObject
import java.io.File

/**
 * Jupyter Notebook text converter.
 *
 * Jupyter notebooks (.ipynb) are JSON files containing code cells,
 * markdown cells, and execution outputs.
 */
class JupyterTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        if (markup.isEmpty()) return ""

        return try {
            // Jupyter notebooks are JSON - parse and render cells
            val notebook = JSONObject(markup)
            val html = StringBuilder()

            html.append("<div class='jupyter-notebook'>")

            // Get cells array
            if (notebook.has("cells")) {
                val cells = notebook.getJSONArray("cells")
                for (i in 0 until cells.length()) {
                    val cell = cells.getJSONObject(i)
                    val cellType = cell.optString("cell_type", "")
                    val source = cell.optJSONArray("source")

                    when (cellType) {
                        "markdown" -> {
                            html.append("<div class='markdown-cell'>")
                            source?.let {
                                for (j in 0 until it.length()) {
                                    html.append(it.getString(j))
                                }
                            }
                            html.append("</div>")
                        }
                        "code" -> {
                            html.append("<div class='code-cell'><pre><code>")
                            source?.let {
                                for (j in 0 until it.length()) {
                                    html.append(it.getString(j).replace("<", "&lt;").replace(">", "&gt;"))
                                }
                            }
                            html.append("</code></pre></div>")
                        }
                    }
                }
            }

            html.append("</div>")
            putContentIntoTemplate(context, html.toString(), lightMode, file, "", "")
        } catch (e: Exception) {
            // If JSON parsing fails, display as plain text
            val html = "<pre>${markup.replace("<", "&lt;").replace(">", "&gt;")}</pre>"
            putContentIntoTemplate(context, html, lightMode, file, "", "")
        }
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT = setOf(".ipynb")
    }
}
