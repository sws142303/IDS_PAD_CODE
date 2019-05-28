package com.azkj.pad.activity;

import com.azkj.pad.activity.CustomDialog.Builder;
import com.azkj.pad.utility.CommonMethod;
import com.juphoon.lemon.MtcCliConstants;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginRepeatDialog extends Dialog{
	
	
		public LoginRepeatDialog(Context context) {
			super(context);
		}

		public LoginRepeatDialog(Context context, int theme) {
			super(context, theme);
		}

		public static class Builder {
			
			private Context context;
			private String title;
			private String message;
			private String btnPositiveText;
			private String btnNeutralText;
			private String btnNegativeText;
			private View contentView;
			private OnClickListener positiveButtonClickListener;
			private OnClickListener neutralbuttonclicklistener;
			private OnClickListener negativeButtonClickListener;

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

			public Builder setTitle(String title) {
				this.title = title;
				return this;
			}

			public Builder setTitle(int title) {
				this.title = (String) context.getText(title);
				return this;
			}

			public Builder setContentView(View v) {
				this.contentView = v;
				return this;
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

		/*	public Builder setNeutralButton(int btnNeutralText, DialogInterface.OnClickListener listener) {
				this.btnNeutralText = (String) context.getText(btnNeutralText);
				this.neutralbuttonclicklistener = listener;
				return this;
			}

			public Builder setNeutralButton(String btnNeutralText, DialogInterface.OnClickListener listener) {
				this.btnNeutralText = btnNeutralText;
				this.neutralbuttonclicklistener = listener;
				return this;
			}

			public Builder setNegativeButton(int btnNegativeText, DialogInterface.OnClickListener listener) {
				this.btnNegativeText = (String) context.getText(btnNegativeText);
				this.negativeButtonClickListener = listener;
				return this;
			}

			public Builder setNegativeButton(String btnNegativeText, DialogInterface.OnClickListener listener) {
				this.btnNegativeText = btnNegativeText;
				this.negativeButtonClickListener = listener;
				return this;
			}*/

			// 创建对话框
			public WaitingDialog create() {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// 实例化对话框
				final WaitingDialog dialog = new WaitingDialog(context, R.style.Dialog);
				View layout = inflater.inflate(R.layout.activity_loginrepeatdialog, null);
				dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				layout.setBackgroundColor(Color.TRANSPARENT);
				// 设置对话框标题
				TextView tv_title = ((TextView) layout.findViewById(R.id.tv_title_repeat));
				
				tv_title.setTypeface(CommonMethod.getTypeface(layout.getContext()));
				
				// 设置确认按钮
			
					Button btnPositive = ((Button) layout.findViewById(R.id.btn_queding_repeatdialog));
					/*btnPositive.setTypeface(CommonMethod.getTypeface(layout.getContext()));*/
					if (positiveButtonClickListener != null) {
						btnPositive.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								if(dialog != null)
								dialog.dismiss();
							}
						});
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
