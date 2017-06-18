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

package me.philip.tv.client4suki.presenter;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import me.philip.tv.client4suki.model.Bangumi;
import me.philip.tv.client4suki.model.Movie;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Bangumi bangumi = (Bangumi) item;

        if (bangumi != null) {
            viewHolder.getTitle().setText(bangumi.getName_cn());
            viewHolder.getSubtitle().setText("放送开始: " + bangumi.getAir_date());
            viewHolder.getBody().setText(bangumi.getSummary());
        }
    }
}
