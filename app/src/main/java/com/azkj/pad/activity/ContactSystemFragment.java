package com.azkj.pad.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SimpleTreeAdapter;
import com.azkj.pad.utility.ToastUtils;
import com.azkj.sws.library.MultilevelTreeLibrary.Node;
import com.azkj.sws.library.MultilevelTreeLibrary.OnTreeNodeClickListener;

import java.util.ArrayList;

import cn.sword.SDK.MediaEngine;

public class ContactSystemFragment {
	// 父窗体
	private Activity mActivity;
	// 可展开视图
	private ListView mContactExpandableView;
	// 可展开适配器
	private SimpleTreeAdapter mContactAdapter = null;
	// 绑定数据
	private ArrayList<Node> mGroupInfos = new ArrayList<Node>();
	private boolean isloaded = false;
	private PTT_3G_PadApplication ptt_3g_PadApplication;
	
	public PopupWindow popupWindow;// 点击显示菜单
	private ImageButton btnContactAudio;
	private ImageButton btnContactVideo;
	private ImageButton btnContactEdit;
	private ImageButton btnContactMessage;
	private Contacts selcContact;
	
	private LinearLayout ll_tab, ll_oper;
	private ViewPager mPager;

	private SipUser sipUser;
	private SharedPreferences prefs;

	private int tressValue = 0;
	public ContactSystemFragment(Activity activity,PTT_3G_PadApplication ptt_3g_PadApplication) {
		mActivity = activity;
		this.ptt_3g_PadApplication = ptt_3g_PadApplication;

		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
	}

	// 加载数据供外部调用
	public boolean loadSystemListView(ArrayList<Node> groupinfo) {

		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}



