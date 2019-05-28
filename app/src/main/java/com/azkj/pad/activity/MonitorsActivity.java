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
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SetVolumeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.sword.SDK.MediaEngine;

public class MonitorsActivity extends Activity {

	// 全局变量定义
	private SharedPreferences prefs;
	
	private ViewPager mPager;
	private List<View> listViews;
	private MonitorListFragment listFragment;
	private MonitorTreeFragment treeFragment;
	private TextView tv_list, tv_tree;
	private boolean isLoadPage2 = false;

	private String sid;
	private String defaultCodec = "121";// 默认编解码信息串

	private ArrayList<MonitorInfo> roots = new ArrayList<MonitorInfo>();
	private ArrayList<MonitorInfo> users = new ArrayList<MonitorInfo>();

	// 登出消息接收
	private ForcedOfflineReceiver offflineReceiver;
	// 获取用户列表广播接收器
	private UserlistBroadcastReceiver userlistReceiver;
	// 添加监控会员接收器
	private MemberBroadcastReceiver memberReceiver;
	// 监控来电提示
	private WaitingDialog informing;
	private PTT_3G_PadApplication ptt_3g_PadApplication = null;
	private SetVolumeUtils setVolumeUtils = null;
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
		
		memberReceiver = new MemberBroadcastReceiver();
		IntentFilter memberIntentFilter = new IntentFilter();
		memberIntentFilter.addAction(GlobalConstant.ACTION_MONITOR_MEMBER);
		registerReceiver(memberReceiver, memberIntentFilter);
		
		InitView();
		InitData();
		
		// 初始化分页
		listFragment = new MonitorListFragment(this);
		treeFragment = new MonitorTreeFragment(this);

		// 加载分页
		listFragment.loadListView();
		
		// 取得用户列表
    	Calendar calendar = Calendar.getInstance();
    	sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		UserList(sid, GlobalConstant.MONITOR_TYPE_MONITOR);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeUtils = new SetVolumeUtils(this,audioManager);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(offflineReceiver);
		unregisterReceiver(userlistReceiver);
		unregisterReceiver(memberReceiver);
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
		
