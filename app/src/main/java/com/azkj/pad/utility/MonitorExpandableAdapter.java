package com.azkj.pad.utility;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.activity.R;

/*监控树状适配器*/
public class MonitorExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<MonitorInfo> monitorinfos;
	
	@SuppressWarnings("static-access")
	public MonitorExpandableAdapter(Context context) {
		this.context = context;
		this.layoutInflater=layoutInflater.from(context);
	}
	@SuppressWarnings("static-access")
	public MonitorExpandableAdapter(Context context, List<MonitorInfo> monitorinfos) {
		this.context = context;
		this.layoutInflater=layoutInflater.from(context);
		this.monitorinfos = monitorinfos;
	}
	
	@Override
	public int getGroupCount() {
		return monitorinfos.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return monitorinfos.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		MonitorInfo videouser = (MonitorInfo)getGroup(groupPosition);
		// 取得组视图
		convertView = layoutInflater.inflate(R.layout.expandable_group_item, null);
		
		TextView textView = (TextView)convertView.findViewById(R.id.groupname);
		textView.setText(videouser.getName()+"（在线："+getMemeberCount(videouser)+"）");
		textView.setTypeface(CommonMethod.getTypeface(context));
		
		ImageView imageView=(ImageView)convertView.findViewById(R.id.jiantou);
		if(isExpanded){
			imageView.setBackgroundResource(R.drawable.jiantou);
		}
		else{
			imageView.setBackgroundResource(R.drawable.jiantou_2);
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return monitorinfos.get(groupPosition).getChildren().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return monitorinfos.get(groupPosition).getChildren().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		MonitorInfo videoUser = (MonitorInfo)getChild(groupPosition, childPosition);
		// 取得子视图
		convertView = layoutInflater.inflate(R.layout.expandable_monitor_item, null);
		// 取得控件
		ImageView head = (ImageView)convertView.findViewById(R.id.head);
		
		TextView userName = (TextView)convertView.findViewById(R.id.username);
		TextView userState = (TextView)convertView.findViewById(R.id.userstate);
		// 设置字体
		userName.setTypeface(CommonMethod.getTypeface(context));
		userState.setTypeface(CommonMethod.getTypeface(context));
		// 设置数值
		if  ((videoUser.getName() != null)
			&& (videoUser.getName().trim().length() > 0)){
			userName.setText(videoUser.getName());
		}
		else {
			userName.setText(videoUser.getNumber());
		}

		// 0为不在线，1为调取，2为在线
		switch(videoUser.getStatus()){
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
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
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
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
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
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
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
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
				userState.setText("离线");
				break;
		}

		return convertView;
	}
	
	// 获取组成员数量
	private String getMemeberCount(MonitorInfo monitorinfo){
		int online = 0;
		for(MonitorInfo user : monitorinfo.getChildren()){
			if (user.getStatus() > 1){
				online++;
			}
		}
		return online+"/"+monitorinfo.getChildren().size();
	}
}
