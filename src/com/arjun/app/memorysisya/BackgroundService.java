package com.arjun.app.memorysisya;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class BackgroundService extends IntentService
{

	private static final String TAG = BackgroundService.class.getCanonicalName();
	private static ArrayList<String> killList = new ArrayList<String>();
	private Process p;

	public BackgroundService()
	{
		super("BackgroundService");
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
//		SharedPreferences preferences = ((Activity) getApplicationContext()).getPreferences(MODE_PRIVATE);
//		if(!preferences.getBoolean("isInitialized", false))
//		{
//			
//		}
		try
		{
			p = Runtime.getRuntime().exec("su");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
//		new CheckRootTask().execute();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
//		killList = new ArrayList<String>();
//		 killList.add("com.facebook.orca");
		// killList.add("com.google.android.gms");
		// killList.add("com.google.android.gms.wearable");
		// killList.add("com.google.process.gapps");
		// killList.add("com.google.process.location");
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
//		Toast.makeText(getApplicationContext(), " onHandleIntent", Toast.LENGTH_SHORT).show();
		//Get current free memory on RAM.
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		long initialFreeMemory = memoryInfo.availMem / 1048576L;

		if (intent != null && intent.getStringArrayListExtra("com.arjun.app.memorysisya.kill_list") != null)
		{
			killList = intent.getStringArrayListExtra("com.arjun.app.memorysisya.kill_list");
			int killCount = 0;
//			Toast.makeText(getApplicationContext(), killList.size() + " stop process onHandleIntent", Toast.LENGTH_SHORT).show();
//			InputStream es = p.getErrorStream();
			DataOutputStream os = new DataOutputStream(p.getOutputStream());

			try
			{
				if(killList.contains(activityManager.getRunningTasks(1).get(0).topActivity.getPackageName()))
					killList.remove(activityManager.getRunningTasks(1).get(0).topActivity.getPackageName());
				for (int i = 0; i < killList.size(); i++)
				{
					// Exclude the process running on top by the user.
					os.writeBytes("am force-stop " + killList.get(i) + "\n");
					killCount++;
					Log.d(TAG, "Killed Process " + killList.get(i));
				}
				os.writeBytes("exit\n");
				os.flush();
				p.waitFor();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			finally
			{
				memoryInfo = new ActivityManager.MemoryInfo();
				activityManager.getMemoryInfo(memoryInfo);
				long finalFreeMemory = memoryInfo.availMem / 1048576L;
				Intent broadcastIntent = new Intent("com.arjun.app.memorysisya.stop_processes");
//				broadcastIntent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", killList);
				getApplicationContext().sendBroadcast(broadcastIntent);
				Intent notificationIntent = new Intent(this, MainActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

				// build notification
				// the addAction re-use the same intent to keep the example short
				Notification n  = new Notification.Builder(this)
				        .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
				        .setContentText(finalFreeMemory - initialFreeMemory + " MB Freed: " + killCount + " Processes stopped")
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentIntent(pIntent)
				        .setAutoCancel(true)
				        /*.addAction(R.drawable.ic_launcher, "Call", pIntent)
				        .addAction(R.drawable.ic_launcher, "More", pIntent)
				        .addAction(R.drawable.ic_launcher, "And more", pIntent)*/.build();


				NotificationManager notificationManager = 
				  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				notificationManager.notify(0, n); 
			}
		}
		intent = null;
		// ActivityManager am = (ActivityManager)
		// getSystemService(Context.ACTIVITY_SERVICE);
		// List<ActivityManager.RunningAppProcessInfo> pids =
		// am.getRunningAppProcesses();

	}
	// android.os.Process.killProcess(pid);
	// am.killBackgroundProcesses(info.processName);
	// android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
	// am.restartPackage(info.processName);
	// android.os.Process.killProcess(pid);

	@Override
	public void onDestroy()
	{

	}

	public class CheckRootTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				p = Runtime.getRuntime().exec("su");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	}

	// ActivityManager actvityManager = (ActivityManager)
	// this.getSystemService(ACTIVITY_SERVICE);
	// List<RunningServiceInfo> serviceInfos =
	// actvityManager.getRunningServices(Integer.MAX_VALUE);
	//
	// for (RunningServiceInfo serviceInfo : serviceInfos)
	// {
	// Log.d(TAG, "Running Process " + serviceInfo.process);
	// if(killList.contains(serviceInfo.process))
	// {
	// actvityManager.killBackgroundProcesses(serviceInfo.process);
	// // stopService(new Intent(getApplicationContext(),
	// serviceInfo.service.toString().getClass()));
	// android.os.Process.killProcess(serviceInfo.pid);
	// Log.d(TAG, "Killed Process " + serviceInfo.process);
	// }
	// }

	// final PackageManager pkgmgr =
	// getApplicationContext().getPackageManager();
	// ApplicationInfo appinfo;
	// try
	// {
	// appinfo = pkgmgr.getApplicationInfo(this.getPackageName(), 0);
	// }
	// catch (final NameNotFoundException e)
	// {
	// appinfo = null;
	// }
	// final String applicationName = (String) (appinfo != null ?
	// pkgmgr.getApplicationLabel(appinfo) : "(unknown)");
}
