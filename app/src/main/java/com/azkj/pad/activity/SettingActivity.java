package com.azkj.pad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.ForbidSlideViewPager;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SetVolumeUtils;

import java.util.ArrayList;
import java.util.List;

import cn.sword.SDK.MediaEngine;

/*设置页面*/
public class SettingActivity extends Activity{
	// 全局变量定义
	private SharedPreferences prefs;
	// 导航及内容布局
	private LinearLayout ll_tab;
			private RelativeLayout ll_content;
	// 内容页面
	private ForbidSlideViewPager mPager;
	/*private ViewPager mPager;*/
	private List<View> listViews;
	// 调度账号
	private SettingDispatcherFragment dispatcherFragment;
	// 对讲通话
	private SettingIntercomsFragment intercomFragment;
	// 视频通话
	private SettingVideoFragment videoFragment;
	// 其他配置
	private SettingOtherFragment otherFragment;
	// 注销当前账号
	private SettingLogoutFragment logoutFragment;
	// 安全退出
	private SettingExitFragment exitFragment;
	// 表头显示
	private TextView txtDispatcher, txtIntercom, txtVideo, txtOther, txtLogout, txtExit;
	private boolean isLoadPage2 = false, isLoadPage3 = false, isLoadPage4 = false, isLoadPage5 = false, isLoadPage6 = false;
	// 底部信息
	private TextView tv_phoneno, tv_officialwebsite, tv_reserved, tv_version,tv_phonenotv, tv_officialwebsitetv, tv_reservedtv, tv_versiontv;
	// 当前分页是否显示
	private Boolean currTabVisible = true;
	
