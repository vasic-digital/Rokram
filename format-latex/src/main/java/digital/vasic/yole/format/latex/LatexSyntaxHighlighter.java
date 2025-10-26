package digital.vasic.yole.format.latex;

import digital.vasic.yole.frontend.textview.SyntaxHighlighterBase;
import digital.vasic.yole.model.AppSettings;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LatexSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Pattern LATEX_COMMANDS = Pattern.compile("\\\\[a-zA-Z]+(\\{[^}]*\\})*");
    private static final Pattern LATEX_INLINE_MATH = Pattern.compile("\\$[^$]+\\$");
    private static final Pattern LATEX_DISPLAY_MATH = Pattern.compile("\\$\\$.*?\\$\\$");
    private static final Pattern LATEX_COMMENTS = Pattern.compile("%.*$");

    public LatexSyntaxHighlighter(AppSettings appSettings) {
        super(appSettings);
    }

    @Override
    public void applySyntaxHighlighting(Spannable spannable, int color) {
        if (spannable == null) return;

        String text = spannable.toString();

        // Highlight LaTeX commands
        Matcher commandMatcher = LATEX_COMMANDS.matcher(text);
        while (commandMatcher.find()) {
            spannable.setSpan(
                new ForegroundColorSpan(getHighlightColor()),
                commandMatcher.start(),
                commandMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // Highlight inline math
        Matcher inlineMathMatcher = LATEX_INLINE_MATH.matcher(text);
        while (inlineMathMatcher.find()) {
            spannable.setSpan(
                new ForegroundColorSpan(getMathColor()),
                inlineMathMatcher.start(),
                inlineMathMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // Highlight display math
        Matcher displayMathMatcher = LATEX_DISPLAY_MATH.matcher(text);
        while (displayMathMatcher.find()) {
            spannable.setSpan(
                new ForegroundColorSpan(getMathColor()),
                displayMathMatcher.start(),
                displayMathMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // Highlight comments
        Matcher commentMatcher = LATEX_COMMENTS.matcher(text);
        while (commentMatcher.find()) {
            spannable.setSpan(
                new ForegroundColorSpan(getCommentColor()),
                commentMatcher.start(),
                commentMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private int getHighlightColor() {
        return 0xFF0066CC; // Blue for commands
    }

    private int getMathColor() {
        return 0xFF009900; // Green for math
    }

    private int getCommentColor() {
        return 0xFF666666; // Gray for comments
    }
}
