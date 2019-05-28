package com.azkj.pad.model;

import java.util.Date;

/*通话记录*/
public class CallingRecords implements Cloneable {

	// 编号
	private Integer id;
	// 登录号码
	private String userNo;
	// 好友号码
	private String buddyNo;
	// 开始时间
	private Date startDate;
	// 通话开始
	private Date answerDate;
	// 结束时间
	private Date stopDate;
	// 通话时长(秒)
	private Integer duration;
	// 通话时长(显示)
	private String time;
	// 呼入呼出区分
	private Integer inOutFlg;
	// 呼叫状态
	private Integer callState;//0未接通1接通
	//呼叫标识
    private Integer sessId;
	// 好友姓名
	private String buddyNm;
	// 会议编号(通话及会议信息在一起添加)
	private String conferenceNo;
	// 会议名称(通话及会议信息在一起添加)
	private String conferenceNm;
	// 是否为监控(通话及会议信息在一起添加)
	private Integer isMonitor;
	// 会议类型(通话及会议信息在一起添加)
	private Integer conferenceType;
	// 通话类型(通话及会议信息在一起添加)
	private Integer mediaType;
	// 调度机里的会议唯一标识(通话及会议信息在一起添加)
	private String cID;
	private boolean ischeck;
	public boolean getIscheck() {
		return ischeck;
	}
	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getAnswerDate() {
		return answerDate;
	}
	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getInOutFlg() {
		return inOutFlg;
	}
	public void setInOutFlg(Integer inOutFlg) {
		this.inOutFlg = inOutFlg;
	}
	public Integer getCallState() {
		return callState;
	}
	public void setCallState(Integer callState) {
		this.callState = callState;
	}
	
	public String getBuddyNm() {
		return buddyNm;
	}
	public void setBuddyNm(String buddyNm) {
		this.buddyNm = buddyNm;
	}
	public String getConferenceNo() {
		return conferenceNo;
	}
	public void setConferenceNo(String conferenceNo) {
		this.conferenceNo = conferenceNo;
	}
	public String getConferenceNm() {
		return conferenceNm;
	}
	public void setConferenceNm(String conferenceNm) {
		this.conferenceNm = conferenceNm;
	}
	public Integer getIsMonitor() {
		return isMonitor;
	}
	public void setIsMonitor(Integer isMonitor) {
		this.isMonitor = isMonitor;
	}
	public Integer getConferenceType() {
		return conferenceType;
	}
	public void setConferenceType(Integer conferenceType) {
		this.conferenceType = conferenceType;
	}
	public Integer getMediaType() {
		return mediaType;
	}
	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}
	public String getcID() {
		return cID;
	}
	public void setcID(String cID) {
		this.cID = cID;
	}
	public Integer getSessId() {
		return sessId;
	}
	public void setSessId(Integer sessId) {
		this.sessId = sessId;
	}
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
