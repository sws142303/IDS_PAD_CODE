package com.azkj.pad.model;

public class Contents {
	private String resolution;
	private int bitrate;
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public int getBitrate() {
		return bitrate;
	}
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}
	@Override
	public String toString() {
		return "Contents [resolution=" + resolution + ", bitrate=" + bitrate
				+ "]";
	}
	
	

}
