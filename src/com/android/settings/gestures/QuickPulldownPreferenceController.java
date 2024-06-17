/*
 * Copyright (C) 2024 Kusuma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.gestures;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import lineageos.providers.LineageSettings;

public class QuickPulldownPreferenceController extends BasePreferenceController {

    public QuickPulldownPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public CharSequence getSummary() {
        int value = LineageSettings.System.getInt(mContext.getContentResolver(),
                LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0);
        String summary=	"";
        switch (value) {
            case 0:
                summary = mContext.getResources().getString(
                    R.string.status_bar_quick_qs_pulldown_off);
                break;

            case 1:
            case 2:
                summary = mContext.getResources().getString(
                    R.string.status_bar_quick_qs_pulldown_summary,
                    mContext.getResources().getString(
                        (value == 2) ^
                        (mContext.getResources().getConfiguration().getLayoutDirection()
                            == View.LAYOUT_DIRECTION_RTL)
                        ? R.string.status_bar_quick_qs_pulldown_summary_left
                        : R.string.status_bar_quick_qs_pulldown_summary_right));
                break;
        }
        return summary;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
