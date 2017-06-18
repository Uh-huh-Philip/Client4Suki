package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean(OnboardingFragment.COMPLETED_ONBOARDING, true)) {
            // This is the first time running the app, let's go to onboarding
            startActivity(new Intent(this, OnboardingActivity.class));
        } else  if(!sharedPreferences.getBoolean(LoginActivity.COMPLETED_LOGIN, false) || sharedPreferences.getString(LoginActivity.HOST, null) == null) {
            // This is the first time running the app, let's go to onboarding
            startActivity(new Intent(this, LoginActivity.class));
        } else startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}
