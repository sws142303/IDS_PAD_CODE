package com.azkj.pad.model;

/*登录用户信息*/
public class SipUser {
	// 用户名
	private String username;
	// 登录密码
	private String passwd;
	// 保存密码
	private boolean savepass;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public boolean isSavepass() {
		return savepass;
	}
	public void setSavepass(boolean savepass) {
		this.savepass = savepass;
	}
}
