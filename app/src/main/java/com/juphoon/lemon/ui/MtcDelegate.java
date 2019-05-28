package com.juphoon.lemon.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.juphoon.lemon.MtcCli;
import com.juphoon.lemon.MtcCliCfg;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.MtcCliDb;
import com.juphoon.lemon.MtcProf;
import com.juphoon.lemon.MtcProvDb;
import com.juphoon.lemon.callback.MtcCliCb;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import Ice.SocketException;

public class MtcDelegate {

    private static SharedPreferences prefs;
    private static SipUser sipUser;

    public interface Callback {
        public void mtcDelegateStateChanged(int state, int statCode);
        public void mtcDelegateNetChanged(int net, int previousNet);
        
        public void mtcDelegateConnectionChanged();
        
        public void mtcCliCbReceiveUserMsgReq(String sPeerUri,
                                              String sBodyType, String sMsgBody) ;
    }
    
    public static void registerCallback(final Callback c) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
            	if(sCallbacks != null){
                sCallbacks.add(c);
            	}
            } 
        });
    }
    
    public static void unregisterCallback(final Callback c) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
            	if(sCallbacks != null){
                sCallbacks.remove(c);
            	}
            } 
        }); 
    }
    
    private static Context sContext;
    public static final int STATE_EXPIRED = -2;
    public static final int STATE_NOT_SUPPORTED = -1;
    public static final int STATE_NONE = 0;
    public static final int STATE_INIT = 1;
    public static final int STATE_LOGINING = 2;
    public static final int STATE_LOGINFAILED = 3;
    public static final int STATE_LOGINED = 4;
    public static final int STATE_LOGOUTING = 5;
    public static final int STATE_LOGOUTED = 6;
    public static final int STATE_UMSG_REQ = 7;
    public static final int STATE_MSGSENDOK = 8;
    public static final int STATE_MSGSENDFAILED = 9;
    
    public static int getState() {
        return sState;
    }
    
    public static void setState(int state, int statCode) {
    	//if (sState == state) return;
        sState = state;
        notifyStateChanged(state, statCode);
    }

    public static boolean isLogined() {
        return sState >= STATE_LOGINED;
    }

    public static int getNet() {
        return sNet;
    }
    
    public static String getIP() {
        return sIP;
    }
    
    public static boolean isNetChangedWhenLogin() {
        return sNetChangedWhenLogin;
    }
    
    public static void setNetChangedWhenLogin(boolean changed) {
        sNetChangedWhenLogin = changed;
    }
    
    public static void log(String s) {
        //MtcUtil.Mtc_AnyLogInfoStr(sApplicationName, s);
//        System.out.println(s);

        Log.e("MtcDelegate log",s);
    }

    public static boolean init(Context applicationContext, 
            String applicationName, boolean checkLicense) {
    	 if (sState > STATE_NONE){
            return true;
    	 }else if (sState < STATE_NONE){
            return false;
    	 }
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
//        if (Build.CPU_ABI.equals("armeabi-v7a") || Build.CPU_ABI2.equals("armeabi-v7a")) {
////            System.loadLibrary("lemon");
//
//            String[] mVideoRotateArray= applicationContext.getResources().getStringArray(R.array.video_rotate);
//            String rotate = prefs.getString(GlobalConstant.SP_VIDEO_ROTATE, mVideoRotateArray[0]);//
//            Camera.CameraInfo cameraInfo;
//			int iCameraNumbers = Camera.getNumberOfCameras();
//			for (int cameraId = 0; cameraId < iCameraNumbers; cameraId++) {
//				cameraInfo = new Camera.CameraInfo();
//				Camera.getCameraInfo(cameraId, cameraInfo);
//
//				// cameraInfo.facing = CameraInfo.CAMERA_FACING_BACK;
//				// cameraInfo.orientation = 270;
//				cameraInfo.orientation = 0;
//				if (rotate.equals(mVideoRotateArray[0])){
//	            	cameraInfo.orientation = 0;
//	            }else if(rotate.equals(mVideoRotateArray[1])){
//	            	cameraInfo.orientation = 90;
//	            }else if(rotate.equals(mVideoRotateArray[2])){
//	            	cameraInfo.orientation = 180;
//	            }else if(rotate.equals(mVideoRotateArray[3])){
//	            	cameraInfo.orientation = 270;
//	            }
//				System.out.print("facing = " + cameraInfo.facing
//						+ cameraInfo.orientation);
//				com.juphoon.Environment
//						.overrideCameraInfo(cameraId, cameraInfo);
//			}
//
//         //   com.juphoon.Environment.initVideo(applicationContext);
//         //   com.juphoon.Environment.initVoice(applicationContext);
//        } else {
//            sState = STATE_NOT_SUPPORTED;
//            return false;
//        }
        sApplicationContext = applicationContext;
        sApplicationName = applicationName;

        String MTC_DATA_PATH = MtcUtils.getDataDir(applicationContext) + "/" + applicationName;
        
        String MTC_PROFILE_PATH = MTC_DATA_PATH + "/profiles";
        File filepro = new File(MTC_PROFILE_PATH);
        filepro.mkdirs();
        String MTC_LOG_PATH = MTC_DATA_PATH + "/log";
        File file = new File(MTC_DATA_PATH);
        file.mkdirs();
        file = new File(MTC_LOG_PATH);
        file.mkdirs();
        Thread.setDefaultUncaughtExceptionHandler(new MtcCrashHandler(MTC_LOG_PATH, MtcUtils.getAppVersion(applicationContext)));
       // MtcCliCfg.Mtc_CliCfgSetLogDir(MTC_LOG_PATH);

       // MtcCliCfg.Mtc_CliCfgSetContext(applicationContext);
        if (checkLicense)
            configLicense(applicationContext, applicationContext.getFilesDir().getAbsolutePath());

      //  ZpandTimer.init(applicationContext, applicationName);
//        if (MtcCli.Mtc_CliInit(MTC_PROFILE_PATH) != MtcCliConstants.ZOK) {
//            sState = STATE_EXPIRED;
//            return false;
//        }
        
        initCallbacks(applicationContext);                

        try{
        applicationContext.registerReceiver(sConnectivityChangeReceiver, 
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }catch(Exception ex){
        	Log.e("237-241行", ex.getMessage());
        }
        sState = STATE_INIT;
        getNetworkInfo();
        
        return true;
    }
        
    public static String getLoginedUser() {
//        MtcString prof = new MtcString();
//        if (MtcProvDb.Mtc_ProvDbGetExtnParm(LOGINED_USER, prof) == MtcCliConstants.ZOK) {
//            String loginedProf = prof.getValue();
//            if (!TextUtils.isEmpty(loginedProf) && MtcProf.Mtc_ProfExistUser(loginedProf)) {
//                return loginedProf;
//            }
//        }
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

        return sipUser.getUsername();
    }
    
    @SuppressLint("Wakelock")
    public static void acquireWakeLock(long timeout) {
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager) sApplicationContext.getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        } else if (sWakeLock.isHeld()) {
            sWakeLock.release();
        }
        if (timeout < 0) {
            sWakeLock.acquire();
        } else {
            sWakeLock.acquire(timeout);
        }
    }
    
    public static void releaseWakeLock() {
        if (sWakeLock != null && sWakeLock.isHeld()) {
            sWakeLock.release();
        }
    }

    public static void login() {
        int ret = MtcCli.Mtc_CliStart();
        if (ret == MtcCliConstants.ZOK) {
            ret = MtcCli.Mtc_CliLogin(getNet(), getIP());
        }
        if (ret != MtcCliConstants.ZOK) {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    loginFailed(-1);
                }
            });
        }
        
        setState(STATE_LOGINING, 0);
    }

    public static void logout() {
        if (MtcCli.Mtc_CliLogout() != MtcCliConstants.ZOK) {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    logouted(true, -1, -1);
                }
            });
        }
        setState(STATE_LOGOUTING, 0);
    }
    
    private static void initCallbacks(Context applicationContext) {
    	sContext = applicationContext;
        MtcCliCb.registerCallback(new MtcCliCb.Callback() {
            @Override
            public void mtcCliCbServLoginOk() {
                loginOk();
            }

            @Override
            public void mtcCliCbLclLoginOk() {
                loginOk();
            }

            @Override
            public void mtcCliCbLoginFailed(int dwStatCode) {
                loginFailed(dwStatCode);
            }

            @Override
            public void mtcCliCbRefreshOk(boolean bActive, boolean bChanged) {
                refreshOk(bActive, bChanged);
            }

            @Override
            public void mtcCliCbRefreshFailed(boolean bActive, int dwStatCode) {
                refreshFailed(bActive, dwStatCode);
            }

            @Override
            public void mtcCliCbLclLogout() {
                logouted(true, -1, -1);
            }

            @Override
            public void mtcCliCbServLogout(boolean bActive, int dwStatCode,
                    int dwExpires) {
                logouted(bActive, dwStatCode, dwExpires);
            }

			@Override
			public void mtcCliCbSubNetChanged(int netType) {
				
			}

			@Override
			public void mtcCliCbSendUserMsgOk(int iCookie) {
				sendMsgOk();
			}

			@Override
			public void mtcCliCbSendUserMsgFailed(int iCookie) {
				sendMsgFailed();
			}

			@Override
			public void mtcCliCbReceiveUserMsgReq(String sPeerUri,
					String sBodyType, String sMsgBody) {
//				sState = STATE_UMSG_REQ;
//		        Intent intent = new Intent(sContext, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra("PEER_URI", sPeerUri);
//                intent.putExtra("MSG_BODY_TYPE", sBodyType);
//                intent.putExtra("MSG_BODY", sMsgBody);
//                sContext.startActivity(intent);
			}

        });
        
    }

	private static void sendMsgOk() {
        setState(STATE_MSGSENDOK, 0);
    }
	
	private static void sendMsgFailed() {
        setState(STATE_MSGSENDFAILED, 0);
    }
	
	private static void licenseActivated(boolean bActivated) {
		if (bActivated) {
			if (MtcCli.Mtc_CliInit(getProfilePath()) != MtcCliConstants.ZOK) {
				return;
			}
		}
	}

	private static void deviceDeactivated(boolean bDeactivated) {
		if (bDeactivated) {

		}
	}
	
	private static void loginOk() {
        MtcProvDb.Mtc_ProvDbSetExtnParm(LOGINED_USER, MtcProf.Mtc_ProfGetCurUser());
        setState(STATE_LOGINED, 0);
        cancelRelogin();
    }

    private static void loginFailed(final int statCode) {
        setState(STATE_LOGINFAILED, statCode);
    }
    
    private static void refreshOk(boolean active, boolean changed) {
        boolean netChanged = false;
        Log.w("********MtcDelegate 417 refreshOk*********", "***********");
        if (active) {
            if (getState() == STATE_LOGINING) {
                netChanged = isNetChangedWhenLogin();
                setState(STATE_LOGINED, 0);
            }
        }
        if (changed && !netChanged) {
            notifyConnectionChanged();
        }
        cancelRelogin();
    }
    
    private static void refreshFailed(boolean active, int statCode) {
        if (active && getState() == STATE_LOGINING) {
            setState(STATE_LOGOUTED, statCode);
        } 
        startRelogin();
    }

    private static void logouted(boolean active, int statCode, int expires) {
        if (active) {
            MtcProvDb.Mtc_ProvDbSetExtnParm(LOGINED_USER, null);
            setState(STATE_INIT, 0);
        } else {
            startRelogin();
            setState(STATE_LOGOUTED, 0);
        }
    }

    private static void netChanged() {
        cancelRelogin();
        int previousNet = getNet();
        getNetworkInfo();
        MtcCliDb.Mtc_CliDbSetLocalIp(getIP());
        MtcCliDb.Mtc_CliDbApplyAll();
        Log.e("===Sws测试网络","getNet():" + getNet() + "previousNet:" + previousNet);
        notifyNetChanged(getNet(), previousNet);
    }

    private static final int MESSAGE_NET_CHANGED = 8;
    public static Handler sHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_NET_CHANGED:
                    netChanged();
                    break;
            }
        }
    };
    
    private static BroadcastReceiver sConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (sNetInit) {
                if (sHandler.hasMessages(MESSAGE_NET_CHANGED)) {
                    sHandler.removeMessages(MESSAGE_NET_CHANGED);
                }
                sHandler.sendEmptyMessageDelayed(MESSAGE_NET_CHANGED, 2000);
                Log.e("===Sws测试网络", "--------mtcDelegateNetChanged()----------    sNetInit:" + sNetInit);
            } else {
                sNetInit = true;
                Log.e("===Sws测试网络", "--------mtcDelegateNetChanged()----------    sNetInit = true;    446:" );
            }
        }
    };

    private static void startRelogin() {
        if (sReloginReceiver == null) {
            sReloginReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getState()) {
                        case STATE_LOGINFAILED:
                        case STATE_LOGINED:
                        case STATE_LOGOUTED:
                            login();
                            break;
                        default:
                            cancelRelogin();
                            break;
                    }
                }
            };
            sReloginAction = "com.juphoon.relogin_action." + sApplicationName;
            sApplicationContext.registerReceiver(sReloginReceiver, new IntentFilter(sReloginAction));
        }
        AlarmManager am = (AlarmManager) sApplicationContext.getSystemService(Context.ALARM_SERVICE);
        if (sReloginTrigger == 0) {
            sReloginTrigger = 1000;
        } else if (sReloginTrigger < 160000) {
            sReloginTrigger *= 2;
        }
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sReloginTrigger, reloginOperation(sApplicationContext));
    }

    private static void cancelRelogin() {
        if (sReloginTrigger > 0) {
            AlarmManager am = (AlarmManager) sApplicationContext.getSystemService(Context.ALARM_SERVICE);
            am.cancel(reloginOperation(sApplicationContext));
            sReloginTrigger = 0;
        }
    }

    private static PendingIntent reloginOperation(Context context) {
        Intent intent = new Intent(sReloginAction);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private static String sReloginAction;
    private static long sReloginTrigger = 0;
    private static BroadcastReceiver sReloginReceiver;

    private static void configLicense(Context context, String path) {		
    	String licensePath = path + "/license.sign";
        MtcCliCfg.Mtc_CliCfgSetLicenseFileName(licensePath);
        SharedPreferences settings = context.getSharedPreferences(
                sApplicationName, 0);
        String version = MtcUtils.getAppVersion(context);
        if (version.equals(settings.getString("version", ""))) {
            File license = new File(licensePath);
            if (license.exists()) {
                return;
            }
        }
        MtcUtils.saveAssetFile(context, "license.sign", licensePath);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("version", version);
        editor.commit();
	}

    public static void notifyStateChanged(int state, int statCode) {
        for (Callback c : sCallbacks) {
            c.mtcDelegateStateChanged(state, statCode);
        }
    }
    
    private static void notifyNetChanged(int net, int previousNet) {
        for (Callback c : sCallbacks) {
            c.mtcDelegateNetChanged(net, previousNet);
        }
    }
    
    private static void notifyConnectionChanged() {
        for (Callback c : sCallbacks) {
            c.mtcDelegateConnectionChanged();
        }
    }

    private static void getNetworkInfo() {
        int net = MtcCliConstants.MTC_ANET_UNAVAILABLE;
        String ip = "";
        ConnectivityManager cm = (ConnectivityManager) sApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            net = (ni.getType() << 8);
         //   ip = ZpandNet.getLocalIP();
            ip =  getIPAddress(ni);
        }
        sNet = net;
        sIP = ip;
    }
    public static String getIPAddress(NetworkInfo info) {
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (java.net.SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) sApplicationContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            }
        }
        return null;
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getLicensePath() {
		return MTC_LICENSE_PATH;
	}

	public static void setLicensePath(String sLicensePath) {
		MTC_LICENSE_PATH = sLicensePath;
	}

	public static String getProfilePath() {
		return MTC_PROFILE_PATH;
	}

	public static void setProfilePath(String sProfilePath) {
		MTC_PROFILE_PATH = sProfilePath;
	}
    
    
    private static PowerManager.WakeLock sWakeLock;
    private static ArrayList<Callback> sCallbacks = new ArrayList<Callback>();
    private static boolean sNetInit = false;
    private static int sNet;
    private static String sIP;
    private static boolean sNetChangedWhenLogin = false;
    private static int sState;
    static Context sApplicationContext;
    static String sApplicationName;
    private static String MTC_PROFILE_PATH;
	static String MTC_LOG_PATH;
	private static String MTC_LICENSE_PATH;
    
    private static final String LOGINED_USER = "auto_login";
}
