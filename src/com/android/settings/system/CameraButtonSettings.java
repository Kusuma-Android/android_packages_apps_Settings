/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.settings.system;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SearchIndexableResource;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.DeviceUtils;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.SwitchPreference;

import java.util.List;

@SearchIndexable
public class CameraButtonSettings extends DashboardFragment {

    private static final String TAG = "CameraButtonSettings";

    private static final String KEY_CAMERA_SLEEP_ON_RELEASE = "camera_sleep_on_release";

    private SwitchPreference mCameraSleepOnRelease;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = getResources();

        mCameraSleepOnRelease = findPreference(KEY_CAMERA_SLEEP_ON_RELEASE);
        if (res.getBoolean(
                org.lineageos.platform.internal.R.bool.config_singleStageCameraKey)) {
            getPreferenceScreen().removePreference(mCameraSleepOnRelease);
        }
    }

    @Override
    public int getMetricsCategory() {
        return -1;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.camera_button;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.camera_button) {
        @Override
        protected boolean isPageSearchEnabled(Context context) {
            return DeviceUtils.hasCameraKey(context);
        }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = super.getNonIndexableKeys(context);
            if (context.getResources().getBoolean(
                    org.lineageos.platform.internal.R.bool.config_singleStageCameraKey)) {
                keys.add(KEY_CAMERA_SLEEP_ON_RELEASE);
            }
        return keys;
        }
    };
}
