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

import java.util.List;

import android.app.Service;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.util.Log;

import android.view.View;

public class MainService extends Service {
	private NotificationManager notificationManager;
	public static boolean isRunning;

	private final int NOTIFICATION = 1;
	private final IBinder binder = new MainBinder();

	private int cnt;
	
	public class MainBinder extends Binder
	{
		MainService getService()
		{
			return MainService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotifyIcon();
		isRunning = true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
		isRunning = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("MainService", "Received start id " + startId + ": " + intent);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}
	
	private void showNotifyIcon()
	{
		CharSequence text = getText(R.string.enter_drawing_mode_status);
		Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
		PendingIntent contentIntent =
			PendingIntent.getActivity(this, 0, new Intent(this, DrawActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		notification.setLatestEventInfo(
				this,
				getText(R.string.app_name),
				text,
				contentIntent);
		startForeground(NOTIFICATION, notification);
	}
}
