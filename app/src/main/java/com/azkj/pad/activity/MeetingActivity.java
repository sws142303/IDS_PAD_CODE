package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MeetingMembers;
import com.azkj.pad.model.MeetingRecords;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.model.SessionCallInfos;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.service.PTTService;
import com.azkj.pad.utility.ButtonUtils;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.KeyboardUtil;
import com.azkj.pad.utility.MeetingAllListAdapter;
import com.azkj.pad.utility.MeetingMemberListAdapter;
import com.azkj.pad.utility.MeetingSelectedListAdapter;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.pad.utility.ThreadPoolProxy;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.MtcCall;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.ui.MtcDelegate;
import com.juphoon.lemon.ui.MtcRing;
import com.juphoon.lemon.ui.MtcVideo;
import com.juphoon.videoengine.ViERenderer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Dispatcher.ConfigureOPPrx;
import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.activity.PTT_3G_PadApplication.handler;
import static com.azkj.pad.utility.GlobalConstant.ACTION_CALLING_TERMED;
import static com.azkj.pad.utility.GlobalConstant.CONFERENCE_TYPE_BROADCAST;

public class MeetingActivity extends Activity implements OnClickListener{
	private String TAG = "MeetingActivity";
	// 当前通话状态
	private int mCallState = GlobalConstant.CALL_STATE_NONE;
	// 全局变量定义
	private SharedPreferences prefs;
	// 数据库操作
	private SQLiteHelper mDbHelper;
	// 本地联系人
	private List<Contacts> mLocalContacts = new ArrayList<Contacts>();
	// 系统联系人
	private ArrayList<GroupInfo> mSystemGroups = new ArrayList<GroupInfo>();
	// 全部联系人
	private List<Contacts> mAllContacts = new ArrayList<Contacts>();
	// 选中联系人
	private List<Contacts> selectedContacts = new ArrayList<Contacts>();
	// 会议中临时增减联系人
	private ArrayList<Contacts> tempContacts = new ArrayList<Contacts>();

	// 发起会议-成员列表
	private LinearLayout ll_List;
	private ViewPager mPager;
	private List<View> listViews;
	private MeetingLocalFragment localFragment;
	private MeetingSystemFragment systemFragment;
	private TextView tv_local, tv_system, tv_seleced;
	private ListView lv_seleced;
	private Button btn_voice, btn_video, btn_broadcast, btn_showplate;
	private boolean isLoadPage2 = false;
	private MeetingSelectedListAdapter selecedAdapter = null;
	private SipUser sipUser = null;
	private SipServer sipServer =null;
	// 当前回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	// 会议中
	private SurfaceView mLocalView ;
	private SurfaceView mRemoteView;

	private LinearLayout ll_meeting_camera;
	private FrameLayout fl_meeting_camera;
	private RelativeLayout rl_meeting;
	private TextView tv_meeting_title;
	private Chronometer mChrSessionTimeState;
	private ListView lv_members;
	private Button btn_hangup, btn_offline, btn_handsfree, btn_mute, btn_member;
	private MeetingMemberListAdapter memberAdapter = null;

	// 邀请入会
	private RelativeLayout rl_append;
	private TextView tv_append_seleced;
	private ListView lv_append_seleced, lv_append_alllist;
	private Button btn_append_showplate, btn_append_ok, btn_append_cancel;
	private MeetingAllListAdapter appendAllListAdapter = null;
	private MeetingSelectedListAdapter appendSelecedAdapter = null;

	// 键盘操作
	private RelativeLayout rl_keyboard, rl_keyboard_input, rl_keyboard_hidden;
	// 键盘输入结果
	private EditText txt_phoneno;
	// 键盘操作完成
	private Button btn_keyboard_delete, btn_keyboard_hideplate, btn_keyboard_complete;
	// 键盘输入操作
	//private android.inputmethodservice.KeyboardView keyboard_view;
	//键盘 1-9  0 * #
	private Button btn_one,btn_two,btn_three,btn_four,btn_five,btn_six,btn_seven,btn_eight,btn_nine,btn_ling,btn_xing,btn_jing;
	private StringBuffer sb = new StringBuffer();
	private LinearLayout metting_linearlayout;
	// 键盘处理
	private KeyboardUtil keyboardUtil;

	// 当前会议
	public static MeetingRecords currMeeting;
	// 登出消息接收
	private ForcedOfflineReceiver offflineReceiver;
	// 界面操作与会成员改变接收器
	private MeetingMemberReceiver meetingmemberReceiver;
	// 联系人处理添加、修改、删除
	private ContactChangeReceiver contactChangeReceiver;
	// 创建会议结果广播接收器
	private CreateBroadcastReceiver createReceiver;
	// 获取会议成员结果广播接收器
	private MeminfoBroadcastReceiver meminfoReceiver;
	// 会议成员状态变化通知事件广播接收器
	private MstatechangeBroadcastReceiver mstatechangeReceiver;
	// 呼叫接收器
	private CallingBroadcastReceiver callingReceiver;
	// 通过通话记录创建会议
	private CallingRecordBroadcastReceiver callingRecordReceiver;
	// 通过定位创建会议
	private PositionRecordBroadcastReceiver positionRecordReceiver;
	private PTT_3G_PadApplication ptt_3g_PadApplication;
	private String cameraCode = "2";
	private MLocalViewHideOrShowReceiver mLocalViewHideOrShowReceiver;
	SurfaceView sfv_new;
	/*public KeyboardUtil getKeyboardUtil() {
		if (keyboardUtil == null){
			keyboardUtil = new KeyboardUtil(MeetingActivity.this, MeetingActivity.this, keyboard_view, txt_phoneno, btn_keyboard_delete);
		}
		return keyboardUtil;
	}*/
	private Button btnGone_meeting;
	private FrameLayout meeting_frame;
	private RelativeLayout callingRemoteViewLayout_meeting;
	private LinearLayout callingRemoteViewFrameInfo_meeting;
	private TextView videoUserName_meeting;
	private TextView videoUserNo_meeting;
	private Chronometer videoUserTime_meeting;
	private SurfaceView surfaceview_meeting;
	private Button btn_meetingSplitScreen;
	private Spinner spinner_meetingsplitscreen_type;
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	private Button btn_meetingStartDecode;
	private Button btn_meetingEndDecode;
	private String toastCount = null;


	private boolean meetingModeIsMCU = false;
	//统计丢包时延相关
	private LinearLayout linearlayout_callInfo_meetingMCU;
	//语音统计
	private TextView tv_voice_Downlink_meetingMCU,tv_voice_Uplink_meetingMCU,tv_voice_Bidirectionaldelay_meetingMCU,tv_voice_downstreambandwidth_meetingMCU,tv_voice_Uplinkbandwidth_meetingMCU;
	//视频统计
	private TextView tv_video_Downlink_meetingMCU,tv_video_Uplink_meetingMCU,tv_video_Bidirectionaldelay_meetingMCU,tv_video_downstreambandwidth_meetingMCU,tv_video_Uplinkbandwidth_meetingMCU;
	//语音统计参数
	private int voiceDownlink_meetingMCU = 0;
	private int voice_Uplink_meetingMCU = 0;
	private int voice_Bidirectionaldelay_meetingMCU = 0;
	private int voice_downstreambandwidth_meetingMCU = 0;
	private int voice_Uplinkbandwidth_meetingMCU = 0;
	//视频统计参数
	private int video_Downlink_meetingMCU = 0;
	private int video_Uplink_meetingMCU = 0;
	private int video_Bidirectionaldelay_meetingMCU = 0;
	private int video_downstreambandwidth_meetingMCU = 0;
	private int video_Uplinkbandwidth_meetingMCU = 0;

	private boolean isStartTimer_meetingMCU = false;

	//是否可以发起会议 逻辑为 第一次发起会议 设置为True  等当前会议挂断后 挂断消息回来 设置为false  每次发起会议前会判断如果为true 则不让发起会议
	private boolean currentMeeting = false;


	private Timer mTimer;
	private TimerTask timerTask;

