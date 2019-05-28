package com.azkj.pad.utility;

/*全局静态变量*/
public class GlobalConstant {
	/* 全局存储键值(shared preference) */
	public static final String SHARED_PREFERENCE = "PTT_PREFERENCE";
	// 系统处于调试状态
	public static final String SP_USERNAME = "PTT_SP_USERNAME";
	public static final String SP_PASSWORD = "PTT_SP_PASSWORD";
	public static final String SP_SAVEPASS = "PTT_SP_SAVEPASS";
	public static final String SP_SERVERIP = "PTT_SP_SERVERIP";
	public static final String SP_PORT = "PTT_SP_PORT";
	public static final String SP_INTERCOM_HIGHPRIORITY = "PTT_SP_INTERCOM_HIGHPRIORITY";
	public static final String SP_INTERCOM_SAMEPRIORITY = "PTT_SP_INTERCOM_SAMEPRIORITY";
	public static final String SP_INTERCOM_LOWPRIORITY = "PTT_SP_INTERCOM_LOWPRIORITY";
	public static final String SP_INTERCOM_DEFAULTGROUP = "PTT_SP_INTERCOM_DEFAULTGROUP";
	public static final String SP_INTERCOM_DEFAULTRINGTONE = "PTT_SP_INTERCOM_DEFAULTRINGTONE";
	public static final String SP_VIDEO_CAMERA = "PTT_SP_VIDEO_CAMERA";
	public static final String SP_VIDEO_ROTATE = "PTT_SP_VIDEO_ROTATE";
	public static final String SP_VIDEO_BITRATE = "PTT_SP_VIDEO_BITRATE_sws";
	public static final String SP_VIDEO_FRAMERATE = "PTT_SP_VIDEO_FRAMERATE";
	public static final String SP_VIDEO_RESOLUTION= "PTT_SP_VIDEO_RESOLUTION";
	public static final String SP_DEBUG_ON = "PTT_SP_DEBUG_ON";
	public static final String SP_ENCRYPTION = "PTT_SP_ENCRYPTION";
	public static final String SP_STUN = "PTT_SP_STUN";
	public static final String SP_AUTO_START = "PTT_AUTO_START";
	public static final String SP_START_SUSPEND = "PTT_START_SUSPEND";
	public static final String SP_START_FIR = "PTT_START_FIR";
	public static final String SP_START_OTHER = "PTT_START_OTHER";
	public static final String SP_KEY_PERIOD = "KEY_PERIOD";
	public static final String SP_START_ARS = "PTT_START_ARS";
	public static final String SP_START_NACK="PTT_START_NACK";
	public static final String SP_START_TMMBR="PTT_START_TMMBR";
	public static final String SP_START_RESOLU="PTT_START_RESOLU";
	public static final String SP_START_ARS_Min="PTT_START_ARS_Min";
	public static final String SP_START_ARS_Max="PTT_START_ARS_Max";
	//设置anr
    public static final String SP_VIDEO_ANR = "PTT_SP_ANR";
    //设置RXanr
	public static final String SP_VIDEO_RXANR = "PTT_SP_RXANR";
	// 用户记录显示或隐藏左侧区域
	public static final String SP_SHOWORHIDDEN = "PTT_SP_SHOWORHIDDEN";
	//YUEZS ADD 2015-09-01 视频端口配置文件名称
	public static final String SP_VEDIOCONFIG="ConfigVedio";
	/*系统消息类型*/
	// application/woasis-ptt类型消息中服务器编号
	public static final String PTT_MSG_SERVER_ID = "9999999";
	public static final String PTT_MSG_SYSTEM_BODY_TYPE = "application/woasis-ptt";
	// application/woasis-ptt类型消息-请求对讲组
	public static final int PTT_MSG_REQ_GROUPINFO = 1;
	// application/woasis-ptt类型消息-请求对讲组成员
	public static final int PTT_MSG_REQ_MEMBERINFO = 2;
	// application/woasis-ptt类型消息-申请话权
	public static final int PTT_MSG_REQ_PTTREQUEST = 3;
	// application/woasis-ptt类型消息-释放话权
	public static final int PTT_MSG_REQ_PTTCANCEL = 4;
	// application/woasis-ptt类型消息-请求创建会议
	public static final int PTT_MSG_REQ_CREATECONFE = 5;
	// application/woasis-ptt类型消息-邀请入会
	public static final int PTT_MSG_REQ_ADDMEM = 6;
	// application/woasis-ptt类型消息-请出会议
	public static final int PTT_MSG_REQ_DELMEM = 7;
	// application/woasis-ptt类型消息-结束会议
	public static final int PTT_MSG_REQ_ENDCONFE = 8;
	// application/woasis-ptt类型消息-获取会议成员
	public static final int PTT_MSG_REQ_MEMINFO = 9;
	// application/woasis-ptt类型消息-获取监控数据
	public static final int PTT_MSG_REQ_USERLIST = 10;
	// application/woasis-ptt类型消息-开始监控终端视频
	public static final int PTT_MSG_REQ_VBUG_START = 11;
	// application/woasis-ptt类型消息-结束监控终端视频
	public static final int PTT_MSG_REQ_VBUG_END = 12;
	// application/woasis-ptt类型消息-获取视频
	public static final int PTT_MSG_REQ_GETVIDEO = 13;
	// application/woasis-ptt类型消息-请求监控用户
	public static final int PTT_MSG_REQ_VIDEO_USERS = 14;
	//获取所有会话信息
	public static final int PTT_MSG_REQ_GETALLSESSIONS=15;
	// application/woasis-ptt类型消息-返回心跳通知
	public static final String PTT_MSG_IND_HEARTBEAT = "heartbeat:";
	// application/woasis-ptt类型消息-返回注册状态变更通知
	public static final String PTT_MSG_IND_MEMBER_STATUSCHANGE = "ind:member-statuschange";
	// application/woasis-ptt类型消息-返回对讲组信息
	public static final String PTT_MSG_IND_GROUPINFO = "ind:groupInfo";
	// application/woasis-ptt类型消息-返回组成员信息
	public static final String PTT_MSG_RSP_MEMBERINFO = "rsp:memberInfo";
	// application/woasis-ptt类型消息-返回动态重组
	public static final String PTT_MSG_IND_RE_GROUPINFO = "ind:re-groupInfo";
	// application/woasis-ptt类型消息-申请话权成功
	public static final String PTT_MSG_RSP_PTT_ACCEPT = "rsp:ptt-accept";
	// application/woasis-ptt类型消息-申请话权等待
	public static final String PTT_MSG_RSP_PTT_WAITING = "rsp:ptt-waiting";
	// application/woasis-ptt类型消息-申请话权拒绝
	public static final String PTT_MSG_RSP_PTT_REJECT = "rsp:ptt-reject";
	// application/woasis-ptt类型消息-对讲状态通知
	public static final String PTT_MSG_IND_PTT_CALLINFO = "ind:call-info";
	// application/woasis-ptt类型消息-返回请求创建会议结果(可能成功可能失败)
	public static final String PTT_MSG_RSP_CREATECONFE = "rsp:createconfe";
	// application/woasis-ptt类型消息-临时视频会议，自己发起会议，自动应答时发送此消息。
	public static final String PTT_MSG_RSP_PUSHVIDEORS = "rsp:push_video_rs";
	// application/woasis-ptt类型消息-临时视频会议，自己发起会议，自动应答时发送此消息。
	public static final String PTT_MSG_RSP_PUSHVIDEORE = "rsp:push_video_re";
	// application/woasis-ptt类型消息-返回请求会议成员结果(可能成功可能失败)
	public static final String PTT_MSG_RSP_MEMINFO = "rsp:meminfo";
	public static final String PTT_MSG_RSP_MEMINFOPJSIP="rsp:memberInfo";
	// application/woasis-ptt类型消息-会议成员状态变化通知
	public static final String PTT_MSG_IND_MSTATECHANGE = "ind:mstatechange";
	// application/woasis-ptt类型消息-监控返回获取用户列表结果
	public static final String PTT_MSG_RSP_USERLIST = "rsp:userlist";
	// application/woasis-ptt类型消息-监控返回获取视频结果
	public static final String PTT_MSG_RSP_GETVIDEO = "rsp:get_video";
	// application/woasis-ptt类型消息-监控返回获取视频事件结果
	public static final String PTT_MSG_RSP_GETVIDEOEVT = "ind:getvideo_evt";
	// application/woasis-ptt类型消息-开始监控终端视频,请求成功
	public static final String PTT_MSG_RSP_VBUG_START_RS = "rsp:vbug_start_rs";
	// application/woasis-ptt类型消息-开始监控终端视频,请求失败
	public static final String PTT_MSG_RSP_VBUG_START_RE = "rsp:vbug_start_re";
	// application/woasis-ptt类型消息-结束监控终端视频,请求成功
	public static final String PTT_MSG_RSP_VBUG_END_RS = "rsp:vbug_end_rs";
	// application/woasis-ptt类型消息-结束监控终端视频,请求失败
	public static final String PTT_MSG_RSP_VBUG_END_RE = "rsp:vbug_end_re";
	// application/woasis-ptt类型消息-监控终端视频成功事件
	public static final String PTT_MSG_IND_VBUG_EVENT = "ind:vbug_event";
	// application/woasis-ptt类型消息-返回监控用户
	public static final String PTT_MSG_RSP_VIDEO_USERS = "rsp:video_users";
	// application/woasis-ptt类型消息-释放视频请求成功
	public static final String PTT_MSG_RSP_VIDEO_RELEAS_RS = "rsp:release_video_rs";
	// application/woasis-ptt类型消息-释放视频请求失败
	public static final String PTT_MSG_RSP_VIDEO_RELEAS_RE = "rsp:release_video_re";
	//成员状态变更
    public static final String PTT_MSG_IND_REG_STATE="ind:reg_state";
    //所有会话消息
	public static final String PTT_MSG_RSP_GETALLSESSIONS="req:all_sessions";
	// application/woasis-ptt类型消息-申请上传文件
	public static final String PTT_MSG_RSP_UPLOAD = "rsp:upload";
	// application/woasis-ptt类型消息-下发文件事件
	public static final String PTT_MSG_IND_D_UPLOAD = "ind:d_upload";
	// application/woasis-ptt类型消息-获取指定好友地理位置信息
	public static final String PTT_MSG_IND_RSP_CUR_POS = "rsp:cur_pos";
	// application/woasis-ptt类型消息-获取长方形范围内的用户位置信息
	public static final String PTT_MSG_IND_RSP_POS_BY_RECTANGLE = "rsp:pos_by_rectangle";
	// application/woasis-ptt类型消息-获取圆形范围内的用户位置信息
	public static final String PTT_MSG_IND_RSP_POS_BY_ELLIPSE = "rsp:pos_by_ellipse";
	// application/woasis-ptt类型消息-获取用户历史位置信息
	public static final String PTT_MSG_IND_RSP_POS_BY_TIME = "rsp:pos_by_time";
	// application/woasis-ptt类型消息-追踪用户位置信息
	public static final String PTT_MSG_IND_RSP_TRACE_POS_RS = "rsp:trace_pos_rs";
	// application/woasis-ptt类型消息-追踪-调度机下发终端位置信息
	public static final String PTT_MSG_IND_IND_D_GIS = "ind:d_gis";
	// application/woasis-ptt类型消息-文本短消息接收
	public static final String PTT_MSG_IND_IND_MSG = "ind:msg";
	// application/woasis-ptt 是否取消话权响应
	public static final String PTT_MSG__RSP_PTT_CANCEL="rsp:ptt-cancel";
	
