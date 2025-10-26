package digital.vasic.yole.format.jupyter;

import digital.vasic.yole.format.TextConverterBase;
import android.content.Context;
import java.io.File;

public class JupyterTextConverter extends TextConverterBase {
    @Override
    public boolean isFileOutOfThisFormat(File file) {
        // TODO: Implement file detection logic
        return false;
    }

    @Override
    public String convertMarkupToHtml(String markup, Context context, boolean lightMode, int lineNum, File file) {
        // TODO: Implement markup to HTML conversion
        return markup;
    }
}
