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

package com.android.settings.gestures;

import static android.view.WindowManagerPolicyConstants.NAV_BAR_MODE_2BUTTON_OVERLAY;

import android.app.settings.SettingsEnums;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.DeviceUtils;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import static com.android.systemui.shared.recents.utilities.Utilities.isLargeScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lineageos.internal.util.DeviceKeysConstants.*;

import lineageos.providers.LineageSettings;

/**
 * A fragment that includes settings for 2-button navigation modes.
 */
@SearchIndexable(forTarget = SearchIndexable.MOBILE)
public class TwoButtonNavigationSettingsFragment extends DashboardFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "TwoButtonNavigationSettingsFragment";

    public static final String TWO_BUTTON_NAVIGATION_SETTINGS =
            "com.android.settings.TWO_BUTTON_NAVIGATION_SETTINGS";

    private static final String KEY_NAVIGATION_BACK_LONG_PRESS = "navigation_back_long_press";
    private static final String KEY_NAVIGATION_HOME_LONG_PRESS = "navigation_home_long_press";
    private static final String KEY_NAVIGATION_HOME_DOUBLE_TAP = "navigation_home_double_tap";

    private ListPreference mNavigationBackLongPressAction;
    private ListPreference mNavigationHomeLongPressAction;
    private ListPreference mNavigationHomeDoubleTapAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources res = getResources();
        final ContentResolver resolver = requireActivity().getContentResolver();

        Action defaultBackLongPressAction = Action.fromIntSafe(res.getInteger(
                org.lineageos.platform.internal.R.integer.config_longPressOnBackBehavior));
        Action defaultHomeLongPressAction = Action.fromIntSafe(res.getInteger(
                org.lineageos.platform.internal.R.integer.config_longPressOnHomeBehavior));
        Action defaultHomeDoubleTapAction = Action.fromIntSafe(res.getInteger(
                org.lineageos.platform.internal.R.integer.config_doubleTapOnHomeBehavior));
        Action backLongPressAction = Action.fromSettings(resolver,
                LineageSettings.System.KEY_BACK_LONG_PRESS_ACTION,
                defaultBackLongPressAction);
        Action homeLongPressAction = Action.fromSettings(resolver,
                LineageSettings.System.KEY_HOME_LONG_PRESS_ACTION,
                defaultHomeLongPressAction);
        Action homeDoubleTapAction = Action.fromSettings(resolver,
                LineageSettings.System.KEY_HOME_DOUBLE_TAP_ACTION,
                defaultHomeDoubleTapAction);

        // Navigation bar back long press
        mNavigationBackLongPressAction = initList(KEY_NAVIGATION_BACK_LONG_PRESS,
                backLongPressAction);

        // Navigation bar home long press
        mNavigationHomeLongPressAction = initList(KEY_NAVIGATION_HOME_LONG_PRESS,
                homeLongPressAction);

        // Navigation bar home double tap
        mNavigationHomeDoubleTapAction = initList(KEY_NAVIGATION_HOME_DOUBLE_TAP,
                homeDoubleTapAction);

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

        mNavigationBackLongPressAction.setEntries(actionEntries);
        mNavigationBackLongPressAction.setEntryValues(actionValues);

        mNavigationHomeLongPressAction.setEntries(actionEntries);
        mNavigationHomeLongPressAction.setEntryValues(actionValues);

        mNavigationHomeDoubleTapAction.setEntries(actionEntries);
        mNavigationHomeDoubleTapAction.setEntryValues(actionValues);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavigationBackLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_BACK_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mNavigationHomeLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_HOME_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mNavigationHomeDoubleTapAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_HOME_DOUBLE_TAP_ACTION);
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

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_BUTTON_NAV_DLG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.two_button_navigation_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.two_button_navigation_settings) {

        @Override
        protected boolean isPageSearchEnabled(Context context) {
            boolean isTaskbarEnabled = LineageSettings.System.getInt(context.getContentResolver(),
                    LineageSettings.System.ENABLE_TASKBAR, isLargeScreen(context) ? 1 : 0) == 1;

            return (DeviceUtils.hasNavigationBar() || 
                    DeviceUtils.isKeyDisablerSupported(context)) && !isTaskbarEnabled && 
                    SystemNavigationPreferenceController.isOverlayPackageAvailable(context,
                    NAV_BAR_MODE_2BUTTON_OVERLAY);
        }
    };
}
