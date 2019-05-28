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

/*会议过程中点击成员，显示所有本地及系统联系人列表适配器*/
public class MeetingAllListAdapter extends ArrayAdapter<Contacts>{

	public MeetingAllListAdapter(Activity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_meeting_allcontacts_list, null);
		}
		Contacts contact = getItem(position);
		// 加载图片
		ImageView imageView = (ImageView)rowView.findViewById(R.id.iv_image);
		if (contact.getPhoto() == null) {
			if ((contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_EMPTY)
				&& (contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_INIT)){
				imageView.setImageResource(R.drawable.contact_photo);
			} else{
				//imageView.setImageResource(R.drawable.head_offline);
				imageView.setImageResource(R.drawable.contact_photo);
			}
		}
		else {
			imageView.setImageBitmap(contact.getPhoto());
		}
		// 设置显示名称
		TextView titleView = (TextView)rowView.findViewById(R.id.tv_title);
		titleView.setTypeface(CommonMethod.getTypeface(activity));
		if ((contact.getBuddyName() != null)
				&& (contact.getBuddyName().trim().length() > 0)){
			titleView.setText(contact.getBuddyName());
		}
		else {
			titleView.setText(contact.getBuddyNo());
		}
		if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY)
			|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT)){
			//titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_ONLINE)
				|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_RELEASE)){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_TALKING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INCOMING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_OUTGOING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else {
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		/*if ((contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_NONE)
			|| (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_OFFLINE)){
			titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_ONLINE){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_RINGING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_TALKING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_HOLDING){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SHUTUP){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_LEAVE){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SPEAK){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_QUEUE){
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}*/
		// 设置添加删除
		ImageView addorcutView = (ImageView)rowView.findViewById(R.id.iv_addorcut);
		if (contact.isInmeeting()){
			addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_null));
		}
		else {
			addorcutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_add));
		}
		
		return rowView;
	}
}
