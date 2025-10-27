/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *########################################################*/
package digital.vasic.yole.format.latex

import android.content.Context
import digital.vasic.opoc.util.GsFileUtils
import digital.vasic.yole.format.TextConverterBase
import java.io.File
import java.util.regex.Pattern

class LatexTextConverter : TextConverterBase() {

    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        if (markup.isEmpty()) return ""

        var html = markup

        // Convert LaTeX math expressions to KaTeX format
        html = convertMathExpressions(html)

        // Basic LaTeX to HTML conversion
        html = html.replace(Regex("\\\\textbf\\{([^}]+)\\}"), "<strong>$1</strong>")
        html = html.replace(Regex("\\\\textit\\{([^}]+)\\}"), "<em>$1</em>")
        html = html.replace(Regex("\\\\texttt\\{([^}]+)\\}"), "<code>$1</code>")
        html = html.replace(Regex("\\\\section\\{([^}]+)\\}"), "<h1>$1</h1>")
        html = html.replace(Regex("\\\\subsection\\{([^}]+)\\}"), "<h2>$1</h2>")
        html = html.replace(Regex("\\\\subsubsection\\{([^}]+)\\}"), "<h3>$1</h3>")

        // Convert line breaks
        html = html.replace(Regex("\\\\\\\\"), "<br/>")
        html = html.replace("\n\n", "</p><p>")

        // Wrap in paragraph tags if not already wrapped
        if (!html.trim().startsWith("<")) {
            html = "<p>$html</p>"
        }

        // Add KaTeX support
        val head = """
            <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
            <script src="file:///android_asset/katex/katex.min.js"></script>
            <script src="file:///android_asset/katex/auto-render.min.js"></script>
        """.trimIndent()

        val onLoadJs = """
            renderMathInElement(document.body, {
                delimiters: [
                    {left: '$$', right: '$$', display: true},
                    {left: '$', right: '$', display: false},
                    {left: '\\[', right: '\\]', display: true},
                    {left: '\\(', right: '\\)', display: false}
                ]
            });
        """.trimIndent()

        return putContentIntoTemplate(context, html, lightMode, file, onLoadJs, head)
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    private fun convertMathExpressions(text: String): String {
        var result = text

        // Convert display math $$...$$ to KaTeX format (must be done first to avoid conflicts with inline math)
        result = LATEX_DISPLAY_MATH.matcher(result).replaceAll("\\\\[$1\\\\]")

        // Convert inline math $...$ to KaTeX format
        result = LATEX_INLINE_MATH.matcher(result).replaceAll("\\\\($1\\\\)")

        return result
    }

    companion object {
        private val EXT = setOf(".tex", ".latex")

        private val LATEX_INLINE_MATH: Pattern = Pattern.compile("\\$([^$]+)\\$")
        private val LATEX_DISPLAY_MATH: Pattern = Pattern.compile("\\$\\$([^$]+)\\$\\$")
        private val LATEX_BEGIN_END: Pattern = Pattern.compile("\\\\begin\\{([^}]+)\\}(.*?)\\\\end\\{\\1\\}", Pattern.DOTALL)
    }
}
