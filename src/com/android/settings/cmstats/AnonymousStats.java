/*
 * Copyright (C) 2015 The CyanogenMod Project
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

package com.android.settings.cmstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.ArraySet;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Set;

public class AnonymousStats extends SettingsPreferenceFragment {
    private static final String PREF_FILE_NAME = "CMStats";
    /* package */ static final String ANONYMOUS_OPT_IN = "pref_anonymous_opt_in";
    /* package */ static final String ANONYMOUS_LAST_CHECKED = "pref_anonymous_checked_in";

    /* package */ static final String KEY_JOB_QUEUE = "pref_job_queue";

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.anonymous_stats);
    }

    public static Set<String> getJobQueue(Context context) {
        return getPreferences(context).getStringSet(KEY_JOB_QUEUE, new ArraySet<String>());
    }

    public static void addJob(Context context, int jobId) {
        Set<String> jobQueue = getJobQueue(context);
        jobQueue.add(String.valueOf(jobId));

        getPreferences(context)
                .edit()
                .putStringSet(KEY_JOB_QUEUE, jobQueue)
                .commit();
    }

    public static void removeJob(Context context, int jobId) {
        Set<String> jobQueue = getJobQueue(context);
        jobQueue.remove(String.valueOf(jobId));
        getPreferences(context)
                .edit()
                .putStringSet(KEY_JOB_QUEUE, jobQueue)
                .commit();
    }

    public static int getNextJobId(Context context) {
        Set<String> currentQueue = getJobQueue(context);

        if (currentQueue == null) {
            return 1;
        } else {
            int currentMax = -1;

            for (String val : currentQueue) {
                Integer i = Integer.parseInt(val);
                if (i > currentMax) {
                    currentMax = i;
                }
            }
            return (currentMax + 1);

        }
    }
}
