package com.arjun.app.memorysisya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionBar.TabListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_HOME);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		intialize();

//		setAlarmForService(intent);
		// try
		// {
		// Process p = Runtime.getRuntime().exec("su");
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}

//		startService(new Intent(this, BackgroundService.class));
	}

	private void intialize()
	{
		SharedPreferences preferences = getSharedPreferences("com.arjun.app.memorysisya_shared_prefs", MODE_PRIVATE);
//		if(!preferences.getBoolean("isInitialized", false))
//		{
			DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
			DatabaseManager.initializeInstance(helper);
			DatabaseManager manager = DatabaseManager.getInstance();
			manager.getDatabase();

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isInitialized", true);
			editor.commit();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	private void setAlarmForService(Intent intent)
	{
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 10001, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1800000, 1800000, pendingIntent);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount()
		{
			// Show 1 total pages.
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0 :
					return getString(R.string.title_section1).toUpperCase(l);
				case 1 :
					return getString(R.string.title_section2).toUpperCase(l);
				case 2 :
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		public interface IButtonListener
		{
			public void stopButtonClickCallback();
		}

		private static IButtonListener mButtonListener;

		public static void setButtonListener(IButtonListener buttonListener)
		{
			mButtonListener = buttonListener;
		}

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber)
		{
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		private ListView appList;
		private AppListAdapter adapter;
		ProgressDialog mProgressDialog;
		private TextView selectedText;
		public ArrayList<AppData> mRunningAppData;

		public PlaceholderFragment()
		{
			mRunningAppData = new ArrayList<AppData>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			appList = (ListView) rootView.findViewById(R.id.app_list);
			selectedText = (TextView) rootView.findViewById(R.id.selection_number);
			mProgressDialog = new ProgressDialog(getActivity());

			getActivity().registerReceiver(mReceiver, new IntentFilter("com.arjun.app.memorysisya.stop_processes"));
			mProgressDialog.setTitle("Loading...");

			new GetRunningProcessesTask().execute();


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
					selectedText.setText(AppListAdapter.processNames.size() + "/" + mRunningAppData.size());
				}
			});

			rootView.findViewById(R.id.kill_button).setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					mProgressDialog.show();
					mButtonListener.stopButtonClickCallback();
				}
			});

			rootView.findViewById(R.id.save_button).setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
//					stopList = AppListAdapter.getSelectedProcesses();
				}
			});
			return rootView;
		}

		public class GetRunningProcessesTask extends AsyncTask<Void, Void, Void>
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
				mRunningAppData = getRunningProcesses();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);
				mProgressDialog.dismiss();
				adapter = new AppListAdapter(getActivity(), mRunningAppData);
				appList.setAdapter(adapter);

				selectedText.setText("0/" + mRunningAppData.size());
			}

		}

		private ArrayList<AppData> getRunningProcesses()
		{
			ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> pIds = am.getRunningAppProcesses();
			List<RunningServiceInfo> runningServiceList = am.getRunningServices(Integer.MAX_VALUE);
			List<String> systemProcessList = new ArrayList<String>();
			for(RunningServiceInfo runningServiceInfo : runningServiceList)
			{
				if(runningServiceInfo.flags == RunningServiceInfo.FLAG_SYSTEM_PROCESS)
					systemProcessList.add(runningServiceInfo.process);
			}
			
//			HashMap<String, String> runningAppsMap = new HashMap<String, String>();
			ArrayList<AppData> appDataList = new ArrayList<AppData>();
			PackageManager packageManager = getActivity().getPackageManager();
			for (int i = 0; i < pIds.size(); i++)
			{
				ActivityManager.RunningAppProcessInfo info = pIds.get(i);
				if(systemProcessList.contains(info.processName) || info.processName.startsWith(getActivity().getPackageName()))
					continue;
				// Log.d(TAG, "Running Process " + info.processName);
				ApplicationInfo applicationInfo = null;
				AppData appData = new AppData();
				try
				{
					applicationInfo = packageManager.getApplicationInfo(info.processName, 0);
				}
				catch (final NameNotFoundException e)
				{
				}
				final String title = (String) ((applicationInfo != null) ? packageManager
						.getApplicationLabel(applicationInfo) : info.processName);
//				runningAppsMap.put(info.processName, title);
				appData.appName = title;
				appData.packageName = info.processName;
				appData.size = am.getProcessMemoryInfo(new int[]{info.pid})[0];
				appDataList.add(appData);
			}
			return appDataList;
		}

		//TODO: USe to refresh after stop.
		private BroadcastReceiver mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				if(intent != null && "com.arjun.app.memorysisya.stop_processes".equals(intent.getAction()))
				{
					new GetRunningProcessesTask().execute();
//					adapter = new AppListAdapter(getActivity(), getRunningProcesses());
//					appList.setAdapter(adapter);
//					progressDialog.dismiss();
//					Toast.makeText(context, intent.getStringArrayListExtra("com.arjun.app.memorysisya.kill_list").size() + " onReceive", Toast.LENGTH_SHORT).show();
//					Intent serviceIntent = new Intent(context, BackgroundService.class);
//					serviceIntent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", intent.getStringArrayListExtra("com.arjun.app.memorysisya.kill_list"));
//					context.startService(serviceIntent);
				}
			}
		};

	}

}
