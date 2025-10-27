package digital.vasic.yole.format.orgmode

import android.content.Context
import digital.vasic.yole.format.TextConverterBase
import digital.vasic.opoc.format.GsTextUtils
import java.io.File

@Suppress("WeakerAccess")
class OrgmodeTextConverter : TextConverterBase() {

    companion object {
        private val EXT = listOf(".org")

        /**
         * this file is exported by browserify from  [org-js](https://github.com/mooz/org-js)
         */
        const val HTML_ORG_JS_INCLUDE = "<script src='file:///android_asset/orgmode/org-bundle.js'></script>\n"
        const val HTML_ORG_CSS_INCLUDE = "<link href='file:///android_asset/orgmode/org.css' type='text/css' rel='stylesheet'/>\n"
    }

    //########################
    //## Methods
    //########################

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        val converted = "<div id=\"orgmode_content\"></div>\n"
        val onLoadJs = buildString {
            append("var textBase64 = `")
            //convert a text to base64 to simplify supporting special characters
            append(GsTextUtils.toBase64(markup))
            append("`;\n")
            //decode base64 to utf8 string
            append("const asciiPlainText = atob(textBase64);\n")
            append("const length = asciiPlainText.length;\n")
            append("const bytes = new Uint8Array(length);\n")
            append("for (let i = 0; i < length; i++) {\n")
            append("    bytes[i] = asciiPlainText.charCodeAt(i);\n")
            append("}\n")
            append("const decoder = new TextDecoder();\n")
            append("var utf8PlainText = decoder.decode(bytes);")
            append("var parser = new org.Parser();\n")
            append("var orgDocument = parser.parse(utf8PlainText);\n")
            append("var orgHTMLDocument = orgDocument.convert(org.ConverterHTML, {});")
            append("document.getElementById(\"orgmode_content\").innerHTML = orgHTMLDocument;")
        }
        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, HTML_ORG_JS_INCLUDE + HTML_ORG_CSS_INCLUDE)
    }

    override fun getContentType(): String {
        return CONTENT_TYPE_HTML
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return EXT.contains(ext)
    }
}
