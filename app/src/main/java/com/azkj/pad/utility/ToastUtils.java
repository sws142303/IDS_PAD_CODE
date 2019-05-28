package com.azkj.pad.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ANGELCOMM on 2018/3/8.
 *
 *      Toast工具类
 *                      史文胜
 */

public class ToastUtils {

    private static Toast toast = null;

    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }



}
