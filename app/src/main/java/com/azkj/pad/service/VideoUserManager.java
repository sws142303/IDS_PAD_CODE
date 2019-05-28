package com.azkj.pad.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.azkj.pad.model.VideoGroup;
import com.azkj.pad.model.VideoUser;
import com.azkj.pad.utility.GlobalConstant;

/*监控组管理器*/
public class VideoUserManager {
	private static final String LOG_TAG = "VideoUserManager";
	// 监控组列表
	private List<VideoGroup> lstGroupData = new ArrayList<VideoGroup>();
	// 监控组成员
	private Map<String, VideoUser[]> groupNumUsersMap;
	// 
	private boolean bDynamic = false;
	// 
	private static VideoUserManager instance = new VideoUserManager();

	public boolean isbDynamic() {
		return bDynamic;
	}

	public void setbDynamic(boolean bDynamic) {
		this.bDynamic = bDynamic;
	}
	
	public static VideoUserManager getInstance() {
		if (instance == null) {
			instance = new VideoUserManager();
		}
		return instance;
	}
	
	// 取得监控组数量
	public int getGroupCount(){
		return lstGroupData.size();
	}
	
	// 获取组列表
	public List<VideoGroup> getGroupData() {
		return lstGroupData;
	}
	
	// 获取组成员
	public Map<String, VideoUser[]> getGroupNumUsersMap() {
		return groupNumUsersMap;
	}

	private VideoUserManager() {
		groupNumUsersMap = new HashMap<String, VideoUser[]>();
	}
	
	// 添加组信息
	public void addGroups(VideoGroup[] videoGroups){
		lstGroupData.clear();
		for (int i = 0; i < videoGroups.length; i++) {
			String name = videoGroups[i].getName();
			String number = videoGroups[i].getNumber();
			Log.d(LOG_TAG, "add group " + name + ", num : " + number);
			lstGroupData.add(videoGroups[i]);
		}
	}
	
	// 获取组信息
	public VideoGroup getVideoGroup(String grpNum) {
		int size = lstGroupData.size();
		VideoGroup info = new VideoGroup();
		info.setNumber(grpNum);

		if (size == 0) {
			info.setName(grpNum);
			return info;
		}

		for (int i = 0; i < size; i++) {
			info = lstGroupData.get(i);
			if (grpNum.trim().equals(info.getNumber().trim())) {
				return info;
			}
		}
		return info;
	}
	
	// 添加接收到的组成员
	public void addReceivedUsers(String grpNumber, VideoUser[] users) {
		if (groupNumUsersMap.containsKey(grpNumber)) {
		}
		else {
			VideoUser[] tempUsers = new VideoUser[users.length];
			System.arraycopy(users, 0, tempUsers, 0, users.length);
			groupNumUsersMap.put(grpNumber, tempUsers);
		}
	}
	
	// 取得监控组成员
	public VideoUser[] getVideoUsers(String grpNumber){
		if (groupNumUsersMap.containsKey(grpNumber)) {
			return groupNumUsersMap.get(grpNumber);
		}
		else{
			return null;
		}
	}
	
	// 取得监控组成员
	public List<VideoUser> getVideoUsersSortedByState(String grpNumber){
		if (groupNumUsersMap.containsKey(grpNumber)) {
			if ((groupNumUsersMap.get(grpNumber) == null)
				|| (groupNumUsersMap.get(grpNumber).length <= 0)){
				return null;
			}
			List<VideoUser> onlineusers = new ArrayList<VideoUser>();
			List<VideoUser> takingusers = new ArrayList<VideoUser>();
			List<VideoUser> offlineusers = new ArrayList<VideoUser>();
			for(VideoUser memberinfo : groupNumUsersMap.get(grpNumber)){
				if ((memberinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_OUTGOING)
					|| (memberinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_RELEASE)){
					onlineusers.add(memberinfo);
				}
				if (memberinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_TAKING){
					takingusers.add(memberinfo);
				}
				if (memberinfo.getStatus() == GlobalConstant.MONITOR_MEMBER_OFFLINE){
					offlineusers.add(memberinfo);
				}
			}
			List<VideoUser> userinfos = new ArrayList<VideoUser>();
			userinfos.addAll(onlineusers);
			userinfos.addAll(takingusers);
			userinfos.addAll(offlineusers);
			return userinfos;
		}
		else{
			return null;
		}
	}
	
	// 检查监控组列表与组成员的对应关系
	public boolean checkReceiveAll() {
		List<VideoGroup> groupData = VideoUserManager.getInstance().getGroupData();

		if (groupData == null) {
			return true;
		}
		return groupNumUsersMap.size() == groupData.size();
	}
	
	// 清除监控组
	public void clear(){
		groupNumUsersMap.clear();
		lstGroupData.clear();
		bDynamic = false;
	}
	
	// 清除组成员
	public void clearNumMemberMap(){
		groupNumUsersMap.clear();
	}
}
