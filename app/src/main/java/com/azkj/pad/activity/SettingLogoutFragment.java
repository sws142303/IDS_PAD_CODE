package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.juphoon.lemon.ui.MtcDelegate;

import cn.sword.SDK.MediaEngine;

/*配置-注销当前账号*/
public class SettingLogoutFragment {

	// 父窗体
	private Activity mActivity;
	
	public SettingLogoutFragment(Activity activity){
    	mActivity = activity;
    }
    
    // 加载数据供外部调用
	public boolean loadLogoutView(){
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
    	TextView tv_title = (TextView)mActivity.findViewById(R.id.tv_title);
    	Button btn_ok = (Button)mActivity.findViewById(R.id.btn_ok);
    	Button btn_cancel = (Button)mActivity.findViewById(R.id.btn_cancel);
    	// 设置字体
    	tv_title.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	btn_ok.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	btn_cancel.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	// 设置侦听事件
    	btn_ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 清空对讲组
				GroupManager.getInstance().clear();
				//通话挂断
				for (CallingInfo callinginfo : CallingManager.getInstance()
						.getCallingData()) 
				{
					// 终止通话
					Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
					mActivity.sendBroadcast(callIntent);
				}
				// 登出
				MediaEngine.GetInstance().ME_UnRegist();
				MediaEngine.GetInstance().ME_Destroy();
				MtcDelegate.logout();
				mActivity.finish();
			}});
    }
}
