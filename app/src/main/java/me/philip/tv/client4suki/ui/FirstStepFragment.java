package me.philip.tv.client4suki.ui;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;

import java.util.List;

import me.philip.tv.client4suki.R;
import okhttp3.HttpUrl;

/**
 * Created by philipjiang on 13/03/2018.
 */


public class FirstStepFragment extends GuidedStepFragment {

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getString(R.string.guidedstep_title);
        String breadcrumb = getString(R.string.guidedstep_breadcrumb);
        String description = getString(R.string.guidedstep_first_description);
        Drawable icon = getActivity().getDrawable(R.drawable.ic_dns);
        return new GuidanceStylist.Guidance(title, description, breadcrumb, null);
    }

    @Override
    public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(new GuidedAction.Builder(getActivity())
                .id(GuidedAction.ACTION_ID_CONTINUE)
                .title(getString(R.string.guidedstep_hostname))
                .descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_TEXT_VARIATION_URI)
                .build());

    }



    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        FragmentManager fm = getFragmentManager();
        if (action.getId() == GuidedAction.ACTION_ID_CONTINUE && HttpUrl.parse(action.getDescription().toString()) != null) {
            Bundle bundle=new Bundle();
            bundle.putString("host", action.getDescription().toString());
            SecondStepFragment mSecondStepFragment = new SecondStepFragment();
            mSecondStepFragment.setArguments(bundle);
            GuidedStepFragment.add(fm, mSecondStepFragment);
        }
    }
}
