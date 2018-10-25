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

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.KeyEvent;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button bb = (Button)findViewById(R.id.button1);
		View running = findViewById(R.id.running);
		if (MainService.isRunning)
		{
			running.setVisibility(View.VISIBLE);
			bb.setText(R.string.service_stop_button);
		}
		else
		{
			running.setVisibility(View.INVISIBLE);
			bb.setText(R.string.service_start_button);
		}


		bb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intents = new Intent(MainActivity.this, MainService.class);
				if(MainService.isRunning){
					stopService(intents);
					Toast.makeText(MainActivity.this, getText(R.string.service_stopped), Toast.LENGTH_LONG).show();
				}else{
					startService(intents);
					Toast.makeText(MainActivity.this, getText(R.string.service_started), Toast.LENGTH_LONG).show();
				}
				finish();
			}
		});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.menu_preferences:
			startActivity(new Intent(this, ConfigActivity.class));			
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			this.openOptionsMenu();
			return true;
		}

		return super.onKeyLongPress(keyCode, event);
	}
}
