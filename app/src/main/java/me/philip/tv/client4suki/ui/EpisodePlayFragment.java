package me.philip.tv.client4suki.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.PlaybackFragment;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.BaseOnItemViewSelectedListener;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.PlaybackRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.URI;

import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.Utils;
import me.philip.tv.client4suki.model.EpisodeDetail;
import me.philip.tv.client4suki.presenter.PlaybackDescriptionPresenter;

/**
 * Created by phili on 6/19/2017.
 */

public class EpisodePlayFragment extends PlaybackFragment {

    private static final String LOG_TAG = "EpisodePlayFragment";
    private static final String AUTO_PLAY = "auto_play";
    private static final Bundle mAutoPlayExtras = new Bundle();

    private MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private SimpleExoPlayer mPlayer;
    private MediaController mMediaController;
    private PlaybackControlHelper mGlue;
    private MediaController.Callback mMediaControllerCallback;
    private AudioManager mAudioManager;
    private boolean mHasAudioFocus;
    private boolean mPauseTransient;
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_LOSS:
                            abandonAudioFocus();
                            pause();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            if (mGlue.isMediaPlaying()) {
                                pause();
                                mPauseTransient = true;
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                            mPlayer.mute(true);
//                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            if (mPauseTransient) {
                                play();
                            }
//                            mPlayer.mute(false);
//                            break;
                    }
                }
            };

    private EpisodeDetail mSelectedEpisode;
    private ArrayObjectAdapter mRowsAdapter;


    public EpisodePlayFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        createMediaSession();
    }

    @Override
    public void resetFocus() {
        super.resetFocus();
    }

    @Override
    public ObjectAdapter getAdapter() {
        return super.getAdapter();
    }

    @Override
    public void setAdapter(ObjectAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public boolean isFadingEnabled() {
        return super.isFadingEnabled();
    }

    @Override
    public void setFadingEnabled(boolean enabled) {
        super.setFadingEnabled(enabled);
    }

    @Override
    public OnFadeCompleteListener getFadeCompleteListener() {
        return super.getFadeCompleteListener();
    }

    @Override
    public void setFadeCompleteListener(OnFadeCompleteListener listener) {
        super.setFadeCompleteListener(listener);
    }

    @Override
    public void tickle() {
        super.tickle();
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        EpisodeDetail video = (EpisodeDetail) getActivity()
                .getIntent().getSerializableExtra(DetailsActivity.EPISODE);
        if (!updateSelectedVideo(video)) {
            return;
        }

        startPlaying();
    }

//    @Override
//    public void setSelectedPosition(int position) {
//        super.setSelectedPosition(position);
//    }

//    @Override
//    public void setSelectedPosition(int position, boolean smooth) {
//        super.setSelectedPosition(position, smooth);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mSelectedEpisode = (EpisodeDetail) getActivity()
                .getIntent().getSerializableExtra(DetailsActivity.EPISODE);
        mGlue = new PlaybackControlHelper(getActivity(), this, mSelectedEpisode);
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnPlaybackItemViewClickedListener(new PlaybackItemViewClickedListener());
    }

    @Override
    public int getBackgroundType() {
        return super.getBackgroundType();
    }

    @Override
    public void setBackgroundType(int type) {
        super.setBackgroundType(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setHostCallback(PlaybackGlueHost.HostCallback hostCallback) {
        super.setHostCallback(hostCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGlue.onCreateControlsRowAndPresenter();
        PlaybackControlsRow controlsRow = mGlue.getControlsRow();
        PlaybackControlsRowPresenter controlsRowPresenter = mGlue.getControlsRowPresenter();
//        controlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
//            @Override
//            public void onActionClicked(Action action) {
//                mGlue.onActionClicked(action);
//            }
//        });

        mMediaControllerCallback = mGlue.createMediaControllerCallback();
        mMediaController = getActivity().getMediaController();
        mMediaController.registerCallback(mMediaControllerCallback);

        ClassPresenterSelector ps = new ClassPresenterSelector();
        ps.addClassPresenter(PlaybackControlsRow.class, controlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);



        mRowsAdapter.add(controlsRow);

        addOtherRows();
        updatePlaybackRow();
        setAdapter(mRowsAdapter);

        Resources res = getResources();
        int cardWidth = res.getDimensionPixelSize(R.dimen.playback_overlay_width);
        int cardHeight = res.getDimensionPixelSize(R.dimen.playback_overlay_height);

        Glide.with(this)
                .load(Uri.parse(mSelectedEpisode.getBangumi().getCover()))
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(cardWidth, cardHeight) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mGlue.getControlsRow().setImageDrawable(resource);
                        updatePlaybackRow();
                    }
                });

        startPlaying();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaSession.release();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGlue.isMediaPlaying()) {
            boolean isVisibleBehind = getActivity().requestVisibleBehind(true);
            boolean isInPictureInPictureMode =
                    PlaybackActivity.supportsPictureInPicture(getActivity())
                            && getActivity().isInPictureInPictureMode();
            if (!isVisibleBehind && !isInPictureInPictureMode) {
                pause();
            }
        } else {
            getActivity().requestVisibleBehind(false);
        }
    }

    @Override
    public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener listener) {
        super.setOnItemViewSelectedListener(listener);
    }

    @Override
    public void setOnItemViewClickedListener(BaseOnItemViewClickedListener listener) {
        super.setOnItemViewClickedListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaControllerCallback);
        }
        mMediaSession.release();
        releasePlayer();
    }

    @Override
    public void setPlaybackRow(Row row) {
        super.setPlaybackRow(row);
    }

    @Override
    public void setPlaybackRowPresenter(PlaybackRowPresenter presenter) {
        super.setPlaybackRowPresenter(presenter);
    }

    @Override
    public void notifyPlaybackRowChanged() {
        super.notifyPlaybackRowChanged();
    }

    public void createMediaSession(){
        if (mMediaSession == null) {
            // Create a MediaSessionCompat
            mMediaSession = new MediaSession(getActivity(), LOG_TAG);

            // Enable callbacks from MediaButtons and TransportControls
            mMediaSession.setFlags(
                    MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setActive(true);

                ((FragmentActivity) getActivity()).setMediaController(
                        new MediaController(getActivity(), mMediaSession.getSessionToken()));
                setPlaybackState(PlaybackState.STATE_NONE);


            // MySessionCallback has methods that handle callbacks from a media controller
            mMediaSession.setCallback(new MediaSessionCallback());

            // Create a MediaControllerCompat
            MediaController mediaController =
                    new MediaController(getActivity(), mMediaSession.getSessionToken());

//            MediaController.setMediaController(getActivity(), mediaController);
        }
    }

    private void addOtherRows() {
        //TODO
//        mVideoCursorAdapter = new CursorObjectAdapter(new CardPresenter());
//        mVideoCursorAdapter.setMapper(new VideoCursorMapper());
//
//        Bundle args = new Bundle();
//        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, mSelectedVideo.category);
//        getLoaderManager().initLoader(RECOMMENDED_VIDEOS_LOADER, args, this);
//
//        HeaderItem header = new HeaderItem(getString(R.string.related_movies));
//        mRowsAdapter.add(new ListRow(header, mVideoCursorAdapter));
    }

    void updatePlaybackRow() {
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
    }

    public void startPlaying() {
        // Prepare the player and start playing the selected video
        mAutoPlayExtras.putBoolean(AUTO_PLAY, true);
        playVideo(mSelectedEpisode, mAutoPlayExtras);

        // Start loading videos for the queue
//        Bundle args = new Bundle();
//        args.putString(VideoContract.VideoEntry.COLUMN_CATEGORY, mSelectedVideo.category);
//        getLoaderManager().initLoader(QUEUE_VIDEOS_LOADER, args, mCallbacks);
    }

    private void playVideo(EpisodeDetail video, Bundle extras) {
//        updateSelectedVideo(video);
        preparePlayer();
        setPlaybackState(PlaybackState.STATE_PAUSED);
        if (extras.getBoolean(AUTO_PLAY)) {
            play();
        } else {
            pause();
        }
    }

    private void play() {
        // Request audio focus whenever we resume playback
        // because the app might have abandoned audio focus due to the AUDIOFOCUS_LOSS.
        requestAudioFocus();

        if (mPlayer == null) {
            setPlaybackState(PlaybackState.STATE_NONE);
            return;
        }
        if (!mGlue.isMediaPlaying()) {

            mPlayer.setPlayWhenReady(true);
            setPlaybackState(PlaybackState.STATE_PLAYING);
        }

        mGlue.updateProgress();
    }

    public void pause() {
        mPauseTransient = false;

        if (mPlayer == null) {
            setPlaybackState(PlaybackState.STATE_NONE);
            return;
        }
        if (mGlue.isMediaPlaying()) {
            mPlayer.setPlayWhenReady(false);
            setPlaybackState(PlaybackState.STATE_PAUSED);
        }
    }

    private void requestAudioFocus() {
        if (mHasAudioFocus) {
            return;
        }
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mHasAudioFocus = true;
        } else {
            pause();
        }
    }

    private void abandonAudioFocus() {
        mHasAudioFocus = false;
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    private boolean updateSelectedVideo(EpisodeDetail video) {
        Intent intent = new Intent(getActivity().getIntent());
        intent.putExtra(DetailsActivity.EPISODE, video);
        if (mSelectedEpisode != null && mSelectedEpisode.equals(video)) {
            return false;
        }
        mSelectedEpisode = video;

        PendingIntent pi = PendingIntent.getActivity(
                getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mMediaSession.setSessionActivity(pi);

        return true;
    }

    private void preparePlayer() {

        if(mPlayer == null) {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);


            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            SurfaceView mSurfaceView = (SurfaceView) getActivity().findViewById(R.id.Play_videoView);
            mPlayer.setVideoSurfaceView(mSurfaceView);
            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(), "Suki"), defaultBandwidthMeter);
            // Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            // This is the MediaSource representing the media to be played.

            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mSelectedEpisode.getVideo_files()[0].getUrl()),
                    dataSourceFactory, extractorsFactory, null, null);
            // Prepare the player with the source.
            mPlayer.prepare(videoSource);
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public long getBufferedPosition() {
        if (mPlayer != null) {
            return mPlayer.getBufferedPosition();
        }
        return 0L;
    }

    public long getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0L;
    }

