package com.azkj.pad.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.azkj.chw.coreprogress.helper.ProgressHelper;
import com.azkj.chw.coreprogress.listener.ProgressListener;
import com.azkj.chw.coreprogress.listener.impl.UIProgressListener;
import com.azkj.pad.activity.MainActivity;
import com.azkj.pad.activity.PTT_3G_PadApplication;
import com.azkj.pad.activity.R;
import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.DownLoadFile;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.IncomingIntercom;
import com.azkj.pad.model.IncomingMeeting;
import com.azkj.pad.model.IncomingSingle;
import com.azkj.pad.model.MemberChangeInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.model.SessionCallInfos;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.MessageNotification;
import com.azkj.pad.utility.ToastUtils;
import com.azkj.sws.library.logger.MyLogger;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.MtcUri;
import com.juphoon.lemon.callback.MtcAnzCb;
import com.juphoon.lemon.callback.MtcCliCb;
import com.juphoon.lemon.ui.MtcCallDelegate;
import com.juphoon.lemon.ui.MtcDelegate;
import com.juphoon.lemon.ui.MtcProximity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.sword.SDK.MediaEngine;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeBroadcast;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeForceremove;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeIntercom;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeInterpose;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeMCUMetting;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeMonitor;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeNone;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeReport;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeSingle;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeSingle2;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeSwitch;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeTemporary;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeTmpgroup;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeTmpintercom;
import static cn.sword.SDK.MediaEngine.ME_SessionType.CallTypeVideobug;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateBusy;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateCallout;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateConnect;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateHold;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateIncoming;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateNone;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateNormal;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateOffhook;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateQueue;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateRelease;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateRinging;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateSpeak;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateUnhold;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateUnspeak;
import static cn.sword.SDK.MediaEngine.ME_UserState.CallStateZombie;


/*综合调度中心(处理接收消息，对讲组、消息、命令等)*/
public class PTTService extends Service implements MtcDelegate.Callback, MtcCliCb.Callback, MtcAnzCb.Callback, MediaEngine.OnReceiveFileListener {

    private String TAG = "SwsTestsip";
    // 全局变量定义
    private SharedPreferences prefs;
    // 当前对讲组索引
    private int currgroupindex = 0;
    // 取得组成员线程
    private GetMemberInfoThread getMemberInfoThread = new GetMemberInfoThread();
    private static NotificationManager nm;
    //全局
    private PTT_3G_PadApplication ptt_3g_PadApplication;
    private static boolean isStopped = false;
    private Ice.Communicator ic = null;
    private Ice.ObjectPrx base = null;

    //离线短信
    private OldMsgReceiver oldMsgReceiver;

    //定时器计时
    private int timerGoing = 0;
    private SipUser sipUser = null;
    private SipServer sipServer = null;
    //保存下载文件列表
    public static List<DownLoadFile> downLoadFileList = null;
    //标识下载文件线程是否正在运行
    public static boolean isRunningDownLoadFile = false;
    public static Timer mTimer = null;
    public static TimerTask mTimerTask = null;
    public static boolean isPauseDown = false;
    public static boolean isDownLoadNow = false;
    private String[] mAnrLevelArray, mArsModeArray;
    public static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            //设置超时，不设置可能会报异常
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    private static Notification notification = null;

    //短信接收统计
    private int fileUploadSuccessCount = 0;
    private int fileUploadErrorCount = 0;

