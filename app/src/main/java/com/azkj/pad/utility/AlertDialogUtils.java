package com.azkj.pad.utility;

import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.azkj.pad.activity.CustomDialog;
import com.azkj.pad.activity.MainActivity;
import com.azkj.pad.activity.WaitingDialog;
//import com.azkj.pad.activity.MainActivity.ShowType;
import com.azkj.pad.model.CallingInfo;
import com.azkj.pad.model.CallingRecords;
import com.azkj.pad.service.CallingManager;
import com.juphoon.lemon.MtcCall;
import com.juphoon.lemon.ui.MtcDelegate;
import com.azkj.pad.activity.R;

public class AlertDialogUtils {
	private Context mContext;
	private int type;//表示呼叫类型
	

	public AlertDialogUtils(Context context,int type){
		this.mContext = context;
		this.type = type;
	}
	
	// 提示通话是否继续发起
		public Dialog showIfContinue(final CallingInfo oldCallInfo) {
		
			String message = "";
			String title = "";
			String positive = null;
			title = mContext.getString(R.string.info_ifContinue);
			positive = mContext.getString(R.string.info_continue);
			/*String neutral = mContext.getString(R.string.btn_calling_answer);
			if (callingInfo.getCalltype() == GlobalConstant.CALL_TYPE_INTERCOM) {
				message = mContext.getString(R.string.info_intercom_new_incoming1);
				title = mContext.getString(R.string.info_intercom_new_incoming)
						+ callingInfo.getRemoteName();
			} else {
				message = mContext.getString(R.string.info_intercom_new_calling1);
				title = mContext.getString(R.string.info_intercom_new_calling)
						+ callingInfo.getRemoteName();
			}
			if (callingInfo.getMediaType() == GlobalConstant.CONFERENCE_MEDIATYPE_VIDEO) {
				positive = mContext.getString(R.string.btn_calling_videoanswer);
				neutral = mContext.getString(R.string.btn_calling_voiceanswer);
			}*/
			CustomDialog.Builder builder = new CustomDialog.Builder(
					mContext);
			builder.setMessage(message);
			builder.setTitle(title);
			// 继续发起
//			if (positive != null) {
				builder.setPositiveButton(positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// 终止原来通话
								Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_CALLHANGUP);
								callIntent.putExtra(GlobalConstant.KEY_CALL_SESSIONID, oldCallInfo.getDwSessId());
								mContext.sendBroadcast(callIntent);
								// 移除通话数据
								CallingManager.getInstance().removeCallingInfo(oldCallInfo.getDwSessId());
								CallingManager.getInstance().removeCallingDialog(((WaitingDialog)dialog).getDwSessId());
								
								// 关闭页面
								dialog.dismiss();
								dialog = null;
								Log.e("!!!!!!!!!!!!!!truetrue!!!!!!!!!!!!!!!!!!!!", "!!!!!!!!!!!!!!!!!!!!!");
								switch(type)
								{
									case GlobalConstant.CALL_TYPE_INTERCOM:
										//发生广播申请话权
										Intent requestCallIntent = new Intent(GlobalConstant.ACTION_CALLING_INTERCOM_REQ);
										mContext.sendBroadcast(requestCallIntent);
										break;
									case GlobalConstant.CALL_TYPE_CALLING:
										
										break;
									case GlobalConstant.CALL_TYPE_MEETING:
										
										break;
									case GlobalConstant.CALL_TYPE_MONITOR:
										
										break;
									case GlobalConstant.CALL_TYPE_TEMPINTERCOM:
										Intent reqTempIntercom = new Intent(GlobalConstant.ACTION_CALLING_TEMPINTERCOM_REQ);
										mContext.sendBroadcast(reqTempIntercom);
										break;
									default:
											break;
								}
				
							}
						});
//			}		
			// 终止对讲
			builder.setNegativeButton(mContext.getString(R.string.info_notcontinue),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 关闭提示框
 							CallingManager.getInstance().removeCallingDialog(((WaitingDialog)dialog).getDwSessId());
							dialog.dismiss();
							dialog = null;
						}
					});
			final WaitingDialog informing = builder.create();
			informing.setCancelable(false);
			informing.setDwSessId(oldCallInfo.getDwSessId());
			informing.show();
			CallingManager.getInstance().addCallingDialog(informing);

			//5秒无操作自动关闭
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					MtcDelegate.log("lizhiwei 超时挂断");
					if ((informing != null) && (informing.isShowing())) {					
						// 关闭提示框
						CallingManager.getInstance().removeCallingDialog(informing.getDwSessId());
						informing.dismiss();
					}
				}
			}, 5000);
			return informing;
		}
}
