package com.arjun.app.memorysisya;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OnBootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			SharedPreferences preferences = context.getSharedPreferences("com.arjun.app.memorysisya_shared_prefs", Activity.MODE_PRIVATE);
			if (preferences.getBoolean("isPersistentService", false))
			{
				long interval = preferences.getLong("runningInterval", 1800000);
				Intent serviceIntent = new Intent(context, BackgroundService.class);
				serviceIntent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", readFromDatabase());
				PendingIntent pendingIntent = PendingIntent.getService(context, 10001, serviceIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent);
			}
		}
	}

	private ArrayList<String> readFromDatabase()
	{
		DatabaseManager manager = DatabaseManager.getInstance();
		SQLiteDatabase db = manager.getDatabase();
		ArrayList<String> stringArrayListExtra = new ArrayList<String>();
		Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
		if(cursor != null && cursor.moveToFirst())
		{
			do
			{
				stringArrayListExtra.add(cursor.getString(cursor.getColumnIndex("package_name")));
			}while(cursor.moveToNext());
		}
		return stringArrayListExtra;
	}

}
