package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.Communicationbridge;
import com.azkj.pad.service.PTTService;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.MtcCallDb;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.MtcCliDb;
import com.juphoon.lemon.ui.MtcDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.sword.SDK.MediaEngine;

@SuppressLint("SdCardPath")
public class LoginActivity extends Activity implements MtcDelegate.Callback {
    // 全局变量定义
    private SharedPreferences prefs;
    private Timer timer;
    private TextView tv_appname, tv_placeholder4, tv_placeholder5, tv_showsetting, tv_copyright, tv_offlinemap;
    private EditText et_username, et_password, et_serverip, et_port;
    private Button btn_login, btn_cancel;
    private CheckBox cb_savepass;
    private boolean settingshown = false;
    //接收登录状态
    private LoginStateReceiver loginStateReceiver;
    // 加载动画
    private WaitingDialog loading;
    //重复登录对话框
    private LoginRepeatDialog.Builder loginrepeatdialog;
    // 重试窗体是否正在显示
    private boolean shownretry = false;
    // 是否强制停止登录
    private boolean forcebreak = false;
    // 音频解码
    private ArrayList<String> mAudioCodecArray;
    // 是否自动登录
    private boolean autologin = false;

    private static final int ONGOING_ID = -1;
    private NotificationManager mNotificationManager;
    //输入授权服务器IP
    private AlertDialog dlg;
    private CustomDialog.Builder rebuilder;
    //全局
    private PTT_3G_PadApplication ptt_3g_PadApplication;
    private Communicationbridge comm;
    // 登录状态消息接收
    private LoginReceiver loginReceiver;
    private int i = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制竖屏
        //MtcDelegate.registerCallback(this);
        // 设置内容视图


        // Android点击图标重新启动问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        i++;

        Log.e("分辨率", " === "+getResources().getDisplayMetrics().widthPixels+",==="
        +getResources().getDisplayMetrics().heightPixels);

        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();

        //自动登陆
        if (this.getIntent().hasExtra("AUTOLOGIN")) {
            autologin = this.getIntent().getBooleanExtra("AUTOLOGIN", false);
            //Toast.makeText(this, "AUTOLOGIN" + autologin, Toast.LENGTH_SHORT).show();
        }

        //全局变量定义
        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        //初始化视图
        InitView();
        //初始化数据
        InitData();
        //取得音频解码
        getAudioCodecArray();
        //设置默认隐藏与显示设置
        ShowOrHiddenSetting();


