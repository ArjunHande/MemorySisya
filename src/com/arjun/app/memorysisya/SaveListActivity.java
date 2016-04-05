package com.arjun.app.memorysisya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SaveListActivity extends Activity
{

	SaveListActivity mSaveListActivity;
	private AppListAdapter adapter;
	private ListView appList;
	private TextView selectedText;
	private ProgressDialog mProgressDialog;
	private ArrayList<AppData> mAllProcessesMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_list);

		mSaveListActivity = this;
		appList = (ListView) findViewById(R.id.app_save_list);
		selectedText = (TextView) findViewById(R.id.save_selection_number);
		mProgressDialog = new ProgressDialog(this);

		mProgressDialog.setTitle("Loading...");

		new GetAllProcessesTask().execute();

		appList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				CheckBox box = (CheckBox) view.findViewById(R.id.checkBox1);
				if (box.isChecked())
					box.setChecked(false);
				else
					box.setChecked(true);
				selectedText.setText(AppListAdapter.processNames.size() + "/" + mAllProcessesMap.size());
			}
		});

		findViewById(R.id.save_button).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				Intent resultIntent = new Intent();
				resultIntent.putStringArrayListExtra("save_list", (ArrayList<String>) adapter.getSelectedProcesses());
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}

	public class GetAllProcessesTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			mAllProcessesMap = InstalledApps();

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			adapter = new AppListAdapter(mSaveListActivity, mAllProcessesMap);
			appList.setAdapter(adapter);

			selectedText.setText("0/" + mAllProcessesMap.size());
			mProgressDialog.dismiss();
		}

	}

	public ArrayList<AppData> InstalledApps()
	{
		PackageManager pm = this.getPackageManager();
		HashMap<String, String> installedAppsMap = new HashMap<String, String>();
		ArrayList<AppData> appDataList = new ArrayList<AppData>();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> PackList = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
		for (ResolveInfo rInfo : PackList)
		{
			if(rInfo.activityInfo.applicationInfo.packageName.startsWith(getPackageName()))
				continue;
			AppData appData = new AppData();
			appData.appName = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
			appData.packageName = rInfo.activityInfo.applicationInfo.packageName;
			appData.size = new MemoryInfo();
			if (installedAppsMap.containsKey(appData.packageName))
			{
				continue;
			}
			installedAppsMap.put(appData.packageName, appData.appName);
			appDataList.add(appData);
		}

		//Get running apps.
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> pIds = am.getRunningAppProcesses();
		PackageManager packageManager = getPackageManager();
		for (int i = 0; i < pIds.size(); i++)
		{
			AppData appData = new AppData();
			ActivityManager.RunningAppProcessInfo info = pIds.get(i);
			if(info.processName.startsWith(getPackageName()))
				continue;
			if (installedAppsMap.containsKey(info.processName))
			{
				continue;
			}
			ApplicationInfo applicationInfo = null;
			try
			{
				applicationInfo = packageManager.getApplicationInfo(info.processName, 0);
			}
			catch (final NameNotFoundException e)
			{
			}
			final String title = (String) ((applicationInfo != null) ? packageManager
					.getApplicationLabel(applicationInfo) : info.processName);
			appData.appName = title;
			appData.packageName = info.processName;
			appData.size = am.getProcessMemoryInfo(new int[]{info.pid})[0];
			installedAppsMap.put(info.processName, title);
			appDataList.add(appData);
		}
		return appDataList;
	}

}
