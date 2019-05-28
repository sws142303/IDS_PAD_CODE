package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.AutoAnswerBena;
import com.azkj.pad.model.CallingRecords;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.utility.CallingRecordListAdapter;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.KeyboardUtil;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.pad.utility.ThreadPoolProxy;
import com.azkj.pad.utility.ToastUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.juphoon.lemon.MtcCallConstants;
import com.juphoon.lemon.MtcMedia;
import com.juphoon.lemon.ui.MtcDelegate;
import com.juphoon.lemon.ui.MtcVideo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.utility.GlobalConstant.ACTION_CALLING_CALLVOICE;

@SuppressLint("HandlerLeak")
public class CallingActivity extends Activity implements OnClickListener {
    // 全局变量定义
    private SharedPreferences prefs;
    private LinearLayout callinglinearLayout;
    // 当前登录用户
    private String userNo;
    private Camera cameras;
    // 当前通话状态
    private int mCallState = GlobalConstant.CALL_STATE_NONE;
    // 当前回话编号
    private int mSessId;
    // 屏幕方向
    @SuppressWarnings("unused")
    private int mOrientation = 0;// 0表示竖屏，1表示横屏
    @SuppressWarnings("unused")
    private int mFixedOrientation = -1;
    @SuppressWarnings("unused")
    private RelativeLayout callingRelativeLayout;
    // 通话记录页面
    private RelativeLayout callingShowRelativeLayout;
    private ListView callingList;
    private List<CallingRecords> callingrecords;
    private List<CheckBox> ckList;
    private List<CallingRecords> asyncCallingRecords;
    private CallingRecordListAdapter listAdapter;
    private CallingRecords currCallingRecord;
    // 弹出菜单
    private PopupWindow localPopupWindow;// 点击显示本地联系人菜单
    private PopupWindow systemPopupWindow;// 点击显示系统联系人菜单
    private PopupWindow conferencePopupWindow;// 点击显示会议菜单

    // 通话按钮
    private Button btnCallingMenuAudio;
    private Button btnCallingMenuVideo;
    private Button btnCallingMenuBroadcast;
    private Button btnCallingMenuMessage;
    private Button btnCallingMenuEdit;
    private Button btnCallingMenuDelete;

    //键盘 1-9  0 * #
    private Button btn_one, btn_two, btn_three, btn_four, btn_five, btn_six, btn_seven, btn_eight, btn_nine, btn_ling, btn_xing, btn_jing;
    private StringBuffer sb = new StringBuffer();
    // 通话中本地显示
    private RelativeLayout callingLocalViewLayout, callingLocalViewTitle;
    private LinearLayout callingLocalViewVideo, callingLocalViewAudio;
    private TextView txtOutUserNo,txtOutUserName, txtAudioUserName, txtAudioUserNo;
    private Chronometer session_time_state, session_time_state_audio;
    private RelativeLayout callingLocalViewFrame;
    //摄像头状态   1为前置   2为后置
    private String cameraCode = "1";

    //记录当前本地远端画面是否改变
    private boolean surfaceIsChange = false;
    //视频画面位置改变
    private ImageView img_SfVChange;


    // 本地摄像头展示
    private SurfaceView mLocalView;
    // 远程摄像头展示
    private SurfaceView mRemoteView;

    private LinearLayout mRemoteLinearLayout;

    // 本地摄像头宽度
    @SuppressWarnings("unused")
    private int mLocalWidth;
    // 本地摄像头高度
    @SuppressWarnings("unused")
    private int mLocalHeight;
    // 远程摄像头宽度
    @SuppressWarnings("unused")
    private int mRemoteWidth;
    // 远程摄像头高度
    @SuppressWarnings("unused")
    private int mRemoteHeight;

    // 通话中操作按钮
    //private Button btnViewStatus;
    private Button btnHangUp;
    private Button btnVoiceAnswer;
    private Button btnVideoAnswer;
    private Button btnDecline;
    private Button btnHandsFree;
    private Button btnMute;
    private Button btnHoldon;
    private Button btnInviteJoin;
    private Button btnSwitchCamera;

    // 对方区域
    @SuppressWarnings("unused")
    private RelativeLayout callingRemoteViewLayout;
    private FrameLayout callingRemoteViewFrame;
    //视频
    private LinearLayout callingRemoteViewFrameInfo;
    private TextView videoUserName, videoUserNo;
    private Chronometer videoUserTime;
    //private TextView txtCallingMessage;
    private Button btnGone;
    private Button btnInfo;

    // 键盘操作
    private ImageButton btnKeyboardShow;
    private RelativeLayout callingKeyboardLayout;
    private EditText numEditText;
    private Button btnBackup;
    //private android.inputmethodservice.KeyboardView keyboard_view;
    private KeyboardUtil keyboardUtil;

    // 通话接入
    private Button btnShrink;
    private Button btnCallVoice;
    private Button btnCallVideo;
    private Button btnAddContact;
    private PTT_3G_PadApplication ptt_3g_PadApplication;

    // 呼叫接收器
    private CallingBroadcastReceiver callingBroadcastReceiver;
    private boolean callingActivityCreateFinished = false;
    /*public KeyboardUtil getKeyboardUtil() {
        if (keyboardUtil == null) {
            keyboardUtil = new KeyboardUtil(CallingActivity.this, CallingActivity.this, keyboard_view, numEditText, btnBackup);
        }
        return keyboardUtil;
    }*/
    //显示批量删除按钮的容器
    LinearLayout topPlan;
    //通话记录上方的删除图标
    ImageButton imgbtn;
    //删除按钮所在容器
    LinearLayout layout;
    //显示通话记录数量
    TextView textNum;
    //全选
    CheckBox checkAll;
    //删除按钮
    Button btndelete;
    private SipUser sipUser;
    //来电回复广播
    private ResponseBroadcastReceiver responseBroadcastReceiver;

    private SurfaceViewReceiver surfaceViewReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //统计丢包时延相关
    private LinearLayout linearlayout_callInfo;
    //语音统计
    private TextView tv_voice_Downlink, tv_voice_Uplink, tv_voice_Bidirectionaldelay, tv_voice_downstreambandwidth, tv_voice_Uplinkbandwidth;
    //视频统计
    private TextView tv_video_Downlink, tv_video_Uplink, tv_video_Bidirectionaldelay, tv_video_downstreambandwidth, tv_video_Uplinkbandwidth;
    //语音统计参数
    private int voiceDownlink = 0;
    private int voice_Uplink = 0;
    private int voice_Bidirectionaldelay = 0;
    private int voice_downstreambandwidth = 0;
    private int voice_Uplinkbandwidth = 0;
    //视频统计参数
    private int video_Downlink = 0;
    private int video_Uplink = 0;
    private int video_Bidirectionaldelay = 0;
    private int video_downstreambandwidth = 0;
    private int video_Uplinkbandwidth = 0;

    private boolean isStartTimer = false;
    private Timer mTimer;
    private TimerTask timerTask;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    tv_voice_Downlink.setText("" + voiceDownlink);
                    tv_voice_Uplink.setText("" + voice_Uplink);
                    tv_voice_Bidirectionaldelay.setText("" + voice_Bidirectionaldelay);
                    tv_voice_downstreambandwidth.setText("" + voice_downstreambandwidth);
                    tv_voice_Uplinkbandwidth.setText("" + voice_Uplinkbandwidth);

                    tv_video_Downlink.setText("" + video_Downlink);
                    tv_video_Uplink.setText("" + video_Uplink);
                    tv_video_Bidirectionaldelay.setText("" + video_Bidirectionaldelay);
                    tv_video_downstreambandwidth.setText("" + video_downstreambandwidth);
                    tv_video_Uplinkbandwidth.setText("" + video_Uplinkbandwidth);
                    break;

                case 2:
                    tv_voice_Downlink.setText("0");
                    tv_voice_Uplink.setText("0");
                    tv_voice_Bidirectionaldelay.setText("0");
                    tv_voice_downstreambandwidth.setText("0");
                    tv_voice_Uplinkbandwidth.setText("0");

                    tv_video_Downlink.setText("0");
                    tv_video_Uplink.setText("0");
                    tv_video_Bidirectionaldelay.setText("0");
                    tv_video_downstreambandwidth.setText("0");
                    tv_video_Uplinkbandwidth.setText("0");

