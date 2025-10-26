/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package digital.vasic.yole.activity;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import digital.vasic.yole.R;
import digital.vasic.yole.model.AppSettings;
import digital.vasic.yole.util.YoleContextUtils;
import digital.vasic.opoc.frontend.base.GsFragmentBase;
import digital.vasic.opoc.model.GsSharedPreferencesPropertyBackend;
import digital.vasic.opoc.util.GsContextUtils;

public class MoreFragment extends GsFragmentBase<GsSharedPreferencesPropertyBackend, GsContextUtils> {
    public static final String FRAGMENT_TAG = "MoreFragment";

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    public MoreFragment() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.more__fragment;
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MoreInfoFragment moreInfoFragment;
        if (_savedInstanceState == null) {
            FragmentTransaction t = getChildFragmentManager().beginTransaction();
            moreInfoFragment = MoreInfoFragment.newInstance();
            t.replace(R.id.more__fragment__placeholder_fragment, moreInfoFragment, MoreInfoFragment.TAG).commit();
        } else {
            moreInfoFragment = (MoreInfoFragment) getChildFragmentManager().findFragmentByTag(MoreInfoFragment.TAG);
        }
    }

    @Override
    public AppSettings createAppSettingsInstance(Context context) {
        return AppSettings.get(context);
    }

    @Override
    public YoleContextUtils createContextUtilsInstance(Context context) {
        return new YoleContextUtils(context);
    }
}