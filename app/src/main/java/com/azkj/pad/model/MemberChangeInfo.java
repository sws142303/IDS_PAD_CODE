package com.azkj.pad.model;

import java.io.Serializable;

/**
 * Created by ANGELCOMM on 2018/8/7.
 */

public class MemberChangeInfo implements Serializable {

    private String cid;//会话ID
    private String employeeid;//号码
    private String name;//名称
    private int callState;//CallState
    private int notspeak;//0可讲话，1不可讲话
    private int nothear;//0可听，1不可听
    private int havevideo;//0可看视频，1不可看
    private int ispush;//0未推送，1被推送
    private int employeeType;//号码类型见枚举消息类型EmployeeType
    private String groupNo;//如果是对讲组类型，显示组号
    private String groupName;//如果是对讲组类型，显示组名

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCallState() {
        return callState;
    }

    public void setCallState(int callState) {
        this.callState = callState;
    }

    public int getNotspeak() {
        return notspeak;
    }

    public void setNotspeak(int notspeak) {
        this.notspeak = notspeak;
    }

    public int getNothear() {
        return nothear;
    }

    public void setNothear(int nothear) {
        this.nothear = nothear;
    }

    public int getHavevideo() {
        return havevideo;
    }

    public void setHavevideo(int havevideo) {
        this.havevideo = havevideo;
    }

    public int getIspush() {
        return ispush;
    }

    public void setIspush(int ispush) {
        this.ispush = ispush;
    }

    public int getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(int employeeType) {
        this.employeeType = employeeType;
    }

    @Override
    public String toString() {
        return "MemberChangeInfo{" +
                "cid='" + cid + '\'' +
                ", employeeid='" + employeeid + '\'' +
                ", name='" + name + '\'' +
                ", callState=" + callState +
                ", notspeak=" + notspeak +
                ", nothear=" + nothear +
                ", havevideo=" + havevideo +
                ", ispush=" + ispush +
                ", employeeType=" + employeeType +
                ", groupNo='" + groupNo + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }

}
