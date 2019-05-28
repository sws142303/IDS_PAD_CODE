package com.azkj.pad.utility;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.Contacts;

import java.util.List;

/*会议本地联系人及系统联系人适配器*/
public class MeetingLocalListAdapter extends ArrayAdapter<Contacts>{

	public MeetingLocalListAdapter(Activity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_meeting_local_list, null);
		}
		Contacts contact = getItem(position);
		// 加载图片
		ImageView imageView = (ImageView)rowView.findViewById(R.id.iv_image);
		if (contact.getPhoto() != null) {
			imageView.setImageBitmap(contact.getPhoto());
		}
		else {
			if ((contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_EMPTY)
				&& (contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_INIT)){
				imageView.setImageResource(R.drawable.contact_photo);
			}
			else{
				//imageView.setImageResource(R.drawable.head_offline);
				imageView.setImageResource(R.drawable.contact_photo);
			}
		}
		// 设置显示名称
		TextView titleView = (TextView)rowView.findViewById(R.id.tv_title);
		titleView.setTypeface(CommonMethod.getTypeface(activity));
		titleView.setText(CommonMethod.subString(contact.getBuddyName(), 6, ""));
		// 设置显示号码
		TextView textView = (TextView)rowView.findViewById(R.id.tv_phoneNo);
		textView.setTypeface(CommonMethod.getTypeface(activity));
		if (contact.getBuddyName().trim().length() <= 0){
			textView.setText(contact.getPhoneNo());
		}
		// 设置状态
		TextView textStatus = (TextView)rowView.findViewById(R.id.tv_status);
		textStatus.setTypeface(CommonMethod.getTypeface(activity));
		if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY)
			|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT)){
			textStatus.setText("离线");
			titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
			textStatus.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_ONLINE)
				|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_RELEASE)){
			textStatus.setText("离会");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_TALKING){
			textStatus.setText("会议中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INCOMING){
			textStatus.setText("呼入");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_OUTGOING){
			textStatus.setText("呼出");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else {
			textStatus.setText("在线");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		/*if ((contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_NONE)
			|| (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_OFFLINE)){
			textStatus.setText("离线");
			titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
			textStatus.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_ONLINE){
			textStatus.setText("在线");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_RINGING){
			textStatus.setText("振铃中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_TALKING){
			textStatus.setText("通话中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_HOLDING){
			textStatus.setText("保持中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SHUTUP){
			textStatus.setText("禁言");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_LEAVE){
			textStatus.setText("离会");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SPEAK){
			textStatus.setText("发言");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_QUEUE){
			textStatus.setText("排队");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			textStatus.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}*/
		// 设置添加删除
		ImageView addorcutView = (ImageView)rowView.findViewById(R.id.iv_addorcut);
		if (contact.isInmeeting()){
			addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_cut));
		}
		else {
			addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_add));
		}
		
		return rowView;
	}
}
