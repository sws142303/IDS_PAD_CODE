package com.azkj.pad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.ButtonUtils;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.sws.library.MultilevelTreeLibrary.Node;
import com.juphoon.lemon.ui.MtcDelegate;

import java.util.ArrayList;
import java.util.List;

import cn.sword.SDK.MediaEngine;

/*通讯录页面*/
public class ContactActivity extends Activity {
	
	// 内容页面
	private ViewPager mPager;
	private List<View> listViews;
	// 本地联系人
	private ContactLocalFragment localFragment;
	// 系统联系人
	private ContactSystemFragment systemFragment;
    // 本地联系人
    private ArrayList<Contacts> mLocalContacts = new ArrayList<Contacts>();
    private ArrayList<Contacts> sLocalContacts = new ArrayList<Contacts>();
	// 系统联系人
	private ArrayList<Node> mSystemGroups = new ArrayList<Node>();
    private ArrayList<Node> sSystemGroups = new ArrayList<Node>();
	private LinearLayout ll_tab, ll_oper,ll_sel;
	private TextView tv_local, tv_system;
	private Button btn_add,btnSelContactOK,btnSelContactCanel;
	private EditText et_nmornb;
	private boolean isLoadPage2 = false;
	private static int lastest = 0;
	// 联系人添加、修改、删除
	private ContactChangeReceiver contactChangeReceiver;
	private PTT_3G_PadApplication ptt_3g_PadApplication;

	private SipUser sipUser;
	private SharedPreferences prefs;
	private SetVolumeUtils setVolumeUtils = null;
	private boolean isLoadingSystem = false;
	private boolean isLoadingLocal = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
    	
