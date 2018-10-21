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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;




/** main activity */
public class BirthdroidActivity extends ListActivity
{
        private final static String TAG = "Birthdroid/BirthdroidActivity";
        /** object holding all birthdays */
        private Birthdays b;
        /** object to hold all our permanent settings */
        private Settings s;

        
        
        /** called by OS when app is created initially */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);

                /* create new settings-object */
                s = new Settings(this);
                /* create new birthday-list-object */
                b = new Birthdays(this);
                
                /* sort birthdays */
                b.sort(s.getString("birthdroid_sort_method", 
                        getResources().getString(R.string.birthdroid_sort_method)));

                /* use our own list adapter */
                setListAdapter(new BirthdroidListAdapter(this));

                /* enable text-filter */
                getListView().setTextFilterEnabled(true);

                /* register click-listener for list-items */
                getListView().setOnItemClickListener(new OnItemClickListener() 
                {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) 
                        {
                                /* open this contact */
                                b.get(position).openContact();
                        }
                });
        }

		
        /** called by OS when this activity becomes active again */
        @Override
        public void onResume()
        {
                super.onResume();
                
                /* sort birthdays */
                b.sort(s.getString("birthdroid_sort_method", getResources().getString(R.string.birthdroid_sort_method)));

                ((BaseAdapter) getListAdapter()).notifyDataSetInvalidated();
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

		
        /**
         * ListAdapter that presents a complete list of birthdays
         */
        private class BirthdroidListAdapter extends BaseAdapter 
        {
                private Context _c;
                private SimpleDateFormat _df;
                private LayoutInflater _i;
                
                
                public BirthdroidListAdapter(Context context) 
                {
                        _c = context;
                        _i = LayoutInflater.from(context);
                        _df = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
                }

                
                /**
                 * @see android.widget.ListAdapter#getCount()
                 */
                public int getCount() 
                {
                        return b.getCount();
                }

                
        

                /**
                 * return id object represents one row in the
                 * list.
                 * 
                 * @see android.widget.ListAdapter#getItem(int)
                 */
                public Object getItem(int position) 
                {
                        return position;
                }

                /**
                 * Use the array index as a unique id.
                 * 
                 * @see android.widget.ListAdapter#getItemId(int)
                 */
                public long getItemId(int position) 
                {
                        return position;
                }

                /**
                 * @see android.widget.ListAdapter#getView(int, android.view.View,
                 *      android.view.ViewGroup)
                 */
                public View getView(int position, View convertView, ViewGroup parent) 
                {
                        ViewHolder holder;


                        /* View not yet created? */
                        if(convertView == null) 
                        {
                                convertView = _i.inflate(R.layout.list_item, null);

                                /* create new ViewHolder with necessary stuff */
                                holder = new ViewHolder();
                                holder.name = (TextView) convertView.findViewById(R.id.list_person);
                                holder.msg = (TextView) convertView.findViewById(R.id.list_message);
                                holder.img = (ImageView) convertView.findViewById(R.id.list_image);
                                convertView.setTag(holder);
                        }
                        else 
                        {
                                /* recycle previously created view-holder */
                                holder = (ViewHolder) convertView.getTag();
                        }

                        /* get birthday */
                        Birthdays.Birthday bday = b.get(position);

                        /* set content of entry */
                        holder.name.setText(bday.personName);

						/* strip year on birthdays without valid year */
						String date;
						if(bday.hasYear)
						{
								date = _df.format(bday.date);
						}
						else
						{			
								SimpleDateFormat df = (SimpleDateFormat) _df.clone();
								String p = df.toLocalizedPattern();
								p = p.replaceAll("\\W?[Yy]+\\W?", "");
								df = new SimpleDateFormat(p);
								date = df.format(bday.date);
						}						
						
						holder.msg.setText(
                              bday.getMessage()+
						      " ("+date+")"+
                              bday.getLeapYearMessage());
                        holder.img.setImageBitmap(bday.photo);
                        
                        return convertView;
                }

                /* ViewHolder class to hold views of one list-entry */
                class ViewHolder 
                {
                        TextView name;
                        TextView msg;
                        ImageView img;
                }
        }
        

        /** options menu */
        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.birthdroid, menu);
                return super.onCreateOptionsMenu(menu);
        }

        
        /** item from options menu selected */
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
                switch (item.getItemId())
                {
                        /** About */
                        case R.id.about:
                        {
                                startActivity(new Intent(this, AboutActivity.class));
                                break;
                        }
                                
                        /** Settings */
                        case R.id.settings:
                        {
                                startActivity(new Intent(this, PreferencesActivity.class));
                                break;
                        }

                        /** wtf? */
                        default:
                        {
                                Log.w(TAG, "Unhandled menu-item. This is a bug!");
                                break;
                        }
                }

                return super.onOptionsItemSelected(item);
        }

}
