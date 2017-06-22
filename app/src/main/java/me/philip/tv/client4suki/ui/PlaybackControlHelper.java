package me.philip.tv.client4suki.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.PlaybackFragment;
import android.support.v17.leanback.media.PlaybackControlGlue;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;

import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.model.EpisodeDetail;
import me.philip.tv.client4suki.presenter.PlaybackDescriptionPresenter;

/**
 * Created by phili on 6/19/2017.
 */

public class PlaybackControlHelper extends PlaybackControlGlue {

    private static final String TAG = "PlaybackControlHelper";
//    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final int[] SEEK_SPEEDS = {2}; // A single seek speed for fast-forward / rewind.
    private static final int DEFAULT_UPDATE_PERIOD = 500;
    private static final int UPDATE_PERIOD = 16;
    Drawable mMediaArt;
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.PictureInPictureAction mPipAction;

    private EpisodePlayFragment mFragment;
    private MediaController mMediaController;
    private MediaController.TransportControls mTransportControls;
    private EpisodeDetail mEpisodeDetail;
    private Handler mHandler = new Handler();
    private Runnable mUpdateProgressRunnable;


    public PlaybackControlHelper(Context context, EpisodePlayFragment fragment, EpisodeDetail episodeDetail) {
        super(context, SEEK_SPEEDS);
        mFragment = fragment;
        mEpisodeDetail = episodeDetail;
        mMediaController = mFragment.getActivity().getMediaController();
        mTransportControls = mMediaController.getTransportControls();
    }

    @Override
    protected void onAttachedToHost(PlaybackGlueHost host) {
        super.onAttachedToHost(host);
    }

    @Override
    protected void onCreateControlsRowAndPresenter() {
        super.onCreateControlsRowAndPresenter();
//        PlaybackControlsRow controlsRow = new PlaybackControlsRow(mEpisodeDetail);
//        setControlsRow(controlsRow);
//        setControlsRowPresenter(new PlaybackControlsRowPresenter(new PlaybackDescriptionPresenter()));
//        ArrayObjectAdapter mSecondaryActionsAdapter = (ArrayObjectAdapter)getControlsRow().getSecondaryActionsAdapter();
//
//        mPipAction = new PlaybackControlsRow.PictureInPictureAction(mFragment.getActivity());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            mSecondaryActionsAdapter.add(mPipAction);
//            getControlsRow().setSecondaryActionsAdapter(mSecondaryActionsAdapter);
//        }
//        getControlsRowPresenter().setOnActionClickedListener(new OnActionClickedListener() {
//            @Override
//            public void onActionClicked(Action action) {
//                PlaybackControlHelper.this.onActionClicked(action);
//            }
//        });
    }

    protected void onCreatePrimaryActions(SparseArrayObjectAdapter primaryActionsAdapter) {
        //TODO
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            primaryActionsAdapter.set(ACTION_CUSTOM_RIGHT_FIRST, new PlaybackControlsRow.PictureInPictureAction(mFragment.getActivity()));
//        }
        super.onCreatePrimaryActions(primaryActionsAdapter);
    }

    @Override
    public void onActionClicked(Action action) {

        if (action instanceof PlaybackControlsRow.PlayPauseAction) {
            if (!isMediaPlaying()) {
                mFragment.startPlaying();
            } else if (isMediaPlaying()) {
                mFragment.pause();
            }
        } else if (action instanceof PlaybackControlsRow.FastForwardAction) {
            Toast.makeText((PlaybackActivity) getContext(), getContext().getResources().getString(R.string.to_be_done), Toast.LENGTH_SHORT).show();
        } else if (action instanceof PlaybackControlsRow.RewindAction) {
            Toast.makeText((PlaybackActivity) getContext(), getContext().getResources().getString(R.string.to_be_done), Toast.LENGTH_SHORT).show();
        } else if (action instanceof PlaybackControlsRow.SkipNextAction) {
            Toast.makeText((PlaybackActivity) getContext(), getContext().getResources().getString(R.string.to_be_done), Toast.LENGTH_SHORT).show();
        } else if (action instanceof PlaybackControlsRow.SkipPreviousAction) {
            Toast.makeText((PlaybackActivity) getContext(), getContext().getResources().getString(R.string.to_be_done), Toast.LENGTH_SHORT).show();
        } else if (action instanceof PlaybackControlsRow.PictureInPictureAction) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((Activity) getContext()).enterPictureInPictureMode();
                return;
            } else {
                Toast.makeText((PlaybackActivity) getContext(), "Pictrue in Pictrue is no Capable", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActionClicked(action);
        }
    }

