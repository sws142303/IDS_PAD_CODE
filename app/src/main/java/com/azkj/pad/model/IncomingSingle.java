package com.azkj.pad.model;

import cn.sword.SDK.MediaEngine;

/**
 * Created by ANGELCOMM on 2018/8/6.
 */
//单呼实体类
public class IncomingSingle {
    //会话id
    private int callId;
    //主叫号码
    private String number;
    //呼叫状态
    private int state;
    //来电是否为视频呼叫
    private boolean isVideo;

    private MediaEngine.ME_IdsPara me_idsPara;


    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public MediaEngine.ME_IdsPara getMe_idsPara() {
        return me_idsPara;
    }

    public void setMe_idsPara(MediaEngine.ME_IdsPara me_idsPara) {
        this.me_idsPara = me_idsPara;
    }
}
