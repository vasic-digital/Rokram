/*#######################################################
 *
 *   Maintained 2017-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.activity.openeditor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import digital.vasic.yole.activity.YoleBaseActivity;
import digital.vasic.yole.activity.StoragePermissionActivity;
import digital.vasic.yole.model.Document;
import digital.vasic.opoc.util.GsIntentUtils;

import java.io.File;

public class OpenEditorActivity extends YoleBaseActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoragePermissionActivity.requestPermissions(this);
    }

    protected void openEditorForFile(final File file, final Integer line) {
        final Intent openIntent = new Intent(getApplicationContext(), OpenFromShortcutOrWidgetActivity.class)
                .setAction(Intent.ACTION_EDIT)
                .putExtra(Document.EXTRA_FILE, file);

        if (line != null) {
            openIntent.putExtra(Document.EXTRA_FILE_LINE_NUMBER, line);
        }

        GsIntentUtils.animateToActivity(this, openIntent, true, 1);
    }
}
