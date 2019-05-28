package com.azkj.pad.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.juphoon.lemon.MtcCliConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*视频会议*/
@SuppressLint("SimpleDateFormat")
public class MeetingRecords implements Parcelable, Cloneable {
	private static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
	// 编号
	private Integer id;
	// 登录号码
	private String userNo;
	// 开始时间
	private Date startDate;
	// 消息标识
	private String sID;
	// 会议编号
	private String conferenceNo;
	// 会议名称
	private String conferenceNm;
	// 是否为监控
	private Integer isMonitor;
	// 会议类型
	private Integer conferenceType;
	// 通话类型
	private Integer mediaType;
	// 调度机里的会议唯一标识
	private String cID;
	// 结束时间
	private Date stopDate;
	// 通话时长(秒)
	private Integer duration;
	// 通话时长(显示)
	private String time;
	// 会议成员
	private static List<MeetingMembers> meetingMembers ;
	// 回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getsID() {
		return sID;
	}
	public void setsID(String sID) {
		this.sID = sID;
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
	public List<MeetingMembers> getMeetingMembers() {
		return meetingMembers;
	}
	public void setMeetingMembers(List<MeetingMembers> meetingMembers) {
		this.meetingMembers = meetingMembers;
	}
	public int getDwSessId() {
		return dwSessId;
	}
	public void setDwSessId(int dwSessId) {
		this.dwSessId = dwSessId;
	}
	@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(userNo);
		dest.writeString((startDate == null)?"":dateFormatter.format(startDate));
		dest.writeString(sID);
		dest.writeString(conferenceNo);
		dest.writeString(conferenceNm);
		dest.writeInt(isMonitor);
		dest.writeInt(conferenceType);
		dest.writeInt(mediaType);
		dest.writeString(cID);
		dest.writeString((stopDate == null)?"":dateFormatter.format(stopDate));
		dest.writeInt(duration);
		dest.writeString(time);
		dest.writeInt(dwSessId);
	}

	public static final Creator<MeetingRecords> CREATOR = new Creator<MeetingRecords>() {

		@Override
		public MeetingRecords createFromParcel(Parcel source) {
			MeetingRecords record = new MeetingRecords();
			record.setId(source.readInt());
			record.setUserNo(source.readString());
			String startDate = source.readString();
			if ((startDate != null)
				&& (startDate.length() > 0)){
				try {
					record.setStartDate(dateFormatter.parse(startDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			record.setsID(source.readString());
			record.setConferenceNo(source.readString());
			record.setConferenceNm(source.readString());
			record.setIsMonitor(source.readInt());
			record.setConferenceType(source.readInt());
			record.setMediaType(source.readInt());
			record.setcID(source.readString());
			String stopDate = source.readString();
			if ((stopDate != null)
				&& (stopDate.length() > 0)){
				try {
					record.setStopDate(dateFormatter.parse(stopDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			record.setDuration(source.readInt());
			record.setTime(source.readString());
			record.setDwSessId(source.readInt());
			return record;
		}

		@Override
		public MeetingRecords[] newArray(int size) {
			return null;
		}
	};
}