//    public long getDuration() {
//        if (mPlayer != null) {
//            return mPlayer.getDuration();
//        }
//        return C.TIME_UNSET;
//    }

    private void updateMetadata(final EpisodeDetail video) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, video.getId());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, video.getBangumi().getName_cn());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, video.getEpisode_no()+". "+video.getName_cn());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION,
                video.getName());

        long duration = Utils.getDuration(video.getVideo_files()[0].getUrl());
        metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, duration);

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, video.getBangumi().getName_cn());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, video.getName());

        Resources res = getResources();
        int cardWidth = res.getDimensionPixelSize(R.dimen.playback_overlay_width);
        int cardHeight = res.getDimensionPixelSize(R.dimen.playback_overlay_height);

        Glide.with(this)
                .load(Uri.parse(video.getBangumi().getCover()))
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(cardWidth, cardHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mMediaSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    private void setPlaybackState(int state) {
        long currPosition = getCurrentPosition();

        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions(state));
        stateBuilder.setState(state, currPosition, 1.0f);
        mMediaSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions(int nextState) {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH |
                PlaybackState.ACTION_SKIP_TO_NEXT |
                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                PlaybackState.ACTION_FAST_FORWARD |
                PlaybackState.ACTION_REWIND |
                PlaybackState.ACTION_PAUSE;

        if (nextState == PlaybackState.STATE_PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private class MediaSessionCallback extends MediaSession.Callback {

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

//            if (item instanceof Video) {
//                Video video = (Video) item;
//                Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
//                intent.putExtra(VideoDetailsActivity.VIDEO, video);
//
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
//                        VideoDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
//                getActivity().startActivity(intent, bundle);
//            }
        }
    }

    private final class PlaybackItemViewClickedListener implements BaseOnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
            if (item instanceof Action){
                EpisodePlayFragment.this.mGlue.onActionClicked((Action)item);
            }
        }
    }
}