    public MediaController.Callback createMediaControllerCallback() {
        return new MediaControllerCallback();
    }

    private class MediaControllerCallback extends MediaController.Callback {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
            // Update your UI to reflect the new state. Do not change media playback here.
            Log.d(TAG, "Playback state changed: " + state.getState());

            int nextState = state.getState();
            if (nextState != PlaybackState.STATE_NONE) {
                updateProgress();
                switch (nextState) {
                    case PlaybackState.STATE_PLAYING:
                        mFragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        play();
                        break;
                    case PlaybackState.STATE_PAUSED:
                        mFragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        pause();
                        break;
                    default:
                        break;
                }

            }
            onStateChanged();
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            PlaybackControlHelper.this.onMetadataChanged(); // Update metadata on controls.
//            mFragment.updatePlaybackRow();
        }
    }

    @Override
    public boolean hasValidMedia() {
        return mEpisodeDetail != null;
    }

    @Override
    public boolean isMediaPlaying() {
        if (mMediaController.getPlaybackState() == null) {
            return false;
        }
        int state = mMediaController.getPlaybackState().getState();
        return (state == PlaybackState.STATE_BUFFERING
                || state == PlaybackState.STATE_CONNECTING
                || state == PlaybackState.STATE_PLAYING
                || state == PlaybackState.STATE_FAST_FORWARDING
                || state == PlaybackState.STATE_REWINDING
                || state == PlaybackState.STATE_SKIPPING_TO_PREVIOUS
                || state == PlaybackState.STATE_SKIPPING_TO_NEXT);
    }

    @Override
    public CharSequence getMediaTitle() {
        return mEpisodeDetail.getEpisode_no() + ". " + mEpisodeDetail.getName_cn();
    }

    @Override
    public CharSequence getMediaSubtitle() {
        return mEpisodeDetail.getBangumi().getName_cn();
    }

    @Override
    public int getMediaDuration() {
        return mEpisodeDetail.getVideo_files()[0].getDuration();
    }

    @Override
    public Drawable getMediaArt() {
        return null;
    }

    @Override
    public long getSupportedActions() {
        return ACTION_PLAY_PAUSE | ACTION_FAST_FORWARD | ACTION_REWIND | ACTION_SKIP_TO_PREVIOUS |
                ACTION_SKIP_TO_NEXT;
    }

    @Override
    public int getCurrentSpeedId() {
        if (isMediaPlaying())
            return 1;
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        return (int) mFragment.getCurrentPosition();
    }

    @Override
    public void updateProgress() {
        if (mUpdateProgressRunnable == null) {
            mUpdateProgressRunnable = new Runnable() {
                @Override
                public void run() {
                    int totalTime = getControlsRow().getTotalTime();
                    int currentTime = getCurrentPosition();
                    getControlsRow().setCurrentTime(currentTime);

                    int progress = (int) mFragment.getBufferedPosition();
                    getControlsRow().setBufferedProgress(progress);

                    if (totalTime > 0 && totalTime <= currentTime) {
                        stopProgressAnimation();
                    } else {
                        updateProgress();
                    }
                }
            };
        }

        mHandler.postDelayed(mUpdateProgressRunnable, getUpdatePeriod());
    }

    private void stopProgressAnimation() {
        if (mHandler != null && mUpdateProgressRunnable != null) {
            mHandler.removeCallbacks(mUpdateProgressRunnable);
            mUpdateProgressRunnable = null;
        }
    }
}
