package me.philip.tv.client4suki.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by phili on 6/1/2017.
 */

public class OnboardingFragment extends android.support.v17.leanback.app.OnboardingFragment {

    public static final String COMPLETED_ONBOARDING = "completed_onboarding";

    @Override
    protected int getPageCount() {
        return 0;
    }

    @Override
    protected CharSequence getPageTitle(int pageIndex) {
        return null;
    }

    @Override
    protected CharSequence getPageDescription(int pageIndex) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        // User has seen OnboardingFragment, so mark our SharedPreferences
        // flag as completed so that we don't show our OnboardingFragment
        // the next time the user launches the app.
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING, true);
        sharedPreferencesEditor.apply();
        // Let's go back to the MainActivity
        getActivity().finish();
    }
}
