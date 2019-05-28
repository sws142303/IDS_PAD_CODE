package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.MonitorListAdapter;

import java.util.ArrayList;
import java.util.List;

/*监控列表显示页面 */
public class MonitorListFragment {
	// 父窗体
	private Activity mActivity;
	private List<MonitorInfo> mMonitorInfo = new ArrayList<MonitorInfo>();
    private ListView mVideoUserListView = null;	
    private MonitorListAdapter adapter = null;
    private PTT_3G_PadApplication ptt_3g_PadApplication = null;

	public MonitorListFragment(Activity activity){
    	mActivity = activity;
    	ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
	}
    
    // 加载数据供外部调用
	public boolean loadListView(){

		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}

		//new AsyncTaskLocal().execute();
		new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);

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
			/*getPhoneContacts();*/
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setListView();
		}
    }
    
    private void setListView() {
    	mVideoUserListView = (ListView)mActivity.findViewById(R.id.videouser_listview);	
        adapter = new MonitorListAdapter(mActivity, mMonitorInfo, mVideoUserListView);
        if  (mVideoUserListView == null){
        	return;
        }
        mVideoUserListView.setAdapter(adapter);
        mVideoUserListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {Log.e("监控MonitorListFragment", "进入监控显示");
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(mActivity,
							mActivity.getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 刷新选中
				MonitorInfo monitorinfo = (MonitorInfo)mMonitorInfo.get(position);
				if (monitorinfo != null){
					//Log.e("端口号", ""+monitorinfo.getVedioPort());
					// 添加监控信息
					Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_MEMBER);
					intent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
					mActivity.sendBroadcast(intent);
					Log.e("获取监控发送请求广播 MonitorListFragment", "获取监控发送请求广播 MonitorListFragment:"+monitorinfo.getId());
				}
			}});
    }
    
	// 刷新联系人列表
	public void refreshUsersList(ArrayList<MonitorInfo> monitorinfos){
		mMonitorInfo.clear();
		mMonitorInfo.addAll(monitorinfos);
		if (mVideoUserListView == null){
        	return;
        }
        mVideoUserListView.setAdapter(adapter);
	}
}