                    break;
            }

        }
    };
    private AudioManager audioManager;
    private SetVolumeUtils setVolumeUtils = null;
    private LinearLayout linar_mLocal;
    private long lastonclickTime  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);


        // 全局变量定义
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
        //11/07 Sws add
        mLocalView = (SurfaceView) findViewById(R.id.surfaceviewlocal);
        mRemoteLinearLayout = (LinearLayout) findViewById(R.id.mRemoteLinearLayout);
        //mLocalView.setZOrderOnTop(true);
        //mLocalView.setZOrderMediaOverlay(true);
        mRemoteView = (SurfaceView) findViewById(R.id.surfaceview);
        mRemoteView.setZOrderMediaOverlay(true);
        //mRemoteView.setZOrderOnTop(true);
        audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        //设置声音模式
        audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        setVolumeUtils = new SetVolumeUtils(this,audioManager);
        MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));

        callingBroadcastReceiver = new CallingBroadcastReceiver();
        IntentFilter callingIntentFilter = new IntentFilter();
        callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_OUTGOING);
        callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_ALERTED);
        callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TALKING);
        callingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TERMED);
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
        registerReceiver(callingBroadcastReceiver, callingIntentFilter);

        surfaceViewReceiver = new SurfaceViewReceiver();
        IntentFilter surfaceViewReceiverIntentFilter = new IntentFilter();
        surfaceViewReceiverIntentFilter.addAction(GlobalConstant.ACTION_CALLING_SURFACEVIEW);
        registerReceiver(surfaceViewReceiver, surfaceViewReceiverIntentFilter);

        responseBroadcastReceiver = new ResponseBroadcastReceiver();
        IntentFilter responseIntentFilter = new IntentFilter();
        responseIntentFilter.addAction(GlobalConstant.ACTION_CALLING_RESPONSE);
        registerReceiver(responseBroadcastReceiver, responseIntentFilter);

        //mLocalView=ViERenderer.CreateLocalRenderer(this);

        //初始化统计丢包时延相关View
        initMediaStatistics();

        linar_mLocal = (LinearLayout) findViewById(R.id.linar_mLocal);
        //获取显示删除图标的容器
        topPlan = (LinearLayout) findViewById(R.id.topPlan);
        //获取删除图标的实例
        imgbtn = (ImageButton) findViewById(R.id.imgdelete);
        //获取删除容器的实例
        layout = (LinearLayout) findViewById(R.id.delete_layout);
        //获取列表数量控件实例
        textNum = (TextView) findViewById(R.id.listNum);
        //全选
        checkAll = (CheckBox) findViewById(R.id.checkAll);
        //删除按钮
        btndelete = (Button) findViewById(R.id.btndelete);
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        // 初始化视图
        initWidgets();
        //键盘部分初始化
        callinglinearLayout = (LinearLayout) findViewById(R.id.calling_linearlayout);
        int heights = 0;

        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_five = (Button) findViewById(R.id.btn_five);
        btn_six = (Button) findViewById(R.id.btn_six);
        btn_seven = (Button) findViewById(R.id.btn_seven);
        btn_eight = (Button) findViewById(R.id.btn_eight);
        btn_nine = (Button) findViewById(R.id.btn_nine);
        btn_ling = (Button) findViewById(R.id.btn_ling);
        btn_xing = (Button) findViewById(R.id.btn_xing);
        btn_jing = (Button) findViewById(R.id.btn_jing);
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

		/*callingRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(CommonMethod.dip2px(getBaseContext(), 240), FrameLayout.LayoutParams.MATCH_PARENT));
        callingRemoteViewLayout.setVisibility(View.GONE);*/

        callingActivityCreateFinished = true;

        numEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("onTextChanged", "onTextChanged:"+s.toString());
                if (mCallState == GlobalConstant.CALL_STATE_TALKING) {
                    int length = s.toString().trim().length();
                    if (length > 0) {
                        char ch = s.charAt(length - 1);
                        String value = String.valueOf(ch);
                        Log.e("得到的字符:", "得到的字符:" + value);
                        if (value.equals("*")) {
                            //yuezs 隐藏 使用jpsipSDK
                            //MtcCall.Mtc_SessDtmf(mSessId,
                            //MtcCallConstants.EN_MTC_CALL_DTMF_STAR);
                            MediaEngine.GetInstance().ME_SendDtmf(mSessId, String.valueOf(MtcCallConstants.EN_MTC_CALL_DTMF_STAR));
                            Log.e("发送dtmf:", "发送dtmf:mSeddId:" + mSessId + "字符:" + value);
                        } else if (value.equals("#")) {
                            //MtcCall.Mtc_SessDtmf(mSessId,
                            //MtcCallConstants.EN_MTC_CALL_DTMF_POUND);
                            MediaEngine.GetInstance().ME_SendDtmf(mSessId, String.valueOf(MtcCallConstants.EN_MTC_CALL_DTMF_POUND));
                            Log.e("发送dtmf:", "发送dtmf:mSeddId:" + mSessId + "字符:" + value);
                        } else {
                            //int dtmf = Integer.valueOf((String) value);
                            //MtcCall.Mtc_SessDtmf(mSessId, dtmf);
                            MediaEngine.GetInstance().ME_SendDtmf(mSessId, value);
                            Log.e("发送dtmf:", "发送dtmf:mSeddId:" + mSessId + "字符:" + value);
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //Log.e("beforeTextChanged", "beforeTextChanged:"+s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.e("afterTextChanged", "afterTextChanged:"+s.toString());
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(ptt_3g_PadApplication).addApi(AppIndex.API).build();

        //视频画面位置改变View
        img_SfVChange = (ImageView) findViewById(R.id.img_SfVChange);
        img_SfVChange.setOnClickListener(new SurfaceViewChange());

    }

    //初始化统计丢包时延相关View
    private void initMediaStatistics() {
        //统计信息根布局
        linearlayout_callInfo = (LinearLayout) findViewById(R.id.linearlayout_callInfo);
        //语音统计View
        tv_voice_Downlink = (TextView) findViewById(R.id.voice_Downlink);
        tv_voice_Uplink = (TextView) findViewById(R.id.voice_Uplink);
        tv_voice_Bidirectionaldelay = (TextView) findViewById(R.id.voice_Bidirectionaldelay);
        tv_voice_downstreambandwidth = (TextView) findViewById(R.id.voice_downstreambandwidth);
        tv_voice_Uplinkbandwidth = (TextView) findViewById(R.id.voice_Uplinkbandwidth);

        //视频统计View
        tv_video_Downlink = (TextView) findViewById(R.id.video_Downlink);
        tv_video_Uplink = (TextView) findViewById(R.id.video_Uplink);
        tv_video_Bidirectionaldelay = (TextView) findViewById(R.id.video_Bidirectionaldelay);
        tv_video_downstreambandwidth = (TextView) findViewById(R.id.video_downstreambandwidth);
        tv_video_Uplinkbandwidth = (TextView) findViewById(R.id.video_Uplinkbandwidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calling, menu);
        //返回true才会显示overflow按钮
        return true;
    }

    @Override
    protected void onPause() {
        System.out.println("mCallState:" + mCallState);
        // 当前状态如果正在通话、发出、回铃、呼入中，且存在视频，则暂停视频
        if ((mCallState == GlobalConstant.CALL_STATE_TALKING
                || mCallState == GlobalConstant.CALL_STATE_OUTGOING
                || mCallState == GlobalConstant.CALL_STATE_ALERTED
                || mCallState == GlobalConstant.CALL_STATE_INCOMING)
                && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {

            //该标识用于标识当前是否切换到Calling界面
            boolean aBoolean = prefs.getBoolean(GlobalConstant.ACTION_CALLSTATE, true);
            if (CallingManager.getInstance()
                    .getCallingData().size() > 0) {     //判断当前是否有呼叫
                if (!aBoolean) {
                    //为了解决视频单呼中 新来对讲 本地远端视频会闪烁问题
                    //切换页面时，隐藏掉视频，否则会造成遮盖百度地图问题
                    mLocalView.setZOrderMediaOverlay(false);
                    mLocalView.setVisibility(View.INVISIBLE);
                    mRemoteView.setZOrderMediaOverlay(false);
                    mRemoteView.setVisibility(View.INVISIBLE);
                    Log.e("Sws测试mRemoteView", "|  onpause   执行隐藏");
                }
            }
        }
        Log.e("Sws测试二次呼叫", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //mLocalView.setVisibility(View.GONE);
        Log.e("6666666666666666666666666666666", "|  onStop");
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }


    @Override
    protected void onResume() {

        //设置默认摄像头为前置还是后置  1为前置，2为后置
        cameraCode = prefs.getString(GlobalConstant.ACTION_GETCAMERA, "1");
        MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));

        //设置帧率 比特率 分辨率
        String bitrateString = prefs.getString(GlobalConstant.SP_VIDEO_BITRATE, "0");
        String framerateString = prefs.getString(GlobalConstant.SP_VIDEO_FRAMERATE, "0");
        String width = prefs.getString("radioWidth", "0");
        String height = prefs.getString("radioHeight", "0");

        if (bitrateString.equals("0")) {
            bitrateString = "1000";
        } else if (bitrateString.equals("")) {
            bitrateString = "1000";
        }

        if (framerateString.equals("0")) {
            framerateString = "20";
        } else if (framerateString.equals("")) {
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


        //得到前置摄像头旋转角度
        String xzjd_q = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "0");
        String xzjd_h = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "180");
        String videoSend_Proportion = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION, "0");

        if (cameraCode.equals("1")) {
            //编码宽度，高度，帧率，码率，旋转角度，0，true   设置前置
            MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_q), 0, true, Integer.valueOf(videoSend_Proportion)); // , Integer.valueOf(videoSend_Proportion)
        } else if (cameraCode.equals("2")) {
            //编码宽度，高度，帧率，码率，旋转角度，0，true   设置后置   , Integer.valueOf(videoSend_Proportion)
            MediaEngine.GetInstance().ME_SetVideoCodecParam(Integer.valueOf(width), Integer.valueOf(height), framerate, bitrate, Integer.valueOf(xzjd_h), 0, true, Integer.valueOf(videoSend_Proportion)); //
        }

        if ((mCallState == GlobalConstant.CALL_STATE_TALKING
                || mCallState == GlobalConstant.CALL_STATE_OUTGOING
                || mCallState == GlobalConstant.CALL_STATE_ALERTED
                || mCallState == GlobalConstant.CALL_STATE_INCOMING)
                && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {

            boolean aBoolean = prefs.getBoolean(GlobalConstant.ACTION_CALLSTATE, true);
//            if (CallingManager.getInstance()
//                    .getCallingData().size() > 0){
//
//
//            if (!aBoolean){
            if (mLocalView instanceof GLSurfaceView)
                ((GLSurfaceView) mLocalView).onResume();
            if (mRemoteView instanceof GLSurfaceView)
                ((GLSurfaceView) mRemoteView).onResume();
            mLocalView.setZOrderMediaOverlay(true);
            mLocalView.setVisibility(View.VISIBLE);
            mRemoteView.setZOrderMediaOverlay(true);
            //mRemoteView.setZOrderOnTop(true);
            mRemoteView.setVisibility(View.VISIBLE);
            System.out.println("又来电话了onResumeonResumeonResumeonResumeonResume");
//            }
//            }
        }
        //解决远程视频不显示
        if ((mCallState == GlobalConstant.CALL_STATE_OUTGOING || mCallState == GlobalConstant.CALL_STATE_ALERTED || mCallState == GlobalConstant.CALL_STATE_INCOMING) && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
            mRemoteView.setVisibility(View.VISIBLE);


        } else if (mCallState == GlobalConstant.CALL_STATE_TALKING && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
            mRemoteView.setVisibility(View.VISIBLE);

        }
        if ((mCallState == GlobalConstant.CALL_STATE_OUTGOING || mCallState == GlobalConstant.CALL_STATE_ALERTED || mCallState == GlobalConstant.CALL_STATE_INCOMING) && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
            mLocalView.setVisibility(View.VISIBLE);
        } else if (mCallState == GlobalConstant.CALL_STATE_TALKING && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
            mLocalView.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (callingBroadcastReceiver != null) {
            unregisterReceiver(callingBroadcastReceiver);
        }
        if (responseBroadcastReceiver != null) {
            unregisterReceiver(responseBroadcastReceiver);
        }
        if (surfaceViewReceiver != null) {
            unregisterReceiver(surfaceViewReceiver);
        }

        if (handler != null) {
            handler = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    // 初始化小工具
    private void initWidgets() {
        //显示批量删除
        imgbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (layout.getVisibility() == View.GONE) {

                    if (listAdapter.getCount() > 0) {
                        topPlan.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        CallingRecordListAdapter callingRecordListAdapter = null;
                        for (int i = 0; i < callingList.getAdapter().getCount(); i++) {
                            //						CallingRecords callingRecords=(CallingRecords) callingList.getAdapter().getItem(i);
                            callingRecordListAdapter = (CallingRecordListAdapter) callingList.getAdapter();
                            callingRecordListAdapter.isckVISIBLE = true;
                            //						Log.e("成员", i+"条"+callingRecords.getBuddyNo()+","+callingRecords.getId());
                        }
                        callingList.setAdapter(callingRecordListAdapter);
                    } else {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_null),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //取消批量删除
        Button btnCancel = (Button) findViewById(R.id.Cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (layout.getVisibility() == View.VISIBLE) {
                    topPlan.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                    if (listAdapter.getCount() > 0) {
                        CallingRecordListAdapter callingRecordListAdapter = null;
                        for (int i = 0; i < callingList.getAdapter().getCount(); i++) {
                            CallingRecords callingRecords = (CallingRecords) callingList.getAdapter().getItem(i);
                            callingRecordListAdapter = (CallingRecordListAdapter) callingList.getAdapter();
                            callingRecordListAdapter.isckVISIBLE = false;
                            callingRecords.setIscheck(false);
                            //Log.e("成员", i+"条"+callingRecords.getBuddyNo()+","+callingRecords.getId());
                        }
                        callingList.setAdapter(callingRecordListAdapter);
                    }
                }
            }
        });

        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                CallingRecordListAdapter callingRecordListAdapter = null;
                for (int i = 0; i < callingList.getAdapter().getCount(); i++) {
                    CallingRecords callingRecords = (CallingRecords) callingList.getAdapter().getItem(i);
                    callingRecordListAdapter = (CallingRecordListAdapter) callingList.getAdapter();
                    callingRecordListAdapter.ischecked = isChecked;
                    callingRecords.setIscheck(isChecked);
                    Log.e("被选中", callingRecords.getBuddyNo());
                }
                callingList.setAdapter(callingRecordListAdapter);
            }
        });

        btndelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallingRecordListAdapter callingRecordListAdapter = null;
                for (int i = 0; i < callingList.getAdapter().getCount(); i++) {
                    CallingRecords callingRecords = (CallingRecords) callingList.getAdapter().getItem(i);
                    callingRecordListAdapter = (CallingRecordListAdapter) callingList.getAdapter();
                    //callingRecordListAdapter.isckVISIBLE=true;
                    boolean checkBoolean = callingRecords.getIscheck();//callingRecordListAdapter.getchecked();

                    try {
                        if (checkBoolean) {
                            Log.e("成员", i + "条" + callingRecords.getBuddyNo() + "," + callingRecords.getId() + "," + checkBoolean);
                            SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
                            sqLiteHelper.open();
                            sqLiteHelper.deleteCallingRecords(callingRecords.getId().toString());
                            sqLiteHelper.closeclose();
                            removePopupWindow();
                            //
                        }
                    } catch (Exception e) {
                        Log.e("CallActivity 788  程序执行出现异常", "" + e.getMessage());
                    }

                }
                callingRecordListAdapter.isckVISIBLE = false;
                topPlan.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                //刷新页面
                initCallingData();
            }
        });
        // 取得当前用户
        userNo = sipUser.getUsername();//MtcDelegate.getLoginedUser();
        // 取得通话记录
        initCallingData();
        callingRelativeLayout = (RelativeLayout) findViewById(R.id.callingRelativeLayout);
        callingShowRelativeLayout = (RelativeLayout) findViewById(R.id.callingShowRelativeLayout);
        callingList = (ListView) findViewById(R.id.callinglist);
        btnKeyboardShow = (ImageButton) findViewById(R.id.btnKeyboardShow);
        callingKeyboardLayout = (RelativeLayout) findViewById(R.id.callingKeyboardLayout);
        //keyboard_view = (android.inputmethodservice.KeyboardView) findViewById(R.id.calling_keyboard);
        numEditText = (EditText) findViewById(R.id.numEditText);
        //设置EditText失去焦点
        numEditText.setInputType(InputType.TYPE_NULL);
        btnBackup = (Button) findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(this);
        btnShrink = (Button) findViewById(R.id.btnShrink);
        btnCallVoice = (Button) findViewById(R.id.btnCallVoice);
        btnCallVideo = (Button) findViewById(R.id.btnCallVideo);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        Log.e("cklist", ckList.size() + "条");
        // 设置绑定
        listAdapter = new CallingRecordListAdapter(this, callingrecords, callingList);
        callingList.setAdapter(listAdapter);
        // 设置点击事件
        callingList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                currCallingRecord = callingrecords.get(arg2);
                // 显示上下文菜单
                showWindow(arg1);
            }
        });

        // 显示键盘按钮点击
        btnKeyboardShow.setOnClickListener(new BtnOnClickListener());
        // 隐藏键盘按钮点击
        btnShrink.setOnClickListener(new BtnOnClickListener());

        // 语音通话
        btnCallVoice.setOnClickListener(new BtnOnClickListener());
        // 视频通话
        btnCallVideo.setOnClickListener(new BtnOnClickListener());
        // 添加联系人
        btnAddContact.setOnClickListener(new BtnOnClickListener());

        // 通话中
        callingLocalViewLayout = (RelativeLayout) findViewById(R.id.callingLocalViewLayout);
        callingLocalViewTitle = (RelativeLayout) findViewById(R.id.callingLocalViewTitle);
        callingLocalViewVideo = (LinearLayout) findViewById(R.id.callingLocalViewVideo);
        callingLocalViewAudio = (LinearLayout) findViewById(R.id.callingLocalViewAudio);
        txtOutUserNo = (TextView) findViewById(R.id.txtOutUserNo);
        txtOutUserName = (TextView) findViewById(R.id.txtOutUserName);
        txtAudioUserName = (TextView) findViewById(R.id.txtAudioUserName);
        txtAudioUserNo = (TextView) findViewById(R.id.txtAudioUserNo);
        session_time_state = (Chronometer) findViewById(R.id.session_time_state);
        session_time_state_audio = (Chronometer) findViewById(R.id.session_time_state_audio);
        callingLocalViewFrame = (RelativeLayout) findViewById(R.id.callingLocalViewFrame);
        //btnViewStatus = (Button) findViewById(R.id.btnViewStatus);
        btnHangUp = (Button) findViewById(R.id.btnHangUp);
        btnVoiceAnswer = (Button) findViewById(R.id.btnVoiceAnswer);
        btnVideoAnswer = (Button) findViewById(R.id.btnVideoAnswer);
        btnDecline = (Button) findViewById(R.id.btnDecline);
        btnHandsFree = (Button) findViewById(R.id.btnHandsFree);
        btnMute = (Button) findViewById(R.id.btnMute);
        btnSwitchCamera = (Button) findViewById(R.id.btnSwitchCamera);
        btnHoldon = (Button) findViewById(R.id.btnHoldon);
        btnInviteJoin = (Button) findViewById(R.id.btnInviteJoin);
        callingRemoteViewLayout = (RelativeLayout) findViewById(R.id.callingRemoteViewLayout);
        callingRemoteViewFrame = (FrameLayout) findViewById(R.id.callingRemoteViewFrame);
        callingRemoteViewFrameInfo = (LinearLayout) findViewById(R.id.callingRemoteViewFrameInfo);
        videoUserName = (TextView) findViewById(R.id.videoUserName);
        videoUserNo = (TextView) findViewById(R.id.videoUserNo);
        videoUserTime = (Chronometer) findViewById(R.id.videoUserTime);
        //txtCallingMessage = (TextView) findViewById(R.id.txtCallingMessage);
        btnGone = (Button) findViewById(R.id.btnGone);
        btnInfo = (Button) findViewById(R.id.btnInfo);


        // 挂断按钮
        btnHangUp.setOnClickListener(new BtnOnClickListener());
        // 语音回复
        btnVoiceAnswer.setOnClickListener(new BtnOnClickListener());
        // 视频回复
        btnVideoAnswer.setOnClickListener(new BtnOnClickListener());
        // 忽略
        btnDecline.setOnClickListener(new BtnOnClickListener());
        // 免提
        btnHandsFree.setOnClickListener(new BtnOnClickListener());
        // 静音
        btnMute.setOnClickListener(new BtnOnClickListener());
        // 保持
        btnHoldon.setOnClickListener(new BtnOnClickListener());
        // 邀请入会
        btnInviteJoin.setOnClickListener(new BtnOnClickListener());
        // 状态
        //btnViewStatus.setOnClickListener(new BtnOnClickListener());
        // 隐藏
        btnGone.setOnClickListener(new BtnOnClickListener());
        // 信息
        btnInfo.setOnClickListener(new BtnOnClickListener());
        //切换本地摄像头
        btnSwitchCamera.setOnClickListener(new LocalViewClick());

		/*mRemoteView=(SurfaceView)findViewById(R.id.surfaceview);
        mLocalView=(SurfaceView)findViewById(R.id.surfaceviewlocal);*/

        // 设置对方视频---
        //mRemoteView = new SurfaceView(this);
        mRemoteView.setVisibility(View.INVISIBLE);
        mRemoteView.setZOrderMediaOverlay(true);
        //mRemoteView.setZOrderOnTop(true);
        Log.e("mRemoteView", "|  902   执行隐藏");
        //callingRemoteViewFrame.addView(mRemoteView, 0);

        // 设置本地视频
        //String[] mVideoCameraArray = getResources().getStringArray(R.array.video_camera);
        //String camera = prefs.getString(GlobalConstant.SP_VIDEO_CAMERA, mVideoCameraArray[1]);
        //mLocalView = ViERenderer.CreateLocalRenderer(this); //Sws 11/07

        //mLocalView.setZOrderMediaOverlay(true);
        //mLocalView.setZOrderOnTop(true);

		/*//设置默认摄像头为前置还是后置  1为前置，2为后置
        int camera = prefs.getInt(GlobalConstant.ACTION_GETCAMERA, 1);
		MediaEngine.GetInstance().ME_ChangeVideoDevice(mSessId, camera);
		Toast.makeText(CallingActivity.this, "camera:"+camera, Toast.LENGTH_SHORT).show();*/
        //11/10 Sws 隐藏  没有影响
        // 设置摄像头
		/*	int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
		if (camCnt > 1) {
	    	if (camera.equals(mVideoCameraArray[0])){
				MtcMedia.Mtc_VideoSetInputDev("front");
	    	}
	    	else {
				MtcMedia.Mtc_VideoSetInputDev("back");
	    	}
		}*/
        mLocalView.setVisibility(View.INVISIBLE);
        //mLocalView.setZOrderMediaOverlay(true);
        //callingLocalViewFrame.addView(mLocalView, 0);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Calling Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    // 页面中按钮点击事件统一处理
    private class BtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            // TODO Auto-generated method stub
            if (v.getId() == R.id.btnKeyboardShow) {
                // 显示键盘按钮点击
                btnKeyboardShow.setVisibility(View.GONE);
                callingKeyboardLayout.setVisibility(View.VISIBLE);
                //getKeyboardUtil().showKeyboard();
                //设置键盘显示
                callinglinearLayout.setVisibility(View.VISIBLE);

                numEditText.setText("");


                if (mCallState == GlobalConstant.CALL_STATE_TALKING)//在视频通话中只能拨打dtmf
                {
                    btnCallVoice.setEnabled(false);
                    btnCallVideo.setEnabled(false);
                    btnAddContact.setEnabled(false);
                    btnBackup.setEnabled(true);
                    numEditText.setEnabled(false);
                } else {
                    btnCallVoice.setEnabled(true);
                    btnCallVideo.setEnabled(true);
                    btnAddContact.setEnabled(true);
                    btnBackup.setEnabled(true);
                    numEditText.setEnabled(true);
                }

            } else if (v.getId() == R.id.btnShrink) {

                // 隐藏键盘按钮点击
                callingKeyboardLayout.setVisibility(View.GONE);
                btnKeyboardShow.setVisibility(View.VISIBLE);
                sb.setLength(0);
            } else if (v.getId() == R.id.btnCallVoice) {

                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    Toast.makeText(CallingActivity.this,
                            getString(R.string.info_network_unavailable),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Editor edit = prefs.edit();
                edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                edit.apply();
                Log.e("*******Sws*****", "voice");

                // 语音通话，通过广播拨号
                Intent callIntent = new Intent(ACTION_CALLING_CALLVOICE);
                callIntent.putExtra("callUserNo", numEditText.getText().toString());
                sendBroadcast(callIntent);

                //hideAndShowLayout();   //隐藏显示布局  sws add 10-25
                //findViewById(R.id.callingKeyboardToolLayout).setVisibility(View.GONE);

                sb.setLength(0);
            } else if (v.getId() == R.id.btnCallVideo) {
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    Toast.makeText(CallingActivity.this,
                            getString(R.string.info_network_unavailable),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 有视频通话不能再次视频
                if (CallingManager.getInstance().isIsvideoing()) {
                    Toast.makeText(CallingActivity.this, getResources().getString(R.string.info_video_inuse), Toast.LENGTH_LONG).show();
                    return;
                }
				/*CallingManager.getInstance().setIsvideoing(true);*/
                Editor edit = prefs.edit();
                edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VIDEO);
                edit.apply();
                Log.e("*******Sws*****", "video");
                Log.e("测试**************通话", "发送广播 携带对方号码： " + numEditText.getText().toString());
                // 视频通话
                Intent callVoiceIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVIDEO);
                callVoiceIntent.putExtra("callUserNo", numEditText.getText().toString());
                sendBroadcast(callVoiceIntent);
                sb.setLength(0);


            } else if (v.getId() == R.id.btnAddContact) {
                // 添加联系人
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ContactAddFragment contactAddFragment = (ContactAddFragment) fragmentManager.findFragmentByTag("contactadd");
                if (contactAddFragment == null) {
                    contactAddFragment = new ContactAddFragment();
                }
                contactAddFragment.setEditNum(numEditText.getText().toString());
                if (!contactAddFragment.isAdded()) {
                    fragmentTransaction.add(R.id.callingShowRelativeLayout, contactAddFragment, "contactadd").commit();
                } else {
                    fragmentTransaction.show(contactAddFragment).commit();
                }
                //sb.setLength(0);
            } else if (v.getId() == R.id.btnHangUp) {
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    Toast.makeText(CallingActivity.this,
                            getString(R.string.info_network_unavailable),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 挂断按钮
                Log.e("手动点击了Calling界面的挂断", "");
                Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
                sendBroadcast(callIntent);
                //hideViewMethod();
                //session_time_state_audio.clearComposingText();
                //mLocalView.setVisibility(View.GONE);
                // MediaEngine.GetInstance().ME_Hangup(mSessId);

            } else if (v.getId() == R.id.btnVoiceAnswer) {
                //语音回复
                voiceResponse();
                CloseSpeaker();
                audioManager.setMicrophoneMute(false);

            } else if (v.getId() == R.id.btnVideoAnswer) {



                // 视频回复
                videoResponse();
                CloseSpeaker();
                audioManager.setMicrophoneMute(false);
            } else if (v.getId() == R.id.btnDecline) {
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    Toast.makeText(CallingActivity.this,
                            getString(R.string.info_network_unavailable),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 忽略
                Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLDECLINE);
                sendBroadcast(callIntent);
            } else if (v.getId() == R.id.btnHandsFree) {
                // 免提
                //系统方法
                if (audioManager.isSpeakerphoneOn()) {
                    btnHandsFree.setText("免提");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            CloseSpeaker();
                        }
                    };
                    ThreadPoolProxy.getInstance().execute(runnable);
                } else {
                    btnHandsFree.setText("取消免提");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            OpenSpeaker();
                        }
                    };
                    ThreadPoolProxy.getInstance().execute(runnable);
                }
            } else if (v.getId() == R.id.btnMute) {

                if (Build.MODEL.equals("3280")) {
                    //飞鸟10寸Pad 无法静音 只能通过修改发送语音增益来达到静音效果
                    if (btnMute.getText().toString().trim().equals("静音")) {
                        MediaEngine.GetInstance().ME_SetTxLevel(mSessId,0);
                        btnMute.setText("取消静音");
                    } else if (btnMute.getText().toString().trim().equals("取消静音")) {
                        MediaEngine.GetInstance().ME_SetTxLevel(mSessId,1);
                        btnMute.setText("静音");
                    }
                    return;
                }



                // 静音（静音方式使用本页面方法，不在给MainActivity发送静音广播）
                //判断当前是否处于静音状态
                if (btnMute.getText().toString().trim().equals("静音")) {
                    audioManager.setMicrophoneMute(true);
                    Log.e("集体测试", "集体测试 设置麦克风 calling 1031  静音");
                    btnMute.setText("取消静音");
                } else if (btnMute.getText().toString().trim().equals("取消静音")) {
                    audioManager.setMicrophoneMute(false);
                    Log.e("集体测试", "集体测试 设置麦克风 intercom 1037  取消静音");
                    btnMute.setText("静音");
                }
            } else if (v.getId() == R.id.btnHoldon) {
                // 保持
                String str = btnHoldon.getText().toString();
                if (str.equals("保持")) {
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHOLD);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
                    sendBroadcast(callIntent);
                    btnHoldon.setText("解除保持");
                } else {
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLUNHOLD);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
                    sendBroadcast(callIntent);
                    btnHoldon.setText("保持");
                }
            } else if (v.getId() == R.id.btnInviteJoin) {
                // 邀请入会
                //}else if (v.getId() == R.id.btnViewStatus){   //开启关闭摄像头 现在不需要了
                // 状态
				/*if(mLocalView.getVisibility()==View.VISIBLE){
					//隐藏
					mLocalView.setVisibility(View.INVISIBLE);
					Toast.makeText(CallingActivity.this, "隐藏", Toast.LENGTH_SHORT).show();
					btnViewStatus.setBackgroundResource(R.drawable.view_gone);
				}
				else{
					//显示
					mLocalView.setVisibility(View.VISIBLE);
					Toast.makeText(CallingActivity.this, "显示", Toast.LENGTH_SHORT).show();
					btnViewStatus.setBackgroundResource(R.drawable.view_show);
				}*/
            } else if (v.getId() == R.id.btnGone) {
                // 隐藏
                if (callingShowRelativeLayout.getVisibility() == View.VISIBLE) {
                    callingShowRelativeLayout.setVisibility(View.GONE);
                    btnGone.setText(getString(R.string.btn_position_visible));
                    btnGone.setBackgroundDrawable(getResources().getDrawable(R.drawable.pos_show_click));

                    Editor editor = prefs.edit();
                    editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN, false);
                    editor.commit();
//                    if (mLocalView.getVisibility() == View.VISIBLE) {
//                        mLocalView.setVisibility(View.INVISIBLE);
//                    }
                } else {
                    callingShowRelativeLayout.setVisibility(View.VISIBLE);
                    btnGone.setText(getString(R.string.btn_position_gone));
                    btnGone.setBackgroundDrawable(getResources().getDrawable(R.drawable.pos_gone_click));

                    Editor editor = prefs.edit();
                    editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN, true);
                    editor.commit();

                    if (mRemoteView.getVisibility() == View.VISIBLE) {
                        if (mLocalView.getVisibility() != View.VISIBLE) {
                            mLocalView.setVisibility(View.VISIBLE);
                        }
                    }

                }
            } else if (v.getId() == R.id.btnInfo) {
                // 信息
                if (callingRemoteViewFrameInfo.getVisibility() == View.VISIBLE) {
                    callingRemoteViewFrameInfo.setVisibility(View.INVISIBLE);
                } else {
                    callingRemoteViewFrameInfo.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    //视频回复
    private void videoResponse() {

        if (ptt_3g_PadApplication.isNetConnection() == false) {
            Toast.makeText(CallingActivity.this,
                    getString(R.string.info_network_unavailable),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //MediaEngine.GetInstance().ME_Trace(true, true, true);//
        // 视频回复
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVIDEOANSWER);
        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        sendBroadcast(callIntent);
    }

    //语音回复
    private void voiceResponse() {
        if (ptt_3g_PadApplication.isNetConnection() == false) {
            Toast.makeText(CallingActivity.this,
                    getString(R.string.info_network_unavailable),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 语音回复
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        sendBroadcast(callIntent);
    }

    private int currVolume = 0;

    //打开扬声器
    public void OpenSpeaker() {
        try {
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
            int currVolume = ptt_3g_PadApplication.getCurrentVolume();
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                Log.e("集体测试", "测试扬声器calling    1153   true");
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        currVolume,
                        AudioManager.STREAM_VOICE_CALL);
            }else {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        currVolume,
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
               // if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    //currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                    currVolume = ptt_3g_PadApplication.getCurrentVolume();
                    Log.e("集体测试", "测试扬声器    calling    1169   false");
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                            currVolume,
                            AudioManager.STREAM_VOICE_CALL);
//                }else {
//                    int currVolume = ptt_3g_PadApplication.getCurrentVolume();
//                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//                            currVolume,
//                            AudioManager.STREAM_VOICE_CALL);
//                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化通话记录数据
    private void initCallingData() {
        if (callingrecords == null) {
            callingrecords = new ArrayList<CallingRecords>();
        }
        if (ckList == null) {
            ckList = new ArrayList<CheckBox>();
        }

        //new AsyncInitCallingData().execute();
        new AsyncInitCallingData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
    }

    private class AsyncInitCallingData extends AsyncTask<Object, Object, Object> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            // 取得通话记录
            SQLiteHelper sqLiteHelper = new SQLiteHelper(getApplicationContext());
            sqLiteHelper.open();
            int pageSize = Integer.valueOf(getString(R.string.default_callingrecord_count));
            // 取出全部通话记录
            asyncCallingRecords = sqLiteHelper.getCallingRecordsByUserNO(userNo, 0, pageSize);
//             if (asyncCallingRecords.size() > 1){
//            for (int i = 0; i < asyncCallingRecords.size() -1; i++){
//                if (asyncCallingRecords.get(i).getTime().equals(asyncCallingRecords.get(i+1).getTime())){
//                    asyncCallingRecords.remove(i);
//
//                }
//            }
//            }
            sqLiteHelper.closeclose();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            // 添加到列表
            callingrecords.clear();
            callingrecords.addAll(asyncCallingRecords);
            //yueadd0629
            //ckList.clear();
            //			for (CallingRecords call : asyncCallingRecords) {
            //				Log.e("call开始", call.getBuddyNo());
            //				CheckBox ckBox=new CheckBox(CallingActivity.this);
            //				//ckBox.setVisibility(View.GONE);
            //				ckBox.setId(call.getId()+1);
            //				ckBox.setWidth(10);
            //				ckBox.setHeight(10);
            //				ckList.add(ckBox);
            //				Log.e("call结束"+ckList.size(), call.getBuddyNo());
            //			}
            listAdapter.notifyDataSetChanged();
            textNum.setText("共[" + listAdapter.getCount() + "]条");
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    // 显示操作菜单窗口
    private void showWindow(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        if ((currCallingRecord.getConferenceNo() != null)
                && (currCallingRecord.getConferenceNo().length() > 0)) {
            // 会议
            View view = layoutInflater.inflate(R.layout.layout_calling_conference_menu, null);
            btnCallingMenuAudio = (Button) view.findViewById(R.id.btnCallingMenuAudio);
            btnCallingMenuVideo = (Button) view.findViewById(R.id.btnCallingMenuVideo);
            btnCallingMenuBroadcast = (Button) view.findViewById(R.id.btnCallingMenubroadcast);
            btnCallingMenuDelete = (Button) view.findViewById(R.id.btnCallingMenuDelete);
            if (currCallingRecord.getConferenceType() == GlobalConstant.CONFERENCE_TYPE_BROADCAST) {
                // 广播
                btnCallingMenuAudio.setVisibility(View.GONE);
                btnCallingMenuVideo.setVisibility(View.GONE);
                btnCallingMenuBroadcast.setVisibility(View.VISIBLE);
            } else {
                if (currCallingRecord.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
                    // 视频会议
                    btnCallingMenuAudio.setVisibility(View.GONE);
                    btnCallingMenuVideo.setVisibility(View.VISIBLE);
                    btnCallingMenuBroadcast.setVisibility(View.GONE);
                } else {
                    // 语音会议
                    btnCallingMenuAudio.setVisibility(View.VISIBLE);
                    btnCallingMenuVideo.setVisibility(View.GONE);
                    btnCallingMenuBroadcast.setVisibility(View.GONE);
                }
            }
            // 设置侦听
            btnCallingMenuAudio.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_network_unavailable),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Editor edit = prefs.edit();
                    edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                    edit.apply();
                    Log.e("*******Sws*****", "voice");

                    // TODO Auto-generated method stub
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CONFERENCE);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_CONFERENCEID, currCallingRecord.getId());
                    Log.e("======CallingActivity=======1163=========", "currCallingRecord:" + currCallingRecord.getId());
                    sendBroadcast(callIntent);
                    removePopupWindow();
                }
            });
            btnCallingMenuVideo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_network_unavailable),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 有视频通话不能再次视频
                    if (CallingManager.getInstance().isIsvideoing()) {
                        Toast.makeText(CallingActivity.this, getResources().getString(R.string.info_video_inuse), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Editor edit = prefs.edit();
                    edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VIDEO);
                    edit.apply();
                    Log.e("*******Sws*****", "video");
                    //mLocalView.setZOrderMediaOverlay(true);
                    //mLocalView.setZOrderOnTop(true);
					/*CallingManager.getInstance().setIsvideoi```ng(true);*/
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CONFERENCE);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_CONFERENCEID, currCallingRecord.getId());
                    sendBroadcast(callIntent);
                    removePopupWindow();
                }
            });
            btnCallingMenuBroadcast.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_network_unavailable),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CONFERENCE);
                    callIntent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_CONFERENCEID, currCallingRecord.getId());
                    sendBroadcast(callIntent);
                    removePopupWindow();
                }
            });
            btnCallingMenuDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
                    sqLiteHelper.open();
                    sqLiteHelper.deleteMeetingRecords(currCallingRecord.getId().toString());
                    sqLiteHelper.closeclose();
                    removePopupWindow();
                    //刷新页面
					/*callingRecordsList.clear();*/
                    initCallingData();
                    //listAdapter.notifyDataSetChanged();
					/*simpleAdapter.notifyDataSetChanged();*/
                }
            });
            if (conferencePopupWindow == null) {
                conferencePopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            conferencePopupWindow.setFocusable(true);
            conferencePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            // popupWindow.setOutsideTouchable(true);
            // 保存anchor在屏幕中的位置
            int[] location = new int[2];
            // 读取位置anchor座标
            v.getLocationOnScreen(location);
            conferencePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40, location[1] - v.getHeight() - 5);
        } else {
//            if (CommonMethod.checkConcactExist(getApplicationContext(), currCallingRecord.getBuddyNo())) {
//                // 本地联系人
//                View view = layoutInflater.inflate(R.layout.layout_calling_local_menu, null);
//                btnCallingMenuAudio = (Button) view.findViewById(R.id.btnCallingMenuAudio);
//                btnCallingMenuEdit = (Button) view.findViewById(R.id.btnCallingMenuEdit);
//                btnCallingMenuDelete = (Button) view.findViewById(R.id.btnCallingMenuDelete);
//                btnCallingMenuAudio.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICE);
//                        callIntent.putExtra("callUserNo", currCallingRecord.getBuddyNo());
//                        sendBroadcast(callIntent);
//                        removePopupWindow();
//                    }
//                });
//                btnCallingMenuEdit.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FragmentManager fragmentManager = getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        ContactAddFragment contactAddFragment = (ContactAddFragment) fragmentManager.findFragmentByTag("contactadd");
//                        if (contactAddFragment != null) {
//                            fragmentTransaction.remove(contactAddFragment);
//                        }
//                        contactAddFragment = new ContactAddFragment();
//                        contactAddFragment.setEditNo(currCallingRecord.getBuddyNm());//编辑联系人
//                        fragmentTransaction.add(R.id.callingShowRelativeLayout, contactAddFragment, "contactadd").commitAllowingStateLoss();
//
//
//                        removePopupWindow();
//                    }
//                });
//                //删除单个通话记录
//                btnCallingMenuDelete.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
//                        sqLiteHelper.open();
//                        sqLiteHelper.deleteCallingRecords(currCallingRecord.getId().toString());
//                        sqLiteHelper.closeclose();
//
//                        removePopupWindow();
//
//                        //刷新页面
//						/*callingRecordsList.clear();*/
//                        initCallingData();
//                        //listAdapter.notifyDataSetChanged();
//						/*simpleAdapter.notifyDataSetChanged();*/
//                    }
//                });
//                if (localPopupWindow == null) {
//                    localPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                }
//
//                localPopupWindow.setFocusable(true);
//                localPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//                // popupWindow.setOutsideTouchable(true);
//                // 保存anchor在屏幕中的位置
//                int[] location = new int[2];
//                // 读取位置anchor座标
//                v.getLocationOnScreen(location);
//                localPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40, location[1] - v.getHeight() - 5);
//            } else {
            //系统联系人
            View view = layoutInflater.inflate(R.layout.layout_calling_system_menu, null);

            btnCallingMenuAudio = (Button) view.findViewById(R.id.btnCallingMenuAudio);
            btnCallingMenuVideo = (Button) view.findViewById(R.id.btnCallingMenuVideo);
            btnCallingMenuMessage = (Button) view.findViewById(R.id.btnCallingMenuMessage);
            btnCallingMenuEdit = (Button) view.findViewById(R.id.btnCallingMenuEdit);
            btnCallingMenuDelete = (Button) view.findViewById(R.id.btnCallingMenuDelete);
            btnCallingMenuAudio.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("测试calling", "音频");
                    Editor edit = prefs.edit();
                    edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                    edit.apply();
                    Log.e("*******Sws*****", "voice");

                    Intent callIntent = new Intent(ACTION_CALLING_CALLVOICE);
                    callIntent.putExtra("callUserNo", currCallingRecord.getBuddyNo());
                    sendBroadcast(callIntent);
                    removePopupWindow();
                }
            });
            btnCallingMenuVideo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("测试calling", "视频");

                    Editor edit = prefs.edit();
                    edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VIDEO);
                    edit.apply();
                    Log.e("*******Sws*****", "video");
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVIDEO);
                    callIntent.putExtra("callUserNo", currCallingRecord.getBuddyNo());
                    sendBroadcast(callIntent);

                    removePopupWindow();
                }
            });
            btnCallingMenuMessage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_network_unavailable),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<String> selUserNo = new ArrayList<String>();
                    selUserNo.add(currCallingRecord.getBuddyNo());
                    Intent newMessageIntent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGMAINNEW);
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setContactList(selUserNo);
                    ;//设置全局变量
                    sendBroadcast(newMessageIntent);
                    removePopupWindow();
                }
            });
            btnCallingMenuEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        Toast.makeText(CallingActivity.this,
                                getString(R.string.info_network_unavailable),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ContactAddFragment contactAddFragment = (ContactAddFragment) fragmentManager.findFragmentByTag("contactadd");
                    if (contactAddFragment == null) {
                        contactAddFragment = new ContactAddFragment();
                    }

                    contactAddFragment.setEditNo(currCallingRecord.getBuddyNm());//编辑联系人
                    contactAddFragment.setEditNum(currCallingRecord.getBuddyNo());
                    if (!contactAddFragment.isAdded()) {
                        fragmentTransaction.add(R.id.callingShowRelativeLayout, contactAddFragment, "contactadd").commit();
                    }
                    removePopupWindow();

                }
            });
            btnCallingMenuDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
                    sqLiteHelper.open();
                    sqLiteHelper.deleteCallingRecords(currCallingRecord.getId().toString());
                    sqLiteHelper.closeclose();

                    removePopupWindow();

                    //刷新页面
						/*callingRecordsList.clear();*/
                    initCallingData();
						/*simpleAdapter.notifyDataSetChanged();*/
                    //listAdapter.notifyDataSetChanged();
                }
            });
            if (systemPopupWindow == null) {
                systemPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }

            systemPopupWindow.setFocusable(true);
            systemPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            // popupWindow.setOutsideTouchable(true);
            // 保存anchor在屏幕中的位置
            int[] location = new int[2];
            // 读取位置anchor座标
            v.getLocationOnScreen(location);
            systemPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40, location[1] - v.getHeight() - 5);
            //  }

        }
    }

    //移除弹出菜单窗口
    private void removePopupWindow() {
        if (localPopupWindow != null) {
            localPopupWindow.dismiss();
        }
        if (systemPopupWindow != null) {
            systemPopupWindow.dismiss();
        }
        if (conferencePopupWindow != null) {
            systemPopupWindow.dismiss();
        }
    }

    // 登出接收器
    public class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            finish();
        }
    }

    // 更新UI
    public class CallingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            //final String callAction = intent.getAction();
            /*new Thread(new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {*/
            //耗时操作
            // mHandler.sendEmptyMessage(0);
            //Message msg = new Message();
					/* @SuppressWarnings("rawtypes")
					List lists=new ArrayList();
	                lists.add(inUserNo);
	                lists.add(changeview);
	                lists.add(GlobalConstant.CALL_TYPE_CALLING);
	                msg.obj=lists;*/
            //msg.obj = intent;
            //msg.obj = GlobalConstant.CALL_TYPE_CALLING;//可以是基本类型，可以是对象，可以是List、map等
            Log.e("CallingActivity", "通知ui线程");
            //mHandler.sendMessage(msg);
              /*  }
            }).start();*///


            {
                String callAction = intent.getAction();
                if (callAction == GlobalConstant.ACTION_CALLING_OUTGOING) {

                    String inUserNo = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);

                    Log.e("测试calling", "calling 1595   dwSessId:" + dwSessId);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    int state = intent.getIntExtra(GlobalConstant.KEY_CALL_STATE, 0);
                    mSessId = dwSessId;
                    Log.e("**********通话OUTGOING Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    // TODO: 2018/12/7 呼出成功 通知主界面进行地图移除
                    Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                    intentS.putExtra("AddOrRemove","Remove");
                    sendBroadcast(intentS);

                    callingOutgoingDo(inUserNo, mSessId);//出错

                } else if (callAction == GlobalConstant.ACTION_CALLING_ALERTED) {
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话ALERTED Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    //mSessId = dwSessId;
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_STARTRINGBACK);
                    sendBroadcast(callIntent);
                } else if (callAction == GlobalConstant.ACTION_CALLING_TALKING) {


                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }

                    boolean aBoolean = prefs.getBoolean(GlobalConstant.ACTION_CALLING_ISANSWER, false);
                    if (aBoolean) {
                        //通话开始 默认关闭扬声器 打开麦克风
                        CloseSpeaker();
                        audioManager.setMicrophoneMute(false);
                    }

                    //通话开始 默认关闭扬声器 打开麦克风
