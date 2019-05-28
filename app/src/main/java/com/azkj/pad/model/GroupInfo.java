package com.azkj.pad.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.juphoon.lemon.MtcCliConstants;

import java.util.ArrayList;
import java.util.List;

/*组信息*/
public class GroupInfo implements Parcelable, Cloneable{
	// 组名
	private String name;
	// 组编号
	private String number;
	// 组级别
	private int level;
	// 是否临时对讲组
	private boolean isTemporary = false;
	// 是否可见
	private boolean visible = false;
	// 组成员
	private List<MemberInfo> listMembers = new ArrayList<MemberInfo>();
	// 回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isTemporary() {
		return isTemporary;
	}
	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public int getOnlineCount() {
		int count = 0;
		for(MemberInfo memberinfo : this.listMembers){
			if (memberinfo.getStatus() > 1){
				count++;
			}
		}
		return count;
	}
	public int getMemberCount() {
		return this.listMembers.size();
	}
	public List<MemberInfo> getListMembers() {
		return listMembers;
	}
	public void setListMembers(List<MemberInfo> listMembers) {
		this.listMembers = listMembers;
	}
	public int getDwSessId() {
		return dwSessId;
	}
	public void setDwSessId(int dwSessId) {
		this.dwSessId = dwSessId;
	}
	public GroupInfo(){
		
	}
	public GroupInfo(String name, String number, int level){
		this.name = name;
		this.number = number;
		this.level = level;
	}
	public GroupInfo(String name, String number, String level) {
		this.name = name;
		this.number = number;
		if ((level == null)
			|| (level.trim().length() <= 0)){
			this.level = 100;
		}
		else {
			this.level = Integer.parseInt(level);
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(number);
		dest.writeInt(level);
		dest.writeList(listMembers);
		dest.writeInt(dwSessId);
	}
	
	@Override
	public String toString() {
		if (number.equals(name)){
			return name;
		}
		else {
			return name + "(" + number + ")";
		}
	}
	
	public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {

		@SuppressWarnings("unchecked")
		@Override
		public GroupInfo createFromParcel(Parcel source) {
			GroupInfo grpInfo = new GroupInfo();
			grpInfo.setName(source.readString());
			grpInfo.setNumber(source.readString());
			grpInfo.setLevel(source.readInt());
			grpInfo.setListMembers(source.readArrayList(ArrayList.class.getClassLoader()));
			grpInfo.setDwSessId(source.readInt());
			return grpInfo;
		}

		@Override
		public GroupInfo[] newArray(int size) {
			return null;
		}
	};
	
	@Override
    public Object clone() throws CloneNotSupportedException {
		GroupInfo groupinfo = new GroupInfo();
		groupinfo.setName(this.getName());
		groupinfo.setNumber(this.getNumber());
		groupinfo.setLevel(this.getLevel());
		groupinfo.setTemporary(this.isTemporary());
		if (this.getListMembers() != null){
			List<MemberInfo> members = new ArrayList<MemberInfo>();
			for(MemberInfo memberinfo : this.getListMembers()){
				members.add((MemberInfo)memberinfo.clone());
			}
			groupinfo.setListMembers(members);
		}
        return groupinfo;
        /*return super.clone();*/
    }
}
