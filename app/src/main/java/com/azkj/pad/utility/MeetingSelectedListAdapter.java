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

import com.azkj.pad.model.Contacts;
import com.azkj.pad.activity.R;

/*会议选中联系人适配器*/
public class MeetingSelectedListAdapter extends ArrayAdapter<Contacts> {
	public MeetingSelectedListAdapter(Activity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// 填充视图
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_meeting_selected_list, null);
		}
		Contacts contact = getItem(position);
		// 加载图片
		ImageView imageView = (ImageView)rowView.findViewById(R.id.iv_image);
		if (contact.getPhoto() == null) {
			imageView.setImageResource(R.drawable.contact_photo);
		}
		else {
			imageView.setImageBitmap(contact.getPhoto());
		}
		// 设置显示名称
		TextView titleView = (TextView)rowView.findViewById(R.id.tv_title);
		titleView.setTypeface(CommonMethod.getTypeface(activity));
		if ((contact.getPhoneNo() != null)
			&& (contact.getPhoneNo().trim().length() > 0)
			&& (!contact.getPhoneNo().trim().equals(contact.getBuddyName()))){
			titleView.setText(contact.getBuddyName() + "(" + contact.getPhoneNo().trim() + ")");
		}
		else if ((contact.getBuddyName() != null)
			&& (contact.getBuddyName().trim().length() > 0)){
			titleView.setText(contact.getBuddyName());
		}
		else {
			titleView.setText(contact.getBuddyName());
		}
		// 设置添加删除
		ImageView cutView = (ImageView)rowView.findViewById(R.id.iv_cut);
		cutView.setImageDrawable(activity.getResources().getDrawable(R.drawable.meeting_cut));
		
		return rowView;
	}
}
