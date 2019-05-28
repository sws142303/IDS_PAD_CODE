package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.PTTInterface;
import com.azkj.pad.model.PTTUtils;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.AlertDialogUtils;
import com.azkj.pad.utility.ButtonUtils;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.GroupExpandableAdapter;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.pad.utility.TipHelper;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.ui.MtcDelegate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.service.Communicationbridge.context;
import static com.azkj.pad.utility.GlobalConstant.PTT_STATUS_ACCEPT;
import static com.azkj.pad.utility.GlobalConstant.PTT_STATUS_REQUEST;

/*对讲页面*/
public class IntercomActivity extends Activity implements PTTInterface {
    private String TAG = "SwsTestsip";
    // 全局变量定义
    private SharedPreferences prefs;
    private String[] mIntercomPriorityArray;
    private String highvalue = "";
    private String samevalue = "";
    private String lowvalue = "";
    private String defaultgroup = "";
    @SuppressWarnings("unused")
    private String ringtone = "";

    private int sessid = -1;

    // 可展开视图
    private ExpandableListView expandableListView;
    // 可展开适配器
    private GroupExpandableAdapter adapter;
    // 绑定数据
    public ArrayList<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
    // 按住对讲
    private Button btn_intercom;
    private SoundPool mSoundPool = null;
    private HashMap<Integer,Integer> soudMap = null;
    private Vibrator sVibrator;

    //标识是否已经振动或响铃
    private boolean isTipHelper = false;
    //模糊查询
    private EditText et_phone;
    private boolean isEditTextHaveString = false;// 标识是否正在输入字符
    private static int lastest = 0;
    // 显示数据
    public ArrayList<GroupInfo> showGroupInfos = new ArrayList<GroupInfo>();
    // 展示信息
    private TextView tv_queuing, tv_queuingvl, tv_speaker, tv_speakervl, tv_states, tv_statesvl, tv_group, tv_groupvl;

    // 登出消息接收
    private ForcedOfflineReceiver offflineReceiver;
    // 对讲组接收器
    private GroupInfoReceiver grpReceiver;
    // 组成员接收器
    private MemberInfoReceiver memReceiver;
    // 申请话权成功接收器
    private PttAcceptReceiver accReceiver;
    // 申请话权等待接收器
    private PttWaitingReceiver waiReceiver;
    // 申请话权拒绝接收器
    private PttRejectReceiver rejReceiver;
    // 对讲状态通知接收器
    private PttCallinfoReceiver infoReceiver;
    // 通话广播接收器
    private CallingBroadcastReceiver callingReceiver;
    // 取消是否成功
    private PttRspCancelReceiver rspCancelReceiver;
    // 通过定位创建对讲
    private PositionRecordBroadcastReceiver positionRecordReceiver;
    //通话状态
    //private boolean callDialog = true;
    private boolean tipHelper = true;

    // 当前通话状态
    @SuppressWarnings("unused")
    private int mCallState = GlobalConstant.CALL_STATE_NONE;
    // 是否在对讲中
    private boolean isintercoming = false;
    // 当前状态
    private int currState = GlobalConstant.PTT_STATUS_BLANK;
    // 当前通话编号(用以区别新来电)
    public static int currSessId = MtcCliConstants.INVALIDID;

    private AudioManager audioManager = null;
    private MediaPlayer mp = null;


    private boolean ignore = true;
    private Handler handler = new Handler();
    // 忽略列表
    private ArrayList<GroupInfo> declines = new ArrayList<GroupInfo>();
    private static final int MAXEMPTYTIMES = 3;
    // 申请话权等待空消息次数
    private int emptytimes = 0;
    private PTT_3G_PadApplication ptt_3g_PadApplication = null;
    private Timer timer;

    /**
     * 记录当前自定义Btn是否按下
     */
    private boolean clickdown = false;

    private ProgressDialog progressDialog;

    //临时对讲cid
    private String temIntercomCid = null;

    Handler dialogHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case 1:
                    showProgressDialog("请稍后","对讲组数据加载中...");
                    break;

                case 2:
                    if (progressDialog != null){
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }

                    break;

            }
        }
    };
    private SipUser sipUser;

    /*
      * 提示加载
      */
    public void showProgressDialog(String title, String message)
    {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(IntercomActivity.this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }
    private SetVolumeUtils setVolumeUtils = null;
    private PTTUtils pttUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercom);

        //显示提示框
        dialogHandler.sendEmptyMessage(1);

        //注册手咪监听
        pttUtils = new PTTUtils(this,this);
        pttUtils.enableListener(true);
        pttUtils.startTimer();

        // 全局变量定义
        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);

        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

        // 取得配置数据
        mIntercomPriorityArray = getResources().getStringArray(R.array.intercom_priority);

        defaultgroup = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, "");
        String ringtone = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, "");

        offflineReceiver = new ForcedOfflineReceiver();
        IntentFilter offflineIntentFilter = new IntentFilter();
        offflineIntentFilter.addAction(GlobalConstant.ACTION_FORCED_OFFLINE);
        registerReceiver(offflineReceiver, offflineIntentFilter);

        grpReceiver = new GroupInfoReceiver();
        IntentFilter groupIntentFilter = new IntentFilter();
        groupIntentFilter.addAction(GlobalConstant.ACTION_GROUPINFO);
        registerReceiver(grpReceiver, groupIntentFilter);

        memReceiver = new MemberInfoReceiver();
        IntentFilter memberIntentFilter = new IntentFilter();
        memberIntentFilter.addAction(GlobalConstant.ACTION_MEMBERINFO);
        registerReceiver(memReceiver, memberIntentFilter);

        accReceiver = new PttAcceptReceiver();
        IntentFilter acceptIntentFilter = new IntentFilter();
        acceptIntentFilter.addAction(GlobalConstant.ACTION_PTTACCEPT);
        acceptIntentFilter.addAction(GlobalConstant.ACTION_CALLING_INTERCOM_REQ);
        registerReceiver(accReceiver, acceptIntentFilter);

        waiReceiver = new PttWaitingReceiver();
        IntentFilter waitingintentfilter = new IntentFilter();
        waitingintentfilter.addAction(GlobalConstant.ACTION_PTTWAITING);
        registerReceiver(waiReceiver, waitingintentfilter);

        rejReceiver = new PttRejectReceiver();
        IntentFilter rejectintentfilter = new IntentFilter();
        rejectintentfilter.addAction(GlobalConstant.ACTION_PTTREJECT);
        registerReceiver(rejReceiver, rejectintentfilter);

        infoReceiver = new PttCallinfoReceiver();
        IntentFilter callinfointentfilter = new IntentFilter();
        callinfointentfilter.addAction(GlobalConstant.ACTION_PTTCALLINFO);
        registerReceiver(infoReceiver, callinfointentfilter);

        rspCancelReceiver = new PttRspCancelReceiver();
        IntentFilter rspCancelFilter = new IntentFilter();
        rspCancelFilter.addAction(GlobalConstant.ACTION_PTTCANCEL);
        registerReceiver(rspCancelReceiver, rspCancelFilter);

        callingReceiver = new CallingBroadcastReceiver();
        IntentFilter callingintentfilter = new IntentFilter();
        callingintentfilter.addAction(GlobalConstant.ACTION_CALLING_TERMED);
        callingintentfilter.addAction(GlobalConstant.ACTION_CALLING_INCOMING);
        callingintentfilter.addAction(GlobalConstant.ACTION_CALLING_TALKING);
        registerReceiver(callingReceiver, callingintentfilter);

        positionRecordReceiver = new PositionRecordBroadcastReceiver();
        IntentFilter positionRecordIntentFilter = new IntentFilter();
        positionRecordIntentFilter.addAction(GlobalConstant.ACTION_POSITION_OPERATE);
        registerReceiver(positionRecordReceiver, positionRecordIntentFilter);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //设置声音模式
        audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        setVolumeUtils = new SetVolumeUtils(this,audioManager);
        intiSoundPoolWav();

        InitView();
        InitData();

        // 设置画面
        //MtcDelegate.log("lizhiwei 页面初始设置了BLANK，设置了页面");
        currState = GlobalConstant.PTT_STATUS_BLANK;
        SetViewShow(currState);

        //判断当前账号是否存在对讲组中
        initGetPTTGroup();
    }

    private void initGetPTTGroup()
    {

       if (timer == null){
           timer = new Timer();
       }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //判断当前账号是否存在对讲组中
                MediaEngine.GetInstance().ME_GetPttGroup_Async(new MediaEngine.ME_GetPttGroup_CallBack() {
                    @Override
                    public void onCallBack(MediaEngine.ME_PttGroupInfo[] me_pttGroupInfos) {
                        if (me_pttGroupInfos.length <= 0){
                            dialogHandler.sendEmptyMessage(2);
                            Log.e("ceshi duijiang","me_pttGroupInfos.length  : " + me_pttGroupInfos.length);
                        }
                    }
                });
            }
        },2000);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
