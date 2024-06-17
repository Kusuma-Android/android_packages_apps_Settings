/*
 * Copyright (C) 2018 The Android Open Source Project
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
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.SelectorWithWidgetPreference;

import lineageos.providers.LineageSettings;

public class QuickPulldownSelectorPreferenceController extends AbstractPreferenceController
        implements SelectorWithWidgetPreference.OnClickListener, LifecycleObserver,
        OnResume, OnPause, PreferenceControllerMixin {

    static final String KEY_NONE = "quick_pulldown_none";
    static final String KEY_RIGHT = "quick_pulldown_right";
    static final String KEY_LEFT = "quick_pulldown_left";

    private final String KEY = "quick_pulldown_category";

    private final Context mContext;

    PreferenceCategory mPreferenceCategory;
    SelectorWithWidgetPreference mNone;
    SelectorWithWidgetPreference mRight;
    SelectorWithWidgetPreference mLeft;

    private SettingObserver mSettingObserver;

    public QuickPulldownSelectorPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mContext = context;

        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreferenceCategory = screen.findPreference(getPreferenceKey());
        mNone = makeRadioPreference(KEY_NONE, R.string.status_bar_quick_qs_pulldown_off);
        if (mContext.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            mRight = makeRadioPreference(KEY_RIGHT, R.string.status_bar_quick_qs_pulldown_left);
            mLeft = makeRadioPreference(KEY_LEFT, R.string.status_bar_quick_qs_pulldown_right);
        } else {
            mRight = makeRadioPreference(KEY_RIGHT, R.string.status_bar_quick_qs_pulldown_right);
            mLeft = makeRadioPreference(KEY_LEFT, R.string.status_bar_quick_qs_pulldown_left);

        }

        if (mPreferenceCategory != null) {
            mSettingObserver = new SettingObserver(mPreferenceCategory);
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    @Override
    public void onResume() {
        if (mSettingObserver != null) {
            mSettingObserver.register(mContext.getContentResolver());
            mSettingObserver.onChange(false, null);
        }
    }

    @Override
    public void onPause() {
        if (mSettingObserver != null) {
            mSettingObserver.unregister(mContext.getContentResolver());
        }
    }

    @Override
    public void onRadioButtonClicked(SelectorWithWidgetPreference preference) {
        if (preference == mNone) {
            LineageSettings.System.putInt(mContext.getContentResolver(),
                    LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0);
        } else if (preference == mRight) {
            LineageSettings.System.putInt(mContext.getContentResolver(),
                    LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 1);
        } else if (preference == mLeft) {
            LineageSettings.System.putInt(mContext.getContentResolver(),
                    LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 2);
        }
    }

    @Override
    public void updateState(Preference preference) {
        int value = LineageSettings.System.getInt(mContext.getContentResolver(),
                LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0);

        switch (value) {
            case 0:
                mNone.setChecked(true);
                mRight.setChecked(false);
                mLeft.setChecked(false);
                break;
            case 1:
                mNone.setChecked(false);
                mRight.setChecked(true);
                mLeft.setChecked(false);
                break;
            case 2:
                mNone.setChecked(false);
                mRight.setChecked(false);
                mLeft.setChecked(true);
                break;
        }
    }

    private SelectorWithWidgetPreference makeRadioPreference(String key, int titleId) {
        SelectorWithWidgetPreference pref = new SelectorWithWidgetPreference(
                mPreferenceCategory.getContext());
        pref.setKey(key);
        pref.setTitle(titleId);
        pref.setOnClickListener(this);
        mPreferenceCategory.addPreference(pref);
        return pref;
    }

    private class SettingObserver extends ContentObserver {
        private final Uri URI_STATUS_BAR_QUICK_QS_PULLDOWN  = LineageSettings.System.getUriFor(
                LineageSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN);

        private final Preference mPreference;
        public SettingObserver(Preference preference) {
            super(new Handler());
            mPreference = preference;
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(URI_STATUS_BAR_QUICK_QS_PULLDOWN, false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null || URI_STATUS_BAR_QUICK_QS_PULLDOWN.equals(uri)) {
                updateState(mPreference);
            }
        }
    }
}