	/*广播消息通讯*/
	// 登出
	public static final String ACTION_LOGOUT = "com.zzy.ptt.action.LOGOUT";
	// 主页面已关闭
	public static final String ACTION_MAINCLOSE = "com.zzy.ptt.action.MAINCLOSE";
	// 上线
	public static final String ACTION_FORCED_ONLINE = "com.zzy.ptt.action.FORCEDONLINE";
	//网络连接改变
	public static final String ACTION_CONNECTION_CHANGE = "com.zzy.ptt.action.CONNECTIONCHANGE";
	// 被动登出
	public static final String ACTION_FORCED_OFFLINE = "com.zzy.ptt.action.FORCEDOFFLINE";
	// 登入
	public static final String ACTION_DIDLOGIN = "com.zzy.ptt.action.DIDLOGIN";
	//显示分页
	public static final String ACTION_SHOW = "com.zzy.ptt.action.show";
	//登录状态-yuezs 2017-09-26
	public static final String ACTION_LOGINSTATE="com.zzy.ptt.action.LOGINSTATE";

	//返回登录状态
	public static final String ACTION_LOGINSTATE_Sws="com.zzy.ptt.action.LOGINSTATEsss";

	
	//登录状态返回成功或者失败-yuezs 2017-09-26
	public static final String ACTION_LOGINSTATEMSG="LOGINSTATEMSG";
	// 取得对讲组
	public static final String ACTION_GROUPINFO = "com.zzy.ptt.action.GROUPINFO";
	// 心跳机制
	public static final String ACTION_HEARTBEAT = "com.zzy.ptt.action.HEARTBEAT";
	// 注册状态变更通知接，当前登录用户状态
	public static final String ACTION_SELFSTATUS = "com.zzy.ptt.action.SELFSTATUS";
	// 注册状态变更通知接收到all，发起重新注册
	public static final String ACTION_REREGISTER = "com.zzy.ptt.action.REREGISTER";
	// 取得监控组
	public static final String ACTION_VIDEOGROUPS = "com.zzy.ptt.action.VIDEOGROUPS";
	// 取得对讲成员
	public static final String ACTION_MEMBERINFO = "com.zzy.ptt.action.MEMBERINFO";
	// 发起对讲
	public static final String ACTION_PTTMAKE = "com.zzy.ptt.action.PTTMAKE";
	// 挂断对讲
	public static final String ACTION_PTTHANGUP = "com.zzy.ptt.action.PTTHANGUP";
	// 申请话权成功
	public static final String ACTION_PTTACCEPT = "com.zzy.ptt.action.PTTACCEPT";
	// 对讲通话信息
	public static final String ACTION_CALLINFO = "com.zzy.ptt.action.CALLINFO";
	// 申请话权等待
	public static final String ACTION_PTTWAITING = "com.zzy.ptt.action.PTTWAITING";
	// 申请话权拒绝
	public static final String ACTION_PTTREJECT = "com.zzy.ptt.action.PTTREJECT";
	// 是否取消话权响应
	public static final String ACTION_PTTCANCEL = "com.zzy.ptt.action.PTTCANCEL";
	// 对讲通话状态
	public static final String ACTION_PTTCALLINFO = "com.zzy.ptt.action.PTTCALLINFO";
	// 会议界面添加人员
	public static final String ACTION_MEETING_MEMBER = "com.zzy.ptt.action.MEETINGMEMBER";
	// 会议创建
	public static final String ACTION_MEETING_CREATE = "com.zzy.ptt.action.MEETINGCREATE";
	// 会议创建后，会议信息刷新
	public static final String ACTION_MEETING_REFRESH = "com.zzy.ptt.action.MEETINGREFRESH";
	// 会议创建结果
	public static final String ACTION_MEETING_CREATERESULT = "com.zzy.ptt.action.CREATERESULT";
	// 推送视频结果
	public static final String ACTION_MEETING_PUSHVIDEORESULT = "com.zzy.ptt.action.PUSHVIDEORESULT";
	// 获取会议成员结果
	public static final String ACTION_MEETING_MEMBERRESULT = "com.zzy.ptt.action.MEMBERRESULT";
	// 会议成员状态变化通知事件
	public static final String ACTION_MEETING_MSTATECHANGE = "com.zzy.ptt.action.MSTATECHANGE";
	// 会议挂断
	public static final String ACTION_MEETING_CALLHANGUP = "com.zzy.ptt.action.MEETINGHANGUP";
	// 会议成员退会
	public static final String ACTION_MEETING_MEMWITHDRAW = "com.zzy.ptt.action.MEETINGMEMWITHDRAW";
	//重新获取会议成员信息
	public static final String ACTION_MEETING_REMEMBERINFO = "com.zzy.ptt.action.MEETINGREMEMBERINFO";
	//视频会议推送视频
	public static final String ACTION_MEETING_PUSHVIDEO = "com.zzy.ptt.action.MEETINGPUSHVIDEO";
	// 联系人添加、修改、删除
	public static final String ACTION_CONTACT_CHANGE = "com.zzy.ptt.action.CONTACTCHANGE";
	// 联系人添加、修改、删除完成通告页面
	public static final String ACTION_CONTACT_CHANGEOVER = "com.zzy.ptt.action.CONTACTCHANGEOVER";
	//联系人窗口隐藏
	public static final String ACTION_CONTACT_HIDDEN="com.zzy.ptt.action.HIDDEN";
	// 获取监控用户列表通知事件
	public static final String ACTION_MONITOR_USERLIST = "com.zzy.ptt.action.MONITORUSERLIST";
	// 获取监控用户列表通知事件
	public static final String ACTION_MONITOR_MEMBER = "com.zzy.ptt.action.MONITORMEMBER";
	// 获取监控视频通知事件
	public static final String ACTION_MONITOR_GETVIDEO = "com.zzy.ptt.action.GETVIDEO";
	// 获取监控视频通知事件
	public static final String ACTION_MONITOR_GETVIDEOEVT = "com.zzy.ptt.action.GETVIDEOEVT";
	// 视频会议或监控添加视频
	public static final String ACTION_MONITOR_CREATEVIDEO = "com.zzy.ptt.action.CREATEVIDEO";
	public static final String ACTION_MONITOR_CREATEVIDEO2 = "com.zzy.ptt.action.CREATEVIDEO2";
	// 视频会议或监控移除视频
	public static final String ACTION_MONITOR_REMOVEVIDEO = "com.zzy.ptt.action.REMOVEVIDEO";
	// 监控没有成员时自动挂断
	public static final String ACTION_MONITOR_CALLHANGUP = "com.zzy.ptt.action.MONITORHANGUP";
	// 开始监控终端视频
	public static final String ACTION_MONITOR_VBUG_START = "com.zzy.ptt.action.VBUGSTART";
	// 结束监控终端视频
	public static final String ACTION_MONITOR_VBUG_END = "com.zzy.ptt.action.VBUGEND";
	// 监控终端视频成功事件
	public static final String ACTION_MONITOR_VBUG_EVENT = "com.zzy.ptt.action.VBUGEVENT";
	// 获取监控用户
	public static final String ACTION_VIDEOUSERS = "com.zzy.ptt.action.VIDEOUSERS";
	// 视频会议或监控某一路开始
	public static final String ACTION_VIDEO_START = "com.zzy.ptt.action.VIDEOSTART";
	// 视频会议或监控某一路结束
	public static final String ACTION_VIDEO_STOP = "com.zzy.ptt.action.VIDEOSTOP";
	// 监控终端视频成功事件
	public static final String ACTION_VIDEO_RELEASE = "com.zzy.ptt.action.VIDEORELEASE";
	