//        if (audioManager == null) {
//            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        }
//        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
//        //打开扬声器
//        audioManager.setMode(AudioManager.MODE_IN_CALL);
        //audioManager.setSpeakerphoneOn(true);
        //Log.e("集体测试","测试扬声器 intercom    241   true");
        //关闭麦克风
        //audioManager.setMicrophoneMute(false);
        //Log.e("设置麦克风","intercom 244  取消静音");
        //audioManager.setMode(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onDestroy()
    {

        if (pttUtils != null){
            pttUtils.enableListener(false);
            pttUtils.cancelTimer();
        }

        Log.w("@@@@@@INtercomAc  onDestroy@@@@@@@@", "@@@@@@INtercomAc@@@@@@@@");
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        unregisterReceiver(offflineReceiver);
        unregisterReceiver(grpReceiver);
        unregisterReceiver(memReceiver);
        unregisterReceiver(accReceiver);
        unregisterReceiver(waiReceiver);
        unregisterReceiver(rejReceiver);
        unregisterReceiver(infoReceiver);
        unregisterReceiver(callingReceiver);
        unregisterReceiver(positionRecordReceiver);
        unregisterReceiver(rspCancelReceiver);

        releaseSoundPoolWav();
        super.onDestroy();
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

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(IntercomActivity.this,
                            getString(R.string.info_network_unavailable));
                    return false;
                }

                if (GroupManager.getInstance().getOptimumgroup() != null) {
                    MtcDelegate.log("lizhiwei 点按申请话权设置了REQUEST，设置了页面");
                    // 设置状态
                    currState = PTT_STATUS_REQUEST;
                    // 申请话权
                    Request();
                    // 设置画面
                    SetViewShow(currState);
                    Log.e("ceshi duijiang","=====    389");
                }
                break;
        }
        return false;
    }

    // 初始化视图
    private void InitView()
    {
        // 取得控件
        // 获取EditText的change
        et_phone = (EditText) findViewById(R.id.et_add_contact);
        expandableListView = (ExpandableListView) findViewById(R.id.treenode);
        btn_intercom = (Button) findViewById(R.id.btn_intercom);
        tv_queuing = (TextView) findViewById(R.id.tv_queuing);
        tv_queuingvl = (TextView) findViewById(R.id.tv_queuingvl);
        tv_speaker = (TextView) findViewById(R.id.tv_speaker);
        tv_speakervl = (TextView) findViewById(R.id.tv_speakervl);
        tv_states = (TextView) findViewById(R.id.tv_states);
        tv_statesvl = (TextView) findViewById(R.id.tv_statesvl);
        tv_group = (TextView) findViewById(R.id.tv_group);

        tv_groupvl = (TextView) findViewById(R.id.tv_groupvl);
        // 设置字体
        // 设置字体
        et_phone.setTypeface(CommonMethod.getTypeface(this));
        tv_queuing.setTypeface(CommonMethod.getTypeface(this));
        tv_queuingvl.setTypeface(CommonMethod.getTypeface(this));
        tv_speaker.setTypeface(CommonMethod.getTypeface(this));
        tv_speakervl.setTypeface(CommonMethod.getTypeface(this));
        tv_states.setTypeface(CommonMethod.getTypeface(this));
        tv_statesvl.setTypeface(CommonMethod.getTypeface(this));
        tv_group.setTypeface(CommonMethod.getTypeface(this));
        tv_groupvl.setTypeface(CommonMethod.getTypeface(this));



        btn_intercom.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (v.getId() == R.id.btn_intercom) {

                    //按下
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        initPttDown();

                        //抬起
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        initPttUp();
                    }
                }
                return false;
            }
        });
    }

    private void initPttDown()
    {
        Log.e("Sws测试OnTouch事件","  -*----**-*-*--*--*》");

        if (ButtonUtils.isFastDoubleClick(R.id.btn_intercom,1000)){
            return ;
        }
        clickdown = true;
//                        handler.postDelayed(new Runnable() {
//
//                            @Override public void run() {
        Log.e("^^^^^ptt键按下---------------^^^^^", "^^^^^ptt键按下---------------^^^^^");

        //关闭FEC前向纠错
        //MediaEngine.GetInstance().ME_EnableFec(false);
        // TODO Auto-generated method stub

        if (ptt_3g_PadApplication.isNetConnection() == false) {
            ToastUtils.showToast(IntercomActivity.this,
                    getString(R.string.info_network_unavailable));
            return ;
        }


        if (GroupManager.getInstance().getOptimumgroup() != null) {
            Log.e("intercomactiv申请话权", "lizhiwei 点按申请话权设置了REQUEST，设置了页面");
            //如果当前已经有单呼或会议，弹框提示是挂断会议或单呼来发起对讲，还是阻止发起对讲继续会议或单呼
            CallingInfo callingCalling = null;
            CallingInfo callingMeeting = null;

            for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {

                if ((!callingInfo.isHolding())) {
                    if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                        callingCalling = callingInfo;
                    } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                        callingMeeting = callingInfo;
                    }
                }
            }
            if(!clickdown)return ;



            AlertDialogUtils alertDialogUtils = new AlertDialogUtils(IntercomActivity.this, GlobalConstant.CALL_TYPE_INTERCOM);
            if (callingCalling != null) {
                alertDialogUtils.showIfContinue(callingCalling);
                return ;
            }
            if (callingMeeting != null) {
                alertDialogUtils.showIfContinue(callingMeeting);
                return ;
            }

            // 设置状态
            currState = PTT_STATUS_REQUEST;
            // 申请话权
            Request();

            // 设置画面
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    505");

            startPttCountDownTimer();
        }

        isTipHelper = false;
    }

    private void initPttUp(){
        clickdown = false;
        Log.e("Sws测试OnTouch事件","  -*----**-*-*--*--*》 抬起");

        if (ptt_3g_PadApplication.isNetConnection() == false) {
            ToastUtils.showToast(IntercomActivity.this,
                    getString(R.string.info_network_unavailable));
            return ;
        }

        if (GroupManager.getInstance().getOptimumgroup() != null) {
            MtcDelegate.log("lizhiwei 抬起释放话权设置了RELEASE，没有设置了页面");
            // 设置状态
            currState = GlobalConstant.PTT_STATUS_RELEASE;
            Log.e("Sws测试OnTouch事件","  -*----**-*-*--*--*》 释放话权");
            Release();// 释放话权
            // 设置画面
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    529");
            isTipHelper = true;
        }
    }


    private Timer pttCountDownTimer = null;
    private TimerTask pttCountDownTask = null;
    private boolean btPttCountDownRunning = false;
    private int count = 0;

    public void startPttCountDownTimer() {
        try {
            if (pttCountDownTimer == null)
                pttCountDownTimer = new Timer();
            if (pttCountDownTask == null) {
                pttCountDownTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.e("到计时", "倒计时执行中......" + count);
                        btPttCountDownRunning = true;
                        if (!clickdown) {
                            // 发送广播释放话权
                            Log.e("到计时", "^^^^^发送广播释放话权----广播前^^^^^");
//                            Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_RELEASE);
//                            sendBroadcast(requestIntercomIntent);
                            Release();
                            stoptPttCountDownTimer();
                        }
                        count++;
                    }
                };
            }
            if (pttCountDownTimer != null && pttCountDownTask != null && !btPttCountDownRunning) {
                pttCountDownTimer.schedule(pttCountDownTask, 1, 1200);
                Log.e("到计时", "IP监控定时器启动.....");
                // 发送广播申请话权
                Log.e("到计时", "^^^^^发送广播申请话权---广播前---------------^^^^^");
//                Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_REQ);
//                sendBroadcast(requestIntercomIntent);
                Request();
            }
        } catch (Exception e) {}
    }


    //停止定时器
    public void stoptPttCountDownTimer() {
        try {
            Log.e("到计时", "停止IP监控定时器");
            // 循环不再继续
            if (pttCountDownTimer != null) {
                pttCountDownTimer.cancel();
                pttCountDownTimer = null;
            }

            if (pttCountDownTask != null) {
                pttCountDownTask.cancel();
                pttCountDownTask = null;
            }
            btPttCountDownRunning = false;
            count = 0;
        } catch (Exception e) {}
    }


    // 初始化数据
    private void InitData()
    {
        // 初始化适配器
        adapter = new GroupExpandableAdapter(this, this.groupInfos);
        expandableListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        expandableListView.setGroupIndicator(null);//取消默认箭头
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (groupPosition != i && expandableListView.isGroupExpanded(groupPosition)) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // 切换对讲组
                MemberInfo memberinfo = (MemberInfo) adapter.getChild(groupPosition, childPosition);
                if (memberinfo.getStatus() == -1) {
                    for (GroupInfo groupinfo : groupInfos) {
                        if (groupinfo.getNumber().equals(memberinfo.getNumber())) {
                            Log.e("", "lizhiwei 手动切换对讲组" + groupinfo.getNumber());
                            GroupManager.getInstance().setOptimumgroup(groupinfo);
                            isintercoming = false;
                            Log.e("================", "" + GroupManager.getInstance().getOptimumgroup());

                            // 挂断原有进行中的对讲
                            if (currSessId != MtcCliConstants.INVALIDID) {
                                // 结束通话
                                Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, currSessId);
                                sendBroadcast(callIntent);
                                // 移除通话数据
                                CallingManager.getInstance().removeCallingInfo(currSessId);
                                currSessId = MtcCliConstants.INVALIDID;
                            }



                            break;
                        }
                    }
                    // 刷新列表
                    Log.e("intercomActiv-460", "lizhiwei 切换对讲组刷新列表");

                    RefreshExpandableList();
                }
                return false;
            }
        });

        // 设置输入
        et_phone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    isEditTextHaveString = true;
                    searchList(s.toString());
                } else {
                    isEditTextHaveString = false;
                    // 如果输入框内容为空，显示全部
                    searchList("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void pttDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //手咪按下
               // ToastUtils.showToast(IntercomActivity.this,"按下");
                initPttDown();
            }
        });
    }

    @Override
    public void pttUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        //手咪抬起
        //ToastUtils.showToast(IntercomActivity.this,"抬起");
                initPttUp();
    }
});
    }

    // 登出接收器
    public class LogoutReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            GroupManager.getInstance().setOptimumgroup(null);
            finish();
        }
    }

    // 强迫下线接收器
    public class ForcedOfflineReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
