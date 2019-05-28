package com.azkj.pad.service;

import android.util.Log;

import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.utility.GlobalConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*对讲组管理器*/
public class GroupManager {
	private static final String LOG_TAG = "GroupManager";
	// 对讲组列表
	//private List<GroupInfo> lstGroupData = new ArrayList<GroupInfo>();
	private List<GroupInfo> lstGroupData = Collections.synchronizedList(new LinkedList());//chw
	//全部组列表
	private List<GroupInfo> lstALLGroupData = Collections.synchronizedList(new LinkedList());//chw
	// 对讲组成员
	private Map<String, MemberInfo[]> groupNumMebersMap;
	//对讲组及非对讲组成员
	private Map<String, MemberInfo[]> groupAllNumMebersMap;
	// 
	private boolean bDynamic = false;
	// 
	private static GroupManager instance = new GroupManager();
	// 默认对讲组
	private GroupInfo optimumgroup;

	public boolean isbDynamic() {
		return bDynamic;
	}

	public void setbDynamic(boolean bDynamic) {
		this.bDynamic = bDynamic;
	}
	
	public static GroupManager getInstance() {
		if (instance == null) {
			instance = new GroupManager();
		}
		return instance;
	}
	
	public GroupInfo getOptimumgroup() {

		return optimumgroup;
	}

	public void setOptimumgroup(GroupInfo optimumgroup) {
		this.optimumgroup = optimumgroup;
	}

	// 取得对讲组数量
	public int getGroupCount(){
		return lstGroupData.size();
	}
	// 取得全部对讲组数量
	public int getALLGroupCount(){
		return lstALLGroupData.size();
	}
	// 获取组列表
	public List<GroupInfo> getGroupData() {
		return lstGroupData;
	}
	// 获取全部组列表
	public List<GroupInfo> getALLGroupData() {
		return lstALLGroupData;
	}
	// 获取成员列表
	public List<MemberInfo> getAllMemberInfoData(){
		List<MemberInfo> result = new ArrayList<MemberInfo>();
		for (GroupInfo group : lstGroupData){
			List<MemberInfo> members = getGroupMembersSortedByState(group.getNumber());
			if ((members != null)
				&& (members.size() > 0)){
				for (MemberInfo memberinfo : members){
					if (!result.contains(memberinfo)){
						result.add(memberinfo);
					}
				}
			}
		}
		return getGroupMembersSorted(result);
	}
	
	// 获取组成员
	public Map<String, MemberInfo[]> getGroupNumMebersMap() {
		return groupNumMebersMap;
	}
	
	private GroupManager() {
		//groupNumMebersMap = new HashMap<String, MemberInfo[]>();//chw
		groupNumMebersMap = Collections.synchronizedMap(new HashMap<String, MemberInfo[]>());
		groupAllNumMebersMap = Collections.synchronizedMap(new HashMap<String, MemberInfo[]>());
	}

	public void clearAllGroupMember(){
		if (groupAllNumMebersMap != null){
			groupAllNumMebersMap.clear();
		}
	}

	// 添加全部组信息（对讲组及非对讲组）
	public void addALLGroup(GroupInfo groupInfo){
		boolean exists = false;
		for(GroupInfo info : lstALLGroupData){
			if (info.getNumber().equals(groupInfo.getNumber())){
				exists = true;
				break;
			}
		}
		if (exists){
			return;
		}

		Log.d(LOG_TAG, "add group " + groupInfo.getName() + ", num : " + groupInfo.getNumber());
		lstALLGroupData.add(groupInfo);
	}

	public void clearALLGroup(){
		if (lstALLGroupData != null && lstALLGroupData.size() > 0){
			lstALLGroupData.clear();
		}
	}


	// 添加组信息
	public void addGroup(GroupInfo groupInfo){
		boolean exists = false;
		for(GroupInfo info : lstGroupData){
			if (info.getNumber().equals(groupInfo.getNumber())){
				exists = true;
				break;
			}
		}
		if (exists){
			return;
		}
		
		Log.d(LOG_TAG, "add group " + groupInfo.getName() + ", num : " + groupInfo.getNumber());
		lstGroupData.add(groupInfo);
	}
	
	// 添加组信息
	public void addGroups(GroupInfo[] groupInfos){

		//lstGroupData.clear();//chw 不清空集合
		ArrayList<GroupInfo> list = new ArrayList<GroupInfo>();
		for(GroupInfo groupInfo:lstGroupData){
			if(groupInfo.isTemporary() == true){
				list.add(groupInfo);
			}			
		}
		lstGroupData.clear();
		lstGroupData.addAll(list);
		for (int i = 0; i < groupInfos.length; i++) {
			String name = groupInfos[i].getName();
			String number = groupInfos[i].getNumber();
			Log.d(LOG_TAG, "add group " + name + ", num : " + number);
		    if(lstGroupData != null && !lstGroupData.contains(groupInfos[i])){		
					lstGroupData.add(groupInfos[i]);
		    }
		}
	}
	