	Handler meetingCreateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);


			switch (msg.what){

				case 1:
					if (toastCount == null){
						return;
					}
					ToastUtils.showToast(MeetingActivity.this,toastCount);
					toastCount = null;
					break;

				case 2 :
					if (mLocalView == null){
						mLocalView = ViERenderer.CreateLocalRenderer(MeetingActivity.this);
						mLocalView.setZOrderOnTop(true);

					}

					if (surfaceview_meeting == null){
						//远端视频展示SurfaceView
						surfaceview_meeting = (SurfaceView) findViewById(R.id.surfaceview_meeting);
						surfaceview_meeting.setVisibility(View.VISIBLE);
						surfaceview_meeting.setZOrderMediaOverlay(true);

					}

					//surfaceview_meeting.setZOrderOnTop(true);
					Log.e("测试会议远端视频"," 显示   270");
					//MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, surfaceview_meeting);
					String string = prefs.getString("ISMCU", "True");
					if (string.equals("True")){
						MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, surfaceview_meeting);
					}else {
						MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, mRemoteView);
					}
					Log.e("MoveYourBaby","收到 msg2 进行SetSurfaceView");
					break;

				case 3:


				break;
				case 4:
					tv_voice_Downlink_meetingMCU.setText(""+voiceDownlink_meetingMCU);
					tv_voice_Uplink_meetingMCU.setText(""+voice_Uplink_meetingMCU);
					tv_voice_Bidirectionaldelay_meetingMCU.setText(""+voice_Bidirectionaldelay_meetingMCU);
					tv_voice_downstreambandwidth_meetingMCU.setText(""+voice_downstreambandwidth_meetingMCU);
					tv_voice_Uplinkbandwidth_meetingMCU.setText(""+voice_Uplinkbandwidth_meetingMCU);

					tv_video_Downlink_meetingMCU.setText(""+video_Downlink_meetingMCU);
					tv_video_Uplink_meetingMCU.setText(""+video_Uplink_meetingMCU);
					tv_video_Bidirectionaldelay_meetingMCU.setText(""+video_Bidirectionaldelay_meetingMCU);
					tv_video_downstreambandwidth_meetingMCU.setText(""+video_downstreambandwidth_meetingMCU);
					tv_video_Uplinkbandwidth_meetingMCU.setText(""+video_Uplinkbandwidth_meetingMCU);
					break;

				case 5:
					tv_voice_Downlink_meetingMCU.setText("0");
					tv_voice_Uplink_meetingMCU.setText("0");
					tv_voice_Bidirectionaldelay_meetingMCU.setText("0");
					tv_voice_downstreambandwidth_meetingMCU.setText("0");
					tv_voice_Uplinkbandwidth_meetingMCU.setText("0");

					tv_video_Downlink_meetingMCU.setText("0");
					tv_video_Uplink_meetingMCU.setText("0");
					tv_video_Bidirectionaldelay_meetingMCU.setText("0");
					tv_video_downstreambandwidth_meetingMCU.setText("0");
					tv_video_Uplinkbandwidth_meetingMCU.setText("0");
					break;

				case 6:

					Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
					switchSolitScreen.putExtra("splitScreen", splitScreen);
					sendBroadcast(switchSolitScreen);
					Log.e("测试会议切屏","handler中 content : " + splitScreen);
					break;
			}
		}
	};
	private AudioManager audioManager;
	private SetVolumeUtils setVolumeUtils = null;
	private String splitScreen = "Four";

	private boolean split = true;

	@SuppressLint("LongLogTag")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting);

		EventBus.getDefault().register(this);

		// 全局变量定义
		ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		sipServer= CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		//系统方法
		audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
		//设置声音模式
		audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
		setVolumeUtils = new SetVolumeUtils(this,audioManager);
		offflineReceiver = new ForcedOfflineReceiver();
		IntentFilter offflineIntentFilter = new IntentFilter();
		offflineIntentFilter.addAction(GlobalConstant.ACTION_FORCED_OFFLINE);
		registerReceiver(offflineReceiver, offflineIntentFilter);

		meetingmemberReceiver = new MeetingMemberReceiver();
		IntentFilter memberIntentFilter = new IntentFilter();
		memberIntentFilter.addAction(GlobalConstant.ACTION_MEETING_MEMBER);
		memberIntentFilter.addAction(GlobalConstant.ACTION_MEETING_REMEMBERINFO);
		registerReceiver(meetingmemberReceiver, memberIntentFilter);

		contactChangeReceiver = new ContactChangeReceiver();
		IntentFilter contactChangeIntentFilter = new IntentFilter();
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_CHANGE);
		contactChangeIntentFilter.addAction(GlobalConstant.ACTION_MEETING_PUSHVIDEO);
		registerReceiver(contactChangeReceiver, contactChangeIntentFilter);

		createReceiver = new CreateBroadcastReceiver();
		IntentFilter createIntentFilter = new IntentFilter();
		createIntentFilter.addAction(GlobalConstant.ACTION_MEETING_CREATERESULT);
		registerReceiver(createReceiver, createIntentFilter);

		meminfoReceiver = new MeminfoBroadcastReceiver();
		IntentFilter meminfoIntentFilter = new IntentFilter();
		meminfoIntentFilter.addAction(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
		registerReceiver(meminfoReceiver, meminfoIntentFilter);

		mstatechangeReceiver = new MstatechangeBroadcastReceiver();
		IntentFilter mstatechangeIntentFilter = new IntentFilter();
		mstatechangeIntentFilter.addAction(GlobalConstant.ACTION_MEETING_MSTATECHANGE);
		registerReceiver(mstatechangeReceiver, mstatechangeIntentFilter);

		callingReceiver = new CallingBroadcastReceiver();
		IntentFilter callingIntentFilter = new IntentFilter();
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TALKING);
		callingIntentFilter.addAction(ACTION_CALLING_TERMED);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_INCOMING);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_STARTPREVIEW);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_STARTVIDEO);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CAPTURESIZE);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_VIDEOSIZE);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_STOPVIDEO);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLHOLDOK);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLHOLDFAILED);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLUNHOLDOK);
		callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLUNHOLDFAILED);
		registerReceiver(callingReceiver, callingIntentFilter);

		callingRecordReceiver = new CallingRecordBroadcastReceiver();
		IntentFilter callingRecordIntentFilter = new IntentFilter();
		callingRecordIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CONFERENCE);
		registerReceiver(callingRecordReceiver, callingRecordIntentFilter);

		positionRecordReceiver = new PositionRecordBroadcastReceiver();
		IntentFilter positionRecordIntentFilter = new IntentFilter();
		positionRecordIntentFilter.addAction(GlobalConstant.ACTION_POSITION_OPERATE);
		registerReceiver(positionRecordReceiver, positionRecordIntentFilter);

		mLocalViewHideOrShowReceiver = new MLocalViewHideOrShowReceiver();
		IntentFilter mLocalViewHideOrShowReceiverIntentFilter = new IntentFilter();
		mLocalViewHideOrShowReceiverIntentFilter.addAction(GlobalConstant.ACTION_MEETING_HIDEORSHOW);
		registerReceiver(mLocalViewHideOrShowReceiver,mLocalViewHideOrShowReceiverIntentFilter);

		// 初始化视图
		InitView();
		InitData();
		//初始化统计丢包时延相关View
		initMediaStatistics();

		String string = prefs.getString("ISMCU", "False");
		if (string.equals("True")) {
			callingRemoteViewLayout_meeting.setVisibility(View.VISIBLE);
		}else {
			callingRemoteViewLayout_meeting.setVisibility(View.GONE);
		}

		// 初始化分页
		localFragment = new MeetingLocalFragment(this);
		systemFragment = new MeetingSystemFragment(this);
		// 加载分页
		localFragment.loadLocalListView(mLocalContacts);

		//设置默认摄像头为前置还是后置  1为前置，2为后置
		MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));
		mLocalView = ViERenderer.CreateLocalRenderer(this);
		//mLocalView.setBackgroundResource(R.drawable.monitor_bg2);
		//mLocalView.setZOrderOnTop(true);
		//远端视频展示SurfaceView
		mRemoteView = ViERenderer.CreateRenderer(this, 1, 0);
		//yuezs add 10-11 初始化视频控件

		mLocalView.setOnClickListener(new OnClickListener() {
			@SuppressLint("LongLogTag")
			@Override
			public void onClick(View view) {

				Log.e("Build=mode","" + Build.MODEL);
				if (Build.MODEL.equals("3280")){
					//飞鸟10寸Pad 不支持摄像头切换
					ToastUtils.showToast(MeetingActivity.this,"当前设备不支持摄像头切换");
					return;
				}

				//设置帧率 比特率 分辨率
				String bitrateString = prefs.getString(GlobalConstant.SP_VIDEO_BITRATE, "0");
				String framerateString = prefs.getString(GlobalConstant.SP_VIDEO_FRAMERATE, "0");
				String width = prefs.getString("radioWidth", "0");
				String height = prefs.getString("radioHeight", "0");
				Log.e("点击本地SurfaceView   Call界面", "bitrateString:" + bitrateString + ",framerateString:" + framerateString + ",width:" + width + ",height:" + height);

				if (bitrateString.equals("0")) {
					bitrateString = "1000";
				}
				if (framerateString.equals("0")) {
					framerateString = "20";
				}
				if (width.equals("0")) {
					width = "640";
				}
				if (height.equals("0")) {
					height = "480";
				}

				int bitrate = Integer.valueOf(bitrateString);
				int framerate = Integer.valueOf(framerateString);


//                //得到前置摄像头旋转角度
				String xzjd_q = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "0");
				String xzjd_h = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "180");
				String videoSend_Proportion = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION, "0");

				if (cameraCode.equals("2")) {
					//编码宽度，高度，帧率，码率，旋转角度，0，true   设置前置   ,Integer.valueOf(videoSend_Proportion)
					MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_q), 0, true, Integer.valueOf(videoSend_Proportion));
				} else if (cameraCode.equals("1")) {
					//编码宽度，高度，帧率，码率，旋转角度，0，true   设置后置   ,Integer.valueOf(videoSend_Proportion)
					MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_h), 0, true, Integer.valueOf(videoSend_Proportion));
				}

				// 切换前后摄像头   11/10 Sws add
				if (cameraCode.equals("1")) {
					cameraCode = "2";
				} else if (cameraCode.equals("2")) {
					cameraCode = "1";
				}

				if (cameraCode.equals("1")) {
					MediaEngine.GetInstance().ME_ChangeVideoDevice(dwSessId, Integer.valueOf(cameraCode));
				} else if (cameraCode.equals("2")) {
					MediaEngine.GetInstance().ME_ChangeVideoDevice(dwSessId, Integer.valueOf(cameraCode));
				}
			}
		});


		Log.e("*************************","*************************");
	}

	//初始化统计丢包时延相关View
	private void initMediaStatistics() {
		//统计信息根布局
		linearlayout_callInfo_meetingMCU = (LinearLayout) findViewById(R.id.linearlayout_callInfo_meetingMCU);
		//语音统计View
		tv_voice_Downlink_meetingMCU = (TextView) findViewById(R.id.voice_Downlink_meetingMCU);
		tv_voice_Uplink_meetingMCU = (TextView) findViewById(R.id.voice_Uplink_meetingMCU);
		tv_voice_Bidirectionaldelay_meetingMCU = (TextView) findViewById(R.id.voice_Bidirectionaldelay_meetingMCU);
		tv_voice_downstreambandwidth_meetingMCU = (TextView) findViewById(R.id.voice_downstreambandwidth_meetingMCU);
		tv_voice_Uplinkbandwidth_meetingMCU = (TextView) findViewById(R.id.voice_Uplinkbandwidth_meetingMCU);

		//视频统计View
		tv_video_Downlink_meetingMCU = (TextView) findViewById(R.id.video_Downlink_meetingMCU);
		tv_video_Uplink_meetingMCU = (TextView) findViewById(R.id.video_Uplink_meetingMCU);
		tv_video_Bidirectionaldelay_meetingMCU = (TextView) findViewById(R.id.video_Bidirectionaldelay_meetingMCU);
		tv_video_downstreambandwidth_meetingMCU = (TextView) findViewById(R.id.video_downstreambandwidth_meetingMCU);
		tv_video_Uplinkbandwidth_meetingMCU = (TextView) findViewById(R.id.video_Uplinkbandwidth_meetingMCU);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void createMeetingReceive(SessionCallInfos sessionCallInfos){
		Log.e("MoveYourBaby","MeetingActivity收到SessionCallInfos  EventBus事件");
		// 是否创建了会议
		if (currMeeting == null){
			Log.e("MoveYourBaby","444 return了");
			return;
		}
		// 判断是否为当前会议
//		String sid = sessionCallInfos.getCid();
//		if ((sid == null)
//				|| (sid.trim().length() <= 0)
//				|| (!currMeeting.getsID().equals(sid))){
//			return;
//		}
		// 取得创建会议结果
		boolean result = true;
		Log.e("MoveYourBaby","lizhiwei:CREATECONFE,result:"+result);

		// 创建成功
		String sessnum = sessionCallInfos.getOthernum();
		String cid = sessionCallInfos.getCid();
		currMeeting.setConferenceNo(sessnum);
		currMeeting.setcID(cid);
		Log.e("MoveYourBaby","设置cid ： " + cid);
		mDbHelper = new SQLiteHelper(MeetingActivity.this);
		try {
			mDbHelper.open();
			mDbHelper.updateMeetingRecords(currMeeting);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			MtcDelegate.log("lizhiwei:" + e.getMessage());
		}
		// 依据结果设置界面
		if (currMeeting.getConferenceType().equals(GlobalConstant.CONFERENCE_TYPE_TEMPORARY)){
			if (currMeeting.getMediaType().equals(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE)){
				// 语音会议
				Log.e("MoveYourBaby","Meeting    2732 语音会议");
				ll_List.setVisibility(View.GONE);
				rl_meeting.setVisibility(View.VISIBLE);
				// 开始计时
				mChrSessionTimeState.setBase(SystemClock.elapsedRealtime());
				mChrSessionTimeState.start();
				tv_meeting_title.setText(getResources().getString(R.string.title_meeting_voicing));
			}
			else {
				Log.e("MoveYourBaby","Meeting    2732 视频会议");
				// 视频会议
				ll_List.setVisibility(View.GONE);
				rl_meeting.setVisibility(View.VISIBLE);
				ll_meeting_camera.setVisibility(View.VISIBLE);


				mLocalView.setVisibility(View.VISIBLE);
				ll_meeting_camera.setVisibility(View.VISIBLE);

				fl_meeting_camera.addView(mLocalView);
				Log.e("MoveYourBaby","&&&&&&&&&&&&&&&&&&&&&&");

				// 开始本地视频
				MtcVideo.startLocal(1, mLocalView);
				// 开始计时
				mChrSessionTimeState.setBase(SystemClock.elapsedRealtime());
				mChrSessionTimeState.start();
				tv_meeting_title.setText(getResources().getString(R.string.title_meeting_videoing));
			}
		}
		else if (currMeeting.getConferenceType().equals(CONFERENCE_TYPE_BROADCAST)){
			// 广播
			ll_List.setVisibility(View.GONE);
			rl_meeting.setVisibility(View.VISIBLE);
			// 开始计时
			mChrSessionTimeState.setBase(SystemClock.elapsedRealtime());
			mChrSessionTimeState.start();
			tv_meeting_title.setText(getResources().getString(R.string.title_meeting_broadcasting));
		}

		// 通知刷新会议记录
		Intent intentnew = new Intent(GlobalConstant.ACTION_MEETING_REFRESH);
		intentnew.putExtra(GlobalConstant.KEY_MEETING_RECORD, currMeeting);
		sendBroadcast(intentnew);



		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 创建成功获取会议成员
		//MemberInfo(currMeeting);   转到Taking时获取成员
		Log.e("MoveYourBaby","Meeting    2732 发送获取会议成员消息");
		Log.e("MoveYourBaby", "广播执行完成");

	}

	@Override
	protected void onPause() {
		MtcDelegate.log("mCallState:"+mCallState);
		// 当前状态如果正在通话、发出、回铃、呼入中，且存在视频，则暂停视频
		if ((mCallState == GlobalConstant.CALL_STATE_TALKING
				|| mCallState == GlobalConstant.CALL_STATE_OUTGOING
				|| mCallState == GlobalConstant.CALL_STATE_ALERTED
				|| mCallState == GlobalConstant.CALL_STATE_INCOMING)
				&& MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {

			//该标识用于标识当前是否切换到Calling界面
			boolean aBoolean = prefs.getBoolean(GlobalConstant.ACTION_MEETINGSTATE, true);
			if (CallingManager.getInstance()
					.getCallingData().size() > 0) {     //判断当前是否有呼叫
				if (!aBoolean) {
					//为了解决视频单呼中 新来对讲 本地远端视频会闪烁问题

					//切换页面时，隐藏掉视频，否则会造成遮盖百度地图问题
					//Sws add 11/02
					mLocalView.setZOrderMediaOverlay(false);
					mLocalView.setZOrderOnTop(false);
					mLocalView.setVisibility(View.GONE);
					mRemoteView.setZOrderMediaOverlay(false);
					mRemoteView.setVisibility(View.GONE);
					surfaceview_meeting.setZOrderMediaOverlay(false);
					//surfaceview_meeting.setZOrderOnTop(false);
					surfaceview_meeting.setVisibility(View.GONE);
					Log.e("测试会议远端视频","onpause 隐藏   464");
				}
			}
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		//设置默认摄像头为前置还是后置  1为前置，2为后置
		cameraCode = prefs.getString(GlobalConstant.ACTION_GETCAMERA, "2");
		MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));


		//设置帧率 比特率 分辨率
		String bitrateString = prefs.getString(GlobalConstant.SP_VIDEO_BITRATE, "0");
		String framerateString = prefs.getString(GlobalConstant.SP_VIDEO_FRAMERATE, "0");
		String width = prefs.getString("radioWidth","0");
		String height = prefs.getString("radioHeight","0");
		Log.e("==Sws测试分辨率===","bitrateString:"+bitrateString+",framerateString:"+framerateString+",width:"+width+",height:"+height);

		if (bitrateString.equals("0")){
			bitrateString = "1000";
		}else if (bitrateString.equals("")){
			bitrateString = "1000";
		}

		if (framerateString.equals("0")){
			framerateString = "20";
		}else if (framerateString.equals("")){
			framerateString = "20";
		}

		if (width.equals("0")){
			width = "640";
		}
		if (height.equals("0")){
			height = "480";
		}

		int bitrate = Integer.valueOf(bitrateString);
		int framerate = Integer.valueOf(framerateString);


		//得到前置摄像头旋转角度
		String xzjd_q = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "0");
		String xzjd_h = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "180");
		String videoSend_Proportion = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION, "0");

		if (cameraCode.equals("1")){
			//编码宽度，高度，帧率，码率，旋转角度，0，true   设置前置     , Integer.valueOf(videoSend_Proportion)
			MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_q), 0, true, Integer.valueOf(videoSend_Proportion));
			Log.e("==Sws测试视频配置", "calling界面 设置了前置摄像头   xzjd_q:" + xzjd_q);
		}else if (cameraCode.equals("2")){
			//编码宽度，高度，帧率，码率，旋转角度，0，true   设置后置    , Integer.valueOf(videoSend_Proportion)
			MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_h), 0, true, Integer.valueOf(videoSend_Proportion));
			Log.e("==Sws测试视频配置", "calling界面 设置了后置摄像头 xzjd_h:" + xzjd_h);
		}

		if(mLocalView == null){
			mLocalView = ViERenderer.CreateLocalRenderer(this);
			mLocalView.setZOrderMediaOverlay(true);
			//mLocalView.setZOrderOnTop(true);

		}
		if(mRemoteView == null){
			mRemoteView = ViERenderer.CreateRenderer(this, 1, 0);
		}
		//MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, surfaceview_meeting);



		if ((mCallState == GlobalConstant.CALL_STATE_TALKING
				|| mCallState == GlobalConstant.CALL_STATE_OUTGOING
				|| mCallState == GlobalConstant.CALL_STATE_ALERTED
				|| mCallState == GlobalConstant.CALL_STATE_INCOMING)
				&& MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
			// rik.gong:
			// MeiZu M040, Coolpad 5860E,
			// localview can't auto destroy/create, so force
			// stop/start it.
//			MtcCall.Mtc_SessPreviewShow(-1, true);
//			MtcCall.Mtc_SessVideoResume(dwSessId);

			if (mLocalView instanceof GLSurfaceView)
				((GLSurfaceView) mLocalView).onResume();
			if (mRemoteView instanceof GLSurfaceView)
				((GLSurfaceView) mRemoteView).onResume();
			//切换页面时，显示视频，否则会造成遮盖百度地图问题
			mLocalView.setZOrderMediaOverlay(true);
			//mLocalView.setZOrderOnTop(true);
			mLocalView.setVisibility(View.VISIBLE);
			mRemoteView.setZOrderMediaOverlay(true);
			mRemoteView.setVisibility(View.VISIBLE);

			String string = prefs.getString("ISMCU", "False");
			if (string.equals("True")) {
				surfaceview_meeting.setZOrderMediaOverlay(true);
				//surfaceview_meeting.setZOrderOnTop(true);
				surfaceview_meeting.setVisibility(View.VISIBLE);
				Log.e("测试会议远端视频"," 显示   559");
				//解决远程视频不显示
				if ((mCallState == GlobalConstant.CALL_STATE_OUTGOING || mCallState == GlobalConstant.CALL_STATE_ALERTED || mCallState == GlobalConstant.CALL_STATE_INCOMING) && MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
					surfaceview_meeting.setVisibility(View.GONE);
					Log.e("测试会议远端视频"," 隐藏   563");

				} else if (mCallState == GlobalConstant.CALL_STATE_TALKING && MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
					surfaceview_meeting.setVisibility(View.VISIBLE);
					Log.e("测试会议远端视频"," 显示   566");

				}
			}

		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		EventBus.getDefault().unregister(this);

		// 结束会议
		EndConference(currMeeting);
		// 更新会议成员状态
		leaveConference();
		// 停止计时
		mChrSessionTimeState.stop();
		unregisterReceiver(offflineReceiver);
		unregisterReceiver(meetingmemberReceiver);
		unregisterReceiver(contactChangeReceiver);
		unregisterReceiver(createReceiver);
		unregisterReceiver(meminfoReceiver);
		unregisterReceiver(mstatechangeReceiver);
		unregisterReceiver(callingReceiver);
		unregisterReceiver(callingRecordReceiver);
		unregisterReceiver(positionRecordReceiver);

		if (handler != null){
			handler = null;
		}
		if (meetingCreateHandler != null){
			meetingCreateHandler = null;
		}


		super.onDestroy();
	}

	@Override
	public void onBackPressed() {}

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
		//mLocalView = (SurfaceView)findViewById(R.id.sfv_new);
		ll_List = (LinearLayout)findViewById(R.id.ll_List);
		tv_local = (TextView)findViewById(R.id.tv_local);
		tv_system = (TextView)findViewById(R.id.tv_system);
		tv_seleced = (TextView)findViewById(R.id.tv_seleced);
		lv_seleced = (ListView)findViewById(R.id.lv_seleced);
		btn_voice = (Button)findViewById(R.id.btn_voice);
		btn_video = (Button)findViewById(R.id.btn_video);
		btn_broadcast = (Button)findViewById(R.id.btn_broadcast);
		btn_showplate = (Button)findViewById(R.id.btn_showplate);

		rl_meeting = (RelativeLayout)findViewById(R.id.rl_meeting);
		tv_meeting_title = (TextView)findViewById(R.id.tv_meeting_title);
		mChrSessionTimeState = (Chronometer) findViewById(R.id.session_time_state);
		ll_meeting_camera = (LinearLayout)findViewById(R.id.ll_meeting_camera);
		fl_meeting_camera = (FrameLayout)findViewById(R.id.fl_meeting_camera);
		lv_members = (ListView)findViewById(R.id.lv_members);
		btn_hangup = (Button)findViewById(R.id.btn_hangup);
		btn_offline = (Button)findViewById(R.id.btn_offline);
		btn_handsfree = (Button)findViewById(R.id.btn_handsfree);
		btn_mute = (Button)findViewById(R.id.btn_mute);
		btn_member = (Button)findViewById(R.id.btn_member);

		rl_append = (RelativeLayout)findViewById(R.id.rl_append);
		tv_append_seleced = (TextView)findViewById(R.id.tv_append_seleced);
		lv_append_seleced = (ListView)findViewById(R.id.lv_append_seleced);
		lv_append_alllist = (ListView)findViewById(R.id.lv_append_alllist);
		btn_append_showplate = (Button)findViewById(R.id.btn_append_showplate);
		btn_append_ok = (Button)findViewById(R.id.btn_append_ok);
		btn_append_cancel = (Button)findViewById(R.id.btn_append_cancel);

		rl_keyboard = (RelativeLayout)findViewById(R.id.rl_keyboard);
		rl_keyboard_input = (RelativeLayout)findViewById(R.id.rl_keyboard_input);
		txt_phoneno = (EditText)findViewById(R.id.txt_phoneno);
		btn_keyboard_delete = (Button)findViewById(R.id.btn_keyboard_delete);
		btn_keyboard_delete.setOnClickListener(this);
		//keyboard_view = (android.inputmethodservice.KeyboardView)findViewById(R.id.keyboard_view);
		rl_keyboard_hidden = (RelativeLayout)findViewById(R.id.rl_keyboard_hidden);
		btn_keyboard_hideplate = (Button)findViewById(R.id.btn_keyboard_hideplate);
		btn_keyboard_complete = (Button)findViewById(R.id.btn_keyboard_complete);


		//左侧根布局
		meeting_frame = (FrameLayout) findViewById(R.id.meeting_frame);
		//初始化配置MCU模式下的View
		//最外围布局
		callingRemoteViewLayout_meeting = (RelativeLayout) findViewById(R.id.callingRemoteViewLayout_meeting);
		//视频信息根布局
		callingRemoteViewFrameInfo_meeting = (LinearLayout) findViewById(R.id.callingRemoteViewFrameInfo_meeting);
		//名称
		videoUserName_meeting = (TextView) findViewById(R.id.videoUserName_meeting);
		//号码
		videoUserNo_meeting = (TextView) findViewById(R.id.videoUserNo_meeting);
		//时长
		videoUserTime_meeting = (Chronometer) findViewById(R.id.videoUserTime_meeting);
		//远端视频展示SurfaceView
		surfaceview_meeting = (SurfaceView) findViewById(R.id.surfaceview_meeting);
		surfaceview_meeting.setZOrderMediaOverlay(true);
		//surfaceview_meeting.setBackgroundResource(R.drawable.monitor_bg2);
		//surfaceview_meeting.setZOrderOnTop(true);
		//隐藏列表
		btnGone_meeting = (Button) findViewById(R.id.btnGone_meeting);
		btnGone_meeting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				// 隐藏
				if (meeting_frame.getVisibility() == View.VISIBLE) {
					meeting_frame.setVisibility(View.GONE);
					btnGone_meeting.setText(getString(R.string.btn_position_visible));
					btnGone_meeting.setBackgroundDrawable(getResources().getDrawable(R.drawable.pos_show_click));

					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN, false);
					editor.commit();

					if (mLocalView.getVisibility() != View.GONE){
						mLocalView.setVisibility(View.GONE);
					}


				} else {
					meeting_frame.setVisibility(View.VISIBLE);
					btnGone_meeting.setText(getString(R.string.btn_position_gone));
					btnGone_meeting.setBackgroundDrawable(getResources().getDrawable(R.drawable.pos_gone_click));

					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN, true);
					editor.commit();

					if (mLocalView.getVisibility() != View.VISIBLE){
						mLocalView.setVisibility(View.VISIBLE);
					}

				}
			}
		});
		//分屏View
		btn_meetingSplitScreen = (Button) findViewById(R.id.btn_meetingSplitScreen);
		btn_meetingSplitScreen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!ptt_3g_PadApplication.isNetConnection()) {
					ToastUtils.showToast(MeetingActivity.this, getString(R.string.info_network_unavailable));
					return;
				}
				if (MainActivity.getVideoType() == GlobalConstant.VIDEO_NONE){
					// infoMonitoring();
					ToastUtils.showToast(MeetingActivity.this,"当前无视频");
					return;
				}


				btn_meetingSplitScreen.setEnabled(false);
				//显示会议分屏Dialog
				showMeetingSplitScreen();
			}
		});

		//开始解码上墙
		btn_meetingStartDecode = (Button) findViewById(R.id.btn_MeetingStartDecode);
		btn_meetingStartDecode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (ButtonUtils.isFastDoubleClick(R.id.btn_intercom,1000)){
					ToastUtils.showToast(getApplication(),"无效点击");
					return;
				}

				if (!ptt_3g_PadApplication.isNetConnection()) {
					ToastUtils.showToast(MeetingActivity.this, getString(R.string.info_network_unavailable));
					return;
				}

				Log.e("===sssss========",""+MainActivity.getVideoType());
