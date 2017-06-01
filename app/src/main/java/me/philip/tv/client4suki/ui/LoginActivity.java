package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.*;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.model.Account;

public class LoginActivity extends Activity {

    public static final String COMPLETED_LOGIN = "completed_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            GuidedStepFragment.addAsRoot(this, new FirstStepFragment(), android.R.id.content);
        }
    }

    public static boolean Login(Account account){
        boolean isLoginSucessful = false;

        isLoginSucessful = true;

        return isLoginSucessful;
    }

    public static class FirstStepFragment extends GuidedStepFragment {

        private Account account;

        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String title = getString(R.string.guidedstep_title);
            String breadcrumb = getString(R.string.guidedstep_breadcrumb);
            String description = getString(R.string.guidedstep_first_description);
            Drawable icon = getActivity().getDrawable(R.drawable.ic_dns);
            return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
        }

        @Override
        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(new GuidedAction.Builder(getActivity())
                    .id(GuidedAction.ACTION_ID_CONTINUE)
                    .title(getString(R.string.guidedstep_hostname))
                    .hasNext(true)
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_URI)
                    .build());
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            FragmentManager fm = getFragmentManager();
            if (action.getId() == GuidedAction.ACTION_ID_CONTINUE) {
                account = new Account();
                account.setHost(action.getDescription().toString());
                Bundle bundle=new Bundle();
                Gson gson = new Gson();
                String json = gson.toJson(account);
                bundle.putString("account", json);
                SecondStepFragment mSecondStepFragment = new SecondStepFragment();
                mSecondStepFragment.setArguments(bundle);
                GuidedStepFragment.add(fm, mSecondStepFragment);
            }
        }
    }

    public static class SecondStepFragment extends GuidedStepFragment {

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
            if (action.getId() == GuidedAction.ACTION_ID_CONTINUE) {
                Gson gson = new Gson();
                account = gson.fromJson(getArguments().getString("account"), Account.class);
                account.setUsr(action.getDescription().toString());
                Bundle bundle=new Bundle();
                String json = gson.toJson(account);
                bundle.putString("account", json);
                ThirdStepFragment mThirdStepFragment = new ThirdStepFragment();
                mThirdStepFragment.setArguments(bundle);
                GuidedStepFragment.add(fm, mThirdStepFragment);
            }
        }
    }

    public static class ThirdStepFragment extends GuidedStepFragment {

        private Account account;

        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String title = getString(R.string.guidedstep_title);
            String breadcrumb = getString(R.string.guidedstep_breadcrumb);
            String description = getString(R.string.guidedstep_third_description);
            Drawable icon = getActivity().getDrawable(R.drawable.ic_key_white);
            return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
        }

        @Override
        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(new GuidedAction.Builder(getActivity())
                    .id(GuidedAction.ACTION_ID_CONTINUE)
                    .title(getString(R.string.guidedstep_psw))
                    .hasNext(true)
                    .descriptionEditable(true)
                    .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .build());
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            FragmentManager fm = getFragmentManager();
            if (action.getId() == GuidedAction.ACTION_ID_CONTINUE) {
                Gson gson = new Gson();
                account = gson.fromJson(getArguments().getString("account"), Account.class);
                account.setPsw(action.getDescription().toString());

                if(Login(account)){
                    Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor sharedPreferencesEditor =
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    sharedPreferencesEditor.putBoolean(
                            COMPLETED_LOGIN, true);
                    sharedPreferencesEditor.apply();
                    getActivity().finishAfterTransition();
                } else {
                    getFragmentManager().beginTransaction().show(new ErrorFragment());
                }
            }
        }
    }
}
