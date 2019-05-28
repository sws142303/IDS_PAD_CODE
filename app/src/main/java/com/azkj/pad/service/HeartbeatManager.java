package com.azkj.pad.service;

import java.util.Date;

// 心跳机制处理(存最近两个心跳)
public class HeartbeatManager {
	private static HeartbeatManager instance = new HeartbeatManager();
	// 最近一个心跳
	private Date date1;
	// 最近心跳的上一个心跳
	private Date date2;

	public static HeartbeatManager getInstance() {
		if (instance == null) {
			instance = new HeartbeatManager();
		}
		return instance;
	}
	
	// 取得两个心跳时间间隔(秒)
	public int getDoubleBeatSpace(){
		if (date2 == null){
			return Integer.MAX_VALUE;
		}
		if (date1 == null){
			return Integer.MAX_VALUE;
		}
		return date1.getSeconds()-date2.getSeconds();
	}
	
	// 取得最后一次心跳时间
	public Date getLastDate() {
		return date1;
	}
	
	// 插入心跳数据
	public void putHeartbeat(Date date){
		if (date == null){
			return;
		}
		date2 = date1;
		date1 = date;
	}
}
