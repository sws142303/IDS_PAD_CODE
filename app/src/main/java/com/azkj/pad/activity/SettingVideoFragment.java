package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.azkj.pad.model.CallResolvingPower;
import com.azkj.pad.model.ResolvingPower;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.MtcCallDb;

import java.util.ArrayList;
import java.util.List;

import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.utility.GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION;


/********
 * yuezs 隐藏 ice相关的
 *********/
//import Ice.Logger;

/*配置-视频通话*/
public class SettingVideoFragment extends PreferenceActivity {
	// 全局变量定义
	private  SharedPreferences prefs;
	private PTT_3G_PadApplication ptt_3g_PadApplication = null;
	private SipUser sipUser = null;
	// 父窗体
	private boolean state = true;
	private Activity mActivity;
	private String[] mVideoCameraArray;
	private String[] mVideoRotateArray;

	private  RadioGroup rg_resolution;
	private TextView rb_camerafront, rb_camerarear, rb_0degree_q, rb_90degree_q, rb_180degree_q, rb_270degree_q,rb_UVC_Camera;//Sws 11/14 隐藏//tv_ars,tv_nack,tv_tmmbr,tv_resolutioncon
	//后置摄像头角度
	private TextView rb_0degree_h, rb_90degree_h, rb_180degree_h, rb_270degree_h;

	private EditText et_bitrate,et_framerate,et_sendBuffer,et_receiveBuffer;
	private Button btn_savebitrate,btn_saveframerate,btn_sendBuffer,btn_receiveBuffer;
	private TextView tv_videoSendProportion_0;
	private TextView tv_videoSendProportion_1;
	private TextView tv_videoSendProportion_2;
	private TextView tv_TO_FIT;
	private TextView tv_TO_FIT_WITH_CROPPING;
	private TextView tv_camera;
	private TextView tv_camera1;
	private TextView tv_camera2;
	private TextView tv_camera3;
	private TextView tv_camera4;
	private TextView tv_camera5;
	private TextView tv_sendBuffer;
	private TextView tv_receiveBuffer;
	private TextView tv_h265;
	private TextView tv_StartUpH265;
	private TextView rb_CloseH265;

	public SettingVideoFragment(Activity activity){
		mActivity = activity;
		ptt_3g_PadApplication = (PTT_3G_PadApplication)mActivity.getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
	}
	private List<OptionInfo> mAnrLevel;


