package digital.vasic.yole.format.restructuredtext;

import digital.vasic.yole.format.TextConverterBase;
import digital.vasic.opoc.util.GsFileUtils;
import android.content.Context;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RestructuredtextTextConverter extends TextConverterBase {
    private static final Pattern RST_HEADERS = Pattern.compile("^(=+|-+|`+|:+|\\.+|\\'+|\"+|~+|\\^+|_+|\\*+|=*)$", Pattern.MULTILINE);
    private static final Pattern RST_INLINE_LITERAL = Pattern.compile("``([^`]+)``");
    private static final Pattern RST_BOLD = Pattern.compile("\\*\\*([^\\*]+)\\*\\*");
    private static final Pattern RST_ITALIC = Pattern.compile("\\*([^\\*]+)\\*");
    private static final Pattern RST_CODE_BLOCK = Pattern.compile("::\\s*\\n\\n(.*?)(?=\\n\\n|\\Z)", Pattern.DOTALL);

    @Override
    public boolean isFileOutOfThisFormat(File file) {
        if (file == null) return false;
        String ext = GsFileUtils.getFilenameExtension(file).toLowerCase();
        return ext.equals("rst") || ext.equals("rest");
    }

    @Override
    public String convertMarkupToHtml(String markup, Context context, boolean lightMode, int lineNum, File file) {
        if (markup == null) return "";

        String html = markup;

        // Convert headers (simplified - would need more complex parsing for proper RST)
        html = html.replaceAll("^=+$", "").replaceAll("^-+$", "");
        html = html.replaceAll("^(.*?)\\n=+$", "<h1>$1</h1>");
        html = html.replaceAll("^(.*?)\\n-+$", "<h2>$1</h2>");

        // Convert inline markup
        html = RST_INLINE_LITERAL.matcher(html).replaceAll("<code>$1</code>");
        html = RST_BOLD.matcher(html).replaceAll("<strong>$1</strong>");
        html = RST_ITALIC.matcher(html).replaceAll("<em>$1</em>");

        // Convert code blocks
        html = RST_CODE_BLOCK.matcher(html).replaceAll("<pre><code>$1</code></pre>");

        // Convert lists (basic)
        html = html.replaceAll("^\\* (.*)$", "<li>$1</li>", Pattern.MULTILINE);
        html = html.replaceAll("((<li>.*</li>\\s*)+)", "<ul>$1</ul>");

        // Convert paragraphs
        html = html.replaceAll("\\n\\n", "</p><p>");
        if (!html.trim().startsWith("<")) {
            html = "<p>" + html + "</p>";
        }

        return html;
    }
}
