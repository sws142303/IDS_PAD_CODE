package com.juphoon.lemon.ui;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MtcProximity {

	//private static WakeLock sProximityWakeLock;
    public static void start(Context context) {
        try{
        if (sProximityWakeLock == null) {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
            sProximityWakeLock = pm.newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "MtcProximity");
            sProximityWakeLock.acquire();
        }
    }catch (Exception e){

    }
    }
    
    public static void stop() {
        try{
            if (sProximityWakeLock != null) {
                sProximityWakeLock.release();
                sProximityWakeLock = null;
            }
        }catch (Exception e){

        }

    }


    private static WakeLock sProximityWakeLock;
}
