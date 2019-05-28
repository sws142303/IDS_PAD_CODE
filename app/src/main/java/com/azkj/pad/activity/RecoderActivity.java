package com.azkj.pad.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.sword.SDK.MediaEngine;


public class RecoderActivity extends Activity {
	// 用来显示图片
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	// 刻录按钮
	private Button recoderbutton;
	// 停止按钮
	private Button stopbutton;
	// 媒体刻录对象
	private MediaRecorder mediaRecorder;
	private Camera mCamera;
	private String videoFilePath;
	private boolean isRecordering=false;
	private SharedPreferences prefs;
	private boolean isOptimizes;
	private Timer timer;
	private long startTime = 0;
	private static final int MIN_INTERVAL_TIME = 2000;// 2s
	private List<MediaEngine.ME_VideoDeviceInfo> localCameras = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 窗口特效为无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置窗口全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设定屏幕显示为横向
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_recoder);
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		final String rotation = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H,"180");

		recoderbutton = (Button) this.findViewById(R.id.recoderbutton);
		stopbutton = (Button) this.findViewById(R.id.stopbutton);
		stopbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stop();
			}
		});
		recoderbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recoder(view,rotation);
			}
		});
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback); // holder加入回调接口
		// 获取的画面直接输出到屏幕上
		//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		//是否优化视频质量
		isOptimizes = prefs.getBoolean(GlobalConstant.ACTION_MSG_SENDVIDEO, false);

		localCameras = new ArrayList<>();
		MediaEngine.ME_VideoDeviceInfo[] videoDevices = MediaEngine.GetInstance().ME_GetVideoDevices();
		for (int i = 0; i < videoDevices.length; i++) {
			if (videoDevices[i].name.equals("Back camera") || videoDevices[i].name.equals("Front camera") || videoDevices[i].name.equals("Usb camera")) {
				localCameras.add(videoDevices[i]);
			}
			Log.e("得到的摄像头","name :" + videoDevices[i].name);
			Log.e("得到的摄像头","id :" + videoDevices[i].id);
		}
	}
	
	Callback surfaceHolderCallback = new Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			surfaceView = null;
			surfaceHolder = null;
			mediaRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			surfaceHolder = arg0;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			surfaceHolder = arg0;
		}
	};

	// 点击刻录按钮处理方法
	public void recoder(View v,String rotation) {


		if (localCameras.size() <= 0){
			ToastUtils.showToast(this,"未检测到视频设备");
			return;
		}

		Log.e("AAAAAAAAAAAAAAAAAAAAAAA",""+rotation);
		try {
			// 判断是否存在SD卡
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 将刻录的视频保存到SD卡中
				File videoFileDir=new File(CommonMethod.getMessageFileDownPath(4));
				if(!videoFileDir.exists()){
					videoFileDir.mkdirs();
				}
				videoFilePath= CommonMethod.getMessageFileDownPath(4)+ FormatController.getNewFileNameByDate()+".mp4";
				mediaRecorder = new MediaRecorder();
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
						== PackageManager.PERMISSION_GRANTED) {
					System.out.println("ok");
				}else {
					ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
				}
				//更改横屏到竖屏方式
				mCamera = Camera.open(localCameras.get(0).id);
				Camera.Parameters params = mCamera.getParameters();
				mCamera.setDisplayOrientation(Integer.valueOf(rotation));  //旋转预览
				//旋转了90度,最好先判断下JDK的版本号，再决定旋转不	(2.2以上)	
				mCamera.setParameters(params);
				//mCamera.stopPreview();//停止预览
				mCamera.unlock();//解锁
				mediaRecorder.setCamera(mCamera);
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// 设置录制视频源为Camera(相机)
				mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
				mediaRecorder.setOrientationHint(Integer.valueOf(rotation));// //旋转影像
				// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); 

				//if (isOptimizes){
					// 设置录制的视频编码h263 h264
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
					// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
					mediaRecorder.setVideoSize(1280, 720);
					mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
					// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
					mediaRecorder.setVideoFrameRate(30);//start failed: -19( 视频的帧率和视频大小是需要硬件支持的，如果设置的帧率和视频大小,如果硬件不支持就会出现错误)
				/*}else {
					// 设置录制的视频编码h263 h264
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
					// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
					mediaRecorder.setVideoSize(176, 144);
					// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
					mediaRecorder.setVideoFrameRate(20);//start failed: -19( 视频的帧率和视频大小是需要硬件支持的，如果设置的帧率和视频大小,如果硬件不支持就会出现错误)
				}*/

				// 设置预览显示
				mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
				// 设置视频的最大持续时间
				mediaRecorder.setMaxDuration(60000);
				mediaRecorder.setOnInfoListener(new OnInfoListener() {
					@Override
					public void onInfo(MediaRecorder mr, int what, int extra) {
						if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
							Toast.makeText(getApplicationContext(),
									"已经达到最长录制时间", Toast.LENGTH_LONG)
									.show();
							if (mediaRecorder != null) {
								mediaRecorder.stop();
								mediaRecorder.release();
								mediaRecorder = null;
							}
						}
					}
				});
				// 设置刻录的视频保存路径
				mediaRecorder.setOutputFile(videoFilePath);
				// 预期准备
				mediaRecorder.prepare();
				// 开始刻录
				mediaRecorder.start();
				startTime = System.currentTimeMillis();
				isRecordering=true;
			} else {
				Toast.makeText(getApplicationContext(), "检测到手机没有存储卡！请插入手机存储卡再开启本应用",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("打开视频","Error :"+e.getMessage());
		}
		// 刻录按钮不可点击
		recoderbutton.setEnabled(false);
		// 停止按钮可点击
		stopbutton.setEnabled(true);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

			stop();

		
	}
	
	@Override
	protected void onDestroy() {

		if (mediaRecorder != null) {
			//Could not execute method of the activity
			//Caused by: java.lang.IllegalStateException
			//MediaRecorder.stop
			//确保调用了start方法后，才调用stop方法
			if(isRecordering){
				mediaRecorder.stop();
			}
			mediaRecorder.release();
			mediaRecorder = null;
		}
		if(surfaceView!=null){
			surfaceView = null;
		}
		if(surfaceHolder!=null){
			surfaceHolder = null;
		}
		if (mCamera != null){
			mCamera.release();
			mCamera = null;
		}
		
		super.onDestroy();
	}

	// 点击停止按钮处理方法
	public void stop() {

		long l = System.currentTimeMillis();
		long intervalTime = l - startTime;
		if (intervalTime < MIN_INTERVAL_TIME){
			ToastUtils.showToast(getApplication(),"录制时间不能小于两秒");
			return;
		}

		// 停止刻录,并释放资源
		if (mediaRecorder != null) {
			if(isRecordering){
				mediaRecorder.stop();
			}
			mediaRecorder.release();
			mediaRecorder = null;
			if(mCamera!=null){
				mCamera.stopPreview();
				mCamera.release();
			}
			isRecordering=false;
		}
		// 刻录按钮可点击
		recoderbutton.setEnabled(true);
		// 停止按钮不可点击
		stopbutton.setEnabled(false);
		
		Intent intent= getIntent();
		intent.putExtra("localpath", videoFilePath);
		setResult(RESULT_OK, intent);
		finish();//关闭
	}

}