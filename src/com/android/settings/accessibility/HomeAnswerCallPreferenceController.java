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

package com.android.settings.accessibility;

import android.content.Context;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.utils.DeviceUtils;

import lineageos.providers.LineageSettings;

public class HomeAnswerCallPreferenceController extends TogglePreferenceController {

    public HomeAnswerCallPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public boolean isChecked() {
        final int incallHomeBehavior = LineageSettings.Secure.getInt(mContext.getContentResolver(),
                LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR,
                LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR_DEFAULT);
        return incallHomeBehavior == LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return LineageSettings.Secure.putInt(mContext.getContentResolver(),
                LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR,
                (isChecked ? LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER
                        : LineageSettings.Secure.RING_HOME_BUTTON_BEHAVIOR_DO_NOTHING));
    }

    @Override
    public int getAvailabilityStatus() {
        return !DeviceUtils.hasHomeKey(mContext)
                || !Utils.isVoiceCapable(mContext) ? UNSUPPORTED_ON_DEVICE : AVAILABLE;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_accessibility;
    }
}
