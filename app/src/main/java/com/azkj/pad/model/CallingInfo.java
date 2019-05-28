package com.azkj.pad.model;

import com.azkj.pad.utility.GlobalConstant;
import com.juphoon.lemon.MtcCliConstants;
import com.juphoon.lemon.ui.MtcDelegate;

import cn.sword.SDK.MediaEngine;

/*通话信息*/
public class CallingInfo {
	// 回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	// 对方号码
	private String remoteName;
	// 呼叫类型
	private int calltype = GlobalConstant.CALL_TYPE_CALLING;
	// 通话类型(语音或视频)
	private int mediaType = GlobalConstant.CONFERENCE_MEDIATYPE_VOICE;
	// 接听类型(语音或视频)
	private int answerType = GlobalConstant.CONFERENCE_MEDIATYPE_VOICE;
	// 是否为拨出(默认为拨入)
	private boolean isOutgoing = false;
	// 是否正在保持(默认为正常)
	private boolean isHolding = false;
	// 通话是否正常(默认为不正常，即通话没有成功，主要用于对讲、会议、监控创建会议成功，但是没有回呼的情况)
	private boolean isalright = false;
	// 是否为临时对讲
	private boolean istempintercom = false;
	//是否为视频
	private boolean isVideo = false;
	//呼叫状态
	private int state;

	private MediaEngine.ME_IdsPara me_idsPara;

	public int getDwSessId() {
		return dwSessId;
	}
	public void setDwSessId(int dwSessId) {
		this.dwSessId = dwSessId;
	}
	public String getRemoteName() {
		return remoteName;
	}
	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}
	public int getCalltype() {
		return calltype;
	}
	public void setCalltype(int calltype) {
		this.calltype = calltype;
	}
	public int getMediaType() {
		return mediaType;
	}
	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}
	public int getAnswerType() {
		return answerType;
	}
	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}
	public boolean isOutgoing() {
		return isOutgoing;
	}
	public void setOutgoing(boolean isOutgoing) {
		this.isOutgoing = isOutgoing;
	}
	public boolean isHolding() {
		return isHolding;
	}
	public void setHolding(boolean isHolding) {
		MtcDelegate.log("lizhiwei 保持通话"+dwSessId);
		this.isHolding = isHolding;
	}
	public boolean isIsalright() {
		return isalright;
	}
	public void setIsalright(boolean isalright) {
		this.isalright = isalright;
	}
	public boolean isIstempintercom() {
		return istempintercom;
	}
	public void setIstempintercom(boolean istempintercom) {
		this.istempintercom = istempintercom;
	}



	public CallingInfo(){}
	public CallingInfo(int dwSessId){
		this.dwSessId = dwSessId;
	}
	public CallingInfo(int dwSessId, boolean isOutgoing){
		this.dwSessId = dwSessId;
		this.isOutgoing = isOutgoing;
	}
	public CallingInfo(int dwSessId, String remoteName, int calltype, int mediaType, boolean isOutgoing, boolean isHolding, boolean isalright){
		this.dwSessId = dwSessId;
		this.remoteName = remoteName;
		this.calltype = calltype;
		this.isOutgoing = isOutgoing;
		this.isHolding = isHolding;
		this.isalright = isalright;
	}
	public CallingInfo(int dwSessId, String remoteName, int calltype, int mediaType, boolean isOutgoing, boolean isHolding, boolean isalright, boolean istempintercom){
		this.dwSessId = dwSessId;
		this.remoteName = remoteName;
		this.calltype = calltype;
		this.isOutgoing = isOutgoing;
		this.isHolding = isHolding;
		this.isalright = isalright;
		this.istempintercom = istempintercom;
	}

	public boolean isVideo() {
		return isVideo;
	}

	public void setVideo(boolean video) {
		isVideo = video;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public MediaEngine.ME_IdsPara getMe_idsPara() {
		return me_idsPara;
	}

	public void setMe_idsPara(MediaEngine.ME_IdsPara me_idsPara) {
		this.me_idsPara = me_idsPara;
	}
}
