package me.philip.tv.client4suki.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.ProgressBarManager;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.api.Client;
import me.philip.tv.client4suki.model.Bangumi;
import me.philip.tv.client4suki.presenter.CardPresenter;
import okhttp3.HttpUrl;

/**
 * Created by phili on 6/22/2017.
 */

public class BangumiListFragment extends RowsFragment {

    private static final String TAG = "BangumiListFragment";
    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private String username;

    private List<Bangumi> bangumiList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

//        prepareBackgroundManager();

//        setupUIElements();

//        getUserInfo();

        setupEventListeners();

    }

    @Override
    public void onStart(){
        super.onStart();
        getBangumi(6);
        getBangumi(2);
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

//    private void setupUIElements() {
//        // setBadgeDrawable(getActivity().getResources().getDrawable(
//        // R.drawable.videos_by_google_banner));
//        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
//        // over title
//        setHeadersState(HEADERS_ENABLED);
//        setHeadersTransitionOnBackEnabled(true);
//
//        // set fastLane (or headers) background color
//        setBrandColor(getResources().getColor(R.color.fastlane_background));
//        // set search icon color
//        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
//    }

    private void getBangumi(final int type) {
        Client client = new Client();
        client.initial(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.HOST, null));

        if (bangumiList != null) {
            bangumiList = null;
        }

        client.getEndpoint().onAir(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        JsonArray data = jsonObject.getAsJsonArray("data");
                        bangumiList = new Gson().fromJson(data, new TypeToken<List<Bangumi>>() {
                        }.getType());
                        loadRows(type, bangumiList);
                        Log.d("TAG", "Bangumi Onair Loaded");
                    }
                });
    }

    private void loadRows(int type, List<Bangumi> bangumis) {
        if (mRowsAdapter == null) {
            mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        }
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

        int i;
        for (i = 0; i < bangumis.size(); i++) {
            listRowAdapter.add(bangumis.get(i));
        }

        HeaderItem header;

        if (mRowsAdapter.size() == 0) {
            switch (type) {
                case 2:
                    header = new HeaderItem(0, getResources().getString(R.string.Bangumi));
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    break;
                case 6:
                    header = new HeaderItem(1, getResources().getString(R.string.TV_serious));
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    break;
            }
        } else if (mRowsAdapter.size() == 1) {
            switch (type) {
                case 2:
                    header = new HeaderItem(0, getResources().getString(R.string.Bangumi));
                    mRowsAdapter.add(0, new ListRow(header, listRowAdapter));
                    break;
                case 6:
                    header = new HeaderItem(1, getResources().getString(R.string.TV_serious));
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                    break;
            }
            setAdapter(mRowsAdapter);
        }
    }

    private void setupEventListeners() {

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Bangumi) {
                Bangumi bangumi = (Bangumi) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.BANGUMI, bangumi);
                startActivity(intent);
            } else if (item instanceof String) {
                if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Bangumi) {
                try {
                    mBackgroundURI = new URI((((Bangumi) item).getCover()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                startBackgroundTimer();
            }

        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (HttpUrl.parse(mBackgroundURI.toString()) != null) {
                        if(((HomeFragment)getParentFragment()).updateBackground(mBackgroundURI.toString()))
                            mBackgroundTimer.cancel();
                    }
                }
            });

        }
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }


    protected void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }
}
