package com.azkj.pad.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.azkj.pad.model.Contents;
import com.azkj.pad.model.DownLoadFile;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.sws.library.logger.Logger;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.juphoon.lemon.ui.MtcCrashHandler;
import com.juphoon.lemon.ui.MtcDelegate;
import com.juphoon.lemon.ui.MtcUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sword.SDK.MediaEngine;


public class PTT_3G_PadApplication extends Application {
	private SharedPreferences prefs;
	public static Application sContext;
	public String outUserNo;//呼叫号码，对讲号码
	public boolean isAddContact;//是否是添加联系人
	public List<String> contactList=new ArrayList<String>();//选中联系人列表
	private int fileType;//文件上传类型
	private String localFilePath;//文件上传本地路径
	private String serverFilePath;//服务器路径
	private String fileId;//服务器文件id
	private boolean isMapRemoved=false;//视频监控，是否把百度地图移除
	private boolean isMessageForward=false;//转发消息选择联系人使用
	private boolean isNetConnection=true;//网络状态
	private String downConfig;
	private int videoPort = 10003;
	public static boolean ifLoginOk= false;
	private static int count = 0;//标识通话计时器
	private static boolean wetherClickCall = false;
	//当前离线地图的CityId
	private int offlineMapCityId = -1;
	//设备支持的摄像头分辨率
	private List<Size> supportedPreviewSizes = null;
	//设备支持的摄像头是否有前置
	private boolean isCameraFront = false;
	//设备支持的摄像头是否有后置
	private boolean isCameraBack = false;
	//设备支持的摄像头是否有UVC
	private boolean isCameraUVC = false;
	//当前UVC对象实体Bean
	private MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo;
	//标识公司名称
	private String companyName = null;
	//标识公司电话
	private String companyPhone = null;
	//标识公司网址
	private String companyWebsite = null;
	private List<Activity> activityList = new ArrayList<Activity>();
	//下载文件列表
	private List<DownLoadFile> downLoadFileList=new ArrayList<DownLoadFile>();
	//当前是否正在下载文件
	private boolean isDownLoadFile=false;

	//多级号码前缀
	private String prefix = "";

	//当前是否存在信息详情界面(默认为不存在)
	private boolean isExistenceMessageShow = false;

	//当前信息详情界面对方号码
	private String messageShowBuddyNo = "-1";

	//当前音量
	private int currentVolume  = 0;

	//用于标识当前地图是否被移除
	private boolean baiduMapisRemove = false;

	//全局AudioManager类型
	private int globalAudioManagerMode = -1;

	//记录当前会议监控分屏数量
	private String currentSpliCount = "One";

	//当前账号所存在机构数据
	private MediaEngine.ME_GroupInfo OrganizationOfMachines = null;

	//标识结构数据是否加载完成
	private boolean isSuccess = false;

	//用于记录当前成员在线状态
	public static HashMap<String,MemberInfo> memberStatehashMap = new HashMap<>();
	//标识当前登录状态
	private int sipUserState = -1;

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public MediaEngine.ME_GroupInfo getOrganizationOfMachines() {
		return OrganizationOfMachines;
	}

	public void setOrganizationOfMachines(MediaEngine.ME_GroupInfo organizationOfMachines) {
		OrganizationOfMachines = organizationOfMachines;
	}


	public int getGlobalAudioManagerMode() {
		return globalAudioManagerMode;
	}

	public void setGlobalAudioManagerMode(int globalAudioManagerMode) {
		Log.e("setGlobalAudioManagerMode","" + globalAudioManagerMode);
		this.globalAudioManagerMode = globalAudioManagerMode;
	}

	public int getCurrentVolume() {
		return currentVolume;
	}

	public void setCurrentVolume(int currentVolume) {
		this.currentVolume = currentVolume;
	}
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getPrefix()
	{
		return prefix;
	}


	public void addActivityList(Activity activity)
	{
		activityList.add(activity);
	}
	
	public List<Activity> getActivityList()
	{
		return activityList; 
	}
	
	public void setCompanyWebsite(String companyWebsite)
	{
		this.companyWebsite = companyWebsite;
	}
	
	public String getCompanyWebsite()
	{
		return companyWebsite;
	}
	
	public void setCompanyPhone(String companyPhone)
	{
		this.companyPhone = companyPhone;
	}
	
