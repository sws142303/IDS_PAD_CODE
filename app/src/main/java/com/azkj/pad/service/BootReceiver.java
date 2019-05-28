package com.azkj.pad.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.azkj.pad.activity.LoginActivity;
import com.azkj.pad.utility.GlobalConstant;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED"; 

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean auto = prefs.getBoolean(GlobalConstant.SP_AUTO_START, true);
		if (intent.getAction().equals(action_boot)) {
		    if(auto == true) {
				if (!isAppOnForeground(context)){
					Intent ootStartIntent=new Intent(context, LoginActivity.class);
					ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("AUTOLOGIN", true);
					context.startActivity(ootStartIntent);
				}
		    }
		    else {
		    	return;
		    }
		}
	}
	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
}