	// 加载数据供外部调用
	public boolean loadVideoView(){
		// 全局变量定义

		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		mVideoCameraArray = mActivity.getResources().getStringArray(R.array.video_camera);
		mVideoRotateArray = mActivity.getResources().getStringArray(R.array.video_rotate);
		//mVideoResolutionArray = mActivity.getResources().getStringArray(R.array.video_resolution);
		new AsyncTaskLocal().execute();
		//new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
		return true;
	}
	// 异步初始化数据信息
	class AsyncTaskLocal extends AsyncTask<Object, Object, Object>
	{
		@Override     
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setViewValue();
		}
	}

	private void setViewValue() {

		// 取得控件
		tv_receiveBuffer = (TextView)mActivity.findViewById(R.id.tv_receiveBuffer);
		tv_sendBuffer = (TextView)mActivity.findViewById(R.id.tv_sendBuffer);
		tv_camera5 = (TextView)mActivity.findViewById(R.id.tv_camera5);
		tv_camera4 = (TextView)mActivity.findViewById(R.id.tv_camera4);
		tv_camera3 = (TextView)mActivity.findViewById(R.id.tv_camera3);
		tv_camera2 = (TextView)mActivity.findViewById(R.id.tv_camera2);
		tv_camera1 = (TextView)mActivity.findViewById(R.id.tv_camera1);
		tv_camera = (TextView)mActivity.findViewById(R.id.tv_camera);
		rb_camerafront = (TextView)mActivity.findViewById(R.id.rb_camerafront);
		rb_camerarear = (TextView)mActivity.findViewById(R.id.rb_camerarear);
		rb_UVC_Camera = (TextView)mActivity.findViewById(R.id.rb_UVC_Camera);
		tv_h265 = (TextView)mActivity.findViewById(R.id.tv_h265);
		tv_StartUpH265 = (TextView)mActivity.findViewById(R.id.tv_StartUpH265);
		rb_CloseH265 = (TextView)mActivity.findViewById(R.id.rb_CloseH265);

		boolean enableH265 = prefs.getBoolean(GlobalConstant.ACTION_ENABLE_H265, false);
		if (enableH265){
			tv_StartUpH265.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_CloseH265.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else {
			rb_CloseH265.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_StartUpH265.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}



		//前后置摄像头
		String camera = prefs.getString(GlobalConstant.ACTION_GETCAMERA,"2");
		if(camera.equals("1")){
			rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if(camera.equals("2")){
			rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));;
			rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));;
		}else if(camera.equals("-1")){
			rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			Editor editor = prefs.edit();
			//editor.putString(GlobalConstant.SP_VIDEO_CAMERA, mVideoCameraArray[0]);
			editor.putString(GlobalConstant.ACTION_GETCAMERA, "2");
			editor.apply();
		}


		//是否有前置摄像头
		boolean isCameraFront = ptt_3g_PadApplication.getIsCameraFront();
		if(isCameraFront == false)
		{
			rb_camerafront.setVisibility(View.GONE);
		}
		else
		{
			rb_camerafront.setVisibility(View.VISIBLE);
		}

		//是否有后置摄像头
		boolean isCameraBack = ptt_3g_PadApplication.getIsCameraBack();

		if(isCameraBack == false)
		{
			rb_camerarear.setVisibility(View.GONE);
		}
		else
		{
			rb_camerarear.setVisibility(View.VISIBLE);
		}

		//是否有UVC摄像头
		boolean cameraUVC = ptt_3g_PadApplication.isCameraUVC();
		if (cameraUVC){
			rb_UVC_Camera.setVisibility(View.VISIBLE);

			MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo = ptt_3g_PadApplication.getMe_videoDeviceInfo();
			if (me_videoDeviceInfo != null){
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.ACTION_GETCAMERA, String.valueOf(me_videoDeviceInfo.id));
				editor.apply();
			}

			rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));

		}else {
			rb_UVC_Camera.setVisibility(View.GONE);
		}


		rb_0degree_q = (TextView)mActivity.findViewById(R.id.rb_0degree_q);
		rb_90degree_q = (TextView)mActivity.findViewById(R.id.rb_90degree_q);
		rb_180degree_q = (TextView)mActivity.findViewById(R.id.rb_180degree_q);
		rb_270degree_q = (TextView)mActivity.findViewById(R.id.rb_270degree_q);
		rb_0degree_h = (TextView)mActivity.findViewById(R.id.rb_0degree_h);
		rb_90degree_h = (TextView)mActivity.findViewById(R.id.rb_90degree_h);
		rb_180degree_h = (TextView)mActivity.findViewById(R.id.rb_180degree_h);
		rb_270degree_h = (TextView)mActivity.findViewById(R.id.rb_270degree_h);
		TextView tv_bitrate = (TextView)mActivity.findViewById(R.id.tv_bitrate);
		TextView tv_framerate = (TextView)mActivity.findViewById(R.id.tv_framerate);
		et_bitrate = (EditText)mActivity.findViewById(R.id.et_bitrate);
		et_framerate = (EditText) mActivity.findViewById(R.id.et_framerate);
		et_sendBuffer = (EditText) mActivity.findViewById(R.id.et_sendBuffer);
		et_receiveBuffer = (EditText) mActivity.findViewById(R.id.et_receiveBuffer);

		//获取默认发送接收缓存大小
		String sendBuffer = prefs.getString(GlobalConstant.SEETINGOTHER_SENDBUFFER, null);
		String receiveBuffer = prefs.getString(GlobalConstant.SEETINGOTHER_RECEIVEBUFFER, null);
		if (sendBuffer != null){
			et_sendBuffer.setText(sendBuffer);
		}
		if (receiveBuffer != null){
			et_receiveBuffer.setText(receiveBuffer);
		}
		//视频编码比例设置
		tv_videoSendProportion_0 = (TextView) mActivity.findViewById(R.id.tv_videoSendProportion_0);
		tv_videoSendProportion_1 = (TextView) mActivity.findViewById(R.id.tv_videoSendProportion_1);
		tv_videoSendProportion_2 = (TextView) mActivity.findViewById(R.id.tv_videoSendProportion_2);

		//视频解码模式设置
		tv_TO_FIT = (TextView) mActivity.findViewById(R.id.tv_TO_FIT);
		tv_TO_FIT_WITH_CROPPING = (TextView) mActivity.findViewById(R.id.tv_TO_FIT_WITH_CROPPING);


		btn_savebitrate = (Button) mActivity.findViewById(R.id.btn_bitrate);
		btn_saveframerate = (Button) mActivity.findViewById(R.id.btn_framerate);
		btn_sendBuffer = (Button) mActivity.findViewById(R.id.btn_sendBuffer);
		btn_receiveBuffer = (Button) mActivity.findViewById(R.id.btn_receiveBuffer);
		btn_saveframerate.setEnabled(false);
		btn_savebitrate.setEnabled(false);
		btn_sendBuffer.setEnabled(false);
		btn_receiveBuffer.setEnabled(false);
		TextView tv_resolution = (TextView)mActivity.findViewById(R.id.tv_resolution);
		int width = 352;
		int height = 288;
		String resolution = prefs.getString(GlobalConstant.SP_VIDEO_RESOLUTION, "352*288");
		if(null != resolution && !"".equals(resolution))
		{
			String[] checkRadioArrays = resolution.split("\\*");
			Log.e("checkRadioArray", "checkRadioArray:"+checkRadioArrays[0]+","+checkRadioArrays[1]);
			if(null != checkRadioArrays)
			{
				width = Integer.parseInt(checkRadioArrays[0]);
				height = Integer.parseInt(checkRadioArrays[1]);
			}
		}

		rg_resolution = (RadioGroup)mActivity.findViewById(R.id.rg_resolution);
		String widths = prefs.getString("width", "");
		String heights = prefs.getString("height","");



		CallResolvingPower callR = new CallResolvingPower(mActivity);
		List<ResolvingPower> list = callR.getResolvingPower();


			for(int i = 0; i <  list.size(); i++)
			{
				RadioButton tempButton = new RadioButton(mActivity);
				tempButton.setBackgroundResource(R.drawable.setting_radio_bg);
				tempButton.setCompoundDrawables(mActivity.getResources().getDrawable(R.drawable.setting_radio_style), null, null, null);
				tempButton.setText("("+list.get(i).getHeight()+"*"+list.get(i).getWidth()+")");
				tempButton.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
				tempButton.setTextSize(14.0f);
				rg_resolution.addView(tempButton, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				tempButton.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
				tempButton.getPaint().setFakeBoldText(true);
			}

		rg_resolution.invalidate();

		// 设置字体
		rb_CloseH265.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_StartUpH265.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_h265.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_receiveBuffer.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_sendBuffer.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera5.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera4.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera3.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_camera.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_camerafront.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_camerarear.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_UVC_Camera.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_0degree_q.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_90degree_q.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_180degree_q.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_270degree_q.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_0degree_h.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_90degree_h.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_180degree_h.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_270degree_h.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_bitrate.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_framerate.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_bitrate.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_framerate.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_sendBuffer.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_receiveBuffer.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_resolution.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_videoSendProportion_0.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_videoSendProportion_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_videoSendProportion_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));

		tv_TO_FIT.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_TO_FIT_WITH_CROPPING.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));


		//网管默认分辨率
		String resolvingPower = prefs.getString(GlobalConstant.SP_MONITOR_RESOLUTION, null);
		if (resolvingPower == null
				|| resolvingPower.equals("")){
			String radioWidth = prefs.getString("radioWidth", null);
			String radioHeight = prefs.getString("radioHeight", null);
			if (radioHeight == null && radioWidth == null){
				rg_resolution.check(rg_resolution.getChildAt(3).getId());
			}else {
				for (int i = 0; i < rg_resolution.getChildCount(); i++){
					RadioButton childAt =(RadioButton) rg_resolution.getChildAt(i);
					String content = "(" + radioWidth + "*" + radioHeight + ")";
					if (content.equals(childAt.getText().toString())){
						childAt.setChecked(true);
					}
				}
			}
		}else {
			for (int i = 0; i < rg_resolution.getChildCount(); i++){
				RadioButton childAt =(RadioButton) rg_resolution.getChildAt(i);
				String content = "(" + resolvingPower + ")";
				if (content.equals(childAt.getText().toString())){
					childAt.setChecked(true);
				}
			}
		}

		//设置比特率默认值
		String monRateValue = prefs.getString("CallRateValue", null);
		if (monRateValue == null
				|| monRateValue.equals("")){
			String bitrate = prefs.getString(GlobalConstant.SP_VIDEO_BITRATE, null);
			if (bitrate == null){
				et_bitrate.setText("1024");
			}else {
				et_bitrate.setText(bitrate);
			}
		}else {
			et_bitrate.setText(monRateValue);
		}

		String monFrameValue = prefs.getString("CallFrameValue", null);
		if (monFrameValue == null
				|| monFrameValue.equals("")){
			//设置帧率默认值
			String framerate = prefs.getString(GlobalConstant.SP_VIDEO_FRAMERATE, null);
			if (framerate== null){
				et_framerate.setText("15");
			}else {
				Log.e("=======哎哎哎","" + framerate);
				et_framerate.setText(framerate);
			}
		}else {
			et_framerate.setText(monFrameValue);
		}

		//视频编码比例设置
		String videoSendProportion = prefs.getString(SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION, "0");
		if (videoSendProportion.equals("0")){
			tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (videoSendProportion.equals("1")){
			tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (videoSendProportion.equals("2")){
			tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}

		//视频解码模式设置
		String videoScalingMode = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"1");
		if (videoScalingMode.equals("1")){
			tv_TO_FIT.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_TO_FIT_WITH_CROPPING.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (videoSendProportion.equals("2")){
			tv_TO_FIT_WITH_CROPPING.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			tv_TO_FIT.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}


		//得到前置旋转角度 默认值
		String rotates_q = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "0");
		if (rotates_q.equals("0")){
			rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_q.equals("90")){
			rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_q.equals("180")){
			rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_q.equals("270")){
			rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}

		//得到后置旋转角度 默认值
		String rotates_h = prefs.getString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "180");
		if (rotates_h.equals("0")){
			rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_h.equals("90")){
			rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_h.equals("180")){
			rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}else if (rotates_h.equals("270")){
			rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
		}



		/*取得ARS、NACK、TMMBR、Resolution的默认值结束*/
		String[] mAnrLevelArray = mActivity.getResources().getStringArray(R.array.anr_level);
		mAnrLevel = new ArrayList<OptionInfo>();
		for (int i = 0; i < mAnrLevelArray.length; i++){
			mAnrLevel.add(new OptionInfo(mAnrLevelArray[i], mAnrLevelArray[i]));
		}

		tv_videoSendProportion_0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Editor edit = prefs.edit();
				edit.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"0");
				edit.apply();

						tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));

			}
		});
		tv_videoSendProportion_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Editor edit = prefs.edit();
				edit.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"1");
				edit.apply();

				tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		tv_videoSendProportion_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Editor edit = prefs.edit();
				edit.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"2");
				edit.apply();

				tv_videoSendProportion_2.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_videoSendProportion_1.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				tv_videoSendProportion_0.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});

		tv_TO_FIT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Editor edit = prefs.edit();
				edit.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"1");
				edit.apply();

				tv_TO_FIT.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_TO_FIT_WITH_CROPPING.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});

		tv_TO_FIT_WITH_CROPPING.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				Editor edit = prefs.edit();
				edit.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_VIDEOSEND_PROPORTION,"2");
				edit.apply();

				tv_TO_FIT_WITH_CROPPING.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_TO_FIT.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});


		tv_StartUpH265.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.ACTION_ENABLE_H265, true);
				editor.apply();
				ToastUtils.showToast(mActivity,"重启后生效");
				tv_StartUpH265.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_CloseH265.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_CloseH265.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.ACTION_ENABLE_H265, false);
				editor.apply();
				ToastUtils.showToast(mActivity,"重启后生效");
				rb_CloseH265.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				tv_StartUpH265.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});

		rb_camerafront.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo = ptt_3g_PadApplication.getMe_videoDeviceInfo_Front();
				if (me_videoDeviceInfo != null){
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.ACTION_GETCAMERA, String.valueOf(me_videoDeviceInfo.id));
					editor.apply();
					MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(me_videoDeviceInfo.id);
					Log.e("******************7777**************","qianzhi  : " + me_videoDeviceInfo.id);
				}