	//语音拨号
	public static final String ACTION_CALLING_CALLVOICE="com.zzy.ptt.action.CALLVOICE";
	//视频拨号
	public static final String ACTION_CALLING_CALLVIDEO="com.zzy.ptt.action.CALLVIDEO";
	//其余页面跳转语音拨号
	public static final String ACTION_CALLING_OTHERCALLVOICE="com.zzy.ptt.action.OTHERCALLVOICE";
	//其余页面跳转视频拨号
	public static final String ACTION_CALLING_OTHERCALLVIDEO="com.zzy.ptt.action.OTHERCALLVIDEO";
	//挂断
	public static final String ACTION_CALLING_CALLHANGUP="com.zzy.ptt.action.CALLHANGUP";
	//断网30秒挂断
	public static final String ACTION_CALLING_CALLNETCHANGEHANGUP="com.zzy.ptt.action.CALLNETCHANGEHANGUP";
	//接听语音
	public static final String ACTION_CALLING_CALLVOICEANSWER="com.zzy.ptt.action.CALLVOICEANSWER";
	//接听视频
	public static final String ACTION_CALLING_CALLVIDEOANSWER="com.zzy.ptt.action.CALLVIDEOANSWER";
	//忽略
	public static final String ACTION_CALLING_CALLDECLINE="com.zzy.ptt.action.CALLDECLINE";
	//免提
	public static final String ACTION_CALLING_CALLSPEAKER="com.zzy.ptt.action.CALLSPEAKER";
	//静音
	public static final String ACTION_CALLING_CALLMUTE="com.zzy.ptt.action.CALLMUTE";
	//播放铃声
	public static final String ACTION_CALLING_STARTRING="com.zzy.ptt.action.STARTRING";
	//停止铃声
	public static final String ACTION_CALLING_STORPING="com.zzy.ptt.action.STORPING";
	//播放回铃音效
	public static final String ACTION_CALLING_STARTRINGBACK="com.zzy.ptt.action.STARTRINGBACK";
	//通话保持
	public static final String ACTION_CALLING_CALLHOLD="com.zzy.ptt.action.CALLHOLD";
	//通话保持成功
	public static final String ACTION_CALLING_CALLHOLDOK="com.zzy.ptt.action.CALLHOLDOK";
	//通话保持失败
	public static final String ACTION_CALLING_CALLHOLDFAILED="com.zzy.ptt.action.CALLHOLDFAILED";
	//解除保持
	public static final String ACTION_CALLING_CALLUNHOLD="com.zzy.ptt.action.CALLUNHOLD";
	//解除保持成功
	public static final String ACTION_CALLING_CALLUNHOLDOK="com.zzy.ptt.action.CALLUNHOLDOK";
	//解除保持失败
	public static final String ACTION_CALLING_CALLUNHOLDFAILED="com.zzy.ptt.action.CALLUNHOLDFAILED";
	//申请话权
	public static final String ACTION_CALLING_INTERCOM_REQ="com.zzy.ptt.action.INTERCOMREQ";
	//创建临时对讲
    public static final String ACTION_CALLING_TEMPINTERCOM_REQ="com.zzy.ptt.action.TEMPINTERCOMREQ";
	
