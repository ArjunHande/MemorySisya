package com.arjun.app.memorysisya;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingsActivity extends Activity implements TimePickerDialog.OnTimeSetListener
{

	private ListView settingsList;
	private BaseAdapter settingsAdapter;
	private TimePickerDialog.OnTimeSetListener timeSetListener;
	SettingsActivity settingsActivity;
	private Intent intent = null;
	private PendingIntent pendingIntent = null;
	public static int mHourOfDay = 0;
	public static int mMinute = 30;
	private static long mLastSetMillis = 1800000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		final ActionBar actionBar = getActionBar();
		actionBar.setTitle("Settings");

		timeSetListener = this;
		settingsActivity = this;
		intent = new Intent(getApplicationContext(), BackgroundService.class);
		pendingIntent  = PendingIntent.getService(getApplicationContext(), 10001, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		settingsList = (ListView) findViewById(R.id.items_list);
		settingsAdapter = new SettingListAdapter(this);
		settingsList.setAdapter(settingsAdapter);

		settingsList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (position == 0)
				{
					CheckBox box = (CheckBox) view.findViewById(R.id.checkBox1);
					if (box.isChecked())
						box.setChecked(false);
					else
						box.setChecked(true);
				}
				else if (position == 1)
				{
					TimePickerDialog picker = new TimePickerDialog(settingsActivity, timeSetListener, mHourOfDay, mMinute, true);
					picker.show();
				}
				else if (position == 2)
				{
					startActivityForResult(new Intent(getBaseContext(), SaveListActivity.class), 10002);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
			intent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", data.getStringArrayListExtra("save_list"));
			final PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 10001, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			setAlarmForService(pendingIntent, true, mLastSetMillis);
			writeToDatabase(data.getStringArrayListExtra("save_list"));
		}
	}

	private void writeToDatabase(ArrayList<String> stringArrayListExtra)
	{
		DatabaseManager manager = DatabaseManager.getInstance();
		SQLiteDatabase db = manager.getDatabase();
		for(int i = 0; i < stringArrayListExtra.size(); i++)
		{
			ContentValues values = new ContentValues();
			values.put("package_name", stringArrayListExtra.get(i));
			db.insert(DatabaseHelper.TABLE_NAME, null, values);
		}
	}

	public class SettingListAdapter extends BaseAdapter
	{
		private Context mContext = null;

		public SettingListAdapter(Context context)
		{
			mContext = context;
		}

		@Override
		public int getCount()
		{
			return 3;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			final ViewHolder holder;
			// reuse views
			if (convertView == null)
			{
				convertView = getLayoutInflater().inflate(R.layout.row_layout, null);

				holder = new ViewHolder();

				holder.settingNameText = (TextView) convertView.findViewById(R.id.textView1);
				// mHolder.mImage=(ImageView)
				// convertView.findViewById(R.id.appIcon);
				holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.findViewById(R.id.memory_text).setVisibility(View.GONE);

				convertView.setTag(holder);
				// configure view holder
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.settingNameText.setText(getTitle(position));
			holder.settingNameText.setTag(new Integer(position));
			holder.mCheckBox.setVisibility(getVisibility(position));

			SharedPreferences preferences = getSharedPreferences("com.arjun.app.memorysisya_shared_prefs", MODE_PRIVATE);
			if(holder.settingNameText.getTag().equals(new Integer(1)) || holder.settingNameText.getTag().equals(new Integer(2)))
			{
				if (!preferences.getBoolean("isPersistentService", false))
				{
					convertView.setEnabled(false);
					convertView.setClickable(false);
					holder.settingNameText.setActivated(false);
				}
				else
				{
					convertView.setEnabled(true);
					convertView.setClickable(true);
					holder.settingNameText.setActivated(true);
				}
			}

			if (holder.mCheckBox.getVisibility() == View.VISIBLE)
			{
				holder.mCheckBox.setTag(new Integer(position));
				holder.mCheckBox.setOnCheckedChangeListener(null);
				// holder.mCheckBox.setChecked(mCheckStates.get(position,
				// false));
				if(preferences.getBoolean("isPersistentService", false))
					holder.mCheckBox.setChecked(true);

				holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{

					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						// mCheckStates.put((Integer) buttonView.getTag(),
						// isChecked);
						SharedPreferences preferences = getSharedPreferences("com.arjun.app.memorysisya_shared_prefs", MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						if (isChecked)
						{
							editor.putBoolean("isPersistentService", true);
							editor.commit();
							notifyDataSetChanged();
							setAlarmForService(pendingIntent, isChecked, mLastSetMillis);
						}
						else
						{
							editor.putBoolean("isPersistentService", false);
							editor.commit();
							notifyDataSetChanged();
							setAlarmForService(pendingIntent, isChecked, -1);
						}
					}
				});
			}

//			convertView.setOnClickListener(new OnClickListener()
//			{
//
//				@Override
//				public void onClick(View arg0)
//				{
//					if (position == 0)
//					{
//						if (holder.mCheckBox.isChecked())
//							holder.mCheckBox.setChecked(false);
//						else
//							holder.mCheckBox.setChecked(true);
//					}
//					else if (position == 1)
//					{
//						TimePickerDialog picker = new TimePickerDialog(mContext, timeSetListener, mHourOfDay, mMinute, true);
//						picker.show();
//					}
//					else if (position == 2)
//					{
//						startActivityForResult(new Intent(mContext, SaveListActivity.class), 10002);
//					}
//				}
//			});

			return convertView;
		}

		private int getVisibility(int position)
		{
			switch (position)
			{
				case 0 :
					return View.VISIBLE;
				case 1 :
					return View.INVISIBLE;
				case 2 :
					return View.INVISIBLE;
				default :
					break;
			}
			return View.INVISIBLE;
		}

		private String getTitle(int position)
		{
			switch (position)
			{
				case 0 :
					return "Run service in the background";
				case 1 :
					return "Force-Stop interval";
				case 2 :
					return "Select Apps to be stopped";
				default :
					break;
			}
			return null;
		}

		private class ViewHolder
		{
			private TextView settingNameText;
			private CheckBox mCheckBox;
		}

	}

	@Override
	public void onTimeSet(TimePicker arg0, int hourOfDay, int minute)
	{
		mHourOfDay = hourOfDay;
		mMinute = minute;
		mLastSetMillis = (hourOfDay * 60) + (minute * 60 * 1000);

		SharedPreferences preferences = getSharedPreferences("com.arjun.app.memorysisya_shared_prefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong("runningInterval", mLastSetMillis);
		editor.commit();

		setAlarmForService(pendingIntent, false, -1);
		setAlarmForService(pendingIntent, true, mLastSetMillis);
	}

	private void setAlarmForService(PendingIntent pendingIntent, boolean isSet, long lastSetMillis)
	{
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		if (isSet)
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), lastSetMillis,
					pendingIntent);
		else
			alarmManager.cancel(pendingIntent);
	}

}
