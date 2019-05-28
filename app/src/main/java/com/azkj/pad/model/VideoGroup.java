package com.azkj.pad.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/*监控用户组*/
public class VideoGroup implements Parcelable{
	// 组名
	private String name;
	// 组编号
	private String number;
	// 类型(1-组；2-普通用户)
	private int type;
	// 组成员
	private List<VideoUser> listUsers = new ArrayList<VideoUser>();
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<VideoUser> getListUsers() {
		return listUsers;
	}
	public void setListUsers(List<VideoUser> listUsers) {
		this.listUsers = listUsers;
	}
	
	public VideoGroup(){
		
	}
	public VideoGroup(String name, String number){
		this.name = name;
		this.number = number;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);
		dest.writeInt(type);
		dest.writeList(listUsers);
	}
	
	@Override
	public String toString() {
		return "VideoGroup [name=" + name + ", number=" + number + "]";
	}
	
	public static final Creator<VideoGroup> CREATOR = new Creator<VideoGroup>() {

		@SuppressWarnings("unchecked")
		@Override
		public VideoGroup createFromParcel(Parcel source) {
			VideoGroup grpInfo = new VideoGroup();
			grpInfo.setName(source.readString());
			grpInfo.setNumber(source.readString());
			grpInfo.setType(source.readInt());
			grpInfo.setListUsers(source.readArrayList(ArrayList.class.getClassLoader()));
			return grpInfo;
		}

		@Override
		public VideoGroup[] newArray(int size) {
			return null;
		}
	};
}
