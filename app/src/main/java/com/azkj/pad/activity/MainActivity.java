package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.azkj.pad.model.AutoAnswerBena;
import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.model.CallingRecords;
import com.azkj.pad.model.DecoderInfo;
import com.azkj.pad.model.DownLoadFile;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.IncomingIntercom;
import com.azkj.pad.model.IncomingMeeting;
import com.azkj.pad.model.IncomingSingle;
import com.azkj.pad.model.MeetingRecords;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.model.MessageRecords;
import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.model.MonitorInfoBean;
import com.azkj.pad.model.PosMarkerInfo;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.CallingManager;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.service.HeartbeatManager;
import com.azkj.pad.service.PTTService;
import com.azkj.pad.utility.AlertDialogUtils;
import com.azkj.pad.utility.ButtonUtils;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.DecoderBean;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.MessageNotification;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.pad.utility.ThreadPoolProxy;
import com.azkj.pad.utility.ToastUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baizhi.app.MultiPlayer;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juphoon.lemon.MtcCall;
import com.juphoon.lemon.MtcCallConstants;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.MtcString;
import com.juphoon.lemon.MtcUri;
import com.juphoon.lemon.callback.MtcAnzCb;
import com.juphoon.lemon.ui.MtcCallDelegate;
import com.juphoon.lemon.ui.MtcDelegate;
import com.juphoon.lemon.ui.MtcProximity;
import com.juphoon.lemon.ui.MtcRing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import Dispatcher.GisInfoByTimeT;
import Dispatcher.Identity;
import Ice.Communicator;
import Ice.ObjectPrx;
import cloud.Monitor.VideoSender.IVideoSenderCallback;
import cloud.Monitor.VideoSender.ServerCommand;
import cn.sword.SDK.MediaEngine;
import cn.sword.SDK.MediaEngine.ME_DecoderDisplayInfo;
import cn.sword.SDK.MediaEngine.ME_DecoderInfo;
import cn.sword.SDK.MediaEngine.ME_GetDecoderChannelState_CallBack;
import cn.sword.SDK.MediaEngine.ME_StartDecode_CallBack;
import cn.sword.SDK.MediaEngine.ME_StopDecode_CallBack;

import static com.azkj.pad.activity.MeetingActivity.currMeeting;
import static com.azkj.pad.activity.PTT_3G_PadApplication.memberStatehashMap;
import static com.azkj.pad.activity.R.drawable.intercom;
import static com.azkj.pad.activity.R.drawable.location;
import static com.azkj.pad.activity.R.drawable.monitor;
import static com.azkj.pad.activity.R.id.voice_Downlink_meeting;
import static com.azkj.pad.service.PTTService.isRunningDownLoadFile;
import static com.azkj.pad.utility.GlobalConstant.ACTION_NOTIFICATIONMAINOLDMESSAGERESULT;
import static com.azkj.pad.utility.GlobalConstant.KEY_CALL_CHANGEVIEW;


@SuppressWarnings("deprecation")
@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class MainActivity extends TabActivity implements OnItemClickListener,
        MtcDelegate.Callback, MtcCallDelegate.Callback, IVideoSenderCallback,
        OnMapLoadedCallback, OnMapStatusChangeListener, MKOfflineMapListener {


    private String TAG = "MainActivity";
    //注释
    private long jslatitude;
    private long jslontitude;
    // 全局变量定义
    private SharedPreferences prefs;
    // 对讲组优先级
    private String[] mIntercomPriorityArray;
    private String highvalue = "";
    private String samevalue = "";
    private String lowvalue = "";
    @SuppressWarnings("unused")
    private String defaultgroup = "";
    //解码上墙相关
    private int position = 0;
    //private String decoderId = null;
    private List<ME_DecoderDisplayInfo> decoderDisplayChans = null;
    private String chanidString = null;
    //private String sessionid = null;
    private ME_DecoderDisplayInfo info;
    List<String> list_decoderchannel = new ArrayList<String>();
    // 蓝牙设备(话音传输)处理
    //private BluetoothHelper mBluetoothHelper;
    // 耳机插入接收器
    //private HeadsetPlug mHeadsetPlug;
    // 会话编号
    private int mSessId = MtcCliConstants.INVALIDID;
    // 当前通话状态
    private int mCallState;
    // 是否延迟
    private boolean mIsDelayOffer;
    // 全局变量保存号码
    private PTT_3G_PadApplication ptt_3g_PadApplication;
    private SipServer sipServer;
    private SipUser sipUser;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    public MainLocationListenner myListener = new MainLocationListenner();
    private LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;
    private boolean isFirstLoc = true;// 是否首次定位
    private UiSettings mUiSettings;
    private LinearLayout singleLinearLayout, multipleLinearLayout,
            filterLinearLayout, trackLinearLayout, traceLinearLayout,
            cleanLinearLayout;
    private TextView btn_clean, btn_trace, btn_track, btn_filter, btn_multiple,
            btn_single;
    //选择多个成员时显示，广播，会议，对讲，消息
    private TextView btnPosBroadcast, btnPosMeeting, btnPosTalk, btnPosMessage;

    private HashMap<String, DecoderBean> decodeMap = new HashMap<>();

    private HashMap<String, String> monitorsSessionID = new HashMap<>();


    //选择一个成员时显示，音频通话，视频通话，消息
    private TextView btnPosOnVoice, btnPosOnVideo, btnPosOnMessage;
    private List<PosMarkerInfo> selPosMarkerInfos = new ArrayList<PosMarkerInfo>();// 选中
    private int posSelType = 1;// 选中模式(1单选2点选3全选4圈选5框选)

    private boolean isCheck = false;

    // 地图窗口
    private PopupWindow multiplePopupWindow, filterPopupWindow,
            tracePopupWindow, trackPopupWindow;// 点击显示菜单
    // 多选
    private TextView btnSelClick, btnMultipleAll;
    //多选显示成员列表
    private ImageButton imgMultip;
    //加载成员列表容器
    private LinearLayout employeeLayout;

    private ArrayList<PosMarkerInfo> checkBoxMarker = new ArrayList<>();

    //private ListView employeeListView;
    // 筛选
    @SuppressWarnings("unused")
    private CheckBox chb3G, chbSip, chboxMonitor, chboxDispacher;
    private Spinner spRange;
    private TextView btnSearch;
    private double myLatitude = 0, myLongitude = 0;
    View mItemView;
    // 追踪
    private TextView btnClean;
    private ListView addUserNoListView;
    private List<String> addUserNoList;
    @SuppressWarnings("unused")
    private Button btnAddNo, btnDelete;
    private EditText editUserNo;
    private PosTraceAdapter posTraceAdapter;
    private boolean isTrace = false;// 是否追踪
    private boolean istrac = false;//表示是否为追踪
    // 轨迹
    private EditText trackBeginDate, trackEndDate, trackBeginTime, trackEndTime;
    private ListView trackListView;
    private TextView btnTrackClean, btnTrackSearch;
    private Button btnTrackAddNo;
    private EditText editTrackUserNo;
    private List<PosMarkerInfo> posMarkerInfosList = null;// 地图标记列表
    private List<PosMarkerInfo> posMarkerInfosTraceList = null;// 轨迹标记列表
    private RelativeLayout ll_trackplay;// 播放轨迹区域
    private TextView txtPosCurrHour, txtPosCurrHour_day, txtPosCurrMinute, txtPosCurrSecond,
            txtPosSpeed1, txtPosSpeed2, txtPosSpeed3, btnPosTrackPause,
            btnPosTrackExit, txtPosProgress;
    private int posPlaySpeedType = 1;// 播放速度(1.默认速度2.2倍速度3.3倍速度)
    private ProgressBar proPosProgress;
    private boolean isPosPlaying = false;// 是否播放轨迹
    private boolean isPosFirstPlaying = true;// 是否是第一次启动，用于播放/暂停按钮控制
    private NewPosTracePlayThread posTracePlayThread;// 播放轨迹线程
    private HashMap<String, List<PosMarkerInfo>> posHashMap = null;//chw 保存追踪的返回的数据
    private Map<String, List<PosMarkerInfo>> trackPosMarkerInfoMap = null;//保存轨迹回放返回的数据
    int count = 1;
    private int[] colorIntList = new int[]{Color.RED, Color.BLACK,
            Color.BLUE, Color.GREEN,
            Color.YELLOW, Color.CYAN,
            Color.DKGRAY, Color.GRAY,
            Color.LTGRAY, Color.MAGENTA
    };
    private HashMap<String, Integer> colorLineLists = new HashMap<String, Integer>();


    private int fileCount = 0;

    private int widthGJ = 0;
    private int heightGJ = 0;

    private StringBuffer sb = new StringBuffer();
    //    private MarqueeTextView tv_TongjiInfo;
//    private MarqueeTextView tv_TongjiInfo2;
    //标识是否可以继续写入
    private boolean isTextSuccess = true;

    //Toast内容
    private String toastContent = null;
    // 用户状态
    private ImageView imgUserStates;
    // 表头显示
    private TextView txtUserNO, txtTitle, txtCurrHour, txtTimeSeparator1,
            txtCurrMinutes, txtTimeSeparator2, txtCurrSeconds, txtCurrDate;
    // 用户显示时间
    private Handler handler = new Handler();
    // 用户显示时间
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            // 一秒钟一次
            handler.postDelayed(updateThread, 1000);
            Calendar calendar = Calendar.getInstance();
            txtCurrHour.setText(String.format("%02d",
                    calendar.get(Calendar.HOUR_OF_DAY)));
            txtCurrMinutes.setText(String.format("%02d",
                    calendar.get(Calendar.MINUTE)));
            txtCurrSeconds.setText(String.format("%02d",
                    calendar.get(Calendar.SECOND)));
            txtCurrDate.setText(CommonMethod.getCurrShortCNDate(calendar));

        }
    };
    // 总画面
    private TabHost mTabHost;
    // 标签页面
    private FrameLayout tabcontent;
    // 标题控件
    private GridView mGridView;
    // 标题图标
    private int[] mImageNormalIds;
    // 标题选中图标
    private int[] mImageSelectedIds;
    // 标题适配器
    private ImageAdapter mImageAdapter;
    // 标签页
    private Intent mIntercomIntent, mCallingIntent, mMeetingIntent,
            mMessageIntent, mContactIntent, mPositionIntent, mMonitorIntent,
            mSettingIntent;
    // 当前分页是否显示
    private Boolean currTabVisible = true;
    //是否继续
    private boolean result = false;
    // 显示类型枚举
    /*public enum ShowType {
        ALL, ONLYTAB, ONLYCONTENT
	}*/
    // 内容畫面
    private RelativeLayout rl_content;
    // 定位区域
    private RelativeLayout ll_position;
    private Timer timer;
    private Timer musicTimer;

    private String cameraCloudnumber = null;
    private int cameraControlType = 0;
    private EditText et_camera_content = null;
    private int numberCount = 10;
    //默认移动时间时长
    private int timeOut = 1;
    private RelativeLayout relativeLayout;
    private Button btn_sertViewGONEorVISIVITY;
    private FileUpLoadResultReservice fileUpLoadResultReservice;
    private List<String> list_decpderdisplayChans = null;
    private Timer receiveTimer;
    private TimerTask receiveTimerTask;
    private MediaStatisticsReceive mediaStatisticsReceive;
    private PopupWindow mPopUpWindow;
    public static List<String> voiceList;
    private List<String> speechList;
    private ReceiverMapView receiverMapView;
    private SwitchSplitScreenReceiver switchSplitScreenReceiver;
    private NotificationManager notificationManager;
    //标识当前百度地图是否初始化完成
    private boolean mapInitSuccess = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private int monitorInfoIndex;
    private GroupInfoBroadCastReceiver groupInfoBroadCastReceiver;
    private ReceiverMemberStateChanged receiverMemberStateChanged;
    private TextView map_memberContent;
    private int gisMemberCount;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    // 单通视频展示区域
	/* private FrameLayout fl_calling; */
    // 分屏枚举
    public enum NumColumns {
        ONE, TWO, THREE
    }

    // 当前分屏
    //private NumColumns currNumColumn = NumColumns.ONE;
    private NumColumns currNumColumn = NumColumns.ONE;
    // 当前端口号
    private int currport = 9998;
    private static ArrayList<MonitorInfo> videos = new ArrayList<MonitorInfo>();
    /*// 视频列表
	private ArrayList<MonitorInfo> monitorVideos = new ArrayList<MonitorInfo>();
	// 视频列表
	private ArrayList<MonitorInfo> meetingVideos = new ArrayList<MonitorInfo>();*/
    // 视频布局
    private ScrollView sl_videos;
    // 视频容器
    private LinearLayout ll_videos;
    // 内容工具布局
    @SuppressWarnings("unused")
    private RelativeLayout rl_bottomtools;
    // 分页布局
    private LinearLayout ll_split;
    // 地图操作布局
    private LinearLayout ll_pos, ll_pos_single;
    // 隐藏列表
    private Button btn_gone, btn_one, btn_four, btn_nine, btn_info, btn_close, btn_pushvideo, btn_decodingupperwall, btn_decodingupperwa;
    // 通话时远程摄像头展示
    private SurfaceView mRemoteView;
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

    // 定位工具布局
    private RelativeLayout rl_positiontools;
    // 登录确认消息接收
    private DidloginReceiver didloginReceiver;
    // 心跳机制消息接收
    private HeartbeatReceiver heartbeatReceiver;
    // 注册状态变更通知，当前登录用户状态接收器
    private SelfstatusReceiver selfstatusReceiver;
    // 注册状态变更通知接收到all，发起重新注册
    private ReregisterReceiver reregisterReceiver;
    // 登出消息接收
    private ForcedOfflineReceiver offflineReceiver;
    //网络重新连接
    private ForcedOnlineReceiver onlineReceiver;
    // 主页面接收器，用于切换页面
    private CallBroadcastReceiver callBroadcastReceiver;
    // 短信
    private MessageBroadcastReceiver messageBroadcastReceiver;
    private MainHandler mMainHandler = new MainHandler();
    // 通过通话记录创建会议
    private CallingRecordBroadcastReceiver callingRecordReceiver;
    // 接聽下一路通話通知
    private AnswerNextCallBroadcastReveiver answerNextCallReceiver;
    // 发送广播到主页面告诉其创建的会议为本地创建，请求自动接听
    private MeetingCreateReceiver meetingCreateReceiver;
    // 会议创建失败，清空本地会议记录
    private MeetingResultReceiver meetingResultReceiver;
    // 会议成员退会处理
    private MeetingMemWithDrawReceiver meetingMemWithDrawReceiver;
    // 临时视频会议，自己发起会议，自动应答时发送此消息。
    private PushVideoResultReceiver pushVideoResultReceiver;
    // 会议、监控结束自动停止视频及监控
    private MeetingHangupReceiver meetingHangupReceiver;
    // 发送广播到主页面告诉其会议记录信息已经改变
    private MeetingRefreshReceiver meetingRefreshReceiver;
    // 会议、监控创建视频接收器
    private CreateVideoBroadcastReceiver createVideoReceiver;
    // 会议、监控移除视频接收器
    private RemoveVideoBroadcastReceiver removeVideoReceiver;
    // 会议获取视频接收器
    private GetVideoBroadcastReceiver getvideoReceiver;
    // 会议获取视频事件接收器
    private GetVideoEVTBroadcastReceiver getvideoevtReceiver;
    // 监控开始视频接收器
    private VbugStartBroadcastReceiver vbugstartReceiver;
    // 监控结束视频接收器
    private VbugEndBroadcastReceiver vbugendReceiver;
    // 监控获取视频接收器
    private VbugEventBroadcastReceiver vbugeventReceiver;
    // 视频释放接收器
    private VideoReleaseBroadcastReceiver videoReleaseReceiver;
    // gis位置接收器
    private GisBroadcastReceiver gisBroadcastReceiver;
    //MCU会议上墙下墙接收器
    private MCUDecodeStartAndEndReceive mcuDecodeStartAndEndReceive;
    //摄像头云控操作接收
    private CameraCloudControlReceive cameraCloudControlReceive;

    private LogoutBroadcast logoutBroadcast;
    private boolean selectAll = true;

    //从服务器获取当前会议模式（普通模式还是MCU模式）
    private MeetingIsMcuReceive meetingIsMcuReceive;

    /*
	 * // 通话信息列表 private HashMap<Integer, CallingInfo> callingInfos = new
	 * HashMap<Integer, CallingInfo>();
	 */
    // 来短信，播放铃声
    private Ringtone ringtone;
    private static final int ONGOING_ID = -1;
    private NotificationManager mNotificationManager;
    private MKOfflineMap mOffline = null;
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private Editor editor = null;

    //轨迹回放
    private String currentTime = "";//设置当前时间
    private String trackStartPosTime = "";//开始时间
    private String trackEndPosTime = "";//结束时间
    private long differenceTimeSeconds = 0;//开始和结束的时间差（以秒为单位）
    private Map<String, Integer> trackMemColorMap = new HashMap<String, Integer>();//轨迹回放的的线段颜色

    private static final int REQUEST_LOGIN = 1;
    //Ice接口
    /********
     * yuezs 隐藏 ice相关的
     *********/
	/*private Ice.Communicator ic = null;
	private Ice.ObjectPrx base = null;*/

    //通知管理
    private NotificationManager nm;
    private static Notification notification = null;

    //来电铃声相关
    private MediaPlayer sMediaPlayer = null;
    private Vibrator sVibrator = null;
    private final long VIBRATE_PATTERN[] = new long[]{1000, 1000};


    //解码器集合
    private ArrayList<DecoderInfo> list = new ArrayList<DecoderInfo>();
    private Spinner selectedDcod = null;
    private ArrayAdapter<String> arr_adapter;
    private List<String> data_list;
    private List<ME_DecoderInfo> listDecode;
    private Spinner selectedchan;
    private List<String> listchan;
    private ArrayAdapter<String> chan_adapter;
    private Button btnsbmit;
    private Button btnCanal;
    private String data_Decorder_ID;
    private Spinner selectedtype;
    private List<ME_DecoderDisplayInfo> typeList;
    private int volume = 7;
    private boolean musicState = true;
    /********
     * yuezs 隐藏 ice相关的
     *********/
    //private HashMap<String, DecoderDisplayCfg> hashchan;
    private ArrayAdapter<String> type_adapter;
    /********
     * yuezs 隐藏 ice相关的
     *********/
    //private List<DecoderDisplayCfg> arrlistchan;
    private String chanName;//当前选择的设备类型
    private View decoderlayout;//全局
    private String docName;//当前选中的设备名称
    //private String number;//当前呼叫号码-适用于呼出yuezs
    //private boolean isVideo;//当前呼叫是否为视频-适用于呼出yuezs
    //默认摄像头    true为前置    false为后置
    private boolean camera_State = false;
    //当前timer是否在运行
    private boolean receiveTimerIsStart = false;
    private RegReceive regReceive = null;


    public static int getVideoType() {
        if ((currMeeting != null)
                && (currMeeting.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO)) {
            // 视频会议
            return GlobalConstant.VIDEO_MEETING;
        } else if ((videos.size() > 0)
                && (videos.get(0).getVedioPort() > 0)
                && (videos.get(0).isIsmonitor())) {
            // 视频监控
            return GlobalConstant.VIDEO_MONITOR;
        } else {
            // 没有进行
            return GlobalConstant.VIDEO_NONE;
        }
    }


    //统计丢包时延相关
    private LinearLayout linearlayout_callInfo_meeting;
    //语音统计
    private TextView tv_voice_Downlink_meeting, tv_voice_Uplink_meeting, tv_voice_Bidirectionaldelay_meeting, tv_voice_downstreambandwidth_meeting, tv_voice_Uplinkbandwidth_meeting;
    //视频统计
    private TextView tv_video_Downlink_meeting, tv_video_Uplink_meeting, tv_video_Bidirectionaldelay_meeting, tv_video_downstreambandwidth_meeting, tv_video_Uplinkbandwidth_meeting;
    //语音统计参数
    private int voiceDownlink_meeting = 0;
    private int voice_Uplink_meeting = 0;
    private int voice_Bidirectionaldelay_meeting = 0;
    private int voice_downstreambandwidth_meeting = 0;
    private int voice_Uplinkbandwidth_meeting = 0;
    //视频统计参数
    private int video_Downlink_meeting = 0;
    private int video_Uplink_meeting = 0;
    private int video_Bidirectionaldelay_meeting = 0;
    private int video_downstreambandwidth_meeting = 0;
    private int video_Uplinkbandwidth_meeting = 0;

    private boolean isStartTimer_meeting = false;
    private Timer mTimer;
    private TimerTask timerTask;

    private Map<String, MonitorInfoBean> monitorMap = new HashMap<>();


    private DecodingTheWallReceiver decodingTheWallReceiver;


    //解码上墙Handler
    Handler decoderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1:   //显示dialog

                    AlertDialog.Builder builder;
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    decoderlayout = inflater.inflate(R.layout.fragment_meeting_menu, (ViewGroup) findViewById(R.id.layout_myview));
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(decoderlayout);
                    alertDialog = builder.create();

                    //初始化取消按钮
                    btnCanal = (Button) decoderlayout.findViewById(R.id.btnCanal);
                    //上墙按钮
                    btnsbmit = (Button) decoderlayout.findViewById(R.id.btnsbmit);
                    //点击取消
                    btnCanal.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            if (alertDialog != null) {
                                if (alertDialog.isShowing()) {
                                    alertDialog.dismiss();

                                    if (btn_decodingupperwall != null) {
                                        btn_decodingupperwall.setEnabled(true);
                                    }
                                }
                            }
                        }
                    });
                    //初始化View
                    //解析器名称列表初始化
                    selectedDcod = (Spinner) decoderlayout.findViewById(R.id.selectedDcod);
                    //解析器关联通道初始化
                    selectedchan = (Spinner) decoderlayout.findViewById(R.id.selectedchan);
                    //类型
                    selectedtype = (Spinner) decoderlayout.findViewById(R.id.selectedtype);

                    if (alertDialog != null) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                        alertDialog.show();
                        decoderHandler.sendEmptyMessage(8); //设置btn_decodingupperwall可点击
                    }

                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            //在Dialog消失时恢复按钮
                            if (btn_decodingupperwall != null) {
                                btn_decodingupperwall.setEnabled(true);
                            }
                        }
                    });

                    break;

                case 2:
                    //适配器
                    arr_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, data_list);
                    //设置样式
                    arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //加载适配器
                    selectedDcod.setAdapter(arr_adapter);    //加载解码器列表
                    docName = selectedDcod.getSelectedItem().toString();
                    break;
                case 3:
                    if (list_decpderdisplayChans == null) {
                        return;
                    }
                    type_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list_decpderdisplayChans);
                    type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectedtype.setAdapter(type_adapter);
                    type_adapter.notifyDataSetChanged();
                    Log.e(TAG, "位置--" + type_adapter.getCount());
                    chanName = selectedtype.getSelectedItem().toString();
                    break;

                case 4:
                    if (list_decoderchannel == null) {
                        return;
                    }
                    chan_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list_decoderchannel);
                    chan_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectedchan.setAdapter(chan_adapter);
                    chan_adapter.notifyDataSetChanged();

                    break;


                case 6:
                    if (toastContent != null) {
                        ToastUtils.showToast(MainActivity.this, toastContent);
                        toastContent = null;
                    }
                    break;
                case 7:
                    if (btn_decodingupperwall != null) {
                        btn_decodingupperwall.setEnabled(false);
                    }

                    break;
                case 8:

                    if (btn_decodingupperwall != null) {
                        btn_decodingupperwall.setEnabled(true);
                    }
                    break;

                case 10:

                    //tv_TongjiInfo.setVisibility(View.VISIBLE);
//                    if (isTextSuccess){
//                        isTextSuccess = false;
                    //  Log.e("===========AAAAAA=====","开始刷新数据 设为false");
//                        tv_TongjiInfo.setText(
//                                "下行丢包: "+voiceDownlink_meeting+"(语音)" + "  " + video_Downlink_meeting+"(视频)" + "  "
//                                        +"上行丢包: "+voice_Uplink_meeting+"(语音)" + "  " + video_Uplink_meeting+"(视频)" + "  "
//                                        +"双向时延: "+voice_Bidirectionaldelay_meeting+"(语音)" + "  " + video_Bidirectionaldelay_meeting+"(视频)" + "  "
//                                        +"下行带宽: "+voice_downstreambandwidth_meeting+"(语音)" + "  " + video_downstreambandwidth_meeting+"(视频)" + "  "
//                                        +"上行带宽: "+voice_Uplinkbandwidth_meeting+"(语音)" + "  " + video_Uplinkbandwidth_meeting+"(视频)"
//                        );
//                        tv_TongjiInfo.setText(
//                                "语音 :   下行丢包: "+voiceDownlink_meeting+ "      "
//                                        +"上行丢包: "+voice_Uplink_meeting+ "      "
//                                        +"双向时延: "+voice_Bidirectionaldelay_meeting+ "      "
//                                        +"下行带宽: "+voice_downstreambandwidth_meeting+ "      "
//                                        +"上行带宽: "+voice_Uplinkbandwidth_meeting
//                        );
//                        tv_TongjiInfo2.setText("视频 :   下行丢包: "+ video_Downlink_meeting+ "      "
//                                +"上行丢包: "+ video_Uplink_meeting+""+ "      "
//                                +"双向时延: "+ video_Bidirectionaldelay_meeting+ "      "
//                                +"下行带宽: "+ video_downstreambandwidth_meeting+"      "
//                                +"上行带宽: "+ video_Uplinkbandwidth_meeting
//                        );
                    //           }

                    tv_voice_Downlink_meeting.setText("" + voiceDownlink_meeting);
                    tv_voice_Uplink_meeting.setText("" + voice_Uplink_meeting);
                    tv_voice_Bidirectionaldelay_meeting.setText("" + voice_Bidirectionaldelay_meeting);
                    tv_voice_downstreambandwidth_meeting.setText("" + voice_downstreambandwidth_meeting);
                    tv_voice_Uplinkbandwidth_meeting.setText("" + voice_Uplinkbandwidth_meeting);

                    tv_video_Downlink_meeting.setText("" + video_Downlink_meeting);
                    tv_video_Uplink_meeting.setText("" + video_Uplink_meeting);
                    tv_video_Bidirectionaldelay_meeting.setText("" + video_Bidirectionaldelay_meeting);
                    tv_video_downstreambandwidth_meeting.setText("" + video_downstreambandwidth_meeting);
                    tv_video_Uplinkbandwidth_meeting.setText("" + video_Uplinkbandwidth_meeting);
                    break;

                case 11:

                    tv_voice_Downlink_meeting.setText("0");
                    tv_voice_Uplink_meeting.setText("0");
                    tv_voice_Bidirectionaldelay_meeting.setText("0");
                    tv_voice_downstreambandwidth_meeting.setText("0");
                    tv_voice_Uplinkbandwidth_meeting.setText("0");

                    tv_video_Downlink_meeting.setText("0");
                    tv_video_Uplink_meeting.setText("0");
                    tv_video_Bidirectionaldelay_meeting.setText("0");
                    tv_video_downstreambandwidth_meeting.setText("0");
                    tv_video_Uplinkbandwidth_meeting.setText("0");
                    break;
            }
        }
    };

    private SetVolumeUtils setVolumeUtils = null;
    //当前界面是否绘制完成
    private boolean isDraw = false;
    AudioManager audioManager = null;

    //用于存储视频布局
    private ArrayList<MonitorInfo> monitorInfoList = new ArrayList<>();

    //标识当前机构数是否加载完成
    private boolean treeLoadIsSuccess = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                .penaltyLog()   //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() //探测SQLite数据库操作
                .penaltyLog()  //打印logcat
                .penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);
        //屏幕常亮
        keepScreenLongLight(this);
        // 注册通话回调
        MtcCallDelegate.setCallback(this);

        setContentView(R.layout.activity_main);
        //注册EventBus
        EventBus.getDefault().register(this);
        // 創建播放器
        //new MultiPlayer().startup();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String content = "屏幕的分辨率为：" + dm.widthPixels + "*" + dm.heightPixels;
        Log.e("屏幕分辨率", content);

        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();
        ptt_3g_PadApplication.addActivityList(this);
        // 全局变量定义
        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        editor = prefs.edit();
        // 取得对讲配置数据
        mIntercomPriorityArray = getResources().getStringArray(R.array.intercom_priority);

        defaultgroup = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, "");
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //设置声音模式
        audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        setVolumeUtils = new SetVolumeUtils(this, audioManager);
        if (posHashMap != null) {
            posHashMap.clear();
        }
        if (trackPosMarkerInfoMap != null) {
            trackPosMarkerInfoMap.clear();
        }
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //发起通知   在PTTService中执行
        initNotification(this, this.getString(R.string.app_name), this.getString(R.string.app_name), this.getString(R.string.app_name));
        showNotification();

        //注册广播
        initregisterBroadcastReceiver();
        // 初始化页面
        InitView();
        InitData();
        initBaiduMap(); //百度地图开始
        initGetCameraUVC();//获取当前设备是否存在USB摄像头

        //初始化统计丢包时延相关View
        initMediaStatistics();

        // 取得状态
        int state = MtcDelegate.getState();
        // 状态为过期则退出
        if (state == MtcDelegate.STATE_EXPIRED) {
            ToastUtils.showToast(this, "Expired");
            finish();
            return;
        }
        // 状态为CPU不支持则退出
        else if (state == MtcDelegate.STATE_NOT_SUPPORTED) {
            ToastUtils.showToast(this, "CPU Not Supported");
            finish();
            return;
        }
        // 设置监控布局
        DealVideoItmes();
        //设置回音消除参数
        initSetEcOptions();
        //运行GC
        System.gc();
        //标识为true
        isDraw = true;

        //ICE连接成功进行读取配置
        if (prefs.getBoolean(GlobalConstant.ICECONNECTION, false)) {
            //进行配置获取
            getOldMsgData();
            //对讲组获取
            GetGroupInfo();
            //机构数获取
            getAllGroupsByICE();

            prefs.edit().putBoolean(GlobalConstant.ICECONNECTION, false).apply();
        }

        //ICE连接成功回调
        MediaEngine.GetInstance().ME_SetOnConnectBusinessServerListener(new MediaEngine.OnConnectBusinessServerListener() {
            @Override
            public void onPublishSuccess() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //进行配置获取
                        getOldMsgData();
                        //对讲组获取
                        GetGroupInfo();
                        //机构数获取
                        getAllGroupsByICE();
                    }
                };
                ThreadPoolProxy.getInstance().execute(runnable);
            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //todo 判断设备是否包含UVC摄像头
    private void initGetCameraUVC() {


        MediaEngine.ME_VideoDeviceInfo[] videoDevices = MediaEngine.GetInstance().ME_GetVideoDevices();
        for (int i = 0; i < videoDevices.length; i++) {
            //存在后置摄像头
            if (videoDevices[i].name.equals("Back camera")) {

            }

            //存在前置摄像头
            if (videoDevices[i].name.equals("Front camera")) {

            }

            //存在UVC摄像头
            if (videoDevices[i].name.equals("Usb camera")) {
                ptt_3g_PadApplication.setMe_videoDeviceInfo(videoDevices[i]);
               ptt_3g_PadApplication.setCameraUVC(true);
            }
        }


    }

    //Incoming 单呼
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void incomingSingle(IncomingSingle incomingSingle) {
        //判断当前是否存在监控,如果存在先结束监控 （不需要结束监控 2018 10-09 add sws）
//        if (monitorMap.size() > 0) {
//            Set<String> strings = monitorMap.keySet();
//            for (String s : strings) {
//                MonitorInfoBean monitorInfoBean = monitorMap.get(s);
//                MonitorEnd(monitorInfoBean.getName(), "", "", monitorInfoBean.getNumber());
//            }
//        }
        mCallState = GlobalConstant.CALL_STATE_INCOMING;
        String number = incomingSingle.getNumber();
        // 设置当前会话编号
        mSessId = incomingSingle.getCallId();
        // 初始化通话信息
        CallingInfo callinginfo = new CallingInfo(incomingSingle.getCallId());
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        callinginfo.setOutgoing(false);
        // 取得正在进行的通话或对讲
        CallingInfo callingIntercom = null;
        CallingInfo callingCalling = null;

        Log.e(TAG, "lizhiwei mtcCallDelegateIncoming:来了一则通话，共有几个进行的通话" + CallingManager.getInstance().getCallingData().size()
                + ",isHolding:" + (CallingManager.getInstance().getCallingData().size() > 0 ? CallingManager.getInstance().getCallingData().get(0).isHolding() : false));

        for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
            if ((!callingInfo.isHolding())
                    && (callingInfo.getDwSessId() != incomingSingle.getCallId())) {   //mSessId 2015-05-07 qq
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                    callingIntercom = callingInfo;
                } else {
                    callingCalling = callingInfo;
                }
            }
        }
        // 设置通话信息

        callinginfo.setRemoteName(number);
        callinginfo.setCalltype(calltype);
        callinginfo.setIsalright(false);
        //MtcCall.Mtc_SessHasVideo(dwSessId)
        Log.e(TAG, "Incoming isiVideo : " + incomingSingle.isVideo());
        if (incomingSingle.isVideo()) {
            callinginfo.setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
        } else {
            callinginfo.setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
        }
        if ((callingIntercom != null) || (callingCalling != null)) {
            if (callingIntercom != null) {
                Log.e(TAG, "main  incoming 7821");
                // 有一个对讲正在进行，来了一个新的单呼呼入，给出提示操作  (之前逻辑  现在改为 来电直接挂断当前对讲 并跳转到calling界面)
                //intercomShowCallingInfo(callinginfo, callingIntercom);

                // 终止原来通话
                onEnd(callingIntercom.getDwSessId());
                // 移除通话数据
                CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());

                // 增加通话记录
                SQLiteHelper sqLiteHelper = new SQLiteHelper(
                        MainActivity.this);
                sqLiteHelper.open();
                CallingRecords callingRecord = new CallingRecords();
                callingRecord.setUserNo(sipUser.getUsername());
                callingRecord.setBuddyNo(callinginfo.getRemoteName());
                Date nowDate = new Date();
                callingRecord.setTime(FormatController.secToTime(0));
                callingRecord.setInOutFlg(1);
                callingRecord.setStartDate(nowDate);
                callingRecord.setAnswerDate(nowDate);
                callingRecord.setStopDate(nowDate);
                callingRecord.setDuration(0);
                callingRecord.setCallState(0);// 忽略，未接通
                callingRecord.setSessId(callinginfo.getDwSessId());
                sqLiteHelper.createCallingRecords(callingRecord);
                sqLiteHelper.closeclose();


                // 切换到通话activity
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {

                    mImageAdapter.setImageLight(1);
                    mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                    SetViewShow(GlobalConstant.ONLYTAB);
                    // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
                }

                // 设置接听类型
                callinginfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
                callinginfo.setMe_idsPara(incomingSingle.getMe_idsPara());
                callinginfo.setVideo(incomingSingle.isVideo());
                callinginfo.setState(incomingSingle.getState());

                // 添加新的通话
                CallingManager.getInstance().addCallingInfo(callinginfo);
                CallingManager.getInstance().addAnswerInfo(callinginfo);
                Log.e(TAG, "发送广播  mediatype为：" + callinginfo.getMediaType());
                // 来电广播
                Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                callingIncoming.putExtra(KEY_CALL_CHANGEVIEW, true);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, callinginfo.getRemoteName());
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, incomingSingle.isVideo());
                sendBroadcast(callingIncoming);
                Log.e(TAG, "main     1042");
                return;
            }

            if (callingCalling != null) {
                Log.e(TAG, "main  incoming 7849");
                // 有一个通话正在进行，来了一个新的单呼呼入了，给出提示操作
                showCallingInfo(callinginfo, callingCalling);
                Log.e(TAG, "return  1020");
                return;
            }
        } else {
            // 通知接听
            // 切换到通话activity
            if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {

                mImageAdapter.setImageLight(1);
                mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                Log.e(TAG, "ShowType.ONLYTAB--" + GlobalConstant.ONLYTAB);
                SetViewShow(GlobalConstant.ONLYTAB);
                Log.e(TAG, "切换到通话页签");
            }
            String userNo = sipUser.getUsername();//MtcDelegate.getLoginedUser();
            Log.e(TAG, "userNo:" + userNo);
            // 增加通话记录
            SQLiteHelper sqLiteHelper = new SQLiteHelper(MainActivity.this);
            sqLiteHelper.open();
            CallingRecords callingRecord = new CallingRecords();
            callingRecord.setUserNo(userNo);
            callingRecord.setBuddyNo(incomingSingle.getNumber());
            Date nowDate = new Date();
            callingRecord.setStartDate(nowDate);
            callingRecord.setAnswerDate(nowDate);
            callingRecord.setStopDate(nowDate);
            callingRecord.setDuration(0);
            callingRecord.setTime(FormatController.secToTime(0));
            callingRecord.setInOutFlg(1);
            callingRecord.setCallState(0);
            callingRecord.setSessId(incomingSingle.getCallId());
            sqLiteHelper.createCallingRecords(callingRecord);
            sqLiteHelper.closeclose();

            if (incomingSingle.isVideo()) {
                mtcCallDelegateStartPreview();
            } else {
                MtcProximity.start(MainActivity.this);
            }
            // 保证界面始终在屏幕上
            keepScreenOn(true);
            // 屏幕始终不锁定
            showWhenLocked(true);
            // 添加新的通话
            CallingManager.getInstance().addCallingInfo(callinginfo);
            Log.e(TAG, "开始广播incoming  7347");
            // 来电广播
            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, incomingSingle.getCallId());   //mSessId 2015-05-07 qq
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, number);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, incomingSingle.isVideo());
            sendBroadcast(callingIncoming);
            Log.e(TAG, "main     7552");
        }
    }

    //Incoming 对讲or临时对讲
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void incomingIntercom(IncomingIntercom incomingIntercom)
    {

        if (incomingIntercom == null) {
            return;
        }
        Log.e(TAG, "Main*****IncomingIntercomEventBus***********");
        // 设置当前会话编号
        mSessId = incomingIntercom.getCallId();
        // 初始化通话信息
        CallingInfo callinginfo = new CallingInfo(incomingIntercom.getCallId());
        //如果是对讲/临时对讲
        //如果已经有单呼，自己在发起对讲，单呼保持，自动接听对讲
        //如果已经有对讲，自己在发起对讲，挂断当前对讲，自动接听新对讲
        //如果已经有会议或者广播，自己在发起对讲，单呼保持，自动接听对讲
        String number = incomingIntercom.getNumber();

        String[] split = number.split("\\~");
        String name = split[0];// 组号或会议号
        String remoteno = split[1];// 发起者号码
//        Log.e("城北待君归","number : " + number + "\r\n" +  "name : " + name + "\r\n" + "remoteno : " + remoteno);
//        Log.e("城北待君归","GroupManager.getInstance().getOptimumgroup() : " + GroupManager.getInstance().getOptimumgroup().toString());
//        if (null == GroupManager.getInstance().getOptimumgroup()) {
//            Log.e("测试新来电", "");
//            MtcCall.Mtc_SessTerm(incomingIntercom.getCallId(),
//                    MtcCallConstants.EN_MTC_CALL_TERM_REASON_NORMAL, null);
//            return;
//        }
        // 对讲
        int calltype = GlobalConstant.CALL_TYPE_INTERCOM;
        Log.e(TAG, "判断来电为对讲===7465  并将calltype赋值为对讲   calltype：" + calltype);
        // 判断是否为自己发起的对讲
//        String OptimumGroupNum = "";
//        OptimumGroupNum = GroupManager.getInstance().getOptimumgroup().getNumber().trim();
        if (((remoteno != null) && (remoteno.equals(sipUser.getUsername())))
                || (incomingIntercom.isTempintercom() && remoteno != null && remoteno.equals(sipUser.getUsername()))) {
            callinginfo.setOutgoing(true);
            Log.e(TAG, "----------收到对讲呼入6837-----------------callinginfo.setOutgoing(true)    拨出");
        } else {
            callinginfo.setOutgoing(false);
            Log.e(TAG, "----------收到对讲呼入6837-----------------callinginfo.setOutgoing(false)       不是拨出");
        }
        //MtcDelegate.log("lizhiwei 是否为呼出=" + callinginfo.isOutgoing());
        // 如果为临时对讲需要获取对讲组成员
        if (incomingIntercom.isTempintercom()) {
            // 设置是临时对讲
            callinginfo.setIstempintercom(true);
            Log.e(TAG, "----------收到对讲呼入6847-----------------判断为临时对讲");
//							/*GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(name);
//        					if (groupinfo == null){
//        						MtcDelegate.log("lizhiwei 没有取得对讲组" + name);*/
//            // 添加临时对讲组信息
//							/*Calendar calendar = Calendar.getInstance();
//        				    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
//        				    	String sessname = (CommonMethod.getCurrLongCNDate(calendar)+"临时对讲").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "").substring("yyyy年MM月dd日".length());
//        				    	SimpleDateFormat shortformat = new SimpleDateFormat("HHmm");
//        				    	sessname = "临时对讲" + shortformat.format(calendar.getTime());*/
            GroupInfo groupinfo = new GroupInfo();
            if (name.length() >= 14) {
                String groupName = "临时对讲" + name.substring(8, 12);
                groupinfo.setName(groupName);
            } else {
                groupinfo.setName("临时对讲");
            }
            Log.e(TAG, "====临时对讲组号：" + name);
            groupinfo.setNumber(name);
							/*groupinfo = new GroupInfo();
        						groupinfo.setName(name);
        						groupinfo.setNumber(name);*/
            groupinfo.setLevel(10);
            groupinfo.setTemporary(true);
            groupinfo.setVisible(false);
            // 添加对讲组
            GroupManager.getInstance().addGroup(groupinfo);
            //MtcDelegate.log("lizhiwei 对讲呼叫进来"+mSessId+"添加对讲组,groupno="+name);
							/*}*/
        }
        // 取得正在进行的通话或对讲
        CallingInfo callingIntercom = null;
        CallingInfo callingCalling = null;
        for (CallingInfo callingInfo : CallingManager.getInstance()
                .getCallingData()) {
            if ((!callingInfo.isHolding())
                    && (callingInfo.getDwSessId() != incomingIntercom.getCallId())) {   //mSessId 2015-05-07 qq
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                    callingIntercom = callingInfo;
                } else {
                    callingCalling = callingInfo;
                }
            }
        }

        if ((callingIntercom != null) || (callingCalling != null)) {
            // 对讲与通话正在进行中
            if (callingIntercom != null) {
                Log.e(TAG, "MainActivity ====7476=========== 当前有对讲正在进行 ");
                // 对讲正在进行中
                callinginfo.setRemoteName(name);
                callinginfo.setCalltype(calltype);
                callinginfo.setIsalright(true);
                Log.e(TAG, "callinginfo.isOutgoing()    : " + callinginfo.isOutgoing());
                if (callinginfo.isOutgoing()) {
                    Log.e(TAG, "MainActivity ====7565===========  这个对讲是自己发起的");
                    // 有一个对讲正在进行，自己又呼出一个对讲需要挂断原来对讲，自动接听新对讲
                    // 终止原来通话
                    Log.e(TAG, "lizhiwei 对讲重叠挂断");

                    IntercomActivity.currSessId = incomingIntercom.getCallId();
                    onEnd(callingIntercom.getDwSessId());
                    // 移除通话数据
                    CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());

                    // 添加新的通话
                    CallingManager.getInstance().addCallingInfo(callinginfo);

//                            // 切换到对讲activity
//                            if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                                mImageAdapter.setImageLight(0);
//                                mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                                SetViewShow(GlobalConstant.ALL);
//                            }

                    // 来电广播
                    MtcProximity.start(MainActivity.this);

                    // 保证界面始终在屏幕上
                    keepScreenOn(true);
                    // 屏幕始终不锁定
                    showWhenLocked(true);
                    Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, incomingIntercom.getCallId());   //mSessId 2015-05-07 qq
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, true);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_CID, incomingIntercom.getMe_idsPara().getSession());
                    sendBroadcast(callingIncoming);
                    Log.e(TAG, "main     1245");

                    Log.e(TAG, "发送 广播   此次对讲为自己发起   结束  发送GlobalConstant.ACTION_CALLING_INCOMING广播到IntercomsActivity界面");
                    return;
                } else {
                    Log.e(TAG, "MainActivity ====7565===========  这个对讲不是自己发起的");
                    // 有一个对讲正在进行，另有一个对讲呼入，按照优先级进行处理
                    // 优先级处理时，如果为自动拒绝不需要切换页面。
                    // 取得默认对讲组
                    GroupInfo currGroupInfo = GroupManager.getInstance().getOptimumgroup();
                    GroupInfo newGroupInfo = GroupManager.getInstance().getGroupInfo(name);
                    Log.e(TAG + (((currGroupInfo != null) && (newGroupInfo != null)) ? "都不为空" : "有一个为空"), "");
                    // 添加新的通话
                    CallingManager.getInstance().addCallingInfo(callinginfo);
//                            // 通知新对讲
//                            if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                                mImageAdapter.setImageLight(0);
//                                mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                                SetViewShow(GlobalConstant.ALL);
//                            }
                    // 来电广播
                    MtcProximity.start(MainActivity.this);

                    // 保证界面始终在屏幕上
                    keepScreenOn(true);
                    // 屏幕始终不锁定
                    showWhenLocked(true);
                    Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, incomingIntercom.getCallId());   //mSessId 2015-05-07 qq
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, false);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_CID, incomingIntercom.getMe_idsPara().getSession());
                    sendBroadcast(callingIncoming);
                    Log.e(TAG, "main     1273");
                    return;
                }
            }

            if (callingCalling != null) {
                // 通话正在进行中
                callinginfo.setRemoteName(name);
                callinginfo.setCalltype(calltype);
                callinginfo.setIsalright(true);
//								/*if (callinginfo.isOutgoing()) {
//        							// 有一个通话正在进行，自己又呼出一个对讲需要保持原来通话，自动接听新对讲
//        							onHold(callingCalling.getDwSessId());
//        							// 添加新的通话
//        							CallingManager.getInstance().addCallingInfo(callinginfo);
//
//        							// 切换到对讲activity
//        							if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//        								mImageAdapter.setImageLight(0);
//        								mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//        								SetViewShow(ShowType.ALL);
//        							}
//
//        							// 来电广播
//        							Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);   //mSessId 2015-05-07 qq
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, true);
//        							sendBroadcast(callingIncoming);
//        							return;
//        						} else {*/
                //有一个通话正在进行，另有一个对讲呼入，自动挂断对讲

                Log.e(TAG, "hangUp :" + callinginfo.getDwSessId());
                MediaEngine.GetInstance().ME_Hangup(callinginfo.getDwSessId());
                callinginfo = null;
                return;
                //	}
            }

        } else {
            // 对讲和通话都没有进行中
            Log.e(TAG, "对讲和通话都没有进行中");
            // TODO Auto-generated method stub
            callinginfo.setRemoteName(name);
            callinginfo.setCalltype(calltype);
            callinginfo.setIsalright(false);
            // 添加新的通话
            CallingManager.getInstance().addCallingInfo(callinginfo);

//                    // 通知新对讲
//                    if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                        mImageAdapter.setImageLight(0);
//                        mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                        SetViewShow(GlobalConstant.ALL);
//                    }

            // 来电广播


            MtcProximity.start(MainActivity.this);

            // 保证界面始终在屏幕上
            keepScreenOn(true);
            // 屏幕始终不锁定
            showWhenLocked(true);
            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, callinginfo.isOutgoing() ? true : false);
            callingIncoming.putExtra(GlobalConstant.KEY_CALL_CID, incomingIntercom.getMe_idsPara().getSession());
            sendBroadcast(callingIncoming);
            Log.e(TAG, "main     1347");
        }
    }

    //Incoming 会议
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void incomingMeeting(IncomingMeeting incomingMeeting)
    {

        if (incomingMeeting == null) {
            return;
        }
        Log.e("MoveYourBaby", "Main界面 收到Eventbus事件  会议 是自己发起的");
        // 设置当前会话编号
        mSessId = incomingMeeting.getCallId();
        Log.e("MoveYourBaby", "mSessId :" + mSessId);
        // 初始化通话信息
        CallingInfo callinginfo = new CallingInfo(incomingMeeting.getCallId());
        // 由于会议只有自己创建的，所以会议创建之后一定要对进行的对讲和通话进行保持操作。需要在创建会议前做路数的判断。
        int calltype = GlobalConstant.CALL_TYPE_MEETING;

        //当是会议或者广播响应时，说明之前发起的会议或者得到响应，在这里可以关闭timer
						/*if(ptt_3g_PadApplication != null)
        				{
        					ptt_3g_PadApplication.endTimer();
        				}*/
        CallingInfo callingIntercom = null;
        CallingInfo callingCalling = null;
        for (CallingInfo callingInfo : CallingManager.getInstance()
                .getCallingData()) {
            if ((!callingInfo.isHolding())
                    && (callingInfo.getDwSessId() != incomingMeeting.getCallId())) {   //mSessId 2015-05-07 qq
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                    callingIntercom = callingInfo;
                } else {
                    callingCalling = callingInfo;
                }
            }
        }

//        /*if (callingCalling != null) {
//        					// 保持通话
//        					callingCalling.setHolding(true);
//        					MtcCall.Mtc_SessHold(callingCalling.getDwSessId());
//        				}*/
        if (callingIntercom != null) {
//								/*// 保持对讲
//        					callingIntercom.setHolding(true);
//        					MtcCall.Mtc_SessHold(callingIntercom.getDwSessId());*/
            Log.e("MoveYourBaby", "callingIntercom != null main 1375");
            //结束对讲
            onEnd(callingIntercom.getDwSessId());
            // 移除通话数据
            CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());
        }
        // 判断来电是否为会议
        if (currMeeting.getIsMonitor() == 1) {
            calltype = GlobalConstant.CALL_TYPE_MONITOR;
            Log.e(TAG, "进入会议2");
            if (mTabHost.getCurrentTabTag() != GlobalConstant.MONITOR_TAB) {
                mImageAdapter.setImageLight(6);
                mTabHost.setCurrentTabByTag(GlobalConstant.MONITOR_TAB);
                SetViewShow(GlobalConstant.ALL);
            }
        } else {
            calltype = GlobalConstant.CALL_TYPE_MEETING;
            if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                mImageAdapter.setImageLight(2);
                mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                SetViewShow(GlobalConstant.ALL);
                Log.e(TAG, "进入会议3");
            }

            // 取得远程摄像头
            //if (MtcCall.Mtc_SessPeerOfferVideo(mSessId)) {
            if (incomingMeeting.isVideo()) {
                //mtcCallDelegateStartPreview();
            } else {
                MtcProximity.start(MainActivity.this);
            }

            // 是否延迟提供音视频
            mIsDelayOffer = MtcCall.Mtc_SessPeerOfferAudio(incomingMeeting.getCallId()) == false
                    && MtcCall.Mtc_SessPeerOfferVideo(incomingMeeting.getCallId()) == false;

            // 保证界面始终在屏幕上
            keepScreenOn(true);
            // 屏幕始终不锁定
            showWhenLocked(true);
        }
        // 设置通话信息
        callinginfo.setRemoteName(incomingMeeting.getNumber());
        callinginfo.setCalltype(calltype);
        callinginfo.setIsalright(false);
        //添加新的通话
        CallingManager.getInstance().addCallingInfo(callinginfo);
        Log.e(TAG, "***INCOMING:" + calltype + "---getCalltype():" + callinginfo.getCalltype());
        // 通知自动接听
        Intent callingIncoming = new Intent(
                GlobalConstant.ACTION_CALLING_INCOMING);
        callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID,
                incomingMeeting.getCallId());   //mSessId 2015-05-07 qq
        callingIncoming
                .putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO,
                incomingMeeting.getNumber());
        sendBroadcast(callingIncoming);

        Log.e("测试会议发起失败", "Main界面 发送incoming广播 calltype ：" + calltype + "\r\n" + "incomingMeeting.getNumber() : " + incomingMeeting.getNumber());


            setFour();

    }

    //初始化统计丢包时延相关View
    private void initMediaStatistics()
    {
        //统计信息根布局
        linearlayout_callInfo_meeting = (LinearLayout) findViewById(R.id.linearlayout_callInfo_meeting);
        //语音统计View
        tv_voice_Downlink_meeting = (TextView) findViewById(voice_Downlink_meeting);
        tv_voice_Uplink_meeting = (TextView) findViewById(R.id.voice_Uplink_meeting);
        tv_voice_Bidirectionaldelay_meeting = (TextView) findViewById(R.id.voice_Bidirectionaldelay_meeting);
        tv_voice_downstreambandwidth_meeting = (TextView) findViewById(R.id.voice_downstreambandwidth_meeting);
        tv_voice_Uplinkbandwidth_meeting = (TextView) findViewById(R.id.voice_Uplinkbandwidth_meeting);

        //视频统计View
        tv_video_Downlink_meeting = (TextView) findViewById(R.id.video_Downlink_meeting);
        tv_video_Uplink_meeting = (TextView) findViewById(R.id.video_Uplink_meeting);
        tv_video_Bidirectionaldelay_meeting = (TextView) findViewById(R.id.video_Bidirectionaldelay_meeting);
        tv_video_downstreambandwidth_meeting = (TextView) findViewById(R.id.video_downstreambandwidth_meeting);
        tv_video_Uplinkbandwidth_meeting = (TextView) findViewById(R.id.video_Uplinkbandwidth_meeting);
    }

    //设置回音消除参数
    private void initSetEcOptions()
    {
        String modular = prefs.getString(GlobalConstant.SEETING_MODULAR, GlobalConstant.MODULAR_WEBRTC);
        boolean noisereduction = prefs.getBoolean(GlobalConstant.SEETING_NOISEREDUCTION, GlobalConstant.NOISEREDUCTION_FALSE);
        String grade = prefs.getString(GlobalConstant.SEETING_GRADE, GlobalConstant.GRADE_1);
        MediaEngine.GetInstance().ME_SetEcOptions(Integer.valueOf(modular), 100, noisereduction, Integer.valueOf(grade));
        Log.e(TAG, "modular : " + modular + "\r\n" + "noisereduction : " + noisereduction + "\r\n" + "grade : " + grade);
    }

    //Notification初始化
    public void initNotification(Context context, String ticker,
                                 String title, String msg)
    {
        if (notification == null) {
            Notification.Builder n = new Notification.Builder(MainActivity.this);
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
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
        if (notification != null) {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.flags = Notification.FLAG_NO_CLEAR;
            notification.contentIntent = pt;
            nm.notify(R.string.app_name, notification);
        }

    }

    //停止Notification
    public void stopNotification()
    {
        if (nm != null) {
            nm.cancel(R.string.app_name);

            nm = null;
        }
    }

    //注册广播
    private void initregisterBroadcastReceiver()
    {
        // TODO Auto-generated method stub

        receiverMemberStateChanged = new ReceiverMemberStateChanged();
        IntentFilter receiverMemberStateChangedFilter = new IntentFilter();
        receiverMemberStateChangedFilter.addAction(GlobalConstant.ACTION_NOTIFICATION_MAIN_GIS_IMG);
        registerReceiver(receiverMemberStateChanged, receiverMemberStateChangedFilter);

        didloginReceiver = new DidloginReceiver();
        IntentFilter didloginIntentFilter = new IntentFilter();
        didloginIntentFilter.addAction(GlobalConstant.ACTION_DIDLOGIN);
        didloginIntentFilter.addAction(GlobalConstant.ACTION_SHOW);
        registerReceiver(didloginReceiver, didloginIntentFilter);

        heartbeatReceiver = new HeartbeatReceiver();
        IntentFilter heartbeatIntentFilter = new IntentFilter();
        heartbeatIntentFilter.addAction(GlobalConstant.ACTION_HEARTBEAT);
        registerReceiver(heartbeatReceiver, heartbeatIntentFilter);

        selfstatusReceiver = new SelfstatusReceiver();
        IntentFilter selfstatusIntentFilter = new IntentFilter();
        selfstatusIntentFilter.addAction(GlobalConstant.ACTION_SELFSTATUS);
        registerReceiver(selfstatusReceiver, selfstatusIntentFilter);

        reregisterReceiver = new ReregisterReceiver();
        IntentFilter reregisterIntentFilter = new IntentFilter();
        reregisterIntentFilter.addAction(GlobalConstant.ACTION_REREGISTER);
        registerReceiver(reregisterReceiver, reregisterIntentFilter);

        offflineReceiver = new ForcedOfflineReceiver();
        IntentFilter offflineIntentFilter = new IntentFilter();
        offflineIntentFilter.addAction(GlobalConstant.ACTION_FORCED_OFFLINE);
        registerReceiver(offflineReceiver, offflineIntentFilter);

        onlineReceiver = new ForcedOnlineReceiver();
        IntentFilter onlineIntentFilter = new IntentFilter();
        onlineIntentFilter.addAction(GlobalConstant.ACTION_FORCED_ONLINE);
        onlineIntentFilter.addAction(GlobalConstant.ACTION_CONNECTION_CHANGE);
        registerReceiver(onlineReceiver, onlineIntentFilter);

        callBroadcastReceiver = new CallBroadcastReceiver();
        IntentFilter incomingIntentFilter = new IntentFilter();
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLVOICE);
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLVIDEO);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLHANGUP);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLNETCHANGEHANGUP);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLVOICEANSWER);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLVIDEOANSWER);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLDECLINE);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLSPEAKER);
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLMUTE);
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_STARTRING);
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_STORPING);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_STARTRINGBACK);
        incomingIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CALLHOLD);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_CALLUNHOLD);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_OTHERCALLVOICE);
        incomingIntentFilter
                .addAction(GlobalConstant.ACTION_CALLING_OTHERCALLVIDEO);
        registerReceiver(callBroadcastReceiver, incomingIntentFilter);

        messageBroadcastReceiver = new MessageBroadcastReceiver();
        IntentFilter messageIntentFilter = new IntentFilter();
        messageIntentFilter
                .addAction(GlobalConstant.ACTION_MESSAGE_MSGRECEIVEOK);
        messageIntentFilter
                .addAction(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
        messageIntentFilter
                .addAction(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTYES);
        messageIntentFilter
                .addAction(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTNO);
        messageIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGMAINNEW);
        messageIntentFilter
                .addAction(GlobalConstant.ACTION_MESSAGE_MSGUPLOADOK);
        registerReceiver(messageBroadcastReceiver, messageIntentFilter);

        callingRecordReceiver = new CallingRecordBroadcastReceiver();
        IntentFilter callingRecordIntentFilter = new IntentFilter();
        callingRecordIntentFilter.addAction(GlobalConstant.ACTION_CALLING_CONFERENCE);
        registerReceiver(callingRecordReceiver, callingRecordIntentFilter);

        answerNextCallReceiver = new AnswerNextCallBroadcastReveiver();
        IntentFilter answerNextCallIntentFilter = new IntentFilter();
        answerNextCallIntentFilter.addAction(GlobalConstant.ACTION_CALLING_ANSWERNEXT);
        registerReceiver(answerNextCallReceiver, answerNextCallIntentFilter);


        meetingCreateReceiver = new MeetingCreateReceiver();
        IntentFilter meetingCreateIntentFilter = new IntentFilter();
        meetingCreateIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_CREATE);
        registerReceiver(meetingCreateReceiver, meetingCreateIntentFilter);

        meetingResultReceiver = new MeetingResultReceiver();
        IntentFilter meetingResultIntentFilter = new IntentFilter();
        meetingResultIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_CREATERESULT);
        registerReceiver(meetingResultReceiver, meetingResultIntentFilter);

        meetingMemWithDrawReceiver = new MeetingMemWithDrawReceiver();
        IntentFilter meetingMemWithDrawIntentFilter = new IntentFilter();
        meetingMemWithDrawIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_MEMWITHDRAW);
        registerReceiver(meetingMemWithDrawReceiver,
                meetingMemWithDrawIntentFilter);

        pushVideoResultReceiver = new PushVideoResultReceiver();
        IntentFilter pushVideoResultIntentFilter = new IntentFilter();
        pushVideoResultIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT);
        pushVideoResultIntentFilter.addAction(GlobalConstant.ACTION_CALLING_TEMPINTERCOM_REQ);
        registerReceiver(pushVideoResultReceiver, pushVideoResultIntentFilter);

        meetingRefreshReceiver = new MeetingRefreshReceiver();
        IntentFilter meetingRefreshIntentFilter = new IntentFilter();
        meetingRefreshIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_REFRESH);
        registerReceiver(meetingRefreshReceiver, meetingRefreshIntentFilter);

        createVideoReceiver = new CreateVideoBroadcastReceiver();
        IntentFilter createVideoIntentFilter = new IntentFilter();
        createVideoIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_CREATEVIDEO);
        createVideoIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_CREATEVIDEO2);
        registerReceiver(createVideoReceiver, createVideoIntentFilter);

        removeVideoReceiver = new RemoveVideoBroadcastReceiver();
        IntentFilter removeVideoIntentFilter = new IntentFilter();
        removeVideoIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_REMOVEVIDEO);
        registerReceiver(removeVideoReceiver, removeVideoIntentFilter);

        meetingHangupReceiver = new MeetingHangupReceiver();
        IntentFilter meetingHangupCreateIntentFilter = new IntentFilter();
        meetingHangupCreateIntentFilter
                .addAction(GlobalConstant.ACTION_MEETING_CALLHANGUP);
        registerReceiver(meetingHangupReceiver, meetingHangupCreateIntentFilter);

        getvideoReceiver = new GetVideoBroadcastReceiver();
        IntentFilter getvideoIntentFilter = new IntentFilter();
        getvideoIntentFilter.addAction(GlobalConstant.ACTION_MONITOR_GETVIDEO);
        registerReceiver(getvideoReceiver, getvideoIntentFilter);

        getvideoevtReceiver = new GetVideoEVTBroadcastReceiver();
        IntentFilter getvideoevtIntentFilter = new IntentFilter();
        getvideoevtIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_GETVIDEOEVT);
        registerReceiver(getvideoevtReceiver, getvideoevtIntentFilter);

        vbugstartReceiver = new VbugStartBroadcastReceiver();
        IntentFilter vbugstartIntentFilter = new IntentFilter();
        vbugstartIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_VBUG_START);
        registerReceiver(vbugstartReceiver, vbugstartIntentFilter);

        vbugendReceiver = new VbugEndBroadcastReceiver();
        IntentFilter vbugendIntentFilter = new IntentFilter();
        vbugendIntentFilter.addAction(GlobalConstant.ACTION_MONITOR_VBUG_END);
        registerReceiver(vbugendReceiver, vbugendIntentFilter);

        vbugeventReceiver = new VbugEventBroadcastReceiver();
        IntentFilter vbugeventIntentFilter = new IntentFilter();
        vbugeventIntentFilter
                .addAction(GlobalConstant.ACTION_MONITOR_VBUG_EVENT);
        registerReceiver(vbugeventReceiver, vbugeventIntentFilter);

        videoReleaseReceiver = new VideoReleaseBroadcastReceiver();
        IntentFilter videoReleaseIntentFilter = new IntentFilter();
        videoReleaseIntentFilter.addAction(GlobalConstant.ACTION_VIDEO_RELEASE);
        registerReceiver(videoReleaseReceiver, videoReleaseIntentFilter);

        gisBroadcastReceiver = new GisBroadcastReceiver();
        IntentFilter gisIntentFilter = new IntentFilter();
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_RECTANGLEPOS);
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_TIMEPOS);
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_TRACEPOS);
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_DSTCURPOS);
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_ELLIPSEPOS);
        gisIntentFilter.addAction(GlobalConstant.ACTION_GIS_IND_D_GIS);
        registerReceiver(gisBroadcastReceiver, gisIntentFilter);

        logoutBroadcast = new LogoutBroadcast();
        IntentFilter logoutFilter = new IntentFilter();
        logoutFilter.addAction(GlobalConstant.ACTION_LOGOUT);
        registerReceiver(logoutBroadcast, logoutFilter);

        meetingIsMcuReceive = new MeetingIsMcuReceive();
        IntentFilter isMcuIntentFilter = new IntentFilter();
        isMcuIntentFilter.addAction(GlobalConstant.ACTION_MEETING_TYPE);
        registerReceiver(meetingIsMcuReceive, isMcuIntentFilter);

        mcuDecodeStartAndEndReceive = new MCUDecodeStartAndEndReceive();
        IntentFilter mcuDecodeStartAndEndReceiveFilter = new IntentFilter();
        mcuDecodeStartAndEndReceiveFilter.addAction(GlobalConstant.ACTION_MCUMEETING_STARTDecode);
        mcuDecodeStartAndEndReceiveFilter.addAction(GlobalConstant.ACTION_MCUMEETING_EndDecode);
        registerReceiver(mcuDecodeStartAndEndReceive, mcuDecodeStartAndEndReceiveFilter);

        cameraCloudControlReceive = new CameraCloudControlReceive();
        IntentFilter cameraCloudControlReceiveFilter = new IntentFilter();
        cameraCloudControlReceiveFilter.addAction(GlobalConstant.ACTION_CAMERA_CLOUDCONTROL);
        cameraCloudControlReceiveFilter.addAction(GlobalConstant.ACTION_CAMERA_HIDE_CLOUDCONTROL);
        registerReceiver(cameraCloudControlReceive, cameraCloudControlReceiveFilter);

        fileUpLoadResultReservice = new FileUpLoadResultReservice();
        IntentFilter fileUpLoadResultIntentFilter = new IntentFilter();
        fileUpLoadResultIntentFilter.addAction(ACTION_NOTIFICATIONMAINOLDMESSAGERESULT);
        fileUpLoadResultIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGSENDFILEERROR);
        registerReceiver(fileUpLoadResultReservice, fileUpLoadResultIntentFilter);

        regReceive = new RegReceive();
        IntentFilter regReceiveIntentFilter = new IntentFilter();
        regReceiveIntentFilter.addAction(GlobalConstant.ACTION_LOGINSTATE_Sws);
        registerReceiver(regReceive, regReceiveIntentFilter);


        mediaStatisticsReceive = new MediaStatisticsReceive();
        IntentFilter mediaStatisticsIntentFilter = new IntentFilter();
        mediaStatisticsIntentFilter.addAction(GlobalConstant.MEETING_STARTTIMER);
        mediaStatisticsIntentFilter.addAction(GlobalConstant.MEETING_STOPTIMER);
        mediaStatisticsIntentFilter.addAction(GlobalConstant.MEETING_SETFALSE);
        mediaStatisticsIntentFilter.addAction(GlobalConstant.MEETING_SETTRUE);
        registerReceiver(mediaStatisticsReceive, mediaStatisticsIntentFilter);

        decodingTheWallReceiver = new DecodingTheWallReceiver();
        IntentFilter decodingTheWallFilter = new IntentFilter();
        decodingTheWallFilter.addAction(GlobalConstant.DECODINGTHEWALLRECEIVER);
        registerReceiver(decodingTheWallReceiver, decodingTheWallFilter);


        receiverMapView = new ReceiverMapView();
        IntentFilter receiverMapViewFilter = new IntentFilter();
        receiverMapViewFilter.addAction(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
        registerReceiver(receiverMapView, receiverMapViewFilter);

        switchSplitScreenReceiver = new SwitchSplitScreenReceiver();
        IntentFilter switchSplitScreenReceiverFilter = new IntentFilter();
        switchSplitScreenReceiverFilter.addAction(GlobalConstant.SWITCHSPLITSCREEN_RECEIVER);
        registerReceiver(switchSplitScreenReceiver, switchSplitScreenReceiverFilter);

        groupInfoBroadCastReceiver = new GroupInfoBroadCastReceiver();
        IntentFilter groupInfoReceiverFilter = new IntentFilter();
        groupInfoReceiverFilter.addAction(GlobalConstant.ACTION_GROUPINFOREFRESH_TRESSDATE);
        registerReceiver(groupInfoBroadCastReceiver, groupInfoReceiverFilter);
    }

    private Communicator ic = null;
    private ObjectPrx base = null;

    //获取离线短信
    private void getOldMsgData() {

        if (ButtonUtils.isFastDoubleClick()) {
            Log.e(TAG, "1m内多次执行 视为无效");
            return;
        }
        Log.e(TAG, "执行getOldMsgData");

        voiceList = getVoiceList();

        //new OldMsgDataAsyncTask().execute();
        new OldMsgDataAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
    }

    private List<String> getVoiceList() {
        //音频编码列表获取
        speechList = new ArrayList<>();

        String data = prefs.getString("voiceList", "");
        Log.e("测试哎哎哎", "data : " + data);
        if (data != null && !data.equals("")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> list = gson.fromJson(data, listType);
            Log.e("测试哎哎哎", "list.size() : " + list.size());
            //speechList.addAll(list);
            String[] mArray = MediaEngine.GetInstance().ME_GetAudioCodecs();
            Log.e("测试哎哎哎", "mArray : " + mArray.length);
            for (int i = 0; i < mArray.length; i++) {
                if (!list.contains(mArray[i])) {
                    list.add(mArray[i]);
                    Log.e("测试哎哎哎", "list.add(mArray[i]); " + mArray[i]);
                }
            }

            speechList.addAll(list);
        } else {
            String[] strings = MediaEngine.GetInstance().ME_GetAudioCodecs();
            speechList = Arrays.asList(strings);
            Log.e("测试哎哎哎", "speechList.size() :  " + speechList.size());
        }
        return speechList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //主界面切换Tab图片监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Editor edit = prefs.edit();
        if (position != 1) {

            edit.putBoolean(GlobalConstant.ACTION_CALLSTATE, false);
            Log.e(TAG, "Main         添加为false");
        } else {
            edit.putBoolean(GlobalConstant.ACTION_CALLSTATE, true);
            Log.e(TAG, "Main         添加为true");
        }
        if (position != 2) {
            edit.putBoolean(GlobalConstant.ACTION_MEETINGSTATE, false);
        } else {
            edit.putBoolean(GlobalConstant.ACTION_MEETINGSTATE, true);

        }

        edit.apply();
        mImageAdapter.setImageLight(position);
        startActivity(position);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 取得通话SESSIONID
        int sessId = intent.getIntExtra(MtcCallDelegate.SESS_ID,
                MtcCliConstants.INVALIDID);
        // 中断则中断处理
        if (intent.getBooleanExtra(MtcCallDelegate.TERMED, false)) {
            int statCode = intent.getIntExtra(MtcCallDelegate.STAT_CODE, -1);
            mtcCallDelegateTermed(sessId, statCode);
        } else {
            if (sessId != MtcCliConstants.INVALIDID) {
                // 电源处理
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
                wl.acquire();
                // 为呼入
                mtcCallDelegateIncoming(sessId);
                wl.release();
            }
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void createMeetingsReceive(SessionCallInfos sessionCallInfos)
//    {
//
//        if (MeetingActivity.currMeeting == null) {
//            return;
//        }
//        // 创建成功
//        String sessnum = sessionCallInfos.getOthernum();
//        String cid = sessionCallInfos.getCid();
//        MeetingActivity.currMeeting.setConferenceNo(sessnum);
//        MeetingActivity.currMeeting.setcID(cid);
//        Log.e("MoveYourBaby", "main 1826 设置cid ： " + cid);
//    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        Log.e(TAG, "Main    onResume");


        if (mTabHost.getCurrentTabTag() == GlobalConstant.POSITION_TAB) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        Log.e(TAG, "Main onDestroy 程序退出!!!!!");
        //mMapView.onDestroy();

//        // 将登录信息保存于全局变量
//        Editor editor = prefs.edit();
//        editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN,
//                true);
//        editor.apply();




        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.cancel(2);


        //取消注册EventBus事件
        EventBus.getDefault().unregister(this);

        stopReceiveTime();
        //如果存在上墙视频  那么结束上墙的视频
        if (decodeMap.size() > 0) {
            for (String key : decodeMap.keySet()) {
                DecoderBean decoderBean = decodeMap.get(key);
                String sessionid = decoderBean.getSessionId();
                String decoderId = decoderBean.getDecoderId();
                String chanidString = decoderBean.getChanidStrings();
                //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
                selectedDecoderEnd(sessionid, key, decoderId, Integer.valueOf(chanidString));
            }
        }
        decodeMap.clear();
        monitorsSessionID.clear();
        if ((CallingManager.getInstance().getCallingDialog() != null)
                && (CallingManager.getInstance().getCallingDialog().size() > 0)) {
            for (WaitingDialog dialog : CallingManager.getInstance().getCallingDialog()) {
                CallingManager.getInstance().removeCallingDialog(dialog.getDwSessId());
                dialog.dismiss();
                dialog = null;
            }
        }
        // 通话挂断
        for (CallingInfo callinginfo : CallingManager.getInstance()
                .getCallingData()) {
            MtcDelegate.log("lizhiwei 销毁挂断");
            onEnd(callinginfo.getDwSessId());
        }
        CallingManager.getInstance().clear();
        // 结束视频
        Log.e(TAG, "Main==OnDestroy=====videos.size():" + videos.size());
        for (MonitorInfo monitorinfo : videos) {
            Log.e(TAG, "Main==OnDestroy=====遍历videos");
            if (monitorinfo == null) {
                Log.e(TAG, "Main==OnDestroy=====monitorinfo == null");
                continue;
            }
            if (monitorinfo.getMultiplayer() != null) {
                monitorinfo.getMultiplayer().stop();
                monitorinfo.getMultiplayer().close();
                monitorinfo.getMultiplayer().destroy();
            }
            if (monitorinfo.getTimer() != null) {
                monitorinfo.getTimer().stop();
                monitorinfo.getTimer().setText("");
            }
            // 如果是监控，则发送结束监控
            if (monitorinfo.isIsmonitor()) {
                MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
            }
        }
        //new MultiPlayer().cleanup();
        videos.clear();

        // 结束响铃
        MtcRing.stop();
        // 结束弱点锁屏
        MtcProximity.stop();
        // 清空通话回调
        if (MtcCallDelegate.getCallback() == this) {
            MtcCallDelegate.setCallback(null);
        }
        MtcDelegate.unregisterCallback(this);
        // 清空视频回调
        MtcAnzCb.setCallback(null);

        // 消息回调
        unregisterReceiver(receiverMemberStateChanged);
        unregisterReceiver(didloginReceiver);
        unregisterReceiver(heartbeatReceiver);
        unregisterReceiver(selfstatusReceiver);
        unregisterReceiver(reregisterReceiver);
        unregisterReceiver(offflineReceiver);
        unregisterReceiver(onlineReceiver);
        unregisterReceiver(callBroadcastReceiver);
        unregisterReceiver(messageBroadcastReceiver);
        unregisterReceiver(callingRecordReceiver);
        unregisterReceiver(answerNextCallReceiver);
        unregisterReceiver(meetingCreateReceiver);
        unregisterReceiver(meetingResultReceiver);
        unregisterReceiver(meetingMemWithDrawReceiver);
        unregisterReceiver(pushVideoResultReceiver);
        unregisterReceiver(meetingRefreshReceiver);
        unregisterReceiver(createVideoReceiver);
        unregisterReceiver(removeVideoReceiver);
        unregisterReceiver(meetingHangupReceiver);
        unregisterReceiver(getvideoReceiver);
        unregisterReceiver(getvideoevtReceiver);
        unregisterReceiver(vbugstartReceiver);
        unregisterReceiver(vbugendReceiver);
        unregisterReceiver(vbugeventReceiver);
        unregisterReceiver(videoReleaseReceiver);
        unregisterReceiver(gisBroadcastReceiver);
        unregisterReceiver(meetingIsMcuReceive);
        unregisterReceiver(mcuDecodeStartAndEndReceive);
        unregisterReceiver(cameraCloudControlReceive);
        unregisterReceiver(fileUpLoadResultReservice);
        unregisterReceiver(regReceive);
        unregisterReceiver(mediaStatisticsReceive);
        unregisterReceiver(decodingTheWallReceiver);
        unregisterReceiver(receiverMapView);
        unregisterReceiver(switchSplitScreenReceiver);
        unregisterReceiver(groupInfoBroadCastReceiver);


        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        if (mMapView != null) {
            mBaiduMap.setMyLocationEnabled(false);
            try {
                mMapView.onDestroy();
                mMapView = null;
            } catch (Exception e) {
            }

        }
        // 停止服务
        stopService(new Intent(MainActivity.this, PTTService.class));

        //销毁短信铃声
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
        }

        if (musicTimer != null) {
            musicTimer.cancel();
            musicTimer = null;
        }
        super.onDestroy();
        Log.e(TAG, "执行完onDestroy Main  1987");
    }

    /* 界面关闭 */
    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());

        if (mWindowView != null) {
            //移除悬浮窗口
            Log.e(TAG, "removeView");
            mWindowManager.removeView(mWindowView);

            mWindowView = null;
        }
        isFloating = false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (event.getRepeatCount() == 0)) {
                    // 提示是否退出
                    askIfExit();
                }
                return true;
            default:
                boolean value = false;
                try {
                    value = super.dispatchKeyEvent(event);
                } catch (IllegalArgumentException ex) {

                }
                return value;
        }
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

    /**
     * 定位SDK监听函数    没有用到MyLocationListenner类
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
			/*
			 * // map view 销毁后不在处理新接收的位置 if (location == null || mMapView ==
			 * null) return; MyLocationData locData = new
			 * MyLocationData.Builder() .accuracy(location.getRadius()) //
			 * 此处设置开发者获取到的方向信息，顺时针0-360
			 * .direction(100).latitude(location.getLatitude())
			 * .longitude(location.getLongitude()).build();
			 * mBaiduMap.setMyLocationData(locData); if (isFirstLoc) {
			 * isFirstLoc = false; LatLng ll = new
			 * LatLng(location.getLatitude(), location.getLongitude());
			 * MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			 * mBaiduMap.animateMapStatus(u); }
			 */
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    // 初始化布局
    private void InitView() {
        // 初始化按钮图标
        mImageNormalIds = new int[]{intercom, R.drawable.calling,
                R.drawable.meeting, R.drawable.message, R.drawable.contacts,
                location, monitor, R.drawable.setting};
        mImageSelectedIds = new int[]{R.drawable.intercom1,
                R.drawable.calling1, R.drawable.meeting1, R.drawable.message1,
                R.drawable.contacts1, R.drawable.location1,
                R.drawable.monitor1, R.drawable.setting1};

//        tv_TongjiInfo = (MarqueeTextView) findViewById(R.id.tv_TongjiInfo);
//        tv_TongjiInfo2 = (MarqueeTextView) findViewById(R.id.tv_TongjiInfo2);
//        boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);
//        if (mediastatistics){
//            tv_TongjiInfo.setVisibility(View.VISIBLE);
//            tv_TongjiInfo2.setVisibility(View.VISIBLE);
//        }else {
//            tv_TongjiInfo.setVisibility(View.GONE);
//            tv_TongjiInfo2.setVisibility(View.GONE);
//        }
//        tv_TongjiInfo.setSelected(true);
//        tv_TongjiInfo2.setSelected(true);

        //通知当前GIS界面数据
        map_memberContent = (TextView) findViewById(R.id.map_MemberContent);
        // 取得控件
        imgUserStates = (ImageView) findViewById(R.id.imgUserStates);
        txtUserNO = (TextView) findViewById(R.id.txtUserNO);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtCurrHour = (TextView) findViewById(R.id.txtCurrHour);
        txtTimeSeparator1 = (TextView) findViewById(R.id.txtTimeSeparator1);
        txtCurrMinutes = (TextView) findViewById(R.id.txtCurrMinutes);
        txtTimeSeparator2 = (TextView) findViewById(R.id.txtTimeSeparator2);
        txtCurrSeconds = (TextView) findViewById(R.id.txtCurrSeconds);
        txtCurrDate = (TextView) findViewById(R.id.txtCurrDate);
        mTabHost = getTabHost();
        tabcontent = mTabHost.getTabContentView();
        mGridView = (GridView) findViewById(R.id.gridView_title);
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        ll_position = (RelativeLayout) findViewById(R.id.ll_position);
		/* fl_calling = (FrameLayout) findViewById(R.id.fl_calling); */
        sl_videos = (ScrollView) findViewById(R.id.sl_videos);
        ll_videos = (LinearLayout) findViewById(R.id.ll_videos);
        rl_bottomtools = (RelativeLayout) findViewById(R.id.rl_bottomtools);
        ll_split = (LinearLayout) findViewById(R.id.ll_split);
        ll_pos = (LinearLayout) findViewById(R.id.ll_pos);
        ll_pos_single = (LinearLayout) findViewById(R.id.ll_pos_single);
        btn_gone = (Button) findViewById(R.id.btn_gone);
        btn_one = (Button) findViewById(R.id.btn_one);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_nine = (Button) findViewById(R.id.btn_nine);
        btn_info = (Button) findViewById(R.id.btn_info);
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_pushvideo = (Button) findViewById(R.id.btn_pushvideo);
        rl_positiontools = (RelativeLayout) findViewById(R.id.rl_positiontools);
        btn_decodingupperwall = (Button) findViewById(R.id.btn_decodingupperwall);
        btn_decodingupperwa = (Button) findViewById(R.id.btn_decodingupperwa);
        // 设置控件字体
        txtUserNO.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        txtTitle.setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        txtCurrHour
                .setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        txtTimeSeparator1.setTypeface(CommonMethod.getTypeface(this
                .getBaseContext()));
        txtCurrMinutes.setTypeface(CommonMethod.getTypeface(this
                .getBaseContext()));
        txtTimeSeparator2.setTypeface(CommonMethod.getTypeface(this
                .getBaseContext()));
        txtCurrSeconds.setTypeface(CommonMethod.getTypeface(this
                .getBaseContext()));
        txtCurrDate
                .setTypeface(CommonMethod.getTypeface(this.getBaseContext()));
        // 设置默认值
        final SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        txtUserNO.setText(sipUser.getUsername());
        // 设置分页
        mIntercomIntent = new Intent(this, IntercomActivity.class);
        mCallingIntent = new Intent(this, CallingActivity.class);
        mMeetingIntent = new Intent(this, MeetingActivity.class);
        mMessageIntent = new Intent(this, MessageActivity.class);
        mContactIntent = new Intent(this, ContactActivity.class);
        mPositionIntent = new Intent(this, PositionActivity.class);
        mMonitorIntent = new Intent(this, MonitorsActivity.class);
        mSettingIntent = new Intent(this, SettingActivity.class);
        // 设置地图和监控显示
        ll_position.setVisibility(View.VISIBLE);
        rl_positiontools.setVisibility(View.VISIBLE);
		/* fl_calling.setVisibility(View.GONE); */
        sl_videos.setVisibility(View.GONE);
        ll_split.setVisibility(View.GONE);
        btn_info.setVisibility(View.GONE);
        btn_close.setVisibility(View.GONE);
        btn_pushvideo.setVisibility(View.GONE);
        btn_decodingupperwall.setVisibility(View.GONE);
        btn_decodingupperwa.setVisibility(View.GONE);
        // 百度地圖（xml引用控件）
        //mMapView = (MapView) findViewById(R.id.bmapView);

        initBaiDuMap(); //初始化

        // 动态加载地图
        // BaiduMapOptions baiduMapOptions=new BaiduMapOptions();
        // baiduMapOptions.rotateGesturesEnabled(false);//禁止旋转屏幕
        // mMapView=new MapView(this,baiduMapOptions);
        // mMapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
        // LayoutParams.MATCH_PARENT));
        // ll_position.addView(mMapView);

        // 获取map对象
        //mBaiduMap = mMapView.getMap();
        //百度地图功能控件
        singleLinearLayout = (LinearLayout) findViewById(R.id.singleLinearLayout);
        multipleLinearLayout = (LinearLayout) findViewById(R.id.multipleLinearLayout);
        filterLinearLayout = (LinearLayout) findViewById(R.id.filterLinearLayout);
        trackLinearLayout = (LinearLayout) findViewById(R.id.trackLinearLayout);
        traceLinearLayout = (LinearLayout) findViewById(R.id.traceLinearLayout);
        cleanLinearLayout = (LinearLayout) findViewById(R.id.cleanLinearLayout);


        btn_clean = (TextView) findViewById(R.id.btn_clean);
        btn_trace = (TextView) findViewById(R.id.btn_trace);
        btn_track = (TextView) findViewById(R.id.btn_track);
        btn_filter = (TextView) findViewById(R.id.btn_filter);
        btn_multiple = (TextView) findViewById(R.id.btn_multiple);
        btn_single = (TextView) findViewById(R.id.btn_single);

        btnPosBroadcast = (TextView) findViewById(R.id.btnPosBroadcast);
        btnPosMeeting = (TextView) findViewById(R.id.btnPosMeeting);
        btnPosTalk = (TextView) findViewById(R.id.btnPosTalk);
        btnPosMessage = (TextView) findViewById(R.id.btnPosMessage);

        btnPosOnVoice = (TextView) findViewById(R.id.btnPosOnVoice);
        btnPosOnVideo = (TextView) findViewById(R.id.btnPosOnVideo);
        btnPosOnMessage = (TextView) findViewById(R.id.btnPosOnMessage);

        ll_trackplay = (RelativeLayout) findViewById(R.id.ll_trackplay);
        txtPosCurrHour = (TextView) findViewById(R.id.txtPosCurrHour);
        txtPosCurrHour_day = (TextView) findViewById(R.id.txtPosCurrHour_day);
        txtPosCurrMinute = (TextView) findViewById(R.id.txtPosCurrMinute);
        txtPosCurrSecond = (TextView) findViewById(R.id.txtPosCurrSecond);
        btnPosTrackPause = (TextView) findViewById(R.id.btnPosTrackPause);
        btnPosTrackExit = (TextView) findViewById(R.id.btnPosTrackExit);
        txtPosSpeed1 = (TextView) findViewById(R.id.txtPosSpeed1);
        txtPosSpeed2 = (TextView) findViewById(R.id.txtPosSpeed2);
        txtPosSpeed3 = (TextView) findViewById(R.id.txtPosSpeed3);
        proPosProgress = (ProgressBar) findViewById(R.id.proPosProgress);
        txtPosProgress = (TextView) findViewById(R.id.txtPosProgress);
		/*解码上墙下拉列表*/
        //selectedDcod=(Spinner)findViewById(R.id.selectedDcod);
        //selectedDcod=(Spinner)layout.findViewById(R.id.selectedDcod);
		/*mUiSettings = mBaiduMap.getUiSettings();
		//是否启用缩放手势
		mUiSettings.setZoomGesturesEnabled(true);
		//是否启用平移手势
		mUiSettings.setScrollGesturesEnabled(true);
		//是否启用旋转手势
		mUiSettings.setRotateGesturesEnabled(false);
		//是否启用俯视手势
		mUiSettings.setOverlookingGesturesEnabled(false);
		//是否启用指南针图层
		mUiSettings.setCompassEnabled(false);*/


    }

    //百度地图初始化
    private void initBaiDuMap() {
        // TODO Auto-generated method stub



        mOffline = new MKOfflineMap();
        mOffline.init(this);
        int num = mOffline.importOfflineData();
        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        if (localMapList != null && localMapList.size() > 0) {
            ll_position.removeAllViews();
            int offlineMapCityId = prefs.getInt(GlobalConstant.SP_OFFLINEMAP_CITYID, -1);
            if (offlineMapCityId == -1) {
                MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                ll_position.addView(mMapView);
                int cityID = mkolUpdateElement.cityID;
                ptt_3g_PadApplication.setOfflineMapCityId(cityID);

                editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                editor.apply();
            } else {
                MKOLUpdateElement mk = null;
                for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                    if (mkolUpdateElement.cityID == offlineMapCityId) {
                        mk = mkolUpdateElement;
                    }
                }
                if (mk == null) {
                    MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                    mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                    ll_position.addView(mMapView);
                    int cityID = mkolUpdateElement.cityID;
                    ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                    editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                    editor.apply();
                } else {
                    mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mk.geoPt).build()));
                    ll_position.addView(mMapView);
                    int cityID = mk.cityID;
                    ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                }
            }

            mBaiduMap = mMapView.getMap();
            mMapView.invalidate();
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
            mBaiduMap.setOnMapLoadedCallback(this);
            mBaiduMap.setOnMapStatusChangeListener(this);
            // 删除百度地图logo
            mMapView.removeViewAt(1);
            View child = mMapView.getChildAt(1);
            // 隐藏缩放控件
            child.setVisibility(View.GONE);
            // 隐藏比例尺
            if (mMapView != null) {

                if (mMapView.getChildCount() > 1) {
                    mMapView.removeViewAt(2);
                }
            }

            mUiSettings = mBaiduMap.getUiSettings();
            //是否启用缩放手势
            mUiSettings.setZoomGesturesEnabled(true);
            //是否启用平移手势
            mUiSettings.setScrollGesturesEnabled(true);
            //是否启用旋转手势
            mUiSettings.setRotateGesturesEnabled(false);
            //是否启用俯视手势
            mUiSettings.setOverlookingGesturesEnabled(false);
            //是否启用指南针图层
            mUiSettings.setCompassEnabled(false);
        } else {
            mMapView = (MapView) findViewById(R.id.bmapView);
            mBaiduMap = mMapView.getMap();
            mMapView.invalidate();
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
            mBaiduMap.setOnMapLoadedCallback(this);
            mBaiduMap.setOnMapStatusChangeListener(this);
            mMapView.removeViewAt(1);
            View child = mMapView.getChildAt(1);
            // 隐藏缩放控件
            child.setVisibility(View.GONE);
            // 隐藏比例尺
            if (mMapView.getChildCount() > 2) {
                mMapView.removeViewAt(2);
            }
            mUiSettings = mBaiduMap.getUiSettings();
            //是否启用缩放手势
            mUiSettings.setZoomGesturesEnabled(true);
            //是否启用平移手势
            mUiSettings.setScrollGesturesEnabled(true);
            //是否启用旋转手势
            mUiSettings.setRotateGesturesEnabled(false);
            //是否启用俯视手势
            mUiSettings.setOverlookingGesturesEnabled(false);
            //是否启用指南针图层
            mUiSettings.setCompassEnabled(false);
        }

        //地图上成员的单击事件
        if (mBaiduMap != null) {
            mBaiduMap.setOnMarkerClickListener(null);
            mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker arg0) {
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        ToastUtils.showToast(MainActivity.this,
                                getString(R.string.info_network_unavailable));
                        return false;
                    }

                    if (isLoadMarker){
                        ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                        return false;
                    }

                    Log.e("Gis添加图标", "地图上点击成员 2548 posSelType : " + posSelType);
                    PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
                            .getExtraInfo().get("pmi");
                    if (posSelType == 1) {
                        Log.e("PTT_3G_Pad_PTTService", "1");
                        if (mBaiduMap != null) {
                            mBaiduMap.clear();// 清空地图
                        }
                        if (!checkBoxMarker.contains(posMarkerInfo)) {
                            checkBoxMarker.clear();// 清空，只能选择一个
                        }

                        for (PosMarkerInfo pmi : posMarkerInfosList) {
                            if (pmi.getName() == posMarkerInfo.getName()) {
                                if (!checkBoxMarker.contains(pmi)) {
                                    Log.e("Gis添加图标","initBaiDuMap 2531");
                                    addPosOverlay(pmi, true);

                                    checkBoxMarker.add(pmi);// 添加到选择列表
                                } else {
                                    Log.e("Gis添加图标","initBaiDuMap 2535");
                                    addPosOverlay(pmi, false);

                                    checkBoxMarker.remove(pmi);// 添加到选择列表
                                }
                            } else {
                                addPosOverlay(pmi, false);
                                Log.e("GIS模块","initBaiDuMap 2540");
                            }
                        }
                    } else if (posSelType == 2) {
                        //TODO Marker的点击事件 点击Marker的话会先将当前Marker进行remove 在调用添加Marker的方法重新进行添加
                        if (hasMark.containsKey(posMarkerInfo.getName())) {
                            Marker marker = hasMark.get(posMarkerInfo.getName());
                            marker.remove();
                            hasMark.remove(posMarkerInfo.getName());
                            if (!checkBoxMarker.contains(posMarkerInfo)) {
                                checkBoxMarker.add(posMarkerInfo);
                                addPosOverlay(posMarkerInfo, true);   //这个就是添加Marker的方法 第一个参数是Marker的实体Bean 记录Marker上的一些信息
                                // 第二个是通过这个参数来设置不同的图标
                                Log.e("GIS模块", "initBaiDuMap 2550");
                            } else {
                                checkBoxMarker.remove(posMarkerInfo);
                                addPosOverlay(posMarkerInfo, false);
                                Log.e("GIS模块", "initBaiDuMap 2554");
                            }
                        }
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();
//                                }
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }}
                    } else if (posSelType == 3) {
                        if (mBaiduMap != null) {
                            mBaiduMap.clear();
                        }// 清空地图
                        for (PosMarkerInfo pmi : posMarkerInfosList) {
                            if (pmi.getName() == posMarkerInfo.getName()) {
                                addPosOverlay(pmi, false);
                                Log.e("GIS模块","initBaiDuMap 2584");
                                checkBoxMarker.remove(pmi);
                            } else {
                                if (!checkBoxMarker.contains(pmi)) {
                                    addPosOverlay(pmi, false);
                                    Log.e("GIS模块","initBaiDuMap 2589");
                                } else {
                                    addPosOverlay(pmi, true);
                                    Log.e("GIS模块","initBaiDuMap 2592");
                                }
                            }
                        }
                    }
                    //Log.e("选择的成员数量", selPosMarkerInfos.size()+"");
                    //                       for (int i = 0; i < selPosMarkerInfos.size(); i++) {
                    //						Log.e("列表", selPosMarkerInfos.get(i).getName());
                    //					}
                    if (checkBoxMarker.size() == 1) {
                        ll_pos_single.setVisibility(View.VISIBLE);
                        ll_pos.setVisibility(View.GONE);
                    } else if (checkBoxMarker.size() > 1) {
                        ll_pos_single.setVisibility(View.GONE);
                        ll_pos.setVisibility(View.VISIBLE);
                    } else {
                        ll_pos_single.setVisibility(View.GONE);
                        ll_pos.setVisibility(View.GONE);
                    }

                    return false;
                }
            });
        }

    }

    //百度地图初始化
    private void initBaiDuMap2() {
        // TODO Auto-generated method stub
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        int num = mOffline.importOfflineData();
        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        if (localMapList != null && localMapList.size() > 0) {
            ll_position.removeAllViews();
            int offlineMapCityId = prefs.getInt(GlobalConstant.SP_OFFLINEMAP_CITYID, -1);
            if (offlineMapCityId == -1) {
                MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                ll_position.addView(mMapView);
                int cityID = mkolUpdateElement.cityID;
                ptt_3g_PadApplication.setOfflineMapCityId(cityID);

                editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                editor.apply();
            } else {
                MKOLUpdateElement mk = null;
                for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                    if (mkolUpdateElement.cityID == offlineMapCityId) {
                        mk = mkolUpdateElement;
                    }
                }
                if (mk == null) {
                    MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                    mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                    ll_position.addView(mMapView);
                    int cityID = mkolUpdateElement.cityID;
                    ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                    editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                    editor.apply();
                } else {
                    mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mk.geoPt).build()));
                    ll_position.addView(mMapView);
                    int cityID = mk.cityID;
                    ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                }
            }

            mBaiduMap = mMapView.getMap();
            mMapView.invalidate();
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
            mBaiduMap.setOnMapLoadedCallback(this);
            mBaiduMap.setOnMapStatusChangeListener(this);
            // 删除百度地图logo
            mMapView.removeViewAt(1);
            View child = mMapView.getChildAt(1);
            // 隐藏缩放控件
            child.setVisibility(View.GONE);
            // 隐藏比例尺
            if (mMapView != null) {

                if (mMapView.getChildCount() > 1) {
                    mMapView.removeViewAt(2);
                }
            }

            mUiSettings = mBaiduMap.getUiSettings();
            //是否启用缩放手势
            mUiSettings.setZoomGesturesEnabled(true);
            //是否启用平移手势
            mUiSettings.setScrollGesturesEnabled(true);
            //是否启用旋转手势
            mUiSettings.setRotateGesturesEnabled(false);
            //是否启用俯视手势
            mUiSettings.setOverlookingGesturesEnabled(false);
            //是否启用指南针图层
            mUiSettings.setCompassEnabled(false);
        } else {
            mMapView = new MapView(this);
            mBaiduMap = mMapView.getMap();
            mMapView.invalidate();
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
            mBaiduMap.setOnMapLoadedCallback(this);
            mBaiduMap.setOnMapStatusChangeListener(this);
            mMapView.removeViewAt(1);
            View child = mMapView.getChildAt(1);
            // 隐藏缩放控件
            child.setVisibility(View.GONE);
            // 隐藏比例尺
            if (mMapView.getChildCount() > 2) {
                mMapView.removeViewAt(2);
            }
            mUiSettings = mBaiduMap.getUiSettings();
            //是否启用缩放手势
            mUiSettings.setZoomGesturesEnabled(true);
            //是否启用平移手势
            mUiSettings.setScrollGesturesEnabled(true);
            //是否启用旋转手势
            mUiSettings.setRotateGesturesEnabled(false);
            //是否启用俯视手势
            mUiSettings.setOverlookingGesturesEnabled(false);
            //是否启用指南针图层
            mUiSettings.setCompassEnabled(false);
        }

        //地图上成员的单击事件
        if (mBaiduMap != null) {
            mBaiduMap.setOnMarkerClickListener(null);
            mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker arg0) {
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        ToastUtils.showToast(MainActivity.this,
                                getString(R.string.info_network_unavailable));
                        return false;
                    }
                    if (isLoadMarker){
                        ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                        return false;
                    }
                    Log.e("Gis添加图标", "地图上点击成员 2768 posSelType : " + posSelType);
                    PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
                            .getExtraInfo().get("pmi");
                    if (posSelType == 1) {
                        Log.e("PTT_3G_Pad_PTTService", "1");
                        if (mBaiduMap != null) {
                            mBaiduMap.clear();// 清空地图
                        }
                        if (!checkBoxMarker.contains(posMarkerInfo)) {
                            checkBoxMarker.clear();// 清空，只能选择一个
                        }

                        for (PosMarkerInfo pmi : posMarkerInfosList) {
                            if (pmi.getName() == posMarkerInfo.getName()) {
                                if (!checkBoxMarker.contains(pmi)) {
                                    Log.e("Gis添加图标", "initBaiDuMap 2531");
                                    addPosOverlay(pmi, true);

                                    checkBoxMarker.add(pmi);// 添加到选择列表
                                } else {
                                    Log.e("Gis添加图标", "initBaiDuMap 2535");
                                    addPosOverlay(pmi, false);

                                    checkBoxMarker.remove(pmi);// 添加到选择列表
                                }
                            } else {
                                addPosOverlay(pmi, false);
                                Log.e("GIS模块", "initBaiDuMap 2540");
                            }
                        }
                    } else if (posSelType == 2) {
                        if (hasMark.containsKey(posMarkerInfo.getName())) {
                            Marker marker = hasMark.get(posMarkerInfo.getName());
                            marker.remove();
                            hasMark.remove(posMarkerInfo.getName());
                            if (!checkBoxMarker.contains(posMarkerInfo)) {
                                checkBoxMarker.add(posMarkerInfo);
                                addPosOverlay(posMarkerInfo, true);
                                Log.e("GIS模块", "initBaiDuMap 2550");
                            } else {
                                checkBoxMarker.remove(posMarkerInfo);
                                addPosOverlay(posMarkerInfo, false);
                                Log.e("GIS模块", "initBaiDuMap 2554");
                            }
                        }
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();
//                                }
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }
                    } else if (posSelType == 3) {
                        if (mBaiduMap != null) {
                            mBaiduMap.clear();
                        }// 清空地图
                        for (PosMarkerInfo pmi : posMarkerInfosList) {
                            if (pmi.getName() == posMarkerInfo.getName()) {
                                addPosOverlay(pmi, false);
                                Log.e("GIS模块", "initBaiDuMap 2584");
                                checkBoxMarker.remove(pmi);
                            } else {
                                if (!checkBoxMarker.contains(pmi)) {
                                    addPosOverlay(pmi, false);
                                    Log.e("GIS模块", "initBaiDuMap 2589");
                                } else {
                                    addPosOverlay(pmi, true);
                                    Log.e("GIS模块", "initBaiDuMap 2592");
                                }
                            }
                        }
                    }
                    //Log.e("选择的成员数量", selPosMarkerInfos.size()+"");
                    //                       for (int i = 0; i < selPosMarkerInfos.size(); i++) {
                    //						Log.e("列表", selPosMarkerInfos.get(i).getName());
                    //					}
                    if (checkBoxMarker.size() == 1) {
                        ll_pos_single.setVisibility(View.VISIBLE);
                        ll_pos.setVisibility(View.GONE);
                    } else if (checkBoxMarker.size() > 1) {
                        ll_pos_single.setVisibility(View.GONE);
                        ll_pos.setVisibility(View.VISIBLE);
                    } else {
                        ll_pos_single.setVisibility(View.GONE);
                        ll_pos.setVisibility(View.GONE);
                    }

                    return false;
                }
            });
        }

            if (ll_position.getParent() != null) {
                ll_position.removeAllViews();
            }
            ll_position.addView(mMapView);
            ll_position.setVisibility(View.VISIBLE);


            String msgBodyString = "req:cur_pos\r\nemployeeid:" + sipUser.getUsername() + "\r\ndstid:" + sipUser.getUsername() + "\r\n";
            MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);

            ptt_3g_PadApplication.setBaiduMapisRemove(false);

    }

    // 初始化数据
    private void InitData() {
        // 取得登录用户信息
        sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        // 文件服务器登    录   11/1 sws
        //InstantClient.instance().ConnectSever(sipServer.getServerIp());

        // 设置时间显示     //11/06
        if (handler != null) {
            handler.post(updateThread);
        } else {
            handler = new Handler();
            handler.post(updateThread);
        }

        HeartbeatManager.getInstance().putHeartbeat(
                Calendar.getInstance().getTime());
        // 设置标题内容
        mGridView.setNumColumns(1);
        mImageAdapter = new ImageAdapter();

        mGridView.setAdapter(mImageAdapter);
        mGridView.setOnItemClickListener(this);
        try {
            // 设置显示页面
            mTabHost.addTab(buildTabSpec(GlobalConstant.INTERCOM_TAB,
                    mIntercomIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.CALLING_TAB, mCallingIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.MEETING_TAB, mMeetingIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.MESSAGE_TAB, mMessageIntent).setIndicator("短信", getResources().getDrawable(R.drawable.message_yuan)));
            mTabHost.addTab(buildTabSpec(GlobalConstant.CONTACT_TAB, mContactIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.POSITION_TAB,
                    mPositionIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.MONITOR_TAB, mMonitorIntent));
            mTabHost.addTab(buildTabSpec(GlobalConstant.SETTING_TAB, mSettingIntent));
        } catch (Exception ex) {
            Log.e(TAG, "2379 Error : " + ex.getMessage());
        }
        startActivity(0);
        // 设置显示隐藏
        btn_gone.setOnClickListener(new BtnOnClickListener());
        // 设置一分屏
        btn_one.setOnClickListener(new BtnOnClickListener());
        // 设置四分屏
        btn_four.setOnClickListener(new BtnOnClickListener());
        // 设置九分屏
        btn_nine.setOnClickListener(new BtnOnClickListener());
        // 设置视频信息
        btn_info.setOnClickListener(new BtnOnClickListener());
        // 设置视频关闭
        btn_close.setOnClickListener(new BtnOnClickListener());
        //设置推送视频
        btn_pushvideo.setOnClickListener(new BtnOnClickListener());
        //设置开始上墙按钮
        btn_decodingupperwall.setOnClickListener(new BtnOnClickListener());
        //设置结束上墙按钮
        btn_decodingupperwa.setOnClickListener(new BtnOnClickListener());

		/*点击显示成员列表 YUEZS add*/
        imgMultip = (ImageButton) findViewById(R.id.imageButton1);
        employeeLayout = (LinearLayout) findViewById(R.id.employeeList);
        //employeeListView = (ListView) findViewById(R.id.list_MapMember);
        imgMultip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (employeeLayout.getVisibility() != View.VISIBLE) {
                    employeeLayout.setVisibility(View.VISIBLE);
                    //employeeListView.setVisibility(View.VISIBLE);
                    //设置地图按钮状态 yuezs add 2015-10-14
                    switchPosButtonStatus(2);
                } else {
                    employeeLayout.setVisibility(View.GONE);
                    //employeeListView.setVisibility(View.GONE);
                    //设置地图按钮状态 yuezs add 2015-10-14
                    switchPosButtonStatus(1);
                }
            }
        });
		/*点击显示成员列表结束 YUEZS add*/
        //		selectedDcod.setOnItemSelectedListener(new  OnItemSelectedListener() {
        //		});
    }

    //百度地图
    private void initBaiduMap() {
        // TODO Auto-generated method stub
		/* 百度地图部分开始 */
        // 使用默认定位图标
        mCurrentMode = LocationMode.NORMAL;// 普通
        // mCurrentMode = LocationMode.FOLLOWING;//跟随
        // mCurrentMode = LocationMode.COMPASS;//罗盘
        // mBaiduMap.setMyLocationConfigeration(new
        // MyLocationConfiguration(mCurrentMode, true, null));

        // 自定义定位图标
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        //if (mBaiduMap != null) {
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //}
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(30000);// 定位时间间隔(ms)
        mLocClient.setLocOption(option);
        mLocClient.start();

        // 向服务器发送请求，获取好友地址位置信息
        singleLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }

                switchPosButtonStatus(1);
            }
        });
        multipleLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }
                switchPosButtonStatus(2);
                showMultipleWindow(v);
            }
        });

        filterLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }
                switchPosButtonStatus(3);
                showFilterWindow(v);
            }
        });
        trackLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }
                Log.e(TAG, "点击了轨迹按钮");
                switchPosButtonStatus(4);
                showTrackWindow(v);
            }
        });

        traceLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }
                Log.e(TAG, "点击了追踪按钮");
                Log.e("Main=======1689======", "点击追踪监听LinearLayout监听");
                if (!isTrace) {
                    Log.e(TAG, "isTrace为false");
                    switchPosButtonStatus(5);
                    showTraceWindow(v);
                } else {
                    Log.e(TAG, "true");
                    if (ptt_3g_PadApplication.isNetConnection() == false) {
                        ToastUtils.showToast(MainActivity.this,
                                getString(R.string.info_network_unavailable));
                        return;
                    }

                    // 取消追踪
					/*String uri = MtcUri.Mtc_UriFormatX(
							GlobalConstant.PTT_MSG_SERVER_ID, false);*/
                    String dstid = CommonMethod
                            .listToString(addUserNoList, ",");
                    String msgBodyString = "req:trace_pos\r\nsid:"
                            + UUID.randomUUID() + "\r\nemployeeid:"
                            + sipUser.getUsername() + "\r\ndstid:"
                            + dstid + "\r\nspace:5\r\nflag:2\r\n";
                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    Log.e(TAG, "发送msg消息    msgbodystring：" + msgBodyString);

                }
            }
        });

        cleanLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                    return ;
                }
                switchPosButtonStatus(6);
                // 清除页面所有，然后重新加载所有Marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posHashMap != null) {
                    posHashMap.clear();
                }
                for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                    CheckBox checkBox = (CheckBox) employeeLayout.getChildAt(i);
                    checkBox.setChecked(false);
                }

                if (ll_pos_single.getVisibility() != View.GONE) {
                    ll_pos_single.setVisibility(View.GONE);
                }
                if (ll_pos.getVisibility() != View.GONE) {
                    ll_pos.setVisibility(View.GONE);
                }

                isCheck = true;

            }
        });

        btnPosBroadcast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 广播
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }

                //为了防止发起呼叫过于频繁
                if (ptt_3g_PadApplication != null) {
                    if (ptt_3g_PadApplication.isWetherClickCall() == false) {
                        ptt_3g_PadApplication.setWetherClickCall(true);
                        boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
                        if (wetherTimerWorking == true) {
                            ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                            //返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
                            return;
                        } else {

                            //返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
                            ptt_3g_PadApplication.startTimer();
                        }
                    } else {
                        ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                        return;
                    }
                }

				/*CallingInfo callingMeeting = null;
				for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
					if ((!callingInfo.isHolding())){
						if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
							callingMeeting = callingInfo;
						}
					}
				}
				if(callingMeeting != null){
					return;
				}*/

                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择广播联系人");
                    return;
                }
                if (CallingManager.getInstance().getCallingCount() >= 4) {
                    ToastUtils.showToast(
                            MainActivity.this,
                            getResources().getString(
                                    R.string.info_meeting_route_busy));
                    return;
                }

                // 设置广播成员
                ArrayList<String> nos = new ArrayList<String>();
                for (PosMarkerInfo info : checkBoxMarker) {
                    nos.add(info.getName());
                }
                //如果当前已经有单呼，禁止发起新的单呼
                CallingInfo callingCalling = null;
                CallingInfo callingMeeting = null;
                CallingInfo callingIntercom = null;
                for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
                    if ((!callingInfo.isHolding())) {
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                            callingCalling = callingInfo;
                        }
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                            callingMeeting = callingInfo;
                        }
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                            callingIntercom = callingInfo;
                        }
                    }
                }
                if (callingCalling != null) {
                    showMeetingIfContinueString(callingCalling, nos, GlobalConstant.BROADCAST_TAB);
                    return;
                }
                if (callingMeeting != null) {
                    showMeetingIfContinueString(callingMeeting, nos, GlobalConstant.BROADCAST_TAB);
                    return;
                }
                if (callingIntercom != null) {
                    showMeetingIfContinueString(callingIntercom, nos, GlobalConstant.BROADCAST_TAB);
                    return;
                }

                // 切换页面
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                    mImageAdapter.setImageLight(2);
                    startActivity(2);
                }

                Intent callIntent = new Intent(
                        GlobalConstant.ACTION_POSITION_OPERATE);
                callIntent.putExtra(GlobalConstant.KEY_POSITION_TYPE,
                        GlobalConstant.BROADCAST_TAB);
                callIntent.putExtra(GlobalConstant.KEY_MEETING_NOS, nos);
                sendBroadcast(callIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posMarkerInfosList != null) {
                    for (PosMarkerInfo pmi : posMarkerInfosList) {
                        addPosOverlay(pmi, false);
                        Log.e("GIS模块","initBaiduMap 3056");
                    }
                }
                //刷新CheckBox
                initializationCheckState();

            }
        });
        btnPosMeeting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 会议
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }

                //为了防止发起呼叫过于频繁
                if (ptt_3g_PadApplication != null) {
                    if (ptt_3g_PadApplication.isWetherClickCall() == false) {
                        ptt_3g_PadApplication.setWetherClickCall(true);
                        boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
                        if (wetherTimerWorking == true) {
                            ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                            //返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
                            return;
                        } else {
                            //返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
                            ptt_3g_PadApplication.startTimer();
                        }
                    } else {
                        ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                        return;
                    }
                }

				/*CallingInfo callingMeeting = null;
				for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
					if ((!callingInfo.isHolding())){
						if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
							callingMeeting = callingInfo;
						}
					}
				}
				if(callingMeeting != null){
					return;
				}*/

                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择会议成员");
                    return;
                }
                if (CallingManager.getInstance().getCallingCount() >= 4) {
                    ToastUtils.showToast(
                            MainActivity.this,
                            getResources().getString(
                                    R.string.info_meeting_route_busy));
                    return;
                }

                // 设置会议成员
                ArrayList<String> nos = new ArrayList<String>();
                for (PosMarkerInfo info : checkBoxMarker) {
                    nos.add(info.getName());
                    MtcDelegate.log("选中成员:" + info.getName());
                }

                //如果当前已经有单呼，禁止发起新的单呼
                CallingInfo callingCalling = null;
                CallingInfo callingMeeting = null;
                CallingInfo callingIntercom = null;
                for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
                    if ((!callingInfo.isHolding())) {
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                            callingCalling = callingInfo;
                        }
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                            callingMeeting = callingInfo;
                        }
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                            callingIntercom = callingInfo;
                        }
                    }
                }
                if (callingCalling != null) {
                    showMeetingIfContinueString(callingCalling, nos, GlobalConstant.MEETING_TAB);
                    return;
                }
                if (callingMeeting != null) {
                    showMeetingIfContinueString(callingMeeting, nos, GlobalConstant.MEETING_TAB);
                    return;
                }
                if (callingIntercom != null) {
                    showMeetingIfContinueString(callingIntercom, nos, GlobalConstant.MEETING_TAB);
                    return;
                }

                // 切换页面
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                    mImageAdapter.setImageLight(2);
                    startActivity(2);
                }

                Intent callIntent = new Intent(
                        GlobalConstant.ACTION_POSITION_OPERATE);
                callIntent.putExtra(GlobalConstant.KEY_POSITION_TYPE,
                        GlobalConstant.MEETING_TAB);
                callIntent.putExtra(GlobalConstant.KEY_MEETING_NOS, nos);
                sendBroadcast(callIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posMarkerInfosList != null) {
                    for (PosMarkerInfo pmi : posMarkerInfosList) {
                        addPosOverlay(pmi, false);
                        Log.e("GIS模块","initBaiduMap 3178");
                    }
                }
                //刷新CheckBox
                initializationCheckState();

            }
        });
        btnPosTalk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对讲
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择对讲联系人");
                    return;
                }

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
                AlertDialogUtils alertDialogUtils = new AlertDialogUtils(MainActivity.this, GlobalConstant.CALL_TYPE_TEMPINTERCOM);
                if (callingCalling != null) {
                    alertDialogUtils.showIfContinue(callingCalling);
                    return;
                }
                if (callingMeeting != null) {
                    alertDialogUtils.showIfContinue(callingMeeting);
                    return;
                }


                // 切换页面
                if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
                    mImageAdapter.setImageLight(0);
                    startActivity(0);
                }
                // 设置对讲组成员
                ArrayList<String> nos = new ArrayList<String>();
                for (PosMarkerInfo info : checkBoxMarker) {
                    if (nos != null && !nos.contains(info.getName())) {
                        nos.add(info.getName());
                    }
                }
                Intent callIntent = new Intent(
                        GlobalConstant.ACTION_POSITION_OPERATE);
                callIntent.putExtra(GlobalConstant.KEY_POSITION_TYPE,
                        GlobalConstant.INTERCOM_TAB);
                callIntent.putExtra(GlobalConstant.KEY_MEETING_NOS, nos);
                sendBroadcast(callIntent);

                Log.w("创建时临时对讲时的成员", "创建时临时对讲时的成员" + nos);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posMarkerInfosList != null) {
                    for (PosMarkerInfo pmi : posMarkerInfosList) {
                        addPosOverlay(pmi, false);
                        Log.e("GIS模块","initBaiduMap 3255");
                    }
                }

                //刷新CheckBox
                initializationCheckState();

            }
        });
        btnPosMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 消息
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择要发送消息的联系人");
                    return;
                }
                List<String> selUserNo = new ArrayList<String>();
                for (PosMarkerInfo pmi : checkBoxMarker) {
                    selUserNo.add(pmi.getName());
                }
                Intent newMessageIntent = new Intent(
                        GlobalConstant.ACTION_MESSAGE_MSGMAINNEW);
                ptt_3g_PadApplication.setAddContact(true);
                ptt_3g_PadApplication.setContactList(selUserNo);
                sendBroadcast(newMessageIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posMarkerInfosList != null) {
                    for (PosMarkerInfo pmi : posMarkerInfosList) {
                        addPosOverlay(pmi, false);
                        Log.e("GIS模块","initBaiduMap 3297");
                    }
                }
                //刷新CheckBox
                initializationCheckState();

            }
        });
        btnPosOnVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择要通话的联系人");
                    return;
                }


                Editor edit = prefs.edit();
                edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                edit.apply();
                Intent voiceIntent = new Intent(
                        GlobalConstant.ACTION_CALLING_OTHERCALLVOICE);
                voiceIntent.putExtra("callUserNo", checkBoxMarker.get(0)
                        .getName());
                sendBroadcast(voiceIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }

                //刷新check状态
                initializationCheckState();

            }
        });
        btnPosOnVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择要发通话的联系人");
                    return;
                }

                Editor edit = prefs.edit();
                edit.putString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VIDEO);
                edit.apply();
                Intent voiceIntent = new Intent(
                        GlobalConstant.ACTION_CALLING_OTHERCALLVIDEO);
                voiceIntent.putExtra("callUserNo", checkBoxMarker.get(0)
                        .getName());
                sendBroadcast(voiceIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }
                //刷新check状态
                initializationCheckState();
            }
        });
        btnPosOnMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 消息
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (checkBoxMarker.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请选择要发送消息的联系人");
                    return;
                }
                List<String> selUserNo = new ArrayList<String>();
                for (PosMarkerInfo pmi : checkBoxMarker) {
                    selUserNo.add(pmi.getName());
                }
                Intent newMessageIntent = new Intent(
                        GlobalConstant.ACTION_MESSAGE_MSGMAINNEW);
                ptt_3g_PadApplication.setAddContact(true);
                ptt_3g_PadApplication.setContactList(selUserNo);
                sendBroadcast(newMessageIntent);

                // 清空地图marker
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }

                //刷新check状态
                initializationCheckState();
            }
        });

        txtPosSpeed1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPosPlaying) {
                    ToastUtils.showToast(MainActivity.this, "没有播放轨迹");
                    return;
                }

                String txtPosSpeedString = txtPosSpeed1.getText().toString();
                if (txtPosSpeedString.equals("1X")) {
                    posPlaySpeedType = 2;
                    txtPosSpeed1.setText("2X");
                } else if (txtPosSpeedString.equals("2X")) {
                    posPlaySpeedType = 4;
                    txtPosSpeed1.setText("4X");
                } else if (txtPosSpeedString.equals("4X")) {
                    posPlaySpeedType = 8;
                    txtPosSpeed1.setText("8X");
                } else if (txtPosSpeedString.equals("8X")) {
                    posPlaySpeedType = 16;
                    txtPosSpeed1.setText("16X");
                } else if (txtPosSpeedString.equals("16X")) {
                    posPlaySpeedType = 32;
                    txtPosSpeed1.setText("32X");
                } else if (txtPosSpeedString.equals("32X")) {
                    posPlaySpeedType = 1;
                    txtPosSpeed1.setText("1X");
                }
                //posPlaySpeedType = 1;
                ToastUtils.showToast(MainActivity.this, "轨迹回放速度：" + posPlaySpeedType + "倍速");
            }
        });
        txtPosSpeed2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPosPlaying) {
                    ToastUtils.showToast(MainActivity.this, "没有播放轨迹");
                    return;
                }
                posPlaySpeedType = 2;
                ToastUtils.showToast(MainActivity.this, "测试速度：2秒");
            }
        });
        txtPosSpeed3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPosPlaying) {
                    ToastUtils.showToast(MainActivity.this, "没有播放轨迹");
                    return;
                }
                posPlaySpeedType = 3;
                ToastUtils.showToast(MainActivity.this, "测试速度：3秒");
            }
        });

        btnPosTrackPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isPosPlaying) {
                    isPosPlaying = true;
                    btnPosTrackPause.setText(R.string.title_postion_pause);
                    // 第一次播放，启动线程，当暂停再次播放的时候不再调用
                    if (isPosFirstPlaying) {
                        isPosFirstPlaying = false;
                        //proPosProgress.setMax(posMarkerInfosTraceList.size());// 设置进度条值
                        proPosProgress.setMax((int) differenceTimeSeconds);

                        // 启动播放轨迹线程
                        posTracePlayThread = new NewPosTracePlayThread();
                        new Thread(posTracePlayThread).start();
                        MtcDelegate.log("播放轨迹线程启动");
                    } else {
                        posTracePlayThread.onPlay();
                    }
                } else {
                    isPosPlaying = false;
                    posTracePlayThread.onPause();
                    btnPosTrackPause.setText(R.string.title_postion_play);
                }
            }
        });
        btnPosTrackExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止播放轨迹
                isPosPlaying = false;
                if (posTracePlayThread != null) {
                    posTracePlayThread.onStop();
                }
                posPlaySpeedType = 1;//还原播放速度-yuezs add
                proPosProgress.setProgress(0);//还原进度条显示-yuezs add
                txtPosProgress.setText("已完成：0%");//还原播放百分比显示-yuezs add
                isPosFirstPlaying = true;//还原播放标记
                btnPosTrackPause.setText(R.string.title_postion_play);//设置为播放按钮-yuezs add

                hasMark.clear();//清空轨迹对象-yuezs add
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }// 清空轨迹
                if (checkBoxMarker != null) {
                    checkBoxMarker.clear();
                }
                if (posMarkerInfosList != null) {
                    for (PosMarkerInfo pmi : posMarkerInfosList) {
                        addPosOverlay(pmi, false);
                        Log.e("GIS模块","initBaiduMap 3509");
                    }
                }

                // 退出
                ll_trackplay.setVisibility(View.GONE);

            }
        });
        btn_single.setText(Html.fromHtml("<u>"
                + getString(R.string.title_postion_single) + "</u>"));
		/* 百度地图部分结束 */
    }

    private void initializationCheckState() {

        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) employeeLayout.getChildAt(i);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            }
        }

        isCheck = true;

        if (ll_pos_single.getVisibility() != View.GONE) {
            ll_pos_single.setVisibility(View.GONE);
        }
        if (ll_pos.getVisibility() != View.GONE) {
            ll_pos.setVisibility(View.GONE);
        }
        checkBoxMarker.clear();
    }

    // 切换地图操作按钮状态
    private void switchPosButtonStatus(int cur) {
        if (cur == 1) {
            posSelType = 1;

            btn_single.setText(Html.fromHtml("<u>"
                    + getString(R.string.title_postion_single) + "</u>"));
            btn_multiple.setText(getString(R.string.title_postion_multiple));
            btn_filter.setText(getString(R.string.title_postion_filter));
            btn_track.setText(getString(R.string.title_postion_track));
            if (isTrace) {
                btn_trace.setText(getString(R.string.title_postion_untrace));
            } else {
                btn_trace.setText(getString(R.string.title_postion_trace));
            }
            //btn_trace.setText(getString(R.string.title_postion_trace));
            btn_clean.setText(getString(R.string.title_postion_clean));
        } else if (cur == 2) {
            btn_single.setText(getString(R.string.title_postion_single));
            btn_multiple.setText(Html.fromHtml("<u>"
                    + getString(R.string.title_postion_multiple) + "</u>"));
            btn_filter.setText(getString(R.string.title_postion_filter));
            btn_track.setText(getString(R.string.title_postion_track));
            if (isTrace) {
                btn_trace.setText(getString(R.string.title_postion_untrace));
            } else {
                btn_trace.setText(getString(R.string.title_postion_trace));
            }
            //btn_trace.setText(getString(R.string.title_postion_trace));
            btn_clean.setText(getString(R.string.title_postion_clean));
        } else if (cur == 3) {
            btn_single.setText(getString(R.string.title_postion_single));
            btn_multiple.setText(getString(R.string.title_postion_multiple));
            btn_filter.setText(Html.fromHtml("<u>"
                    + getString(R.string.title_postion_filter) + "</u>"));
            btn_track.setText(getString(R.string.title_postion_track));
            if (isTrace) {
                btn_trace.setText(getString(R.string.title_postion_untrace));
            } else {
                btn_trace.setText(getString(R.string.title_postion_trace));
            }
            //btn_trace.setText(getString(R.string.title_postion_trace));
            btn_clean.setText(getString(R.string.title_postion_clean));
        } else if (cur == 4) {
            btn_single.setText(getString(R.string.title_postion_single));
            btn_multiple.setText(getString(R.string.title_postion_multiple));
            btn_filter.setText(getString(R.string.title_postion_filter));
            btn_track.setText(Html.fromHtml("<u>"
                    + getString(R.string.title_postion_track) + "</u>"));
            if (isTrace) {
                btn_trace.setText(getString(R.string.title_postion_untrace));
            } else {
                btn_trace.setText(getString(R.string.title_postion_trace));
            }
            //btn_trace.setText(getString(R.string.title_postion_trace));
            btn_clean.setText(getString(R.string.title_postion_clean));
        } else if (cur == 5) {
            btn_single.setText(getString(R.string.title_postion_single));
            btn_multiple.setText(getString(R.string.title_postion_multiple));
            btn_filter.setText(getString(R.string.title_postion_filter));
            btn_track.setText(getString(R.string.title_postion_track));
            if (isTrace) {
                btn_trace.setText(Html.fromHtml("<u>"
                        + getString(R.string.title_postion_untrace) + "</u>"));
            } else {
                btn_trace.setText(Html.fromHtml("<u>"
                        + getString(R.string.title_postion_trace) + "</u>"));
            }
			/*btn_trace.setText(Html.fromHtml("<u>"
					+ getString(R.string.title_postion_trace) + "</u>"));*/
            btn_clean.setText(getString(R.string.title_postion_clean));
        } else if (cur == 6) {
            btn_single.setText(getString(R.string.title_postion_single));
            btn_multiple.setText(getString(R.string.title_postion_multiple));
            btn_filter.setText(getString(R.string.title_postion_filter));
            btn_track.setText(getString(R.string.title_postion_track));
            //btn_trace.setText(getString(R.string.title_postion_trace));
            if (isTrace) {
                btn_trace.setText(getString(R.string.title_postion_untrace));
            } else {
                btn_trace.setText(getString(R.string.title_postion_trace));
            }
            btn_clean.setText(Html.fromHtml("<u>"
                    + getString(R.string.title_postion_clean) + "</u>"));
        }
    }


    // 播放轨迹更新UI
    @SuppressLint("HandlerLeak")
    private Handler posPlayHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int pro = msg.getData().getInt("progress");
                    proPosProgress.setProgress(pro);
                    txtPosProgress.setText("已完成："
                            + CommonMethod.getPercentage(pro,
                            proPosProgress.getMax() - 1));
                    break;
                case 2:
                    ToastUtils.showToast(MainActivity.this, "轨迹播放完成");
                    btnPosTrackPause.setText(R.string.title_postion_play);
                    proPosProgress.setMax((int) differenceTimeSeconds);
                    proPosProgress.setProgress((int) differenceTimeSeconds);
                    txtPosProgress.setText("已完成：100%");
                    isPosFirstPlaying = true;
                    break;
                case 3:
				/*String[]date=posMarkerInfosTraceList.get(msg.getData().getInt("time")).getTime().split(" ");
		    String [] time=date[1].split(":");
		    txtPosCurrHour.setText(time[0]);
		    txtPosCurrMinute.setText(time[1]);
		    txtPosCurrSecond.setText(time[2]);*/
                    int difference = msg.getData().getInt("time");
                    long diff = StrToDate(currentTime).getTime() + difference * 1000;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(diff);
                    currentTime = sdf.format(date);    //当前时间赋值
                    SimpleDateFormat yearSdf = new SimpleDateFormat("MM月dd日");
                    String yearStr = yearSdf.format(date);
                    SimpleDateFormat daySdf = new SimpleDateFormat("HH:mm:ss");
                    String dayStr = daySdf.format(date);
                    //显示月日
                    txtPosCurrHour_day.setText(yearStr);
                    //显示小时
                    txtPosCurrHour.setText(dayStr);
                    break;
            }
        }

        ;
    };

    // 播放轨迹线程
    private class PosTracePlayThread implements Runnable {
        private Object posPlayLock;
        private boolean posPlayPaused;
        private boolean posPlayFinished;

        public PosTracePlayThread() {
            posPlayLock = new Object();
            posPlayPaused = false;
            posPlayFinished = false;
        }

        @SuppressWarnings("unused")
        @Override
        public void run() {
            int i = 0;
            while (!posPlayFinished) {
                // 控制播放/暂停
                synchronized (posPlayLock) {
                    while (posPlayPaused) {
                        try {
                            posPlayLock.wait();
                        } catch (Exception e) {
                        }
                    }
                }
                Message posPlayMessage = new Message();
                //yuezs add 2015-11-10
                Message posMessage = new Message();
                if (i >= posMarkerInfosTraceList.size() - 1) {
                    isPosPlaying = false;
                    posPlayFinished = true;
                    posPlayMessage.what = 2;
                } else {

                    PosMarkerInfo firstPosMarkerInfo = posMarkerInfosTraceList
                            .get(0);
                    PosMarkerInfo onePosMarkerInfo = posMarkerInfosTraceList
                            .get(i);
                    PosMarkerInfo twoPosMarkerInfo = posMarkerInfosTraceList
                            .get(i + 1);
                    int posTimeSpan = FormatController.getSecondByDate(
                            FormatController.stringToLongDate(twoPosMarkerInfo
                                    .getTime()), FormatController
                                    .stringToLongDate(onePosMarkerInfo
                                            .getTime()));
                    LatLng firstLatLng = new LatLng(
                            firstPosMarkerInfo.getLatitude(),
                            firstPosMarkerInfo.getLongitude());
                    LatLng oneLatLng = new LatLng(
                            onePosMarkerInfo.getLatitude(),
                            onePosMarkerInfo.getLongitude());
                    LatLng twoLatLng = new LatLng(
                            twoPosMarkerInfo.getLatitude(),
                            twoPosMarkerInfo.getLongitude());

                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(oneLatLng);
                    points.add(twoLatLng);
                    OverlayOptions ooPolyline = new PolylineOptions().width(5)
                            .color(0xAAFF0000).points(points);//画线
                    //OerlayOptions oo=new PolygonOptions()
                    try {
                        if (posPlaySpeedType == 1) {
                            // Thread.sleep(posTimeSpan*1000);
                            Thread.sleep(3000);// 测试
                        } else if (posPlaySpeedType == 2) {
                            // Thread.sleep(posTimeSpan/2*1000);
                            Thread.sleep(2000);// 测试
                        } else if (posPlaySpeedType == 3) {
                            // Thread.sleep(posTimeSpan/3*1000);
                            Thread.sleep(1000);// 测试
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "3355 ---->" + hasMark.values().size() + "");
                    /*************************yuezs add 图标跟轨迹行走开始*****************************/
                    if (onePosMarkerInfo.getName().equals(twoPosMarkerInfo.getName()) && hasMark.containsKey(onePosMarkerInfo.getName())) {
                        //hasMark.get(onePosMarkerInfo.getName()).setPosition(firstLatLng);
                        hasMark.get(onePosMarkerInfo.getName()).remove();
                        mBaiduMap.addOverlay(ooPolyline);
                        Log.e(TAG, "3361 ----> " + onePosMarkerInfo.getName());
                        addPosOverlay(twoPosMarkerInfo, false);
                        Log.e("GIS模块","PosTracePlayThread 3763");
                    }

                    /*************************yuezs add 图标跟轨迹行走结束*****************************/
                    // 缩放地图
                    LatLngBounds.Builder posPlayBuilder = new LatLngBounds.Builder();
                    posPlayBuilder.include(firstLatLng);
                    posPlayBuilder.include(twoLatLng);
                    LatLngBounds posPlayLatLngBounds = posPlayBuilder.build();
                    MapStatusUpdate posPlayMapStatusUpdate = MapStatusUpdateFactory
                            .newLatLngBounds(posPlayLatLngBounds);
                    // mBaiduMap.animateMapStatus(posPlayMapStatusUpdate);
                    if (mBaiduMap != null) {
                        mBaiduMap.setMapStatus(posPlayMapStatusUpdate);
                    }
                    i++;
                    // 更新进度条
                    posPlayMessage.what = 1;
                    Bundle posBundle = new Bundle();
                    posBundle.putInt("progress", i);
                    posPlayMessage.setData(posBundle);
                    // 更新轨迹时间
                    posMessage.what = 3;//时间
                    Bundle posBundletime = new Bundle();
                    posBundletime.putInt("time", i);
                    posMessage.setData(posBundletime);
                    posPlayHandler.sendMessage(posMessage);
                    Log.e(TAG, "轨迹次数 ：" + i + "");
                }
                posPlayHandler.sendMessage(posPlayMessage);
            }
        }

        // 暂停
        public void onPause() {
            synchronized (posPlayLock) {
                posPlayPaused = true;
            }
        }

        // 播放
        public void onPlay() {
            synchronized (posPlayLock) {
                posPlayPaused = false;
                posPlayLock.notifyAll();
            }
        }

        // 停止
        public void onStop() {
            posPlayFinished = true;
        }

    }

    private class NewPosTracePlayThread implements Runnable {
        LatLngBounds.Builder posPlayBuilder = new LatLngBounds.Builder();
        private Object posPlayLock;
        private boolean posPlayPaused;
        private boolean posPlayFinished;

        private NewPosTracePlayThread() {
            posPlayLock = new Object();
            posPlayPaused = false;
            posPlayFinished = false;
            if (trackMemColorMap != null) {
                trackMemColorMap.clear();
            }
        }

        @Override
        public void run() {
            while (!posPlayFinished) {
                // 控制播放/暂停
                synchronized (posPlayLock) {
                    while (posPlayPaused) {
                        try {
                            posPlayLock.wait();
                        } catch (Exception e) {

                        }
                    }
                }

                Message posPlayMessage = new Message();
                Message posMessage = new Message();

                if (StrToDate(currentTime).getTime() >= StrToDate(trackEndPosTime).getTime()) {
                    isPosPlaying = false;
                    posPlayFinished = true;
                    posPlayMessage.what = 2;
                } else {
                    //按设置倍数从集合获取轨迹成员相应的GPS点
                    long currentTimeLong = StrToDate(currentTime).getTime();
                    long posPlaySpeedLong = 0;
                    switch (posPlaySpeedType) {
                        case 1:
                            posPlaySpeedLong = currentTimeLong / 1000 + 1;
                            break;
                        case 2:
                            posPlaySpeedLong = currentTimeLong / 1000 + 2;
                            break;
                        case 4:
                            posPlaySpeedLong = currentTimeLong / 1000 + 4;
                            break;
                        case 8:
                            posPlaySpeedLong = currentTimeLong / 1000 + 8;
                            break;
                        case 16:
                            posPlaySpeedLong = currentTimeLong / 1000 + 16;
                            break;
                        case 32:
                            posPlaySpeedLong = currentTimeLong / 1000 + 32;
                            break;
                        case 64:
                            posPlaySpeedLong = currentTimeLong / 1000 + 64;
                            break;
                        default:
                            break;
                    }

                    try {
                        //获取时间段GPS点
                        Map<String, List<PosMarkerInfo>> posMapInfos = getPosMarkerInfoByTime(currentTimeLong, posPlaySpeedLong * 1000);
                        //划线显示轨迹成员Marker
                        Iterator iterator = posMapInfos.keySet().iterator();
                        List<LatLng> points = new ArrayList<LatLng>();
                        while (iterator.hasNext()) {
                            String no = iterator.next().toString();
                            //标记线段颜色
                            int color = Color.RED;
                            if (trackMemColorMap.containsKey(no)) {
                                color = Integer.parseInt(trackMemColorMap.get(no) + "");
                            } else {
                                for (int cl : colorIntList) {
                                    if (!trackMemColorMap.containsValue(cl)) {
                                        color = cl;
                                        trackMemColorMap.put(no, Integer.valueOf(cl));
                                        break;
                                    }
                                }
                            }

                            //打印Marker和折线
                            List<PosMarkerInfo> pList = posMapInfos.get(no);
                            if (pList != null && pList.size() >= 1) {
                                Collections.sort(pList);
                                for (PosMarkerInfo psInfo : pList) {
                                    LatLng latLng = new LatLng(psInfo.getLatitude(), psInfo.getLongitude());
                                    points.add(latLng);
                                    posPlayBuilder.include(latLng);
                                }

                                OverlayOptions ooPolylines = null;
                                if (points != null && points.size() > 0) {
                                    ooPolylines = new PolylineOptions().width(6).color(color).points(points);//画线
                                }
                                if (hasMark.containsKey(no)) {
                                    hasMark.get(no).remove();
                                    if (ooPolylines != null) {
                                        mBaiduMap.addOverlay(ooPolylines);
                                    }
                                    addPosOverlay(pList.get(pList.size() - 1), false);
                                    Log.e("GIS模块","NewPosTracePlayThread 3925");
                                }
                            }
                        }
                    } catch (Exception e) {

                    }

                    // 缩放地图
                    LatLngBounds posPlayLatLngBounds = posPlayBuilder.build();
                    MapStatusUpdate posPlayMapStatusUpdate = MapStatusUpdateFactory
                            .newLatLngBounds(posPlayLatLngBounds);
                    if (mBaiduMap != null) {
                        mBaiduMap.setMapStatus(posPlayMapStatusUpdate);
                    }

                    //更新进度条,通过posPlaySpeedLong时间
                    posPlayMessage.what = 1;
                    Bundle posBundle = new Bundle();
                    posBundle.putInt("progress", (int) differenceTime(trackStartPosTime, currentTime));
                    posPlayMessage.setData(posBundle);

                    //更新轨迹时间,通过posPlaySpeedLong时间
                    posMessage.what = 3;//时间
                    Bundle posBundletime = new Bundle();
                    posBundletime.putInt("time", posPlaySpeedType);
                    posMessage.setData(posBundletime);
                    posPlayHandler.sendMessage(posMessage);

                }
                posPlayHandler.sendMessage(posPlayMessage);

                //线程在没有暂停情况下一秒执行一次
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        // 暂停
        public void onPause() {
            synchronized (posPlayLock) {
                posPlayPaused = true;
            }
        }

        // 播放
        public void onPlay() {
            synchronized (posPlayLock) {
                posPlayPaused = false;
                posPlayLock.notifyAll();
            }
        }

        // 停止
        public void onStop() {
            posPlayFinished = true;
        }

    }


    //通过时间段获取集合中GPS点信息
    private synchronized Map<String, List<PosMarkerInfo>> getPosMarkerInfoByTime(long currentTimeLong, long posPlaySpeedLong) {
        Map<String, List<PosMarkerInfo>> tempMap = new HashMap<String, List<PosMarkerInfo>>();

        Iterator iterator = trackPosMarkerInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String no = entry.getKey().toString();
            List<PosMarkerInfo> posList = (List<PosMarkerInfo>) entry.getValue();
            Collections.sort(posList);//按time升序

            List<PosMarkerInfo> tempPosList = new ArrayList<PosMarkerInfo>();
            boolean haveResult = false;
            for (PosMarkerInfo ps : posList) {
                Date pTimeDate = StrToDate(ps.getTime());
                if (pTimeDate.getTime() >= currentTimeLong && pTimeDate.getTime() < posPlaySpeedLong) {
                    if (haveResult == false) {
                        int index = posList.indexOf(ps);
                        if (index > 0) {
                            PosMarkerInfo psIndex = posList.get(index - 1);
                            if (!tempPosList.contains(psIndex)) {
                                tempPosList.add(psIndex);
                            }
                        }
                    }
                    if (!tempPosList.contains(ps)) {
                        tempPosList.add(ps);
                    }
                    haveResult = true;
                }
            }
            if (tempMap.containsKey(no)) {
                tempMap.remove(no);
            }
            Collections.sort(tempPosList);
            tempMap.put(no, tempPosList);
        }
        return tempMap;
    }

    public Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //计算两个时间的差值
    public long differenceTime(String dateSmall, String dateBig) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        long seconds = 0;
        try {
            Date d1 = df.parse(dateSmall);
            Date d2 = df.parse(dateBig);
            long diff = d2.getTime() - d1.getTime();
            seconds = diff / 1000;
        } catch (Exception e) {

        }
        return seconds;
    }

    // 页面中按钮点击事件统一处理
    private class BtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_gone) {
                // 设置显示隐藏
                if (tabcontent.getVisibility() == View.GONE) {
                    // 显示分页
                    SetViewShow(GlobalConstant.ALL);



                    currTabVisible = true;
                    // 将登录信息保存于全局变量
                    Editor editor = prefs.edit();
                    editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN,
                            currTabVisible);
                    editor.apply();

                    if (mTabHost.getCurrentTabTag() == GlobalConstant.MEETING_TAB) {
                        Intent intent = new Intent(GlobalConstant.ACTION_MEETING_HIDEORSHOW);
                        intent.putExtra("state", currTabVisible);
                        sendBroadcast(intent);
                    }

                } else {
                    // 隐藏分页
                    SetViewShow(GlobalConstant.ONLYCONTENT);
                    currTabVisible = false;
                    // 将登录信息保存于全局变量
                    Editor editor = prefs.edit();
                    editor.putBoolean(GlobalConstant.SP_SHOWORHIDDEN,
                            currTabVisible);
                    editor.apply();

                    if (mTabHost.getCurrentTabTag() == GlobalConstant.MEETING_TAB) {
                        Intent intent = new Intent(GlobalConstant.ACTION_MEETING_HIDEORSHOW);
                        intent.putExtra("state", currTabVisible);
                        sendBroadcast(intent);
                    }
                }
                //对显示视频控件大小进行重新计算
                calculationLayout();

            } else if (v.getId() == R.id.btn_one) {
                if (currNumColumn != NumColumns.ONE) {
                    setOne();
                }

            } else if (v.getId() == R.id.btn_four) {
                if (currNumColumn != NumColumns.TWO) {
                    setFour();
                }
            } else if (v.getId() == R.id.btn_nine) {
                if (currNumColumn != NumColumns.THREE) {
                    setNine();
                }
            } else if (v.getId() == R.id.btn_info) {
                // 显示视频信息
                ShowMonitorInfo();
            } else if (v.getId() == R.id.btn_close) {
                if (!ptt_3g_PadApplication.isNetConnection()) {
                    ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
                    return;
                }
                // 关闭视频
                MtcDelegate.log("lizhiwei 点选关闭单个布局DelVideoItem");
                DelVideoItem();

                //会议结束 发送广播结束当前以上墙的视频
                Intent closeDecoderVideo = new Intent(GlobalConstant.DECODINGTHEWALLRECEIVER);
                closeDecoderVideo.putExtra(GlobalConstant.ISALL, GlobalConstant.ISALL_NO);
                closeDecoderVideo.putExtra(GlobalConstant.DECODER_NUMBER, prefs.getString("number", "0"));
                sendBroadcast(closeDecoderVideo);
                Log.e(TAG, "发送结束上墙视频广播");

            } else if (v.getId() == R.id.btn_pushvideo) {
                if (!ptt_3g_PadApplication.isNetConnection()) {
                    ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
                    return;
                }
                //推送视频
                pushVideoItem();
            } else if (v.getId() == R.id.btn_decodingupperwall) {

                if (ButtonUtils.isFastDoubleClick(R.id.btn_decodingupperwall, 1200)) {
                    ToastUtils.showToast(getApplicationContext(), "无效点击");
                    return;
                }

                decoderHandler.sendEmptyMessage(7); //设置btn_decodingupperwall不可点击

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!ptt_3g_PadApplication.isNetConnection()) {
                    ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
                    return;
                }
                String number = prefs.getString("number", "0");
                //解码上墙
                //showdcodupperwallmenu(number);
                //new AsyncDecodeTheUpperWall().execute(number);
                new AsyncDecodeTheUpperWall().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,number);

            } else if (v.getId() == R.id.btn_decodingupperwa) {

                //结束上墙
                String number = prefs.getString("number", "0");
                endDecoder(number);
            }
        }
    }

    //设置九分屏
    private void setNine() {
        String meetingType = prefs.getString("ISMCU", "False");
        if (currMeeting != null) {
            //当前存在会议
            if (meetingType.equals("True")){
                //当前会议类型为MCU
                Log.e("return掉了","setNine 停了 ");
                return;
            }
        }

        if (!ptt_3g_PadApplication.isNetConnection()) {
            ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
            return;
        }
        // 设置九分屏
        currNumColumn = NumColumns.THREE;
        // 处理视频显示
        MtcDelegate.log("lizhiwei 九分屏设置视频布局DealVideoItmes");
        DealVideoItmes();
        ptt_3g_PadApplication.setCurrentSpliCount("Nine");
    }

    //设置一分屏
    private void setOne() {
        String meetingType = prefs.getString("ISMCU", "False");
        if (currMeeting != null) {
            //当前存在会议
            if (meetingType.equals("True")){
                //当前会议类型为MCU
                Log.e("return掉了","setOne 停了 ");
                return;
            }
        }

        if (!ptt_3g_PadApplication.isNetConnection()) {
            ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
            return;
        }

        // 设置一分屏
        currNumColumn = NumColumns.ONE;
        // 处理视频显示
        Log.e(TAG, "videos：" + videos.size());
        if (currNumColumn == NumColumns.ONE) {
            if (videos.size() > 1) {
                MonitorInfo minfo = videos.get(0);
                for (int i = 1; i < videos.size() - 1; i++) {
                    MonitorInfo monitorinfo = (videos.get(i));
                    if (monitorinfo == null) {
                        continue;
                    }
                    if (monitorinfo.getMultiplayer() != null) {
                        monitorinfo.getMultiplayer().stop();
                        monitorinfo.getMultiplayer().close();
                        monitorinfo.getMultiplayer().destroy();
                    }
                    if (monitorinfo.getTimer() != null) {
                        monitorinfo.getTimer().stop();
                        monitorinfo.getTimer().setText("");
                    }
                    ReleaseVideo(monitorinfo);
                    // 如果是监控，则发送结束监控
                    if (monitorinfo.isIsmonitor()) {
                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
                    }
                }
                videos.clear();
                videos.add(minfo);
                //new MultiPlayer().cleanup();
                Log.e(TAG, "lizhiwei 视频类型不同结束之前设置视频布局DealVideoItmes");
            }
        }
        DealVideoItmes();
        ptt_3g_PadApplication.setCurrentSpliCount("One");



    }

    //设置四分屏
    private void setFour() {

        String meetingType = prefs.getString("ISMCU", "False");
        if (currMeeting != null) {
            //当前存在会议
            if (meetingType.equals("True")){
                //当前会议类型为MCU
                Log.e("return掉了","setFour 停了 ");
                return;
            }
        }

        if (!ptt_3g_PadApplication.isNetConnection()) {
            ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
            return;
        }
        // 设置四分屏
        currNumColumn = NumColumns.TWO;
        // 处理视频显示
        MtcDelegate.log("lizhiwei 四分屏设置视频布局DealVideoItmes");

        if (currNumColumn == NumColumns.TWO) {
            if (videos.size() > 4) {
                MonitorInfo minfo1 = videos.get(0);
                MonitorInfo minfo2 = videos.get(1);
                MonitorInfo minfo3 = videos.get(2);
                MonitorInfo minfo4 = videos.get(3);
                for (int i = 4; i < videos.size() - 1; i++) {
                    MonitorInfo monitorinfo = (videos.get(i));
                    if (monitorinfo == null) {
                        continue;
                    }
                    if (monitorinfo.getMultiplayer() != null) {
                        monitorinfo.getMultiplayer().stop();
                        monitorinfo.getMultiplayer().close();
                        monitorinfo.getMultiplayer().destroy();
                    }
                    if (monitorinfo.getTimer() != null) {
                        monitorinfo.getTimer().stop();
                        monitorinfo.getTimer().setText("");
                    }
                    ReleaseVideo(monitorinfo);
                    // 如果是监控，则发送结束监控
                    if (monitorinfo.isIsmonitor()) {
                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
                    }
                }
                videos.clear();
                videos.add(minfo1);
                videos.add(minfo2);
                videos.add(minfo3);
                videos.add(minfo4);
                //new MultiPlayer().cleanup();
                Log.e(TAG, "lizhiwei 视频类型不同结束之前设置视频布局DealVideoItmes");
            }
        }
        DealVideoItmes();
        ptt_3g_PadApplication.setCurrentSpliCount("Four");
    }

    private void endDecoder(String number) {


        Log.e(TAG, "取消解码上墙  得到的number：" + number);

        if (number.length() == 0) {
            ToastUtils.showToast(MainActivity.this, "请选择目标");
            return;
        }
        if (number.equals("0")) {
            ToastUtils.showToast(MainActivity.this, "请选择目标");
            return;
        }

        if (number.equals("0")) {
            ToastUtils.showToast(MainActivity.this, "请选择目标");
            return;
        }

        if (number.contains("-")) {
            ToastUtils.showToast(MainActivity.this, "请选择目标");
            return;
        }

        Log.e(TAG, "取消解码上墙  得到number：" + number);

        if (!decodeMap.containsKey(number)) {
            Log.e(TAG, "当前选中用户没有上墙：" + number);
            ToastUtils.showToast(MainActivity.this, "当前选中用户没有上墙");
            return;
        }

        if (selectedchan == null) {
            return;
        }
        //取消解码上墙
        String chanidStrings = selectedchan.getSelectedItem().toString();//当前选择的解码器通道
        if (chanidStrings.equals(null)) {
            return;
        }
        if (info == null) {
            info = getDisArrayList(data_Decorder_ID).get(position);
        }
        if (decodeMap.size() == 0) {
            return;
        }
        DecoderBean decoderBean = decodeMap.get(number);
        String sessionid = decoderBean.getSessionId();
        String decoderId = decoderBean.getDecoderId();
        String chanidString = decoderBean.getChanidStrings();
        //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
        selectedDecoderEnd(sessionid, number, info.decoderId, Integer.valueOf(chanidString));

    }

    // 创建Tab页
    private TabHost.TabSpec buildTabSpec(String tag, Intent intent) {
        TabHost.TabSpec tabSpec = null;
        try {
            tabSpec = mTabHost.newTabSpec(tag);
            tabSpec.setContent(intent).setIndicator("");
        } catch (Exception ex) {
            Log.e(TAG, "Error 3908  :" + ex.getMessage());
        }
        return tabSpec;
    }

    // 打开标签页面
    public void startActivity(int position) {

        if (position != 2) {

            if (mPopUpWindow != null) {
                if (mPopUpWindow.isShowing()) {
                    mPopUpWindow.dismiss();
                    Log.e(TAG, " 隐藏  main 13913");
                }
            }
        }


        if (position == 0) {
            mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
            if (currTabVisible) {
                // 分页显示
                SetViewShow(GlobalConstant.ALL);
            } else {
                // 分页隐藏
                SetViewShow(GlobalConstant.ONLYCONTENT);
            }

            // 如果视频会议正在进行则移除地图
            if ((videos.size() > 0)
                    && (videos.get(0).getVedioPort() > 0)) {

                if (ll_position.getVisibility() != View.GONE
                        && rl_positiontools.getVisibility() != View.GONE
                        && sl_videos.getVisibility() != View.VISIBLE
                        && ll_split.getVisibility() != View.VISIBLE
                        && btn_close.getVisibility() != View.VISIBLE
                        && btn_pushvideo.getVisibility() != View.VISIBLE
                        && btn_decodingupperwall.getVisibility() != View.VISIBLE
                        && btn_decodingupperwa.getVisibility() != View.VISIBLE) {

                    // 设置地图和监控显示
                    ll_position.setVisibility(View.GONE);
                    //ll_position.removeAllViews();
                    //ptt_3g_PadApplication.setMapRemoved(true);
                    rl_positiontools.setVisibility(View.GONE);
                    sl_videos.setVisibility(View.VISIBLE);
                    ll_split.setVisibility(View.VISIBLE);
                    btn_info.setVisibility(View.VISIBLE);
                    btn_close.setVisibility(View.VISIBLE);
                    btn_pushvideo.setVisibility(View.VISIBLE);

                    if (videos.get(0).isIsmonitor()) {
                        return;
                    }
                    DealVideoItmes();
                }
            }

        } else if (position == 1) {

            // 需要将监控视频终止
//            Log.e(TAG,"videos.size() : " + videos.size());
//            if ((videos.size() > 0)
//                    && (videos.get(0).getVedioPort() > 0)) {
//                for (MonitorInfo monitorinfo : videos) {
//                    if (monitorinfo == null) {
//                        continue;
//                    }
//                    if (monitorinfo.getMultiplayer() != null) {
//                        monitorinfo.getMultiplayer().stop();
//                        monitorinfo.getMultiplayer().close();
//                        monitorinfo.getMultiplayer().destroy();
//                    }
//                    if (monitorinfo.getTimer() != null) {
//                        monitorinfo.getTimer().stop();
//                        monitorinfo.getTimer().setText("");
//                    }
//                    Log.e(TAG,"number : " + monitorinfo.getNumber() + "\r\n" + "Ismonitor : " + monitorinfo.isIsmonitor());
//                    if (monitorinfo.isIsmonitor()) {
//                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
//                    }else {
//                        //如果为会议 隐藏当前视频
//                        ReleaseVideo(monitorinfo);
//                    }
//                }
//                videos.clear();
//                new MultiPlayer().cleanup();
//            }
            if (CallingManager.getInstance().getCallingCount() > 0) {
                if (!ptt_3g_PadApplication.isBaiduMapisRemove()) {
                    //TODO: 2018/12/7 切换到通话界面 如果当前存在通话则将百度地图移除
                    Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                    intentS.putExtra("AddOrRemove", "Remove");
                    sendBroadcast(intentS);
                }
            }
            mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
            SetViewShow(GlobalConstant.ONLYTAB);
//            // 需要将监控视频终止
//            if ((videos.size() > 0)
//                    && (videos.get(0).getVedioPort() > 0)
//                    && (videos.get(0).isIsmonitor())) {
//                for (MonitorInfo monitorinfo : videos) {
//                    if (monitorinfo == null) {
//                        continue;
//                    }
//                    if (monitorinfo.getMultiplayer() != null) {
//                        monitorinfo.getMultiplayer().stop();
//                        monitorinfo.getMultiplayer().close();
//                        monitorinfo.getMultiplayer().destroy();
//                    }
//                    if (monitorinfo.getTimer() != null) {
//                        monitorinfo.getTimer().stop();
//                        monitorinfo.getTimer().setText("");
//                    }
//                    if (monitorinfo.isIsmonitor()) {
//                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
//                    }
//                }
//                videos.clear();
//                new MultiPlayer().cleanup();
//            }
//            DealVideoItmes();
        } else if (position == 2) {
            mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
//            if (currTabVisible) {
//                // 分页显示
//                SetViewShow(GlobalConstant.ALL);
//            } else {
//                // 分页隐藏
//                SetViewShow(GlobalConstant.ONLYCONTENT);
//            }

            String string = prefs.getString("ISMCU", "False");
            if (string.equals("True")) {
                SetViewShow(GlobalConstant.ONLYTAB);
            } else {
                if (currTabVisible){
                    SetViewShow(GlobalConstant.ALL);
                }else {

                    SetViewShow(GlobalConstant.ONLYCONTENT);
                }
            }
            // 如果视频会议正在进行则移除地图
            //(videos.size() > 0)&& (videos.get(0).getVedioPort() > 0)
            if (currMeeting != null) {

                if (!ptt_3g_PadApplication.isBaiduMapisRemove()) {
                    //TODO: 2018/12/7 切换到会议界面 如果当前存在会议则将百度地图移除
                    Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                    intentS.putExtra("AddOrRemove", "Remove");
                    sendBroadcast(intentS);
                }

                if (ll_position.getVisibility() != View.GONE
                        && rl_positiontools.getVisibility() != View.GONE
                        && sl_videos.getVisibility() != View.VISIBLE
                        && ll_split.getVisibility() != View.VISIBLE
                        && btn_close.getVisibility() != View.VISIBLE
                        && btn_pushvideo.getVisibility() != View.VISIBLE
                        && btn_decodingupperwall.getVisibility() != View.VISIBLE
                        && btn_decodingupperwa.getVisibility() != View.VISIBLE) {

                    // 设置地图和监控显示
                    ll_position.setVisibility(View.GONE);
//                ll_position.removeAllViews();
//                ptt_3g_PadApplication.setMapRemoved(true);
                    rl_positiontools.setVisibility(View.GONE);
                    sl_videos.setVisibility(View.VISIBLE);
                    ll_split.setVisibility(View.VISIBLE);
                    btn_info.setVisibility(View.VISIBLE);
                    btn_close.setVisibility(View.VISIBLE);
                    btn_pushvideo.setVisibility(View.VISIBLE);
                    btn_decodingupperwall.setVisibility(View.VISIBLE);
                    btn_decodingupperwa.setVisibility(View.VISIBLE);
//                if (videos.get(0).isIsmonitor()) {
//                    return;
//                }
                    DealVideoItmes();
                }
            }
        } else if (position == 3) {
            mTabHost.setCurrentTabByTag(GlobalConstant.MESSAGE_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
            if (currTabVisible) {
                // 分页显示
                SetViewShow(GlobalConstant.ALL);
            } else {
                // 分页隐藏
                SetViewShow(GlobalConstant.ONLYCONTENT);
            }
            // 如果全局拨号不为空，从别的页面直接跳转发送短信，不再执行跳转短信列表
            if (ptt_3g_PadApplication.isAddContact()) {
                if (ptt_3g_PadApplication.isMessageForward()) {
                    // 转发消息选择联系人
                    Intent intent = new Intent(
                            GlobalConstant.ACTION_MESSAGE_MSGFORWARD);
                    sendBroadcast(intent);
                } else {
                    Intent switchIntent = new Intent(
                            GlobalConstant.ACTION_MESSAGE_SWITCHMESSAGENEW);
                    sendBroadcast(switchIntent);
                }
            } else {
                // 点击左侧短信导航，返回短信列表
                Intent switchIntent = new Intent(
                        GlobalConstant.ACTION_MESSAGE_SWITCHFRAGMENT);
                sendBroadcast(switchIntent);
            }
        } else if (position == 4) {
            mTabHost.setCurrentTabByTag(GlobalConstant.CONTACT_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
            if (currTabVisible) {
                // 分页显示
                SetViewShow(GlobalConstant.ALL);
            } else {
                // 分页隐藏
                SetViewShow(GlobalConstant.ONLYCONTENT);
            }
        } else if (position == 5) {


            //TODO: 2018/12/7 切换到定位界面 如果当前地图被移除 则发送广播通知添加地图
            if (ptt_3g_PadApplication.isBaiduMapisRemove()) {
                Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                intentS.putExtra("AddOrRemove", "ADD");
                sendBroadcast(intentS);
            }


            // 需要将监控视频终止
            Log.e(TAG, "videos.size() : " + videos.size());
            if ((videos.size() > 0)
                    && (videos.get(0).getVedioPort() > 0)) {
                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo == null) {
                        continue;
                    }
                    if (monitorinfo.getMultiplayer() != null) {
                        monitorinfo.getMultiplayer().stop();
                        monitorinfo.getMultiplayer().close();
                        monitorinfo.getMultiplayer().destroy();
                    }
                    if (monitorinfo.getTimer() != null) {
                        monitorinfo.getTimer().stop();
                        monitorinfo.getTimer().setText("");
                    }
                    Log.e(TAG, "number : " + monitorinfo.getNumber() + "\r\n" + "Ismonitor : " + monitorinfo.isIsmonitor());
                    if (monitorinfo.isIsmonitor()) {
                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
                    } else {
                        //如果为会议 隐藏当前视频
                        ReleaseVideo(monitorinfo);
                    }
                }
                videos.clear();
                //new MultiPlayer().cleanup();
            }

            // 设置地图和监控显示
            ll_position.setVisibility(View.VISIBLE);
            // 如果百度地图被移除，则从新添加到布局
            Log.e("==Sws测试百度地图====", "=====是否被移除：" + ptt_3g_PadApplication.isMapRemoved());
            if (ptt_3g_PadApplication.isMapRemoved()) {

                if (mMapView.getParent() != null) {
                    ll_position.removeAllViews();
                }
                ll_position.addView(mMapView);

                ptt_3g_PadApplication.setMapRemoved(false);
            }

            // 定位
            mTabHost.setCurrentTabByTag(GlobalConstant.POSITION_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
            if (currTabVisible) {
                // 分页显示
                SetViewShow(GlobalConstant.ALL);
            } else {
                // 分页隐藏
                SetViewShow(GlobalConstant.ONLYCONTENT);
            }


            // 如果百度地图被移除，则从新添加到布局
//            Log.e(TAG, "=====是否被移除：" + ptt_3g_PadApplication.isMapRemoved());
//            if (ptt_3g_PadApplication.isMapRemoved()) {
//                // 设置地图和监控显示
//                ll_position.setVisibility(View.VISIBLE);
//                if (mMapView.getParent() != null) {
//                    ll_position.removeAllViews();
//                }
//                ll_position.addView(mMapView);
//
//                ptt_3g_PadApplication.setMapRemoved(false);
//            }

            //点击定位界面 判断地图是否被移除 如果移除  进行重新加载
            //百度地图会在通话 或者会议调取成员视频时移除
            Log.e("点击定位", "size() : " + monitorMap.size() + "callCount  : " + CallingManager.getInstance().getCallingCount());
//            if(monitorMap.size() <= 0  && CallingManager.getInstance().getCallingCount() <= 0){
//                if (ptt_3g_PadApplication.isBaiduMapisRemove()) {
//
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    initBaiDuMap2();
//
//                    initBaiduMap();
//
//                    String msgBodyString="req:cur_pos\r\nemployeeid:"+sipUser.getUsername()+"\r\ndstid:"+sipUser.getUsername()+"\r\n";
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//
//                    ptt_3g_PadApplication.setBaiduMapisRemove(false);
//
//
//                }
//            }

//            if (ptt_3g_PadApplication.isBaiduMapisRemove()){
//                rl_positiontools.setVisibility(View.GONE);
//            }else {
//                rl_positiontools.setVisibility(View.VISIBLE);
//            }

            if (mCurrentMode != null) {
                mCurrentMode = LocationMode.NORMAL;// 普通
                // 自定义定位图标
                mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_geo);
            }

            rl_positiontools.setVisibility(View.VISIBLE);
			/* fl_calling.setVisibility(View.GONE); */
            sl_videos.setVisibility(View.GONE);
            ll_split.setVisibility(View.GONE);
            btn_info.setVisibility(View.GONE);
            btn_close.setVisibility(View.GONE);
            btn_pushvideo.setVisibility(View.GONE);
            btn_decodingupperwall.setVisibility(View.GONE);
            btn_decodingupperwa.setVisibility(View.GONE);

        } else if (position == 6) {
            mTabHost.setCurrentTabByTag(GlobalConstant.MONITOR_TAB);
            // 取得当前tab页是否显示
            currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
                    GlobalConstant.SP_SHOWORHIDDEN);
            if (currTabVisible) {
                // 分页显示
                SetViewShow(GlobalConstant.ALL);
            } else {
                // 分页隐藏
                SetViewShow(GlobalConstant.ONLYCONTENT);
            }

            // 如果视频监控正在进行则移除地图
//            if ((videos.size() > 0)
//                    && (videos.get(0).getVedioPort() > 0)) {
//                // 设置地图和监控显示
//                ll_position.setVisibility(View.GONE);
//                //	ll_position.removeAllViews();
//                //	ptt_3g_PadApplication.setMapRemoved(true);
//                rl_positiontools.setVisibility(View.GONE);
//                sl_videos.setVisibility(View.VISIBLE);
//                ll_split.setVisibility(View.VISIBLE);
//                //btn_info.setVisibility(View.VISIBLE);
//                btn_close.setVisibility(View.VISIBLE);
//                btn_pushvideo.setVisibility(View.VISIBLE);
//                btn_decodingupperwall.setVisibility(View.VISIBLE);
//                btn_decodingupperwa.setVisibility(View.VISIBLE);
//                if (videos.get(0).isIsmonitor()) {
//                    return;
//                }
//            }

        } else if (position == 7) {

//            Log.e(TAG,"videos.size() : " + videos.size());
//            if ((videos.size() > 0)
//                    && (videos.get(0).getVedioPort() > 0)) {
//                for (MonitorInfo monitorinfo : videos) {
//                    if (monitorinfo == null) {
//                        continue;
//                    }
//                    if (monitorinfo.getMultiplayer() != null) {
//                        monitorinfo.getMultiplayer().stop();
//                        monitorinfo.getMultiplayer().close();
//                        monitorinfo.getMultiplayer().destroy();
//                    }
//                    if (monitorinfo.getTimer() != null) {
//                        monitorinfo.getTimer().stop();
//                        monitorinfo.getTimer().setText("");
//                    }
//                    Log.e(TAG,"number : " + monitorinfo.getNumber() + "\r\n" + "Ismonitor : " + monitorinfo.isIsmonitor());
//                    if (monitorinfo.isIsmonitor()) {
//                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
//                    }else {
//                        //如果为会议 隐藏当前视频
//                        ReleaseVideo(monitorinfo);
//                    }
//                }
//                videos.clear();
//                new MultiPlayer().cleanup();
//            }
            mTabHost.setCurrentTabByTag(GlobalConstant.SETTING_TAB);
            SetViewShow(GlobalConstant.ONLYTAB);
            // 如果视频监控正在进行则移除地图
//            if ((videos.size() > 0)
//                    && (videos.get(0).getVedioPort() > 0)) {
//                // 设置地图和监控显示
//                ll_position.setVisibility(View.GONE);
//                //ll_position.removeAllViews();
//                //ptt_3g_PadApplication.setMapRemoved(true);
//                rl_positiontools.setVisibility(View.GONE);
//                sl_videos.setVisibility(View.VISIBLE);
//                ll_split.setVisibility(View.VISIBLE);
//                //btn_info.setVisibility(View.VISIBLE);
//                btn_close.setVisibility(View.VISIBLE);
//                btn_pushvideo.setVisibility(View.VISIBLE);
//                btn_decodingupperwall.setVisibility(View.VISIBLE);
//                btn_decodingupperwa.setVisibility(View.VISIBLE);
//                DealVideoItmes();
//            }
        }
    }


    //获取设备分辨率-宽度
    private int getwindowmanager() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = screenWidth = display.getWidth();
        return screenWidth;
    }

    //获取设备分辨率-高度
    private int getwindowmanager_height() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenHeight = screenHeight = display.getHeight();
        return screenHeight;
    }

    // 页面适配   sws 11/07
    private void SetViewShow(int showtype) {
        switch (showtype) {
            case GlobalConstant.ALL:
//                int width = 450;
//                //分辨率
//                if (getwindowmanager() == 1280 && getwindowmanager_height() != 752) {
//                    // P1
//                    width = 360;
//                } else if (getwindowmanager() == 1280 && getwindowmanager_height() == 752) {
//                    width = 300;
//                    //Pad N800
//                } else if (getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024) {
//                    width = 300;
//                    //Pad IS910 RG910
//                } else if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
//                    width = 540;
//                }
                // 全部显示
                tabcontent.setVisibility(View.VISIBLE);
                tabcontent.setLayoutParams(new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.setleft_width_liner),
                        LinearLayout.LayoutParams.MATCH_PARENT));
                rl_content.setVisibility(View.VISIBLE);
                btn_gone.setText(getString(R.string.btn_position_gone));
                btn_gone.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pos_gone_click));
                break;
            case GlobalConstant.ONLYTAB:

                // 只显示Tab页
                tabcontent.setVisibility(View.VISIBLE);
                tabcontent.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                rl_content.setVisibility(View.GONE);

                if (currTabVisible) {
                    btn_gone.setText(getString(R.string.btn_position_gone));
                    btn_gone.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.pos_gone_click));
                } else {
                    btn_gone.setText(getString(R.string.btn_position_visible));
                    btn_gone.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.pos_show_click));
                }
                Log.e("计算Layout","布局设置完成");
                break;
            case GlobalConstant.ONLYCONTENT:
                // 只显示内容页
                tabcontent.setVisibility(View.GONE);
                rl_content.setVisibility(View.VISIBLE);
                btn_gone.setText(getString(R.string.btn_position_visible));
                btn_gone.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pos_show_click));
                Log.e("计算Layout","布局设置完成");
                break;
        }
    }

    // 项目展示适配器
    private class ImageAdapter extends BaseAdapter {
        private int currentItem;

        public void setImageLight(int selectItem) {
            this.currentItem = selectItem;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImageNormalIds.length;
        }

        @Override
        public Object getItem(int position) {
            return mImageNormalIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ImageView(MainActivity.this);

                convertView.setLayoutParams(new AbsListView.LayoutParams((int) parent.getWidth() / 1, (int) parent.getHeight() / 8));
            } else {
                convertView.setLayoutParams(new AbsListView.LayoutParams((int) parent.getWidth() / 1, (int) parent.getHeight() / 8));
            }
            if (position == currentItem) {
                convertView.setBackgroundResource(mImageSelectedIds[position]);
            } else {
                convertView.setBackgroundResource(mImageNormalIds[position]);
            }
            return convertView;
        }
    }

    // 登出接收器
    public class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MtcDelegate.log("lizhiwei 收到注销通知");
            // TODO Auto-generated method stub
            GroupManager.getInstance().clear();
            // 打开登录窗口
            // 通知调度台退出(有可能两次调用退出方法OnDestory也有退出)
            //MtcDelegate.logout();
            //MtcCli.Mtc_CliLogout();
            //SDK
            MediaEngine.GetInstance().ME_UnRegist();
        }
    }

    // 登入接收器
    public class DidloginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == GlobalConstant.ACTION_DIDLOGIN) {
                Log.e(TAG, "^^^^^MainAc 3444登入接收器^^^^^^");

                //getOldMsgData();
                String curruser = intent
                        .getStringExtra(GlobalConstant.KEY_CURR_USER);
                imgUserStates.setImageResource(R.drawable.userstates);
                txtUserNO.setText(sipUser.getUsername());


                //刷新对讲组
                Log.e(TAG, "============刷新对讲组");
                Intent intent1 = new Intent(GlobalConstant.ACTION_GROUPINFO);
                sendBroadcast(intent1);
            } else if (intent.getAction() == GlobalConstant.ACTION_SHOW) {

            }
        }
    }

    // 心跳机制消息接收
    public class HeartbeatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 需要依据心跳间隔进行登出操作。多长时间收不到心跳即需要退出
			/*
			 * HeartbeatManager.getInstance().putHeartbeat(Calendar.getInstance()
			 * .getTime());
			 */
        }
    }

    // 注册状态变更通知接收到all，当前登录用户状态
    public class SelfstatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (!intent.hasExtra(GlobalConstant.KEY_CURR_USER)) {
                return;
            }
			/*
			 * MemberInfo self =
			 * intent.getParcelableExtra(GlobalConstant.KEY_CURR_USER); if
			 * (self.getStatus() == GlobalConstant.GROUP_MEMBER_OFFLINE){
			 * imgUserStates.setImageResource(R.drawable.userstates1); } else {
			 * imgUserStates.setImageResource(R.drawable.userstates); }
			 */
        }
    }

    // 注册状态变更通知接收到all，发起重新注册
    public class ReregisterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 重新注册
            //MtcDelegate.login();
			/*String serverIP=sipServer.getServerIp();
			int serverPort=Integer.parseInt(sipServer.getPort());
			int disPort=10001;
			String userName=sipUser.getUsername();
			String password=sipUser.getPasswd();*/
            //重新注册SDK新方法
            //MediaEngine.GetInstance().ME_Regist(serverIP,serverPort , disPort,userName, password, true, 50);
            //Communicationbridge.PjSipLogin();
            // 取得状态
            int state = MtcDelegate.getState();
            // 状态为过期则退出
            if (state == MtcDelegate.STATE_EXPIRED) {
                ToastUtils.showToast(MainActivity.this, "Expired");
                finish();
                return;
            }
            // 状态为CPU不支持则退出
            else if (state == MtcDelegate.STATE_NOT_SUPPORTED) {
                ToastUtils.showToast(MainActivity.this, "CPU Not Supported");
                finish();
                return;
            }
        }
    }

    // 强迫下线接收器
    public class ForcedOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if ((CallingManager.getInstance().getCallingDialog() != null)
                    && (CallingManager.getInstance().getCallingDialog().size() > 0)) {
                for (WaitingDialog dialog : CallingManager.getInstance().getCallingDialog()) {
                    CallingManager.getInstance().removeCallingDialog(dialog.getDwSessId());
                    dialog.dismiss();
                    dialog = null;
                }
            }
            // 通话挂断
			/*for (CallingInfo callinginfo : CallingManager.getInstance()
					.getCallingData()) {
				MtcDelegate.log("lizhiwei 强退挂断");
//				onEnd(callinginfo.getDwSessId());   //yqq
				mtcCallDelegateTermed(callinginfo.getDwSessId(),
						MtcCallConstants.EN_MTC_CALL_TERM_REASON_NORMAL);
			}*/
            // 清空通话列表
            //			CallingManager.getInstance().clear();
            // 设置状态
            imgUserStates.setImageResource(R.drawable.userstates1);
            // 发送广播通知已获取到对讲组成员
			/*GroupManager.getInstance().clear();
			Intent temp = new Intent(GlobalConstant.ACTION_MEMBERINFO);
			sendBroadcast(temp);*/
            // 结束视频
			/*for (MonitorInfo monitorinfo : videos) {
				if (monitorinfo == null) {
					continue;
				}
				if (monitorinfo.getMultiplayer() != null) {
					monitorinfo.getMultiplayer().stop();
					monitorinfo.getMultiplayer().close();
					monitorinfo.getMultiplayer().destroy();
				}
				if (monitorinfo.getTimer() != null) {
					monitorinfo.getTimer().stop();
					monitorinfo.getTimer().setText("");
				}
			}
			new MultiPlayer().cleanup();
			videos.clear();
			MtcDelegate.log("lizhiwei 被迫下线设置视频布局DealVideoItmes");
			DealVideoItmes();*/
            // 结束响铃
            MtcRing.stop();
            // 结束弱电锁屏
            MtcProximity.stop();

            // 关闭定位图层
            if (mMapView != null) {
                mBaiduMap.setMyLocationEnabled(false);
            }

            //网络断开，不在定位发送消息
            if (mLocClient != null) {
                mLocClient.stop();
            }
        }
    }

    private class ForcedOnlineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(GlobalConstant.ACTION_FORCED_ONLINE)) {
                // 开启定位图层
                if (mBaiduMap != null) {
                    mBaiduMap.setMyLocationEnabled(true);
                }

                if (mLocClient != null) {
                    mLocClient.start();
                }

            } else if (action.equals(GlobalConstant.ACTION_CONNECTION_CHANGE)) {
                if (mSessId != MtcCliConstants.INVALIDID) {

                }
            }

        }

    }

    // 发送广播到主页面告诉其创建的会议为本地创建，请求自动接听
    public class MeetingCreateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GlobalConstant.KEY_MEETING_RECORD)) {
                currMeeting = intent
                        .getParcelableExtra(GlobalConstant.KEY_MEETING_RECORD);
                //Log.e("测试会议发起失败","Main 收到广播 5081 并对currMeeting赋值");
            }
        }
    }

    // TODO 现在应该是不会执行 会议创建失败，清空本地会议记录
    public class MeetingResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 是否创建了会议
            if (currMeeting == null) {
                return;
            }
            // 判断是否为当前会议
            String sid = intent.getStringExtra(GlobalConstant.KEY_MEETING_SID);
            if ((sid == null) || (sid.trim().length() <= 0)
                    || (!currMeeting.getsID().equals(sid))) {
                return;
            }
            // 设置为空
            boolean result = intent.getBooleanExtra(
                    GlobalConstant.KEY_MEETING_RESULT, false);
            if (!result) {
                currMeeting = null;
            }
        }
    }

    // 会议成员退会处理
    public class MeetingMemWithDrawReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String employeeid = intent
                    .getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);
            if ((employeeid == null) || (employeeid.trim().length() <= 0)) {
                return;
            }
            MonitorInfo currMonitorInfo = null;
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getNumber().equals(employeeid)) {
                    currMonitorInfo = monitorinfo;
                    break;
                }
            }
            if (currMonitorInfo != null) {
                for (MonitorInfo monitorinfo : videos) {
                    monitorinfo.setFocused(false);
                }
                currMonitorInfo.setFocused(true);
                DelVideoItem();
            }
        }
    }

    // 临时视频会议，自己发起会议，自动应答时发送此消息。
    public class PushVideoResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(GlobalConstant.ACTION_CALLING_TEMPINTERCOM_REQ)) {
                // 切换页面
                if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
                    mImageAdapter.setImageLight(0);
                    startActivity(0);
                }
                // 设置对讲组成员
                ArrayList<String> nos = new ArrayList<String>();
                for (PosMarkerInfo info : checkBoxMarker) {
                    if (nos != null && !nos.contains(info.getName())) {
                        nos.add(info.getName());
                    }
                }
                Intent callIntent = new Intent(
                        GlobalConstant.ACTION_POSITION_OPERATE);
                callIntent.putExtra(GlobalConstant.KEY_POSITION_TYPE,
                        GlobalConstant.INTERCOM_TAB);
                callIntent.putExtra(GlobalConstant.KEY_MEETING_NOS, nos);
                sendBroadcast(callIntent);

                Log.w("创建时临时对讲时的成员", "创建时临时对讲时的成员" + nos);

                // 清空地图marker  //11/06 sws 隐藏
				/*if(mBaiduMap != null)
				{
					mBaiduMap.clear();
				}
				if (selPosMarkerInfos != null) {
					selPosMarkerInfos.clear();
				}
				if (posMarkerInfosList != null) {
					for (PosMarkerInfo pmi : posMarkerInfosList) {
						addPosOverlay(pmi, false);
					}
				}*/
            } else if (action.equals(GlobalConstant.ACTION_MEETING_PUSHVIDEORESULT)) {
                boolean result = intent.getBooleanExtra(
                        GlobalConstant.KEY_MEETING_RESULT, true);
                if (result) {
                    //ToastUtils.showToast(MainActivity.this, "推送成功！");
                    String pushVideoNum = intent.getStringExtra(GlobalConstant.KEY_MEETING_PUSHNUM);


                    if (null != pushVideoNum && !"".equals(pushVideoNum)) {
                        Log.w(TAG, "##########pushVideoNum：" + pushVideoNum);
                        ptt_3g_PadApplication.setPushVideoNum(pushVideoNum);
                        Intent pushIntent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEO);
                        sendBroadcast(pushIntent);
                    }
                } else {
                    //ToastUtils.showToast(MainActivity.this, "推送失败,请重试！");
                }
            }
        }
    }

    // 会议、监控结束自动停止视频及监控
    public class MeetingHangupReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ((videos.size() > 0)
                    && (videos.get(0).getVedioPort() > 0)) {
                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo == null) {
                        continue;
                    }
                    if (monitorinfo.getMultiplayer() != null) {
                        monitorinfo.getMultiplayer().stop();
                        monitorinfo.getMultiplayer().close();
                        monitorinfo.getMultiplayer().destroy();
                    }
                    if (monitorinfo.getTimer() != null) {
                        monitorinfo.getTimer().stop();
                        monitorinfo.getTimer().setText("");
                    }
                    Log.e(TAG, "number : " + monitorinfo.getNumber() + "\r\n" + "Ismonitor : " + monitorinfo.isIsmonitor());
                    if (monitorinfo.isIsmonitor()) {
                        MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
                    } else {
                        //如果为会议 隐藏当前视频
                        ReleaseVideo(monitorinfo);
                    }
                }
                videos.clear();
            }
            Log.e(TAG, "会议、监控结束自动停止视频及监控" + videos.size());
            // 会议结束，停止视频
//                for (MonitorInfo monitorinfo : videos) {
//                    if (monitorinfo.getMultiplayer() != null) {
//                        monitorinfo.getMultiplayer().stop();
//                        monitorinfo.getMultiplayer().close();
//                        monitorinfo.getMultiplayer().destroy();
//                    }
//                    if (monitorinfo.getTimer() != null) {
//                        monitorinfo.getTimer().stop();
//                        monitorinfo.getTimer().setText("");
//                    }
//                }

            // 清空视频列表
            videos.clear();
            monitorMap.clear();
            // 处理视频界面
            DealVideoItmes();


            // 清空会议记录
            //currMeeting = null;
        }
    }

    // 发送广播到主页面告诉其会议记录信息已经改变
    public class MeetingRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GlobalConstant.KEY_MEETING_RECORD)) {
                MeetingRecords meeeting = intent
                        .getParcelableExtra(GlobalConstant.KEY_MEETING_RECORD);
                if ((currMeeting != null)
                        && (currMeeting.getId() == meeeting.getId())) {
                    currMeeting = meeeting;
                    //Log.e("测试会议发起失败","Main 收到广播 5276 并对currMeeting赋值");
                }
            }
        }
    }

    // 通话接收器
    public class CallBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String callAction = intent.getAction();
            if (callAction == GlobalConstant.ACTION_CALLING_CALLVOICE) {

                // 语音单通话
                String callUserNo = intent.getStringExtra("callUserNo");
                Log.e(TAG, "main  收到发起音频广播     callUserNo:" + callUserNo);
                if (callUserNo.length() > 0) {
                    onVoiceCall(callUserNo);
                }
            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLVIDEO) {
                // 视频单通话
                String callUserNo = intent.getStringExtra("callUserNo");
                Log.e(TAG, "main  收到发起视频广播     callUserNo:" + callUserNo);
                if (callUserNo.length() > 0) {
                    onVideoCall(callUserNo);
                }
            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLHANGUP) {
                // 通话挂断
                int mSessId = intent.getIntExtra(
                        GlobalConstant.KEY_CALL_SESSIONID,
                        MtcCliConstants.INVALIDID);
                Log.e(TAG, "=======SessId：" + mSessId);
                onEnd(mSessId);
                CallingManager.getInstance().removeCallingInfo(mSessId);
                MtcRing.stop();

            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLVOICEANSWER) {

                // 语音回复
                voiceResponse(intent);

            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLVIDEOANSWER) {
                // 视频回复
                videoReplies(intent);

            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLDECLINE) {
                // 忽略
                igNor(intent);

            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLSPEAKER) {
                // 扬声器
                //操作以不在Main界面
            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLMUTE) {

                //判断当前是否处于静音状态
                if (audioManager == null) {
                    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    //设置声音模式
                    audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
                }
                boolean mute = audioManager.isMicrophoneMute();
                boolean nute = intent.getBooleanExtra("NUTE", false);
                if (mute) {
                    //设置取消静音
                    audioManager.setMicrophoneMute(nute);
                    Log.e(TAG, "设置麦克风 main 4146  nute:" + nute);
                }

            } else if (callAction == GlobalConstant.ACTION_CALLING_STARTRING) {
                // 开始响铃
                String ringtone = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, "ringtone.mp3");
                onStartRing(ringtone);
                //MtcRing.startRing(MainActivity.this, ringtone);
            } else if (callAction == GlobalConstant.ACTION_CALLING_STORPING) {
                // 结束响铃
                onStopRing();
            } else if (callAction == GlobalConstant.ACTION_CALLING_STARTRINGBACK) {
                // 开始回铃
                onStartRingBack("ringback.wav");
            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLHOLD) {
                int sessId = intent.getIntExtra(
                        GlobalConstant.KEY_CALL_SESSIONID,
                        MtcCliConstants.INVALIDID);
                // 保持
                onHold(sessId);
            } else if (callAction == GlobalConstant.ACTION_CALLING_CALLUNHOLD) {
                // 解除保持
                int sessId = intent.getIntExtra(
                        GlobalConstant.KEY_CALL_SESSIONID,
                        MtcCliConstants.INVALIDID);
                onUnHold(sessId);
            } else if (callAction == GlobalConstant.ACTION_CALLING_OTHERCALLVOICE) {
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                    mImageAdapter.setImageLight(1);
                    startActivity(1);// 切换到通讯录
                }
                String callUserNo = intent.getStringExtra("callUserNo");
                if (callUserNo.length() > 0) {
                    onVoiceCall(callUserNo);
                }
            } else if (callAction == GlobalConstant.ACTION_CALLING_OTHERCALLVIDEO) {
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                    mImageAdapter.setImageLight(1);
                    startActivity(1);// 切换到通讯录
                }
                String callUserNo = intent.getStringExtra("callUserNo");
                if (callUserNo.length() > 0) {
                    onVideoCall(callUserNo);
                }
            } else if (GlobalConstant.ACTION_CALLING_CALLNETCHANGEHANGUP.equals(callAction)) {
                // 通话挂断
                int mSessId = intent.getIntExtra(
                        GlobalConstant.KEY_CALL_SESSIONID,
                        MtcCliConstants.INVALIDID);
                Log.e(TAG, "***30秒超时网络未连接挂断所有呼叫***mSessId：" + mSessId);
                mCallState = GlobalConstant.CALL_STATE_ENDING;
                mtcCallDelegateTermed(mSessId, MtcCallConstants.EN_MTC_CALL_TERM_REASON_NORMAL);
                CallingManager.getInstance().removeCallingInfo(mSessId);
            }
        }
    }

    //忽略
    private void igNor(Intent intent) {
        int sessId = intent.getIntExtra(
                GlobalConstant.KEY_CALL_SESSIONID,
                MtcCliConstants.INVALIDID);
        onDecline(sessId);
        CallingManager.getInstance().removeCallingInfo(sessId);
    }

    //视频回复
    private void videoReplies(Intent intent) {
        int sessId = intent.getIntExtra(
                GlobalConstant.KEY_CALL_SESSIONID,
                MtcCliConstants.INVALIDID);
        Log.e(TAG, "====3875");
        onVideoAnswer(sessId);
        MtcRing.stop();
    }

    //接听语音
    private void voiceResponse(Intent intent) {
        int sessId = intent.getIntExtra(
                GlobalConstant.KEY_CALL_SESSIONID,
                MtcCliConstants.INVALIDID);
        Log.w("******收到接听广播********", "******收到接听广播********" + sessId);
        Log.e(TAG, "Main界面收到自动接听广播 进行语音回复 sessId:" + sessId);
        onVoiceAnswer(sessId);
        MtcRing.stop();

    }

    // 短消息接收
    public class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            String messageAction = intent.getAction();
            if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGRECEIVEOK) {

                String userNo = intent.getStringExtra("userNo");
                String msgBody = intent.getStringExtra("content");
                Log.e(TAG, "短信接收 4862 userNo :" + userNo + "\r\n" + "msgbody: " + msgBody);
                SQLiteHelper sqLiteHelper = new SQLiteHelper(MainActivity.this);
                sqLiteHelper.open();

                MessageRecords newMessageRecords = new MessageRecords();
                newMessageRecords.setUserNo(sipUser.getUsername());
                newMessageRecords.setBuddyNo(userNo);
                newMessageRecords.setContent(msgBody);
                newMessageRecords.setContentType(GlobalConstant.MESSAGE_TEXT);
                newMessageRecords.setLocalFileUri("");
                newMessageRecords.setServerFileUri("");
                newMessageRecords.setLength(msgBody.length());
                newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_IN);
                Date nowDate = new Date();
                newMessageRecords.setSendDate(nowDate);
                newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_OK);
                newMessageRecords.setReceiveDate(nowDate);
                newMessageRecords
                        .setReceiveState(GlobalConstant.MESSAGE_READ_NO);
                newMessageRecords.setLayout(GlobalConstant.LAYOUT_TEXT_IN);
                sqLiteHelper.createMessageRecords(newMessageRecords);
                sqLiteHelper.closeclose();

                // 更新导航UI，播放短信铃声
                //PlayRingTone();

                // 发送广播刷新短信列表或对讲页面
                Intent messageIncoming = new Intent(
                        GlobalConstant.ACTION_MESSAGE_MSGINCOMMING);
                sendBroadcast(messageIncoming);


            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGUPLOADOK) {
                Log.e(TAG, "MainActivity=====3942===  接收到发送的广播  ACTION_MESSAGE_MSGUPLOADOK");
                // 切换到消息activity
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MESSAGE_TAB) {
                    ToastUtils.showToast(MainActivity.this, "收到新的短信");
                    //mImageAdapter.setImageLight(3);
                    //mTabHost.setCurrentTabByTag(GlobalConstant.MESSAGE_TAB);
                }
                ToastUtils.showToast(MainActivity.this, "收到新短信");
                // 发送文件接收
                String srcnum = intent.getStringExtra("srcnum");
                String fileid = intent.getStringExtra("fileid");
                String filename = intent.getStringExtra("filename");
                String filepath = intent.getStringExtra("filepath");
                int filetype = intent.getIntExtra("filetype", 0);
                Log.e(TAG, "srcnum:" + srcnum + "  fileid:" + fileid
                        + "  filename:" + filename + "   filepath:" + filepath
                        + "  filetype:" + filetype);
                SQLiteHelper sqLiteHelper = new SQLiteHelper(MainActivity.this);
                sqLiteHelper.open();

                MessageRecords newMessageRecords = new MessageRecords();
                newMessageRecords.setUserNo(sipUser.getUsername());
                newMessageRecords.setBuddyNo(srcnum);
                newMessageRecords.setContent(filename);

                newMessageRecords.setServerFileUri(filepath);
                newMessageRecords.setLength(0);
                newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_IN);
                Date nowDate = new Date();
                newMessageRecords.setSendDate(nowDate);
                newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_OK);
                newMessageRecords.setReceiveDate(nowDate);
                newMessageRecords
                        .setReceiveState(GlobalConstant.MESSAGE_READ_NO);

                if (filetype == 2) {
                    newMessageRecords.setLocalFileUri(CommonMethod
                            .getMessageFileDownPath(2) + filename);// 等待下载
                    newMessageRecords
                            .setContentType(GlobalConstant.MESSAGE_IMG);
                    newMessageRecords.setLayout(GlobalConstant.LAYOUT_IMG_IN);
                } else if (filetype == 3) {
                    newMessageRecords.setLocalFileUri(CommonMethod
                            .getMessageFileDownPath(3) + filename);// 等待下载
                    newMessageRecords
                            .setContentType(GlobalConstant.MESSAGE_AUDIO);
                    newMessageRecords.setLayout(GlobalConstant.LAYOUT_AUDIO_IN);
                } else if (filetype == 4) {
                    newMessageRecords.setLocalFileUri(CommonMethod
                            .getMessageFileDownPath(4) + filename);// 等待下载
                    newMessageRecords
                            .setContentType(GlobalConstant.MESSAGE_VIDEO);
                    newMessageRecords.setLayout(GlobalConstant.LAYOUT_VIDEO_IN);
                } else if (filetype == 8) {
                    newMessageRecords.setLocalFileUri(CommonMethod
                            .getMessageFileDownPath(8) + filename);// 等待下载
                    newMessageRecords
                            .setContentType(GlobalConstant.MESSAGE_FILE);
                    newMessageRecords.setLayout(GlobalConstant.LAYOUT_TEXT_IN);
                } else if (filetype == 5) {//位置截图
                    newMessageRecords.setLocalFileUri(CommonMethod
                            .getMessageFileDownPath(5) + filename);// 等待下载
                    newMessageRecords
                            .setContentType(GlobalConstant.MESSAGE_IMG);
                    newMessageRecords.setLayout(GlobalConstant.LAYOUT_IMG_IN);
                }

                sqLiteHelper.createMessageRecords(newMessageRecords);
                sqLiteHelper.closeclose();

                //PlayRingTone(); //播放提示音


                ptt_3g_PadApplication.setDownConfig("");
                // 发送广播刷新短信列表或对讲页面
                Intent messageIncoming = new Intent(
                        GlobalConstant.ACTION_MESSAGE_MSGINCOMMING);
                sendBroadcast(messageIncoming);
                Log.e(TAG, "MainActivity=====4014===  发送广播通知消息界面刷新数据   ACTION_MESSAGE_MSGINCOMMING");
            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT) {
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CONTACT_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.CONTACT_TAB);
                    mImageAdapter.setImageLight(4);
                    startActivity(4);// 切换到通讯录
                }
            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTYES) {
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MESSAGE_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.MESSAGE_TAB);
                    mImageAdapter.setImageLight(3);
                    startActivity(3);
                }
            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGADDCONTACTNO) {
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MESSAGE_TAB) {
                    mTabHost.setCurrentTabByTag(GlobalConstant.MESSAGE_TAB);
                    mImageAdapter.setImageLight(3);
                    startActivity(3);
                }
            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGMAINNEW) {
                //  if (mTabHost.getCurrentTabTag() != GlobalConstant.MESSAGE_TAB) {
                mTabHost.setCurrentTabByTag(GlobalConstant.MESSAGE_TAB);
                mImageAdapter.setImageLight(3);
                startActivity(3);
                // }
            }
        }
    }

    // 通话记录广播接收器
    public class CallingRecordBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int conferenceid = intent.getIntExtra(
                    GlobalConstant.KEY_CALL_CONFERENCEID, 0);
            if (conferenceid <= 0) {
                return;
            }
            // 页面切换
            if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                mImageAdapter.setImageLight(2);
                startActivity(2);
            }
            // 通知会议
            Intent callIntent = new Intent(
                    GlobalConstant.ACTION_CALLING_CONFERENCE);
            callIntent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
            callIntent.putExtra(GlobalConstant.KEY_CALL_CONFERENCEID,
                    conferenceid);
            sendBroadcast(callIntent);
        }
    }

    // 接聽下一路通話通知
    public class AnswerNextCallBroadcastReveiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (CallingManager.getInstance().getAnswerData().size() <= 0) {
                return;
            }
            final CallingInfo callingInfo = CallingManager.getInstance().getAnswerData().get(0);


            Log.e(TAG, "接听下一路通知 5045 : " + callingInfo.getAnswerType());
            CallingManager.getInstance().removeAnswerInfo(callingInfo.getDwSessId());
            if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                Log.e(TAG, " GlobalConstant.CALL_TYPE_INTERCOM ======4083");
                if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
                    Log.e(TAG, "^^^^接听下一路切换到对讲界面^^^^^^");
                    mImageAdapter.setImageLight(0);
                    mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
                    // 取得当前tab页是否显示
                    currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication, GlobalConstant.SP_SHOWORHIDDEN);
                    if (currTabVisible) {
                        // 分页显示
                        SetViewShow(GlobalConstant.ALL);
                    } else {
                        // 分页隐藏
                        SetViewShow(GlobalConstant.ONLYCONTENT);
                    }
                }
            } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                Log.e(TAG, " GlobalConstant.CALL_TYPE_CALLING ======4099");
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
                    mImageAdapter.setImageLight(1);
                    mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                    SetViewShow(GlobalConstant.ONLYTAB);
                    Log.e(TAG, "************callingInfo.getDwSessId:4192");
                }
            } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                Log.e(TAG, " GlobalConstant.CALL_TYPE_MEETING ======4105");
                if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                    mImageAdapter.setImageLight(2);

                    mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                    // 取得当前tab页是否显示
                    currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication, GlobalConstant.SP_SHOWORHIDDEN);
                    if (currTabVisible) {
                        // 分页显示
                        SetViewShow(GlobalConstant.ALL);
                    } else {
                        // 分页隐藏
                        SetViewShow(GlobalConstant.ONLYCONTENT);
                    }
                    // 如果视频会议正在进行则移除地图
                    if ((videos.size() > 0)
                            && (videos.get(0).getVedioPort() > 0)) {
                        // 设置地图和监控显示
                        ll_position.setVisibility(View.GONE);
                        ll_position.removeAllViews();
                        ptt_3g_PadApplication.setMapRemoved(true);
                        rl_positiontools.setVisibility(View.GONE);
                        sl_videos.setVisibility(View.VISIBLE);
                        ll_split.setVisibility(View.VISIBLE);
                        btn_info.setVisibility(View.VISIBLE);
                        btn_close.setVisibility(View.VISIBLE);
                        btn_pushvideo.setVisibility(View.VISIBLE);
                        btn_decodingupperwall.setVisibility(View.VISIBLE);
                        btn_decodingupperwa.setVisibility(View.VISIBLE);
                        DealVideoItmes();
                    }
                }
            }
            // 取得本地前置摄像头，并设置 yuezs 隐藏
			/*int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
			if (camCnt >= 1) {
				MtcMedia.Mtc_VideoSetInputDev("front");
			}*/

            // 取得远程摄像头
            if (MediaEngine.GetInstance().ME_HasVideo(callingInfo.getDwSessId())) {
                // mtcCallDelegateStartPreview();//会议 还没做等SDK出来 加上 yuezs
            } else {
                // MtcProximity.start(MainActivity.this);
            }

            // 是否延迟提供音视频
            mIsDelayOffer = MediaEngine.GetInstance().ME_HasVideo(callingInfo.getDwSessId()) == false && MediaEngine.GetInstance().ME_HasVideo(callingInfo.getDwSessId()) == false;

            // 保证界面始终在屏幕上
            keepScreenOn(true);
            // 屏幕始终不锁定
            showWhenLocked(true);

            AutoAnswerBena autoAnswerBena = new AutoAnswerBena();
            autoAnswerBena.setCallId(callingInfo.getDwSessId());
            autoAnswerBena.setIsAutoAnswer(GlobalConstant.AUTOANSWER_IMPLEMENT);   //1自动接听
            CommonMethod.getInstance().setAutoAnswerBena(autoAnswerBena);

//                    //呼入新来电
//                    MtcCallDelegate.getCallback().mtcCallDelegateIncoming(callingInfo.getDwSessId());

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //二次呼叫逻辑
            //挂断当前呼叫在接听新呼叫
            IncomingSingle incomingSingle = new IncomingSingle();
            incomingSingle.setCallId(callingInfo.getDwSessId());
            incomingSingle.setState(callingInfo.getState());
            if (callingInfo.getAnswerType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
                incomingSingle.setVideo(true);
            } else if (callingInfo.getAnswerType() == GlobalConstant.CONFERENCE_MEDIATYPE_VOICE) {
                incomingSingle.setVideo(false);
            }
            incomingSingle.setNumber(callingInfo.getRemoteName());
            incomingSingle.setMe_idsPara(callingInfo.getMe_idsPara());
            EventBus.getDefault().post(incomingSingle);
            Log.e(TAG, "5106 isiVideo : " + callingInfo.isVideo());

//            // 音频回复
//            if (callingInfo.getAnswerType() == GlobalConstant.CONFERENCE_MEDIATYPE_VOICE) {
//                onVoiceAnswer(callingInfo.getDwSessId());
//            } else {
//                Log.e("MainActivity======sws========", "4161");
//                onVideoAnswer(callingInfo.getDwSessId());
//            }
//            Log.e("MainActivity========4257=====", "lizhiwei 接听了通话" + callingInfo.getDwSessId());
//            Log.e("**************MainAc接聽下一路通話通知*************4256", "**************MainAc接聽下一路通話通知*************4256");
//            // 来电广播
//            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
//            callingIncoming.putExtra(KEY_CALL_CHANGEVIEW, false);
//            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
//            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callingInfo.getCalltype());
//            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, callingInfo.getRemoteName());
//            sendBroadcast(callingIncoming);
//            Log.e("==Sws测试响铃错误", "main     4316");

        }
    }

    // 视频会议、监控创建视频接收器
    public class CreateVideoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == GlobalConstant.ACTION_MONITOR_CREATEVIDEO2) {



                // 将页面设置到监控页面
                // 隐藏地图
                //ll_position.setVisibility(View.GONE);
                //ll_position.removeAllViews();// 造成切换到定位页面，背景为蓝屏
                ptt_3g_PadApplication.setMapRemoved(false);
                rl_positiontools.setVisibility(View.GONE);
                ll_pos.setVisibility(View.GONE);
                ll_pos_single.setVisibility(View.GONE);
                // 隐藏单通视频
			/* fl_calling.setVisibility(View.GONE); */
                // 显示监控
                sl_videos.setVisibility(View.VISIBLE);
                // 显示分屏
                ll_split.setVisibility(View.VISIBLE);
                btn_info.setVisibility(View.VISIBLE);
                btn_close.setVisibility(View.VISIBLE);
                btn_pushvideo.setVisibility(View.VISIBLE);
                btn_decodingupperwall.setVisibility(View.VISIBLE);
                btn_decodingupperwa.setVisibility(View.VISIBLE);

                return;
            }

            List<MonitorInfo> monitorInfos = new ArrayList<>();
            for(MonitorInfo monitorInfo : videos){
                if (monitorInfo.getVedioPort() > 0){
                    monitorInfos.add(monitorInfo);
                }
            }

            //TODO 判断当前分屏数是否可以容纳当前展示的视频数量
            if (ptt_3g_PadApplication.getCurrentSpliCount().equals("Nine")){
                if (monitorInfos.size() >= 9){
                    //TODO 如果当前为九分屏并且当前视频数量大于等于9个则return 并提示用户
                    Toast.makeText(context, "当前视频展示区域已满", Toast.LENGTH_SHORT).show();
                    return;

                }
            }

            if (ptt_3g_PadApplication.getCurrentSpliCount().equals("One")){
                if (monitorInfos.size() >= 1 ){
                    Toast.makeText(context, "当前视频展示区域已满", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (ptt_3g_PadApplication.getCurrentSpliCount().equals("Four")){
                if (monitorInfos.size() >=4){
                    Toast.makeText(context, "当前视频展示区域已满", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // TODO Auto-generated method stub
            // 需要判断当前是否有监控视频正在进行
            MonitorInfo currMonitorinfo = intent.getParcelableExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO);

            if (currMonitorinfo == null) {
                return;
            }

            if (currNumColumn == NumColumns.ONE) {
                Log.e(TAG, "videos.size():" + videos.size());
                if (videos.size() > 0) {
                    boolean exists = false;
                    for (MonitorInfo monitorinfo : videos) {
                        if (monitorinfo.getNumber().equals(currMonitorinfo.getNumber())
                                && monitorinfo.isIsmonitor()) {
                            Log.e(TAG, "monitorinfo.getNumber():" + monitorinfo.getNumber());
                            Log.e(TAG, "currMonitorinfo.getNumber():" + currMonitorinfo.getNumber());
                            Log.e(TAG, "monitorinfo.isIsmonitor():" + monitorinfo.isIsmonitor());
                            exists = true;
                        }
                    }
                    if (exists) {
                        ToastUtils.showToast(MainActivity.this, getString(R.string.monitorAlreayGoing));
                        return;
                    }
                    for (MonitorInfo monitorinfo : videos) {
                        if (monitorinfo == null) {
                            Log.e(TAG, "monitorinfo == null    4294");
                            continue;
                        }
                        if (monitorinfo.getMultiplayer() != null) {

                            monitorinfo.getMultiplayer().stop();
                            monitorinfo.getMultiplayer().close();
                            monitorinfo.getMultiplayer().destroy();
                            Log.e(TAG, "monitorinfo != null    4305  走完了stop，close，destroy");
                        }
                        if (monitorinfo.getTimer() != null) {
                            monitorinfo.getTimer().stop();
                            monitorinfo.getTimer().setText("");
                            Log.e(TAG, "monitorinfo.getTimer() != null    4307  走完了monitorinfo.getTimer().stop();");
                        }
                        ReleaseVideo(monitorinfo);
                        // 如果是监控，则发送结束监控
                        Log.e(TAG, "判断是否是监控monitorinfo.isIsmonitor():" + monitorinfo.isIsmonitor());
                        if (monitorinfo.isIsmonitor()) {
                            MonitorEnd(currMonitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
                        }
                    }
                    videos.clear();
                    //new MultiPlayer().cleanup();
                    //MtcDelegate.log("lizhiwei 视频类型不同结束之前设置视频布局DealVideoItmes");

                    DealVideoItmes();
                }
            } else if (currNumColumn == NumColumns.TWO) {

            } else if (currNumColumn == NumColumns.THREE) {

            }

            // 将页面设置到监控页面
            // 隐藏地图
            ll_position.setVisibility(View.GONE);
            //ll_position.removeAllViews();// 造成切换到定位页面，背景为蓝屏
            ptt_3g_PadApplication.setMapRemoved(false);
            rl_positiontools.setVisibility(View.GONE);
            ll_pos.setVisibility(View.GONE);
            ll_pos_single.setVisibility(View.GONE);
            // 隐藏单通视频
			/* fl_calling.setVisibility(View.GONE); */
            // 显示监控
            sl_videos.setVisibility(View.VISIBLE);
            // 显示分屏
            ll_split.setVisibility(View.VISIBLE);
            btn_info.setVisibility(View.VISIBLE);
            btn_close.setVisibility(View.VISIBLE);
            btn_pushvideo.setVisibility(View.VISIBLE);
            btn_decodingupperwall.setVisibility(View.VISIBLE);
            btn_decodingupperwa.setVisibility(View.VISIBLE);
            // 取得端口
            boolean exists = false;
			/* int maxport = 9998; */
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getNumber().equals(currMonitorinfo.getNumber())) {
                    Log.e(TAG, "4351===monitorinfo.getNumber():" + monitorinfo.getNumber());
                    Log.e(TAG, "4351===currMonitorinfo.getNumber():" + currMonitorinfo.getNumber());
                    exists = true;
                }
				/*
				 * if (monitorinfo.getVedioPort() > maxport){ maxport =
				 * monitorinfo.getVedioPort(); }
				 */
            }
            if (exists) {
                Toast.makeText(context, getString(R.string.monitorAlreayGoing), Toast.LENGTH_SHORT).show();
                Log.e("*/*/*/*/*/*/*/**","ting le 5886");
                return;
            }
            //YUEZS ADD 2015-09-01
            Integer VedioPort = getVedioPort();
            if (VedioPort > 0) {
                currport = getVedioPort() + 2;
                settingsVedioPort(currport);
            } else {
                currport = currport + 2;
                settingsVedioPort(currport);
            }
            Log.e(TAG, "设置端口5287  ：" + currport + "");
            // 设置端口
            currMonitorinfo.setVedioPort(currport);
			/*
			 * MtcDelegate.log("lizhiwei 添加新的视频设置视频布局currMonitorinfo");
			 * CreateVideoItem(currMonitorinfo);
			 */
            videos.add(currMonitorinfo);


            for (int i = 0; i < monitorInfoList.size(); i++){
                if (monitorInfoList.get(i).getVedioPort() <= 0){
                    MonitorInfo monitorInfo = monitorInfoList.get(i);
                    currMonitorinfo.setView(monitorInfo.getView());
                    currMonitorinfo.setmLinearLayot(monitorInfo.getmLinearLayot());
                    monitorInfoList.remove(monitorInfo);
                    monitorInfoList.add(i,currMonitorinfo);
                    break;
                }
            }
			/*
			 * MtcDelegate.log("lizhiwei 添加新的视频设置视频布局DealVideoItmes");
			 * DealVideoItmes();
			 */

            if (currMonitorinfo.isIsmonitor()) {
                // 视频监控，发出视频申请
                currMonitorinfo.setIp(CommonMethod.getIP().trim());
                currMonitorinfo.setGetVideo(false);
                MonitorStart(currMonitorinfo);
                Log.e(TAG, "视频监控，发出视频申请");
            } else {
                // 视频会议，发出视频申请
                currMonitorinfo.setGetVideo(true);
                GetVideo(currMonitorinfo);
                Log.e(TAG, "视频会议，发出视频申请");
            }
        }
    }

    // 视频会议、监控移除视频接收器
    public class RemoveVideoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            MonitorInfo currMonitorinfo = intent
                    .getParcelableExtra(GlobalConstant.KEY_MONITOR_MEMBERINFO);
            if (currMonitorinfo == null) {
                return;
            }
            boolean exists = false;
            for (MonitorInfo monitor : videos) {
                if (monitor.getNumber().equals(currMonitorinfo.getNumber())) {
                    currMonitorinfo = monitor;
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return;
            }
            // 移除监控组
            videos.remove(currMonitorinfo);
            // 结束视频
            if (currMonitorinfo.getMultiplayer() != null) {
                currMonitorinfo.getMultiplayer().stop();
                currMonitorinfo.getMultiplayer().close();
                currMonitorinfo.getMultiplayer().destroy();
            }
            if (currMonitorinfo.getTimer() != null) {
                currMonitorinfo.getTimer().stop();
                currMonitorinfo.getTimer().setText("");
            }

            // 如果是监控，则发送结束监控
            if (currMonitorinfo.isIsmonitor()) {
                MonitorEnd(currMonitorinfo.getName(), currMonitorinfo.getSid(), currMonitorinfo.getCid(), currMonitorinfo.getNumber());
            }
            // 如果没有监控则结束会议，设置结束视频
			/*if (videos.size() <= 0) {
				if (currMonitorinfo.isIsmonitor()) {
					 CallingManager.getInstance().setIsvideoing(false);
					 // 结束会议 Intent meetIntent = new
					 Intent(GlobalConstant.ACTION_MONITOR_CALLHANGUP);
					 sendBroadcast(meetIntent);
				}
			}*/
            // 处理布局
            MtcDelegate.log("lizhiwei 会议请出成员设置视频布局DealVideoItmes");
            DealVideoItmes();
        }

    }

    // 会议获取视频接收器
    public class GetVideoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            boolean result = intent.getBooleanExtra(
                    GlobalConstant.KEY_MEETING_RESULT, false);
           // DealVideoItmes();
            if (result) {
				/*
				 * String streamid =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_STREAMID);//
				 * 监控视频流标识 String ip =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_IP);//
				 * 监控获取到的视频的IP地址 String port =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_PORT);//
				 * 监控获取到的视频的端口 String codec =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_CODEC);//
				 * 监控编解码 String stream =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_STREAM);//
				 * 监控码流 String framerate =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_FRAMERATE
				 * );// 监控帧率
				 */
                String cid = intent
                        .getStringExtra(GlobalConstant.KEY_MEETING_CID);// 会议唯一标识
                String employeeid = intent
                        .getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);// 请求用户编号

                //保存视频cid  用于用户上墙下墙
                monitorsSessionID.put(employeeid, cid);

                // 判断视频是否存在
                MonitorInfo currMonitorInfo = null;
                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo.getNumber().equals(employeeid)) {
                        currMonitorInfo = monitorinfo;
                        break;
                    }
                }
                if (currMonitorInfo == null) {
                    return;
                }
                // 设置视频信息
                currMonitorInfo.setCid(cid);

                AddVideoItem(currMonitorInfo,false);

				/*
				 * currMonitorInfo.setStreamid(streamid);
				 * currMonitorInfo.setIp(ip);
				 * currMonitorInfo.setRemotePort(port);
				 * currMonitorInfo.setCodec(codec);
				 * currMonitorInfo.setStream(stream);
				 * currMonitorInfo.setFramerate(framerate); // 创建视频
				 * MtcDelegate.log
				 * ("lizhiwei GetVideoBroadcastReceiver视频展示AddVideoItem");
				 * AddVideoItem(currMonitorInfo, false);
				 */
            } else {
				/*
				 * String error =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_ERROR);//
				 * 监控获取视频错误号 String dis =
				 * intent.getStringExtra(GlobalConstant.KEY_MONITOR_DIS);//
				 * 监控获取视频错误描述 String cid =
				 * intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);//
				 * 会议唯一标识
				 */
                String employeeid = intent
                        .getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);
                ToastUtils.showToast(MainActivity.this, "获取视频监控失败");
                // 取得创建视频失败用户
                MonitorInfo currMonitorInfo = null;
                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo.getNumber().equals(employeeid)) {
                        currMonitorInfo = monitorinfo;
                        break;
                    }
                }
                if (currMonitorInfo == null) {
                    return;
                }
                if (currMonitorInfo.getMultiplayer() != null) {
                    currMonitorInfo.getMultiplayer().stop();
                    currMonitorInfo.getMultiplayer().close();
                    currMonitorInfo.getMultiplayer().destroy();
                }
                if (currMonitorInfo.getTimer() != null) {
                    currMonitorInfo.getTimer().stop();
                    currMonitorInfo.getTimer().setText("");
                }
                // 移除用户
                videos.remove(currMonitorInfo);

                // 如果是监控，则发送结束监控
                if (currMonitorInfo.isIsmonitor()) {
                    MonitorEnd(currMonitorInfo.getName(), currMonitorInfo.getSid(),
                            currMonitorInfo.getCid(), currMonitorInfo.getNumber());
                }
				/*
				 * // 如果是监控 if (videos.size() <= 0){ // 通知结束监控会议 if
				 * (currMonitorInfo.isIsmonitor()){ // 结束监控会议 Intent meetIntent
				 * = new Intent(GlobalConstant.ACTION_MONITOR_CALLHANGUP);
				 * sendBroadcast(meetIntent); } }
				 */
                // 处理视频显示
                MtcDelegate.log("lizhiwei 会议获取视频失败设置视频布局DealVideoItmes");
                DealVideoItmes();
            }
        }
    }

    // 会议获取视频事件接收器
    private class GetVideoEVTBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);// 会议唯一标识
            String employeeid = intent
                    .getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);// 请求用户编号
            String ip = intent.getStringExtra(GlobalConstant.KEY_MONITOR_IP);// 监控获取到的视频的IP地址
            String port = intent
                    .getStringExtra(GlobalConstant.KEY_MONITOR_PORT);// 监控获取到的视频的端口
            Log.e(TAG, "收到PTT的广播     cid：" + cid + " ,employeeid:" + employeeid + " ,ip:" + ip + ", port:" + port);
            MtcDelegate.log("lizhiwei GetVideoBroadcastReceiver视频展示"
                    + employeeid + "," + ip + "," + port);
            // 判断视频是否存在

            for (MonitorInfo monitorinfo : videos) {
                String no = monitorinfo.getNumber();
            }

            MonitorInfo currMonitorInfo = null;
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getNumber().equals(employeeid)) {
                    currMonitorInfo = monitorinfo;
                    break;
                }
            }
            if (currMonitorInfo == null) {
                return;
            }
            // 设置视频信息
            currMonitorInfo.setCid(cid);
            currMonitorInfo.setIp(ip);
            currMonitorInfo.setRemotePort(port);
            // 创建视频
            MtcDelegate
                    .log("lizhiwei GetVideoBroadcastReceiver视频展示AddVideoItem");
            AddVideoItem(currMonitorInfo, false);
        }
    }

    // 监控开始视频接收器
    private class VbugStartBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub

            boolean result = intent.getBooleanExtra(
                    GlobalConstant.KEY_MEETING_RESULT, false);// 是否成功

            String dstid = intent
                    .getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);// 监控号码
            if (result) {
                String cid = intent
                        .getStringExtra(GlobalConstant.KEY_MEETING_CID);// 会议唯一标识
                if (cid != null) {
                    Log.e(TAG, "添加到集合中 将cid:" + cid + "  number:" + prefs.getString("number", null));
                }

                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo.getNumber().equals(dstid)) {
                        monitorinfo.setCid(cid);
                        Log.w(TAG, "开启监控视频获取CID=" + cid);
                        break;
                    }
                }
            } else {
                // 需要将对应视频关闭
                MonitorInfo currMonitorInfo = null;
                for (MonitorInfo monitorinfo : videos) {
                    if (monitorinfo.getNumber().equals(dstid)) {
                        currMonitorInfo = monitorinfo;
                        break;
                    }
                }
                if (currMonitorInfo != null) {
                    currMonitorInfo.setFocused(true);
                    // 关闭视频
                    MtcDelegate.log("lizhiwei 开始视频失败设置视频布局DelVideoItem");
                    DelVideoItem();
                }
                ToastUtils.showToast(MainActivity.this, "调取监控失败！");
            }
        }

    }

    // 监控结束视频接收器
    private class VbugEndBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            boolean result = intent.getBooleanExtra(
                    GlobalConstant.KEY_MEETING_RESULT, false);// 是否成功
            String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);// 会议唯一标识
			/*
			 * String dstid =
			 * intent.getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);//
			 * 监控号码
			 */
//            if (result) {
//                // 需要将对应视频关闭
//                MonitorInfo currMonitorInfo = null;
//                for (MonitorInfo monitorinfo : videos) {
//					/*
//					 * if (monitorinfo.getNumber().equals(dstid)){
//					 * currMonitorInfo = monitorinfo; break; }
//					 */
//                    if ((monitorinfo.getCid() != null)
//                            && (monitorinfo.getCid().equals(cid))) {
//                        currMonitorInfo = monitorinfo;
//                        break;
//                    }
//                }
//                if (currMonitorInfo != null) {
//                    MtcDelegate.log("lizhiwei 收到结束消息，找到结束视频");
//                    currMonitorInfo.setFocused(true);
//                    // 关闭视频
//                    DelVideoItem();
//                }
//            } else {
//                // 无操作
//                //ToastUtils.showToast(MainActivity.this, "结束监控失败！");
//            }
        }
    }

    // 监控获取视频接收器
    private class VbugEventBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String cid = intent.getStringExtra(GlobalConstant.KEY_MEETING_CID);// 会议唯一标识
            String dstid = intent.getStringExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID);// 监控号码
            String ip = intent.getStringExtra(GlobalConstant.KEY_MONITOR_IP);// 监控获取到的视频的IP地址
            String port = intent.getStringExtra(GlobalConstant.KEY_MONITOR_PORT);// 监控获取到的视频的端口

            // 判断视频是否存在
            MonitorInfo currMonitorInfo = null;
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getNumber().equals(dstid)) {
                    currMonitorInfo = monitorinfo;
                    break;
                }
            }
            if (currMonitorInfo == null) {
                Log.e("*/*/*/*/*/*/*/**","ting le  6254");
                return;
            }
            MtcDelegate.log("lizhiwei 找到了对应的监控数据");
            // 设置视频信息
            currMonitorInfo.setIp(ip);
            currMonitorInfo.setRemotePort(port);
            currMonitorInfo.setCid(cid);
            // 创建视频
            MtcDelegate.log("lizhiwei VbugEventBroadcastReceiver视频展示AddVideoItem");
            AddVideoItem(currMonitorInfo, false);

        }
    }

    // 视频释放接收器
    private class VideoReleaseBroadcastReceiver extends BroadcastReceiver {

        @SuppressWarnings("unused")
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            boolean result = intent.getBooleanExtra(GlobalConstant.KEY_MEETING_RESULT, false);// 是否成功
            if (!result) {
                String error = intent
                        .getStringExtra(GlobalConstant.KEY_MONITOR_ERROR);// 错误号
                String dis = intent
                        .getStringExtra(GlobalConstant.KEY_MONITOR_DIS);// 频错误描述
            }
        }
    }
    private int oldMapLevel;
    // GIS返回接收器
    private class GisBroadcastReceiver extends BroadcastReceiver {
        @SuppressWarnings("unused")
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionIntent = intent.getAction();
            if (actionIntent == GlobalConstant.ACTION_GIS_RECTANGLEPOS) {
//                ---------------------------------------------------------------------------------------------------

                String rectangle_pos = intent.getStringExtra("rectangle_pos");

                int mapLevel = mMapView.getMapLevel();
                //TODO 加载地图Marker条件
                //TODO 1.当前地图级别没有改变 则不进行添加Marker
                //TODO 2.没有操作地图Marker 比如进行Marker清空或筛选
//                if (mapLevel != oldMapLevel || ){
//                    oldMapLevel = mapLevel;
//                    return;
//                }
//                oldMapLevel = mapLevel;
                Log.e("Gis添加图标","收到广播进行加载 判断当前Gis图标是否正在加载中 :" + isLoadMarker);
                if (!isLoadMarker){
                   new MapAddMarkerAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,rectangle_pos);
                }

            } else if (actionIntent == GlobalConstant.ACTION_GIS_ELLIPSEPOS) {
                Log.e("GIS模块","获取椭圆形gis信息");
                if (mBaiduMap != null) {
                    mBaiduMap.clear();
                }// 先清空覆盖物
                // 椭圆形形搜索
                String ellipse_pos = intent.getStringExtra("ellipse_pos");


                Log.e(TAG, "==5118=====  ellipse_pos:" + ellipse_pos);
                employeeLayout.removeAllViews();
                if (ellipse_pos.length() <= 0) {
                    Log.e(TAG, "==5121=====  服务器返回空");

                    TextView text = new TextView(MainActivity.this);
                    text.setText("无任何成员");
                    text.setHeight(employeeLayout.getHeight());
                    text.setWidth(employeeLayout.getWidth());
                    text.setTextColor(Color.WHITE);
                    //employeeLayout.addView(text);
                    employeeLayout.setVisibility(View.GONE);
                    //切换地图按钮状态Yuezs 2015-10-14
                    switchPosButtonStatus(1);
                    return;
                }
                Log.e(TAG, "==5135=====  服务器返回不为空");
                // 8032,116.546,39.7721,20141126181418,6;
                String[] loc = ellipse_pos.split(";");
                Log.e(TAG, "==5138=====  截取服务器返回数据  得到String[]   数组中的第一个元素:" + loc[0]);
                if (posMarkerInfosList == null) {
                    posMarkerInfosList = new ArrayList<PosMarkerInfo>();
                } else {
                    posMarkerInfosList.clear();
                }

                for (int i = 0; i < loc.length; i++) {
                    Log.e(TAG, "==5146=====  对得到的数组进行遍历:");
                    String[] msg = loc[i].split(",");
                    Log.e(TAG, "==5148=====  遍历完成进行再次截取  得到一个String[] :" + msg[0]);
                    if (msg.length != 5) {
                        Log.e(TAG, "==5150=====    得到的String[]长度不是5");
                        continue;
                    }
                    String no = msg[0];
                    Log.e(TAG, "==5155=====    no:" + no);
                    double lat = Double.valueOf(msg[2]);//
                    Log.e(TAG, "==5155=====    lat:" + lat);
                    double lont = Double.valueOf(msg[1]);//
                    Log.e(TAG, "==5155=====    lont:" + lont);
                    int type = Integer.parseInt(msg[4]);
                    Log.e(TAG, "==5155=====    type:" + type);


					/*
					 * String time=msg[3]; String type=msg[4];
					 */

                    PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                    if (ptt_3g_PadApplication.memberStatehashMap.containsKey(no)){
                        posMarkerInfo.setName(ptt_3g_PadApplication.memberStatehashMap.get(no).getName());
                    }else {
                        posMarkerInfo.setName(no);
                    }
                    posMarkerInfo.setLatitude(lat);
                    posMarkerInfo.setLongitude(lont);

                    switch (type) {
                        case 0://调度台
                            posMarkerInfo.setType(0);
                            break;
                        case 1://对讲终端
                        case 3://外线用户
                        case 6://3G对讲 ,这三种类型都作为3G用户处理
                            posMarkerInfo.setType(6);
                            break;
                        case 2://软电话(SIP电话)
                            posMarkerInfo.setType(2);
                            break;
                        case 7://监控设备
                            posMarkerInfo.setType(7);
                            break;
                        default:
                            break;
                    }


                    // 加载到地图中
                    addPosOverlay(posMarkerInfo, false);
                    Log.e("GIS模块","GisBroadcastReceiver 6718");
					/*成员列表*/
                    CheckBox check = new CheckBox(MainActivity.this);
                    //分辨率
                    if (getwindowmanager() == 1280 && getwindowmanager_height() != 752) {
                        Log.e(TAG, "4907=====分辨率判断已执行");
                        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                        if (loc.length == 2) {
                            if (i == 0) {
                                //layoutParam.setMargins(-10, -20, -10, -10);
                            } else if (i == 1) {
                                Log.e(TAG, "4913=====loc.length==2    || i==1");
                                layoutParam.setMargins(0, Integer.valueOf("-50"), Integer.valueOf("-10"), Integer.valueOf("-10"));
                            }
                        } else {
                            Log.e(TAG, "4917=====loc.length != 2 ");
                            layoutParam.setMargins(Integer.valueOf("-10"), Integer.valueOf("-20"), Integer.valueOf("-10"), Integer.valueOf("-10"));
                        }

                        check.setScaleX((float) 0.6);
                        check.setScaleY((float) 0.6);
                        check.setLayoutParams(layoutParam);
                    } else if (getwindowmanager() == 1024) {
                        Log.e(TAG, "4924=====分辨率判断未执行");
                    }

                    //分辨率
                    if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
                        Log.e(TAG, "=============4892");
                        check.setScaleX((float) 0.8);
                        check.setScaleY((float) 0.8);
                        check.setTextSize(11);
                    } else if (getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024) {


                    } else if (getResources().getDisplayMetrics().heightPixels == 1280 || getResources().getDisplayMetrics().widthPixels == 800) {

                    }

                    check.setText(no);
                    check.setTag(posMarkerInfo);
                    check.setTextColor(Color.WHITE);
//                    check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                        @Override
//                        public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
//                            PosMarkerInfo markerinfo = (PosMarkerInfo) arg0.getTag();
//                            if (mBaiduMap != null) {
//                                mBaiduMap.clear();
//                            }
//                            if (ischecked) {
//                                if (!checkBoxMarker.contains(markerinfo)) {
//                                    checkBoxMarker.add(markerinfo);// 从新添加
//                                }
//                            } else {
//                                checkBoxMarker.remove(markerinfo);
//                            }
//
//                            for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                if (!checkBoxMarker.contains(pmi)) {
//                                    addPosOverlay(pmi, false);
//                                } else {
//                                    addPosOverlay(pmi, true);
//                                }
//                            }
//                            // TODO Auto-generated method stub
//							/*PosMarkerInfo markerinfo=(PosMarkerInfo)arg0.getTag();
//								addPosOverlay(markerinfo,ischecked);
//								// 重新添加选中状态-yuezs add 2015-10-14
//								if(ischecked){
//									if(!selPosMarkerInfos.contains(markerinfo)){
//								selPosMarkerInfos.add(markerinfo);// 从新添加
//									}
//								}else{
//									selPosMarkerInfos.remove(markerinfo);
//								}
//
//								if (selPosMarkerInfos.size() == 1) {
//									ll_pos_single.setVisibility(View.VISIBLE);
//									ll_pos.setVisibility(View.GONE);
//								} else if (selPosMarkerInfos.size() > 1) {
//									ll_pos_single.setVisibility(View.GONE);
//									ll_pos.setVisibility(View.VISIBLE);
//								} else {
//									ll_pos_single.setVisibility(View.GONE);
//									ll_pos.setVisibility(View.GONE);
//								}
//								if (multiplePopupWindow != null) {
//									multiplePopupWindow.dismiss();
//								}*/
//                            /************************************/
//                        }
//                    });
                    //Sws 12-24add 解决全选时app无响应问题
                    check.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                                isCheck = true;
                            }
                            return false;
                        }
                    });
                    check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {

                            if (isLoadMarker){
                                ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                                return ;
                            }
                            // TODO Auto-generated method stub
                            Log.e(TAG, "arg0：" + arg0);
                            Log.e(TAG, "arg0：" + ischecked);
                            Log.e("点击CheckBox", "onCheckedChanged  6789");
                            if (isCheck) {


                                //得到当前点击的CheckBox对应的PosMarkerInfo
									/*
									PosMarkerInfo中的属性
										private String name;		号码
										private double latitude;	经度
										private double longitude;	纬度
										private String time;		时间
										private int type;			类型
									 */
                                PosMarkerInfo markerinfo = (PosMarkerInfo) arg0.getTag();
                                //判断当前地图上是否有marker  如果有全部清空
                                if (mBaiduMap != null) {
                                    mBaiduMap.clear();
                                }
                                //判断当前CheckBox选中状态
                                if (ischecked) {
                                    //如果是选中状态  判断是否包含对应的Marker，如果不包含name那么添加
                                    if (!checkBoxMarker.contains(markerinfo)) {
                                        checkBoxMarker.add(markerinfo);// 从新添加
                                        Log.e(TAG, "添加" + checkBoxMarker.size());
                                    }
                                } else {
                                    //如果不是选中状态  判断是否包含对应的Marker，如果包含那么删除
                                    if (checkBoxMarker.contains(markerinfo)) {
                                        checkBoxMarker.remove(markerinfo);// 从新添加
                                        Log.e(TAG, "删除" + checkBoxMarker.size());
                                    }
                                }
                                //根据选中状态 进行添加对应的marker图片
                                if (ischecked) {
                                    addPosOverlay(markerinfo, true);
                                    Log.e("GIS模块","GisBroadcastReceiver 6852");
                                    Log.e(TAG, "设置marker状态为选中");
                                } else {
                                    addPosOverlay(markerinfo, false);
                                    Log.e("GIS模块","GisBroadcastReceiver 6856");
                                    Log.e(TAG, "设置marker状态为未选中");
                                }


                                if (mBaiduMap != null) {
                                    mBaiduMap.clear();
                                }

                                //遍历集合 如果集合中包含当前marker那么将状态设置为选中状态  否则为未选中状态
                                for (PosMarkerInfo pmi : checkBoxMarker) {
                                    if (!checkBoxMarker.contains(pmi)) {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 6868");
                                    } else {
                                        addPosOverlay(pmi, true);
                                        Log.e("GIS模块","GisBroadcastReceiver 6872");
                                    }
                                }

                                //判断当前选中的集合长度   是选中了一个 还是多个  还是没有选中 根除对应的布局
                                if (checkBoxMarker.size() == 1) {
                                    ll_pos_single.setVisibility(View.VISIBLE);
                                    ll_pos.setVisibility(View.GONE);
                                } else if (checkBoxMarker.size() > 1) {
                                    ll_pos_single.setVisibility(View.GONE);
                                    ll_pos.setVisibility(View.VISIBLE);
                                } else {
                                    ll_pos_single.setVisibility(View.GONE);
                                    ll_pos.setVisibility(View.GONE);
                                }

                                //关闭全选popupwindow
                                if (multiplePopupWindow != null) {
                                    multiplePopupWindow.dismiss();
                                }
                                isCheck = false;
                            }
                        }
                    });

//                    check.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            isCheck = true;
//                        }
//                    });
                    employeeLayout.addView(check);
                    //切换地图按钮状态Yuezs 2015-10-14
                    switchPosButtonStatus(2);
                    posSelType = 2;

					/*成员列表*/
                    //posMarkerInfosList.add(posMarkerInfo);

                    List<PosMarkerInfo> posLists = new ArrayList<PosMarkerInfo>();
                    if (posMarkerInfosList != null) {
                        //posMarkerInfosList.add(posMarkerInfo);
                        for (PosMarkerInfo pos : posMarkerInfosList) {
                            if (pos.getName().equals(no)) {
                                posLists.add(pos);
                            }
                        }
                        if (posLists != null && posLists.size() > 0) {
                            posMarkerInfosList.removeAll(posLists);
                        }
                        posMarkerInfosList.add(posMarkerInfo);
                    }

                }
                // 清空选中列表
                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                    checkBoxMarker.clear();
                }
                if (mBaiduMap != null) {
                    mBaiduMap.setOnMarkerClickListener(null);
                    mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker arg0) {

                            Log.e("Gis添加图标","onMarkerClick");
                            if (ptt_3g_PadApplication.isNetConnection() == false) {
                                ToastUtils.showToast(MainActivity.this,
                                        getString(R.string.info_network_unavailable));
                                return false;
                            }
                            if (isLoadMarker){
                                ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                                return false;
                            }
                            PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
                                    .getExtraInfo().get("pmi");
                            if (posSelType == 1) {
                                mBaiduMap.clear();// 清空地图
                                if (!checkBoxMarker.contains(posMarkerInfo)) {
                                    checkBoxMarker.clear();// 清空，只能选择一个
                                }

                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 6950");
                                            checkBoxMarker.add(pmi);// 添加到选择列表
                                        } else {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 6955");
                                            checkBoxMarker.remove(pmi);// 添加到选择列表
                                        }
                                    } else {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 6960");
                                    }
                                }
                            } else if (posSelType == 2) {
//                                mBaiduMap.clear();
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }
                                Log.e("PTT_3G_Pad_PTTService", "2");
                                if (hasMark.containsKey(posMarkerInfo.getName())) {
                                    Marker marker = hasMark.get(posMarkerInfo.getName());
                                    marker.remove();
                                    if (!checkBoxMarker.contains(posMarkerInfo)) {
                                        checkBoxMarker.add(posMarkerInfo);
                                        addPosOverlay(posMarkerInfo, true);
                                        Log.e("GIS模块", "GisBroadcastReceiver 6990");
                                    } else {
                                        checkBoxMarker.remove(posMarkerInfo);
                                        addPosOverlay(posMarkerInfo, false);
                                        Log.e("GIS模块", "GisBroadcastReceiver 6992");
                                    }
                                }
                            } else if (posSelType == 3) {
                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 6998");
                                        checkBoxMarker.remove(pmi);
                                    } else {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 7003");
                                        } else {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 7006");
                                        }
                                    }
                                }
                            }

                            if (checkBoxMarker.size() == 1) {
                                ll_pos_single.setVisibility(View.VISIBLE);
                                ll_pos.setVisibility(View.GONE);
                            } else if (checkBoxMarker.size() > 1) {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.VISIBLE);
                            } else {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                }
				/*如果列表为隐藏就显示它*/
                if (employeeLayout.getVisibility() != View.VISIBLE) {
                    employeeLayout.setVisibility(View.VISIBLE);
                }
                // 根据坐标显示在地图上
            } else if (actionIntent == GlobalConstant.ACTION_GIS_TIMEPOS) {
                Log.e("GIS模块","获取历史位置信息");
                if (mBaiduMap != null) {
                    mBaiduMap.clear();// 先清空覆盖物
                }


                // 历史位置搜索
                // 1001,39.656,112.265,20140530182025;
                String time_pos = intent.getStringExtra("time_pos");
                employeeLayout.removeAllViews();
                if (time_pos.length() <= 0) {


                    return;
                }
                hasMark.clear();//清空轨迹对象 yuezs add
                ll_trackplay.setVisibility(View.VISIBLE);// 有历史GIS位置信息，显示轨迹播放控件


                // 2023,116.446674,39.929732,$GPRMC,042625.025,A,3992.9732,N,11644.6674,E,0,0,280814,0,0,0,0,2023,2023,10,2;
                String[] loc = time_pos.split(";");

                if (posMarkerInfosTraceList == null) {
                    posMarkerInfosTraceList = new ArrayList<PosMarkerInfo>();
                } else {
                    posMarkerInfosTraceList.clear();
                }

                for (int i = 0; i < loc.length; i++) {
                    String[] msg = loc[i].split(",");
                    if (msg.length != 21)
                        continue;
                    String no = msg[0];
                    double lat = Double.valueOf(msg[2]);// 39.772145
                    double lont = Double.valueOf(msg[1]);// 116.546191
                    String time = msg[4];
                    String date = msg[12];
                    String utcTime = FormatController.getUTCTimeFromStr(time,
                            date);

                    //yuezs add 2015-11-10
                    if (i == 0) {
                        String[] dates = utcTime.split(" ");
                        String[] times = dates[1].split(":");
                        txtPosCurrHour.setText(times[0]);
                        txtPosCurrMinute.setText(times[1]);
                        txtPosCurrSecond.setText(times[2]);
                    }
                    PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                    if (ptt_3g_PadApplication.memberStatehashMap.containsKey(no)){
                        posMarkerInfo.setName(ptt_3g_PadApplication.memberStatehashMap.get(no).getName());
                    }else {
                        posMarkerInfo.setName(no);
                    }
                    posMarkerInfo.setLatitude(lat);
                    posMarkerInfo.setLongitude(lont);
                    posMarkerInfo.setTime(utcTime);
                    posMarkerInfosTraceList.add(posMarkerInfo);
                    Log.e(TAG, no + ",经度：" + lont);
                }

                // 过滤数据，只显示每组第一条数据
                posMarkerInfosList = CommonMethod
                        .removeDuplicates(posMarkerInfosTraceList);
                for (PosMarkerInfo pmi : posMarkerInfosList) {
                    // 加载到地图中
                    addPosOverlay(pmi, false);
                    Log.e("GIS模块","GisBroadcastReceiver 7094");
                }
                // 清空选中列表
                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                    checkBoxMarker.clear();
                }
                if (mBaiduMap != null) {
                    mBaiduMap.setOnMarkerClickListener(null);
                    mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker arg0) {

                            Log.e("Gis添加图标","onMarkerClick");
                            if (ptt_3g_PadApplication.isNetConnection() == false) {
                                ToastUtils.showToast(MainActivity.this,
                                        getString(R.string.info_network_unavailable));
                                return false;
                            }
                            if (isLoadMarker){
                                ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                                return false;
                            }
                            PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
                                    .getExtraInfo().get("pmi");
                            if (posSelType == 1) {
                                if (mBaiduMap != null) {
                                    mBaiduMap.clear();// 先清空覆盖物
                                }// 清空地图
                                if (!checkBoxMarker.contains(posMarkerInfo)) {
                                    checkBoxMarker.clear();// 清空，只能选择一个
                                }

                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 7123");
                                            checkBoxMarker.add(pmi);// 添加到选择列表
                                        } else {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 7127");
                                            checkBoxMarker.remove(pmi);// 添加到选择列表
                                        }
                                    } else {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 7132");
                                    }
                                }
                            } else if (posSelType == 2) {
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();// 先清空覆盖物
//                                }
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }
                                Log.e("PTT_3G_Pad_PTTService", "2");
                                if (hasMark.containsKey(posMarkerInfo.getName())) {
                                    Marker marker = hasMark.get(posMarkerInfo.getName());
                                    marker.remove();
                                    if (!checkBoxMarker.contains(posMarkerInfo)) {
                                        checkBoxMarker.add(posMarkerInfo);
                                        addPosOverlay(posMarkerInfo, true);
                                        Log.e("GIS模块", "GisBroadcastReceiver 7162");
                                    } else {
                                        checkBoxMarker.remove(posMarkerInfo);
                                        Log.e("GIS模块", "GisBroadcastReceiver 7165");
                                        addPosOverlay(posMarkerInfo, false);
                                    }
                                }
                            } else if (posSelType == 3) {
                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 7172");
                                        checkBoxMarker.remove(pmi);
                                    } else {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 7177");
                                        } else {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 7180");
                                        }
                                    }
                                }
                            }

                            if (checkBoxMarker.size() == 1) {
                                ll_pos_single.setVisibility(View.VISIBLE);
                                ll_pos.setVisibility(View.GONE);
                            } else if (checkBoxMarker.size() > 1) {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.VISIBLE);
                            } else {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                }
            } else if (actionIntent == GlobalConstant.ACTION_GIS_DSTCURPOS) {
                Log.e("GIS模块","获取当前GIS地址");
                //TODO 点击定位模块成员时服务器返回GIS信息 执行
                int type = -1;

                String cur_pos = intent.getStringExtra("cur_pos");
                if (cur_pos.length() <= 0) {

                    return;
                }

                Log.e(TAG, "Main界面收到广播   cur_pos返回:" + cur_pos);
                // 2033,2033,116.617448,39.928743,$GPRMC,102954.054,A,3992.8743,N,11661.7448,E,0,0,201114,0,0,0,0,2033,2033,10,6
                String[] loc = cur_pos.split(";");
                if (posMarkerInfosList == null) {
                    posMarkerInfosList = new ArrayList<PosMarkerInfo>();
                }
                Log.e(TAG, "loc.length:" + loc.length);
                for (int i = 0; i < loc.length; i++) {
                    String[] msg = loc[i].split(",");
                    Log.e(TAG, "循环中  msg.lenght：" + msg.length);
                    // Sws 2018/01/02 add 这样判断的 手动在网管上配置的经纬度 不会执行定位
                    // 服务器返回的数组长度为 22   手动配置的经纬度返回来数组长度应为 5
                    //	if (msg.length != 22)
                    //	if (msg.length < 5 || msg.length >22)
                    //	continue;
                    //等于 22 Ice获取的经纬度
                    if (msg.length == 22) {            //长度为22  Ice返回
                        int typePosition = msg.length - 1;
                        type = Integer.parseInt(msg[typePosition]);

                    } else if (msg.length == 4) {            //长度为4  手动配置的经纬度   缺少type
                        Log.e(TAG, "msg长度为4 进入判断");
                        String ellipsePos = prefs.getString("ellipsePos", null);
                        Log.e(TAG, "得到字符串长度  ellipsePos:" + ellipsePos);
                        if (ellipsePos == null) {
                            Log.e(TAG, "ellipsePos == null continue:");
                            continue;
                        }
                        String[] locs = ellipsePos.split(";");
                        Log.e(TAG, "locs.length:" + locs.length);
                        for (int y = 0; y < locs.length; y++) {
                            String[] msgs = locs[y].split(",");
                            if (msgs.length != 5) {
                                Log.e(TAG, "msgs.length != 5:" + msgs.length);
                                Log.e(TAG, "==5150=====    得到的String[]长度不是5");
                                continue;
                            }
                            Log.e(TAG, "msg[0]:" + msg[0]);
                            Log.e(TAG, "msgs[0]:" + msgs[0]);
                            if (msg[0].equals(msgs[0])) {
                                int typePosition = msgs.length - 1;
                                type = Integer.parseInt(msgs[typePosition]);
                                break;
                            }
                        }
                    } else {
                        continue;
                    }

                    String no = msg[0];
                    double lat = Double.valueOf(msg[3]);// 39.772145
                    double lont = Double.valueOf(msg[2]);// 116.546191
                    //	String contentString = msg[4];
                    //	String[] msgContent = contentString.split(",");
                    //	int type = Integer.parseInt(msg[21]);
                    //	int typePosition = msg.length - 1;
                    //int type = Integer.parseInt(msg[typePosition]);
                    Log.e(TAG, "循环中  no：" + no + "lat:" + lat + "lont:" + lont + "contentString:" + "type:" + type);
                    PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                    if (ptt_3g_PadApplication.memberStatehashMap.containsKey(no)){
                        posMarkerInfo.setName(ptt_3g_PadApplication.memberStatehashMap.get(no).getName());
                    }else {
                        posMarkerInfo.setName(no);
                    }
                    posMarkerInfo.setLatitude(lat);
                    posMarkerInfo.setLongitude(lont);


                    if (type == -1) {
                        return;
                    }

                    switch (type) {
                        case 0://调度台
                            posMarkerInfo.setType(0);
                            break;
                        case 1://对讲终端
                        case 3://外线用户
                        case 6://3G对讲 ,这三种类型都作为3G用户处理
                            posMarkerInfo.setType(6);
                            break;
                        case 2://软电话(SIP电话)
                            posMarkerInfo.setType(2);
                            break;
                        case 7://监控设备
                            posMarkerInfo.setType(7);
                            break;
                        default:
                            break;
                    }

                    // 加载到地图中
                    addPosOverlay(posMarkerInfo, false);
                    Log.e("GIS模块","GisBroadcastReceiver 7298");
                    //posMarkerInfosList.add(posMarkerInfo);
                    List<PosMarkerInfo> posLists = new ArrayList<PosMarkerInfo>();
                    if (posMarkerInfosList != null) {
                        //posMarkerInfosList.add(posMarkerInfo);
                        for (PosMarkerInfo pos : posMarkerInfosList) {
                            if (pos.getName().equals(no)) {
                                posLists.add(pos);
                            }
                        }
                        if (posLists != null && posLists.size() > 0) {
                            posMarkerInfosList.removeAll(posLists);
                        }
                        posMarkerInfosList.add(posMarkerInfo);
                    }
                    Log.e(TAG, "点击了定位  下面是刷新地图");
                    // 显示范围
                    LatLng ll = new LatLng(lat, lont);
                    MapStatus mapStatus = new MapStatus.Builder().target(ll)
                            .zoom(18).build();
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newMapStatus(mapStatus);
                    if (mBaiduMap != null) {
                        mBaiduMap.setMapStatus(u);
                    }
                }
                // 清空选中列表
//                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
//                    checkBoxMarker.clear();
//                }
//                if (mBaiduMap != null) {
//                    mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(final Marker arg0) {
//                            if (ptt_3g_PadApplication.isNetConnection() == false) {
//                                ToastUtils.showToast(MainActivity.this,
//                                        getString(R.string.info_network_unavailable));
//                                return false;
//                            }
//                            PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
//                                    .getExtraInfo().get("pmi");
//                            if (posSelType == 1) {
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();// 先清空覆盖物
//                                }// 清空地图
//                                if (!checkBoxMarker.contains(posMarkerInfo)) {
//                                    checkBoxMarker.clear();// 清空，只能选择一个
//                                }
//
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, true);
//                                            Log.e("GIS模块","GisBroadcastReceiver 7350");
//                                            checkBoxMarker.add(pmi);// 添加到选择列表
//                                        } else {
//                                            addPosOverlay(pmi, false);
//                                            Log.e("GIS模块","GisBroadcastReceiver 7355");
//                                            checkBoxMarker.remove(pmi);// 添加到选择列表
//                                        }
//                                    } else {
//                                        addPosOverlay(pmi, false);
//                                        Log.e("GIS模块","GisBroadcastReceiver 7360");
//                                    }
//                                }
//                            } else if (posSelType == 2) {
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();// 先清空覆盖物
//                                }
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }
////                                Log.e("PTT_3G_Pad_PTTService", "2");
////                                Marker marker = hasMark.get(posMarkerInfo.getName());
////                                marker.remove();
////                                if (!checkBoxMarker.contains(posMarkerInfo)) {
////                                    checkBoxMarker.add(posMarkerInfo);
////                                    addPosOverlay(posMarkerInfo, true);
////                                    Log.e("GIS模块","GisBroadcastReceiver 7390");
////                                } else {
////                                    checkBoxMarker.remove(posMarkerInfo);
////                                    addPosOverlay(posMarkerInfo, false);
////                                    Log.e("GIS模块","GisBroadcastReceiver 7394");
////                                }
//                            } else if (posSelType == 3) {
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        addPosOverlay(pmi, false);
//                                        Log.e("GIS模块","GisBroadcastReceiver 7400");
//                                        checkBoxMarker.remove(pmi);
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                            Log.e("GIS模块","GisBroadcastReceiver 7405");
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                            Log.e("GIS模块","GisBroadcastReceiver 7408");
//                                        }
//                                    }
//                                }
//                            }
//
//                            if (checkBoxMarker.size() == 1) {
//                                ll_pos_single.setVisibility(View.VISIBLE);
//                                ll_pos.setVisibility(View.GONE);
//                            } else if (checkBoxMarker.size() > 1) {
//                                ll_pos_single.setVisibility(View.GONE);
//                                ll_pos.setVisibility(View.VISIBLE);
//                            } else {
//                                ll_pos_single.setVisibility(View.GONE);
//                                ll_pos.setVisibility(View.GONE);
//                            }
//                            return false;
//                        }
//                    });
//                }
            } else if (actionIntent == GlobalConstant.ACTION_GIS_TRACEPOS) {
                Log.e("GIS模块","追踪好友位置信息");
                // 追踪
                String sid = intent.getStringExtra("sid");
                String error = intent.getStringExtra("error");
                String dis = intent.getStringExtra("dis");
                if (error.length() > 0 && dis.length() > 0) {
                    ToastUtils.showToast(MainActivity.this,
                            "追踪GIS位置信息失败，错误码error:" + error + "-描述：" + dis);
                    Log.e(TAG, "追踪GIS位置信息失败，错误码error:" + error + "-描述：" + dis);
                    return;
                } else {
                    Log.e(TAG, "isTrace:" + isTrace);
                    if (isTrace) {
                        Log.e(TAG, "isTrace为true进入判断");
                        isTrace = false;
                        btn_trace
                                .setText(getString(R.string.title_postion_trace));
                        if (posHashMap != null) {
                            posHashMap.clear();
                        }
                        ToastUtils.showToast(MainActivity.this, "取消追踪GIS位置信息成功");
                        Log.e(TAG, "取消追踪GIS位置信息成功");
                    } else {
                        isTrace = true;// 追踪成功
                        btn_trace
                                .setText(getString(R.string.title_postion_untrace));
                        ToastUtils.showToast(MainActivity.this, "追踪GIS位置信息成功");
                        Log.e(TAG, "追踪GIS位置信息成功:" + sid);
                    }

                }
            } else if (actionIntent == GlobalConstant.ACTION_GIS_IND_D_GIS) {
                Log.e("GIS模块","追踪调度台推送的好友位置信息");
                String employeeid = intent.getStringExtra("employeeid");

                if (isTrace) {
                    //当前isTrace为true时表示当前已经处于追踪状态，在这里可以不做处理
                } else {
                    //当前isTrace为false时表示当前并未处于追踪状态，但是追踪的回调接口还一直发送，说明服务器之前没有取消追踪
                    btn_trace.setText(getString(R.string.title_postion_untrace));
                    isTrace = true;
                    if (addUserNoList == null) {
                        addUserNoList = new ArrayList<String>();
                    }
                    if (addUserNoList != null) {
                        if (!addUserNoList.contains(employeeid)) {
                            addUserNoList.add(employeeid);
                        }
                    }
                }

                // 追踪返回
                if (mBaiduMap != null) {
                    mBaiduMap.clear();// 先清空覆盖物
                }
                count++;
                //String employeeid = intent.getStringExtra("employeeid");
				/*double X = 0;
				if(Integer.parseInt(employeeid) == 2201)
				{
					X = 0;
				}
				else {
					X = 0.00003*3.14;
				}*/
                double latitude = Double.valueOf(intent
                        .getStringExtra("latitude"));
                double lontitude = Double.valueOf(intent
                        .getStringExtra("lontitude"));

                String time = intent.getStringExtra("time");
                String gprmc = intent.getStringExtra("gprmc");
                String[] gprmcSplit = gprmc.split(",");
                int type = -1;
                if (gprmcSplit != null && gprmcSplit.length == 18) {
                    type = Integer.parseInt(gprmcSplit[17]);
                }

                Log.e(TAG, "追踪返回数据：employeeid:" + employeeid
                        + " latitude:" + latitude + " lontitude:" + lontitude
                        + " time:" + time + " gprmc:" + gprmc);
                Log.e(TAG, "追踪成功，调度机返回数据：employeeid:" + employeeid
                        + " latitude:" + latitude + " lontitude:" + lontitude
                        + " time:" + time + " gprmc:" + gprmc + "gprmLength:" + gprmcSplit.length + " type:" + type);

                if (posMarkerInfosList == null) {
                    posMarkerInfosList = new ArrayList<PosMarkerInfo>();
                }
                PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                posMarkerInfo.setName(employeeid);
                posMarkerInfo.setLatitude(latitude);
                posMarkerInfo.setLongitude(lontitude);

                switch (type) {
                    case 0://调度台
                        posMarkerInfo.setType(0);
                        break;
                    case 1://对讲终端
                    case 3://外线用户
                    case 6://3G对讲 ,这三种类型都作为3G用户处理
                        posMarkerInfo.setType(6);
                        break;
                    case 2://软电话(SIP电话)
                        posMarkerInfo.setType(2);
                        break;
                    case 7://监控设备
                        posMarkerInfo.setType(7);
                        break;
                    default:
                        break;
                }

                // 加载到地图中
                addPosOverlay(posMarkerInfo, false);
                Log.e("GIS模块","GisBroadcastReceiver 7542");
                //posMarkerInfosList.add(posMarkerInfo);

                List<PosMarkerInfo> posList = new ArrayList<PosMarkerInfo>();
                if (posMarkerInfosList != null) {
                    //posMarkerInfosList.add(posMarkerInfo);
                    for (PosMarkerInfo pos : posMarkerInfosList) {
                        if (pos.getName().equals(employeeid)) {
                            posList.add(pos);
                        }
                    }
                    if (posList != null && posList.size() > 0) {
                        posMarkerInfosList.removeAll(posList);
                    }
                    posMarkerInfosList.add(posMarkerInfo);
                }

                //设置key-value的方式保存，key保存用户id，value保存marker集合
                if (posHashMap == null) {
                    posHashMap = new HashMap<String, List<PosMarkerInfo>>();
                }

                if (posHashMap.containsKey(employeeid)) {
                    //集合中已经存在用户id，直接在value中实现追加
                    List<PosMarkerInfo> arrayPosMarker = posHashMap.get(employeeid);
                    arrayPosMarker.add(posMarkerInfo);
                } else {
                    List<PosMarkerInfo> arrayPosMarkerInfo = new ArrayList<PosMarkerInfo>();
                    arrayPosMarkerInfo.add(posMarkerInfo);
                    posHashMap.put(employeeid, arrayPosMarkerInfo);
                }
                LatLng latLng = null;
				/*	if(posHashMap.size() > 0)//有成员被追踪
				{
					if(posHashMap.containsKey(employeeid))
					{
						List<PosMarkerInfo> arrayPosMarkerTrace = posHashMap.get(employeeid);
						if(arrayPosMarkerTrace.size() > 1)//集合中至少有两个点再画线
						{
							List<LatLng> points = new ArrayList<LatLng>();
							for (PosMarkerInfo pInfo : arrayPosMarkerTrace)
							{
								latLng = new LatLng(pInfo.getLatitude(),
										pInfo.getLongitude());
								points.add(latLng);
							}
							OverlayOptions ooPolyline = new PolylineOptions().width(6)
									.color(0xAAFF0000).points(points);
							mBaiduMap.addOverlay(ooPolyline);
						}
					}
				}*/

                LatLngBounds.Builder posPlayBuilder = new LatLngBounds.Builder();
                int k = -1;
                Iterator iterator = posHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    List<PosMarkerInfo> arrayPosMarkerTrace = posHashMap.get(iterator.next());
                    if (arrayPosMarkerTrace.size() > 1)//集合中至少有一个点再画线
                    {
                        List<LatLng> points = new ArrayList<LatLng>();
                        for (PosMarkerInfo pInfo : arrayPosMarkerTrace) {
                            latLng = new LatLng(pInfo.getLatitude(),
                                    pInfo.getLongitude());
                            points.add(latLng);
                        }
                        k++;

                        PolylineOptions ooPolyline = new PolylineOptions().width(6)
                                .color(Color.RED).points(points);    //0xAAFF0000
                        if (mBaiduMap != null) {
                            mBaiduMap.addOverlay(ooPolyline);
                        }
                        PosMarkerInfo posMar = new PosMarkerInfo();
                        posMar.setName(arrayPosMarkerTrace.get(arrayPosMarkerTrace.size() - 1).getName());
                        posMar.setLatitude(arrayPosMarkerTrace.get(arrayPosMarkerTrace.size() - 1).getLatitude());
                        posMar.setLongitude(arrayPosMarkerTrace.get(arrayPosMarkerTrace.size() - 1).getLongitude());
                        posMar.setType(arrayPosMarkerTrace.get(arrayPosMarkerTrace.size() - 1).getType());
                        // 加载到地图中
                        addPosOverlay(posMar, false);
                        Log.e("GIS模块","GisBroadcastReceiver 7622");
                        //posMarkerInfosList.add(posMarkerInfo);

                        List<PosMarkerInfo> posLists = new ArrayList<PosMarkerInfo>();
                        if (posMarkerInfosList != null) {
                            //posMarkerInfosList.add(posMarkerInfo);
                            for (PosMarkerInfo pos : posMarkerInfosList) {
                                if (pos.getName().equals(employeeid)) {
                                    posLists.add(pos);
                                }
                            }
                            if (posLists != null && posLists.size() > 0) {
                                posMarkerInfosList.removeAll(posLists);
                            }
                            posMarkerInfosList.add(posMarkerInfo);
                        }

                        PosMarkerInfo ceShiOne = arrayPosMarkerTrace.get(arrayPosMarkerTrace.size() - 1);
                        LatLng twoLatLngs = new LatLng(ceShiOne.getLatitude(),
                                ceShiOne.getLongitude());
                        posPlayBuilder.include(twoLatLngs);

                    }
                    if (arrayPosMarkerTrace.size() == 1) {
                        PosMarkerInfo posMar = new PosMarkerInfo();
                        posMar.setName(arrayPosMarkerTrace.get(0).getName());
                        posMar.setLatitude(arrayPosMarkerTrace.get(0).getLatitude());
                        posMar.setLongitude(arrayPosMarkerTrace.get(0).getLongitude());
                        posMar.setType(arrayPosMarkerTrace.get(0).getType());
                        // 加载到地图中
                        addPosOverlay(posMar, false);
                        Log.e("GIS模块","GisBroadcastReceiver 7653");
                        //posMarkerInfosList.add(posMarkerInfo);

                        List<PosMarkerInfo> posLists = new ArrayList<PosMarkerInfo>();
                        if (posMarkerInfosList != null) {
                            //posMarkerInfosList.add(posMarkerInfo);
                            for (PosMarkerInfo pos : posMarkerInfosList) {
                                if (pos.getName().equals(employeeid)) {
                                    posLists.add(pos);
                                }
                            }
                            if (posLists != null && posLists.size() > 0) {
                                posMarkerInfosList.removeAll(posLists);
                            }
                            posMarkerInfosList.add(posMarkerInfo);
                        }

                        PosMarkerInfo ceShiOne = arrayPosMarkerTrace.get(0);
                        LatLng twoLatLngs = new LatLng(ceShiOne.getLatitude(),
                                ceShiOne.getLongitude());
                        posPlayBuilder.include(twoLatLngs);
                    }

                }

                LatLngBounds posPlayLatLngBounds = posPlayBuilder.build();
                MapStatusUpdate posPlayMapStatusUpdate = MapStatusUpdateFactory
                        .newLatLngBounds(posPlayLatLngBounds);
                if (mBaiduMap != null) {
                    mBaiduMap.setMapStatus(posPlayMapStatusUpdate);
                }


                //原来的画线和地图缩放
                // 画线(只有大于1个点才画线)
				/*if (posMarkerInfosList.size() > 1) {
					// 最后一个点
					PosMarkerInfo lastTwo = posMarkerInfosList
							.get(posMarkerInfosList.size() - 2);
					LatLng oneLatLng = new LatLng(lastTwo.getLatitude(),
							lastTwo.getLongitude());
					// 当前位置
					PosMarkerInfo lastOne = posMarkerInfosList
							.get(posMarkerInfosList.size() - 1);
					LatLng twoLatLng = new LatLng(lastOne.getLatitude(),
							lastOne.getLongitude());
					List<LatLng> points = new ArrayList<LatLng>();
					points.add(oneLatLng);
					points.add(twoLatLng);
					OverlayOptions ooPolyline = new PolylineOptions().width(10)
							.color(0xAAFF0000).points(points);
					mBaiduMap.addOverlay(ooPolyline);

					// 追踪当前位置不在显示区域，进行地图缩放
					LatLngBounds.Builder posPlayBuilder = new LatLngBounds.Builder();
					posPlayBuilder.include(oneLatLng);
					posPlayBuilder.include(twoLatLng);
					LatLngBounds posPlayLatLngBounds = posPlayBuilder.build();
					MapStatusUpdate posPlayMapStatusUpdate = MapStatusUpdateFactory
							.newLatLngBounds(posPlayLatLngBounds);
					mBaiduMap.setMapStatus(posPlayMapStatusUpdate);
				}
				 */
                // 清空选中列表
                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                    checkBoxMarker.clear();
                }
                if (mBaiduMap != null) {
                    mBaiduMap.setOnMarkerClickListener(null);
                    mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker arg0) {

                            Log.e("添加图标","onMarkerClick");
                            if (ptt_3g_PadApplication.isNetConnection() == false) {
                                ToastUtils.showToast(MainActivity.this,
                                        getString(R.string.info_network_unavailable));
                                return false;
                            }

                            if (isLoadMarker){
                                ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                                return false;
                            }
                            PosMarkerInfo posMarkerInfo = (PosMarkerInfo) arg0
                                    .getExtraInfo().get("pmi");
                            if (posSelType == 1) {
                                if (mBaiduMap != null) {
                                    mBaiduMap.clear();// 先清空覆盖物
                                }// 清空地图
                                if (!checkBoxMarker.contains(posMarkerInfo)) {
                                    checkBoxMarker.clear();// 清空，只能选择一个
                                }

                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 7743");
                                            checkBoxMarker.add(pmi);// 添加到选择列表
                                        } else {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 7747");
                                            checkBoxMarker.remove(pmi);// 添加到选择列表
                                        }
                                    } else {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 7752");
                                    }
                                }
                            } else if (posSelType == 2) {
//                                if (mBaiduMap != null) {
//                                    mBaiduMap.clear();// 先清空覆盖物
//                                }
//                                for (PosMarkerInfo pmi : posMarkerInfosList) {
//                                    if (pmi.getName() == posMarkerInfo.getName()) {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            checkBoxMarker.add(pmi);
//                                            addPosOverlay(pmi, true);
//                                        } else {
//                                            checkBoxMarker.remove(pmi);
//                                            addPosOverlay(pmi, false);
//                                        }
//                                    } else {
//                                        if (!checkBoxMarker.contains(pmi)) {
//                                            addPosOverlay(pmi, false);
//                                        } else {
//                                            addPosOverlay(pmi, true);
//                                        }
//                                    }
//                                }
                                if (hasMark.containsKey(posMarkerInfo.getName())){
                                    Marker marker = hasMark.get(posMarkerInfo.getName());
                                    marker.remove();
                                    if (!checkBoxMarker.contains(posMarkerInfo)) {
                                        checkBoxMarker.add(posMarkerInfo);
                                        addPosOverlay(posMarkerInfo, true);
                                        Log.e("GIS模块","GisBroadcastReceiver 7781");
                                    } else {
                                        checkBoxMarker.remove(posMarkerInfo);
                                        addPosOverlay(posMarkerInfo, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 7785");
                                    }
                                }

                            } else if (posSelType == 3) {
                                for (PosMarkerInfo pmi : posMarkerInfosList) {
                                    if (pmi.getName() == posMarkerInfo.getName()) {
                                        addPosOverlay(pmi, false);
                                        Log.e("GIS模块","GisBroadcastReceiver 7791");
                                        checkBoxMarker.remove(pmi);
                                    } else {
                                        if (!checkBoxMarker.contains(pmi)) {
                                            addPosOverlay(pmi, false);
                                            Log.e("GIS模块","GisBroadcastReceiver 7796");
                                        } else {
                                            addPosOverlay(pmi, true);
                                            Log.e("GIS模块","GisBroadcastReceiver 7799");
                                        }
                                    }
                                }
                            }

                            if (checkBoxMarker.size() == 1) {
                                ll_pos_single.setVisibility(View.VISIBLE);
                                ll_pos.setVisibility(View.GONE);
                            } else if (checkBoxMarker.size() > 1) {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.VISIBLE);
                            } else {
                                ll_pos_single.setVisibility(View.GONE);
                                ll_pos.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });
                }
            }

        }

    }

    //没有用到screenUser这个方法
    private void screenUser(String no, int type) {

        if (type != 6) {

        }
        List<MemberInfo> allMemberInfoData = GroupManager.getInstance().getAllMemberInfoData();
        Log.e("=====测试====6147", "allMemberInfoData长度：" + allMemberInfoData.size());
        if (allMemberInfoData != null) {
            for (int i = 0; i < allMemberInfoData.size(); i++) {
                Log.e("=====测试====6147", "name:" + allMemberInfoData.get(i).getName());
                if (allMemberInfoData.get(i).getName().equals(no)) {
                    Log.e("=====测试====6147", "result:" + allMemberInfoData.get(i).getName().equals(no));

                }
            }
        }
    }

    /**
     * 地图添加marker
     *
     * @param
     * @param
     */

    private void addPosOverlay(final PosMarkerInfo pmi,final boolean isSel) {


                Log.e("Gis添加图标", "addPosOverlay getName :" + pmi.getName() + "\r\n" + "isSel :" + isSel);
                Log.e(TAG, "==6140=====  进行添加View");
                Log.e(TAG, "trackHandler中  main === 6147    addPosOverlay中");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("pmi",pmi);
//                bundle.putBoolean("isSel",isSel);
//                new MarkerAddAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);

        //TODO 添加Marker的地方  放入了一个Runnable执行。每当地图发生移动的时候都会通过这个地方来添加Marker
        final BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromBitmap(getPosHeadBitmap(pmi, isSel));// 把布局转换成Bigmap
        Runnable runnable =  new Runnable() {
             @Override
                public void run() {
                if (mBaiduMap != null) {

                    LatLng latLng = new LatLng(pmi.getLatitude(), pmi.getLongitude());
                    OverlayOptions overlayOptions = new MarkerOptions().position(latLng)
                            .icon(bitmapDescriptor);
                    Marker marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
                    Bundle markerBundle = new Bundle();
                    markerBundle.putSerializable("pmi", pmi);
                    marker.setExtraInfo(markerBundle);
                    if (hasMark != null) {
                        hasMark.put(pmi.getName(), marker);
                    }
                }
            }
        };
        ThreadPoolProxy.getInstance().execute(runnable);
    }

    private HashMap<String,Marker> hasMark = new HashMap<String, Marker>();

    //Dictionary<String, Marker> dicMarker=null;
    // 更新地图头像状态
    @SuppressLint("LongLogTag")
    private Bitmap getPosHeadBitmap(PosMarkerInfo pmi, boolean isCheck) {
        // int userState = getUserState(pmi);


        LayoutInflater posLayoutInflater = getLayoutInflater();
        View headView = null;
		/*if (isCheck) {
			headView = posLayoutInflater.inflate(
					R.layout.layout_pos_head_check, null);
			TextView txtPosCheckName = (TextView) headView
					.findViewById(R.id.txtPosCheckName);
			txtPosCheckName.setText(pmi.getName());
		} else {
			headView = posLayoutInflater.inflate(
					R.layout.layout_pos_head_uncheck, null);
			TextView txtPosUnCheckName = (TextView) headView
					.findViewById(R.id.txtPosUnCheckName);
			txtPosUnCheckName.setText(pmi.getName());
		}*/

        int type = pmi.getType();
        switch (type) {
            case 0://调度台
                if (isCheck) {

                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_check, null);
                    headView.setBackgroundResource(R.drawable.pos_dispather_checked);
                    TextView txtPosCheckName = (TextView) headView
                            .findViewById(R.id.txtPosCheckName);
                    txtPosCheckName.setText(pmi.getName());
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(true);
                                break;
                            }
                        }
                    }
                } else {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_uncheck, null);
                    //headView.setBackgroundResource(R.drawable.pos_dispather_unchecked);
                    //添加时根据成员的在线状态设置对应Icon
                    if (memberStatehashMap.containsKey(pmi.getName())){
                        int state = ptt_3g_PadApplication.memberStatehashMap.get(pmi.getName()).getStatus();
                        if (state != GlobalConstant.GROUP_MEMBER_EMPTY
                                && state != GlobalConstant.GROUP_MEMBER_INIT){
                            //在线
                            headView.setBackgroundResource(R.drawable.pos_dispather_unchecked);
                        }else {
                            //离线
                            headView.setBackgroundResource(R.drawable.pos_dispather_unchecked_offline);
                        }
                    }else {
                        //不包含默认为在线
                        headView.setBackgroundResource(R.drawable.pos_dispather_unchecked);
                    }
                    TextView txtPosUnCheckName = (TextView) headView
                            .findViewById(R.id.txtPosUnCheckName);
                    txtPosUnCheckName.setText(pmi.getName());
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(false);
                                break;
                            }
                        }
                    }
                }
                break;
            case 1://对讲终端
            case 3://外线用户
            case 6://3G对讲 ,这三种类型都作为3G用户处理
                if (isCheck) {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_check, null);
                    headView.setBackgroundResource(R.drawable.pos_head_2);
                    TextView txtPosCheckName = (TextView) headView
                            .findViewById(R.id.txtPosCheckName);
                    txtPosCheckName.setText(pmi.getName());
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(true);
                                break;
                            }
                        }
                    }
                } else {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_uncheck, null);
                    //添加时根据成员的在线状态设置对应Icon
                    if (memberStatehashMap.containsKey(pmi.getName())){
                        int state = ptt_3g_PadApplication.memberStatehashMap.get(pmi.getName()).getStatus();
                        if (state != GlobalConstant.GROUP_MEMBER_EMPTY
                                && state != GlobalConstant.GROUP_MEMBER_INIT){
                            //在线
                            headView.setBackgroundResource(R.drawable.pos_head_1);
                        }else {
                            //离线
                            headView.setBackgroundResource(R.drawable.pos_head_offline);
                        }
                    }else {
                        //不包含默认为在线
                        headView.setBackgroundResource(R.drawable.pos_head_1);
                    }
                    TextView txtPosUnCheckName = (TextView) headView
                            .findViewById(R.id.txtPosUnCheckName);
                    txtPosUnCheckName.setText(pmi.getName());
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(false);
                                break;
                            }
                        }
                    }
                }
                break;
            case 2://软电话(SIP电话)
                if (isCheck) {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_check, null);
                    headView.setBackgroundResource(R.drawable.pos_sip_checked);
                    TextView txtPosCheckName = (TextView) headView
                            .findViewById(R.id.txtPosCheckName);
                    txtPosCheckName.setText(pmi.getName());
                    txtPosCheckName.bringToFront();
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(true);
                                break;
                            }
                        }
                    }
                } else {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_uncheck, null);
                    //headView.setBackgroundResource(R.drawable.pos_sip_unchecked);
                    //添加时根据成员的在线状态设置对应Icon
                    if (memberStatehashMap.containsKey(pmi.getName())){
                        int state = ptt_3g_PadApplication.memberStatehashMap.get(pmi.getName()).getStatus();
                        if (state != GlobalConstant.GROUP_MEMBER_EMPTY
                                && state != GlobalConstant.GROUP_MEMBER_INIT){
                            //在线
                            headView.setBackgroundResource(R.drawable.pos_sip_unchecked);
                        }else {
                            //离线
                            headView.setBackgroundResource(R.drawable.pos_sip_unchecked_offline);
                        }
                    }else {
                        //不包含默认为在线
                        headView.setBackgroundResource(R.drawable.pos_sip_unchecked);
                    }
                    TextView txtPosUnCheckName = (TextView) headView
                            .findViewById(R.id.txtPosUnCheckName);
                    txtPosUnCheckName.setText(pmi.getName());
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(false);
                                break;
                            }
                        }
                    }
                }
                break;
            case 7://监控设备

                if (isCheck) {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_check, null);
                    headView.setBackgroundResource(R.drawable.pos_moitor_checked);
                    TextView txtPosCheckName = (TextView) headView
                            .findViewById(R.id.txtPosCheckName);
                    txtPosCheckName.setText(pmi.getName());
                    txtPosCheckName.bringToFront();
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(true);
                                break;
                            }
                        }
                    }
                } else {
                    headView = posLayoutInflater.inflate(
                            R.layout.layout_pos_head_uncheck, null);
                    //headView.setBackgroundResource(R.drawable.pos_moitor_unchecked);
                    //添加时根据成员的在线状态设置对应Icon
                    if (memberStatehashMap.containsKey(pmi.getName())){
                        int state = ptt_3g_PadApplication.memberStatehashMap.get(pmi.getName()).getStatus();
                        if (state != GlobalConstant.GROUP_MEMBER_EMPTY
                                && state != GlobalConstant.GROUP_MEMBER_INIT){
                            //在线
                            headView.setBackgroundResource(R.drawable.pos_moitor_unchecked);
                        }else {
                            //离线
                            headView.setBackgroundResource(R.drawable.pos_moitor_unchecked_offline);
                        }
                    }else {
                        //不包含默认为在线
                        headView.setBackgroundResource(R.drawable.pos_moitor_unchecked);
                    }
                    TextView txtPosUnCheckName = (TextView) headView
                            .findViewById(R.id.txtPosUnCheckName);
                    txtPosUnCheckName.setText(pmi.getName());
				/*更改成员列表中对应成员的选中状态*/
                    if (employeeLayout != null && employeeLayout.getChildCount() > 0 && employeeLayout.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                            CheckBox box = (CheckBox) employeeLayout.getChildAt(i);
                            if (box.getText().equals(pmi.getName())) {
                                box.setChecked(false);
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }

        return getViewBitmap(headView);
    }

    //获取组成员状态
    private int getUserState(PosMarkerInfo pmi) {
        List<MemberInfo> allMemberInfoData = GroupManager.getInstance().getAllMemberInfoData();
        for (int i = 0; i < allMemberInfoData.size(); i++) {
            if (pmi.getType() == 6) {
                if (pmi.getName().equals(allMemberInfoData.get(i).getName())) {
                    return allMemberInfoData.get(i).getStatus();
                }
            }
        }
        return GlobalConstant.GROUP_MEMBER_INIT;
    }

    // 把布局转换成Bitmap
    private Bitmap getViewBitmap(View headView) {


        headView.setDrawingCacheEnabled(true);
        headView.bringToFront();
        headView.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        headView.layout(0, 0, headView.getMeasuredWidth(),
                headView.getMeasuredHeight());

        headView.buildDrawingCache();
        Bitmap cacheBitmap = headView.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }

    // 会议、监控获取视频
    private void GetVideo(MonitorInfo monitorinfo) {
        if (currMeeting == null) {
            return;
        }


        Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_GETVIDEO);
        intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
        intent.putExtra(GlobalConstant.KEY_MEETING_CID, monitorinfo.getCid());// 会议唯一标识
        intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, monitorinfo.getNumber());// 请求用户编号
        sendBroadcast(intent);
    }

    // 释放视频
    @SuppressLint("LongLogTag")
    private void ReleaseVideo(final MonitorInfo monitorinfo) {
//       Runnable runnable = new Runnable() {
//           @Override
//           public void run() {
        if (monitorMap.containsKey(monitorinfo.getNumber())) {
            MonitorInfoBean monitorInfoBean = monitorMap.get(monitorinfo.getNumber());
            String cid1 = monitorInfoBean.getCid();
            SurfaceView surfaceView = monitorInfoBean.getSurfaceView();
            Log.e(TAG, "结束监控   cid1 : " + cid1);
            Log.e("=======----======AAAAAAFFFFF-------======", "结束监控   cid1 : " + cid1);

            boolean result = MediaEngine.GetInstance().ME_StopReceiveVideo(cid1, monitorinfo.getNumber());
            surfaceView.setVisibility(View.GONE);
            surfaceView = null;
            Log.e("=======----======AAAAAAFFFFF-------======", "释放视频  result : " + result + "\r\n" + "cid : " + cid1 + "\r\n" + "number : " + monitorinfo.getNumber());

            if (result) {
                // 释放视频请求成功
                Intent intent = new Intent(GlobalConstant.ACTION_VIDEO_RELEASE);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, cid1);// ID透传
                sendBroadcast(intent);
                Log.e(TAG, "结束监控  number : " + monitorinfo.getNumber());
            } else {
                Intent intent = new Intent(GlobalConstant.ACTION_VIDEO_RELEASE);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                intent.putExtra(GlobalConstant.KEY_MEETING_SID, "");// ID透传
                intent.putExtra(GlobalConstant.KEY_MONITOR_ERROR, "");// 监控获取视频错误号
                intent.putExtra(GlobalConstant.KEY_MONITOR_DIS, "");// 监控获取视频错误描述
                sendBroadcast(intent);
                return;
            }
            monitorMap.remove(monitorinfo.getNumber());
        }

        if (decodeMap.size() > 0) {
            if (decodeMap.containsKey(monitorinfo.getNumber())) {
                DecoderBean decoderBean = decodeMap.get(monitorinfo.getNumber());
                String sessionid = decoderBean.getSessionId();
                String decoderIds = decoderBean.getDecoderId();
                String chanidString = decoderBean.getChanidStrings();
                //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
                selectedDecoderEnd(sessionid, monitorinfo.getNumber(), decoderIds, Integer.valueOf(chanidString));
                decodeMap.remove(monitorinfo.getNumber());
            }
        }
//           }
//       };
//        ThreadPoolProxy.getInstance().execute(runnable);
    }

    // 开始监控终端视频
    private void MonitorStart(MonitorInfo monitorinfo) {

        if (monitorinfo == null) {
            return;
        }

        Log.e(TAG, "发起监控   number : " + monitorinfo.getNumber());

        Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_START);
        intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
        intent.putExtra(GlobalConstant.KEY_MEETING_CID, monitorinfo.getCid());// 会议唯一标识
        intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, monitorinfo.getNumber());// 监控号码
        sendBroadcast(intent);


        Intent intent1 = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_EVENT);
        intent1.putExtra(GlobalConstant.KEY_MEETING_CID, monitorinfo.getCid());// 会议唯一标识
        intent1.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, monitorinfo.getNumber());// 监控号码
        intent1.putExtra(GlobalConstant.KEY_MONITOR_IP, "192626215");// 服务器IP
        intent1.putExtra(GlobalConstant.KEY_MONITOR_PORT, "9000");// 服务器端口
        sendBroadcast(intent1);
    }

    //设置视频端口，存入配置文件 YUEZS ADD 2015-09-01
    private void settingsVedioPort(Integer vedioPort) {//this.getClass().getName()
        SharedPreferences settings = getSharedPreferences(GlobalConstant.SP_VEDIOCONFIG, 0);
        Editor editor = settings.edit();
        editor.putInt("VedioPort", vedioPort);
        editor.apply();
    }

    //获取配置文件中的视频端口号 YUEZS ADD 2015-09-01
    private Integer getVedioPort() {
        SharedPreferences settings = getSharedPreferences(
                GlobalConstant.SP_VEDIOCONFIG, 0);
        return settings.getInt("VedioPort", 0);
    }

    // 结束监控终端视频
    private void MonitorEnd(final String name, String cid, String dwss, final String number) {
//        // 取得登录用户信息
//        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(
//                prefs);
//        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
//
//        // 发送创建会议请求
//		/*String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID,
//				false);
//		// 配置服务器IP
//		if (!uri.contains(sipServer.getServerIp())) {
//			uri += sipServer.getServerIp();
//		}*/
//        String msgBody = "req:vbug_end\r\n" + "sid:" + sid + "\r\n" + "cid:"
//                + cid + "\r\n" + "employeeid:" + sipUser.getUsername() + "\r\n";
//		/*MtcDelegate.log("lizhiwei:send,req:vbug_end," + uri + "," + msgBody);
//		MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_VBUG_END, uri, 1,
//				GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);*/
//        String uri = sipUser.getUsername() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();
//        MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
//        Log.e("===Sws测试会议===", "结束监控终端视频");
//        Log.e("===Sws测试会议===", "cid:" + cid);

        //结束监控
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
        if (monitorMap.containsKey(number)) {
            MonitorInfoBean monitorInfoBean = monitorMap.get(number);
            String cid1 = monitorInfoBean.getCid();
            SurfaceView surfaceView = monitorInfoBean.getSurfaceView();
            boolean result = MediaEngine.GetInstance().ME_StopReceiveVideo(cid1);
            surfaceView.setVisibility(View.GONE);
            surfaceView = null;
            Log.e(TAG, "结束监控终端视频  result : " + result + "\r\n" + "cid : " + cid1);

            if (result) {
                // 结束监控终端视频,请求成功
                Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_END);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, true);
                intent.putExtra(GlobalConstant.KEY_MEETING_CID, cid1);// 会议唯一标识
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_END);
                intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                intent.putExtra(GlobalConstant.KEY_MEETING_CID, cid1);// 会议唯一标识
			/*intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, strMsg[3].substring("dstid:".length()));// 监控号码*/
                sendBroadcast(intent);
                return;
            }
            monitorMap.remove(number);
        }

        if (decodeMap.size() > 0) {
            if (decodeMap.containsKey(number)) {
                DecoderBean decoderBean = decodeMap.get(number);
                String sessionid = decoderBean.getSessionId();
                String decoderIds = decoderBean.getDecoderId();
                String chanidString = decoderBean.getChanidStrings();
                //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
                selectedDecoderEnd(sessionid, name, decoderIds, Integer.valueOf(chanidString));
            }
        }
//            }
//        };
//
//        ThreadPoolProxy.getInstance().execute(runnable);

    }

    // 获取对讲组-新方法 yuezs add
    private void GetGroupInfo() {
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(
                prefs);
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        String uri = sipUser.getUsername() + "@" + sipServer.getServerIp() + ":" + sipServer.getPort();
        String msgBody = "req:groupInfo\r\nemployeeid:" + sipUser.getUsername()
                + "\r\n";
        MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
//        //获取对讲组
//        MediaEngine.GetInstance().ME_GetPttGroup_Async(new MediaEngine.ME_GetPttGroup_CallBack() {
//            @Override
//            public void onCallBack(MediaEngine.ME_PttGroupInfo[] me_pttGroupInfos) {
//
//                if (me_pttGroupInfos.length <= 0){
//                    return;
//                }
//                List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
//                for(MediaEngine.ME_PttGroupInfo g : me_pttGroupInfos){
////                    GroupInfo groupInfo = new GroupInfo(g.groupName, g.groupNumber,g.);
////                    groupInfos.add(groupInfo);
//                }
//
//
//            }
//        });
    }

    // 发起通话(适用于呼出,此处只有单呼)(Over)
    @Override
    public void mtcCallDelegateCall(Object contact, String number,
                                    boolean isVideo) {
        Log.e(TAG, "mtcCallDelegateCall   number : " + number);
        // 号码为空则返回
        if (number.length() == 0) {
            return;
        }
        // 设置状态
        mCallState = GlobalConstant.CALL_STATE_CALLING;
        // 发起通话
        MediaEngine.GetInstance().ME_MakeCall(number, isVideo);

        Log.e(TAG, "ME_MakeCall   number : " + number + "\r\n" + "isVideo : " + isVideo);
        //MtcCall.Mtc_SessCa	ll(uri, 0, true, isVideo);
        //}
        Log.e(TAG, "-----------mtcCallDelegateCall 发起通话------------" + "mSessId:" + mSessId);

		/*yuezs add 移到OutGoing事件回调中// 如果发起通话没有成功则终止
		if (mSessId == MtcCliConstants.INVALIDID) {
			mtcCallDelegateTermed(mSessId, -1);
			return;
		}
		// 如果大于4路则终止通话
		if (CallingManager.getInstance().getCallingCount() >= 4) {
			MtcDelegate.log("lizhiwei 呼出多于4路挂断");
			onEnd(mSessId);
			CallingManager.getInstance().removeCallingInfo(mSessId);
			return;
		}
		// 如果当前有正在进行中的对讲则保持对讲
		CallingInfo callingIntercom = CallingManager.getInstance().getCallingInfoByType(GlobalConstant.CALL_TYPE_INTERCOM);
		if ((callingIntercom != null) && (!callingIntercom.isHolding())) {
			callingIntercom.setHolding(true);
			MtcCall.Mtc_SessHold(callingIntercom.getDwSessId());
		}

		//如果当前有正在进行中的对讲则挂断对讲(新逻辑)
		if((callingIntercom != null) && (!callingIntercom.isHolding()))
		{
			MtcDelegate.log("如果当前有对讲则挂断对讲");
			onEnd(mSessId);
		}


		// 添加通话信息列表
		CallingManager.getInstance().addCallingInfo(
				new CallingInfo(mSessId, number, GlobalConstant.CALL_TYPE_CALLING,
						isVideo ? GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO
								: GlobalConstant.CONFERENCE_MEDIATYPE_VOICE,
						true, false, false));*/
    }

    // 通话呼入(适用于呼入)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void mtcCallDelegateIncoming(int dwSessId) {


        Log.e(TAG, "MainActivity ====7103=====收到新来电====== dwSessId：" + dwSessId);
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            Log.e(TAG, "dwSessId == -1   return");
            return;
        }
        // 设置当前会话编号
        //mSessId = dwSessId;
        Log.e(TAG, "当前mSessId：" + dwSessId);
        //new Thread(new Runnable(){
        //   @Override
        //  public void run() {
        //耗时操作
		/*mHandlerIncoming.sendEmptyMessage(0);
		Message msg = new Message();
		msg.obj = GlobalConstant.ONLYTAB;//可以是基本类型，可以是对象，可以是List、map等
		mHandlerIncoming.sendMessage(msg);*/
        Log.e(TAG, "mHandlerIncoming已发送");
        //   }
        //}).start();
        // 设置状态

        //int data = (Integer)msg.obj;
        mCallState = GlobalConstant.CALL_STATE_INCOMING;
        Log.e(TAG, "给mCallState状态设置为通话状态呼入中：" + GlobalConstant.CALL_STATE_INCOMING);
        // 初始化通话信息
        CallingInfo callinginfo = new CallingInfo(dwSessId);

        Log.e(TAG, "通过dwSessId得到callinginfo         callType:" + callinginfo.getCalltype());
        // 取得对方名称及地址
				/*MtcString ppcDispName = new MtcString();
        		MtcString ppcUri = new MtcString();
        		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
        		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*///"1802" <sip:1802@192.168.1.15>
        //得到远端完整号码
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(dwSessId);
        Log.e(TAG, "MainActivity ====7139=========== 得到远端号码：" + callString);
        Log.e(TAG, "远端完整号码:" + callString);
        if ("".equals(callString) || callString == null) {
            Log.e(TAG, "MainActivity ====7142=========== 得到远端号码为空   return掉了s");
            return;
        }
        int calltype;
        String names[] = callString.split("sip:");

        String name1[] = names[1].split("@");
        //String n[]=MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId).split("<");
        String name = name1[0];//n[0].toString().replace("\"", "");//MediaEngine.GetInstance().ME_GetRemoteFullUri(dwSessId);
        Log.e(TAG, "Sws 中   name:" + name + ",dwSessId:" + dwSessId);
        Log.e(TAG, "对callString进行解析   得到name：" + name);
        if (name == null || "".equals(name)) {
            Log.e(TAG, "name为空  return掉了");
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*5*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼

            calltype = GlobalConstant.CALL_TYPE_CALLING;
            callinginfo.setOutgoing(false);
            // 取得正在进行的通话或对讲
            CallingInfo callingIntercom = null;
            CallingInfo callingCalling = null;

            Log.e(TAG, "lizhiwei mtcCallDelegateIncoming:来了一则通话，共有几个进行的通话" + CallingManager.getInstance().getCallingData().size()
                    + ",isHolding:" + (CallingManager.getInstance().getCallingData().size() > 0 ? CallingManager.getInstance().getCallingData().get(0).isHolding() : false));

            for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
                if ((!callingInfo.isHolding())
                        && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07 qq
                    if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                        callingIntercom = callingInfo;
                    } else {
                        callingCalling = callingInfo;
                    }
                }
            }
            // 设置通话信息
            callinginfo.setRemoteName(name);
            callinginfo.setCalltype(calltype);
            callinginfo.setIsalright(false);
            //MtcCall.Mtc_SessHasVideo(dwSessId)

            if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
                callinginfo.setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
            } else {
                callinginfo.setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
            }
            Log.e(TAG, "main  incoming 7817");
            if ((callingIntercom != null) || (callingCalling != null)) {
                Log.e(TAG, "main  incoming 7819");
                if (callingIntercom != null) {
                    Log.e(TAG, "main  incoming 7821");
                    // 有一个对讲正在进行，来了一个新的单呼呼入，给出提示操作  (之前逻辑  现在改为 来电直接挂断当前对讲 并跳转到calling界面)
                    //intercomShowCallingInfo(callinginfo, callingIntercom);

                    // 终止原来通话
                    onEnd(callingIntercom.getDwSessId());
                    // 移除通话数据
                    CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());


                    // 增加通话记录
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(
                            MainActivity.this);
                    sqLiteHelper.open();
                    CallingRecords callingRecord = new CallingRecords();
                    callingRecord.setUserNo(sipUser.getUsername());
                    callingRecord.setBuddyNo(callinginfo.getRemoteName());
                    Date nowDate = new Date();
                    callingRecord.setStartDate(nowDate);
                    callingRecord.setAnswerDate(nowDate);
                    callingRecord.setStopDate(nowDate);
                    callingRecord.setDuration(0);
                    callingRecord.setTime(FormatController.secToTime(0));
                    callingRecord.setInOutFlg(1);
                    callingRecord.setCallState(0);// 忽略，未接通
                    callingRecord.setSessId(callinginfo.getDwSessId());
                    sqLiteHelper.createCallingRecords(callingRecord);
                    sqLiteHelper.closeclose();


                    // 切换到通话activity
                    if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {

                        mImageAdapter.setImageLight(1);
                        mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                        SetViewShow(GlobalConstant.ONLYTAB);
                        // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
                    }

                    // 设置接听类型
                    callinginfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);

                    // 添加新的通话
                    CallingManager.getInstance().addCallingInfo(callinginfo);
                    CallingManager.getInstance().addAnswerInfo(callinginfo);
                    // 来电广播
                    Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                    callingIncoming.putExtra(KEY_CALL_CHANGEVIEW, true);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, callinginfo.getRemoteName());
                    sendBroadcast(callingIncoming);
                    Log.e(TAG, "main     1245");
                    return;
                }


                if (callingCalling != null) {
                    Log.e(TAG, "main  incoming 7849");
                    // 有一个通话正在进行，来了一个新的单呼呼入了，给出提示操作
                    showCallingInfo(callinginfo, callingCalling);
                    return;
                }
            } else {
                // 通知接听
                // 切换到通话activity
                if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {

                    mImageAdapter.setImageLight(1);
                    mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                    Log.e(TAG, "ShowType.ONLYTAB--" + GlobalConstant.ONLYTAB);
                    SetViewShow(GlobalConstant.ONLYTAB);
                    Log.e(TAG, "切换到通话页签");
                }
                String userNo = sipUser.getUsername();//MtcDelegate.getLoginedUser();
                Log.e(TAG, "userNo:" + userNo);
                // 增加通话记录
                SQLiteHelper sqLiteHelper = new SQLiteHelper(MainActivity.this);
                sqLiteHelper.open();
                CallingRecords callingRecord = new CallingRecords();
                callingRecord.setUserNo(userNo);
                callingRecord.setBuddyNo(name);
                Date nowDate = new Date();
                callingRecord.setStartDate(nowDate);
                callingRecord.setAnswerDate(nowDate);
                callingRecord.setStopDate(nowDate);
                callingRecord.setDuration(0);
                callingRecord.setTime(FormatController.secToTime(0));
                callingRecord.setInOutFlg(1);
                callingRecord.setCallState(0);
                callingRecord.setSessId(dwSessId);
                sqLiteHelper.createCallingRecords(callingRecord);
                sqLiteHelper.closeclose();

                // 取得本地前置摄像头，并设置*2016-07-04隐藏因为它会影响视频通话摄像头的设置，每次都是前置*
                //        				int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
                //        				if (camCnt >= 1) {
                //        					MtcMedia.Mtc_VideoSetInputDev("front");
                //        				}

                // 取得远程摄像头
//						/*if (MtcCall.Mtc_SessPeerOfferVideo(dwSessId)) {
//        					mtcCallDelegateStartPreview();
//        				} else {
//        					MtcProximity.start(this);
//        				}*/

                if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
                    mtcCallDelegateStartPreview();
                } else {
                    MtcProximity.start(MainActivity.this);
                }
//                // 是否延迟提供音视频
//						/*mIsDelayOffer = MtcCall.Mtc_SessPeerOfferAudio(dwSessId) == false
//        						&& MtcCall.Mtc_SessPeerOfferVideo(dwSessId) == false;*/


                //保证界面始终在屏幕上
                keepScreenOn(true);
                // 屏幕始终不锁定
                showWhenLocked(true);
                // 添加新的通话
                CallingManager.getInstance().addCallingInfo(callinginfo);
                Log.e(TAG, "开始广播incoming  7347");
                // 来电广播
                Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);   //mSessId 2015-05-07 qq
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                sendBroadcast(callingIncoming);
                Log.e(TAG, "开始广播incoming  7354");
                Log.e(TAG, "main     7552");
            }
        } else if (name.startsWith("*7*")) {    // 3G监控，现阶段只做自动应答
            return;
//					/*calltype = GlobalConstant.CALL_TYPE_MONITOR;
//        			// 自动视频应答
//        			onVideoAnswer(dwSessId);   //mSessId 2015-05-07 qq
//        			// 取得视频信息
//        			name = name.substring(3);
//        			String camera = name.substring(0, 1);
//
//        			 // String codec = name.substring(1, name.length() - 1); String
//        			 // answer = name.substring(name.length() - 1);
//
//        			// 设置摄像头
//        			int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
//        			if (camCnt > 1) {
//        				String strName = MtcMedia.Mtc_VideoGetInputDev();
//        				if (camera.equals("1")) {
//        					// 前摄像头
//        					if (strName.equals("back")) {
//        						MtcMedia.Mtc_VideoSetInputDev("front");
//        					}
//        				} else {
//        					// 后摄像头
//        					if (strName.equals("front")) {
//        						MtcMedia.Mtc_VideoSetInputDev("back");
//        					}
//        				}
//        			}*/
        } else {

            Log.e(TAG, "对讲或会议");
            // 对讲或会议
            boolean istempintercom = false;
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                Log.e(TAG, "判断name为*9*");
                // 临时对讲
                if (name.startsWith("*9*")) {
                    istempintercom = true;
                }
                // 临时会议
                name = name.substring(3);
                Log.e(TAG, "name截取后：" + name);
            }

            String[] callinfo = name.split("\\~");
            Log.e(TAG, "Sws---> 是否为临时对讲:" + istempintercom + ",  callinfo长度：" + callinfo.length);
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
            String remoteno = callinfo[1];// 发起者号码

//            //判断发起者号码是不是当前注册号码
//            if (remoteno.equals(sipUser.getUsername())){
//                return;
//            }

            Log.e(TAG, "MainActivity ====7342=========== 解析后   name：" + name + ",remoteno" + remoteno);
            // 判断是否为会议
            //如果是会议/广播
            //如果已经有单呼，自己再发起会议，首先判断会议是否已经存在，如果会议存在，自动拒绝新来电，如果没有会议存在，接听新的会议并且将单呼保持
            //如果已经有对讲，自己再发起会议，挂断对讲，自动接听会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 由于会议只有自己创建的，所以会议创建之后一定要对进行的对讲和通话进行保持操作。需要在创建会议前做路数的判断。
                calltype = GlobalConstant.CALL_TYPE_MEETING;

                //当是会议或者广播响应时，说明之前发起的会议或者得到响应，在这里可以关闭timer
						/*if(ptt_3g_PadApplication != null)
        				{
        					ptt_3g_PadApplication.endTimer();
        				}*/
                Log.e(TAG, "进入会议1");
                CallingInfo callingIntercom = null;
                CallingInfo callingCalling = null;
                for (CallingInfo callingInfo : CallingManager.getInstance()
                        .getCallingData()) {
                    if ((!callingInfo.isHolding())
                            && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07 qq
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                            callingIntercom = callingInfo;
                        } else {
                            callingCalling = callingInfo;
                        }
                    }
                }

//						/*if (callingCalling != null) {
//        					// 保持通话
//        					callingCalling.setHolding(true);
//        					MtcCall.Mtc_SessHold(callingCalling.getDwSessId());
//        				}*/

                if (callingIntercom != null) {

//							/*// 保持对讲
//        					callingIntercom.setHolding(true);
//        					MtcCall.Mtc_SessHold(callingIntercom.getDwSessId());*/

                    //结束对讲
                    onEnd(callingIntercom.getDwSessId());
                    // 移除通话数据
                    CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());
                }
                // 判断来电是否为会议
                if (currMeeting.getIsMonitor() == 1) {
                    calltype = GlobalConstant.CALL_TYPE_MONITOR;
                    Log.e(TAG, "进入会议2");
                    if (mTabHost.getCurrentTabTag() != GlobalConstant.MONITOR_TAB) {
                        mImageAdapter.setImageLight(6);
                        mTabHost.setCurrentTabByTag(GlobalConstant.MONITOR_TAB);
                        SetViewShow(GlobalConstant.ALL);
                    }
                } else {
                    calltype = GlobalConstant.CALL_TYPE_MEETING;
                    if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                        mImageAdapter.setImageLight(2);
                        mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                        SetViewShow(GlobalConstant.ALL);
                        Log.e(TAG, "进入会议3");
                    }

                    // 取得本地前置摄像头，并设置*2016-07-04隐藏因为它会影响视频会议摄像头的设置，每次都是前置*
                    //        					int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
                    //        					if (camCnt >= 1) {
                    //        						MtcMedia.Mtc_VideoSetInputDev("front");
                    //        						Log.e("mtcmedia:", "mainActivit>>>>>");
                    //        					}

                    // 取得远程摄像头
                    //if (MtcCall.Mtc_SessPeerOfferVideo(mSessId)) {
                    if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
                        mtcCallDelegateStartPreview();
                    } else {
                        MtcProximity.start(MainActivity.this);
                    }

                    // 是否延迟提供音视频
                    mIsDelayOffer = MtcCall.Mtc_SessPeerOfferAudio(dwSessId) == false
                            && MtcCall.Mtc_SessPeerOfferVideo(dwSessId) == false;

                    // 保证界面始终在屏幕上
                    keepScreenOn(true);
                    // 屏幕始终不锁定
                    showWhenLocked(true);
                }
                // 设置通话信息
                callinginfo.setRemoteName(name);
                callinginfo.setCalltype(calltype);
                callinginfo.setIsalright(false);
                // 添加新的通话
                CallingManager.getInstance().addCallingInfo(callinginfo);
                Log.e(TAG, "***INCOMING:" + calltype + "---getCalltype():" + callinginfo.getCalltype());
                // 通知自动接听
                Intent callingIncoming = new Intent(
                        GlobalConstant.ACTION_CALLING_INCOMING);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID,
                        dwSessId);   //mSessId 2015-05-07 qq
                callingIncoming
                        .putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO,
                        name);
                sendBroadcast(callingIncoming);
                Log.e(TAG, "main     7710");

            } else {
                Log.e(TAG, "MainActivity ====7446===========  来电为对讲 ");
                //如果是对讲/临时对讲
                //如果已经有单呼，自己在发起对讲，单呼保持，自动接听对讲
                //如果已经有对讲，自己在发起对讲，挂断当前对讲，自动接听新对讲
                //如果已经有会议或者广播，自己在发起对讲，单呼保持，自动接听对讲
                Log.e(TAG, "----------收到对讲呼入-----------------");
                if (null == GroupManager.getInstance().getOptimumgroup()) {
                    Log.e(TAG, "");
                    MtcCall.Mtc_SessTerm(callinginfo.getDwSessId(),
                            MtcCallConstants.EN_MTC_CALL_TERM_REASON_NORMAL, null);
                    callinginfo = null;
                    return;
                }
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
                Log.e(TAG, "判断来电为对讲===7465  并将calltype赋值为对讲   calltype：" + calltype);
                // 判断是否为自己的对讲
                String OptimumGroupNum = "";
                OptimumGroupNum = GroupManager.getInstance().getOptimumgroup().getNumber().trim();
                if (((remoteno != null) && (!"".equals(OptimumGroupNum))
                        && (remoteno.equals(sipUser.getUsername())) && OptimumGroupNum.equals(name.trim()))
                        || (istempintercom && remoteno != null && remoteno.equals(sipUser.getUsername()))) {
                    callinginfo.setOutgoing(true);
                    Log.e(TAG, "----------收到对讲呼入6837-----------------callinginfo.setOutgoing(true)    拨出");
                } else {
                    callinginfo.setOutgoing(false);
                    Log.e(TAG, "----------收到对讲呼入6837-----------------callinginfo.setOutgoing(false)       不是拨出");
                }
                //MtcDelegate.log("lizhiwei 是否为呼出=" + callinginfo.isOutgoing());
                // 如果为临时对讲需要获取对讲组成员
                Log.e(TAG, "MainActivity ====7476===========  是否为临时对讲： " + istempintercom);
                if (istempintercom) {
                    // 设置是临时对讲
                    callinginfo.setIstempintercom(true);
                    Log.e(TAG, "----------收到对讲呼入6847-----------------判断为临时对讲");
//							/*GroupInfo groupinfo = GroupManager.getInstance().getGroupInfo(name);
//        					if (groupinfo == null){
//        						MtcDelegate.log("lizhiwei 没有取得对讲组" + name);*/
//                    // 添加临时对讲组信息
//							/*Calendar calendar = Calendar.getInstance();
//        				    	String sid = CommonMethod.getCurrDate(calendar).replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
//        				    	String sessname = (CommonMethod.getCurrLongCNDate(calendar)+"临时对讲").replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "").substring("yyyy年MM月dd日".length());
//        				    	SimpleDateFormat shortformat = new SimpleDateFormat("HHmm");
//        				    	sessname = "临时对讲" + shortformat.format(calendar.getTime());*/
                    GroupInfo groupinfo = new GroupInfo();
                    if (name.length() >= 14) {
                        groupinfo.setName("临时对讲" + name.substring(8, 12));
                    } else {
                        groupinfo.setName("临时对讲");
                    }
                    Log.e(TAG, "====临时对讲组号：" + name);
                    groupinfo.setNumber(name);
//							/*groupinfo = new GroupInfo();
//        						groupinfo.setName(name);
//        						groupinfo.setNumber(name);*/
                    groupinfo.setLevel(10);
                    groupinfo.setTemporary(true);
                    groupinfo.setVisible(false);
                    // 添加对讲组
                    GroupManager.getInstance().addGroup(groupinfo);
                }
                Log.e(TAG, "8160");
                // 取得正在进行的通话或对讲
                CallingInfo callingIntercom = null;
                CallingInfo callingCalling = null;
                for (CallingInfo callingInfo : CallingManager.getInstance()
                        .getCallingData()) {
                    if ((!callingInfo.isHolding())
                            && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07 qq
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                            callingIntercom = callingInfo;
                        } else {
                            callingCalling = callingInfo;
                        }
                    }
                }

                if ((callingIntercom != null) || (callingCalling != null)) {
                    Log.e(TAG, "8177");
                    // 对讲与通话正在进行中
                    if (callingIntercom != null) {
                        Log.e(TAG, "MainActivity ====7476=========== 当前有对讲正在进行 ");
                        // 对讲正在进行中
                        callinginfo.setRemoteName(name);
                        callinginfo.setCalltype(calltype);
                        callinginfo.setIsalright(true);
                        Log.e(TAG, "callinginfo.isOutgoing()    : " + callinginfo.isOutgoing());
                        if (callinginfo.isOutgoing()) {
                            Log.e(TAG, "MainActivity ====7565===========  这个对讲是自己发起的");
                            Log.e(TAG, "------传递mSessId：" + dwSessId + "---" + callinginfo.getDwSessId());
                            // 有一个对讲正在进行，自己又呼出一个对讲需要挂断原来对讲，自动接听新对讲
                            // 终止原来通话
                            Log.e(TAG, "lizhiwei 对讲重叠挂断");

                            IntercomActivity.currSessId = dwSessId;
                            onEnd(callingIntercom.getDwSessId());
                            // 移除通话数据
                            CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());

                            // 添加新的通话
                            CallingManager.getInstance().addCallingInfo(callinginfo);

//                            // 切换到对讲activity
//                            if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                                mImageAdapter.setImageLight(0);
//                                mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                                SetViewShow(GlobalConstant.ALL);
//                            }

                            // 来电广播
                            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);   //mSessId 2015-05-07 qq
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, true);
                            sendBroadcast(callingIncoming);
                            Log.e(TAG, "main     7829");

                            Log.e(TAG, "发送 广播   此次对讲为自己发起   结束  发送GlobalConstant.ACTION_CALLING_INCOMING广播到IntercomsActivity界面");
                            return;
                        } else {
                            Log.e(TAG, "MainActivity ====7565===========  这个对讲不是自己发起的");
                            // 有一个对讲正在进行，另有一个对讲呼入，按照优先级进行处理
                            // 优先级处理时，如果为自动拒绝不需要切换页面。
                            // 取得默认对讲组
                            GroupInfo currGroupInfo = GroupManager.getInstance().getOptimumgroup();
                            GroupInfo newGroupInfo = GroupManager.getInstance().getGroupInfo(name);
                            Log.e("lizhiwei 走到了通知流程" + (((currGroupInfo != null) && (newGroupInfo != null)) ? "都不为空" : "有一个为空"), "");
                            // 添加新的通话
                            CallingManager.getInstance().addCallingInfo(callinginfo);
//                            // 通知新对讲
//                            if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                                mImageAdapter.setImageLight(0);
//                                mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                                SetViewShow(GlobalConstant.ALL);
//                            }
                            // 来电广播
                            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);   //mSessId 2015-05-07 qq
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, false);
                            sendBroadcast(callingIncoming);
                            Log.e(TAG, "开始广播incoming  7771");
                            return;
                        }
                    }

                    if (callingCalling != null) {
                        Log.e(TAG, "8246");
                        // 通话正在进行中
                        callinginfo.setRemoteName(name);
                        callinginfo.setCalltype(calltype);
                        callinginfo.setIsalright(true);
//								/*if (callinginfo.isOutgoing()) {
//        							// 有一个通话正在进行，自己又呼出一个对讲需要保持原来通话，自动接听新对讲
//        							onHold(callingCalling.getDwSessId());
//        							// 添加新的通话
//        							CallingManager.getInstance().addCallingInfo(callinginfo);
//
//        							// 切换到对讲activity
//        							if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//        								mImageAdapter.setImageLight(0);
//        								mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//        								SetViewShow(ShowType.ALL);
//        							}
//
//        							// 来电广播
//        							Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);   //mSessId 2015-05-07 qq
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
//        							callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, true);
//        							sendBroadcast(callingIncoming);
//        							return;
//        						} else {*/
                        //有一个通话正在进行，另有一个对讲呼入，自动挂断对讲
                        Log.e(TAG, "hangUp :" + callinginfo.getDwSessId());
                        MediaEngine.GetInstance().ME_Hangup(callinginfo.getDwSessId());
                        callinginfo = null;
                        return;
                        //	}
                    }
                } else {
                    // 对讲和通话都没有进行中
                    Log.e(TAG, "对讲和通话都没有进行中");
                    // TODO Auto-generated method stub
                    callinginfo.setRemoteName(name);
                    callinginfo.setCalltype(calltype);
                    callinginfo.setIsalright(false);
                    // 添加新的通话
                    CallingManager.getInstance().addCallingInfo(callinginfo);

//                    // 通知新对讲
//                    if (mTabHost.getCurrentTabTag() != GlobalConstant.INTERCOM_TAB) {
//                        mImageAdapter.setImageLight(0);
//                        mTabHost.setCurrentTabByTag(GlobalConstant.INTERCOM_TAB);
//                        SetViewShow(GlobalConstant.ALL);
//                    }

                    // 来电广播
                    Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, callinginfo.isOutgoing() ? true : false);
                    sendBroadcast(callingIncoming);
                    Log.e(TAG, "开始广播incoming  7840");
                    Log.e(TAG, "MainActivity ====7782===========发送广播：" + callinginfo.getCalltype());
                }
            }
        }

    }

    //对讲发起-对讲组申请话权/释放话权结果-SDK
    @Override
    public void mtcPttReqState(int state, String reason) {
        //calltype = GlobalConstant.CALL_TYPE_INTERCOM;
        Log.e(TAG, "state:" + state + ",reason:" + reason);
    }

    // 通话呼出(适用于呼出)
    @Override
    public void mtcCallDelegateOutgoing(int dwSessId) {

        Log.e(TAG, "收到通话呼出CallState   dwSessId: " + dwSessId);
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        mSessId = dwSessId;//yuezs add 10/25
        //mtcCallDelegateOutgoing得到响应说明，自己发起的
        // 如果发起通话没有成功则终止
        if (mSessId == MtcCliConstants.INVALIDID) {
            mtcCallDelegateTermed(mSessId, -1);
            return;
        }
        // 如果大于4路则终止通话
        if (CallingManager.getInstance().getCallingCount() >= 4) {
            MtcDelegate.log("lizhiwei 呼出多于4路挂断");
            onEnd(mSessId);
            CallingManager.getInstance().removeCallingInfo(mSessId);
            return;
        }
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(dwSessId);
        String name = "";
        String names[];
        String name1[];

        if (callString == null) {
            return;
        }

        names = callString.split("sip:");
        name1 = names[1].split("@");
        //String n[]=MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId).split("<");
        name = name1[0];

        boolean isVideo = MediaEngine.GetInstance().ME_HasVideo(dwSessId);
        Log.e(TAG, "main 7733   isVideo:" + isVideo);
        // 添加通话信息列表
        CallingManager.getInstance().addCallingInfo(
                new CallingInfo(mSessId, name, GlobalConstant.CALL_TYPE_CALLING,
                        isVideo ? GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO
                                : GlobalConstant.CONFERENCE_MEDIATYPE_VOICE,
                        true, false, false));
        Log.e(TAG, "gengsonghang 拨打电话");
        boolean exists = true;
        CallingInfo callinginfo = CallingManager.getInstance().getCallingInfo(
                dwSessId);
        if (callinginfo == null) {
            exists = false;
            callinginfo = new CallingInfo(dwSessId);   //mSessId 2015-05-07qq
        }
        // 设置状态
        mCallState = GlobalConstant.CALL_STATE_OUTGOING;
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/

        //String name=MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
		/*Log.w("7202-main","lizhiwei mtcCallDelegateOutgoing:" + name
				+ ",dwSessId:" + dwSessId);*/
        Log.e(TAG, "------mtcCallDelegateOutgoing-------" + "dwSessId:" + dwSessId + "---mSessId" + mSessId + "---name:" + name);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;

            //当是单呼响应时，说明之前发起的通话得到响应，在这里可以关闭timer
			/*if(ptt_3g_PadApplication != null)
			{
				ptt_3g_PadApplication.endTimer();
			}*/


            // 如果有对讲或通话，会将原来的对讲或通话进行保持
            CallingInfo callingIntercom = null;
            CallingInfo callingCalling = null;
            for (CallingInfo callingInfo : CallingManager.getInstance()
                    .getCallingData()) {
                if ((!callingInfo.isHolding())
                        && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07qq
                    if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                        callingIntercom = callingInfo;
                    } else {
                        callingCalling = callingInfo;
                    }
                }
            }
            if ((callingCalling != null)
                    && (callingCalling.getDwSessId() != dwSessId)) {
				/*// 保持通话
				callingCalling.setHolding(true);
				MtcCall.Mtc_SessHold(callingCalling.getDwSessId());*/

                if (callingCalling.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                    //如果已经有单呼，自己再发起单呼，挂断当前通话
                    onEnd(dwSessId);
                    CallingManager.getInstance().removeCallingInfo(dwSessId);
                    ToastUtils.showToast(MainActivity.this, "当前呼叫已经存在，请挂断后重试...");
                    return;
                } else//会议或者广播
                {
                    callingCalling.setHolding(true);
                    MtcCall.Mtc_SessHold(callingCalling.getDwSessId());
                    // 来电广播
                    Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, callinginfo.isOutgoing() ? true : false);
                    String callType = prefs.getString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                    if (callType.equals(GlobalConstant.CALLTYPE_VIDEO)) {
                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, true);
                    } else {
                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, false);
                    }
                    sendBroadcast(callingIncoming);
                    Log.e(TAG, "main     8489  :" + MediaEngine.GetInstance().ME_HasVideo(callinginfo.getDwSessId()));

                }
            }
            if (callingIntercom != null) {
				/*// 保持对讲
				callingIntercom.setHolding(true);
				MtcCall.Mtc_SessHold(callingIntercom.getDwSessId());*/

                //如果已经有对讲，自己再发起单呼，挂断对讲，自动接听呼叫
                onEnd(callingIntercom.getDwSessId());
                CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());
                // 发送自动接听广播
                Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callinginfo.getDwSessId());
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callinginfo.getCalltype());
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
                callingIncoming.putExtra(GlobalConstant.KEY_CALL_AUTOANSWER, callinginfo.isOutgoing() ? true : false);
                String callType = prefs.getString(GlobalConstant.CALLHASVIDEO, GlobalConstant.CALLTYPE_VOICE);
                if (callType.equals(GlobalConstant.CALLTYPE_VIDEO)) {
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, true);
                } else {
                    callingIncoming.putExtra(GlobalConstant.KEY_CALL_MEDIATYPE, false);
                }
                sendBroadcast(callingIncoming);
                Log.e(TAG, "main     8508 :" + callType);
            }
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 设置通话记录信息
        if (!exists) {
            callinginfo.setRemoteName(name);
            callinginfo.setCalltype(calltype);
            if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
                callinginfo
                        .setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
            } else {
                callinginfo
                        .setMediaType(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
            }
            callinginfo.setOutgoing(true);
            CallingManager.getInstance().addCallingInfo(callinginfo);
        }
        Log.e(TAG, "main  7902   calltype: " + calltype);
        // 发送广播
        Intent callingOutgoing = new Intent(GlobalConstant.ACTION_CALLING_OUTGOING);
        callingOutgoing.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingOutgoing.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingOutgoing.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingOutgoing);

        // 如果有视频则开始预览
        if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {   //mSessId 2015-05-07qq
            mtcCallDelegateStartPreview();
        }
        // 设置不延迟提供音视频
        mIsDelayOffer = false;

    }
    // 回铃提醒(适用于呼出-新方法SDK)

    // 回铃提醒(适用于呼出)
    @Override
    public void mtcCallDelegateAlerted(int dwSessId, int dwAlertType) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        Log.w("7395-main", "gengsonghang 电话通了");
        mCallState = GlobalConstant.CALL_STATE_ALERTED;
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        //String name=MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.e(TAG, "-----------mtcCallDelegateAlerted------------" + "dwSessId:" + dwSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].contains(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingAlerted = new Intent(
                GlobalConstant.ACTION_CALLING_ALERTED);
        callingAlerted.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingAlerted.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingAlerted.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingAlerted);

        // 开始弱电锁屏
        MtcProximity.start(this);
    }

    // 开始通话(适用于呼出、呼入)
    @Override
    public void mtcCallDelegateTalking(int dwSessId) {

        //设置默认摄像头为前置还是后置  1为前置，2为后置
        String cameraCode = prefs.getString(GlobalConstant.ACTION_GETCAMERA, "1");
        MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(Integer.valueOf(cameraCode));

        //获取配置界面语音发送增益，默认为1 无增益
        String voiceSendGain = prefs.getString("voiceSendGain", "1");
        //获取配置界面语音接收增益，默认为1 无增益
        String voiceReceiveGain = prefs.getString("voiceReceiveGain", "1");

        if (voiceSendGain.equals("0.5")) {
            //设置语音发送增益
            MediaEngine.GetInstance().ME_SetTxLevel(dwSessId, (float) 0.5);
        } else {
            //设置语音发送增益
            MediaEngine.GetInstance().ME_SetTxLevel(dwSessId, Integer.valueOf(voiceSendGain));
        }

        if (voiceReceiveGain.equals("0.5")) {
            //设置语音接收增益
            MediaEngine.GetInstance().ME_SetRxLevel(dwSessId, (float) 0.5);
        } else {
            //设置语音接收增益
            MediaEngine.GetInstance().ME_SetRxLevel(dwSessId, Integer.valueOf(voiceReceiveGain));
        }

        Log.e("增益", "接收 voiceReceiveGain : " + voiceReceiveGain + "\r\n" + "dwSessId : " + dwSessId);
        Log.e("增益", "发送 voiceSendGain : " + voiceSendGain + "\r\n" + "dwSessId : " + dwSessId);
        //视频解码模式设置
        String videoScalingMode = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSCALINGMODE, "1");
        MediaEngine.GetInstance().ME_SetVideoScalingMode(Integer.valueOf(videoScalingMode));

        //视频编码比例设置


        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }

        mSessId = dwSessId;

        Log.e(TAG, "gengsonghang 通话中");
        mCallState = GlobalConstant.CALL_STATE_TALKING;


//        AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
//        if (audioManager.isSpeakerphoneOn()) {
//            CloseSpeaker(audioManager);
//        }

//        // 有视频同时延迟提供
//        if (mIsDelayOffer && MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
//            // 开始预览
//           // mtcCallDelegateStartPreview();
//        } else if (MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
//            // 开始视频
//            mtcCallDelegateStartVideo(mSessId);
//        } else {
//            // 停止视频
//            mtcCallDelegateStopVideo(mSessId);
//        }

        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        Log.e(TAG, "mtcCallDelegateTalkingcallString: " + callString);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallDelegateTalking:" + name
                + ",dwSessId:" + mSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.e(TAG, "-----------mtcCallDelegateTalking------------" + "dwSessId:" + mSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].contains(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
            Log.e(TAG, "" + calltype);
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.contains(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingTalking = new Intent(
                GlobalConstant.ACTION_CALLING_TALKING);
        callingTalking.putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        callingTalking.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingTalking.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingTalking);
        Log.e(TAG, "发送TALKING广播");


    }

    // 通话挂断(适用于呼出、呼入)
    @Override
    public void mtcCallDelegateTermed(int dwSessId, int dwStatCode) {

        Log.e(TAG, "挂断dwSessId：" + dwSessId);

        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }

        Log.e(TAG, "-----------mtcCallDelegateTermed------------" + "dwSessId:" + dwSessId + "---当前mSessId:" + mSessId);
        //MtcDelegate.log("gengsonghang 挂断了");
        // 设置状态
        mCallState = GlobalConstant.CALL_STATE_NONE;
        // 关闭视频
        mtcCallDelegateStopVideo(dwSessId);
        // 清空会话
        mSessId = MtcCliConstants.INVALIDID;
        // 停止
        MtcProximity.stop();

//        // 取得对方名称及地址
//		/*MtcString ppcDispName = new MtcString();
//		MtcString ppcUri = new MtcString();
//		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
//		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
//		/*String n[]=MediaEngine.GetInstance().ME_GetRemoteFullUri(dwSessId).split("<");
//		String name=n[0].toString().replace("\"", "");*/
//        //String name=MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
//        //MtcDelegate.log("lizhiwei mtcCallDelegateTermed:" + name + ",dwSessId:" + dwSessId);

        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(dwSessId);

        Log.e(TAG, "callString" + callString);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
//		/*if("".equals(callString) || callString==null){
//			Intent callingTermed = new Intent(GlobalConstant.ACTION_CALLING_TERMED);
//			callingTermed.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
//			callingTermed.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
//			//callingTermed.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
//			sendBroadcast(callingTermed);
//			return;
//		}*/
        if (callString.length() < 2) {
            return;
        }
        String name = "";
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        name = name1[0];

        Log.e(TAG, "----mtcCallDelegateTermed----dwSessId:" + dwSessId + "---name:" + name);

        // 清除提示
        WaitingDialog dialog = CallingManager.getInstance().getCallingDialog(dwSessId);
        if (dialog != null) {
            MtcDelegate.log("lizhiwei 对讲通话结束 移除对话框");
            CallingManager.getInstance().removeCallingDialog(dialog.getDwSessId());
            dialog.dismiss();
            dialog = null;
        }
        Log.e(TAG, "mtcCallDelegateTermed通话挂断回调中    name:" + name);
        if (name == null || "".equals(name)) {//yuezs add
            Intent callingTermed = new Intent(GlobalConstant.ACTION_CALLING_TERMED);
            callingTermed.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
            callingTermed.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
            callingTermed.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
            sendBroadcast(callingTermed);
            return;
        }

        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!CommonMethod.hashMap.containsKey(dwSessId)) {
            return;
        }

        String type = CommonMethod.hashMap.get(dwSessId).getType();
        if (type == null
                || type.equals("")) {
            return;
        }
//        !name.contains("~")
//                || ((!name.startsWith("*9*"))
//                && (!GroupManager.getInstance().isGroupNumber(
//                (name.contains("*") ? name.substring(3) : name)
//                        .split("\\~")[0])) && (!(name
//                .contains("*") ? name.substring(3) : name)
//                .split("\\~")[1].equals(sipUser.getUsername())))
//                || ((!name.startsWith("*9*"))
//                && (!GroupManager.getInstance().isGroupNumber(
//                (name.contains("*") ? name.substring(3) : name)
//                        .split("\\~")[0])) && ((currMeeting == null)
//                || !(name.contains("*") ? name.substring(3) : name)
//                .split("\\~")[0].contains(currMeeting.getConferenceNo())))
        if (type.equals(GlobalConstant.INCOMINGCALLTYPE_SINGLE)
                || type.equals(GlobalConstant.INCOMINGCALLTYPE_SINGLE2)
//                || type.equals(GlobalConstant.INCOMINGCALLTYPE_TransferVideo)
//                || type.equals(GlobalConstant.INCOMINGCALLTYPE_TransferVideo2)
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
            String userNo = sipUser.getUsername();//MtcDelegate.getLoginedUser();
            // 更新通话时间
            SQLiteHelper sqLiteHelper = new SQLiteHelper(MainActivity.this);
            sqLiteHelper.open();
            //List<CallingRecords> cRecords = sqLiteHelper.getCallingRecordsByUserNO(userNo, 0, 1);
            List<CallingRecords> cRecords = sqLiteHelper.getCallingRecordsByUserNOAndSessId(userNo, dwSessId);
            if ((cRecords != null) && (cRecords.size() > 0)) {
                CallingRecords callingRecord = cRecords.get(0);
                Date nowDate = new Date();
                callingRecord.setStopDate(nowDate);
                // 如果开始时间等于应答时间，未通话
                if (callingRecord.getStartDate().toString().equals(callingRecord.getAnswerDate().toString())) {
                    callingRecord.setDuration(0);
                    callingRecord.setTime(FormatController.secToTime(0));
                } else {
                    int dur = FormatController.getSecondByDate(callingRecord.getAnswerDate(), callingRecord.getStopDate());
                    callingRecord.setDuration(dur);
                    callingRecord.setTime(FormatController.secToTime(dur));
                }

                sqLiteHelper.updateCallingRecords(callingRecord);
            }
            sqLiteHelper.closeclose();
        } else if (type.equals(GlobalConstant.INCOMINGCALLTYPE_VIDEOBUG)) {

            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {

            Log.e(TAG, "----------*********--------");

            // 对讲或会议
            boolean istempintercom = false;
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                if (name.startsWith("*9*")) {
                    istempintercom = true;
                }
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if (type.equals(GlobalConstant.INCOMINGCALLTYPE_MCUMEETING)
                    || type.equals(GlobalConstant.INCOMINGCALLTYPE_TEMPORARY)
                    || type.equals(GlobalConstant.INCOMINGCALLTYPE_BROADCAST)) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;

            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
                // 移除临时对讲
                if (istempintercom) {
                    GroupManager.getInstance().removeGroupInfo(name);
                    Log.e(TAG, "" + GroupManager.getInstance().getGroupCount());
                    // 发送广播通知对讲组变更
                    Intent intent = new Intent(GlobalConstant.ACTION_GROUPINFO);
                    sendBroadcast(intent);
                }
            }
        }

        // 发送广播
        Intent callingTermed = new Intent(GlobalConstant.ACTION_CALLING_TERMED);
        callingTermed.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingTermed.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        //callingTermed.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingTermed);

        AutoAnswerBena autoAnswerBena = CommonMethod.getInstance().getAutoAnswerBena();
        if (dwSessId == autoAnswerBena.getCallId()) {
            autoAnswerBena.setCallId(GlobalConstant.AUTOANSWER_INIT);
            autoAnswerBena.setIsAutoAnswer(GlobalConstant.AUTOANSWER_INIT);
            CommonMethod.getInstance().setAutoAnswerBena(autoAnswerBena);
        }

        if (CommonMethod.hashMap.containsKey(dwSessId)) {
            CommonMethod.hashMap.remove(dwSessId);
        }
    }

    // 开始视频预览(本地视频)
    @Override
    public void mtcCallDelegateStartPreview() {
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(mSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*5*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].contains(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingStartPreview = new Intent(
                GlobalConstant.ACTION_CALLING_STARTPREVIEW);
        callingStartPreview
                .putExtra(GlobalConstant.KEY_CALL_SESSIONID, mSessId);
        callingStartPreview.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingStartPreview.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingStartPreview);
    }

    // 本地图像尺寸
    @Override
    public void mtcCallDelegateCaptureSize(int dwSessId, int dwWidth,
                                           int dwHeight) {
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallDelegateCaptureSize:" + name
                + ",dwSessId:" + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
			/*
			 * // 设置远程视频尺寸 Point localSize =
			 * MtcVideo.calcLocalSize(fl_calling.getWidth(),
			 * fl_calling.getHeight(), fl_calling.getWidth(),
			 * fl_calling.getHeight()); MtcVideo.setViewSize(mRemoteView,
			 * localSize);
			 */
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingCaptureSize = new Intent(
                GlobalConstant.ACTION_CALLING_CAPTURESIZE);
        callingCaptureSize
                .putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingCaptureSize.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingCaptureSize.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        callingCaptureSize.putExtra(GlobalConstant.KEY_CALL_DWWIDTH, dwWidth);
        callingCaptureSize.putExtra(GlobalConstant.KEY_CALL_DWHEIGHT, dwHeight);
        sendBroadcast(callingCaptureSize);
    }

    // 开始远程视频
    @Override
    public void mtcCallDelegateStartVideo(int dwSessId) {
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallDelegateStartVideo:" + name
                + ",dwSessId:" + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
			/*
			 * // 清空原有视频 if (videos.size() > 0){ // 停止现有监控，切换至新的监控 for
			 * (MonitorInfo monitorinfo : videos){ if (monitorinfo == null){
			 * continue; } if (monitorinfo.getMultiplayer() != null){
			 * monitorinfo.getMultiplayer().stop();
			 * monitorinfo.getMultiplayer().close();
			 * monitorinfo.getMultiplayer().destroy(); } if
			 * (monitorinfo.getTimer() != null){ monitorinfo.getTimer().stop();
			 * monitorinfo.getTimer().setText(""); } } // 通知结束监控会议 if
			 * (videos.get(0).isIsmonitor()){ // 通话挂断 onEnd(); // 结束会议 Intent
			 * meetIntent = new
			 * Intent(GlobalConstant.ACTION_MONITOR_CALLHANGUP);
			 * sendBroadcast(meetIntent); } videos.clear(); DealVideoItmes(); }
			 * // 将页面设置到监控页面 // 隐藏地图 ll_position.setVisibility(View.GONE);
			 * ll_position.removeAllViews();
			 * rl_positiontools.setVisibility(View.GONE);
			 * ll_pos.setVisibility(View.GONE);
			 * ll_pos_single.setVisibility(View.GONE); // 隐藏单通视频
			 * fl_calling.setVisibility(View.VISIBLE); // 显示监控
			 * sl_videos.setVisibility(View.GONE); // 显示分屏
			 * ll_split.setVisibility(View.GONE);
			 * btn_info.setVisibility(View.GONE);
			 * btn_close.setVisibility(View.GONE);
			 * btn_pushvideo.setVisibility(View.GONE);
			 */

			/*
			 * // 开始远程视频 if (mRemoteView.getVisibility() != View.VISIBLE) {
			 * MtcVideo.startRemote(mSessId, 0);
			 * mRemoteView.setVisibility(View.VISIBLE); }
			 */
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingStartVideo = new Intent(
                GlobalConstant.ACTION_CALLING_STARTVIDEO);
        callingStartVideo.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingStartVideo.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingStartVideo.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingStartVideo);
    }

    // 远程视频尺寸
    @Override
    public void mtcCallDelegateVideoSize(int dwSessId, int dwWidth,
                                         int dwHeight, int iOrientation) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.w(TAG, "lizhiwei mtcCallDelegateVideoSize:" + name
                + ",dwSessId:" + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
			/*
			 * // 设置远程视频尺寸 MtcVideo.videoSize(dwSessId, dwWidth, dwHeight, 0,
			 * fl_calling.getWidth(), fl_calling.getHeight(), mRemoteView);
			 * mRemoteWidth = dwWidth; mRemoteHeight = dwHeight;
			 */
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        // 发送广播
        Intent callingVideoSize = new Intent(
                GlobalConstant.ACTION_CALLING_VIDEOSIZE);
        callingVideoSize.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingVideoSize.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingVideoSize.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        callingVideoSize.putExtra(GlobalConstant.KEY_CALL_DWWIDTH, dwWidth);
        callingVideoSize.putExtra(GlobalConstant.KEY_CALL_DWHEIGHT, dwHeight);
        sendBroadcast(callingVideoSize);
    }

    // 结束远程视频
    @Override
    public void mtcCallDelegateStopVideo(int dwSessId) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }

        Log.e(TAG, "dwSessId:" + dwSessId + "       mSessId:" + mSessId);

        if (dwSessId != mSessId) {
            return;
        }

        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallDelegateStopVideo:" + name
                + ",dwSessId:" + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;

        if (name == null || "".equals(name)) {
            Intent callingStopVideo = new Intent(
                    GlobalConstant.ACTION_CALLING_STOPVIDEO);
            callingStopVideo.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
            callingStopVideo.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
            //callingStopVideo.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
            sendBroadcast(callingStopVideo);
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
			/*
			 * // 停止预览 MtcCall.Mtc_SessPreviewShow(-1, false);
			 * mRemoteView.setVisibility(View.INVISIBLE);
			 *
			 * // 隐藏地图 ll_position.setVisibility(View.VISIBLE);
			 * rl_positiontools.setVisibility(View.VISIBLE);
			 * ll_pos.setVisibility(View.VISIBLE);
			 * ll_pos_single.setVisibility(View.VISIBLE); // 隐藏单通视频
			 * fl_calling.setVisibility(View.GONE); // 显示监控
			 * sl_videos.setVisibility(View.GONE); // 显示分屏
			 * ll_split.setVisibility(View.GONE);
			 * btn_info.setVisibility(View.GONE);
			 * btn_close.setVisibility(View.GONE);
			 * btn_pushvideo.setVisibility(View.GONE);
			 */
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
			/* String remoteno = callinfo[1];// 对方号码 */
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }
        Intent callingStopVideo = new Intent(
                GlobalConstant.ACTION_CALLING_STOPVIDEO);
        callingStopVideo.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        callingStopVideo.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        callingStopVideo.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(callingStopVideo);
    }


    @Override
    public void mtccallcbheld(int dwSessId) {
        Log.e(TAG, "-----------mtccallcbheld 被保持成功------------" + "dwSessId:" + dwSessId);
    }

    @Override
    public void mtcCallCbUnHeld(int dwSessId) {
        Log.e(TAG, "-----------mtcCallCbUnHeld 解除被保持成功------------" + "dwSessId:" + dwSessId);
    }

    // 保持成功
    @Override
    public void mtcCallCbHoldOk(int dwSessId) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallCbHoldOk:" + name + ",dwSessId:"
                + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.w(TAG, "-----------mtcCallCbHoldOk 保持成功------------" + "dwSessId:" + dwSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }

        CallingManager.getInstance().updateCallingHolding(dwSessId, true);

        Intent holdOkIntent = new Intent(
                GlobalConstant.ACTION_CALLING_CALLHOLDOK);
        holdOkIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        holdOkIntent.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        holdOkIntent.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(holdOkIntent);
    }

    // 保持失败
    @Override
    public void mtcCallCbHoldFailed(int dwSessId) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
		/*MtcString ppcDispName = new MtcString();
		MtcString ppcUri = new MtcString();
		MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
		String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);*/
        String callString = MediaEngine.GetInstance().ME_GetRemoteFullUri(mSessId);
        if ("".equals(callString) || callString == null)
            return;
        String names[] = callString.split("sip:");
        String name1[] = names[1].split("@");
        String name = name1[0];
        Log.e(TAG, "lizhiwei mtcCallCbHoldFailed:" + name + ",dwSessId:"
                + dwSessId);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.w(TAG, "-----------mtcCallCbHoldFailed 保持失败------------" + "dwSessId:" + dwSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }

        CallingManager.getInstance().updateCallingHolding(dwSessId, false);

        Intent holdFailedIntent = new Intent(
                GlobalConstant.ACTION_CALLING_CALLHOLDFAILED);
        holdFailedIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        holdFailedIntent.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        holdFailedIntent.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(holdFailedIntent);
    }

    // 解除保持
    @Override
    public void mtcCallCbUnHoldOk(int dwSessId) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
        MtcString ppcDispName = new MtcString();
        MtcString ppcUri = new MtcString();
        MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
        String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);
        MtcDelegate.log(TAG + name + ",dwSessId:"
                + dwSessId);

        CallingInfo callingIntercom = null;
        CallingInfo callingCalling = null;


        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.w(TAG, "-----------mtcCallCbUnHoldOk 解除保持成功------------" + "dwSessId:" + dwSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;

            // 如果有对讲或通话，会将原来的对讲或通话进行保持
            for (CallingInfo callingInfo : CallingManager.getInstance()
                    .getCallingData()) {
                if ((!callingInfo.isHolding())
                        && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07qq
                    if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                        callingIntercom = callingInfo;
                    } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                        callingCalling = callingInfo;
                    }
                }
            }

            if (callingIntercom != null && callingIntercom.getDwSessId() != dwSessId) {
                //如果已经有对讲，单呼解除保持，挂断对讲
                onEnd(callingIntercom.getDwSessId());
                CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());
            }

            if (callingCalling != null && callingCalling.getDwSessId() != dwSessId) {
                //如果当前已经有会议或者广播，保持会议或者广播
                callingCalling.setHolding(true);
                MtcCall.Mtc_SessHold(callingCalling.getDwSessId());
            }

        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;

                //如果解除保持的是会议或者广播，如果有单呼保持单呼，如果有对讲，结束对讲
                for (CallingInfo callingInfo : CallingManager.getInstance()
                        .getCallingData()) {
                    if ((!callingInfo.isHolding())
                            && (callingInfo.getDwSessId() != dwSessId)) {   //mSessId 2015-05-07qq
                        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
                            callingIntercom = callingInfo;
                        } else if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                            callingCalling = callingInfo;
                        }
                    }
                }

                if (callingIntercom != null && callingIntercom.getDwSessId() != dwSessId) {
                    //如果已经有对讲，会议解除保持，挂断对讲
                    onEnd(callingIntercom.getDwSessId());
                    CallingManager.getInstance().removeCallingInfo(callingIntercom.getDwSessId());
                }

                if (callingCalling != null && callingCalling.getDwSessId() != dwSessId) {
                    //如果当前单呼，保持单呼
                    callingCalling.setHolding(true);
                    MtcCall.Mtc_SessHold(callingCalling.getDwSessId());
                }
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }

        CallingManager.getInstance().updateCallingHolding(dwSessId, false);

        //如果当前麦克风处于关闭状态，则打开麦克风--yuezs 注释
		/*boolean mute = MtcCall.Mtc_SessGetMicMute(mSessId);
		if(mute){
			MtcCall.Mtc_SessSetMicMute(mSessId, !mute);
			Log.w("^^^^true解除保持成功后若麦克风关闭则打开麦克风^^^^", "^^^^解除保持成功后打开麦克风^^^^");
		}else{
			MtcCall.Mtc_SessSetMicMute(mSessId, mute);
			Log.w("^^^^false解除保持成功后打开麦克风^^^^", "^^^^解除保持成功后打开麦克风^^^^");
		} */

        Intent unHoldOkIntent = new Intent(
                GlobalConstant.ACTION_CALLING_CALLUNHOLDOK);
        unHoldOkIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        unHoldOkIntent.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        unHoldOkIntent.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(unHoldOkIntent);
    }

    @Override
    public void mtcCallCbUnHoldFailed(int dwSessId) {
        // TODO Auto-generated method stub
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        // 取得对方名称及地址
        MtcString ppcDispName = new MtcString();
        MtcString ppcUri = new MtcString();
        MtcCall.Mtc_SessGetPeerId(dwSessId, ppcDispName, ppcUri);
        String name = MtcUri.Mtc_UriGetName(ppcUri.getValue(), 32);
        // 判断呼叫类型
        int calltype = GlobalConstant.CALL_TYPE_CALLING;
        Log.e(TAG, "-----------mtcCallCbUnHoldFailed 解除保持失败------------" + "dwSessId:" + dwSessId + "---name:" + name);

        if (name == null || "".equals(name)) {
            return;
        }
        // 普通对讲和普通会议不带前缀，格式为对讲组号(或会议号)+~+创建者编号
        // *9*为临时对讲，格式为*9*+对讲组号+~+创建者编号
        // *5*为自己创建的临时会议，格式为*9*+会议编号+~+创建者编号
        // *7*为3G视频监控，pad不错处理
        // 会议接收方带有~，格式为会议编号+~+创建者编号，但被视为单呼
        SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        if (!name.contains("~")
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && (!(name
                .contains("*") ? name.substring(3) : name)
                .split("\\~")[1].equals(sipUser.getUsername())))
                || ((!name.startsWith("*9*"))
                && (!GroupManager.getInstance().isGroupNumber(
                (name.contains("*") ? name.substring(3) : name)
                        .split("\\~")[0])) && ((currMeeting == null)
                || !(name.contains("*") ? name.substring(3) : name)
                .split("\\~")[0].equals(currMeeting.getConferenceNo())))
                ) {
            // 不包含~，或者不是对讲组且不是自己创建的，则被认为是单呼，即会议成员
            // 或者不是对讲组且不是自己创建的会议，则被认为是单呼，即服务器会议会议成员
            // 单呼
            calltype = GlobalConstant.CALL_TYPE_CALLING;
        } else if (name.startsWith("*7*")) {
            return;
            // 3G监控
            //calltype = GlobalConstant.CALL_TYPE_MONITOR;
        } else {
            // 对讲或会议
            if ((name.startsWith("*9*")) || (name.startsWith("*5*"))) {
                // 临时对讲
                // 临时会议
                name = name.substring(3);
            }
            String[] callinfo = name.split("\\~");
            if (callinfo.length < 2) {
                return;
            }
            name = callinfo[0];// 组号或会议号
            // 判断是否为会议
            if ((!GroupManager.getInstance().isGroupNumber(name))
                    && (currMeeting != null)
                    && (name.equals(currMeeting.getConferenceNo()))) {
                // 会议
                calltype = GlobalConstant.CALL_TYPE_MEETING;
            } else {
                // 对讲
                calltype = GlobalConstant.CALL_TYPE_INTERCOM;
            }
        }

        CallingManager.getInstance().updateCallingHolding(dwSessId, true);

        Intent unHoldFailedIntent = new Intent(
                GlobalConstant.ACTION_CALLING_CALLUNHOLDFAILED);
        unHoldFailedIntent
                .putExtra(GlobalConstant.KEY_CALL_SESSIONID, dwSessId);
        unHoldFailedIntent.putExtra(GlobalConstant.KEY_CALL_TYPE, calltype);
        unHoldFailedIntent.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, name);
        sendBroadcast(unHoldFailedIntent);
    }

    // 网络状态改变
    @Override
    public void mtcCallDelegateNetStaChanged(int dwSessId, boolean bVideo,
                                             boolean bSend, int iType, int iReason) {
        Log.e(TAG, "-----------mtcCallDelegateNetStaChanged 网络状态改变------------" + "dwSessId:" + dwSessId);
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
    }

    // 通话状态改变
    @Override
    public void mtcCallDelegateMdfyed(int dwSessId) {
        if (dwSessId == MtcCliConstants.INVALIDID) {
            return;
        }
        if (MediaEngine.GetInstance().ME_HasVideo(dwSessId)) {
            mtcCallDelegateStartVideo(dwSessId);
        }
    }

    // 语音呼叫(适用于呼出)
    public void onVoiceCall(String callUserNo) {
        if (!ptt_3g_PadApplication.isNetConnection()) {
            ToastUtils.showToast(MainActivity.this,
                    getString(R.string.info_network_unavailable));
            return;
        }

        //为了防止发起呼叫过于频繁
        if (ptt_3g_PadApplication != null) {
            if (ptt_3g_PadApplication.isWetherClickCall() == false) {
                ptt_3g_PadApplication.setWetherClickCall(true);
                boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
                if (wetherTimerWorking == true) {
                    ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                    //返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
                    return;
                } else {

                    //返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
                    ptt_3g_PadApplication.startTimer();
                }
            } else {
                ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                return;
            }
        }

        Log.e(TAG, "main  进入通话呼叫   9308");
        //如果当前已经有呼叫，弹框提示是否继续
        CallingInfo callingCalling = null;
        CallingInfo callingMeeting = null;
        for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
            if ((!callingInfo.isHolding())) {
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                    callingCalling = callingInfo;
                }
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                    callingMeeting = callingInfo;
                }
            }
        }
        if (callingCalling != null) {
            showmainIfContinue(callingCalling, callUserNo, false);
            return;
        }
        if (callingMeeting != null) {
            showmainIfContinue(callingMeeting, callUserNo, false);
            return;
        }

        mtcCallDelegateCall(null, callUserNo, false);
        //yuezs add 2017-09-26
        //MediaEngine.GetInstance().ME_MakeCall(callUserNo, false);
    }

    // 视频呼叫(适用于呼出)
    public void onVideoCall(String callUserNo) {
        if (!ptt_3g_PadApplication.isNetConnection()) {
            ToastUtils.showToast(MainActivity.this,
                    getString(R.string.info_network_unavailable));
            return;
        }

        //为了防止发起呼叫过于频繁
        if (ptt_3g_PadApplication != null) {
            if (ptt_3g_PadApplication.isWetherClickCall() == false) {
                ptt_3g_PadApplication.setWetherClickCall(true);
                boolean wetherTimerWorking = ptt_3g_PadApplication.wetherTimerWorking();
                if (wetherTimerWorking == true) {
                    ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                    //返回值为true说明之前已经存在发起呼叫，并且之前的呼叫并没有响应，需要等到呼叫响应或者大于5秒才能再次发起
                    return;
                } else {
                    //返回值为false说明之前并没有发起呼叫或者发起的呼叫没有响应已经大于5秒
                    ptt_3g_PadApplication.startTimer();
                }
            } else {
                ToastUtils.showToast(MainActivity.this, getString(R.string.callquicktimer));
                return;
            }
        }
        Log.e(TAG, "main  9363");
        //如果当前已经有单呼，禁止发起新的单呼
        CallingInfo callingCalling = null;
        CallingInfo callingMeeting = null;
        for (CallingInfo callingInfo : CallingManager.getInstance().getCallingData()) {
            if ((!callingInfo.isHolding())) {
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_CALLING) {
                    Log.e(TAG, "main  9370");
                    callingCalling = callingInfo;
                }
                if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING) {
                    Log.e(TAG, "main  9374");
                    callingMeeting = callingInfo;
                }
            }
        }
        if (callingCalling != null) {
            showmainIfContinue(callingCalling, callUserNo, true);
            Log.e(TAG, "main  9381   callingCalling != null");
            return;
        }
        if (callingMeeting != null) {
            showmainIfContinue(callingMeeting, callUserNo, true);
            Log.e(TAG, "main  9386   callingMeeting != null");
            return;
        }
        Log.e(TAG, "main  9389");
        mtcCallDelegateCall(null, callUserNo, true);
    }

    // 音频回复(适用于呼入)
    public void onVoiceAnswer(int mSessId) {
        // 更新通话状态
        mCallState = GlobalConstant.CALL_STATE_ANSWERING;

        // 音频回复
        if (!MtcCall.Mtc_SessAnswer(mSessId, false)) {
            mtcCallDelegateTermed(mSessId, -1);
        }
    }

    // 视频回复(适用于呼入)
    public void onVideoAnswer(int mSessId) {
        // 更新通话状态
        mCallState = GlobalConstant.CALL_STATE_ANSWERING;
        Log.e(TAG, "视频回复");
        // 视频回复
        if (!MtcCall.Mtc_SessAnswer(mSessId, true)) {
            mtcCallDelegateTermed(mSessId, -1);
        }
    }

    // 主动停止
    public void onDecline(int dwSessId) {
        MtcDelegate.log("lizhiwei 手动挂断onDecline(int dwSessId)");
        // 更新通话状态
        mCallState = GlobalConstant.CALL_STATE_DECLINING;
        int result = -1;
        // 停止Session
        result = MtcCall.Mtc_SessTerm(dwSessId,
                MtcCallConstants.EN_MTC_CALL_TERM_REASON_DECLINE, null);
        Log.w(TAG, "--------------手动忽略----------result:" + result);
        mtcCallDelegateTermed(dwSessId,
                MtcCallConstants.EN_MTC_CALL_TERM_REASON_DECLINE);
    }

    // 结束通话
    public void onEnd(int dwSessId) {

        if (ptt_3g_PadApplication.isNetConnection() == false) {
            ToastUtils.showToast(MainActivity.this, getString(R.string.info_network_unavailable));
            return;
        }
        Log.e(TAG, "挂断dwSessId:" + dwSessId);
        // 更新状态
        mCallState = GlobalConstant.CALL_STATE_ENDING;
        int result = -1;
        // 停止Session
        Log.e(TAG, "--------------手动挂断-------dwSessId:" + dwSessId + "---result:" + result);
        // 停止通话
        mtcCallDelegateTermed(dwSessId, MtcCallConstants.EN_MTC_CALL_TERM_REASON_NORMAL);
        MediaEngine.GetInstance().ME_Hangup(dwSessId);
    }

	/*
	 * //通话保持 public void onHold(){ MtcCall.Mtc_SessHold(mSessId); }
	 */

    // 通话保持
    public void onHold(int dwSessId) {
        Log.e(TAG + dwSessId, "dwSwssID:" + dwSessId);
        CallingManager.getInstance().updateCallingHolding(dwSessId, true);
        MtcCall.Mtc_SessHold(dwSessId);
    }

    // 解除保持
    public void onUnHold() {
        MtcCall.Mtc_SessUnhold(mSessId);
    }

    // 解除保持
    public void onUnHold(int dwSessId) {
        Log.e(TAG + dwSessId, "");
        CallingManager.getInstance().updateCallingHolding(dwSessId, false);
        MtcCall.Mtc_SessUnhold(dwSessId);
    }

    // 来电 停止播放
    public void onStopRing() {

        if (sMediaPlayer != null) {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
        if (audioManager != null) {
            audioManager = null;
        }

        if (sVibrator != null) {
            sVibrator.cancel();
            sVibrator = null;
        }

        //MtcRing.stop();
    }

    // 来电 播放铃声
    public void onStartRing(String fileName) {
        Log.e(TAG, "MainActivity_8942  播放铃声");
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }

        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        int mode = audioManager.getRingerMode();
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
            Log.e(TAG, "设置麦克风 main 9514  取消静音");
        }

        // 打开扬声器
        audioManager.setSpeakerphoneOn(true);
        int currVolume = ptt_3g_PadApplication.getCurrentVolume();
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                currVolume,
                AudioManager.STREAM_VOICE_CALL);
        Log.e(TAG, "测试扬声器 main    9520   true");
        switch (mode) {

            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (sVibrator == null)
                    sVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                sVibrator.vibrate(VIBRATE_PATTERN, 0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:

                if (sVibrator == null)
                    sVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                try {
                    int value = Settings.System.getInt(getContentResolver(), "vibrate_when_ringing");
                    if (value == 1) {
                        sVibrator.vibrate(VIBRATE_PATTERN, 0);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    sVibrator.vibrate(VIBRATE_PATTERN, 0);
                }

                if (sMediaPlayer == null) {
                    sMediaPlayer = new MediaPlayer();
                } else if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.stop();
                    sMediaPlayer.reset();
                }

                sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    //播放 assets/a2.mp3 音乐文件
                    AssetFileDescriptor fd = getAssets().openFd(fileName);
                    //sMediaPlayer = new MediaPlayer();
                    sMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                    fd.close();
                    sMediaPlayer.setLooping(true);
                    sMediaPlayer.prepare();


                    sMediaPlayer.setVolume(volume,
                            volume);
                    sMediaPlayer.start();

                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }


    }

    // 开始播放回铃音效(适用于呼出)
    public void onStartRingBack(String fileName) {

        //MtcRing.startRingBack(MainActivity.this, "ringback.wav");
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        } else if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.stop();

            sMediaPlayer.reset();
        }
        sMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }
        audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        try {
            //播放 assets/a2.mp3 音乐文件
            AssetFileDescriptor fd = getAssets().openFd(fileName);
            sMediaPlayer = new MediaPlayer();
            fd.close();
            sMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            sMediaPlayer.setLooping(true);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // 保持界面始终在屏幕上
    private void keepScreenOn(boolean show) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (show) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }
        getWindow().setAttributes(attrs);
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

    @Override
    public void mtcDelegateStateChanged(int state, int statCode) {
        // TODO Auto-generated method stub
		/*
		 * switch (state) { case MtcDelegate.STATE_LOGINED: // 设置状态
		 * imgUserStates.setImageResource(R.drawable.userstates); // 重新取得对讲组
		 * GroupManager.getInstance().clear(); GetGroupInfo(); break; case
		 * MtcDelegate.STATE_LOGINFAILED: // 清空通话列表 callingInfos.clear(); //
		 * 设置状态 imgUserStates.setImageResource(R.drawable.userstates1); // 通知退出
		 * Intent intent0 = new Intent(GlobalConstant.ACTION_FORCED_OFFLINE);
		 * sendBroadcast(intent0); // 重新登录 MtcDelegate.login(); break; case
		 * MtcDelegate.STATE_LOGOUTED: // 清空通话列表 callingInfos.clear(); // 设置状态
		 * imgUserStates.setImageResource(R.drawable.userstates1); // 通知退出
		 * Intent intent1 = new Intent(GlobalConstant.ACTION_FORCED_OFFLINE);
		 * sendBroadcast(intent1); // 重新登录 MtcDelegate.login(); break; case
		 * MtcDelegate.STATE_INIT: // 状态初始化中停止客户端服务
		 * getWindow().getDecorView().post(new Runnable() {
		 *
		 * @Override public void run() { // 登陆成功或者失败，结果都会上传到
		 * MtcCli.Mtc_CliStop(); } }); break; }
		 */
    }

    @Override
    public void mtcDelegateNetChanged(int net, int previousNet) {

        // TODO Auto-generated method stub
		/*
		 * switch (MtcDelegate.getState()) { case MtcDelegate.STATE_LOGINING: //
		 * 当前状态为登录状态，网络状态为可用，则继续登录 if (MtcDelegate.getNet() !=
		 * MtcCliConstants.MTC_ANET_UNAVAILABLE) { MtcDelegate.login(); } break;
		 * case MtcDelegate.STATE_LOGOUTING: // 如果登出中，则无操作 break; default:
		 * String s = null; // 网络不可用 if (net ==
		 * MtcCliConstants.MTC_ANET_UNAVAILABLE) { s =
		 * getString(R.string.info_network_unavailable);
		 * imgUserStates.setImageResource(R.drawable.userstates1); // 清空通话列表
		 * callingInfos.clear(); // 设置状态
		 * imgUserStates.setImageResource(R.drawable.userstates1); // 通知退出
		 * Intent intent1 = new Intent(GlobalConstant.ACTION_FORCED_OFFLINE);
		 * sendBroadcast(intent1); // 重新登录 MtcDelegate.login(); } else { //
		 * 网络状态良好 if (previousNet == MtcCliConstants.MTC_ANET_UNAVAILABLE) { s =
		 * getString(R.string.info_network_ok);
		 * imgUserStates.setImageResource(R.drawable.userstates1); } else { //
		 * 网络发生改变 s = getString(R.string.info_network_changed); } }
		 */
    }

    @Override
    public void mtcDelegateConnectionChanged() {
        // TODO Auto-generated method stub
        if (mSessId != MtcCliConstants.INVALIDID) {
            if (MediaEngine.GetInstance().ME_HasVideo(mSessId)) {
                //MtcCall.Mtc_SessUpdate(mSessId, true, true);
            } else {
                //MtcCall.Mtc_SessUpdate(mSessId, true, false);
            }
        }
    }

    @Override
    public void mtcCliCbReceiveUserMsgReq(String sPeerUri, String sBodyType,
                                          String sMsgBody) {
        // TODO Auto-generated method stub
    }

    // RingtoneManager.TYPE_NOTIFICATION; 通知声音
    // RingtoneManager.TYPE_ALARM; 警告
    // RingtoneManager.TYPE_RINGTONE; 铃声

    //播放短信铃声
    public void PlayRingTone() {
        if (musicTimer == null) {
            musicTimer = new Timer();
        }

        if (musicState) {
            startAlarm();
            musicState = false;
        }
        musicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                musicState = true;
            }
        }, 1000);

    }

    // 处理视频项目
    private void DealVideoItmes()
    {

        Log.e("测试会议切屏", "DealVideoItmes currNumColumn :" + currNumColumn);
        if (currNumColumn == NumColumns.TWO) {

            // 四分屏
//            sl_videos.setBackgroundColor(getResources().getColor(
//                    R.color.content_bg));
            sl_videos.setBackgroundColor(getResources().getColor(
                    R.color.black));
            sl_videos.setBackgroundResource(R.drawable.monitor_bg);
            ll_videos.removeAllViews();
            // 移除填充布局
            ArrayList<MonitorInfo> removes = new ArrayList<MonitorInfo>();
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getVedioPort() <= 0) {
                    removes.add(monitorinfo);
                }
            }
            videos.removeAll(removes);
            // 计算行数
            int linecount = (int) Math.ceil((double) videos.size() / 2);
            if (linecount < 2) {
                linecount = 2;
            }
            // 添加填充布局
            if (videos.size() < linecount * 2) {
                int count = linecount * 2 - videos.size();
                for (int i = 0; i < count; i++) {
                    // 创建填充好友
                    MonitorInfo monitorinfo = new MonitorInfo(
                            String.valueOf(-i), -i);
                    // 创建填充布局
                    MtcDelegate.log("lizhiwei 四分屏处理添加空视频CreateVideoItem");
                    Log.e(TAG, "2");
                    CreateVideoItem(monitorinfo);
                    // 添加填充布局
                    videos.add(monitorinfo);
                    Log.e("=========测试测试阿打算打算222","添加填充布局");
                }
            }
            // 按行添加
            for (int i = 0; i < linecount; i++) {
                // 创建行布局
                LinearLayout rowView = new LinearLayout(this);
                rowView.setBaselineAligned(false);
                rowView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, sl_videos
                        .getHeight() / 2));
                rowView.setOrientation(LinearLayout.HORIZONTAL);

                // 添加第一个项目
                // 需要移除原有布局
                ViewGroup viewgroup = null;
                if ((videos.get(i * 2) != null)
                        && (videos.get(i * 2).getView() != null)) {
                    viewgroup = (ViewGroup) videos.get(i * 2).getView()
                            .getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(videos.get(i * 2).getView());
                    }
                }
                // 取消选中布局
                videos.get(i * 2).setFocused(false);
                if (videos.get(i * 2).getView() == null) {
                    Log.e(TAG, "");
                    CreateVideoItem(videos.get(i * 2));
                }
                if (videos.get(i * 2).getView() != null) {
                    videos.get(i * 2).getView()
                            .setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(videos.get(i * 2).getView(),
                            currNumColumn);

                    LinearLayout videoView = new LinearLayout(this);
                    videoView.setBaselineAligned(false);
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(
                            sl_videos
                                    .getWidth() / 2, sl_videos
                            .getHeight() / 2));
                    videoView.addView(videos.get(i * 2).getView());

                    // 添加行布局
                    rowView.addView(videoView);
                    videos.get(i * 2).setmLinearLayot(videoView);

                }

                // 添加第二个项目
                // 需要移除原有布局
                if ((videos.get(i * 2 + 1) != null)
                        && (videos.get(i * 2 + 1).getView() != null)) {
                    viewgroup = (ViewGroup) videos.get(i * 2 + 1).getView()
                            .getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(videos.get(i * 2 + 1).getView());
                    }
                }
                // 取消选中布局
                videos.get(i * 2 + 1).setFocused(false);
                if (videos.get(i * 2 + 1).getView() == null) {
                    CreateVideoItem(videos.get(i * 2 + 1));
                }
                if (videos.get(i * 2 + 1).getView() != null) {
                    videos.get(i * 2 + 1).getView()
                            .setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(videos.get(i * 2 + 1).getView(),
                            currNumColumn);
                    LinearLayout videoView = new LinearLayout(this);
                    videoView.setBaselineAligned(false);
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(
                            sl_videos
                                    .getWidth() / 2, sl_videos
                            .getHeight() / 2));
                    videoView.addView(videos.get(i * 2 + 1).getView());
                    // 添加行布局
                    rowView.addView(videoView);
                    videos.get(i * 2 + 1).setmLinearLayot(videoView);

                }
                // 添加视图布局
                ll_videos.addView(rowView);
            }
             monitorInfoIndex = videos.size();
        } else if (currNumColumn == NumColumns.THREE) {
            // 九分屏
//            sl_videos.setBackgroundColor(getResources().getColor(
//                    R.color.content_bg));
            sl_videos.setBackgroundColor(getResources().getColor(
                    R.color.black));
            sl_videos.setBackgroundResource(R.drawable.monitor_bg);
            ll_videos.removeAllViews();
            // 移除填充布局
            ArrayList<MonitorInfo> removes = new ArrayList<MonitorInfo>();
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getVedioPort() <= 0) {
                    removes.add(monitorinfo);
                }
            }
            videos.removeAll(removes);
            // 计算行数
            int linecount = (int) Math.ceil((double) videos.size() / 3);
            if (linecount < 3) {
                linecount = 3;
            }
            // 添加填充布局
            if (videos.size() < linecount * 3) {
                int count = linecount * 3 - videos.size();
                for (int i = 0; i < count; i++) {
                    // 创建填充好友
                    MonitorInfo monitorinfo = new MonitorInfo(
                            String.valueOf(-i), 0);
                    // 创建填充布局
                    MtcDelegate.log("lizhiwei 3分屏处理添加空视频CreateVideoItem");
                    CreateVideoItem(monitorinfo);
                    // 添加填充布局
                    videos.add(monitorinfo);
                }
            }
            // 按行添加
            for (int i = 0; i < linecount; i++) {
                // 创建行布局
                LinearLayout rowView = new LinearLayout(this);
                rowView.setBaselineAligned(false);
                rowView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, sl_videos
                        .getHeight() / 3));
                rowView.setOrientation(LinearLayout.HORIZONTAL);

                // 添加第一个项目
                // 需要移除原有布局
                ViewGroup viewgroup = null;
                if ((videos.get(i * 3) != null)
                        && (videos.get(i * 3).getView() != null)) {
                    viewgroup = (ViewGroup) videos.get(i * 3).getView()
                            .getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(videos.get(i * 3).getView());
                    }
                }
                // 取消选中布局
                videos.get(i * 3).setFocused(false);
                if (videos.get(i * 3).getView() == null) {
                    CreateVideoItem(videos.get(i * 3));
                }
                if (videos.get(i * 3).getView() != null) {
                    videos.get(i * 3).getView()
                            .setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(videos.get(i * 3).getView(),
                            currNumColumn);
                    LinearLayout videoView = new LinearLayout(this);
                    videoView.setBaselineAligned(false);
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(
                            sl_videos
                                    .getWidth() / 3, sl_videos
                            .getHeight() / 3));
                    videoView.addView(videos.get(i * 3).getView());


                    // 添加行布局
                    rowView.addView(videoView);
                    videos.get(i * 3).setmLinearLayot(videoView);

                }

                // 添加第二个项目
                // 需要移除原有布局
                if ((videos.get(i * 3 + 1) != null)
                        && (videos.get(i * 3 + 1).getView() != null)) {
                    viewgroup = (ViewGroup) videos.get(i * 3 + 1).getView()
                            .getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(videos.get(i * 3 + 1).getView());
                    }
                }
                // 取消选中布局
                videos.get(i * 3 + 1).setFocused(false);
                if (videos.get(i * 3 + 1).getView() == null) {
                    CreateVideoItem(videos.get(i * 3 + 1));
                }
                if (videos.get(i * 3 + 1).getView() != null) {
                    videos.get(i * 3 + 1).getView()
                            .setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(videos.get(i * 3 + 1).getView(),
                            currNumColumn);
                    LinearLayout videoView = new LinearLayout(this);
                    videoView.setBaselineAligned(false);
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(
                            sl_videos
                                    .getWidth() / 3, sl_videos
                            .getHeight() / 3));
                    videoView.addView(videos.get(i * 3 + 1).getView());

                    // 添加行布局
                    rowView.addView(videoView);
                    videos.get(i * 3 + 1).setmLinearLayot(videoView);

                }

                // 添加第三个项目
                // 需要移除原有布局
                if ((videos.get(i * 3 + 2) != null)
                        && (videos.get(i * 3 + 2).getView() != null)) {
                    viewgroup = (ViewGroup) videos.get(i * 3 + 2).getView()
                            .getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(videos.get(i * 3 + 2).getView());
                    }
                }
                // 取消选中布局`
                videos.get(i * 3 + 2).setFocused(false);
                if (videos.get(i * 3 + 2).getView() == null) {
                    CreateVideoItem(videos.get(i * 3 + 2));
                }
                if (videos.get(i * 3 + 2).getView() != null) {
                    videos.get(i * 3 + 2).getView()
                            .setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(videos.get(i * 3 + 2).getView(),
                            currNumColumn);
                    LinearLayout videoView = new LinearLayout(this);
                    videoView.setBaselineAligned(false);
                    videoView.setLayoutParams(new LinearLayout.LayoutParams(
                            sl_videos
                                    .getWidth() / 3, sl_videos
                            .getHeight() / 3));
                    videoView.addView(videos.get(i * 3 + 2).getView());

                    // 添加行布局
                    rowView.addView(videoView);
                    videos.get(i * 3 + 2).setmLinearLayot(videoView);
                }

                // 添加视图布局
                ll_videos.addView(rowView);
            }
             monitorInfoIndex = videos.size();
        } else {
            // 一分屏
            ll_videos.removeAllViews();
            // 移除填充布局
            ArrayList<MonitorInfo> removes = new ArrayList<MonitorInfo>();
            for (MonitorInfo monitorinfo : videos) {
                if (monitorinfo.getVedioPort() <= 0) {
                    removes.add(monitorinfo);
                }
            }

            videos.removeAll(removes);
            // 添加填充布局
            if (videos.size() <= 0) {
                sl_videos.setBackgroundResource(R.drawable.monitor_bg);
                // 创建填充好友
                MonitorInfo monitorinfo = new MonitorInfo("0", 0);
                // 创建填充布局
                CreateVideoItem(monitorinfo);
                // 添加填充布局
                videos.add(monitorinfo);
            } else {
//                sl_videos.setBackgroundColor(getResources().getColor(
//                        R.color.content_bg));
                sl_videos.setBackgroundColor(getResources().getColor(
                        R.color.black));
                sl_videos.setBackgroundResource(R.drawable.monitor_bg);
            }
            for (MonitorInfo monitorinfo : videos) {
                // 创建行布局
                LinearLayout rowView = new LinearLayout(this);
                rowView.setBaselineAligned(false);
                rowView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, sl_videos
                        .getHeight()));
                rowView.setOrientation(LinearLayout.HORIZONTAL);
                // 需要移除原有布局
                ViewGroup viewgroup = null;
                if ((monitorinfo != null) && (monitorinfo.getView() != null)) {
                    viewgroup = (ViewGroup) monitorinfo.getView().getParent();
                    if (viewgroup != null) {
                        viewgroup.removeView(monitorinfo.getView());
                    }
                }
                // 取消选中布局
                monitorinfo.setFocused(false);
                if (monitorinfo.getView() == null) {
                    CreateVideoItem(monitorinfo);
                }
                if (monitorinfo.getView() != null) {
                    monitorinfo.getView().setBackgroundColor(Color.TRANSPARENT);
                    // 设置显示信息
                    DealMonitorInfoView(monitorinfo.getView(), currNumColumn);
                    // 添加行布局
                    rowView.addView(monitorinfo.getView());
                    monitorinfo.setIndex(0);

                }
                // 添加视图布局
                ll_videos.addView(rowView);
            }
            monitorInfoIndex = videos.size();
        }

        monitorInfoList.clear();
        monitorInfoList.addAll(videos);
    }

    // 添加视频项目
    private void DealVideoItemsAdd()
    {
        Log.e("啊啊啊啊啊啊2222","DealVideoItemsAdd" );

                MonitorInfo monitorInfo = videos.get(videos.size() - 1);
                Log.e("*/*/*/*/*/*/*/**","size():" + monitorInfoList.size());
                for (int i = 0; i < monitorInfoList.size(); i++){
                 if (monitorInfoList.get(i).getNumber() == monitorInfo.getNumber()){
                     LinearLayout linearLayout = monitorInfoList.get(i).getmLinearLayot();

                //LinearLayout linearLayout = monitorInfo.getmLinearLayot();
                if (linearLayout == null){
                    Log.e("*/*/*/*/*/*/*/**","return ");
                    return;
                }
                     Log.e("*/*/*/*/*/*/*/**","No return ");
                linearLayout.removeAllViews();
                ViewGroup parent = (ViewGroup) monitorInfo.getView().getParent();
                if (parent != null){
                    parent.removeAllViews();
                }
                linearLayout.addView(monitorInfo.getView());
                DealMonitorInfoView(monitorInfo.getView(),currNumColumn);
                monitorInfo.setFocused(false);
                break;
            }
        }



    }

    // 删除视频项目
    private void DealVideoItemsRemove()
    {
        Log.e("啊啊啊啊啊啊2222","DealVideoItemsRemove" );
        String number = prefs.getString("number", "0");
        if (number.contains("-")){
            toastContent = "请选择目标";
            decoderHandler.sendEmptyMessage(6);
            return;
        }
        if (number.equals("0")){
            toastContent = "请选择目标";
            decoderHandler.sendEmptyMessage(6);
            return;
        }

        //TODO 1 遍历videos集合 找到需要进行remove操作的MonitorInfo对象
        //TODO 2 通过当前遍历的索引可以通过videoInfoList集合得到LinearLayout对象
        //TODO 3 通过得到的LinearLayout进行View的移除及填充新的空布局
        Log.e("========AAAA=====","monitorInfoList.size() ：" + monitorInfoList.size());

        for (int i = 0; i < monitorInfoList.size(); i++){
            monitorInfoIndex++;
            if (monitorInfoList.get(i).isFocused()) {

                if (number.equals(monitorInfoList.get(i).getNumber())) {
                    Log.e("========AAAA=====", "monitorInfoList.isFocused() ：" + monitorInfoList.get(i).isFocused() + "\r\n" + "i : " + i + "\r\n" + "index :" + monitorInfoIndex);
                    LinearLayout linearLayout = monitorInfoList.get(i).getmLinearLayot();

                    linearLayout.removeAllViews();
                    // 添加填充布局
                    monitorInfoList.get(i).setInmonitoring(false);
                    // 创建填充好友
                    MonitorInfo monitorinfo = new MonitorInfo(
                            String.valueOf(-monitorInfoIndex), -monitorInfoIndex);

                    // 发送结束视频
                    if (monitorInfoList.get(i).isGetVideo()) {
                        ReleaseVideo(monitorInfoList.get(i));
                    } else {
                        MonitorEnd(monitorInfoList.get(i).getName(), monitorInfoList.get(i).getSid(), monitorInfoList.get(i).getCid(), monitorInfoList.get(i).getNumber());

                    }
                    monitorinfo.setmLinearLayot(monitorInfoList.get(i).getmLinearLayot());
                    videos.remove(monitorInfoList.get(i));
                    monitorInfoList.remove(monitorInfoList.get(i));

                    // 创建填充布局
                    MtcDelegate.log("lizhiwei 四分屏处理添加空视频CreateVideoItem");
                    Log.e(TAG, "2");
                    CreateVideoItem(monitorinfo);
                    videos.add(i, monitorinfo);
                    // 设置显示信息
                    DealMonitorInfoView(monitorinfo.getView(),
                            currNumColumn);
                    linearLayout.addView(monitorinfo.getView());
                    monitorInfoList.add(i, monitorinfo);
                    break;
                }
            }
        }
    }


    // 依据分屏设置显示信息布局
    private void DealMonitorInfoView(View view, NumColumns currNumColumn) {
        TableLayout tl_title = (TableLayout) view.findViewById(R.id.tl_title);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_namevl = (TextView) view.findViewById(R.id.tv_namevl);
        TextView tv_no = (TextView) view.findViewById(R.id.tv_no);
        TextView tv_novl = (TextView) view.findViewById(R.id.tv_novl);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
        TextView tv_datevl = (TextView) view.findViewById(R.id.tv_datevl);
        TextView tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        Chronometer ch_durationvl = (Chronometer) view
                .findViewById(R.id.ch_durationvl);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tl_title
                .getLayoutParams();
        if (currNumColumn == NumColumns.TWO) {
            // 四分屏
            params.width = CommonMethod.dip2px(this, 180) / 2
                    + CommonMethod.dip2px(this, 10);
            tl_title.setPadding(2, 2, 2, 2);
            tv_name.setTextSize(14);
            tv_namevl.setTextSize(14);
            tv_no.setTextSize(14);
            tv_novl.setTextSize(14);
            tv_date.setTextSize(14);
            tv_datevl.setTextSize(14);
            tv_duration.setTextSize(14);
            ch_durationvl.setTextSize(14);
        } else if (currNumColumn == NumColumns.THREE) {
            // 九分屏
            params.width = CommonMethod.dip2px(this, 180) / 3
                    + CommonMethod.dip2px(this, 10);
            tl_title.setPadding(1, 1, 1, 1);
            tv_name.setTextSize(10);
            tv_namevl.setTextSize(10);
            tv_no.setTextSize(10);
            tv_novl.setTextSize(10);
            tv_date.setTextSize(10);
            tv_datevl.setTextSize(10);
            tv_duration.setTextSize(10);
            ch_durationvl.setTextSize(10);
        } else {
            // 一分屏
            params.width = CommonMethod.dip2px(this, 180);
            tl_title.setPadding(5, 5, 5, 5);
            tv_name.setTextSize(18);
            tv_namevl.setTextSize(18);
            tv_no.setTextSize(18);
            tv_novl.setTextSize(18);
            tv_date.setTextSize(18);
            tv_datevl.setTextSize(18);
            tv_duration.setTextSize(18);
            ch_durationvl.setTextSize(18);
        }
        tl_title.setLayoutParams(params);
    }

    // 添加视频项目
    private void AddVideoItem(MonitorInfo currMonitorinfo, boolean iscalling) {


        boolean exists = false;
        for (MonitorInfo monitorinfo : videos) {
            if (currMonitorinfo.getNumber() == monitorinfo.getNumber()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            Log.e("*/*/*/*/*/*/*/**","ting le");
            return;
        }




        // 移除填充布局
        ArrayList<MonitorInfo> removes = new ArrayList<MonitorInfo>();
        for (MonitorInfo monitorinfo : videos) {
            if (monitorinfo.getVedioPort() <= 0) {
                removes.add(monitorinfo);
            }
        }
        videos.removeAll(removes);
        // 创建布局
        Log.e(TAG, "1");



        //CreateVideoItem(currMonitorinfo);
        // 处理布局
        MtcDelegate.log("lizhiwei 添加视频设置视频布局DealVideoItmes");

        if (!ptt_3g_PadApplication.getCurrentSpliCount().equals("One")){

            for (MonitorInfo monitorInfo : monitorInfoList){
                if (monitorInfo.getNumber().equals(currMonitorinfo.getNumber())){
                    CreateVideoItem(monitorInfo);
                    Log.e("*/*/*/*/*/*/*/**","添加完成 " + monitorInfo.getNumber());
                    DealVideoItemsAdd();
                    break;
                }
            }




            return;
        }else {
            CreateVideoItem(currMonitorinfo);
            DealVideoItmes();

        }

    }

    // 创建视频布局
    private void CreateVideoItem(MonitorInfo monitorinfo) {

        // 创建布局
        LayoutInflater flater = LayoutInflater.from(this);

        mItemView = flater.inflate(R.layout.layout_meeting_video_item,
                null);

		/*
		 * View mItemView = View.inflate(this,
		 * R.layout.layout_meeting_video_item, null);
		 */


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        param.weight = 1;
        mItemView.setLayoutParams(param);
        mItemView.setTag(monitorinfo);
        mItemView.setOnClickListener(new ItemClickListener());

        // 设置透明度
        TableLayout tl_title = (TableLayout) mItemView
                .findViewById(R.id.tl_title);
        tl_title.getBackground().setAlpha(230);
        // 设置信息
        TextView tv_name = (TextView) mItemView.findViewById(R.id.tv_name);
        TextView tv_namevl = (TextView) mItemView.findViewById(R.id.tv_namevl);
        TextView tv_no = (TextView) mItemView.findViewById(R.id.tv_no);
        TextView tv_novl = (TextView) mItemView.findViewById(R.id.tv_novl);
        TextView tv_date = (TextView) mItemView.findViewById(R.id.tv_date);
        TextView tv_datevl = (TextView) mItemView.findViewById(R.id.tv_datevl);
        TextView tv_duration = (TextView) mItemView
                .findViewById(R.id.tv_duration);
        Chronometer ch_durationvl = (Chronometer) mItemView
                .findViewById(R.id.ch_durationvl);
        tv_name.setTypeface(CommonMethod.getTypeface(this));
        tv_namevl.setTypeface(CommonMethod.getTypeface(this));
        tv_no.setTypeface(CommonMethod.getTypeface(this));
        tv_novl.setTypeface(CommonMethod.getTypeface(this));
        tv_date.setTypeface(CommonMethod.getTypeface(this));
        tv_datevl.setTypeface(CommonMethod.getTypeface(this));
        tv_duration.setTypeface(CommonMethod.getTypeface(this));
        ch_durationvl.setTypeface(CommonMethod.getTypeface(this));

        if (monitorinfo.getVedioPort() > 0) {

            if (!ptt_3g_PadApplication.isBaiduMapisRemove()) {
//                if (mMapView != null){
//                    mMapView.onDestroy();
//                }
//                ll_position.setVisibility(View.GONE);
//                ll_position.removeView(mMapView);
//                ptt_3g_PadApplication.setBaiduMapisRemove(true);
                Intent intentS = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
                intentS.putExtra("AddOrRemove", "Remove");
                sendBroadcast(intentS);
            }


            FrameLayout rl_video = (FrameLayout) mItemView
                    .findViewById(R.id.rl_video);
            rl_video.setBackgroundColor(Color.BLACK);
            //rl_video.setBackgroundResource(R.drawable.monitor_bg2);
            Log.e(TAG, "执行  AA");
            // SurfaceView surfaceView = new SurfaceView(this);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            boolean isStartPMD = prefs.getBoolean("isStartPMD", true);
            linearLayout.removeAllViews();
            if (isStartPMD) {
                TextView textView = new TextView(this);
                textView.setText("名称:" + monitorinfo.getName() + "    " + "号码:" + monitorinfo.getNumber());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textView.setGravity(Gravity.CENTER);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.setHorizontallyScrolling(true);
                textView.setSingleLine(true);
                linearLayout.addView(textView);
            }

            //SurfaceView surfaceView = ViERenderer.CreateLocalRenderer(this);
            SurfaceView surfaceView = new SurfaceView(this);
            //surfaceView.setZOrderOnTop(true);      // 这句不能少
            surfaceView.setZOrderMediaOverlay(true);
            surfaceView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            surfaceView.setTag(monitorinfo);
            surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
            //surfaceView.setBackgroundResource(R.drawable.monitor_bg2);

            // linearLayout.addView(textView);
            linearLayout.addView(surfaceView);
            rl_video.removeAllViews();
            rl_video.addView(linearLayout);


            Log.e(TAG, "cid : " + monitorinfo.getCid() + "\r\n" + "number : " + monitorinfo.getNumber());
            Log.e(TAG, "执行");
            Log.e(TAG, "ME_StartReceiveVideo  会议222  cid : " + monitorinfo.getCid() + "\r\n" + "number : " + monitorinfo.getNumber());
            if (monitorinfo.isGetVideo()) {

                MonitorInfoBean monitorInfoBean1 = new MonitorInfoBean();
                monitorInfoBean1.setNumber(monitorinfo.getNumber());
                monitorInfoBean1.setSurfaceView(surfaceView);
                monitorInfoBean1.setMonitor(false);
                monitorInfoBean1.setName(monitorinfo.getName());
                monitorInfoBean1.setCid(monitorinfo.getCid());
                monitorMap.put(monitorinfo.getNumber(), monitorInfoBean1);

                //当前为会议获取成员视频
                boolean result = MediaEngine.GetInstance().ME_StartReceiveVideo(monitorinfo.getCid(), monitorinfo.getNumber(), surfaceView, new MediaEngine.ME_StartReceiveVideo_callback() {
                    @Override
                    public void onCallBack(String number, String cid, SurfaceView surfaceView) {
                        Log.e(TAG, "ME_StartReceiveVideo  会议  cid : " + cid + "\r\n" + "number : " + number);

                        if (monitorMap.containsKey(number)) {
                            MonitorInfoBean monitorInfoBean1 = monitorMap.get(number);
                            monitorInfoBean1.setCid(cid);
                            monitorMap.put(number, monitorInfoBean1);
                        }

                        monitorsSessionID.put(number, cid);
                    }
                });

                if (!result) {
                    //失败处理
                    Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_GETVIDEO);
                    intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                    intent.putExtra(GlobalConstant.KEY_MONITOR_ERROR, "");// 监控获取视频错误号
                    intent.putExtra(GlobalConstant.KEY_MONITOR_DIS, "");// 监控获取视频错误描述
                    intent.putExtra(GlobalConstant.KEY_MEETING_CID, "");// 会议唯一标识
                    intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, monitorinfo.getNumber());// 请求用户编号
                    sendBroadcast(intent);
                }
            } else {

                //当前为获取监控方式
                MonitorInfoBean monitorInfoBean = new MonitorInfoBean();
                monitorInfoBean.setNumber(monitorinfo.getNumber());
                monitorInfoBean.setSurfaceView(surfaceView);
                monitorInfoBean.setMonitor(true);
                monitorInfoBean.setName(monitorinfo.getName());
                monitorMap.put(monitorinfo.getNumber(), monitorInfoBean);
                Log.e(TAG, "发起监控   number : " + monitorinfo.getNumber());
                boolean result = MediaEngine.GetInstance().ME_StartReceiveVideo(monitorinfo.getNumber(), monitorInfoBean.getSurfaceView(), new MediaEngine.ME_StartReceiveVideo_callback() {
                    @Override
                    public void onCallBack(String number, String cid, SurfaceView surfaceView) {
                        Log.e(TAG, "ME_StartReceiveVideo  监控  cid : " + cid + "\r\n" + "number : " + number);
                        if (monitorMap.containsKey(number)) {
                            MonitorInfoBean monitorInfoBean1 = monitorMap.get(number);
                            monitorInfoBean1.setCid(cid);
                            monitorMap.put(number, monitorInfoBean1);
                        }

                        monitorsSessionID.put(number, cid);
                        Log.e(TAG, "开始监控  number : " + number);
                    }
                });
                //如果操作失败
                if (!result) {
                    Log.e(TAG, "发起监控   失败 : ");
                    Intent intent = new Intent(GlobalConstant.ACTION_MONITOR_VBUG_START);
                    intent.putExtra(GlobalConstant.KEY_MEETING_RESULT, false);
                    intent.putExtra(GlobalConstant.KEY_MEETING_EMPLOYEEID, monitorinfo.getNumber());// 监控号码
                    sendBroadcast(intent);
                }
            }
            Calendar calendar = Calendar.getInstance();
            monitorinfo.setStartDate(calendar.getTime());
            tv_namevl.setText(monitorinfo.getName());
            tv_novl.setText(monitorinfo.getNumber());

            tv_datevl.setText(CommonMethod.getCurrShortMMdd(calendar));
            ch_durationvl.setBase(SystemClock.elapsedRealtime());
            ch_durationvl.start();
        }
        // 设置布局
        monitorinfo.setView(mItemView);
    }

    // 删除视频项目
    private void DelVideoItem() {


        if (!ptt_3g_PadApplication.getCurrentSpliCount().equals("One")){
            DealVideoItemsRemove();
            return;

        }



        MonitorInfo monitorinfo = null;
        for (MonitorInfo monitor : videos) {
            if (monitor.isFocused()) {
                monitorinfo = monitor;
                break;
            }
        }
        if (monitorinfo == null) {
            return;
        }
        // 设置状态
        monitorinfo.setInmonitoring(false);
        // 移除监控组
        videos.remove(monitorinfo);

//        for (int i = 0; i < monitorInfoList.size(); i++){
//            MonitorInfo monitorInfo = monitorInfoList.get(i);
//            if (monitorInfo.getNumber().equals(monitorinfo.getNumber())){
//                // 创建填充好友
//                MonitorInfo monitorinfo2 = new MonitorInfo(
//                        String.valueOf(-i), -i);
//                monitorinfo2.setView(monitorInfo.getView());
//                monitorInfoList.remove(monitorInfo);
//                monitorInfoList.add(i,monitorinfo2);
//            }
//        }

        Log.e(TAG, "videos:" + videos.size());
        // 结束视频
        if (monitorinfo.getMultiplayer() != null) {
            monitorinfo.getMultiplayer().stop();
            monitorinfo.getMultiplayer().close();
            monitorinfo.getMultiplayer().destroy();
        }
        if (monitorinfo.getTimer() != null) {
            monitorinfo.getTimer().stop();
            monitorinfo.getTimer().setText("");
        }

        // 发送结束视频
        if (monitorinfo.isGetVideo()) {
            ReleaseVideo(monitorinfo);
        } else {
            MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());

        }
        Log.e(TAG, "对方ID：" + monitorinfo.getId());
        Log.e(TAG, "对方CID：" + monitorinfo.getCid());
        Log.e(TAG, "对方userName：" + monitorinfo.getNumber());

//        //如果是监控，则发送结束监控
////        if (monitorinfo.isIsmonitor()) {
////            MonitorEnd(monitorinfo.getName(), monitorinfo.getSid(), monitorinfo.getCid(), monitorinfo.getNumber());
////        }
//		/*
//		 * // 此处通知监控页面，监控要请出会议，如会议成员为空则结束会议。这是与会议的差异。 if (videos.size() <= 0){
//		 * // 通知结束监控会议 if (monitorinfo.isIsmonitor()){ // 结束监控会议 Intent
//		 * meetIntent = new Intent(GlobalConstant.ACTION_MONITOR_CALLHANGUP);
//		 * sendBroadcast(meetIntent); } }
//		 */
        // 处理布局
        MtcDelegate.log("lizhiwei 删除视频设置视频布局DelVideoItem-DealVideoItmes");


        DealVideoItmes();
    }

    //推送视频
    private void pushVideoItem() {
        Log.e("视频推送", "进入");
        MonitorInfo monitorinfo = null;
        for (MonitorInfo monitor : videos) {
            if (monitor.isFocused()) {
                monitorinfo = monitor;
                break;
            }
        }
        if (monitorinfo == null) {
            Log.e("视频推送", "11564 return");
            return;
        }

        if (monitorinfo.isIsmonitor()) {
            ToastUtils.showToast(MainActivity.this, "暂不支持推送");
            return;
        }


        Log.e("视频推送", "" + monitorinfo.isIsmonitor());

//		// 取得登录用户信息
//		SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
//		SipUser sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

        // 发送创建会议请求
        //String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
        // 配置服务器IP
//		if (!uri.contains(sipServer.getServerIp())) {
//			uri += sipServer.getServerIp();
//		}
        String msgBody = "req:push_video\r\n" +
                "sid:" + monitorinfo.getSid() + "\r\n" +
                "cid:" + monitorinfo.getCid() + "\r\n" +
                "dstid:" + monitorinfo.getNumber() + "\r\n" +
                "streamid:\r\n" +
                "receivers:\r\n" +
                "flag:1\r\n";
//		/*	MtcCli.Mtc_CliSendUserMsg(GlobalConstant.PTT_MSG_REQ_ADDMEM
//		     			, uri
//		     			, 1
//		     			, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE
//		     			, msgBody);*/
        //MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);

        boolean result = MediaEngine.GetInstance().ME_ConfPushMemberVideo_Async(monitorinfo.getCid(), monitorinfo.getNumber());
        if (result) {
            ToastUtils.showToast(MainActivity.this, "推送成功");
        } else {
            ToastUtils.showToast(MainActivity.this, "推送失败");
        }

		/*ptt_3g_PadApplication.setPushVideoNum(monitorinfo.getNumber());
		     	Intent pushIntent = new Intent(GlobalConstant.ACTION_MEETING_PUSHVIDEO);
		     	sendBroadcast(pushIntent);*/

        Log.e("视频推送", "#######monitorinfo.getSid()：" + monitorinfo.getSid()
                + "---monitorinfo.getCid():" + monitorinfo.getCid() + "---monitorinfo.getNumber():" + monitorinfo.getNumber());

    }

    private AlertDialog alertDialog;
    private String channe;
    private String decodIdString;
    private String decoderBeansessionid = null;
    private String decoderBeannumber = null;
    private String decoderBeandecoderId = null;
    private String decoderBeanchannelId = null;

    /********
     * yuezs 隐藏 ice相关的
     *********/
    //private List<opDecoderInfo> channeList=new ArrayList<opDecoderInfo>();

    //开始解码上墙
    public void selectedDecoder(String session, String number, final String decoderId, final int channelId) {

        //初始化值
        decoderBeansessionid = null;
        decoderBeannumber = null;
        decoderBeandecoderId = null;
        decoderBeanchannelId = null;

        Log.e(TAG, "开始上墙   session：" + session + "number:" + number + "decoderId:" + decoderId + "channelId:" + channelId);
        if (session == null) {
            Log.e(TAG, "session == null   10876");
        } else {
            decoderBeansessionid = session;
        }
        if (number == null) {
            Log.e(TAG, "number == null   10881");
        } else {
            decoderBeannumber = number;
        }
        if (decoderId == null) {
            Log.e(TAG, "decoderId == null   10886");
        } else {
            decoderBeandecoderId = decoderId;
        }
        if (channelId == 0) {
            Log.e(TAG, "channelId == null   10891");
        } else {
            decoderBeanchannelId = String.valueOf(channelId);
        }
        Log.e(TAG, "session : " + session + "\r\n" + "number : " + number + "\r\n" + "decoderId : " + decoderId + "\r\n" + "channelId : " + channelId);
        MediaEngine.GetInstance().ME_StartDecode_Async(session, number, decoderId, channelId, new ME_StartDecode_CallBack() {
            @Override
            public void onCallBack(boolean arg0) {
                // TODO Auto-generated method stub

                if (arg0) {
                    initDecoderChannelStateAsync(decoderId, channelId);

                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ToastUtils.showToast(MainActivity.this, "失败");
                        }
                    });
                }
            }
        });
    }

    protected void initDecoderChannelStateAsync(String decoderId, int channelId) {
        // TODO Auto-generated method stub
        MediaEngine.GetInstance().ME_GetDecoderChannelState_Async(decoderId, channelId, new ME_GetDecoderChannelState_CallBack() {

            @Override
            public void onCallBack(boolean arg0) {
                // TODO Auto-generated method stub
                if (arg0) {

                    DecoderBean decoderBean = new DecoderBean();
                    decoderBean.setSessionId(decoderBeansessionid);
                    decoderBean.setDecoderId(decoderBeandecoderId);
                    decoderBean.setChanidStrings(decoderBeanchannelId);
                    decodeMap.put(decoderBeannumber, decoderBean);


                    Log.e(TAG, "解码上墙结果 成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ToastUtils.showToast(MainActivity.this, "上墙成功");
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    Log.e(TAG, "解码上墙结果 失败");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(MainActivity.this, "上墙失败，请重试");
                        }
                    });
                }
            }
        });
    }

    //结束解码上墙
    public void selectedDecoderEnd(final String session, final String number, final String decoderId, final int channelId) {
        Log.e(TAG, "结束上墙   session：" + session + "number:" + number + "decoderId:" + decoderId + "channelId:" + channelId);
        if (session == null) {
            Log.e("取消上墙session为空：", session);
            ToastUtils.showToast(MainActivity.this, "session为空");
            return;
        } else {
            Log.e("取消上墙session不为空：", session);
        }
        if (number.length() == 0) {
            Log.e("取消上墙number为空：", number);
            ToastUtils.showToast(MainActivity.this, "number为空");
            return;
        } else {
            Log.e("取消上墙number不为空：", number);
        }
        if (decoderId.length() == 0) {
            Log.e("取消上墙decoderId为空：", decoderId);
            ToastUtils.showToast(MainActivity.this, "decoderId为空");
            return;

        } else {
            Log.e("取消上墙decoderId不为空：", decoderId);
        }
        if (channelId == 0) {
            Log.e("取消上墙channel为空：", "" + channelId);
            ToastUtils.showToast(MainActivity.this, "channelId为空");
            return;
        } else {
            Log.e("取消上墙channel不为空：", "" + channelId);
        }

        MediaEngine.GetInstance().ME_StopDecode_Async(session, number, decoderId, channelId, new ME_StopDecode_CallBack() {
            @Override
            public void onCallBack(boolean arg0) {
                // TODO Auto-generated method stub
                if (arg0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //ToastUtils.showToast(MainActivity.this, "操作成功");
                            toastContent = "操作成功";
                            decoderHandler.sendEmptyMessage(6);
                            if (decodeMap.containsKey(number)) {
                                decodeMap.remove(number);
                                Log.e(TAG, "取消解码上墙的Key：" + number);
                                return;
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //ToastUtils.showToast(MainActivity.this, "操作失败，请重试");
                            //toastContent = "操作失败，请重试";
                            //decoderHandler.sendEmptyMessage(6);
                        }
                    });
                }
            }
        });
    }

    //stopcodupperwallmenu这个方法没有用了现在
    private void stopcodupperwallmenu() {


        /********yuezs 隐藏 ice相关的*********/
		/*MonitorInfo monitorinfo = null;
		for (MonitorInfo monitor : videos) {
			if (monitor.isFocused()) {
				monitorinfo = monitor;
				break;
			}
		}
		if (monitorinfo == null || decodIdString==null) {
			return;
		}
		ic = Ice.Util.initialize();
		base = ic.stringToProxy("MDCSrv:tcp -p 10001 -h " + sipServer.getServerIp());
		DecoderOPPrx decoderOP = DecoderOPPrxHelper.checkedCast(base);
		Identity identity = new Identity(sipUser.getUsername());
		opDecoderInfo opdecinfo=new opDecoderInfo();
		opdecinfo.Decoderid=decodIdString;//数据库中解码器的ID
		opdecinfo.cid=monitorinfo.getCid();//呼叫id
		opdecinfo.disNumber=monitorinfo.getNumber();//号码
		//Log.e("channe>>>>>", channe+"");
		opdecinfo.Channel=channe;//"1";//通道号
		decoderOP.begin_opStopDecoder(identity, opdecinfo, new Callback_DecoderOP_opStopDecoder() {

			@Override
			public void response(boolean __ret) {
				// TODO Auto-generated method stub
				if(__ret){
					Log.e("停止上墙", "成功");
				    btn_decodingupperwall.setText("上墙");
				}else{
					btn_decodingupperwall.setText("上墙");
					Log.e("停止上墙", "停止上墙失败");
				}
			}

			@Override
			public void exception(LocalException __ex) {
				// TODO Auto-generated method stub

			}
		});*/

    }

    private String sessionid = null;

    //点击类型的监听
    private void loadchan(String typeName, int i) {
        if (list_decoderchannel.size() > 0) {
            list_decoderchannel.clear();
        }

        Log.e(TAG, "" + decoderDisplayChans.get(i).decodeChannelIds.length);


        if (decoderDisplayChans.get(i).decodeChannelIds.length > 0) {
            for (int j = 0; j < decoderDisplayChans.get(i).decodeChannelIds.length; j++) {
                list_decoderchannel.add("" + decoderDisplayChans.get(i).decodeChannelIds[j]);
            }
        } else {
            list_decoderchannel.add("当前类型没有关联通道");
        }

        if (list_decoderchannel.size() > 0) {
            Log.e(TAG, "5");
            chan_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_decoderchannel);
            chan_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectedchan.setAdapter(chan_adapter);
            chan_adapter.notifyDataSetChanged();
            Log.e(TAG, "加载通道结束");
        }


        /********yuezs 隐藏 ice相关的*********/
		/*if(hashchan!=null&& hashchan.size()>0){
	      	  String dcodName=selectedtype.getSelectedItem().toString();

	      	 //Log.e("typeInteger", "typeInteger:"+chanid+",hashchan.size():"+hashchan.size());
	      	 listchan=new ArrayList<String>();
	      	 DecoderDisplayChan[] decdispchan=hashchan.get(dcodName).byJoinDecChans;
	      	 for (int i = 0; i < decdispchan.length; i++) {
					listchan.add(String.valueOf(decdispchan[i].byChan));
				}
	        selectedchan=(Spinner)layout.findViewById(R.id.selectedchan);
			  chan_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listchan);
	        chan_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        selectedchan.setAdapter(chan_adapter);
	        Log.e("结束", "加载通道结束");
	        }*/
    }

    //加载解码器类型、
    private void loadlist(String deviceId, String deviceName) {
        Log.e(TAG, "loadlist中      deviceId:" + deviceId + "deviceName:" + deviceName);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (deviceId != null && deviceName != null) {
                data_Decorder_ID = deviceId;
            }

            if (data_Decorder_ID != null && !"".equals(data_Decorder_ID)) {
                int sleeptype20 = 1;
                decoderDisplayChans = getDisArrayList(data_Decorder_ID);
                Log.e(TAG, "sws：" + decoderDisplayChans.size() + "data_Decorder_ID: " + data_Decorder_ID);
                while (true) {
                    if (decoderDisplayChans == null || decoderDisplayChans.size() == 0) {
                        sleeptype20--;
                        if (sleeptype20 > 0) {
                            Log.e(TAG, "decoderDisplayChans--0");
                            decoderDisplayChans = getDisArrayList(data_Decorder_ID);
                        } else {
                            ToastUtils.showToast(MainActivity.this, getString(R.string.loadTimeout_DecodType));
                            break;
                        }
                    } else {
                        Log.e(TAG, "decoderDisplayChans--" + decoderDisplayChans.size());
                        break;
                    }
                    Thread.sleep(1000);
                }
                list_decpderdisplayChans = new ArrayList<String>();

                if (decoderDisplayChans.size() != 0) {
                    int type1 = 1;
                    int type2 = 1;
                    int type3 = 1;
                    int type4 = 1;
                    Log.e(TAG, "" + decoderDisplayChans.get(0).decodeChannelIds.length);
                    for (int i = 0; i < decoderDisplayChans.size(); i++) {
                        //加载类型数据
                        if (decoderDisplayChans.get(i).displayType == 0) {
                            list_decpderdisplayChans.add("BNC" + type1);
                            type1++;
                        } else if (decoderDisplayChans.get(i).displayType == 1) {
                            //加载类型数据
                            list_decpderdisplayChans.add("VGA" + type2);
                            type2++;
                        } else if (decoderDisplayChans.get(i).displayType == 2) {
                            //加载类型数据
                            list_decpderdisplayChans.add("HDMI" + type3);
                            type3++;
                        } else if (decoderDisplayChans.get(i).displayType == 3) {
                            //加载类型数据
                            list_decpderdisplayChans.add("DVI" + type4);
                            type4++;
                        }
                    }
                } else {
                    list_decpderdisplayChans.add("当前解码器没有配置类型");
                }
//                selectedtype = (Spinner) decoderlayout.findViewById(R.id.selectedtype);
//                type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_decpderdisplayChans);
//                type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                selectedtype.setAdapter(type_adapter);
//                type_adapter.notifyDataSetChanged();
//                Log.e("位置", "位置--" + type_adapter.getCount());
//                chanName = selectedtype.getSelectedItem().toString();
                decoderHandler.sendEmptyMessage(3);
            } else {
                Log.e(TAG, "data_Decorder_ID为空！");
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    //解码上墙-获取解码器列表显示到弹出层
    private ME_DecoderInfo[] dcodupperwall() {

        ME_DecoderInfo[] medecoderInfo = MediaEngine.GetInstance().ME_GetDecoderList();
        Log.e(TAG, "解码器集合数组长度：" + medecoderInfo.length);
        return medecoderInfo;
    }

    //List<DecoderDisplayChan []> listChans;
    private List<ME_DecoderDisplayInfo> getDisArrayList(String idString) {

        if (typeList != null) {
            typeList.clear();
        }
        decodIdString = idString;
        ME_DecoderDisplayInfo[] me_decoder = MediaEngine.GetInstance().ME_GetDecoderDisplays(decodIdString);
        if (me_decoder == null) {
            return null;
        }
        List<ME_DecoderDisplayInfo> typeList = Arrays.asList(me_decoder);
        Log.e(TAG, "sws：" + typeList.size());
        return typeList;

    }

    // 项目点击事件
    private class ItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            MonitorInfo monitorinfo = (MonitorInfo) v.getTag();
            if (monitorinfo == null) {
                return;
            }
            Log.e(TAG, "点击：" + monitorinfo.getNumber());

            Log.e(TAG, "保存number：" + monitorinfo.getNumber());
            Log.e("=========测试测试阿打算打算222","点击：" + monitorinfo.getNumber());

            prefs.edit().putString("number", monitorinfo.getNumber()).commit();
            for (MonitorInfo monitor : monitorInfoList) {
                if (monitor.getNumber() != monitorinfo.getNumber()) {
                    monitor.setFocused(false);
                    if (monitor.getView() != null) {
                        monitor.getView().setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
            if (monitorinfo.isFocused()) {
                monitorinfo.setFocused(false);
                v.setBackgroundColor(Color.TRANSPARENT);
            } else {
                monitorinfo.setFocused(true);
                v.setBackgroundColor(getResources().getColor(
                        R.color.meeting_meeting_selected));
            }
        }
    }

    // 显示监控信息
    private void ShowMonitorInfo() {
        if (monitorInfoList == null) {
            return;
        }
        for (MonitorInfo monitor : monitorInfoList) {
            if (monitor.isFocused()) {
                View view = monitor.getView();
                if (view == null) {
                    return;
                }
                RelativeLayout rl_title = (RelativeLayout) view
                        .findViewById(R.id.rl_title);

                if (rl_title.getVisibility() == View.VISIBLE) {
                    rl_title.setVisibility(View.GONE);
                } else if (rl_title.getVisibility() == View.GONE) {
                    rl_title.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    // 视频播放处理
    private class ItemSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @SuppressWarnings("unused")
        TextureView view;
        MonitorInfo monitorinfo;

        ItemSurfaceTextureListener(TextureView view, MonitorInfo monitorinfo) {
            this.view = view;
            this.monitorinfo = monitorinfo;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            // TODO Auto-generated method stub
            Surface s = new Surface(surface);
            monitorinfo.setSurface(s);
            try {
                boolean stun = prefs.getBoolean(GlobalConstant.SP_STUN, false);
                MultiPlayer multiplayer = new MultiPlayer();
                multiplayer.create(s);
                String url = "rtp://localhost:"
                        + Integer.toString(monitorinfo.getVedioPort())
                        + "?codec=h.264";
                Log.e(TAG, "stun: " + stun);
                if (stun) {
                    // 如果需要穿越则调用穿越
                    url = url + "&sip=" + monitorinfo.getIp() + "&sport="
                            + monitorinfo.getRemotePort();
                    Log.e(TAG, "url: " + url);
                }
                multiplayer.open(url);
                multiplayer.play();
                Log.e(TAG, "MultiPlayer state "
                        + multiplayer.getState() + " "
                        + monitorinfo.getNumber() + " "
                        + monitorinfo.getVedioPort() +
                        ",IP=" + monitorinfo.getIp() + ",romtePort=" + monitorinfo.getRemotePort());
                monitorinfo.setMultiplayer(multiplayer);
            } catch (Exception e) {
                MtcDelegate.log("Exception e:" + e);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }


        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (monitorinfo.getMultiplayer() != null) {
                monitorinfo.getMultiplayer().stop();
                monitorinfo.getMultiplayer().close();
                monitorinfo.getMultiplayer().destroy();
            }
            if (monitorinfo.getTimer() != null) {
                monitorinfo.getTimer().stop();
                monitorinfo.getTimer().setText("");
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }


    }

    /**
     * 定位SDK监听函数
     */
    private class MainLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            Log.e("测试定位", "定位回调 成功");
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            if (mBaiduMap == null) {
                return;
            }
            if (location.getLatitude() == 4.9E-324 || location.getLongitude() == 4.9E-324) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            // 我的位置赋值
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                // mBaiduMap.animateMapStatus(u);
                mBaiduMap.setMapStatus(u);

                getPosRectangleGis();//启动后不再，获取矩形区域坐标成员，yuezs取消注释因为有个bug说要启动时显示成员
            }

            // 向服务器发送gis信息
			/*String gisUri = MtcUri.Mtc_UriFormatX(
					GlobalConstant.PTT_MSG_SERVER_ID, false);*/
            StringBuilder sb = new StringBuilder();
            sb.append("$GPRMC,");
            sb.append(FormatController.getGPRMCDateFormatSec() + ",");
            sb.append("A,");
            BigDecimal bLat1 = new BigDecimal(location.getLatitude());
            BigDecimal bLon1 = new BigDecimal(location.getLongitude());
            double mLat1 = bLat1.setScale(6, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            double mLon1 = bLon1.setScale(6, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            DecimalFormat dfLon = new DecimalFormat("00000.0000");
            DecimalFormat dfLat = new DecimalFormat("0000.0000");
            sb.append(dfLat.format(mLat1 * 100) + ",");
            sb.append("N,");
            sb.append(dfLon.format(mLon1 * 100) + ",");
            sb.append("E,");
            sb.append("0,");
            sb.append("0,");
            sb.append(FormatController.getGPRMCDateFormatDay() + ",");
            sb.append("0,0,0,0");
            String ngprmc = sb.toString();
            String myGisBodyString = "ind:gis\r\nlatitude:"
                    + location.getLatitude() + "\r\nlontitude:"
                    + location.getLongitude() + "\r\ntime:"
                    + FormatController.getNewFileNameByDate() + "\r\ngprmc:"
                    + ngprmc + "\r\n";
            //MtcCli.Mtc_CliSendUserMsg(null, gisUri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, myGisBodyString);
            //MtcDelegate.log("上报gis:"+gisUri+" msgBodyString:"+myGisBodyString);
            MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, myGisBodyString);

            mLocClient.stop();
        }

        @SuppressWarnings("unused")
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    // 显示多选操作菜单窗口
    private void showMultipleWindow(View v) {



        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.layout_pos_multiple, null);

        multiplePopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        multiplePopupWindow.setFocusable(true);
        multiplePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popupWindow.setOutsideTouchable(true);
        // 保存anchor在屏幕中的位置
        int[] location = new int[2];
        // 读取位置anchor座标
        v.getLocationOnScreen(location);

        // multiplePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
        // location[0]-330,(int)multipleLinearLayout.getTop());
        if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
            multiplePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 410, location[1]);
        } else {
            multiplePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 317, location[1]);
        }


        btnSelClick = (TextView) view.findViewById(R.id.btnSelClick);
        btnMultipleAll = (TextView) view.findViewById(R.id.btnMultipleAll);
        btnSelClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                posSelType = 2;
                ToastUtils.showToast(MainActivity.this, "点选模式");

                if (multiplePopupWindow != null) {
                    multiplePopupWindow.dismiss();
                }
                //判断是否有成员
                if (checkBoxMarker.size() <= 0) {
                    return;
                }
                //设置列表checkbox状态
                if (employeeLayout.getChildCount() > 0) {
                    for (int i = 0; i < employeeLayout.getChildCount(); i++) {
                        CheckBox checkBox = (CheckBox) employeeLayout.getChildAt(i);
                        checkBox.setChecked(false);
                    }
                }
                //设置地图marker状态
                for (int i = 0; i < checkBoxMarker.size(); i++) {
                    addPosOverlay(checkBoxMarker.get(i), false);
                }
                checkBoxMarker.clear();

                if (ll_pos_single.getVisibility() != View.GONE) {
                    ll_pos_single.setVisibility(View.GONE);
                }
                if (ll_pos.getVisibility() != View.GONE) {
                    ll_pos.setVisibility(View.GONE);
                }

                isCheck = true;

            }
        });
        btnMultipleAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 选中模式(1单选2点选3全选4圈选5框选)

                if (isLoadMarker){
                    ToastUtils.showToast(MainActivity.this,"数据加载中 请等待");
                    return;
                }

                posSelType = 3;
                ToastUtils.showToast(MainActivity.this, "全选模式");
                if (posMarkerInfosList == null) {
                    ToastUtils.showToast(MainActivity.this, "数据为空");
                    return;
                }
                Log.e(TAG, "employeeLayout.getChildCount():" + employeeLayout.getChildCount());
                if (employeeLayout.getChildCount() <= 0) {

                    return;
                }
                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                    checkBoxMarker.clear();// 清空选择列表
                }

                if (mBaiduMap != null) {
                    mBaiduMap.clear();// 先清空覆盖物
                }// 清空当前marker

                // 重新添加选中状态
                for (PosMarkerInfo pmi : posMarkerInfosList) {
                    addPosOverlay(pmi, true);
                    checkBoxMarker.add(pmi);// 从新添加
                }

                if (checkBoxMarker.size() == 1) {
                    ll_pos_single.setVisibility(View.VISIBLE);
                    ll_pos.setVisibility(View.GONE);
                } else if (checkBoxMarker.size() > 1) {
                    ll_pos_single.setVisibility(View.GONE);
                    ll_pos.setVisibility(View.VISIBLE);
                } else {
                    ll_pos_single.setVisibility(View.GONE);
                    ll_pos.setVisibility(View.GONE);
                }
                if (multiplePopupWindow != null) {
                    multiplePopupWindow.dismiss();
                }
                isCheck = true;



            }
        });

    }

    boolean iswindow = false;
    private PopupWindow popupWindow;

    private void showPopupWindow(View v, final EditText edit) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View view = layoutInflater.inflate(R.layout.pos_listitem, null);
            ListView lv = (ListView) view.findViewById(R.id.listitem);
            lv.setAdapter(arr_adapter);

            popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            // }
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.update();
            lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    edit.setText(arr_adapter.getItem(position));
                    popupWindow.dismiss();
                }
            });
            int[] location = new int[2];
            // 读取位置anchor座标
            v.getLocationOnScreen(location);
            if (getwindowmanager() == 1280 && getwindowmanager_height() == 800) {
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                        location[0] + 4, location[1] + 66);
            } else if (getwindowmanager() == 1280 && getwindowmanager_height() == 752) {
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                        location[0] + 250, location[1] + 146);
            } else if (getwindowmanager() == 2048 && getwindowmanager_height() == 1440) {

                if (istrac) {
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                            location[0] + 2, location[1] + 68);
                } else {
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                            location[0] + 430, location[1] + 295);
                }
            } else if (getwindowmanager() == 1024 && getwindowmanager_height() == 720) {
                if (istrac) {
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                            location[0] + 2, location[1] + 35);
                } else {
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY,
                            location[0] + 320, location[1] + 152);
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "" + ex);
        }
    }

    // 显示筛选操作菜单窗口
    private void showFilterWindow(View v) {
        istrac = true;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.layout_pos_filter, null);
        // if (filterPopupWindow == null) {
        filterPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // }
        filterPopupWindow.setFocusable(true);
        filterPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popupWindow.setOutsideTouchable(true);
        // 保存anchor在屏幕中的位置
        int[] location = new int[2];
        // 读取位置anchor座标
        v.getLocationOnScreen(location);


        Log.e(TAG, "getResources().getDisplayMetrics().heightPixels:" + getResources().getDisplayMetrics().heightPixels);
        Log.e(TAG, "getResources().getDisplayMetrics().widthPixels:" + getResources().getDisplayMetrics().widthPixels);

        //分辨率
        if (getwindowmanager() == 1280) {
            filterPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 331, location[1] - 120);
            Log.e(TAG, "=============11317");
        } else if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
            Log.e(TAG, "=============11319");
            filterPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 475, location[1] - 170);
        } else if (getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024) {
            Log.e(TAG, "=============11324");
            filterPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 245, location[1] - 72);
        } else {
            Log.e(TAG, "=============11390");
            filterPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    location[0] - 292, location[1] - 86);
        }
        chb3G = (CheckBox) view.findViewById(R.id.chbox_3g);
        chbSip = (CheckBox) view.findViewById(R.id.chbox_sip);
        chboxMonitor = (CheckBox) view.findViewById(R.id.chbox_monitor);
        chboxDispacher = (CheckBox) view.findViewById(R.id.chbox_dispacher);
        spRange = (Spinner) view.findViewById(R.id.spRange);
        btnSearch = (TextView) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }

                String type = "";
                if (chb3G.isChecked() == false && chbSip.isChecked() == false
                        && chboxMonitor.isChecked() == false && chboxDispacher.isChecked() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.req_filter_leixing));
                    return;
                } else {
                    if (chb3G.isChecked() == true) {
                        type += 6 + ",";
                    }
                    if (chbSip.isChecked() == true) {
                        type += 2 + ",";
                    }
                    if (chboxMonitor.isChecked() == true) {
                        type += 7 + ",";
                    }
                    if (chboxDispacher.isChecked() == true) {
                        type += 0 + ",";
                    }
                }

                String posRange = spRange.getSelectedItem().toString();
                int raidus = 0;// 半径
                if (posRange.equals("一公里")) {
                    raidus = 1000;
                } else if (posRange.equals("两公里")) {
                    raidus = 2000;
                } else if (posRange.equals("三公里")) {
                    raidus = 3000;
                } else if (posRange.equals("四公里")) {
                    raidus = 4000;
                }
                if (myLatitude == 0 || myLongitude == 0) {
                    ToastUtils.showToast(MainActivity.this, "请定位成功以后在使用筛选功能");
                    return;
                }
                double[] around = CommonMethod.getAround(myLatitude,
                        myLongitude, raidus);

                String uri = MtcUri.Mtc_UriFormatX(
                        GlobalConstant.PTT_MSG_SERVER_ID, false);
                double lngmin = around[1];
                double lngmax = around[3];
                double latmin = around[0];
                double latmax = around[2];

                DecimalFormat decimalFormat = new DecimalFormat("#.000000");


                String msgBodyString = "req:pos_by_ellipse\r\nemployeeid:"
                        + sipUser.getUsername() + "\r\nlngmin:"
                        + decimalFormat.format(lngmin) + "\r\nlngmax:"
                        + decimalFormat.format(lngmax) + "\r\nlatmin:"
                        + decimalFormat.format(latmin) + "\r\nlatmax:"
                        + decimalFormat.format(latmax) + "\r\ntype:"
                        + type + "\r\n";
				/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
						GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
				MtcDelegate.log("椭圆筛选发送信息uri:" + uri + " msgBodyString:"
						+ msgBodyString);
				Log.w("发送圆形筛选请求", "发送圆形筛选请求"+msgBodyString);*/
                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                ToastUtils.showToast(MainActivity.this, "已发送筛选请求");
                if (filterPopupWindow != null) {
                    filterPopupWindow.dismiss();
                }
            }
        });
    }

    // 显示追踪操作菜单窗口
    private void showTraceWindow(View v) {
        Log.e(TAG, "进入显示追踪轨迹操作菜单窗口方法");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.layout_pos_trace, null);
        // if (tracePopupWindow == null) {
        tracePopupWindow = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.set_layout_zhuizong),
                getResources().getDimensionPixelSize(R.dimen.set_layout_pos_trace_main_heigth));
        // }
        tracePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        tracePopupWindow.setFocusable(true);
        // popupWindow.setOutsideTouchable(true);
        // 保存anchor在屏幕中的位置
        int[] traceLocation = new int[2];
        // 读取位置anchor座标
        v.getLocationOnScreen(traceLocation);

        if (getwindowmanager() == 1280) {
            tracePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    traceLocation[0] - 440, traceLocation[1] - 220);
            Log.e(TAG, "=============11471");
        } else if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
            Log.e(TAG, "=============11319");
            tracePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    traceLocation[0] - 700, traceLocation[1] - 278);
            Log.e(TAG, "traceLocation[0]:" + traceLocation[0]);
            Log.e(TAG, "traceLocation[1]:" + traceLocation[1]);
        } else if (getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024) {
            Log.e(TAG, "=============11513");
            tracePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    traceLocation[0] - 278, traceLocation[1] - 136);
        } else {
            Log.e(TAG, "=============11479");
            tracePopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    traceLocation[0] - 248, traceLocation[1] - 181);
        }
        // 追踪
        btnClean = (TextView) view.findViewById(R.id.btnClean);
        btnSearch = (TextView) view.findViewById(R.id.btnSearch);
        addUserNoListView = (ListView) view.findViewById(R.id.listUserNo);
        btnAddNo = (Button) view.findViewById(R.id.btnAddNo);
        btnDelete = (Button) view.findViewById(R.id.btnDelete);
        editUserNo = (EditText) view.findViewById(R.id.editUserNo);
        editUserNo.setText("");
        if (addUserNoList == null) {
            addUserNoList = new ArrayList<String>();
        } else {
            addUserNoList.clear();
        }

        if (checkBoxMarker.size() > 0) {
            for (int i = 0; i < checkBoxMarker.size(); i++) {
                addUserNoList.add(checkBoxMarker.get(i).getName());
            }
        }

        posTraceAdapter = new PosTraceAdapter(this, addUserNoList);
        addUserNoListView.setAdapter(posTraceAdapter);

        btnAddNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUserNo.getText().length() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请输入用户号码");
                    return;
                }
                addUserNoList.add(editUserNo.getText().toString());
                posTraceAdapter.notifyDataSetChanged();
                editUserNo.setText("");
            }
        });

        btnClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addUserNoList.size() > 0) {
                    addUserNoList.clear();
                    posTraceAdapter.notifyDataSetChanged();
                }
            }
        });
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "点击了追踪的查询");
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }
                Log.e(TAG, "addUserNoList.size():" + addUserNoList.size());
                if (addUserNoList.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请输入用户号码");
                    return;
                }


                if (addUserNoList.size() > 0) {
                    for (String aUser : addUserNoList) {
                        Log.e(TAG, "追踪的用户名:" + aUser);
                        String uri = sipUser.getUsername();//MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                        String dstid = aUser.toString();
                        String msgBodyString = "req:trace_pos\r\nsid:"
                                + UUID.randomUUID() + "\r\nemployeeid:"
                                + sipUser.getUsername() + "\r\ndstid:" + dstid
                                + "\r\nspace:5\r\nflag:1\r\n";
//						MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
//								GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                        Log.e(TAG, "开始追踪发送uri" + uri + "  消息:" + msgBodyString);
                    }
                }


				/*String uri = MtcUri.Mtc_UriFormatX(
						GlobalConstant.PTT_MSG_SERVER_ID, false);*/
                String dstid = CommonMethod.listToString(addUserNoList, ",");
                String msgBodyString = "req:trace_pos\r\nsid:"
                        + UUID.randomUUID() + "\r\nemployeeid:"
                        + sipUser.getUsername() + "\r\ndstid:" + dstid
                        + "\r\nspace:5\r\nflag:1\r\n";
				/*	MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
						GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
				MtcDelegate.log("开始追踪发送uri" + uri + "  消息:" + msgBodyString);*/
                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                Log.e(TAG, "=========");
                if (tracePopupWindow != null) {
                    editUserNo.setText("");
                    tracePopupWindow.dismiss();
                    Log.e(TAG, "disimiss");
                }
            }
        });
    }

    // 显示轨迹操作菜单窗口
    private void showTrackWindow(View v) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.layout_pos_track, null);
        // if (trackPopupWindow == null) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.e(TAG, "屏幕宽：" + displayMetrics.widthPixels);
        Log.e(TAG, "屏幕高：" + displayMetrics.heightPixels);

        trackPopupWindow = new PopupWindow(view, getResources().getDimensionPixelSize(R.dimen.set_layout_pos_filter_track_main_width),
                getResources().getDimensionPixelSize(R.dimen.set_layout_pos_filter_height));
        // }

        trackPopupWindow.setFocusable(true);
        trackPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 保存anchor在屏幕中的位置
        int[] trackLocation = new int[2];
        // 读取位置anchor座标
        v.getLocationOnScreen(trackLocation);

        if (getwindowmanager() == 1280) {
            Log.e(TAG, "=============11578");
            trackPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    trackLocation[0] - 520, trackLocation[1] - 163);
        } else if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
            Log.e(TAG, "=============11582" + trackLocation[0] + "," + trackLocation[1]);
            trackPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, trackLocation[0] - 758, trackLocation[1] - 237);
        } else if (getResources().getDisplayMetrics().heightPixels == 720 || getResources().getDisplayMetrics().widthPixels == 1024) {
            Log.e(TAG, "=============11699" + trackLocation[0] + "," + trackLocation[1]);
            trackPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    trackLocation[0] - 388, trackLocation[1] - 102);
        } else {
            Log.e(TAG, "=============11587");
            trackPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                    trackLocation[0] - 10, trackLocation[1] - 10);
        }


        trackListView = (ListView) view.findViewById(R.id.trackListView);
        trackBeginDate = (EditText) view.findViewById(R.id.trackBeginDate);
        trackEndDate = (EditText) view.findViewById(R.id.trackEndDate);
        trackBeginTime = (EditText) view.findViewById(R.id.trackBeginTime);
        trackEndTime = (EditText) view.findViewById(R.id.trackEndTime);
        btnTrackClean = (TextView) view.findViewById(R.id.btnTrackClean);
        btnTrackSearch = (TextView) view.findViewById(R.id.btnTrackSearch);
        btnTrackAddNo = (Button) view.findViewById(R.id.btnTrackAddNo);
        editTrackUserNo = (EditText) view.findViewById(R.id.editTrackUserNo);


        if (addUserNoList == null) {
            addUserNoList = new ArrayList<String>();
        } else {
            addUserNoList.clear();
        }

        if (checkBoxMarker.size() > 0) {
            for (int i = 0; i < checkBoxMarker.size(); i++) {
                addUserNoList.add(checkBoxMarker.get(i).getName());
            }
        }


        posTraceAdapter = new PosTraceAdapter(this, addUserNoList);
        trackListView.setAdapter(posTraceAdapter);

        btnTrackAddNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTrackUserNo.getText().length() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请输入用户号码");
                    return;
                }

                addUserNoList.add(editTrackUserNo.getText().toString());
                posTraceAdapter.notifyDataSetChanged();
                editTrackUserNo.setText("");
            }
        });
        trackBeginDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Dialog dateDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date = year + "-" + (monthOfYear + 1)
                                        + "-" + dayOfMonth;
                                trackBeginDate.setText(date);
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dateDialog.show();
            }
        });
        trackEndDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                Dialog dateDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String date = year + "-" + (monthOfYear + 1)
                                        + "-" + dayOfMonth;
                                trackEndDate.setText(date);
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dateDialog.show();

            }
        });

        trackBeginTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                Dialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        // TODO Auto-generated method stub
                        String time = hourOfDay + ":" + minute;
                        trackBeginTime.setText(time);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //焦点释放处理
                        if (dialog instanceof TimePickerDialog) {
                            ((TimePickerDialog) dialog).getWindow().getDecorView().clearFocus();
                        }
                        super.onClick(dialog, which);
                    }
                };
                dialog.show();
            }
        });
        trackEndTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                Dialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        String time = hourOfDay + ":" + minute;
                        trackEndTime.setText(time);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //焦点释放处理
                        if (dialog instanceof TimePickerDialog) {
                            ((TimePickerDialog) dialog).getWindow().getDecorView().clearFocus();
                        }
                        super.onClick(dialog, which);
                    }
                };
                dialog.show();
            }
        });
        /********yuezs 隐藏 ice相关的*********/
        btnTrackSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "进入查询监听");
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MainActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }

                String trackBeginDateString = trackBeginDate.getText().toString().trim();
                String trackBeginTimeString = trackBeginTime.getText().toString().trim();
                String trackEndDateString = trackEndDate.getText().toString().trim();
                String trackEndTimeString = trackEndTime.getText().toString().trim();
                Log.e(TAG, "得到时间   trackBeginDateString：" + trackBeginDateString + "trackBeginTimeString：" + trackBeginTimeString + "trackEndDateString:" + trackEndDateString + "trackEndTimeString：" + trackEndTimeString);
                if (trackBeginDateString == null || "".equals(trackBeginDateString)) {
                    ToastUtils.showToast(MainActivity.this, "开始时间不能为空");
                    return;
                }

                if (trackBeginTimeString == null || "".equals(trackBeginTimeString)) {
                    ToastUtils.showToast(MainActivity.this, "开始时间不能为空");
                    return;
                }

                if (trackEndDateString == null || "".equals(trackEndDateString)) {
                    ToastUtils.showToast(MainActivity.this, "结束时间不能为空");
                    return;
                }

                if (trackEndTimeString == null || "".equals(trackEndTimeString)) {
                    ToastUtils.showToast(MainActivity.this, "结束时间不能为空");
                    return;
                }

                if (addUserNoList.size() <= 0) {
                    ToastUtils.showToast(MainActivity.this, "请输入用户号码");
                    return;
                }

                if (addUserNoList.size() > 10) {
                    ToastUtils.showToast(MainActivity.this, "轨迹回放用户数不大于10");
                    return;
                }

                Identity identity = new Identity(sipUser.getUsername());
                GisInfoByTimeT gisInfoByTime = new GisInfoByTimeT();
                gisInfoByTime.employeeid = CommonMethod.listToString(addUserNoList, ",");
                Log.e(TAG, "gisInfoByTime.employeeid：" + gisInfoByTime.employeeid);
                gisInfoByTime.begin = (FormatController.formatPosDates(trackBeginDateString) + FormatController.formatPosTime(trackBeginTimeString));
                Log.e(TAG, "gisInfoByTime.begin：" + gisInfoByTime.begin);
                gisInfoByTime.end = (FormatController.formatPosDates(trackEndDateString) + FormatController.formatPosTime(trackEndTimeString));
                Log.e(TAG, "gisInfoByTime.end：" + gisInfoByTime.end);
                gisInfoByTime.type = -1;
                Log.e(TAG, "gisInfoByTime.type：" + gisInfoByTime.type);
                trackStartPosTime = gisInfoByTime.begin;
                Log.e(TAG, "trackStartPosTime：" + trackStartPosTime);
                trackEndPosTime = gisInfoByTime.end;
                Log.e(TAG, "trackEndPosTime：" + trackEndPosTime);
                differenceTimeSeconds = differenceTime(gisInfoByTime.begin, gisInfoByTime.end);
                Log.e(TAG, "两个时间的差值：" + differenceTimeSeconds);

                if (Long.valueOf(trackStartPosTime) > Long.valueOf(trackEndPosTime)) {
                    ToastUtils.showToast(MainActivity.this, "开始时间不能大于结束时间");
                    return;
                }


                if (differenceTimeSeconds < 300)//开始和结束时间不小于5分钟
                {
                    ToastUtils.showToast(MainActivity.this, "轨迹回放开始和结束时间不小于5分钟");
                    return;
                }

                if (differenceTimeSeconds > 14400) //开始和结束时间不大于4个小时
                {
                    ToastUtils.showToast(MainActivity.this, "轨迹回放开始和结束时间不大于4个小时");
                    return;
                }


                if (hasMark != null && hasMark.size() > 0) {
                    hasMark.clear();//清空轨迹对象
                    Log.e(TAG, "清空轨迹对象");
                }
                // 清空选中列表
                if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                    checkBoxMarker.clear();
                    Log.e(TAG, "清空选中列表");
                }

                if (trackPosMarkerInfoMap == null) {
                    trackPosMarkerInfoMap = Collections.synchronizedMap(new HashMap<String, List<PosMarkerInfo>>());
                } else {
                    trackPosMarkerInfoMap.clear();
                }

                boolean result = MediaEngine.GetInstance().ME_GetGisInfoByTime_Async(gisInfoByTime.employeeid, trackStartPosTime, trackEndPosTime, gisInfoByTime.type, new MediaEngine.ME_GetGisInfoByTime_CallBack() {
                    @Override
                    public void onCallBack(final MediaEngine.ME_GisInfo[] __ret) {

                        Log.e(TAG, " main  12027 监听中  __ret：" + __ret.length);
                        if (__ret.length <= 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(MainActivity.this, "当前号码Gis信息为空");
                                }
                            });
                            Log.e(TAG, "__ret == null   return掉了");
                            return;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBaiduMap != null) {
                                        mBaiduMap.clear();// 先清空覆盖物
                                        Log.e(TAG, "清空覆盖物");
                                    }
                                    String modify5 = timeUtils(__ret[0].time);
                                    ToastUtils.showToast(MainActivity.this, "用户轨迹从" + modify5 + "开始绘制");
                                }
                            });
                        }


                        for (MediaEngine.ME_GisInfo detail : __ret) {
                            String mEmployeeid = detail.userNumber;
                            Log.e(TAG, "mEmployeeid:" + mEmployeeid);
                            double mLatitude = Double.valueOf(detail.latitude);
                            Log.e(TAG, "mLatitude:" + mLatitude);
                            double mLongitude = Double.valueOf(detail.longitude);
                            Log.e(TAG, "mLongitude:" + mLongitude);
                            String mPositionTime = detail.time;
                            Log.e(TAG, "mPositionTime:" + mPositionTime);
                            //	String mMessage = detail.mMessage;
                            //	Log.e("==Sws测试轨迹回放","mMessage:" + mMessage);

                            //Sws 11/08 hide
								/*								UserType userType = detail.type;
								int type = userType.value();*/
                            //Sws 11/08 add
                            int type = detail.userType;
                            Log.e(TAG, "type:" + type);
                            Log.e(TAG, "mEmployeeid: " + mEmployeeid + ",mLatitude: " + mLatitude + ",mLongitude: " + mLongitude + ",type: " + type);

                            PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                            posMarkerInfo.setName(mEmployeeid);
                            posMarkerInfo.setLatitude(mLatitude);
                            posMarkerInfo.setLongitude(mLongitude);
                            posMarkerInfo.setTime(mPositionTime);
                            Log.e(TAG, "将数据封装到了PosMarkerInfo中");
                            switch (type) {
                                case 0://调度台
                                    posMarkerInfo.setType(0);
                                    break;
                                case 1://对讲终端
                                case 3://外线用户
                                case 6://3G对讲 ,这三种类型都作为3G用户处理
                                    posMarkerInfo.setType(6);
                                    break;
                                case 2://软电话(SIP电话)
                                    posMarkerInfo.setType(2);
                                    break;
                                case 7://监控设备
                                    posMarkerInfo.setType(7);
                                    break;
                                default:
                                    posMarkerInfo.setType(6);
                                    break;
                            }

                            if (!trackPosMarkerInfoMap.containsKey(mEmployeeid)) {
                                List<PosMarkerInfo> posList = new ArrayList<PosMarkerInfo>();
                                posList.add(posMarkerInfo);
                                trackPosMarkerInfoMap.put(mEmployeeid, posList);
                            } else {
                                List<PosMarkerInfo> posList = trackPosMarkerInfoMap.get(mEmployeeid);
                                posList.add(posMarkerInfo);
                            }
                        }
                        Message message = new Message();
                        message.what = 1;
                        message.obj = trackStartPosTime;
                        trackHandler.sendMessage(message);
                        Log.e(TAG, "trackHandler.sendMessage已发送   main 12105");
                    }
                });

                Log.e(TAG, "请求result： " + result);

                if (trackPopupWindow != null) {
                    editTrackUserNo.setText("");
                    trackBeginDate.setText("");
                    trackBeginTime.setText("");
                    trackEndDate.setText("");
                    trackEndTime.setText("");
                    trackPopupWindow.dismiss();
                }
            }
        });
        btnTrackClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addUserNoList.size() > 0) {
                    addUserNoList.clear();
                    posTraceAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private String timeUtils(String h) {
        String time = h;
        StringBuilder sb = new StringBuilder(time);
        String modify1 = sb.insert(4, "-").toString();
        StringBuilder sb1 = new StringBuilder(modify1);
        String modify2 = sb1.insert(7, "-").toString();
        StringBuilder sb2 = new StringBuilder(modify2);
        String modify3 = sb2.insert(10, "-").toString();
        StringBuilder sb3 = new StringBuilder(modify3);
        String modify4 = sb3.insert(13, ":").toString();
        StringBuilder sb4 = new StringBuilder(modify4);
        String modify5 = sb4.insert(16, ":").toString();

        return modify5;
    }

    Handler trackHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "trackHandler中  main 12144");
                    Iterator iterator = trackPosMarkerInfoMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry map = (Map.Entry) iterator.next();
                        String no = map.getKey().toString();
                        Log.e(TAG, "trackHandler中  no：" + no);
                        List<PosMarkerInfo> pmiList = (List<PosMarkerInfo>) map.getValue();
                        if (pmiList != null && pmiList.size() > 0) {
                            // 加载到地图中
                            addPosOverlay(pmiList.get(pmiList.size() - 1), false);
                            Log.e("GIS模块","showMultipleWindow 3056");
                            Log.e(TAG, "trackHandler中  加载到地图中");
                        }
                    }

                    currentTime = trackStartPosTime;
                    Log.e(TAG, "trackHandler中  currentTime:" + currentTime);
                    txtPosCurrHour.setText(currentTime);
                    //显示进度条
                    ll_trackplay.setVisibility(View.VISIBLE);// 有历史GIS位置信息，显示轨迹播放控件
                    txtPosSpeed1.setText("1X");//重新开始初始化倍数
                    Log.e(TAG, "trackHandler中  isPosPlaying:" + isPosPlaying);
                    if (!isPosPlaying) {
                        isPosPlaying = true;
                        btnPosTrackPause.setText(R.string.title_postion_pause);
                        // 第一次播放，启动线程，当暂停再次播放的时候不再调用
                        if (isPosFirstPlaying) {
                            isPosFirstPlaying = false;
                            //proPosProgress.setMax(posMarkerInfosTraceList.size());// 设置进度条值
                            proPosProgress.setMax((int) differenceTimeSeconds);

                            // 启动播放轨迹线程
                            posTracePlayThread = new NewPosTracePlayThread();
                            new Thread(posTracePlayThread).start();
                            Log.e(TAG, "trackHandler中  启动播放轨迹线程");
                            MtcDelegate.log("播放轨迹线程启动");
                        } else {
                            posTracePlayThread.onPlay();
                        }
                    } else {
                        isPosPlaying = false;
                        posTracePlayThread.onPause();
                        btnPosTrackPause.setText(R.string.title_postion_play);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onMapLoaded() {
        // 地图加载完成回调

    }

    //文件服务器登录
    @Override
    public void OnSessionConnectStatus(int status) {
        // 2登录成功 0登录中   11/1 sws
		/*if (status == 2) {
			Log.e("文件服务器登录", "登录成功");
		} else if (status == 0) {
			Log.e("文件服务器登录", "登录中。。。。。。");
		} else if (status == 1) {
			Log.e("文件服务器登录", "登录失败");
			InstantClient.instance().ConnectSever(sipServer.getServerIp());
		}*/

    }

    @Override
    public void OnDispatchCmd(int cmd, int nSourceId, String SourceContent) {
        if (ServerCommand.UPLOADIMAGEFILE == cmd) {
            Message msg = new Message();
            msg.what = cmd;
            msg.arg1 = cmd;
            msg.arg2 = nSourceId;
            msg.obj = SourceContent;
            sendMessage(msg);
        }

    }

    private int sendMessage(Message msg) {
        mMainHandler.sendMessage(msg);
        return 0;
    }

    @Override
    public void OnUpLoadFile(String strFileName, int nStatus,
                             double nStatusReport) {
        // 0：准备上传，1：上传中，2：上传成功
		/*		String strInfo = strFileName + "|" + nStatus + "|" + nStatusReport
				+ "%";

		Message msg = new Message();
		msg.what = 10001;
		msg.obj = strInfo;
		sendMessage(msg);*/

        //***************

        // 上传完成
        if (nStatus == 2) {
            Intent intent = new Intent(
                    GlobalConstant.ACTION_MESSAGE_MSGSENDFILEOK);
            intent.putExtra("strFileName", strFileName);
            sendBroadcast(intent);
        }
    }

    @Override
    public void OnDownLoadFile(String strFileName, int nStatus,
                               double nStatusReport) {

        Log.e(TAG, "------" + strFileName + "," + nStatus + "," + nStatusReport);

        PTTService.isDownLoadNow = true;
        if (nStatus == 2) {
            if (ptt_3g_PadApplication.getDownConfig() != null) {
                String[] arr = ptt_3g_PadApplication.getDownConfig().split("\\|");
                Log.e(TAG, arr[4]);
                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGUPLOADOK);
                intent.putExtra("srcnum", arr[0]);
                intent.putExtra("filename", arr[1]);
                intent.putExtra("filepath", arr[2]);
                intent.putExtra("fileid", arr[3]);
                intent.putExtra("filetype", Integer.valueOf(arr[4]));
                sendBroadcast(intent);
                MessageNotification.NEWESGNUM++;
                new MessageNotification(this, R.drawable.unread_msg, arr[0]);

                //上报文件事件,通知服务器下载成功1-下载中；2-下载成功
                //String uri= MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                String msgBody = "ind:u_downevt\r\nemployeeid:" + sipUser.getUsername() + "\r\nfileid:" + arr[3] + "\r\ntype:2\r\n";
                //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
            }
            List<DownLoadFile> downLoadFileList = ptt_3g_PadApplication.getDownLoadFileList();
            if (downLoadFileList != null && downLoadFileList.size() > 0) {
                downLoadFileList.remove(0);
            }
            PTTService.isDownLoadNow = false;
        }
    }

    private class MainHandler extends Handler {
        public MainHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            onHandleMessage(msg);
        }
    }

    private Toast mToast;

    @SuppressWarnings("unused")
    protected void onHandleMessage(Message msg) {

       switch (msg.arg1){

           case 0:
               // 长方形搜索
               if (mBaiduMap != null) {
                   mBaiduMap.clear();// 先清空覆盖物
               }

               if (ll_pos_single.getVisibility() == View.VISIBLE) {
                   ll_pos_single.setVisibility(View.GONE);
               }
               if (ll_pos.getVisibility() == View.VISIBLE) {
                   ll_pos.setVisibility(View.GONE);
               }
               employeeLayout.removeAllViews();

               map_memberContent.setText("当前区域成员数量: "+ gisMemberCount);
               break;

           case 1:
               employeeLayout.setVisibility(View.GONE);
               //切换地图按钮状态Yuezs 2015-10-14
               switchPosButtonStatus(1);
               break;

           case 2:
                PosMarkerInfo posMarkerInfo = (PosMarkerInfo) msg.obj;
               if (posMarkerInfo != null){
                   // 加载到地图中
                   addPosOverlay(posMarkerInfo, false);
               }

               Log.e("GIS模块","GisBroadcastReceiver 6458  ***********");
					/*成员列表*/
               CheckBox check = new CheckBox(MainActivity.this);
               //分辨率
//               if (getwindowmanager() == 1280 && getwindowmanager_height() != 752) {
//                   Log.e(TAG, "4907=====分辨率判断已执行");
//                   LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//                   if (loc.length == 2) {
//                       if (i == 0) {
//                           //layoutParam.setMargins(-10, -20, -10, -10);
//                       } else if (i == 1) {
//                           Log.e(TAG, "4913=====loc.length==2    || i==1");
//                           layoutParam.setMargins(0, Integer.valueOf("-50"), Integer.valueOf("-10"), Integer.valueOf("-10"));
//                       }
//                   } else {
//                       Log.e(TAG, "4917=====loc.length != 2 ");
//                       layoutParam.setMargins(Integer.valueOf("-10"), Integer.valueOf("-20"), Integer.valueOf("-10"), Integer.valueOf("-10"));
//                   }
//
//                   check.setScaleX((float) 0.6);
//                   check.setScaleY((float) 0.6);
//                   check.setLayoutParams(layoutParam);
//               }
               //分辨率
               if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
                   check.setScaleX((float) 0.8);
                   check.setScaleY((float) 0.8);
                   check.setTextSize(11);
               }
               check.setText(posMarkerInfo.getName());
               check.setTag(posMarkerInfo);
               check.setTextColor(Color.WHITE);


               //Sws 2017-12-24add 解决全选时app无响应问题
               check.setOnTouchListener(new View.OnTouchListener() {
                   @Override
                   public boolean onTouch(View view, MotionEvent motionEvent) {

                       if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                           isCheck = true;
                       }
                       return false;
                   }
               });


               check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {

                       if (isLoadMarker){
                           ToastUtils.showToast(MainActivity.this,"GIS成员加载中");
                           return ;
                       }
                       // TODO Auto-generated method stub
                       if (isCheck) {
                           PosMarkerInfo markerinfo = (PosMarkerInfo) arg0.getTag();
                           //得到当前点击的CheckBox对应的PosMarkerInfo
									/*
									PosMarkerInfo中的属性
										private String name;		号码
										private double latitude;	经度
										private double longitude;	纬度
										private String time;		时间
										private int type;			类型
									 */
                           //判断当前marker是否存在集合中 如果存在 找到这个maiker并将它remove 避免出现画面重叠
                           if (hasMark.containsKey(markerinfo.getName())){
                               Marker marker = hasMark.get(markerinfo.getName());
                               marker.remove();
                               hasMark.remove(markerinfo.getName());
                           }

                           if (ischecked) {
                               //如果是选中状态  判断是否包含对应的Marker，如果不包含name那么添加
                               if (!checkBoxMarker.contains(markerinfo)) {
                                   checkBoxMarker.add(markerinfo);// 从新添加
                                   addPosOverlay(markerinfo, true);
                               }
                           }else {
                               //如果不是选中状态  判断是否包含对应的Marker，如果包含那么删除
                               if (checkBoxMarker.contains(markerinfo)) {
                                   checkBoxMarker.remove(markerinfo);// 从新添加
                                   addPosOverlay(markerinfo, false);
                               }
                           }

                           if (checkBoxMarker.size() == 1) {
                               ll_pos_single.setVisibility(View.VISIBLE);
                               ll_pos.setVisibility(View.GONE);
                           } else if (checkBoxMarker.size() > 1) {
                               ll_pos_single.setVisibility(View.GONE);
                               ll_pos.setVisibility(View.VISIBLE);
                           } else {
                               ll_pos_single.setVisibility(View.GONE);
                               ll_pos.setVisibility(View.GONE);
                           }

                           //关闭全选popupwindow
                           if (multiplePopupWindow != null) {
                               multiplePopupWindow.dismiss();
                           }
                           isCheck = false;
                       }
                   }
               });

               employeeLayout.addView(check);
               //切换地图按钮状态Yuezs 2015-10-14
               switchPosButtonStatus(2);
               break;

           case 3:
                	/*如果有成员就显示列表*/
               if (employeeLayout.getVisibility() != View.VISIBLE) {
                   employeeLayout.setVisibility(View.VISIBLE);
               }
               // 清空选中列表
               if (checkBoxMarker != null && checkBoxMarker.size() > 0) {
                   checkBoxMarker.clear();
               }
               break;

           case 4:
               map_memberContent.setText("当前区域成员数量: "+ gisMemberCount);
               break;
       }


        if (msg.what == ServerCommand.UPLOADIMAGEFILE) {
            String strInfo = null;
            if (msg.arg2 == 0) {
                strInfo = msg.obj + " 上传成功";
            } else {
                strInfo = msg.obj + " 上传失败";
            }
        } else if (msg.what == ServerCommand.DOWNLOADFILE) {
            String strInfo = null;
            if (msg.arg2 == 0) {
                strInfo = msg.obj + " 开始下载";
            } else {
                strInfo = msg.obj + " 无法下载";
            }

        } else if (msg.what == 10001) {

            mToast = Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT);
            mToast.setDuration(Toast.LENGTH_SHORT);
            //mToast.setDuration(1 * 1000);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }

    // 提示通话是否接听
    private void intercomShowCallingInfo(final CallingInfo callingInfo,
                                         final CallingInfo oldCallInfo)
    {
//        String message = "";
//        String title = "";
//        String positive = null;
//        String neutral = getString(R.string.btn_calling_answer);
//        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
//            message = getString(R.string.info_intercom_new_incoming1);
//            title = getString(R.string.info_intercom_new_incoming)
//                    + callingInfo.getRemoteName();
//        } else {
//            message = getString(R.string.info_intercom_new_calling1);
//            title = getString(R.string.info_intercom_new_calling)
//                    + callingInfo.getRemoteName();
//        }
//        if (callingInfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
//            positive = getString(R.string.btn_calling_videoanswer);
//            neutral = getString(R.string.btn_calling_voiceanswer);
//        }
//        CustomDialog.Builder builder = new CustomDialog.Builder(
//                MainActivity.this);
//        builder.setMessage(message);
//        builder.setTitle(title);


//        // 视频接听
//        if (positive != null) {
//            builder.setPositiveButton(positive,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            // 终止原来通话
//                            onEnd(oldCallInfo.getDwSessId());
//                            // 移除通话数据
//                            CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
//
//                            // 设置接听类型
//                            callingInfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
//
//                            // 添加新的通话
//                            CallingManager.getInstance().addCallingInfo(callingInfo);
//                            CallingManager.getInstance().addAnswerInfo(callingInfo);
//
//
//                            // 切换到通话activity
//                            if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
//                                mImageAdapter.setImageLight(1);
//                                mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
//                                SetViewShow(GlobalConstant.ONLYTAB);
//                                // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
//                            }
//                            Log.e("MainActivity======sws========", "====12199");
//                            // 视频回复
//                            onVideoAnswer(callingInfo.getDwSessId());
//
//                            // 来电广播
//                            Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
//                            callingIncoming.putExtra(KEY_CALL_CHANGEVIEW, true);
//                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
//                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callingInfo.getCalltype());
//                            callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, callingInfo.getRemoteName());
//                            sendBroadcast(callingIncoming);
//                            CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
//                            // 关闭页面
//                            dialog.dismiss();
//                            dialog = null;
//                            Log.e("==Sws测试响铃错误", "main     12738");
//
//                        }
//                    });
//        }
//        // 语音接听
//        builder.setNeutralButton(neutral,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO Auto-generated method stub
//                        // 设置接听类型
//                        callingInfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
//                        // 添加新的通话
//                        CallingManager.getInstance().addCallingInfo(callingInfo);
//                        CallingManager.getInstance().addAnswerInfo(callingInfo);
//
//				/*if(oldCallInfo.getCalltype() == GlobalConstant.CALL_TYPE_MEETING){
//							// 保持会议
//							oldCallInfo.setHolding(true);
//							MtcCall.Mtc_SessHold(oldCallInfo.getDwSessId());
//							// 通知接聽下一路通話
//							Intent answernext = new Intent(GlobalConstant.ACTION_CALLING_ANSWERNEXT);
//							sendBroadcast(answernext);
//						}else{*/
//                        // 终止原来通话
//                        onEnd(oldCallInfo.getDwSessId());
//
//                        // 移除通话数据
//                        CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
//
//                        //						}
//				/*//2017/12/06 Sws add   语音接听
//				Intent voiceIntent = new Intent(GlobalConstant.ACTION_CALLING_RESPONSE);
//				voiceIntent.putExtra("type","voice");
//				sendBroadcast(voiceIntent);*/
//
//                        // 通知接听
//                        // 切换到通话activity
//                        if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
//                            mImageAdapter.setImageLight(1);
//                            mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
//                            SetViewShow(GlobalConstant.ONLYTAB);
//
//                            Log.e("===============Main点击视频接听=====11630=======", "=========广播发送======");
//                            // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
//                        }
//
//				/*// 取得本地前置摄像头，并设置
//						int camCnt = MtcMedia.Mtc_VideoGetInputDevCnt();
//						if (camCnt >= 1) {
//							MtcMedia.Mtc_VideoSetInputDev("front");
//						}
//
//						// 取得远程摄像头
//						if (MtcCall.Mtc_SessPeerOfferVideo(callingInfo
//								.getDwSessId())) {
//							mtcCallDelegateStartPreview();
//						} else {
//							MtcProximity.start(MainActivity.this);
//						}
//
//						// 是否延迟提供音视频
//						mIsDelayOffer = MtcCall.Mtc_SessPeerOfferAudio(callingInfo.getDwSessId()) == false
//								&& MtcCall.Mtc_SessPeerOfferVideo(callingInfo.getDwSessId()) == false;
//
//						// 保证界面始终在屏幕上
//						keepScreenOn(true);
//						// 屏幕始终不锁定
//						showWhenLocked(true);
//*/
//                        // 音频回复
//                        onVoiceAnswer(callingInfo.getDwSessId());
//                        Log.e("MainActivity===========12326===", "接听了通话" + callingInfo.getDwSessId());
//
//                        // 来电广播
//                        Intent callingIncoming = new Intent(GlobalConstant.ACTION_CALLING_INCOMING);
//                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_CHANGEVIEW, false);
//                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_SESSIONID, callingInfo.getDwSessId());
//                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_TYPE, callingInfo.getCalltype());
//                        callingIncoming.putExtra(GlobalConstant.KEY_CALL_OPPOSITENO, callingInfo.getRemoteName());
//                        sendBroadcast(callingIncoming);
//                        Log.e("==Sws测试响铃错误", "main     12821");
//
//                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
//                        // 关闭页面
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                });
//        // 忽略
//        builder.setNegativeButton(getString(R.string.btn_calling_decline),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.w("MainActiviy-11347", "lizhiwei 忽略挂断");
//                        // 终止新的通话
//                        onEnd(callingInfo.getDwSessId());
//                        Log.e("--------忽略新来电-----------", "--------------DwSessId:" + callingInfo.getDwSessId() + "---对方号码：" + callingInfo.getRemoteName());
//
//                        // 关闭提示框
//                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
//                        dialog.dismiss();
//                        dialog = null;
//
//                        // 增加通话记录
//				/*SQLiteHelper sqLiteHelper = new SQLiteHelper(
//								MainActivity.this);
//						sqLiteHelper.open();
//						CallingRecords callingRecord = new CallingRecords();
//						callingRecord.setUserNo(sipUser.getUsername());
//						callingRecord.setBuddyNo(callingInfo.getRemoteName());
//						Date nowDate = new Date();
//						callingRecord.setStartDate(nowDate);
//						callingRecord.setAnswerDate(nowDate);
//						callingRecord.setStopDate(nowDate);
//						callingRecord.setDuration(0);
//						callingRecord.setTime(FormatController.secToTime(0));
//						callingRecord.setInOutFlg(1);
//						callingRecord.setCallState(0);// 忽略，未接通
//						callingRecord.setSessId(callingInfo.getDwSessId());
//						sqLiteHelper.createCallingRecords(callingRecord);
//						sqLiteHelper.closeclose();*/
//                    }
//                });
//
//        WaitingDialog hasWaitingDialog = CallingManager.getInstance().getCallingDialog(callingInfo.getDwSessId());
//        if (hasWaitingDialog != null) {
//            return;
//        }
//        final WaitingDialog informing = builder.create();
//        informing.setCancelable(false);
//        informing.setDwSessId(callingInfo.getDwSessId());
//        informing.show();
//        CallingManager.getInstance().addCallingDialog(informing);
//
//        // 10秒无操作自动关闭
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                MtcDelegate.log("lizhiwei 超时挂断");
//                if ((informing != null) && (informing.isShowing())) {
//                    // 终止新的通话
//                    onEnd(callingInfo.getDwSessId());
//                    // 关闭提示框
//                    CallingManager.getInstance().removeCallingDialog(informing.getDwSessId());
//                    informing.dismiss();
//
//                    // 增加通话记录
//					/*SQLiteHelper sqLiteHelper = new SQLiteHelper(
//							MainActivity.this);
//					sqLiteHelper.open();
//					CallingRecords callingRecord = new CallingRecords();
//					callingRecord.setUserNo(sipUser.getUsername());
//					callingRecord.setBuddyNo(callingInfo.getRemoteName());
//					Date nowDate = new Date();
//					callingRecord.setStartDate(nowDate);
//					callingRecord.setAnswerDate(nowDate);
//					callingRecord.setStopDate(nowDate);
//					callingRecord.setDuration(0);
//					callingRecord.setTime(FormatController.secToTime(0));
//					callingRecord.setInOutFlg(1);
//					callingRecord.setCallState(0);// 忽略，未接通
//					callingRecord.setSessId(callingInfo.getDwSessId());
//					sqLiteHelper.createCallingRecords(callingRecord);
//					sqLiteHelper.closeclose();*/
//                }
//            }
//        }, 10000);
    }

    // 提示通话是否接听
    private void showCallingInfo(final CallingInfo callingInfo,
                                 final CallingInfo oldCallInfo) {

        Log.e(TAG, "进入  showCallingInfo");

        String message = "";
        String title = "";
        String positive = null;
        String neutral = getString(R.string.btn_calling_answer);
        if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
            message = getString(R.string.info_intercom_new_incoming1);
            title = getString(R.string.info_intercom_new_incoming)
                    + callingInfo.getRemoteName();
        } else {
            message = getString(R.string.info_intercom_new_calling1);
            title = getString(R.string.info_intercom_new_calling)
                    + callingInfo.getRemoteName();
        }
        if (callingInfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
            positive = getString(R.string.btn_calling_videoanswer);
            neutral = getString(R.string.btn_calling_voiceanswer);
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(
                MainActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);

//        // 增加通话记录
//        SQLiteHelper sqLiteHelper = new SQLiteHelper(
//                MainActivity.this);
//        sqLiteHelper.open();
//        CallingRecords callingRecord = new CallingRecords();
//        callingRecord.setUserNo(sipUser.getUsername());
//        callingRecord.setBuddyNo(callingInfo.getRemoteName());
//        Date nowDate = new Date();
//        callingRecord.setStartDate(nowDate);
//        callingRecord.setAnswerDate(nowDate);
//        callingRecord.setStopDate(nowDate);
//        callingRecord.setDuration(0);
//        callingRecord.setTime(FormatController.secToTime(0));
//        callingRecord.setInOutFlg(1);
//        callingRecord.setCallState(0);// 忽略，未接通
//        callingRecord.setSessId(callingInfo.getDwSessId());
//        sqLiteHelper.createCallingRecords(callingRecord);
//        sqLiteHelper.closeclose();

        // 视频接听
        if (positive != null) {
            builder.setPositiveButton(positive,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "进入  视频接听监听");
                            // 终止原来通话
                            Log.e(TAG, "终止原来通话    12496: Old DWSessId:" + oldCallInfo.getDwSessId());
                            Log.e(TAG, "终止原来通话    12496: 当前 DWSessId:" + callingInfo.getDwSessId());
                            onEnd(oldCallInfo.getDwSessId());
                            // 移除通话数据
                            CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
                            // 切换到通话activity
                            if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
                                mImageAdapter.setImageLight(1);
                                mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                                SetViewShow(GlobalConstant.ONLYTAB);
                                // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
                            } else {
//                                Intent intent = new Intent(GlobalConstant.ACTION_CALLING_SURFACEVIEW);
//                                sendBroadcast(intent);
                            }


                            // 设置接听类型
                            callingInfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO);
                            // 添加新的通话
                            CallingManager.getInstance().addCallingInfo(callingInfo);
                            CallingManager.getInstance().addAnswerInfo(callingInfo);

                            Log.e(TAG, "====12455");
                            // 来电广播
                            CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
                            // 关闭页面
                            dialog.dismiss();
                            dialog = null;
                        }
                    });
        }
        // 语音接听
        builder.setNeutralButton(neutral,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 设置接听类型

                        // 终止原来通话
                        onEnd(oldCallInfo.getDwSessId());

                        // 移除通话数据
                        CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());

                        // 通知接听
                        // 切换到通话activity
                        if (mTabHost.getCurrentTabTag() != GlobalConstant.CALLING_TAB) {
                            mImageAdapter.setImageLight(1);
                            mTabHost.setCurrentTabByTag(GlobalConstant.CALLING_TAB);
                            SetViewShow(GlobalConstant.ONLYTAB);

                            Log.e(TAG, "=========广播发送======");
                            // 第二通电话如果是视频通话则需要将视频控件调出，如果是予以通话则将视频空间隐藏
                        }

                        callingInfo.setAnswerType(GlobalConstant.CONFERENCE_MEDIATYPE_VOICE);
                        // 添加新的通话
                        CallingManager.getInstance().addCallingInfo(callingInfo);
                        CallingManager.getInstance().addAnswerInfo(callingInfo);


                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
                        // 关闭页面
                        dialog.dismiss();
                        dialog = null;
                    }
                });
        // 忽略
        builder.setNegativeButton(getString(R.string.btn_calling_decline),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "lizhiwei 忽略挂断");
                        // 终止新的通话
                        onEnd(callingInfo.getDwSessId());
                        Log.e(TAG, "--------------DwSessId:" + callingInfo.getDwSessId() + "---对方号码：" + callingInfo.getRemoteName());
                        // 关闭提示框
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
                        dialog.dismiss();
                        dialog = null;
                    }
                });

        WaitingDialog hasWaitingDialog = CallingManager.getInstance().getCallingDialog(callingInfo.getDwSessId());
        if (hasWaitingDialog != null) {
            return;
        }
        final WaitingDialog informing = builder.create();
        informing.setCancelable(false);
        informing.setDwSessId(callingInfo.getDwSessId());
        informing.show();
        CallingManager.getInstance().addCallingDialog(informing);

        // 10秒无操作自动关闭
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                MtcDelegate.log("lizhiwei 超时挂断");
                if ((informing != null) && (informing.isShowing())) {
                    // 终止新的通话
                    onEnd(callingInfo.getDwSessId());
                    // 关闭提示框
                    CallingManager.getInstance().removeCallingDialog(informing.getDwSessId());
                    informing.dismiss();
                }
            }
        }, 10000);
    }

    // 提示是否退出
    private void askIfExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_main_exitinfo));
        builder.setMessage(getString(R.string.title_main_ifexit));
        builder.setPositiveButton(getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // 清空对讲组
                        GroupManager.getInstance().clear();
                        dialog.dismiss();
                        // 通话挂断
                        for (CallingInfo callinginfo : CallingManager.getInstance()
                                .getCallingData()) {
                            Log.w("MainActivity-退出", "lizhiwei 销毁挂断");
                            onEnd(callinginfo.getDwSessId());
                        }
                        CallingManager.getInstance().clear();
                        // 登出
                        //MtcDelegate.logout();
				/*MediaEngine.GetInstance().ME_UnRegist();
						MediaEngine.GetInstance().ME_Destroy(); */
                        //MainActivity.this.finish();//直接关掉程序会有问题，需要注销当前账号，否则下次再登陆就登陆不上了


                        new SettingExitFragment(MainActivity.this).exit(); //之前的退出操作

                        //MainActivity.this.finish();
				/* Runtime.getRuntime().gc();
					    android.os.Process.killProcess(android.os.Process.myPid());*/

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

    @Override
    public void onMapStatusChange(MapStatus arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus arg0) {
        if (ptt_3g_PadApplication.isNetConnection() == true) {
            if (isTrace == false) {
                getPosRectangleGis();

            }
        } else {
            ToastUtils.showToast(MainActivity.this,
                    getString(R.string.info_network_unavailable));
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    // 获取矩形内gis消息
    @SuppressWarnings("unused")
    private void getPosRectangleGis() {

        if (mBaiduMap != null) {
//			/*DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int screenWidth = dm.widthPixels;
//		int screenHeigh = dm.heightPixels;
//		int targetScreenX = mBaiduMap.getMapStatus().targetScreen.x;// 地图操作中心点在屏幕中的坐标x
//		int targetScreenY = mBaiduMap.getMapStatus().targetScreen.y;// 地图操作中心点在屏幕中的坐标y
//		int navHeight = screenHeigh - 2 * targetScreenY;// 导航条(除地图之外的部分)的高度
//
//		// Point rightup = new Point(screenWidth, navHeight);
//		// Point leftdown = new Point(0, screenHeigh);
//		Point rightup = new Point(screenWidth, 45);
//		Point leftdown = new Point(82 + 160, 45 + mMapView.getHeight());
//		LatLng northeast = mBaiduMap.getProjection()
//				.fromScreenLocation(rightup);// 东北坐标(大)
//		LatLng southwest = mBaiduMap.getProjection().fromScreenLocation(
//				leftdown);// 西南坐标(小)
//		double lngmin = southwest.longitude;
//		double lngmax = northeast.longitude;
//		double latmin = southwest.latitude;
//		double latmax = northeast.latitude;
//		String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID,
//				false);*/
//            //String uri = MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID,false);
            mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
                @Override
                public void onMapStatusChangeStart(MapStatus mapStatus) {
                    Log.e("mBaiduMap", "onMapStatusChangeStart");
                }

                @Override
                public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                    Log.e("mBaiduMap", "onMapStatusChangeStart");

                }

                @Override
                public void onMapStatusChange(MapStatus mapStatus) {
                    Log.e("mBaiduMap", "onMapStatusChange");

                }

                @Override
                public void onMapStatusChangeFinish(MapStatus mapStatus) {
                    Log.e("mBaiduMap", "onMapStatusChangeFinish");

                    //此时地图绘制完成
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int screenWidth = dm.widthPixels;
                    int screenHeigh = dm.heightPixels;
                    int targetScreenX = mBaiduMap.getMapStatus().targetScreen.x;
                    int targetScreenY = mBaiduMap.getMapStatus().targetScreen.y;
                    int navHeight = screenHeigh - 2 * targetScreenY;

                    Point rightup = new Point(screenWidth, navHeight - 45);
                    Point leftdown = new Point(0, screenHeigh + 45);

                    LatLng northeast = mBaiduMap.getProjection()
                            .fromScreenLocation(rightup);
                    LatLng southwest = mBaiduMap.getProjection().fromScreenLocation(
                            leftdown);

                    double latmin = southwest.latitude;
                    double lngmin = southwest.longitude;
                    double latmax = northeast.latitude;
                    double lngmax = northeast.longitude;

//                    TODO SipMsg方式 2018-09-20 Sws修改为ICE方式
//                    String msgBodyString = "req:pos_by_rectangle\r\nemployeeid:"
//                            + sipUser.getUsername() + "\r\nlngmin:" + lngmin
//                            + "\r\nlngmax:" + lngmax + "\r\nlatmin:" + latmin
//                            + "\r\nlatmax:" + latmax + "\r\ntype:-1\r\n";
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);

                    //TODO 参数说明 经度最小值，经度最大值，纬度最小值，纬度最大值，Type（-1为所有类型）
                    MediaEngine.GetInstance().ME_GetGisInfoByEllipse_Async(lngmin, lngmax, latmin, latmax, -1, new MediaEngine.ME_GetGisInfoByEllipse_CallBack() {
                        @Override
                        public void onCallBack(MediaEngine.ME_GisInfo[] me_gisInfos) {
                            //获取矩形gis信息
                            String gisInfoContent = "";
                            for (MediaEngine.ME_GisInfo gisInfo : me_gisInfos) {
                                gisInfoContent = gisInfoContent + gisInfo.userNumber + "," + gisInfo.longitude + "," + gisInfo.latitude + "," + gisInfo.time + "," + gisInfo.userType + ";";
                            }
                            Intent intent = new Intent(GlobalConstant.ACTION_GIS_RECTANGLEPOS);
                            intent.putExtra("rectangle_pos", gisInfoContent);
                            sendBroadcast(intent);
                            Log.e("PTTServiceGIS相关", "PTTService====获取矩形gis信息 strMsg[1] : " + gisInfoContent);
                        }
                    });
                }
            });
        }
    }

    // 视频监控删除视频，每次切换activity检查地图是否存在
    @SuppressWarnings("unused")
    private void checkMapView() {
        if (mMapView == null) {
			/*mMapView = new MapView(this);
			mMapView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			ll_position.addView(mMapView);
			mBaiduMap = mMapView.getMap();
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
			mBaiduMap.setOnMapStatusChangeListener(this);*/

            localMapList = mOffline.getAllUpdateInfo();
            if (localMapList == null) {
                localMapList = new ArrayList<MKOLUpdateElement>();
            }
            if (localMapList != null && localMapList.size() > 0) {
                ll_position.removeAllViews();
                int offlineMapCityId = prefs.getInt(GlobalConstant.SP_OFFLINEMAP_CITYID, -1);
                if (offlineMapCityId == -1) {
                    MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                    mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                    ll_position.addView(mMapView);
                    int cityID = mkolUpdateElement.cityID;
                    ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                    editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                    editor.apply();
                } else {
                    MKOLUpdateElement mk = null;
                    for (MKOLUpdateElement mkolUpdateElement : localMapList) {
                        if (mkolUpdateElement.cityID == offlineMapCityId) {
                            mk = mkolUpdateElement;
                        }
                    }
                    if (mk == null) {
                        MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
                        mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
                        ll_position.addView(mMapView);
                        int cityID = mkolUpdateElement.cityID;
                        ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                        editor.putInt(GlobalConstant.SP_OFFLINEMAP_CITYID, cityID);
                        editor.apply();
                    } else {
                        mMapView = new MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mk.geoPt).build()));
                        ll_position.addView(mMapView);
                        int cityID = mk.cityID;
                        ptt_3g_PadApplication.setOfflineMapCityId(cityID);
                    }
                }


                mBaiduMap = mMapView.getMap();
                mMapView.invalidate();
                mBaiduMap.setMyLocationEnabled(true);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
                mBaiduMap.setOnMapLoadedCallback(this);
                mBaiduMap.setOnMapStatusChangeListener(this);
                // 删除百度地图logo
                mMapView.removeViewAt(1);
                View child = mMapView.getChildAt(1);
                // 隐藏缩放控件
                child.setVisibility(View.GONE);
                // 隐藏比例尺
                mMapView.removeViewAt(2);
                mUiSettings = mBaiduMap.getUiSettings();
                //是否启用缩放手势
                mUiSettings.setZoomGesturesEnabled(true);
                //是否启用平移手势
                mUiSettings.setScrollGesturesEnabled(true);
                //是否启用旋转手势
                mUiSettings.setRotateGesturesEnabled(false);
                //是否启用俯视手势
                mUiSettings.setOverlookingGesturesEnabled(false);
                //是否启用指南针图层
                mUiSettings.setCompassEnabled(false);
            } else {
                mMapView = (MapView) findViewById(R.id.bmapView);
                mBaiduMap = mMapView.getMap();
                mMapView.invalidate();
                mBaiduMap.setMyLocationEnabled(true);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
                mBaiduMap.setOnMapLoadedCallback(this);
                mBaiduMap.setOnMapStatusChangeListener(this);
                mMapView.removeViewAt(1);
                View child = mMapView.getChildAt(1);
                // 隐藏缩放控件
                child.setVisibility(View.GONE);
                // 隐藏比例尺
                mMapView.removeViewAt(2);

                mUiSettings = mBaiduMap.getUiSettings();
                //是否启用缩放手势
                mUiSettings.setZoomGesturesEnabled(true);
                //是否启用平移手势
                mUiSettings.setScrollGesturesEnabled(true);
                //是否启用旋转手势
                mUiSettings.setRotateGesturesEnabled(false);
                //是否启用俯视手势
                mUiSettings.setOverlookingGesturesEnabled(false);
                //是否启用指南针图层
                mUiSettings.setCompassEnabled(false);
            }

        }
    }

    private static int currVolume = 0;

    //判断用户网络是否连接
    public void JudgeNetConnected() {
        if (ptt_3g_PadApplication.isNetConnection() == false) {
            ToastUtils.showToast(MainActivity.this,
                    getString(R.string.info_network_unavailable));
            return;
        }
    }

    // 提示通话是否继续发起
    public Dialog showmainIfContinue(final CallingInfo oldCallInfo, final String callUserNo, final boolean isVideo) {

        String message = "";
        String title = "";
        String positive = null;
        title = getString(R.string.info_ifContinue);
        positive = getString(R.string.info_continue);
        CustomDialog.Builder builder = new CustomDialog.Builder(
                MainActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);
        // 继续发起
        //				if (positive != null) {
        builder.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 终止原来通话
				/*Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
									callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, oldCallInfo.getDwSessId());
									sendBroadcast(callIntent);*/
                        onEnd(oldCallInfo.getDwSessId());
                        // 移除通话数据
                        CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());

                        // 关闭页面
                        dialog.dismiss();
                        dialog = null;
                        mtcCallDelegateCall(null, callUserNo, isVideo);

                    }
                });
        //				}
        // 终止对讲
        builder.setNegativeButton(getString(R.string.info_notcontinue),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 关闭提示框
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
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

    // 提示通话是否继续发起
    public Dialog showMeetingIfContinueString(final CallingInfo oldCallInfo, final ArrayList<String> nos, final String tabType) {

        String message = "";
        String title = "";
        String positive = null;
        title = getString(R.string.info_ifContinue);
        positive = getString(R.string.info_continue);
        CustomDialog.Builder builder = new CustomDialog.Builder(
                MainActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);

        // 继续发起
        //				if (positive != null) {
        builder.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
				/*// 终止原来通话
									Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
									callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, oldCallInfo.getDwSessId());
									sendBroadcast(callIntent);*/
                        onEnd(oldCallInfo.getDwSessId());
                        // 移除通话数据
                        CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());

                        // 关闭页面
                        dialog.dismiss();
                        dialog = null;

                        // 切换页面
                        if (mTabHost.getCurrentTabTag() != GlobalConstant.MEETING_TAB) {
                            mTabHost.setCurrentTabByTag(GlobalConstant.MEETING_TAB);
                            mImageAdapter.setImageLight(2);
                            startActivity(2);
                        }
                        Intent callIntent2 = new Intent(
                                GlobalConstant.ACTION_POSITION_OPERATE);
                        callIntent2.putExtra(GlobalConstant.KEY_POSITION_TYPE,
                                tabType);
                        callIntent2.putExtra(GlobalConstant.KEY_MEETING_NOS, nos);
                        sendBroadcast(callIntent2);

                        // 清空地图marker
                        if (mBaiduMap != null) {
                            mBaiduMap.clear();// 先清空覆盖物
                        }
                        if (checkBoxMarker != null) {
                            checkBoxMarker.clear();
                        }
                        if (posMarkerInfosList != null) {
                            for (PosMarkerInfo pmi : posMarkerInfosList) {
                                addPosOverlay(pmi, false);
                                Log.e("GIS模块","showMeetingIfContinueString 14343");
                            }
                        }
                    }
                });
        //				}
        // 终止对讲
        builder.setNegativeButton(getString(R.string.info_notcontinue),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 关闭提示框
                        CallingManager.getInstance().removeCallingDialog(((WaitingDialog) dialog).getDwSessId());
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

    //判断是否为轨迹回放模式
    private Boolean IsTrackmodel() {
        if (ll_trackplay.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    @Override
    public void onGetOfflineMapState(int arg0, int arg1) {
        int type = arg0;
        int state = arg1;
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                // 离线地图下载更新事件类型
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                ToastUtils.showToast(this, String.format("%s : %d%%", update.cityName,
                        update.ratio));
                break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装

                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                break;
        }

    }

    class LogoutBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalConstant.ACTION_LOGOUT)) {
                logoutOk();
            }
        }
    }

    // 登出系统
    private void logoutOk() {
        startActivityForResult(new Intent(this, LoginActivity.class),
                REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            exit();
        }
    }

    private void exit() {
        finish();
    }

    //执行离线短信
    class OldMsgDataAsyncTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... params) {

            Log.e(TAG, "执行离线短信");
            // TODO Auto-generated method stub

            //获取离线短信及条数
            MediaEngine.GetInstance().ME_GetOfflineMessage_Async(new MediaEngine.ME_GetOfflineMessage_CallBack() {
                @Override
                public void onCallBack(MediaEngine.ME_Message[] me_messages, MediaEngine.ME_MessageFile[] me_messageFiles) {

                    if (me_messages.length != 0 || me_messageFiles.length != 0) {
                        if (PTTService.downLoadFileList.size() > 0) {
                            PTTService.downLoadFileList.clear();
                        }

                        // 开启定时器
                        if (PTTService.downLoadFileList != null) {
                            if (isRunningDownLoadFile == false) {
                                Intent intent = new Intent("com.azkj.pad.startTimer");
                                sendBroadcast(intent);
                            }
                        }

                        Log.e(TAG, "测试离线短信文本数量message2Ts:" + me_messages.length);
                        Log.e(TAG, "测试离线短信文件数量uploadEvent2Ts:" + me_messageFiles.length);
                        fileCount = me_messageFiles.length;

                        if (me_messages != null && me_messages.length > 0) {
                            for (MediaEngine.ME_Message me_message : me_messages) { //Message2T
                                String msgid = me_message.msgid;
                                String nbody = me_message.body;
                                String sendid = me_message.sender;
                                String time = me_message.time;
                                String[] receiver = me_message.receivers;
                                MediaEngine.ME_MessageFile[] yyyy = me_message.fileList;

                                // 短消息
                                if (nbody.length() > 0) {
                                    Intent intent = new Intent(
                                            GlobalConstant.ACTION_MESSAGE_MSGRECEIVEOK);
                                    intent.putExtra("userNo", sendid);
                                    intent.putExtra("content", nbody);
                                    sendBroadcast(intent);
                                    MessageNotification.NEWESGNUM++;
                                    new MessageNotification(
                                            MainActivity.this,
                                            R.drawable.unread_msg, sendid);
//										Intent intentMSG = new Intent(GlobalConstant.ACTION_GETOLDMSG_MUSIC);
//										sendBroadcast(intentMSG);
                                    try {
                                        Thread.sleep(150);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }

                        if (me_messageFiles != null
                                && me_messageFiles.length > 0) {
                            for (MediaEngine.ME_MessageFile me_messageFile : me_messageFiles) {
                                Log.e(TAG,
                                        "====Sws测试离线短信");
                                Log.e(TAG, "fileid: " + me_messageFile.fileid);
                                Log.e(TAG, "fileName: "
                                        + me_messageFile.fileName);
                                Log.e(TAG, "filePath: "
                                        + me_messageFile.filePath);
                                Log.e(TAG, "time: "
                                        + me_messageFile.time);
                                Log.e(TAG, "sender: "
                                        + me_messageFile.sender);
                                Log.e(TAG, "receivers: "
                                        + me_messageFile.receivers);
                                for (String dn : me_messageFile.receivers) {
                                    Log.e(TAG, "###" + dn);
                                }

                                // 文件下载状态
                                int fileState = me_messageFile.fileState;
                                Log.e(TAG, "fileState: "
                                        + fileState);
                                // 文件类型
                                int filetype = me_messageFile.fileType;
                                Log.e(TAG, "int fileType: " + filetype);
                                if (filetype != -1) {
                                    if (fileState == 2) {
                                        // 下载通知
                                        String srcnum = me_messageFile.sender;
                                        String filename = me_messageFile.fileName;
                                        String filepath = me_messageFile.filePath;
                                        String fileid = me_messageFile.fileid;
                                        String serverpath = filepath;
                                        Log.e(TAG, "回调回来的值： srcnum:" + srcnum + ",  filename:" + filename + ",  filepath:" + filepath + ",  fileid:" + fileid + ",  filetype:" + filetype);
                                        serverpath = serverpath.substring(
                                                0,
                                                serverpath.lastIndexOf('/') + 1);
                                        String downName = filepath.substring(
                                                filepath.lastIndexOf('/') + 1,
                                                filepath.length());

                                        DownLoadFile dFile = new DownLoadFile();
                                        dFile.setSrcnum(srcnum);
                                        dFile.setFilename(filename);
                                        dFile.setFilepath(filepath);
                                        dFile.setFileid(fileid);
                                        dFile.setServerpath(serverpath);
                                        dFile.setDownName(downName);
                                        dFile.setFileType(filetype);
                                        PTTService.downLoadFileList.add(dFile);
                                        Log.e(TAG, "PTTService.downLoadFileList.size(): "
                                                + PTTService.downLoadFileList.size());
                                        Log.e(TAG, "MainActivity中 downLoadFileList.size(): "
                                                + PTTService.downLoadFileList.size());
                                    }
                                }
                            }
                        }
                    }
                }
            }, 50);


            MediaEngine.GetInstance().ME_GetLocalPrefix_Async(new MediaEngine.ME_GetLocalPrefix_callback() {
                @Override
                public void onCallback(String prefix) {
                    Log.e(TAG, "prefix : " + prefix);
                    ptt_3g_PadApplication.setPrefix(prefix);
                }
            });

            //获取配置信息
            String[] strings = {"CallResolution", "MessagePort", "ISUSEMCU", "CallFrameValue", "CallRateValue"};
            MediaEngine.ME_ConfigItem[] me_configItems = MediaEngine.GetInstance().ME_GetConfigByKeys(strings);
            if (me_configItems == null
                    || me_configItems.length <= 0) {
                return null;
            }
            for (MediaEngine.ME_ConfigItem me : me_configItems) {
                Editor editor = prefs.edit();
                switch (me.getKey()) {
                    case "CallResolution":
                        editor.putString(GlobalConstant.SP_MONITOR_RESOLUTION, me.getValue());
                        break;

                    case "MessagePort":
                        editor.putString("settingOtherport", me.getValue());
                        break;

                    case "ISUSEMCU":
                        editor.putString("ISMCU", me.getValue());
                        break;

                    case "CallFrameValue":
                        editor.putString("CallFrameValue", me.getValue());
                        break;

                    case "CallRateValue":
                        editor.putString("CallRateValue", me.getValue());
                        break;
                }
                editor.apply();
            }
            return "True";
        }
    }

    MediaPlayer mMediaPlayer = null;


    //播放铃声
    private void startAlarm() {
        if (audioManager == null) {
            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            //设置声音模式
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());
        }
        //关闭麦克风
        audioManager.setMicrophoneMute(false);
        Log.e(TAG, "测试麦克风 main    13609   取消静音");
        // 打开扬声器
        audioManager.setSpeakerphoneOn(true);
        int currVolume = ptt_3g_PadApplication.getCurrentVolume();
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                currVolume,
                AudioManager.STREAM_VOICE_CALL);
        Log.e(TAG, "测试扬声器 main    13607   true");

        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());

        //mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, " mMediaPlayer.start===========");
        mMediaPlayer.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
    }

    //离线短信提示音
    class OLDMSGMUSICReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //PlayRingTone(); //播放音乐
                            }
                        });
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    //键位适配
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("Mainpppppppppppppppp", "keyCode : " + keyCode + "\r\n" + "Build.MODEL : " + Build.MODEL);
        Log.e("文胜的百宝箱", "Main 进入onkeydown   1");
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            Log.e("文胜的百宝箱", "Main 进入onkeydown    down");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            Log.e("文胜的百宝箱", "Main 进入onkeydown up");
            return true;
        } else if ("Runbo-NBBS-P1".equals(Build.MODEL)) {
            Log.e("文胜的百宝箱", "Main 进入Runbo-NBBS-P1");
            if (audioManager != null) {
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            }

            if (keyCode == 302) {
                if (audioManager != null) {
                    if (volume == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                        volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    } else {
                        volume += 1;
                    }
                }
            }

            if (keyCode == 301) {
                if (volume == 1) {
                    volume = 1;
                } else {
                    volume -= 1;
                }
            }

            if (audioManager != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                        AudioManager.FLAG_PLAY_SOUND);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.e("文胜的百宝箱", "进入onkeyup");
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            setVolumeUtils.showPopupWindow(false, getWindow().getDecorView());
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            setVolumeUtils.showPopupWindow(true, getWindow().getDecorView());
            return true;
        } else return super.onKeyUp(keyCode, event);
    }

    //会议开始之前 根据会议类型进行布局切换
    class MeetingIsMcuReceive extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            String meetingType = intent.getStringExtra("meetingType");
            if (meetingType.equals("Error2")) {
                Log.e("MoveYourBaby", "ICE连接失败 请检查网络");
                ToastUtils.showToast(MainActivity.this, "ICE连接失败 请检查网络");
                //添加地图
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                 if (ptt_3g_PadApplication.isBaiduMapisRemove()) {
//                     initBaiDuMap2();
//
//                     initBaiduMap();
//
//                     String msgBodyString = "req:cur_pos\r\nemployeeid:" + sipUser.getUsername() + "\r\ndstid:" + sipUser.getUsername() + "\r\n";
//                     MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//
//                     ptt_3g_PadApplication.setBaiduMapisRemove(false);
//                 }
                return;
            } else if (meetingType.equals("Error")) {
                Log.e("MoveYourBaby", "收到会议创建失败广播");
                ToastUtils.showToast(MainActivity.this, "会议创建失败 请检查网络");
                //添加地图
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                 if (ptt_3g_PadApplication.isBaiduMapisRemove()) {
//                     initBaiDuMap2();
//
//                     initBaiduMap();
//
//                     String msgBodyString = "req:cur_pos\r\nemployeeid:" + sipUser.getUsername() + "\r\ndstid:" + sipUser.getUsername() + "\r\n";
//                     MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//
//                     ptt_3g_PadApplication.setBaiduMapisRemove(false);
//                 }
                return;
            } else if (Integer.valueOf(meetingType) == GlobalConstant.ONLYTAB) {
                ToastUtils.showToast(MainActivity.this, "会议模式：MCU会议");
                SetViewShow(Integer.valueOf(meetingType));
//                 if (!ptt_3g_PadApplication.isBaiduMapisRemove()){
//                     //移除地图
//                     if (mMapView != null){
//                         mMapView.onDestroy();
//                     }
//                     ll_position.setVisibility(View.GONE);
//                     ll_position.removeView(mMapView);
//                     ptt_3g_PadApplication.setBaiduMapisRemove(true);
//                 }

            } else if (Integer.valueOf(meetingType) == GlobalConstant.ALL) {
                ToastUtils.showToast(MainActivity.this, "会议模式：普通会议");
//                 if (sl_videos.getVisibility() != View.VISIBLE){
//                     sl_videos.setVisibility(View.VISIBLE);
//                 }
//                 if (rl_bottomtools.getVisibility() != View.VISIBLE){
//                     rl_bottomtools.setVisibility(View.VISIBLE);
//                 }
                SetViewShow(Integer.valueOf(meetingType));
            }

        }
    }

    //MCU会议解码开始于结束
    class MCUDecodeStartAndEndReceive extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            String number = intent.getStringExtra("number");
            if (number == null) {
                return;
            }
            if (number.equals("")) {
                return;
            }
            switch (intent.getAction()) {
                //开始解码上墙
                case GlobalConstant.ACTION_MCUMEETING_STARTDecode:
                    //showdcodupperwallmenu(number);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    new AsyncDecodeTheUpperWall().execute(number);
                    break;
                //停止解码上墙
                case GlobalConstant.ACTION_MCUMEETING_EndDecode:

                    if (decodeMap.containsKey(number)) {
                        DecoderBean decoderBean = decodeMap.get(number);
                        String sessionid = decoderBean.getSessionId();
                        String decoderId = decoderBean.getDecoderId();
                        String chanidString = decoderBean.getChanidStrings();
                        //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
                        selectedDecoderEnd(sessionid, number, info.decoderId, Integer.valueOf(chanidString));
                        return;
                    }
                    ToastUtils.showToast(MainActivity.this, "未找到上墙视频");
                    break;
            }


        }
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    private boolean isFloating = false;
    private boolean isMove;//判断悬浮窗是否移动
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View mWindowView;

    //接收摄像头云控消息
    class CameraCloudControlReceive extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case GlobalConstant.ACTION_CAMERA_HIDE_CLOUDCONTROL:

                    if (mPopUpWindow != null) {
                        if (mPopUpWindow.isShowing()) {
                            mPopUpWindow.dismiss();
                        }
                    }

                    break;
                case GlobalConstant.ACTION_CAMERA_CLOUDCONTROL:

                    if (mWindowView != null) {
                        Log.e(TAG, "return   14033");
                        //移除悬浮窗口
                        return;
                    }

                    //得到号码
                    cameraCloudnumber = intent.getStringExtra("number");
                    Log.e(TAG, "=================" + cameraCloudnumber);
                    if (cameraCloudnumber == null) {
                        return;
                    }
                    if (cameraCloudnumber.equals("")) {
                        return;
                    }
                    mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
                    wmParams = new WindowManager.LayoutParams();
                    wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                    wmParams.format = PixelFormat.TRANSLUCENT;
                    wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    wmParams.gravity = Gravity.LEFT | Gravity.TOP;
                    wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    wmParams.x = 180;
                    wmParams.y = 240;
                    mWindowView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_camera_cloudcontrol, null);


                    mWindowView.setOnTouchListener(new FloatingListener());
                    mWindowManager.addView(mWindowView, wmParams);


                    btn_sertViewGONEorVISIVITY = (Button) mWindowView.findViewById(R.id.btn_sertViewGONEorVISIVITY);
                    relativeLayout = (RelativeLayout) mWindowView.findViewById(R.id.relative_seeting);
                    btn_sertViewGONEorVISIVITY.setOnClickListener(new CameraButtonClickListener());

                    //上下左右
                    Button btn_camera_top = (Button) mWindowView.findViewById(R.id.btn_camera_top);
                    Button btn_camera_down = (Button) mWindowView.findViewById(R.id.btn_camera_down);
                    Button btn_camera_left = (Button) mWindowView.findViewById(R.id.btn_camera_left);
                    Button btn_camera_right = (Button) mWindowView.findViewById(R.id.btn_camera_right);
                    ImageView img_close_pop = (ImageView) mWindowView.findViewById(R.id.img_close_pop);
                    img_close_pop.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //移除WindowManager
                            // private void removeWindowView() {
//                                if(mLocalView != null) {
//                                    mLocalView.setVisibility(View.GONE);
//                                }
//                                if(mRemoteView != null) {
//                                    mRemoteView.setVisibility(View.GONE);
//                                }
                            if (mWindowView != null) {
                                //移除悬浮窗口
                                Log.i(TAG, "removeView");
                                mWindowManager.removeView(mWindowView);

                                mWindowView = null;
                            }
                            isFloating = false;
                            // }
                        }
                    });
                    btn_camera_top.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_down.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_left.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_right.setOnClickListener(new CameraButtonClickListener());
                    //缩放
                    Button btn_camera_jia = (Button) mWindowView.findViewById(R.id.btn_camera_jia);
                    Button btn_camera_jian = (Button) mWindowView.findViewById(R.id.btn_camera_jian);
                    btn_camera_jia.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_jian.setOnClickListener(new CameraButtonClickListener());
                    //光圈
                    Button btn_camera_big = (Button) mWindowView.findViewById(R.id.btn_camera_big);
                    Button btn_camera_small = (Button) mWindowView.findViewById(R.id.btn_camera_small);
                    btn_camera_big.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_small.setOnClickListener(new CameraButtonClickListener());
                    //聚焦
                    Button btn_camera_far = (Button) mWindowView.findViewById(R.id.btn_camera_far);
                    Button btn_camera_near = (Button) mWindowView.findViewById(R.id.btn_camera_near);
                    btn_camera_far.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_near.setOnClickListener(new CameraButtonClickListener());
                    //步长
                    Button btn_camera_bc_jia = (Button) mWindowView.findViewById(R.id.btn_camera_bc_jia);
                    Button btn_camera_bc_jian = (Button) mWindowView.findViewById(R.id.btn_camera_bc_jian);
                    et_camera_content = (EditText) mWindowView.findViewById(R.id.et_camera_content);
                    btn_camera_bc_jia.setOnClickListener(new CameraButtonClickListener());
                    btn_camera_bc_jian.setOnClickListener(new CameraButtonClickListener());


                    break;

            }
        }
    }

    //设置移动监听
    private class FloatingListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View arg0, MotionEvent event) {

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mWindowView, wmParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    //System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
                    //System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            return true;  //此处必须返回false，否则OnClickListener获取不到监听
        }
    }

    class CameraButtonClickListener implements OnClickListener
    {

        @Override
        public void onClick(View view) {

            if (cameraCloudnumber == null) {
                ToastUtils.showToast(MainActivity.this, "number为空");
                return;
            }

            String step = et_camera_content.getText().toString();


            switch (view.getId()) {


                case R.id.btn_sertViewGONEorVISIVITY:

                    if (btn_sertViewGONEorVISIVITY.getText().equals("展开")) {
                        if (relativeLayout.getVisibility() != View.VISIBLE) {
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                        btn_sertViewGONEorVISIVITY.setText("收起");
                    } else if (btn_sertViewGONEorVISIVITY.getText().equals("收起")) {
                        if (relativeLayout.getVisibility() != View.GONE) {
                            relativeLayout.setVisibility(View.GONE);
                        }
                        btn_sertViewGONEorVISIVITY.setText("展开");
                    }
                    break;
                //上下左右
                case R.id.btn_camera_top:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeY;
                    boolean VideoControlTypeY = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat("-" + step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat("-" + step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + VideoControlTypeY);
                    break;
                case R.id.btn_camera_down:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeY;
                    boolean fVideoControlTypeY = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat(step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat(step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + fVideoControlTypeY);
                    break;
                case R.id.btn_camera_left:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeX;
                    boolean results = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat("-" + step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat("-" + step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + results);
                    break;
                case R.id.btn_camera_right:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeX;
                    boolean resultss = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat(step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat(step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultss);
                    break;
                //缩放
                case R.id.btn_camera_jia:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeZoom;
                    boolean resultcc = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat(step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat(step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultcc);
                    break;
                case R.id.btn_camera_jian:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeZoom;
                    boolean resultaa = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat("-" + step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat("-" + step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultaa);
                    break;
                //光圈
                case R.id.btn_camera_big:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeLR;
                    boolean resultww = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat(step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat(step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultww);
                    break;
                case R.id.btn_camera_small:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeLR;
                    boolean resultw = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat("-" + step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat("-" + step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultw);
                    break;
                //聚焦
                case R.id.btn_camera_far:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeFocus;
                    boolean result = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat(step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat(step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + result);
                    break;
                case R.id.btn_camera_near:
                    cameraControlType = MediaEngine.ME_CameraControl.VideoControlTypeFocus;
                    boolean resultssss = MediaEngine.GetInstance().ME_OpCameraNumberControl_Async(cameraCloudnumber, cameraControlType, Float.parseFloat("-" + step), timeOut);
                    Log.e(TAG, "参数  cameraCloudnumber：" + cameraCloudnumber);
                    Log.e(TAG, "参数  cameraControlType：" + cameraControlType);
                    Log.e(TAG, "参数  Float.parseFloat(\"-\"+step)：" + Float.parseFloat("-" + step));
                    Log.e(TAG, "参数  timeOut：" + timeOut);
                    Log.e(TAG, "参数  操作返回值：" + resultssss);
                    break;
                //步长
                case R.id.btn_camera_bc_jia:
                    if (numberCount >= 90) {
                        numberCount = 90;
                        et_camera_content.setText("0.90");
                        Log.e(TAG, "btn_camera_bc_jia:" + et_camera_content.getText());
                    } else {
                        numberCount += 5;
                        float progress = (float) numberCount / (float) 100;
                        et_camera_content.setText(doubleToString(progress));
                        Log.e(TAG, "btn_camera_bc_jia:" + et_camera_content.getText());
                    }
                    break;
                case R.id.btn_camera_bc_jian:
                    if (numberCount <= 5) {
                        numberCount = 5;
                        et_camera_content.setText("0.05");
                        Log.e(TAG, "btn_camera_bc_jian:" + et_camera_content.getText());
                    } else {
                        numberCount -= 5;
                        float progress = (float) numberCount / (float) 100;
                        et_camera_content.setText(doubleToString(progress));
                        Log.e(TAG, "btn_camera_bc_jian:" + et_camera_content.getText());
                    }
                    break;
            }
        }
    }

    /**
     * double转String,保留小数点后两位
     *
     * @param num
     * @return
     */
    public static String doubleToString(float num)
    {
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }

    //接收PTTService界面文件下载情况
    class FileUpLoadResultReservice extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == GlobalConstant.ACTION_MESSAGE_MSGSENDFILEERROR) {
                ToastUtils.showToast(MainActivity.this, "文件上传失败");
            } else if (intent.getAction() == GlobalConstant.ACTION_NOTIFICATIONMAINOLDMESSAGERESULT) {
                if (fileCount <= 0) {
                    return;
                }
                String fileUploadSuccessCount = intent.getStringExtra("fileUploadSuccessCount");
                String fileUploadErrorCount = intent.getStringExtra("fileUploadErrorCount");
                ToastUtils.showToast(MainActivity.this, "短信总数:" + fileCount + "     " + "成功:" + fileUploadSuccessCount + "     " + "失败:" + fileUploadErrorCount);
                fileCount = 0;
            }

        }
    }

    //异步解码上墙
    class AsyncDecodeTheUpperWall extends AsyncTask<String, Object, String>
    {

        @Override
        protected String doInBackground(String... str) {
            String number = str[0];
            Log.e(TAG, "开始解码上墙  得到的number：" + number);
            if (number.length() == 0) {
                //ToastUtils.showToast(MainActivity.this,"请选择目标");
                toastContent = "请选择目标";
                decoderHandler.sendEmptyMessage(6);//提示
                decoderHandler.sendEmptyMessage(8); //设置btn_decodingupperwall可点击
                return null;
            }
            if (number.equals("0")) {
                //ToastUtils.showToast(MainActivity.this,"请选择目标");
                toastContent = "请选择目标";
                decoderHandler.sendEmptyMessage(6);
                decoderHandler.sendEmptyMessage(8); //设置btn_decodingupperwall可点击
                return null;
            }

            if (number.contains("-")) {
                //ToastUtils.showToast(MainActivity.this,"请选择目标");
                toastContent = "请选择目标";
                decoderHandler.sendEmptyMessage(6);
                decoderHandler.sendEmptyMessage(8); //设置btn_decodingupperwall可点击
                return null;
            }

            //判断当前用户是否上墙
            if (decodeMap.containsKey(number) || decodeMap.containsKey(number + "decoderId") || decodeMap.containsKey(number + "chanidStrings")) {

//                ToastUtils.showToast(MainActivity.this,"该用户已经上墙");
                toastContent = "该用户已经上墙";
                decoderHandler.sendEmptyMessage(6);
                decoderHandler.sendEmptyMessage(8); //设置btn_decodingupperwall可点击
                return null;
            }

            toastContent = "解码器加载中，请稍后...";
            decoderHandler.sendEmptyMessage(6);

            decoderHandler.sendEmptyMessage(1);

            try {
                //获取解码器列表
                ME_DecoderInfo[] medecoderInfo = dcodupperwall();
                Log.e(TAG, "返回来的数组长度:" + medecoderInfo.length);
                List<ME_DecoderInfo> arrayList = Arrays.asList(medecoderInfo);
                Log.e(TAG, "转为集合：" + arrayList.size());

                int sleep20 = 1;
                while (true) {
                    if (arrayList.size() == 0) {
                        Log.e(TAG, "arrayList:" + arrayList.size());
                        //arrayList=dcodupperwall();
                        sleep20--;
                        if (sleep20 < 1) {
                            toastContent = getString(R.string.loadTimeout_DecodList);
                            decoderHandler.sendEmptyMessage(6);
                            break;
                        }
                    } else {
                        listDecode = arrayList;
                        Log.e(TAG, "arrayList:" + arrayList.size());
                        break;
                    }
                    Thread.sleep(1000);
                }

                data_list = new ArrayList<String>();
                for (int i = 0; i < listDecode.size(); i++) {
                    data_list.add(listDecode.get(i).deviceName);
                }

                decoderHandler.sendEmptyMessage(2);

                Log.e(TAG, "selectedDcod加载完毕！");

                if (listDecode.size() <= 0) {
                    return null;
                }

                /*****************通道菜单*******************/
                loadlist(listDecode.get(0).deviceId, listDecode.get(0).deviceName);//加载解码器列表，解码器类型，解码器通道
                /*****************按钮*****************************/

                //加载通道号
                if (decoderDisplayChans.size() > 0) {
                    if (list_decoderchannel.size() > 0) {
                        list_decoderchannel.clear();
                    }
                    if (decoderDisplayChans.get(0).decodeChannelIds.length > 0) {

                        for (int j = 0; j < decoderDisplayChans.get(0).decodeChannelIds.length; j++) {
                            list_decoderchannel.add("" + decoderDisplayChans.get(0).decodeChannelIds[j]);
                        }
                    } else {
                        list_decoderchannel.add("当前类型没有关联通道");
                    }

                    decoderHandler.sendEmptyMessage(4);
                }
            } catch (Exception ex) {
                decoderHandler.sendEmptyMessage(6);
            }
            return number;
        }

        @Override
        protected void onPostExecute(final String number) {
            super.onPostExecute(number);

            if (number == null) {
                return;
            }


            //开始解码上墙监听
            btnsbmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (ButtonUtils.isFastDoubleClick(R.id.btnsbmit)) {
                        toastContent = "无效点击";
                        decoderHandler.sendEmptyMessage(6);
                        return;
                    }
                    //new StartDecoderAsync().execute(number);
                    new StartDecoderAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,number);
                }
            });

            selectedDcod.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int positions, long id) {
                    // TODO Auto-generated method stub
                    Log.e(TAG, "setOnItemSelectedListener中      listDecode.get(positions).deviceId:" + listDecode.get(positions).deviceId + "listDecode.get(positions).deviceName:" + listDecode.get(positions).deviceName);
                    loadlist(listDecode.get(positions).deviceId, listDecode.get(positions).deviceName);
                    position = positions;

                    Log.e(TAG, "" + selectedDcod.getSelectedItem().toString());
                    if (!docName.equals(selectedDcod.getSelectedItem().toString())) {
                        docName = selectedDcod.getSelectedItem().toString();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                    Log.e(TAG, "" + selectedDcod.getSelectedItem().toString());

                }
            });


            selectedtype.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // TODO Auto-generated method stub


                    if (selectedtype == null) {
                        return;
                    }

                    if (selectedtype.getSelectedItem() == null) {
                        return;
                    }
                    if (selectedtype.getSelectedItem().toString() == null) {
                        return;
                    }


                    Log.e(TAG, "" + selectedtype.getSelectedItem().toString());
                    if (!chanName.equals(selectedtype.getSelectedItem().toString())) {
                        chanName = selectedtype.getSelectedItem().toString();
                        loadchan(chanName, position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                    if (selectedtype.getSelectedItem().toString() == null) {
                        return;
                    }
                    Log.e(TAG, "" + selectedtype.getSelectedItem().toString());
                }
            });
        }
    }

    class StartDecoderAsync extends AsyncTask<String, Object, String>
    {


        @Override
        protected String doInBackground(String... objects) {


            String number = objects[0];

            List<ME_DecoderDisplayInfo> disArrayList = getDisArrayList(data_Decorder_ID);
            if (disArrayList == null) {
                return null;
            }

            if (disArrayList.size() <= 0) {
                return null;
            }

            info = disArrayList.get(position);

            Log.e(TAG, "chanidString : " + chanidString);

            if (!monitorsSessionID.containsKey(number)) {

                MediaEngine.ME_SessionInfo[] me = MediaEngine.GetInstance().ME_GetMySessionList();
                if (me.length <= 0) {
                    toastContent = "无cid";
                    decoderHandler.sendEmptyMessage(6);
                    return null;
                } else {
                    for (MediaEngine.ME_SessionInfo ss : me) {
                        if (ss.callingNumber.contains(sipUser.getUsername())) {
                            sessionid = ss.sessionId;
                        }
                    }
                }
            } else {
                sessionid = monitorsSessionID.get(number);
                Log.e(TAG, "从集合获取sessionId:" + sessionid + "    number:" + number);
                if (sessionid == null) {
                    toastContent = "sessionid空";
                    decoderHandler.sendEmptyMessage(6);
                    return null;
                }
            }
            return number;
        }

        @Override
        protected void onPostExecute(String number) {
            super.onPostExecute(number);

            if (number == null) {
                return;
            }

            if (list_decoderchannel.size() <= 0) {
                toastContent = "请选择通道";
                decoderHandler.sendEmptyMessage(6);
                return;
            }

            String chanidStrings = selectedchan.getSelectedItem().toString();//当前选择的解码器通道
            if (chanidStrings == null) {
                toastContent = "请选择通道";
                decoderHandler.sendEmptyMessage(6);
                return;
            }
            if (chanidStrings.equals("当前类型没有关联通道")) {
                toastContent = "请选择通道";
                decoderHandler.sendEmptyMessage(6);
                return;
            }

            if (decodeMap.size() > 0) {
                for (String key : decodeMap.keySet()) {
                    DecoderBean value = decodeMap.get(key);
                    if (value.getChanidStrings().equals(chanidStrings)) {
                        if (decodeMap.containsKey(key)) {
                            Log.e(TAG, " 通道号相同  map删除的Key：" + key);
                            decodeMap.remove(key);
                            break;
                        }
                    }
                }
            }
            selectedDecoder(sessionid, number, info.decoderId, Integer.valueOf(chanidStrings));
        }
    }

    //接收断网重连注册状态
    class RegReceive extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "收到广播");
            boolean arg0 = intent.getBooleanExtra("arg0", false);
            String arg1 = intent.getStringExtra("arg1");
            String arg2 = intent.getStringExtra("arg2");

            if (arg1.equals("")) {
                return;
            }
            if (!arg0) {
                startReceiveTime();
                if (Integer.valueOf(arg1) != 200) {
                    startReceiveTime();
                } else {
                    stopReceiveTime();
                }
            } else {
                stopReceiveTime();
            }


        }
    }

    private void startReceiveTime()
    {

        if (receiveTimerIsStart) {
            return;
        }
        if (receiveTimer == null) {
            receiveTimer = new Timer();
        }

        if (receiveTimerTask == null) {
            receiveTimerTask = new TimerTask() {
                @Override
                public void run() {

                    receiveTimerIsStart = true;
                    MediaEngine.GetInstance().ME_HandleIpChange();
                    Log.e(TAG, "Main  发起注册");
                }
            };
        }


        if (receiveTimer != null && receiveTimerTask != null) {
            try {
                receiveTimer.schedule(receiveTimerTask, 5000, 5000);
                Log.e(TAG, "Main  发起注册");
            } catch (Exception e) {
                Log.e(TAG, "Main  发起注册  抛异常：" + e.getMessage());
            }
        }


    }

    private void stopReceiveTime()
    {
        Log.e(TAG, "Main stopTimer");
        //循环不再继续
        receiveTimerIsStart = false;
        if (receiveTimer != null) {
            receiveTimer.cancel();
            receiveTimer = null;
        }

        if (receiveTimerTask != null) {
            receiveTimerTask.cancel();
            receiveTimerTask = null;
        }
    }

    //停止统计丢包信息定时器
    private void stopTimer()
    {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        decoderHandler.sendEmptyMessage(11);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        decoderHandler.sendEmptyMessage(10);

    }

    //开启统计丢包信息定时器
    private void startTimer(final int dwSessid)
    {


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
                    if (isStartTimer_meeting) {
                        //丢包率信息统计
                        Log.e(TAG, "startTimer");
                        new AsyncMediaStatistics().execute(String.valueOf(dwSessid));
                    } else {
                        Log.e(TAG, "stopTimer");
                        stopTimer();
                    }
                }
            };
        }

        if (mTimer != null && timerTask != null) {
            try {
                mTimer.schedule(timerTask, 1000, 1000);

            } catch (Exception e) {
                Log.e(TAG, "" + e.getMessage());
            }
        }
    }

    //开始请求统计信息
    class AsyncMediaStatistics extends AsyncTask<String, Object, Object>
    {

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

            voiceDownlink_meeting = aud_down;
            voice_Uplink_meeting = aud_up;
            voice_Bidirectionaldelay_meeting = aud_rtt;
            voice_downstreambandwidth_meeting = aud_bwD;
            voice_Uplinkbandwidth_meeting = aud_bwU;

            video_Downlink_meeting = vid_down;
            video_Uplink_meeting = vid_up;
            video_Bidirectionaldelay_meeting = vid_rtt;
            video_downstreambandwidth_meeting = vid_bwD;
            video_Uplinkbandwidth_meeting = vid_bwU;

            decoderHandler.sendEmptyMessage(10);

            return null;
        }
    }

    class MediaStatisticsReceive extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {

                case GlobalConstant.MEETING_STARTTIMER:

                    String dwSessid = intent.getStringExtra("dwSessid");
                    if (dwSessid != null) {
                        startTimer(Integer.valueOf(dwSessid));
                    }

                    break;

                case GlobalConstant.MEETING_STOPTIMER:

                    stopTimer();

                    break;

                case GlobalConstant.MEETING_SETFALSE:

                    isStartTimer_meeting = false;

                    if (linearlayout_callInfo_meeting.getVisibility() == View.VISIBLE) {
                        linearlayout_callInfo_meeting.setVisibility(View.GONE);
                    }
//                    if (tv_TongjiInfo.getVisibility() == View.VISIBLE) {
//                        tv_TongjiInfo.setVisibility(View.GONE);
//                    }
                    break;

                case GlobalConstant.MEETING_SETTRUE:
                    isStartTimer_meeting = true;

                    //取得配置是否开启
                    boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);

                    if (mediastatistics) {
                        linearlayout_callInfo_meeting.setVisibility(View.VISIBLE);
                    } else {
                        linearlayout_callInfo_meeting.setVisibility(View.GONE);
                    }
//                    if (mediastatistics) {
//                        tv_TongjiInfo.setVisibility(View.VISIBLE);
//
//                    } else {
//                        tv_TongjiInfo.setVisibility(View.GONE);
//
//                    }
                    break;
            }
        }
    }

    /**
     * 是否使屏幕常亮
     *
     * @param activity
     */
    public static void keepScreenLongLight(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public class DecodingTheWallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "收到结束上墙视频广播");
            String isAll = intent.getStringExtra(GlobalConstant.ISALL);
            Log.e(TAG, "isAll : " + isAll + "\r\n" + "decodeMap.size : " + decodeMap.size());

            if (isAll.equals(GlobalConstant.ISALL_YES)) {   //是否全部下墙
                //会议结束 将上墙视频结束
                //如果存在上墙视频  那么结束上墙的视频
                if (decodeMap.size() > 0) {
                    Log.e(TAG, "IsAll_Yes");
                    for (String key : decodeMap.keySet()) {
                        Log.e(TAG, "IsAll_Yes    KEY : " + key);
                        DecoderBean decoderBean = decodeMap.get(key);
                        String sessionid = decoderBean.getSessionId();
                        String decoderId = decoderBean.getDecoderId();
                        String chanidString = decoderBean.getChanidStrings();
                        Log.e(TAG, "KEY" + key + "\r\n" + "sessionId:" + sessionid + "\r\n" + "decoderId:" + decoderId + "\r\n" + "chanidString" + chanidString);
                        selectedDecoderEnd(sessionid, key, decoderId, Integer.valueOf(chanidString));
                    }
                }
                decodeMap.clear();
            } else {

                String key = intent.getStringExtra(GlobalConstant.DECODER_NUMBER);
                if (decodeMap.containsKey(key)) {
                    DecoderBean decoderBean = decodeMap.get(key);
                    String sessionid = decoderBean.getSessionId();
                    String decoderId = decoderBean.getDecoderId();
                    String chanidString = decoderBean.getChanidStrings();
                    //Log.e("===Sws测试解码上墙","从集合中读取    number：" + number + "sessionId:" + sessionid + "decoderId:" + decoderId + "chanidStrings" + chanidStrings);
                    selectedDecoderEnd(sessionid, key, decoderId, Integer.valueOf(chanidString));
                    decodeMap.remove(key);
                }
            }
        }
    }


    //用于删除或添加地图
    private class ReceiverMapView extends BroadcastReceiver {

        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                // TODO: 2018/12/7  收到广播 根据传值来判断是添加还是移除地图
                //boolean addOrRemove = intent.getBooleanExtra("AddOrRemove",false);
                String addOrRemove = intent.getStringExtra("AddOrRemove");
                if (addOrRemove.equals("ADD")) {
                    //tv_TongjiInfo.setVisibility(View.VISIBLE);
                    //tv_TongjiInfo2.setVisibility(View.VISIBLE);

                    // TODO: 2018/12/7  收到广播 执行地图添加
                    if (ptt_3g_PadApplication.isBaiduMapisRemove()) {
                        initBaiDuMap2();

                        initBaiduMap();


                        rl_positiontools.setVisibility(View.VISIBLE);
                        // 显示监控
                        sl_videos.setVisibility(View.GONE);
                        // 显示分屏
                        ll_split.setVisibility(View.GONE);
                        btn_info.setVisibility(View.GONE);
                        btn_close.setVisibility(View.GONE);
                        btn_pushvideo.setVisibility(View.GONE);
                        btn_decodingupperwall.setVisibility(View.GONE);
                        btn_decodingupperwa.setVisibility(View.GONE);
                        mapInitSuccess = true;
                    }
                } else {


                    if (!mapInitSuccess) {
                        Thread.sleep(1000);
                    }

                    if (!ptt_3g_PadApplication.isBaiduMapisRemove()) {
                        //移除地图
                        ll_position.removeView(mMapView);
                        if (mMapView != null) {
                            mMapView.onDestroy();
                        }

                        ptt_3g_PadApplication.setBaiduMapisRemove(true);
                        rl_positiontools.setVisibility(View.GONE);
                        mapInitSuccess = false;
                    }
                    // tv_TongjiInfo.setVisibility(View.GONE);
                    // tv_TongjiInfo2.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }
        }
    }

    //用于切换分屏
    private class SwitchSplitScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String splitScreen = intent.getStringExtra("splitScreen");

            //判断当前是否为MCU会议
            String string = prefs.getString("ISMCU", "True");
            if (string.equals("True")) {  //当前会议为MCU会议
                return;
            }

            if (sl_videos.getVisibility() != View.VISIBLE) {
                Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAA", "Main 15631 ");
                sl_videos.setVisibility(View.VISIBLE);
            }

            if (splitScreen.equals("One")) {
                Log.e("测试会议切屏", "收到广播 content : " + splitScreen + "\r\n" + "currNumColumn :" + currNumColumn);
                if (currNumColumn != NumColumns.ONE) {
                    Log.e("测试会议切屏", "进入if判断 收到广播 content : " + splitScreen);
                    //切换一分屏
                    setOne();
                }
            } else if (splitScreen.equals("Four")) {
                Log.e("测试会议切屏", "收到广播 content : " + splitScreen + "\r\n" + "currNumColumn :" + currNumColumn);
                if (currNumColumn != NumColumns.TWO) {
                    if (currNumColumn != NumColumns.THREE) {
                        Log.e("测试会议切屏", "进入if判断 收到广播 content : " + splitScreen);
                        //切换四分屏
                        setFour();
                    }
                }
            } else if (splitScreen.equals("Nine")) {
                Log.e("测试会议切屏", "收到广播 content : " + splitScreen + "\r\n" + "currNumColumn :" + currNumColumn);
                if (currNumColumn != NumColumns.THREE) {
                    Log.e("测试会议切屏", "进入if判断 收到广播 content : " + splitScreen);
                    //切换九分屏
                    setNine();
                }
            }


        }
    }

    //机构数获取
    private void getAllGroupsByICE()
    {

        MediaEngine.GetInstance().ME_GetGroup_Async(new MediaEngine.ME_GetGroup_CallBack() {
            @Override
            public void onCallBack(MediaEngine.ME_GroupInfo me_groupInfo) {
                //判断上次数据是否加载完成 如果没有完成则不进行本次加载
            if (treeLoadIsSuccess){
                treeLoadIsSuccess = false;
                Log.e("测试机构数","groupName:"+me_groupInfo.groupName);
                Log.e("测试机构数","size:"+me_groupInfo.children.size());
                if (me_groupInfo.children.size() > 0) {
                    //清空对讲组
                    GroupManager.getInstance().clearAllGroupMember();
                    GroupManager.getInstance().clearALLGroup();
                    String groupnum = me_groupInfo.prefix + me_groupInfo.groupNumber;
                    String groupname = me_groupInfo.groupName;
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setNumber(groupnum);
                    groupInfo.setName(groupname);
                    Log.e("测试机构数","groupname ：" + groupname + "\r\n" + "conshowAll :" + me_groupInfo.canShowAll);
                    if (me_groupInfo.canShowAll == 1){
                        GroupManager.getInstance().addALLGroup(groupInfo);
                    }
                    treeGroups(me_groupInfo.children);
                    ptt_3g_PadApplication.setOrganizationOfMachines(me_groupInfo);
               }
            }
            }
        });
    }

    // 取得全部组成员线程
    private GetALLMemberInfoThread getALLMemberInfoThread = new GetALLMemberInfoThread();
    //获取全部对讲组当前索引
    private int currALLgroupindex = 0;

    //对取到的数据进行拆分 并将对讲组及非对讲组解析出来
    private void treeGroups(List<MediaEngine.ME_GroupInfo> groupTs)
    {
        Log.e("treeGroups","进入treeGroups");
        if(groupTs == null){
            Log.e("treeGroups","== null return了");
            return;
        }
        Log.e("treeGroups","没有return");
        for (MediaEngine.ME_GroupInfo gt : groupTs) {
            if (gt == null) continue;
            String groupnum = gt.prefix + gt.groupNumber;
            String groupname = gt.groupName;
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setNumber(groupnum);
            groupInfo.setName(groupname);
            Log.e("测试机构数","groupname ：" + groupname + "\r\n" + "conshowAll :" + gt.canShowAll);
            if (gt.canShowAll == 1){
                GroupManager.getInstance().addALLGroup(groupInfo);
            }
            List<MediaEngine.ME_GroupInfo> grpArray = gt.children;
            if(grpArray != null){
                treeGroups(grpArray);
            }
        }

        // 异步请求机构数成员
        if (!getALLMemberInfoThread.flags) {
            ptt_3g_PadApplication.setSuccess(false);
            Log.e("treeGroups","发送获取成员");
            currALLgroupindex = 0;
            getALLMemberInfoThread.setFlag(true);
            new Thread(getALLMemberInfoThread).start();
        }
    }

    // 取得对讲组及非对讲成员线程
    public class GetALLMemberInfoThread extends Thread
    {
        private volatile boolean flags;

        public void setFlag(boolean flags) {
            this.flags = flags;
        }

        @Override
        public void run() {
            while (flags) {
                Message ms = Message.obtain();
                ms.arg2 = currALLgroupindex;
                getALLMemberInfoHandler.sendMessage(ms);
                currALLgroupindex++;
                try {
                    Thread.currentThread();
                    Thread.sleep(240);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void stopCurrentThread() {
            this.flags = false;
            treeLoadIsSuccess = true;
            ptt_3g_PadApplication.setSuccess(true);
            Intent intent = new Intent(GlobalConstant.ACTION_DATELOADINGSUCCESS);
            sendBroadcast(intent);
            Log.e("测试机构数","stopCurrentThread  通知界面刷新");
        }
    }

    // 具体请求对讲组成员
    @SuppressLint("HandlerLeak")
    private Handler getALLMemberInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg2 < GroupManager.getInstance().getALLGroupCount()) {
                   getAllGroupMemsInfoByICE(GroupManager.getInstance().getALLGroupData()
                            .get(msg.arg2).getNumber());
            } else {
                // 停止发送消息
                if (getALLMemberInfoThread != null) {
                    getALLMemberInfoThread.stopCurrentThread();
                }
            }
        }
    };

    //获取对讲组及非对讲组成员
    private void getAllGroupMemsInfoByICE(String groupNo)
    {
        Log.e("2019--03--w07","groupNo:"+groupNo);
        //异步方式获取对讲组成员
        MediaEngine.GetInstance().ME_GetGroupMember_Async(groupNo, new MediaEngine.ME_GetGroupMember_CallBack() {
            @Override
            public void onCallBack(String groupNumber, MediaEngine.ME_UserInfo[] me_userInfos) {
                Log.e("treeGroups","获取成员成功");
                List<MemberInfo> memberInfos = new ArrayList<MemberInfo>();
                for(MediaEngine.ME_UserInfo userInfo : me_userInfos) {
                    MemberInfo memberInfo = new MemberInfo();
                    memberInfo.setName(userInfo.userName);
                    memberInfo.setNumber(userInfo.userId);
                    memberInfo.setMemberType(userInfo.userType);
                    int memstate = 0;
                    if (userInfo.isLogin) {
                        memstate = 2;
                    }else {
                        //判断当前类型如果为监控类型 则设置为在线状态
                        if (userInfo.userType == GlobalConstant.MONITOR_USERTYPE_MONITOR){
                            memstate = 2;
                        }
                    }
                    memberInfo.setStatus(memstate);
                    Log.e("treeGroups","UserName: " + userInfo.userName + " ,UserId: " + userInfo.userId + " ,UserState: " + userInfo.userState);
                    memberInfos.add(memberInfo);
                    memberStatehashMap.put(userInfo.userId,memberInfo);
                    //保存成员Info
                    CommonMethod.prefixMap.put(userInfo.userId,userInfo);
                }
                if (memberInfos.size() > 0) {
                    MemberInfo[] s = new MemberInfo[memberInfos.size()];
                    s = memberInfos.toArray(s);
                    GroupManager.getInstance().addALLReceivedMembers(groupNumber, s);
                }
                // 发送广播通知已获取到对讲组成员
                Intent intent = new Intent(GlobalConstant.ACTION_MEMBERINFO);
                sendBroadcast(intent);

                // 发送更新联系人
                Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
                sendBroadcast(callIntent);

            }
        });
    }

    //收到动态重组的消息 重新获取机构数据
    class GroupInfoBroadCastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("2019-03-07","收到动态重组广播");
            //重新获取机构数据
            getAllGroupsByICE();
        }
    }

    //收到成员上下线消息 对GIS图标进行修改
    class ReceiverMemberStateChanged extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ptt_3g_PadApplication.isBaiduMapisRemove()){
                //如果当前百度地图被移除 则return
                return;
            }
            String action = intent.getAction();
            if (!action.equals(GlobalConstant.ACTION_NOTIFICATION_MAIN_GIS_IMG)){
                return;
            }
            //状态改变号码
            String employeeid = intent.getStringExtra("employeeid");
            //当前状态
            String status = intent.getStringExtra("status");

            if (employeeid == null || status == null){
                return;
            }

            new GisMemberStateChanged().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,employeeid);
        }
    }

    //收到成员上下线消息 对GIS图标进行修改
    class GisMemberStateChanged extends AsyncTask<String,Object,PosMarkerInfo>
    {

        @Override
        protected PosMarkerInfo doInBackground(String[] str) {

            if (posMarkerInfosList == null){
                Log.e("addPossssssssssadadOverlay"," return 16335");
                return null;
            }
            //状态改变号码
            String employeeid =  str[0];

            if (employeeid.equals(sipUser.getUsername())){
                Log.e("addPossssssssssadadOverlay"," return 16342");
                return null;
            }

            for (PosMarkerInfo pmi : posMarkerInfosList) {
                if (employeeid.equals(pmi.getName())){
                    return pmi;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(PosMarkerInfo posMarkerInfo) {
            super.onPostExecute(posMarkerInfo);
            Log.e("addPossssssssssadadOverlay"," onPostExecute");
            if (posMarkerInfo != null){
                Log.e("addPossssssssssadadOverlay"," posMarkerInfo != null");

                if(hasMark != null){
                    Log.e("addPossssssssssadadOverlay"," hasMark != null");
                    if (hasMark.containsKey(posMarkerInfo.getName())) {
                        Marker marker = hasMark.get(posMarkerInfo.getName());
                        marker.remove();
                    }
                    //Log.e("addPossssssssssadadOverlay"," onPostExecute :" + marker.getTitle());
                }
                addPosOverlay(posMarkerInfo,false);
            }
        }
    }

    //点击隐藏列表或显示列表时需对视频空间大小进行重新计算
    private void calculationLayout(){

        if (monitorInfoList.size() <= 1){
            return;
        }

        if (sl_videos == null){
            return;
        }
        //只有这个监听可以获取到改变的view最新的宽高 但是他会多次执行  所以需要在回调回来第一次将这个监听取消掉
        sl_videos.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sl_videos.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                for (MonitorInfo monitorInfo : monitorInfoList){
                    LinearLayout linearLayout = monitorInfo.getmLinearLayot();
                    if (currNumColumn == NumColumns.TWO){
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                sl_videos
                                        .getWidth() / 2, sl_videos
                                .getHeight() / 2));
                    }else if(currNumColumn == NumColumns.THREE){
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                sl_videos
                                        .getWidth() / 3, sl_videos
                                .getHeight() / 3));
                    }
                }
                Log.e("计算Layout","布局计算 ****************  完成*******");

            }
        });
    }


    private boolean isLoadMarker = false;
    //用于地图添加marker优化
    class MapAddMarkerAsync extends  AsyncTask<String,Object,String>
    {

        @Override
        protected String doInBackground(String... objects) {

            String rectangle_pos = objects[0];
            isLoadMarker = true;
            Log.e("Gis添加图标","进入doInbackGround 设置为true");
            //String rectangle_pos = objects[0];
            Log.e("GIS模块","获取矩形gis信息");

            //如果是轨迹回放则返回
            if (IsTrackmodel())
                return null;

            if (checkBoxMarker.size() > 0) {
                checkBoxMarker.clear();
            }

            if (!rectangle_pos.equals("")) {
                //定位返回来消息没有类型  从这里获取然后进行判断
                Editor edit = prefs.edit();
                edit.putString("ellipsePos", rectangle_pos);
                edit.apply();
                Log.e(TAG, "ellipsePos 已添加:" + rectangle_pos);
            }
            Log.e(TAG, "rectangle:" + rectangle_pos);
            Message message = Message.obtain();
            message.arg1 = 0;
            sendMessage(message);
            //employeeListView.removeAllViews();
            if (rectangle_pos.length() <= 0) {
//                TextView text = new TextView(MainActivity.this);
//                text.setText("无任何成员");
//
//                int height = employeeLayout.getHeight();
//                //int height = employeeListView.getHeight();
//                if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
//                    height = 50;
//                }
//                text.setHeight(height);
//                text.setWidth(employeeLayout.getWidth());
//                //text.setWidth(employeeListView.getWidth());
//                text.setTextSize(10);
//                text.setTextColor(Color.WHITE);
//                //employeeLayout.addView(text);
                gisMemberCount = 0;
                Message message1 = Message.obtain();
                message1.arg1 = 1;
                sendMessage(message1);
                return null;
            }

            //2034,116.546095,39.77219,20140905182452,2;
            String[] loc = rectangle_pos.split(";");

            gisMemberCount = loc.length;
            Message message4 = Message.obtain();
            message4.arg1 = 4;
            sendMessage(message4);

            if (posMarkerInfosList == null) {
                posMarkerInfosList = new ArrayList<PosMarkerInfo>();
            } else {
                posMarkerInfosList.clear();
            }

            for (int i = 0; i < loc.length; i++) {
                String[] msg = loc[i].split(",");
                Log.e(TAG, "msg 长度：" + msg.length);
                if (msg.length != 5)
                    continue;
                String no = msg[0];
                Log.e(TAG, "no：" + no);
                Log.e(TAG, "msg[3]：" + msg[3]);
                double lat = Double.valueOf(msg[2]);// 39.772145
                Log.e(TAG, "lat：" + lat);
                double lont = Double.valueOf(msg[1]);// 116.546191
                Log.e(TAG, "lont：" + lont);
                int type = Integer.parseInt(msg[4]);
                Log.e(TAG, "type：" + type);


					/*
					 * String time=msg[3]; String type=msg[4];
					 */
                PosMarkerInfo posMarkerInfo = new PosMarkerInfo();
                if (ptt_3g_PadApplication.memberStatehashMap.containsKey(no)){
                    posMarkerInfo.setName(ptt_3g_PadApplication.memberStatehashMap.get(no).getName());
                }else {
                    posMarkerInfo.setName(no);
                }
                posMarkerInfo.setLatitude(Double.valueOf(lat));
                posMarkerInfo.setLongitude(Double.valueOf(lont));
                switch (type) {
                    case 0://调度台
                        posMarkerInfo.setType(0);
                        break;
                    case 1://对讲终端
                    case 3://外线用户
                    case 6://3G对讲 ,这三种类型都作为3G用户处理
                        posMarkerInfo.setType(6);
                        break;
                    case 2://软电话(SIP电话)
                        posMarkerInfo.setType(2);
                        break;
                    case 7://监控设备
                        posMarkerInfo.setType(7);
                        break;
                    default:
                        break;
                }


                //PosMarkerInfo posMarkerInfo = (PosMarkerInfo) msg.obj;
//                if (posMarkerInfo != null){
//                    // 加载到地图中
//                    addPosOverlay(posMarkerInfo, false);
//                }

                //发送消息 进行添加Marker
                Message message2 = Message.obtain();
                message2.obj = posMarkerInfo;
                message2.arg1 = 2;
                sendMessage(message2);
//                Log.e("GIS模块","GisBroadcastReceiver 6458  ***********");
//					/*成员列表*/
//                CheckBox check = new CheckBox(MainActivity.this);
//                //分辨率
////               if (getwindowmanager() == 1280 && getwindowmanager_height() != 752) {
////                   Log.e(TAG, "4907=====分辨率判断已执行");
////                   LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
////                   if (loc.length == 2) {
////                       if (i == 0) {
////                           //layoutParam.setMargins(-10, -20, -10, -10);
////                       } else if (i == 1) {
////                           Log.e(TAG, "4913=====loc.length==2    || i==1");
////                           layoutParam.setMargins(0, Integer.valueOf("-50"), Integer.valueOf("-10"), Integer.valueOf("-10"));
////                       }
////                   } else {
////                       Log.e(TAG, "4917=====loc.length != 2 ");
////                       layoutParam.setMargins(Integer.valueOf("-10"), Integer.valueOf("-20"), Integer.valueOf("-10"), Integer.valueOf("-10"));
////                   }
////
////                   check.setScaleX((float) 0.6);
////                   check.setScaleY((float) 0.6);
////                   check.setLayoutParams(layoutParam);
////               }
//                //分辨率
//                if (getResources().getDisplayMetrics().heightPixels == 1200 || getResources().getDisplayMetrics().widthPixels == 1920) {
//                    check.setScaleX((float) 0.8);
//                    check.setScaleY((float) 0.8);
//                    check.setTextSize(11);
//                }
//                check.setText(posMarkerInfo.getName());
//                check.setTag(posMarkerInfo);
//                check.setTextColor(Color.WHITE);
//
//
//                //Sws 2017-12-24add 解决全选时app无响应问题
//                check.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                            isCheck = true;
//                        }
//                        return false;
//                    }
//                });
//
//
//                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
//                        // TODO Auto-generated method stub
//                        if (isCheck) {
//                            PosMarkerInfo markerinfo = (PosMarkerInfo) arg0.getTag();
//                            //得到当前点击的CheckBox对应的PosMarkerInfo
//									/*
//									PosMarkerInfo中的属性
//										private String name;		号码
//										private double latitude;	经度
//										private double longitude;	纬度
//										private String time;		时间
//										private int type;			类型
//									 */
//                            //判断当前marker是否存在集合中 如果存在 找到这个maiker并将它remove 避免出现画面重叠
//                            if (hasMark.containsKey(markerinfo.getName())){
//                                Marker marker = hasMark.get(markerinfo.getName());
//                                marker.remove();
//                                hasMark.remove(markerinfo.getName());
//                            }
//
//                            if (ischecked) {
//                                //如果是选中状态  判断是否包含对应的Marker，如果不包含name那么添加
//                                if (!checkBoxMarker.contains(markerinfo)) {
//                                    checkBoxMarker.add(markerinfo);// 从新添加
//                                    addPosOverlay(markerinfo, true);
//                                }
//                            }else {
//                                //如果不是选中状态  判断是否包含对应的Marker，如果包含那么删除
//                                if (checkBoxMarker.contains(markerinfo)) {
//                                    checkBoxMarker.remove(markerinfo);// 从新添加
//                                    addPosOverlay(markerinfo, false);
//                                }
//                            }
//
//                            if (checkBoxMarker.size() == 1) {
//                                ll_pos_single.setVisibility(View.VISIBLE);
//                                ll_pos.setVisibility(View.GONE);
//                            } else if (checkBoxMarker.size() > 1) {
//                                ll_pos_single.setVisibility(View.GONE);
//                                ll_pos.setVisibility(View.VISIBLE);
//                            } else {
//                                ll_pos_single.setVisibility(View.GONE);
//                                ll_pos.setVisibility(View.GONE);
//                            }
//
//                            //关闭全选popupwindow
//                            if (multiplePopupWindow != null) {
//                                multiplePopupWindow.dismiss();
//                            }
//                            isCheck = false;
//                        }
//                    }
//                });
//
//                employeeLayout.addView(check);
                //切换地图按钮状态Yuezs 2015-10-14
//                switchPosButtonStatus(2);
                posSelType = 2;

					/*成员列表*/
                //posMarkerInfosList.add(posMarkerInfo);

                List<PosMarkerInfo> posLists = new ArrayList<PosMarkerInfo>();
                if (posMarkerInfosList != null) {
                    //posMarkerInfosList.add(posMarkerInfo);
                    for (PosMarkerInfo pos : posMarkerInfosList) {
                        if (pos.getName().equals(no)) {
                            posLists.add(pos);
                        }
                    }
                    if (posLists != null && posLists.size() > 0) {
                        posMarkerInfosList.removeAll(posLists);
                    }
                    posMarkerInfosList.add(posMarkerInfo);
                }
            }
            Log.e("GIS模块","************************************** 6458 6545 6549 6562 6565   *******************************");
            Message message3 = Message.obtain();
            message3.arg1 = 3;
            sendMessage(message3);
            // 根据坐标显示在地图上
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isLoadMarker = false;
            ToastUtils.showToast(MainActivity.this," GIS成员加载完成");
            Log.e("Gis添加图标","onPostExecute 设置为false");
        }
    }


}

