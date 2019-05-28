package com.juphoon.lemon.ui;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MtcRing {
	
    private static MediaPlayer sMediaPlayer = null;
    private static Vibrator sVibrator = null;
    private static final long VIBRATE_PATTERN[] = new long[] {1000,1000};
    private static AudioManager audioManager = null;
    public static void startRing(final Context c, String fileName) {
    	/*
    	//获取音频服务
    	AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    	//设置声音模式
    	audioManager.setMode(AudioManager.STREAM_MUSIC);
    	//关闭麦克风
    	audioManager.setMicrophoneMute(false);
    	// 打开扬声器
    	audioManager.setSpeakerphoneOn(true);*/
    	//打开麦克风
        
        audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);

        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        int mode = audioManager.getRingerMode();
        if(audioManager.isMicrophoneMute()) {
			audioManager.setMicrophoneMute(false);
            Log.e("集体测试","设置麦克风 mtcRing 39  取消静音");
		}

        int aa = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    	// 打开扬声器
    	audioManager.setSpeakerphoneOn(true);

        Log.e("集体测试   ","测试扬声器 mtcRing    45   true");
        Toast.makeText(c, "mode: "+mode + "," + aa + "," + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), Toast.LENGTH_SHORT).show();
         switch (mode) {

            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (sVibrator == null)
                    sVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                sVibrator.vibrate(VIBRATE_PATTERN, 0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
            	Toast.makeText(c, "进入 "+fileName, Toast.LENGTH_SHORT).show();
                if (sVibrator == null)
                    sVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                try {
                    int value = Settings.System.getInt(c.getContentResolver(), "vibrate_when_ringing");
                    if (value == 1) {
                        sVibrator.vibrate(VIBRATE_PATTERN, 0);
                    }
                } catch (SettingNotFoundException e) {
                    sVibrator.vibrate(VIBRATE_PATTERN, 0);
                }

                if (sMediaPlayer == null) {
                    sMediaPlayer = new MediaPlayer();
                } else if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.stop();
                    sMediaPlayer.reset();
                }

                sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    	try {
            //播放 assets/a2.mp3 音乐文件
            AssetFileDescriptor fd = c.getAssets().openFd(fileName);
            //sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            fd.close();
            sMediaPlayer.setLooping(true);
            sMediaPlayer.prepare();

            sMediaPlayer.setVolume(0.1f,
                    0.1f);
            sMediaPlayer.start();

    	 } catch (IllegalArgumentException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (IllegalStateException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
    	break;
         }

    }

    public static void startRingBack(Context c, String fileName) {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        } else if (sMediaPlayer.isPlaying()) {
            sMediaPlayer.stop();
            
            sMediaPlayer.reset();
        }
        

        sMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        AudioManager audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

    	  try {
              //播放 assets/a2.mp3 音乐文件
              AssetFileDescriptor fd = c.getAssets().openFd(fileName);
              sMediaPlayer = new MediaPlayer();
              fd.close();
              sMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
              sMediaPlayer.setLooping(true);
              sMediaPlayer.prepare();
              sMediaPlayer.start();
          } catch (IOException e) {
              e.printStackTrace();
          }  
    }

    public static void stop() {
        if (sMediaPlayer != null) {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    	if(audioManager != null){
    		audioManager = null;
    	}

        if (sVibrator != null) {
            sVibrator.cancel();
            sVibrator = null;
        }
    }

    
}

