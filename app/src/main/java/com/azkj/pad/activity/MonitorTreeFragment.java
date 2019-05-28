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
import com.azkj.pad.utility.MonitorTreeListAdapter;

import java.util.ArrayList;
import java.util.List;

/*监控树状显示页面 */
public class MonitorTreeFragment {
	// 父窗体
	private Activity mActivity;
	// 绑定数据
	private List<MonitorInfo> mMonitorInfo = new ArrayList<MonitorInfo>();
	/*// 可展开视图
	private ExpandableListView expandableListView;
	// 可展开适配器
	private MonitorExpandableAdapter adapter;*/
	
	// 列表视图
	private ListView mVideoUserListView = null;	
	// 列表适配器
	private MonitorTreeListAdapter treeAdapter;
	// 是否已加载
	private boolean isloaded = false;
	private PTT_3G_PadApplication ptt_3g_PadApplication = null;

	public MonitorTreeFragment(Activity activity){
    	mActivity = activity;
    	ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
	}
    
    // 加载数据供外部调用
	public boolean loadTreeView(List<MonitorInfo> monitorinfos){

		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}

		// 添加系统用户
		mMonitorInfo.clear();
		mMonitorInfo.addAll(monitorinfos);
		
		//new AsyncTaskLocal().execute();
		new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
		isloaded = true;
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
			setTreeView(mMonitorInfo);
		}
    }
    
    private void setTreeView(List<MonitorInfo> monitorinfos) {
		// 取得控件
		/*expandableListView = (ExpandableListView) mActivity.findViewById(R.id.videouser_treeview);*/
		mVideoUserListView = (ListView) mActivity.findViewById(R.id.videouser_treeview);

		/*// 初始化适配器
		adapter = new MonitorExpandableAdapter(mActivity, monitorinfos);
		expandableListView.setAdapter(adapter);
		expandableListView.setGroupIndicator(null);//取消默认箭头
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener(){

			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < adapter.getGroupCount(); i++) {
					if (groupPosition != i && expandableListView.isGroupExpanded(groupPosition)) {
						expandableListView.collapseGroup(i);
					}
				}
			}});
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Toast.makeText(mActivity, "点击"+adapter.getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
				MonitorInfo monitorinfo = (MonitorInfo)adapter.getChild(groupPosition, childPosition);
				if (monitorinfo != null){
					// 添加监控信息
					Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_MEMBER);
					intent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
					mActivity.sendBroadcast(intent);
				}
				return false;
			}
		});*/
		
		// 
		treeAdapter = new MonitorTreeListAdapter(mActivity, mMonitorInfo);
		if (mVideoUserListView == null){
        	return;
        }
		mVideoUserListView.setAdapter(treeAdapter);
		mVideoUserListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(mActivity,
							mActivity.getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				// 设置展开
				((MonitorTreeListAdapter) parent.getAdapter()).OnListItemClick(position);
				// 取得选择值
				MonitorInfo monitorinfo = (MonitorInfo)((MonitorTreeListAdapter) parent.getAdapter()).getItem(position);
				if ((monitorinfo == null)
					|| (!monitorinfo.isIsuser())){
					return;
				}
				// 添加监控信息
				Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_MEMBER);
				intent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
				mActivity.sendBroadcast(intent);
				Log.e("获取监控发送请求广播 MonitorTreeFragment", "获取监控发送请求广播 MonitorTreeFragment:"+monitorinfo.getId());
			}});
    }
    
	// 刷新联系人列表
	public void refreshUsersList(ArrayList<MonitorInfo> monitorinfos){
		if (!isloaded){
			return;
		}
		// 添加系统用户
		mMonitorInfo.clear();
		mMonitorInfo.addAll(monitorinfos);
		if (mVideoUserListView == null){
        	return;
        }
		mVideoUserListView.setAdapter(treeAdapter);
	}
}
