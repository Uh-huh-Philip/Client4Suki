package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v17.leanback.widget.Action;
import android.support.v4.app.FragmentActivity;
import android.support.v4.os.BuildCompat;
import android.util.Log;
import android.view.KeyEvent;

import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.Utils;

public class PlaybackActivity extends FragmentActivity {

    private static final String TAG = "PlaybackActivity";
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        EpisodePlayFragment mEpisodePlayFragment = (EpisodePlayFragment) getFragmentManager().findFragmentById(R.id.playback_fragment);
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){

            Log.d(TAG, getCurrentFocus().toString());
        }
        return super.onKeyUp(keyCode, event);
    }
}
