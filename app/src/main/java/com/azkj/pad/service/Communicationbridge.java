package com.azkj.pad.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import cn.sword.SDK.MediaEngine;

public class Communicationbridge {
	//public static MediaEngine media=null;
	public static SipUser sipUser;
	public static SipServer sipServer;
	public static SharedPreferences prefs;
	public static ArrayList<String> mAudioCodecArray;
	public static Context context;
	public static boolean loginVoidState = false;
	public static Editor ed;
	private static boolean issucces;
	
	public Communicationbridge(Context context,SharedPreferences prefs,ArrayList<String> mAudioCodecArray){
		
		// media=MediaEngine.GetInstance();
		this.context=context;
		 this.prefs=prefs;
		 this.mAudioCodecArray=mAudioCodecArray;
		 sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		 sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
		 ed = prefs.edit();
		 init();

	}
	//初始化
	public static boolean init(){



		 String localip=getHostIP();
		 if(localip==null)
			 localip="";
		 Log.e("初始化", "准备初始化："+localip);
		boolean LogIsOpen = prefs.getBoolean(GlobalConstant.SP_START_LOG, false);
		 boolean ret= MediaEngine.GetInstance().ME_Init("0.0.0.0",50004, 5, "/sdcard/sdk_log.txt", LogIsOpen, false);
		 loginVoidState = ret;
         MediaEngine.GetInstance().ME_MonitorUsbCamera(context.getApplicationContext());
		 if(ret){
			Log.e("初始化", "初始化成功");
		}else{
			Intent loginStateIntent = new Intent();
			loginStateIntent.setAction(GlobalConstant.ACTION_LOGINSTATE_Sws);
			loginStateIntent.putExtra("arg0", false);
			loginStateIntent.putExtra("arg1", String.valueOf(-1));
			loginStateIntent.putExtra("arg2", "Login Has Failed");
			if (context != null){
				context.sendBroadcast(loginStateIntent);
			}

			Log.e("初始化失败", "广播已经发送");
			Log.e("初始化", "初始化失败");
		}
		return ret;
	}
	//登录
    public  void Login(Context context){
    	//pjsip登录
		this.context=context;
    	PjSipLogin();
    }

    //退出
	public static void Logout(){
		//MtcCli.Mtc_CliLogout();
		
	}
/*	//注销          Sws 隐藏 2017/11/08
	public static void me_Destroy(){
		if(media != null){
			media.ME_Destroy();
			media=null;		
			Log.e("me_Destroy", "清空");
		}
	}*/
	//PJsip登录
	public static boolean PjSipLogin(){
		try{

					
			//Sws 11/09 add
			String userName = prefs.getString(GlobalConstant.SP_USERNAME, null);
			String password = prefs.getString(GlobalConstant.SP_PASSWORD, null);
			String serverIP = prefs.getString(GlobalConstant.SP_SERVERIP, null);
			//sipServer.setServerIp(serverIP);
			String serverPortS = prefs.getString(GlobalConstant.SP_PORT, null);
			int serverPort = Integer.valueOf(serverPortS);
			int disPort=10001;
			//Sws 11/09 add
			if(serverIP != null){
				if(userName != null){
					if(password != null){

						//是否开启SDK日志
						MediaEngine.GetInstance().ME_Trace(true,false,false);
						//启动硬编辑   参数：1，开启硬编码 ,2，开启硬解码  ,3，采用H264硬编辑码（需硬件配合） ,4，采用H265硬编辑码（需硬件配合）
						boolean enableH265 = prefs.getBoolean(GlobalConstant.ACTION_ENABLE_H265,false);
						Log.e("H265测试配置","enableH265:" + enableH265);
						MediaEngine.GetInstance().ME_UseHardwareCodec(true,true,true,enableH265);
						Log.e("=========测试断网重连","regist 参数=======================serverIP:" + serverIP);
						Log.e("=========测试断网重连","regist 参数=======================serverPort:" + serverPort);
						Log.e("=========测试断网重连","regist 参数=======================disPort:" + disPort);
						Log.e("=========测试断网重连","regist 参数=======================userName:" + userName);
						Log.e("=========测试断网重连","regist 参数=======================password:" + password);
						issucces = MediaEngine.GetInstance().ME_Regist(serverIP,serverPort ,disPort,userName, password, MediaEngine.ME_UserType.Dispatcher,60,context);
						if (!issucces){
							Toast.makeText(context, "初始化失败请尝试重启App", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(context, "密码为空", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(context, "用户名为空", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(context, "IP为空", Toast.LENGTH_SHORT).show();
			}
			Log.e("ME_Regist", "返回状态："+issucces+",本地IP:"+getHostIP());
		}catch(Exception ex){
			Log.e("log", ""+ex.getMessage());
		}
		return issucces;
	}
	 /**
     * 获取ip地址
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        Log.e("本地IP", "本地IP："+hostIp);
        return hostIp;

    }
}
