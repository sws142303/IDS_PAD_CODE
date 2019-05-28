package com.azkj.pad.utility;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.activity.R;

/*定位本地联系人及系统联系人适配器*/
public class PositionLocalListAdapter extends ArrayAdapter<Contacts>{
	Context context;
	public PositionLocalListAdapter(Activity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 context = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			//LayoutInflater inflater = activity.getLayoutInflater();
			rowView = LayoutInflater.from(context).inflate(R.layout.layout_position_local_list, null);
		}
		Contacts contact = getItem(position);
		// 加载图片
		ImageView imageView = (ImageView)rowView.findViewById(R.id.iv_image);
		if (contact.getPhoto() != null) {
			imageView.setImageBitmap(contact.getPhoto());
		}
		else {
			if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY)
				|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT)){
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_offline));
			}
			else {
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_online));
			}
		}
		// 设置显示名称
		TextView titleView = (TextView)rowView.findViewById(R.id.tv_title);
		titleView.setTypeface(CommonMethod.getTypeface(context));
		if ((contact.getPhoneNo() != null)
			&& (!contact.getPhoneNo().equals(contact.getBuddyName()))){
			titleView.setText(contact.getBuddyName() + "("+contact.getPhoneNo()+")");
		}
		else {
			if ((contact.getBuddyName() != null)
				&& (contact.getBuddyName().trim().length() > 0)){
				titleView.setText(contact.getBuddyName());
			}
			else {
				titleView.setText(contact.getBuddyNo());
			}
		}
		// 设置状态
		TextView textStatus = (TextView)rowView.findViewById(R.id.tv_status);
		textStatus.setTypeface(CommonMethod.getTypeface(context));
		/*// 0为不在线，1为听讲，2为讲话，3为在线
		switch(contact.getMeetingState()){
			case GlobalConstant.GROUP_MEMBER_OFFLINE:
				// 0为不在线
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_offline));
				titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
				textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
				textStatus.setText("离线");
				break;
			case GlobalConstant.GROUP_MEMBER_LISTENING:
				// 1为听讲
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_online));
				titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setText("在线");
				break;
			case GlobalConstant.GROUP_MEMBER_SPEAKING:
				// 2为讲话
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_online));
				titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setText("在线");
				break;
			case GlobalConstant.GROUP_MEMBER_ONLINE:
				// 3为在线
				imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_online));
				titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
				textStatus.setText("在线");
				break;
			default:
				break;
		}*/
		// 1为离线，在线包括： 发言  12,听讲  11,空闲  2、10
		if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY)
			|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT)){
			/*imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_offline));*/
			titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
			textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_offline));
			textStatus.setText("离线");
		}
		else {
			/*imageView.setImageDrawable(rowView.getContext().getResources().getDrawable(R.drawable.head_online));*/
			titleView.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
			textStatus.setTextColor(rowView.getResources().getColor(R.color.intercom_list_online));
			textStatus.setText("在线");
		}
		// 设置定位状态
		ImageView addorcutView = (ImageView)rowView.findViewById(R.id.iv_addorcut);
		if (contact.isInmeeting()){
			addorcutView.setImageDrawable(context.getResources().getDrawable(R.drawable.location_status));
		}
		else {
			/*addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.location_status));*/
		}
		
		return rowView;
	}
}
