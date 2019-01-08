/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tasermonkeys.google.json.Gson;

public class PrivateSettingsStorage {
	private static String LOG_TAG = "PrivateSettingsStorage";
	private static PrivateSettingsStorage instance = new PrivateSettingsStorage();
	private Gson serializer;
	
	private PrivateSettingsStorage() {
		serializer = PwmGsonBuilder.makeBuilder().create();
	}

	public static PrivateSettingsStorage getInstance() {
		return instance;
	}

	public void putObject(Activity context, String key, Object obj)
			throws IOException {
		if ( obj == null ) {
			deleteObject(context, key);
			return;
		}
		FileOutputStream fos = context.openFileOutput(key + ".pss",
				Context.MODE_PRIVATE);
		try {
			String jsonStr = serializer.toJson(obj);
			fos.write(jsonStr.getBytes("UTF-8"));
		} finally {
			StreamUtils.closeNoThrow(fos);
		}
	}
	
	public void deleteObject(Activity context, String key) {
		String filename = key + ".pss";
		File f = new File(context.getFilesDir(), filename);
		if (!f.exists()) 
			return;
		f.delete();
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Activity context, String key, T defaultValue)
			throws IOException {
		String filename = key + ".pss";
		File f = new File(context.getFilesDir(), filename);
		if (!f.exists()) 
			return defaultValue;
		InputStream fis = context.openFileInput(filename);
		try {
			Reader reader = new InputStreamReader(fis, "UTF-8");
			T retVal = (T) serializer.fromJson(reader, defaultValue.getClass());
			if ( retVal == null ) {
				Log.e(LOG_TAG, "Json serializer return null on key " + key);
				return defaultValue;
				
			}
			return retVal;
		} finally {
			StreamUtils.closeNoThrow(fis);
		}
	}

}
