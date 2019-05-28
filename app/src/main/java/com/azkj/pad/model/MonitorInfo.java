package com.azkj.pad.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import com.azkj.pad.utility.GlobalConstant;
import com.baizhi.app.MultiPlayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* 监控用户信息*/
public class MonitorInfo implements Parcelable{
	
	// 用户名
	private String name;
	// 用户编号
	private String number;
	// 组号
	private String id;
	// grouptype：组类型(0-对讲组，1部门)
	// usertype：用户类型（0-调度台，1-对讲终端，2-软电话，3-外线用户，6-3G对讲，7-监控设备）
	private int type;
	// 用户状态
	private int status = GlobalConstant.MONITOR_MEMBER_OFFLINE;
	// 是否为用户
	private boolean isuser = false;
	// 组用户
	private String userinfo;
	// 子组成员
	private String childreninfo;
	// 子组成员
	private List<MonitorInfo> children = new ArrayList<MonitorInfo>();
	// 是否为监控
	private boolean ismonitor = false;
	// 是否为通话
	private boolean iscalling = false;
	// 在监控中
	private boolean inmonitoring = false;
	// 视频端口
	private int vedioPort = 0;
	// 对应布局
	private View view;
	// 视频容器
	private Surface surface;
	// 视频容器
	private SurfaceHolder surfaceHolder;
	// 视频播放
	private MultiPlayer multiplayer;
	// 视频容器
	private TextureView textureView;
	// 消息标识
	private String sid;
	// 监控视频流标识
	private String streamid;
	// 监控获取到的视频的IP地址
	private String ip;
	// 监控获取到的视频的端口
	private String port;
	// 监控编解码
	private String codec;
	// 监控码流
	private String stream;
	// 监控帧率
	private String framerate;
	// 远程端口
	private String remotePort;
	// 开始计时
	private Date startDate;
	// 计时器
	private Chronometer timer;
	// 唯一标识
	private String cid;
	// 是否得到焦点
	private boolean focused = false;
	// 上级组
	private MonitorInfo parent = null;
	// 是否为叶节点
	private boolean isLeaf = false;
	// 该节点是否展开
	private boolean isExpanded = false;
	//该节点的图标对应的id
	private int icon = -1;
	// 展开图片
	private int iconForExpanding = -1;
	// 折叠图片
	private int iconForFolding = -1;
	//多级前缀
	private String prefix = null;
	//当前为获取成员视频还是获取监控
	private boolean isGetVideo = false;
	//当前视频对应容器 例如：1 2 3
	//						 4 5 6
	//						 7 8 9
	private int index = -1;
	//布局
	private LinearLayout mLinearLayot;




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isIsuser() {
		return isuser;
	}

	public void setIsuser(boolean isuser) {
		this.isuser = isuser;
	}

	public String getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(String userinfo) {
		this.userinfo = userinfo;
	}

	public String getChildreninfo() {
		return childreninfo;
	}

	public void setChildreninfo(String childreninfo) {
		this.childreninfo = childreninfo;
	}

	public List<MonitorInfo> getChildren() {
		return children;
	}

	public void setChildren(List<MonitorInfo> children) {
		this.children = children;
	}

	public boolean isIsmonitor() {
		return ismonitor;
	}

	public void setIsmonitor(boolean ismonitor) {
		this.ismonitor = ismonitor;
	}

	public boolean isIscalling() {
		return iscalling;
	}

	public void setIscalling(boolean iscalling) {
		this.iscalling = iscalling;
	}

	public boolean isInmonitoring() {
		return inmonitoring;
	}

	public void setInmonitoring(boolean inmonitoring) {
		this.inmonitoring = inmonitoring;
	}

	public int getVedioPort() {
		return vedioPort;
	}

	public void setVedioPort(int vedioPort) {
		this.vedioPort = vedioPort;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Surface getSurface() {
		return surface;
	}

	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	public SurfaceHolder getSurfaceHolder() {
		return surfaceHolder;
	}

	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}

	public MultiPlayer getMultiplayer() {
		return multiplayer;
	}

	public void setMultiplayer(MultiPlayer multiplayer) {
		this.multiplayer = multiplayer;
	}
	
	public TextureView getTextureView() {
		return textureView;
	}
	
