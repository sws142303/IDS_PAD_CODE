package com.azkj.pad.utility;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.activity.R;

/*监控列表适配器*/
public class MonitorListAdapter extends ArrayAdapter<MonitorInfo>{

	public MonitorListAdapter(Activity activity, List<MonitorInfo> videousers, ListView listViews) {
		super(activity, 0, videousers);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_monitor_list, null);
		}
		MonitorInfo videoUser = getItem(position);
		// 取得控件
		ImageView head = (ImageView)rowView.findViewById(R.id.head);
		TextView userName = (TextView)rowView.findViewById(R.id.username);
		TextView userState = (TextView)rowView.findViewById(R.id.userstate);
		// 设置字体
		userName.setTypeface(CommonMethod.getTypeface(activity));
		userState.setTypeface(CommonMethod.getTypeface(activity));
		// 设置数值
		if (videoUser.getName().equals(videoUser.getNumber())){
			userName.setText(videoUser.getName());
		}
		else {
			userName.setText(videoUser.getName()+"("+videoUser.getNumber()+")");
		}

		/*switch(videoUser.getStatus()){
			case GlobalConstant.MONITOR_MEMBER_OUTGOING:
				// 4为外呼中
				// 0为默认，1为摄像头，2为手机
				switch(videoUser.getType()){
					case GlobalConstant.MONITOR_TYPE_CAMERA:
						head.setBackgroundResource(R.drawable.monitor_camera_talking);
						break;
					case GlobalConstant.MONITOR_TYPE_PHONE:
						head.setBackgroundResource(R.drawable.monitor_phone_talking);
						break;
					default:
						head.setBackgroundResource(R.drawable.monitor_default_talking);
						break;
				}
				userName.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("外呼中");
				break;
			case GlobalConstant.MONITOR_MEMBER_TAKING:
				// 7为通话中
				// 0为默认，1为摄像头，2为手机
				switch(videoUser.getType()){
					case GlobalConstant.MONITOR_TYPE_CAMERA:
						head.setBackgroundResource(R.drawable.monitor_camera_talking);
						break;
					case GlobalConstant.MONITOR_TYPE_PHONE:
						head.setBackgroundResource(R.drawable.monitor_phone_talking);
						break;
					default:
						head.setBackgroundResource(R.drawable.monitor_default_talking);
						break;
				}
				userName.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("在线");
				break;
			case GlobalConstant.MONITOR_MEMBER_RELEASE:
				// 11为释放
				// 0为默认，1为摄像头，2为手机
				switch(videoUser.getType()){
					case GlobalConstant.MONITOR_TYPE_CAMERA:
						head.setBackgroundResource(R.drawable.monitor_camera_online);
						break;
					case GlobalConstant.MONITOR_TYPE_PHONE:
						head.setBackgroundResource(R.drawable.monitor_phone_online);
						break;
					default:
						head.setBackgroundResource(R.drawable.monitor_default_online);
						break;
				}
				userName.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("释放");
				break;
			default:
				// 0为不在线
				// 0为默认，1为摄像头，2为手机
				switch(videoUser.getType()){
					case GlobalConstant.MONITOR_TYPE_CAMERA:
						head.setBackgroundResource(R.drawable.monitor_camera_offline);
						break;
					case GlobalConstant.MONITOR_TYPE_PHONE:
						head.setBackgroundResource(R.drawable.monitor_phone_offline);
						break;
					default:
						head.setBackgroundResource(R.drawable.monitor_default_offline);
						break;
				}
				userName.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
				userState.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
				userState.setText("离线");
				break;
		}*/
		// 设置状态
		switch(videoUser.getType()){
			case GlobalConstant.MONITOR_TYPE_CAMERA:
				head.setBackgroundResource(R.drawable.monitor_camera_online);
				break;
			case GlobalConstant.MONITOR_TYPE_PHONE:
				head.setBackgroundResource(R.drawable.monitor_phone_online);
				break;
			default:
				head.setBackgroundResource(R.drawable.monitor_default_online);
				break;
		}
		userName.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
		userState.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
		if (videoUser.isInmonitoring()){
			userState.setText("监控中");
		}
		else {
			userState.setText("在线");
		}
		
		return rowView;
	}
}
