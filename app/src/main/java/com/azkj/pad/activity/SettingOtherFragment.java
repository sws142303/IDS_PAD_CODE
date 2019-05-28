package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.OnRecyclerItemClickListener;
import com.azkj.pad.utility.SwipRecyclerViewAdapter;
import com.azkj.pad.utility.SwipeRecyclerView;
import com.azkj.pad.utility.TipHelper;
import com.azkj.pad.utility.ToastUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.activity.R.id.rg_grade_2;
import static com.azkj.pad.activity.R.id.rg_grade_3;


/*配置-其他配置*/
public class SettingOtherFragment extends PreferenceActivity {
	// 全局变量定义
	private SharedPreferences prefs;
	// 父窗体
	private Activity mActivity;
	private CheckBox cb_stun;
	private CheckBox cb_startup;
	private CheckBox cb_other;
	private CheckBox cb_encrypt;
	EditText et_settingOther_port;
	Button btn_settingOther_port;

	//语音发送增益相关
	private RadioButton rb_voiceSend_0, rb_voiceSend_0_5, rb_voiceSend_1, rb_voiceSend_2;
	private RadioButton rb_voiceReceive_0, rb_voiceReceive_0_5, rb_voiceReceive_1, rb_voiceReceive_2;
	private RadioGroup rb_voiceSend;
	private RadioGroup rb_voiceReceive;
	private Button btn_rtpSize;
	private EditText et_rtpSize;
	private CheckBox cb_isOpenFEC;
	private CheckBox cb_MediaStatistics;
	private CheckBox cb_MediaStatistics2;
	private CheckBox cb_FlowerScreen;
	private TextView tv_isOpenFEC;
	private TextView tv_MediaStatistics2;
	private TextView tv_FlowerScreen;
	private TextView tv_text1;
	//回音消除相关
	private RadioGroup rg_modular, rg_noiseReduction, rg_grade;
	private RadioButton rb_modular_1, rb_modular_2, rb_modular_3, rb_noiseReduction_1, rb_noiseReduction_2, rb_grade_1, rb_grade_2, rb_grade_3;
	private Button btn_Preservation;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */

	private AlertDialog.Builder dialogVoice;
	private SwipeRecyclerView swipeRefreshLayoutVoice;
	private SwipRecyclerViewAdapter swipRecyclerViewAdapter;
	private TextView et_audioMode;
	private PTT_3G_PadApplication ptt_3g_PadApplication;
	private RadioButton rb_normal;
	private RadioButton rb_in_call;
	private RadioButton rb_in_communication;
	private PopupWindow popupWindow;
	private CheckBox cb_callAnswer;

	public SettingOtherFragment(Activity activity) {
		mActivity = activity;
	}

	// 加载数据供外部调用
	public boolean loadOtherView() {
		// 全局变量定义
		ptt_3g_PadApplication = (PTT_3G_PadApplication) mActivity.getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		new AsyncTaskLocal().execute();
		//new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("SettingOtherFragment Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();


	}