//
//				// 判断视频会议是否进行如果进行则给出提示
				if (MainActivity.getVideoType() == GlobalConstant.VIDEO_NONE){
					// infoMonitoring();
					ToastUtils.showToast(MeetingActivity.this,"当前无视频");
					return;
				}


				Intent intent = new Intent(GlobalConstant.ACTION_MCUMEETING_STARTDecode);
				intent.putExtra("number",sipUser.getUsername());
				sendBroadcast(intent);

			}
		});


		//结束解码上墙
		btn_meetingEndDecode = (Button) findViewById(R.id.btn_MeetingEndDecode);
		btn_meetingEndDecode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!ptt_3g_PadApplication.isNetConnection()) {
					ToastUtils.showToast(MeetingActivity.this, getString(R.string.info_network_unavailable));
					return;
				}

				Intent intent = new Intent(GlobalConstant.ACTION_MCUMEETING_EndDecode);
				intent.putExtra("number",sipUser.getUsername());
				sendBroadcast(intent);
			}
		});


		//初始化数字键盘
		metting_linearlayout = (LinearLayout) findViewById(R.id.metting_linearlayout);
		btn_one = (Button) findViewById(R.id.btn_metting_one);
		btn_two = (Button) findViewById(R.id.btn_metting_two);
		btn_three = (Button) findViewById(R.id.btn_metting_three);
		btn_four = (Button) findViewById(R.id.btn_metting_four);
		btn_five = (Button) findViewById(R.id.btn_metting_five);
		btn_six = (Button) findViewById(R.id.btn_metting_six);
		btn_seven = (Button) findViewById(R.id.btn_metting_seven);
		btn_eight = (Button) findViewById(R.id.btn_metting_eight);
		btn_nine = (Button) findViewById(R.id.btn_metting_nine);
		btn_ling = (Button) findViewById(R.id.btn_metting_ling);
		btn_xing = (Button) findViewById(R.id.btn_metting_xing);
		btn_jing = (Button) findViewById(R.id.btn_metting_jing);
		btn_one.setOnClickListener(this);
		btn_two.setOnClickListener(this);
		btn_three.setOnClickListener(this);
		btn_four.setOnClickListener(this);
		btn_five.setOnClickListener(this);
		btn_six.setOnClickListener(this);
		btn_seven.setOnClickListener(this);
		btn_eight.setOnClickListener(this);
		btn_nine.setOnClickListener(this);
		btn_ling.setOnClickListener(this);
		btn_xing.setOnClickListener(this);
		btn_jing.setOnClickListener(this);


		// 设置字体
		tv_local.setTypeface(CommonMethod.getTypeface(this));
		tv_system.setTypeface(CommonMethod.getTypeface(this));
		tv_seleced.setTypeface(CommonMethod.getTypeface(this));
		btn_voice.setTypeface(CommonMethod.getTypeface(this));
		btn_video.setTypeface(CommonMethod.getTypeface(this));
		btn_broadcast.setTypeface(CommonMethod.getTypeface(this));
		btn_showplate.setTypeface(CommonMethod.getTypeface(this));

		tv_meeting_title.setTypeface(CommonMethod.getTypeface(this));
		btn_hangup.setTypeface(CommonMethod.getTypeface(this));
		btn_offline.setTypeface(CommonMethod.getTypeface(this));
		btn_handsfree.setTypeface(CommonMethod.getTypeface(this));
		btn_mute.setTypeface(CommonMethod.getTypeface(this));
		btn_member.setTypeface(CommonMethod.getTypeface(this));

		tv_append_seleced.setTypeface(CommonMethod.getTypeface(this));
		btn_append_showplate.setTypeface(CommonMethod.getTypeface(this));
		btn_append_ok.setTypeface(CommonMethod.getTypeface(this));
		btn_append_cancel.setTypeface(CommonMethod.getTypeface(this));

		txt_phoneno.setTypeface(CommonMethod.getTypeface(this));
		btn_keyboard_hideplate.setTypeface(CommonMethod.getTypeface(this));
		btn_keyboard_complete.setTypeface(CommonMethod.getTypeface(this));
		// 设置侦听
		tv_local.setOnClickListener(new MyOnClickListener(0));
		tv_system.setOnClickListener(new MyOnClickListener(1));

		// 语音通话
		btn_voice.setOnClickListener(new BtnOnClickListener());
		// 视频通话
		btn_video.setOnClickListener(new BtnOnClickListener());
		// 广播
		btn_broadcast.setOnClickListener(new BtnOnClickListener());

		// 挂断
		btn_hangup.setOnClickListener(new BtnOnClickListener());
		// 离线
		btn_offline.setOnClickListener(new BtnOnClickListener());
		// 免提
		btn_handsfree.setOnClickListener(new BtnOnClickListener());
		// 静音
		btn_mute.setOnClickListener(new BtnOnClickListener());
		// 成员
		btn_member.setOnClickListener(new BtnOnClickListener());

		// 邀请入会-显示键盘
		btn_append_showplate.setOnClickListener(new BtnOnClickListener());
		// 邀请入会-确定
		btn_append_ok.setOnClickListener(new BtnOnClickListener());
		// 邀请入会-取消
		btn_append_cancel.setOnClickListener(new BtnOnClickListener());

		// 显示键盘
		btn_showplate.setOnClickListener(new BtnOnClickListener());
		// 手工输入电话号，不弹出系统键盘
		txt_phoneno.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				txt_phoneno.setInputType(InputType.TYPE_NULL);
				return false;
			}});
		// 隐藏键盘
		btn_keyboard_hideplate.setOnClickListener(new BtnOnClickListener());
		// 键盘输入完成
		btn_keyboard_complete.setOnClickListener(new BtnOnClickListener());
	}

	private List<String> splitScreenList = new ArrayList<>();
	//Map集合 作用为设置成员显示在具体的某个分屏上（当前没有这种需求 且本身逻辑不同 暂时先传空集合）
	private HashMap<String,Integer> hashMap = new HashMap<String, Integer>();

	//MCU会议分屏操作Method
	private void showMeetingSplitScreen() {

		//加载数据
		new Thread(){
			@Override
			public void run() {
				super.run();

				if (splitScreenList.size() > 0){
					splitScreenList.clear();
				}
				splitScreenList.add("1");
				splitScreenList.add("2");
				splitScreenList.add("3");
				splitScreenList.add("4");
				splitScreenList.add("5+1");
				splitScreenList.add("9");
				splitScreenList.add("8+2");
				splitScreenList.add("12+1");
				splitScreenList.add("16");
			}
		}.start();


		View inflate = LayoutInflater.from(this).inflate(R.layout.layout_meetingsplitscreen, null);

		builder = new AlertDialog.Builder(this);
		builder.setView(inflate);
		alertDialog = builder.create();
		alertDialog.show();

		btn_meetingSplitScreen.setEnabled(true);
		//MCU会议分屏选择View
		spinner_meetingsplitscreen_type = (Spinner) inflate.findViewById(R.id.spinner_meetingsplitscreen_type);
		//确定View
		Button btn_meetingSplitScreen_Determine = (Button) inflate.findViewById(R.id.btn_meetingSplitScreen_Determine);
		//取消View
		Button btn_meetingSplitScreen_Cancel = (Button) inflate.findViewById(R.id.btn_meetingSplitScreen_Cancel);

		//设置数据
		ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,splitScreenList);
		//设置样式
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_meetingsplitscreen_type.setAdapter(arrayAdapter);

		btn_meetingSplitScreen_Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (alertDialog.isShowing()){
					alertDialog.dismiss();
				}
			}
		});

		//确认View点击事件
		btn_meetingSplitScreen_Determine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.e("测试会议MCU分屏","CID:" +String.valueOf(currMeeting.getcID()));

				boolean splitScreenResult = MediaEngine.GetInstance().ME_ConfSetVideoScreenLayout_Async(String.valueOf(currMeeting.getcID()),
						spinner_meetingsplitscreen_type.getSelectedItem().toString(),
						hashMap);


				for (int i = 0; i < currMeeting.getMeetingMembers().size(); i++){
					Log.e("测试会议MCU分屏","成员号码:" + currMeeting.getMeetingMembers().get(i).getBuddyNo());
				}
				if (splitScreenResult){
					ToastUtils.showToast(MeetingActivity.this, "设置分屏指令发送成功");

					if (alertDialog.isShowing()){
						alertDialog.dismiss();
					}

				return;
				}
				ToastUtils.showToast(MeetingActivity.this, "设置分屏指令发送失败,请检查网络");

				if (alertDialog.isShowing()){
					alertDialog.dismiss();
				}
			}
		});
	}

	// 初始化视图
	private void InitData(){
		/*// 处理视频界面
		DealVideoItmes();*/
		// 取得本地联系人列表
		this.getPhoneContacts();
		this.getSystemGroups();

		// 设置Tab页
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.fragment_meeting_local, null));
		listViews.add(mInflater.inflate(R.layout.fragment_meeting_system, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		// 设置选中成员(会议前选中与会成员，点击删除与会成员)
		selecedAdapter = new MeetingSelectedListAdapter(this, selectedContacts, lv_seleced);
		lv_seleced.setAdapter(selecedAdapter);
		lv_seleced.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Contacts contact = selectedContacts.get(position);
				// 踢出与会人员
				selectedContacts.remove(position);
				// 更新本地联系人会议状态
				for (Contacts contacts : mLocalContacts){
					if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
						contacts.setInmeeting(false);
					}
				}
				// 更新系统联系人会议状态
				for (GroupInfo groupinfo : mSystemGroups){
					if ((groupinfo.getListMembers() != null)
							&& (groupinfo.getListMembers().size() > 0)){
						for (MemberInfo memberinfo : groupinfo.getListMembers()){
							if (memberinfo.getNumber().equals(contact.getBuddyNo())){
								memberinfo.setInmeeting(false);
							}
						}
					}
				}
				// 刷新列表
				localFragment.refreshContactsList(mLocalContacts);
				//systemFragment.refreshContactsList(mSystemGroups);
				systemFragment.refreshExpandleView();
				// 刷新选中
				lv_seleced.setAdapter(selecedAdapter);
				// 刷新与会成员
				lv_members.setAdapter(memberAdapter);
				Log.e("---------创建视频会议2222---------", "1243   selectedContacts.size:" + selectedContacts.size());
				// 刷新追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
			}

		});

		// 设置参会成员(点击调取视频)
		memberAdapter = new MeetingMemberListAdapter(MeetingActivity.this,this, selectedContacts, lv_members);
		lv_members.setAdapter(memberAdapter);
		Log.e("---------创建视频会议2222---------", "1256   selectedContacts.size:" + selectedContacts.size());
		lv_members.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}

				if (currMeeting == null){
					ToastUtils.showToast(MeetingActivity.this, "当前会议为空！");
					return;
				}
				//判断当前是否为MCU会议  如果是 那么Return
				String string = prefs.getString("ISMCU", "True");
				if (string.equals("True")){
					Log.e("点击点击点击","retunr  了");
					return;
				}

				if (currMeeting.getMediaType() != GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO){
					Log.e("当前会议类型"," 当前会议类型不是视频会议  点击事件return ");
					return;
				}


				Contacts contact = selectedContacts.get(position);
				if (contact == null){
					return;
				}
				// 判断是否为视频会议，选中的是否视频设备
				// 将联系人转为监控用户
				MonitorInfo monitorinfo = new MonitorInfo();
				monitorinfo.setNumber(contact.getBuddyNo());
				monitorinfo.setName(contact.getBuddyName());
				monitorinfo.setType(GlobalConstant.MONITOR_USERTYPE_3GINTERCOM);
				monitorinfo.setStatus(GlobalConstant.MONITOR_MEMBER_OFFLINE);
				monitorinfo.setIsmonitor(false);
				monitorinfo.setInmonitoring(false);
				monitorinfo.setSid(currMeeting.getsID());
				monitorinfo.setCid(currMeeting.getcID());
				// 通知视频展示
				Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
				callIntent.putExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO, monitorinfo);
				sendBroadcast(callIntent);
				Log.e("获取会议成员视频MeetingActivity", "**********获取会议成员视频MeetingActivity NO:"+contact.getBuddyNo()+",CID:"+currMeeting.getcID());

			}
		});

		memberAdapter.setGetPositionInterface(new MeetingMemberListAdapter.GetPositionInterface() {
			@Override
			public void getnumber(int position) {

				Contacts contact = selectedContacts.get(position);
				if (contact == null){
					return;
				}

				Intent intent = new Intent(GlobalConstant.ACTION_CAMERA_CLOUDCONTROL);
				intent.putExtra("number", contact.getBuddyNo());
				sendBroadcast(intent);
				Log.e("======广播已发送","======广播已发送 " + contact.getBuddyNo());
			}
		});


		// 设置追加所有联系人列表(会议过程中点击成员，追加参会成员)
		appendAllListAdapter = new MeetingAllListAdapter(this, mAllContacts, lv_append_alllist);
		lv_append_alllist.setAdapter(appendAllListAdapter);
		lv_append_alllist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Contacts contact = mAllContacts.get(position);
				if (contact.isInmeeting()){
					return;
				}
				contact.setInmeeting(true);
				// 添加与会人员
				boolean exists = false;
				for (Contacts contacts : tempContacts){
					if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
						exists = true;
						break;
					}
				}
				if (exists){
					return;
				}
				tempContacts.add(contact);
				// 刷新追加列表
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
			}});

		// 设置追加选中成员
		appendSelecedAdapter = new MeetingSelectedListAdapter(this, tempContacts, lv_append_seleced);
		lv_append_seleced.setAdapter(appendSelecedAdapter);
		lv_append_seleced.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Contacts contact = tempContacts.get(position);
				contact.setInmeeting(false);
				// 更新联系人会议状态
				for (Contacts contacts : mAllContacts){
					if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
						contacts.setInmeeting(false);
						break;
					}
				}
				tempContacts.remove(position);

				// 追加列表
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
			}

		});
	}


	// 页面中按钮点击事件统一处理
	private class BtnOnClickListener implements OnClickListener{



		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_voice){


				if (ButtonUtils.isFastDoubleClick(R.id.btn_voice)){
					ToastUtils.showToast(MeetingActivity.this, "无效点击！");
					return;
				}


				//判断网络
				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}

				boolean iceIsConnection = MediaEngine.GetInstance().ME_ServerAvailable();
				Log.e("MoveYourBaby","ME_GetConfigByKeys 判断当前是否配置MCU   ICE连接状态 : " + iceIsConnection);
				if (!iceIsConnection){
					//当前ICE连接失败
					Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
					intent2.putExtra("meetingType","Error2");
					sendBroadcast(intent2);
					return ;
				}

				if (currentMeeting){
					ToastUtils.showToast(MeetingActivity.this,"会议发起失败 请等待上次会议结束");
					return;
				}


				//为了防止发起呼叫过于频繁
				if(ptt_3g_PadApplication != null){

					if(ptt_3g_PadApplication.isWetherClickCall() == false){

						ptt_3g_PadApplication.setWetherClickCall(true);
						boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
						if(wetherTimerWorking == true){
							ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
							//返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
							return;
						}else{
							//返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
							ptt_3g_PadApplication.startTimer();
						}
					}
					else
					{
						ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
						return;
					}
				}

				Log.e("*****发起语音************", "*****************发起语音************");

				// 判断选择的与会人员
				if ((selectedContacts == null)
						|| (selectedContacts.size() <= 0)){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_empty_member));
					return;
				}
				if (CallingManager.getInstance().getCallingCount() >= 4){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_route_busy));
					return;
				}
				//btn_voice.setEnabled(false);

				//如果当前已经有单呼，禁止发起新的单呼
				CallingInfo callingCalling = null;
				CallingInfo callingMeeting = null;
				CallingInfo callingIntercom = null;
				for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
					if ((!callingInfo.isHolding())){
						if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
							callingCalling = callingInfo;
						}
						if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING){
							callingMeeting = callingInfo;
						}
                        if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM){
                            callingIntercom = callingInfo;
                        }
					}
				}
				if(callingCalling != null)
				{
					showMeetingIfContinue(callingCalling,selectedContacts,GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
					return;
				}
				if(callingMeeting != null){
					showMeetingIfContinue(callingMeeting,selectedContacts,GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
					return;
				}
                if(callingIntercom != null){
					showMeetingIfContinue(callingIntercom,selectedContacts,GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
					return;
				}

				//判断当前会议成员是否只选中自己
				if (selectedContacts.size() == 0){
					if (selectedContacts.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}


				// 创建会议
				currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
				if (currMeeting == null){
					return;
				}
			}
			else if (v.getId() == R.id.btn_video){

				Log.e("MoveYourBaby","点击视频会议");
				if (ButtonUtils.isFastDoubleClick(R.id.btn_video)){
					ToastUtils.showToast(MeetingActivity.this, "无效点击！");
					return;
				}
				Log.e("MoveYourBaby","开始执行MeetingCreateAsync");

				boolean iceIsConnection = MediaEngine.GetInstance().ME_ServerAvailable();
				Log.e("MoveYourBaby","ME_GetConfigByKeys 判断当前是否配置MCU   ICE连接状态 : " + iceIsConnection);
				if (!iceIsConnection){
					//当前ICE连接失败
					Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
					intent2.putExtra("meetingType","Error2");
					sendBroadcast(intent2);
					return ;
				}
				if (currentMeeting){
					ToastUtils.showToast(MeetingActivity.this,"会议发起失败 请等待上次会议结束");
					return;
				}

				//new MeetingCreateAsync().execute();
				new MeetingCreateAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
			}
			else if (v.getId() == R.id.btn_broadcast){

				if (ButtonUtils.isFastDoubleClick(R.id.btn_broadcast,1000)){
					return;
				}

				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}

				boolean iceIsConnection = MediaEngine.GetInstance().ME_ServerAvailable();
				Log.e("MoveYourBaby","ME_GetConfigByKeys 判断当前是否配置MCU   ICE连接状态 : " + iceIsConnection);
				if (!iceIsConnection){
					//当前ICE连接失败
					Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
					intent2.putExtra("meetingType","Error2");
					sendBroadcast(intent2);
					return ;
				}

				if (currentMeeting){
					ToastUtils.showToast(MeetingActivity.this,"会议发起失败 请等待上次会议结束");
					return;
				}

				//为了防止发起呼叫过于频繁
				if(ptt_3g_PadApplication != null)
				{
					if(ptt_3g_PadApplication.isWetherClickCall() == false)
					{
						ptt_3g_PadApplication.setWetherClickCall(true);
						boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
						if(wetherTimerWorking == true)
						{
							ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
							//返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
							return;
						}
						else
						{
							//返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
							ptt_3g_PadApplication.startTimer();
						}
					}
					else
					{
						ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
						return;
					}
				}

				// 判断选择的与会人员
				if ((selectedContacts == null)
						|| (selectedContacts.size() <= 0)){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_empty_member));
					return;
				}
				if (CallingManager.getInstance().getCallingCount() >= 4){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_route_busy));
					return;
				}

				//如果当前已经有单呼，禁止发起新的单呼
				CallingInfo callingCalling = null;
				CallingInfo callingMeeting = null;
				CallingInfo callingIntercom = null;
				for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
					if ((!callingInfo.isHolding())){
						if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
							callingCalling = callingInfo;
						}
						if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING){
							callingMeeting = callingInfo;
						}
                        if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM){
                            callingIntercom = callingInfo;
						}
					}
				}
				if(callingCalling != null)
				{
					showMeetingIfContinue(callingCalling,selectedContacts, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
					return;
				}
				if(callingMeeting != null){
					showMeetingIfContinue(callingMeeting,selectedContacts, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
					return;
				}
                if(callingIntercom != null){
                    showMeetingIfContinue(callingIntercom,selectedContacts, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
                    return;
                }
				btn_broadcast.setEnabled(false);
				//判断当前会议成员是否只选中自己
				if (selectedContacts.size() == 0){
					if (selectedContacts.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}


				// 创建广播
				currMeeting = CreateConference(selectedContacts, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
				if (currMeeting == null){
					return;
				}
				Log.e("---------创建广播---------", "结果：" +currMeeting );
			}
			else if (v.getId() == R.id.btn_hangup){

				if (ButtonUtils.isFastDoubleClick(R.id.btn_hangup,1000)){
					return;
				}

				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}

				/*Intent holdIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLUNHOLD);
				holdIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
				sendBroadcast(holdIntent);*/
				if (currMeeting == null){
					ToastUtils.showToast(MeetingActivity.this, "当前会议为空！");
					return;
				}

				//清空会议联系人
				PTTService.hashMap.clear();
				//清除选中联系人
				selectedContacts.clear();

				// TODO: 2018/12/7 当前通话结束 通知添加地图
				Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
				intentS.putExtra("AddOrRemove","ADD");
				sendBroadcast(intentS);

				CallingManager.getInstance().removeCallingInfo(dwSessId);
				CallingManager.getInstance().removeAnswerInfo(dwSessId);

				//会议结束 发送广播结束当前以上墙的视频
				Intent closeDecoderVideo = new Intent(GlobalConstant.DECODINGTHEWALLRECEIVER);
				closeDecoderVideo.putExtra(GlobalConstant.ISALL,GlobalConstant.ISALL_YES);
				sendBroadcast(closeDecoderVideo);
				Log.e("2018-09-03","发送结束上墙视频广播");


				// 结束通话
				Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
				callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
				sendBroadcast(callIntent);
				// 结束会议(通知更新视频)
				Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
				sendBroadcast(meetIntent);
//				Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
//				intentS.putExtra("AddOrRemove","ADD");
//				sendBroadcast(intentS);

				// 更新会议成员状态
				leaveConference();

				if (surfaceview_meeting.getVisibility() != View.GONE){
					surfaceview_meeting.setVisibility(View.GONE);
				}


				// 结束会议
				EndConference(currMeeting);

			}
			else if (v.getId() == R.id.btn_offline){
				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}
				if (currMeeting == null){
					ToastUtils.showToast(MeetingActivity.this, "当前会议为空！");
					return;
				}

				// 保持
//				/*Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHOLD);
//				callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
//				sendBroadcast(callIntent);*/

				String str = btn_offline.getText().toString();
				if(str.equals("保持")){
					Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHOLD);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
					sendBroadcast(callIntent);
					btn_offline.setText("解除保持");
				}
				else{
					Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLUNHOLD);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
					sendBroadcast(callIntent);
					btn_offline.setText("保持");
				}
			}
			else if (v.getId() == R.id.btn_handsfree){
				// 免提
				if(audioManager.isSpeakerphoneOn()){
					btn_handsfree.setText("免提");
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							CloseSpeaker();
						}
					};
					ThreadPoolProxy.getInstance().execute(runnable);
				}else{
					btn_handsfree.setText("取消免提");
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							OpenSpeaker();
						}
					};
					ThreadPoolProxy.getInstance().execute(runnable);
				}
			}
			else if (v.getId() == R.id.btn_mute){
				// 静音
				if (Build.MODEL.equals("3280")) {
					//飞鸟10寸Pad 无法静音 只能通过修改发送语音增益来达到静音效果
					if (btn_mute.getText().toString().trim().equals("静音")) {
						MediaEngine.GetInstance().ME_SetTxLevel(dwSessId,0);
						btn_mute.setText("取消静音");
					} else if (btn_mute.getText().toString().trim().equals("取消静音")) {
						MediaEngine.GetInstance().ME_SetTxLevel(dwSessId,1);
						btn_mute.setText("静音");
					}
					return;
				}

				//判断当前是否处于静音状态
				boolean mute = audioManager.isMicrophoneMute();
				if(!mute){
					btn_mute.setText("取消静音");
					audioManager.setMicrophoneMute(true);
					Log.e("集体测试","设置麦克风 meeting 1386  静音");

				}else{
					btn_mute.setText("静音");
					audioManager.setMicrophoneMute(false);
					Log.e("集体测试","设置麦克风 meeting 1393  取消静音");
				}
			}
			else if (v.getId() == R.id.btn_member){
				// 成员
				// 清空临时
				tempContacts.clear();
				if ((selectedContacts != null)
						&& (selectedContacts.size() > 0)){
					tempContacts.addAll(selectedContacts);
				}
				// 刷新追加成员
				for (Contacts contact : tempContacts){
					for (Contacts allcontact : mAllContacts){
						if (contact.getBuddyNo().equals(allcontact.getBuddyNo())){
							allcontact.setInmeeting(true);
							break;
						}
					}
				}
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);

				// 设置页面
				rl_append.setVisibility(View.VISIBLE);
				rl_meeting.setVisibility(View.GONE);
				//隐藏本地视频
				if (mLocalView.getVisibility() != View.GONE){
					mLocalView.setVisibility(View.GONE);
				}
