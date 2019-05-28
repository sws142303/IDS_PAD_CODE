package com.azkj.pad.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.MeetingLocalListAdapter;
import com.azkj.pad.activity.R;

/*会议-本地联系人*/
public class MeetingLocalFragment {
	// 父窗体
	private Activity mActivity;
    // 联系人图片
    private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();
    private ListView mContactListView = null;	
    private MeetingLocalListAdapter adapter = null;
	
	public MeetingLocalFragment(Activity activity){
    	mActivity = activity;
    }
    
    // 加载数据供外部调用
	public void loadLocalListView(List<Contacts> contacts){
		// 添加本地联系人
		mContacts.clear();
		mContacts.addAll(contacts);
		// 异步加载
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
			/*getPhoneContacts();*/
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setListViewByLocal();
		}
    }
    
    // 设置列表显示本地联系人
    private void setListViewByLocal() {
		mContactListView = (ListView)mActivity.findViewById(R.id.meeting_local_listview);	
        adapter = new MeetingLocalListAdapter(mActivity, mContacts, mContactListView);
        if (mContactListView == null){
        	return;
        }
        mContactListView.setAdapter(adapter);
		mContactListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// 刷新选中
				Contacts contact = (Contacts)mContacts.get(position);
				if (!contact.isInmeeting()){
					// 设置会议状态
					contact.setInmeeting(true);
					// 刷新列表
					mContactListView.setAdapter(adapter);
					// 添加会议人员
					Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBER);
					intent.putExtra(GlobalConstant.KEY_MEETING_INORDE, true);
					intent.putExtra(GlobalConstant.KEY_MEETING_MEMBERINFO, contact);
					mActivity.sendBroadcast(intent);
				}
			}});
	}
	
	// 刷新联系人列表
	public void refreshContactsList(List<Contacts> contacts){
		// 添加系统用户
		mContacts.clear();
		mContacts.addAll(contacts);
		if (mContactListView == null){
        	return;
        }
        mContactListView.setAdapter(adapter);
	}
}
