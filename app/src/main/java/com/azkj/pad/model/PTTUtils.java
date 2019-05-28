package com.azkj.pad.model;

import android.app.smdt.SmdtManager;
import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ANGELCOMM on 2019/4/19.
 */

public class PTTUtils {

    private String TAG = "PttUtils";
    private Timer mTimer = null;
    private SmdtManager mSmdtManager;
    private boolean isPttDown = false;
    private boolean enable = true;//是否监听ptt手咪
    private PTTInterface pttInterface;

    public PTTUtils(Context context,PTTInterface pttInterface) {
        mSmdtManager = SmdtManager.create(context);
        this.pttInterface = pttInterface;
    }

    public void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mSmdtManager != null) {
                        if (mSmdtManager.smdtReadGpioValue(1) == 1) {
                            if (isPttDown) {
                               // Global.getInstance().controller.pttUp(PttKeyType.PTT_BLUETOOTH);
                                isPttDown = false;
                                Log.e(TAG,"isPttDown :" + isPttDown);
                                pttInterface.pttUp();
                            }
                        } else {
                            if (enable) {
                                if (!isPttDown) {
                                    //   Global.getInstance().controller.pttDown(PttKeyType.PTT_BLUETOOTH);
                                    isPttDown = true;
                                    Log.e(TAG,"isPttDown :" + isPttDown);

                                    pttInterface.pttDown();
                                }
                            }
                        }
                    }
                }
            }, 50, 50);
        }
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public void enableListener(boolean enable) {
        Log.e(TAG, "enableListener " + enable);
        this.enable = enable;
    }

}