//                    CloseSpeaker();
//                    audioManager.setMicrophoneMute(false);

                    //取得配置是否开启
                    boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);
                    if (mediastatistics) {
                        linearlayout_callInfo.setVisibility(View.VISIBLE);
                    } else {
                        linearlayout_callInfo.setVisibility(View.GONE);
                    }
//                    //渲染SurfaceView
//                    if (mLocalView == null) {
//                        mLocalView = (SurfaceView) findViewById(R.id.surfaceviewlocal);
//                        mLocalView.setZOrderOnTop(true);
//                        mLocalView.setZOrderMediaOverlay(true);
//                    }
//                    if (mRemoteView == null) {
//                        mRemoteView = (SurfaceView) findViewById(R.id.surfaceview);
//                    }
                    Log.e("**********通话TALKING Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    mSessId = dwSessId;

                    isStartTimer = true;

                    callingTalkingDo(mSessId);

                } else if (callAction == GlobalConstant.ACTION_CALLING_TERMED) {

                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    //String name=intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }

                    CallingManager.getInstance().removeCallingInfo(dwSessId);
                    CallingManager.getInstance().removeAnswerInfo(dwSessId);
                    Log.e("**********通话TERMED Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    // 如果不是当前通话则忽略
                    if (dwSessId != mSessId) {
                        return;
                    }
                    btnHoldon.setText("保持");
                    mSessId = dwSessId;
						/*boolean isvideoing = false;
        				for (CallingInfo callinginfo : CallingManager.getInstance().getCallingData()){
        					if ((callinginfo != null)
        						&& (callinginfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO)){
        						isvideoing = true;
        					}
        				}
        				CallingManager.getInstance().setIsvideoing(isvideoing);*/

                    isStartTimer = false;
                    if (linearlayout_callInfo.getVisibility() == View.VISIBLE) {
                        linearlayout_callInfo.setVisibility(View.GONE);
                    }
                    Intent setFalse = new Intent(GlobalConstant.MEETING_SETFALSE);
                    sendBroadcast(setFalse);

                    // TODO: 2018/12/7 当前通话结束 通知添加地图
                    Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                    intentS.putExtra("AddOrRemove","ADD");
                    sendBroadcast(intentS);

                    MediaEngine.GetInstance().ME_Hangup(dwSessId);
                    callingTermedDo(mSessId);


                    //TODO 恢复布局
                    if (surfaceIsChange){
                        mRemoteLinearLayout.removeAllViews();
                        linar_mLocal.removeAllViews();

                        surfaceIsChange = false;
                        mRemoteLinearLayout.addView(mRemoteView);
                        linar_mLocal.addView(mLocalView);
                    }


                    // 開始接聽下一路通話
                    if (CallingManager.getInstance().getAnswerData().size() <= 0) {
                        return;
                    }
                    Log.e("MainActivity======sws========", "Calling界面  发送下一轮接听");
                    // 通知接聽下一路通話
                    Intent answernext = new Intent(GlobalConstant.ACTION_CALLING_ANSWERNEXT);
                    sendBroadcast(answernext);




                } else if (callAction == GlobalConstant.ACTION_CALLING_INCOMING) {

//                    //收到新来电
//                    Log.e("============1759======","state:"+ isAppOnForeground());
//                    //判断当前App是否允许在后台，是：启动App，否：不做操作
//                    if (!isAppOnForeground()){
//                        Intent ootStartIntent=new Intent(context, MainActivity.class);
//                        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("AUTOLOGIN", true);
//                        context.startActivity(ootStartIntent);
//                        Log.e("============1765======","执行跳转");
//                    }
                    Log.e("CallingActivity", "进入ACTION_CALLING_INCOMING判断");
                    final String inUserNo = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    boolean mediaType = intent.getBooleanExtra(GlobalConstant.KEY_CALL_MEDIATYPE, false);
                    //final boolean changeview = intent.getBooleanExtra(GlobalConstant.KEY_CALL_CHANGEVIEW, true);
                    final boolean changeview = intent.getBooleanExtra(GlobalConstant.KEY_CALL_AUTOANSWER, false);

                    //boolean isvideo=intent.getBooleanExtra(GlobalConstant.KEY_CALL_ISVIDEO, false);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                    Log.e("===Sws测试===手机屏幕状态", "state:" + powerManager.isScreenOn());
                    if (!powerManager.isScreenOn()) {
                        //屏幕唤醒
                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                                | PowerManager.SCREEN_DIM_WAKE_LOCK, "StartupReceiver");//最后的参数是LogCat里用的Tag
                        wl.acquire();

                        //屏幕解锁
                        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("StartupReceiver");//参数是LogCat里用的Tag
                        kl.disableKeyguard();
                    }

                    if (!isAppOnForeground()) {
                        //开机启动
                        Intent mainIntent = new Intent(context, MainActivity.class);
                        //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mainIntent);
                    } else {
                    }


                    //OpenSpeaker();

                    Editor edit = prefs.edit();
                    edit.putBoolean(GlobalConstant.ACTION_CALLSTATE, true);
                    edit.apply();
                    Log.e("Sws测试二次呼叫", "calling 1674   设置为true");

                    Log.e("**********通话INCOMING Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    mSessId = dwSessId;

                    //TODO: 2018/12/7 收到新来电 通知主界面进行地图移除
                    Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                    intentS.putExtra("AddOrRemove","Remove");
                    sendBroadcast(intentS);

                    callingIncomingDo(inUserNo, mSessId, changeview, mediaType);
                    Log.e("incomingsaaaaaaaa", "callActivity : " + mediaType);
                } else if (callAction == GlobalConstant.ACTION_CALLING_STARTPREVIEW) {

                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话STARTPREVIEW Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
						/*if (dwSessId != mSessId){
        					return;
        				}*/
                    mSessId = dwSessId;
                    callingStartPreviewDo(mSessId);

                } else if (callAction == GlobalConstant.ACTION_CALLING_STARTVIDEO) {

                    // TODO Auto-generated method stub
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话STARTVIDEO Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
						/*if (dwSessId != mSessId){
        					return;
        				}*/
                    mSessId = dwSessId;
                    callingStartVideoDo(mSessId);

                } else if (callAction == GlobalConstant.ACTION_CALLING_CAPTURESIZE) {
                    // TODO Auto-generated method stub
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话CAPTURESIZE Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
						/*if (dwSessId != mSessId){
        					return;
        				}*/
                    //        				mSessId = dwSessId;  //2014-04-24 qq
                    int dwWidth = intent.getIntExtra(GlobalConstant.KEY_CALL_DWWIDTH, 0);
                    int dwHeight = intent.getIntExtra(GlobalConstant.KEY_CALL_DWHEIGHT, 0);
                    callingCaptureSizeDo(dwWidth, dwHeight);
                } else if (callAction == GlobalConstant.ACTION_CALLING_VIDEOSIZE) {
                    // TODO Auto-generated method stub
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话VIDEOSIZE Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
						/*if (dwSessId != mSessId){
        					return;
        				}*/
                    mSessId = dwSessId;
                    int dwWidth = intent.getIntExtra(GlobalConstant.KEY_CALL_DWWIDTH, 0);
                    int dwHeight = intent.getIntExtra(GlobalConstant.KEY_CALL_DWHEIGHT, 0);
                    callingVideoSizeDo(mSessId, dwWidth, dwHeight);
                } else if (callAction == GlobalConstant.ACTION_CALLING_STOPVIDEO) {
                    //Toast.makeText(CallingActivity.this, "&&&&&&&&&&&&&", Toast.LENGTH_SHORT).show();
                    Log.e("$$$$$$$$$$$$$", "$$$$$$$$$$$$$");

                    // TODO Auto-generated method stub
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("**********通话关闭视频Id比较*************", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    // 如果不是当前通话则忽略
                    if (dwSessId != mSessId) {
                        return;
                    }
                    mSessId = dwSessId;
                    callingStopVideDo(mSessId);
                    Log.e("********通话关闭视频CallActivity1140*******", "**********通话关闭视频*************");
                } else if (callAction == GlobalConstant.ACTION_CALLING_CALLHOLDOK) {
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("***通话CALLHOLDOK Id比较***不同则return", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    if (dwSessId != mSessId) {
                        return;
                    }
                    mSessId = dwSessId;
                    callingHoldOk();
                } else if (callAction == GlobalConstant.ACTION_CALLING_CALLHOLDFAILED) {
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("***通话CALLHOLDFAILED Id比较***不同则return", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    if (dwSessId != mSessId) {
                        return;
                    }
                    mSessId = dwSessId;
                    callingHoldFailed();
                } else if (callAction == GlobalConstant.ACTION_CALLING_CALLUNHOLDOK) {
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    Log.e("*****通话CALLUNHOLDOK Id比较******", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    mSessId = dwSessId;
                    callingUnHoldOk();
                } else if (callAction == GlobalConstant.ACTION_CALLING_CALLUNHOLDFAILED) {
                    int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                    if (calltype != GlobalConstant.CALL_TYPE_CALLING) {
                        return;
                    }
                    int dwSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, 0);
                    Log.e("*****通话CALLUNHOLDFAILED Id比较******", "当前dwSessId：" + dwSessId + "---mSessId:" + mSessId);
                    mSessId = dwSessId;
                    callingUnHoldFailed();
                }
            }
        }
    }

    private boolean stateCall = true;

    // 呼出操作
    private void callingOutgoingDo(String no, final int dwSessId) {



        String callType = prefs.getString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
        Log.e("callingOutgoingDo", "呼出操作  callType : " + callType);
        if (callType.equals(GlobalConstant.CALLTYPE_VIDEO)) {
            //渲染SurfaceView
            if (mLocalView == null) {
                mLocalView = (SurfaceView) findViewById(R.id.surfaceviewlocal);
                //mLocalView.setZOrderOnTop(true);
                mLocalView.setZOrderMediaOverlay(true);
            }
            if (mRemoteView == null) {
                mRemoteView = (SurfaceView) findViewById(R.id.surfaceview);
                mRemoteView.setZOrderMediaOverlay(true);
                //mRemoteView.setZOrderOnTop(true);
            }
            MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, mRemoteView);
        }
        //chw 为了解决会议挂断之后，呼叫视频单呼，本地视频不显示问题
        String[] mVideoCameraArray = getResources().getStringArray(R.array.video_camera);
        //Sws 11/17 隐藏
        //String camera = prefs.getString(GlobalConstant.SP_VIDEO_CAMERA, mVideoCameraArray[1]);

        //mLocalView.setZOrderOnTop(true);
        mLocalView.setZOrderMediaOverlay(true);

		/*//设置默认摄像头为前置还是后置  1为前置，2为后置
		int camera = prefs.getInt(GlobalConstant.ACTION_GETCAMERA, 1);	
		MediaEngine.GetInstance().ME_ChangeVideoDevice(mSessId, camera);
		Toast.makeText(CallingActivity.this, "camera:"+camera, Toast.LENGTH_SHORT).show();*/
        //Sws 11/17 隐藏
        // 设置摄像头
        int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
		/*if (camCnt > 1) {
	    	if (camera.equals(mVideoCameraArray[0])){
				MtcMedia.Mtc_VideoSetInputDev("front");
	    	}
	    	else {
				MtcMedia.Mtc_VideoSetInputDev("back");
	    	}
		}*/
        //callingLocalViewFrame.addView(mLocalView, 0);
        // 开始本地视频
        MtcVideo.startLocal(mSessId, mLocalView);
        // 设置本地视频可见
        mLocalView.setVisibility(View.VISIBLE);


        //mLocalView.setZOrderOnTop(true);   //Sws 11/07 add
        mLocalView.setZOrderMediaOverlay(true);

        mCallState = GlobalConstant.CALL_STATE_OUTGOING;

        // 记录呼出通话记录
        SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
        sqLiteHelper.open();
        CallingRecords callingRecord = new CallingRecords();
        callingRecord.setUserNo(userNo);
        callingRecord.setBuddyNo(no);
        Date nowDate = new Date();
        callingRecord.setStartDate(nowDate);
        callingRecord.setAnswerDate(nowDate);
        callingRecord.setStopDate(nowDate);
        callingRecord.setDuration(0);
        callingRecord.setTime(FormatController.secToTime(0));
        callingRecord.setInOutFlg(0);
        callingRecord.setCallState(0);
        callingRecord.setSessId(dwSessId);
        sqLiteHelper.createCallingRecords(callingRecord);
        sqLiteHelper.closeclose();


        // 更新UI
        callingList.setVisibility(View.GONE);
        callingKeyboardLayout.setVisibility(View.GONE);
        btnKeyboardShow.setVisibility(View.VISIBLE);
        callingLocalViewLayout.setVisibility(View.VISIBLE);
        numEditText.setText("");
        btnHangUp.setVisibility(View.VISIBLE);
        btnMute.setVisibility(View.VISIBLE);
        btnHandsFree.setVisibility(View.VISIBLE);
        btnHoldon.setVisibility(View.GONE);
        //btnInviteJoin.setVisibility(View.VISIBLE);
        btnVoiceAnswer.setVisibility(View.GONE);
        btnVideoAnswer.setVisibility(View.GONE);
        btnDecline.setVisibility(View.GONE);

//        btnHandsFree.setEnabled(false);
//        btnMute.setEnabled(false);
//        btnHoldon.setEnabled(false);


        //  if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
        btnInfo.setVisibility(View.VISIBLE);
        videoUserName.setText(no);
        txtOutUserName.setText(no);
        txtOutUserNo.setText(no);
        videoUserNo.setText(no);
        txtAudioUserName.setText(no);

        if (CommonMethod.prefixLocalMap.containsKey(no)){
            videoUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
            txtOutUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
            txtAudioUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
        }else {
            if (CommonMethod.prefixMap.containsKey(no)) {
                videoUserName.setText(CommonMethod.prefixMap.get(no).userName);
                txtOutUserName.setText(CommonMethod.prefixMap.get(no).userName);
                txtAudioUserName.setText(CommonMethod.prefixMap.get(no).userName);
            }
        }

        if (callType.equals(GlobalConstant.CALLTYPE_VIDEO)) {
            callingLocalViewTitle.setVisibility(View.VISIBLE);
            callingLocalViewVideo.setVisibility(View.VISIBLE);

            callingLocalViewAudio.setVisibility(View.GONE);
            //txtCallingMessage.setVisibility(View.VISIBLE);

        } else {
            //txtAudioUserName.setText(no);
            txtAudioUserNo.setText(no);
            callingLocalViewAudio.setVisibility(View.VISIBLE);

            callingLocalViewTitle.setVisibility(View.GONE);
            callingLocalViewVideo.setVisibility(View.GONE);
        }

        //默认关闭扬声器 打开麦克风
        CloseSpeaker();
        audioManager.setMicrophoneMute(false);
    }

    // 开始通话
    private void callingTalkingDo(int dwSessId) {

        Log.e("callingTalkingDo", "呼叫建立成功");

        mCallState = GlobalConstant.CALL_STATE_TALKING;
        // 更新收听时间
        SQLiteHelper sqLiteHelper = new SQLiteHelper(CallingActivity.this);
        sqLiteHelper.open();
        //List<CallingRecords> cRecords = sqLiteHelper.getCallingRecordsByUserNO(userNo, 0, 1);
        List<CallingRecords> cRecords = sqLiteHelper.getCallingRecordsByUserNOAndSessId(userNo, dwSessId);
		/*List<CallingRecords> cRecords = sqLiteHelper.getCallingAndMeetingRecordsByUserNO(userNo, 0, 1);*/
        if (cRecords != null && cRecords.size() > 0) {
            CallingRecords callingRecord = cRecords.get(0);
            Date nowDate = new Date();
            callingRecord.setAnswerDate(nowDate);
            callingRecord.setCallState(1);//1接通
            sqLiteHelper.updateCallingRecords(callingRecord);
        }
        sqLiteHelper.closeclose();
        // 通知停止铃声
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_STORPING);
        sendBroadcast(callIntent);

        //更改静音，免提状态
        btnHandsFree.setEnabled(true);
        btnMute.setEnabled(true);
        btnHoldon.setEnabled(true);
        btnSwitchCamera.setEnabled(true);
        Log.e("切换摄像头Btn","callingTalkingDo setEnabled(true)");
        //检查是否开启免提，如果开启则关闭
		/*String pcDevName = MtcMedia.Mtc_AudioGetOutputDev();
		if (pcDevName.contains("speaker")) {
			MtcMedia.Mtc_AudioSetOutputDev("headset");
		}*/


        //判断当前是否开启免提
