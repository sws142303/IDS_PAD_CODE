package com.juphoon.lemon.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MtcBootReceiver extends BroadcastReceiver {

    public static final String MTC_BOOT_ACTION = "com.juphoon.lemon.ui.mtcboot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Intent i = new Intent();
            i.setAction(MTC_BOOT_ACTION);
            context.startService(i);
        }
    }
}
