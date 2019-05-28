package com.azkj.pad.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.PositionLocalListAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.sword.SDK.MediaEngine;


/*定位-本地联系人*/
public class PositionListFragment {
	// 父窗体
	private Activity mActivity;
    // 联系人图片
	 ArrayList<Contacts> mContacts;
    private ListView mContactListView = null;	
    private PositionLocalListAdapter adapter = null;
    private PTT_3G_PadApplication ptt_3g_PadApplication = null;
    private SipUser sipUser;
    private SharedPreferences prefs;
	public PositionListFragment(Activity activity){
		mContacts = new ArrayList<Contacts>();
    	mActivity = activity;
    	ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
    	// 全局变量定义
    	prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
    	sipUser= CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    }
    
    // 加载数据供外部调用
	public boolean  loadLocalListView(List<Contacts> contacts){

		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}

		// 添加本地联系人
		if (mContacts.size() > 0){
			mContacts.clear();
		}
		mContacts.addAll(contacts);
		// 异步加载
		//new AsyncTaskLocal().execute();
		new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				setListViewByLocal(mContacts);
//			}
//		}).start();
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
			//getPhoneContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			Log.e("=================", "集合长度"+mContacts.size());
			setListViewByLocal(mContacts);
		}
    }
    
    // 设置列表显示本地联系人
    private void setListViewByLocal(List<Contacts> contacts) {
		mContactListView = (ListView) mActivity.findViewById(R.id.position_local_listview);	
        adapter = new PositionLocalListAdapter(mActivity, contacts, mContactListView);
        if (mContactListView == null||adapter==null){
        	return;
        }
        mContactListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
		mContactListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Contacts c = mContacts.get(position);
				
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(mActivity,
							mActivity.getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(c==null){
					Toast.makeText(mActivity, "找不到联系人", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ImageView addorcutView = (ImageView)arg1.findViewById(R.id.iv_addorcut);
				if(addorcutView!=null){
					if(!c.isPos()){
						//修改页面定位状态
						addorcutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.location_status));
						//发送定位请求
						String uri=sipUser.getUsername();//MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
						String  dstid=c.getBuddyNo();
						String msgBodyString="req:cur_pos\r\nemployeeid:"+sipUser.getUsername()+"\r\ndstid:"+dstid+"\r\n";
						MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
						Log.e("====Sws测试定位","点击Item  发送消息   msgBody:"+msgBodyString);
						/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
						MtcDelegate.log("定位本地列表发送uri"+uri+"  消息:"+msgBodyString);*/
						c.setPos(true);
					}else{
						addorcutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.meeting_null));
						c.setPos(false);
					}
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
        adapter.notifyDataSetChanged();
	}
}
