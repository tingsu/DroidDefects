/**
 * Copyright (C) 2011-2012 Takahiro Yoshimura
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

package com.gmail.altakey.effy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import java.util.UnknownFormatConversionException;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private ListPreference drop_alpha;
	private ListPreference pen_alpha;
	private ListPreference pen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config);

		this.drop_alpha = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.DROP_ALPHA);
		this.pen_alpha = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.PEN_ALPHA);
		this.pen_width = (ListPreference)getPreferenceScreen().findPreference(ConfigKey.PEN_WIDTH);
    }

    @Override
    protected void onResume() {
        super.onResume();

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		this.updateSummary(sharedPreferences, ConfigKey.DROP_ALPHA);
		this.updateSummary(sharedPreferences, ConfigKey.PEN_ALPHA);
		this.updateSummary(sharedPreferences, ConfigKey.PEN_WIDTH);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.updateSummary(sharedPreferences, key);
    }

	private void updateSummary(SharedPreferences sharedPreferences, String key)
	{
        if (key.equals(ConfigKey.DROP_ALPHA))
			this.setSummary(this.drop_alpha, this.drop_alpha.getEntry());
        if (key.equals(ConfigKey.PEN_ALPHA))
			this.setSummary(this.pen_alpha, this.pen_alpha.getEntry());
        if (key.equals(ConfigKey.PEN_WIDTH))
			this.setSummary(this.pen_width, this.pen_width.getEntry());
	}

	private void setSummary(ListPreference lp, CharSequence summary)
	{
		try
		{
			lp.setSummary(summary);
			lp.getSummary();
		}
		catch (UnknownFormatConversionException e)
		{
			lp.setSummary(summary.toString().replace("%", "%%"));
			lp.getSummary();
		}
	}
   
}
