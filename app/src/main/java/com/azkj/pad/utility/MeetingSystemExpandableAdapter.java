package com.azkj.pad.utility;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.activity.R;

/*会议系统用户可展开列表适配器*/
public class MeetingSystemExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<GroupInfo> groupInfos;
	
	@SuppressWarnings("static-access")
	public MeetingSystemExpandableAdapter(Context context) {
		this.context = context;
		this.layoutInflater=layoutInflater.from(context);
	}
	@SuppressWarnings("static-access")
	public MeetingSystemExpandableAdapter(Context context, List<GroupInfo> groupInfos) {
		this.context = context;
		this.layoutInflater=layoutInflater.from(context);
		this.groupInfos = groupInfos;
	}
	
	@Override
	public int getGroupCount() {
		return groupInfos.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupInfos.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupInfo groupinfo = (GroupInfo)getGroup(groupPosition);
		// 取得组视图
		convertView = layoutInflater.inflate(R.layout.expandable_group_item, null);
		
		TextView textView = (TextView)convertView.findViewById(R.id.groupname);
		textView.setText(groupinfo.getName()+"(在线："+getMemeberCount(groupinfo)+")");
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
		return groupInfos.get(groupPosition).getListMembers().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupInfos.get(groupPosition).getListMembers().get(childPosition);
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
		MemberInfo memberinfo = (MemberInfo)getChild(groupPosition, childPosition);
		// 取得子视图
		convertView = layoutInflater.inflate(R.layout.expandable_system_item, null);
		// 取得控件
		ImageView head = (ImageView)convertView.findViewById(R.id.iv_image);
		TextView userName = (TextView)convertView.findViewById(R.id.tv_title);
		ImageView addorcut = (ImageView)convertView.findViewById(R.id.iv_addorcut);
		TextView userState = (TextView)convertView.findViewById(R.id.tv_status);
		// 设置字体
		userName.setTypeface(CommonMethod.getTypeface(context));
		userState.setTypeface(CommonMethod.getTypeface(context));
		// 设置数值
		if  ((memberinfo.getName() != null)
				&&(memberinfo.getName().trim().length() > 0)){
			userName.setText(memberinfo.getName());
		}
		else {
			userName.setText(memberinfo.getNumber());
		}
		// 0为不在线，1为听讲，2为讲话，3为在线
		/*switch(memberinfo.getStatus()){
			case GlobalConstant.GROUP_MEMBER_OFFLINE:
				// 0为不在线
				head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
				userState.setText("离线");
				break;
			case GlobalConstant.GROUP_MEMBER_LISTENING:
				// 1为听讲
				head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("在线");
				break;
			case GlobalConstant.GROUP_MEMBER_SPEAKING:
				// 2为讲话
				head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("在线");
				break;
			case GlobalConstant.GROUP_MEMBER_ONLINE:
				// 3为在线
				head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
				userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
				userState.setText("在线");
				break;
			default:
				break;
		}*/
		// 1为离线，在线包括：  离会   2、10,会议中 6,呼入   4,呼出   3
		if ((memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_EMPTY)
			|| (memberinfo.getStatus() == GlobalConstant.GROUP_MEMBER_INIT)){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
			userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
			userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
			userState.setText("离线");
		}
		else {
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
			userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
			userState.setText("在线");
		}
		/*if ((memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_NONE)
			|| (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_OFFLINE)){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
			userState.setText("离线");
			userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
			userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_ONLINE){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("在线");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_RINGING){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
			userState.setText("振铃中");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_TALKING){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("通话中");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_HOLDING){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("保持中");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SHUTUP){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("禁言");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_LEAVE){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
			userState.setText("离会");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SPEAK){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("发言");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (memberinfo.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_QUEUE){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
			userState.setText("排队");
			userName.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
			userState.setTextColor(convertView.getResources().getColor(R.color.meeting_meeting_online_bg));
		}*/
		// 设置会议状态
		if (memberinfo.isInmeeting()){
			/*addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_cut));*/
		}
		else {
			addorcut.setImageDrawable(convertView.getResources().getDrawable(R.drawable.meeting_add));
		}

		return convertView;
	}
	
	// 获取组成员数量
	private String getMemeberCount(GroupInfo groupinfo){
		int count = 0, online = 0;
		for(MemberInfo memberinfo : groupinfo.getListMembers()){
			if (!memberinfo.getName().equals(this.context.getResources().getString(R.string.title_intercom_changegroup))){
				count++;
				if (memberinfo.getStatus() > 1){
					online++;
				}
			}
		}
		return online+"/"+count;
	}
}