	//单呼部分
	public static final String ACTION_CALLING_NONE="com.zzy.ptt.action.NONE";
	//呼叫
	public static final String ACTION_CALLING_OUTGOING="com.zzy.ptt.action.OUTGOING";
	//来电回复
	public static final String ACTION_CALLING_RESPONSE = "com.azkj.pad.callingActivity.response";
	//来电
	public static final String ACTION_CALLING_INCOMING="com.zzy.ptt.action.INCOMING";
	//对方响铃
	public static final String ACTION_CALLING_ALERTED="com.zzy.ptt.action.ALERTED";
	//通知CallingACctivity设置SurfaceView渲染
	public static final String ACTION_CALLING_SURFACEVIEW="com.zzy.ptt.action.ALE";
	//通话中
	public static final String ACTION_CALLING_TALKING="com.zzy.ptt.action.TALKING";
	//对方拒绝或终止
	public static final String ACTION_CALLING_TERMED="com.zzy.ptt.action.TERMED";
	//开始通话视频预览
	public static final String ACTION_CALLING_STARTPREVIEW="com.zzy.ptt.action.STARTPREVIEW";
	//开始远程视频
	public static final String ACTION_CALLING_STARTVIDEO="com.zzy.ptt.action.STARTVIDEO";
	//视频显示尺寸
	public static final String ACTION_CALLING_CAPTURESIZE="com.zzy.ptt.action.CAPTURESIZE";
	//视频尺寸
	public static final String ACTION_CALLING_VIDEOSIZE="com.zzy.ptt.action.VIDEOSIZE";
	//停止视频
	public static final String ACTION_CALLING_STOPVIDEO="com.zzy.ptt.action.STOPVIDEO";
	// 通话记录创建会议
	public static final String ACTION_CALLING_CONFERENCE="com.zzy.ptt.action.CALLINGCONFERENCE";
	// 通知接聽下一路通話
	public static final String ACTION_CALLING_ANSWERNEXT="com.zzy.ptt.action.CALLINGANSWERNEXT";
	// 定位创建对讲、通话、会议、消息
	public static final String ACTION_POSITION_OPERATE="com.zzy.ptt.action.POSITIONOPERATE";
	