	// 异步初始化数据信息
	class AsyncTaskLocal extends AsyncTask<Object, Object, Object> {
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
		TextView tv_text9 = (TextView) mActivity.findViewById(R.id.tv_text9);
		TextView tv_text8 = (TextView) mActivity.findViewById(R.id.tv_text8);
		TextView tv_text7 = (TextView) mActivity.findViewById(R.id.tv_text7);
		TextView tv_text6 = (TextView) mActivity.findViewById(R.id.tv_text6);
		TextView tv_text5 = (TextView) mActivity.findViewById(R.id.tv_text5);
		TextView tv_text4 = (TextView) mActivity.findViewById(R.id.tv_text4);
		TextView tv_text3 = (TextView) mActivity.findViewById(R.id.tv_text3);
		TextView tv_text2 = (TextView) mActivity.findViewById(R.id.tv_text2);
		TextView tv_text1 = (TextView) mActivity.findViewById(R.id.tv_text1);
		TextView tv_text = (TextView) mActivity.findViewById(R.id.textView);
		TextView tv_encrypt = (TextView) mActivity.findViewById(R.id.tv_encrypt);
		cb_encrypt = (CheckBox) mActivity.findViewById(R.id.cb_encrypt);
		TextView tv_stun = (TextView) mActivity.findViewById(R.id.tv_stun);
		cb_stun = (CheckBox) mActivity.findViewById(R.id.cb_stun);
		TextView tv_startup = (TextView) mActivity.findViewById(R.id.tv_startup);
		cb_startup = (CheckBox) mActivity.findViewById(R.id.cb_startup);
		TextView tv_setfir = (TextView) mActivity.findViewById(R.id.tv_setfir);
		//自动接听
		cb_callAnswer = (CheckBox) mActivity.findViewById(R.id.cb_CallAnswer);
		TextView tv_CallAnswer = (TextView) mActivity.findViewById(R.id.tv_CallAnswer);