    public static HashMap<String,Contacts> hashMap = new HashMap<>();
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Handler handlerS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int state = msg.arg2;
            int callId = msg.arg1;
            String number = (String) msg.obj;
            if (MediaEngine.ME_CallState.STATE_CONFIRMED == state) {
                //呼出连接建立成功
                MtcCallDelegate.getCallback().mtcCallDelegateTalking(callId);
                Log.e("PTTService","PTTService mtcCallDelegateTalking 呼出连接建立成功");
            } else if (MediaEngine.ME_CallState.STATE_INCOMING == state) {

                //新来电在IncomingCall中处理

           }  else if (MediaEngine.ME_CallState.STATE_CALLING == state) {
                Log.e("PTTService","呼出");
                //呼出操作 (都认为是单呼)
                if (!CommonMethod.hashMap.containsKey(callId)){
                    MediaEngine.ME_IdsPara me_idsPara = new MediaEngine.ME_IdsPara();
                    me_idsPara.setType(GlobalConstant.INCOMINGCALLTYPE_SINGLE);
                    CommonMethod.hashMap.put(callId,me_idsPara);
                }
                MtcCallDelegate.getCallback().mtcCallDelegateOutgoing(callId);
                Log.e("PTTService","PTTService mtcCallDelegateOutgoing 呼出操作");
            }else if (MediaEngine.ME_CallState.STATE_CONNECTING == state){
                //呼出建立连接中
                MtcCallDelegate.getCallback().mtcCallDelegateAlerted(callId, GlobalConstant.CALL_TYPE_CALLING);
                Log.e("PTTService","PTTService mtcCallDelegateAlerted 呼出建立连接中");
            }
        }
    };


    @Override
    public void onCreate()
    {
        // 全局变量定义
        Log.e("PTTService","PTTService    onCreate");
        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        // 取得登录用户信息
        sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initNotification(this, this.getString(R.string.app_name), this.getString(R.string.app_name), this.getString(R.string.app_name));

        showNotification();

        downLoadFileList = ptt_3g_PadApplication.getDownLoadFileList();

		/*applicationContext.registerReceiver(sConnectivityChangeReceiver, 
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));*/
        mediadeleteRegist();

        oldMsgReceiver = new OldMsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.azkj.pad.startTimer");
        registerReceiver(oldMsgReceiver, intentFilter);

        //TODO: 设置接收文件监听
        MediaEngine.GetInstance().ME_SetReceiveFileListener(this);

//        //ICE连接成功回调
//        MediaEngine.GetInstance().ME_SetOnConnectBusinessServerListener(new MediaEngine.OnConnectBusinessServerListener() {
//            @Override
//            public void onPublishSuccess() {
//
//                String uri = sipUser.getUsername() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();
//                String msgBody = "req:groupInfo\r\nemployeeid:" + sipUser.getUsername()
//                        + "\r\n";
//                MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
//            }
//        });
    }

    //YUEZS ADD 2015-09-01 设置视频端口
    public void settingsVedioPort(Integer vedioPort)
    {
        SharedPreferences settings = getSharedPreferences(GlobalConstant.SP_VEDIOCONFIG, 0);
        Editor editor = settings.edit();
        editor.putInt("VedioPort", vedioPort);
        editor.commit();
    }

    //获取配置文件中的视频端口号
    private Integer getVedioPort()
    {
        SharedPreferences settings = getSharedPreferences(
                GlobalConstant.SP_VEDIOCONFIG, 0);
        return settings.getInt("VedioPort", 0);
    }

    @Override
    public void onDestroy()
    {
        Log.e("PTTService", "*********** 计时清除*****202*****");

        stopTimer();
        /*Log.w("%%%%%%PTTSer onDestroy%%%%%%", "%%%%%%%%%%%%%%%");
    	Log.e("PTTService.onDestroy", "重置视频端口号前的值为："+getVedioPort());
    	settingsVedioPort(0);
    	Log.e("PTTService.onDestroy", "重置视频端口号成功！新值为："+getVedioPort());*/
        // 结束自启动
        stopNotification();
        Log.e("PTTService", "*********** stopNotification*****211*****");

        Log.e("PTTService","PTTService    onDestroy");
        // 结束线程
        if (getMemberInfoThread != null) {
            getMemberInfoThread.stopCurrentThread();
        }
        Log.e("PTTService", "*********** 计时清除*****215*****");
        //启动注册线程之后，关闭30秒超时定时器
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            Log.e("PTTService", "*********** 计时清除**********");
        }


        Log.e("PTTService", "*********** 通知清除**********");
		/*int iRet = MtcLcs.Mtc_LcsDeactivateDevice("192.168.1.222",
  				MtcDelegate.getLicensePath());
          Log.w("%%%%%%PTTSer onDestroy%%%%%%", "%%%%%%%%%%%%%%%iRet:" + iRet);*/
        // 注销
        //MtcDelegate.logout();
        // 停止代理
        MtcProximity.stop();
        // 清空通话回调
        if (MtcCallDelegate.getCallback() == this) {
            MtcCallDelegate.setCallback(null);
        }
        // 注销回调
        MtcDelegate.unregisterCallback(this);
        // 清空视频回调
        MtcAnzCb.setCallback(null);
        // 消息回调
        MtcCliCb.unregisterCallback(this);
        //System.exit(0);
        unregisterReceiver(oldMsgReceiver);

        android.os.Process.killProcess(android.os.Process.myPid());
    }


    //离线消息广播接收器--由Main界面发送通知   接收到后开启timer
    public class OldMsgReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            startTimer();
        }
    }


    // 登出接收器
    public class LogoutReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.w("#######PTTSe 123", "#######LogoutReceiver登出接收器stopSelf");
            //stopSelf();
        }
    }

    //Notification初始化
    public void initNotification(Context context, String ticker,
                                 String title, String msg)
    {
        if (notification == null) {
            //notification = new Notification(R.drawable.ptt, ticker,System.currentTimeMillis());
            Notification.Builder n = new Notification.Builder(PTTService.this);
            n.setSmallIcon(R.drawable.ic_launcher); //设置图标
            n.setLargeIcon(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_launcher));//落下后显示的图标
            n.setTicker(ticker);
            n.setContentTitle(title); //设置标题
            n.setContentText("程序正在运行中"); //消息内容
            n.setWhen(System.currentTimeMillis()); //发送时间
            n.setDefaults(Notification.FLAG_NO_CLEAR); //设置默认的提示音，振动方式，灯光
            n.setAutoCancel(false);//打开程序后图标消失
            notification = n.build();
        }
    }

    //Notification显示
    private void showNotification()
    {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
     //   intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
        if (notification != null) {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.flags = Notification.FLAG_NO_CLEAR;
            //notification.setLatestEventInfo(this, getString(R.string.app_name),"程序正在运行中", pt);
     //       notification.contentIntent = pt;
            nm.notify(R.string.app_name, notification);
        }
    }

    //停止Notification
    public void stopNotification()
    {
        isStopped = true;
        if (nm != null){
            nm.cancel(R.string.app_name);
            nm = null;
        }
    }

    @Override
    public void onStart(Intent intent, int startId)
    {

        super.onStart(intent, startId);
        Log.e("PTTService", "onStart()");
        Log.e("PTTService","PTTService    onStart");
        // 取得登录的用户
        String user = sipUser.getUsername();
        if (user == null) {
            logoutOk();
        } else {
            didLogin();
            getAllSessions();
            //获取所有的离线短信  ---- 转到Main界面进行离线短信的获取
        }
    }

    //获取所有的会话信息
    private void getAllSessions()
    {
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		/*String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
  			if (!uri.contains(sipServer.getServerIp()))
  			{
  				uri += sipServer.getServerIp();
  			}*/
        Calendar calendar = Calendar.getInstance();
        String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        //MtcDelegate.log("chw:send," + uri);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("req:all_sessions");
        stringBuffer.append("\r\nsid:");
        stringBuffer.append(sid);
        stringBuffer.append("\r\nemployeeid:");
        stringBuffer.append(sipUser.getUsername());
        stringBuffer.append("\r\n");
        //String uri="deserve";
        String uri = sipUser.getUsername() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();
        //MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_GETALLSESSIONS, uri, 1, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, stringBuffer.toString());
        MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, stringBuffer.toString());
    }

    // 登出系统
    private void logoutOk()
    {
        Log.w("---logoutOk PTTSer241--", "-------------------logoutOklogoutOklogoutOk---------------------------");
        // 释放软锁
        MtcDelegate.releaseWakeLock();
        // 注销回调
        MtcDelegate.unregisterCallback(this);
        // 注销通话回调
        MtcCallDelegate.setCallback(null);
        //pjsip
        //Communicationbridge.me_Destroy();
        // 停止服务
        this.stopSelf();
        List<Activity> activities = ptt_3g_PadApplication.getActivityList();
        for (Activity activity : activities) {
            activity.finish();
        }

    }

    // 确认登录
    private void didLogin()
    {
        // 注册回调
        MtcDelegate.registerCallback(this);

        if (MtcDelegate.getState() == MtcDelegate.STATE_LOGINED) {
            // 通知更新名称
            SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
            Intent intent = new Intent(GlobalConstant.ACTION_DIDLOGIN);
            intent.putExtra(GlobalConstant.KEY_CURR_USER, sipUser.getUsername());
            sendBroadcast(intent);
        }
        // 取得对讲组
        // 消息接收方SIP URI
        // sip:phone@domain
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        String uri = sipUser.getUsername() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();

        MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, "req:groupInfo\r\nemployeeid:" + sipUser.getUsername() + "\r\n");

    }

    @Override
    public void mtcCliCbServLoginOk() {
        Log.w("#######PTTSe 280", "#######mtcCliCbServLoginOk");
    }

    @Override
    public void mtcCliCbLclLoginOk() {
        Log.w("#######PTTSe 285", "#######mtcCliCbLclLoginOk");
    }

    @Override
    public void mtcCliCbLoginFailed(int dwStatCode) {
        Log.w("#######PTTSe 290", "#######mtcCliCbLoginFailed");
    }

    @Override
    public void mtcCliCbRefreshOk(boolean bActive, boolean bChanged) {
        Log.w("#######PTTSe 295", "#######mtcCliCbRefreshOk");
    }

    @Override
    public void mtcCliCbRefreshFailed(boolean bActive, int dwStatCode) {
        Log.w("#######PTTSe 300", "#######mtcCliCbRefreshFailed");
    }

    @Override
    public void mtcCliCbLclLogout()
    {
        Log.w("#######PTTSe 305", "#######mtcCliCbLclLogout");
    }

    @Override
    public void mtcCliCbServLogout(boolean bActive, int iStatCode, int dwExpires)
    {
        Log.w("#######PTTSe 310", "#######mtcCliCbServLogout");
    }

    @Override
    public void mtcCliCbSubNetChanged(int netType)
    {
        Log.w("#######PTTSe315", "#######mtcCliCbSubNetChanged");
    }

    // 发送消息成功
    @Override
    public void mtcCliCbSendUserMsgOk(int iCookie)
    {

        //10/31  sws add
        //这个方法不调用，短信发送成会回调这个方法         -----》onReceiveMsg
        Log.e("PTTService", "iCookie" + iCookie);
    }

    // 发送消息失败
    @Override
    public void mtcCliCbSendUserMsgFailed(int iCookie)
    {
        MtcDelegate.log("发送消息失败！" + iCookie);
        //如何全局声明是发送的短消息，发送广播，刷新页面
        if (sipUser.getUsername() != null && ptt_3g_PadApplication.getMsgUserNo() != null && ptt_3g_PadApplication.getMsgBody() != null && ptt_3g_PadApplication.getMsgBody().length() > 0) {
            Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGSENDFAILED);
            sendBroadcast(intent);
        }
    }

    //删除数组中空字符串的元素
    public String[] change(String[] strings)
    {

        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < strings.length; i++) {
            if (!"".equals(strings[i])) {
                list.add(strings[i]);
                //Log.e("数组元素",strings[i]);
            }
        }

        return list.toArray(new String[list.size()]);
    }

    // 登录状态改变
    @Override
    public void mtcDelegateStateChanged(int state, int statCode)
    {
        Log.e("PTTService", "--------mtcDelegateStateChanged()------state：" + state + "---statCode:" + statCode);
        Log.e("PTTService", "--------mtcDelegateNetChanged()----------    state:" + state);
        Log.e("PTTService", "--------mtcDelegateNetChanged()----------    statCode:" + statCode);
        if (state == MtcDelegate.STATE_LOGINED) {

        } else if (state == MtcDelegate.STATE_LOGINFAILED) {   //登录失败

        } else if (state == MtcDelegate.STATE_LOGOUTED) {

        }
        String s = null;
        switch (state) {
            case MtcDelegate.STATE_INIT:
                logoutOk();
                ptt_3g_PadApplication.setSipUserState(MtcDelegate.STATE_INIT);
                Log.e("onRegState","设置  离线");
                return;
            case MtcDelegate.STATE_LOGINING:
                s = "Logining...";
                break;
            case MtcDelegate.STATE_LOGINED:

                Log.e("PTTService", "**********登录成功***********");

                s = "Login OK";
                SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
                SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
                // 通知更新名称
                Intent intent = new Intent(GlobalConstant.ACTION_LOGINSTATE);
                intent.putExtra(GlobalConstant.KEY_CURR_USER, sipUser.getUsername());
                intent.putExtra(GlobalConstant.ACTION_LOGINSTATEMSG, true);//新加 yuezs 2017-10-09
                sendBroadcast(intent);
                // 重新取得对讲组
                String uri = sipUser.getUsername();

                MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, "req:groupInfo\r\nemployeeid:" + sipUser.getUsername() + "\r\n");

                CallingInfo callingMeeting = null;
                for (CallingInfo callingInfo : CallingManager.getInstance()
                        .getCallingData()) {
                    if (!callingInfo.isHolding()) {
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                            callingMeeting = callingInfo;
                        }
                        if (callingMeeting != null) {
                            //当前有会议，发送广播重新获取会议成员状态
                            Intent meetingInfoIntent = new Intent(GlobalConstant.ACTION_MEETING_REMEMBERINFO);
                            sendBroadcast(meetingInfoIntent);

                        }
                    }
                }
                ptt_3g_PadApplication.setSipUserState(MtcDelegate.STATE_LOGINED);
                Log.e("onRegState","设置  在线");
                break;
            case MtcDelegate.STATE_LOGINFAILED:
            case MtcDelegate.STATE_LOGOUTED:
                s = "Login Failed " + statCode;
                break;
            case MtcDelegate.STATE_NONE:
                break;
            default:
                break;
        }
        if (s != null) {

        }
    }

    // 网络环境改变
    @Override
    public void mtcDelegateNetChanged(int net, int previousNet)
    {
        Log.e("PTTService", "--------mtcDelegateNetChanged()----------    MtcDelegate.getState():" + MtcDelegate.getState());
        String s = null;
        // 网络不可用
        if (net == MtcCliConstants.MTC_ANET_UNAVAILABLE) {

            Log.e("PTTService", "*********** 网络不可用**********");
            // 网络变为不可用
            s = getString(R.string.info_network_unavailable);
            // 通知退出
            Intent intent = new Intent(GlobalConstant.ACTION_FORCED_OFFLINE);
            sendBroadcast(intent);
            ptt_3g_PadApplication.setNetConnection(false);

            //增加改变成员状态下线
            GroupManager.getInstance().updateGroupMembersStateOffline();
            // 发送广播通知已获取到对讲组成员
            Intent intentOffline = new Intent(GlobalConstant.ACTION_MEMBERINFO);
            sendBroadcast(intentOffline);

            //开启timer，当网络断开30之后，仍然无任何网络连接，关闭呼叫
            int i = 0;
            timerGoing = i;
            handler.postDelayed(runnable, 1000);
            Log.e("PTTService", "***********计时开始**********");
            ptt_3g_PadApplication.setSipUserState(MtcDelegate.STATE_INIT);
            Log.e("onRegState","设置  离线");
        } else {
            Log.e("PTTService", "*********** 网络可用**********");
            //启动注册线程之后，关闭30秒超时定时器
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                Log.e("PTTService", "*********** 计时清除**********");
            }

            // 网络状态良好
            if (previousNet == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
                // 网络由不可用变为可用
                s = getString(R.string.info_network_ok);
                // 重新登录
                MtcDelegate.login();
                Log.e("PTTService", "PTTservice 重新登录");

                MediaEngine.GetInstance().ME_HandleIpChange();
                //boolean state = Communicationbridge.PjSipLogin();
//                if (state) {
//                    //发送定位广播
//                    Intent intent = new Intent(GlobalConstant.ACTION_FORCED_ONLINE);
//                    sendBroadcast(intent);
//
//                    //通知主界面更新用户图标状态
//                    Intent intent1 = new Intent(GlobalConstant.ACTION_DIDLOGIN);
//                    sendBroadcast(intent1);
//                }

                ptt_3g_PadApplication.setNetConnection(true);
            } else {
                // 网络状态发生改变
                s = getString(R.string.info_network_changed);
                MediaEngine.GetInstance().ME_HandleIpChange();
            }
        }
        ToastUtils.showToast(this, s);
    }

    // 连接改变
    @Override
    public void mtcDelegateConnectionChanged()
    {
        Log.e("PTTService", "#######mtcDelegateConnectionChanged 连接改变");

        Intent intent = new Intent(GlobalConstant.ACTION_CONNECTION_CHANGE);
        sendBroadcast(intent);
    }

    // 消息接收，接收系统及普通消息
    @SuppressWarnings("unused")
    @Override
    public void mtcCliCbReceiveUserMsgReq(String sPeerUri, String sBodyType,
                                          String sMsgBody)
    {
        //TODO 所有SIP Message 接收处理
        Log.e("PTTService", "sMsgBody : " + sMsgBody + "\r\n" + "sBodyType : " + sBodyType + "\r\n" + "sPeerUri : " + sPeerUri);
        if (sMsgBody == null || "".equals(sMsgBody)) {
            return;
        }

        String[] strMsg = sMsgBody.split("\r\n");
        // 获取消息类型
        String msgType = "";
        if (strMsg.length > 0) {
            msgType = strMsg[0].toLowerCase(Locale.getDefault());
        }
        Log.e("PTTService", "-------mtcCliCbReceiveUserMsgReq()------msgType:" + msgType);
        if (msgType.contains(GlobalConstant.PTT_MSG_IND_HEARTBEAT.toLowerCase(Locale.getDefault()))) {
            // 心跳机制
            String groupnos = msgType.substring(GlobalConstant.PTT_MSG_IND_HEARTBEAT.length());
            if ((groupnos == null)
                    || (groupnos.trim().length() <= 0)) {
                return;
            }
            String[] nos = groupnos.split(",");
            if ((nos == null)
                    || (nos.length <= 0)) {
                return;
            }
            List<GroupInfo> groupinfos = GroupManager.getInstance().getGroupData();
            List<GroupInfo> noexistsgroups = new ArrayList<GroupInfo>();
            // 判断是否存在于对讲组中
            for (GroupInfo info : groupinfos) {
                boolean exists = false;
                for (String no : nos) {
                    if ((no == null)
                            || (no.trim().length() <= 0)) {
                        continue;
                    }
                    if (info.getNumber().equals(no)) {
                        exists = true;
                        break;
                    }
                }
                // 不在对讲组中则删除对讲组
                if (!exists) {
                    noexistsgroups.add(info);
                }
            }
            // 发出心跳处理广播
            Intent intent = new Intent(GlobalConstant.ACTION_HEARTBEAT);
            sendBroadcast(intent);
            // 不在对讲组中则删除对讲组
            if (noexistsgroups.size() <= 0) {
                return;
            }
            for (GroupInfo info : noexistsgroups) {
                GroupManager.getInstance().getGroupNumMebersMap().remove(info.getNumber());
            }
            GroupManager.getInstance().getGroupData().removeAll(noexistsgroups);
            // 发送广播通知对讲组已变更
            Intent intent1 = new Intent(GlobalConstant.ACTION_GROUPINFO);
            sendBroadcast(intent1);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_MEMBER_STATUSCHANGE.toLowerCase(Locale.getDefault()))) {
            // 注册状态变更
            if (strMsg.length <= 0) {
                return;
            }
            String[] statuses = strMsg[1].split(";");
            if ((statuses == null)
                    || (statuses.length <= 0)) {
                return;
            }
            SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
            boolean hasall = false;
            MemberInfo selfInfo = null;
            List<MemberInfo> memberinfos = new ArrayList<MemberInfo>();
            for (String status : statuses) {
                if ((status == null)
                        || (status.trim().length() <= 0)) {
                    continue;
                }
                String[] infos = status.split(",");
                if ((infos == null)
                        || (infos.length < 3)) {
                    continue;
                }
                // 判断是否接收到all
                if (infos[0].toLowerCase(Locale.getDefault()).startsWith("all")) {
                    hasall = true;
                }
                MemberInfo info = new MemberInfo();
                info.setNumber(infos[0]);
                info.setName(infos[1]);
                info.setStatus(Integer.parseInt(infos[2]));
                // 是否为当前登录用户
                if (sipUser.getUsername().equals(infos[0])) {
                    selfInfo = info;
                }
                memberinfos.add(info);
            }
            if (memberinfos.size() <= 0) {
                return;
            }
            if (hasall) {
                // 注册状态变更通知接收到all，发起重新注册
                Intent intent = new Intent(GlobalConstant.ACTION_REREGISTER);
                sendBroadcast(intent);
            } else {
                // 如果是自己则通知自己的状态
                if (selfInfo != null) {
                    Intent intent = new Intent(GlobalConstant.ACTION_SELFSTATUS);
                    intent.putExtra(GlobalConstant.KEY_CURR_USER, selfInfo);
                    sendBroadcast(intent);
                }
                // 更新成员状态
                GroupManager.getInstance().updateGroupMembersState(memberinfos);
                // 发送广播通知已获取到对讲组成员
                Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
                sendBroadcast(intent);
            }
        } else if ((msgType.contains(GlobalConstant.PTT_MSG_IND_GROUPINFO.toLowerCase(Locale.getDefault())))
                || (msgType.contains(GlobalConstant.PTT_MSG_IND_RE_GROUPINFO.toLowerCase(Locale.getDefault())))) {
            // 对讲组信息
            // 动态重组
            List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
            if ((strMsg.length > 1)
                    && (strMsg[1].indexOf("general-group:") == 0)) {
                Log.e("PTTService", "对讲组：" + strMsg[1]);
                String[] groups = strMsg[1].substring("general-group:".length()).split(";");

                if (groups.length > 0) {
                    for (int i = 0; i < groups.length; i++) {
                        if (groups[i].trim().length() <= 0) {
                            continue;
                        }
                        String[] groupinfos = groups[i].split(",");
                        Log.e("PTTService", groupinfos.length + "类型:" + groupinfos[6]);
                        if (groupinfos.length == 7) {//为7个参数，如果不等于7说明有问题不需要处理
                            Integer num = Integer.parseInt(groupinfos[groupinfos.length - 1]);
                            if (num == 0) {
                                int level = ((groupinfos[2] == null || groupinfos[2].trim().length() <= 0) ? 100 : Integer.parseInt(groupinfos[2]));
                                if (groupinfos.length >= 3) {
                                    GroupInfo groupInfo = new GroupInfo(groupinfos[0], groupinfos[1], level);
                                    groupInfos.add(groupInfo);
                                    Log.w("------------PTTService 690 ---------------", "-----------对讲组信息------------" + groupInfo);
                                }
                            }
                        }
                    }
                }
            }

            if (groupInfos.size() > 0) {
                GroupInfo[] s = new GroupInfo[groupInfos.size()];
                s = groupInfos.toArray(s);
                //GroupManager.getInstance().clear(`); //chw 不清空集合
                GroupManager.getInstance().addGroups(s);

                //发送广播通知已获取到对讲组
                Intent intent = new Intent(GlobalConstant.ACTION_GROUPINFO);
                sendBroadcast(intent);

                // 异步请求对讲组成员
                currgroupindex = 0;
                getMemberInfoThread.setFlag(true);
                new Thread(getMemberInfoThread).start();

                //通知重新加载机构数数据
                Log.e("2019-03-07","通知重新加载机构数数据");
                Intent groupInfoRefreshTressDate = new Intent(GlobalConstant.ACTION_GROUPINFOREFRESH_TRESSDATE);
                sendBroadcast(groupInfoRefreshTressDate);
            }





        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_MEMBERINFO.toLowerCase(Locale.getDefault()))) {
//            Log.e("城北待君归---","**************收到服务器返回组成员信息   sMsgBody : "  + sMsgBody);
//            // 组成员信息
//            if ((strMsg.length > 1)
//                    && (strMsg[1].indexOf("(") > 0)) {
//                // 取得组编号
//                String groupno = strMsg[1].substring(0, strMsg[1].indexOf("("));
//                Log.e("------------PTTService 717 ---------------", "-----------对讲组成员信息------------" + "---组号：" + groupno + "---strMsg[1]:" + strMsg[1]);
//                if (groupno.equals(GlobalConstant.PTT_MSG_REQ_GROUPINFO)) {
//                    return;
//                }
//                List<MemberInfo> memberInfos = new ArrayList<MemberInfo>();
//                String memberinfos = strMsg[1].substring(strMsg[1].indexOf("(") + 1);
//                memberinfos = memberinfos.substring(0, memberinfos.length() - 1);
//                String[] members = memberinfos.split(";");
//                if (members.length > 0) {
//                    for (int i = 0; i < members.length; i++) {
//                        if (members[i].trim().length() <= 0) {
//                            continue;
//                        }
//                        String[] memberinfo = members[i].split(",");
//                        if (memberinfo.length >= 3) {
//                            MemberInfo memberInfo = new MemberInfo(memberinfo[1], memberinfo[0], memberinfo[2]);
//                            memberInfos.add(memberInfo);
//                            //						    Log.w("------------PTTService 679 ---------------", "-----------对讲组成员信息------------" + "组号：" + groupno + "---成员：" + memberInfo);
//                        }
//                    }
//                }
//                Log.e("","lizhiwei:[[" + memberinfos + "]]");
//                if (memberInfos.size() > 0) {
//                    MemberInfo[] s = new MemberInfo[memberInfos.size()];
//                    s = memberInfos.toArray(s);
//                    GroupManager.getInstance().addReceivedMembers(groupno, s);
//
//                    // 发送广播通知已获取到对讲组成员
//                    Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
//                    sendBroadcast(intent);
//                    // 发送更新联系人
//                    Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
//                    sendBroadcast(callIntent);
//                }
//            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_PTT_ACCEPT.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTService" ,"申请成功");
            // 申请话权成功
            // 发送广播通知申请话权成功
            Intent intent = new Intent(GlobalConstant.ACTION_PTTACCEPT);
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_PTT_WAITING.toLowerCase(Locale.getDefault()))) {

//            // 申请话权等待或取消排队成功
//            /*if (strMsg.length < 4) {
//                // 发送广播通知申请话权等待
//                Intent intent = new Intent(GlobalConstant.ACTION_PTTWAITING);
//                intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, strMsg[1].substring("speaker:".length()));
//                intent.putExtra(GlobalConstant.KEY_PTT_QUEUE, strMsg[2].substring("queue:".length()));
//                sendBroadcast(intent);
//            } else {
//                Intent intent = new Intent(GlobalConstant.ACTION_PTTWAITING);
//                intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, strMsg[1].substring("speaker:".length()));
//                intent.putExtra(GlobalConstant.KEY_PTT_QUEUE, strMsg[2].substring("queue:".length()));
//                intent.putExtra(GlobalConstant.KEY_PTT_RELESEQUEUE, strMsg[3].substring("releasequeue:".length()));
//                sendBroadcast(intent);
//                Log.e("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊" ,"申请等待");
//            }*/

        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_PTT_REJECT.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTService" ,"申请拒绝");
//            // 申请话权拒绝
//            // 发送广播通知申请话权拒绝
//            /*Intent intent = new Intent(GlobalConstant.ACTION_PTTREJECT);
//            intent.putExtra(GlobalConstant.KEY_PTT_ERROR, strMsg[1].substring("err:".length()));
//            sendBroadcast(intent);*/

        } else if (msgType.contains(GlobalConstant.PTT_MSG__RSP_PTT_CANCEL)) {
//            // 是否取消话权响应
//            /*Intent intent = new Intent(GlobalConstant.ACTION_PTTCANCEL);
//            intent.putExtra(GlobalConstant.KEY_PTT_CANCEL_RSPCODE, strMsg[1].substring("ret:".length()));
//            sendBroadcast(intent);*/

        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_PTT_CALLINFO.toLowerCase(Locale.getDefault()))) {
//            Log.e(TAG,"收到对讲状态通知 KEY_PTT_GROUP = " + strMsg[1].substring("group:".length()));
//            Log.e(TAG,"收到对讲状态通知 KEY_PTT_SPEAKER = " + strMsg[2].substring("speaker:".length()));
//            Log.e(TAG,"收到对讲状态通知 KEY_PTT_CALLID = " + strMsg[3].substring("call-id:".length()));
//            // 对讲状态通知
//           /* Intent intent = new Intent(GlobalConstant.ACTION_PTTCALLINFO);
//            intent.putExtra(GlobalConstant.KEY_PTT_GROUP, strMsg[1].substring("group:".length()));
//            intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, strMsg[2].substring("speaker:".length()));
//            intent.putExtra(GlobalConstant.KEY_PTT_CALLID, strMsg[3].substring("call-id:".length()));
//            sendBroadcast(intent);*/

        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_CREATECONFE.toLowerCase(Locale.getDefault()))) {
//            /*// 申请创建会议结果返回
//            // 发送广播通知创建会议结果
//            Intent intent = new Intent(GlobalConstant.ACTION_MEETING_CREATERESULT);
//            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));
//            String aa = strMsg[2].toString();
//            if (strMsg[2].startsWith("sessnum:")) {
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
//                intent.putExtra(GlobalConstant.KEY_MEETING_SESSNUM, strMsg[2].substring("sessnum:".length()));
//                intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[3].substring("cid:".length()));
//                Log.e("GlobalConstant.PTT_MSG_RSP_CREATECONFE", "GlobalConstant.PTT_MSG_RSP_CREATECONFE 创建会议成功");
//            } else if (strMsg[2].startsWith("error:")) {
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
//                intent.putExtra(GlobalConstant.KEY_MEETING_ERROR, strMsg[2].substring("error:".length()));
//                intent.putExtra(GlobalConstant.KEY_MEETING_DIS, strMsg[3].substring("dis:".length()));
//                Log.e("GlobalConstant.PTT_MSG_RSP_CREATECONFE", "GlobalConstant.PTT_MSG_RSP_CREATECONFE 创建会议失败");
//            }
//            sendBroadcast(intent);*/


        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_PUSHVIDEORS.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTService","推送视频成功 strMsg.leng"+strMsg.length+" ,strMsg[1].substring(\"sid:\".length()):"+strMsg[1].substring("sid:".length())+" ,strMsg[2].substring(\"dstid:\".length()):"+strMsg[2].substring("dstid:".length()));
            // 推送视频成功
            if (strMsg.length > 2) {
                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_PUSHNUM, strMsg[2].substring("dstid:".length()));
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));
                sendBroadcast(intent);
            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_PUSHVIDEORE.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTService","推送视频失败 strMsg.leng"+strMsg.length+" ,strMsg[1].substring(\"sid:\".length()):"+strMsg[1].substring("sid:".length())+" ,strMsg[2].substring(\"dstid:\".length()):"+strMsg[2].substring("dstid:".length())+" ,strMsg[3].substring(\"dis:\".length():"+strMsg[3].substring("dis:".length())+" ,strMsg[4].substring(\"dstid:\".length()):"+strMsg[4].substring("dstid:".length()));
            // 推送视频失败
            if (strMsg.length > 4) {
                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_ERROR, strMsg[2].substring("errid:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_DIS, strMsg[3].substring("dis:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_PUSHNUM, strMsg[4].substring("dstid:".length()));
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_ERROR, strMsg[2].substring("errid:".length()));
                intent.putExtra(GlobalConstant.KEY_MEETING_DIS, strMsg[3].substring("dis:".length()));
                sendBroadcast(intent);
            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_MEMINFO.toLowerCase(Locale.getDefault()))) {

//            Log.e("测试会议成员","结果返回");
//            // 获取会议成员结果返回
//            if (strMsg.length <= 2) {
//                return;
//            }
//            if (strMsg[2].startsWith("meminfo:")) {
//                Log.w("*********获取会议成员***********", "******获取会议成员*****memberinfos:" + strMsg[2] + "---cid:" + strMsg[1].substring("cid:".length()));
//                // 获取成功
//                /*String[] meminfos = strMsg[2].substring("meminfo:".length()).split(";");
//                Log.e("获取会议成员","获取成功    :");
//                if ((meminfos == null)
//                        || (meminfos.length <= 0)) {
//                    return;
//                }
//                ArrayList<Contacts> contacts = new ArrayList<Contacts>();
//                ArrayList<String> contactsNo = new ArrayList<String>();
//                for (String meminfo : meminfos) {
//                    if ((meminfo == null)
//                            || (meminfo.trim().length() <= 0)) {
//                        continue;
//                    }
//                    String[] infos = meminfo.split(",");
//                    if ((infos == null)
//                            || (infos.length < 4)) {
//                        continue;
//                    }
//                    Log.e("获取会议成员","获取成功   infos[0] :" + infos[0]);
//                    Log.e("测试会议成员","结果返回  成功  number:" + infos[0]);
//                    Contacts contact = new Contacts();
//                    contact.setBuddyNo(infos[0]);
//                    contact.setBuddyName(infos[1]);
//                    contact.setMemberType(Integer.parseInt(infos[2]));
//                    contact.setMeetingState(Integer.parseInt(infos[3]));
//
//                    if (!contactsNo.contains(infos[0])) {
//                        contactsNo.add(infos[0]);
//                        contacts.add(contact);
//                    }
//
//
//                }
//                Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
//                intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));
//                intent.putParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO, contacts);
//                sendBroadcast(intent);*/
//
//            } else if (strMsg[2].startsWith("error:")) {
//                Log.e("获取会议成员","获取失败    :" + strMsg[2]);
//                Log.e("测试会议成员","结果返回  失败   Error：" + strMsg[2]);
//                // 获取失败
//                /*Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
//                intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));
//                intent.putExtra(GlobalConstant.KEY_MEETING_ERROR, strMsg[2].substring("error:".length()));
//                intent.putExtra(GlobalConstant.KEY_MEETING_DIS, strMsg[3].substring("dis:".length()));
//                sendBroadcast(intent);*/
//            }

        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_MSTATECHANGE.toLowerCase(Locale.getDefault()))) {
            SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
            // 自己的状态发生变化
            if (sipUser.getUsername().equals(strMsg[3].substring("employeeid:".length()))) {
                Log.w("PTTService", "*********自己的状态发生变化***********");
                MemberInfo selfInfo = new MemberInfo();
                selfInfo.setNumber(strMsg[3].substring("employeeid:".length()));
                selfInfo.setStatus(Integer.parseInt(strMsg[5].substring("state:".length()) == null || strMsg[5].substring("state:".length()).length() <= 0 ? "0" : strMsg[5].substring("state:".length())));
                Intent intent = new Intent(GlobalConstant.ACTION_SELFSTATUS);
                intent.putExtra(GlobalConstant.KEY_CURR_USER, selfInfo);
                sendBroadcast(intent);

            }
            Log.w("PTTService", "********会议成员状态变化通知***********");
            // 会议成员状态变化通知
            Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MSTATECHANGE);
            intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));
            intent.putExtra(GlobalConstant.KEY_MEETING_NAME, strMsg[2].substring("name:".length()));
            intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[3].substring("employeeid:".length()));
            intent.putExtra(GlobalConstant.KEY_MEETING_TYPE, Integer.parseInt(strMsg[4].substring("type:".length())));
            intent.putExtra(GlobalConstant.KEY_MEETING_STATE, Integer.parseInt(strMsg[5].substring("state:".length()) == null || strMsg[5].substring("state:".length()).length() <= 0 ? "0" : strMsg[5].substring("state:".length())));
            sendBroadcast(intent);

            Log.e("PTTService","收到成员离会信息  PTTService===1259   strMsg[1].substring(\"cid:\".length())："+strMsg[1].substring("cid:".length()) + "strMsg[2].substring(\"name:\".length()):"+strMsg[2].substring("name:".length()) + "strMsg[3].substring(\"employeeid:\".length()):"+strMsg[3].substring("employeeid:".length())+"strMsg[4].substring(\"type:\".length())):"+strMsg[4].substring("type:".length()) + "strMsg[5].substring(\"state:\".length()) == null || strMsg[5].substring(\"state:\".length()).length() <= 0 ? \"0\" : strMsg[5].substring(\"state:\".length()):"+strMsg[5].substring("state:".length()) == null || strMsg[5].substring("state:".length()).length() <= 0 ? "0" : strMsg[5].substring("state:".length()));
            Log.e("PTTService","发送广播通知MeetingAcivity");
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_USERLIST.toLowerCase(Locale.getDefault()))) {
            // 监控获取用户列表结果返回(树形结构)
            if (strMsg.length < 3) {
                return;
            }
            // 截取前缀
            String sid = strMsg[1].substring("sid:".length());
            Log.e("PTTService","" + strMsg[2]);
            String[] userlist = strMsg[2].substring(("list:").length()).split(";");
            if ((userlist == null)
                    || (userlist.length <= 0)) {
                return;
            }
            ArrayList<MonitorInfo> videogroups = new ArrayList<MonitorInfo>();
            for (String user : userlist) {
                if ((user == null)
                        || (user.trim().length() <= 0)) {
                    continue;
                }
                String[] infos = user.split("\\}");
                if ((infos == null)
                        || (infos.length < 2)) {
                    continue;
                }
                // 监控组信息
                String[] userinfos = infos[0].substring(1).split(",");
                if (userinfos.length < 4) {
                    continue;
                }

                // 初始化组信息
                MonitorInfo group = new MonitorInfo();
                group.setId(userinfos[0]);
                group.setNumber((userinfos[1] == null || userinfos[1].trim().length() <= 0) ? userinfos[0] : userinfos[1]);
                group.setName(userinfos[2]);
                group.setType(Integer.parseInt(userinfos[3]));
                group.setChildreninfo("");
                group.setSid(sid);

                // 监控成员信息
                String[] childinfos = infos[1].split("\\]");
                // 初始化成员信息
                if ((childinfos != null)
                        && (childinfos.length > 1)) {

                    group.setUserinfo(childinfos[0].substring(1));
                    group.setChildreninfo(childinfos[1].substring(1));
                }
                // 添加到组列表
                videogroups.add(group);
            }
            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_USERLIST);
            intent.putExtra(GlobalConstant.KEY_MEETING_SID, sid);
            intent.putParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO, videogroups);
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_GETVIDEO.toLowerCase(Locale.getDefault()))) {
//            Log.e("Test监控","PTT_MSG_RSP_GETVIDEO  事件  ");
//            // 监控获取视频返回结果
//            if (strMsg.length < 3) {
//                return;
//            }
//            Log.e("获取会议成员视频画面","1   ACTION_MONITOR_GETVIDEO");
//            if (strMsg[1].startsWith("cid:")) {
//                Log.e("===Sws测试会议===","监控获取视频返回结果====成功");
//                // 成功处理
//                Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_GETVIDEO);
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
//                intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));// 会议唯一标识
//                intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, (strMsg[2].length() > "employeeid:".length() ? strMsg[2].substring("employeeid:".length()) : ""));// 请求用户编号
//                sendBroadcast(intent);
//            } else if (strMsg[1].startsWith("error:")) {
//                // 失败处理
//                Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_GETVIDEO);
//                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
//                intent.putExtra(GlobalConstant.KEY_MONITOR_ERROR, strMsg[1].substring("error:".length()));// 监控获取视频错误号
//                intent.putExtra(GlobalConstant.KEY_MONITOR_DIS, strMsg[2].substring("dis:".length()));// 监控获取视频错误描述
//                intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[3].substring("cid:".length()));// 会议唯一标识
//                intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[4].substring("employeeid:".length()));// 请求用户编号
//                sendBroadcast(intent);
//            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_GETVIDEOEVT.toLowerCase(Locale.getDefault()))) {
//            Log.e("===Sws测试会议===","获取成员视频成功====PTT  strMsg.length:"+strMsg.length);
//            Log.e("获取会议成员视频画面","2   ACTION_MONITOR_GETVIDEOEVT");
//            // 获取视频成功事件
//            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_GETVIDEOEVT);
//            intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));// 会议唯一标识
//            intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[2].substring("dstid:".length()));// 监控号码
//            intent.putExtra(GlobalConstant.KEY_MONITOR_IP, strMsg[3].substring("ip:".length()));// 监控获取到的视频的IP地址
//            intent.putExtra(GlobalConstant.KEY_MONITOR_PORT, strMsg[4].substring("port:".length()));// 监控获取到的视频的端口
//            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VBUG_START_RS.toLowerCase(Locale.getDefault()))) {
            // 开始监控终端视频,请求成功
//            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_START);
//            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
//            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
//            intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[2].substring("cid:".length()));// 会议唯一标识
//            if (strMsg.length > 3) {
//                intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[3].substring("dstid:".length()));// 监控号码
//            }
//            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VBUG_START_RE.toLowerCase(Locale.getDefault()))) {
            // 开始监控终端视频,请求失败
//            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_START);
//            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
//            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
//            intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[2].substring("dstid:".length()));// 监控号码
//            sendBroadcast(intent);
            Log.w("开始监控终端视频,请求失败", "开始监控终端视频,请求失败" + strMsg[2].substring("dstid:".length()));
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VBUG_END_RS.toLowerCase(Locale.getDefault()))) {
            // 结束监控终端视频,请求成功
            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_END);
            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
            intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[2].substring("cid:".length()));// 会议唯一标识
			/*intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[3].substring("dstid:".length()));// 监控号码*/
            sendBroadcast(intent);
            Log.e("PTTService", "结束监控终端视频,请求成功" + strMsg[2].substring("cid:".length()));
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VBUG_END_RE.toLowerCase(Locale.getDefault()))) {
            // 结束监控终端视频,请求失败
            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_END);
            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
            intent.putExtra(GlobalConstant.KEY_MEETING_CID, (strMsg[2] == null || strMsg[2].length() <= 4 ? "" : strMsg[2].substring("cid:".length())));// 会议唯一标识
			/*intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[3].substring("dstid:".length()));// 监控号码*/
            sendBroadcast(intent);
            Log.e("PTTService", "结束监控终端视频,请求失败" + (strMsg[2] == null || strMsg[2].length() <= 4 ? "" : strMsg[2].substring("cid:".length())));
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_VBUG_EVENT.toLowerCase(Locale.getDefault()))) {

            // 监控终端视频成功事件
            Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_EVENT);
            intent.putExtra(GlobalConstant.KEY_MEETING_CID, strMsg[1].substring("cid:".length()));// 会议唯一标识
            intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, (strMsg[2] == null || strMsg[2].length() <= 6 ? "" : strMsg[2].substring("dstid:".length())));// 监控号码
            intent.putExtra(GlobalConstant.KEY_MONITOR_IP, strMsg[3].substring("ip:".length()));// 服务器IP
            intent.putExtra(GlobalConstant.KEY_MONITOR_PORT, strMsg[4].substring("port:".length()));// 服务器端口
            sendBroadcast(intent);
            Log.e("PTTService", "监控终端视频成功事件 Cid:" + strMsg[1].substring("cid:".length())
                    + "\r\n EmployeeID:" + (strMsg[2] == null || strMsg[2].length() <= 6 ? "" : strMsg[2].substring("dstid:".length()))
                    + "\r\n IP:" + strMsg[3].substring("ip:".length())
                    + "\r\n Port:" + strMsg[4].substring("port:".length()));
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VIDEO_RELEAS_RS.toLowerCase(Locale.getDefault()))) {
            // 释放视频请求成功
            Intent intent = new Intent(GlobalConstant.ACTION_VIDEO_RELEASE);
            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VIDEO_RELEAS_RE.toLowerCase(Locale.getDefault()))) {
            // 释放视频请求失败
            Intent intent = new Intent(GlobalConstant.ACTION_VIDEO_RELEASE);
            intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
            intent.putExtra(GlobalConstant.KEY_MEETING_SID, strMsg[1].substring("sid:".length()));// ID透传
            intent.putExtra(GlobalConstant.KEY_MONITOR_ERROR, strMsg[2].substring("errid:".length()));// 监控获取视频错误号
            intent.putExtra(GlobalConstant.KEY_MONITOR_DIS, strMsg[3].substring("dis:".length()));// 监控获取视频错误描述
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_VIDEO_USERS.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4 监控用户(列表结构)(此处已废弃不用)
//            //
//            if ((strMsg.length > 1)
//                    && (strMsg[1].length() > 0)) {
//                String[] videouserinfos = strMsg[1].split(";");
//                if (videouserinfos.length > 0) {
//                    List<VideoUser> videousers = new ArrayList<VideoUser>();
//                    for (String videouserinfo : videouserinfos) {
//                        if ((videouserinfo == null)
//                                || (videouserinfo.trim().length() <= 0)) {
//                            continue;
//                        }
//                        String[] userinfo = videouserinfo.split(",");
//                        if ((userinfo != null)
//                                && (userinfo.length == 7)) {
//                            videousers.add(new VideoUser(userinfo[0], userinfo[1], userinfo[2], userinfo[3], userinfo[4], userinfo[5], userinfo[6]));
//                        }
//                    }
//                    if (videousers.size() > 0) {
//                        VideoUser[] s = new VideoUser[videousers.size()];
//                        s = videousers.toArray(s);
//                        // 发送广播通知获取监控用户
//                        Intent intent = new Intent(GlobalConstant.ACTION_VIDEOUSERS);
//                        intent.putExtra(GlobalConstant.KEY_VIDEO_USER, s);
//                        sendBroadcast(intent);
//                    }
//                }
//            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_UPLOAD.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4 申请上传  此处已废弃
//            //申请上传文件
//            /*Log.e("申请上传", "PTTService =====  ==== = == 申请文件上传:" + strMsg[3]);
//            if (strMsg[3].startsWith("error")) {
//                String dis = strMsg[4];
//                //申请上传失败
//                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADNO);
//                intent.putExtra("dis", dis);
//                sendBroadcast(intent);
//                Log.e("申请上传", "PTTService =====  ==== = == 文件上传失败:" + strMsg[3]);
//            } else {
//                //申请上传成功
//                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADYES);
//                intent.putExtra("filepath", strMsg[3].replace("filepath:", ""));
//                intent.putExtra("fileid", strMsg[4].replace("fileid:", ""));
//                intent.putExtra("filename", strMsg[2].replace("filename", ""));
//                sendBroadcast(intent);
//                Log.e("申请上传", "PTTService =====  ==== = == 文件上传成功:" + strMsg[3]);
//            }*/
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_D_UPLOAD.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4 文件下载通知 此处已废弃
//           /* Log.e("下载", "gengsonghang有新文件通知");
//            int type = Integer.valueOf(strMsg[5].replace("type:", ""));
//            if (type == 1) {
//                //新文件通知
//                Log.e("下载", "gengsonghang对方申请发送文件");
//            } else if (type == 2) {
//                Log.e("下载", "gengsonghang开始下载新文件");
//                for (int i = 0; i < strMsg.length; i++) {
//                    Log.e("Short message file transmission", "PTTService======1469===遍历strMsg数组：" + strMsg[i]);
//                }
//
//                //下载通知
//                String srcnum = strMsg[1].replace("srcnum:", "");
//                String filename = strMsg[2].replace("filename:", "");
//                String filepath = strMsg[3].replace("filepath:", "");
//                String fileid = strMsg[4].replace("fileid:", "");
//                int filetype = Integer.parseInt(strMsg[6].replace("filetype:", ""));
//                String serverpath = filepath;
//                serverpath = serverpath.substring(0, serverpath.lastIndexOf('/') + 1);
//                String downName = filepath.substring(filepath.lastIndexOf('/') + 1, filepath.length());
//                //下载文件
//                DownLoadFile dFile = new DownLoadFile();
//                dFile.setSrcnum(srcnum);
//                dFile.setFilename(filename);
//                dFile.setFilepath(filepath);
//                dFile.setFileid(fileid);
//                dFile.setServerpath(serverpath);
//                dFile.setDownName(downName);
//                dFile.setFileType(filetype);
//                downLoadFileList.add(dFile);
//
//                //开启定时器
//                if (downLoadFileList != null && downLoadFileList.size() > 0) {
//                    if (isRunningDownLoadFile == false) {
//                        startTimer();
//                    }
//                }
//            }*/
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_POS_BY_RECTANGLE.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4   已改为ICE方式获取 在主界面进行
//

//            //获取矩形gis信息
//            Log.e("PTTServiceGIS相关","PTTService====获取矩形gis信息 strMsg[1] : " + strMsg[1]);
//            Intent intent = new Intent(GlobalConstant.ACTION_GIS_RECTANGLEPOS);
//            if ((strMsg.length > 1) && (strMsg[1].length() > 0)) {
//                intent.putExtra("rectangle_pos", strMsg[1]);
//            } else {
//                intent.putExtra("rectangle_pos", "");
//            }
//            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_POS_BY_ELLIPSE.toLowerCase(Locale.getDefault()))) {
            //获取椭圆形gis信息
            Log.e("PTTServiceGIS相关","PTTService====获取椭圆形gis信息");
            Intent intent = new Intent(GlobalConstant.ACTION_GIS_ELLIPSEPOS);
            if ((strMsg.length > 1) && (strMsg[1].length() > 0)) {
                intent.putExtra("ellipse_pos", strMsg[1]);
            } else {
                intent.putExtra("ellipse_pos", "");
            }
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_POS_BY_TIME.toLowerCase(Locale.getDefault()))) {
//            //获取历史位置信息
//            Log.e("====Sws测试定位","PTTService====获取历史位置信息");
//			Intent intent=new Intent(GlobalConstant.ACTION_GIS_TIMEPOS);
//			if((strMsg.length > 1)&& (strMsg[1].length() > 0)){
//				intent.putExtra("time_pos", strMsg[1]);
//			}else{
//				intent.putExtra("time_pos", "");
//			}
//			sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_CUR_POS.toLowerCase(Locale.getDefault()))) {
            //TODO: 2018/12/4  点击定位界面Item
            Intent intent = new Intent(GlobalConstant.ACTION_GIS_DSTCURPOS);
            if ((strMsg.length > 1) && (strMsg[1].length() > 0)) {
                intent.putExtra("cur_pos", strMsg[1]);
            } else {
                intent.putExtra("cur_pos", "");
            }
            sendBroadcast(intent);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_TRACE_POS_RS.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTService","PTTService====追踪");
           Log.e("PTTServiceGIS相关","if判断结果："+msgType.contains(GlobalConstant.PTT_MSG_IND_RSP_TRACE_POS_RS.toLowerCase(Locale.getDefault())));
            //追踪
            String sid = strMsg[1];
            Log.e("PTTService","sid:"+sid);
            String error = "", dis = "";
            if (strMsg.length > 2) {
                if (strMsg[2] != null && strMsg[3] != null) {
                    error = strMsg[2];
                    Log.e("PTTService","error:"+error);
                    dis = strMsg[3];
                    Log.e("PTTService","dis:"+dis);
                }
            }
            Intent intent = new Intent(GlobalConstant.ACTION_GIS_TRACEPOS);
            intent.putExtra("sid", sid);
            intent.putExtra("error", error);
            intent.putExtra("dis", dis);
            sendBroadcast(intent);
            Log.e("PTTServiceGIS追踪", "追踪某个用户消息，成功或者失败 " + error);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_IND_D_GIS.toLowerCase(Locale.getDefault()))) {
            Log.e("PTTServiceGIS追踪","追踪消息发送后，服务器定时推送的gis消息");
            // TODO: 2018/12/4 追踪消息发送后，服务器定时推送的gis消息
            //追踪消息发送后，服务器定时推送的gis消息
            String employeeid = strMsg[1].replace("employeeid:", "");
            String latitude = strMsg[2].replace("latitude:", "");
            String lontitude = strMsg[3].replace("lontitude:", "");
            String time = strMsg[4].replace("time:", "");
            String gprmc = strMsg[5].replace("gprmc:", "");

            Intent intent = new Intent(GlobalConstant.ACTION_GIS_IND_D_GIS);
            intent.putExtra("employeeid", employeeid);
            intent.putExtra("latitude", latitude);
            intent.putExtra("lontitude", lontitude);
            intent.putExtra("time", time);
            intent.putExtra("gprmc", gprmc);
            sendBroadcast(intent);
            Log.e("PTTService，服务器定时推送的gis消息", "追踪消息发送后，服务器定时推送的gis消息\r\n "
                    + employeeid + " ," + latitude + " ," + lontitude + " ," + time);
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_IND_MSG.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4 文本短信接收 此处已作废
//            //因为与服务器的协议是依\r\n来截取的，如果消息中包含回车pad解析消息会有问题
//            //**************
//            String[] strMsg2;
//            for (int i = 0; i < strMsg.length; i++) {
//                if (!strMsg[i].contains("ind:msg")
//                                && !strMsg[i].contains("msgid:")
//                                && !strMsg[i].contains("body:")
//                                && !strMsg[i].contains("sendid:")
//                                && !strMsg[i].contains("time:")
//                                && !strMsg[i].contains("receiver:")
//                                && !strMsg[i].contains("filelist:")) {
//                    if (strMsg[i - 1].contains("body:")) {
//                        strMsg[i - 1] += "\r\n" + strMsg[i];
//                        strMsg[i] = "";
//                    } else {
//                        strMsg[2] += "\r\n" + strMsg[i];
//                        strMsg[i] = "";
//                    }
//                }
//            }
//            strMsg2 = change(strMsg);
//            //文本短消息接受
//            String msgid = strMsg2[1].replace("msgid:", "");
//            String nbody = strMsg2[2].replace("body:", "");
//            String sendid = strMsg2[3].replace("sendid:", "");
//            String time = strMsg2[4].replace("time:", "");
//            String receiver = strMsg2[5].replace("receiver:", "");
//            String filelist = strMsg2[6].replace("filelist:", "");
//            Log.e("PTTService","短消息返回：sendid:" + sendid + "  time:" + time + "  nbody:" + nbody);
//            Log.e("PTTService", "短消息返回：sendid:" + sendid + "  time:" + time + "  nbody:" + strMsg2[2]);
//            //短消息
//            if (nbody.length() > 0) {
//                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGRECEIVEOK);
//                intent.putExtra("userNo", sendid);
//                intent.putExtra("content", nbody);
//                sendBroadcast(intent);
//
//                MessageNotification.NEWESGNUM++;
//                new MessageNotification(PTTService.this, R.drawable.unread_msg, sendid);
//
//                //判断如果当前存在消息详情界面，且短信发送人不是当前界面联系人 播放短信提示音
//                if (ptt_3g_PadApplication.isExistenceMessageShow()){
//                    if (!ptt_3g_PadApplication.getMessageShowBuddyNo().equals(sendid)){
//                        playRingtone(); //播放短信提示音
//                    }
//                }else {     //判断如果当前不存在消息详情界面，新来短信播放提示音
//                    playRingtone(); //播放短信提示音
//                }
//
//            }

        } else if (msgType.contains(GlobalConstant.PTT_MSG_RSP_GETALLSESSIONS.toLowerCase(Locale.getDefault()))) {
            //获取所有会话信息
            Log.e("PTT_3G_Pad_PTTService","获取所有会话消息");
            String sid = strMsg[1].replace("sid:", "");
            String sessions = strMsg[2].replace("sessions:", "");
            MtcDelegate.log("获取所有会话信息返回：sid:" + sid + "  sessions:" + sessions);
            Log.e("PTTService", "获取所有会话信息返回：sid:" + sid + "  sessions:" + sessions);
            //dealAllSessions(sid,sessions);
            SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
            String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
            if (!"".equals(sessions) && sessions != null) {
                String[] sessionsList = sessions.split(";");
                if (sessionsList != null && sessionsList.length > 0) {
                    for (String sessionString : sessionsList) {
                        if (!"".equals(sessionString) || sessionString != null) {
                            String[] session = sessionString.split(",");
                            if (session != null && session.length > 0) {
                                String cid = session[0].toString();
                                int calltype = Integer.parseInt(session[7].toString());
                                switch (calltype) {
                                    case 1:  //
                                    case 12: //单呼
                                    case 10: //转接
                                    case 5:  //临时会议
                                    case 4:  //广播
                                        //单呼(挂断)(已会议方式挂断,因为不走bye)
                                        if (!uri.contains(sipServer.getServerIp())) {
                                            uri += sipServer.getServerIp();
                                        }
                                        if (uri == null) return;
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append("ind:endconfe");
                                        stringBuffer.append("\r\ncid:");
                                        stringBuffer.append(cid);
                                        stringBuffer.append("\r\n");
                                        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, stringBuffer.toString());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        } else if (msgType.contains(GlobalConstant.PTT_MSG_IND_REG_STATE.toLowerCase(Locale.getDefault()))) {
            // TODO: 2018/12/4 成员上下线通知 此处已作废
            //返回成员注册状态
//            String employeeid = strMsg[1].replace("employeeid:", "");
//            String state = strMsg[2].replace("state:", "");
//            Log.e("返回成员注册状态", "返回成员注册状态：employeeid:" + employeeid + "  state:" + state);
//            int status = Integer.parseInt(state);
//            if (status == 1) {
//                //在线
//                GroupManager.getInstance().updateGroupMemberState(employeeid, 2);
//            } else if (status == 2) {
//                //离线
//                GroupManager.getInstance().updateGroupMemberState(employeeid, 0);
//            }
//            // 发送广播通知已获取到对讲组成员
//            Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
//            sendBroadcast(intent);
//            // 发送更新联系人
//            Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
//            sendBroadcast(callIntent);
        }
    }

    @Override
    public void mtcAnzDataIncoming(int dwTptId, byte[] pcUdpData, int iDataLen)
    {
        String recString = null;
        try {
            recString = new String(pcUdpData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 取得对讲组成员线程
    public class GetMemberInfoThread extends Thread
    {
        private volatile boolean flag;

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            while (flag) {
                Message m = Message.obtain();
                m.arg1 = currgroupindex;
                getMemberInfoHandler.sendMessage(m);
                currgroupindex++;
                try {
                    Thread.currentThread();
                    Thread.sleep(240);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void stopCurrentThread() {
            this.flag = false;
        }
    }

    // 具体请求对讲组成员
    @SuppressLint("HandlerLeak")
    private Handler getMemberInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 < GroupManager.getInstance().getGroupCount()) {
//                SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
//                SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
                GroupManager instance = GroupManager.getInstance();
                try {
//                    String msgBody = "req:memberInfo\r\nemployeeid:" + sipUser.getUsername() + "\r\ngroupnum:" + instance.getGroupData().get(msg.arg1).getNumber() + "\r\n";
//                    String uri = GroupManager.getInstance().getGroupData().get(msg.arg1).getNumber() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();
//                    Log.e("pttservice--获取组成员", "uri:" + uri + ",msgbody:" + msgBody);
//                    MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
                Log.e("PTTService","开始获取 grouoNumber : " + instance.getGroupData().get(msg.arg1).getNumber());
                MediaEngine.GetInstance().ME_GetGroupMember_Async(instance.getGroupData().get(msg.arg1).getNumber(), new MediaEngine.ME_GetGroupMember_CallBack() {
                    @Override
                    public void onCallBack(String groupNumber, MediaEngine.ME_UserInfo[] me_userInfos) {

                        if (groupNumber.equals(GlobalConstant.PTT_MSG_REQ_GROUPINFO)) {
                            return;
                        }
                        List<MemberInfo> memberInfos = new ArrayList<MemberInfo>();
                        Log.e("PTTService","获取成功 me_userInfos.length : " + me_userInfos.length);
                        for (MediaEngine.ME_UserInfo userInfo :me_userInfos){
                            MemberInfo memberInfo = null;
                           if (userInfo.isLogin){
                                memberInfo = new MemberInfo(userInfo.userName,userInfo.prefix + userInfo.userId,2);
                                memberInfo.setPrefix(userInfo.prefix);
                           }else {
                               //判断当前类型如果为监控类型 则设置为在线状态
                               if (userInfo.userType == GlobalConstant.MONITOR_USERTYPE_MONITOR){
                                   memberInfo = new MemberInfo(userInfo.userName,userInfo.prefix + userInfo.userId,2);
                                   memberInfo.setPrefix(userInfo.prefix);
                               }else {
                                   memberInfo = new MemberInfo(userInfo.userName,userInfo.prefix + userInfo.userId,1);
                                   memberInfo.setPrefix(userInfo.prefix);
                               }
                           }
                            memberInfos.add(memberInfo);

                            Log.e("PTTService","userId : " + userInfo.userId + "\r\n" + "type : " + userInfo.userType + "\r\n" + "isLogin : " + userInfo.isLogin);
                        }

                        if (memberInfos.size() > 0) {
                            MemberInfo[] s = new MemberInfo[memberInfos.size()];
                            s = memberInfos.toArray(s);
                            GroupManager.getInstance().addReceivedMembers(groupNumber, s);

                            // 发送广播通知已获取到对讲组成员
                            Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
                            sendBroadcast(intent);
                            // 发送更新联系人
                            Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
                            sendBroadcast(callIntent);
                        }
                    }
                });

                }catch (Exception e){
                    Log.e("PTTService","exception:" + e);
                }

            } else {
                // 停止发送消息
                if (getMemberInfoThread != null) {
                    getMemberInfoThread.stopCurrentThread();
                }
            }
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //计时开始
            ++timerGoing;
            handler.postDelayed(this, 1000);
            if (timerGoing >= 30) {
                Log.w("*********PTTService 5389***30秒超时******", "***30秒超时网络未连接挂断所有呼叫*****" + timerGoing);
                timerGoing = 0;
                handler.removeCallbacks(runnable);

                for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
                    if (!callingInfo.isHolding()) {
                        // 更新状态
                        Intent callIntent = new Intent(GlobalConstant.ACTION_CALLING_CALLNETCHANGEHANGUP);
                        callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
                        sendBroadcast(callIntent);
                    }
                }
            }
        }
    };

    private static int count = 0;
    //下载文件定时器
    public void startTimer()
    {
        Log.e("PTTService", "PTTService======1787===进入startTimer  ");
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    do {
                        //count ++;
                        //Log.e("******定时器每秒执行一次*****", "定时器每秒执行一次 count: "+String.valueOf(count));
                        //标识线程是否正在运行
                        isRunningDownLoadFile = true;
                       // Log.e("====测试离线短信", "startTimer中 downLoadFileList.size() :" + downLoadFileList.size());
                        //标识文件是否正在下载
                        if (!isDownLoadNow) {
                            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                                Log.e("PTTService", "PTTService 定时器中 downLoadFileList.size(): " + PTTService.downLoadFileList.size());
                                DownLoadFile dFile = downLoadFileList.get(0);
                                String srcnum = dFile.getSrcnum();
                                String filename = dFile.getFilename();
                                String filepath = dFile.getFilepath();
                                String fileid = dFile.getFileid();
                                int filetype = dFile.getFileType();
                                String serverpath = dFile.getServerpath();
                                String downName = dFile.getDownName();

                                // 下载文件状态写入到全局变量
                                String tmpConfig = srcnum + "|" + filename + "|" + filepath + "|" + fileid + "|" + filetype;
                                Log.e("PTTService", "tmpConfig:" + tmpConfig);
                                ptt_3g_PadApplication.setDownConfig(tmpConfig);
                                //标识有文件正在下载
                                isDownLoadNow = true;
                                //http协议   11/01 add  sws  文件下载
                                okHttpDownloadFile(filename, CommonMethod.getMessageFileDownPath(filetype), serverpath, downName, tmpConfig);

                            } else {
                                count ++;
                                Log.e("PTTService", "下载定时器空转" + String.valueOf(count) +"次");
                                Log.e("PTTService", "下载定时器空转" + String.valueOf(count) +"次");
                                if(count >= 3) {
                                    //关闭定时器，防止定时器空转，导致CPU上涨，耗电量增加
                                    stopTimer();
                                    Intent intent = new Intent(GlobalConstant.ACTION_NOTIFICATIONMAINOLDMESSAGERESULT);
                                    intent.putExtra("fileUploadSuccessCount",String.valueOf(fileUploadSuccessCount));
                                    intent.putExtra("fileUploadErrorCount",String.valueOf(fileUploadErrorCount));
                                    sendBroadcast(intent);
                                    fileUploadSuccessCount = 0;
                                    fileUploadErrorCount = 0;
                                }
                            }
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {

                        }
                    } while (!isPauseDown);
                }
            };
        }

        if (mTimer != null && mTimerTask != null) {
            try {
                mTimer.schedule(mTimerTask, 1000, 1000);

            }catch (Exception e){
                Log.e("PTTService","1763 ：" + e.getMessage());
            }
        }
    }

    public static void stopTimer()
    {
        Log.e("PTTService","stoptimer已执行");
        //循环不再继续
        isPauseDown = true;
        isRunningDownLoadFile = false;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        count = 0;
    }

    //pjsip sdk 事件注册
    private void mediadeleteRegist()
    {

        //文本短信接收
        MediaEngine.GetInstance().ME_SetOnMessageListener(new MediaEngine.ME_OnMessageListener() {
            @Override
            public void onMessage(MediaEngine.ME_Message me_message) {
//                //因为与服务器的协议是依\r\n来截取的，如果消息中包含回车pad解析消息会有问题
//                //**************
//                String[] strMsg2;
//                for (int i = 0; i < strMsg.length; i++) {
//                    if (!strMsg[i].contains("ind:msg")
//                            && !strMsg[i].contains("msgid:")
//                            && !strMsg[i].contains("body:")
//                            && !strMsg[i].contains("sendid:")
//                            && !strMsg[i].contains("time:")
//                            && !strMsg[i].contains("receiver:")
//                            && !strMsg[i].contains("filelist:")) {
//                        if (strMsg[i - 1].contains("body:")) {
//                            strMsg[i - 1] += "\r\n" + strMsg[i];
//                            strMsg[i] = "";
//                        } else {
//                            strMsg[2] += "\r\n" + strMsg[i];
//                            strMsg[i] = "";
//                        }
//                    }
//                }
//                strMsg2 = change(strMsg);
//                //文本短消息接受
//                String msgid = strMsg2[1].replace("msgid:", "");
//                String nbody = strMsg2[2].replace("body:", "");
//                String sendid = strMsg2[3].replace("sendid:", "");
//                String time = strMsg2[4].replace("time:", "");
//                String receiver = strMsg2[5].replace("receiver:", "");
//                String filelist = strMsg2[6].replace("filelist:", "");
//                Log.e("PTTService","短消息返回：sendid:" + sendid + "  time:" + time + "  nbody:" + nbody);
//                Log.e("PTTService", "短消息返回：sendid:" + sendid + "  time:" + time + "  nbody:" + strMsg2[2]);
                //短消息
                if (me_message.body.length() > 0) {
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGRECEIVEOK);
                    intent.putExtra("userNo", me_message.sender);
                    intent.putExtra("content", me_message.body);
                    sendBroadcast(intent);

                    MessageNotification.NEWESGNUM++;
                    new MessageNotification(PTTService.this, R.drawable.unread_msg, me_message.sender);
                    MediaEngine.GetInstance().ME_SetMessageReceived(me_message.msgid,sipUser.getUsername());

                    //当前存在通话不播放提示音
                    if (CallingManager.getInstance().getCallingCount() <= 0){
                        //判断如果当前存在消息详情界面，且短信发送人不是当前界面联系人 播放短信提示音
                        if (ptt_3g_PadApplication.isExistenceMessageShow()){
                            if (!ptt_3g_PadApplication.getMessageShowBuddyNo().equals(me_message.sender)){
                                playRingtone(); //播放短信提示音
                            }
                        }else {     //判断如果当前不存在消息详情界面，新来短信播放提示音
                            playRingtone(); //播放短信提示音
                        }
                    }
                }
            }
        });


        //消息回调
        MediaEngine.GetInstance().ME_SetCallBack(new MediaEngine.ME_CallBack() {


            // TODO -   通话状态
            @Override
            public void onCallState(int callId, String number, int state)
            {
                // TODO Auto-generated method stub
                Log.e("onCallState", "呼叫返回" + callId + "," + number + "," + state);

                Message message = Message.obtain();

                message.arg1 = callId;

                message.obj = number;

                message.arg2 = state;

                handlerS.sendMessage(message);

                //下面内容都以转到Handler中
                if (MediaEngine.ME_CallState.STATE_DISCONNECTED == state) {
                    //呼出挂断
                    Log.e("PTTService mtcCallDelegateTermed 挂断", "PTTService mtcCallDelegateTermed 挂断");
                    Log.e("---------创建视频会议2222---------","收到挂断消息 ");
                    MtcCallDelegate.getCallback().mtcCallDelegateTermed(callId, state);
                }
            }


            // TODO -   新来电
            @Override
            public void onIncomingCall(int callId, String number, int state, boolean isVideo, MediaEngine.ME_IdsPara me_idsPara)
            {

                boolean isSingle = true;

                Log.e("MoveYourBaby","PTTService*****Incoming**********");
                IncomingIntercom incomingIntercom = null;
                Intent mainIntent = null;
                IncomingSingle incomingSingle = null;
                IncomingMeeting incomingMeeting = null;
                Log.e("onIncomingCall", "callId : " + callId);  //0,1,2,3
                Log.e("onIncomingCall", "number : " + number);      //对方号码
                Log.e("onIncomingCall", "state : " + state);        //呼叫状态
                Log.e("onIncomingCall", "isVideo : " + isVideo);    //来电是否为视频
                Log.e("onIncomingCall", "me_idsPara.getSession() : " + me_idsPara.getSession()); // D1F8993A-0397-4C6E-89F8-8B02B266A90A
                Log.e("onIncomingCall", "me_idsPara.getNumber() : " + me_idsPara.getNumber());   //对方号码
                Log.e("onIncomingCall", "me_idsPara.getType() :  " + me_idsPara.getType());   //对方号码

                if (me_idsPara == null){
                    return;
                }
                //判断来电类型
                switch (me_idsPara.getType()){

                    case GlobalConstant.INCOMINGCALLTYPE_SINGLE: //单呼
                        Log.e("PTTService","单呼");
                        Log.e("PTTService","单呼 session ：" + me_idsPara.getSession());

                        isSingle = false;

                        //开机启动
                             mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                          //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(mainIntent);

                        incomingSingle = new IncomingSingle();
                        incomingSingle.setCallId(callId);
                        incomingSingle.setState(state);
                        incomingSingle.setVideo(isVideo);
                        incomingSingle.setNumber(number);
                        incomingSingle.setMe_idsPara(me_idsPara);
                        EventBus.getDefault().post(incomingSingle);

                        if (!CommonMethod.hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }

                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_SINGLE2: //单呼
                        Log.e("PTTService","单呼");
                        Log.e("PTTService","单呼 session ：" + me_idsPara.getSession());

                        isSingle = false;

                        //开机启动
                           mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                          //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(mainIntent);

                        incomingSingle = new IncomingSingle();
                        incomingSingle.setCallId(callId);
                        incomingSingle.setState(state);
                        incomingSingle.setVideo(isVideo);
                        incomingSingle.setNumber(number);
                        incomingSingle.setMe_idsPara(me_idsPara);
                        EventBus.getDefault().post(incomingSingle);

                        if (!CommonMethod.hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_INTERCOM: //对讲
                        Log.e("PTTService","PTTService*****Incoming***********type为对讲***  callId : " + callId + "\r\n" + "number : " + number);
                        Log.e("PTTService","对讲");
                        //开机启动
                        mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                        //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);

                        isSingle = false;

                        incomingIntercom = new IncomingIntercom();
                        incomingIntercom.setCallId(callId);
                        incomingIntercom.setState(state);
                        incomingIntercom.setTempintercom(false);
                        incomingIntercom.setNumber(number);
                        incomingIntercom.setMe_idsPara(me_idsPara);
                        EventBus.getDefault().post(incomingIntercom);

                        if (!CommonMethod.hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_BROADCAST: //广播

                        Log.e("PTTService","广播");

                        isSingle = false;
                        String numberContent = null;
                        if (me_idsPara.getNumber().contains(",")){
                            String[] split = me_idsPara.getNumber().split(",");
                            numberContent = split[0];
                        }else {
                            numberContent = me_idsPara.getNumber();
                        }


                        //收到广播判断是否为自己发起
                        //按会议逻辑处理
                        //收到会议邀请 （视为单呼）
                        if (!numberContent.equals(sipUser.getUsername())
                                && !numberContent.equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())){
                            //开机启动
                            mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                            //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);
                            incomingSingle = new IncomingSingle();
                            incomingSingle.setCallId(callId);
                            incomingSingle.setState(state);
                            incomingSingle.setVideo(isVideo);
                            incomingSingle.setNumber(number);
                            incomingSingle.setMe_idsPara(me_idsPara);
                            EventBus.getDefault().post(incomingSingle);
                            Log.e("PTTService","会议 1813");
                            me_idsPara.setType(GlobalConstant.INCOMINGCALLTYPE_SINGLE);

                        }else {
                            incomingMeeting = new IncomingMeeting();
                            incomingMeeting.setCallId(callId);
                            incomingMeeting.setState(state);
                            incomingMeeting.setVideo(isVideo);
                            incomingMeeting.setNumber(number);
                            incomingMeeting.setMe_idsPara(me_idsPara);
                            incomingMeeting.setMCU(false);
                            EventBus.getDefault().post(incomingMeeting);
                            Log.e("PTTService","会议 1823");
                        }
                        if (!hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_TEMPORARY: //会议
                        Log.e("MoveYourBaby","会议");
                        Log.e("MoveYourBaby","会议 session ：" + me_idsPara.getSession());
                        Log.e("MoveYourBaby","会议 sipUser.getUsername() ：" + sipUser.getUsername());
                        isSingle = false;
                        String numberMeeting = null;
                        if (me_idsPara.getNumber().contains(",")){
                            String[] split = me_idsPara.getNumber().split(",");
                            numberMeeting = split[0];
                        }else {
                            numberMeeting = me_idsPara.getNumber();
                        }


                        //收到会议邀请 （视为单呼）
                        if (!numberMeeting.equals(sipUser.getUsername())
                                && !numberMeeting.equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())){
                            //开机启动
                            mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                            //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);


                            incomingSingle = new IncomingSingle();
                            incomingSingle.setCallId(callId);
                            incomingSingle.setState(state);
                            incomingSingle.setVideo(isVideo);
                            incomingSingle.setNumber(number);
                            incomingSingle.setMe_idsPara(me_idsPara);
                            EventBus.getDefault().post(incomingSingle);
                            Log.e("PTTService","会议 1813");
                            me_idsPara.setType(GlobalConstant.INCOMINGCALLTYPE_SINGLE);

                        }else {
                            incomingMeeting = new IncomingMeeting();
                            incomingMeeting.setCallId(callId);
                            incomingMeeting.setState(state);
                            incomingMeeting.setVideo(isVideo);
                            incomingMeeting.setNumber(number);
                            incomingMeeting.setMe_idsPara(me_idsPara);
                            incomingMeeting.setMCU(false);
                            EventBus.getDefault().post(incomingMeeting);
                            Log.e("PTTService","会议 1823");
                        }

                        if (!hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_TMPINTERCOM: //临时对讲

                        Log.e("PTTService","临时对讲");
                        isSingle = false;
                        incomingIntercom = new IncomingIntercom();
                        incomingIntercom.setCallId(callId);
                        incomingIntercom.setState(state);
                        incomingIntercom.setTempintercom(true);
                        incomingIntercom.setNumber(number);
                        incomingIntercom.setMe_idsPara(me_idsPara);
                        EventBus.getDefault().post(incomingIntercom);
                        if (!hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_MCUMEETING: //MCU会议
                        isSingle = false;
                        String numberMeetingMCU = null;
                        if (me_idsPara.getNumber().contains(",")){
                            String[] split = me_idsPara.getNumber().split(",");
                            numberMeetingMCU = split[0];
                        }else {
                            numberMeetingMCU = me_idsPara.getNumber();
                        }

                        //收到会议邀请 （视为单呼）
                        if (!numberMeetingMCU.equals(sipUser.getUsername())
                                && !numberMeetingMCU.equals(ptt_3g_PadApplication.getPrefix() + sipUser.getUsername())){
                            //开机启动
                            mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                            //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);

                            incomingSingle = new IncomingSingle();
                            incomingSingle.setCallId(callId);
                            incomingSingle.setState(state);
                            incomingSingle.setVideo(isVideo);
                            incomingSingle.setNumber(number);
                            incomingSingle.setMe_idsPara(me_idsPara);
                            EventBus.getDefault().post(incomingSingle);

                            me_idsPara.setType(GlobalConstant.INCOMINGCALLTYPE_SINGLE);
                        }else {
                            incomingMeeting = new IncomingMeeting();
                            incomingMeeting.setCallId(callId);
                            incomingMeeting.setState(state);
                            incomingMeeting.setVideo(isVideo);
                            incomingMeeting.setNumber(number);
                            incomingMeeting.setMe_idsPara(me_idsPara);
                            incomingMeeting.setMCU(true);
                            EventBus.getDefault().post(incomingMeeting);
                        }

                        if (!hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;

                    case GlobalConstant.INCOMINGCALLTYPE_VIDEOBUG: //3G监控
                        isSingle = false;
                        Log.e("PTTService","3G监控");
                        if (!hashMap.containsKey(callId)){
                            CommonMethod.hashMap.put(callId,me_idsPara);
                        }
                        break;
                }

                //如果来电不是以上类型 则默认先按照单呼处理，如果有需求在进行修改
                //Sws 2018-09-17 add
                if (isSingle){
                    //开机启动
                    mainIntent = new Intent(PTTService.this.getApplicationContext(), MainActivity.class);
                    //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);

                    incomingSingle = new IncomingSingle();
                    incomingSingle.setCallId(callId);
                    incomingSingle.setState(state);
                    incomingSingle.setVideo(isVideo);
                    incomingSingle.setNumber(number);
                    me_idsPara.setType(GlobalConstant.INCOMINGCALLTYPE_SINGLE);   //视为单呼
                    incomingSingle.setMe_idsPara(me_idsPara);
                    EventBus.getDefault().post(incomingSingle);

                    if (!CommonMethod.hashMap.containsKey(callId)){
                        CommonMethod.hashMap.put(callId,me_idsPara);
                    }
                }

            }

            // TODO -   日志回调
            @Override
            public void onLogWriter(int arg0, String arg1)
            {
                // TODO Auto-generated method stub
                //开启日志输出
                MyLogger.startLog1("MtcCliJNI",arg1);
            }

            // TODO -   对讲组申请话权/释放话权结果
            @Override
            public void onPttReqState(int state, String reason) {

                Log.e("PTTService","onPttReqState  state : " + state);
                Log.e("PTTService","onPttReqState  reason : " + reason);

            }

            // TODO -   对讲申请等待
            @Override
            public void onPttWaiting(String nspeaker, String queue) {
                Log.e("PTTService","onPttWaiting  对讲申请排队回调 申请人 nspeaker : " + nspeaker);
                Log.e("PTTService","onPttWaiting   对讲申请排队回调 排队 queue : " + queue);

                // 发送广播通知申请话权等待
                Intent intent = new Intent(GlobalConstant.ACTION_PTTWAITING);
                intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, nspeaker);
                intent.putExtra(GlobalConstant.KEY_PTT_QUEUE, queue);
                sendBroadcast(intent);
            }

            // TODO -   对讲申请拒绝
            @Override
            public void onPttReject(String error) {
                Log.e("PTTService","onPttReject 申请被拒绝  s : " + error);
                // 申请话权拒绝
                // 发送广播通知申请话权拒绝
                Intent intent = new Intent(GlobalConstant.ACTION_PTTREJECT);
                intent.putExtra(GlobalConstant.KEY_PTT_ERROR, error);
                sendBroadcast(intent);
            }

            // TODO -   组成员注册状态变更回调
            @Override
            public void onMemberRegStateChange(String employeeid, int status)
            {
                //返回成员注册状态
                Log.e("PTTService", "返回成员注册状态：employeeid:" + employeeid + "  state:" + status);
                String state = "";
                if (status == 1) {
                    //在线
                    GroupManager.getInstance().updateGroupMemberState(employeeid, 2);
                    if (PTT_3G_PadApplication.memberStatehashMap.containsKey(employeeid)){
                        MemberInfo memberInfo = PTT_3G_PadApplication.memberStatehashMap.get(employeeid);
                        memberInfo.setStatus(2);
                        PTT_3G_PadApplication.memberStatehashMap.put(employeeid,memberInfo);
                        state = "在线";
                    }
                } else if (status == 2) {
                    //离线
                    GroupManager.getInstance().updateGroupMemberState(employeeid, 0);
                    if (PTT_3G_PadApplication.memberStatehashMap.containsKey(employeeid)){
                       // PTT_3G_PadApplication.memberStatehashMap.put(employeeid,0);
                        MemberInfo memberInfo = PTT_3G_PadApplication.memberStatehashMap.get(employeeid);
                        memberInfo.setStatus(0);
                        PTT_3G_PadApplication.memberStatehashMap.put(employeeid,memberInfo);
                        state = "离线";
                    }
                }
                // 发送广播通知已获取到对讲组成员
                Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
                sendBroadcast(intent);


                Log.e("2019-03-07","收到成员上下线消息 state :" + state + "\r\n" + "userNo:" + employeeid);
                // 发送更新联系人
                Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
                sendBroadcast(callIntent);

                Intent refreshIntent = new Intent(GlobalConstant.ACTION_REFRESH_DATE);
                sendBroadcast(refreshIntent);

                //通知主界面更新Gis图标
                Intent mainIntent = new Intent(GlobalConstant.ACTION_NOTIFICATION_MAIN_GIS_IMG);
                mainIntent.putExtra("employeeid",employeeid);
                mainIntent.putExtra("status",String.valueOf(status));
                sendBroadcast(mainIntent);
            }


            @Override
            public void onError(String s, String s1, String s2)
            {
            }

            private Map<String,SessionCallInfos> sessionCallInfosMap = new HashMap<String, SessionCallInfos>();//保存Session状态
            private Map<String,MemberChangeInfo> memChangeInfoMap = new HashMap<String, MemberChangeInfo>();//保存成员状态

            // TODO -   异步推送事件
            @Override
            public void onReceivePublishMsg(String header, String count)
            {


                if(header == null || count == null) {
                    return;
                }
                if(header.equals("Session.State.Change")) {
//                     /*[{"cid":"4A2BE08A-0CF6-493A-9010-77176B6973B6","callingnum":"1006",
//                        "callingname":"1006","othernum":"10064626","othername":"TI4626","direction":"1",
//                        "state":"10","type":"13","level":"9","isVideo":"1","RoadParameters":"",
//                        "remark":"邀请成员","frontcid1":"A26B5A61-58A0-4705-8112-C2454B2C9A15",
//                        "frontcid2":""}]*/
                    Log.e("MoveYousssssrBaby","PTT异步推送事件 header : " + header);
                    Log.e("MoveYousssssrBaby","PTT异步推送事件 count : " + count);
                     try {
                         JSONArray contentJsonArray = new JSONArray(count);
                         if(contentJsonArray != null)
                             for (int i = 0; i < contentJsonArray.length(); i++) {
                                 JSONObject itemObject = (JSONObject) contentJsonArray.get(i);
                                 String cid = itemObject.getString("cid"); //会话ID
                                 String callingnum = itemObject.getString("callingnum");//号码
                                 String callingname = itemObject.getString("callingname");//名称
                                 String othernum = itemObject.getString("othernum");//对方号码
                                 String othername = itemObject.getString("othername");//对方名称
                                 String direction = itemObject.getString("direction");//呼叫方向：1呼出2呼入
                                 String state = itemObject.getString("state");//CallSate
                                 String type = itemObject.getString("type");//CallType
                                 String level = itemObject.getString("level");//呼叫等级
                                 String isVideo = itemObject.getString("isVideo");//是否视频呼叫
                                 String remark = itemObject.getString("remark");//相关的操作取值有：协商转接,监听，代接，强插，强拆，转接，邀请成员
                                 String frontcid1 = itemObject.getString("frontcid1");//相关的前一个呼叫CID
                                 String frontcid2 = itemObject.getString("frontcid2");//相关的前一个呼叫的CID

                                 int callType = Integer.parseInt(type);
                                 int callState = Integer.parseInt(state);

                                 if(cid == null) continue;
                                 if(!sessionCallInfosMap.containsKey(cid)) {
                                     sessionCallInfosMap.put(cid,new SessionCallInfos());
                                 }
                                 SessionCallInfos callInfo = sessionCallInfosMap.get(cid);
                                 callInfo.setCid(cid);
                                 Log.e("PTTService","解析cid ：" + cid);
                                 callInfo.setCallingnum(callingnum);
                                 callInfo.setCallingname(callingname);
                                 callInfo.setOthernum(othernum);
                                 callInfo.setOthername(othername);
                                 callInfo.setDirection(Integer.parseInt(direction));
                                 callInfo.setCallState(callState);
                                 callInfo.setCallType(callType);
                                 callInfo.setLevel(Integer.parseInt(level));
                                 callInfo.setIsVideo(Integer.parseInt(isVideo));
                                 callInfo.setRemark(remark);

                                 switch (callType) {
                                     case CallTypeNone://无

                                         break;
                                     case CallTypeSingle2:
                                     case CallTypeSingle://单呼

                                         break;
                                     case CallTypeTmpgroup://组呼

                                         break;
                                     case CallTypeReport://通播

                                         break;
                                     case CallTypeBroadcast://广播
                                     case CallTypeMCUMetting://mcu会议
                                     case CallTypeTemporary://会议
                                         if(callState == CallStateCallout) {
                                             EventBus.getDefault().post(callInfo);
                                             Log.e("MoveYousssssrBaby","PTTService 发送EventBus SessionCallInfos 事件");
                                         }
                                         break;
                                     case CallTypeInterpose://强插

                                         break;
                                     case CallTypeForceremove://强拆

                                         break;
                                     case CallTypeMonitor://监听

                                         break;
                                     case CallTypeIntercom://对讲

                                         break;
                                     case CallTypeSwitch://转接

                                         break;
                                     case CallTypeTmpintercom://临时对讲

                                         break;
                                     case CallTypeVideobug://3G监控

                                         break;
                                 }


                                 switch (callState) {
                                     case CallStateNone://无

                                         break;
                                     case CallStateNormal://空闲

                                         break;
                                     case CallStateCallout://呼出

                                         break;
                                     case CallStateIncoming://呼入

                                         break;
                                     case CallStateRinging://回铃

                                         break;
                                     case CallStateConnect://通话

                                         break;
                                     case CallStateHold://保持

                                         break;
                                     case CallStateBusy://忙

                                         break;
                                     case CallStateOffhook://摘机

                                         break;
                                     case CallStateRelease://离会

                                         break;
                                     case CallStateUnspeak://听讲

                                         break;
                                     case CallStateSpeak://讲话

                                         break;
                                     case CallStateQueue://排队

                                         break;
                                     case CallStateUnhold://取消保持

                                         break;
                                     case CallStateZombie://暂离
                                         break;
                                 }
                             }
                     } catch (JSONException e) {

                     }
                } else if(header.equals("Session.Member.Change")) {
//                    /*[{"cid":"4A2BE08A-0CF6-493A-9010-77176B6973B6","employeeid":"1006",
//                            "name":"1006","state":"11","notspeak":0,
//                            "nothear":0,"havevideo":0,"ispush":0,"type":2}]*/
                    try {
//                        JSONArray contentJsonArray = new JSONArray(count);
//                        //JSONArray contentJsonArray = contentJsonObject.getJSONArray(count);
//
//                        JSONObject contentJsonObject = new JSONObject();
//                        JSONArray contentJsonArray = contentJsonObject.getJSONArray(count);
                        JSONArray contentJsonArray = new JSONArray(count);
                        if(contentJsonArray != null) {
                            for(int i = 0; i < contentJsonArray.length(); i++) {
                                JSONObject itemObject = (JSONObject) contentJsonArray.get(i);
                                String cid = itemObject.getString("cid");//会话ID
                                String employeeid = itemObject.getString("employeeid");//号码
                                String name = itemObject.getString("name");//名称
                                String state = itemObject.getString("state");//CallState
                                String notspeak = itemObject.getString("notspeak");//0可讲话，1不可讲话
                                String nothear = itemObject.getString("nothear");//0可听，1不可听
                                String havevideo = itemObject.getString("havevideo");//0可看视频，1不可看
                                String ispush = itemObject.getString("ispush");//0未推送，1被推送
                                String type = itemObject.getString("type");//号码类型见枚举消息类型EmployeeType

                                int callState = Integer.parseInt(state);
                                int empleoyeeType = Integer.parseInt(type);
                                //首先需要判断，Session是否存在，不存在的不处理
                                Log.e("PTTService","" + cid);
                                Log.e("PTTService","" + sessionCallInfosMap.containsKey(cid));
                                if(sessionCallInfosMap.containsKey(cid)) {
                                    SessionCallInfos callInfo = sessionCallInfosMap.get(cid);
                                    if(callInfo != null) {
                                        MemberChangeInfo memberChangeInfo = null;
                                        if(memChangeInfoMap.containsKey(cid)) {
                                            memberChangeInfo = memChangeInfoMap.get(cid);
                                        } else {
                                            memberChangeInfo = new MemberChangeInfo();
                                        }
                                        memberChangeInfo.setCid(cid);
                                        memberChangeInfo.setEmployeeid(employeeid);
                                        memberChangeInfo.setName(name);
                                        memberChangeInfo.setCallState(callState);
                                        memberChangeInfo.setEmployeeType(empleoyeeType);
                                        memberChangeInfo.setNotspeak(Integer.parseInt(notspeak));
                                        memberChangeInfo.setNothear(Integer.parseInt(nothear));
                                        memberChangeInfo.setHavevideo(Integer.parseInt(havevideo));
                                        memberChangeInfo.setIspush(Integer.parseInt(ispush));

                                        Log.e("PTTService"," 来电类型  9为对讲  13为临时对讲：" + callInfo.getCallType());
                                        Log.e("PTTService"," employeeid ：" + employeeid);
                                        switch (callInfo.getCallType()) {

                                            case CallTypeNone://无
                                                //do nothing
                                                break;
                                            case CallTypeSingle2:
                                            case CallTypeSingle://单呼
                                                //do nothing
                                                break;
                                            case CallTypeMCUMetting://mcu会议
                                            case CallTypeBroadcast://广播
                                            case CallTypeTemporary://会议
                                                switch (callState) {

                                                    case CallStateConnect://入会
                                                    case CallStateRelease://离会

                                                        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
                                                        // 自己的状态发生变化
                                                        if (sipUser.getUsername().equals(employeeid)) {
                                                            Log.e("PTTService", "*********自己的状态发生变化***********");
                                                            MemberInfo selfInfo = new MemberInfo();
                                                            selfInfo.setNumber(employeeid);
                                                            selfInfo.setStatus(callState);
                                                            Intent intent = new Intent(GlobalConstant.ACTION_SELFSTATUS);
                                                            intent.putExtra(GlobalConstant.KEY_CURR_USER, selfInfo);
                                                            sendBroadcast(intent);
                                                        }

                                                        Log.e("PTTService", "********会议成员状态变化通知***********");
                                                        // 会议成员状态变化通知
                                                        Intent intent = new Intent(GlobalConstant.ACTION_MEETING_MSTATECHANGE);
                                                        intent.putExtra(GlobalConstant.KEY_MEETING_CID, cid);
                                                        intent.putExtra(GlobalConstant.KEY_MEETING_NAME, name);
                                                        intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID,employeeid);
                                                        intent.putExtra(GlobalConstant.KEY_MEETING_TYPE, empleoyeeType);
                                                        intent.putExtra(GlobalConstant.KEY_MEETING_STATE, callState);
                                                        sendBroadcast(intent);



                                                        Contacts contact = new Contacts();
                                                        contact.setBuddyNo(employeeid);
                                                        contact.setBuddyName(name);
                                                        contact.setMemberType(empleoyeeType);
                                                        contact.setMeetingState(callState);
                                                        //contacts.add(contact);

                                                        hashMap.put(employeeid,contact);

                                                        Set<String> strings = hashMap.keySet();
                                                        ArrayList<Contacts> contacts = new ArrayList<Contacts>();
                                                        for (String key : strings){
                                                            contacts.add(hashMap.get(key));
                                                        }


                                                        Intent intent2 = new Intent(GlobalConstant.ACTION_MEETING_MEMBERRESULT);
                                                        intent2.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                                                        intent2.putExtra(GlobalConstant.KEY_MEETING_CID, cid);
                                                        intent2.putParcelableArrayListExtra(GlobalConstant.KEY_MEETING_MEMINFO, contacts);
                                                        sendBroadcast(intent2);
                                                        Log.e("PTTService","PTTService 界面  发送广播  cid ：" + cid);
                                                        Log.e("PTTService","PTTService 界面  发送广播  contacts ：" + contacts);
                                                        break;
                                                }
                                                break;
                                            case CallTypeIntercom://对讲
                                            case CallTypeTmpintercom://临时对讲

                                                Log.e("PTTService","对讲  callState ：" + callState);
                                                memberChangeInfo.setGroupNo(callInfo.getOthernum());
                                                memberChangeInfo.setGroupName(callInfo.getOthername());
                                                // 必须是呼叫建立了，才能进行下面操作
                                                //if(callInfo.getCallState() == CallStateConnect) {
                                                //if(callInfo.getCallState() == CallStateConnect) {
//                                                    switch (callState) {
//                                                        case CallStateNone://无
//                                                        case CallStateNormal://空闲
//                                                            Log.e("测试对讲状态","空闲");
//                                                            //显示空闲状态,通知UI刷新
//                                                            Intent intent = new Intent(GlobalConstant.ACTION_PTTCALLINFO);
//                                                            intent.putExtra(GlobalConstant.KEY_PTT_GROUP, callInfo.getOthernum());
//                                                            intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, employeeid);
//                                                            intent.putExtra(GlobalConstant.KEY_PTT_CALLID, cid);
//                                                            intent.putExtra(GlobalConstant.KEY_PTT_CALLSTATE, state);
//                                                            sendBroadcast(intent);
//                                                            break;
//                                                        case CallStateUnspeak://听讲
//                                                            Log.e("测试对讲状态","听讲");
//                                                            //显示听讲,通知UI刷新
//                                                            Intent intent1 = new Intent(GlobalConstant.ACTION_PTTCALLINFO);
//                                                            intent1.putExtra(GlobalConstant.KEY_PTT_GROUP, callInfo.getOthernum());
//                                                            intent1.putExtra(GlobalConstant.KEY_PTT_SPEAKER, employeeid);
//                                                            intent1.putExtra(GlobalConstant.KEY_PTT_CALLID, cid);
//                                                            intent1.putExtra(GlobalConstant.KEY_PTT_CALLSTATE, state);
//                                                            sendBroadcast(intent1);
//                                                            break;
//                                                        case CallStateSpeak://讲话
//                                                            Log.e("测试对讲状态","讲话");
//                                                            //显示讲话,通知UI刷新
//                                                            Intent intent2 = new Intent(GlobalConstant.ACTION_PTTCALLINFO);
//                                                            intent2.putExtra(GlobalConstant.KEY_PTT_GROUP, callInfo.getOthernum());
//                                                            intent2.putExtra(GlobalConstant.KEY_PTT_SPEAKER, employeeid);
//                                                            intent2.putExtra(GlobalConstant.KEY_PTT_CALLID, cid);
//                                                            intent2.putExtra(GlobalConstant.KEY_PTT_CALLSTATE, state);
//                                                            sendBroadcast(intent2);
//                                                            break;
//                                                        case CallStateQueue://排队
//                                                            Log.e("测试对讲状态","排队");
//
//                                                            //显示排队,通知UI刷新
//                                                            // 发送广播通知申请话权等待
//                                                            Intent intent3 = new Intent(GlobalConstant.ACTION_PTTWAITING);
//                                                            intent3.putExtra(GlobalConstant.KEY_PTT_SPEAKER, employeeid);
//                                                            intent3.putExtra(GlobalConstant.KEY_PTT_QUEUE, "排队中");
//                                                            intent3.putExtra(GlobalConstant.KEY_PTT_CALLSTATE, state);
//                                                            sendBroadcast(intent3);
//                                                            break;
//                                                    }
                                                break;
                                            case CallTypeVideobug://3G监控
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            }

            // TODO -   对讲状态通知
            @Override
            public void onPttStatus(String groupNumber, String speaker)
            {
                // TODO Auto-generated method stub 对讲组状态推送
                Log.e("PTTService","onPttStatus  对讲状态通知 groupNumber : " + groupNumber + "\r\n" + "speaker : " + speaker);
                //对讲状态通知
                Intent intent = new Intent(GlobalConstant.ACTION_PTTCALLINFO);
                intent.putExtra(GlobalConstant.KEY_PTT_GROUP, groupNumber);
                intent.putExtra(GlobalConstant.KEY_PTT_SPEAKER, speaker);
                //intent.putExtra(GlobalConstant.KEY_PTT_CALLID, strMsg[3].substring("call-id:".length()));
                sendBroadcast(intent);
            }

//			/*@Override   //已经废弃
//						public void onRegState(boolean arg0, String arg1) {
//							// TODO Auto-generated method stub
//						}*/
            @Override
            public void onSendMsgState(int arg0, String arg1)
            {
                // TODO Auto-generated method stub
                Log.e("PTTService", "arg0:" + arg0 + ",arg1:" + arg1);
            }

            // TODO -   系统消息返回
            @Override
            public void onReceiveMsg(String arg0, String arg1, String sMsgBody)
            {
                // TODO Auto-generated method stub
                Log.e("收到短消息onReceiveMsg", "arg0 : " + arg0 + "\r\n" + "arg1 : " + arg1 + "\r\n" + "sMsgBody : " + sMsgBody);

                if (sipUser.getUsername() != null && ptt_3g_PadApplication.getMsgUserNo() != null && ptt_3g_PadApplication.getMsgBody() != null && ptt_3g_PadApplication.getMsgBody().length() > 0) {
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGSENDOK);
                    sendBroadcast(intent);
                }
                mtcCliCbReceiveUserMsgReq(arg0, arg1, sMsgBody);
            }

            // TODO -   注册状态回调
            @Override
            public void onRegState(boolean arg0, int arg1,
                                   String arg2)
            {
                // TODO Auto-generated method stub

                Log.e("onRegState", "onRegState 返回注册状态  :" + arg0 + ":" + arg1);

                if (ptt_3g_PadApplication.getSipUserState() == MtcDelegate.STATE_LOGINED){
                    //如果当前已经登录成功 不需要进行重复处理
                    Log.e("onRegState", "当前已经登录成功 不需要进行重复处理");
                    return;
                }

                if (arg0){
                    if (arg1 == 200){
                        //发送定位广播
                        Intent intent = new Intent(GlobalConstant.ACTION_FORCED_ONLINE);
                        sendBroadcast(intent);

                        //通知主界面更新用户图标状态
                        Intent intent1 = new Intent(GlobalConstant.ACTION_DIDLOGIN);
                        sendBroadcast(intent1);

                        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
                    }
                }

                //返回登录状态
                MtcDelegate.notifyStateChanged(arg0 == true ? 4 : 6, 0);

                Log.e("PTTService", " 注册返回：boolean arg0:" + arg0);
                Log.e("PTTService", " 注册返回：int arg1:" + arg1);
                Log.e("PTTService", " 注册返回：String arg2:" + arg2);
                Intent loginStateIntent = new Intent();
                loginStateIntent.setAction(GlobalConstant.ACTION_LOGINSTATE_Sws);
                loginStateIntent.putExtra("arg0", arg0);
                loginStateIntent.putExtra("arg1", String.valueOf(arg1));
                loginStateIntent.putExtra("arg2", arg2);
                sendBroadcast(loginStateIntent);
                Log.e("PTTService", "广播已经发送");
            }
        });
       //MediaEngine.GetInstance().ME_Trace(true, false, false);
    }

    //2017/11/01 sws add
    //OkHttp下载文件
    public void okHttpDownloadFile(String filename, String downloadLocalPath, String fileServerPath, String downloadName, final String tmpConfig)
    {
        Log.e("PTTService", "PTTService======2031===  进入okHttpDownloadFile   开始下载文件 ");
        //这个是非UI线程回调，不可直接操作UI
        final ProgressListener progressResponseListener = new ProgressListener() {
            @Override
            public void onProgress(String filename, long bytesRead, long contentLength, boolean done) {

                Log.e("PTTService", "bytesRead:" + bytesRead);
                Log.e("PTTService", "contentLength:" + contentLength);
                Log.e("PTTService", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.e("PTTService", (100 * bytesRead) / contentLength + "% done");
                }
            }
        };

        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressResponseListener = new UIProgressListener() {
            @Override
            public void onUIProgress(String filename, long bytesRead, long contentLength, boolean done) {
                Log.i("TAG", "bytesRead:" + bytesRead);
                Log.i("TAG", "contentLength:" + contentLength);
                Log.i("TAG", "done:" + done);
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    Log.i("TAG", (100 * bytesRead) / contentLength + "% done");
                }
                Log.i("TAG", "================================");
                //ui层回调
            }

            @Override
            public void onUIStart(long bytesRead, long contentLength, boolean done) {
                super.onUIStart(bytesRead, contentLength, done);
            }

            @Override
            public void onUIFinish(String filename, long bytesRead, long contentLength, boolean done) {
                super.onUIFinish(filename, bytesRead, contentLength, done);

//                List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                        .getDownLoadFileList();
                if (downLoadFileList != null && downLoadFileList.size() > 0) {
                    downLoadFileList.remove(0);
                }
                PTTService.isDownLoadNow = false;
            }
        };

        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        if (sipServer.getServerIp() == null) {
            Log.e("PTTService", "2644 服务器IP:" + sipServer.getServerIp());
        return;
        }

        //得到配置界面的端口号
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        String port = prefs.getString("settingOtherport", "80");

        String url = "http://" + sipServer.getServerIp() + ":" + port + "/DownloadServlet?fileName=" + downloadName + "&" + "serverPath=" + fileServerPath;
        Log.e("PTTService", " 2653 url:"+url);
        //构造请求
        final Request request1 = new Request.Builder()
                .url(url)
                .build();
        final File file = new File(downloadLocalPath + filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e("PTTService", "onFailure 抛异常    2242  Error:" + e1.getMessage());
//            List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                    .getDownLoadFileList();
            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                downLoadFileList.remove(0);
            }
            PTTService.isDownLoadNow = false;
        }

        //包装Response使其支持进度回调
        ProgressHelper.addProgressResponseListener(okHttpClient, progressResponseListener, filename)
                .newCall(request1).enqueue(new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {

                Log.e("PTTService", "onFailure 下载");
                Log.e("PTTService", "[postBeanExecute]Call===" + arg1.code() + "Response===");
                if (arg1.isSuccessful()) {
                    try {
                        PTTService.isDownLoadNow = true;
                            if (tmpConfig != null) {
 //                               Log.e("zzt", "=====切分之前 tt_3g_PadApplication.getDownConfig():" + ptt_3g_PadApplication.getDownConfig());
 //                               String[] arr = ptt_3g_PadApplication.getDownConfig().split("\\|");
                                String[] arr = tmpConfig.split("\\|");
                                Log.e("PTTService", "   2141  arr长度:" + arr.length);
                                for (int i = 0; i < arr.length; i++){
                                    Log.e("PTTService", "    2141  arr["+i+"] Content:" + arr[i]);
                                }
                                //TODO: 文件接收成功
                                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGUPLOADOK);
                                intent.putExtra("srcnum", arr[0]);
                                intent.putExtra("filename", arr[1]);
                                intent.putExtra("filepath", arr[2]);
                                intent.putExtra("fileid", arr[3]);
                                intent.putExtra("filetype", Integer.valueOf(arr[4]));
                                sendBroadcast(intent);
                                MessageNotification.NEWESGNUM++;
                                new MessageNotification(PTTService.this, R.drawable.unread_msg, arr[0]);

                                //当前存在通话不播放提示音
                                if (CallingManager.getInstance().getCallingCount() <= 0) {
                                    //判断如果当前存在消息详情界面，且短信发送人不是当前界面联系人 播放短信提示音
                                    if (ptt_3g_PadApplication.isExistenceMessageShow()) {
                                        if (!ptt_3g_PadApplication.getMessageShowBuddyNo().equals(arr[0])) {
                                            playRingtone(); //播放短信提示音

                                        }
                                    } else {     //判断如果当前不存在消息详情界面，新来短信播放提示音
                                        playRingtone(); //播放短信提示音
                                    }
                                }
                                // 上报文件事件,通知服务器下载成功1-下载中；2-下载成功
                                SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
/*                                String msgBody = "ind:u_downevt\r\nemployeeid:"
                                        + sipUser.getUsername() + "\r\nfileid:"
                                        + arr[3] + "\r\ntype:2\r\n";
                                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);*/
                                Log.e("PTTService", "PTTService======2052===  okHttpDownloadFile中  发送ME_SendMsg ");

                                //TODO: 设置文件接收完成
                               boolean isCommitSucess = MediaEngine.GetInstance().ME_SetFileReceived(arr[3], sipUser.getUsername());
                                if (isCommitSucess){
                                    Log.e("PTTService", "设置文件接收完成---成功");
                                }else {
                                    Log.e("PTTService", "设置文件接收完成---失败");
                                }

                            }
//                        List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                                .getDownLoadFileList();
                            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                                downLoadFileList.remove(0);
                                Log.e("PTTService", "PTTService======2104===  downLoadFileList.remove(0); ");

                            }
                            PTTService.isDownLoadNow = false;
                           fileUploadSuccessCount ++;
                    } catch (Exception e) {
                        Log.e("PTTService", "onFailure 抛异常    2170  Error:" + e.getMessage());
//                    List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                            .getDownLoadFileList();

                        if (downLoadFileList != null && downLoadFileList.size() > 0) {
                            downLoadFileList.remove(0);
                        }
                        PTTService.isDownLoadNow = false;
                        fileUploadErrorCount ++;
                    }

                    Log.e("PTTService", "onFailure 下载成功");
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        long total = arg1.body().contentLength();
                        //Log.e("PTTService sssssssssssssssssssss OKHTTP", "total------>" + total);
                        if (total == 0){
                            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                                downLoadFileList.remove(0);
                                Log.e("PTTService", " 内容为空-----------");
                            }
                            PTTService.isDownLoadNow = false;
                            return;
                        }


                        long current = 0;
                        is = arg1.body().byteStream();

                        Log.e("PTTService","is:" + is.toString());
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            current += len;
                            fos.write(buf, 0, len);
                            Log.i("PTTService OKHTTP", "current------>" + current);
                        }
                        fos.flush();
                    } catch (IOException e) {
                        Log.w("PTTService OKHTTP", e.toString());
//                        List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                                .getDownLoadFileList();
                        Log.e("PTTService", "onFailure 抛异常    2281 Error:" + e.getMessage());
                        if (downLoadFileList != null && downLoadFileList.size() > 0) {
                            downLoadFileList.remove(0);
                        }
                        PTTService.isDownLoadNow = false;
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }

                            // 发送广播刷新短信列表或对讲页面
//                            Intent messageIncoming = new Intent(GlobalConstant.ACTION_MESSAGE_MSGINCOMMING);
//                            sendBroadcast(messageIncoming);

                        } catch (IOException e) {
                            Log.e("PTTService OKHTTP", e.toString());
//                            List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                                    .getDownLoadFileList();
                            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                                downLoadFileList.remove(0);
                            }
                            PTTService.isDownLoadNow = false;
                            Log.e("PTTService", "onFailure 抛异常    2306 Error:" + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
                     fileUploadErrorCount ++;
                    Log.e("PTTService", "onFailure 下载失败");
                    Log.e("PTTService", "onFailure 下载失败");
//                    List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication
//                            .getDownLoadFileList();
                    if (downLoadFileList != null && downLoadFileList.size() > 0) {
                        downLoadFileList.remove(0);
                    }
                    PTTService.isDownLoadNow = false;
            }
        });
    }


    // TODO -   文件短信下载监听
    @Override
    public void OnReceiveFileStateChanged(MediaEngine.FileMsg fileMsg)
    {
        switch (fileMsg.state){
            case 1:
                Log.e("PTTService", "对方申请发送文件");
                break;
            case 2:
                Log.e("PTTService", "正在下载========");
                //下载通知
                String srcnum = fileMsg.sender;
                String filename = fileMsg.fileName;
                String filepath = fileMsg.filePath;
                String fileid = fileMsg.fileId;
                int filetype = fileMsg.fileType;

                Log.e("PTTService", "接收文件监听， srcnum:"+srcnum+", filename:"+filename+", filePath:"+filepath+", type:"+filetype);
                String serverpath = filepath;
                serverpath = serverpath.substring(0, serverpath.lastIndexOf('/') + 1);
                String downName = filepath.substring(filepath.lastIndexOf('/') + 1, filepath.length());
                //下载文件
                DownLoadFile dFile = new DownLoadFile();
                dFile.setSrcnum(srcnum);
                dFile.setFilename(filename);
                dFile.setFilepath(filepath);
                dFile.setFileid(fileid);
                dFile.setServerpath(serverpath);
                dFile.setDownName(downName);
                dFile.setFileType(filetype);
                downLoadFileList.add(dFile);

                //开启定时器
                if (downLoadFileList != null && downLoadFileList.size() > 0) {
                    if (isRunningDownLoadFile == false) {
                        startTimer();
                    }
                }

                break;
            default:
                break;
        }
    }

//    private int index = 0;
//    //播放系统提示音
//    public void playRingtone()
//    {
//        Log.e("playRingtone","播放 Index : " + (++index));
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
//    }

    /**
     * 获取的是铃声的Uri
     * @param ctx
     * @param type
     * @return
     */
    public static Uri getDefaultRingtoneUri(Context ctx,int type)
    {

        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);
    }

    /**
     * 播放铃声
     */
    public  void playRingtone()
    {
        if (mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(this,
                    getDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
        }
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();

    }
}