//        if (btnHandsFree.getText().toString().trim().equals("免提")) {
//            if (audioManager.isSpeakerphoneOn()) {
//                CloseSpeaker();
//            }
//        } else if (btnHandsFree.getText().toString().trim().equals("取消免提")) {
//            if (!audioManager.isSpeakerphoneOn()) {
//                OpenSpeaker();
//            }
//        }
//
//        //判断当前是否处于静音状态
//        boolean mute = audioManager.isMicrophoneMute();
//        if (mute) {
//            if (btnMute.getText().toString().trim().equals("静音")) {
//                audioManager.setMicrophoneMute(false);
//                Log.e("集体测试","设置麦克风 calling 2069  取消静音");
//            } else if (btnMute.getText().toString().trim().equals("取消静音")) {
//                audioManager.setMicrophoneMute(true);
//                Log.e("集体测试","设置麦克风 calling 2071  静音");
//            }
//        }


        //String pcDevName = MtcMedia.Mtc_AudioGetOutputDev();
        //btnHandsFree.setText(pcDevName.contains("speaker") ? "取消免提": "免提");


        // btnHandsFree.setText("免提");
        //btnMute.setText("静音");

        //视频通话时长
        session_time_state.setBase(SystemClock.elapsedRealtime());
        session_time_state.start();
        //语音通话时长，视频通话可以语音接听
        session_time_state_audio.setBase(SystemClock.elapsedRealtime());
        session_time_state_audio.start();
        // 开始计时

        String callType = prefs.getString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
        //  if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
        Log.e("切换摄像头Btn","Talking callType ：" + callType);
        videoUserTime.setBase(SystemClock.elapsedRealtime());
        videoUserTime.start();
        if (callType.equals(GlobalConstant.CALLTYPE_VIDEO)) {
            btnHangUp.setVisibility(View.VISIBLE);
            btnMute.setVisibility(View.VISIBLE);
            btnHandsFree.setVisibility(View.VISIBLE);
            btnHoldon.setVisibility(View.GONE);
            mRemoteView.setVisibility(View.VISIBLE);
            mLocalView.setVisibility(View.VISIBLE);
            //btnInviteJoin.setVisibility(View.GONE);
            btnVoiceAnswer.setVisibility(View.GONE);
            btnVideoAnswer.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
            btnSwitchCamera.setVisibility(View.VISIBLE);
            Log.e("切换摄像头Btn","Talking setVisibility(View.VISIBLE)");
            //txtCallingMessage.setVisibility(View.GONE);
        } else {
            mRemoteView.setVisibility(View.INVISIBLE);
            mLocalView.setVisibility(View.INVISIBLE);
            btnHangUp.setVisibility(View.VISIBLE);
            btnMute.setVisibility(View.VISIBLE);
            btnHandsFree.setVisibility(View.VISIBLE);
            btnHoldon.setVisibility(View.GONE);
            //btnInviteJoin.setVisibility(View.VISIBLE);
            btnVoiceAnswer.setVisibility(View.GONE);
            btnVideoAnswer.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
            btnSwitchCamera.setVisibility(View.GONE);
            Log.e("切换摄像头Btn","Talking setVisibility(View.GONE)");
        }


        //取得配置是否开启
        boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

        if (mediastatistics){

            startTimer(dwSessId);

//            Intent setTrue = new Intent(GlobalConstant.MEETING_SETTRUE);
//            sendBroadcast(setTrue);
//
//            Intent startTimerIntent = new Intent(GlobalConstant.MEETING_STARTTIMER);
//            startTimerIntent.putExtra("dwSessid",String.valueOf(dwSessId));
//            sendBroadcast(startTimerIntent);
        }
    }

    // 挂断操作
    private void callingTermedDo(int dwSessId) {
        Log.e("callingTermedDo", "呼叫挂断  2218");
        mCallState = GlobalConstant.CALL_STATE_NONE;





        // 通知停止铃声
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_STORPING);
        sendBroadcast(callIntent);

        Toast.makeText(CallingActivity.this, "呼叫被终止 ", Toast.LENGTH_SHORT).show();

        btnMute.setText("静音");

        session_time_state.stop();
        session_time_state.setText("");

        session_time_state_audio.stop();
        session_time_state_audio.setText("");
        // 停止计时
        //if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
        videoUserTime.stop();
        videoUserTime.setText("");
        videoUserName.setText("");
        videoUserNo.setText("");
        callingRemoteViewFrameInfo.setVisibility(View.GONE);
        btnInfo.setVisibility(View.GONE);
        // }
        //隐藏视频
        callingLocalViewTitle.setVisibility(View.GONE);
        callingLocalViewVideo.setVisibility(View.GONE);
        //隐藏语音
        callingLocalViewAudio.setVisibility(View.GONE);
        //隐藏整个布局
        callingLocalViewLayout.setVisibility(View.GONE);
        btnSwitchCamera.setVisibility(View.GONE);
        Log.e("切换摄像头Btn","挂断 setVisibility(View.GONE)");
        mRemoteView.setVisibility(View.GONE);
        mLocalView.setVisibility(View.GONE);

        // 管理锁屏
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            showWhenLocked(false);
        }

            btnHandsFree.setText("免提");
            CloseSpeaker();
            btnMute.setText("静音");
            audioManager.setMicrophoneMute(false);
            Log.e("集体测试", "设置麦克风 calling 2210  取消静音");


        callingList.setVisibility(View.VISIBLE);
		/*callingRecordsList.clear();*/
        initCallingData();
		/*simpleAdapter.notifyDataSetChanged();*/
        //listAdapter.notifyDataSetChanged();
        callingKeyboardLayout.setVisibility(View.GONE);
        btnKeyboardShow.setVisibility(View.VISIBLE);
		/*callingRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(CommonMethod.dip2px(getBaseContext(), 240), FrameLayout.LayoutParams.MATCH_PARENT));*/
        //callingRemoteViewLayout.setVisibility(View.GONE);//造成第二次视频不显示
    }

    // 来电操作
    private void callingIncomingDo(String no, int dwSessId, boolean changeview, boolean isVideo) {
        Log.e("callingIncomingDo", "来电");

        Editor edit = prefs.edit();
        if (isVideo) {
            edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VIDEO);
            Log.e("*******Sws*****", "video");
        } else {
            edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
            Log.e("*******Sws*****", "voice  2272");
        }
        edit.apply();


        if (isVideo) {
            //渲染SurfaceView
            //渲染SurfaceView
            if (mLocalView == null) {
                mLocalView = (SurfaceView) findViewById(R.id.surfaceviewlocal);
                //mLocalView.setZOrderOnTop(true);
                mLocalView.setZOrderMediaOverlay(true);
                mLocalView.setVisibility(View.VISIBLE);
            }
            if (mRemoteView == null) {
                mRemoteView = (SurfaceView) findViewById(R.id.surfaceview);
                mRemoteView.setZOrderMediaOverlay(true);
                //mRemoteView.setZOrderOnTop(true);
                mRemoteView.setVisibility(View.VISIBLE);
            }

            if (mLocalView.getVisibility() != View.VISIBLE) {
                //mLocalView.setZOrderOnTop(true);
                mLocalView.setZOrderMediaOverlay(true);
                mLocalView.setVisibility(View.VISIBLE);
            }
            if (mRemoteView.getVisibility() != View.VISIBLE) {
                mRemoteView.setZOrderMediaOverlay(true);
                //mRemoteView.setZOrderOnTop(true);
                mRemoteView.setVisibility(View.VISIBLE);
            }

            MediaEngine.GetInstance().ME_SetSurfaceView(mLocalView, mRemoteView);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // TODO Auto-generated method stub
        mCallState = GlobalConstant.CALL_STATE_INCOMING;

        //通讯录页面发广播，如果正在操作弹出菜单则隐藏（popupwindow）
        Intent closeIntent = new Intent(GlobalConstant.ACTION_CONTACT_HIDDEN);
        sendBroadcast(closeIntent);

        if (localPopupWindow != null) {
            localPopupWindow.dismiss();
        }
        if (systemPopupWindow != null) {
            systemPopupWindow.dismiss();
        }
        if (conferencePopupWindow != null) {
            conferencePopupWindow.dismiss();
        }

        if (!changeview) {
            // 播放铃声
            Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_STARTRING);
            sendBroadcast(callIntent);
        }

        //来电，还未接通，静音免提按钮不可用