//				Editor editor = prefs.edit();
//				//editor.putString(GlobalConstant.SP_VIDEO_CAMERA, mVideoCameraArray[0]);
//				editor.putString(GlobalConstant.ACTION_GETCAMERA, "1");
//				editor.apply();
				rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_camerarear.setOnClickListener(new OnClickListener() {
			@Override 
			public void onClick(View v) {
//				Editor editor = prefs.edit();
//				editor.putString(GlobalConstant.ACTION_GETCAMERA, "2");
//				editor.apply();
				MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo = ptt_3g_PadApplication.getMe_videoDeviceInfo_Back();
				if (me_videoDeviceInfo != null){
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.ACTION_GETCAMERA, String.valueOf(me_videoDeviceInfo.id));
					editor.apply();
					MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(me_videoDeviceInfo.id);
					Log.e("******************7777**************","后置  : " + me_videoDeviceInfo.id);
				}
				rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_UVC_Camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaEngine.ME_VideoDeviceInfo me_videoDeviceInfo = ptt_3g_PadApplication.getMe_videoDeviceInfo();
				if (me_videoDeviceInfo != null){
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.ACTION_GETCAMERA, String.valueOf(me_videoDeviceInfo.id));
					editor.apply();
					MediaEngine.GetInstance().ME_SetDefaultVideoCaptureDevice(me_videoDeviceInfo.id);
					Log.e("******************7777**************","USB  : " + me_videoDeviceInfo.id);
				}

				rb_UVC_Camera.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_camerafront.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_camerarear.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});




		rb_0degree_q.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "0");
				editor.apply();

				rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_0degree_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "0");
				editor.apply();

				rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		

		rb_90degree_q.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "90");
				editor.apply();

				rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_90degree_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "90");
				editor.apply();

				rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_180degree_q.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "180");
				editor.apply();

				rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
			}
		});
		rb_180degree_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "180");
				editor.apply();

				rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
				rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));

			}
		});
		rb_270degree_q.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_Q, "270");
				editor.apply();


				rb_0degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_q.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_q.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			}
		});
		rb_270degree_h.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SETTINGVIDEO_ACTIVITY_CAMERA_H, "270");
				editor.apply();


				rb_0degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_90degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_180degree_h.setTextColor(mActivity.getResources().getColor(R.color.contact_title_unselected));
				rb_270degree_h.setTextColor(mActivity.getResources().getColor(R.color.intercom_list_online));
			}
		});

		et_bitrate.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				btn_savebitrate.setEnabled(true);
			}});

		btn_savebitrate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((et_bitrate.getText() == null)
						|| (et_bitrate.getText().length() <= 0)){
					ToastUtils.showToast(mActivity.getBaseContext(), "请输入比特率");
					return;
				}
				int bitrate = Integer.valueOf(et_bitrate.getText().toString());
				if (bitrate < 30){
					ToastUtils.showToast(mActivity.getBaseContext(), "比特率不能小于30");
					et_bitrate.setText("30");
					bitrate = 30;
				}
				else if (bitrate > 10000){
					ToastUtils.showToast(mActivity.getBaseContext(), "比特率不能大于10000");
					et_bitrate.setText("10000");
					bitrate = 10000;
				}
				et_bitrate.setText(String.valueOf(bitrate));
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_VIDEO_BITRATE,String.valueOf(bitrate));
				editor.apply();

				Log.w("#######设置码率########", "#######设置码率bps ：" + bitrate);

				et_bitrate.clearFocus();

				ToastUtils.showToast(mActivity,"保存成功");

				btn_savebitrate.setEnabled(false);
			}
		});

		et_framerate.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				btn_saveframerate.setEnabled(true);
			}});
		btn_saveframerate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ((et_framerate.getText() == null)
						|| (et_framerate.getText().length() <= 0)){
					ToastUtils.showToast(mActivity.getBaseContext(), "请输入帧率");
					return;
				}
				int framerate = Integer.valueOf(et_framerate.getText().toString().trim());
				if (framerate < 10 || framerate == 10){
					ToastUtils.showToast(mActivity.getBaseContext(), "帧率需大于10");
					et_framerate.setText("10");
					framerate = 10;
				}
				else if (framerate > 30){
					ToastUtils.showToast(mActivity.getBaseContext(), "帧率不能大于30");
					et_framerate.setText("30");
					framerate = 30;
				}
				et_framerate.setText(String.valueOf(framerate));
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_VIDEO_FRAMERATE, String.valueOf(framerate));
				editor.apply();
				Log.w("#######设置帧率########", "#######设置帧率 ：" + framerate);

				// 设置帧率
				MtcCallDb.Mtc_CallDbSetVideoFramerate(framerate);
				et_framerate.clearFocus();

				ToastUtils.showToast(mActivity,"保存成功");

				btn_saveframerate.setEnabled(false);
			}
		});

		btn_sendBuffer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String sendBufferSize = et_sendBuffer.getText().toString().trim();
				MediaEngine.GetInstance().ME_SetSocketSndBufferSize(Integer.valueOf(sendBufferSize));
				prefs.edit().putString(GlobalConstant.SEETINGOTHER_SENDBUFFER,sendBufferSize).apply();


				et_sendBuffer.clearFocus();

				ToastUtils.showToast(mActivity,"保存成功");

				btn_sendBuffer.setEnabled(false);
			}
		});


		//发送缓冲
		et_sendBuffer.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				btn_sendBuffer.setEnabled(true);
			}});
		//接收缓冲
		et_receiveBuffer.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				btn_receiveBuffer.setEnabled(true);
			}});


		btn_receiveBuffer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String receiveBufferSize = et_receiveBuffer.getText().toString().trim();
				MediaEngine.GetInstance().ME_SetSocketSndBufferSize(Integer.valueOf(receiveBufferSize));
				prefs.edit().putString(GlobalConstant.SEETINGOTHER_RECEIVEBUFFER,receiveBufferSize).apply();

				ToastUtils.showToast(mActivity,"保存成功");

				btn_receiveBuffer.setEnabled(false);

				et_receiveBuffer.clearFocus();
			}
		});

		rg_resolution.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				Editor editor = prefs.edit();
				editor.putInt("checkedId", checkedId);
				editor.apply();


				RadioButton checkRadioButton = (RadioButton)rg_resolution.findViewById(checkedId);				
				String radioText = checkRadioButton.getText().toString().trim();
				String checkRadio = radioText.substring(1, radioText.length() - 1).toString();
				ToastUtils.showToast(mActivity, checkRadio);
				if(null != checkRadio && !"".equals(checkRadio))
				{				
					String[] checkRadioArray = checkRadio.split("\\*");					
					if(null != checkRadioArray)
					{
						String width = checkRadioArray[0].toString();
						String height = checkRadioArray[1].toString();
						Editor edit = prefs.edit();
						edit.putString("radioWidth",width);
						edit.putString("radioHeight",height);
						edit.apply();

						Log.e("视频配置----------->", width);
						Log.e("视频配置----------->", height);


						if(width.equals("1920") || height.equals("1080")){
							et_bitrate.setText("2048");
							et_framerate.setText("15");
							prefs.edit().putString(GlobalConstant.SP_VIDEO_BITRATE,et_bitrate.getText().toString().trim()).apply();
							prefs.edit().putString(GlobalConstant.SP_VIDEO_FRAMERATE,et_framerate.getText().toString().trim()).apply();
							Log.e("==Sws测试分辨率===","点击radioButton 修改帧率比特率    et_bitrate:"+et_bitrate.getText().toString().trim() + "et_framerate:"+et_framerate.getText().toString().trim());
						}else if(width.equals("1280") || height.equals("720")){
							et_bitrate.setText("1024");
							et_framerate.setText("15");
							prefs.edit().putString(GlobalConstant.SP_VIDEO_BITRATE,et_bitrate.getText().toString().trim()).apply();
							prefs.edit().putString(GlobalConstant.SP_VIDEO_FRAMERATE,et_framerate.getText().toString().trim()).apply();
							Log.e("==Sws测试分辨率===","点击radioButton 修改帧率比特率    et_bitrate:"+et_bitrate.getText().toString().trim() + "et_framerate:"+et_framerate.getText().toString().trim());
						}else if(width.equals("640") || height.equals("480")){
							et_bitrate.setText("1000");
							et_framerate.setText("20");
							prefs.edit().putString(GlobalConstant.SP_VIDEO_BITRATE,et_bitrate.getText().toString().trim()).apply();
							prefs.edit().putString(GlobalConstant.SP_VIDEO_FRAMERATE,et_framerate.getText().toString().trim()).apply();
							Log.e("==Sws测试分辨率===","点击radioButton 修改帧率比特率    et_bitrate:"+et_bitrate.getText().toString().trim() + "et_framerate:"+et_framerate.getText().toString().trim());
						}else if(width.equals("352") || height.equals("288")){
							et_bitrate.setText("600");
							et_framerate.setText("25");
							prefs.edit().putString(GlobalConstant.SP_VIDEO_BITRATE,et_bitrate.getText().toString().trim()).apply();
							prefs.edit().putString(GlobalConstant.SP_VIDEO_FRAMERATE,et_framerate.getText().toString().trim()).apply();
							Log.e("==Sws测试分辨率===","点击radioButton 修改帧率比特率    et_bitrate:"+et_bitrate.getText().toString().trim() + "et_framerate:"+et_framerate.getText().toString().trim());
						}
					}
				}
			}});

	}

	// 选项信息类
	public class OptionInfo {
		private String number;
		private String name;
		private boolean selected = false;
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public OptionInfo(){

		}

		public OptionInfo(String number, String name){
			this.number = number;
			this.name = name;
		}

		public OptionInfo(String number, String name, boolean selected){
			this.number = number;
			this.name = name;
			this.selected = selected;
		}
	}

	// 选项适配器
	public class OptionAdapter extends BaseAdapter {

		private Context context;

		private List<OptionInfo> list;

		public OptionAdapter(Context context, List<OptionInfo> list) {

			this.context = context;
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_setting_options_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.groupItem = (TextView) convertView.findViewById(R.id.groupItem);
				holder.itemlayout = (LinearLayout) convertView.findViewById(R.id.itemlayout);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.groupItem.setTextColor(Color.BLACK);
			if (list.get(position).getName().equals(list.get(position).getNumber())){
				holder.groupItem.setText(list.get(position).getName());
			}
			else {
				holder.groupItem.setText(list.get(position).getName() + "("+list.get(position).getNumber()+")");
			}

			if (list.get(position).isSelected()){
				holder.groupItem.setBackgroundColor(context.getResources().getColor(R.color.setting_content_bg));
				holder.groupItem.setTextColor(context.getResources().getColor(R.color.contact_title_selected));
			}
			else {
				holder.groupItem.setBackgroundColor(Color.WHITE);
				holder.groupItem.setTextColor(Color.BLACK);
			}

			return convertView;
		}

		class ViewHolder {
			LinearLayout itemlayout;
			TextView groupItem;
		}
	}
}
