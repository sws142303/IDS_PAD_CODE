package com.azkj.pad.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;


/*配置-调度账号*/
public class SettingDispatcherFragment {
	// 父窗体
	private Activity mActivity;
	// 全局变量定义
	private SharedPreferences prefs;
	
	public SettingDispatcherFragment(Activity activity){
    	mActivity = activity;
		// 全局变量定义
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);

		new AsyncTaskLocal().execute();
		//new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
    }
    
    // 加载数据供外部调用
	public void loadDispatcherView(){
		//new AsyncTaskLocal().execute();
		new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
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
    	TextView tv_username = (TextView)mActivity.findViewById(R.id.tv_username);
    	TextView tv_usernamevl = (TextView)mActivity.findViewById(R.id.tv_usernamevl);
    	TextView tv_password = (TextView)mActivity.findViewById(R.id.tv_password);
    	TextView tv_passwordvl = (TextView)mActivity.findViewById(R.id.tv_passwordvl);
    	TextView tv_serverip = (TextView)mActivity.findViewById(R.id.tv_serverip);
    	TextView tv_serveripvl = (TextView)mActivity.findViewById(R.id.tv_serveripvl);
    	TextView tv_port = (TextView)mActivity.findViewById(R.id.tv_port);
    	TextView tv_portvl = (TextView)mActivity.findViewById(R.id.tv_portvl);
    	// 设置字体
    	tv_title.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_username.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_usernamevl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_password.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_passwordvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_serverip.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_serveripvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_port.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_portvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	// 取得用户及服务器信息
		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		tv_usernamevl.setText(sipUser.getUsername());
		tv_passwordvl.setText("●●●●●●");
		tv_serveripvl.setText(sipServer.getServerIp());
		tv_portvl.setText(sipServer.getPort());
    }

}