	public String getCompanyPhone()
	{
		return companyPhone;
	}
		
	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}
	
	public String getCompanyName()
	{
		return companyName;
	}
	public List<DownLoadFile> getDownLoadFileList()
	{
		return downLoadFileList;
	}
	
	public void setDownLoadFileList(List<DownLoadFile> downLoadFileList)
	{
		this.downLoadFileList = downLoadFileList;
	}
	
	public boolean getIsDownLoadFile()
	{
		return isDownLoadFile;
	}
	
	public void setIsDownLoadFile(boolean flags)
	{
		this.isDownLoadFile = flags;
	}
	public int getOfflineMapCityId()
	{
		return offlineMapCityId;
	}
	
	public void setOfflineMapCityId(int cityId)
	{
		this.offlineMapCityId = cityId;
	}
	
	public boolean isWetherClickCall()
	{
		return wetherClickCall;
	}
	public void setWetherClickCall(boolean wetherClickCall)
	{
		this.wetherClickCall = wetherClickCall;
	}
	
	public int getVideoPort()
	{
		return videoPort;
	}
	public void setVideoPort(int videoPort)
	{
		this.videoPort = videoPort;
	}	
	public String getDownConfig() {
        return downConfig;
    }
    public void setDownConfig(String downConfig) {
        this.downConfig = downConfig;
    }
    public boolean isNetConnection() {
		return isNetConnection;
	}
	public void setNetConnection(boolean isNetConnection) {
		this.isNetConnection = isNetConnection;
	}
	public boolean isMessageForward() {
		return isMessageForward;
	}
	public void setMessageForward(boolean isMessageForward) {
		this.isMessageForward = isMessageForward;
	}
	public boolean isMapRemoved() {
		return isMapRemoved;
	}
	public void setMapRemoved(boolean isMapRemoved) {
		this.isMapRemoved = isMapRemoved;
	}
	//百度地图定位
	public LocationClient mLocationClient = null;
	//百度地图定位
	//短消息
	private String msgUserNo;
	private String msgBody;
	public String getMsgUserNo() {
		return msgUserNo;
	}
	public void setMsgUserNo(String msgUserNo) {
		this.msgUserNo = msgUserNo;
	}
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	public boolean isAddContact() {
		return isAddContact;
	}
	public void setAddContact(boolean isAddContact) {
		this.isAddContact = isAddContact;
	}
	public List<String> getContactList() {
		return contactList;
	}
	public void setContactList(List<String> contactList) {
		this.contactList = contactList;
	}
	public String getOutUserNo() {
		return outUserNo;
	}
	public void setOutUserNo(String outUserNo) {
		this.outUserNo = outUserNo;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public String getLocalFilePath() {
		return localFilePath;
	}
	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}
	public String getServerFilePath() {
		return serverFilePath;
	}
	public void setServerFilePath(String serverFilePath) {
		this.serverFilePath = serverFilePath;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	private String pushVideoNum = "";			
	public String getPushVideoNum() {
		return pushVideoNum;
	}
	public void setPushVideoNum(String pushVideoNum) {
		this.pushVideoNum = pushVideoNum;
	}

	@Override
	public void onCreate() {
		//腾讯bugly相关
		Context context = getApplicationContext();
		// 获取当前包名
		String packageName = context.getPackageName();
		// 获取当前进程名
		String processName = getProcessName(android.os.Process.myPid());
		// 设置是否为上报进程
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
		strategy.setUploadProcess(processName == null || processName.equals(packageName));
		// 初始化Bugly
		CrashReport.initCrashReport(context, "1bbdffb181", true, strategy);
	    // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
	    // CrashReport.initCrashReport(context, strategy);

		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		}
		super.onCreate();
		sContext = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		MtcDelegate.init(this, "PTT_3G_Pad", true);
		// 全局初始化
//        if (MtcDelegate.init(this, "PTT_3G_Pad", true)) {
//            MtcCallDelegate.init(this);
//            MtcCallDelegate.setCallActivityClass(MainActivity.class);
//            String user = MtcDelegate.getLoginedUser();
//            if (user != null) {
//         		//注销
//        		MtcDelegate.logout();
//        		// 停止代理
//                MtcProximity.stop();
//                // 清空呼叫回调
//                if (MtcCallDelegate.getCallback() == this){
//                    MtcCallDelegate.setCallback(null);
//                }
//               //MtcCli.Mtc_CliOpen(user);
//               //MtcDelegate.login();
//           }
//        }
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		
		//上传下载初始化   11/1 sws
		//InstantClient.instance(this.getApplicationContext()).create();//
		
		//设置摄像头角度
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		String[] mVideoRotateArray= this.getResources().getStringArray(R.array.video_rotate);
		String rotate = prefs.getString(GlobalConstant.SP_VIDEO_ROTATE, mVideoRotateArray[0]);//
		Camera.CameraInfo cameraInfo;
        int iCameraNumbers = Camera.getNumberOfCameras();
        for (int cameraId = 0; cameraId < iCameraNumbers; cameraId++) {
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if(cameraInfo.facing == cameraInfo.CAMERA_FACING_BACK)
            {
            	isCameraBack = true;
            }            
            else 
            {
            	isCameraFront = true;
            }
            cameraInfo.orientation = 0;
            if (rotate.equals(mVideoRotateArray[0])){
            	cameraInfo.orientation = 0;
            }else if(rotate.equals(mVideoRotateArray[1])){
            	cameraInfo.orientation = 90;
            }else if(rotate.equals(mVideoRotateArray[2])){
            	cameraInfo.orientation = 180;
            }else if(rotate.equals(mVideoRotateArray[3])){
            	cameraInfo.orientation = 270;
            }
           // com.juphoon.Environment.overrideCameraInfo(cameraId, cameraInfo);
        }
		       
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
       
        try
		{					
	        if(!checkCameraHardware(this))
	    	{
	    		Log.e("没有监测到摄像头设备", "没有监测到摄像头设备");
	    		return;
	    	}
			else 
			{
				//读取摄像头分辨率
				Camera camera = Camera.open();
				Parameters parameters = camera.getParameters();
				camera.unlock();
				supportedPreviewSizes = parameters.getSupportedPreviewSizes();
				//supportedPreviewSizes = parameters.getSupportedVideoSizes();
				//supportedPreviewSizes = parameters.getSupportedPictureSizes();
				camera.release();
				camera.lock();
				camera = null;
			}
	        
	        searchBitrate();
		} catch (Exception e)
		{
			
		}
		initLog();

		initLogger(this);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		setCurrentVolume(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));

		// 取得默认数据
		String audioMode = prefs.getString(GlobalConstant.SP_AUDIOMANAGER_GETMODE,getResources().getString(R.string.text_mode_in_communication));
		if (audioMode.equals(getResources().getString(R.string.text_mode_normal))){
			setGlobalAudioManagerMode(AudioManager.MODE_NORMAL);

		}else if (audioMode.equals(getResources().getString(R.string.text_mode_in_call))){
			setGlobalAudioManagerMode(AudioManager.MODE_IN_CALL);

		}else if (audioMode.equals(getResources().getString(R.string.text_mode_in_communication))){
			setGlobalAudioManagerMode(AudioManager.MODE_IN_COMMUNICATION);

		}


	}
	//捕获日志
	private void initLog() {
		String MTC_DATA_PATH = MtcUtils.getDataDir(this) + "/" + "PTT_3G_Pad";
		String MTC_LOG_PATH = MTC_DATA_PATH + "/log";
		Thread.setDefaultUncaughtExceptionHandler(new MtcCrashHandler(MTC_LOG_PATH, MtcUtils.getAppVersion(this)));
	}

	//获取进程名称
	private static String getProcessName(int pid) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
			String processName = reader.readLine();
			if (!TextUtils.isEmpty(processName)) {
				processName = processName.trim();
			}
			return processName;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}

	private void initLogger(Context context) {
		/**
		 * 初始化
		 * context必须设置
		 */
		Logger.setContext(context);
		/**
		 * 设置Log开关,可根据debug-release配置
		 *  默认为true
		 */
		boolean LogIsOpen = prefs.getBoolean(GlobalConstant.SP_START_LOG, false);
		Log.e("---------sadadwqwdqdqw------","是否开启日志  " + LogIsOpen);
		Logger.setOpen(LogIsOpen);
		/**
		 * 设置Log等级, >= 这个配置的log才会显示
		 * 默认为Log.VERBOSE = 2
		 */
		Logger.setLevel(Log.VERBOSE);
		/**
		 * 设置本地Log日志的存储路径
		 * 默认为sd卡Logger目录下
		 * Environment.getExternalStorageDirectory().getAbsolutePath() + "/Logger/"
		 */

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String date = sdf.format(new Date());

//		Logger.setPath(Environment.getExternalStorageDirectory().toString() + "/Log/");
//		MyLogger.startLog("PTT_3G_PadApplication","出现异常,记录下来,方便查找错误");
	}


	//设备是否有摄像头
	private boolean checkCameraHardware(Context context) 
	{
	  if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			// this device has a camera
			return true;
		} 
		else 
		{
			// no camera on this device
		    return false;
		}
	}
	
	//获取手机摄像头分辨率
	public List<Size> getSupportedPreviewSizesPhone()
	{
		return supportedPreviewSizes;
	}
	
	//是否有前置摄像头
	public boolean getIsCameraFront()
	{
		return isCameraFront;
	}
	
	//是否有后置摄像头
	public boolean getIsCameraBack()
	{
		return isCameraBack;
	}

	public boolean wetherTimerWorking()
	{
		if(count > 0 && count <= 3)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void startTimer()
	{
		handler.post(runnable);
	}
	
	public void endTimer()
	{
		handler.removeCallbacks(runnable);
		count = 0;
		wetherClickCall = false;
	}
	
	public int getCount()
	{
		return count;
	}
	
	static Handler handler = new Handler();

	static Runnable runnable = new Runnable()
	{	
		@Override
		public void run()
		{
			count++;
			if(count >= 3)
			{
				handler.removeCallbacks(runnable);
				count = 0;
				wetherClickCall = false;
			}
			else 
			{
				handler.postDelayed(runnable, 1000);
				Log.e("Timer正在运行", "Timer正在运行"+count);
			}			
		}
	};
	
    private List<Contents> mContentsList = new ArrayList<Contents>();	
	
	public List<Contents> getVideoBitrateList() {
		return mContentsList;
	}	

	public void searchBitrate(){
		Contents mContents = null;		
		XmlResourceParser xml = getResources().getXml(R.xml.videobitrate);
		
		try {
			int eventType = xml.next();
			while (true){
				if (eventType == XmlPullParser.START_DOCUMENT){
					
				}else if (eventType == XmlPullParser.START_TAG){
					if("contents".equals(xml.getName())){
						mContents = new Contents();
					}
					else if("resolution".equals(xml.getName())){
						eventType = xml.next();
						mContents.setResolution(xml.getText());
					}else if("bitrate".equals(xml.getName())){
						eventType = xml.next();
						mContents.setBitrate(Integer.parseInt(xml.getText()));
					}
				}else if (eventType == XmlPullParser.END_TAG){
					if("contents".equals(xml.getName())){
						mContentsList.add(mContents);
						mContents = null;
					}
				}else if (eventType == XmlPullParser.TEXT){
					
				}else if (eventType == XmlPullParser.END_DOCUMENT){
					break;
				}
				eventType = xml.next();
			}
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean isExistenceMessageShow() {
		return isExistenceMessageShow;
	}

	public void setExistenceMessageShow(boolean existenceMessageShow) {
		isExistenceMessageShow = existenceMessageShow;
	}

	public String getMessageShowBuddyNo() {
		return messageShowBuddyNo;
	}

	public void setMessageShowBuddyNo(String messageShowBuddyNo) {
		this.messageShowBuddyNo = messageShowBuddyNo;
	}

	public boolean isBaiduMapisRemove() {
		return baiduMapisRemove;
	}

	public void setBaiduMapisRemove(boolean baiduMapisRemove) {
		this.baiduMapisRemove = baiduMapisRemove;
	}

	public String getCurrentSpliCount() {
		return currentSpliCount;
	}

	public void setCurrentSpliCount(String currentSpliCount) {
		this.currentSpliCount = currentSpliCount;
	}

	public int getSipUserState() {
		return sipUserState;
	}

	public void setSipUserState(int sipUserState) {
		this.sipUserState = sipUserState;
	}

	public boolean isCameraUVC() {
		return isCameraUVC;
	}

	public void setCameraUVC(boolean cameraUVC) {
		isCameraUVC = cameraUVC;
	}

	public MediaEngine.ME_VideoDeviceInfo getMe_videoDeviceInfo() {
		return me_videoDeviceInfo;
	}

	public void setMe_videoDeviceInfo(MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo) {
		this.me_videoDeviceInfo = me_videoDeviceInfo;
	}
}