		tv_CallAnswer.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text9.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text8.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text7.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text6.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text5.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text4.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text3.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_text.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));


		//优化视频
		cb_other = (CheckBox) mActivity.findViewById(R.id.cb_setfir);

		cb_isOpenFEC = (CheckBox) mActivity.findViewById(R.id.cb_isOpenFEC);
		tv_isOpenFEC = (TextView) mActivity.findViewById(R.id.tv_isOpenFEC);
		tv_FlowerScreen = (TextView) mActivity.findViewById(R.id.tv_FlowerScreen);
		tv_MediaStatistics2 = (TextView) mActivity.findViewById(R.id.tv_MediaStatistics2);
		cb_isOpenFEC.setChecked(true);
		//默认开启FEC
		MediaEngine.GetInstance().ME_EnableFec(true);

		//统计丢包率，时延相关
		cb_MediaStatistics = (CheckBox) mActivity.findViewById(R.id.cb_MediaStatistics);
		cb_MediaStatistics2 = (CheckBox) mActivity.findViewById(R.id.cb_MediaStatistics2);
		//无花屏模式设置
		cb_FlowerScreen = (CheckBox) mActivity.findViewById(R.id.cb_FlowerScreen);
		TextView tv_MediaStatistics = (TextView) mActivity.findViewById(R.id.tv_MediaStatistics);
		//取得默认配置
		boolean mediastatistics = prefs.getBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, false);
		cb_MediaStatistics.setChecked(mediastatistics);

		//取得无花屏模式默认值  默认为不开启
		boolean flowerScreen = prefs.getBoolean(GlobalConstant.SEETING_OTHER_FLOWER_SCREEN, false);
		cb_FlowerScreen.setChecked(flowerScreen);
		MediaEngine.GetInstance().ME_SetVideoNoBadPicture(flowerScreen);
		Log.e("=====AA======无花屏","" + flowerScreen);

		//取得默认配置
		boolean mediastatistics2 = prefs.getBoolean("isStartPMD", true);
		cb_MediaStatistics2.setChecked(mediastatistics2);

		//配置RTP分包大小
		btn_rtpSize = (Button) mActivity.findViewById(R.id.btn_rtpSize);
		btn_rtpSize.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_rtpSize = (EditText) mActivity.findViewById(R.id.et_rtpSize);
		et_rtpSize.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//获取分包大小
		int rtpPayloadSize = MediaEngine.GetInstance().ME_GetRtpPayloadSize();
		et_rtpSize.setText(rtpPayloadSize + "");

		btn_rtpSize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String rtpSizes = et_rtpSize.getText().toString();
				int rtpSize = Integer.valueOf(rtpSizes);
				if (rtpSize > 1500) {
					rtpSize = 1500;
					ToastUtils.showToast(mActivity, "配置不能大于1500");
				} else if (rtpSize < 400) {
					rtpSize = 400;
					ToastUtils.showToast(mActivity, "配置不能小于400");
				}
				MediaEngine.GetInstance().ME_SetRtpPayloadSize(rtpSize);
				et_rtpSize.setText(String.valueOf(rtpSize));
				et_rtpSize.clearFocus();
				ToastUtils.showToast(mActivity,"保存成功");
			}
		});

		//语音发送增益相关
		rb_voiceSend = (RadioGroup) mActivity.findViewById(R.id.rb_voiceSend);
		rb_voiceSend_0 = (RadioButton) mActivity.findViewById(R.id.rb_voiceSend_0);
		rb_voiceSend_0_5 = (RadioButton) mActivity.findViewById(R.id.rb_voiceSend_0_5);
		rb_voiceSend_1 = (RadioButton) mActivity.findViewById(R.id.rb_voiceSend_1);
		rb_voiceSend_2 = (RadioButton) mActivity.findViewById(R.id.rb_voiceSend_2);

		rb_voiceSend_0.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceSend_0_5.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceSend_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceSend_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//语音接收增益相关
		rb_voiceReceive = (RadioGroup) mActivity.findViewById(R.id.rb_voiceReceive);
		rb_voiceReceive_0 = (RadioButton) mActivity.findViewById(R.id.rb_voiceReceive_0);
		rb_voiceReceive_0_5 = (RadioButton) mActivity.findViewById(R.id.rb_voiceReceive_0_5);
		rb_voiceReceive_1 = (RadioButton) mActivity.findViewById(R.id.rb_voiceReceive_1);
		rb_voiceReceive_2 = (RadioButton) mActivity.findViewById(R.id.rb_voiceReceive_2);

		rb_voiceReceive_0.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceReceive_0_5.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceReceive_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_voiceReceive_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//回音消除相关
		rg_modular = (RadioGroup) mActivity.findViewById(R.id.rg_modular);
		rg_noiseReduction = (RadioGroup) mActivity.findViewById(R.id.rg_noiseReduction);
		rg_grade = (RadioGroup) mActivity.findViewById(R.id.rg_grade);
		rb_modular_1 = (RadioButton) mActivity.findViewById(R.id.rb_modular_1);
		rb_modular_2 = (RadioButton) mActivity.findViewById(R.id.rb_modular_2);
		rb_modular_3 = (RadioButton) mActivity.findViewById(R.id.rb_modular_3);
		rb_noiseReduction_1 = (RadioButton) mActivity.findViewById(R.id.rg_noiseReduction_1);
		rb_noiseReduction_2 = (RadioButton) mActivity.findViewById(R.id.rg_noiseReduction_2);
		rb_grade_1 = (RadioButton) mActivity.findViewById(R.id.rg_grade_1);
		rb_grade_2 = (RadioButton) mActivity.findViewById(rg_grade_2);
		rb_grade_3 = (RadioButton) mActivity.findViewById(rg_grade_3);
		btn_Preservation = (Button) mActivity.findViewById(R.id.btn_Preservation);

		rb_modular_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_modular_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_modular_3.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_noiseReduction_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_noiseReduction_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_grade_1.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_grade_2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		rb_grade_3.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		btn_Preservation.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));

		//获取回音消除模块默认值
		String modular = prefs.getString(GlobalConstant.SEETING_MODULAR, GlobalConstant.MODULAR_WEBRTC);
		switch (modular) {
			case GlobalConstant.MODULAR_SPEEX:
				rb_modular_1.setChecked(true);
				rb_modular_2.setChecked(false);
				rb_modular_3.setChecked(false);
				break;

			case GlobalConstant.MODULAR_SIMPLE:
				rb_modular_2.setChecked(true);
				rb_modular_1.setChecked(false);
				rb_modular_3.setChecked(false);
				break;

			case GlobalConstant.MODULAR_WEBRTC:
				rb_modular_3.setChecked(true);
				rb_modular_2.setChecked(false);
				rb_modular_1.setChecked(false);
				break;
		}

		//获取回音消除降噪默认值
		boolean noiseReduction = prefs.getBoolean(GlobalConstant.SEETING_NOISEREDUCTION, GlobalConstant.NOISEREDUCTION_FALSE);
		if (noiseReduction) {
			rb_noiseReduction_1.setChecked(true);
			rb_noiseReduction_2.setChecked(false);

		} else {
			rb_noiseReduction_2.setChecked(true);
			rb_noiseReduction_1.setChecked(false);

		}

		//获取回音消除等级默认值
		String grade = prefs.getString(GlobalConstant.SEETING_GRADE, GlobalConstant.GRADE_1);
		switch (grade) {
			case GlobalConstant.GRADE_1:
				rb_grade_1.setChecked(true);
				rb_grade_2.setChecked(false);
				rb_grade_3.setChecked(false);
				break;

			case GlobalConstant.GRADE_2:
				rb_grade_1.setChecked(false);
				rb_grade_2.setChecked(true);
				rb_grade_3.setChecked(false);
				break;

			case GlobalConstant.GRADE_3:
				rb_grade_1.setChecked(false);
				rb_grade_2.setChecked(false);
				rb_grade_3.setChecked(true);
				break;
		}

		//设置语音发送增益默认选中值
		String voiceSendGain = prefs.getString("voiceSendGain", "99");
		if (voiceSendGain.equals("99")) {
			rb_voiceSend_1.setChecked(true);
		} else {
			switch (voiceSendGain) {
				case "0":
					rb_voiceSend_0.setChecked(true);
					break;

				case "0.5":
					rb_voiceSend_0_5.setChecked(true);
					break;

				case "1":
					rb_voiceSend_1.setChecked(true);
					break;

				case "2":
					rb_voiceSend_2.setChecked(true);
					break;
			}
		}
		//设置语音接收增益默认选中值
		String voiceReceiveGain = prefs.getString("voiceReceiveGain", "99");
		if (voiceReceiveGain.equals("99")) {
			rb_voiceReceive_1.setChecked(true);
		} else {
			switch (voiceReceiveGain) {
				case "0":
					rb_voiceReceive_0.setChecked(true);
					break;
				case "0.5":
					rb_voiceReceive_0_5.setChecked(true);
					break;
				case "1":
					rb_voiceReceive_1.setChecked(true);
					break;

				case "2":
					rb_voiceReceive_2.setChecked(true);
					break;
			}
		}

		//配置回音消除保存监听
		btn_Preservation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				String modular = prefs.getString(GlobalConstant.SEETING_MODULAR, GlobalConstant.MODULAR_WEBRTC);
				boolean noisereduction = prefs.getBoolean(GlobalConstant.SEETING_NOISEREDUCTION, GlobalConstant.NOISEREDUCTION_FALSE);
				String grade = prefs.getString(GlobalConstant.SEETING_GRADE, GlobalConstant.GRADE_1);
				MediaEngine.GetInstance().ME_SetEcOptions(Integer.valueOf(modular),100,noisereduction,Integer.valueOf(grade));
				Log.e("测试回音消除","modular : " + modular);
				Log.e("测试回音消除","noisereduction : " + noisereduction);
				Log.e("测试回音消除","grade : " + grade);
				ToastUtils.showToast(mActivity, "配置完成");

			}
		});
		//回音消除模块选中监听事件
		rg_modular.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				Editor modularEdit = prefs.edit();

				switch (id){
					case R.id.rb_modular_1:
						modularEdit.putString(GlobalConstant.SEETING_MODULAR,GlobalConstant.MODULAR_SPEEX);
						break;
					case R.id.rb_modular_2:
						modularEdit.putString(GlobalConstant.SEETING_MODULAR,GlobalConstant.MODULAR_SIMPLE);
						break;
					case R.id.rb_modular_3:
						modularEdit.putString(GlobalConstant.SEETING_MODULAR,GlobalConstant.MODULAR_WEBRTC);
						break;
				}
				modularEdit.apply();
			}
		});
		//回音消除降噪选中监听事件
		rg_noiseReduction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {

				Editor noiseReductionEdit = prefs.edit();
				switch (id){

					case R.id.rg_noiseReduction_1:
						noiseReductionEdit.putBoolean(GlobalConstant.SEETING_NOISEREDUCTION,GlobalConstant.NOISEREDUCTION_TRUE);
						break;
					case R.id.rg_noiseReduction_2:
						noiseReductionEdit.putBoolean(GlobalConstant.SEETING_NOISEREDUCTION,GlobalConstant.NOISEREDUCTION_FALSE);
						break;
				}
				noiseReductionEdit.apply();
			}
		});
		//回音消除等级选中监听事件
		rg_grade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				Editor gradeEdit = prefs.edit();
				switch (id){
					case R.id.rg_grade_1:
						gradeEdit.putString(GlobalConstant.SEETING_GRADE,GlobalConstant.GRADE_1);
						break;
					case R.id.rg_grade_2:
						gradeEdit.putString(GlobalConstant.SEETING_GRADE,GlobalConstant.GRADE_2);
						break;
					case R.id.rg_grade_3:
						gradeEdit.putString(GlobalConstant.SEETING_GRADE,GlobalConstant.GRADE_3);
						break;

				}
				gradeEdit.apply();
			}
		});

		//语音发送增益选中监听事件
		rb_voiceSend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {

				Editor editVoiceSend = prefs.edit();

				switch (i) {
					case R.id.rb_voiceSend_0:
						editVoiceSend.putString("voiceSendGain", "0");
						break;

					case R.id.rb_voiceSend_0_5:
						editVoiceSend.putString("voiceSendGain", "0.5");
						break;

					case R.id.rb_voiceSend_1:
						editVoiceSend.putString("voiceSendGain", "1");
						break;

					case R.id.rb_voiceSend_2:
						editVoiceSend.putString("voiceSendGain", "2");
						break;

				}
				editVoiceSend.apply();
			}
		});
		//语音接收增益选中监听事件
		rb_voiceReceive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				Editor editVoiceReceive = prefs.edit();
				switch (i) {
					case R.id.rb_voiceReceive_0:
						editVoiceReceive.putString("voiceReceiveGain", "0");
						break;

					case R.id.rb_voiceReceive_0_5:
						editVoiceReceive.putString("voiceReceiveGain", "0.5");
						break;

					case R.id.rb_voiceReceive_1:
						editVoiceReceive.putString("voiceReceiveGain", "1");
						break;

					case R.id.rb_voiceReceive_2:
						editVoiceReceive.putString("voiceReceiveGain", "2");
						break;
				}
				editVoiceReceive.apply();
			}
		});


		//服务器端口
		et_settingOther_port = (EditText) mActivity.findViewById(R.id.et_settingOther_port);
		et_settingOther_port.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//得到服务器端口    
		String servicePort = prefs.getString("settingOtherport", "80");
		Log.e("============", "服务器返回端口号码：" + servicePort);
		if (servicePort.equals("") || servicePort == null) {
			et_settingOther_port.setText("8080");
		} else {
			et_settingOther_port.setText(servicePort);
		}


		btn_settingOther_port = (Button) mActivity.findViewById(R.id.btn_settingOther_port);
		btn_settingOther_port.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		btn_settingOther_port.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				prefs.edit().putString("settingOtherport", et_settingOther_port.getText().toString().trim()).apply();
				et_settingOther_port.clearFocus();
				ToastUtils.showToast(mActivity, "保存成功");
			}
		});


		// 设置字体
		tv_encrypt.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_stun.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_startup.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_setfir.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_isOpenFEC.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_MediaStatistics.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_MediaStatistics2.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		tv_FlowerScreen.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		// 取得默认数据
		boolean encryption = prefs.getBoolean(GlobalConstant.SP_START_LOG, false);
		boolean stun = prefs.getBoolean(GlobalConstant.SP_STUN, false);
		boolean autostart = prefs.getBoolean(GlobalConstant.SP_AUTO_START, true);
		boolean isOptimizes = prefs.getBoolean(GlobalConstant.ACTION_MSG_SENDVIDEO, false);
		boolean callAnswer = prefs.getBoolean(GlobalConstant.ACTION_CALLING_ISANSWER, false);

		cb_callAnswer.setChecked(callAnswer);
		cb_encrypt.setChecked(encryption);
		cb_stun.setChecked(stun);
		cb_startup.setChecked(autostart);
		cb_other.setChecked(isOptimizes);
		cb_other.setChecked(isOptimizes);
		// 设置侦听
		cb_encrypt.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				ToastUtils.showToast(mActivity, "app重启后生效");
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.SP_START_LOG, arg1);
				editor.apply();
			}
		});
		cb_isOpenFEC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@SuppressLint("LongLogTag")
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean boo) {

				MediaEngine.GetInstance().ME_EnableFec(boo);
				if (boo) {
					Log.e("=========sssssssssssssss", "FEC  Open");
				} else {
					Log.e("=========sssssssssssssss", "FEC  close");
				}
			}
		});


		cb_stun.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				ToastUtils.showToast(mActivity, "NAT");
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.SP_STUN, arg1);
				editor.apply();
			}
		});
		cb_startup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				ToastUtils.showToast(mActivity, "开机启动");
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.SP_AUTO_START, arg1);
				editor.apply();
			}
		});
		cb_MediaStatistics.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				//ToastUtils.showToast(mActivity, "开启统计丢包率,时延");
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.SEETING_MEDIASTATISTICS, arg1);
				editor.apply();
