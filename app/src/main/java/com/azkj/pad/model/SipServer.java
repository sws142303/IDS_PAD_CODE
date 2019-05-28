package com.azkj.pad.model;

/*服务器信息*/
public class SipServer {
	
	// 服务器地址
	private String serverIp;
	//授权服务器地址
//	private String accreditServerip;
	// 服务器端口
	private String port;
	
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	/*public String getAccreditServerip() {
		return accreditServerip;
	}
	public void setAccreditServerip(String accreditServerip) {
		this.accreditServerip = accreditServerip;
	}*/
	
}
