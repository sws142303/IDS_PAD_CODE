package com.azkj.pad.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/*会议成员*/
@SuppressLint("SimpleDateFormat")
public class MeetingMembers implements Parcelable, Cloneable {
	private static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

	// 编号
	private Integer id;
	// 会议编号
	private Integer meetingId;
	// 好友号码
	private String buddyNo;
	// 入会时间
	private Date startDate;
	// 应答模式
	private Integer answerType;
	// 成员类型
	private Integer memberType;
	// 离会时间
	private Date stopDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
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
	public Integer getAnswerType() {
		return answerType;
	}
	public void setAnswerType(Integer answerType) {
		this.answerType = answerType;
	}
	public Integer getMemberType() {
		return memberType;
	}
	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
	
	public MeetingMembers(){
		
	}
	public MeetingMembers(String buddyNo, Date startDate, Date stopDate){
		this.buddyNo = buddyNo;
		this.startDate = startDate;
	}
	public MeetingMembers(Integer meetingId, String buddyNo, Date startDate, Integer answerType, Date stopDate){
		this.meetingId = meetingId;
		this.buddyNo = buddyNo;
		this.startDate = startDate;
		this.answerType = answerType;
		this.stopDate = stopDate;
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
		dest.writeInt(meetingId);
		dest.writeString(buddyNo);
		dest.writeString((startDate == null)?"":dateFormatter.format(startDate));
		dest.writeInt(answerType);
		dest.writeInt(memberType);
		dest.writeString((stopDate == null)?"":dateFormatter.format(stopDate));
	}

	public static final Creator<MeetingMembers> CREATOR = new Creator<MeetingMembers>() {

		@Override
		public MeetingMembers createFromParcel(Parcel source) {
			MeetingMembers member = new MeetingMembers();
			member.setId(source.readInt());
			member.setMeetingId(source.readInt());
			member.setBuddyNo(source.readString());
			String startDate = source.readString();
			if ((startDate != null)
				&& (startDate.length() > 0)){
				try {
					member.setStartDate(dateFormatter.parse(startDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			member.setAnswerType(source.readInt());
			member.setMemberType(source.readInt());
			String stopDate = source.readString();
			if ((stopDate != null)
				&& (stopDate.length() > 0)){
				try {
					member.setStopDate(dateFormatter.parse(stopDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return member;
		}

		@Override
		public MeetingMembers[] newArray(int size) {
			return null;
		}
	};
}
