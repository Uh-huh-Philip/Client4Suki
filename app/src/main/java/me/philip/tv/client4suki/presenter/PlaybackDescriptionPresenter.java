package me.philip.tv.client4suki.presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import me.philip.tv.client4suki.model.EpisodeDetail;

/**
 * Created by phili on 6/20/2017.
 */

public class PlaybackDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    EpisodeDetail mEpisodeDetail;
    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        mEpisodeDetail = (EpisodeDetail) item;
        viewHolder.getTitle().setText(((EpisodeDetail) item).getBangumi().getName_cn());
        viewHolder.getSubtitle().setText(((EpisodeDetail) item).getName_cn());
    }
}
