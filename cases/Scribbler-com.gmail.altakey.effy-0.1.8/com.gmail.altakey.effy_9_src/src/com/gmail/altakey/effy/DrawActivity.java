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
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;
import android.view.Window;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.util.Log;
import android.util.DisplayMetrics;

import com.example.android.apis.graphics.ColorPickerDialog;

public class DrawActivity extends Activity implements ColorPickerDialog.OnColorChangedListener
{
	private Paint paint = new Paint();
	private ScribbleView view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.view = new ScribbleView(this, this.paint);

		this.initialSetup();
        setContentView(this.view);
		this.refresh();
    }

	private void initialSetup()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(0xFFFFFFFF);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);

		int color = pref.getInt("_color", 0xffffffff);
		this.setPenColor(color);
	}

	private void restyle()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		int alpha = Integer.parseInt(pref.getString("drop_alpha", "192"));
		this.view.setBackgroundColor(alpha << 24);

		this.setPenColor(this.paint.getColor());

		float pen_width = (float)Integer.parseInt(pref.getString(ConfigKey.PEN_WIDTH, "5"));
		this.paint.setStrokeWidth(pen_width);
	}

	private void refresh()
	{
		this.view.invalidate();
	}

    @Override
    protected void onResume() {
        super.onResume();
		this.restyle();
		this.refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.draw, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
		case R.id.menu_pen_color:
			new ColorPickerDialog(this, this, this.paint.getColor()).show();
			return true;
		case R.id.menu_preferences:
			startActivity(new Intent(this, ConfigActivity.class));
			return true;
		case R.id.menu_close:
			Intent intents = new Intent(DrawActivity.this, MainService.class);
			stopService(intents);
			Toast.makeText(DrawActivity.this, getText(R.string.service_stopped), Toast.LENGTH_LONG).show();
			this.finish();
		}
		return true;
	}

	public void colorChanged(int color)
	{
		SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
		edit.putInt("_color", color);
		edit.commit();

		this.setPenColor(color);
	}

	private void setPenColor(int color)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		int alpha = Integer.parseInt(pref.getString("pen_alpha", "255"));
		color = (color & 0xffffff) | (alpha << 24);
		this.paint.setColor(color);
	}
}
