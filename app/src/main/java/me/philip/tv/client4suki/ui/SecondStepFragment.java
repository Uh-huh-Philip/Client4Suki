package me.philip.tv.client4suki.ui;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.google.gson.Gson;

import java.util.List;

import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.model.Account;

/**
 * Created by philipjiang on 13/03/2018.
 */


public class SecondStepFragment extends GuidedStepFragment {

    private Account account;

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getString(R.string.guidedstep_title);
        String breadcrumb = getString(R.string.guidedstep_breadcrumb);
        String description = getString(R.string.guidedstep_second_description);
        Drawable icon = getActivity().getDrawable(R.drawable.ic_account_circle);
        return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
    }

    @Override
    public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(new GuidedAction.Builder(getActivity())
                .id(GuidedAction.ACTION_ID_CONTINUE)
                .title(getString(R.string.guidedstep_usr))
                .hasNext(true)
                .descriptionEditable(true)
                .build());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        FragmentManager fm = getFragmentManager();
        if (action.getId() == GuidedAction.ACTION_ID_CONTINUE && action.getDescription().toString().length() > 0) {
            account = new Account();
            Gson gson = new Gson();
            account.setUsr(action.getDescription().toString());
            account.setRemember(true);
            Bundle bundle=new Bundle();
            String json = gson.toJson(account);
            bundle.putString("account", json);
            bundle.putString("host", getArguments().getString("host"));
            ThirdStepFragment mThirdStepFragment = new ThirdStepFragment();
            mThirdStepFragment.setArguments(bundle);
            GuidedStepFragment.add(fm, mThirdStepFragment);
        }
    }
}
