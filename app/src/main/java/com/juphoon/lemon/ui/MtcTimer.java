package com.juphoon.lemon.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

public class MtcTimer {

    private static final String MTC_TIMER_BROADCAST_ACTION = "com.juphoon.lemon.ACTION_TIMER";
    private static AlarmManager sAlarmManager;
    private static BroadcastReceiver sTimerReceiver = null;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        sTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MtcTimer.active();
            }
        };
        context.registerReceiver(sTimerReceiver, new IntentFilter(MTC_TIMER_BROADCAST_ACTION));
        initCallback();
    }

    public static void destroy() {
        sContext.unregisterReceiver(sTimerReceiver);
        sContext = null;
        sTimerReceiver = null;
        sAlarmManager = null;
        destroyCallback();
    }

    private static native void active();

    private static native void initCallback();

    private static native void destroyCallback();

    private static void timerInit() {
        sAlarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
    }

    private static void timerDestroy() {
        sAlarmManager = null;
    }

    private static void timerStart(long delay) {
        sAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, timerOperation(sContext));
    }

    private static void timerStop() {
        sAlarmManager.cancel(timerOperation(sContext));
    }

    private static PendingIntent timerOperation(Context context) {
        Intent intent = new Intent(MTC_TIMER_BROADCAST_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private static final int CALLBACK_TIMER_INIT = 0;
    private static final int CALLBACK_TIMER_DESTROY = 1;
    private static final int CALLBACK_TIMER_START = 2;
    private static final int CALLBACK_TIMER_STOP = 3;

    private static void mtcTimerCallback(int function, long delay) {
        switch (function) {
            case CALLBACK_TIMER_INIT:
                timerInit();
                break;
            case CALLBACK_TIMER_DESTROY:
                timerDestroy();
                break;
            case CALLBACK_TIMER_START:
                timerStart(delay);
                break;
            case CALLBACK_TIMER_STOP:
                timerStop();
                break;
        }
    }
}
