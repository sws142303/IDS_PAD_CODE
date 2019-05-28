package com.azkj.pad.model;

public class DecoderInfo {
	private String id;
	
	private String videoip;

    private String videoport;

    private String user;

    private String Password;

    private String showname;

    public int thetype;
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVideoip() {
		return videoip;
	}

	public void setVideoip(String videoip) {
		this.videoip = videoip;
	}

	public String getVideoport() {
		return videoport;
	}

	public void setVideoport(String videoport) {
		this.videoport = videoport;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getShowname() {
		return showname;
	}

	public void setShowname(String showname) {
		this.showname = showname;
	}

	public int getThetype() {
		return thetype;
	}

	public void setThetype(int thetype) {
		this.thetype = thetype;
	}

	


}
