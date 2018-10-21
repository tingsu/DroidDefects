/*
 * Birthdroid - Android upcoming birthday App/Widget
 * Copyright (C) 2011-2013 Daniel Hiepler <daniel@niftylight.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rigid.birthdroid;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
//import android.content.SharedPreferences;
//import android.preference.Preference;
import android.preference.PreferenceActivity;
//import android.preference.Preference.OnPreferenceClickListener;
import android.os.Bundle;
import android.util.Log;




public class PreferencesActivity extends PreferenceActivity
{
        private final static String TAG = "Birthdroid/PreferencesActivity";
        
        
        
        /** called by OS when app is created initially */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);

                /** create prefs from xml */
                addPreferencesFromResource(R.xml.preferences);
        }

        /** another activity comes over this activity */
        @Override
        public void onPause()
        {
                /** update widget */
                Intent i = new Intent(this, BirthdroidWidget.class);
                i.setAction("com.rigid.birthdroid.PREFS_UPDATE");
                sendBroadcast(i);

                super.onPause();
        }
        
}
