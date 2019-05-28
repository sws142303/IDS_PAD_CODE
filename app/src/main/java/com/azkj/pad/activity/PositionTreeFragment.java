package com.azkj.pad.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.PositionTreeExpandableAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.sword.SDK.MediaEngine;


/*定位-定位联系人*/
public class PositionTreeFragment {

	private SipUser sipUser;
	private SharedPreferences prefs;
	// 父窗体
	private Activity mActivity;
	// 可展开视图
	private ExpandableListView mContactExpandableView;
	// 可展开适配器
	private PositionTreeExpandableAdapter mContactAdapter = null;
	// 绑定数据
	private ArrayList<GroupInfo> mGroups = new ArrayList<GroupInfo>();
    // 此页面是否已加载
	private boolean isloaded = false;
	private PTT_3G_PadApplication ptt_3g_PadApplication = null;
	
	public PositionTreeFragment(Activity activity){
    	mActivity = activity;
    	ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser= CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    }
    
    // 加载数据供外部调用
	public boolean loadSystemListView(List<GroupInfo> groups){

		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}

		// 添加对讲组
		mGroups.clear();
		mGroups.addAll(groups);
		// 异步加载
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
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			InitData();
		}
    }
	
	// 初始化数据
	private void InitData(){
		// 取得控件
		mContactExpandableView = (ExpandableListView)this.mActivity.findViewById(R.id.position_system_expandableview);
		// 设置适配器
		mContactAdapter = new PositionTreeExpandableAdapter(this.mActivity, this.mGroups);
		if (mContactExpandableView == null){
			return;
		}
		mContactExpandableView.setAdapter(mContactAdapter);
		mContactExpandableView.setGroupIndicator(null);//取消默认箭头
		mContactExpandableView.setOnGroupExpandListener(new OnGroupExpandListener(){

			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < mContactAdapter.getGroupCount(); i++) {
					if (groupPosition != i && mContactExpandableView.isGroupExpanded(groupPosition)) {
						mContactExpandableView.collapseGroup(i);
					}
				}
			}});
		mContactExpandableView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(mActivity,
							mActivity.getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return false;
				}
				
				MemberInfo memberinfo = (MemberInfo)mContactAdapter.getChild(groupPosition, childPosition);
				if (memberinfo != null){
					ImageView addorcutView = (ImageView)v.findViewById(R.id.iv_addorcut);
					if(addorcutView!=null){
						if(!memberinfo.isPos()){
							addorcutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.location_status));
							
							String uri=sipUser.getUsername();//MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
							String  dstid=memberinfo.getNumber();
							String msgBodyString="req:cur_pos\r\nemployeeid:"+sipUser.getUsername()+"\r\ndstid:"+dstid+"\r\n";
							//MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
							MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
							Log.e("","定位列表发送uri"+uri+"  消息:"+msgBodyString);
							
							memberinfo.setPos(true);
						}else{
							addorcutView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.meeting_null));
							memberinfo.setPos(false);
						}
					}
					
					
					return true;
				}
				return false;
			}});
	}
	
	// 刷新联系人列表
	public void refreshContactsList(List<GroupInfo> groups){
		if (!isloaded){
			return;
		}
		// 刷新对讲组
		mGroups.clear();
		mGroups.addAll(groups);
		if (mContactExpandableView == null){
			return;
		}
		mContactExpandableView.setAdapter(mContactAdapter);
	}
}
