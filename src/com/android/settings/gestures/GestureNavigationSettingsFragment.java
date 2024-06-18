/*
 * Copyright (C) 2019 The Android Open Source Project
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

import android.app.settings.SettingsEnums;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.DeviceUtils;
import com.android.settings.widget.LabeledSeekBarPreference;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.systemui.shared.recents.utilities.Utilities.isLargeScreen;

import static org.lineageos.internal.util.DeviceKeysConstants.*;

import lineageos.providers.LineageSettings;

/**
 * A fragment to include all the settings related to Gesture Navigation mode.
 */
@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class GestureNavigationSettingsFragment extends DashboardFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "GestureNavigationSettingsFragment";

    public static final String GESTURE_NAVIGATION_SETTINGS =
            "com.android.settings.GESTURE_NAVIGATION_SETTINGS";

    private static final String LEFT_EDGE_SEEKBAR_KEY = "gesture_left_back_sensitivity";
    private static final String RIGHT_EDGE_SEEKBAR_KEY = "gesture_right_back_sensitivity";

    private static final String NAVIGATION_BAR_HINT_KEY = "navigation_bar_hint";

    private static final String KEY_EDGE_LONG_SWIPE = "navigation_bar_edge_long_swipe";
    private static final String KEY_ENABLE_TASKBAR = "enable_taskbar";

    private WindowManager mWindowManager;
    private BackGestureIndicatorView mIndicatorView;

    private float[] mBackGestureInsetScales;
    private float mDefaultBackGestureInset;

    private ListPreference mEdgeLongSwipeAction;
    private SwitchPreference mEnableTaskbar;
    private SwitchPreference mNavbarHint;

    public GestureNavigationSettingsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = getResources();
        final ContentResolver resolver = requireActivity().getContentResolver();

        Action edgeLongSwipeAction = Action.fromSettings(resolver,
                LineageSettings.System.KEY_EDGE_LONG_SWIPE_ACTION,
                Action.NOTHING);

        // Edge long swipe gesture
        mEdgeLongSwipeAction = initList(KEY_EDGE_LONG_SWIPE, edgeLongSwipeAction);

        mNavbarHint = findPreference(NAVIGATION_BAR_HINT_KEY);

        mEnableTaskbar = findPreference(KEY_ENABLE_TASKBAR);
        if (mEnableTaskbar != null) {
            if (!isLargeScreen(requireContext()) || !DeviceUtils.hasNavigationBar()) {
                getPreferenceScreen().removePreference(mEnableTaskbar);
            } else if (DeviceUtils.isSwipeUpEnabled(requireContext())) {
                getPreferenceScreen().findPreference(KEY_ENABLE_TASKBAR).setEnabled(false);
                getPreferenceScreen().findPreference(KEY_ENABLE_TASKBAR)
                        .setSummary(R.string.navigation_bar_enable_taskbar_disabled_gesture);
            } else {
                mEnableTaskbar.setOnPreferenceChangeListener(this);
                mEnableTaskbar.setChecked(LineageSettings.System.getInt(resolver,
                        LineageSettings.System.ENABLE_TASKBAR,
                        isLargeScreen(requireContext()) ? 1 : 0) == 1);
                toggleTaskBarDependencies(mEnableTaskbar.isChecked());
            }
        }

        List<Integer> unsupportedValues = new ArrayList<>();
        List<String> entries = new ArrayList<>(
                Arrays.asList(res.getStringArray(R.array.hardware_keys_action_entries)));
        List<String> values = new ArrayList<>(
                Arrays.asList(res.getStringArray(R.array.hardware_keys_action_values)));

        // hide split screen option unconditionally - it doesn't work at the moment
        // once someone gets it working again: hide it only for low-ram devices
        // (check ActivityManager.isLowRamDeviceStatic())
        unsupportedValues.add(Action.SPLIT_SCREEN.ordinal());

        for (int unsupportedValue: unsupportedValues) {
            entries.remove(unsupportedValue);
            values.remove(unsupportedValue);
        }

        String[] actionEntries = entries.toArray(new String[0]);
        String[] actionValues = values.toArray(new String[0]);

        mEdgeLongSwipeAction.setEntries(actionEntries);
        mEdgeLongSwipeAction.setEntryValues(actionValues);

        mIndicatorView = new BackGestureIndicatorView(getActivity());
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);

        final Resources res = getActivity().getResources();
        mDefaultBackGestureInset = res.getDimensionPixelSize(
                com.android.internal.R.dimen.config_backGestureInset);
        mBackGestureInsetScales = getFloatArray(res.obtainTypedArray(
                com.android.internal.R.array.config_backGestureInsetScales));

        initSeekBarPreference(LEFT_EDGE_SEEKBAR_KEY);
        initSeekBarPreference(RIGHT_EDGE_SEEKBAR_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();

        mWindowManager.addView(mIndicatorView, mIndicatorView.getLayoutParams(
                getActivity().getWindow().getAttributes()));
    }

    @Override
    public void onPause() {
        super.onPause();

        mWindowManager.removeView(mIndicatorView);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEdgeLongSwipeAction) {
            handleListChange(mEdgeLongSwipeAction, newValue,
                    LineageSettings.System.KEY_EDGE_LONG_SWIPE_ACTION);
            return true;
        } else if (preference == mEnableTaskbar) {
            toggleTaskBarDependencies((Boolean) newValue);
            LineageSettings.System.putInt(getContentResolver(),
                    LineageSettings.System.ENABLE_TASKBAR, ((Boolean) newValue) ? 1 : 0);
            return true;
        }
        return false;
    }

    private ListPreference initList(String key, Action value) {
        return initList(key, value.ordinal());
    }

    private ListPreference initList(String key, int value) {
        ListPreference list = getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        LineageSettings.System.putInt(getContentResolver(), setting, Integer.parseInt(value));
    }

    private void toggleTaskBarDependencies(boolean enabled) {
        enablePreference(mNavbarHint, !enabled);
    }

    private void enablePreference(Preference pref, boolean enabled) {
        if (pref != null) {
            pref.setEnabled(enabled);
        }
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.gesture_navigation_settings;
    }

    @Override
    public int getHelpResource() {
        // TODO(b/146001201): Replace with gesture navigation help page when ready.
        return R.string.help_uri_default;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_GESTURE_NAV_BACK_SENSITIVITY_DLG;
    }

    private void initSeekBarPreference(final String key) {
        final LabeledSeekBarPreference pref = getPreferenceScreen().findPreference(key);
        pref.setContinuousUpdates(true);
        pref.setHapticFeedbackMode(SeekBarPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS);

        final String settingsKey = key == LEFT_EDGE_SEEKBAR_KEY
                ? Settings.Secure.BACK_GESTURE_INSET_SCALE_LEFT
                : Settings.Secure.BACK_GESTURE_INSET_SCALE_RIGHT;
        final float initScale = Settings.Secure.getFloat(
                getContext().getContentResolver(), settingsKey, 1.0f);

        // Find the closest value to initScale
        float minDistance = Float.MAX_VALUE;
        int minDistanceIndex = -1;
        for (int i = 0; i < mBackGestureInsetScales.length; i++) {
            float d = Math.abs(mBackGestureInsetScales[i] - initScale);
            if (d < minDistance) {
                minDistance = d;
                minDistanceIndex = i;
            }
        }
        pref.setProgress(minDistanceIndex);

        pref.setOnPreferenceChangeListener((p, v) -> {
            final int width = (int) (mDefaultBackGestureInset * mBackGestureInsetScales[(int) v]);
            mIndicatorView.setIndicatorWidth(width, key == LEFT_EDGE_SEEKBAR_KEY);
            return true;
        });

        pref.setOnPreferenceChangeStopListener((p, v) -> {
            mIndicatorView.setIndicatorWidth(0, key == LEFT_EDGE_SEEKBAR_KEY);
            final float scale = mBackGestureInsetScales[(int) v];
            Settings.Secure.putFloat(getContext().getContentResolver(), settingsKey, scale);
            return true;
        });
    }

    private static float[] getFloatArray(TypedArray array) {
        int length = array.length();
        float[] floatArray = new float[length];
        for (int i = 0; i < length; i++) {
            floatArray[i] = array.getFloat(i, 1.0f);
        }
        array.recycle();
        return floatArray;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.gesture_navigation_settings) {

        @Override
        protected boolean isPageSearchEnabled(Context context) {
            return (DeviceUtils.hasNavigationBar() ||
                    DeviceUtils.isKeyDisablerSupported(context)) &&
                    SystemNavigationPreferenceController.isGestureAvailable(context);
            }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = super.getNonIndexableKeys(context);
            if (!isLargeScreen(context) || !DeviceUtils.hasNavigationBar()) {
                keys.add(KEY_ENABLE_TASKBAR);
            }
        return keys;
        }
    };

}
