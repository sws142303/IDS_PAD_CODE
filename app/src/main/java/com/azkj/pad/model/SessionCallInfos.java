package com.azkj.pad.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ANGELCOMM on 2018/8/7.
 */

public class SessionCallInfos implements Serializable{

    private String cid;//会话ID
    private String callingnum;//号码
    private String callingname;//名称
    private String othernum;//对方号码
    private String othername;//对方名称
    private int direction;//呼叫方向：1呼出2呼入
    private int callState;//CallSate
    private int callType;//CallType
    private int level;//呼叫等级
    private int isVideo;//是否视频呼叫
    private String remark;//相关的操作取值有：协商转接,监听，代接，强插，强拆，转接，邀请成员
    //private List<MemberChangeInfo> memsList;

    /*public List<MemberChangeInfo> getMemsList() {
        return memsList;
    }

    public void setMemsList(List<MemberChangeInfo> memsList) {
        this.memsList = memsList;
    }*/

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCallingnum() {
        return callingnum;
    }

    public void setCallingnum(String callingnum) {
        this.callingnum = callingnum;
    }

    public String getCallingname() {
        return callingname;
    }

    public void setCallingname(String callingname) {
        this.callingname = callingname;
    }

    public String getOthernum() {
        return othernum;
    }

    public void setOthernum(String othernum) {
        this.othernum = othernum;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getCallState() {
        return callState;
    }

    public void setCallState(int callState) {
        this.callState = callState;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(int isVideo) {
        this.isVideo = isVideo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "SessionCallInfos{" +
                "cid='" + cid + '\'' +
                ", callingnum='" + callingnum + '\'' +
                ", callingname='" + callingname + '\'' +
                ", othernum='" + othernum + '\'' +
                ", othername='" + othername + '\'' +
                ", direction=" + direction +
                ", callState=" + callState +
                ", callType=" + callType +
                ", level=" + level +
                ", isVideo=" + isVideo +
                ", remark='" + remark + '\'' +
                '}';
    }
}