//			MtcDelegate.log("lizhiwei 强迫下线设置对讲组为空");
//			GroupManager.getInstance().setOptimumgroup(null);
            // 设置画面
            Log.e("intercomActiv-488", "lizhiwei 强迫下线设置了BLANK，没有设置了页面");
            currState = GlobalConstant.PTT_STATUS_BLANK;
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    640");
        }
    }

    // 对讲组接收器
    public class GroupInfoReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
			/*MtcDelegate.log("lizhiwei 对讲组刷新设置对讲组为空");
			// 清空对讲组
			GroupManager.getInstance().setOptimumgroup(null);*/
            // 刷新对讲组展示
            Log.e("intercomActiv-503", "lizhiwei 取得到对讲组刷新列表");
            //临时对讲结束
            RefreshExpandableList();

        }

    }

    // 对讲组成员接收器
    public class MemberInfoReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
			/*MtcDelegate.log("lizhiwei 对讲组刷新设置对讲组为空");
			// 清空对讲组
			GroupManager.getInstance().setOptimumgroup(null);*/
            // 刷新对讲组展示
            Log.e("intercomActiv-518", "lizhiwei 取得到组成员刷新列表");
            RefreshExpandableList();
        }

    }

    // 刷新对讲组数据展示
    private void RefreshExpandableList()
    {
        Log.e("城北待君归","进入 刷新对讲组数据展示");
        this.groupInfos.clear();
        // 取得对讲组
        List<GroupInfo> groupinfos = GroupManager.getInstance().getGroupData();

        if (groupinfos == null) {
            Log.e("", "=======================null============");
            return;
        }

        // 验证当前对讲组
        if (GroupManager.getInstance().getOptimumgroup() != null) {
            if (
                    ((GroupManager.getInstance().getOptimumgroup().isTemporary())
                    && (!GroupManager.getInstance().getOptimumgroup().isVisible())
                    )
                    || (!GroupManager.getInstance().isGroupNumber(GroupManager.getInstance().getOptimumgroup().getNumber()))
                    ) {
                // 临时对讲组不可见或者不是有效对讲组
                Log.e("intercomActiv-538", "lizhiwei 对讲组刷新设置对讲组为空" + GroupManager.getInstance().getOptimumgroup().isTemporary() + GroupManager.getInstance().getOptimumgroup().isVisible() + GroupManager.getInstance().getOptimumgroup().getNumber());
                GroupManager.getInstance().setOptimumgroup(null);
            } else {
                // 由于当前对讲组可能会修改名称，则需要重新设置当前对讲组信息
                GroupInfo optimumgroup = GroupManager.getInstance().getGroupInfo(GroupManager.getInstance().getOptimumgroup().getNumber());
                Log.e("intercomActiv-544", "lizhiwei 取得新对讲组信息" + optimumgroup == null ? "" : optimumgroup.getNumber() + "," + optimumgroup.getName());
                GroupManager.getInstance().setOptimumgroup(optimumgroup);
            }
        }
        // 添加对讲组
        for (int i = 0; i < groupinfos.size(); i++) {
            GroupInfo group = (GroupInfo) groupinfos.get(i);
            // 临时对讲组不可见
            if ((group.isTemporary())
                    && (!group.isVisible())) {
                Log.e("城北待君归","***************************************** : continue掉了" + group.getName());
                continue;
            }
            // 初始化
            if (group.getListMembers() == null) {
                group.setListMembers(new ArrayList<MemberInfo>());
            }
            // 清空
            group.getListMembers().clear();

            if (group.getNumber().equals("")){
                Log.e("城北待君归","***************************************** : return掉了" + group.getName());
                return;
            }
            List<MemberInfo> memberinfos = new ArrayList<>();
            try {
                // 取得组成员
                List<MemberInfo> mem = GroupManager.getInstance().getGroupMembersSortedByState(group.getNumber());
                memberinfos.addAll(mem);
            }catch (Exception e){

            }
            // 添加组成员
            group.getListMembers().addAll(memberinfos);
            // 设置默认对讲组
            if (GroupManager.getInstance().getOptimumgroup() == null) {
                // TODO Auto-generated method stub
                if ((defaultgroup != null)
                        && (defaultgroup.length() > 0)) {
                    if (group.getNumber().equals(defaultgroup)) {
                        Log.e("intercomActiv-575", "lizhiwei 对讲组刷新设置对讲组" + group.getNumber());
                        GroupManager.getInstance().setOptimumgroup(group);
                    }
                } else {
                    if (i == 0) {
                        Log.e("intercomActiv-581", "lizhiwei 对讲组刷新设置对讲组" + group.getNumber());
                        GroupManager.getInstance().setOptimumgroup(group);
                    }
                }
            }
            // 添加切换对讲组
            if ((GroupManager.getInstance().getOptimumgroup() == null)
                    || (!group.getNumber().equals(GroupManager.getInstance().getOptimumgroup().getNumber()))) {
                MemberInfo member = new MemberInfo(getResources().getString(R.string.title_intercom_changegroup), group.getNumber(), -1);
                group.getListMembers().add(0, member);
            }
            groupInfos.add(group);
            Log.e("城北待君归","groupInfos添加组aaaaaaaaaa : " + group.getName());
        }

        // 重新展示
        //expandableListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // 设置默认对讲组
        if (GroupManager.getInstance().getOptimumgroup() != null) {
            //加载完成  隐藏提示框
            dialogHandler.sendEmptyMessage(2);

            btn_intercom.setEnabled(true);
            // 设置默认对讲组
            tv_groupvl.setText(GroupManager.getInstance().getOptimumgroup().getName());
            // 设置画面
            Log.e("intercomActiv-602", "lizhiwei 刷新列表有对讲组设置了" + currState + "，设置了页面");
            if (currState == GlobalConstant.PTT_STATUS_BLANK && ptt_3g_PadApplication.isNetConnection()) {
                currState = GlobalConstant.PTT_STATUS_EMPTY;
                Log.e("SetViewShow","=====    704");
            }
            // 设置画面
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    780");
        } else {
            // 设置画面
            Log.e("intercomActiv-611", "lizhiwei 刷新列表无对讲组设置了BLANK，设置了页面");
            currState = GlobalConstant.PTT_STATUS_BLANK;
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    785");
        }

        dialogHandler.sendEmptyMessage(2);

    }

    // 申请话权成功接收器
    public class PttAcceptReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
			/*if(action.equals(GlobalConstant.ACTION_CALLING_INTERCOM_REQ)){
				// 设置状态
				currState = GlobalConstant.PTT_STATUS_REQUEST;
				// 申请话权
				Request();
				// 设置画面
				SetViewShow(currState);
			}*/
            if (action.equals(GlobalConstant.ACTION_PTTACCEPT)) {
                // 设置状态
                Log.w("*******InterAc631 申请话权成功********", "*******InterAc631 申请话权成功********");
                isintercoming = true;
                Log.w("intercomActiv-638", "lizhiwei 申请话权成功设置了ACCEPT，设置了页面");
                emptytimes = 0;
                currState = PTT_STATUS_ACCEPT;
                Log.w("intercomActiv-641", "lizhiwei 申请话权成功处理界面展示。");

                openMute();

                SetViewShow(currState);
                Log.e("ceshi duijiang","=====    818");
               // TipHelper.Vibrate(IntercomActivity.this,200);
            }
        }
    }

    // 申请话权等待接收器
    public class PttWaitingReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String speaker = intent.getStringExtra(GlobalConstant.KEY_PTT_SPEAKER);
            String queue = intent.getStringExtra(GlobalConstant.KEY_PTT_QUEUE);
            String relesequeueString = intent.getStringExtra(GlobalConstant.KEY_PTT_RELESEQUEUE);
            // 如果当前正在对讲中，则不显示等待
            if (currState == PTT_STATUS_ACCEPT) {
                return;
            }
            // 设置界面
            MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(GroupManager.getInstance().getOptimumgroup().getNumber(), speaker);
            btn_intercom.setBackgroundResource(R.drawable.intercom_listening);
            tv_statesvl.setText("收听");
            Log.e("deserve", "----------------->");
            if (memberinfo != null) {
                if ((memberinfo.getName() != null)
                        && (memberinfo.getName().trim().length() > 0)) {
                    tv_speakervl.setText(memberinfo.getName());
                } else {
                    tv_speakervl.setText(memberinfo.getNumber());
                }
            } else {
                tv_speakervl.setText(speaker);
            }
            if ((queue != null)
                    && (queue.trim().length() > 0)
                    && (!queue.trim().equals("/"))
                    && (queue != null || !"".equals(queue))
                    && (!queue.equals("-1"))) {
                tv_queuing.setVisibility(View.VISIBLE);
                tv_queuingvl.setVisibility(View.VISIBLE);
                tv_queuingvl.setText(queue);

                // 设置画面
                Log.w("intercomActiv-684", "lizhiwei 申请话权等待设置了WAITING，设置了页面");
                currState = GlobalConstant.PTT_STATUS_WAITING;
                SetViewShow(currState);
            } else {
                tv_queuing.setVisibility(View.INVISIBLE);
                tv_queuingvl.setVisibility(View.INVISIBLE);
                tv_queuingvl.setText("");

                // 设置画面
                Log.e("测试对讲状aaaaaaaaaaaaaaaaaaaaaaa态", "取消排队，设置了页面");
                currState = GlobalConstant.PTT_STATUS_RELEASE;
                //SetViewShow(currState);
            }

        }

    }

    // 申请话权拒绝接收器
    public class PttRejectReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String error = intent.getStringExtra(GlobalConstant.KEY_PTT_ERROR);
            Log.e("测试队列已满","error:" + error);
            // 如果当前正在对讲中，则不显示等待
            if (currState == PTT_STATUS_ACCEPT) {
                return;
            }
            if ((error != null)
                    && (error.trim().length() > 0)) {
                if (error.trim().equals("1")){

                    tv_queuing.setVisibility(View.VISIBLE);
                    tv_queuingvl.setVisibility(View.VISIBLE);
                    tv_queuingvl.setText("不在对讲组中");

                }else if (error.trim().equals("2")) {
                    tv_queuing.setVisibility(View.VISIBLE);
                    tv_queuingvl.setVisibility(View.VISIBLE);
                    tv_queuingvl.setText("队列已满");
                } else if (error.trim().equals("3")) {
                    //ToastUtils.showToast(IntercomActivity.this, "被踢除");
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("被踢除");
                } else if (error.trim().equals("4")) {
                    //ToastUtils.showToast(IntercomActivity.this, "处理超时");
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("处理超时");
                } else if (error.trim().equals("5")){
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("失败");
                }else if (error.trim().equals("6")){
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("不是对讲组");
                }else if (error.trim().equals("7")){
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("呼叫建立失败");
                }else if (error.trim().equals("8")){
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("找不到session");
                }
            } else {
                tv_queuing.setVisibility(View.INVISIBLE);
                tv_queuingvl.setVisibility(View.INVISIBLE);
                tv_queuingvl.setText("");
            }
            // 设置状态
            Log.w("intercomActiv-744", "lizhiwei 申请话权拒绝设置了REJECT，设置了页面");
            currState = GlobalConstant.PTT_STATUS_REJECT;
            // 设置画面
            SetViewShow(currState);
            Log.e("ceshi duijiang","=====    922");
        }
    }

    public class PttRspCancelReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String codeString = intent.getExtras().getString(GlobalConstant.KEY_PTT_CANCEL_RSPCODE);
            int code = Integer.parseInt(codeString);
            if (code == 0) {
                //成功
                // 显示
				/*tv_queuing.setVisibility(View.INVISIBLE);
				tv_queuingvl.setVisibility(View.INVISIBLE);
				tv_queuingvl.setText("");*/
                //currState = GlobalConstant.PTT_STATUS_RELEASE;
            } else {
                //失败
            }
        }
    }

    // 对讲状态通知接收器
    public class PttCallinfoReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
            String group = intent.getStringExtra(GlobalConstant.KEY_PTT_GROUP);
            String speaker = intent.getStringExtra(GlobalConstant.KEY_PTT_SPEAKER);
            if (speaker.equals(sipUser.getUsername()) || speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())){
                currState = PTT_STATUS_ACCEPT;
            }
            Log.e("城北待君归","对讲状态通知接收器 group :" + group + "\r\n" + "speaker : " + speaker);

            // 来电是否为有效对讲组
            boolean effective = GroupManager.getInstance().isGroupNumber(group);
            Log.e("城北待君归","effective:" + effective);
            if (!effective) {
                Log.e("城北待君归","return掉了");
                return;
            }
            // 判断是否当前对讲组   2015-05-07 qq
            if ((GroupManager.getInstance().getOptimumgroup() != null)
                    && !(GroupManager.getInstance().getOptimumgroup().getNumber().equals(group)
                    || GroupManager.getInstance().getOptimumgroup().getNumber().equals("*9*"+group) )  ) {

				return;
            }
            Log.e("城北待君归","currState : " + currState);
            // 判断是否申请话权成功
            if (currState == PTT_STATUS_ACCEPT) {

                if ((speaker == null)
                        || (speaker.trim().length() <= 0)
                        ) {
                    currState = GlobalConstant.PTT_STATUS_EMPTY;
                    Log.e("SetViewShow","=====    922");
                    // 设置画面
                    SetViewShow(currState);

                }else if ((speaker != null || speaker.trim().length() > 0)
                        && (speaker.trim().equals(sipUser.getUsername()) || speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername()))) {

                    openMute(); //打开麦克风
                    if (audioManager.isSpeakerphoneOn()) {
                        Log.e("OpenOrClose","Close 1026");
                        CloseSpeaker(); //关闭扬声器
                    }
                    // 图标
                    btn_intercom.setBackgroundResource(R.drawable.intercom_speaking);
                    // 显示
                    tv_statesvl.setText("发言");
                    MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(speaker);
                    if ((memberinfo != null)
                            && (memberinfo.getName() != null)
                            && (memberinfo.getName().trim().length() > 0)) {
                        tv_speakervl.setText(memberinfo.getName() + "(自己)");
                    } else {
                        tv_speakervl.setText(sipUser.getUsername() + "(自己)");
                    }
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("");

                } else if ((speaker != null || speaker.trim().length() > 0)
                        && (!speaker.trim().equals(sipUser.getUsername()) && !speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername()))) {

                    if (!audioManager.isSpeakerphoneOn()){
                        OpenSpeaker(audioManager);  //打开扬声器
                    }
                    Log.e("CLOSEmUTE","1125");
                    CloseMute(); //关闭麦克风

                    Log.w("测试对讲状态", "lizhiwei PttCallinfoReceiver:" + group + ",speaker:" + speaker + "," + sipUser.getUsername() + "not equle");
                    // 设置状态
                    Log.e("测试对讲状aaaaaaaaaaaaaaaaaaaaaaa态", "1060，设置了页面");
                    currState = GlobalConstant.PTT_STATUS_RELEASE;
                    // 设置画面
                    SetViewShow(currState);
                    tv_statesvl.setText("收听");
                    btn_intercom.setBackgroundResource(R.drawable.intercom_listening);

                    MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(speaker);
                    if (memberinfo == null) {
                        tv_speakervl.setText(speaker);
                    } else {
                        if ((memberinfo.getName() != null)
                                && (memberinfo.getName().length() > 0)) {
                            tv_speakervl.setText(memberinfo.getName());
                        } else {
                            tv_speakervl.setText(speaker);
                        }
                    }
                }
            } else if (currState == GlobalConstant.PTT_STATUS_WAITING) {
                // 申请话权等待时如果来的是通是人是自己那么进入申请成功状态
                if ((speaker != null)
                        && (speaker.trim().length() > 0)
                        && (speaker.trim().equals(sipUser.getUsername())|| speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())))
                {
                    openMute(); //打开麦克风
                    if (audioManager.isSpeakerphoneOn()) {
                        Log.e("OpenOrClose","Close 1086");
                        CloseSpeaker(); //关闭扬声器
                    }
                    // 设置状态
                    isintercoming = true;
                    Log.e("测试对讲状态", "lizhiwei 申请等待收到对讲状态发言人为自己设置了ACCEPT，设置了页面");
                    currState = PTT_STATUS_ACCEPT;
                    Log.e("测试对讲状态","=====    1058");
                    SetViewShow(currState);
                }

            } else if ((currState == GlobalConstant.PTT_STATUS_REJECT)
                    || (currState == GlobalConstant.PTT_STATUS_RELEASE)
                    || (currState == GlobalConstant.PTT_STATUS_EMPTY)
                    || (currState == GlobalConstant.PTT_STATUS_BLANK)) {
                // 申请话权被拒绝
                // 释放话权中
                // 空闲
                // 空白
                if (currState == GlobalConstant.PTT_STATUS_REJECT){
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (!audioManager.isSpeakerphoneOn()) {
                        OpenSpeaker(audioManager);  //打开扬声器
                    }
                    Log.e("CLOSEmUTE","1185");
                        CloseMute(); //关闭麦克风
                }

                if ((speaker == null)
                        || (speaker.trim().length() <= 0)) {
                    Log.e("测试对讲状态", "申请拒绝释放空空白收到对讲状态发言人为空设置了EMPTY，设置了页面");
                    currState = GlobalConstant.PTT_STATUS_EMPTY;
                    Log.e("**********PTT_STATUS_EMPTY********* 880", "*************PTT_STATUS_EMPTY********880");
                    Log.e("SetViewShow","=====    1014");
                    SetViewShow(currState);
                    if (!audioManager.isSpeakerphoneOn()) {
                        OpenSpeaker(audioManager);  //打开扬声器
                    }
                    Log.e("CLOSEmUTE","1198");
                    CloseMute(); //关闭麦克风

                }
                else if ((speaker != null || speaker.trim().length() > 0)
                        && (speaker.trim().equals(sipUser.getUsername()) || speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())))
                {
                    openMute(); //打开麦克风
                    if (audioManager.isSpeakerphoneOn()) {
                        Log.e("OpenOrClose","Close 1131");
                        CloseSpeaker(); //关闭扬声器
                    }
                        // 图标
                    btn_intercom.setBackgroundResource(R.drawable.intercom_speaking);
                    // 显示
                    tv_statesvl.setText("发言");
                    MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(speaker);
                    if ((memberinfo != null)
                            && (memberinfo.getName() != null)
                            && (memberinfo.getName().trim().length() > 0)) {
                        tv_speakervl.setText(memberinfo.getName() + "(自己)");
                    } else {
                        tv_speakervl.setText(sipUser.getUsername() + "(自己)");
                    }
                    tv_queuing.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setVisibility(View.INVISIBLE);
                    tv_queuingvl.setText("");

                } else if ((speaker != null || speaker.trim().length() > 0)
                        && (!speaker.trim().equals(sipUser.getUsername()) && !speaker.trim().equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername()))) {

                    if (!audioManager.isSpeakerphoneOn()) {
                        OpenSpeaker(audioManager);  //打开扬声器
                    }
                    Log.e("CLOSEmUTE","1232");
                    CloseMute(); //关闭麦克风

                    Log.w("测试对讲状态", "lizhiwei PttCallinfoReceiver:" + group + ",speaker:" + speaker + "," + sipUser.getUsername() + "not equle");
                    // 设置状态
                    Log.w("测试对讲状态", " 申请成功收到对讲状态发言人为其他人设置了RELEASE，设置了页面");
                    currState = GlobalConstant.PTT_STATUS_RELEASE;
                    // 设置画面
                    Log.e("测试对讲状aaaaaaaaaaaaaaaaaaaaaaa态","=====    1099");
                    SetViewShow(currState);
                    tv_statesvl.setText("收听");
                    btn_intercom.setBackgroundResource(R.drawable.intercom_listening);
                    MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(speaker);
                    if (memberinfo == null) {
                        tv_speakervl.setText(speaker);
                    } else {
                        if ((memberinfo.getName() != null)
                                && (memberinfo.getName().length() > 0)) {
                            tv_speakervl.setText(memberinfo.getName());
                        } else {
                            tv_speakervl.setText(speaker);
                        }
                    }
                }
            }
        }
    }

    // 通话广播接收器
    public class CallingBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            String callAction = intent.getAction();

            if (callAction == GlobalConstant.ACTION_CALLING_TERMED) {
                Log.e("intercomActiv-1076","ACTION_CALLING_TERMED");
                int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
                int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                // 判断是否为对讲
                if (calltype != GlobalConstant.CALL_TYPE_INTERCOM) {
                    return;
                }
                mCallState = GlobalConstant.CALL_STATE_NONE;
                Log.w("intercomActiv-1001", "lizhiwei 对讲通话结束" + mSessId);
                // 清除提示
                WaitingDialog dialog = CallingManager.getInstance().getCallingDialog(mSessId);
                if (dialog != null) {
                    Log.w("intercomActiv-1005", "lizhiwei 对讲通话结束 移除对话框");
                    CallingManager.getInstance().removeCallingDialog(dialog.getDwSessId());
                    dialog.dismiss();
                    dialog = null;
                }
                // 结束通话
                CallingManager.getInstance().removeCallingInfo(mSessId);
                CallingManager.getInstance().removeAnswerInfo(mSessId);

                Log.w("--------------IntercomAc---ACTION_CALLING_TERMED---不同就return933", "------mSessId:" + mSessId + "---currSessId:" + currSessId);
                // 如果不是当前通话对页面不做处理
                if (mSessId != currSessId) {
                    return;
                }
                // 管理锁屏
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                if (km.inKeyguardRestrictedInputMode()) {
                    showWhenLocked(false);
                }

                //对讲结束时如果当前有通话则不关闭麦克风
                CallingInfo callingCalling = null;
                CallingInfo callingMeeting = null;

                for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
                    if (callingInfo.getDwSessId() != mSessId) {   //mSessId 2015-05-07 qq
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                            callingCalling = callingInfo;
                        } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                            callingMeeting = callingInfo;
                        }
                    }
                }
                if (callingCalling == null && callingMeeting == null) {
                    Log.w("----------------IntercomAc959----------------------", "----------对讲结束关闭麦克风--------------");
                    // 关闭麦克风
//				Intent callmuteIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
//				callmuteIntent.putExtra("NUTE", false);
//				sendBroadcast(callmuteIntent);
                }
                Log.e("intercomActiv-1045", "lizhiwei 关闭扬声器CallingBroadcastReceiver");

                // 关闭扬声器
