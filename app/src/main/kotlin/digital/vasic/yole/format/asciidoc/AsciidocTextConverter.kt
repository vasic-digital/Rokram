/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.asciidoc

import android.content.Context
import digital.vasic.opoc.format.GsTextUtils
import digital.vasic.opoc.util.GsContextUtils
import digital.vasic.yole.format.TextConverterBase
import java.io.File

@Suppress("WeakerAccess")
class AsciidocTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        val converted = "<div id=\"asciidoc_content\"></div>\n"
        val onLoadJs = buildString {
            append("var textBase64 = `")
            append(GsTextUtils.toBase64(markup))
            append("`;\n")
            append("const asciiPlainText = atob(textBase64);\n")
            append("const length = asciiPlainText.length;\n")
            append("const bytes = new Uint8Array(length);\n")
            append("for (let i = 0; i < length; i++) {\n")
            append("    bytes[i] = asciiPlainText.charCodeAt(i);\n")
            append("}\n")
            append("const decoder = new TextDecoder();\n")
            append("var utf8PlainText = decoder.decode(bytes);")
            append("var asciidoctor = Asciidoctor();\n")
            append("var html = asciidoctor.convert(utf8PlainText, {standalone : true, attributes : {nofooter: true, stylesheet: \"")
            append(
                if (!lightMode && GsContextUtils.instance.isDarkModeEnabled(context))
                    HTML_ASCIIDOCJS_DARK_CSS_INCLUDE
                else
                    HTML_ASCIIDOCJS_DEFAULT_CSS_INCLUDE
            )
            append("\"}});\n")
            append("document.getElementById(\"asciidoc_content\").innerHTML = html;")
        }
        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, HTML_ASCIIDOCJS_JS_INCLUDE)
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT = setOf(".adoc", ".asciidoc", ".asc")
        const val HTML_ASCIIDOCJS_JS_INCLUDE = "<script src='file:///android_asset/asciidoc/asciidoctor.min.js'></script>"
        const val HTML_ASCIIDOCJS_DEFAULT_CSS_INCLUDE = "file:///android_asset/asciidoc/asciidoctor.css"

        /**
         * that file was loaded from [dark.css](https://github.com/darshandsoni/asciidoctor-skins/blob/gh-pages/css/dark.css)
         * "import" block was changed to load local css
         * "literalblock" block was changes to support new rules
         */
        const val HTML_ASCIIDOCJS_DARK_CSS_INCLUDE = "file:///android_asset/asciidoc/dark.css"
    }
}
