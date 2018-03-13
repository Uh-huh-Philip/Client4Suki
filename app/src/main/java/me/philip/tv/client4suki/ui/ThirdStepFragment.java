package me.philip.tv.client4suki.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.api.Client;
import me.philip.tv.client4suki.model.Account;

/**
 * Created by philipjiang on 13/03/2018.
 */


public class ThirdStepFragment extends GuidedStepFragment {
    public static final String COMPLETED_LOGIN = "completed_login";
    public static final String HOST = "host_server";

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


    public boolean Login(Account account, final String host){
        boolean isLoginSucessful = false;

        Client client = new Client();
        client.initial(getActivity(), host);
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
                                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        sharedPreferencesEditor.putBoolean(
                                COMPLETED_LOGIN, true);
                        sharedPreferencesEditor.putString(
                                HOST, host);
                        sharedPreferencesEditor.apply();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finishAfterTransition();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG", e.getMessage());
                        getActivity().getFragmentManager().beginTransaction().show(new ErrorFragment());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        isLoginSucessful = true;

        return isLoginSucessful;
    }

}