	public void setTextureView(TextureView textureView) {
		this.textureView = textureView;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getStreamid() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getFramerate() {
		return framerate;
	}

	public void setFramerate(String framerate) {
		this.framerate = framerate;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Chronometer getTimer() {
		return timer;
	}

	public void setTimer(Chronometer timer) {
		this.timer = timer;
	}

	public MonitorInfo getParent() {
		return parent;
	}

	public void setParent(MonitorInfo parent) {
		this.parent = parent;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getIconForExpanding() {
		return iconForExpanding;
	}

	public void setIconForExpanding(int iconForExpanding) {
		this.iconForExpanding = iconForExpanding;
	}

	public int getIconForFolding() {
		return iconForFolding;
	}

	public void setIconForFolding(int iconForFolding) {
		this.iconForFolding = iconForFolding;
	}

	public MonitorInfo(){
		
	}
	
	public MonitorInfo(String buddyNo, int vedioPort){
		this.number = buddyNo;
		this.vedioPort = vedioPort;
	}
	
	public MonitorInfo(MonitorInfo parent, String id, String number, String name, int type, int status, boolean isLeaf, int icon, int exIcon, int foIcon){
		this.parent = parent;
		this.id = id;
		this.number = number;
		this.name = name;
		this.type = type;
		this.status = status;
		this.isLeaf = isLeaf;
		this.icon = icon;
		this.iconForExpanding = exIcon;
		this.iconForFolding = foIcon;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);
		dest.writeString(id);
		dest.writeInt(type);
		dest.writeInt(status);
		dest.writeBooleanArray(new boolean[]{ismonitor});
		dest.writeString(userinfo);
		dest.writeString(childreninfo);
		dest.writeString(sid);
		dest.writeString(codec);
		dest.writeString(cid);
	}
	
	@Override
	public String toString() {
		return "MonitorInfo [name=" + name + ", number=" + number + ", id=" + id + "]";
	}
	
	public static final Creator<MonitorInfo> CREATOR = new Creator<MonitorInfo>() {

		@Override
		public MonitorInfo createFromParcel(Parcel source) {
			MonitorInfo grpInfo = new MonitorInfo();
			grpInfo.setName(source.readString());
			grpInfo.setNumber(source.readString());
			grpInfo.setId(source.readString());
			grpInfo.setType(source.readInt());
			grpInfo.setStatus(source.readInt());
			boolean[] ismonitor = new boolean[1];
			source.readBooleanArray(ismonitor);
			grpInfo.setIsmonitor(ismonitor[0]);
			grpInfo.setUserinfo(source.readString());
			grpInfo.setChildreninfo(source.readString());
			// 处理子组数据
			if ((grpInfo.getChildreninfo() != null)
				&& (grpInfo.getChildreninfo().length() > 0)){
				/*System.out.println("lizhiwei getChildreninfo " + grpInfo.getChildreninfo());*/
				String[] childinfos = grpInfo.getChildreninfo().split(",");
				if ((childinfos != null)
					&& (childinfos.length > 0)){
					for(String userinfo : childinfos){
						if ((userinfo == null)
							|| (userinfo.trim().length() <= 0)){
							continue;
						}
						MonitorInfo videouser = new MonitorInfo();
						videouser.setId(userinfo.trim());
						videouser.setNumber(userinfo.trim());
						grpInfo.getChildren().add(videouser);
					}
				}
			}
			// 处理用户数据
			if ((grpInfo.getUserinfo() != null)
				&& (grpInfo.getUserinfo().length() > 0)){
				/*System.out.println("lizhiwei getUserinfo " + grpInfo.getUserinfo());*/
				String[] users = grpInfo.getUserinfo().split("\\)");
				if ((users != null)
					&& (users.length > 0)){
					for(String userinfo : users){
						if ((userinfo == null)
							|| (userinfo.trim().length() <= 0)){
							continue;
						}
						String[] userinfos = userinfo.split(",");
						if (userinfos.length < 3){
							continue;
						}
						MonitorInfo videouser = new MonitorInfo();
						videouser.setId(userinfos[0].substring(1));
						videouser.setNumber(userinfos[0].substring(1));
						videouser.setName(userinfos[1]);
						videouser.setType(Integer.parseInt(userinfos[2]));
						videouser.setIsuser(true);
						grpInfo.getChildren().add(videouser);
					}
				}
			}
			grpInfo.setSid(source.readString());
			grpInfo.setCodec(source.readString());
			grpInfo.setCid(source.readString());
			
			return grpInfo;
		}

		@Override
		public MonitorInfo[] newArray(int size) {
			return null;
		}
	};
	
	// 得到当前节点所在的层数，根为0层
	public int getLevel()
	{
		return parent == null?0:parent.getLevel() + 1;
	}
	
	// 是否为根节点
	public boolean isRoot()
	{
		return this.parent == null?true:false;
	}
	
	//得到展开或折叠图标
	public int getExpandOrFoldIcon()
	{
		if(this.isExpanded == true){
			return this.iconForExpanding;
		}
		else {
			return this.iconForFolding;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isGetVideo() {
		return isGetVideo;
	}

	public void setGetVideo(boolean getVideo) {
		isGetVideo = getVideo;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public LinearLayout getmLinearLayot() {
		return mLinearLayot;
	}

	public void setmLinearLayot(LinearLayout mLinearLayot) {
		this.mLinearLayot = mLinearLayot;
	}
}
