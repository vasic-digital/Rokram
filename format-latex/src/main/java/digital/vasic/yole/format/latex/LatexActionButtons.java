package digital.vasic.yole.format.latex;

import digital.vasic.yole.format.ActionButtonBase;
import digital.vasic.yole.model.Document;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import java.util.Arrays;
import java.util.List;

public class LatexActionButtons extends ActionButtonBase {
    private static final List<String> LATEX_COMMANDS = Arrays.asList(
        "\\textbf{}", "\\textit{}", "\\texttt{}",
        "\\section{}", "\\subsection{}", "\\subsubsection{}",
        "$ $", "$$ $$",
        "\\begin{itemize}\\n\\item \\n\\end{itemize}",
        "\\begin{enumerate}\\n\\item \\n\\end{enumerate}"
    );

    public LatexActionButtons(Context context, Document document) {
        super(context, document);
    }

    @Override
    protected List<String> getFormatActions() {
        return LATEX_COMMANDS;
    }

    @Override
    protected String getFormatPrefix() {
        return "LaTeX: ";
    }
}
