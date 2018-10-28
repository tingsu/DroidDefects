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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;



/** our widget */
public class BirthdroidWidget extends AppWidgetProvider 
{
        private final static String TAG = "BirthdroidWidget";
        private PendingIntent pi;
        /** object to hold all our permanent settings */
        private Settings s;

        
        /** called by OS when first widget instance is created */
        @Override
        public void onEnabled(Context c)
        {
                super.onEnabled(c);

                /* create settings */
                s = new Settings(c);
                
                /* create intent that launches our service */
                Intent i = new Intent(c, BirthdroidService.class);
                pi = PendingIntent.getService(c, 0, i, 0);

                /* get alarm-manager */
                AlarmManager m = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

                /* create first alarm at midnight */
                Calendar alarm = new GregorianCalendar();
                alarm.set(Calendar.HOUR_OF_DAY, 0);
                alarm.set(Calendar.MINUTE, 0);
                alarm.set(Calendar.SECOND, 0);
                alarm.set(Calendar.MILLISECOND, 0);
                
                /* set alarm to go off every day */
                m.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }


        /** called by OS when last widget instance is removed */
        @Override
        public void onDisabled(Context c)
        {
                /* get alarm-manager */
                AlarmManager m = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

                /* cancel alarm */
                m.cancel(pi);
                
                super.onDisabled(c);
        }

        
        /** called by OS when widget is updated */
        @Override
        public void onUpdate(Context c, AppWidgetManager m, int[] ids) 
        {
                Log.v(TAG, "Launching update-service");
                
                /**
                 * To prevent any "Application-Not-Responding" timeouts with
                 * LOTS of contacts, we perform the update in a service */
                c.startService(new Intent(c, BirthdroidService.class));
        }

                
        /** called by OS to receive actions */
        @Override
        public void onReceive(Context context, Intent intent) 
        {
                String action = intent.getAction();

                /* click on widget? */
                if(action.equals("com.rigid.birthdroid.CLICK"))
                {
                        Settings settings = new Settings(context);
                        if(settings.getBoolean("widget_clickable", context.getResources().getString(R.string.widget_clickable).equals("true")))
                        {
                                Log.v(TAG, "Click on widget -> Opening app");
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setClassName(context.getPackageName(), context.getPackageName()+".BirthdroidActivity");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                        }
                        else
                        {
                                Log.v(TAG,"Click on widget but not opening app");
                        }
                }
                /* Preferences changed? */
                else if(action.equals("com.rigid.birthdroid.PREFS_UPDATE")) 
                {
                        AppWidgetManager m = AppWidgetManager.getInstance(context);
                        int[] ids = m.getAppWidgetIds(new ComponentName(context, BirthdroidWidget.class));
                        this.onUpdate(context, m, ids);
                }
                else
                {
                        super.onReceive(context, intent);
                }
 
        }

        
        /** our Update "service" */
        public static class BirthdroidService extends Service
        {
                /** called by OS service is launched */
                @Override
                public void onStart(Intent intent, int startId) 
                {
                        /* resources */
                        Resources r = getResources();
                        
                        /* get preferences */
                        Settings s = new Settings(getApplicationContext());
                        
                        /* create Birthday list */
                        Birthdays birthdays = new Birthdays(this);

                        /* sort birthdays */
                        birthdays.sort(s.getString("widget_sort_method", r.getString(R.string.widget_sort_method)));
                        
                        /* get list of upcoming birthdays */
                        int upcoming_n = Integer.parseInt(s.getString("widget_future_days", Integer.toString(r.getInteger(R.integer.widget_future_days))));
                        Log.v(TAG, "Checking for birthdays in the next "+upcoming_n+" days");
                        List<Birthdays.Birthday> list = birthdays.getUpcoming(upcoming_n);
                                       
                        /* create view from widget layout */
                        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);

                        /* remove all entries of ViewFlipper */
                        views.removeAllViews(R.id.view_flipper);
                        
                        /* set view-flipper delay */                        
                        views.setInt(R.id.view_flipper, "setFlipInterval", Integer.parseInt(s.getString("widget_flip_interval", Integer.toString(r.getInteger(R.integer.widget_flip_interval))))*1000);
                        
                        /* set view-flipper animation */
                        //views.setInt(R.id.view_flipper, "setInAnimation", R.anim.push_up_in);
                        //views.setInt(R.id.view_flipper, "setOutAnimation", R.anim.push_up_out);

                                                
                        /* attach handler for clicks on widget */
                        views.setOnClickPendingIntent
                        (
                                R.id.view_flipper, 
                                PendingIntent.getBroadcast
                                (
                                        this, 
                                        0, 
                                        new Intent("com.rigid.birthdroid.CLICK"), 
                                        0
                                )
                        );

                        /* got birthdays? */
                        if(list.size() > 0)
                        {
                                /* create text-views */
                                for(int i = 0; i < list.size(); i++)
                                {
                                        RemoteViews tview = new RemoteViews(getPackageName(), R.layout.widget_text);

                                        /* set message text */
                                        tview.setTextViewText(R.id.widget_person, list.get(i).personName);
                                        tview.setTextViewText(R.id.widget_message, list.get(i).getMessage());
                                        
                                        /* add TextViews to layout */
                                        views.addView(R.id.view_flipper, tview);
                                }
                        }
                        else
                        {
                                Log.v(TAG, "No upcoming birthdays in next "+
                                           upcoming_n+                                                                                   
                                           " days");

                                /** set "no birthday" message */
                                RemoteViews tview = new RemoteViews(getPackageName(), R.layout.widget_no_birthdays);
                                views.addView(R.id.view_flipper, tview);
                        }
                        
                        /* update widget view */
                        ComponentName widget = new ComponentName(this, BirthdroidWidget.class);
                        AppWidgetManager manager = AppWidgetManager.getInstance(this);
                        manager.updateAppWidget(widget, views);

                        /* exit service */
                        stopSelf();
                }

                /** called by OS when intent binds to this service */
                @Override
                public IBinder onBind(Intent intent) 
                {
                        /* no binding needed */
                        return null;
                }
        }
        
}
