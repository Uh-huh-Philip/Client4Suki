package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        OnboardingFragment mOnboardingFragment = new OnboardingFragment();
        getFragmentManager().beginTransaction().add(frame.getId(), mOnboardingFragment);
    }
}