//				/*CustomDialog.Builder builder = new CustomDialog.Builder(MeetingActivity.this);
//				builder.setMessage(getString(R.string.info_intercom_new_incoming1));
//				builder.setTitle(getString(R.string.info_intercom_new_incoming));
//				builder.setPositiveButton(getString(R.string.btn_calling_answer), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				builder.setNegativeButton(getString(R.string.btn_calling_decline),
//						new android.content.DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				WaitingDialog informing = builder.create();
//				informing.setCancelable(false);
//				informing.show();*/
			}
			else if (v.getId() == R.id.btn_append_showplate){
				// 邀请入会-显示键盘
				rl_keyboard.setVisibility(View.VISIBLE);
				rl_keyboard_input.setVisibility(View.VISIBLE);
				//getKeyboardUtil().showKeyboard();
				metting_linearlayout.setVisibility(View.VISIBLE);

				rl_keyboard_hidden.setVisibility(View.VISIBLE);
			}
			else if (v.getId() == R.id.btn_append_ok){
				if (currMeeting == null){
					ToastUtils.showToast(MeetingActivity.this, "当前会议为空！");
					return;
				}
				// 邀请入会-确定
				// 处理选中数据
				//SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
				ArrayList<Contacts> delContacts = new ArrayList<Contacts>();
				ArrayList<Contacts> addContacts = new ArrayList<Contacts>();
				for(Contacts contact : selectedContacts){
					boolean exists = false;
					for(Contacts temp : tempContacts){
						if (contact.getBuddyNo().equals(temp.getBuddyNo())){
							exists = true;
							break;
						}

					}
					if (!exists){
						delContacts.add(contact);
					}
				}
				for(Contacts contact : tempContacts){
					boolean exists = false;
					for(Contacts selected : selectedContacts){
						if (contact.getBuddyNo().equals(selected.getBuddyNo())){
							exists = true;
							break;
						}
					}
					if (!exists){
						addContacts.add(contact);
					}
				}
				// 删除添加选择的与会成员
				selectedContacts.removeAll(delContacts);
				selectedContacts.addAll(addContacts);
				// 通知删除与会人员
				for(Contacts contact : delContacts) {
					// 请出会议
					for (Contacts con : mLocalContacts) {
						if (con.getBuddyNo().equals(contact.getBuddyNo())) {
							con.setInmeeting(false);
						}
					}
					for (GroupInfo group : mSystemGroups) {
						if ((group.getListMembers() == null)
								|| (group.getListMembers().size() <= 0)) {
							continue;
						}
						for (MemberInfo memberinfo : group.getListMembers()) {
							if (memberinfo.getNumber().equals(contact.getBuddyNo())) {
								memberinfo.setInmeeting(false);
							}
						}
					}
					if (!contact.getBuddyNo().equals(sipUser.getUsername()) && !contact.getBuddyNo().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())) {
						DelMember(currMeeting, contact);
					}
				}
				for(Contacts contact : addContacts){
					// 加入会议
					for (Contacts con : mLocalContacts){
						if (con.getBuddyNo().equals(contact.getBuddyNo())){
							con.setInmeeting(true);
						}
					}
					for (GroupInfo group : mSystemGroups){
						if ((group.getListMembers() == null)
								|| (group.getListMembers().size() <= 0)){
							continue;
						}
						for(MemberInfo memberinfo : group.getListMembers()){
							if (memberinfo.getNumber().equals(contact.getBuddyNo())){
								memberinfo.setInmeeting(true);
							}
						}
					}
					AddMember(currMeeting, contact);
				}
				tempContacts.clear();

				// 刷新联系人列表
				localFragment.refreshContactsList(mLocalContacts);
				systemFragment.refreshContactsList(mSystemGroups);
				// 刷新选中成员
				lv_seleced.setAdapter(selecedAdapter);
				// 刷新与会成员
				lv_members.setAdapter(memberAdapter);
				// 刷新追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
				// 此处应该为点击确定后进行添加删除的操作，需要建立临时数组
				rl_append.setVisibility(View.GONE);
				rl_meeting.setVisibility(View.VISIBLE);
				if (mLocalView.getVisibility() == View.GONE){
					mLocalView.setVisibility(View.VISIBLE);
				}

			}
			else if (v.getId() == R.id.btn_append_cancel){
				// 刷新追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 邀请入会-取消
				rl_append.setVisibility(View.GONE);
				rl_meeting.setVisibility(View.VISIBLE);
				if (mLocalView.getVisibility() == View.GONE){
					mLocalView.setVisibility(View.VISIBLE);
				}
			}
			else if (v.getId() == R.id.btn_showplate){
				// 显示键盘
				rl_keyboard.setVisibility(View.VISIBLE);
				rl_keyboard_input.setVisibility(View.VISIBLE);
				//getKeyboardUtil().showKeyboard();
				metting_linearlayout.setVisibility(View.VISIBLE);

				rl_keyboard_hidden.setVisibility(View.VISIBLE);
			}
			else if (v.getId() == R.id.btn_keyboard_hideplate){
				// 隐藏键盘
				rl_keyboard_input.setVisibility(View.GONE);
				//getKeyboardUtil().hideKeyboard();
				metting_linearlayout.setVisibility(View.GONE);
				rl_keyboard_hidden.setVisibility(View.GONE);
				rl_keyboard.setVisibility(View.GONE);
				sb.setLength(0);
				txt_phoneno.setText(sb.toString());
			}
			else if (v.getId() == R.id.btn_keyboard_complete){
				// 键盘输入完成
				// 验证输入长度
				if (txt_phoneno.getText().toString().trim().length() <= 0){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_empty_phoneno));
					return;
				}
				// 创建成员
				Contacts contact = new Contacts();
				contact.setBuddyNo(txt_phoneno.getText().toString().trim());
				contact.setBuddyName(txt_phoneno.getText().toString().trim());
				contact.setInmeeting(true);
				// 邀请加入会议
				if (rl_append.getVisibility() != View.VISIBLE){
					// 判断是否为本地联系人
					for(Contacts local : mLocalContacts){
						if (local.getBuddyNo().equals(contact.getBuddyNo())){
							local.setInmeeting(true);
							contact = local;
						}
					}
					// 判断是否为系统联系人
					boolean exists = false;
					for (GroupInfo groupinfo : mSystemGroups){
						if ((groupinfo.getListMembers() != null)
								&& (groupinfo.getListMembers().size() > 0)){
							for (MemberInfo memberinfo : groupinfo.getListMembers()){
								if (memberinfo.getNumber().equals(contact.getBuddyNo())){
									memberinfo.setInmeeting(true);
									contact.setMeetingState(memberinfo.getMeetingState());
									contact.setMemberType(memberinfo.getMemberType());
									exists = true;
								}
							}
						}
					}
					// 在非会议状态
					exists = false;
					for(Contacts selected : selectedContacts){
						if (selected.getBuddyNo().equals(contact.getBuddyNo())){
							exists = true;
							break;
						}
					}
					if (!exists){
						selectedContacts.add(contact);
					}
					// 刷新列表
					localFragment.refreshContactsList(mLocalContacts);
					systemFragment.refreshContactsList(mSystemGroups);
					// 刷新追加成员
					getAllContacts();
					lv_append_alllist.setAdapter(appendAllListAdapter);
					// 刷新选中成员
					lv_seleced.setAdapter(selecedAdapter);
					// 刷新与会成员
					lv_members.setAdapter(memberAdapter);
				}
				else {
					// 在会议状态
					boolean exists = false;
					for(Contacts selected : tempContacts){
						if (selected.getBuddyNo().equals(contact.getBuddyNo())){
							exists = true;
							break;
						}
					}
					if (exists){
						return;
					}
					for (Contacts contacts : mAllContacts){
						if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
							contacts.setInmeeting(true);
							break;
						}
					}
					tempContacts.add(contact);
					// 刷新追加列表
					lv_append_alllist.setAdapter(appendAllListAdapter);
					// 刷新追加选中
					lv_append_seleced.setAdapter(appendSelecedAdapter);
				}
				// 页面设置
				rl_keyboard_input.setVisibility(View.GONE);
				//getKeyboardUtil().hideKeyboard();
				metting_linearlayout.setVisibility(View.GONE);
				rl_keyboard_hidden.setVisibility(View.GONE);
				rl_keyboard.setVisibility(View.GONE);
				sb.setLength(0);
				txt_phoneno.setText(sb.toString());
			}
		}
	};

	// 分页适配器
	public class MyPagerAdapter extends PagerAdapter
	{
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
	public class MyOnClickListener implements OnClickListener
	{
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (index == 0){
				mPager.setCurrentItem(0);
				tv_local.setTextColor(getResources().getColor(R.color.contact_title_selected));
				tv_system.setTextColor(getResources().getColor(R.color.contact_title_unselected));
			}
			else {
				mPager.setCurrentItem(1);
				tv_local.setTextColor(getResources().getColor(R.color.contact_title_unselected));
				tv_system.setTextColor(getResources().getColor(R.color.contact_title_selected));
			}
		}
	};

	// 页面改变事件
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
	{
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				tv_local.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
				tv_system.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				break;
			case 1:
				if(!isLoadPage2){
					isLoadPage2 = systemFragment.loadSystemListView(mSystemGroups);
				}
				tv_local.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_unseleted));
				tv_system.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.tab_seleted));
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

	// 创建会议
	@SuppressLint("SimpleDateFormat")
	private MeetingRecords CreateConference(ArrayList<String> nos, int conferenceType, int mediaType)
	{
//        int callingCount = CallingManager.getInstance().getCallingCount();
//        if (callingCount > 0){
//            List<CallingInfo> callingData = CallingManager.getInstance().getCallingData();
//            for (CallingInfo callingInfo : callingData){
//                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM){
//                    MediaEngine.GetInstance().ME_Hangup(callingInfo.getDwSessId());
//                }
//            }
//        }
        if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			return null;
		}
		//if (mLocalView == null){
			//mLocalView = ViERenderer.CreateLocalRenderer(this);
			//mLocalView.setZOrderOnTop(true);
		//}



		if (surfaceview_meeting == null){
			//远端视频展示SurfaceView
			surfaceview_meeting = (SurfaceView) findViewById(R.id.surfaceview_meeting);
			//surfaceview_meeting.setZOrderOnTop(true);
		}

		String string = prefs.getString("ISMCU", "True");
		if (string.equals("True")){
			MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, surfaceview_meeting);
		}else {
			MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, mRemoteView);
		}


		Log.e("===uri","111111111111111111111");
		MeetingRecords result = null;
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		//SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		// 设置会议名称
		Calendar calendar = Calendar.getInstance();
		String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String sessname = (CommonMethod.getCurrLongCNDate(calendar)+"会议").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		SimpleDateFormat shortformat = new SimpleDateFormat("HHmm");
		if (conferenceType == CONFERENCE_TYPE_BROADCAST){
			sessname = "广播" + shortformat.format(calendar.getTime());
		}
		else if (conferenceType == GlobalConstant.CONFERENCE_TYPE_TEMPORARY){
			sessname = "临时会议" + shortformat.format(calendar.getTime());
		}
		Log.e("===uri","111111111111122222222222222");
		// 记录会议历史
		MeetingRecords record = new MeetingRecords();
		record.setId(0);
		record.setUserNo(sipUser.getUsername());
		record.setsID(sid);
		record.setStartDate(calendar.getTime());
		record.setConferenceNo(sid);
		record.setConferenceNm(sessname);
		record.setIsMonitor(0);
		record.setConferenceType(conferenceType);
		record.setMediaType(mediaType);
		//record.setcID("");
		record.setStopDate(calendar.getTime());
		record.setDuration(0);
		record.setTime("00:00:00");

		String members = sipUser.getUsername() + ",";
		List<MeetingMembers> meetingmembers = new ArrayList<MeetingMembers>();
		for(String no : nos){
			members += no + ",";
			meetingmembers.add(new MeetingMembers(no, calendar.getTime(), calendar.getTime()));
		}

        String[] member = new String[nos.size()];
        for (int i = 0; i < nos.size(); i++){
            member[i] = nos.get(i);
        }



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
			MtcDelegate.log("lizhiwei:" + e.getMessage());
		}
		Log.e("===uri","22222222222222");
		// 发送广播到主页面告诉其创建的会议为本地创建，请求自动接听
		Intent intent = new Intent(GlobalConstant.ACTION_MEETING_CREATE);
		intent.putExtra(GlobalConstant.KEY_MEETING_RECORD, result);
		sendBroadcast(intent);

		/*// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}*/
		String msgBody = "req:createconfe\r\n" +
				"sid:"+sid+"\r\n" +
				"employeeid:"+sipUser.getUsername()+"\r\n" +
				"sessname:"+sessname+"\r\n" +
				"sessnum:"+sid+"\r\n" +
				"members:"+members+"\r\n" +
				"calltype:"+conferenceType+"\r\n" +
				"mediatype:"+mediaType+"\r\n";
		/*MtcDelegate.log("lizhiwei:send,req:createconfe," + uri + "," + msgBody);
     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_CREATECONFE
     			, uri
     			, 1
     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
     			, msgBody);*/

		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
		Log.e("===uri",uri);
		Log.e("===uri",sipUser.getUsername());
		//MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
		//根据mediaType判断会议类型（语音or视频）
		if (mediaType == GlobalConstant.CONFERENCE_MEDIATYPE_VOICE){
			if (conferenceType == GlobalConstant.CONFERENCE_TYPE_BROADCAST){
				//发起广播
				MediaEngine.GetInstance().ME_CreateConf(member,false, MediaEngine.ME_ConfCallType.broadcast);
			}else {
				//发起语音会议
				MediaEngine.GetInstance().ME_CreateConf(member,false, MediaEngine.ME_ConfCallType.conf);
			}
		}else {
			//发起视频会议
			MediaEngine.GetInstance().ME_CreateConf(member,true,MediaEngine.ME_ConfCallType.conf);
		}
		// 播放声音
		MtcRing.startRingBack(MeetingActivity.this, "ringback.wav");
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	private MeetingRecords CreateConference(List<Contacts> contacts, int conferenceType, int mediaType)
	{
		Log.e("MoveYourBaby","进入CreateConference contacts.size() : " + contacts.size()
				+ "\r\n" + "conferenceType : " + conferenceType
				+ "\r\n" + "mediaType : " + mediaType
		);


        int callingCount = CallingManager.getInstance().getCallingCount();
        Log.e("=-==============","************************** : " + callingCount);

        //判断当前是否为MCU会议
		String string = prefs.getString("ISMCU", "True");
		if (string.equals("True")){
			if (mediaType == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
				meetingCreateHandler.sendEmptyMessage(2);
				Log.e("MoveYourBaby","发送msg2");
			}
		}

		Log.e("会议", "执行创建会议方法");
		if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			Log.e("MoveYourBaby","return 2164");
			return null;
		}


		MeetingRecords result = null;
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		//SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		// 设置会议名称
		Calendar calendar = Calendar.getInstance();
		String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String sessname = (CommonMethod.getCurrLongCNDate(calendar)+"会议").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		SimpleDateFormat shortformat = new SimpleDateFormat("HHmm");
		if (conferenceType == CONFERENCE_TYPE_BROADCAST){
			sessname = "广播" + shortformat.format(calendar.getTime());
		}
		else if (conferenceType == GlobalConstant.CONFERENCE_TYPE_TEMPORARY){
			sessname = "临时会议" + shortformat.format(calendar.getTime());
		}
		// 记录会议历史
		MeetingRecords record = new MeetingRecords();
		record.setId(0);
		record.setUserNo(sipUser.getUsername());
		record.setsID(sid);
		record.setStartDate(calendar.getTime());
		record.setConferenceNo(sid);
		record.setConferenceNm(sessname);
		record.setIsMonitor(0);
		record.setConferenceType(conferenceType);
		record.setMediaType(mediaType);
		//record.setcID("");
		record.setStopDate(calendar.getTime());
		record.setDuration(0);
		record.setTime("00:00:00");

		String members= "";//sipUser.getUsername() + ",";
		List<MeetingMembers> meetingmembers = new ArrayList<MeetingMembers>();
		for(Contacts contact : contacts){
			members += contact.getBuddyNo() + ",";
			meetingmembers.add(new MeetingMembers(contact.getBuddyNo(), calendar.getTime(), calendar.getTime()));
		}
		boolean exitSipUser = false;
		for(int i = 0;i < meetingmembers.size();i ++){
			if(sipUser.getUsername().equals(meetingmembers.get(i).getBuddyNo())){
				exitSipUser = true;
			}
		}
		if(!exitSipUser){
			meetingmembers.add(new MeetingMembers(sipUser.getUsername(), calendar.getTime(), calendar.getTime()));
		}
		record.setMeetingMembers(meetingmembers);
		// 将记录保存至数据库
		mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			long meetingid = mDbHelper.createMeetingRecords(record);
			result = mDbHelper.getMeetingRecordsByID((int) meetingid);
			mDbHelper.closeclose();
			Log.e("MoveYourBaby","将记录保存至数据库 成功");
		}
		catch (Exception e) {
			Log.e("MoveYourBaby","将记录保存至数据库 出现异常 ErrorMsg ：" + e.getMessage());
			MtcDelegate.log("lizhiwei:" + e.getMessage());
		}

		// 发送广播到主页面告诉其创建的会议为本地创建，请求自动接听
		Intent intent = new Intent(GlobalConstant.ACTION_MEETING_CREATE);
		intent.putExtra(GlobalConstant.KEY_MEETING_RECORD, result);
		sendBroadcast(intent);
		Log.e("MoveYourBaby","发送广播到主页面告诉其创建的会议为本地创建，请求自动接听");
		String[] member = new String[contacts.size()];
		for (int i = 0; i < contacts.size(); i++){
			member[i] = contacts.get(i).getBuddyNo();


		}
		Log.e("MoveYourBaby","成员数量 : " + member.length);
