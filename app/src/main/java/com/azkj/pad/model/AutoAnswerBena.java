package com.azkj.pad.model;

/**
 * Created by ANGELCOMM on 2018/4/20.
 */

//是否自动接听
 public class AutoAnswerBena {
    //通话ID
    private int callId = 0;
    //是否自动接听
    private int isAutoAnswer = 0;

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public int getIsAutoAnswer() {
        return isAutoAnswer;
    }

    public void setIsAutoAnswer(int isAutoAnswer) {
        this.isAutoAnswer = isAutoAnswer;
    }
}