//				AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
//				if(audioManager.isSpeakerphoneOn()){
//					CloseSpeaker(audioManager);
//				}
                // 设置不在对讲中
                isintercoming = false;
                declines.clear();
                Log.e("============aaaaa====", "已执行");
                // 设置画面
                Log.e("intercomActiv-1056", "lizhiwei 通话结束设置了EMPTY，设置了页面");
                currState = GlobalConstant.PTT_STATUS_EMPTY;
                Log.e("SetViewShow","=====    1375");
                SetViewShow(currState);

                temIntercomCid = null;
                // 開始接聽下一路通話
				/*if (CallingManager.getInstance().getAnswerData().size() <= 0){
					return;
				}*/
                // 通知接聽下一路通話
                //Intent answernext = new Intent(GlobalConstant.ACTION_CALLING_ANSWERNEXT);
                //sendBroadcast(answernext);
            } else if (callAction == GlobalConstant.ACTION_CALLING_TALKING) {
                Log.e("intercomActiv-1076","ACTION_CALLING_TALKING");
                // 通话中
                int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
                int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                // 判断是否为对讲
                if (calltype != GlobalConstant.CALL_TYPE_INTERCOM) {
                    return;
                }
                mCallState = GlobalConstant.CALL_STATE_TALKING;
                Log.w("intercomActiv-1076", "lizhiwei 对讲通话中" + mSessId);
                currSessId = mSessId;


				/*if (isfirstrequest){
					// 设置画面
					MtcDelegate.log("lizhiwei 通话中设置了画面");
					SetViewShow(GlobalConstant.PTT_STATUS_ACCEPT);
					isfirstrequest = false;
				}*/
            } else if (callAction == GlobalConstant.ACTION_CALLING_INCOMING) {
                Log.e("城北待君归","对讲界面  收到ACTION_CALLING_INCOMING");
                // 取得参数
                final int mSessId = intent.getIntExtra(GlobalConstant.KEY_CALL_SESSIONID, MtcCliConstants.INVALIDID);
                int calltype = intent.getIntExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_CALLING);
                String groupno = intent.getStringExtra(GlobalConstant.KEY_CALL_OPPOSITENO);
                boolean autoanswer = intent.getBooleanExtra(GlobalConstant.KEY_CALL_AUTOANSWER, false);
                temIntercomCid = intent.getStringExtra(GlobalConstant.KEY_CALL_CID);
                Log.e("城北待君归","mSessId : " + mSessId + "\r\n" + "callType : " + calltype + "\r\n" + "groupno : " + groupno + "\r\n" + "autoanswer : " + autoanswer);
                // 判断是否为对讲
                if (calltype != GlobalConstant.CALL_TYPE_INTERCOM) {
                    return;
                }
                mCallState = GlobalConstant.CALL_STATE_INCOMING;
                // 需要判断来电是否为对讲，临时对讲时此时对讲组中还没有对讲
                if (!GroupManager.getInstance().isGroupNumber(groupno == null ? "" : groupno)) {
                    return;
                }
                if (mSessId == MtcCliConstants.INVALIDID) {
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



                // 取得对讲组编号
                final GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(groupno == null ? "" : groupno);
                if (groupinfo == null) {
                    return;
                }

                // 如果自动接听需要将默认对讲组切换到对应对讲组上
                if (autoanswer && groupinfo.getNumber().contains(sipUser.getUsername())) {
                 //   if (!GroupManager.getInstance().getOptimumgroup().getNumber().trim().equals(groupinfo.getNumber().trim())) {
                        // 不是默认对讲组
                        GroupManager.getInstance().setOptimumgroup(groupinfo);
                        // 设置状态
                        if (groupinfo.isTemporary()) {
                            groupinfo.setVisible(true);
                            GetMemberInfo(groupinfo.getNumber(),temIntercomCid,autoanswer);
                        }
                        // 刷新列表
                        RefreshExpandableList();
                  //  }
                    currSessId = mSessId;
                    isintercoming = true;
                    // 设置可用性
                    btn_intercom.setEnabled(true);
                    // 设置默认对讲组
                    tv_groupvl.setText(GroupManager.getInstance().getOptimumgroup().getName());
                    // 打开扬声器
                    if (!audioManager.isSpeakerphoneOn()) {
                        OpenSpeaker(audioManager);
                    }
                    // 自动接听
                    AutoAnswer(mSessId, GroupManager.getInstance().getOptimumgroup());
                    return;
                }






                Log.e("====AASSSSSSSSSSSS","GroupManager.getInstance().getOptimumgroup() : " + GroupManager.getInstance().getOptimumgroup()
                        + "\r\n"
                        + "groupinfo.getNumber() : " + groupinfo.getNumber()
                        + "\r\n"
                        + "groupinfo.isTemporary() : " + groupinfo.isTemporary()
                        + "\r\n"
                        + "sipUser.getUsername() : " + sipUser.getUsername()
                        + "\r\n"
                        + "groupinfo.getNumber().contains(sipUser.getUsername()) : " + groupinfo.getNumber().contains(sipUser.getUsername()));
                if (GroupManager.getInstance().getOptimumgroup() == null
                        && groupinfo.getNumber() != null
                        && groupinfo.isTemporary()
                        && !groupinfo.getNumber().contains(sipUser.getUsername())){

                    // 不是默认对讲组
                    GroupManager.getInstance().setOptimumgroup(groupinfo);
                    // 设置状态
                    if (groupinfo.isTemporary()) {
                        groupinfo.setVisible(true);
                        GetMemberInfo(groupinfo.getNumber(),temIntercomCid,true);
                    }
                    // 刷新列表
                    RefreshExpandableList();
                    //  }
                    currSessId = mSessId;
                    isintercoming = true;
                    // 设置可用性
                    btn_intercom.setEnabled(true);
                    // 设置默认对讲组
                    tv_groupvl.setText(GroupManager.getInstance().getOptimumgroup().getName());
                    // 打开扬声器
                    if (!audioManager.isSpeakerphoneOn()) {
                        OpenSpeaker(audioManager);
                    }
                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                    //Sws 2018-05-16 隐藏 用于解决终端首次发起对讲时状态变更两次
                    // 自动接听
                    AutoAnswer(mSessId, GroupManager.getInstance().getOptimumgroup());
                    Log.e("来了临时对讲",groupinfo.getNumber());
                }else {
                    // 是当前对讲组自动接听
                    if (GroupManager.getInstance().getOptimumgroup().getNumber().trim().equals(groupinfo.getNumber().trim())) {
                        // 设置状态
                        if (groupinfo.isTemporary()) {
                            groupinfo.setVisible(true);

                            GetMemberInfo(groupinfo.getNumber(),temIntercomCid,autoanswer);
                            // 刷新列表
                            RefreshExpandableList();
                        }
                        currSessId = mSessId;
                        // 设置回话编号
                        GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);
                        // 是当前对讲组，自动接听
                        isintercoming = true;
                        // 保证界面始终在屏幕上
                        keepScreenOn(true);
                        // 屏幕始终不锁定
                        showWhenLocked(true);
                        // 不是自己呼出的对讲
                        Log.e("intercomActiv-1166", "lizhiwei 不是自己呼出的对讲设置了EMPTY，设置了页面");
                        currState = GlobalConstant.PTT_STATUS_EMPTY;
                        Log.e("SetViewShow","=====    1282");
                        SetViewShow(currState);
                        // 关闭麦克风
//					Intent callmuteIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
//					callmuteIntent.putExtra("NUTE", true);
//					sendBroadcast(callmuteIntent);
                        // 打开扬声器
                        if (!audioManager.isSpeakerphoneOn()) {
                            OpenSpeaker(audioManager);
                            Log.e("集体测试","OpenSpeaker 1352");
                        }
                        // 发送自动接听广播
					/*Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
					callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
					sendBroadcast(callIntent);*/

                        //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                        // 自动接听
                        AutoAnswer(mSessId, GroupManager.getInstance().getOptimumgroup());
                        Log.e("ceshi duijiang","自动接听   1287");
                    } else {
                        // 不是当前对讲组按优先级处理
                        highvalue = prefs.getString(GlobalConstant.SP_INTERCOM_HIGHPRIORITY, mIntercomPriorityArray[1]);
                        Log.e(TAG + "===========1206===========", "highvalue:" + highvalue);
                        samevalue = prefs.getString(GlobalConstant.SP_INTERCOM_SAMEPRIORITY, mIntercomPriorityArray[1]);
                        lowvalue = prefs.getString(GlobalConstant.SP_INTERCOM_LOWPRIORITY, mIntercomPriorityArray[1]);
                        // 非当前对讲组，按照优先级进行操作
                        if (!isintercoming) {
                            Log.e("***********IntercomAc1109不是当前对讲组且当前无对讲***************", "***********不是当前对讲组且当前无对讲***************");
                            // 不是当前对讲组，不在通话中，依据配置进行处理
                            if (groupinfo.getLevel() < GroupManager.getInstance().getOptimumgroup().getLevel()) {
                                // 大于当前级别
                                if (highvalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.w("intercomActiv-1198", "lizhiwei 未在通话中 大于当前级别 挂断对讲");
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.w("intercomActiv-1209", "lizhiwei 未在对讲通话中，大于当前级别 挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);

                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    // 自动接听
                                    AutoAnswer(mSessId, GroupManager.getInstance().getOptimumgroup());
                                    Log.e("ceshi duijiang","自动接听   1324");
                                    isintercoming = true;
                                } else if (highvalue.equals(mIntercomPriorityArray[1]) || highvalue.equals("提示消息")) {
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (null == newCalling) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.w("intercomActiv-1224", "lizhiwei 不是当前对讲组，不在通话中，大于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            } else if (groupinfo.getLevel() == GroupManager.getInstance().getOptimumgroup().getLevel()) {
                                // 等于当前级别
                                if (samevalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.w("intercomActiv-1237", "lizhiwei 未在通话中 等于当前级别 挂断对讲");
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.e("intercomActiv-1248", "lizhiwei 未在对讲通话中，等于当前级别 挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    // 自动接听
                                    AutoAnswer(mSessId, groupinfo);
                                    Log.e("ceshi duijiang","自动接听   1371");
                                    isintercoming = true;
                                } else if (samevalue.equals(mIntercomPriorityArray[1]) || samevalue.equals("提示消息")) {
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (null == newCalling) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.e("intercomActiv-1266", "lizhiwei 不是当前对讲组，不在通话中，等于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            } else {
                                // 小于当前级别
                                if (lowvalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.e("intercomActiv-1279", "lizhiwei 未在通话中 小于当前级别 挂断对讲");
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.e("intercomActiv-1290", "lizhiwei 未在对讲通话中， 小于当前级别 挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    // 自动接听
                                    AutoAnswer(mSessId, groupinfo);
                                    Log.e("ceshi duijiang","自动接听   1418");
                                    isintercoming = true;
                                } else if (lowvalue.equals(mIntercomPriorityArray[1]) || lowvalue.equals("提示消息")) {
                                    Log.e(TAG, "提示消息1326");
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (null == newCalling) {
                                        Log.e(TAG, "提示消息1330");
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.e("intercomActiv-1305", "lizhiwei 不是当前对讲组，不在通话中，小于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            }
                        } else {
                            Log.e("********IntercomAc1230不是当前对讲组且当前有对讲************", "***********不是当前对讲组且当前有对讲***************");
                            // 不是当前对讲组，且在通话中，不需要挂断之前的对讲
                            if (groupinfo.getLevel() < GroupManager.getInstance().getOptimumgroup().getLevel()) {
                                // 大于当前级别
                                if (highvalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.e("intercomActiv-1322", "lizhiwei 大于当前级别 挂断原来对讲，新级别" + groupinfo.getNumber() + "," + groupinfo.getLevel() + "原级别" + GroupManager.getInstance().getOptimumgroup().getNumber() + "," + GroupManager.getInstance().getOptimumgroup().getLevel());
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (newCalling == null) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.e("intercomActiv-1333", "lizhiwei 对讲通话中， 小大于当前级别 挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    // 自动接听
                                    AutoAnswer(mSessId, GroupManager.getInstance().getOptimumgroup());
                                    Log.e("ceshi duijiang","自动接听   1475");
                                    isintercoming = true;
                                } else if (highvalue.equals(mIntercomPriorityArray[1]) || highvalue.equals("提示消息")) {
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (newCalling == null) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.e("intercomActiv-1348", "lizhiwei 不是当前对讲组，在通话中，大于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            } else if (groupinfo.getLevel() == GroupManager.getInstance().getOptimumgroup().getLevel()) {
                                // 等于当前级别
                                if (samevalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.w("intercomActiv-1361", "lizhiwei 等于当前级别 挂断对讲");
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.w("intercomActiv-1372", "lizhiwei 对讲通话中， 等于当前级别挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);

                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    // 自动接听
                                    AutoAnswer(mSessId, groupinfo);
                                    Log.e("ceshi duijiang","自动接听   1521");
                                    isintercoming = true;
                                } else if (samevalue.equals(mIntercomPriorityArray[1]) || samevalue.equals("提示消息")) {
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (newCalling == null) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭
                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.e("intercomActiv-1387", "lizhiwei 不是当前对讲组，在通话中，等于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            } else {
                                // 小于当前级别
                                if (lowvalue.equals(mIntercomPriorityArray[0])) {
                                    currSessId = mSessId;
                                    // 退出原来对讲
                                    if (GroupManager.getInstance().getOptimumgroup().getDwSessId() != MtcCliConstants.INVALIDID) {
                                        Log.w("intercomActiv-1400", "lizhiwei 小于当前级别 挂断对讲");
                                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                        sendBroadcast(callIntent);
                                    }
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                    // 设置状态
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    newCalling.setIsalright(true);
                                    // 设置状态
                                    Log.e("intercomActiv-1411", "lizhiwei 对讲通话中，小于当前级别挂断原对讲设置新对讲" + groupinfo.getNumber());
                                    GroupManager.getInstance().setOptimumgroup(groupinfo);
								/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                                    GroupManager.getInstance().getOptimumgroup().setDwSessId(mSessId);
                                    //判断手机当前屏幕是否处于亮屏状态   true为打开   false为关闭


                                    // 自动接听
                                    AutoAnswer(mSessId, groupinfo);
                                    Log.e("ceshi duijiang","自动接听   1571");
                                    isintercoming = true;
                                } else if (lowvalue.equals(mIntercomPriorityArray[1]) || lowvalue.equals("提示消息")) {
                                    // 提示消息
                                    CallingInfo newCalling = CallingManager.getInstance().getCallingInfo(mSessId);
                                    if (newCalling == null) {
                                        return;
                                    }
                                    newCalling.setIsalright(true);


                                    InformAnswer(newCalling, GroupManager.getInstance().getOptimumgroup().getDwSessId());
                                } else {
                                    Log.e("intercomActiv-1426", "lizhiwei 不是当前对讲组，在通话中，小于当前级别 自动拒绝");
                                    // 自动拒绝
                                    AutoReject(mSessId);
                                    // 删除通话信息
                                    CallingManager.getInstance().removeCallingInfo(mSessId);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 定位广播接收器
    @SuppressLint("SimpleDateFormat")
    public class PositionRecordBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String type = intent.getStringExtra(GlobalConstant.KEY_POSITION_TYPE);
            if ((type == null)
                    || (!type.equals(GlobalConstant.INTERCOM_TAB))) {
                return;
            }
            ArrayList<String> nos = intent.getStringArrayListExtra(GlobalConstant.KEY_MEETING_NOS);
            if ((nos == null)
                    || (nos.size() <= 0)) {
                return;
            }
            // 创建临时对讲组
            Calendar calendar = Calendar.getInstance();
            String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
            String sessname = (CommonMethod.getCurrLongCNDate(calendar) + "临时对讲").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "").substring("yyyy年MM月dd日".length());
            SimpleDateFormat shortformat = new SimpleDateFormat("HHmm");
            sessname = "临时对讲" + shortformat.format(calendar.getTime());
            GroupInfo newgroup = new GroupInfo();
            newgroup.setName(sessname);
            newgroup.setNumber(sid);
            newgroup.setLevel(10);
            newgroup.setTemporary(true);

            List<MemberInfo> listMembers = new ArrayList<MemberInfo>();
            // 设置组成员
            for (String no : nos) {
                MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(no);
                if (memberinfo == null) {
                    memberinfo = new MemberInfo(no, no, 0);
                }
                listMembers.add(memberinfo);
            }
            newgroup.setListMembers(listMembers);

            Log.e("------------IntercomAc 1396---------------", "------创建临时对讲sid：" + sid + "---sessname：" + sessname);
			/*// 添加到对讲组管理
			GroupManager.getInstance().addGroup(newgroup);
			MemberInfo[] s = new MemberInfo[listMembers.size()];
			s = listMembers.toArray(s);
			GroupManager.getInstance().addReceivedMembers(newgroup.getNumber(), s);
			Toast.makeText(IntercomActivity.this, GroupManager.getInstance().getGroupCount() + "," + sid, Toast.LENGTH_SHORT).show();*/


            btn_intercom.setEnabled(true);
            Log.e("intercomActiv-1489", "lizhiwei 临时对讲设置对讲组" + newgroup.getNumber());
			/*// 设置当前对讲组
			GroupManager.getInstance().setOptimumgroup(newgroup);
			// 刷新对讲组列表
			MtcDelegate.log("lizhiwei 收到定位对讲，刷新列表");
			RefreshExpandableList();
			// 设置画面
			MtcDelegate.log("lizhiwei 临时对讲设置EMPTY，设置了页面");
			currState = GlobalConstant.PTT_STATUS_EMPTY;
			SetViewShow(currState);*/

            // 创建会议
            CreateConference(newgroup, listMembers, GlobalConstant.CONFERENCE_TYPE_INTERCOM, GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
        }
    }

    //适配各个机型的ptt键
    private class PttKeyAdapterReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || action.length() == 0) {
                return;
            }
            if (action.equals("com.hikvision.keyevent.PPTConn")
                    || action.equals("com.android.network.intercom")
                    || action.equals("com.dfl.f034.pttdown")
                    || action.equals("com.yl.ptt.keydown")
                    || action.equals("android.intent.action.PTT.down")) {
                Log.e("PTT 按下状态", "PTT 按下状态");

                //发送广播申请话权
                Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_REQ);
                sendBroadcast(requestIntercomIntent);
            }


            if (action.equals("com.hikvision.keyevent.PPTDisConn")
                    || action.equals("com.android.network.intercom.close")
                    || action.equals("com.dfl.f034.pttup")
                    || action.equals("com.yl.ptt.keyup")
                    || action.equals("android.intent.action.PTT.up")) {
                Log.e("PTT 弹起状态", "PTT 弹起状态");

                //发送广播释放话权
                //Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_RELEASE);
                //sendBroadcast(requestIntercomIntent);
            }

            //ZTE GH800
            if (action.equals("com.ztegota.action.gotakey203")) {
                boolean keyAction = intent.getBooleanExtra("KeyAction", true);
                if (keyAction == true) {

                    //发送广播申请话权
                    Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_REQ);
                    sendBroadcast(requestIntercomIntent);
                } else {
                    //弹起
                    //发送广播释放话权
                    //Intent requestIntercomIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_RELEASE);
                    //sendBroadcast(requestIntercomIntent);
                }
            }

        }
    }

    // 自动接听
    private void AutoAnswer(int mSessId, GroupInfo groupinfo)
    {
        // 设置状态
        if (groupinfo.isTemporary()) {
            groupinfo.setVisible(true);
            GetMemberInfo(groupinfo.getNumber(),temIntercomCid,false);
        }
        isintercoming = true;
        btn_intercom.setEnabled(true);
        // 切换对讲组
        Log.e("=======测试临时对讲====", "lizhiwei 自动应答设置对讲组" + groupinfo.getNumber());
        GroupManager.getInstance().setOptimumgroup(groupinfo);
        // 设置优先对讲组
        tv_groupvl.setText(GroupManager.getInstance().getOptimumgroup().getName());
        // 设置画面
        Log.e("=======测试临时对讲====", "lizhiwei 自动接听设置了EMPTY，设置了页面");

        //解决首次发起对讲 状态闪一下问题
       //currState = GlobalConstant.PTT_STATUS_EMPTY;
        Log.e("=======测试临时对讲====","=====    1749");
        SetViewShow(currState);

        // 关闭麦克风
//		Intent callmuteIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
//		callmuteIntent.putExtra("NUTE", true);
//		sendBroadcast(callmuteIntent);
        // 打开扬声器
        if (!audioManager.isSpeakerphoneOn()) {
            OpenSpeaker(audioManager);
            Log.e("=======测试临时对讲====","OpenSpeaker 1829");
        }
        // 屏幕始终不锁定
        showWhenLocked(true);
        // 刷新列表
        Log.e("=======测试临时对讲====", "lizhiwei 自动接听对讲通话，刷新列表");
        RefreshExpandableList();
        // 发送自动接听广播
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        sendBroadcast(callIntent);
    }

    // 提示消息
    private void InformAnswer(final CallingInfo callingInfo, final int oldsessionid)
    {
        if (!isAppOnForeground()){
            Intent ootStartIntent=new Intent(context, MainActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("AUTOLOGIN", true);
            startActivity(ootStartIntent);
            Log.e("============1765======","执行跳转");
        }

        if (callingInfo == null) {
            return;
        }
        // TODO Auto-generated method stub
        Log.e("intercomActiv-1612", "lizhiwei 忽略列表" + declines.size() + "," + callingInfo.getRemoteName());
        // 正在对讲中，如果呼入存在于忽略列表中则自动忽略，直到当前对讲结束
        if (isintercoming) {
            for (GroupInfo group : declines) {
                if (callingInfo.getRemoteName().equals(group.getNumber())) {
                    Log.e("intercomActiv-1617", "lizhiwei 正在对讲中，如果呼入存在于忽略列表中则自动忽略，直到当前对讲结束");
                    // 发送自动拒绝广播
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
                    sendBroadcast(callIntent);

                    return;
                }
            }
        }



        CustomDialog.Builder builder = new CustomDialog.Builder(IntercomActivity.this);

        builder.setMessage(getString(R.string.info_intercom_new_incoming1));
        builder.setTitle(getString(R.string.info_intercom_new_incoming) + callingInfo.getRemoteName());
        builder.setPositiveButton(getString(R.string.btn_calling_answer), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(callingInfo.getRemoteName());
                // 设置状态
                if (groupinfo.isTemporary()) {
                    //设置为临时对讲组
                    groupinfo.setVisible(true);
                }
                currSessId = callingInfo.getDwSessId();
                isintercoming = true;
                // 释放原有话权
                Release();
                // 设置按钮可用
                btn_intercom.setEnabled(true);
                // 切换对讲组
                Log.e("intercomActiv-1643", "lizhiwei 对讲提示框接听对讲设置对讲组" + groupinfo.getNumber());

                GroupManager.getInstance().setOptimumgroup(groupinfo);
				/*GroupManager.getInstance().getOptimumgroup().setTemporary(true);*/
                GroupManager.getInstance().getOptimumgroup().setDwSessId(currSessId);
                // 设置优先对讲组
                tv_groupvl.setText(GroupManager.getInstance().getOptimumgroup().getName());
                // 设置画面
                Log.e("intercomActiv-1650", "lizhiwei 提示消息接听设置了EMPTY，设置了页面");
                currState = GlobalConstant.PTT_STATUS_EMPTY;
                Log.e("SetViewShow","=====    2094");
                SetViewShow(currState);
                // 打开麦克风
                Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
                callIntent.putExtra("NUTE", false);
                sendBroadcast(callIntent);

                // 打开扬声器
                if (!audioManager.isSpeakerphoneOn()) {
                    OpenSpeaker(audioManager);
                    Log.e("集体测试","OpenSpeaker 1915");
                }
                // 屏幕始终不锁定
                showWhenLocked(true);
                // 刷新列表
                Log.e("intercomActiv-1665", "lizhiwei 消息通知选择接听，刷新列表");
                RefreshExpandableList();

                // 如果是临时对讲发送取得对讲组信息请求
                if (callingInfo.isIstempintercom()) {
					/*// 添加临时对讲组信息
					GroupInfo newgroup = new GroupInfo();
					newgroup.setName(callingInfo.getRemoteName());
					newgroup.setNumber(callingInfo.getRemoteName());
					newgroup.setLevel(0);
					newgroup.setTemporary(true);
					// 添加对讲组
					GroupManager.getInstance().addGroup(newgroup);
					// 设置当前对讲组
					MtcDelegate.log("lizhiwei 临时对讲设置对讲组"+groupinfo.getNumber());
					GroupManager.getInstance().setOptimumgroup(groupinfo);*/
                    // 发送广播通知已获取到对讲组
                    Intent intent = new Intent(GlobalConstant.ACTION_GROUPINFO);
                    sendBroadcast(intent);
                    // 发起获取对讲组消息
                    GetMemberInfo(callingInfo.getRemoteName(),temIntercomCid,true);
                }

                // 发送自动拒绝广播
                Log.e("intercomActiv-1690", "lizhiwei 正在对讲中，如果呼入存在于忽略列表中则自动忽略，直到当前对讲结束");
                callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, oldsessionid);
                sendBroadcast(callIntent);

                // 发送自动接听广播
                callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
                callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
                sendBroadcast(callIntent);

                // 关闭页面
                CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(getString(R.string.btn_calling_decline),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(callingInfo.getRemoteName());
                        // 自动记录忽略列表
                        boolean exists = false;
                        for (GroupInfo group : declines) {
                            Log.e("intercomActiv-1712", "lizhiwei 正在对讲中，如果呼入存在于忽略列表中则自动忽略，直到当前对讲结束" + groupinfo.getNumber() + "," + group.getNumber());
                            if (group.getNumber().equals(groupinfo.getNumber())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            declines.add(groupinfo);
                        }

                        // 移除临时对讲组
                        if (groupinfo.isTemporary()) {
                            GroupManager.getInstance().removeGroupInfo(groupinfo.getNumber());
                        }
                        // 发送自动拒绝广播
                        Log.e("", "lizhiwei 正在对讲中，如果呼入存在于忽略列表中则自动忽略，直到当前对讲结束" + groupinfo.getNumber());
                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
                        sendBroadcast(callIntent);
                        // 关闭提示框
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
                        dialog.dismiss();

                        ignore = false;
                    }
                });
		/*builder.create().show();*/

        WaitingDialog hasWaitingDialog = CallingManager.getInstance().getCallingDialog(callingInfo.getDwSessId());
        if (hasWaitingDialog != null) {
            return;
        }

        final WaitingDialog informing = builder.create();

        //if(callDialog){
        informing.setCancelable(false);
        informing.setDwSessId(callingInfo.getDwSessId());
        informing.show();
        CallingManager.getInstance().addCallingDialog(informing);
        //callDialog = false;
        //}

        CallingManager.getInstance().getCallingDialog(informing.getDwSessId());

        // 10秒无操作自动关闭
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(callingInfo.getRemoteName());
                // 发送自动拒绝广播
                if ((informing != null)
                        && (informing.isShowing())) {
                    MtcDelegate.log("lizhiwei 正在对讲中，10秒无操作自动关闭");
                    // 移除临时对讲组
                    if (groupinfo.isTemporary()) {
                        GroupManager.getInstance().removeGroupInfo(groupinfo.getNumber());
                    }
                    // 发出结束通话
                    Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
                    callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
                    sendBroadcast(callIntent);
                    // 关闭窗口
                    CallingManager.getInstance().removeCallingDialog(informing.getDwSessId());
                    informing.dismiss();
                    //callDialog = true;
                }
            }
        }, 10000);
    }

    // 自动拒绝
    private void AutoReject(int mSessId)
    {
        Log.e(TAG, "*****AutoReject****: " + mSessId);
        // 发送自动拒绝广播
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        sendBroadcast(callIntent);
    }

    // 申请话权
    private void Request()
    {

        if ((GroupManager.getInstance().getOptimumgroup() == null)
                || (GroupManager.getInstance().getOptimumgroup().getNumber() == null)) {
            ToastUtils.showToast(this, getResources().getString(R.string.info_meeting_empty_member));
            return;
        }
        String groupNumber = GroupManager.getInstance().getOptimumgroup().getNumber();
        if (groupNumber.contains("*")){
            groupNumber = groupNumber.substring(3,groupNumber.length());
        }
        btn_intercom.setBackgroundResource(R.drawable.intercom_speaking);
        MediaEngine.GetInstance().ME_PttReqRight_Async(groupNumber, true, new MediaEngine.ME_PttReqRight_CallBack() {
            @Override
            public void onCallback(boolean b, boolean b1) {
                if (!isTipHelper) {
                    TipHelper.Vibrate(IntercomActivity.this, 200);
                    Log.e("Sws测试OnTouch事件","  -*----**-*-*--*--*》 申请话权");
                    // 播放声音
                    //OpenSpeaker(audioManager);
                    playPttAlert(IntercomActivity.this, PTT_STATUS_ACCEPT);

                    isTipHelper = true;
                }
            }
        });
        Log.e("城北待君归", "*******IntercomActivity********申请话权****** groupNumber : " + groupNumber);
        // 通知主页面有一个对讲呼出
        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLINFO);
        callIntent.putExtra(GlobalConstant.KEY_CALL_TYPE, GlobalConstant.CALL_TYPE_INTERCOM);
        callIntent.putExtra(GlobalConstant.KEY_PTT_GROUP, groupNumber);
        sendBroadcast(callIntent);
    }

    // 释放话权
    private void Release()
    {
        if ((GroupManager.getInstance().getOptimumgroup() == null)
                || (GroupManager.getInstance().getOptimumgroup().getNumber() == null)) {
            return;
        }
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        String groupNumber = GroupManager.getInstance().getOptimumgroup().getNumber();
        if (groupNumber.contains("*")){
            groupNumber = groupNumber.substring(3,groupNumber.length());
            Log.e("城北待君归", "groupNumber : " + groupNumber);
        }
        String msgBody = "req:ptt-cancel\r\nemployeeid:" + sipUser.getUsername() + "\r\ngroupnum:" + groupNumber + "\r\n";
        Log.e("intercomActivity对讲", msgBody);
        Log.e("Sws测试OnTouch事件","  -*----**-*-*--*--*》 释放话权");
        //释放话权
        MediaEngine.GetInstance().ME_PttReqRight_Async(groupNumber, false, new MediaEngine.ME_PttReqRight_CallBack() {
            @Override
            public void onCallback(boolean b, boolean b1) {

            }
        });
    }

    // 创建会议
    private void CreateConference(GroupInfo newgroup, List<MemberInfo> listMembers, int conferenceType, int mediaType)
    {
        // 取得登录用户信息
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        // 设置会议名称

        String members = sipUser.getUsername() + ",";
        for (MemberInfo memberinfo : listMembers) {
            members += memberinfo.getNumber() + ",";
        }

        // 发送创建会议请求
//        String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
//        // 配置服务器IP
//        if (!uri.contains(sipServer.getServerIp())) {
//            uri += sipServer.getServerIp();
//        }
        String msgBody = "req:createconfe\r\n" +
                "sid:" + newgroup.getNumber() + "\r\n" +
                "employeeid:" + sipUser.getUsername() + "\r\n" +
                "sessname:" + newgroup.getName() + "\r\n" +
                "sessnum:" + newgroup.getNumber() + "\r\n" +
                "members:" + members + "\r\n" +
                "calltype:" + conferenceType + "\r\n" +
                "mediatype:" + mediaType + "\r\n";

        String[] member = new String[listMembers.size()];
        for (int i = 0; i < listMembers.size(); i++){
            member[i] = listMembers.get(i).getNumber();
        }

 //       MtcDelegate.log("lizhiwei:send,req:get_video," + uri + "," + msgBody);
//     	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_CREATECONFE
//     			, uri
//     			, 1
//     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//     			, msgBody);
        //MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
        //发起临时对讲
        MediaEngine.GetInstance().ME_CreateConf(member,false, MediaEngine.ME_ConfCallType.tempPtt);

    }

    // 取得对讲组信息
    private void GetMemberInfo(String groupno, String sessid,boolean autoanswer)
    {
        // groupno 对讲组号码
        // sessid 当前会议为临时对讲用到
        // autoanswer true为临时对讲

        Log.e("城北待君归","*****************GetMemberInfo********************  groupno ：" + groupno + "\r\n" + "sessid : " + sessid + "\r\n" + "autoanswer : " + autoanswer);
        //临时对讲操作
        if (autoanswer){
            if (sessid != null){
                MediaEngine.ME_ConfUserInfo[] me_confUserInfos = MediaEngine.GetInstance().ME_ConfGetMembers(sessid);

                List<MemberInfo> mList = new ArrayList<>();
                //得到对讲组成员
                for (MediaEngine.ME_ConfUserInfo meConfUserInfo : me_confUserInfos){
                    MemberInfo memberInfo = new MemberInfo();
                    memberInfo.setName(meConfUserInfo.name);
                    memberInfo.setNumber(meConfUserInfo.userId);
                    if (meConfUserInfo.userId.equals(sipUser.getUsername())){
                        memberInfo.setStatus(2);
                    }else {
                        memberInfo.setStatus(meConfUserInfo.userState);
                    }
                    mList.add(memberInfo);
                }
                MemberInfo[] s = new MemberInfo[mList.size()];
                s = mList.toArray(s);
                GroupManager.getInstance().addReceivedMembers(groupno, s);

                // 发送广播通知已获取到对讲组成员
                Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
                sendBroadcast(intent);
                // 发送更新联系人
                Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
                sendBroadcast(callIntent);
            }
        }



        //SipMsg方式获取对讲组信息 该为ICE获取
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

        String msgBody = "req:memberInfo\r\n" +
                "employeeid:" + sipUser.getUsername() + "\r\n" +
                "groupnum:" + groupno + "\r\n";
        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
    }

    // 保持界面始终在屏幕上
    private void keepScreenOn(boolean show)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (show) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }
        getWindow().setAttributes(attrs);
    }

    // 屏幕始终不锁定
    private void showWhenLocked(boolean show)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (show) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        }
        getWindow().setAttributes(attrs);
    }

    // 设置页面显示类型
    private void SetViewShow(int pttstatus)
    {
        Log.e("SetViewShow","pttstatus  : " + pttstatus);
        if (pttstatus == GlobalConstant.PTT_STATUS_EMPTY) {
            // 空闲无操作时
            // 图标
            btn_intercom.setBackgroundResource(R.drawable.intercom_leisure);
            // 显示
            tv_statesvl.setText("空闲");
            tv_speakervl.setText("无");
            tv_queuing.setVisibility(View.INVISIBLE);
           // tv_queuingvl.setVisibility(View.INVISIBLE);
           // tv_queuingvl.setText("");

            Log.e("ceshi duijiang","空闲");
        } else if (pttstatus == PTT_STATUS_REQUEST) {
            // 按钮被按下时
            // 图标
            btn_intercom.setBackgroundResource(R.drawable.intercom_listening);
            Log.e("ceshi duijiang","按下");
        }
        //获得话权
        else if (pttstatus == PTT_STATUS_ACCEPT) {
            if (audioManager.isSpeakerphoneOn()) {
                Log.e("OpenOrClose","Close 2327");
                CloseSpeaker();
            }
            int currVolume = ptt_3g_PadApplication.getCurrentVolume();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    currVolume,
                    AudioManager.STREAM_VOICE_CALL);

            // 打开麦克风
            Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
            callIntent.putExtra("NUTE", false);
            //callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, GroupManager.getInstance().getOptimumgroup().getDwSessId());
            sendBroadcast(callIntent);

            // 申请话权成功
            SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
            MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(sipUser.getUsername());

//            //打开扬声器
//            if (!audioManager.isSpeakerphoneOn()) {
//                OpenSpeaker(audioManager);
//            }
            Log.e("集体测试","OpenSpeaker 2234");
//            // 播放声音
//            playPttAlert(IntercomActivity.this, PTT_STATUS_ACCEPT);

            // 图标
            btn_intercom.setBackgroundResource(R.drawable.intercom_speaking);
            // 显示
            tv_statesvl.setText("发言");
            if ((memberinfo != null)
                    && (memberinfo.getName() != null)
                    && (memberinfo.getName().trim().length() > 0)) {
                tv_speakervl.setText(memberinfo.getName() + "(自己)");
            } else {
                tv_speakervl.setText(sipUser.getUsername() + "(自己)");
            }
            tv_queuing.setVisibility(View.INVISIBLE);
            tv_queuingvl.setVisibility(View.INVISIBLE);
            tv_queuingvl.setText("");

            Log.e("ceshi duijiang","申请到话权");

        } else if (pttstatus == GlobalConstant.PTT_STATUS_WAITING) {
            // 申请话权等待

            Log.e("===========", "等待==================");

        } else if (pttstatus == GlobalConstant.PTT_STATUS_REJECT) {
            // 申请话权拒绝
            // 播放声音
            playPttAlert(IntercomActivity.this, GlobalConstant.PTT_STATUS_REJECT);
            // 图标
            btn_intercom.setBackgroundResource(R.drawable.intercom_leisure);
            Log.e("ceshi duijiang","申请话权失败");
        } else if (pttstatus == GlobalConstant.PTT_STATUS_RELEASE) {
            // 申请话权释放
            // 关闭麦克风
            Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLMUTE);
            callIntent.putExtra("NUTE", true);
            sendBroadcast(callIntent);
            if (GroupManager.getInstance().getOptimumgroup() != null) {
                if (CallingManager.getInstance().getCallingCount() <= 0){
                    if (MeetingActivity.currMeeting == null){
                        CloseMute();
                    }
                }
            }
            // 打开扬声器
            if (audioManager == null) {
                audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
                //设置声音模式
                audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
            }

            int callingCount = CallingManager.getInstance().getCallingCount();
            if (callingCount <= 0){
                if (!audioManager.isSpeakerphoneOn()) {
                    OpenSpeaker(audioManager);
                    Log.e("集体测试","OpenSpeaker 2280     /******************************/***************/***************");
                }
            }
			// 图标
			btn_intercom.setBackgroundResource(R.drawable.intercom_leisure);
			// 显示
			tv_statesvl.setText("空闲");
			tv_speakervl.setText("无");
//			tv_statesvl.setText("收听");
//			tv_speakervl.setText("无");
			tv_queuing.setVisibility(View.INVISIBLE);
			tv_queuingvl.setVisibility(View.INVISIBLE);
			tv_queuingvl.setText("");
            // 播放声音
           // playPttAlert(IntercomActivity.this, GlobalConstant.PTT_STATUS_RELEASE);
            Log.e("ceshi duijiang","释放话权");
        } else {
            Log.e("ceshi duijiang","离线");
            // 图标
            btn_intercom.setBackgroundResource(R.drawable.intercom_offline);
            btn_intercom.setEnabled(false);
            // 显示
            tv_groupvl.setText("空");
            tv_statesvl.setText("空");
            tv_speakervl.setText("无");
            tv_queuing.setVisibility(View.INVISIBLE);
            tv_queuingvl.setVisibility(View.INVISIBLE);
            tv_queuingvl.setText("");
        }
    }

    // 播放提示音
    public synchronized void playPttAlert(Context context, final int pttAlertType)
    {

        switch (pttAlertType) {
            case PTT_STATUS_ACCEPT:
                mp = MediaPlayer.create(context, R.raw.pttaccept);
                break;
            case GlobalConstant.PTT_STATUS_REJECT:
                mp = MediaPlayer.create(context, R.raw.pttreject);
                break;
            case GlobalConstant.PTT_STATUS_RELEASE:
                mp = MediaPlayer.create(context, R.raw.pttrelease);
                break;
            default:
                break;
        }

        if (mp == null) {
            return;
        }

        //设置播放音量为当前系统的媒体音量
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        float volume = (float) current;
        mp.setVolume(volume,volume);

        // 播放完毕操作
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                }
                mp.release();
            }
        });

        // 播放失败操作
        mp.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.release();

                return true;
            }
        });

        if (audioManager == null) {
            audioManager = (AudioManager) IntercomActivity.this.getSystemService(Context.AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }

        //int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        currVolume = ptt_3g_PadApplication.getCurrentVolume();

        if (audioManager.isWiredHeadsetOn()) {
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
        } else {

            int currVolumeCall = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            //Log.e("按下PTT", ""+currVolumeCall);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolumeCall, AudioManager.STREAM_VOICE_CALL);
        }

        //关闭麦克风
        audioManager.setMicrophoneMute(false);
        Log.e("集体测试","设置麦克风 intercom 2322  取消静音");
        audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        // 打开扬声器
        audioManager.setSpeakerphoneOn(true);
        int currVolume = ptt_3g_PadApplication.getCurrentVolume();
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                currVolume,
                AudioManager.STREAM_VOICE_CALL);
        Log.e("集体测试","测试扬声器 intercom    2325   true");

        if (mp != null || mp.isPlaying()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mp.stop();
        }
        mp.setLooping(false);
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (pttAlertType) {
            case PTT_STATUS_ACCEPT:

                mp.start();

                break;
            case GlobalConstant.PTT_STATUS_REJECT:
                mp.start();
                break;
            case GlobalConstant.PTT_STATUS_RELEASE:
                mp.start();
                break;
            default:
                break;
        }

    }

    private static int currVolume = 0;

    //打开扬声器
    public void OpenSpeaker(AudioManager audioManager)
    {
        try {
            Log.e("==IntercomActivity==", "lizhiwei:对讲打开了扬声器");
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
            //currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            int currVolume = ptt_3g_PadApplication.getCurrentVolume();
            if (!audioManager.isSpeakerphoneOn()){
                //打开扬声器
                audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        currVolume,
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker()
    {
        try {
            Log.e("==IntercomActivity==", "lizhiwei:对讲关闭了扬声器");

            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()){
                    audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
                    //关闭扬声器
                    audioManager.setSpeakerphoneOn(false);
//                    int currVolume = ptt_3g_PadApplication.getCurrentVolume();
//                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//                            currVolume,
//                            AudioManager.STREAM_VOICE_CALL);
                }
                Log.e("集体测试","测试扬声器 intercom    2396   false");
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭麦克风
    public void CloseMute()
    {
        Log.e("==IntercomActivity==", "lizhiwei:关闭了麦克风");

        if (audioManager == null) {
            Log.e("----====----", "关闭了麦克风");
            audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }

        audioManager.setMicrophoneMute(true);
        Log.e("集体测试","设置麦克风 intercom 2426  静音");

    }
    // 打开麦克风
    public void openMute()
    {
        Log.e("==IntercomActivity==", "lizhiwei:打开麦克风");
        if (audioManager == null) {
            Log.e("----====----", "打开麦克风");
            audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }
        audioManager.setMicrophoneMute(false);
        Log.e("集体测试","设置麦克风 intercom 2435  取消静音");
    }


    // 提示是否退出
    @SuppressWarnings("unused")
    private void askIfExit()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_main_exitinfo));
        builder.setMessage(getString(R.string.title_main_ifexit));
        builder.setPositiveButton(getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new SettingExitFragment(IntercomActivity.this).exit(); //之前的退出操作
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

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground()
    {
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

    /**
     * 唤醒手机屏幕并解锁
     */
    public  void wakeUpAndUnlock()
    {
    }

    //播放SoundPool
    private void playSoundPoolWav(int soundID)
    {
        try {
            if (audioManager.isSpeakerphoneOn()) {
                CloseSpeaker();
            }

            Log.e("当前扬声器状态", "当前扬声器状态： " + audioManager.isSpeakerphoneOn());

            switch (soundID) {
                case 1:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSoundPool != null)
                                mSoundPool.play(soudMap.get(1), 1, 1, 0, 0, 1);
                            if (sVibrator == null)
                                sVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            sVibrator.vibrate(100);
                            Log.e("播放提示音accept", "播放提示音accept");
                        }
                    }, 200);
                    break;
                case 2:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSoundPool != null)
                                mSoundPool.play(soudMap.get(2), 1, 1, 0, 0, 1);
                            Log.e("播放提示音release", "播放提示音release");
                        }
                    }, 200);
                    break;
                case 3:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSoundPool != null)
                                mSoundPool.play(soudMap.get(3), 1, 1, 0, 0, 1);
                            Log.e("播放提示音reject", "播放提示音reject");
                        }
                    }, 200);
                    break;
                case 4:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSoundPool != null)
                                mSoundPool.play(soudMap.get(4), 1, 1, 0, 0, 1);
                            Log.e("播放提示音message", "播放提示音message");
                        }
                    }, 5);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {}
    }

    //是否SoundPool
    private void releaseSoundPoolWav()
    {
        if(mSoundPool != null)
            mSoundPool.release();
    }

    //初始化SoundPool
    private void intiSoundPoolWav()
    {
        try {
            mSoundPool = new SoundPool(4, AudioManager.STREAM_SYSTEM, 0);
            soudMap = new HashMap<Integer, Integer>();
            soudMap.put(1, mSoundPool.load(IntercomActivity.this, R.raw.pttaccept, 1));
            soudMap.put(2, mSoundPool.load(IntercomActivity.this, R.raw.pttrelease, 1));
            soudMap.put(3, mSoundPool.load(IntercomActivity.this, R.raw.pttreject, 1));
            soudMap.put(4, mSoundPool.load(IntercomActivity.this, R.raw.message, 1));
        } catch (Exception e) {}
    }

    // 查询列表操作
    // 实现线程同步搜索联系人
    // 线程同步问题可能会导致数据显示错误，比如会显示上一次搜索到的数据
    public void searchList(final String s) {
        // 用户可能正在快速输入电话号码，之前在搜索的联系人列表已经过时了。
        final int i = ++lastest;
        // 开一个线程来进行快速搜索
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (TextUtils.isEmpty(s)) {
                    // 如果输入的字符串为空，则显示所有联系人
                    showGroupInfos = groupInfos;
                } else {
                    // searhContacts指向搜索返回的list
                    showGroupInfos = searchSystemContacts(s);
                }

                if (i == lastest) { // 避免线程同步问题
                    IntercomActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (i == lastest) { // 避免线程同步问题
                                refreshContactsList(showGroupInfos);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    // 查询系统联系人
    private ArrayList<GroupInfo> searchSystemContacts(String search) {
        ArrayList<GroupInfo> result = new ArrayList<GroupInfo>();
        for (GroupInfo groupinfo : groupInfos) {
            GroupInfo newgroup = new GroupInfo();
            newgroup.setName(groupinfo.getName());
            newgroup.setNumber(groupinfo.getNumber());
            newgroup.setLevel(groupinfo.getLevel());
            newgroup.setTemporary(groupinfo.isTemporary());
            if ((groupinfo.getListMembers() != null)
                    && (groupinfo.getListMembers().size() > 0)) {
                newgroup.setListMembers(new ArrayList<MemberInfo>());
                for (MemberInfo memberinfo : groupinfo.getListMembers()) {
                    if (((memberinfo.getNumber() != null) && (memberinfo
                            .getNumber().indexOf(search) >= 0))
                            || ((memberinfo.getName() != null) && (memberinfo
                            .getName().indexOf(search) >= 0))) {
                        newgroup.getListMembers().add(memberinfo);
                    }
                }
            }
            result.add(newgroup);
        }
        return result;
    }

    // 刷新联系人列表
    public void refreshContactsList(ArrayList<GroupInfo> groupinfo) {
        ArrayList<GroupInfo> groupInfo = new ArrayList<GroupInfo>();
        // 添加系统用户
        groupInfo.clear();
        groupInfo.addAll(groupinfo);
        if (expandableListView == null) {
            return;
        }
        adapter = new GroupExpandableAdapter(this, groupInfo);
        expandableListView.setAdapter(adapter);
        groupInfo = null;
    }
}
