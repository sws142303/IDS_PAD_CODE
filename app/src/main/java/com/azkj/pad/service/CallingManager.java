package com.azkj.pad.service;

import android.util.Log;

import com.azkj.pad.activity.WaitingDialog;
import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.utility.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

// 通话管理器
public class CallingManager {
	private static final String LOG_TAG = "CallingManager";
	// 通话列表
	private List<CallingInfo> lstCallingData = new ArrayList<CallingInfo>();
    // 通知消息
    private List<WaitingDialog> infoList = new ArrayList<WaitingDialog>();
    // 接听列表
    private List<CallingInfo> lstAnswerData = new ArrayList<CallingInfo>();
	
	private boolean bDynamic = false;
	private static CallingManager instance = new CallingManager();

	public boolean isbDynamic() {
		return bDynamic;
	}

	public void setbDynamic(boolean bDynamic) {
		this.bDynamic = bDynamic;
	}
	
	public static CallingManager getInstance() {
		if (instance == null) {
			instance = new CallingManager();
		}
		return instance;
	}
	
	public boolean isIsvideoing() {
		boolean isvideoing = false;
		for (CallingInfo callingInfo : lstCallingData){
			if (callingInfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO){
				isvideoing = true;
				break;
			}
		}
		return isvideoing;
	}

	// 取得呼叫数量
	public int getCallingCount(){
		return lstCallingData.size();
	}
	
	// 获取通话列表
	public List<CallingInfo> getCallingData() {
		return lstCallingData;
	}
	
	// 获取通知列表
	public List<WaitingDialog> getCallingDialog() {
		return infoList;
	}
	
	// 获取接听列表
	public List<CallingInfo> getAnswerData(){
		return lstAnswerData;
	}
	
	// 添加通话信息
	public void addCallingInfo(CallingInfo callingInfo){
		boolean exists = false;
		for(CallingInfo info : lstCallingData){
			if (info.getDwSessId() == callingInfo.getDwSessId()){
				exists = true;
				break;
			}
		}
		if (exists){
			return;
		}
		
		Log.d(LOG_TAG, "add CallingInfo " + callingInfo.getRemoteName() + ", num : " + callingInfo.getDwSessId());
		lstCallingData.add(callingInfo);
	}
	
	// 添加通知消息
	public void addCallingDialog(WaitingDialog callingInfo){
		boolean exists = false;
		for(WaitingDialog info : infoList){
			if (info.getDwSessId() == callingInfo.getDwSessId()){
				exists = true;
				break;
			}
		}
		if (exists){
			return;
		}
		
		Log.d(LOG_TAG, "add WaitingDialog num : " + callingInfo.getDwSessId());
		infoList.add(callingInfo);
	}
	
	// 添加接听信息
	public void addAnswerInfo(CallingInfo callingInfo){
		boolean exists = false;
		for(CallingInfo info : lstAnswerData){
			if (info.getDwSessId() == callingInfo.getDwSessId()){
				exists = true;
				break;
			}
		}
		if (exists){
			return;
		}
		
		Log.d(LOG_TAG, "add AnswerInfo " + callingInfo.getRemoteName() + ", num : " + callingInfo.getDwSessId());
		lstAnswerData.add(callingInfo);
	}
	
	// 获取通话信息
	public CallingInfo getCallingInfo(int dwSessId) {
		CallingInfo result = null;
		for (CallingInfo callingInfo : lstCallingData){
			if (callingInfo.getDwSessId() == dwSessId){
				result = callingInfo;
				break;
			}
		}
		return result;
	}
	
	// 获取通知信息
	public WaitingDialog getCallingDialog(int dwSessId) {
		WaitingDialog result = null;
		for(WaitingDialog info : infoList){
			if (info.getDwSessId() == dwSessId){
				result = info;
				break;
			}
		}
		return result;
	}
	
	// 获取通话信息
	public CallingInfo getAnswerInfo(int dwSessId) {
		CallingInfo result = null;
		for (CallingInfo callingInfo : lstAnswerData){
			if (callingInfo.getDwSessId() == dwSessId){
				result = callingInfo;
				break;
			}
		}
		return result;
	}
	
	// 获取通话信息
	public CallingInfo getCallingInfoByType(int calltype) {
		CallingInfo result = null;
		for (CallingInfo callingInfo : lstCallingData){
			if (callingInfo.getCalltype() == calltype){
				result = callingInfo;
				break;
			}
		}
		return result;
	}
	
	// 获取通话信息(是否保持中)
	public CallingInfo getCallingInfo(int calltype, boolean isHolding) {
		CallingInfo result = null;
		for (CallingInfo callingInfo : lstCallingData){
			if ((callingInfo.getCalltype() == calltype)
				&& (callingInfo.isHolding() == isHolding)){
				result = callingInfo;
				break;
			}
		}
		return result;
	}
	
	// 更新通话信息状态
	public void updateCallingAlright(int dwSessId, boolean isalright) {
		for (CallingInfo callingInfo : lstCallingData){
			if (callingInfo.getDwSessId() == dwSessId){
				callingInfo.setIsalright(isalright);
				break;
			}
		}
	}
	
	// 更新通话信息状态
	public void updateCallingHolding(int dwSessId, boolean isHolding) {
		for (CallingInfo callingInfo : lstCallingData){
			if (callingInfo.getDwSessId() == dwSessId){
				callingInfo.setHolding(isHolding);
				break;
			}
		}
	}
	
	// 移除通话信息
	public void removeCallingInfo(int dwSessId){
		CallingInfo callingInfo = null;
		for(CallingInfo info : lstCallingData){
			if (info.getDwSessId() == dwSessId){
				callingInfo = info;
				break;
			}
		}
		if (callingInfo == null){
			return;
		}
		
		Log.e(LOG_TAG, "remove CallingInfo " + callingInfo.getRemoteName() + ", num : " + callingInfo.getDwSessId());
		lstCallingData.remove(callingInfo);
	}
	
	// 移除提示信息
	public void removeCallingDialog(int dwSessId){
		WaitingDialog callingInfo = null;
		for(WaitingDialog info : infoList){
			if (info.getDwSessId() == dwSessId){
				callingInfo = info;
				break;
			}
		}
		if (callingInfo == null){
			return;
		}
		
		Log.d(LOG_TAG, "remove CustomDialog num : " + callingInfo.getDwSessId());
		infoList.remove(callingInfo);
	}
	
	// 移除通话信息
	public void removeAnswerInfo(int dwSessId){
		CallingInfo callingInfo = null;
		for(CallingInfo info : lstAnswerData){
			if (info.getDwSessId() == dwSessId){
				callingInfo = info;
				break;
			}
		}
		if (callingInfo == null){
			return;
		}
		
		Log.d(LOG_TAG, "remove AnswerInfo " + callingInfo.getRemoteName() + ", num : " + callingInfo.getDwSessId());
		lstAnswerData.remove(callingInfo);
	}
	
	// 清除通话信息
	public void clear(){
		lstCallingData.clear();
		infoList.clear();
		lstAnswerData.clear();
		bDynamic = false;
	}
}
