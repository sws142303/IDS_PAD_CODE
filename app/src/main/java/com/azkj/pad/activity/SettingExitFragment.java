package com.azkj.pad.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.azkj.pad.utility.GlobalConstant;
import com.juphoon.lemon.ui.MtcDelegate;

import cn.sword.SDK.MediaEngine;


/*配置-安全退出*/
public class SettingExitFragment {
	// 父窗体
	private  Activity mActivity;
	// 全局变量保存号码
	private PTT_3G_PadApplication ptt_3g_PadApplication;
	private SharedPreferences prefs;

	public SettingExitFragment(Activity activity){
		mActivity = activity;
		ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
	}

	// 加载数据供外部调用
	public boolean loadExitView(){
		new AsyncTaskLocal().execute();
		//new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
		return true;
	}

	// 异步初始化数据信息
	class AsyncTaskLocal extends AsyncTask<Object, Object, Object>{
		@Override     
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setViewValue();
		}
	}
	private void setViewValue() {
		// 取得控件
		final Button bt_exit = (Button)mActivity.findViewById(R.id.bt_exit);
		// 设置字体
		//bt_exit.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		// 设置侦听事件
		bt_exit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				bt_exit.setEnabled(false);
				exit();
			}
		});
	}



	//全局退出操作----都是直接调用该方法
	public void exit(){

//		// 清空对讲组
//		GroupManager.getInstance().clear();
//		//通话挂断
//		for (CallingInfo callinginfo : CallingManager.getInstance()
//				.getCallingData())
//		{
//			// 终止通话
//			Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
//			callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
//			mActivity.sendBroadcast(callIntent);
//		}

		// 登出
		//mActivity.stopService(new Intent(mActivity,PTTService.class));

		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(GlobalConstant.isFileUpLoad,false);
		edit.commit();

		MediaEngine.GetInstance().ME_UnRegist();  //注销账户

		MediaEngine.GetInstance().ME_Destroy();

		MtcDelegate.logout();

		Log.e("开始结束", "===============");
		mActivity.finish();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}
}
