/*
 * Copyright (C) 2020 The Android Open Source Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import lineageos.providers.LineageSettings;

public class PlaybackControlSettingsUtils {

    static final Uri VOLUME_WAKE_DEVICE_URI =
            LineageSettings.System.getUriFor(LineageSettings.System.VOLUME_WAKE_SCREEN);

    private final Context mContext;
    private final SettingsObserver mSettingsObserver;

    PlaybackControlSettingsUtils(Context context) {
        mContext = context;
        mSettingsObserver = new SettingsObserver(new Handler(Looper.getMainLooper()));
    }

    public static boolean isVolumeWakeDeviceEnabled(Context context) {
        return LineageSettings.System.getInt(context.getContentResolver(),
                LineageSettings.System.VOLUME_WAKE_SCREEN, 0) == 1;
    }

    public void registerToggleAwareObserver(TogglesCallback callback) {
        mSettingsObserver.observe();
        mSettingsObserver.setCallback(callback);
    }

    public void unregisterToggleAwareObserver() {
        final ContentResolver resolver = mContext.getContentResolver();
        resolver.unregisterContentObserver(mSettingsObserver);
    }

    private final class SettingsObserver extends ContentObserver {
        private TogglesCallback mCallback;

        SettingsObserver(Handler handler) {
            super(handler);
        }

        private void setCallback(TogglesCallback callback) {
            mCallback = callback;
        }

        public void observe() {
            final ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(VOLUME_WAKE_DEVICE_URI, true, this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (mCallback != null) mCallback.onChange(uri);
        }
    }

    public interface TogglesCallback {
        void onChange(Uri uri);
    }

}
