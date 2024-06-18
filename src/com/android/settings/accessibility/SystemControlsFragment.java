/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.settings.accessibility;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.Utils;
import com.android.settings.utils.DeviceUtils;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.PreferenceScreen;

import java.util.List;

/** Accessibility settings for system controls. */
@SearchIndexable
public class SystemControlsFragment extends DashboardFragment
        implements Indexable {

    private static final String TAG = "SystemControlsFragment";

    private static final String KEY_VOLUME_ANSWER_CALL = "volume_answer_call";

    private static final String KEY_HOME_WAKE_SCREEN = "home_wake_screen";
    private static final String KEY_BACK_WAKE_SCREEN = "back_wake_screen";
    private static final String KEY_MENU_WAKE_SCREEN = "menu_wake_screen";
    private static final String KEY_ASSIST_WAKE_SCREEN = "assist_wake_screen";
    private static final String KEY_APP_SWITCH_WAKE_SCREEN = "app_switch_wake_screen";
    private static final String KEY_VOLUME_WAKE_SCREEN = "volume_wake_screen";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DeviceUtils.canWakeUsingHomeKey(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(KEY_HOME_WAKE_SCREEN));
        }
        if (!DeviceUtils.canWakeUsingBackKey(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(KEY_BACK_WAKE_SCREEN));
        }
        if (!DeviceUtils.canWakeUsingMenuKey(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(KEY_MENU_WAKE_SCREEN));
        }
        if (!DeviceUtils.canWakeUsingAssistKey(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(KEY_ASSIST_WAKE_SCREEN));
        }
        if (!DeviceUtils.canWakeUsingAppSwitchKey(getActivity())) {
            getPreferenceScreen().removePreference(findPreference(KEY_APP_SWITCH_WAKE_SCREEN));
        }
        if (DeviceUtils.hasVolumeKeys(getActivity())) {
            if (!Utils.isVoiceCapable(requireActivity())) {
                getPreferenceScreen().removePreference(findPreference(KEY_VOLUME_ANSWER_CALL));
            }
            if (!DeviceUtils.canWakeUsingVolumeKeys(getActivity())) {
                getPreferenceScreen().removePreference(findPreference(KEY_VOLUME_WAKE_SCREEN));
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.ACCESSIBILITY_SYSTEM_CONTROLS;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.accessibility_system_controls;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.accessibility_system_controls) {

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = super.getNonIndexableKeys(context);
            if (!DeviceUtils.canWakeUsingHomeKey(context)) {
                keys.add(KEY_HOME_WAKE_SCREEN);
            }
            if (!DeviceUtils.canWakeUsingBackKey(context)) {
                keys.add(KEY_BACK_WAKE_SCREEN);
            }
            if (!DeviceUtils.canWakeUsingMenuKey(context)) {
                keys.add(KEY_MENU_WAKE_SCREEN);
            }
            if (!DeviceUtils.canWakeUsingAssistKey(context)) {
                keys.add(KEY_ASSIST_WAKE_SCREEN);
            }
            if (!DeviceUtils.canWakeUsingAppSwitchKey(context)) {
                keys.add(KEY_APP_SWITCH_WAKE_SCREEN);
            }
            if (!Utils.isVoiceCapable(context)) {
                keys.add(KEY_VOLUME_ANSWER_CALL);
            }
            if (!DeviceUtils.canWakeUsingVolumeKeys(context)) {
                keys.add(KEY_VOLUME_WAKE_SCREEN);
            }
        return keys;
        }
    };
}
