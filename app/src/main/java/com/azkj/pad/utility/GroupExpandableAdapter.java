package com.azkj.pad.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.model.MemberInfo;

import java.util.List;


public class GroupExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<GroupInfo> groupInfos;
	private RelativeLayout relativeLayout;

	@SuppressWarnings("static-access")
	public GroupExpandableAdapter(Context context) {
		this.context = context;
		this.layoutInflater=layoutInflater.from(context);
	}
	@SuppressWarnings("static-access")
	public GroupExpandableAdapter(Context context, List<GroupInfo> groupInfos) {
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
		convertView = layoutInflater.inflate(R.layout.expandable_child_item, null);
		// 取得控件
		relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_item);

		ImageView head = (ImageView)convertView.findViewById(R.id.head);
		TextView userName = (TextView)convertView.findViewById(R.id.username);
		TextView userState = (TextView)convertView.findViewById(R.id.userstate);
		// 设置字体
		userName.setTypeface(CommonMethod.getTypeface(context));
		userState.setTypeface(CommonMethod.getTypeface(context));
		// 设置数值
		if (memberinfo.getName().equals(this.context.getResources().getString(R.string.title_intercom_changegroup))){
			head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.intercom_change));
			userName.setText(memberinfo.getName());
			userState.setText("");
		}
		else {
			if ((memberinfo.getName() != null)
					&& (memberinfo.getName().trim().length() > 0)){
				userName.setText(memberinfo.getName());
			}
			else {
				userName.setText(memberinfo.getNumber());
			}

//			// 0为不在线，1为听讲，2为讲话，3为在线
//			/*switch(memberinfo.getStatus()){
//				case GlobalConstant.GROUP_MEMBER_OFFLINE:
//					// 0为不在线
//					head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_offline));
//					userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
//					userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
//					userState.setText("离线");
//					break;
//				case GlobalConstant.GROUP_MEMBER_LISTENING:
//					// 1为听讲
//					head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
//					userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setText("在线");
//					break;
//				case GlobalConstant.GROUP_MEMBER_SPEAKING:
//					// 2为讲话
//					head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
//					userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setText("在线");
//					break;
//				case GlobalConstant.GROUP_MEMBER_ONLINE:
//					// 3为在线
//					head.setImageDrawable(convertView.getContext().getResources().getDrawable(R.drawable.head_online));
//					userName.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
//					userState.setText("在线");
//					break;
//				default:
//					break;
//			}*/
			
			// 1为离线，在线包括： 发言  12,听讲  11,空闲  2、10
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
