/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package me.philip.tv.client4suki.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.philip.tv.client4suki.api.Client;
import me.philip.tv.client4suki.model.Bangumi;
import me.philip.tv.client4suki.model.BangumiDetail;
import me.philip.tv.client4suki.model.Episode;
import me.philip.tv.client4suki.model.EpisodeDetail;
import me.philip.tv.client4suki.presenter.CardPresenter;
import me.philip.tv.client4suki.presenter.DetailsDescriptionPresenter;
import me.philip.tv.client4suki.model.Movie;
import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.Utils;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment {
    private static final String TAG = "VideoDetailsFragment";
    private static final String EPISODES = "EpisodeList";

    private static final int ACTION_FAVORITE = 1;
//    private static final int ACTION_RENT = 2;
//    private static final int ACTION_BUY = 3;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;

    private Movie mSelectedMovie;
    private Bangumi mSelectedBangumi;
    private BangumiDetail mBangumidetail;
    private String bangumiID;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedBangumi = (Bangumi) getActivity().getIntent()
                .getSerializableExtra(DetailsActivity.BANGUMI);
        if (mSelectedBangumi != null) {
            setupAdapter();
            prepareBangumiDetail();
            setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        return super.onCreateView(inflater, container,savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    protected void updateBackground(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
    }

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedBangumi.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedBangumi);
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background));
        int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(getActivity())
                .load(mSelectedBangumi.getCover())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });


//        List<Episode> episodes = mBangumidetail.getEpisodes();
//        row.addAction(new Action(ACTION_WATCH_TRAILER, getResources().getString(
//                R.string.watch_trailer_1), getResources().getString(R.string.watch_trailer_2)));
//        row.addAction(new Action(ACTION_RENT, getResources().getString(R.string.rent_1),
//                getResources().getString(R.string.rent_2)));
//        row.addAction(new Action(ACTION_BUY, getResources().getString(R.string.buy_1),
//                getResources().getString(R.string.buy_2)));

        SparseArrayObjectAdapter actionAdapter = new SparseArrayObjectAdapter();

//        for(int i=0; i < episodes.size(); i++){
//            Episode mEpisode = episodes.get(i);
//            switch (mEpisode.getStatus()){
//                case 2: actionAdapter.set(i, new Action(i, new Integer(mEpisode.getEpisode_no()).toString(), mEpisode.getName_cn()));
//            }
//        }

        actionAdapter.set(ACTION_FAVORITE, new Action(ACTION_FAVORITE, getResources().getString(R.string.favorite)));
        row.setActionsAdapter(actionAdapter);

        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
//                if (action.getId() == ACTION_WATCH_TRAILER) {
//                    Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
//                    intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
//                }
//                Client client = new Client();
//                client.initial(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.HOST, null));
//
//                client.getEndpoint().getEpisode(mBangumidetail.getEpisodes().get((int) action.getId()).getId())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<JsonObject>() {
//                            @Override
//                            public void accept(JsonObject jsonObject) throws Exception {
////                                JsonObject data = jsonObject.getAsJsonObject("data");
//                                EpisodeDetail episodeDetail = new Gson().fromJson(jsonObject, EpisodeDetail.class);
//                                Log.d("TAG", "EpisodeDetail Loaded");
//                                Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
//                                intent.putExtra(DetailsActivity.BANGUMI, episodeDetail);
//                                startActivity(intent);
//                            }
//                        });
                if (action.getId() == ACTION_FAVORITE) {
                    Utils.showToast(getActivity(),R.string.to_be_done);
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void prepareBangumiDetail(){
        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        updateBackground(mSelectedBangumi.getCover());

        bangumiID = mSelectedBangumi.getId();
        Client client = new Client();
        client.initial(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.HOST, null));
        client.getEndpoint().getBangumiDetail(bangumiID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        JsonObject data = jsonObject.getAsJsonObject("data");
                        mBangumidetail = new Gson().fromJson(data, BangumiDetail.class);
//                        setupDetailsOverviewRow();
//                        setupDetailsOverviewRowPresenter();
                        if(getActivity() != null) {
                            setupMovieListRow();
                            setupMovieListRowPresenter();
                        }
//                        updateBackground(mSelectedBangumi.getCover());
                        Log.d("TAG", "BangumiDetail Loaded");
                    }
                });
    }

    private void setupMovieListRow() {
        String subcategories[] = {getString(R.string.episodes)};
//        List<Movie> list = MovieList.list;
//
//        Collections.shuffle(list);
//        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
//        for (int j = 0; j < NUM_COLS; j++) {
//            listRowAdapter.add(list.get(j % 5));
//        }
//
//        HeaderItem header = new HeaderItem(0, subcategories[0]);
//        mAdapter.add(new ListRow(header, listRowAdapter));
        List<Episode> episodes = mBangumidetail.getEpisodes();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for(int i=0; i < episodes.size(); i++){
            Episode mEpisode = episodes.get(i);
            switch (mEpisode.getStatus()){
                case 2: listRowAdapter.add(mEpisode);
            }
        }
        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Episode) {
//                Movie movie = (Movie) item;
//                Log.d(TAG, "Item: " + item.toString());
//                Intent intent = new Intent(getActivity(), DetailsActivity.class);
//                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie);
//                intent.putExtra(getResources().getString(R.string.should_start), true);
//                startActivity(intent);

                Client client = new Client();
                client.initial(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.HOST, null));

                client.getEndpoint().getEpisode(((Episode) item).getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JsonObject>() {
                            @Override
                            public void accept(JsonObject jsonObject) throws Exception {
//                                JsonObject data = jsonObject.getAsJsonObject("data");
                                EpisodeDetail episodeDetail = new Gson().fromJson(jsonObject, EpisodeDetail.class);
                                Log.d("TAG", "EpisodeDetail Loaded");
                                //TODO
//                                Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                                intent.putExtra(DetailsActivity.EPISODE, episodeDetail);
                                startActivity(intent);
                            }
                        });


//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
//                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
//                getActivity().startActivity(intent, bundle);
            }
        }
    }
}
