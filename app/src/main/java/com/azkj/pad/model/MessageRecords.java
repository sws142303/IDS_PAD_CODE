package com.azkj.pad.model;

import java.util.Date;

/*消息记录*/
public class MessageRecords implements Cloneable {
	// 编号
	private Integer id;
	//Name
	private String name;
	// 登录号码
	private String userNo;
	// 好友号码
	private String buddyNo;
	// 消息内容
	private String content;
	//消息类型
	private Integer contentType;
	//本地文件路径
	private String localFileUri;
	//服务器文件路径
	private String serverFileUri;
	// 消息长度
	private Integer length;
	// 发送接收区分
	private Integer inOutFlg;
	// 发送时间
	private Date sendDate;
	// 发送接收区分
	private Integer sendState;
	// 接收时间
	private Date receiveDate;
	// 阅读状态
	private Integer receiveState;
	//布局类型
	private Integer layout;
	//是否选中
	private boolean ischecked;
	//标识当前消息进度是否完成(0 未完成  1完成)
	private Integer progressState;

	public boolean getIschecked() {
		return ischecked;
	}
	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}
	public Integer getLayout() {
		return layout;
	}
	public void setLayout(Integer layout) {
		this.layout = layout;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getContentType() {
		return contentType;
	}
	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}
	public String getLocalFileUri() {
		return localFileUri;
	}
	public void setLocalFileUri(String localFileUri) {
		this.localFileUri = localFileUri;
	}
	public String getServerFileUri() {
		return serverFileUri;
	}
	public void setServerFileUri(String serverFileUri) {
		this.serverFileUri = serverFileUri;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getInOutFlg() {
		return inOutFlg;
	}
	public void setInOutFlg(Integer inOutFlg) {
		this.inOutFlg = inOutFlg;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public Integer getSendState() {
		return sendState;
	}
	public void setSendState(Integer sendState) {
		this.sendState = sendState;
	}
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public Integer getReceiveState() {
		return receiveState;
	}
	public void setReceiveState(Integer receiveState) {
		this.receiveState = receiveState;
	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getProgressState() {
		return progressState;
	}

	public void setProgressState(Integer progressState) {
		this.progressState = progressState;
	}
}