        //ICE连接成功回调
        MediaEngine.GetInstance().ME_SetOnConnectBusinessServerListener(new MediaEngine.OnConnectBusinessServerListener() {
            @Override
            public void onPublishSuccess() {
                Log.e("==========AAAAAAAAAAAw==============","ICE连接成功 Login");
                Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.ICECONNECTION,true);
                edit.apply();
            }
        });
        Log.e("*****************","comm == null 进行init");
        comm = new Communicationbridge(ptt_3g_PadApplication, prefs, mAudioCodecArray);
        //mediadeleteRegist();
        loginReceiver = new LoginReceiver();
        IntentFilter didloginIntentFilter = new IntentFilter();
        didloginIntentFilter.addAction(GlobalConstant.ACTION_LOGINSTATE);
        registerReceiver(loginReceiver, didloginIntentFilter);

        //Sws 11/27 add   用于接受登录状态返回
        loginStateReceiver = new LoginStateReceiver();
        IntentFilter loginStateIntentFilter = new IntentFilter();
        loginStateIntentFilter.addAction(GlobalConstant.ACTION_LOGINSTATE_Sws);
        registerReceiver(loginStateReceiver, loginStateIntentFilter);

        if (!isServiceRunning(LoginActivity.this, getPackageName() + ".PTTService")) {
            // 开启服务
            Intent intentServeice = new Intent(PTT_3G_PadApplication.sContext, PTTService.class);
            startService(intentServeice);
        }

        // 自动登录
        if (autologin) {
            if (doSave()) {
                forcebreak = false;
                doLogin();
            }
        }

        //适配
        int height = 30;
        if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
            height = 60;
            et_password.setHeight(height);
            et_port.setHeight(height);
            et_serverip.setHeight(height);
            et_username.setHeight(height);
        }
    }

    /*显示界面*/
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("==Sws测试启动问题", "LoginActivity    onResume");
        // 取得网络状态
        if (MtcDelegate.getNet() == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
            ToastUtils.showToast(ptt_3g_PadApplication, getString(R.string.info_network_unavailable));
        }
    }

    /*界面销毁*/
    @Override
    protected void onDestroy() {
        // 移除通知栏
        //mNotificationManager.cancel(ONGOING_ID);
        // 取消注册回调
        MtcDelegate.unregisterCallback(this);
//
//        MediaEngine.GetInstance().ME_SetCallBack(null);
//        MediaEngine.GetInstance().ME_UnRegist();
//        MediaEngine.GetInstance().ME_Destroy();

        Log.e("onDestroy-LoginAcivity", "onDestroy");
        //Communicationbridge.me_Destroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (dlg != null) {
            dlg.dismiss();
            dlg = null;
        }

        if (loginrepeatdialog != null) {
            loginrepeatdialog = null;
        }
        if (loginReceiver!= null){
            unregisterReceiver(loginReceiver);
        }
        if (loginStateReceiver != null){
            unregisterReceiver(loginStateReceiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = false;
        try {
            value = super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {

        }
        return value;
    }

    // 初始化控件
    private void InitView() {
        // 取得控件
        tv_appname = (TextView) findViewById(R.id.tv_appname);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        //tv_placeholder4 = (TextView)findViewById(R.id.tv_placeholder4);
        //		tv_placeholdernew = (TextView) findViewById(R.id.tv_placeholdernew);

        et_serverip = (EditText) findViewById(R.id.et_serverip);
        //tv_placeholder5 = (TextView)findViewById(R.id.tv_placeholder5);
        et_port = (EditText) findViewById(R.id.et_port);
        cb_savepass = (CheckBox) findViewById(R.id.cb_savepass);
        tv_showsetting = (TextView) findViewById(R.id.tv_showsetting);
        tv_copyright = (TextView) findViewById(R.id.tv_copyright);
        tv_offlinemap = (TextView) findViewById(R.id.tv_offlinemap);

        // 设置控件
        tv_appname.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        tv_appname.setTextSize(45);
        tv_appname.setTextScaleX((float) 1.1);
        Shader shader = new LinearGradient(0, 0, 0, 40, Color.WHITE, Color.GRAY, TileMode.CLAMP);
        tv_appname.getPaint().setShader(shader);
        et_username.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        et_password.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        btn_login.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        btn_cancel.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        et_serverip.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));

        et_port.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        cb_savepass.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        TextPaint tp1 = cb_savepass.getPaint();
        tp1.setFakeBoldText(true);
        tv_showsetting.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        TextPaint tp2 = tv_showsetting.getPaint();
        tp2.setFakeBoldText(true);
        tv_copyright.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        tv_copyright.setTextScaleX((float) 1.1);
        tv_offlinemap.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        tv_offlinemap.setTextScaleX((float) 1.1);
        // 使用输入过滤器InputFilter约束用户输入
        et_serverip.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        et_port.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_username.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		/*android:digits="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`~!@#$%^&*()_+-={}|[]\:";'<>?,./"*/

        // 设置默认值
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        et_username.setText(sipUser.getUsername());
        if (sipUser.isSavepass()) {
            et_password.setText(sipUser.getPasswd());
            cb_savepass.setChecked(true);
        } else {
            et_password.setText("");
            cb_savepass.setChecked(false);
        }
        et_serverip.setText(sipServer.getServerIp());
        et_port.setText(sipServer.getPort());


    }

    // 初始化数据
    private void InitData() {
        // 登录
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

				/*	Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_USERNAME, et_username.getText().toString());
				editor.putString(GlobalConstant.SP_PASSWORD, et_password.getText().toString());
				editor.putString(GlobalConstant.SP_SERVERIP, et_serverip.getText().toString());
				editor.putString(GlobalConstant.SP_PORT, (et_port.getText().toString() == null || et_port.getText().toString().length() <= 0?"0":et_port.getText().toString()));
				editor.putBoolean(GlobalConstant.SP_SAVEPASS, cb_savepass.isChecked());
				editor.commit();*/
                if (doSave()) {
                    forcebreak = false;
                    doLogin();
                }
                //========================分割线================================

            }
        });
        // 退出
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.killBackgroundProcesses(getPackageName());
                // 移除通知栏
                //mNotificationManager.cancel(ONGOING_ID);
                // 提示确认退出
                stopService(new Intent(LoginActivity.this, PTTService.class));
                new SettingExitFragment(LoginActivity.this).exit();  //之前的退出操作