	// 获取组信息
	public GroupInfo getGroupInfo(String grpNum) {
		int size = lstGroupData.size();
		GroupInfo info = new GroupInfo();
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
	
	// 移除对讲组
	public void removeGroupInfo(String grpNum) {
		GroupInfo result = null;
		for(GroupInfo groupinfo : lstGroupData){
			if ((groupinfo.getNumber().equals(grpNum))
					|| (("*9*" + grpNum).equals(groupinfo.getNumber()) ||("*9*" + groupinfo.getNumber()).equals(grpNum) ) ){
				result = groupinfo;
				break;
			}
		}
		if (result != null){
			lstGroupData.remove(result);
		}
		groupNumMebersMap.remove(grpNum);
		groupAllNumMebersMap.remove(grpNum);
	}
	
	// 判断是否为对讲组(包括临时对讲组)
	public boolean isGroupNumber(String grpNum){
		boolean result = false;
		for(GroupInfo groupinfo : lstGroupData){
			if (groupinfo.getNumber().equals(grpNum)
					|| (("*9*" + grpNum).equals(groupinfo.getNumber()) || ("*9*" + groupinfo.getNumber()).equals(grpNum))){
				result = true;
				break;
			}
		}
		return result;
	}

	// 添加接收到的组成员(对讲组及非对讲成员)
	public void addALLReceivedMembers(String grpNumber, MemberInfo[] members) {
		if (groupAllNumMebersMap.containsKey(grpNumber)) {
			MemberInfo[] tempMembers = new MemberInfo[members.length];
			System.arraycopy(members, 0, tempMembers, 0, members.length);
			groupAllNumMebersMap.put(grpNumber, tempMembers);
		}
		else {
			MemberInfo[] tempMembers = new MemberInfo[members.length];
			System.arraycopy(members, 0, tempMembers, 0, members.length);
			groupAllNumMebersMap.put(grpNumber, tempMembers);
		}
	}

	// 添加接收到的组成员
	public void addReceivedMembers(String grpNumber, MemberInfo[] members) {
		if (groupNumMebersMap.containsKey(grpNumber)) {
			MemberInfo[] tempMembers = new MemberInfo[members.length];
			System.arraycopy(members, 0, tempMembers, 0, members.length);
			groupNumMebersMap.put(grpNumber, tempMembers);
		}
		else {
			MemberInfo[] tempMembers = new MemberInfo[members.length];
			System.arraycopy(members, 0, tempMembers, 0, members.length);
			groupNumMebersMap.put(grpNumber, tempMembers);
		}
	}
	
	// 取得对讲组成员
	public MemberInfo[] getGroupMembers(String grpNumber){
		if (groupNumMebersMap.containsKey(grpNumber)) {
			return groupNumMebersMap.get(grpNumber);
		}
		else{
			return null;
		}
	}
	
	// 取得对讲组成员
	public List<MemberInfo> getGroupMembersSortedByState(String grpNumber){
		if (groupNumMebersMap.containsKey(grpNumber)) {
			if ((groupNumMebersMap.get(grpNumber) == null)
				|| (groupNumMebersMap.get(grpNumber).length <= 0)){
				return null;
			}
			return getGroupMembersSorted(Arrays.asList(groupNumMebersMap.get(grpNumber)));
		}
		else if(groupNumMebersMap.containsKey("*9*" + grpNumber)){
			if ((groupNumMebersMap.get("*9*" + grpNumber) == null)
					|| (groupNumMebersMap.get("*9*" + grpNumber).length <= 0)){
					return null;
				}
				return getGroupMembersSorted(Arrays.asList(groupNumMebersMap.get("*9*" + grpNumber)));
		}
		else{
			return null;
		}
	}

	//取得对讲组及非对讲组成员
	public MemberInfo[] getALLGroupMemberSortedByState(String grpNumber){
		if (groupAllNumMebersMap.containsKey(grpNumber)) {
			return groupAllNumMebersMap.get(grpNumber);
		}
		else{
			return null;
		}
	}
	
	// 组成员排序
	private List<MemberInfo> getGroupMembersSorted(List<MemberInfo> memberinfos){
		/*List<MemberInfo> onlinemembers = new ArrayList<MemberInfo>();
		List<MemberInfo> speakingmembers = new ArrayList<MemberInfo>();
		List<MemberInfo> listeningmembers = new ArrayList<MemberInfo>();
		List<MemberInfo> offlinemembers = new ArrayList<MemberInfo>();
		for(MemberInfo memberinfo : memberinfos){
			if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_ONLINE){
				onlinemembers.add(memberinfo);
			}
			if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_SPEAKING){
				speakingmembers.add(memberinfo);
			}
			if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_LISTENING){
				listeningmembers.add(memberinfo);
			}
			if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_OFFLINE){
				offlinemembers.add(memberinfo);
			}
		}
		List<MemberInfo> result = new ArrayList<MemberInfo>();
		result.addAll(onlinemembers);
		result.addAll(speakingmembers);
		result.addAll(listeningmembers);
		result.addAll(offlinemembers);*/
		List<MemberInfo> offlinemembers = new ArrayList<MemberInfo>();
		List<MemberInfo> listeningmembers = new ArrayList<MemberInfo>();
		List<MemberInfo> speakingmembers = new ArrayList<MemberInfo>();
		List<MemberInfo> onlinemembers = new ArrayList<MemberInfo>();
		List<MemberInfo> busymembers = new ArrayList<MemberInfo>();
		for(MemberInfo memberinfo : memberinfos){
			if ((memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_EMPTY)
				|| (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_INIT)){
				// 离线
				offlinemembers.add(memberinfo);
			}
			else if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_LISTENING){
				// 听讲
				listeningmembers.add(memberinfo);
			}
			else if (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_SPEAKING){
				// 发言
				speakingmembers.add(memberinfo);
			}
			else if ((memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_ONLINE)
					|| (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_RELEASE)){
				// 空闲
				onlinemembers.add(memberinfo);
			}
			else {
				// 用户忙
				busymembers.add(memberinfo);
			}
		}
		List<MemberInfo> result = new ArrayList<MemberInfo>();
		result.addAll(onlinemembers);
		result.addAll(busymembers);
		result.addAll(listeningmembers);
		result.addAll(speakingmembers);
		result.addAll(offlinemembers);
		return result;
	}
	