//				Log.e("==========AA===========","点击  mediastatistics : " + arg1);
//				Intent intent = new Intent(GlobalConstant.REMOVE_OR_ADD_MAPVIEW);
//				intent.putExtra("AddOrRemove",arg1);
//				mActivity.sendBroadcast(intent);
			}
		});

		//设置无花屏模式
		cb_FlowerScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub

				Editor edit = prefs.edit();
				edit.putBoolean(GlobalConstant.SEETING_OTHER_FLOWER_SCREEN,arg1);
				edit.apply();


				Log.e("=====AA======无花屏","" + arg1);

				MediaEngine.GetInstance().ME_SetVideoNoBadPicture(arg1);

				}
		});
		//开启显示名称或号码
		cb_MediaStatistics2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean("isStartPMD", arg1);
				editor.apply();
				}
		});


		cb_other.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				if (arg1) {
					editor.putBoolean(GlobalConstant.ACTION_MSG_SENDVIDEO, arg1);
				} else {
					editor.putBoolean(GlobalConstant.ACTION_MSG_SENDVIDEO, arg1);
				}
				editor.apply();
			}
		});
		//自动接听
		cb_callAnswer.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Editor editor = prefs.edit();
				editor.putBoolean(GlobalConstant.ACTION_CALLING_ISANSWER, arg1);
				editor.apply();
			}
		});



        //设置音频编解码
        TextView et_voice_priority =  (TextView) mActivity.findViewById(R.id.et_voice_priority);
		et_voice_priority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
        Button btn_settingOther_voicePriority = (Button) mActivity.findViewById(R.id.btn_settingOther_voicePriority);
		btn_settingOther_voicePriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//点击保存
		btn_settingOther_voicePriority.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int minimumLevle = 0;
				int maxLevle = 255;
				audioCpdecs = MainActivity.voiceList;
				for (String voiceName : audioCpdecs){
					int levle = maxLevle - minimumLevle;
					MediaEngine.GetInstance().ME_SetAudioCodecPriority(voiceName,(short) levle);
					minimumLevle+=10;//(第一个是最大优先级 之后每次优先级小于上一个10优先级)
					Log.e("SettingOtherFragment"," levle:" + (short) levle + "\r\n" + "voiceName : " + voiceName);
				}
				//保存本地
				Gson gson = new Gson();
				String data = gson.toJson(MainActivity.voiceList);
				prefs.edit().putString("voiceList", data).apply();


				ToastUtils.showToast(mActivity,"设置成功");
			}
		});

		//设置音频模式
		String audioMode = prefs.getString(GlobalConstant.SP_AUDIOMANAGER_GETMODE,mActivity.getResources().getString(R.string.text_mode_in_communication));
		et_audioMode = (TextView) mActivity.findViewById(R.id.et_audioMode);
		et_audioMode.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		et_audioMode.setText("当前音频模式:" + audioMode);

		et_audioMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				initShowDialog();
			}
		});
		Button btn_settingOther_AudioMode = (Button) mActivity.findViewById(R.id.btn_settingOther_AudioMode);
		btn_settingOther_AudioMode.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
		//点击保存
		btn_settingOther_AudioMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
			ToastUtils.showToast(mActivity,"保存成功");
			}
		});


		//列表展示
        et_voice_priority.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载音频编解码列表
                initVoicePriority();
            }
        });
	}
    List<String> audioCpdecs;
	private ItemTouchHelper mItemTouchHelper;
    private void initVoicePriority() {

        audioCpdecs = MainActivity.voiceList;

        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.voice_codecpriority, null);

		swipeRefreshLayoutVoice = (SwipeRecyclerView) inflate.findViewById(R.id.list_voiceCodecPriority);

		swipeRefreshLayoutVoice.setLayoutManager(new LinearLayoutManager(this));
		swipeRefreshLayoutVoice.addItemDecoration(new DividerItemDecoration(mActivity, LinearLayoutManager.VERTICAL));
		swipRecyclerViewAdapter = new SwipRecyclerViewAdapter(audioCpdecs, mActivity);
		swipeRefreshLayoutVoice.setAdapter(swipRecyclerViewAdapter);

		swipeRefreshLayoutVoice.addOnItemTouchListener(new OnRecyclerItemClickListener(swipeRefreshLayoutVoice) {
			@Override
			public void onItemClick(RecyclerView.ViewHolder vh) {
			}

			@Override
			public void onItemLongClick(RecyclerView.ViewHolder vh) {
				//判断被拖拽的是否是前两个，如果不是则执行拖拽
//				if (vh.getLayoutPosition() != 0 && vh.getLayoutPosition() != 1) {
					mItemTouchHelper.startDrag(vh);
					TipHelper.Vibrate(mActivity, 70);
				}
//			}
		});

		mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

			/**
			 * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
			 * @param recyclerView
			 * @param viewHolder
			 * @return
			 */
			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
					final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
							ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
					final int swipeFlags = 0;
					return makeMovementFlags(dragFlags, swipeFlags);
				} else {
					final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
					final int swipeFlags = 0;
//                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
					return makeMovementFlags(dragFlags, swipeFlags);
				}
			}

			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				//得到当拖拽的viewHolder的Position
				int fromPosition = viewHolder.getAdapterPosition();
				//拿到当前拖拽到的item的viewHolder
				int toPosition = target.getAdapterPosition();
				if (fromPosition < toPosition) {
					for (int i = fromPosition; i < toPosition; i++) {
						Collections.swap(audioCpdecs, i, i + 1);
					}
				} else {
					for (int i = fromPosition; i > toPosition; i--) {
						Collections.swap(audioCpdecs, i, i - 1);
					}
				}
				swipRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);
				return true;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

			}

			/**
			 * 重写拖拽可用
			 * @return
			 */
			@Override
			public boolean isLongPressDragEnabled() {
				return false;
			}

			/**
			 * 长按选中Item的时候开始调用
			 *
			 * @param viewHolder
			 * @param actionState
			 */
			@Override
			public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
				if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
					viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
				}
				super.onSelectedChanged(viewHolder, actionState);
			}

			/**
			 * 手指松开的时候还原
			 * @param recyclerView
			 * @param viewHolder
			 */
			@Override
			public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				super.clearView(recyclerView, viewHolder);
				viewHolder.itemView.setBackgroundColor(0);
				if (swipRecyclerViewAdapter != null){
					swipRecyclerViewAdapter.notifyDataSetChanged();
				}
			}
		});

		mItemTouchHelper.attachToRecyclerView(swipeRefreshLayoutVoice);