//		String msgBody = "req:createconfe\r\n" +
//				"sid:"+sid+"\r\n" +
//				"employeeid:"+sipUser.getUsername()+"\r\n" +
//				"sessname:"+sessname+"\r\n" +
//				"sessnum:"+sid+"\r\n" +
//				"members:"+members+"\r\n" +
//				"calltype:"+conferenceType+"\r\n" +
//				"mediatype:"+mediaType+"\r\n";
//
//		Log.e("会议", "=============会议开始=========================");
//		//String uri="8888888";
//		String uri = sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
		//MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
		//根据mediaType判断会议类型（语音or视频）
		String createMeetingResult = "";

		if (mediaType == GlobalConstant.CONFERENCE_MEDIATYPE_VOICE){
			if (conferenceType == GlobalConstant.CONFERENCE_TYPE_BROADCAST){
				//发起广播
				createMeetingResult = MediaEngine.GetInstance().ME_CreateConf(member,false, MediaEngine.ME_ConfCallType.broadcast);
				Log.e("MoveYourBaby"," 发起广播  ME_CreateConf");

			}else {
				//发起语音会议
				createMeetingResult = MediaEngine.GetInstance().ME_CreateConf(member,false, MediaEngine.ME_ConfCallType.conf);
				Log.e("MoveYourBaby"," 发起语音会议  ME_CreateConf");

			}
		}else {
			//发起视频会议
			createMeetingResult = MediaEngine.GetInstance().ME_CreateConf(member,true,MediaEngine.ME_ConfCallType.conf);
			Log.e("MoveYourBaby"," 发起 视频会议  ME_CreateConf");
		}
		Log.e("====Sws测试===会议移除","createMeetingResult : " + createMeetingResult);
		if (createMeetingResult==null||createMeetingResult.length()==0||createMeetingResult.equals("")){
			//通知主界面更换布局
			Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
			intent2.putExtra("meetingType","Error");
			sendBroadcast(intent2);
			btn_broadcast.setEnabled(true);
			Log.e("MoveYourBaby","会议发起失败");
			Log.e("MoveYourBaby", "=============================创建会议执行完毕==============================");
			return result;
		}
		Log.e("MoveYourBaby","会议发起成功");
		Log.e("MoveYourBaby", "=============================创建会议执行完毕==============================");
		Log.e("会议", sipUser.getUsername());
		// 播放声音
		MtcRing.startRingBack(MeetingActivity.this, "ringback.wav");

		//得到当前分屏数
		String currentSpliCount = ptt_3g_PadApplication.getCurrentSpliCount();

		int selectedContactsCount = member.length;
		if (selectedContactsCount > 0 && selectedContactsCount < 2){
			if (!currentSpliCount.equals("One")) {
				//通知切换为1分屏
//				splitScreen = "One";
//				Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
//				switchSolitScreen.putExtra("splitScreen", splitScreen);
//				sendBroadcast(switchSolitScreen);
				Log.e("分屏Test","设置1分屏");
			}
		}else if (selectedContactsCount > 1 && selectedContactsCount < 4){
			if (currentSpliCount.equals("One")){
				//通知切换为4分屏
				splitScreen = "Four";
				Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
				switchSolitScreen.putExtra("splitScreen", splitScreen);
				sendBroadcast(switchSolitScreen);
				Log.e("分屏Test","设置4分屏");
			}
		}else if (selectedContactsCount > 4){
			if (!currentSpliCount.equals("Nine")){
				//通知切换为9分屏
				splitScreen = "Nine";
				Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
				switchSolitScreen.putExtra("splitScreen", splitScreen);
				sendBroadcast(switchSolitScreen);
				Log.e("分屏Test","设置9分屏");
			}
		}
		Log.e("分屏Test","selectedContactsCount :  " + selectedContactsCount + "\r\n" + "currentSpliCount : " + currentSpliCount);
		Log.e("分屏Test","****************************结束********************************");



		Log.e("测试会议切屏","会议发起 : " + splitScreen);
		if (result != null){
			Log.e("MoveYousssssrBaby","会议创建完成");
		}
		return result;
	}

	// 视频推送
	private void PushVideo(MeetingRecords record)
	{
		if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			return;
		}


		if (record == null){
			return;
		}

		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		//SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

		/*// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}*/
		String msgBody = "req:push_video\r\n" +
				"sid:"+record.getsID()+"\r\n" +
				"cid:"+record.getcID()+"\r\n" +
				"dstid:"+sipUser.getUsername()+"\r\n" +
				"streamid:\r\n" +
				"receivers:\r\n" +
				"flag:1\r\n";
		/*MtcDelegate.log("lizhiwei:send,req:push_video," + uri + "," + msgBody);
     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_ADDMEM
     			, uri
     			, 1
     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
     			, msgBody);*/
		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
		MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
		Log.e("====Sws测试会议成员离会"," 推送视频   msgBody： "+msgBody);
		//ptt_3g_PadApplication.setPushVideoNum(sipUser.getUsername());
	}

	// 邀请入会
	private void AddMember(MeetingRecords record, Contacts contact)
	{
		if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			return;
		}
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		// 邀请入会时间
		Calendar calendar = Calendar.getInstance();
		MeetingMembers meetingmember = new MeetingMembers(record.getId(), contact.getBuddyNo(), calendar.getTime(), GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO, calendar.getTime());
		// 将记录保存至数据库
		mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			long meetingid = mDbHelper.createMeetingMembers(meetingmember);
			meetingmember.setId((int) meetingid);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			MtcDelegate.log("lizhiwei:" + e.getMessage());
		}

		List<MeetingMembers> meetingMembers = record.getMeetingMembers();

		meetingMembers.add(meetingmember);

		/*// 发送创建会议请求
 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
 		// 配置服务器IP
    	if (!uri.contains(sipServer.getServerIp())){
    		uri += sipServer.getServerIp();
    	}*/
		//MtcDelegate.log("lizhiwei:send,ind:addmem,"+uri);
		/*     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_ADDMEM
     			, uri
     			, 1
     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
     			, "ind:addmem\r\n" +
 					"cid:"+record.getcID()+"\r\n" +
					"employeeid:"+contact.getBuddyNo()+"\r\n" +
					"answertype:"+GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO+"\r\n");*/
		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
		String msgBody="ind:addmem\r\n" +
				"cid:"+record.getcID()+"\r\n" +
				"employeeid:"+contact.getBuddyNo()+"\r\n" +
				"answertype:"+GlobalConstant.CONFERENCE_ANSWERTYPE_AUTO+"\r\n";
		//MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
		MediaEngine.GetInstance().ME_ConfAddMember_Async(record.getcID(),contact.getBuddyNo());
	}

	// 请出会议
	private void DelMember(MeetingRecords record, Contacts contact)
	{
		if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			return;
		}
		Log.e("====Sws测试===会议移除","====进入移除会议Method");
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		// 请出入会时间
		Calendar calendar = Calendar.getInstance();
		Log.e("====Sws测试===会议移除","contact.getBuddyName()："+contact.getBuddyName());
		Log.e("====Sws测试===会议移除","contact.getBuddyNo()："+contact.getBuddyNo());

		List<MeetingMembers> meetingMembers = record.getMeetingMembers();
		MeetingMembers meetingmember = null;
		for(MeetingMembers member : meetingMembers){
			Log.e("====Sws测试===会议移除","member.getBuddyNo()："+member.getBuddyNo());
			Log.e("====Sws测试===会议移除","member.getId()："+member.getId());
			if (member.getBuddyNo().equals(contact.getBuddyName())){
				meetingmember = member;
				Log.e("====Sws测试===会议移除","meetingmember = member;");
				break;
			}
			else  if(member.getBuddyNo().equals(contact.getBuddyNo())){

				meetingmember = member;
				Log.e("====Sws测试===会议移除","meetingmember = member;");
				break;
			}
		}

		if (meetingmember == null){
		Log.e("====Sws测试===会议移除","meetingmember为空：return掉了");
				return;
		}

		if (meetingmember != null){
			Log.e("====Sws测试===会议移除","meetingmember不是null");
			meetingmember.setStopDate(calendar.getTime());
			// 将记录保存至数据库
			mDbHelper = new SQLiteHelper(this);
			try {
				mDbHelper.open();
				mDbHelper.updateMeetingMembers(meetingmember);
				mDbHelper.closeclose();
			}
			catch (Exception e) {
				MtcDelegate.log("lizhiwei:" + e.getMessage());
			}
		}
		record.getMeetingMembers().remove(meetingmember);

		Log.e("====Sws测试===会议移除","meetingmember.getBuddyNo():"+meetingmember.getBuddyNo());
		String msgBody="ind:delmem\r\n" +
				"cid:"+record.getcID()+"\r\n" +
				"employeeid:"+meetingmember.getBuddyNo()+"\r\n";
		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
		//MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
		MediaEngine.GetInstance().ME_ConfDeleteMember_Async(record.getcID(),contact.getBuddyNo());
	}

	// 结束会议
	private void EndConference(MeetingRecords record)
	{
		/*if(!ptt_3g_PadApplication.isNetConnection()){
			return;
		}*/
		if (currMeeting == null){
			return;
		}


		// 停止播放音效
		MtcRing.stop();
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		// 请出入会时间
		Calendar calendar = Calendar.getInstance();
		record.setStopDate(calendar.getTime());
		int dur = FormatController.getSecondByDate(record.getStartDate(), record.getStopDate());
		record.setDuration(dur);
		record.setTime(FormatController.secToTime(dur));

		/*record.setDuration((int) (SystemClock.elapsedRealtime() - mChrSessionTimeState.getBase()));
    	record.setTime(mChrSessionTimeState.getText().toString());*/

		if (record.getMeetingMembers() != null && record.getMeetingMembers().size() > 0){
			for(MeetingMembers meetingmember : record.getMeetingMembers()){
				meetingmember.setStopDate(calendar.getTime());
			}
		}
		// 将记录保存至数据库
		mDbHelper = new SQLiteHelper(this);
		try {
			mDbHelper.open();
			mDbHelper.updateMeetingRecords(record);
			mDbHelper.closeclose();
		}
		catch (Exception e) {
			MtcDelegate.log("lizhiwei:" + e.getMessage());
		}

//		/*// 发送创建会议请求
// 		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
// 		// 配置服务器IP
//    	if ((sipServer.getServerIp() != null)
//    		&& (!uri.contains(sipServer.getServerIp()))){
//    		uri += sipServer.getServerIp();
//    	}
//    	if (uri == null){
//    		return;
//    	}
//    	MtcDelegate.log("lizhiwei:send,ind:endconfe,"+uri);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_DELMEM
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, "ind:endconfe\r\n" +
// 					"cid:"+record.getcID()+"\r\n");*/
//		String msgBody="ind:endconfe\r\n" +
//				"cid:"+record.getcID()+"\r\n";
//		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
//		//MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
//		Log.e("测试会议结束","结束cid ： " + record.getcID());
		//结束会议
		MediaEngine.GetInstance().ME_DeleteConf_Async(record.getcID());
		// 通知主页面

		// 设置页面效果
		if ((currMeeting.getConferenceType().equals(GlobalConstant.CONFERENCE_TYPE_TEMPORARY))
				&& (currMeeting.getMediaType().equals(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO))){
			// 设置本地视频
			//Sws add 11/02
			mLocalView.setVisibility(View.INVISIBLE);
			mLocalView.setOnTouchListener(null);
			// 视频会议
			ll_meeting_camera.setVisibility(View.GONE);
			// 删除地视频
			fl_meeting_camera.removeAllViews();
		}
		// 设置界面
		ll_List.setVisibility(View.VISIBLE);
		rl_meeting.setVisibility(View.GONE);
		// 设置可操作
		btn_voice.setEnabled(true);
		btn_video.setEnabled(true);
		btn_broadcast.setEnabled(true);
		// 停止计时
		mChrSessionTimeState.stop();
		mChrSessionTimeState.setText("");
		tv_meeting_title.setText("");
		Log.e("%%%%%%MeetingA%%", "%%%%%%currMeeting:" + currMeeting);
		currMeeting = null;

		//会议结束 关闭免提 开始麦克风
		if(audioManager.isSpeakerphoneOn()) {
			btn_handsfree.setText("免提");
			CloseSpeaker();
		}
		btn_mute.setText("静音");
		audioManager.setMicrophoneMute(false);

	}

	// 获取会议成员
	private void MemberInfo(final MeetingRecords record)
	{
		Log.e("====Sws测试===会议移除"," cid : " + record.getcID() + "\r\n" + "sid : " + record.getsID());
		if(!ptt_3g_PadApplication.isNetConnection()){
			ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
			return;
		}
		if (record == null){
			return;
		}
		// 取得登录用户信息
		//SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		//SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
//		Log.e("获取会议成员", "获取会议成员="+"req:meminfo\r\n" +
//				"cid:"+record.getcID()+"\r\n" +
//				"employeeid:"+sipUser.getUsername()+"\r\n");
//		String msgBody="req:meminfo\r\n" +
//				"cid:"+record.getcID()+"\r\n" +
//				"employeeid:"+sipUser.getUsername()+"\r\n";
//		String uri=sipUser.getUsername()+"@"+sipServer.getServerIp()+":"+sipServer.getPort();
//		MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
//		MediaEngine.GetInstance().ME_ConfGetMembers_Async(record.getcID(), new MediaEngine.ME_ConfGetMembers_CallBack() {
//			@Override
//			public void onCallBack(MediaEngine.ME_ConfUserInfo[] me_confUserInfos) {
//				ArrayList<Contacts> contacts = new ArrayList<Contacts>();
//				ArrayList<String> contactsNo = new ArrayList<String>();
//				for (MediaEngine.ME_ConfUserInfo meminfo : me_confUserInfos) {
//					if ((meminfo == null)) {
//						continue;
//					}
//					Contacts contact = new Contacts();
//					contact.setBuddyNo(meminfo.userId);
//					contact.setBuddyName(meminfo.name);
//					contact.setMemberType(meminfo.userType);
//					contact.setMeetingState(meminfo.userState);
//
//					if (!contactsNo.contains(meminfo.userId)) {
//						contactsNo.add(meminfo.userId);
//						contacts.add(contact);
//					}
//				}
//				Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
//				intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
//				intent.putExtra(GlobalConstant.KEY_MEETING_CID, me_confUserInfos[0].sessionId);
//				intent.putParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO, contacts);
//				sendBroadcast(intent);
//				Log.e("测试会议成员","结果返回  成功  发送广播");
//
//				Log.e("====Sws测试===会议移除","   dwSessId："  + dwSessId);
//			}
//		});

		boolean result = MediaEngine.GetInstance().ME_ConfGetMembers_Async(record.getcID(), new MediaEngine.ME_ConfGetMembers_CallBack() {
			@Override
			public void onCallBack(MediaEngine.ME_ConfUserInfo[] me_confUserInfos) {

				Log.e("====Sws测试===会议移除","获取成功    :");
                if ((me_confUserInfos == null)
                        || (me_confUserInfos.length <= 0)) {
                    return;
                }
                ArrayList<Contacts> contacts = new ArrayList<Contacts>();
                ArrayList<String> contactsNo = new ArrayList<String>();
                for (MediaEngine.ME_ConfUserInfo meminfo : me_confUserInfos) {
                    if ((meminfo == null)
                            || (meminfo.userId.length() <= 0)) {
                        continue;
                    }

					if (meminfo.userState == GlobalConstant.GROUP_MEMBER_ONLINE
							|| meminfo.userState == GlobalConstant.GROUP_MEMBER_RELEASE
							|| meminfo.userState == GlobalConstant.GROUP_MEMBER_EMPTY
							|| meminfo.userState == GlobalConstant.GROUP_MEMBER_INIT
							){
						//当前成员状态为离会 不做处理
						continue;
					}

                    Log.e("====Sws测试===会议移除","获取成功   meminfoUserId :" + meminfo.userId);
                    Log.e("====Sws测试===会议移除","结果返回  成功  meminfo.name:" + meminfo.name);
                    Contacts contact = new Contacts();
                    contact.setBuddyNo(meminfo.userId);
                    contact.setBuddyName(meminfo.name);
                    contact.setMemberType(meminfo.userType);
                    contact.setMeetingState(meminfo.userState);

                    if (!contactsNo.contains(meminfo.userId)) {
                        contactsNo.add(meminfo.userId);
                        contacts.add(contact);
                    }


                }
                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                intent.putExtra(GlobalConstant.KEY_MEETING_CID, record.getcID());
                intent.putParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO, contacts);
                sendBroadcast(intent);


			}
		});

		Log.e("====Sws测试===会议移除","获取result     : " + result);

	}

	// 登出接收器
	public class LogoutReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			finish();
		}
	}

	// 强迫下线接收器
	public class ForcedOfflineReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 结束会议
			//			EndConference(currMeeting);
			// 更新会议成员状态
			//			leaveConference();
			// 停止计时
			//			mChrSessionTimeState.stop();
		}
	}

	// 与会成员改变广播接收器
	public class MeetingMemberReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equals(GlobalConstant.ACTION_MEETING_REMEMBERINFO)){
				//Log.e("接收断网重连","重连后准备获取会议成员");
				// 断网重连后获取会议成员
				MemberInfo(currMeeting);
				Log.e("测试会议获取成成员","2348  收到获取会议成员广播");
				Log.e("测试会议成员","Meeting    2362 发送获取会议成员消息");

			}else if(action.equals(GlobalConstant.ACTION_MEETING_MEMBER)){
				// 从tab页中过来的不需要再次刷新tab页
				Boolean inorde = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_INORDE, true);
				Contacts contact = intent.getParcelableExtra(GlobalConstant.KEY_MEETING_MEMBERINFO);
				if (inorde){
					// 添加与会人员
					boolean exists = false;
					for (Contacts contacts : selectedContacts){
						if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
							exists = true;
							break;
						}
					}
					if (exists){
						return;
					}
					selectedContacts.add(contact);
					// 更新本地联系人与会状态
					for (Contacts contacts : mLocalContacts){
						if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
							contacts.setInmeeting(true);
						}
					}
					// 更新系统联系人与会状态
					for (GroupInfo groupinfo : mSystemGroups){
						if ((groupinfo.getListMembers() != null)
								&& (groupinfo.getListMembers().size() > 0)){
							for (MemberInfo memberinfo : groupinfo.getListMembers()){
								if (memberinfo.getNumber().equals(contact.getBuddyNo())){
									memberinfo.setInmeeting(true);
								}
							}
						}
					}
				}
				else {
					// 减少与会人员
					List<Contacts> contactex = new ArrayList<Contacts>();
					for (Contacts contacts : selectedContacts){
						if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
							contactex.add(contacts);
							break;
						}
					}
					if (contactex.size() <= 0){
						return;
					}
					selectedContacts.removeAll(contactex);
					// 与会状态设置
					for (Contacts contacts : mLocalContacts){
						if (contacts.getBuddyNo().equals(contact.getBuddyNo())){
							contacts.setInmeeting(false);
						}
					}
					// 更新系统联系人会议状态
					for (GroupInfo groupinfo : mSystemGroups){
						if ((groupinfo.getListMembers() != null)
								&& (groupinfo.getListMembers().size() > 0)){
							for (MemberInfo memberinfo : groupinfo.getListMembers()){
								if (memberinfo.getNumber().equals(contact.getBuddyNo())){
									memberinfo.setInmeeting(false);
								}
							}
						}
					}
				}
				// 刷新列表
				localFragment.loadLocalListView(mLocalContacts);
				//systemFragment.loadSystemListView(mSystemGroups);
				// 刷新选中成员
				lv_seleced.setAdapter(selecedAdapter);
				// 刷新与会成员
				lv_members.setAdapter(memberAdapter);
				Log.e("---------创建视频会议2222---------", "2883   selectedContacts.size:" + selectedContacts.size());
				// 刷新追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
			}
		}
	}

	// 联系人处理添加、修改、删除
	public class ContactChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String callAction = intent.getAction();

			if(callAction.equals(GlobalConstant.ACTION_CONTACT_CHANGE)){
				Log.e("******会议联系人刷新**********", "*********会议联系人刷新**********");
				getPhoneContacts();
				getSystemGroups();
				localFragment.refreshContactsList(mLocalContacts);
				systemFragment.refreshContactsList(mSystemGroups);
				// 追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
			}else if(callAction.equals(GlobalConstant.ACTION_MEETING_PUSHVIDEO)){
				// 是否创建了会议
				if (currMeeting == null){
					return;
				}
				memberAdapter.notifyDataSetChanged();
			}
		}

	}

	// 创建会议结果广播接收器
	public class CreateBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}

	// 获取会议成员结果广播接收器
	public class MeminfoBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.e("测试会议成员","Meeting 界面  收到广播");

			if (currMeeting == null){
				Log.e("测试会议成员","currMeeting == null   return");
				return;
			}
			boolean result = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_RESULT, false);
			String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);

			Log.e("测试会议成员","Meeting 界面  收到广 播   cid:" + cid + "\n" + "result :" + result);
			Log.e("测试会议成员","获取cid ： " + cid);
			Log.e("测试会议成员","获取currMeeting.getcID() ： " + currMeeting.getcID());
