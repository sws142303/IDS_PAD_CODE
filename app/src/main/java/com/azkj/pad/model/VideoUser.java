package com.azkj.pad.model;

import android.os.Parcel;
import android.os.Parcelable;

/*视频用户信息*/
public class VideoUser implements Parcelable{
	// 视频设备IP
	private String ip;
	// 视频端口
	private int port;
	// 登录用户名
	private String user;
	// 登录密码
	private String pwd;
	// 显示名称
	private String showname;
	// 设备类型（1、海康）
	private int type;
	// 绑定的号码
	private String bindemployeeid;
	// 状态
	private int status = 0;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getBindemployeeid() {
		return bindemployeeid;
	}
	public void setBindemployeeid(String bindemployeeid) {
		this.bindemployeeid = bindemployeeid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public VideoUser(){
		
	}
	public VideoUser(String ip, int port, String user, String pwd, String showname, int type, String bindemployeeid){
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
		this.showname = showname;
		this.type = type;
		this.bindemployeeid = bindemployeeid;
	}
	public VideoUser(String ip, String port, String user, String pwd, String showname, String type, String bindemployeeid){
		this.ip = ip;
		this.port = Integer.parseInt(port);
		this.user = user;
		this.pwd = pwd;
		this.showname = showname;
		this.type = Integer.parseInt(type);
		this.bindemployeeid = bindemployeeid;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ip);
		dest.writeInt(port);
		dest.writeString(user);
		dest.writeString(pwd);
		dest.writeString(showname);
		dest.writeInt(type);
		dest.writeString(bindemployeeid);
		dest.writeInt(status);
	}
	
	public static final Creator<VideoUser> CREATOR = new Creator<VideoUser>() {

		@Override
		public VideoUser createFromParcel(Parcel source) {
			VideoUser vdoUser = new VideoUser();
			vdoUser.setIp(source.readString());
			vdoUser.setPort(source.readInt());
			vdoUser.setUser(source.readString());
			vdoUser.setPwd(source.readString());
			vdoUser.setShowname(source.readString());
			vdoUser.setType(source.readInt());
			vdoUser.setBindemployeeid(source.readString());
			vdoUser.setStatus(source.readInt());
			return vdoUser;
		}

		@Override
		public VideoUser[] newArray(int size) {
			return null;
		}
	};
}