//				MediaEngine.GetInstance().ME_Destroy();
//    			Runtime.getRuntime().gc();
//				LoginActivity.this.finish();
            }
        });
        // 显示隐藏高级设置
        tv_showsetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                settingshown = !settingshown;
                ShowOrHiddenSetting();
            }
        });
        //离线地图
        tv_offlinemap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offlineMapIntent = new Intent(LoginActivity.this, OfflineMapActivity.class);
                offlineMapIntent.putExtra("OfflineMapForwardTo", 1);
                startActivity(offlineMapIntent);
            }
        });
        tv_copyright.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offlineMapIntent = new Intent(LoginActivity.this, OfflineMapActivity.class);
                offlineMapIntent.putExtra("OfflineMapForwardTo", 1);
                startActivity(offlineMapIntent);
            }
        });
    }

    // 显示或隐藏高级设置
    private void ShowOrHiddenSetting() {
        if (settingshown) {
            // 设置高级设置显示
            tv_showsetting.setText(getString(R.string.tv_hiddensettings));
            //tv_placeholder4.setVisibility(View.VISIBLE);
            et_serverip.setVisibility(View.VISIBLE);
            //			tv_placeholdernew.setVisibility(View.VISIBLE);
            //tv_placeholder5.setVisibility(View.VISIBLE);
            et_port.setVisibility(View.VISIBLE);
        } else {
            // 设置高级设置隐藏
            tv_showsetting.setText(getString(R.string.tv_advancedsettings));
            //tv_placeholder4.setVisibility(View.GONE);
            et_serverip.setVisibility(View.GONE);
            //			tv_placeholdernew.setVisibility(View.GONE);
            //tv_placeholder5.setVisibility(View.GONE);
            et_port.setVisibility(View.GONE);
        }
    }

    // 保存登录信息
    private boolean doSave() {
        String ip = et_serverip.getText().toString();
        String userNameString = et_username.getText().toString();
        String passWordString = et_password.getText().toString();


        if (CommonMethod.getInstance().isStrBlank(userNameString)) {
            ToastUtils.showToast(getApplicationContext(), "账号为空！");
            return false;
        }

        if (CommonMethod.getInstance().isStrBlank(passWordString)) {
            ToastUtils.showToast(getApplicationContext(), "密码为空！");
            return false;
        }

        // 服务器地址检查
        if (!CommonMethod.getInstance().checkIP(ip)) {
            ToastUtils.showToast(getApplicationContext(), getString(R.string.wrong_ip));
            return false;
        }

        if (et_port.length() <= 0) {
            ToastUtils.showToast(getApplicationContext(), getString(R.string.wrong_port));
            return false;
        }
        // 将登录信息保存于全局变量
        Editor editor = prefs.edit();
        editor.putString(GlobalConstant.SP_USERNAME, et_username.getText().toString());
        editor.putString(GlobalConstant.SP_PASSWORD, et_password.getText().toString());
        editor.putString(GlobalConstant.SP_SERVERIP, ip);
        editor.putString(GlobalConstant.SP_PORT, (et_port.getText().toString() == null || et_port.getText().toString().length() <= 0 ? "0" : et_port.getText().toString()));
        editor.putBoolean(GlobalConstant.SP_SAVEPASS, cb_savepass.isChecked());
        editor.commit();
        return true;
    }

    // 注册操作
    private void doLogin() {
        // 初始化加载对话框
        WaitingDialog.Builder builder = new WaitingDialog.Builder(this);
        builder.setMessage(getString(R.string.info_logining));
        loading = builder.create();
        loading.setCancelable(false);

        loading.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                // 设置登录按钮可用
                btn_login.setEnabled(true);
                // 关闭加载对话框
                if ((loading != null)
                        && (loading.isShowing())) {
                    // 关闭加载框
                    loading.dismiss();
                    loading = null;
                    // 为强制退出
                    forcebreak = true;
                    // 退出操作
                    //MtcCli.Mtc_CliLogout();
                    MediaEngine.GetInstance().ME_UnRegist();
                }
                return false;
            }
        });
        loading.show();


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 登录操作
                login();
            }
        }, 1000);

    }

    // 登录操作
    private void login() {

        comm.Login(ptt_3g_PadApplication);
    }


    // 显示重试对话框
    private void ShowRetry() {

        // 如果正在展示不要重复展示
        if (!this.shownretry) {
            this.shownretry = true;
            // 登录失败提示
            rebuilder = new CustomDialog.Builder(this);
            rebuilder.setMessage(getString(R.string.info_contact_admin));
            rebuilder.setTitle(getString(R.string.wrong_login));
            rebuilder.setPositiveButton(getString(R.string.btn_retry), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    shownretry = false;
                    dialog.dismiss();
                    dialog = null;
                    //设置重新登录
                    doLogin();
                }
            });
            rebuilder.setNegativeButton(getString(R.string.btn_cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            shownretry = false;
                            dialog.dismiss();
                            dialog = null;
                        }
                    });
            rebuilder.create().show();
        }
    }

    // 登录状态改变
    @Override
    public void mtcDelegateStateChanged(int state, int statCode) {
        Log.e("-----------------LoginAc mtcDelegateStateChanged---------------------", "---------state:" + state + "---statCode:" + statCode);
        switch (state) {
            case MtcDelegate.STATE_LOGINED:
                // 登录成功
                loginOk();
                break;
            case MtcDelegate.STATE_LOGINFAILED:
            case MtcDelegate.STATE_LOGOUTED:
                // 登录失败或退出
                loginFailed();
            case MtcDelegate.STATE_INIT:
                btn_login.setEnabled(true);
                if (loading != null) {
                    loading.dismiss();
                    loading = null;
                }
                // 状态初始化中停止客户端服务
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        // 登陆成功或者失败，结果都会停止
                        //MtcCli.Mtc_CliStop();
                    }
                });
                break;
        }
    }

    // 登录成功
    public void loginOk() {
        // 关闭加载对话框
        if (loading != null) {
            loading.dismiss();
            loading = null;
        }

        // 登录成功进入页面
        Intent it = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(it);
        this.finish();
    }

    // 登录失败
    public void loginFailed() {
        // 关闭加载对话框
        if (loading != null) {
            loading.dismiss();
            loading = null;
        }
        // 设置登录按钮可用
        btn_login.setEnabled(true);
        // 如果不是用户点击back按钮，则给出重试对话框
        if (!forcebreak && (dlg == null || (dlg != null && !dlg.isShowing()))) {
            ShowRetry();
        }
    }

    // 网络状态发生改变时的操作
    @Override
    public void mtcDelegateNetChanged(int net, int previousNet) {
        Log.e("----------LoginAc 576网络环境改变-------------", "--------mtcDelegateNetChanged()----------net:" + net + "---previousNet:" + previousNet);
        switch (MtcDelegate.getState()) {
            case MtcDelegate.STATE_LOGINING:
                // 当前状态为登录状态，网络状态为可用，则继续登录
                if (MtcDelegate.getNet() != MtcCliConstants.MTC_ANET_UNAVAILABLE) {
                    //MtcDelegate.login();
                    Communicationbridge.PjSipLogin();
                }
                break;
            case MtcDelegate.STATE_LOGOUTING:
                // 如果登出中，则无操作
                break;
            default:
                String s = null;
                // 网络不可用
                if (net == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
                    s = getString(R.string.info_network_unavailable);
                } else {
                    // 网络状态良好
                    if (previousNet == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
                        s = getString(R.string.info_network_ok);
                    } else {
                        // 网络发生改变
                        s = getString(R.string.info_network_changed);
                    }
                }
                ToastUtils.showToast(ptt_3g_PadApplication, s);
        }
    }

    @Override
    public void mtcDelegateConnectionChanged() {
    }

    @Override
    public void mtcCliCbReceiveUserMsgReq(String sPeerUri, String sBodyType,
                                          String sMsgBody) {
    }

    // 取得音频解码
    protected ArrayList<String> getAudioCodecArray() {
        if (mAudioCodecArray == null) {
            mAudioCodecArray = new ArrayList<String>();
            final int audioCodecCnt = MtcCallDb.Mtc_CallDbGetAudioCodecCount();
            for (short i = 0; i < audioCodecCnt; ++i) {
                String codec = MtcCallDb.Mtc_CallDbGetAudioCodecByPriority(i);
                if (codec.contains("G722") || codec.contains("iLBC")
                        || codec.contains("iSAC") || codec.contains("opus")
                        || codec.contains("AMR")) {
                    MtcCallDb.Mtc_CallDbSetAudioCodecEnable(codec, false);
                    MtcCliDb.Mtc_CliDbApplyAll();
                    continue;
                }
                mAudioCodecArray.add(codec);
            }
            // 将G729放置于第一个
            short index = -1;
            for (short i = 0; i < mAudioCodecArray.size(); i++) {
                if (mAudioCodecArray.get(i).toLowerCase(Locale.getDefault()).contains("g729")) {
                    index = i;
                    break;
                }
            }
            if (index <= 0) {
                return mAudioCodecArray;
            }
            String temp = mAudioCodecArray.get(0);
            mAudioCodecArray.set(0, mAudioCodecArray.get(index));
            mAudioCodecArray.set(index, temp);
        }

        return mAudioCodecArray;
    }


    private String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity).getClassName();
        } else {
            return null;
        }
    }

    // 登入接收器
    public class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("^^^^^MainAc 627 登入接收器^^^^^^", "^^^^^MainAc 627 登入接收器^^^^^^");
			/*String curruser = intent
						.getStringExtra(GlobalConstant.ACTION_LOGINSTATE);*/
            Boolean msg = intent.getBooleanExtra(GlobalConstant.ACTION_LOGINSTATEMSG, false);
            if (msg) {
                //loginOk();
                //MtcDelegate.setState(4, 0);
                mtcDelegateStateChanged(4, 0);
            } else {
                //loginFailed(0);
                //MtcDelegate.setState(6, 0);
                mtcDelegateStateChanged(6, 0);
            }
            // MtcDelegate.setState(msg==true? 4:6, 0);

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
//        Runtime.getRuntime().gc();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        finish();
        stopService(new Intent(PTT_3G_PadApplication.sContext, PTTService.class));
        new SettingExitFragment(LoginActivity.this).exit();  //之前的退出操作
        super.onBackPressed();
    }

    class LoginStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

