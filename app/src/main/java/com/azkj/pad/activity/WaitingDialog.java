package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azkj.pad.utility.CommonMethod;
import com.juphoon.lemon.MtcCliConstants;

/*等待对话框*/
@SuppressLint("Instantiatable")
public class WaitingDialog extends Dialog {

	// 回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	
	public int getDwSessId() {
		return dwSessId;
	}

	public void setDwSessId(int dwSessId) {
		this.dwSessId = dwSessId;
	}
	
	
	public WaitingDialog(Context context) {
		super(context);
	}

	public WaitingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String message;
		private View contentView;
		

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		// 创建对话框
		public WaitingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// 实例化对话框
			final WaitingDialog dialog = new WaitingDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.activity_waiting_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout.setBackgroundColor(Color.TRANSPARENT);
			// 设置等待图片
			ImageView img_loading = (ImageView)layout.findViewById(R.id.iv_loading);
			// 加载XML文件中定义的动画  
			RotateAnimation rotateAnimation = (RotateAnimation)AnimationUtils.loadAnimation(context, R.anim.waiting);  
			// 开始动画  
			img_loading.setAnimation(rotateAnimation); 

			// 设置内容消息
			if (message != null) {
				TextView tv_message = ((TextView) layout.findViewById(R.id.tv_message));
				tv_message.setText(message);
				tv_message.setTypeface(CommonMethod.getTypeface(layout.getContext()));
			}
			else if (contentView != null) {
				// 如果未设置消息
				// 添加内容视图到对话框体
				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