//		swipeRefreshLayoutVoice.setRightClickListener(new SwipeRecyclerView.OnRightClickListener() {
//			@Override
//			public void onRightClick(int position, String id) {
//				audioCpdecs.remove(position);
//				swipRecyclerViewAdapter.notifyDataSetChanged();
//				Toast.makeText(mActivity, " position = " + position, Toast.LENGTH_SHORT).show();
//			}
//		});

		dialogVoice = new AlertDialog.Builder(mActivity);
		dialogVoice.setView(inflate);
		dialogVoice.show();
	}
	//加载音频模式dialog
	private void initShowDialog() {

		String audioMode = prefs.getString(GlobalConstant.SP_AUDIOMANAGER_GETMODE,mActivity.getResources().getString(R.string.text_mode_in_communication));
		LayoutInflater layoutInflater = mActivity.getLayoutInflater();
		View inflate = layoutInflater.inflate(R.layout.seeting_audiomanager_mode,null);
		popupWindow = new PopupWindow(inflate, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
		// 设置PopupWindow的背景
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		// 设置PopupWindow是否能响应外部点击事件
		popupWindow.setOutsideTouchable(true);
		// 设置PopupWindow是否能响应点击事件
		popupWindow.setTouchable(true);

		RadioGroup rg_AudioManger = (RadioGroup) inflate.findViewById(R.id.rg_AudioManger);
		rb_normal = (RadioButton) inflate.findViewById(R.id.rb_normal);
		rb_in_call = (RadioButton) inflate.findViewById(R.id.rb_in_call);
		rb_in_communication = (RadioButton) inflate.findViewById(R.id.rb_in_communication);

		if (audioMode.equals(mActivity.getResources().getString(R.string.text_mode_normal))){
			if (rb_normal != null) {
				rb_normal.setChecked(true);
			}
		}else if (audioMode.equals(mActivity.getResources().getString(R.string.text_mode_in_call))){
			if (rb_in_call != null) {
				rb_in_call.setChecked(true);
			}
		}else if (audioMode.equals(mActivity.getResources().getString(R.string.text_mode_in_communication))){
			if (rb_in_communication  != null) {
				rb_in_communication.setChecked(true);
			}
		}

		rg_AudioManger.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				String content = "";
				switch (id){
					case R.id.rb_normal:
						content =  rb_normal.getText().toString();
						break;

					case R.id.rb_in_call:
						content =  rb_in_call.getText().toString();
						break;

					case R.id.rb_in_communication:
						content =  rb_in_communication.getText().toString();
						break;
				}


				if (content.equals(mActivity.getResources().getString(R.string.text_mode_normal))){
					ptt_3g_PadApplication.setGlobalAudioManagerMode(AudioManager.MODE_NORMAL);

				}else if (content.equals(mActivity.getResources().getString(R.string.text_mode_in_call))){
					ptt_3g_PadApplication.setGlobalAudioManagerMode(AudioManager.MODE_IN_CALL);

				}else if (content.equals(mActivity.getResources().getString(R.string.text_mode_in_communication))){
					ptt_3g_PadApplication.setGlobalAudioManagerMode(AudioManager.MODE_IN_COMMUNICATION);

				}
				if (et_audioMode != null){
					et_audioMode.setText("当前音频模式:"+content);
				}
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_AUDIOMANAGER_GETMODE,content);
				editor.apply();
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});

		// 显示PopupWindow，其中：
		// 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
		popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);



	}

}