	//获取当前GIS地址
	public static final String ACTION_GIS_DSTCURPOS="com.zzy.ptt.action.DSTCURPOS";
	// 获取矩形gis信息
	public static final String ACTION_GIS_RECTANGLEPOS="com.zzy.ptt.action.RECTANGLEPOS";
	// 获取椭圆形gis信息
	public static final String ACTION_GIS_ELLIPSEPOS="com.zzy.ptt.action.ELLIPSEPOS";
	// 获取历史位置信息
	public static final String ACTION_GIS_TIMEPOS="com.zzy.ptt.action.TIMEPOS";
	// 追踪好友位置信息
	public static final String ACTION_GIS_TRACEPOS="com.zzy.ptt.action.TRACEPOS";
	// 追踪调度台推送的好友位置信息
	public static final String ACTION_GIS_IND_D_GIS="com.zzy.ptt.action.INDD_GIS";
	// 定位界面添加人员
	public static final String ACTION_GIS_MEMBER = "com.zzy.ptt.action.GISMEMBER";
	//申请上传成功
	public static final String ACTION_MESSAGE_APPLYUPLOADYES="com.zzy.ptt.action.APPLYUPLOADYES";
	//申请上传失败
	public static final String ACTION_MESSAGE_APPLYUPLOADNO="com.zzy.ptt.action.APPLYUPLOADNO";
	//从相册选择图片
	public static final String ACTION_MESSAGE_CHOOSEPHOTO="com.zzy.ptt.action.CHOOSEPHOTO";
	//文件上传成功
	public static final String ACTION_MESSAGE_FILEUPLOADYES="com.zzy.ptt.action.FILEUPLOADYES";
	//消息发送成功
	public static final String ACTION_MESSAGE_MSGSENDOK="com.zzy.ptt.action.MSGSENDOK";
	//消息发送失败
	public static final String ACTION_MESSAGE_MSGSENDFAILED="com.zzy.ptt.action.MSGSENDFAILED";
	//消息接收成功
	public static final String ACTION_MESSAGE_MSGRECEIVEOK="com.zzy.ptt.action.MSGRECEIVEOK";
	//新来消息
	public static final String ACTION_MESSAGE_MSGINCOMMING="com.zzy.ptt.action.MSGINCOMMING";
	//文件接收成功
	public static final String ACTION_MESSAGE_MSGUPLOADOK="com.zzy.ptt.action.MSGUPLOADOK";
	//文件接收异常
	public static final String ACTION_MESSAGE_MSGUPLOADERROR="com.zzy.ptt.action.MSGUPLOADERROR";
	//文件发送成功
	public static final String ACTION_MESSAGE_MSGSENDFILEOK="com.zzy.ptt.action.MSGSENDFILEOK";
	//文件发送异常
	public static final String ACTION_MESSAGE_MSGSENDFILEERROR="com.zzy.ptt.action.MSGSENDFILEERROR";
	//选择联系人
	public static final String ACTION_MESSAGE_MSGADDCONTACT="com.zzy.ptt.action.MSGADDCONTACT";
	//选好了
	public static final String ACTION_MESSAGE_MSGADDCONTACTYES="com.zzy.ptt.action.MSGADDCONTACTYES";
	//取消
	public static final String ACTION_MESSAGE_MSGADDCONTACTNO="com.zzy.ptt.action.MSGADDCONTACTNO";
	//主页面写短信
	public static final String ACTION_MESSAGE_MSGMAINNEW="com.zzy.ptt.action.MSGMAINNEW";
	//选好，取消 切换到新建短信页面
	public static final String ACTION_MESSAGE_SWITCHMESSAGENEW="com.zzy.ptt.action.SWITCHMESSAGENEW";
	//转发文本
	public static final String ACTION_MESSAGE_MSGFORWARD="com.zzy.ptt.action.MSGFORWARD";
	//扫描音频文件的大小，之后刷新listview
	public static final String ACTION_MEDIASCANNER_FILE_DATA_SIZE="com.zzy.ptt.action.mediascanner.file.data.size";
	// 保存当前离线地图的城市id
	public static final String SP_OFFLINEMAP_CITYID = "PTT_SP_OFFLINEMAP_CITYID";
	//切换离线地图到其他城市
	public final static String ACTION_CHANGE_OFFLINEMAP_TO_OTHERCITY="com.chw.change.offlinemap.to.othercity";
	//离线短信提示音
	public static final String ACTION_GETOLDMSG_MUSIC="com.sws.getOldmsg.music";
	//默认摄像头
	public static final String ACTION_GETCAMERA="com.azkj.camera";
	//选择文件返回码
	//拍照
	public static final int RESULT_MESSAGE_PHOTO=1;
	public static final int SHOWRESULT_MESSAGE_PHOTO=6;
	//图片
	public static final int RESULT_MESSAGE_IMAGE=2;
	public static final int SHOWRESULT_MESSAGE_IMAGE=7;
	//音频
	public static final int RESULT_MESSAGE_AUDIO=3;
	public static final int SHOWRESULT_MESSAGE_AUDIO=8;
	//视频
	public static final int RESULT_MESSAGE_VIDEO=4;
	public static final int SHOWRESULT_MESSAGE_VIDEO=9;
	//位置
	public static final int RESULT_MESSAGE_LOCATION=5;
	public static final int SHOWRESULT_MESSAGE_LOCATION=10;
	//切换短信列表
	public static final String ACTION_MESSAGE_SWITCHFRAGMENT="com.zzy.ptt.action.SWITCHFRAGMENT";

	/* intent key */
	public static final String KEY_CURR_USER = "CURR_USER";
	public static final String KEY_VIDEO_USER = "VIDEO_USER";
	public static final String KEY_CALL_SESSIONID = "CALL_SESSIONID";// 通话回话编号
	public static final String KEY_CALL_OPPOSITENO = "CALL_OPPOSITENO";// 通话对方号码
	public static final String KEY_CALL_MEDIATYPE = "CALL_MediaType";// 通话类型
	public static final String KEY_CALL_TYPE = "CALL_TYPE";// 通话类型(对讲、单呼、会议)
	public static final String KEY_CALL_ID = "CALL_ID";// 呼叫ID=session,yuezs add 2017-09-26
	public static final String KEY_CALL_NUMBER = "CALL_NUMBER";// 被呼叫号码,yuezs add 2017-09-26
	public static final String KEY_CALL_STATE = "CALL_STATE";// 状态,yuezs add 2017-09-26
	
	public static final String KEY_CALL_AUTOANSWER = "CALL_AUTOANSWER";// 是否自动接听(对讲、单呼、会议)
	public static final String KEY_CALL_CID = "CALL_cid";// cid
	public static final String KEY_CALL_DWWIDTH = "CALL_DWWIDTH";// 视频宽度
	public static final String KEY_CALL_DWHEIGHT = "CALL_DWHEIGHT";// 视频高度
	public static final String KEY_CALL_CONFERENCEID = "CONFERENCEID";// 会议编号
	public static final String KEY_CALL_CHANGEVIEW = "CHANGEVIEW";// 是否改变界面
	public static final String KEY_CALL_ISVIDEO = "ISVIDEO";// 是否改变界面
	public static final String KEY_PTT_GROUP = "PTT_GROUP";// 当前对讲组
	public static final String KEY_PTT_SPEAKER = "PTT_SPEAKER";// 对讲当前说话人
	public static final String KEY_PTT_QUEUE = "PTT_QUEUE";// 申请话权排队
	public static final String KEY_PTT_RELESEQUEUE = "PTT_RELESEQUEUE";// 取消排队
	public static final String KEY_PTT_ERROR = "PTT_ERROR";// 错误信息
	public static final String KEY_PTT_CALLID = "PTT_CALLID";// 会议唯一标识
	public static final String KEY_PTT_CALLSTATE = "PTT_CALLSTATE";// 通话状态s
	public static final String KEY_MEETING_INORDE = "MEETING_INORDE";// 增减状态
	public static final String KEY_MEETING_RECORD = "MEETING_RECORD";// 创建会议告知主页面为本地创建，自动接听
	public static final String KEY_MEETING_MEMBERINFO = "MEETING_MEMBERINFO";// 成员信息
	public static final String KEY_MEETING_RESULT = "MEETING_RESULT";// 创建会议结果
	public static final String KEY_MEETING_SID = "MEETING_SID";// 创建会议消息标识
	public static final String KEY_MEETING_SESSNUM = "MEETING_SESSNUM";// 创建会议名称
	public static final String KEY_MEETING_CID = "MEETING_CID";// 创建会议调度机返回的会议的唯一标识
	public static final String KEY_MEETING_ERROR = "MEETING_ERROR";// 创建会议错误号
	public static final String KEY_MEETING_DIS = "MEETING_DIS";// 创建会议错误描述
	public static final String KEY_MEETING_MEMINFO = "MEETING_MEMINFO";// 获取会议成员结果
	public static final String KEY_MEETING_NAME = "MEETING_NAME";// 状态发生变化的会议成员名称
	public static final String KEY_MEETING_EMPLOYEEID = "MEETING_EMPLOYEEID";// 状态发生变化的会议成员ID
	public static final String KEY_MEETING_TYPE = "MEETING_TYPE";// 成员类型
	public static final String KEY_MEETING_STATE = "MEETING_STATE";// 成员状态
	public static final String KEY_MEETING_NOS = "MEETING_NOS";// 成员编号列表
	public static final String KEY_MEETING_PUSHNUM = "MEETING_PUSHNUM";// 视频推送成员号
	public static final String KEY_MONITOR_MEMBERINFO = "MONITOR_MEMBERINFO";// 监控成员信息
	public static final String KEY_MONITOR_STREAMID = "MONITOR_STREAMID";// 监控视频流标识
	public static final String KEY_MONITOR_IP = "MONITOR_IP";// 监控获取到的视频的IP地址
	public static final String KEY_MONITOR_PORT = "MONITOR_PORT";// 监控获取到的视频的端口
	public static final String KEY_MONITOR_CODEC = "MONITOR_CODEC";// 监控编解码
	public static final String KEY_MONITOR_STREAM = "MONITOR_STREAM";// 监控码流
	public static final String KEY_MONITOR_FRAMERATE = "MONITOR_FRAMERATE";// 监控帧率
	public static final String KEY_MONITOR_ERROR = "MONITOR_ERROR";// 监控获取视频错误号
	public static final String KEY_MONITOR_DIS = "MONITOR_DIS";// 监控获取视频错误描述
	public static final String KEY_POSITION_TYPE = "POSITION_TYPE";// 定位创建对讲、会议、通话、消息类型
	public static final String KEY_PTT_CANCEL_RSPCODE="PTT_CANCEL_RSPCODE";
	
