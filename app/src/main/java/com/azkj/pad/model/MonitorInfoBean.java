package com.azkj.pad.model;

import android.view.SurfaceView;

/**
 * Created by ANGELCOMM on 2018/8/17.
 */

public class MonitorInfoBean {

    //会话id
    private String cid;
    //号码
    private String number;
    //是否为监控
    private boolean isMonitor;
    //SurfaceView
    private SurfaceView surfaceView;
    //名称
    private String name;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
