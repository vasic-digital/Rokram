package digital.vasic.yole.util;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentManager;

import digital.vasic.yole.R;
import digital.vasic.yole.format.ActionButtonBase;
import digital.vasic.yole.frontend.filebrowser.YoleFileBrowserFactory;
import digital.vasic.yole.model.AppSettings;
import digital.vasic.opoc.frontend.filebrowser.GsFileBrowserOptions;
import digital.vasic.opoc.util.GsBackupUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackupUtils {

    public static void showBackupSelectFromDialog(final Context context, final FragmentManager manager) {
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;

            YoleFileBrowserFactory.showFileDialog(
                    new GsFileBrowserOptions.SelectionListenerAdapter() {
                        @Override
                        public void onFsViewerConfig(GsFileBrowserOptions.Options dopt) {
                            dopt.rootFolder = AppSettings.get(context).getNotebookDirectory();
                            dopt.titleText = R.string.select;
                        }

                        @Override
                        public void onFsViewerSelected(String request, File file, final Integer lineNumber) {
                            GsBackupUtils.loadBackup(context, file);
                        }
                    }, manager, activity,
                    (c, file) -> file != null && file.exists() && file.toString().trim().toLowerCase().endsWith(".json")
            );
        }
    }

    public static void showBackupWriteToDialog(final Context context, final FragmentManager manager) {
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;

            YoleFileBrowserFactory.showFolderDialog(
                    new GsFileBrowserOptions.SelectionListenerAdapter() {
                        @Override
                        public void onFsViewerConfig(GsFileBrowserOptions.Options dopt) {
                            dopt.rootFolder = AppSettings.get(context).getNotebookDirectory();
                            dopt.titleText = R.string.select_folder;
                        }

                        @Override
                        public void onFsViewerSelected(String request, File dir, final Integer lineNumber) {
                            GsBackupUtils.makeBackup(context, getPrefNamesToBackup(), GsBackupUtils.generateBackupFilepath(context, dir));
                        }
                    }, manager, activity
            );
        }
    }

    public static List<String> getPrefNamesToBackup() {
        List<String> prefs = new ArrayList<>(GsBackupUtils.getPrefNamesToBackup());
        prefs.add(ActionButtonBase.ACTION_ORDER_PREF_NAME);
        return prefs;
    }
}