//			 此处没有CID
			if ((cid == null)
					|| (cid.trim().length() <= 0)
					|| (!cid.trim().equals(currMeeting.getcID()))){
				return;
			}

			if (!result){
				String error= "";
				String dis = "";
				error = intent.getStringExtra(GlobalConstant.KEY_MEETING_ERROR);
				dis =  intent.getStringExtra(GlobalConstant.KEY_MEETING_DIS);
				Log.e("0 获取会议成员错误^^^^^", "^^error:" + error +"---dis:" + dis);

				if(!ptt_3g_PadApplication.isNetConnection()){
					ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
					return;
				}
				// 结束通话
				Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
				callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
				sendBroadcast(callIntent);
				// 结束会议(通知更新视频)
				Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
				sendBroadcast(meetIntent);

				// 更新会议成员状态
				leaveConference();

			}else{

				selectedContacts.clear();

				ArrayList<Contacts> contacts = intent.getParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO);
				Log.e("测试会议成员","Meeting 界面  收到广播  contacts.size():" + contacts.size());
				Log.e("*********获取会议成员结**", "*********获取会议成员结果广播接收器*********"+selectedContacts.size());
				MtcDelegate.log("lizhiwei 会议成员接收"+selectedContacts.size());
				List<Contacts> offlineMemList = new ArrayList<Contacts>();
				// 通知更新状态
				for(Contacts contact : contacts){
					// 更新本地联系人信息
					for(Contacts selectedcontact : mLocalContacts){
						if (contact.getBuddyNo().equals(selectedcontact.getBuddyNo())){
							selectedcontact.setBuddyName(contact.getBuddyName());
							selectedcontact.setMemberType(contact.getMemberType());
							selectedcontact.setMeetingState(contact.getMeetingState());
							selectedcontact.setInmeeting(true);
						}
						else {
							selectedcontact.setInmeeting(false);

						}
					}

					// 更新系统联系人信息
					for (GroupInfo groupinfo : mSystemGroups){
						if ((groupinfo.getListMembers() != null)
								&& (groupinfo.getListMembers().size() > 0)){
							for (MemberInfo memberinfo : groupinfo.getListMembers()){
								if (memberinfo.getNumber().equals(contact.getBuddyNo())){
									memberinfo.setName(contact.getBuddyName());
									memberinfo.setMemberType(contact.getMemberType());
									memberinfo.setMeetingState(contact.getMeetingState());
									memberinfo.setInmeeting(true);
								}
								else {
									memberinfo.setInmeeting(false);

								}
							}
						}
					}
					Log.e("测试会议成员","Meeting 界面  收到广播 2820");
					if(contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY ||
							contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT ||
							contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_RELEASE ||
							contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_WITHDRAW){
						offlineMemList.add(contact);
					}

					// 更新选中联系人信息(普通会议需要将原来会议成员清空)
					if (selectedContacts.size() > 0){
						/*for(Contacts selectedcontact : selectedContacts){
						if (contact.getBuddyNo().equals(selectedcontact.getBuddyNo())){
							selectedcontact.setBuddyName(contact.getBuddyName());
							selectedcontact.setMemberType(contact.getMemberType());
							selectedcontact.setMeetingState(contact.getMeetingState());
							break;
						}
					}*/
						selectedContacts.clear();
					}
				}
				Log.e("测试会议成员","Meeting 界面  收到广播 2841");

				if(contacts.size() > 0 && offlineMemList.size() > 0){
					contacts.removeAll(offlineMemList);
				}
				// 临时会议
				if (selectedContacts.size() <= 0){
					selectedContacts.addAll(contacts);
				}
				String println = "";
				for(Contacts selectedcontact : selectedContacts){
					println += selectedcontact.getBuddyNo() + "," + selectedcontact.getBuddyName() + "," + selectedcontact.getMemberType() + "," + selectedcontact.getMeetingState() + ";";
				}
				MtcDelegate.log("lizhiwei 选中的会议成员列表"+println);

				// 刷新列表
				localFragment.refreshContactsList(mLocalContacts);
				systemFragment.refreshContactsList(mSystemGroups);
				// 刷新选中成员
				lv_seleced.setAdapter(selecedAdapter);
				Log.e("测试会议成员","Meeting 界面  收到广播 2861  selectedContacts.size():" + selectedContacts.size());
				// 刷新与会成员
				lv_members.setAdapter(memberAdapter);
				memberAdapter.notifyDataSetChanged();
				Log.e("---------创建视频会议2222---------", "3061   selectedContacts.size:" + selectedContacts.size());
				// 刷新追加列表
				getAllContacts();
				lv_append_alllist.setAdapter(appendAllListAdapter);
				appendAllListAdapter.notifyDataSetChanged();
				// 刷新追加选中
				lv_append_seleced.setAdapter(appendSelecedAdapter);
				appendSelecedAdapter.notifyDataSetChanged();

				Log.e("测试会议成员","Meeting 界面  刷新列表完成");
			}
		}
	}

	// 会议成员状态变化通知事件广播接收器
	public class MstatechangeBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("====Sws测试会议成员","MeetingActivity收到广播");
			if (currMeeting == null){
				Log.e("====Sws测试会议成员","当前会议为空");
				return;
			}
			Log.e("====Sws测试会议成员","当前会议不为空");
			String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);
			Log.e("====Sws测试会议成员","cid:"+cid);
			String name = intent.getStringExtra(GlobalConstant.KEY_MEETING_NAME);
			Log.e("====Sws测试会议成员","name:"+name);
			String employeeid = intent.getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);
			Log.e("====Sws测试会议成员","employeeid:"+employeeid);
			int type = intent.getIntExtra(GlobalConstant.KEY_MEETING_TYPE, 0);
			Log.e("====Sws测试会议成员","type:"+type);
			int state = intent.getIntExtra(GlobalConstant.KEY_MEETING_STATE, 0);
			Log.e("====Sws测试会议成员","state:"+state);
			if ((cid == null)
					|| (cid.trim().length() <= 0)
					|| (currMeeting == null)
					|| (currMeeting.getcID() == null)
					|| (!cid.trim().equals(currMeeting.getcID()))){
				Log.e("====Sws测试===会议移除","cid为空 return掉了");
				return;
			}
			Log.e("====Sws测试===会议移除","cid不为空 ");
			Log.w("====Sws测试===会议移除", "###成员状态：" + state + "---employeeid：" + employeeid);


			// 更新本地联系人信息
			for(Contacts contact : mLocalContacts){
				if (contact.getBuddyNo().equals(employeeid)){
					contact.setBuddyName(name);
					contact.setMemberType(type);
					contact.setMeetingState(state);
					if (((state == GlobalConstant.GROUP_MEMBER_WITHDRAW)
							|| (state == GlobalConstant.GROUP_MEMBER_RELEASE)
							|| (state == GlobalConstant.GROUP_MEMBER_EMPTY)
							|| (state == GlobalConstant.GROUP_MEMBER_INIT))
							&& (!employeeid.equals(CommonMethod.getInstance().getSipUserFromPrefs(prefs).getUsername()))){
						contact.setInmeeting(false);
					}else if(state == GlobalConstant.GROUP_MEMBER_TALKING
							|| state == GlobalConstant.GROUP_MEMBER_INCOMING
							|| state == GlobalConstant.GROUP_MEMBER_OUTGOING){
						contact.setInmeeting(true);
					}
				}
			}
			// 更新系统联系人信息
			for (GroupInfo groupinfo : mSystemGroups){
				if ((groupinfo.getListMembers() != null)
						&& (groupinfo.getListMembers().size() > 0)){
					for (MemberInfo memberinfo : groupinfo.getListMembers()){
						if (memberinfo.getNumber().equals(employeeid)){
							memberinfo.setName(name);
							memberinfo.setMemberType(type);
							memberinfo.setMeetingState(state);
							if (((state == GlobalConstant.GROUP_MEMBER_WITHDRAW)
									|| (state == GlobalConstant.GROUP_MEMBER_RELEASE)
									|| (state == GlobalConstant.GROUP_MEMBER_EMPTY)
									|| (state == GlobalConstant.GROUP_MEMBER_INIT))
									&& (!employeeid.equals(CommonMethod.getInstance().getSipUserFromPrefs(prefs).getUsername()))){
								memberinfo.setInmeeting(false);
							}else if(state == GlobalConstant.GROUP_MEMBER_TALKING
									|| state == GlobalConstant.GROUP_MEMBER_INCOMING
									|| state == GlobalConstant.GROUP_MEMBER_OUTGOING
									|| state == GlobalConstant.GROUP_MEMBER_ALERTED){
								memberinfo.setInmeeting(true);

							}
						}
					}
				}
			}
			Contacts contactMem = null;
			// 更新选中联系人信息
			for(Contacts contact : selectedContacts){
				if (contact.getBuddyNo().equals(employeeid)){
					/*contact.setBuddyName(name);*/
					contact.setMemberType(type);
					contact.setMeetingState(state);
					contactMem = contact;
					break;
				}
			}

			// 如果已经退会则删除视频
			if (((state == GlobalConstant.GROUP_MEMBER_WITHDRAW)
					|| (state == GlobalConstant.GROUP_MEMBER_RELEASE)
					|| (state == GlobalConstant.GROUP_MEMBER_EMPTY)
					|| (state == GlobalConstant.GROUP_MEMBER_INIT))
					&& (!employeeid.equals(CommonMethod.getInstance().getSipUserFromPrefs(prefs).getUsername()))){
				// 通知移除视频
				Intent memwithdrawIntent = new Intent(GlobalConstant.ACTION_MEETING_MEMWITHDRAW);
				memwithdrawIntent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, employeeid);
				sendBroadcast(memwithdrawIntent);
				Log.e("====Sws测试会议成员离会","MeetingActivity===2588=== 发送GlobalConstant.ACTION_MEETING_MEMWITHDRAW广播通知移除视频 employeeid:"+employeeid);
				if(null != contactMem && selectedContacts.contains(contactMem)){
					selectedContacts.remove(contactMem);
				}

				//成员离会后推送视频改为自己
				String pushNumString = ptt_3g_PadApplication.getPushVideoNum();
				if(pushNumString.equals(employeeid)){
					//判断当前是否为MCU会议
					String string = prefs.getString("ISMCU", "True");
					if (!string.equals("True")) {
						// 发送推送视频消息
						PushVideo(currMeeting);
					}
					Log.e("====Sws测试会议成员离会","成员离会后推送视频改为自己   已执行     ptt_3g_PadApplication.getPushVideoNum()："+ptt_3g_PadApplication.getPushVideoNum());
				}
			}else if(state == GlobalConstant.GROUP_MEMBER_TALKING
					|| state == GlobalConstant.GROUP_MEMBER_INCOMING
					|| state == GlobalConstant.GROUP_MEMBER_OUTGOING
					|| state == GlobalConstant.GROUP_MEMBER_ALERTED){
				if(null == contactMem){
					contactMem = new Contacts();
					contactMem.setBuddyNo(employeeid);
					contactMem.setBuddyName(name);
					contactMem.setMeetingState(state);
					contactMem.setMemberType(type);
					selectedContacts.add(contactMem);
				}
			}

			//计算当前成员数量切换分屏
			initSendSwitchSpli();

			// 刷新列表
			localFragment.refreshContactsList(mLocalContacts);
			systemFragment.refreshContactsList(mSystemGroups);

			/*lv_seleced.setAdapter(selecedAdapter);
			lv_members.setAdapter(memberAdapter);
			lv_append_alllist.setAdapter(appendAllListAdapter);
			lv_append_seleced.setAdapter(appendSelecedAdapter);*/

			// 刷新选中成员
			selecedAdapter.notifyDataSetChanged();
			// 刷新与会成员
			memberAdapter.notifyDataSetChanged();
			getAllContacts();
			// 刷新追加列表
			appendAllListAdapter.notifyDataSetChanged();
			// 刷新追加选中
			appendSelecedAdapter.notifyDataSetChanged();

			contactMem = null;



		}
	}

	//计算当前成员数量切换分屏
	private void initSendSwitchSpli() {

		String string = prefs.getString("ISMCU", "True");
		if (string.equals("True")) {
			//TODO 如果是MCU会议不进行下面操作
			return;
		}
//		int selectedContactsCount = selectedContacts.size();
//
//
//		if (selectedContactsCount > 0 && selectedContactsCount < 2){
//			//通知切换为1分屏
//			splitScreen = "One";
//		}else if (selectedContactsCount > 1 && selectedContactsCount < 4){
//			//通知切换为4分屏
//			splitScreen = "Four";
//		}else if (selectedContactsCount > 4){
//			//通知切换为9分屏
//			splitScreen = "Nine";
//		}
//
//		meetingCreateHandler.sendEmptyMessage(6);
//得到当前分屏数
		String currentSpliCount = ptt_3g_PadApplication.getCurrentSpliCount();

		int selectedContactsCount = selectedContacts.size();
		if (selectedContactsCount > 0 && selectedContactsCount < 2){
			if (!currentSpliCount.equals("One")) {
				if (split){
					split = false;
					//通知切换为1分屏
					splitScreen = "One";
					Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
					switchSolitScreen.putExtra("splitScreen", splitScreen);
					sendBroadcast(switchSolitScreen);
					Log.e("分屏Test","设置1分屏");
				}

			}
		}else if (selectedContactsCount > 1 && selectedContactsCount < 4){
			if (currentSpliCount.equals("One")){
				//通知切换为4分屏
				splitScreen = "Four";
				Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
				switchSolitScreen.putExtra("splitScreen", splitScreen);
				sendBroadcast(switchSolitScreen);
				Log.e("分屏Test","设置4分屏");
			}
		}else if (selectedContactsCount > 4){
			if (!currentSpliCount.equals("Nine")){
				//通知切换为9分屏
				splitScreen = "Nine";
				Intent switchSolitScreen = new Intent(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
				switchSolitScreen.putExtra("splitScreen", splitScreen);
				sendBroadcast(switchSolitScreen);
				Log.e("分屏Test","设置9分屏");
			}
		}
		Log.e("分屏Test","selectedContactsCount :  " + selectedContactsCount + "\r\n" + "currentSpliCount : " + currentSpliCount);
		Log.e("分屏Test","****************************结束********************************");

	}

	// 通话广播接收器
	public class CallingBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String callAction = intent.getAction();
			// TODO Auto-generated method stub

			if(callAction == GlobalConstant.ACTION_CALLING_INCOMING){

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


				Log.e("MoveYousssssrBaby","Meeting界面  收到incoming事件");
				// 有呼入来到，适用于呼入
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				Log.e("MoveYourBaby","Meeting界面  收到incoming事件  meetingno : " + meetingno);
				Log.e("MoveYourBaby","incoming cid ： " + mSessId);
				Log.e("MoveYourBaby","calltype : " + calltype);

				if (calltype != GlobalConstant.CALL_TYPE_MEETING){
					return;
				}

				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))
						){

					if (currMeeting == null){
						Log.e("MoveYousssssrBaby","会议yi常currMeeting == null   return" );
					}else {
						Log.e("MoveYousssssrBaby","会议yi常currMeeting.getConferenceNo() ：" + currMeeting.getConferenceNo());
					}
					Log.e("MoveYousssssrBaby","会议yi常meetingno ：" + meetingno);


					ToastUtils.showToast(MeetingActivity.this,"会议发起失败");
					//清空会议联系人
					PTTService.hashMap.clear();
					//清除选中联系人
					selectedContacts.clear();

					// TODO: 2018/12/7 当前通话结束 通知添加地图
					Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
					intentS.putExtra("AddOrRemove","ADD");
					sendBroadcast(intentS);

					CallingManager.getInstance().removeCallingInfo(mSessId);
					CallingManager.getInstance().removeAnswerInfo(mSessId);

					//会议结束 发送广播结束当前以上墙的视频
					Intent closeDecoderVideo = new Intent(GlobalConstant.DECODINGTHEWALLRECEIVER);
					closeDecoderVideo.putExtra(GlobalConstant.ISALL,GlobalConstant.ISALL_YES);
					sendBroadcast(closeDecoderVideo);
					Log.e("2018-09-03","发送结束上墙视频广播");


					// 结束通话
//					Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
//					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currMeeting.getDwSessId());
//					sendBroadcast(callIntent);
					// 结束会议(通知更新视频)
					Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
					sendBroadcast(meetIntent);
//				Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
//				intentS.putExtra("AddOrRemove","ADD");
//				sendBroadcast(intentS);

					// 更新会议成员状态
					leaveConference();

					if (surfaceview_meeting.getVisibility() != View.GONE){
						surfaceview_meeting.setVisibility(View.GONE);
					}


					// 结束会议
					EndConference(currMeeting);
					Log.e("MoveYousssssrBaby","return   3257");
//					Log.e("测试会议发起失败","*************** 当前为异常发起 calltype : " + calltype);
//					if (currMeeting != null){
//						Log.e("测试会议发起失败","*************** 当前为异常发起 currMeeting.getConferenceNo() : " + currMeeting.getConferenceNo());
//					}else {
//						Log.e("测试会议发起失败","*************** 当前为异常发起 currMeeting == null");
//					}
//					Log.e("测试会议发起失败","*************** 当前为异常发起 meetingno : " + meetingno);
					return;
				}

					Log.e("MoveYousssssrBaby","会议正常发起currMeeting == null   return" );

					Log.e("MoveYousssssrBaby","会议正常发起currMeeting.getConferenceNo() ：" + currMeeting.getConferenceNo());

				Log.e("MoveYousssssrBaby","会议正常发起meetingno ：" + meetingno);
//				Log.e("测试会议发起失败","*************** 当前为正常发起 calltype : " + calltype);
//				Log.e("测试会议发起失败","*************** 当前为正常发起 currMeeting.getConferenceNo() : " + currMeeting.getConferenceNo());
//				Log.e("测试会议发起失败","*************** 当前为正常发起 meetingno : " + meetingno);
				Log.e("MoveYousssssrBaby","没有return");
				if (mSessId == MtcCliConstants.INVALIDID){
					Log.e("MoveYourBaby","2901 return了");
					return;
				}
				// TODO: 进行地图移除
				if (!ptt_3g_PadApplication.isBaiduMapisRemove()) {
					Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
					intentS.putExtra("AddOrRemove", "Remove");
					sendBroadcast(intentS);
				}

				dwSessId = mSessId;
				mCallState = GlobalConstant.CALL_STATE_INCOMING;
				// 停止播放音效
				MtcRing.stop();
				// 屏幕始终不锁定
				showWhenLocked(true);
				// 发送自动接听广播
				Log.e("MoveYourBaby","currMeeting.getMediaType() : " + currMeeting.getMediaType());
				if (currMeeting.getMediaType().equals(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE)){
					Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
					sendBroadcast(callIntent);
					Log.e("MoveYourBaby","语音会议自动接听");
				}
				else {
					Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVIDEOANSWER);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
					sendBroadcast(callIntent);
					//判断当前是否为MCU会议
					String string = prefs.getString("ISMCU", "True");
					if (!string.equals("True")) {
						// 发送推送视频消息
						PushVideo(currMeeting);
					}
					Timer timer = new Timer();

					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							//判断当前是否为MCU会议
							String string = prefs.getString("ISMCU", "True");
							if (!string.equals("True")) {
								// 发送推送视频消息
								PushVideo(currMeeting);
							}
							Log.e("MoveYourBaby","===========timer===");
						}
					},10000);

					Log.e("MoveYourBaby","视频会议自动接听");
				}
				currMeeting.setDwSessId(mSessId);
				CallingManager.getInstance().updateCallingAlright(mSessId, true);

				currentMeeting = true;

				btn_hangup.setEnabled(false);
				btn_member.setEnabled(false);
			}
			else if(callAction == GlobalConstant.ACTION_CALLING_OUTGOING){
				// 呼出
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				MtcDelegate.log("lizhiwei Meeting ACTION_CALLING_OUTGOING;calltype,"+calltype+";meetingno,"+meetingno);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				if (dwSessId != mSessId){
					return;
				}
				dwSessId = mSessId;
				mCallState = GlobalConstant.CALL_STATE_OUTGOING;
			}
			else if(callAction == GlobalConstant.ACTION_CALLING_TALKING){

				// 开始讲话，适用于呼入呼出
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				Log.e("MoveYourBaby","lizhiwei Meeting ACTION_CALLING_TALKING;calltype,"+calltype+";meetingno,"+meetingno);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					Log.e("MoveYourBaby","2974 return");
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					Log.e("MoveYourBaby","3163 return");
					return;
				}
				if (dwSessId != mSessId){
//					return;
					Log.e("MoveYourBaby","dwSessId != mSessId)");
				}
				dwSessId = mSessId;
				mCallState = GlobalConstant.CALL_STATE_TALKING;
				if (((currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo())))){
					Log.e("MoveYourBaby","2989 return");
					return;
				}

				//会议开始 默认关闭扬声器 打开麦克风
				CloseSpeaker();
				audioManager.setMicrophoneMute(false);

				// 开始计时
				mChrSessionTimeState.setBase(SystemClock.elapsedRealtime());
				mChrSessionTimeState.start();

				MemberInfo(currMeeting);
				Log.e("MoveYourBaby","3129  呼叫建立成功");

				//判断当前是否为MCU会议
				String string = prefs.getString("ISMCU", "True");
				if (!string.equals("True")){

					Intent setTrue = new Intent(GlobalConstant.MEETING_SETTRUE);
					sendBroadcast(setTrue);

					Intent startTimerIntent = new Intent(GlobalConstant.MEETING_STARTTIMER);
					startTimerIntent.putExtra("dwSessid",String.valueOf(dwSessId));
					sendBroadcast(startTimerIntent);

					Log.e("MoveYourBaby","3011 ");

					Log.e("测试会议发起失败","--------------------------***************----------------------------");
				}else {

					//取得配置是否开启
					boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);
