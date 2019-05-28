package com.azkj.pad.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.MeetingSystemExpandableAdapter;
import com.azkj.pad.utility.MonitorTreeListAdapter;
import com.azkj.pad.activity.R;

/*会议-系统联系人*/
public class MeetingSystemFragment {
	// 父窗体
	private Activity mActivity;
	// 可展开视图
	private ExpandableListView mContactExpandableView;
	// 可展开适配器
	private MeetingSystemExpandableAdapter mContactAdapter = null;
	// 绑定数据
	private ArrayList<GroupInfo> mGroups = new ArrayList<GroupInfo>();
    // 此页面是否已加载
	private boolean isloaded = false;
	
	public MeetingSystemFragment(Activity activity){
    	mActivity = activity;
    }
    
    // 加载数据供外部调用
	public boolean loadSystemListView(List<GroupInfo> groups){
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
		mContactExpandableView = (ExpandableListView)this.mActivity.findViewById(R.id.meeting_system_expandableview);
		// 设置适配器
		mContactAdapter = new MeetingSystemExpandableAdapter(this.mActivity, this.mGroups);
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
				MemberInfo memberinfo = (MemberInfo)mContactAdapter.getChild(groupPosition, childPosition);
				if (memberinfo != null){
					if (!memberinfo.isInmeeting()){
						// 设置会议状态
						memberinfo.setInmeeting(true);
						// 设置显示效果
						ImageView addorcut = (ImageView)v.findViewById(R.id.iv_addorcut);
						if (memberinfo.isInmeeting()){
							addorcut.setImageDrawable(v.getResources().getDrawable(R.drawable.meeting_null));
						}
						else {
							addorcut.setImageDrawable(v.getResources().getDrawable(R.drawable.meeting_add));
						}
						// 添加或删除会议人员
						Contacts contact = new Contacts();
						contact.setBuddyNo(memberinfo.getNumber());
						contact.setBuddyName(memberinfo.getName());
						contact.setInmeeting(memberinfo.isInmeeting());
						contact.setMeetingState(memberinfo.getMeetingState());
						contact.setMemberType(memberinfo.getMemberType());
						// 通知添加或删除会议人员
						Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBER);
						intent.putExtra(GlobalConstant.KEY_MEETING_INORDE, true);
						intent.putExtra(GlobalConstant.KEY_MEETING_MEMBERINFO, contact);
						mActivity.sendBroadcast(intent);
					}
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
	
	public void refreshExpandleView()
	{
		if(mContactAdapter != null)
		{
			mContactAdapter.notifyDataSetChanged();
		}
	}
}
