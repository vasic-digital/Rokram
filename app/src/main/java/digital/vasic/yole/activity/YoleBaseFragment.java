package digital.vasic.yole.activity;

import android.content.Context;

import digital.vasic.yole.model.AppSettings;
import digital.vasic.yole.util.YoleContextUtils;
import digital.vasic.opoc.frontend.base.GsFragmentBase;

public abstract class YoleBaseFragment extends GsFragmentBase<AppSettings, YoleContextUtils> {
    @Override
    public AppSettings createAppSettingsInstance(Context context) {
        return AppSettings.get(context);
    }

    @Override
    public YoleContextUtils createContextUtilsInstance(Context context) {
        return new YoleContextUtils(context);
    }
}
