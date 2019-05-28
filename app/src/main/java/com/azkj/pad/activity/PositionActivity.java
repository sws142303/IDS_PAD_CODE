package com.azkj.pad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.SetVolumeUtils;

import java.util.ArrayList;
import java.util.List;

/*定位页面*/
public class PositionActivity extends Activity {

	// 全局变量定义
	@SuppressWarnings("unused")
	private SharedPreferences prefs;
	// 数据库操作
	@SuppressWarnings("unused")
	private SQLiteHelper mDbHelper;
	// 本地联系人
    private List<Contacts> mListContacts = new ArrayList<Contacts>();
	// 系统联系人
	private ArrayList<GroupInfo> mTreeGroups = new ArrayList<GroupInfo>();

	private ViewPager mPager;
	private List<View> listViews;
	private PositionListFragment listFragment;
	private PositionTreeFragment treeFragment;
	private TextView tv_list, tv_tree;
	private boolean isLoadPage2 = false;

	// 列表选择监控用户广播接收器
	private GisMemberReceiver gismemberReceiver;
	// 联系人处理添加、修改、删除
	private ContactChangeReceiver contactChangeReceiver;
	private SetVolumeUtils setVolumeUtils = null;

	private boolean listFragmentIsLoadSuccess = false,treeFragmentIsLoadSuccess = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position);

		// 初始化视图
		InitView();
		InitData();
		
		// 初始化分页
		listFragment = new PositionListFragment(this);
		treeFragment = new PositionTreeFragment(this);
		
		// 全局变量定义
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		
		// 列表选择监控用户广播接收器
		gismemberReceiver = new GisMemberReceiver();
		IntentFilter gismemberIntentFilter = new IntentFilter();
		gismemberIntentFilter.addAction(GlobalConstant.ACTION_GIS_MEMBER);
		registerReceiver(gismemberReceiver, gismemberIntentFilter);
		
		// 联系人处理添加、修改、删除
		contactChangeReceiver = new ContactChangeReceiver();
		IntentFilter contactChangeIntentFilter = new IntentFilter();
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_CHANGE);
		registerReceiver(contactChangeReceiver, contactChangeIntentFilter);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeUtils = new SetVolumeUtils(this,audioManager);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//平铺列表放在onResume，每次切换都从新读取数据（通讯录修改）
		//this.getListContacts();
		// 加载分页
		//listFragment.loadLocalListView(mListContacts);

		if (!listFragmentIsLoadSuccess && mListContacts != null){
			listFragmentIsLoadSuccess = listFragment.loadLocalListView(mListContacts);
		}
		if (!isLoadPage2 && mTreeGroups != null){
			isLoadPage2 = treeFragment.loadSystemListView(mTreeGroups);
		}
		
	}
	
	@Override
	protected void onDestroy() {
    	
		unregisterReceiver(gismemberReceiver);
		unregisterReceiver(contactChangeReceiver);
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	//监听 返回键
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
		tv_list = (TextView)findViewById(R.id.tv_list);
		tv_tree = (TextView)findViewById(R.id.tv_tree);
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		
		// 设置字体
		tv_list.setTypeface(CommonMethod.getTypeface(this));
		tv_tree.setTypeface(CommonMethod.getTypeface(this));
		
		// 设置侦听
		tv_list.setOnClickListener(new MyOnClickListener(0));
		tv_tree.setOnClickListener(new MyOnClickListener(1));
	}
	
	// 初始化数据
	private void InitData(){
		// 取得联系人列表
		this.getListContacts();
		
		this.getTreeContacts();
		// 设置Tab页

		listViews.add(LayoutInflater.from(this).inflate(R.layout.fragment_position_list, null));
		listViews.add(LayoutInflater.from(this).inflate(R.layout.fragment_position_tree, null));
		
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
		public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
			//((ViewPager) arg0).removeView(mListViews.get(arg1));
			arg0.removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
		//	((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			arg0.addView(mListViews.get(arg1),0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
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
		}    //结束
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
				tv_list.setTextColor(getResources().getColor(R.color.contact_title_selected));
				tv_tree.setTextColor(getResources().getColor(R.color.contact_title_unselected));
			}
			else {
				mPager.setCurrentItem(1);
				tv_list.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				tv_tree.setTextColor(getResources().getColor(R.color.contact_title_selected));
			}
		}
	};
	
	// 页面改变事件
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				tv_list.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
				tv_tree.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				break;
			case 1:
				if(!isLoadPage2){
					isLoadPage2 = treeFragment.loadSystemListView(mTreeGroups);
				}
				tv_list.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				tv_tree.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
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
	
	// 联系人处理添加、修改、删除
		public class ContactChangeReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("*********PositionAc1741定位联系人刷新**********", "*********定位联系人刷新**********");
				getListContacts();
				getTreeContacts();
				listFragment.refreshContactsList(mListContacts);
				treeFragment.refreshContactsList(mTreeGroups);
			}
			
		}

	// 取得平铺联系人
	private void getListContacts(){
		// 清空已有
		this.mListContacts.clear();
		List<MemberInfo> memberinfos = GroupManager.getInstance().getAllMemberInfoData();
		for(MemberInfo memberinfo : memberinfos){
			boolean exists = false;
			for (Contacts contact : this.mListContacts){
				if (contact.getBuddyNo().equals(memberinfo.getNumber())){
					exists = true;
					break;
				}
			}
			if (exists){
				continue;
			}
			Contacts contact = new Contacts();
			contact.setBuddyNo(memberinfo.getNumber());
			contact.setBuddyName(memberinfo.getName());
			contact.setPhoneNo(memberinfo.getNumber());
			contact.setMeetingState(memberinfo.getStatus());
			this.mListContacts.add(contact);
			
		}
		Log.e("PositionActivity", "mListContracts集合长度"+mListContacts.size());
		// 加载数据供外部调用
		if (listFragment == null){
			listFragment = new PositionListFragment(this);
			listFragmentIsLoadSuccess = listFragment.loadLocalListView(mListContacts);
		}else {
			listFragmentIsLoadSuccess = listFragment.loadLocalListView(mListContacts);
		}

		//ArrayList<Contacts> contacts = CommonMethod.getPhoneContacts(this);
		//this.mListContacts.addAll(contacts);
	}

	// 取得按组联系人
	private void getTreeContacts(){
		// 清空已有
		this.mTreeGroups.clear();
		// 取得对讲组信息
		List<GroupInfo> groups = GroupManager.getInstance().getGroupData();
		// 移除切换对讲组成员
		for (GroupInfo groupinfo : groups){
			GroupInfo group = null;
			try {
				group = (GroupInfo)groupinfo.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			// 临时对讲组不显示
			if (group.isTemporary()){
				continue;
			}
			if (group != null){
				if ((group.getListMembers() != null)
					&& (group.getListMembers().size() > 0)){
					MemberInfo member = null;
					for (MemberInfo memberinfo : group.getListMembers()){
						if (memberinfo.getNumber().equals(group.getNumber())){
							member = memberinfo;
							break;
						}
					}
					if (member != null){
						group.getListMembers().remove(member);
					}
				}
				this.mTreeGroups.add(group);
			}
		}
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
						PositionActivity.this.finish();
						dialog.dismiss();
						System.exit(0);
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

	// 登出接收器
	public class LogoutReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	
	// 列表选择监控用户广播接收器
	public class GisMemberReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Contacts contact = intent.getParcelableExtra(GlobalConstant.KEY_MEETING_MEMBERINFO);
			if (contact == null){
				return;
			}
			boolean inorde = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_INORDE, true);
			if (inorde)
			{
				for (Contacts contacts :  mListContacts){
					if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
						contacts.setInmeeting(true);
						break;
					}
				}
				for (GroupInfo groupinfo : mTreeGroups){
					for (MemberInfo memberinfo : groupinfo.getListMembers()){
						if (memberinfo.getNumber().equals(contact.getBuddyNo())){
							memberinfo.setInmeeting(true);
							break;
						}
					}
				}
			}
			else {
				for (Contacts contacts :  mListContacts){
					if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
						contacts.setInmeeting(false);
						break;
					}
				}
				for (GroupInfo groupinfo : mTreeGroups){
					for (MemberInfo memberinfo : groupinfo.getListMembers()){
						if (memberinfo.getNumber().equals(contact.getBuddyNo())){
							memberinfo.setInmeeting(false);
							break;
						}
					}
				}
			}
			listFragment.refreshContactsList(mListContacts);
			treeFragment.refreshContactsList(mTreeGroups);
		}
		
	}
}