		// 添加系统用户
		Log.e("测试机构数","执行 loadSystemListView :" + groupinfo.size());
		if (groupinfo.size() > 0){
			mGroupInfos.clear();
			mGroupInfos.addAll(groupinfo);
			//new AsyncTaskLocal().execute();
			new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
			isloaded = true;
		}
		return true;
	}

	// 异步初始化数据信息
	class AsyncTaskLocal extends AsyncTask<Object, Object, Object> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {
			/*getSystemContacts();*/
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			InitData();
		}
	}

	// 设置列表显示本地联系人
	private void InitData() {
		// 取得控件
		ll_tab = (LinearLayout)mActivity.findViewById(R.id.ll_tab);
		ll_oper = (LinearLayout)mActivity.findViewById(R.id.ll_oper);
		mPager = (ViewPager) mActivity.findViewById(R.id.vPager);
		mContactExpandableView = (ListView) mActivity.findViewById(R.id.contact_system_expandableview);

		//防止数据迭代中数据源发生改变
		ArrayList<Node> list = (ArrayList<Node>) mGroupInfos.clone();
		// 设置适配器
		mContactAdapter = new SimpleTreeAdapter(mContactExpandableView
				,mActivity
				,list
				,tressValue
				, R.drawable.jiantou
				, R.drawable.jiantou_2);

		if (mContactExpandableView == null){
        	return;
        }
		mContactExpandableView.setAdapter(mContactAdapter);
		mContactAdapter.notifyDataSetChanged();
		MediaEngine.ME_GroupInfo organizationOfMachines = ptt_3g_PadApplication.getOrganizationOfMachines();
		if (mGroupInfos.size() <= 0
				&& organizationOfMachines != null){
			if (ptt_3g_PadApplication.getOrganizationOfMachines().children.size() > 0){
				//如果数据没有加载出来 重新加载一遍
				new AsyncTaskLocal().executeOnExecutor(AsyncTaskLocal.THREAD_POOL_EXECUTOR,0);
			}
		}
		mContactExpandableView.setDivider(null);

		mContactAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
			@Override
			public void onClick(View v, Node node, int position) {

				Log.e("2019-03-07","Name :" + node.getName());
				Log.e("2019-03-07","isMember :" + node.isMember());

				if (ptt_3g_PadApplication.isAddContact()){

					if (node.getId().equals(sipUser.getUsername())){
						ToastUtils.showToast(mActivity,"发送联系人不能为自己");
						return;
					}



					if(!ptt_3g_PadApplication.getContactList().contains(String.valueOf(node.getId()))){
						//v.setBackgroundResource(R.color.black);
						//mActivity.getSharedPreferences("sws", Context.MODE_PRIVATE).edit().putBoolean("state",true).commit();
						setItemBackGroundResourse(true,v,node.isMember());
						if (node.isMember()){
							ptt_3g_PadApplication.getContactList().add(String.valueOf(node.getId()));
						}
					}else {
						setItemBackGroundResourse(false, v, node.isMember());
						//v.setBackgroundResource(R.color.blue);
						//mActivity.getSharedPreferences("sws", Context.MODE_PRIVATE).edit().putBoolean("state",false).commit();
						if (node.isMember()){
							ptt_3g_PadApplication.getContactList().remove(String.valueOf(node.getId()));
						}
					}


				}else {
					if (node.isMember()){
						showWindow(v,String.valueOf(node.getId()),node.getName());
					}else if(!node.isMember() && node.getpId().equals("-1")){
						if (tressValue == 0){
							tressValue = 1;
						}else {
							tressValue = 0;
						}
						//TODO 如果当前数据加载完成 进行刷新组成员
						if (ptt_3g_PadApplication.isSuccess()) {
							//如果点击组的话刷新组成员
							Intent refreshMember = new Intent(GlobalConstant.ACTION_DATELOADINGSUCCESS);
							mActivity.sendBroadcast(refreshMember);
						}
					}
				}
			}
		});

	}

	private void setItemBackGroundResourse(boolean state,View view,boolean isMember) {

		if (!isMember){
			return;
		}

		if (state){
		view.setBackgroundResource(R.color.contact_user_color);

		}else {
			view.setBackgroundResource(R.color.meeting_keyboard_bg);
		}

	}

	// 刷新联系人列表
	public void refreshContactsList(){
		Log.e("2019-03-07","执行成员上线下消息刷新广播");
		// 添加系统用户
		if (mContactAdapter == null){
        	return;
        }
		mContactAdapter.notifyDataSetChanged();
	}

	// 显示操作菜单窗口
	private void showWindow(View v, final String buddyNo, final String buddyName)
	{
		LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
		View view = layoutInflater.inflate(R.layout.layout_contact_system_menu,null);
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// popupWindow.setOutsideTouchable(true);
		// 保存anchor在屏幕中的位置
		int[] location = new int[2];
		// 读取位置anchor座标
		v.getLocationOnScreen(location);
		if (mActivity.getResources().getDisplayMetrics().heightPixels == 1200 || mActivity.getResources().getDisplayMetrics().widthPixels == 1920){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1]- v.getHeight()- v.getHeight());
		}else if (mActivity.getResources().getDisplayMetrics().heightPixels == 800 || mActivity.getResources().getDisplayMetrics().widthPixels == 1280){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1]- (v.getHeight() * 2));
		}else if (mActivity.getResources().getDisplayMetrics().heightPixels == 720 || mActivity.getResources().getDisplayMetrics().widthPixels == 1024){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1]- v.getHeight());
		}else {
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1]- v.getHeight()- v.getHeight());
		}

		//Sws 2017/12/01修改


		btnContactAudio = (ImageButton) view.findViewById(R.id.btnContactAudio);
		btnContactVideo= (ImageButton) view.findViewById(R.id.btnContactVideo);
		btnContactEdit = (ImageButton) view.findViewById(R.id.btnContactEdit);
		btnContactMessage = (ImageButton) view.findViewById(R.id.btnContactMessage);

		btnContactAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(GlobalConstant.CALLHASVIDEO,GlobalConstant.CALLTYPE_VOICE);
				edit.apply();
				Log.e("*******Sws*****","voice");


				Intent voiceIntent=new Intent(GlobalConstant.ACTION_CALLING_OTHERCALLVOICE);
				voiceIntent.putExtra("callUserNo", buddyNo);
				mActivity.sendBroadcast(voiceIntent);
				removePopupWindow();
			}
		});
		btnContactVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(GlobalConstant.CALLHASVIDEO,GlobalConstant.CALLTYPE_VIDEO);
				edit.apply();
				Log.e("*******Sws*****","video");

				Intent voiceIntent=new Intent(GlobalConstant.ACTION_CALLING_OTHERCALLVIDEO);
				voiceIntent.putExtra("callUserNo", buddyNo);
				mActivity.sendBroadcast(voiceIntent);

				removePopupWindow();
			}
		});
		btnContactEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager=mActivity.getFragmentManager();
				FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
				ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");
				if(contactAddFragment==null){
					contactAddFragment=new ContactAddFragment();
				}
				contactAddFragment.setEditNo(buddyName);//编辑联系人
				contactAddFragment.setSystemContent(true);
				if(!contactAddFragment.isAdded()){
					fragmentTransaction.add(R.id.ll_contact,contactAddFragment, "contactadd").commit();
				}

				ll_tab.setVisibility(View.GONE);
				mPager.setVisibility(View.GONE);
				ll_oper.setVisibility(View.GONE);

				removePopupWindow();
			}
		});
		btnContactMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newMessageIntent=new Intent(GlobalConstant.ACTION_MESSAGE_MSGMAINNEW);
				ptt_3g_PadApplication.setAddContact(true);
				ptt_3g_PadApplication.getContactList().add(buddyNo);//设置全局变量
				mActivity.sendBroadcast(newMessageIntent);

				removePopupWindow();
			}
		});

	}

	private void removePopupWindow()
	{
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	// 刷新联系人列表
	public void searchListRefreshContactsList(ArrayList<Node> list){
		// 模糊查询
		if (mContactExpandableView == null)
			return;

		this.mGroupInfos = list;
		InitData();
	}
}
