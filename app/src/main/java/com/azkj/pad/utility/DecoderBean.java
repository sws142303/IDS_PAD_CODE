package com.azkj.pad.utility;

/**
 * Created by ANGELCOMM on 2018/1/12.
 */

public class DecoderBean {

    private String sessionId;
    private String decoderId;
    private String chanidStrings;

    public DecoderBean(){

    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDecoderId() {
        return decoderId;
    }

    public void setDecoderId(String decoderId) {
        this.decoderId = decoderId;
    }

    public String getChanidStrings() {
        return chanidStrings;
    }

    public void setChanidStrings(String chanidStrings) {
        this.chanidStrings = chanidStrings;
    }
}
