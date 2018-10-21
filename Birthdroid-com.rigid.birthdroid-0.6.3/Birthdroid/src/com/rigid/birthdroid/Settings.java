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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;



public class Settings
{
        /** our context */
        Context _c;
        /** our preferences */
        private SharedPreferences _prefs;
        private SharedPreferences.Editor _edit;



        /** constructor */
        public Settings(Context c)
        {
                /** save context */
                _c = c;
                
                /** read prefs */
                //_prefs = PreferenceManager.getDefaultSharedPreferences(c);
                _prefs = c.getSharedPreferences(c.getPackageName()+"_preferences", 
                                                Context.MODE_PRIVATE);
                _edit = _prefs.edit();
                commit();
        }


        /** commit recent edits */
        public void commit()
        {
                /* commit edits */
                _edit.commit();
        }

        
        /** getter */
        String getString(String key, String defValue)
        {
                return _prefs.getString(key, defValue);
        }

        int getInt(String key, int defValue)
        {
                return _prefs.getInt(key, defValue);
        }

        boolean getBoolean(String key, boolean defValue)
        {
                return _prefs.getBoolean(key, defValue);
        }
        
        /** setter */
        void putString(String key, String value)
        {
                _edit.putString(key, value);
        }

        void putInt(String key, int value)
        {
                _edit.putInt(key, value);
        }

        void putBoolean(String key, boolean value)
        {
                _edit.putBoolean(key, value);
        }
}