	/*主页tab索引*/
	public static final String INTERCOM_TAB = "intercom_tab";
	public static final String CALLING_TAB = "calling_tab";
	public static final String MEETING_TAB = "meeting_tab";
	public static final String MESSAGE_TAB = "message_tab";
	public static final String CONTACT_TAB = "contact_tab";
	public static final String POSITION_TAB = "position_tab";
	public static final String MONITOR_TAB = "monitor_tab";
	public static final String SETTING_TAB = "setting_tab";
	public static final String BROADCAST_TAB = "broadcast_tab";//此不是广播页面，而是为了区分监控页面中的按钮操作
	
	/*组成员状态*/
//	public static final int GROUP_MEMBER_OFFLINE = 0; // 不在线
//	public static final int GROUP_MEMBER_LISTENING = 1;
//	public static final int GROUP_MEMBER_SPEAKING = 2;
//	public static final int GROUP_MEMBER_ONLINE = 3; // 在线
	public static final int GROUP_MEMBER_EMPTY = 0;// 无状态
	public static final int GROUP_MEMBER_INIT = 1;// 初始
	public static final int GROUP_MEMBER_ONLINE = 2; // 在线
	public static final int GROUP_MEMBER_OUTGOING = 3; // 呼出
	public static final int GROUP_MEMBER_INCOMING = 4; // 呼入
	public static final int GROUP_MEMBER_ALERTED = 5; // 振铃
	public static final int GROUP_MEMBER_TALKING = 6; // 通话
	public static final int GROUP_MEMBER_CALLHOLD = 7; // 保持
	public static final int GROUP_MEMBER_BUSY = 8; // 用户忙
	public static final int GROUP_MEMBER_OFFHOOK = 9; // 摘机
	public static final int GROUP_MEMBER_RELEASE = 10; // 释放
	public static final int GROUP_MEMBER_LISTENING = 11; // 听讲
	public static final int GROUP_MEMBER_SPEAKING = 12; // 讲话
	public static final int GROUP_MEMBER_QUEUING = 13; // 排队
	public static final int GROUP_MEMBER_WITHDRAW = 14; // 退会

	/*对讲状态*/
	public static final int PTT_STATUS_EMPTY = 0;// 空闲
	public static final int PTT_STATUS_REQUEST = 1;// 申请话权
	public static final int PTT_STATUS_ACCEPT = 2;// 已获得
	public static final int PTT_STATUS_WAITING = 3;// 等待中
	public static final int PTT_STATUS_REJECT = 4;// 被拒绝
	public static final int PTT_STATUS_BLANK = 5;// 空白
	public static final int PTT_STATUS_RELEASE = 6;// 释放
	
	/*参会成员状态*/
	public static final int MEETING_MEMBER_OFFLINE = 0;//离线
	public static final int MEETING_MEMBER_LIVING = 1;//离会
	public static final int MEETING_MEMBER_HOLDON = 2;//保持
	public static final int MEETING_MEMBER_MEETING = 3;//会议
	
	/*呼叫类型*/
	public static final int CALL_TYPE_INTERCOM = 1;// 对讲
	public static final int CALL_TYPE_CALLING = 2;// 单呼
	public static final int CALL_TYPE_MEETING = 3;// 会议
	public static final int CALL_TYPE_MONITOR = 4;// 监控
	public static final int CALL_TYPE_TEMPINTERCOM = 5;//地图发起的临时对讲

	/*通话状态*/
	// 通话状态无
	public static final int CALL_STATE_NONE = 0;
	// 通话状态呼入中
	public static final int CALL_STATE_INCOMING = 1;
	// 通话状态回答中
	public static final int CALL_STATE_ANSWERING = 2;
	// 通话状态呼出中
	public static final int CALL_STATE_CALLING = 3;
	// 通话状态发出中
	public static final int CALL_STATE_OUTGOING = 4;
	// 通话状态通知中
	public static final int CALL_STATE_ALERTED = 5;
	// 通话状态通话中
	public static final int CALL_STATE_TALKING = 6;
	// 通话状态结束中
	public static final int CALL_STATE_ENDING = 7;
	// 通话状态消亡中
	public static final int CALL_STATE_DECLINING = 8;
	
	//短消息布局类型
	public static final int LAYOUT_TEXT_IN=0;//接收文本
	public static final int LAYOUT_TEXT_OUT=1;//发送文本
	public static final int LAYOUT_IMG_IN=2;//接收图片
	public static final int LAYOUT_IMG_OUT=3;//发送图片
	public static final int LAYOUT_AUDIO_IN=4;//接收语音
	public static final int LAYOUT_AUDIO_OUT=5;//发送语音
	public static final int LAYOUT_VIDEO_IN=6;//接收视频
	public static final int LAYOUT_VIDEO_OUT=7;//发送视频
	public static final int LAYOUT_FILE_IN=8;//接收文件

