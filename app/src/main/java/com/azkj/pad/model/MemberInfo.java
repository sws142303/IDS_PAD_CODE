package com.azkj.pad.model;

import android.os.Parcel;
import android.os.Parcelable;

/*对讲组成员信息*/
public class MemberInfo implements Parcelable, Comparable<MemberInfo>, Cloneable{
	// 成员名称
	private String name;
	// 成员编号
	private String number;
	// 成员状态
	private int status;
	// 在会议中
	private boolean inmeeting = false;
	//是否定位
	private boolean isPos=false;
	// 与会成员状态
	private int meetingState = 0;
	// 成员类型
	private int memberType = 0;
	//多级前缀
	private String prefix = null;

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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isInmeeting() {
		return inmeeting;
	}
	public void setInmeeting(boolean inmeeting) {
		this.inmeeting = inmeeting;
	}
	public boolean isPos() {
		return isPos;
	}
	public void setPos(boolean ispos) {
		this.isPos = ispos;
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
	
	public MemberInfo(){
		
	}
	public MemberInfo(String name, String number, int status){
		this.name = name;
		this.number = number;
		this.status = status;
	}
	public MemberInfo(String name, String number, String status){
		this.name = name;
		this.number = number;
		if ((status != null)
			&& (status.trim().length() > 0)){
			this.status = Integer.parseInt(status.trim());
		}
	}
	
	@Override
	public int compareTo(MemberInfo another) {
		return another.status - this.status;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(status);
		dest.writeString(name);
		dest.writeString(number);
	}
	
	@Override
	public String toString() {
		return "MemberInfo [status=" + status + ", name=" + name + ", number=" + number + "]";
	}
	
	public static final Creator<MemberInfo> CREATOR = new Creator<MemberInfo>() {

		@Override
		public MemberInfo createFromParcel(Parcel source) {
			MemberInfo memberInfo = new MemberInfo();
			memberInfo.setStatus(source.readInt());
			memberInfo.setName(source.readString());
			memberInfo.setNumber(source.readString());
			return memberInfo;
		}

		@Override
		public MemberInfo[] newArray(int size) {
			return null;
		}
	};
	
	@Override
    public Object clone() throws CloneNotSupportedException {
		MemberInfo memberinfo = new MemberInfo();
		memberinfo.setName(this.getName());
		memberinfo.setNumber(this.getNumber());
		memberinfo.setStatus(this.getStatus());
		memberinfo.setInmeeting(this.isInmeeting());
		memberinfo.setMeetingState(this.getMeetingState());
		memberinfo.setMemberType(this.getMemberType());
        return memberinfo;
        /*return super.clone();*/
    }

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
