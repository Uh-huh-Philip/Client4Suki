package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.*;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.reactivestreams.Subscriber;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.api.Client;
import me.philip.tv.client4suki.model.Account;
import okhttp3.HttpUrl;
import io.reactivex.functions.Consumer;

public class LoginActivity extends Activity {

    public static final String COMPLETED_LOGIN = "completed_login";
    public static final String HOST = "host_server";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            GuidedStepFragment.addAsRoot(this, new FirstStepFragment(), android.R.id.content);
        }
    }

    public boolean Login(Account account, final String host){
        boolean isLoginSucessful = false;

        Client client = new Client();
        client.initial(this, host);
        client.getEndpoint().login(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject value) {
                        Log.d("TAG", value.toString());
                        //Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor sharedPreferencesEditor =
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                        sharedPreferencesEditor.putBoolean(
                                COMPLETED_LOGIN, true);
                        sharedPreferencesEditor.putString(
                                HOST, host);
                        sharedPreferencesEditor.apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finishAfterTransition();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", e.getMessage());
                        LoginActivity.this.getFragmentManager().beginTransaction().show(new ErrorFragment());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        isLoginSucessful = true;

        return isLoginSucessful;
    }

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

    public class ThirdStepFragment extends GuidedStepFragment {

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
            if (action.getId() == GuidedAction.ACTION_ID_CONTINUE && action.getDescription().toString().length() > 0) {
                Gson gson = new Gson();
                account = gson.fromJson(getArguments().getString("account"), Account.class);
                account.setPsw(action.getDescription().toString());

                Login(account, getArguments().getString("host"));
//                if(Login(account, getArguments().getString("host"))){
//                    Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor sharedPreferencesEditor =
//                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
//                    sharedPreferencesEditor.putBoolean(
//                            COMPLETED_LOGIN, true);
//                    sharedPreferencesEditor.apply();
//                    getActivity().finishAfterTransition();
//                } else {
//                    getFragmentManager().beginTransaction().show(new ErrorFragment());
//                }
            }
        }
    }
}