	//短消息类型
	public static final int MESSAGE_TEXT=0;//文本
	public static final int MESSAGE_IMG=2;//图片
	public static final int MESSAGE_AUDIO=3;//语音
	public static final int MESSAGE_VIDEO=4;//视频
	public static final int MESSAGE_FILE=8;
	//短消息发送接收类型
	public static final int MESSAGE_IN=1;//呼入
	public static final int MESSAGE_OUT=0;//呼出
	
	//短消息发送状态
	public static final int MESSAGE_SEND_OK=0;//成功
	public static final int MESSAGE_SEND_FAILED=1;//失败
	
	//短消息阅读状态
	public static final int MESSAGE_READ_YES=0;//已读
	public static final int MESSAGE_READ_NO=1;//未读
	//音频收听状态
	public static final String MESSAGE_AUDIO_READ_KEY="AUDIO_READ_KEY";
	public static final int MESSAGE_AUDIO_READ_YES=1;
	public static final int MESSAGE_AUDIO_READ_NO=0;
	
	/*音频线路 */
	public static final int AR_HANDSET = 0;// 手机听筒
	public static final int AR_SPEAKER = 1;// 扬声器
	public static final int AR_EARPHONE = 2;// 耳机
	
	
	/*会议类型*/
	public static final int CONFERENCE_TYPE_BROADCAST = 4;//广播
	public static final int CONFERENCE_TYPE_TEMPORARY = 5;//临时会议
	public static final int CONFERENCE_TYPE_INTERCOM = 13;//临时对讲

	/*会议媒体类型*/
	public static final int CONFERENCE_MEDIATYPE_VOICE = 1;//语音通话
	public static final int CONFERENCE_MEDIATYPE_VIDEO = 2;//视频通话
	
	/*会议成员应答模式*/
	public static final int CONFERENCE_ANSWERTYPE_NORMAL = 0;//正常
	public static final int CONFERENCE_ANSWERTYPE_AUTO = 1;//自动应答
	
	/*会议成员成员类型*/
	public static final int CONFERENCE_MEMBERTYPE_DISPATCH = 0;//调度台
	public static final int CONFERENCE_MEMBERTYPE_HANDSET = 1;//手持终端
	public static final int CONFERENCE_MEMBERTYPE_USER = 2;//调度用户
	public static final int CONFERENCE_MEMBERTYPE_EXTERNAL = 3;//外线号码
	
	/*会议成员成员状态*/
	/*public static final int CONFERENCE_MEMBERSTATE_NONE = 0;//无
	public static final int CONFERENCE_MEMBERSTATE_OFFLINE = 1;//离线
	public static final int CONFERENCE_MEMBERSTATE_ONLINE = 2;//在线
	public static final int CONFERENCE_MEMBERSTATE_RINGING = 6;//振铃中
	public static final int CONFERENCE_MEMBERSTATE_TALKING = 7;//通话中
	public static final int CONFERENCE_MEMBERSTATE_HOLDING = 8;//保持中
	public static final int CONFERENCE_MEMBERSTATE_SHUTUP = 12;//禁言
	public static final int CONFERENCE_MEMBERSTATE_LEAVE = 13;//离会
	public static final int CONFERENCE_MEMBERSTATE_SPEAK = 14;//发言
	public static final int CONFERENCE_MEMBERSTATE_QUEUE = 15;//排队*/
	
	/*监控组用户类型*/
	public static final int MONITOR_GROUPTYPE_GROUP = 0;// 对讲组
	public static final int MONITOR_GROUPTYPE_DEPART = 1;// 部门
	public static final int MONITOR_USERTYPE_DISPATCH = 0;// 调度台
	public static final int MONITOR_USERTYPE_INTERCOM = 1;// 对讲终端
	public static final int MONITOR_USERTYPE_TELEPHONE = 2;// 软电话
	public static final int MONITOR_USERTYPE_OUTERUSER = 3;// 外线用户
	public static final int MONITOR_USERTYPE_3GINTERCOM = 6;// 3G对讲
	public static final int MONITOR_USERTYPE_MONITOR = 7;// 监控设备
	
	/*监控用户类型*/
	public static final int MONITOR_TYPE_DEFAULT = 0;//默认
	public static final int MONITOR_TYPE_CAMERA = 1;//摄像头
	public static final int MONITOR_TYPE_PHONE = 2;//手机
	public static final int MONITOR_TYPE_MONITOR = 7;//监控用户
	
	/*监控用户状态*/
	public static final int MONITOR_MEMBER_OFFLINE = 0;//离线
	public static final int MONITOR_MEMBER_OUTGOING = 4;//外呼中
	public static final int MONITOR_MEMBER_TAKING = 7;//通话中
	public static final int MONITOR_MEMBER_RELEASE = 11;//释放
	
	/*视频类型(用于区分视频会议和视频监控)*/
	public static final int VIDEO_NONE = 0;//未进行视频
	public static final int VIDEO_MEETING = 1;//视频会议
	public static final int VIDEO_MONITOR = 2;//视频监控
	/*切换页签*/
	public static final int ALL=0;
	public static final int ONLYTAB=1;
	public static final int ONLYCONTENT=2;

	//呼出类型
	public static final String CALLTYPE_VIDEO = "video";
	public static final String CALLTYPE_VOICE = "voice";



	/*分辨率*/
	public static final String SP_MONITOR_RESOLUTION = "CallResolution";
	//宽
	public static final String SETTINGVIDEO_ACTIVITY_K = "com.azkj.activity.settingActivity.k";
	//高
	public static final String SETTINGVIDEO_ACTIVITY_H = "com.azkj.activity.settingActivity.h";
	//前置旋转角度
	public static final String SETTINGVIDEO_ACTIVITY_CAMERA_Q = "com.azkj.activity.settingActivity.camera_q";
	//后置旋转角度
	public static final String SETTINGVIDEO_ACTIVITY_CAMERA_H = "com.azkj.activity.settingActivity.camera_h";
	//文件开始上传
	public static final String FILE_PROGRESS_START = "com.azkj.activity.file.progress_start";
	//文件上传进度
	public static final String FILE_PROGRESS = "com.azkj.activity.file.progress";
	//文件上传结束
	public static final String FILE_PROGRESS_STOP = "com.azkj.activity.file.progress_stop";
	//视频编码比例设置
	public static final String SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION = "com.azkj.activity.settingActivity.videoSend_Proportion";
	//视频解码模式设置
	public static final String SETTINGVIDEO_ACTIVITY_VIDEOSCALINGMODE = "settingVideo_Activity_VideoScalingMode";
	//视频录制优化
	public static final String ACTION_MSG_SENDVIDEO = "Optimize the quality of video recording";
	//获取当前会议模式
	public static final String ACTION_MEETING_TYPE = "action_meeting_type";
	//MCU会议解码上墙开始
	public static final String ACTION_MCUMEETING_STARTDecode = "ACTION_McuMeeting_StartDecode";
	//MCU会议解码上墙结束
	public static final String ACTION_MCUMEETING_EndDecode = "ACTION_McuMeeting_EndDecode";
	//开始云控
	public static final String ACTION_CAMERA_CLOUDCONTROL = "ACTION_Camera_CloudControl";
	//隐藏云控操作页面
	public static final String ACTION_CAMERA_HIDE_CLOUDCONTROL = "ACTION_Camera_Hide_CloudControl";
	//标识消息上传是否完成
	public static final int MESSAGE_PROGRESS_COMPLETE = 1;
	public static final int MESSAGE_PROGRESS_NoCOMPLETE = 0;

