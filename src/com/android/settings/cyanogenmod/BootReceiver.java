/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.settings.cyanogenmod;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.settings.*;
import com.android.settings.contributors.ContributorsCloudFragment;
import com.android.settings.hardware.VibratorIntensity;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.livedisplay.DisplayGamma;
import com.android.settings.location.LocationSettings;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        /* Restore the hardware tunable values */
        ButtonSettings.restoreKeyDisabler(ctx);
        DisplayGamma.restore(ctx);
        VibratorIntensity.restore(ctx);
        InputMethodAndLanguageSettings.restore(ctx);
        LocationSettings.restore(ctx);

        // Extract the contributors database
        ContributorsCloudFragment.extractContributorsCloudDatabase(ctx);

        // start the DataUsage monitoring service
        Log.v(TAG, "Starting DataUsageService Alarm from BootReceiver");
        DataUsageUtils.enbDataUsageService(ctx, true);
    }
}
