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

import android.net.Uri;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

import com.android.settings.R;
import com.android.settings.utils.DeviceUtils;

import lineageos.providers.LineageSettings;

public class PlaybackControlPreferenceController extends GesturePreferenceController
        implements PlaybackControlSettingsUtils.TogglesCallback, LifecycleObserver, OnStart, OnStop {

    private final int ON = 1;
    private final int OFF = 0;

    private final PlaybackControlSettingsUtils mUtils;

    private Preference mPreference;

    private static final String PREF_KEY_VIDEO = "playback_control_video";

    public PlaybackControlPreferenceController(Context context, String key) {
        super(context, key);
        mUtils = new PlaybackControlSettingsUtils(context);
    }

    private static boolean isGestureAvailable(Context context) {
        return DeviceUtils.hasVolumeKeys(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return isAvailable(mContext) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
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

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        final ContentResolver resolver = mContext.getContentResolver();
        final boolean volumeWakeDeviceEnabled = LineageSettings.System.getInt(
                resolver, LineageSettings.System.VOLUME_WAKE_SCREEN, 0) == 1;

        if (volumeWakeDeviceEnabled) {
            preference.setEnabled(false);
            preference.setSummary(R.string.volbtn_music_controls_summary_disabled);
        }
    }

    @Override
    public void onStart() {
        mUtils.registerToggleAwareObserver(this);
    }

    @Override
    public void onStop() {
        mUtils.unregisterToggleAwareObserver();
    }

    @Override
    public void onChange(Uri uri) {
        if (mPreference == null) {
            return;
        }

        if (uri.equals(PlaybackControlSettingsUtils.VOLUME_WAKE_DEVICE_URI)) {
            updateState(mPreference);
        }
    }

    public static boolean isAvailable(Context context) {
        return isGestureAvailable(context);
    }

}
