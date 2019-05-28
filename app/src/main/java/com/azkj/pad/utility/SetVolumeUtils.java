package com.azkj.pad.utility;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.azkj.pad.activity.PTT_3G_PadApplication;
import com.azkj.pad.activity.R;


/**
 * Created by ANGELCOMM on 2018/11/16.
 */

//用于音量调节
public class SetVolumeUtils {

    private PTT_3G_PadApplication ptt_3g_PadApplication = null;
    private PopupWindow popupWindow;
    private Activity context;
    private AudioManager audioManager;
    private VerticalProgressBar progressBar;
    private View inflate;
    public SetVolumeUtils(Activity context, AudioManager audioManager){
                    ptt_3g_PadApplication = (PTT_3G_PadApplication) context.getApplication();
                    this.context = context;
                    this.audioManager = audioManager;
    }

    public SetVolumeUtils(){

    }

    //调整音量值
    public void showPopupWindow(boolean isDown, View v) {
        int currentVolume = ptt_3g_PadApplication.getCurrentVolume();
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        if (isDown){
            if (currentVolume < streamMaxVolume){
                currentVolume++;
            }
        }else {
            if (currentVolume > 1){
                currentVolume--;
            }
        }

        Log.e("当前2222222","" + audioManager.isSpeakerphoneOn());


        audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                currentVolume,
                AudioManager.STREAM_VOICE_CALL);
        Log.e("当前状态","扬声器状态 : " + audioManager.isSpeakerphoneOn());
        ptt_3g_PadApplication.setCurrentVolume(currentVolume);

        if (progressBar == null){
            inflate = LayoutInflater.from(context).inflate(R.layout.mainpopuplayout, null);
            progressBar = (VerticalProgressBar) inflate.findViewById(R.id.progressBar);
            progressBar.setMax(streamMaxVolume);
        }
        progressBar.setProgress(currentVolume);
        if (popupWindow == null){
            popupWindow = new PopupWindow(inflate, WindowManager.LayoutParams.WRAP_CONTENT, v.getHeight() / 2);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
        popupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.CENTER_VERTICAL,0,0);
    }


    public void cancelPopupWindow(){
            if (popupWindow != null){
                popupWindow.dismiss();
                popupWindow = null;
                progressBar = null;
                inflate = null;
            }


    }





}
