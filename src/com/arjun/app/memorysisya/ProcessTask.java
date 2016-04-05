package com.arjun.app.memorysisya;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.AsyncTask;

public class ProcessTask extends AsyncTask<Object, Object, Object>
{

	@Override
	protected Object doInBackground(Object... arg0)
	{
//		Toast.makeText(, text, duration)
		ActivityManager actvityManager = (ActivityManager) .getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = servMng.getRunningAppProcesses();
	    if(list != null){
	     for(int i=0;i<list.size();++i){
	      if("com.android.email".matches(list.get(i).processName)){
	       int pid = android.os.Process.getUidForName("com.android.email");
	             android.os.Process.killProcess(pid);
	      }else{
	       mTextVIew.append(list.get(i).processName + "\n");
	      }
	     }
	    }
		return null;
	}

}
