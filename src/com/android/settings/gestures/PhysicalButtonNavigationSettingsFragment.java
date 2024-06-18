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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lineageos.internal.util.DeviceKeysConstants.*;

import lineageos.providers.LineageSettings;

/**
 * A fragment that includes settings for physical button navigation modes.
 */
@SearchIndexable(forTarget = SearchIndexable.MOBILE)
public class PhysicalButtonNavigationSettingsFragment extends DashboardFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "PhysicalButtonNavigationSettingsFragment";

    public static final String BUTTON_NAVIGATION_SETTINGS =
            "com.android.settings.BUTTON_NAVIGATION_SETTINGS";

    private static final String KEY_BACK_LONG_PRESS = "hardware_keys_back_long_press";
    private static final String KEY_HOME_LONG_PRESS = "hardware_keys_home_long_press";
    private static final String KEY_HOME_DOUBLE_TAP = "hardware_keys_home_double_tap";
    private static final String KEY_MENU_PRESS = "hardware_keys_menu_press";
    private static final String KEY_MENU_LONG_PRESS = "hardware_keys_menu_long_press";
    private static final String KEY_ASSIST_PRESS = "hardware_keys_assist_press";
    private static final String KEY_ASSIST_LONG_PRESS = "hardware_keys_assist_long_press";
    private static final String KEY_APP_SWITCH_PRESS = "hardware_keys_app_switch_press";
    private static final String KEY_APP_SWITCH_LONG_PRESS = "hardware_keys_app_switch_long_press";

    private static final String KEY_BUTTON_BACKLIGHT = "button_backlight";
    private static final String KEY_SWAP_CAPACITIVE_KEYS = "swap_capacitive_keys";

    private ListPreference mBackLongPressAction;
    private ListPreference mHomeLongPressAction;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mMenuPressAction;
    private ListPreference mMenuLongPressAction;
    private ListPreference mAssistPressAction;
    private ListPreference mAssistLongPressAction;
    private ListPreference mAppSwitchPressAction;
    private ListPreference mAppSwitchLongPressAction;

    private SwitchPreference mSwapCapacitiveKeys;

    private LineageHardwareManager mHardware;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHardware = LineageHardwareManager.getInstance(getActivity());

        final Resources res = getResources();
        final ContentResolver resolver = requireActivity().getContentResolver();

        final boolean hasHomeKey = DeviceUtils.hasHomeKey(getActivity());
        final boolean hasBackKey = DeviceUtils.hasBackKey(getActivity());
        final boolean hasMenuKey = DeviceUtils.hasMenuKey(getActivity());
        final boolean hasAssistKey = DeviceUtils.hasAssistKey(getActivity());
        final boolean hasAppSwitchKey = DeviceUtils.hasAppSwitchKey(getActivity());

        if (hasBackKey) {
            Action defaultBackLongPressAction = Action.fromIntSafe(res.getInteger(
                    org.lineageos.platform.internal.R.integer.config_longPressOnBackBehavior));
            Action backLongPressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_BACK_LONG_PRESS_ACTION,
                    defaultBackLongPressAction);
            mBackLongPressAction = initList(KEY_BACK_LONG_PRESS, backLongPressAction);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_BACK_LONG_PRESS));
        }

        if (hasHomeKey) {
            Action defaultHomeLongPressAction = Action.fromIntSafe(res.getInteger(
                    org.lineageos.platform.internal.R.integer.config_longPressOnHomeBehavior));
            Action homeLongPressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_HOME_LONG_PRESS_ACTION,
                    defaultHomeLongPressAction);
            mHomeLongPressAction = initList(KEY_HOME_LONG_PRESS, homeLongPressAction);

            Action defaultHomeDoubleTapAction = Action.fromIntSafe(res.getInteger(
                    org.lineageos.platform.internal.R.integer.config_doubleTapOnHomeBehavior));
            Action homeDoubleTapAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_HOME_DOUBLE_TAP_ACTION,
                    defaultHomeDoubleTapAction);
            mHomeDoubleTapAction = initList(KEY_HOME_DOUBLE_TAP, homeDoubleTapAction);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_HOME_LONG_PRESS));
            getPreferenceScreen().removePreference(findPreference(KEY_HOME_DOUBLE_TAP));
        }

        if (hasMenuKey) {
            Action pressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_MENU_ACTION, Action.MENU);
            mMenuPressAction = initList(KEY_MENU_PRESS, pressAction);

            Action longPressAction = Action.fromSettings(resolver,
                        LineageSettings.System.KEY_MENU_LONG_PRESS_ACTION,
                        hasAssistKey ? Action.NOTHING : Action.APP_SWITCH);
            mMenuLongPressAction = initList(KEY_MENU_LONG_PRESS, longPressAction);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_MENU_PRESS));
            getPreferenceScreen().removePreference(findPreference(KEY_MENU_LONG_PRESS));
        }

        if (hasAssistKey) {
            Action pressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_ASSIST_ACTION, Action.SEARCH);
            mAssistPressAction = initList(KEY_ASSIST_PRESS, pressAction);

            Action longPressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_ASSIST_LONG_PRESS_ACTION, Action.VOICE_SEARCH);
            mAssistLongPressAction = initList(KEY_ASSIST_LONG_PRESS, longPressAction);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_ASSIST_PRESS));
            getPreferenceScreen().removePreference(findPreference(KEY_ASSIST_LONG_PRESS));
        }

        if (hasAppSwitchKey) {
            Action pressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_APP_SWITCH_ACTION, Action.APP_SWITCH);
            mAppSwitchPressAction = initList(KEY_APP_SWITCH_PRESS, pressAction);

            Action defaultAppSwitchLongPressAction = Action.fromIntSafe(res.getInteger(
                    org.lineageos.platform.internal.R.integer.config_longPressOnAppSwitchBehavior));
            Action appSwitchLongPressAction = Action.fromSettings(resolver,
                    LineageSettings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION,
                    defaultAppSwitchLongPressAction);
            mAppSwitchLongPressAction = initList(KEY_APP_SWITCH_LONG_PRESS,
                    appSwitchLongPressAction);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_APP_SWITCH_PRESS));
            getPreferenceScreen().removePreference(findPreference(KEY_APP_SWITCH_LONG_PRESS));
        }

        final ButtonBacklightBrightness backlight = findPreference(KEY_BUTTON_BACKLIGHT);
        if (!DeviceUtils.hasButtonBacklightSupport(requireActivity())
                && !DeviceUtils.hasKeyboardBacklightSupport(getActivity())) {
            getPreferenceScreen().removePreference(backlight);
        }

        mSwapCapacitiveKeys = findPreference(KEY_SWAP_CAPACITIVE_KEYS);
        if (mSwapCapacitiveKeys != null && 
                !DeviceUtils.isKeySwapperSupported(getActivity())) {
            getPreferenceScreen().removePreference(mSwapCapacitiveKeys);
        } else {
            if (DeviceUtils.hasNavigationBar() && 
                    DeviceUtils.isKeyDisablerSupported(getActivity())) {
                getPreferenceScreen().findPreference(KEY_SWAP_CAPACITIVE_KEYS).setEnabled(false);
            } else {
                mSwapCapacitiveKeys.setOnPreferenceChangeListener(this);
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

        if (hasBackKey) {
            mBackLongPressAction.setEntries(actionEntries);
            mBackLongPressAction.setEntryValues(actionValues);
        }

        if (hasHomeKey) {
            mHomeLongPressAction.setEntries(actionEntries);
            mHomeLongPressAction.setEntryValues(actionValues);

            mHomeDoubleTapAction.setEntries(actionEntries);
            mHomeDoubleTapAction.setEntryValues(actionValues);
        }

        if (hasMenuKey) {
            mMenuPressAction.setEntries(actionEntries);
            mMenuPressAction.setEntryValues(actionValues);

            mMenuLongPressAction.setEntries(actionEntries);
            mMenuLongPressAction.setEntryValues(actionValues);
        }

        if (hasAssistKey) {
            mAssistPressAction.setEntries(actionEntries);
            mAssistPressAction.setEntryValues(actionValues);

            mAssistLongPressAction.setEntries(actionEntries);
            mAssistLongPressAction.setEntryValues(actionValues);
        }

        if (hasAppSwitchKey) {
            mAppSwitchPressAction.setEntries(actionEntries);
            mAppSwitchPressAction.setEntryValues(actionValues);

            mAppSwitchLongPressAction.setEntries(actionEntries);
            mAppSwitchLongPressAction.setEntryValues(actionValues);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBackLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_BACK_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mHomeLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_HOME_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mHomeDoubleTapAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_HOME_DOUBLE_TAP_ACTION);
            return true;
        } else if (preference == mMenuPressAction) {
            handleListChange(mMenuPressAction, newValue,
                    LineageSettings.System.KEY_MENU_ACTION);
            return true;
        } else if (preference == mMenuLongPressAction) {
            handleListChange(mMenuLongPressAction, newValue,
                    LineageSettings.System.KEY_MENU_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mAssistPressAction) {
            handleListChange(mAssistPressAction, newValue,
                    LineageSettings.System.KEY_ASSIST_ACTION);
            return true;
        } else if (preference == mAssistLongPressAction) {
            handleListChange(mAssistLongPressAction, newValue,
                    LineageSettings.System.KEY_ASSIST_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mAppSwitchPressAction) {
            handleListChange(mAppSwitchPressAction, newValue,
                    LineageSettings.System.KEY_APP_SWITCH_ACTION);
            return true;
        } else if (preference == mAppSwitchLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    LineageSettings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mSwapCapacitiveKeys) {
            mHardware.set(LineageHardwareManager.FEATURE_KEY_SWAP, (Boolean) newValue);
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
        return R.xml.physical_button_navigation_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.physical_button_navigation_settings) {

        @Override
        protected boolean isPageSearchEnabled(Context context) {
            return (DeviceUtils.isKeyDisablerSupported(context) ||
                    DeviceUtils.hasPhysicalNavKeys(context));
        }

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> keys = super.getNonIndexableKeys(context);
            if (!DeviceUtils.hasBackKey(context)) {
                keys.add(KEY_BACK_LONG_PRESS);
            }
            if (!DeviceUtils.hasHomeKey(context)) {
                keys.add(KEY_HOME_LONG_PRESS);
                keys.add(KEY_HOME_DOUBLE_TAP);
            }
            if (!DeviceUtils.hasMenuKey(context)) {
                keys.add(KEY_MENU_PRESS);
                keys.add(KEY_MENU_LONG_PRESS);
            }
            if (!DeviceUtils.hasAssistKey(context)) {
                keys.add(KEY_ASSIST_PRESS);
                keys.add(KEY_ASSIST_LONG_PRESS);
            }
            if (!DeviceUtils.hasAppSwitchKey(context)) {
                keys.add(KEY_APP_SWITCH_PRESS);
                keys.add(KEY_APP_SWITCH_LONG_PRESS);
            }
            if (!DeviceUtils.isKeySwapperSupported(context)) {
                keys.add(KEY_SWAP_CAPACITIVE_KEYS);
            }
            if (!DeviceUtils.hasButtonBacklightSupport(context)
                    && !DeviceUtils.hasKeyboardBacklightSupport(context)) {
                keys.add(KEY_BUTTON_BACKLIGHT);
            }
        return keys;
        }
    };
}
