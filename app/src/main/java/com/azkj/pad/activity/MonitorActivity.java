package com.azkj.pad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.MeetingMembers;
import com.azkj.pad.model.MeetingRecords;
import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SQLiteHelper;
import com.juphoon.lemon.MtcCli;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.MtcUri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.sword.SDK.MediaEngine;

/*监控页面*/
public class MonitorActivity extends Activity{
	
	// 全局变量定义
	private SharedPreferences prefs;
	// 数据库操作
	private SQLiteHelper mDbHelper;
	
	private ViewPager mPager;
	private List<View> listViews;
	private MonitorListFragment listFragment;
	private MonitorTreeFragment treeFragment;
	private TextView tv_list, tv_tree;
	private boolean isLoadPage2 = false;

	private ArrayList<MonitorInfo> roots = new ArrayList<MonitorInfo>();
	private ArrayList<MonitorInfo> users = new ArrayList<MonitorInfo>();
	
	// 当前会议
	private MeetingRecords currMeeting;
	private MonitorInfo firstMonitorinfo;

	// 登出消息接收
	private ForcedOfflineReceiver offflineReceiver;
	// 获取用户列表广播接收器
	private UserlistBroadcastReceiver userlistReceiver;
	// 呼叫接收器
	private CallingBroadcastReceiver callingReceiver;
	// 创建会议结果广播接收器
	private CreateBroadcastReceiver createReceiver;
	// 获取会议成员结果广播接收器
	private MeminfoBroadcastReceiver meminfoReceiver;
	// 会议成员状态变化通知事件广播接收器
	private MstatechangeBroadcastReceiver mstatechangeReceiver;
	// 监控没有成员时自动挂断
	private MonitorHangupBroadcastReceiver monitorHangupReceiver;
	private PTT_3G_PadApplication ptt_3g_PadApplication = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		
		// 全局变量定义
		ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);

		offflineReceiver = new ForcedOfflineReceiver();
		IntentFilter offflineIntentFilter = new IntentFilter();
		offflineIntentFilter.addAction(GlobalConstant.ACTION_FORCED_OFFLINE);
		registerReceiver(offflineReceiver, offflineIntentFilter);
		
		userlistReceiver = new UserlistBroadcastReceiver();
		IntentFilter userlistIntentFilter = new IntentFilter();
		userlistIntentFilter.addAction(GlobalConstant.ACTION_MONITOR_USERLIST);
		registerReceiver(userlistReceiver, userlistIntentFilter);

		createReceiver = new CreateBroadcastReceiver();
		IntentFilter createIntentFilter = new IntentFilter();
		createIntentFilter.addAction(GlobalConstant.ACTION_MEETING_CREATERESULT);
		registerReceiver(createReceiver, createIntentFilter);
		
		callingReceiver = new CallingBroadcastReceiver();
		IntentFilter callingIntentFilter = new IntentFilter();
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TALKING);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TERMED);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_INCOMING);
		registerReceiver(callingReceiver, callingIntentFilter);
		
		meminfoReceiver = new MeminfoBroadcastReceiver();
		IntentFilter meminfoIntentFilter = new IntentFilter();
		meminfoIntentFilter.addAction(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
		registerReceiver(meminfoReceiver, meminfoIntentFilter);
		
		mstatechangeReceiver = new MstatechangeBroadcastReceiver();
		IntentFilter mstatechangeIntentFilter = new IntentFilter();
		mstatechangeIntentFilter.addAction(GlobalConstant.ACTION_MEETING_MSTATECHANGE);
		registerReceiver(mstatechangeReceiver, mstatechangeIntentFilter);
		
		monitorHangupReceiver = new MonitorHangupBroadcastReceiver();
		IntentFilter monitorHangupIntentFilter = new IntentFilter();
		monitorHangupIntentFilter.addAction(GlobalConstant.ACTION_MONITOR_CALLHANGUP);
		registerReceiver(monitorHangupReceiver, monitorHangupIntentFilter);
		
		InitView();
		InitData();
		
		// 初始化分页
		listFragment = new MonitorListFragment(this);
		treeFragment = new MonitorTreeFragment(this);

		// 加载分页
		listFragment.loadListView();
		
		// 取得用户列表
    	Calendar calendar = Calendar.getInstance();
    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		UserList(sid, GlobalConstant.MONITOR_TYPE_MONITOR);
	}
	
	@Override
	protected void onResume() {
		/*// 刷新联系人列表
		listFragment.refreshUsersList(users);
		treeFragment.refreshUsersList(roots);*/
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 结束会议
		EndConference(currMeeting);
		unregisterReceiver(offflineReceiver);
		unregisterReceiver(userlistReceiver);
		unregisterReceiver(createReceiver);
		unregisterReceiver(callingReceiver);
		unregisterReceiver(meminfoReceiver);
		unregisterReceiver(mstatechangeReceiver);
		unregisterReceiver(monitorHangupReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(CommonMethod.hanndleChildBackButtonPress(this, keyCode, event)) {
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_UP:
				int direction = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? AudioManager.ADJUST_RAISE
						: AudioManager.ADJUST_LOWER;
				int flags = AudioManager.FX_FOCUS_NAVIGATION_UP;
				int streamType = AudioManager.STREAM_RING;
				AudioManager aduioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				aduioMgr.adjustStreamVolume(streamType, direction, flags);
				return true;
			case KeyEvent.KEYCODE_BACK:
				askIfExit();
				return false;
			default:
				return true;
		}
	}*/
	
	// 初始化视图
	private void InitView(){
		// 取得控件
		tv_list = (TextView)findViewById(R.id.tv_list);
		tv_tree = (TextView)findViewById(R.id.tv_tree);
		
		// 设置字体
		tv_list.setTypeface(CommonMethod.getTypeface(this));
		tv_tree.setTypeface(CommonMethod.getTypeface(this));
		
		// 设置侦听
		tv_list.setOnClickListener(new MyOnClickListener(0));
		tv_tree.setOnClickListener(new MyOnClickListener(1));
	}
	
	// 初始化数据
	private void InitData(){
		// 设置Tab页
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();		
		listViews.add(mInflater.inflate(R.layout.fragment_monitor_list, null));
		listViews.add(mInflater.inflate(R.layout.fragment_monitor_tree, null));
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
					isLoadPage2 = treeFragment.loadTreeView(roots);
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
	
	// 获取用户列表
	private void UserList(String sid, int memberType){
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    	
 		// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,req:userlist,"+uri);
     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_USERLIST
     			, uri
     			, 1
     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
     			, "req:userlist\r\n" +
 					"sid:"+sid+"\r\n" +
 					"employeeid:"+sipUser.getUsername()+"\r\n" +
 					"type:"+memberType+"\r\n");
		MediaEngine.GetInstance().ME_SendMsg(uri
				, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
				, "req:userlist\r\n" +
						"sid:"+sid+"\r\n" +
						"employeeid:"+sipUser.getUsername()+"\r\n" +
						"type:"+memberType+"\r\n");
	}

	// 创建会议
	private MeetingRecords CreateConference(MonitorInfo monitorinfo, int conferenceType, int mediaType){
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		MeetingRecords result = null;
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
 		// 设置会议名称
    	Calendar calendar = Calendar.getInstance();
    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
    	String sessname = (CommonMethod.getCurrLongCNDate(calendar)+"监控").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
    	// 记录会议历史
    	MeetingRecords record = new MeetingRecords();
    	record.setId(0);
    	record.setUserNo(sipUser.getUsername());
    	record.setsID(sid);
    	record.setStartDate(calendar.getTime());
    	record.setConferenceNo(sid);
    	record.setConferenceNm(sessname);
    	record.setIsMonitor(1);
    	record.setConferenceType(conferenceType);
    	record.setMediaType(mediaType);
    	record.setcID("");
    	record.setStopDate(calendar.getTime());
    	record.setDuration(0);
    	record.setTime("00:00:00");
    	
    	String members = monitorinfo.getNumber() + ",";
    	List<MeetingMembers> meetingmembers = new ArrayList<MeetingMembers>();
		meetingmembers.add(new MeetingMembers(monitorinfo.getNumber(), calendar.getTime(), calendar.getTime()));
    	record.setMeetingMembers(meetingmembers);
    	// 将记录保存至数据库
    	mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			long meetingid = mDbHelper.createMeetingRecords(record);
			result = mDbHelper.getMeetingRecordsByID((int) meetingid);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			System.out.println("lizhiwei:" + e.getMessage());
		}
		
     	// 发送广播到主页面告诉其创建的会议为本地创建，请求自动接听
		Intent intent = new Intent(GlobalConstant.ACTION_MEETING_CREATE);
		intent.putExtra(GlobalConstant.KEY_MEETING_RECORD, result);
		sendBroadcast(intent);
		
    	// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,req:createconfe,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_CREATECONFE
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, "req:createconfe\r\n" +
// 					"sid:"+sid+"\r\n" +
//					"employeeid:"+sipUser.getUsername()+"\r\n" +
//					"sessname:"+sessname+"\r\n" +
//					"sessnum:"+sid+"\r\n" +
//					"members:"+members+"\r\n" +
//					"calltype:"+conferenceType+"\r\n" +
//					"mediatype:"+mediaType+"\r\n");

		MediaEngine.GetInstance().ME_SendMsg(uri
		,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
				, "req:createconfe\r\n" +
						"sid:"+sid+"\r\n" +
						"employeeid:"+sipUser.getUsername()+"\r\n" +
						"sessname:"+sessname+"\r\n" +
						"sessnum:"+sid+"\r\n" +
						"members:"+members+"\r\n" +
						"calltype:"+conferenceType+"\r\n" +
						"mediatype:"+mediaType+"\r\n");
     	
     	return result;
	}
	
	// 获取会议成员
	private void MemberInfo(MeetingRecords record){
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    	
 		// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,req:meminfo,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_MEMINFO
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, "req:meminfo\r\n" +
// 					"cid:"+record.getcID()+"\r\n" +
// 					"employeeid:"+sipUser.getUsername()+"\r\n");
		MediaEngine.GetInstance().ME_SendMsg(uri
				,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
				,"req:meminfo\r\n" +
				"cid:"+record.getcID()+"\r\n" +
				"employeeid:"+sipUser.getUsername()+"\r\n");
	}
	
	// 邀请入会
	private void AddMember(MeetingRecords record, MonitorInfo monitorinfo){
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		// 邀请入会时间
    	Calendar calendar = Calendar.getInstance();
    	MeetingMembers meetingmember = new MeetingMembers(record.getId(), monitorinfo.getNumber(), calendar.getTime(), GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO, calendar.getTime());
    	// 将记录保存至数据库
    	mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			long meetingid = mDbHelper.createMeetingMembers(meetingmember);
			meetingmember.setId((int) meetingid);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			System.out.println("lizhiwei:" + e.getMessage());
		}
		record.getMeetingMembers().add(meetingmember);
    	
 		// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,ind:addmem,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_ADDMEM
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, "ind:addmem\r\n" +
// 					"cid:"+record.getcID()+"\r\n" +
//					"employeeid:"+monitorinfo.getNumber()+"\r\n" +
//					"answertype:"+GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO+"\r\n");
		MediaEngine.GetInstance().ME_SendMsg(uri,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,"ind:addmem\r\n" +
 					"cid:"+record.getcID()+"\r\n" +
					"employeeid:"+monitorinfo.getNumber()+"\r\n" +
					"answertype:"+GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO+"\r\n");
	}
	
	// 请出会议
	private void DelMember(MeetingRecords record, MonitorInfo monitorinfo){
		
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		// 请出入会时间
    	Calendar calendar = Calendar.getInstance();
    	MeetingMembers meetingmember = null;
    	for(MeetingMembers member : record.getMeetingMembers()){
    		if (member.getBuddyNo().equals(monitorinfo.getNumber())){
    			meetingmember = member;
    			break;
    		}
    	}
    	meetingmember.setStopDate(calendar.getTime());
    	// 将记录保存至数据库
    	mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			mDbHelper.updateMeetingMembers(meetingmember);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			System.out.println("lizhiwei:" + e.getMessage());
		}
		record.getMeetingMembers().remove(meetingmember);
    	
 		// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,ind:delmem,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_DELMEM
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, "ind:delmem\r\n" +
// 					"cid:"+record.getcID()+"\r\n" +
//					"employeeid:"+meetingmember.getBuddyNo()+"\r\n");

		MediaEngine.GetInstance().ME_SendMsg(uri,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,"ind:delmem\r\n" +
				"cid:"+record.getcID()+"\r\n" +
				"employeeid:"+meetingmember.getBuddyNo()+"\r\n");
	}
	
	// 结束会议
	private void EndConference(MeetingRecords record){
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (currMeeting == null){
			return;
		}
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		// 请出入会时间
    	Calendar calendar = Calendar.getInstance();
    	record.setStopDate(calendar.getTime());
    	int dur = FormatController.getSecondByDate(record.getStartDate(), record.getStopDate());
    	record.setDuration(dur);
    	record.setTime(FormatController.secToTime(dur));
    	/*record.setDuration(calendar.getTime().getSeconds() - record.getStartDate().getSeconds());
    	record.setTime("00:00:00");*/
    	for(MeetingMembers meetingmember : record.getMeetingMembers()){
        	meetingmember.setStopDate(calendar.getTime());
    	}
    	// 将记录保存至数据库
    	mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			mDbHelper.updateMeetingRecords(record);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			System.out.println("lizhiwei:" + e.getMessage());
		}
    	
 		// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if ((sipServer.getServerIp() != null)
        		&& (!uri.contains(sipServer.getServerIp()))){
    		uri += sipServer.getServerIp();
    	}
    	System.out.println("lizhiwei:send,ind:endconfe,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_DELMEM
//				, uri
//				, 1
//				, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//				, "ind:endconfe\r\n" +
//						"cid:"+record.getcID()+"\r\n");

		MediaEngine.GetInstance().ME_SendMsg(uri
				, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
				, "ind:endconfe\r\n" +
				"cid:"+record.getcID()+"\r\n");

     	currMeeting = null;
	}

	// 登出接收器
	public class LogoutReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	
	// 强迫下线接收器
	public class ForcedOfflineReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 结束会议
			EndConference(currMeeting);
			// 设置状态
			for(MonitorInfo monitorinfo : users){
				monitorinfo.setStatus(GlobalConstant.MONITOR_MEMBER_OFFLINE);
			}
			for(MonitorInfo monitorinfo : roots){
				UpdateMonitorStatus(monitorinfo, GlobalConstant.MONITOR_MEMBER_OFFLINE);
			}
			// 刷新联系人列表
			listFragment.refreshUsersList(users);
			treeFragment.refreshUsersList(roots);
		}
	}

	// 获取用户列表广播接收器
	public class UserlistBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<MonitorInfo> monitorinfos = intent.getParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO);
			if ((monitorinfos == null)
				|| (monitorinfos.size() <= 0)){
				return;
			}
			// 取得根组
			roots.clear();
			getRootMonitorInfo(monitorinfos, roots);
			// 取得用户
			users.clear();
			getUserMonitorInfo(monitorinfos, users);
			// 建立树形结构
			for (MonitorInfo parent : roots){
				getTreeMonitorInfo(monitorinfos, parent.getChildren(), parent);
			}
			// 刷新联系人列表
			listFragment.refreshUsersList(users);
			treeFragment.refreshUsersList(roots);
		}
		
	}
	
	// 取得根监控组信息
	private void getRootMonitorInfo(ArrayList<MonitorInfo> monitorinfos, ArrayList<MonitorInfo> roots){
		// 节点不是其他节点的子节点即为根节点
		for (int i = 0; i < monitorinfos.size(); i++){
			boolean exists = false;
			for (int j = 0; j < monitorinfos.size(); j++){
				if ((monitorinfos.get(j).getChildren() == null)
					|| (monitorinfos.get(j).getChildren().size() <= 0)){
					continue;
				}
				// 是其他组的子项
				for(MonitorInfo child : monitorinfos.get(j).getChildren()){
					if (monitorinfos.get(i).getNumber().equals(child.getNumber())){
						exists = true;
						break;
					}
				}
			}
			if (!exists){
				roots.add(monitorinfos.get(i));
			}
		}
	}
	
	// 建立树形结构
	private void getTreeMonitorInfo(ArrayList<MonitorInfo> monitorinfos, List<MonitorInfo> children, MonitorInfo parent){
		if ((children == null)
			|| (children.size() <= 0)){
			parent.setLeaf(true);
			return;
		}
		parent.setLeaf(false);
		parent.setIcon(-1);
		parent.setIconForExpanding(R.drawable.jiantou);
		parent.setIconForFolding(R.drawable.jiantou_2);
		
		ArrayList<MonitorInfo> result = new ArrayList<MonitorInfo>();
		for(MonitorInfo child : children){
			for(MonitorInfo monitorinfo : monitorinfos){
				if  (child.getNumber().equals(monitorinfo.getNumber())){
					MonitorInfo newchild = new MonitorInfo();
					newchild.setParent(parent);
					newchild.setNumber(monitorinfo.getNumber());
					newchild.setName(monitorinfo.getName());
					newchild.setType(monitorinfo.getType());
					result.add(newchild);
					getTreeMonitorInfo(monitorinfos, monitorinfo.getChildren(), newchild);
					break;
				}
			}
		}
		parent.setChildren(result);
	}
	
	// 取得用户监控信息
	private void getUserMonitorInfo(ArrayList<MonitorInfo> monitorinfos, ArrayList<MonitorInfo> users)
	{
		for(MonitorInfo monitorinfo : monitorinfos){
			if (monitorinfo.isIsuser()){
				boolean exists = false;
				for(MonitorInfo userinfo : users){
					if (userinfo.getId().equals(monitorinfo.getId())){
						exists = true;
						break;
					}
				}
				if (!exists){
					users.add(monitorinfo);
				}
			}
		}
	}

