package com.azkj.pad.activity;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.MultiRadioGroup;
import com.azkj.pad.utility.MultiRadioGroup.OnCheckedChangeListener;
import com.azkj.pad.activity.R;

/*视频监控参数配置对话框*/
@SuppressLint("Instantiatable")
public class MonitorDialog extends Dialog {

	public MonitorDialog(Context context) {
		super(context);
	}

	public MonitorDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {

		private Context context;
		private String title;

	    private RadioGroup rg_camera, rg_answer, rg_monitor;
	    private MultiRadioGroup rg_size;
	    private TextView tv_title, tv_camera, tv_answer, tv_monitor, tv_size;
	    private RadioButton rb_camera_front, rb_camera_rear, rb_answer_auto, rb_answer_manu, rb_monitor_av, rb_monitor_video, rb_size_720p, rb_size_1080p, rb_size_cif, rb_size_qcif, rb_size_d1;
	    
		private String btnPositiveText;
		private String btnNeutralText;
		private String btnNegativeText;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener neutralbuttonclicklistener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public String getResult() {
			String result = "";
			// 摄像头
			if (rg_camera.getCheckedRadioButtonId() == R.id.rb_camera_front){
				result += "1";
			}
			else {
				result += "2";
			}
			// 视频尺寸
			if (rg_size.getCheckedRadioButtonId() == rb_size_1080p.getId()){
				result += "5";
			}
			else if (rg_size.getCheckedRadioButtonId() == rb_size_cif.getId()){
				result += "2";
			}
			else if (rg_size.getCheckedRadioButtonId() == rb_size_qcif.getId()){
				result += "1";
			}
			else if (rg_size.getCheckedRadioButtonId() == rb_size_d1.getId()){
				result += "3";
			}
			else {
				result += "4";
			}
			// 接听方式
			if (rg_answer.getCheckedRadioButtonId() == R.id.rb_answer_auto){
				result += "1";
			}
			else {
				result += "2";
			}
			/*if (rg_monitor.getCheckedRadioButtonId() == R.id.rb_monitor_av){
				result += "1";
			}
			else {
				result += "2";
			}*/
			return result;
		}

		public void setResult(String result) {
			if ((result == null)
				|| (result.trim().length() <= 0)){
				return;
			}
			String camera = result.trim().substring(0, 1);
			String answer = result.trim().substring(result.trim().length() - 1, result.trim().length());
			String size = result.trim().substring(1, result.trim().length() - 1);
			if (camera.equals("2")){
				rg_camera.check(R.id.rb_camera_rear);
			}
			else {
				rg_camera.check(R.id.rb_camera_front);
			}
			if (answer.equals("2")){
				rg_answer.check(R.id.rb_answer_manu);
			}
			else {
				rg_answer.check(R.id.rb_answer_auto);
			}
			if (size.toLowerCase(Locale.getDefault()).equals("5")){
				rg_size.check(R.id.rb_size_1080p);
			}
			else if (size.toLowerCase(Locale.getDefault()).equals("2")){
				rg_size.check(R.id.rb_size_cif);
			}
			else if (size.toLowerCase(Locale.getDefault()).equals("1")){
				rg_size.check(R.id.rb_size_qcif);
			}
			else if (size.toLowerCase(Locale.getDefault()).equals("3")){
				rg_size.check(R.id.rb_size_d1);
			}
			else {
				rg_size.check(R.id.rb_size_720p);
			}
		}

		public Builder setPositiveButton(int btnPositiveText, OnClickListener listener) {
			this.btnPositiveText = (String) context.getText(btnPositiveText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String btnPositiveText, OnClickListener listener) {
			this.btnPositiveText = btnPositiveText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int btnNeutralText, OnClickListener listener) {
			this.btnNeutralText = (String) context.getText(btnNeutralText);
			this.neutralbuttonclicklistener = listener;
			return this;
		}

		public Builder setNeutralButton(String btnNeutralText, OnClickListener listener) {
			this.btnNeutralText = btnNeutralText;
			this.neutralbuttonclicklistener = listener;
			return this;
		}

		public Builder setNegativeButton(int btnNegativeText, OnClickListener listener) {
			this.btnNegativeText = (String) context.getText(btnNegativeText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String btnNegativeText, OnClickListener listener) {
			this.btnNegativeText = btnNegativeText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		// 创建对话框
		public WaitingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// 实例化对话框
			final WaitingDialog dialog = new WaitingDialog(context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.activity_monitor_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout.setBackgroundColor(Color.TRANSPARENT);
			
			// 取得控件
			rg_camera = ((RadioGroup) layout.findViewById(R.id.rg_camera));
			rg_answer = ((RadioGroup) layout.findViewById(R.id.rg_answer));
			rg_monitor = ((RadioGroup) layout.findViewById(R.id.rg_monitor));
			rg_size = ((MultiRadioGroup) layout.findViewById(R.id.rg_size));
			
		    tv_title = ((TextView) layout.findViewById(R.id.tv_title));
		    tv_camera = ((TextView) layout.findViewById(R.id.tv_camera));
		    tv_answer = ((TextView) layout.findViewById(R.id.tv_answer));
		    tv_monitor = ((TextView) layout.findViewById(R.id.tv_monitor));
		    tv_size = ((TextView) layout.findViewById(R.id.tv_size));
			
		    rb_camera_front = ((RadioButton) layout.findViewById(R.id.rb_camera_front));
		    rb_camera_rear = ((RadioButton) layout.findViewById(R.id.rb_camera_rear));
		    rb_answer_auto = ((RadioButton) layout.findViewById(R.id.rb_answer_auto));
		    rb_answer_manu = ((RadioButton) layout.findViewById(R.id.rb_answer_manu));
		    rb_monitor_av = ((RadioButton) layout.findViewById(R.id.rb_monitor_av));
		    rb_monitor_video = ((RadioButton) layout.findViewById(R.id.rb_monitor_video));
		    rb_size_720p = ((RadioButton) layout.findViewById(R.id.rb_size_720p));
		    rb_size_1080p = ((RadioButton) layout.findViewById(R.id.rb_size_1080p));
		    rb_size_cif = ((RadioButton) layout.findViewById(R.id.rb_size_cif));
		    rb_size_qcif = ((RadioButton) layout.findViewById(R.id.rb_size_qcif));
		    rb_size_d1 = ((RadioButton) layout.findViewById(R.id.rb_size_d1));
		    
		    // 设置字体
			tv_title.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    tv_camera.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    tv_answer.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    tv_monitor.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    tv_size.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    
		    rb_camera_front.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_camera_rear.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_answer_auto.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_answer_manu.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_monitor_av.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_monitor_video.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_size_720p.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_size_1080p.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_size_cif.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_size_qcif.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    rb_size_d1.setTypeface(CommonMethod.getTypeface(layout.getContext()));
		    
		    // 设置默认值
			tv_title.setText(title);
			rg_camera.check(R.id.rb_camera_front);
			rg_answer.check(R.id.rb_answer_auto);
			rg_monitor.check(R.id.rb_monitor_av);
			rg_size.check(R.id.rb_size_720p);
			rg_size.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(MultiRadioGroup group, int checkedId) {
					// 此处必须调用设置编号
					rg_size.setCheckedId(checkedId);
				}});
			
			// 设置确认按钮
			if (btnPositiveText != null) {
				Button btnPositive = ((Button) layout.findViewById(R.id.btnPositive));
				btnPositive.setText(btnPositiveText);
				btnPositive.setTypeface(CommonMethod.getTypeface(layout.getContext()));
				if (positiveButtonClickListener != null) {
					btnPositive.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			}
			else {
				// 未设置显示文本则隐藏
				layout.findViewById(R.id.btnPositive).setVisibility(View.GONE);
			}
			// 设置中立按钮
			Button btnNeutral = ((Button) layout.findViewById(R.id.btnNeutral));
			if (btnNeutralText != null) {
				btnNeutral.setText(btnNeutralText);
				btnNeutral.setTypeface(CommonMethod.getTypeface(layout.getContext()));
			}
			if (neutralbuttonclicklistener != null) {
				btnNeutral.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						neutralbuttonclicklistener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
					}
				});
			}
			// 设置取消按钮
			if (btnNegativeText != null) {
				Button btnNegative = ((Button) layout.findViewById(R.id.btnNegative));
				btnNegative.setText(btnNegativeText);
				btnNegative.setTypeface(CommonMethod.getTypeface(layout.getContext()));
				if (negativeButtonClickListener != null) {
					btnNegative.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			}
			else {
				// 未设置显示文本则隐藏
				layout.findViewById(R.id.btnNegative).setVisibility(View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
