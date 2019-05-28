package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayViewActivity extends Activity {
	private VideoView videoView;
	private String src;
	private int duration = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		setContentView(R.layout.activity_playview);
		
		Intent intent = getIntent();
		src = intent.getStringExtra("src");
		
		videoView = (VideoView) findViewById(R.id.videoView);
		videoView.setVideoURI(Uri.parse(src));
		MediaController mediaController = new MediaController(this, false);
		videoView.setMediaController(mediaController);
		videoView.requestFocus();
		videoView.start();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (videoView != null) {
			videoView.seekTo(duration);
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (videoView!=null) {
			duration = videoView.getCurrentPosition();
			videoView.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (videoView != null){
			videoView = null;
		}
	}
}
