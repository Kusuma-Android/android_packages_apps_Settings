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

package com.android.settings.gestures;

import static lineageos.providers.LineageSettings.System.VOLBTN_MUSIC_CONTROLS;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import lineageos.providers.LineageSettings;

public class PlaybackControlPreferenceController extends GesturePreferenceController {

    private final int ON = 1;
    private final int OFF = 0;

    private static final String PREF_KEY_VIDEO = "playback_control_video";

    public PlaybackControlPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        boolean enabled = LineageSettings.System.getInt(
                mContext.getContentResolver(),
                LineageSettings.System.VOLUME_WAKE_SCREEN, 0) != 0;

        if (!enabled) {
        	return DISABLED_DEPENDENT_SETTING;
        }
        return AVAILABLE;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "volbtn_music_controls");
    }

    @Override
    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return LineageSettings.System.putInt(mContext.getContentResolver(), VOLBTN_MUSIC_CONTROLS,
                isChecked ? ON : OFF);
    }

    @Override
    public boolean isChecked() {
        return LineageSettings.System.getInt(mContext.getContentResolver(), VOLBTN_MUSIC_CONTROLS, 0) != 0;
    }
}