//
//					if (mediastatistics){
//						Intent setTrue = new Intent(GlobalConstant.MEETING_SETTRUE);
//						sendBroadcast(setTrue);
//
//						Intent startTimerIntent = new Intent(GlobalConstant.MEETING_STARTTIMER);
//						startTimerIntent.putExtra("dwSessid",String.valueOf(dwSessId));
//						sendBroadcast(startTimerIntent);
//					}

					if (mediastatistics){
						linearlayout_callInfo_meetingMCU.setVisibility(View.VISIBLE);
					}else {
						linearlayout_callInfo_meetingMCU.setVisibility(View.GONE);

					}
					isStartTimer_meetingMCU = true;
					startTimer(dwSessId);
//					Log.e("MoveYourBaby","3025 ");
					Log.e("MoveYourBaby","============================================================================================= ");
				}

				// 通知视频展示
				Intent callIntent = new Intent(GlobalConstant.ACTION_MONITOR_CREATEVIDEO2);
				sendBroadcast(callIntent);

				btn_hangup.setEnabled(true);
				btn_member.setEnabled(true);
			}
			else if(callAction == ACTION_CALLING_TERMED){
				// 通话终止，适用于呼入呼出
				// 停止播放音效
				MtcRing.stop();
				// 取得参数
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				MtcDelegate.log("lizhiwei Meeting ACTION_CALLING_TERMED;calltype,"+calltype+";meetingno,"+meetingno);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						/*|| (currMeeting == null)
					|| (!meetingno.endsWith(currMeeting.getConferenceNo()))*/
						){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				if (dwSessId != mSessId){
					return;
				}
				Log.e("---------创建视频会议2222---------","收到挂断消息 Meeting");
				dwSessId = mSessId;
				mCallState = GlobalConstant.CALL_STATE_NONE;
//				/*if (((currMeeting == null)
//					|| (currMeeting.getConferenceNo() == null)
//					|| (!meetingno.equals(currMeeting.getConferenceNo())))){
//					return;
//				}*/
//				/*boolean isvideoing = false;
//				for (CallingInfo callinginfo : CallingManager.getInstance().getCallingData()){
//					if ((callinginfo != null)
//						&& (callinginfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO)){
//						isvideoing = true;
//					}
//				}
//				CallingManager.getInstance().setIsvideoing(isvideoing);*/
				btn_offline.setText("保持");
				btn_handsfree.setText("免提");
				MtcDelegate.log("lizhiwei ACTION_CALLING_TERMED保持");
//				String pcDevName = MtcMedia.Mtc_AudioGetOutputDev();
//				if (pcDevName.contains("speaker")) {
//					MtcMedia.Mtc_AudioSetOutputDev("headset");
//					btn_handsfree.setText("免提");
//				}
				// 结束通话
				CallingManager.getInstance().removeCallingInfo(mSessId);
				CallingManager.getInstance().removeAnswerInfo(mSessId);
				// 结束会议(通知更新视频)
				Intent meetIntent = new Intent(GlobalConstant.ACTION_MEETING_CALLHANGUP);
				sendBroadcast(meetIntent);
//				// 視頻控件操作
//				/*MtcCall.Mtc_SessVideoPause(dwSessId);
//				MtcCall.Mtc_SessPreviewShow(-1, false);
//				if (mLocalView instanceof GLSurfaceView){
//					((GLSurfaceView) mLocalView).onPause();
//				}
//				if (mLocalView != null){
//					mLocalView.setZOrderMediaOverlay(false);
//					mLocalView.setVisibility(View.INVISIBLE);
//				}*/

				//判断当前是否为MCU会议
				String string = prefs.getString("ISMCU", "True");
				if (!string.equals("True")){
					Intent setFalse = new Intent(GlobalConstant.MEETING_SETFALSE);
					sendBroadcast(setFalse);
				}else {

					isStartTimer_meetingMCU = false;
					if (linearlayout_callInfo_meetingMCU.getVisibility() == View.VISIBLE){
						linearlayout_callInfo_meetingMCU.setVisibility(View.GONE);
					}
					Intent setFalse = new Intent(GlobalConstant.MEETING_SETFALSE);
					sendBroadcast(setFalse);
				}

				if (surfaceview_meeting.getVisibility() != View.GONE){
					surfaceview_meeting.setVisibility(View.GONE);
				}
				Log.e("测试会议远端视频"," 显示   270");

//				Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
//				intentS.putExtra("AddOrRemove","ADD");
//				sendBroadcast(intentS);

				// 结束会议
				EndConference(currMeeting);
				//清除选中联系人
				selectedContacts.clear();
				// 更新会议成员状态
				leaveConference();
				// 停止计时
				mChrSessionTimeState.stop();
				mChrSessionTimeState.setText("");
				try{
					// 管理锁屏
					KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
					if (km.inKeyguardRestrictedInputMode()) {
						showWhenLocked(false);
					}
				}catch (Exception e){

				}
				currentMeeting = false;
				// 開始接聽下一路通話
				if (CallingManager.getInstance().getAnswerData().size() <= 0){
					return;
				}
				// 通知接聽下一路通話
				Intent answernext = new Intent(GlobalConstant.ACTION_CALLING_ANSWERNEXT);
				sendBroadcast(answernext);



			}
			else if (callAction == GlobalConstant.ACTION_CALLING_STARTPREVIEW) {
				// 开始预览
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				if (dwSessId != mSessId){
					return;
				}
				dwSessId = mSessId;
				// 开始本地视频
				MtcVideo.startLocal(mSessId, mLocalView);
			}
			else if (callAction == GlobalConstant.ACTION_CALLING_STARTVIDEO) {
				// 开始视频
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				if (dwSessId != mSessId){
					return;
				}
				dwSessId = mSessId;
				// 开始远程视频
				MtcVideo.startRemote(mSessId, 0);
			}
			else if (callAction == GlobalConstant.ACTION_CALLING_CAPTURESIZE) {
				// 摄像头尺寸
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
			}
			else if (callAction == GlobalConstant.ACTION_CALLING_VIDEOSIZE) {
				// 视频尺寸
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
			}
			else if (callAction == GlobalConstant.ACTION_CALLING_STOPVIDEO) {
				// 结束视频
				final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);

				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				Log.e("**********会议关闭视频I*", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
				// 如果不是当前通话则忽略
				if (dwSessId != mSessId){
					return;
				}
				Log.e("&&&&&&&&&Meeting", "&&&&&&&&&&currMeeting:" + currMeeting);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting != null && currMeeting.getConferenceNo() == null)
						|| (currMeeting != null && (!meetingno.contains(currMeeting.getConferenceNo())))){
					return;
				}
				Log.e("&&&&&&&&&Mee", "&&&&&&&&&&currMeeting:" + currMeeting);
				try {
					MtcCall.Mtc_SessPreviewShow(-1, false);
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("MtcCall.", e.getMessage());
				}
				// 设置本地摄像头隐藏   Sws add 11/02
				/*if(mLocalView!=null){
					mLocalView.setVisibility(View.GONE);
					mLocalView.setOnTouchListener(null);
				}*/
				Log.e("********会议结束视频******", "********会议结束视频***********");
			}
			else if(callAction==GlobalConstant.ACTION_CALLING_CALLHOLDOK){
				int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))
						){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				Log.e("***会议CALLHOLDOK ***", "***会议CALLHOLDOK ***");
				btn_offline.setText("解除保持");
				MtcDelegate.log("lizhiwei ACTION_CALLING_CALLHOLDOK解除保持");
			}
			else if(callAction==GlobalConstant.ACTION_CALLING_CALLHOLDFAILED){
				int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				Log.e("***会议CALLHOLDFAILED ***", "***会议CALLHOLDFAILED ***");

				ToastUtils.showToast(MeetingActivity.this, "会议保持失败");
			}
			else if(callAction==GlobalConstant.ACTION_CALLING_CALLUNHOLDOK){
				int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))
						){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				Log.e("***会议CALLUNHOLDOK ***", "***会议CALLUNHOLDOK ***");

				btn_offline.setText("保持");
				MtcDelegate.log("lizhiwei ACTION_CALLING_CALLUNHOLDOK保持");
			}
			else if(callAction==GlobalConstant.ACTION_CALLING_CALLUNHOLDFAILED){
				int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
				int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
				String meetingno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
				// 判断是否为会议
				if ((calltype != GlobalConstant.CALL_TYPE_MEETING)
						|| (currMeeting == null)
						|| (currMeeting.getConferenceNo() == null)
						|| (!meetingno.contains(currMeeting.getConferenceNo()))){
					return;
				}
				if (mSessId == MtcCliConstants.INVALIDID){
					return;
				}
				Log.e("***会LED ***", "***会议CALLUNHOLDFAILED ***");

				ToastUtils.showToast(MeetingActivity.this, "会议解除保持失败");
			}
		}
	}

	// 通话记录广播接收器
	public class CallingRecordBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 判断从通话记录中过来的消息不做处理，只有在主界面中处理过的消息才接手
			boolean create = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_RESULT, false);
			if (!create){
				return;
			}
			int conferenceid = intent.getIntExtra(GlobalConstant.KEY_CALL_CONFERENCEID, 0);
			if (conferenceid <= 0){
				return;
			}
			// 取得会议记录
			MeetingRecords result = null;
			mDbHelper = new SQLiteHelper(MeetingActivity.this);
			try {
				mDbHelper.open();
				result = mDbHelper.getMeetingRecordsByID((int)conferenceid);
				mDbHelper.closeclose();
			}
			catch (Exception e) {
				MtcDelegate.log("lizhiwei:" + e.getMessage());
			}
			if (result == null){
				return;
			}
			// 设置选中
			selectedContacts.clear();
			for (MeetingMembers meetingmember : result.getMeetingMembers()){
				// 更新本地联系人会议状态
				for(Contacts contact : mLocalContacts){
					if (contact.getBuddyNo().equals(meetingmember.getBuddyNo())){
						contact.setInmeeting(true);
						selectedContacts.add(contact);
					}
					else {
						contact.setInmeeting(false);
					}
				}
				// 更新系统联系人会议状态
				for (GroupInfo groupinfo : mSystemGroups){
					if ((groupinfo.getListMembers() != null)
							&& (groupinfo.getListMembers().size() > 0)){
						for (MemberInfo memberinfo : groupinfo.getListMembers()){
							if (memberinfo.getNumber().equals(meetingmember.getBuddyNo())){
								memberinfo.setInmeeting(true);
								Contacts contact = new Contacts();
								contact.setBuddyNo(memberinfo.getNumber());
								contact.setBuddyName(memberinfo.getName());
								contact.setInmeeting(memberinfo.isInmeeting());
								contact.setMeetingState(memberinfo.getMeetingState());
								contact.setMemberType(memberinfo.getMemberType());
								selectedContacts.add(contact);
							} else {
								memberinfo.setInmeeting(false);
							}
						}
					}
				}
			}
			// 刷新列表
			localFragment.refreshContactsList(mLocalContacts);
			systemFragment.refreshContactsList(mSystemGroups);
			// 刷新选中成员
			lv_seleced.setAdapter(selecedAdapter);
			// 刷新与会成员
			lv_members.setAdapter(memberAdapter);
			Log.e("---------创建视频会议2222---------", "3843   selectedContacts.size:" + selectedContacts.size());
			// 刷新追加列表
			getAllContacts();
			lv_append_alllist.setAdapter(appendAllListAdapter);
			// 刷新追加选中
			lv_append_seleced.setAdapter(appendSelecedAdapter);
			// 创建会议
			if (result.getConferenceType() == CONFERENCE_TYPE_BROADCAST){
				// 广播
				btn_voice.setEnabled(true);
				btn_video.setEnabled(true);
				btn_broadcast.setEnabled(false);
				//判断当前会议成员是否只选中自己
				if (selectedContacts.size() == 1){
					if (selectedContacts.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}
				currMeeting = CreateConference(selectedContacts, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
			}
			else {
				if (result.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO){
					// 视频会议
					btn_voice.setEnabled(true);
					btn_video.setEnabled(false);
					btn_broadcast.setEnabled(true);
					//判断当前会议成员是否只选中自己
					if (selectedContacts.size() == 0){
						if (selectedContacts.get(0).equals(sipUser.getUsername())){
							ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
							return;
						}
					}
					currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
				}
				else {
					// 语音会议
					btn_voice.setEnabled(false);
					btn_video.setEnabled(true);
					btn_broadcast.setEnabled(true);
					//判断当前会议成员是否只选中自己
					if (selectedContacts.size() == 0){
						if (selectedContacts.get(0).equals(sipUser.getUsername())){
							ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
							return;
						}
					}
					currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
				}
			}
		}
	}

	// 定位广播接收器
	public class PositionRecordBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String type = intent.getStringExtra(GlobalConstant.KEY_POSITION_TYPE);
			if ((type == null)
					|| ((!type.equals(GlobalConstant.MEETING_TAB))
							&& (!type.equals(GlobalConstant.BROADCAST_TAB)))){
				return;
			}
			ArrayList<String> nos = intent.getStringArrayListExtra(GlobalConstant.KEY_MEETING_NOS);
			if ((nos == null)
					|| (nos.size() <= 0)){
				return;
			}
			// 设置选中
			selectedContacts.clear();
			for(Contacts contact : mLocalContacts){
				contact.setInmeeting(false);
			}
			for (GroupInfo groupinfo : mSystemGroups){
				if ((groupinfo.getListMembers() != null)
						&& (groupinfo.getListMembers().size() > 0)){
					for (MemberInfo memberinfo : groupinfo.getListMembers()){
						memberinfo.setInmeeting(false);
					}
				}
			}
			/*for (String no : nos){
				// 更新本地联系人会议状态
				for(Contacts contact : mLocalContacts){
					if (contact.getBuddyNo().equals(no)){
						contact.setInmeeting(true);
						boolean exists = false;
						for (Contacts allin : selectedContacts){
							if (allin.getBuddyNo().equals(no)){
								exists = true;
								break;
							}
						}
						if (!exists){
							selectedContacts.add(contact);
						}
					}
					else {
						contact.setInmeeting(false);
					}
				}
				// 更新系统联系人会议状态
				for (GroupInfo groupinfo : mSystemGroups){
					if ((groupinfo.getListMembers() != null)
						&& (groupinfo.getListMembers().size() > 0)){
						for (MemberInfo memberinfo : groupinfo.getListMembers()){
							if (memberinfo.getNumber().equals(no)){
								memberinfo.setInmeeting(true);
								boolean exists = false;
								for (Contacts allin : selectedContacts){
									if (allin.getBuddyNo().equals(no)){
										exists = true;
										break;
									}
								}
								if (!exists){
									Contacts contact = new Contacts();
									contact.setBuddyNo(memberinfo.getNumber());
									contact.setBuddyName(memberinfo.getName());
									contact.setInmeeting(memberinfo.isInmeeting());
									contact.setMeetingState(memberinfo.getMeetingState());
									contact.setMemberType(memberinfo.getMemberType());
									selectedContacts.add(contact);
								}
							}
							else {
								memberinfo.setInmeeting(false);
							}
						}
					}
				}
			}*/
			// 刷新列表
			localFragment.refreshContactsList(mLocalContacts);
			systemFragment.refreshContactsList(mSystemGroups);
			// 刷新选中成员
			lv_seleced.setAdapter(selecedAdapter);
			// 刷新与会成员
			lv_members.setAdapter(memberAdapter);
			Log.e("---------创建视频会议2222---------", "3987   selectedContacts.size:" + selectedContacts.size());
			// 刷新追加列表
			getAllContacts();
			lv_append_alllist.setAdapter(appendAllListAdapter);
			// 刷新追加选中
			lv_append_seleced.setAdapter(appendSelecedAdapter);
			// 创建会议
			if (type.equals(GlobalConstant.MEETING_TAB)){
				// 有视频通话不能再次视频
				if (CallingManager.getInstance().isIsvideoing()){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_video_inuse));
					return;
				}
				// 不能超过4路
				if (CallingManager.getInstance().getCallingCount() >= 4){
					ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_route_busy));
					return;
				}
				/*CallingManager.getInstance().setIsvideoing(true);*/
				// 创建会议
				btn_video.setEnabled(false);
				//判断当前是否为MCU会议
				String string = prefs.getString("ISMCU", "True");
				if (string.equals("True")) {  //当前会议为MCU会议

					accordingToTypeSetView(true);

				} else {    //普通会议

					accordingToTypeSetView(false);

				}

				//判断当前会议成员是否只选中自己
				if (nos.size() == 0){
					if (nos.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}

				currMeeting = CreateConference(nos, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
				/*currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);*/
			}
			else {
				//判断当前是否为MCU会议
				String string = prefs.getString("ISMCU", "True");
				if (string.equals("True")) {  //当前会议为MCU会议

					accordingToTypeSetView(true);

				} else {    //普通会议

					accordingToTypeSetView(false);

				}

				//判断当前会议成员是否只选中自己
				if (nos.size() == 0){
					if (nos.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}

				// 创建广播
				btn_broadcast.setEnabled(false);
				currMeeting = CreateConference(nos, CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
				/*currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_BROADCAST, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);*/
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

	// 取得本地联系人
	private void getPhoneContacts(){
		// 清空已有
		this.mLocalContacts.clear();
		ArrayList<Contacts> contacts = CommonMethod.getPhoneContacts(this);
		this.mLocalContacts.addAll(contacts);
	}

	// 取得系统联系人
	private void getSystemGroups(){
		try{
			// 清空已有
			this.mSystemGroups.clear();
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
				// 临时对讲组不可见
				if (group.isTemporary()){
					continue;
				}
				if (group != null){
					if ((group.getListMembers() != null)
							&& (group.getListMembers().size() > 0)){
						MemberInfo member = null;
						for (MemberInfo memberinfo : group.getListMembers()){
							if (memberinfo.getNumber().equals(group.getNumber())){
								// 将切换对讲组去掉
								member = memberinfo;
								break;
							}
						}
						if (member != null){
							group.getListMembers().remove(member);
						}
					}
					this.mSystemGroups.add(group);
				}
			}
		}catch (ConcurrentModificationException e){

		}
	}

	// 所有联系人
	private void getAllContacts(){
		mAllContacts.clear();
		mAllContacts.addAll(getGroupMemberInfo());
		mAllContacts.addAll(mLocalContacts);
	}

	// 将对讲组成员转换为联系人数据
	private List<Contacts> getGroupMemberInfo(){
		List<Contacts> result = new ArrayList<Contacts>();
		HashMap<String,Contacts> hashMap = new HashMap<>();
		for(GroupInfo groupinfo : mSystemGroups){
			if ((groupinfo.getListMembers() != null)
					&& (groupinfo.getListMembers().size() > 0)){
				for(MemberInfo memberinfo : groupinfo.getListMembers()){
					Contacts contact = new Contacts();
					contact.setBuddyNo(memberinfo.getNumber());
					contact.setBuddyName(memberinfo.getName());
					contact.setInmeeting(memberinfo.isInmeeting());
					contact.setMeetingState(memberinfo.getMeetingState());
					contact.setMemberType(memberinfo.getMemberType());
					//if (!result.contains(contact)){
					if (!hashMap.containsKey(memberinfo.getNumber())){
						result.add(contact);
						hashMap.put(memberinfo.getNumber(),contact);
					}
					//}
				}
			}
		}
		return result;
	}

	// 离开会议，刷新成员状态
	private void leaveConference(){
		getPhoneContacts();
		getSystemGroups();
		// 更新本地联系人会议状态
		for(Contacts contact : mLocalContacts){
			for(Contacts con : selectedContacts){
				if (contact.getBuddyNo().equals(con.getBuddyNo())){
					contact.setInmeeting(true);
				}
			}
		}
		// 更新系统联系人会议状态
		for (GroupInfo groupinfo : mSystemGroups){
			if ((groupinfo.getListMembers() != null)
					&& (groupinfo.getListMembers().size() > 0)){
				for (MemberInfo memberinfo : groupinfo.getListMembers()){
					for(Contacts con : selectedContacts){
						if (memberinfo.getNumber().equals(con.getBuddyNo())){
							memberinfo.setInmeeting(true);
						}
					}
				}
			}
		}
		// 更新选中联系人会议状态
		for(Contacts contact : selectedContacts){
			contact.setMeetingState(GlobalConstant.GROUP_MEMBER_EMPTY);
		}
		// 刷新列表
		localFragment.refreshContactsList(mLocalContacts);
		systemFragment.refreshContactsList(mSystemGroups);
		// 刷新选中成员
		lv_seleced.setAdapter(selecedAdapter);
		// 刷新与会成员
		lv_members.setAdapter(memberAdapter);
		Log.e("---------创建视频会议2222---------", "4188   selectedContacts.size:" + selectedContacts.size());
		// 刷新追加列表
		getAllContacts();
		lv_append_alllist.setAdapter(appendAllListAdapter);
		// 刷新追加选中
		lv_append_seleced.setAdapter(appendSelecedAdapter);
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
				new SettingExitFragment(MeetingActivity.this).exit(); //之前的退出操作
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

	// 提示监控正在进行
	@SuppressWarnings("unused")
	private void infoMonitoring() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.title_meeting_title));
		builder.setMessage(getString(R.string.title_meeting_info));
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void createMeetingsReceive(SessionCallInfos sessionCallInfos)
	{

		if (MeetingActivity.currMeeting == null){
			return;
		}
		// 创建成功
		String sessnum = sessionCallInfos.getOthernum();
		String cid = sessionCallInfos.getCid();
		currMeeting.setConferenceNo(sessnum);
		currMeeting.setcID(cid);
		Log.e("MoveYousssssrBaby","收到异步推送事件 currMeeting 设置cid ： " + cid + "\r\n" + "设置ConferenceNo : " + sessnum);
	}

	// 提示通话是否继续发起
	public Dialog showMeetingIfContinue(final CallingInfo oldCallInfo,final List<Contacts> contacts, final int conferenceType, final int mediaType) {



		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}




		String message = "";
		String title = "";
		String positive = null;
		title = getString(R.string.info_ifContinue);
		positive =getString(R.string.info_continue);
		CustomDialog.Builder builder = new CustomDialog.Builder(
				MeetingActivity.this);
		builder.setMessage(message);
		builder.setTitle(title);
		// 继续发起
		//					if (positive != null) {
		builder.setPositiveButton(positive,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 终止原来通话
				Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
				callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, oldCallInfo.getDwSessId());
				sendBroadcast(callIntent);
				// 移除通话数据
				CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
				CallingManager.getInstance().removeCallingDialog(((WaitingDialog)dialog).getDwSessId());



				// 关闭页面
				dialog.dismiss();
				dialog = null;

				//判断当前会议成员是否只选中自己
				if (contacts.size() == 1){
					if (contacts.get(0).equals(sipUser.getUsername())){
						ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
						return;
					}
				}
				// 创建会议
				currMeeting = CreateConference(contacts, conferenceType, mediaType);
			}
		});
		//					}
		// 终止对讲
		builder.setNegativeButton(getString(R.string.info_notcontinue),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 关闭提示框
				CallingManager.getInstance().removeCallingDialog(((WaitingDialog)dialog).getDwSessId());
				dialog.dismiss();
				dialog = null;
			}
		});
		final WaitingDialog informing = builder.create();
		informing.setCancelable(false);
		informing.setDwSessId(oldCallInfo.getDwSessId());
		informing.show();
		CallingManager.getInstance().addCallingDialog(informing);

		//5秒无操作自动关闭
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				MtcDelegate.log("lizhiwei 超时挂断");
				if ((informing != null) && (informing.isShowing())) {
					// 关闭提示框
					CallingManager.getInstance().removeCallingDialog(informing.getDwSessId());
					informing.dismiss();
				}
			}
		}, 5000);
		return informing;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_metting_one:
			sb.append("1");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_two:
			sb.append("2");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());

			break;
		case R.id.btn_metting_three:
			sb.append("3");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_four:
			sb.append("4");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_five:
			sb.append("5");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_six:
			sb.append("6");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_seven:
			sb.append("7");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_eight:
			sb.append("8");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_nine:
			sb.append("9");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_xing:
			sb.append("*");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_ling:
			sb.append("0");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_metting_jing:
			sb.append("#");
			txt_phoneno.setText(sb.toString().trim());
			txt_phoneno.setSelection(txt_phoneno.getText().length());
			break;
		case R.id.btn_keyboard_delete:
			if(sb.length() > 0){
				sb.deleteCharAt(sb.length()-1);
			}
			if(txt_phoneno.getText().toString().length() > 0){
				txt_phoneno.setText(sb.toString());
				txt_phoneno.setSelection(txt_phoneno.getText().length());
			}

			break;
		}
	}

	private static int currVolume = 0;
	//打开扬声器
	public void OpenSpeaker() {
			try{
				audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
				//currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
				int currVolume = ptt_3g_PadApplication.getCurrentVolume();
				if(!audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(true);
					Log.e("集体测试","测试扬声器 meeting    3924   true");
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
							currVolume,
							AudioManager.STREAM_VOICE_CALL);
				}else {
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
							currVolume,
							AudioManager.STREAM_VOICE_CALL);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	//关闭扬声器
	public void CloseSpeaker() {
			try {
				if(audioManager != null) {
					if(audioManager.isSpeakerphoneOn()) {
						audioManager.setSpeakerphoneOn(false);
						//currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
						int currVolume = ptt_3g_PadApplication.getCurrentVolume();
						Log.e("集体测试","测试扬声器 meeting    3941   false");
						audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
								currVolume,
								AudioManager.STREAM_VOICE_CALL);
					}else {
						int currVolume = ptt_3g_PadApplication.getCurrentVolume();
						audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
								currVolume,
								AudioManager.STREAM_VOICE_CALL);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}


	//根据会议类型切换布局
	private void accordingToTypeSetView(boolean isMcuMeeting){

		//当前Method在异步线程中调用，当对界面进行操作时 需切换到UI线程
		Log.e("MoveYourBaby","根据会议类型更换布局 isMcu ： " + isMcuMeeting);
		if (isMcuMeeting){	//MCU会议
			Log.e("MoveYourBaby","当前为MCU会议");
			//通知主界面更换布局
			Intent intent = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
			intent.putExtra("meetingType",String.valueOf(GlobalConstant.ONLYTAB));
			sendBroadcast(intent);
			Log.e("MoveYourBaby","发送广播到主界面通知切换布局  meetingType ：" + GlobalConstant.ONLYTAB);

			if (surfaceview_meeting != null){
				Log.e("MoveYourBaby","surfaceview_meeting != null");
				if (surfaceview_meeting.getVisibility() != View.VISIBLE){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							surfaceview_meeting.setVisibility(View.VISIBLE);
							surfaceview_meeting.setZOrderMediaOverlay(true);
							//surfaceview_meeting.setZOrderOnTop(true);
							Log.e("MoveYourBaby","surfaceview_meeting.setVisibility(View.VISIBLE)");
						}
					});
				}else {
					Log.e("MoveYourBaby","surfaceview_meeting.getVisibility() == View.VISIBLE");
				}
			}else {
				Log.e("MoveYourBaby","surfaceview_meeting == null");
			}


			if (callingRemoteViewLayout_meeting != null){
				Log.e("MoveYourBaby","callingRemoteViewLayout_meeting != null");
				if (callingRemoteViewLayout_meeting.getVisibility() != View.VISIBLE){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.e("MoveYourBaby","callingRemoteViewLayout_meeting != View.VISIBLE");
							callingRemoteViewLayout_meeting.setVisibility(View.VISIBLE);
						}
					});
				}else {
					Log.e("MoveYourBaby","callingRemoteViewLayout_meeting == View.VISIBLE");
				}
			}else {
				Log.e("MoveYourBaby","callingRemoteViewLayout_meeting == null");
			}


		}else {			//普通会议

			if (surfaceview_meeting.getVisibility() != View.GONE){
				surfaceview_meeting.setVisibility(View.GONE);
			}

//			if (callingRemoteViewLayout_meeting != null && callingRemoteViewLayout_meeting.getVisibility() != View.GONE) {
//				callingRemoteViewLayout_meeting.setVisibility(View.GONE);
//			}

			Log.e("MoveYourBaby","当前为普通会议");
			//通知主界面更换布局
			Intent intent = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
			intent.putExtra("meetingType",String.valueOf(GlobalConstant.ALL));
			sendBroadcast(intent);

			Log.e("MoveYourBaby","发送广播到主界面通知切换布局 meetingType : " + GlobalConstant.ALL);

			if (surfaceview_meeting != null){
				if (surfaceview_meeting.getVisibility() != View.GONE){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							surfaceview_meeting.setVisibility(View.GONE);
							Log.e("MoveYourBaby"," 隐藏  远端SurfaceView 4275");

						}
					});
				}
			}