// TODO Auto-generated method stub
            Log.e("LoginStateReceiver", "接收到广播");

            boolean arg0 = intent.getBooleanExtra("arg0", false);
            String  arg11 = intent.getStringExtra("arg1");
            Integer arg1 = Integer.valueOf(arg11);
            String arg2 = intent.getStringExtra("arg2");
            Log.e("LoginStateReceiver","arg0: " + arg0);
            Log.e("LoginStateReceiver","arg2: " + arg1);
            Log.e("LoginStateReceiver","arg2: " + arg2);
            if(arg0){
                //注册成功


            }else{

                if (loading == null){
                    return;
                }


                //重复登录
                if(arg1 == 10001 && arg2.equals("Registration failed: repeat login")){
                    ToastUtils.showToast(ptt_3g_PadApplication, "该账号已在别处登录");
                    // 关闭加载对话框
                    if(loading != null || loading.isShowing()){
                        loading.dismiss();
                    }

                    loginRepeat();
                }

                if(arg1 == 401){
                    // 关闭加载对话框
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}*/
                    loginFailed();
                }

                //无网络
                if(arg1 == 503 && arg2.equals("Registration failed: Network is unreachable")){
                    // 关闭加载对话框
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}*/
                    loginFailed();
                }
                //无效参数
                if(arg1 == 503 && arg2.equals("Registration failed: Invalid argument")){
                    // 关闭加载对话框
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}*/
                    loginFailed();
                }
                //无效参数
                if(arg1 == 10003){
                    // 关闭加载对话框
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}*/
                    loginFailed();
                }

                //sip类型账号登录
                if(arg1 == 10002){
                    // 关闭加载对话框
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}*/
                    loginFailed();
                }

                //无网络
                if(arg1 == 403 && arg2.equals("Registration failed: Forbidden")){
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}	*/
                    loginFailed();
                }

                //登录超时
                if(arg1 == 408 && arg2.equals("Registration failed: Request Timeout")){
					/*if(loading != null || loading.isShowing()){
						loading.dismiss();
					}	*/
                    loginFailed();
                }

                if (arg1 == -1 && arg2.equals("Login Has Failed")){
                    Log.e("=============Login====","arg1 == -1 && arg2.equals(\"Login Has Failed\")");
                    if (loading.isShowing()){
                        Log.e("=============Login====","loading.isShowing()");
                        loading.dismiss();
                    }
                    loginHasFailedDialog();
                    Log.e("=============Login====","loginHasFailedDialog()");
                }
            }
        }

        //重复登录
        private void loginRepeat() {
            // TODO Auto-generated method stub
            if (loginrepeatdialog == null) {
                loginrepeatdialog = new LoginRepeatDialog.Builder(LoginActivity.this);
                loginrepeatdialog.setTitle(getString(R.string.wrong_login));
                loginrepeatdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        dialog = null;
                    }
                });
            }
            loginrepeatdialog.create().show();
        }

    }

    private void loginHasFailedDialog() {
        if (loginrepeatdialog == null) {
            loginrepeatdialog = new LoginRepeatDialog.Builder(LoginActivity.this);
            loginrepeatdialog.setTitle(getString(R.string.wrong_loginHasFailed));
            loginrepeatdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new SettingExitFragment(LoginActivity.this).exit(); //之前的退出操作
                }
            });
        }
        loginrepeatdialog.create().show();
    }


    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }



}
