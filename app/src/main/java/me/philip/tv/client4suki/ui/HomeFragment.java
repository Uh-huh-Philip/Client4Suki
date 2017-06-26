package me.philip.tv.client4suki.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.executor.Prioritized;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import me.philip.tv.client4suki.R;

/**
 * Created by phili on 6/22/2017.
 */

public class HomeFragment extends BrowseFragment {
    private static final long HEADER_ID_1 = 1;
    private static final String BACKGROUND = "Background";
    private static final String HEADER_NAME_1 = "On Air";
    private static final long HEADER_ID_2 = 2;
    private static final String HEADER_NAME_2 = "All Bangumi";
    private static final long HEADER_ID_3 = 3;
    private static final String HEADER_NAME_3 = "All TV Serious";
    private static final long HEADER_ID_4 = 4;
    private static final String HEADER_NAME_4 = "User agreement Fragment";
    private BackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mRowsAdapter;
    private DisplayMetrics mMetrics;
    private String mBackground ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new PageRowFragmentFactory(mBackgroundManager));
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    @Override
    public void onStart(){
        super.onStart();
        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(BACKGROUND, mBackground);
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
        setTitle("Suki.Moe");
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(
                        getActivity(), getString(R.string.to_be_done), Toast.LENGTH_SHORT)
                        .show();
            }
        });
//        prepareEntranceTransition();
    }

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        createRows();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                createRows();
//                startEntranceTransition();
//            }
//        }, 2000);
    }

    private void createRows() {
        HeaderItem headerItem1 = new HeaderItem(HEADER_ID_1, getResources().getString(R.string.on_air));
        PageRow pageRow1 = new PageRow(headerItem1);
        mRowsAdapter.add(pageRow1);

        HeaderItem headerItem2 = new HeaderItem(HEADER_ID_2, getResources().getString(R.string.all_bangumi));
        PageRow pageRow2 = new PageRow(headerItem2);
        mRowsAdapter.add(pageRow2);

        HeaderItem headerItem3 = new HeaderItem(HEADER_ID_3, getResources().getString(R.string.all_tv_serious));
        PageRow pageRow3 = new PageRow(headerItem3);
        mRowsAdapter.add(pageRow3);
    }

    private static class PageRowFragmentFactory extends BrowseFragment.FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        PageRowFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row)rowObj;
            mBackgroundManager.setDrawable(null);
            if (row.getHeaderItem().getId() == HEADER_ID_1) {
                return new BangumiListFragment();
            } else if (row.getHeaderItem().getId() == HEADER_ID_2) {
                return new AllBangumiFragment(2);
            } else if (row.getHeaderItem().getId() == HEADER_ID_3) {
                return new AllBangumiFragment(6);
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }

    protected boolean updateBackground(String uri) {
        mBackground = uri;
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(getResources().getDrawable(R.drawable.default_background))
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        return true;
    }
}