		// 取得登录用户信息
		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
 		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    	
 		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(MonitorsActivity.this,
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
 		
 		/*// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}*/
 		String uri = sipUser.getUsername();
    	System.out.println("lizhiwei:send,req:userlist,"+uri);
     	/*MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_USERLIST
     			, uri
     			, 1
     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
     			, String msgBody = "req:userlist\r\n" +
 					"sid:"+sid+"\r\n" +
 					"employeeid:"+sipUser.getUsername()+"\r\n" +
 					"type:"+memberType+"\r\n");*/
    	String msgBody = "req:userlist\r\n" +
					"sid:"+sid+"\r\n" +
					"employeeid:"+sipUser.getUsername()+"\r\n" +
					"type:"+memberType+"\r\n";
     	MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
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
			// 设置状态
			Log.e("---------------MOnitorsAc 316--------------------size-", "users:" + users.size() + "---roots:" + roots.size());
//			/*for(MonitorInfo monitorinfo : users){
//				monitorinfo.setStatus(GlobalConstant.MONITOR_MEMBER_OFFLINE);
//			}
//			for(MonitorInfo monitorinfo : roots){
//				UpdateMonitorStatus(monitorinfo, GlobalConstant.MONITOR_MEMBER_OFFLINE);
//			}
//			// 刷新联系人列表
//			listFragment.refreshUsersList(users);
//			treeFragment.refreshUsersList(roots);*/
		}
	}

	// 获取用户列表广播接收器
	public class UserlistBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			/*String sid = intent.getStringExtra(GlobalConstant.KEY_MEETING_SID);*/
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
			System.out.println("lizhwiei 收到监控列表"+roots.size() + ","+users.size());
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
					if ((!child.isIsuser())
						&& (monitorinfos.get(i).getId().equals(child.getId()))){
						// 不是用户且编号仙童
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
		if (parent.isIsuser()
			|| (children == null)
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
			if (child.isIsuser()){
				// 用户处理
				MonitorInfo newchild = new MonitorInfo();
				newchild.setParent(parent);
				newchild.setId(child.getId());
				String number = null;
				if (CommonMethod.prefixMap.containsKey(child.getNumber())){
					String prefixMa = CommonMethod.prefixMap.get(child.getNumber()).prefix;
					number = prefixMa +child.getNumber();
				}else{
					number = child.getNumber();
				}
				Log.e("等到监控设备","number : " + number);

				newchild.setNumber(number);
				newchild.setName(child.getName());
				newchild.setType(child.getType());
				newchild.setIsuser(true);
				result.add(newchild);
			}
			else {
				// 子组处理
				for(MonitorInfo monitorinfo : monitorinfos){
					if  (child.getId().equals(monitorinfo.getId())){
						MonitorInfo newchild = new MonitorInfo();
						newchild.setParent(parent);
						newchild.setId(monitorinfo.getId());
						newchild.setNumber(monitorinfo.getNumber());
						newchild.setName(monitorinfo.getName());
						newchild.setType(monitorinfo.getType());
						newchild.setIsuser(monitorinfo.isIsuser());
						result.add(newchild);
						getTreeMonitorInfo(monitorinfos, monitorinfo.getChildren(), newchild);
						break;
					}
				}
			}
		}
		parent.setChildren(result);
	}
	
	// 取得用户监控信息
	private void getUserMonitorInfo(List<MonitorInfo> monitorinfos, ArrayList<MonitorInfo> users){
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

					String number = null;
					if (CommonMethod.prefixMap.containsKey(monitorinfo.getNumber())){
						number = CommonMethod.prefixMap.get(monitorinfo.getNumber()).prefix + monitorinfo.getNumber();
					}else{
						number = monitorinfo.getNumber();
					}
					monitorinfo.setNumber(number);
					Log.e("=====AAAAAAAAAAAAAAA",number);
					users.add(monitorinfo);
				}
			}
			else {
				if ((monitorinfo.getChildren() != null)
					&& (monitorinfo.getChildren().size() > 0)){
					getUserMonitorInfo(monitorinfo.getChildren(), users);
				}
			}
		}
	}

	// 获取添加监控用户广播接收器
	public class MemberBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final MonitorInfo monitorinfo = intent.getParcelableExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO);
			if (monitorinfo == null){
				return;
			}
			// 判断视频会议是否进行如果进行则给出提示
			if (MainActivity.getVideoType() == GlobalConstant.VIDEO_MEETING){
				// infoMeeting();
				Toast.makeText(MonitorsActivity.this, getResources().getString(R.string.title_monitor_info), Toast.LENGTH_LONG).show();
				return;
			}
			monitorinfo.setIsmonitor(true);
			monitorinfo.setInmonitoring(false);

			// 如果不为3G设备则默认打开
			if (monitorinfo.getType() != GlobalConstant.MONITOR_USERTYPE_3GINTERCOM){
				Calendar calendar = Calendar.getInstance();
		    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
				monitorinfo.setCodec(defaultCodec);
				monitorinfo.setSid(sid);
				// 发送获取视频
				Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
				callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
				sendBroadcast(callIntent);
				Log.e("获取监控发送请求广播 MinitorsActivity", "获取监控发送请求广播 MinitorsActivity:"+monitorinfo.getId());
				return;
			}
//			// 展示配置对话框
//			final MonitorDialog.Builder builder = new MonitorDialog.Builder(MonitorsActivity.this);
//			builder.setTitle(getString(R.string.title_monitor_setting_title));
//			builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					// 有视频通话不能再次视频
//					/*if (CallingManager.getInstance().isIsvideoing()){
//						Toast.makeText(MonitorsActivity.this, getResources().getString(R.string.info_video_inuse), Toast.LENGTH_LONG).show();
//						return;
//					}
//					CallingManager.getInstance().setIsvideoing(true);*/
//					Calendar calendar = Calendar.getInstance();
//			    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
//					monitorinfo.setCodec(builder.getResult());
//					monitorinfo.setSid(sid);
//					// 发送获取视频
//					Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
//					callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
//					sendBroadcast(callIntent);
//
//					// 关闭提示框
//					dialog.dismiss();
//				}
//			});
//			builder.setNeutralButton(null, new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// 关闭提示框
//					dialog.dismiss();
//				}});
//			builder.setNegativeButton(getString(R.string.title_setting_exit),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//					// 关闭提示框
//					dialog.dismiss();
//				}
//			});
//			informing = builder.create();
//			informing.setCancelable(false);
//			builder.setResult(defaultCodec);
//			informing.show();
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
	// 提示视频会议正在进行
	@SuppressWarnings("unused")
	private void infoMeeting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_monitor_title));
		builder.setMessage(getString(R.string.title_monitor_info));
		builder.setNegativeButton(getString(R.string.btn_ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog informing = builder.create();
		informing.show();
		// 3秒无操作自动关闭
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// 发送自动拒绝广播
				if ((informing != null)
					&& (informing.isShowing())){
					// 关闭窗口
					informing.dismiss();
				}
			}
		}, 3000); 
	}
}
