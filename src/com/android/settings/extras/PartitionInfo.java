/*
* Copyright (C) 2008 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.android.settings.extras;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.settings.R;

public class PartitionInfo extends PreferenceActivity {
    private static final String TAG = "PartitionInfo";
    private static final String SYSTEM_PART_SIZE = "system_part_info";
    private static final String DATA_PART_SIZE = "data_part_info";
    private static final String CACHE_PART_SIZE = "cache_part_info";
    private static final String SDCARDFAT_PART_SIZE = "sdcard_part_info_fat";

    private Preference mSystemPartSize;
    private Preference mDataPartSize;
    private Preference mCachePartSize;
    private Preference mSDCardPartFATSize;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.partition_info);

        PreferenceScreen prefSet = getPreferenceScreen();

        mSystemPartSize = (Preference) prefSet.findPreference(SYSTEM_PART_SIZE);
        mDataPartSize = (Preference) prefSet.findPreference(DATA_PART_SIZE);
        mCachePartSize = (Preference) prefSet.findPreference(CACHE_PART_SIZE);
        mSDCardPartFATSize = (Preference) prefSet.findPreference(SDCARDFAT_PART_SIZE);

        try {
            mSystemPartSize.setSummary(ObtainFSPartSize ("/system"));
            mDataPartSize.setSummary(ObtainFSPartSize ("/data"));
            mCachePartSize.setSummary(ObtainFSPartSize ("/cache"));
            mSDCardPartFATSize.setSummary(ObtainFSPartSize ("/storage/sdcard0"));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private String ObtainFSPartSize(String PartitionPath) {
        String retstr;
        File extraPath = new File(PartitionPath);
        StatFs extraStat = new StatFs(extraPath.getPath());
        long eBlockSize = extraStat.getBlockSize();
        long eTotalBlocks = extraStat.getBlockCount();
        retstr = Formatter.formatFileSize(this, (eTotalBlocks * eBlockSize) - (extraStat.getAvailableBlocks() * eBlockSize));
        retstr += " " + getResources().getString(R.string.partition_info_used_of) + " ";
        retstr += Formatter.formatFileSize(this, eTotalBlocks * eBlockSize);
        return retstr;
    }

    public boolean fileExists(String filename) {
        File f = new File(filename);
        return f.exists();
    }
}
