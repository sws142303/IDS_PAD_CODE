package com.azkj.pad.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

import com.baizhi.app.MultiPlayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*通讯录*/
@SuppressLint("SimpleDateFormat")
public class Contacts implements Parcelable, Cloneable{
	private static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
	// 登录号码
	private String userNo;
	// 好友号码
	private String buddyNo;
	// 好友姓名
	private String buddyName;
	// 头像
	private Bitmap photo;
	// 组别
	private String groupNo;
	// 本地号码
	private String phoneNo;
	// 备注
	private String remark;
	// 添加时间
	private Date createDate;
	// 修改时间
	private Date updateDate;
	//是否定位
	private boolean isPos=false;
	// 在会议中
	private boolean inmeeting = false;
	// 与会成员状态
	private int meetingState = 0;
	// 成员类型
	private int memberType = 0;
	// 视频端口
	private int vedioPort = 0;
	// 对应布局
	private View view;
	// 视频容器
	private SurfaceHolder surfaceHolder;
	// 视频播放
	private MultiPlayer multiplayer;
	// 视频容器
	private TextureView textureView;
	// 视频播放
	private MediaPlayer mediaPlayer;
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
	// 是否得到焦点
	private boolean focused = false;
	
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getBuddyNo() {
		return buddyNo;
	}
	public void setBuddyNo(String buddyNo) {
		this.buddyNo = buddyNo;
	}
	public String getBuddyName() {
		return buddyName;
	}
	public void setBuddyName(String buddyName) {
		this.buddyName = buddyName;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public String getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public boolean isPos() {
		return isPos;
	}
	public void setPos(boolean ispos) {
		this.isPos = ispos;
	}
	
	public boolean isInmeeting() {
		return inmeeting;
	}
	public void setInmeeting(boolean inmeeting) {
		this.inmeeting = inmeeting;
	}
	public int getMeetingState() {
		return meetingState;
	}
	public void setMeetingState(int meetingState) {
		this.meetingState = meetingState;
	}
	public int getMemberType() {
		return memberType;
	}
	public void setMemberType(int memberType) {
		this.memberType = memberType;
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
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
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
	public boolean isFocused() {
		return focused;
	}
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
	public Contacts(){
		
	}
	
	public Contacts(String buddyNo, int vedioPort){
		this.buddyNo = buddyNo;
		this.vedioPort = vedioPort;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userNo);
		dest.writeString(buddyNo);
		dest.writeString(buddyName);
		dest.writeString(groupNo);
		dest.writeString(phoneNo);
		dest.writeString(remark);
		dest.writeString((createDate == null)?"":dateFormatter.format(createDate));
		dest.writeString((updateDate == null)?"":dateFormatter.format(updateDate));
		dest.writeInt(meetingState);
		dest.writeInt(memberType);
	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
	public static final Creator<Contacts> CREATOR = new Creator<Contacts>() {

		@Override
		public Contacts createFromParcel(Parcel source) {
			Contacts contact = new Contacts();
			contact.setUserNo(source.readString());
			contact.setBuddyNo(source.readString());
			contact.setBuddyName(source.readString());
			contact.setGroupNo(source.readString());
			contact.setPhoneNo(source.readString());
			contact.setRemark(source.readString());
			String createdate = source.readString();
			if ((createdate != null)
				&& (createdate.length() > 0)){
				try {
					contact.setCreateDate(dateFormatter.parse(createdate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			String updatedate = source.readString();
			if ((updatedate != null)
				&& (updatedate.length() > 0)){
				try {
					contact.setUpdateDate(dateFormatter.parse(updatedate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			contact.setMeetingState(source.readInt());
			contact.setMemberType(source.readInt());
			return contact;
		}

		@Override
		public Contacts[] newArray(int size) {
			return null;
		}
	};
}