//        btnHandsFree.setEnabled(false);
//        btnMute.setEnabled(false);
//        btnHoldon.setEnabled(false);

        callingList.setVisibility(View.GONE);
        callingKeyboardLayout.setVisibility(View.GONE);
        callingLocalViewLayout.setVisibility(View.VISIBLE);
        btnSwitchCamera.setVisibility(View.GONE);
        Log.e("切换摄像头Btn","来电 setVisibility(View.GONE)");
        //判断来电是否是视频
        //if(MtcCall.Mtc_SessHasVideo(dwSessId)){
        //if(isvideo){

        AutoAnswerBena autoAnswerBena = CommonMethod.getInstance().getAutoAnswerBena();
        btnInfo.setVisibility(View.VISIBLE);
        txtOutUserNo.setText(no);
        videoUserName.setText(no);
        txtOutUserName.setText(no);
        videoUserNo.setText(no);
        txtAudioUserName.setText(no);

        if (CommonMethod.prefixLocalMap.containsKey(no)){
            videoUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
            txtOutUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
            txtAudioUserName.setText(CommonMethod.prefixLocalMap.get(no).getBuddyName());
        }else {
            if (CommonMethod.prefixMap.containsKey(no)) {
                videoUserName.setText(CommonMethod.prefixMap.get(no).userName);
                txtOutUserName.setText(CommonMethod.prefixMap.get(no).userName);
                txtAudioUserName.setText(CommonMethod.prefixMap.get(no).userName);
            }
        }

        if (isVideo) {

            callingLocalViewTitle.setVisibility(View.VISIBLE);
            callingLocalViewVideo.setVisibility(View.VISIBLE);
            btnHangUp.setVisibility(View.VISIBLE);
            btnMute.setVisibility(View.GONE);
            btnHandsFree.setVisibility(View.GONE);
            btnHoldon.setVisibility(View.GONE);
            //btnInviteJoin.setVisibility(View.GONE);
            btnVoiceAnswer.setVisibility(View.VISIBLE);
            btnVideoAnswer.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.GONE);


            if (dwSessId == autoAnswerBena.getCallId()) {
                //当前通话是否自动接听
                if (autoAnswerBena.getIsAutoAnswer() > 0) {
                    // 视频回复
                    videoResponse();
                }
            }
        } else {
            //txtAudioUserName.setText(no);
            txtAudioUserNo.setText(no);
            callingLocalViewAudio.setVisibility(View.VISIBLE);
            btnHangUp.setVisibility(View.VISIBLE);
            btnMute.setVisibility(View.GONE);
            btnHandsFree.setVisibility(View.GONE);
            btnHoldon.setVisibility(View.GONE);
            //btnInviteJoin.setVisibility(View.GONE);
            btnVoiceAnswer.setVisibility(View.VISIBLE);
            btnVideoAnswer.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);

            if (dwSessId == autoAnswerBena.getCallId()) {
                //当前通话是否自动接听
                if (autoAnswerBena.getIsAutoAnswer() > 0) {
                    // 语音回复
                    voiceResponse();
                }
            }

        }
        // 屏幕始终不锁定
        showWhenLocked(true);

        //默认关闭扬声器 打开麦克风
        CloseSpeaker();
        audioManager.setMicrophoneMute(false);

        //取得配置是否自动接听
        boolean aBoolean = prefs.getBoolean(GlobalConstant.ACTION_CALLING_ISANSWER, false);
        if (aBoolean){
            if (isVideo){
                // 视频回复
                videoResponse();
            }else {
                // 语音回复
                voiceResponse();
            }
        }
    }

    // 开始本地视频预览
    @SuppressWarnings("unused")
    private void callingStartPreviewDo(int mSessId) {

        Log.e("开启本地视频", "进入开启");
        // 本地视频没有打开则打开
        if (mLocalView.getVisibility() != View.VISIBLE) {
            // 取得视频展示尺寸
            int screenWidth = callingLocalViewFrame.getWidth();
            int screenHeight = callingLocalViewFrame.getHeight();

            // 设置本地视频可见
            //mLocalView.setZOrderOnTop(true);  //Sws add 11/07
            mLocalView.setZOrderMediaOverlay(true);
            mLocalView.setVisibility(View.VISIBLE);
        }
    }

    // 开始视频
    private void callingStartVideoDo(int mSessId) {

    }

    // 视频显示尺寸
    private void callingCaptureSizeDo(int dwWidth, int dwHeight) {

    }

    // 视频尺寸
    private void callingVideoSizeDo(int dwSessId, int dwWidth, int dwHeight) {
        int screenWidth = callingRemoteViewFrame.getWidth();
        int screenHeight = callingRemoteViewFrame.getHeight();
        MtcDelegate.log("callingVideoSizeDo接受的参数screenWidth:" + screenWidth + "  screenHeight:" + screenHeight);

        // 设置远程视频尺寸 yuezs 隐藏
        //MtcVideo.videoSize(dwSessId, dwWidth, dwHeight, 0, screenWidth, screenHeight, mRemoteView);
        mRemoteWidth = dwWidth;
        mRemoteHeight = dwHeight;
    }

    // 停止视频
    private void callingStopVideDo(int mSessId) {
        //MtcCall.Mtc_SessPreviewShow(-1, false);
        // 设置本地摄像头隐藏
        mLocalView.setVisibility(View.INVISIBLE);
        mLocalView.setOnTouchListener(null);
        // 设置远程摄像头隐藏
        mRemoteView.setVisibility(View.INVISIBLE);
        Log.e("callingStopVideDo", "| 停止视频 2475");
    }

    // 屏幕始终不锁定
    private void showWhenLocked(boolean show) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (show) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        }
        getWindow().setAttributes(attrs);
    }

    //保持成功
    private void callingHoldOk() {
        btnHoldon.setText("解除保持");
    }

    //保持失败
    private void callingHoldFailed() {
        Toast.makeText(CallingActivity.this, "保持通话失败", Toast.LENGTH_SHORT).show();
    }

    //解除保持成功
    private void callingUnHoldOk() {
        btnHoldon.setText("保持");
    }

    //解除保持失败
    private void callingUnHoldFailed() {
        Toast.makeText(CallingActivity.this, "解除保持失败", Toast.LENGTH_SHORT).show();
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
                        new SettingExitFragment(CallingActivity.this).exit(); //之前的退出操作
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

    //批量删除通话记录
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_delete:
                //layout=(LinearLayout)findViewById(R.id.delete_layout);
                if (layout.getVisibility() == View.GONE) {
                    topPlan.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
                Toast.makeText(CallingActivity.this, "选择了批量删除",
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_one:
                sb.append("1");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_two:
                sb.append("2");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());

                break;
            case R.id.btn_three:
                sb.append("3");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_four:
                sb.append("4");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_five:
                sb.append("5");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_six:
                sb.append("6");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_seven:
                sb.append("7");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_eight:
                sb.append("8");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_nine:
                sb.append("9");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_xing:
                sb.append("*");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_ling:
                sb.append("0");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btn_jing:
                sb.append("#");
                numEditText.setText(sb.toString().trim());
                numEditText.setSelection(numEditText.getText().length());
                break;
            case R.id.btnBackup:
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                if (numEditText.getText().toString().length() > 0) {
                    numEditText.setText(sb.toString());
                    numEditText.setSelection(numEditText.getText().length());
                }

                break;
        }
    }

    class ResponseBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            videoResponse();
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    class SurfaceViewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //设置默认摄像头为前置还是后置  1为前置，2为后置
            String devId = prefs.getString(GlobalConstant.ACTION_GETCAMERA, "1");
            MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));
            //渲染SurfaceView
            if (mLocalView == null) {
                mLocalView = (SurfaceView) findViewById(R.id.surfaceviewlocal);
            }
            //mLocalView.setZOrderOnTop(true);
            mLocalView.setZOrderMediaOverlay(true);
            mLocalView.setVisibility(View.VISIBLE);
            if (mRemoteView == null) {
                mRemoteView = (SurfaceView) findViewById(R.id.surfaceview);
                mRemoteView.setZOrderMediaOverlay(true);
                //mRemoteView.setZOrderOnTop(true);
            }
            mRemoteView.setZOrderMediaOverlay(true);
           // mRemoteView.setZOrderOnTop(true);
            mRemoteView.setVisibility(View.VISIBLE);
            Log.e("Sws测试mRemoteView", "|  显示 2711");

            Log.e("===Sws测试摄像头", "当前不在call界面 接收到广播进行视频  Integer.valueOf(devId): " + Integer.valueOf(devId));
        }
    }

    //停止统计丢包信息定时器
    private void stopTimer() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        handler.sendEmptyMessage(2);

    }

    //开启统计丢包信息定时器
    private void startTimer(final int dwSessid) {


        //取得配置是否开启
        boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

        if (!mediastatistics) return; //如果配置未开启 不执行下列代码


        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isStartTimer) {
                        //丢包率信息统计
                        new AsyncMediaStatistics().execute(String.valueOf(dwSessid));

                    } else {
                        stopTimer();
                    }
                }
            };
        }

        if (mTimer != null && timerTask != null) {
            try {
                mTimer.schedule(timerTask, 1000, 1000);

            } catch (Exception e) {
                Log.e("================PaoError", "" + e.getMessage());
            }
        }
    }

    //开始请求统计信息
    class AsyncMediaStatistics extends AsyncTask<String, Object, Object> {

        @Override
        protected String doInBackground(String... strings) {

            //取得配置是否开启
            boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

            if (!mediastatistics) return null; //如果配置未开启 不执行下列代码

            String dwSessid = strings[0];

            if (dwSessid.equals("-1")) {
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
            MediaEngine.GetInstance().ME_GetMediaStatistics(Integer.valueOf(dwSessid), aud_down, aud_up, aud_rtt, aud_bwD, aud_bwU, vid_down, vid_up, vid_rtt, vid_bwD, vid_bwU);

            voiceDownlink = aud_down;
            voice_Uplink = aud_up;
            voice_Bidirectionaldelay = aud_rtt;
            voice_downstreambandwidth = aud_bwD;
            voice_Uplinkbandwidth = aud_bwU;

            video_Downlink = vid_down;
            video_Uplink = vid_up;
            video_Bidirectionaldelay = vid_rtt;
            video_downstreambandwidth = vid_bwD;
            video_Uplinkbandwidth = vid_bwU;

            handler.sendEmptyMessage(1);

            return null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
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


    //本地画面点击事件
    private class LocalViewClick implements OnClickListener{

        @Override
        public void onClick(View view) {

            Log.e("Build=mode","" + Build.MODEL);
            if (Build.MODEL.equals("3280")){
                //飞鸟10寸Pad 不支持摄像头切换
                ToastUtils.showToast(CallingActivity.this,"当前设备不支持摄像头切换");
                return;
            }


            //设置帧率 比特率 分辨率
            String bitrateString = prefs.getString(GlobalConstant.SP_VIDEO_BITRATE, "0");
            String framerateString = prefs.getString(GlobalConstant.SP_VIDEO_FRAMERATE, "0");
            String width = prefs.getString("radioWidth", "0");
            String height = prefs.getString("radioHeight", "0");
            Log.e("点击本地SurfaceView   Call界面", "bitrateString:" + bitrateString + ",fra;merateString:" + framerateString + ",width:" + width + ",height:" + height);

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
                MediaEngine.GetInstance().ME_ChangeVideoDevice(mSessId, Integer.valueOf(cameraCode));
            } else if (cameraCode.equals("2")) {
                MediaEngine.GetInstance().ME_ChangeVideoDevice(mSessId, Integer.valueOf(cameraCode));
            }
        }
    }





    private class SurfaceViewChange implements OnClickListener{

        @Override
        public void onClick(View view) {

            //全局变量

            long time=SystemClock.uptimeMillis();//局部变量
            if (time- lastonclickTime < 2000) {
                ToastUtils.showToast(CallingActivity.this,"操作过于频繁");
                return;
            }else {
                lastonclickTime = time;
            }




            mRemoteLinearLayout.removeAllViews();
            linar_mLocal.removeAllViews();

            if (surfaceIsChange){
                //TODO 恢复布局
                surfaceIsChange = false;


//                mLocalView.setLayoutParams(new LinearLayout.LayoutParams(mRemoteView.getWidth(),mRemoteView.getHeight()));
//                mRemoteView.setLayoutParams(new LinearLayout.LayoutParams(mLocalView.getWidth(),mLocalView.getHeight()));

                mRemoteLinearLayout.addView(mRemoteView);
                linar_mLocal.addView(mLocalView);

            }else {

                //TODO 改变布局

                surfaceIsChange = true;


//                mLocalView.setLayoutParams(new LinearLayout.LayoutParams(mRemoteView.getWidth(),mRemoteView.getHeight()));
//                mRemoteView.setLayoutParams(new LinearLayout.LayoutParams(mLocalView.getWidth(),mLocalView.getHeight()));
                mRemoteLinearLayout.addView(mLocalView);
                linar_mLocal.addView(mRemoteView);
            }

        }
    }
}
