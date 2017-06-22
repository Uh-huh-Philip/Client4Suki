package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.os.BuildCompat;

import me.philip.tv.client4suki.R;

public class PlaybackActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
    }

    public static boolean supportsPictureInPicture(Context context) {
        return BuildCompat.isAtLeastN()
                && context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_PICTURE_IN_PICTURE);
    }

    @Override
    public void onVisibleBehindCanceled() {
        // App-specific method to stop playback and release resources
        getMediaController().getTransportControls().pause();
        super.onVisibleBehindCanceled();
    }
}