	// 更新组成员状态
	public void updateGroupMembersState(List<MemberInfo> memberinfos){
		for(MemberInfo[] infos : groupNumMebersMap.values()){
			for (MemberInfo info : infos){
				for (MemberInfo state : infos){
					if (info.getNumber().equals(state.getNumber())){
						info.setStatus(state.getStatus());
						break;
					}
				}
			}
		}
	}
	
	// 取得组内成员信息
	public MemberInfo getMemberInfo(String grpNumber, String memNumber){
		MemberInfo[] memberinfos =  getGroupMembers(grpNumber);
		if (memberinfos == null){
			return null;
		}
		for(MemberInfo memberinfo : memberinfos){
			if (memberinfo.getNumber().equals(memNumber)){
				return memberinfo;
			}
		}
		return null;
	}

	// 取得组内成员信息
	public MemberInfo getMemberInfo(String memNumber){
		for(MemberInfo[] infos : groupNumMebersMap.values()){
			for (MemberInfo info : infos){
				if (info.getNumber().equals(memNumber)){
					return info;
				}
			}
		}
		return null;
	}
	
	// 依名字取得组内成员信息
			public MemberInfo getMemberInfoByName(String memName){
				for(MemberInfo[] infos : groupNumMebersMap.values()){
					for (MemberInfo info : infos){
						if (info.getName().equals(memName)){
							return info;
						}
					}
				}
				return null;
			}
	
	// 检查对讲组列表与组成员的对应关系
	public boolean checkReceiveAll() {
		List<GroupInfo> groupData = GroupManager.getInstance().getGroupData();

		if (groupData == null) {
			return true;
		}
		return groupNumMebersMap.size() == groupData.size();
	}
	
	// 清除对讲组
	public void clear(){
		groupAllNumMebersMap.clear();
		groupNumMebersMap.clear();
		lstGroupData.clear();
		bDynamic = false;
	}
	
	// 清除组成员
	public void clearNumMemberMap(){
		groupNumMebersMap.clear();
	}
	//更新组内成员是否在线状态
	public void updateGroupMemberState(String employeeid,int state)
	{
		if(groupNumMebersMap != null && groupNumMebersMap.size() > 0)
		{
			for(MemberInfo[] mInfos : groupNumMebersMap.values())
			{
				if(mInfos != null && mInfos.length > 0)
				{
					for(MemberInfo mi : mInfos)
					{
						if(mi.getNumber().equals(employeeid))
						{
							mi.setStatus(state);
							break;
						}
					}
				}
			}
		}
	}
	
	//改变组内所有成员的状态下线
		public void updateGroupMembersStateOffline()
		{
			if(groupNumMebersMap != null && groupNumMebersMap.size() > 0)
			{
				for(MemberInfo[] mInfos : groupNumMebersMap.values())
				{
					for(MemberInfo mi : mInfos)
					{
						mi.setStatus(0);
					}
				}
			}
		}

}
