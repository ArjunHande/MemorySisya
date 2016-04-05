package com.arjun.app.memorysisya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arjun.app.memorysisya.MainActivity.PlaceholderFragment;
import com.arjun.app.memorysisya.MainActivity.PlaceholderFragment.IButtonListener;

public class AppListAdapter extends BaseAdapter implements IButtonListener
{
	private Context mContext = null;
	private int mCount;
	private HashMap<String,String> mRunningProcessesMap;
	private static ArrayList<String> mRunningProcesses = null;
	private static ArrayList<String> mRunningProcessesName = null;
	protected SparseBooleanArray mCheckStates;
	private ArrayList<AppData> mAppData;
	protected static ArrayList<String> processNames = null;

	public AppListAdapter(Context context, ArrayList<AppData> appData)
	{
		mContext = context;
		mAppData = appData;
		mCount = appData.size();
		intialize();
	}

	private void intialize()
	{
//		mRunningProcessesMap = runningProcessesMap;
		mCheckStates = new SparseBooleanArray(mCount);
		processNames = new ArrayList<String>(mCount);
		PlaceholderFragment.setButtonListener(this);
//		mRunningProcesses = new ArrayList<String>();
//		mRunningProcessesName = new ArrayList<String>();
//		for (String key : mRunningProcessesMap.keySet())
//		{
//			mRunningProcesses.add(key);
//			mRunningProcessesName.add(mRunningProcessesMap.get(key));
//		}
	}

	@Override
	public int getCount()
	{
		return mCount;
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
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(R.layout.row_layout, null);

			holder = new ViewHolder();

			holder.appNameText = (TextView) convertView.findViewById(R.id.textView1);
			// mHolder.mImage=(ImageView)
			// convertView.findViewById(R.id.appIcon);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.memorySizeText = (TextView) convertView.findViewById(R.id.memory_text);

			convertView.setTag(holder);
			// configure view holder
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

//		final ActivityManager.RunningAppProcessInfo info = mPids.get(position);
//		// Log.d(TAG, "Running Process " + info.processName);
//		PackageManager packageManager = mContext.getPackageManager();
//		ApplicationInfo applicationInfo = null;
//		try
//		{
//			applicationInfo = packageManager.getApplicationInfo(info.processName, 0);
//		}
//		catch (final NameNotFoundException e)
//		{
//		}
//		final String title = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : null);
		
		holder.appNameText.setText(mAppData.get(position).appName);
		holder.memorySizeText.setText("" + String.format("%.1f", (float)mAppData.get(position).size.getTotalPss()/1024) + " MB");

		holder.mCheckBox.setTag(new Integer(position));
		holder.mCheckBox.setOnCheckedChangeListener(null);
		holder.mCheckBox.setChecked(mCheckStates.get(position, false));

		holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				mCheckStates.put((Integer) buttonView.getTag(), isChecked);
				if(isChecked)
					processNames.add(mAppData.get(position).packageName);
				else
					processNames.remove(mAppData.get(position).packageName);
			}
		});

//		convertView.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View arg0)
//			{
//				if (holder.mCheckBox.isChecked())
//					holder.mCheckBox.setChecked(false);
//				else
//					holder.mCheckBox.setChecked(true);
//			}
//		});

		return convertView;
	}

	private class ViewHolder
	{
		private TextView appNameText;
		public TextView memorySizeText;
		private CheckBox mCheckBox;
		private ImageView mImage;
	}

	@Override
	public void stopButtonClickCallback()
	{
//		Toast.makeText(mContext, processNames.size() + " stopButtonClickCallback", Toast.LENGTH_SHORT).show();
		Intent serviceIntent = new Intent(mContext, BackgroundService.class);
		serviceIntent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", processNames);
		mContext.startService(serviceIntent);
//		Intent intent = new Intent("com.arjun.app.memorysisya.stop_processes");
//		intent.putStringArrayListExtra("com.arjun.app.memorysisya.kill_list", (ArrayList<String>) processNames);
//		mContext.sendBroadcast(intent);
	}

	public List<String> getSelectedProcesses()
	{
		return processNames;
	}

}
