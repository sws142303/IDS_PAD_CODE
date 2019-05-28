package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azkj.pad.utility.CommonMethod;
import com.juphoon.lemon.MtcCliConstants;

/*自定义对话框*/
@SuppressLint("Instantiatable")
public class CustomDialog extends Dialog {

	// 回话编号
	private int dwSessId = MtcCliConstants.INVALIDID;
	
	public int getDwSessId() {
		return dwSessId;
	}

	public void setDwSessId(int dwSessId) {
		this.dwSessId = dwSessId;
	}
	
	public CustomDialog(Context context) {
		super(context);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}

	public CustomDialog(Context context, int theme) {
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
			View layout = inflater.inflate(R.layout.activity_custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout.setBackgroundColor(Color.TRANSPARENT);
			// 设置对话框标题
			TextView tv_title = ((TextView) layout.findViewById(R.id.tv_title));
			tv_title.setText(title);
			tv_title.setTypeface(CommonMethod.getTypeface(layout.getContext()));
			
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
			if (btnNeutralText != null) {
				Button btnNeutral = ((Button) layout.findViewById(R.id.btnNeutral));
				btnNeutral.setText(btnNeutralText);
				btnNeutral.setTypeface(CommonMethod.getTypeface(layout.getContext()));
				if (neutralbuttonclicklistener != null) {
					btnNeutral.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							neutralbuttonclicklistener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						}
					});
				}
			}
			else {
				// 未设置显示文本则隐藏
				layout.findViewById(R.id.btnNeutral).setVisibility(View.GONE);
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