//	// 获取添加监控用户广播接收器   Sws 2018 -08-16 注释
//	public class MemberBroadcastReceiver extends BroadcastReceiver
//	{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//
//			Log.e("MemberBroadcastReceiver", "MemberBroadcastReceiver");
//
//			// TODO Auto-generated method stub
//			MonitorInfo monitorinfo = intent.getParcelableExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO);
//			if (monitorinfo == null){
//				return;
//			}
//			monitorinfo.setIsmonitor(true);
//			monitorinfo.setInmonitoring(false);
//			if (currMeeting != null){
//				// 邀请入会
//				AddMember(currMeeting, monitorinfo);
//				// 通知视频展示
////				Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
////				callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
////				sendBroadcast(callIntent);
//				Log.e("MemberBroadcastReceiver", "MemberBroadcastReceiver 当前会议存在，添加成员");
//			}
//			else
//			{
//				if (CallingManager.getInstance().getCallingCount() >= 4){
//					Toast.makeText(MonitorActivity.this, getResources().getString(R.string.info_meeting_route_busy), Toast.LENGTH_LONG).show();
//					return;
//				}
//				Log.e("MemberBroadcastReceiver", "MemberBroadcastReceiver 当前会议不存在，创建会议");
//				// 设置首个视频
//				firstMonitorinfo = monitorinfo;
//				// 创建会议
//				currMeeting = CreateConference(monitorinfo, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
//			}
//		}
//	}
	
	// 通话广播接收器
	public class CallingBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String callAction = intent.getAction();

			if(callAction == GlobalConstant.ACTION_CALLING_INCOMING){
				// 有呼入来到，适用于呼入
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MONITOR)
					|| (currMeeting == null)
					|| (currMeeting.getConferenceNo() == null)
					|| (!meetingno.endsWith(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				// 屏幕始终不锁定
				showWhenLocked(true);
				// 发送自动接听音频广播
				Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
				callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
				sendBroadcast(callIntent);
				CallingManager.getInstance().updateCallingAlright(mSessId, true);
			}
			else if(callAction == GlobalConstant.ACTION_CALLING_TALKING){
				// 开始讲话，适用于呼入呼出
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				System.out.println("lizhiwei Meeting ACTION_CALLING_TALKING;calltype,"+calltype+";meetingno,"+meetingno);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MONITOR)
					|| (currMeeting == null)
					|| (!meetingno.endsWith(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}

				if (((currMeeting == null)
					|| (currMeeting.getConferenceNo() == null)
					|| (!meetingno.equals(currMeeting.getConferenceNo())))){
					return;
				}
			}
			else if(callAction == GlobalConstant.ACTION_CALLING_TERMED){
				// 通话终止,适用于呼入呼出
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				System.out.println("lizhiwei Meeting ACTION_CALLING_TERMED;calltype,"+calltype+";meetingno,"+meetingno);
				// 判断是否为监控
				if ((calltype != GlobalConstant.CALL_TYPE_MONITOR)
					|| (currMeeting == null)
					|| (!meetingno.endsWith(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}

				if (((currMeeting == null)
					|| (currMeeting.getConferenceNo() == null)
					|| (!meetingno.equals(currMeeting.getConferenceNo())))){
					return;
				}
				firstMonitorinfo = null;
				// 结束通话
				CallingManager.getInstance().removeCallingInfo(mSessId);
				// 结束会议(通知更新视频)
				Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
				meetIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
				sendBroadcast(meetIntent);
				// 结束会议
				EndConference(currMeeting);
				// 更新会议成员状态
				for(MonitorInfo monitor : users){
					monitor.setStatus(GlobalConstant.MONITOR_MEMBER_OFFLINE);
				}
				for(MonitorInfo monitor : roots){
					UpdateMonitorStatus(monitor, GlobalConstant.MONITOR_MEMBER_OFFLINE);
				}
				// 刷新列表
				listFragment.refreshUsersList(users);
				treeFragment.refreshUsersList(roots);
				//管理锁屏
				KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
				if (km.inKeyguardRestrictedInputMode()) {
					showWhenLocked(false);
				}
			}
		}
	}

	// 屏幕始终不锁定
	private void showWhenLocked(boolean show) {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		if (show) {
			attrs.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		}
		else {
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
		}
		getWindow().setAttributes(attrs);
	}
	
	// 创建会议结果广播接收器
	public class CreateBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// 是否创建了会议
			if (currMeeting == null){
				return;
			}
			// 判断是否为当前会议
			String sid = intent.getStringExtra(GlobalConstant.KEY_MEETING_SID);
			if ((sid == null)
				|| (sid.trim().length() <= 0)
				|| (!currMeeting.getsID().equals(sid))){
				return;
			}
			// 取得创建会议结果
			boolean result = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_RESULT, false);
			System.out.println("lizhiwei:CREATECONFE,result:"+result);
			if (!result){
				// 首个视频不能为空
				if (firstMonitorinfo == null){
					return;
				}
				// 取得参数
				int errorcode = intent.getIntExtra(GlobalConstant.KEY_MEETING_ERROR, 0);
				String dis = intent.getStringExtra(GlobalConstant.KEY_MEETING_DIS);
				// 创建失败，提示是否重新创建
				CustomDialog.Builder builder = new CustomDialog.Builder(MonitorActivity.this);
				builder.setMessage(getString(R.string.info_monitor_create_error));
				builder.setTitle(getString(R.string.info_monitor_create_errorinfo) + errorcode + "\n" + dis);
				builder.setPositiveButton(getString(R.string.title_monitor_recreate), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 重新创建
						currMeeting = null;
						currMeeting = CreateConference(firstMonitorinfo, currMeeting.getConferenceType(), currMeeting.getMediaType());
						// 关闭页面
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(getString(R.string.btn_calling_decline),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				return;
			}
			
			// 创建成功
			String sessnum = intent.getStringExtra(GlobalConstant.KEY_MEETING_SESSNUM);
			String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);
			currMeeting.setConferenceNo(sessnum);
			currMeeting.setcID(cid);
			mDbHelper = new SQLiteHelper(MonitorActivity.this);
			try {
				mDbHelper.open();
				mDbHelper.updateMeetingRecords(currMeeting);
				mDbHelper.closeclose();
			}
			catch (Exception e) {
				System.out.println("lizhiwei:" + e.getMessage());
			}
			
			// 通知刷新会议记录
			Intent intentnew = new Intent(GlobalConstant.ACTION_MEETING_REFRESH);
			intentnew.putExtra(GlobalConstant.KEY_MEETING_RECORD, currMeeting);
			sendBroadcast(intentnew);
			
			// 创建成功获取视频
			MemberInfo(currMeeting);
		}
	}

	// 获取会议成员结果广播接收器
	public class MeminfoBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (currMeeting == null){
				return;
			}
			boolean result = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_RESULT, false);
			if (!result){
				return;
			}
			String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);
			ArrayList<Contacts> contacts = intent.getParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO);
			if ((cid == null)
				|| (cid.trim().length() <= 0)
				|| (!cid.trim().equals(currMeeting.getcID()))){
				return;
			}
			// 通知更新状态
			for(Contacts contact : contacts){
				for(MonitorInfo monitorinfo : users){
					if ((contact.getBuddyNo().equals(monitorinfo.getNumber()))
							|| (contact.getBuddyName().equals(monitorinfo.getName()))){
						monitorinfo.setName(contact.getBuddyName());
						monitorinfo.setType(contact.getMemberType());
						/*// 触发获取视频与切断视频
						if ((monitorinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_TAKING)
							&& (contact.getMeetingState() != GlobalConstant.MONITOR_MEMBER_TAKING)){
							// 由监控转为非监控
							// 请出会议
							DelMember(currMeeting, monitorinfo);
							// 通知结束视频
							Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_REMOVEVIDEO);
							callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
							sendBroadcast(callIntent);
						}
						if ((monitorinfo.getStatus() != GlobalConstant.MONITOR_MEMBER_TAKING)
							&& (contact.getMeetingState() == GlobalConstant.MONITOR_MEMBER_TAKING)){
							// 由非监控转为监控
							// 邀请入会
							AddMember(currMeeting, monitorinfo);
							// 通知开启视频
							Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
							callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
							sendBroadcast(callIntent);
						}*/
						monitorinfo.setStatus(contact.getMeetingState());
						break;
					}
				}
				for(MonitorInfo monitorinfo : roots){
					UpdateMonitorInfo(monitorinfo, contact);
				}
			}
			// 刷新联系人列表
			listFragment.refreshUsersList(users);
			treeFragment.refreshUsersList(roots);
		}
	}

	// 会议成员状态变化通知事件广播接收器
	public class MstatechangeBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (currMeeting == null){
				return;
			}
			String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);
			String name = intent.getStringExtra(GlobalConstant.KEY_MEETING_NAME);
			String employeeid = intent.getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);
			int type = intent.getIntExtra(GlobalConstant.KEY_MEETING_TYPE, 0);
			int state = intent.getIntExtra(GlobalConstant.KEY_MEETING_STATE, 0);
			if ((cid == null)
				|| (cid.trim().length() <= 0)
				|| (currMeeting == null)
				|| (currMeeting.getcID() == null)
				|| (!cid.trim().equals(currMeeting.getcID()))){
				return;
			}
			Contacts contact = new Contacts();
			contact.setBuddyNo(employeeid);
			contact.setBuddyName(name);
			contact.setMemberType(type);
			contact.setMeetingState(state);
			// 更新状态
			for(MonitorInfo monitorinfo : users){
				if ((contact.getBuddyNo().equals(monitorinfo.getNumber()))
					|| (contact.getBuddyName().equals(monitorinfo.getName()))){
					monitorinfo.setName(contact.getBuddyName());
					monitorinfo.setType(contact.getMemberType());
					// TODO Auto-generated method stub
					// 触发获取视频与切断视频
					if ((monitorinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_TAKING)
						&& (contact.getMeetingState() != GlobalConstant.MONITOR_MEMBER_TAKING)){
						// 请出会议
						DelMember(currMeeting, monitorinfo);
						// 通知结束视频
						Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_REMOVEVIDEO);
						callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
						sendBroadcast(callIntent);
					}
					if ((monitorinfo.getStatus() != GlobalConstant.MONITOR_MEMBER_TAKING)
						&& (contact.getMeetingState() == GlobalConstant.MONITOR_MEMBER_TAKING)){
						// 由非监控转为监控
						/*// 邀请入会
						AddMember(currMeeting, monitorinfo);*/
						// 通知开启视频
						Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
						callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
						sendBroadcast(callIntent);
					}
					monitorinfo.setStatus(contact.getMeetingState());
					break;
				}
			}
			for(MonitorInfo monitorinfo : roots){
				UpdateMonitorInfo(monitorinfo, contact);
			}
			// 刷新联系人列表
			listFragment.refreshUsersList(users);
			treeFragment.refreshUsersList(roots);
		}
	}

	// 监控没有成员时自动挂断
	public class MonitorHangupBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			firstMonitorinfo = null;
			// 结束会议(通知更新视频)
			Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
			meetIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
			sendBroadcast(meetIntent);
			// 结束会议
			EndConference(currMeeting);
			// 更新会议成员状态
			for(MonitorInfo monitor : users){
				monitor.setStatus(GlobalConstant.MONITOR_MEMBER_OFFLINE);
			}
			for(MonitorInfo monitor : roots){
				UpdateMonitorStatus(monitor, GlobalConstant.MONITOR_MEMBER_OFFLINE);
			}
			// 刷新列表
			listFragment.refreshUsersList(users);
			treeFragment.refreshUsersList(roots);
		}
	}
	
	// 更新监控信息
	private void UpdateMonitorInfo(MonitorInfo monitorinfo, Contacts contact){
		if (contact.getBuddyNo().equals(monitorinfo.getNumber())){
			monitorinfo.setName(contact.getBuddyName());
			monitorinfo.setType(contact.getMemberType());
			monitorinfo.setStatus(contact.getMeetingState());
		}
		if ((monitorinfo.getChildren() != null)
			&& (monitorinfo.getChildren().size() > 0)){
			for(MonitorInfo child : monitorinfo.getChildren()){
				UpdateMonitorInfo(child, contact);
			}
		}
	}
	
	// 更新监控状态
	private void UpdateMonitorStatus(MonitorInfo monitorinfo, int status){
		monitorinfo.setStatus(status);
		if ((monitorinfo.getChildren() != null)
			&& (monitorinfo.getChildren().size() > 0)){
			for(MonitorInfo child : monitorinfo.getChildren()){
				UpdateMonitorStatus(child, status);
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
						new SettingExitFragment(MonitorActivity.this).exit(); //之前的退出操作
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
}
