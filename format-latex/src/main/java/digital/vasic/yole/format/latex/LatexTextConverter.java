package digital.vasic.yole.format.latex;

import digital.vasic.yole.format.TextConverterBase;
import digital.vasic.opoc.util.GsFileUtils;
import android.content.Context;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LatexTextConverter extends TextConverterBase {
    private static final Pattern LATEX_INLINE_MATH = Pattern.compile("\\$([^$]+)\\$");
    private static final Pattern LATEX_DISPLAY_MATH = Pattern.compile("\\$\\$([^$]+)\\$\\$");
    private static final Pattern LATEX_BEGIN_END = Pattern.compile("\\\\begin\\{([^}]+)\\}(.*?)\\\\end\\{\\1\\}", Pattern.DOTALL);

    @Override
    public boolean isFileOutOfThisFormat(File file) {
        if (file == null) return false;
        String ext = GsFileUtils.getFilenameExtension(file).toLowerCase();
        return ext.equals("tex") || ext.equals("latex");
    }

    @Override
    public String convertMarkupToHtml(String markup, Context context, boolean lightMode, int lineNum, File file) {
        if (markup == null) return "";

        String html = markup;

        // Convert LaTeX math expressions to KaTeX format
        html = convertMathExpressions(html);

        // Basic LaTeX to HTML conversion
        html = html.replaceAll("\\\\textbf\\{([^}]+)\\}", "<strong>$1</strong>");
        html = html.replaceAll("\\\\textit\\{([^}]+)\\}", "<em>$1</em>");
        html = html.replaceAll("\\\\texttt\\{([^}]+)\\}", "<code>$1</code>");
        html = html.replaceAll("\\\\section\\{([^}]+)\\}", "<h1>$1</h1>");
        html = html.replaceAll("\\\\subsection\\{([^}]+)\\}", "<h2>$1</h2>");
        html = html.replaceAll("\\\\subsubsection\\{([^}]+)\\}", "<h3>$1</h3>");

        // Convert line breaks
        html = html.replaceAll("\\\\\\\\", "<br/>");
        html = html.replaceAll("\n\n", "</p><p>");

        // Wrap in paragraph tags if not already wrapped
        if (!html.trim().startsWith("<")) {
            html = "<p>" + html + "</p>";
        }

        return html;
    }

    private String convertMathExpressions(String text) {
        // Convert inline math $...$ to KaTeX
        text = LATEX_INLINE_MATH.matcher(text).replaceAll("\\\\($1\\\\)");

        // Convert display math $$...$$ to KaTeX
        text = LATEX_DISPLAY_MATH.matcher(text).replaceAll("\\\\[$1\\\\]");

        return text;
    }
}
