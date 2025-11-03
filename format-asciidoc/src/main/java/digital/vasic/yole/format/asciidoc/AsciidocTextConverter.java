/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.format.asciidoc;

import android.content.Context;

import digital.vasic.yole.format.TextConverterBase;
import digital.vasic.opoc.format.GsTextUtils;
import digital.vasic.opoc.util.GsContextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * AsciiDoc text converter for Android.
 * 
 * This converter handles AsciiDoc markup format using Asciidoctor.js for client-side
 * rendering. It supports both light and dark themes and provides automatic format
 * detection based on file extensions.
 * 
 * @author Gregor Santner
 * @since 2018
 */
@SuppressWarnings("WeakerAccess")
public class AsciidocTextConverter extends TextConverterBase {
    //########################
    //## Extensions
    //########################
    /**
     * Supported file extensions for AsciiDoc format.
     */
    private static final Set<String> EXT = new HashSet<>(Arrays.asList(".adoc", ".asciidoc", ".asc"));
    
    /**
     * JavaScript include for Asciidoctor.js library.
     */
    public static final String HTML_ASCIIDOCJS_JS_INCLUDE = "<script src='file:///android_asset/asciidoc/asciidoctor.min.js'></script>";
    
    /**
     * Default CSS include for light theme.
     */
    public static final String HTML_ASCIIDOCJS_DEFAULT_CSS_INCLUDE = "file:///android_asset/asciidoc/asciidoctor.css";
    
    /**
     * Dark theme CSS include.
     * 
     * This file was loaded from <a href="https://github.com/darshandsoni/asciidoctor-skins/blob/gh-pages/css/dark.css">dark.css</a>
     * The "import" block was changed to load local CSS and the "literalblock" block was
     * modified to support new rules.
     */
    public static final String HTML_ASCIIDOCJS_DARK_CSS_INCLUDE = "file:///android_asset/asciidoc/dark.css";

    /**
     * Convert AsciiDoc markup to HTML.
     * 
     * This method uses Asciidoctor.js to convert AsciiDoc content to HTML. The content
     * is first converted to base64 to handle special characters properly, then decoded
     * and processed by Asciidoctor. The method automatically selects the appropriate
     * CSS theme based on the current app theme settings.
     * 
     * @param markup The AsciiDoc markup content to convert
     * @param context Android context for accessing resources and settings
     * @param lightMode Whether to use light theme (true) or dark theme (false)
     * @param lineNum Whether to show line numbers (not used in this implementation)
     * @param file The source file (may be null)
     * @return HTML string with the converted content
     */
    @Override
    public String convertMarkup(String markup, Context context, boolean lightMode, boolean lineNum, File file) {
        String converted = "<div id=\"asciidoc_content\"></div>\n";
        String onLoadJs = "var textBase64 = `" +
                //convert a text to base64 to simplify supporting special characters
                GsTextUtils.toBase64(markup) +
                "`;\n" +
                //decode base64 to utf8 string
                "const asciiPlainText = atob(textBase64);\n" +
                "const length = asciiPlainText.length;\n" +
                "const bytes = new Uint8Array(length);\n" +
                "for (let i = 0; i < length; i++) {\n" +
                "    bytes[i] = asciiPlainText.charCodeAt(i);\n" +
                "}\n" +
                "const decoder = new TextDecoder();\n" +
                "var utf8PlainText = decoder.decode(bytes);" +
                "var asciidoctor = Asciidoctor();\n" +
                //standalone : true - to generate header 1 (= title) in the page. if don't do that - title will be absent.
                //nofooter: true - to don't generate footer (Last updated ...). if don't do that and use standalone : true - the page will have that footer.
                "var html = asciidoctor.convert(utf8PlainText, {standalone : true, attributes : {nofooter: true, stylesheet: \"" +
                (!lightMode && GsContextUtils.instance.isDarkModeEnabled(context) ? HTML_ASCIIDOCJS_DARK_CSS_INCLUDE : HTML_ASCIIDOCJS_DEFAULT_CSS_INCLUDE)
                + "\"}});\n" +
                "document.getElementById(\"asciidoc_content\").innerHTML = html;";
        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, HTML_ASCIIDOCJS_JS_INCLUDE);
    }

    /**
     * Check if a file is in AsciiDoc format based on its extension.
     * 
     * @param file The file to check (may be null)
     * @param name The filename (may be null)
     * @param ext The file extension (without dot)
     * @return true if the file extension is supported by this converter, false otherwise
     */
    @Override
    protected boolean isFileOutOfThisFormat(final File file, final String name, final String ext) {
        return EXT.contains(ext);
    }
}
