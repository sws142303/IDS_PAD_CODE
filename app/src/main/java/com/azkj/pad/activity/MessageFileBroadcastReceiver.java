package com.azkj.pad.activity;

import cloud.Monitor.VideoSender.IVideoSenderCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageFileBroadcastReceiver extends BroadcastReceiver implements IVideoSenderCallback{
	@Override
	public void OnSessionConnectStatus(int status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDispatchCmd(int cmd, int nSourceId, String SourceContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUpLoadFile(String strFileName, int nStatus,
			double nStatusReport) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDownLoadFile(String strFileName, int nStatus,
			double nStatusReport) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}

}
