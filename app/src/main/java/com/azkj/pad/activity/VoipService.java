package com.azkj.pad.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VoipService extends Service {

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
