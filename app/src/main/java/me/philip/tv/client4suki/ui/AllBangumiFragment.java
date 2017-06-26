package me.philip.tv.client4suki.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v17.leanback.app.BackgroundManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.philip.tv.client4suki.R;
import me.philip.tv.client4suki.Utils;
import me.philip.tv.client4suki.api.Client;
import me.philip.tv.client4suki.model.Bangumi;
import me.philip.tv.client4suki.presenter.CardPresenter;
import okhttp3.HttpUrl;

import static java.lang.System.in;

/**
 * Created by phili on 6/22/2017.
 */

public class AllBangumiFragment extends RowsFragment {

    private static final String TAG = "AllBangumiFragment";
    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private String username;
    private int type;

    private List<Bangumi> bangumiList;

    AllBangumiFragment(int type){
        this.type = type;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        switch (type){
            case 2:
                ((HomeFragment)getParentFragment()).setTitle(getActivity().getResources().getString(R.string.all_bangumi));
                break;
            case 6:
                ((HomeFragment)getParentFragment()).setTitle(getActivity().getResources().getString(R.string.all_tv_serious));
                break;
            default:
                break;
        }
        ((ProgressBar)getActivity().findViewById(R.id.temp)).setVisibility(View.VISIBLE);
        getBangumi();

        return super.onCreateView(inflater, container,savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();

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

    private void getBangumi() {
        Client client = new Client();
        client.initial(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(LoginActivity.HOST, null));

        if (bangumiList != null) {
            bangumiList = null;
        }

        client.getEndpoint().getSearchBangumi(-1, "air_date", 1, "desc")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        JsonArray data = jsonObject.getAsJsonArray("data");
                        bangumiList = new Gson().fromJson(data, new TypeToken<List<Bangumi>>() {
                        }.getType());
                        if(getActivity()!=null) {
                            loadRows(bangumiList);
                        }
                        Log.d("TAG", "Bangumi Onair Loaded");
                    }
                });
    }

    private void loadRows(List<Bangumi> bangumis) {
        if (mRowsAdapter == null) {
            mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        }
        CardPresenter cardPresenter = new CardPresenter();
//        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

//        for(Bangumi bangumi : bangumis){
//            if(bangumi.getType() == 2){
//                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
//                if bangumis.indexOf(bangumi)
//            }
//        }

//        int i;

        HashMap<String, ArrayObjectAdapter> bangumiMap = new HashMap<String, ArrayObjectAdapter>();
        ArrayList<String> seasonList = new ArrayList<String>();

        String lastSeason = "";

        for (Bangumi bangumi : bangumis) {
            String currentSeason = null;
            try {
                currentSeason = Utils.getSeason(getActivity(), bangumi);
            } catch (ParseException e) {
                currentSeason = "other";
                e.printStackTrace();
            }
            if (bangumi.getType() == type) {
                if (!currentSeason.equals(lastSeason)) {
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                    listRowAdapter.add(bangumi);
                    bangumiMap.put(currentSeason, listRowAdapter);
                    lastSeason = currentSeason;
                    seasonList.add(currentSeason);
                } else {
                    bangumiMap.get(lastSeason).add(bangumi);
                }
            }
        }

        int i = 0;
        for(String season : seasonList){
            HeaderItem header = new HeaderItem(i, season);
            mRowsAdapter.add(new ListRow(header, bangumiMap.get(season)));
            i++;
        }

        ((ProgressBar)getActivity().findViewById(R.id.temp)).setVisibility(View.GONE);
        setAdapter(mRowsAdapter);
//
//        HeaderItem header;

//        if (mRowsAdapter.size() == 0) {
//            switch (type) {
//                case 2:
//                    header = new HeaderItem(0, getResources().getString(R.string.Bangumi));
//                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
//                    break;
//                case 6:
//                    header = new HeaderItem(1, getResources().getString(R.string.TV_serious));
//                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
//                    break;
//            }
//        } else if (mRowsAdapter.size() == 1) {
//            switch (type) {
//                case 2:
//                    header = new HeaderItem(0, getResources().getString(R.string.Bangumi));
//                    mRowsAdapter.add(0, new ListRow(header, listRowAdapter));
//                    break;
//                case 6:
//                    header = new HeaderItem(1, getResources().getString(R.string.TV_serious));
//                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
//                    break;
//            }
//            setAdapter(mRowsAdapter);
//        }
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
