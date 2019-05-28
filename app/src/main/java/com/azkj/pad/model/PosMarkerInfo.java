package com.azkj.pad.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PosMarkerInfo implements Serializable,Comparable{	
	/**
	 * 地图覆盖物标记类
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private double latitude;
	private double longitude;
	private String time;
	private int type;
	
	
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	@Override
	public int compareTo(Object another)
	{
		PosMarkerInfo posMarkerInfo = (PosMarkerInfo)another;
		String timePos = posMarkerInfo.getTime();
		return this.time.compareTo(timePos);
	}

	
	
}
