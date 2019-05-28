package com.azkj.pad.utility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.azkj.pad.activity.MeetingActivity;
import com.azkj.pad.activity.PTT_3G_PadApplication;
import com.azkj.pad.activity.R;
import com.azkj.pad.model.Contacts;

import java.util.List;

/*会议与会成员适配器*/
public class MeetingMemberListAdapter extends ArrayAdapter<Contacts>{
	private PTT_3G_PadApplication application;
	private Context context;
	private List<Contacts> contacts;
	private GetPositionInterface getPositionInterface;
	public MeetingMemberListAdapter(Context context, MeetingActivity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
		application = (PTT_3G_PadApplication) activity.getApplication();
		this.context = context;
		this.contacts = contacts;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Activity activity = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_meeting_member_list, null);
		}
		final Contacts contact = getItem(position);

		Button btn_Control = (Button) rowView.findViewById(R.id.btn_Control);
		if (contact.getMemberType() == GlobalConstant.MONITOR_USERTYPE_MONITOR){
			btn_Control.setVisibility(View.VISIBLE);
		}else {
			btn_Control.setVisibility(View.GONE);
		}
		btn_Control.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					getPositionInterface.getnumber(position);

			}
		});


		// 加载图片
		ImageView imageView = (ImageView)rowView.findViewById(R.id.iv_image);
		if (contact.getPhoto() == null) {
			if ((contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_EMPTY)
				&& (contact.getMeetingState() != GlobalConstant.GROUP_MEMBER_INIT)){
				imageView.setImageResource(R.drawable.contact_photo);
			}
			else{
				imageView.setImageResource(R.drawable.head_offline);
			}
		}
		else {
			imageView.setImageBitmap(contact.getPhoto());
		}
		// 设置显示名称
		TextView titleView = (TextView)rowView.findViewById(R.id.tv_title);
		titleView.setTypeface(CommonMethod.getTypeface(activity));
		
		if ((contact.getPhoneNo() != null)
			&& (contact.getPhoneNo().trim().length() > 0)){
			titleView.setText(contact.getBuddyName() + "(" + contact.getPhoneNo().trim() + ")");
		}
		else if ((contact.getBuddyName() != null)
			&& (contact.getBuddyName().trim().length() > 0)){
			titleView.setText(contact.getBuddyName());
		}
		else {
			titleView.setText(contact.getBuddyNo());
		}
		
		ImageView pushImage = (ImageView)rowView.findViewById(R.id.iv_pushImage);
		String pushNum = application.getPushVideoNum();
		if(contact.getBuddyNo().equals(pushNum)){
			pushImage.setVisibility(View.VISIBLE);
		}else{
			pushImage.setVisibility(View.GONE);
		}
		
		// 设置状态名称
		TextView stateView = (TextView)rowView.findViewById(R.id.tv_state);
		stateView.setTypeface(CommonMethod.getTypeface(activity));
		if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_EMPTY)
			|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INIT)){
			/*stateView.setText("离线");
			titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
			stateView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));*/
			stateView.setText("离会");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if ((contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_ONLINE)
				|| (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_RELEASE)){
			stateView.setText("离会");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_TALKING){
			stateView.setText("会议中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_INCOMING
				|| contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_ALERTED){
			stateView.setText("呼入");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.GROUP_MEMBER_OUTGOING){
			stateView.setText("呼出");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else {
			stateView.setText("在线");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		/*if ((contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_NONE)
			|| (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_OFFLINE)){
			stateView.setText("离线");
			titleView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
			stateView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_ONLINE){
			stateView.setText("在线");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_RINGING){
			stateView.setText("振铃中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_TALKING){
			stateView.setText("通话中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_HOLDING){
			stateView.setText("保持中");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SHUTUP){
			stateView.setText("禁言");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_LEAVE){
			stateView.setText("离会");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.intercom_list_offline));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_SPEAK){
			stateView.setText("发言");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}
		else if (contact.getMeetingState() == GlobalConstant.CONFERENCE_MEMBERSTATE_QUEUE){
			stateView.setText("排队");
			titleView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
			stateView.setTextColor(activity.getResources().getColor(R.color.meeting_meeting_online_bg));
		}*/
		return rowView;
	}

	public void setGetPositionInterface(GetPositionInterface getPositionInterface) {
		this.getPositionInterface = getPositionInterface;
	}

	public interface GetPositionInterface{
		void getnumber(int position);
	}
}