	//标识当前是否手动切换calling界面
	public static final String ACTION_CALLSTATE = "callState";
	//标识当前是否手动切换meeting界面
	public static final String ACTION_MEETINGSTATE = "MeetingState";

	//初始值
	public static final int AUTOANSWER_INIT = 0;
	//自动接听
	public static final int AUTOANSWER_IMPLEMENT = 1;

	//通知会议界面隐藏/显示本地视频
	public static final String ACTION_MEETING_HIDEORSHOW = "action_meeting_hideOrShow";
	//通知主界面显示当前离线短信下载情况
	public static final String ACTION_NOTIFICATIONMAINOLDMESSAGERESULT = "action_notification_main_messageLoadResult";
	//是否输出日志
	public static final String SP_START_LOG = "SP_START_LOG";

	//标识当前是否有文件上传
	public static final String isFileUpLoad = "isFileUpLoad";

	//标识当前主动呼出通话类型
	public static final String CALLHASVIDEO = "callHasVideo";

	//回音消除相关
	//模块参数
	public static final String MODULAR_SPEEX = "1";
	public static final String MODULAR_SIMPLE = "2";
	public static final String MODULAR_WEBRTC = "3";

	//降噪参数
	public static final boolean NOISEREDUCTION_TRUE = true;
	public static final boolean NOISEREDUCTION_FALSE = false;

	//等级参数
	public static final String GRADE_1 = "1";
	public static final String GRADE_2 = "2";
	public static final String GRADE_3 = "3";

	//模块
	public static final String SEETING_MODULAR = "seeting_modular";
	//降噪
	public static final String SEETING_NOISEREDUCTION = "seeting_noiseReduction";
	//等级
	public static final String SEETING_GRADE = "seeting_grade";

	//统计丢包率时延
	public static final String SEETING_MEDIASTATISTICS = "seeting_mediastatistics";

	//广播
	public static final String MEETING_STARTTIMER = "meetingStartTimer";
	public static final String MEETING_STOPTIMER = "meetingStopTimer";
	public static final String MEETING_SETFALSE = "meetingsetFalse";
	public static final String MEETING_SETTRUE = "meetingSetTrue";

	//发送缓存大小
	public static final String SEETINGOTHER_SENDBUFFER = "seetingOther_sendBuffer";
	//接收缓存大小
	public static final String SEETINGOTHER_RECEIVEBUFFER = "seetingOther_receiveBuffer";


	public static final int STATE_UPLOADING = 1; //正在上传状态
	public static final int STATE_UPLOADED = 2;  //上传完成状态

	//IncomingCallType
	public static final String INCOMINGCALLTYPE_SINGLE = "1";  //单呼
	public static final String INCOMINGCALLTYPE_SINGLE2 = "12";  //单呼
	public static final String INCOMINGCALLTYPE_INTERCOM = "9";  //对讲
	public static final String INCOMINGCALLTYPE_BROADCAST = "4";  //广播
	public static final String INCOMINGCALLTYPE_TEMPORARY = "5";  //会议
	public static final String INCOMINGCALLTYPE_TMPINTERCOM = "13";  //临时对讲
	public static final String INCOMINGCALLTYPE_MCUMEETING = "15";  //MCU会议
	public static final String INCOMINGCALLTYPE_VIDEOBUG = "14";  //3G监控
	public static final String INCOMINGCALLTYPE_TransferVideo = "16";  //紧急呼叫
	public static final String INCOMINGCALLTYPE_TransferVideo2 = "11";  //紧急呼叫

	public static final int FileTypeNone = 0;
	public static final int FileTypeText = 1;
	public static final int FileTypePic = 2;
	public static final int FileTypeVoice = 3;
	public static final int FileTypeVideo = 4;
	public static final int FileTypeGisinfo = 5;
	public static final int FileTypePlayAudio = 6;
	public static final int FileTypeFax = 7;
	public static final int FileTypeOther = 8;
	public static final int FileTypePlayVideo = 9;

	//标识ICE是否连接成功
	public static final String ICECONNECTION = "ICE_Conncetion";
	//切换界面停止播放音频
	public static final String StopMediaPlayerReceiver = "StopMediaPlayerReceiver";
	//将上墙视频下墙
	public static final String DECODINGTHEWALLRECEIVER = "decodingTheWallReceiver";
	//是否全部下墙
	public static final String ISALL = "ISALL";
	//是
	public static final String ISALL_YES = "ISALL_Yes";
	//不是
	public static final String ISALL_NO = "ISALL_No";
	//上墙号码
	public static final String DECODER_NUMBER = "Decoder_Number";
	//移除或者添加地图Action
	public static final String REMOVE_OR_ADD_MAPVIEW = "removeoraddmapview";
	//切换分屏
	public static final String SWITCHSPLITSCREEN_RECEIVER = "SwitchSplitScreen";
	public static final String ACTION_CALLING_INTERCOM_RELEASE="com.zzy.ptt.action.INTERCOMRELEASE";
	//音频模式
	public static final String SP_AUDIOMANAGER_GETMODE = "PTT_SP_AUDIOMANAGER_MODE";
	//无花屏模式
	public static final String SEETING_OTHER_FLOWER_SCREEN = "Seeting_Other_FlowerScreen";
	//是否自动接听
	public static final String ACTION_CALLING_ISANSWER = "action_calling_IsAnswer";
	//是否启用h265
	public static final String ACTION_ENABLE_H265 = "action_Enable_h265";
	//机构数数据加载完毕通知系统联系人界面加载数据
	public static final String ACTION_DATELOADINGSUCCESS = "action_DateLoadingSuccess";
	//成员上下线刷新机构数广播
	public static final String ACTION_REFRESH_DATE = "Action_Refresh_Date";
	//动态重组重新加载机构数广播
	public static final String ACTION_GROUPINFOREFRESH_TRESSDATE = "Action_GroupInfoRefresh_TressDate";
	//通知主界面更新GIS图标
	public static final String ACTION_NOTIFICATION_MAIN_GIS_IMG = "Action_Notificetion_Main_Gis_Img";
}