	// 呼入通知接收器
	private CallingReceiver callingReceiver;
	// 全局变量保存号码
	private PTT_3G_PadApplication ptt_3g_PadApplication;
	//公司信息
	private String companyPhone,companyName,companyWebsite;
	private Button btn_show;
	private LinearLayout lll_show;
	private SetVolumeUtils setVolumeUtils = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_setting);
		ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		
		
		//获取公司信息
		MediaEngine me = MediaEngine.GetInstance();
		companyPhone = me.ME_GetCompanyPhone();
		companyName = me.ME_GetCompanyName();
		companyWebsite = me.ME_GetCompanyWebsite();
				
		InitView();
		InitData();
		dispatcherFragment = new SettingDispatcherFragment(this);
		intercomFragment = new SettingIntercomsFragment(this);
		videoFragment = new SettingVideoFragment(this);
		otherFragment = new SettingOtherFragment(this);
		logoutFragment = new SettingLogoutFragment(this);
		exitFragment = new SettingExitFragment(this);
		
		dispatcherFragment.loadDispatcherView();

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setVolumeUtils = new SetVolumeUtils(this,audioManager);
	}
	//获取当前应用的版本号：
	private String getVersionName()
	{
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String version = packInfo.versionName;
		return version;
	}
	//界面获取焦点
	@Override
	protected void onResume() {
		super.onResume();
		// 取得当前tab页是否显示
		currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication, GlobalConstant.SP_SHOWORHIDDEN);
		
		// 呼入通知接收器
		callingReceiver = new CallingReceiver();
		IntentFilter callingintentfilter = new IntentFilter();
		callingintentfilter.addAction(GlobalConstant.ACTION_CALLING_INCOMING);
		registerReceiver(callingReceiver, callingintentfilter);
		
		// 设置视图显示
		SetViewShow();

	}
	//界面销毁
	@Override
	protected void onDestroy() {
    	// 注销注册
		unregisterReceiver(callingReceiver);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		
	}
	//监听返回键
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
		ll_content = (RelativeLayout)findViewById(R.id.ll_content);
		lll_show = (LinearLayout) findViewById(R.id.lll_show);
		lll_show.setVisibility(View.GONE);
		txtDispatcher = (TextView)findViewById(R.id.txtDispatcher);
		txtIntercom = (TextView)findViewById(R.id.txtIntercom);
		txtVideo = (TextView)findViewById(R.id.txtVideo);
		txtOther = (TextView)findViewById(R.id.txtOther);
		//txtLogout = (TextView)findViewById(R.id.txtLogout);
		txtExit = (TextView)findViewById(R.id.txtExit);
		tv_phoneno = (TextView)findViewById(R.id.tv_phoneno);
		tv_officialwebsite= (TextView)findViewById(R.id.tv_officialwebsite);
		tv_reserved = (TextView)findViewById(R.id.tv_reserved);
		tv_version = (TextView)findViewById(R.id.tv_version);
		tv_phonenotv = (TextView)findViewById(R.id.tv_phonenotv);
		tv_officialwebsitetv= (TextView)findViewById(R.id.tv_officialwebsitetv);
		tv_reservedtv = (TextView)findViewById(R.id.tv_reservedtv);
		tv_versiontv = (TextView)findViewById(R.id.tv_versiontv);
		btn_show = (Button) findViewById(R.id.btn_show);
		btn_show.setVisibility(View.GONE);
		btn_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				// 展示Tab视图
				ll_tab.setVisibility(View.VISIBLE);
				int width = 300;
			/*if(getwindowmanager()==1280 && getwindowmanager_height()!=752){
				width=360;
			}else if(getwindowmanager()==1280 && getwindowmanager_height()==752){
				width=300;
			}else*/
				if(getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024){
					width=300;
					Log.e("SeetingActivity======","219");
				}else if(getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920){
					width=200;
					Log.e("SeetingActivity======","221");
				}else if(getResources().getDisplayMetrics().heightPixels == 800 || getResources().getDisplayMetrics().widthPixels == 1280 && getResources().getDisplayMetrics().heightPixels == 1280 || getResources().getDisplayMetrics().widthPixels == 800){
					Log.e("SeetingActivity======","224");
					width=120;
				}

				ll_tab.setLayoutParams(new LinearLayout.LayoutParams(CommonMethod.dip2px(getBaseContext(), width), LinearLayout.LayoutParams.MATCH_PARENT));
				ll_content.setVisibility(View.VISIBLE);

				//将登录信息保存于全局变量
				currTabVisible = true;
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN,
						currTabVisible);
				editor.commit();

				btn_show.setVisibility(View.GONE);
				lll_show.setVisibility(View.GONE);
			}
		});
		// 设置控件字体
		txtDispatcher.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		txtIntercom.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		txtVideo.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		txtOther.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		//txtLogout.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		txtExit.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_phoneno.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_officialwebsite.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_reserved.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_version.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_phonenotv.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_officialwebsitetv.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_reservedtv.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		tv_versiontv.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
		// 设置侦听
		txtDispatcher.setOnClickListener(new MyOnClickListener(0));
		txtIntercom.setOnClickListener(new MyOnClickListener(1));
		txtVideo.setOnClickListener(new MyOnClickListener(2));
		txtOther.setOnClickListener(new MyOnClickListener(3));
	//	txtLogout.setOnClickListener(new MyOnClickListener(4));
		txtExit.setOnClickListener(new MyOnClickListener(4));
		new InitData().execute(); //设置字体
		//new InitData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0); //设置字体
		/*//设置字体大小
		int textSize = 13;
		if(getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920){
			textSize = 10;
		
		}else if(getResources().getDisplayMetrics().heightPixels == 800 || getResources().getDisplayMetrics().widthPixels == 1280){
			textSize = 10;
		}
		txtDispatcher.setTextSize(textSize);
		txtIntercom.setTextSize(textSize);
		txtVideo.setTextSize(textSize);
		txtOther.setTextSize(textSize);
		//txtLogout.setTextSize(textSize);
		txtExit.setTextSize(textSize);
		tv_phoneno.setTextSize(textSize);
		tv_officialwebsite.setTextSize(textSize);
		tv_reserved.setTextSize(textSize);
		tv_version.setTextSize(textSize);
		tv_phonenotv.setTextSize(textSize);
		tv_officialwebsitetv.setTextSize(textSize);
		tv_versiontv.setTextSize(textSize);
		tv_reservedtv.setTextSize(textSize);*/
	}

	// 初始化数据
	private void InitData(){
		
		//设置公司信息
		tv_version.setText("VERSION "+getVersionName());
		tv_phoneno.setText(companyPhone);
		tv_officialwebsite.setText(companyWebsite);
		tv_reserved.setText(companyName);
		
		mPager = (ForbidSlideViewPager) findViewById(R.id.vPager);
		/*mPager = (ViewPager) findViewById(R.id.vPager);*/
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();		
		listViews.add(mInflater.inflate(R.layout.fragment_setting_dispatcher, null));
		listViews.add(mInflater.inflate(R.layout.fragment_setting_intercoms, null));
		listViews.add(mInflater.inflate(R.layout.fragment_setting_video, null));
		listViews.add(mInflater.inflate(R.layout.fragment_setting_other, null));
		//listViews.add(mInflater.inflate(R.layout.fragment_setting_logout, null));
		listViews.add(mInflater.inflate(R.layout.fragment_setting_exit, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	// 设置视图显示
	private void SetViewShow(){
		if (currTabVisible){
			// 展示Tab视图
			ll_tab.setVisibility(View.VISIBLE);
			
			int width = 300;
			/*if(getwindowmanager()==1280 && getwindowmanager_height()!=752){
				width=360;
			}else if(getwindowmanager()==1280 && getwindowmanager_height()==752){
				width=300;
			}else*/
			if(getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024){
				width=300;
			}else if(getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920){
				width=200;
			}else if(getResources().getDisplayMetrics().heightPixels == 800 || getResources().getDisplayMetrics().widthPixels == 1280){
				width=200;
			}
						
			ll_tab.setLayoutParams(new LinearLayout.LayoutParams(CommonMethod.dip2px(getBaseContext(), width), LinearLayout.LayoutParams.MATCH_PARENT));
			ll_content.setVisibility(View.VISIBLE);

			btn_show.setVisibility(View.GONE);
			lll_show.setVisibility(View.GONE);
		}
		else {
			// 隐藏Tab视图
			ll_tab.setVisibility(View.GONE);
			ll_content.setVisibility(View.VISIBLE);
			lll_show.setVisibility(View.VISIBLE);
			btn_show.setVisibility(View.VISIBLE);
		}
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
			mPager.setCurrentItem(index);
		}
	};
	
	// 页面改变事件
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				txtDispatcher.setBackgroundResource(R.drawable.list_2);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_selected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_1);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				break;
			case 1:
				txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_2);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_selected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_1);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				if(!isLoadPage2){
					isLoadPage2 = intercomFragment.loadIntercomView();
				}
				break;
			case 2:
				txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_2);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_selected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_1);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				if(!isLoadPage3){
					isLoadPage3 = videoFragment.loadVideoView();
				}
				break;
			case 3:
				txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_2);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_selected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_1);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				if(!isLoadPage4){
					isLoadPage4 = otherFragment.loadOtherView();
				}
				break;
			case 4:
				/*txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_2);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_selected));
				txtExit.setBackgroundResource(R.drawable.list_1);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				if(!isLoadPage5){
					isLoadPage5 = logoutFragment.loadLogoutView();
				}*/
				
				txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_2);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_selected));
				if(!isLoadPage6){
					isLoadPage6 = exitFragment.loadExitView();
				}
				
				break;
			/*case 5:
				txtDispatcher.setBackgroundResource(R.drawable.list_1);
				txtDispatcher.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtIntercom.setBackgroundResource(R.drawable.list_1);
				txtIntercom.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtVideo.setBackgroundResource(R.drawable.list_1);
				txtVideo.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtOther.setBackgroundResource(R.drawable.list_1);
				txtOther.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				//txtLogout.setBackgroundResource(R.drawable.list_1);
				//txtLogout.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				txtExit.setBackgroundResource(R.drawable.list_2);
				txtExit.setTextColor(getResources().getColor(R.color.contact_title_selected));
				if(!isLoadPage6){
					isLoadPage6 = exitFragment.loadExitView();
				}
				break;*/
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
	// 呼入通知接收器
	public class CallingReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intercomFragment != null){
				intercomFragment.HiddenPopupWindow();
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
						new SettingExitFragment(SettingActivity.this).exit(); //之前的退出操作
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
	//获取设备分辨率-宽度
	private int getwindowmanager(){
		WindowManager windowManager = getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        int screenWidth = screenWidth = display.getWidth();    
        return screenWidth;
	}
	//获取设备分辨率-高度
	private int getwindowmanager_height(){
		WindowManager windowManager = getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        int screenHeight = screenHeight = display.getHeight();    
        return screenHeight;
	}
	
	class InitData extends AsyncTask<Object, Object, Object>{

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			/*//设置字体大小
			int textSize = 13;
			if(getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920){
				textSize = 10;
			
			}else if(getResources().getDisplayMetrics().heightPixels == 800 || getResources().getDisplayMetrics().widthPixels == 1280){
				textSize = 10;
			}
			txtDispatcher.setTextSize(textSize);
			txtIntercom.setTextSize(textSize);
			txtVideo.setTextSize(textSize);
			txtOther.setTextSize(textSize);
			//txtLogout.setTextSize(textSize);
			txtExit.setTextSize(textSize);
			tv_phoneno.setTextSize(textSize);
			tv_officialwebsite.setTextSize(textSize);
			tv_reserved.setTextSize(textSize);
			tv_version.setTextSize(textSize);
			tv_phonenotv.setTextSize(textSize);
			tv_officialwebsitetv.setTextSize(textSize);
			tv_versiontv.setTextSize(textSize);
			tv_reservedtv.setTextSize(textSize);*/
			super.onPostExecute(result);
		}
	}
	
}
