/*
 * Copyright (C) 2022 Project Kaleidoscope
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

package com.android.settings.fuelgauge.batteryhealth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.Preference;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.List;

import org.lineageos.internal.util.FileUtils;

@SearchIndexable
public class BatteryHealthFragment extends SettingsPreferenceFragment {

    private static final String TAG = "BatteryHealthFragment";

    private static final String KEY_BATTERY_CIRCLE = "battery_charge_circle";
    private static final String KEY_BATTERY_MAXIMUM = "battery_maximum_percentage";

    private Preference mBatteryMaximum;
    private Preference mBatteryCircle;

    private String mBatteryNowCapacityNode;
    private String mBatteryDesignCapacityNode;
    private String mBatteryChargeCycleNode;
    private String mBatteryHealthOneplus;

    private boolean updateBatteryMaximumPercentageOneplus() {
        if (TextUtils.isEmpty(mBatteryHealthOneplus))
            return true;

        String batteryHealth = null;

        if (FileUtils.isFileReadable(mBatteryHealthOneplus)) {
            batteryHealth = FileUtils.readOneLine(mBatteryHealthOneplus);
        } else {
            Log.e(TAG, "Unable to read battery health node.");
        }

        mBatteryMaximum.setSummary(batteryHealth + "%");
        return false;
    }

    private boolean updateBatteryMaximumPercentage() {
        if (TextUtils.isEmpty(mBatteryNowCapacityNode)
                || TextUtils.isEmpty(mBatteryDesignCapacityNode))
            return true;

        int batteryNowCapacity = 0;
        int batteryDesignCapacity = 0;

        if (FileUtils.isFileReadable(mBatteryNowCapacityNode) &&
                FileUtils.isFileReadable(mBatteryDesignCapacityNode)) {
            batteryNowCapacity = Integer.parseInt(
                    FileUtils.readOneLine(mBatteryNowCapacityNode));
            batteryDesignCapacity = Integer.parseInt(
                    FileUtils.readOneLine(mBatteryDesignCapacityNode));
        } else {
            Log.e(TAG, "Unable to read battery capacity node.");
        }

        mBatteryMaximum.setSummary(100 * batteryNowCapacity / batteryDesignCapacity + "%");
        return false;
    }

    private boolean updateBatteryChargeCircle() {
        if (TextUtils.isEmpty(mBatteryChargeCycleNode))
            return true;

        String batteryChargeCycle = null;

        if (FileUtils.isFileReadable(mBatteryChargeCycleNode)) {
            batteryChargeCycle = FileUtils.readOneLine(mBatteryChargeCycleNode);
        } else {
            Log.e(TAG, "Unable to read battery charge cycle node.");
        }

        mBatteryCircle.setSummary(batteryChargeCycle);
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_health);

        mBatteryNowCapacityNode = requireContext().getString(
                R.string.config_battery_capacity_now);
        mBatteryDesignCapacityNode = requireContext().getString(
                R.string.config_battery_capacity_design);
        mBatteryChargeCycleNode = requireContext().getString(
                R.string.config_battery_charge_cycle);
        mBatteryHealthOneplus = requireContext().getString(
                R.string.config_battery_health_oneplus);

        mBatteryCircle = findPreference(KEY_BATTERY_CIRCLE);
        mBatteryMaximum = findPreference(KEY_BATTERY_MAXIMUM);

        if (updateBatteryMaximumPercentage() && updateBatteryMaximumPercentageOneplus())
            removePreference(KEY_BATTERY_MAXIMUM);
        if (updateBatteryChargeCircle())
            removePreference(KEY_BATTERY_CIRCLE);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.BATTERY_HEALTH;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.battery_health) {
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    String batteryNowCapacityNode = context.getString(
                            R.string.config_battery_capacity_now);
                    String batteryDesignCapacityNode = context.getString(
                            R.string.config_battery_capacity_design);
                    String batteryChargeCycleNode = context.getString(
                            R.string.config_battery_charge_cycle);
                    String batteryHealthOneplus = context.getString(
                            R.string.config_battery_health_oneplus);

                    if ((TextUtils.isEmpty(batteryNowCapacityNode)
                            || TextUtils.isEmpty(batteryDesignCapacityNode))
                            && TextUtils.isEmpty(batteryHealthOneplus))
                        keys.add(KEY_BATTERY_MAXIMUM);
                    if (TextUtils.isEmpty(batteryChargeCycleNode))
                        keys.add(KEY_BATTERY_CIRCLE);

                    return keys;
                }
            };
}