//			if (callingRemoteViewLayout_meeting != null){
//				if (callingRemoteViewLayout_meeting.getVisibility() != View.GONE){
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							Log.e("MoveYourBaby"," 隐藏  callingRemoteViewLayout_meeting 4288");
//							callingRemoteViewLayout_meeting.setVisibility(View.GONE);
//						}
//					});
//
//				}
//			}
		}
	}

	class MLocalViewHideOrShowReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			boolean state = intent.getBooleanExtra("state",false);
			if (mLocalView  == null){
				return;
			}
				if (!state){
					mLocalView.setVisibility(View.GONE);
				}else {
					mLocalView.setVisibility(View.VISIBLE);
				}
		}
	}

	//MCU会议异步创建
	class MeetingCreateAsync extends AsyncTask<Object,Object,Object>{
		private ConfigureOPPrx configureOPPrx;

		@SuppressLint("LongLogTag")
		@Override
		protected Object doInBackground(Object... objects) {
			Log.e("MoveYourBaby","进入DoInBackGround");
			if(!ptt_3g_PadApplication.isNetConnection()){
				//ToastUtils.showToast(MeetingActivity.this,getString(R.string.info_network_unavailable));
				toastCount = getString(R.string.info_network_unavailable);
				meetingCreateHandler.sendEmptyMessage(1);
				Log.e("MoveYourBaby","isNetConnection() return 4313");
				return null;
			}

			//为了防止发起呼叫过于频繁
			if(ptt_3g_PadApplication != null)
			{
				if(ptt_3g_PadApplication.isWetherClickCall() == false)
				{
					ptt_3g_PadApplication.setWetherClickCall(true);
					boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
					if(wetherTimerWorking == true)
					{
						//ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
						toastCount = getString(R.string.callquicktimer);
						meetingCreateHandler.sendEmptyMessage(1);
						Log.e("MoveYourBaby","wetherTimerWorking == true return 4329");
						//返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
						return null;
					}
					else
					{
						//返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
						ptt_3g_PadApplication.startTimer();

					}
				}
				else
				{
					//ToastUtils.showToast(MeetingActivity.this,getString(R.string.callquicktimer));
					toastCount = getString(R.string.callquicktimer);
					meetingCreateHandler.sendEmptyMessage(1);
					Log.e("MoveYourBaby"," 呼叫过于频繁 return 4345");
					return null;
				}
			}

			//如果当前已经有单呼，禁止发起新的单呼
			CallingInfo callingCalling = null;
			CallingInfo callingMeeting = null;
			CallingInfo callingIntercom = null;
			for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
				if ((!callingInfo.isHolding())){
					if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
						callingCalling = callingInfo;
					}
					if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING){
						callingMeeting = callingInfo;
					}
                    if(callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM){
                        callingIntercom = callingInfo;
					}
				}
			}
			if(callingCalling != null)
			{
				Log.e("MoveYourBaby"," callingCalling != null  return");
				final CallingInfo finalCallingCalling = callingCalling;
				runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showMeetingIfContinue(finalCallingCalling,selectedContacts,GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);

			}
		});
				return null;
			}
			if(callingMeeting != null){
				Log.e("MoveYourBaby"," callingMeeting != null  return");
				final CallingInfo finalCallingMeeting = callingMeeting;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showMeetingIfContinue(finalCallingMeeting,selectedContacts,GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
					}
				});
				return null;
			}
            if(callingIntercom != null) {
				Log.e("MoveYourBaby"," callingIntercom != null  return");
                final CallingInfo callingIntercoms = callingIntercom;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMeetingIfContinue(callingIntercoms, selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);

                    }
                });
                return null;
            }

			Log.e("MoveYourBaby","meetingCreateHandler.sendEmptyMessage(3)  return");
			meetingCreateHandler.sendEmptyMessage(3);


			Log.e("Meeting", "*****************发起视频************");

			// 判断视频会议是否进行如果进行则给出提示
			if (MainActivity.getVideoType() == GlobalConstant.VIDEO_MONITOR){
				// infoMonitoring();
				//ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.title_meeting_info));
				toastCount = getString(R.string.title_meeting_info);
				meetingCreateHandler.sendEmptyMessage(1);
				Log.e("MoveYourBaby","当前存在监控  return");
				return null;
			}
			Log.e("测试getVideoType","MainActivity.getVideoType() : " + MainActivity.getVideoType());
			// 有视频通话不能再次视频
			if (CallingManager.getInstance().isIsvideoing()){
				//ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_video_inuse));
				toastCount = getString(R.string.info_video_inuse);
				meetingCreateHandler.sendEmptyMessage(1);
				Log.e("MoveYourBaby","当前存在视频通话  return");
				return null;
			}
			// 不能超过4路
			if (CallingManager.getInstance().getCallingCount() >= 4){
				toastCount = getString(R.string.info_meeting_route_busy);
				meetingCreateHandler.sendEmptyMessage(1);
				//ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_route_busy));
				return null;
			}
			// 判断选择的与会人员
			if ((selectedContacts == null)
					|| (selectedContacts.size() <= 0)){
				//ToastUtils.showToast(MeetingActivity.this, getResources().getString(R.string.info_meeting_empty_member));
				toastCount = getString(R.string.info_meeting_empty_member);
				meetingCreateHandler.sendEmptyMessage(1);
				Log.e("MoveYourBaby","没有选中与会成员");
				return null;
			}

			//判断当前会议成员是否只选中自己
			if (selectedContacts.size() == 0){
				if (selectedContacts.get(0).equals(sipUser.getUsername())){
					Log.e("MoveYourBaby","会议成员不能只包含自己 return");
					ToastUtils.showToast(MeetingActivity.this,"会议成员不能只包含自己");
					return null;
				}
			}

			Log.e("MoveYourBaby","发送 msg2");
			meetingCreateHandler.sendEmptyMessage(2);

			/*CallingManager.getInstance().setIsvideoing(true);*/
			//btn_video.setEnabled(false);
			// 如果正在监控则给出提示不允许视频会议
			//获取当前会议是否是MCU会议
			try{
				boolean iceIsConnection = MediaEngine.GetInstance().ME_ServerAvailable();
				Log.e("MoveYourBaby","ME_GetConfigByKeys 判断当前是否配置MCU   ICE连接状态 : " + iceIsConnection);
				if (!iceIsConnection){
					//当前ICE连接失败
					Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_TYPE);
					intent2.putExtra("meetingType","Error2");
					sendBroadcast(intent2);
					return null;
				}
				MediaEngine.ME_ConfigItem[] configItems = MediaEngine.GetInstance().ME_GetConfigByKeys(new String[]{"ISUSEMCU"});
				Log.e("MoveYourBaby","ME_GetConfigByKeys 判断当前是否配置MCU 结果返回");
				//会返回  True/ False
				Log.e("MoveYourBaby","当前会议模式 result：" + configItems[0].getValue());
				if (configItems.length > 0){
					//得到当前会议是否配置了MCU
					SharedPreferences.Editor edit = prefs.edit();
					edit.putString("ISMCU",configItems[0].getValue());
					edit.apply();

					if (configItems[0].getValue().equals("True")) {  //当前会议为MCU会议
						Log.e("MoveYourBaby","mcu会议");
						accordingToTypeSetView(true);
					} else {    //普通会议
						Log.e("MoveYourBaby","普通会议");
						accordingToTypeSetView(false);
					}

					// 创建会议
					currMeeting = CreateConference(selectedContacts, GlobalConstant.CONFERENCE_TYPE_TEMPORARY, GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);

					Log.e("---------创建视频会议---------", "结果：name:" +currMeeting.getConferenceNm()+"," +
							"number:"+currMeeting.getConferenceNo()+",type:"+currMeeting.getConferenceType());


				}
			}catch (Exception e){
				Log.e("测试会议发起失败","会议创建出现异常 \r\n" + "\r\n" + "\r\n" + "\r\n" + "ErrorMsg :: " + e.getMessage());
			}
			return null;
		}
	}

	//停止统计丢包信息定时器
	private void stopTimer(){

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}

		meetingCreateHandler.sendEmptyMessage(5);

	}

	//开启统计丢包信息定时器
	private void startTimer(final int dwSessid){

		//取得配置是否开启
		boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

		if (!mediastatistics)return ; //如果配置未开启 不执行下列代码



		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (timerTask == null){
			timerTask = new TimerTask() {
				@Override
				public void run() {
					if (isStartTimer_meetingMCU){
						//丢包率信息统计
						new AsyncMediaStatisticsMeeting().execute(String.valueOf(dwSessid));
					}else {
						stopTimer();
					}
				}
			};
		}

		if (mTimer != null && timerTask != null) {
			try {
				mTimer.schedule(timerTask, 1000, 1000);

			}catch (Exception e){
				Log.e("=======PaoError","" + e.getMessage());
			}
		}
	}

	//开始请求统计信息
	class AsyncMediaStatisticsMeeting extends AsyncTask<String,Object,Object>{

		@Override
		protected String doInBackground(String... strings) {

			//取得配置是否开启
			boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

			if (!mediastatistics)return null; //如果配置未开启 不执行下列代码

			String dwSessid = strings[0];

			if (dwSessid.equals("-1")){
				return null;
			}

			Integer aud_down = new Integer(-1);
			Integer aud_up = new Integer(-1);
			Integer aud_rtt = new Integer(-1);
			Integer aud_bwD = new Integer(-1);
			Integer aud_bwU = new Integer(-1);

			Integer vid_down = new Integer(-1);
			Integer vid_up = new Integer(-1);
			Integer vid_rtt = new Integer(-1);
			Integer vid_bwD = new Integer(-1);
			Integer vid_bwU = new Integer(-1);
			MediaEngine.GetInstance().ME_GetMediaStatistics(Integer.valueOf(dwSessid),aud_down,aud_up,aud_rtt,aud_bwD,aud_bwU, vid_down,vid_up,vid_rtt,vid_bwD,vid_bwU);

			voiceDownlink_meetingMCU = aud_down;
			voice_Uplink_meetingMCU = aud_up;
			voice_Bidirectionaldelay_meetingMCU = aud_rtt;
			voice_downstreambandwidth_meetingMCU = aud_bwD;
			voice_Uplinkbandwidth_meetingMCU = aud_bwU;

			video_Downlink_meetingMCU = vid_down;
			video_Uplink_meetingMCU = vid_up;
			video_Bidirectionaldelay_meetingMCU = vid_rtt;
			video_downstreambandwidth_meetingMCU = vid_bwD;
			video_Uplinkbandwidth_meetingMCU = vid_bwU;

			meetingCreateHandler.sendEmptyMessage(4);

			return null;
		}
	}

}