    	ptt_3g_PadApplication=(PTT_3G_PadApplication)getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		contactChangeReceiver = new ContactChangeReceiver();
		IntentFilter contactChangeIntentFilter = new IntentFilter();
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_CHANGE);
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_CHANGEOVER);
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_HIDDEN);
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_DATELOADINGSUCCESS);
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_REFRESH_DATE);
		registerReceiver(contactChangeReceiver, contactChangeIntentFilter);
		
		InitView();
		InitData();
		
		localFragment = new ContactLocalFragment(this,ptt_3g_PadApplication);
		systemFragment = new ContactSystemFragment(this,ptt_3g_PadApplication);

		isLoadingLocal = localFragment.loadLocalListView(mLocalContacts);
		//systemFragment.loadSystemListView(mSystemGroups);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeUtils = new SetVolumeUtils(this,audioManager);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(contactChangeReceiver);
        
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("文胜的百宝箱","进入onkeydown   1");
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
			Log.e("文胜的百宝箱","进入onkeydown    down");
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
		{
			Log.e("文胜的百宝箱","进入onkeydown up");
			return true;
		}else if (CommonMethod.hanndleChildBackButtonPress(this, keyCode, event)) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e("文胜的百宝箱","进入onkeyup");
		if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
			setVolumeUtils.showPopupWindow(false,getWindow().getDecorView());
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
		{
			setVolumeUtils.showPopupWindow(true,getWindow().getDecorView());
			return true;
		} else return super.onKeyUp(keyCode, event);
	}

	// 初始化视图
	private void InitView(){
		// 取得控件
		ll_tab = (LinearLayout)findViewById(R.id.ll_tab);
		tv_local = (TextView)findViewById(R.id.tv_local);
		tv_system = (TextView)findViewById(R.id.tv_system);
		ll_oper = (LinearLayout)findViewById(R.id.ll_oper);
		ll_sel = (LinearLayout)findViewById(R.id.ll_sel);
		btn_add = (Button)findViewById(R.id.btn_add);
		btnSelContactOK= (Button)findViewById(R.id.btnSelContactOK);
		btnSelContactCanel= (Button)findViewById(R.id.btnSelContactCanel);
		et_nmornb = (EditText)findViewById(R.id.et_nmornb);
		// 设置字体
		tv_local.setTypeface(CommonMethod.getTypeface(this));
		tv_system.setTypeface(CommonMethod.getTypeface(this));
		btn_add.setTypeface(CommonMethod.getTypeface(this));
		et_nmornb.setTypeface(CommonMethod.getTypeface(this));
		// 设置侦听
		tv_local.setOnClickListener(new MyOnClickListener(1));
		tv_system.setOnClickListener(new MyOnClickListener(0));
		// 设置输入
		et_nmornb.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
		
		// 设置添加
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				ContactAddFragment contactAddFragment = (ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");
				if(contactAddFragment != null){
					fragmentTransaction.remove(contactAddFragment);
				}
				contactAddFragment = new ContactAddFragment();
				
				fragmentTransaction.add(R.id.ll_contact, contactAddFragment, "contactadd").commit();
				
				ll_tab.setVisibility(View.GONE);
				mPager.setVisibility(View.GONE);
				ll_oper.setVisibility(View.GONE);
			}
		});
		btnSelContactOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//必须要有成员才能点击确定
				if(ptt_3g_PadApplication.getContactList().size() > 0)
				{
					ll_oper.setVisibility(View.VISIBLE);
					ll_sel.setVisibility(View.GONE);

					//新消息页面选择联系人
					Intent intent=new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTYES);
					sendBroadcast(intent);
	
					//ptt_3g_PadApplication.getContactList().clear();
					isLoadingLocal = localFragment.loadLocalListView(mLocalContacts);
					isLoadingSystem = systemFragment.loadSystemListView(mSystemGroups);
				}
				else
				{
					Toast.makeText(ContactActivity.this, "请选择要转发的成员", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
		btnSelContactCanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {Log.e("取消", "您选择了取消");
				if(ptt_3g_PadApplication.getContactList().size()>0){
					ptt_3g_PadApplication.getContactList().clear();
				}
				
				ll_oper.setVisibility(View.VISIBLE);
				ll_sel.setVisibility(View.GONE);

				ptt_3g_PadApplication.setAddContact(false);

				Intent intent=new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTNO);
				sendBroadcast(intent);
				
				//ptt_3g_PadApplication.getContactList().clear();
				isLoadingLocal = localFragment.loadLocalListView(mLocalContacts);
				isLoadingSystem = systemFragment.loadSystemListView(mSystemGroups);
			}
		});
		et_nmornb.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					searchList(s.toString());
				}
				else {
					// 如果输入框内容为空，显示全部
					searchList("");
				}
			}});
	}
	
	@Override
	protected void onResume() {
		MtcDelegate.log("ptt_3g_PadApplication.isAddContact():"+ptt_3g_PadApplication.isAddContact());
		if(ptt_3g_PadApplication.isAddContact()){
			ll_oper.setVisibility(View.GONE);
			ll_sel.setVisibility(View.VISIBLE);
			ll_tab.setVisibility(View.VISIBLE);
			mPager.setVisibility(View.VISIBLE);
			//如果添加联系人显示，则删除
			FragmentManager fragmentManager=getFragmentManager();
			FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
			ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");

			if(contactAddFragment!=null){
				fragmentTransaction.remove(contactAddFragment).commitAllowingStateLoss();
			}
		}

		if (!isLoadingSystem){
			isLoadingSystem = systemFragment.loadSystemListView(mSystemGroups);
		}
		if (!isLoadingLocal){
			isLoadingLocal = localFragment.loadLocalListView(mLocalContacts);
		}


		super.onResume();
	}

	// 初始化视图
	private void InitData(){
		// 取得本地联系人
		getPhoneContacts();
		Log.e("2019-03-07","InitData 进行数据加载");
		// 取得系统联系人
		getSystemGroups();
		
		// 设置分页显示
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.fragment_contact_system, null));
		listViews.add(mInflater.inflate(R.layout.fragment_contact_local, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	// 分页适配器
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	// 页面切换侦听
	public class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (index == 0){
				mPager.setCurrentItem(0);
				tv_local.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				tv_system.setTextColor(getResources().getColor(R.color.contact_title_selected));
			}
			else {
				mPager.setCurrentItem(1);
				tv_local.setTextColor(getResources().getColor(R.color.contact_title_selected));
				tv_system.setTextColor(getResources().getColor(R.color.contact_title_unselected));
			}
		}
	};
	
	// 页面改变事件
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:

				//isLoadPage2 = systemFragment.loadSystemListView(mSystemGroups);

				tv_local.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				tv_system.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
				break;
			case 1:
				//localFragment.loadLocalListView(mLocalContacts);
				tv_local.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
				tv_system.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	}
	
	// 登出接收器
	public class LogoutReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	
	// 联系人添加、修改、删除
	public class ContactChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionIntent=intent.getAction();
			if(actionIntent==GlobalConstant.ACTION_CONTACT_CHANGE){
				Log.e("*********ContactAc391通讯录联系人刷新**********", "*********通讯录联系人刷新**********");
				// 取得本地联系人
				getPhoneContacts();
				// 刷新列表
				localFragment.refreshContactsList(mLocalContacts);
				// 取得系统联系人
				//getSystemGroups();
				//成员上下线 当前只是notification  不重新加载数据
				//systemFragment.refreshContactsList();
			}else if(actionIntent==GlobalConstant.ACTION_CONTACT_CHANGEOVER){
				FragmentManager fragmentManager=getFragmentManager();
				FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
				ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");

				/*if(contactAddFragment!=null){
					fragmentTransaction.remove(contactAddFragment).commitAllowingStateLoss();
				}*/
				if(contactAddFragment==null){
				ll_tab.setVisibility(View.VISIBLE);
				mPager.setVisibility(View.VISIBLE);
				ll_oper.setVisibility(View.VISIBLE);
				}
			}else if(actionIntent==GlobalConstant.ACTION_CONTACT_HIDDEN){
				//接受来电广播，来电话后取关闭菜单窗口
				if(localFragment!=null&&localFragment.popupWindow!=null){
					localFragment.popupWindow.dismiss();
				}
				if(systemFragment!=null&&systemFragment.popupWindow!=null){
					systemFragment.popupWindow.dismiss();
				}
			}else if (actionIntent.equals(GlobalConstant.ACTION_DATELOADINGSUCCESS)){
				Log.e("2019-03-07","收到数据加载完成广播 ");
				// 取得系统联系人 并加载数据
				getSystemGroups();
			}else if (actionIntent.equals(GlobalConstant.ACTION_REFRESH_DATE)){
				Log.e("2019-03-07","收到成员上下线广播");
				//成员上下线 当前只是notification  不重新加载数据
				if (systemFragment != null){
					systemFragment.refreshContactsList();
				}
			}
		}
		
	}
		
	// 联系人添加、修改、删除操作完成
	public class ContactChangeOverReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	}
	
	// 查询列表操作
	// 实现线程同步搜索联系人
	// 线程同步问题可能会导致数据显示错误，比如会显示上一次搜索到的数据
	public void searchList(final String s){
		// 用户可能正在快速输入电话号码，之前在搜索的联系人列表已经过时了。
		final int i = ++lastest;
		// 开一个线程来进行快速搜索
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (TextUtils.isEmpty(s)) {
					// 如果输入的字符串为空，则显示所有联系人
					sLocalContacts = mLocalContacts;
					sSystemGroups = (ArrayList<Node>) mSystemGroups.clone();
				}
				else {
					// searhContacts指向搜索返回的list
					sLocalContacts = searchLocalContacts(s);
					sSystemGroups = searchSystemContacts(s);
				}

				if (i == lastest) { // 避免线程同步问题
					ContactActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (i == lastest) { // 避免线程同步问题
								localFragment.refreshContactsList(sLocalContacts);
								systemFragment.searchListRefreshContactsList(sSystemGroups);
							}
						}
					});
				}
			}
		}).start();
	}

	// 取得手机通讯录联系人
	private void getPhoneContacts() {
//		Runnable runnable = new Runnable() {
//			@Override
//			public void run() {
				try {
					mLocalContacts.clear();
					ArrayList<Contacts> contacts = CommonMethod.getPhoneContacts(ContactActivity.this);
					Log.e("本地联系人","取出来时 size : " + contacts.size());
					mLocalContacts.addAll(contacts);

				}catch (Exception e){

				}
//			}
//		};
//		ThreadPoolProxy.getInstance().execute(runnable);
}
	
	// 取得系统联系人
	private void getSystemGroups(){

		// 取得对讲组信息
//		List<GroupInfo> groups = GroupManager.getInstance().getGroupData();
//		// 移除切换对讲组成员
//		for (GroupInfo groupinfo : groups){
//			GroupInfo group = null;
//			try {
//				group = (GroupInfo)groupinfo.clone();
//			} catch (CloneNotSupportedException e) {
//				e.printStackTrace();
//			}
//			// 临时对讲组不显示
//			if (group.isTemporary()){
//				continue;
//			}
//			if (group != null){
//				if ((group.getListMembers() != null)
//					&& (group.getListMembers().size() > 0)){
//					MemberInfo member = null;
//					for (MemberInfo memberinfo : group.getListMembers()){
//						if (memberinfo.getNumber().equals(group.getNumber())){
//							member = memberinfo;
//							break;
//						}
//					}
//					if (member != null){
//						group.getListMembers().remove(member);
//					}
//				}
//				this.mSystemGroups.add(group);
//			}
//		}

   		new AsyncTaskLocal().execute(AsyncTask.THREAD_POOL_EXECUTOR,0);

	}
	
	// 查询本地联系人
	private ArrayList<Contacts> searchLocalContacts(String search){
		ArrayList<Contacts> result = new ArrayList<Contacts>();
		 for(Contacts contact : mLocalContacts){
			 if (((contact.getPhoneNo() != null)
					 && (contact.getPhoneNo().indexOf(search) >= 0))
				 ||
				 ((contact.getBuddyName() != null)
						 && (contact.getBuddyName().indexOf(search) >= 0))){
				 result.add(contact);
			 }
		 }
		 return result;
	}
	
	// 查询系统联系人
	private ArrayList<Node> searchSystemContacts(String search){
		ArrayList<Node> clone = (ArrayList<Node>) mSystemGroups.clone();
		ArrayList<Node> result = new ArrayList<Node>();
		 for(Node groupinfo : clone){
			 //Log.e("2019-03-07","name:" + groupinfo.getName() + "\r\n" + "buddyNo :" + groupinfo.getId() + "\r\n" + "isMember:" + groupinfo.isMember());
			 if (!groupinfo.isMember()){
				 result.add(groupinfo);
			 }else {
				 if (((groupinfo.getId() != null)
						 && (String.valueOf(groupinfo.getId()).indexOf(search) >= 0))
						 ||
						 ((groupinfo.getName() != null)
								 && (groupinfo.getName().indexOf(search) >= 0))){
					 result.add(groupinfo);
				 }
			 }

		 }
		 return result;
	}

	// 提示是否退出
	@SuppressWarnings("unused")
	private void askIfExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_main_exitinfo));
		builder.setMessage(getString(R.string.title_main_ifexit));
		builder.setPositiveButton(getString(R.string.btn_ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						new SettingExitFragment(ContactActivity.this).exit(); //之前的退出操作
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(getString(R.string.btn_cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	//对获取到的机构数数据进行组装
	class AsyncTaskLocal extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... objects) {
			if (ButtonUtils.isFastDoubleClick()){
				return null;
			}

			if (mSystemGroups.size() > 0){
				mSystemGroups.clear();
			}

			MediaEngine.ME_GroupInfo organizationOfMachines = ptt_3g_PadApplication.getOrganizationOfMachines();
			if (organizationOfMachines != null){
				Node node = new Node(organizationOfMachines.groupNumber,"-1",organizationOfMachines.groupName);
				mSystemGroups.add(node);
				loadInstitutionalData(organizationOfMachines.children,organizationOfMachines.groupNumber);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			//InitData();
			Log.e("测试机构数","调用 loadSystemListView mSystemGroups :"+mSystemGroups.size());
			isLoadingSystem = systemFragment.loadSystemListView(mSystemGroups);

		}

	}

	private void loadInstitutionalData(List<MediaEngine.ME_GroupInfo> organizationOfMachines,String groupNum)
	{

//		if (organizationOfMachines == null) {
//			return;
//		}
//
//		if (organizationOfMachines.size() <= 0){
//			return;
//		}
		recursiveGroupT(organizationOfMachines,groupNum);
	}

	//组装数据
	private void recursiveGroupT(List<MediaEngine.ME_GroupInfo> organizationOfMachines, String superNumber)
	{
		//if (organizationOfMachines == null)return;
		for (MediaEngine.ME_GroupInfo groupT : organizationOfMachines){
			Node node = new Node(groupT.groupNumber,superNumber,groupT.groupName);
			mSystemGroups.add(node);
			if (groupT.children.size() > 0){
				//TODO 当前如果有子节点还包含子节点进行递归
				recursiveGroupT(groupT.children,groupT.groupNumber);
			}else {
				//TODO 当前子节点不包含子节点 查找该节点所包含的成员
				List<Node> onlineNodes = new ArrayList<>();
				List<Node> offlineNodes = new ArrayList<>();
				//取得组成员
				MemberInfo[] groupMembers = GroupManager.getInstance().getALLGroupMemberSortedByState(groupT.groupNumber);
				if (groupMembers != null){
					for (MemberInfo memberInfo : groupMembers){
						Node node1 = new Node(memberInfo.getNumber(),groupT.groupNumber,memberInfo.getName());
						node1.setMember(true);
						if (PTT_3G_PadApplication.memberStatehashMap.containsKey(memberInfo.getNumber())){
							Integer state = PTT_3G_PadApplication.memberStatehashMap.get(memberInfo.getNumber()).getStatus();
							if (state != GlobalConstant.GROUP_MEMBER_EMPTY
									&& state != GlobalConstant.GROUP_MEMBER_INIT){
								onlineNodes.add(node1);
							}else {
								offlineNodes.add(node1);
							}
						}
					}
					if(onlineNodes.size() > 0)
						mSystemGroups.addAll(onlineNodes);
					if(offlineNodes.size() > 0)
						mSystemGroups.addAll(offlineNodes);
				}
			}
		}

		//TODO 请求此次组内成员
		List<Node> onlineNodes = new ArrayList<>();
		List<Node> offlineNodes = new ArrayList<>();
		MemberInfo[] groupMembers = GroupManager.getInstance().getALLGroupMemberSortedByState(superNumber);
		if (groupMembers != null) {
			for (MemberInfo memberInfo : groupMembers) {
				Node node1 = new Node(memberInfo.getNumber(), superNumber, memberInfo.getName());
				node1.setMember(true);
				if (PTT_3G_PadApplication.memberStatehashMap.containsKey(memberInfo.getNumber())) {
					Integer state = PTT_3G_PadApplication.memberStatehashMap.get(memberInfo.getNumber()).getStatus();
					if (state != GlobalConstant.GROUP_MEMBER_EMPTY
							&& state != GlobalConstant.GROUP_MEMBER_INIT) {
						onlineNodes.add(node1);
						//mDatas.add(0,node1);
					} else {
						//mDatas.add(node1);
						offlineNodes.add(node1);
					}
				}
			}
			if (onlineNodes.size() > 0)
				mSystemGroups.addAll(onlineNodes);
			if (offlineNodes.size() > 0)
				mSystemGroups.addAll(offlineNodes);
		}
	}

}
