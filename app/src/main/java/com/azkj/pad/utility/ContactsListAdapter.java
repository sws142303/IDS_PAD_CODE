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

/*联系人本地联系人适配器*/
public class ContactsListAdapter extends ArrayAdapter<Contacts>{

	public ContactsListAdapter(Activity activity, List<Contacts> contacts, ListView listViews) {
		super(activity, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		// 填充视图
		View rowView = convertView;
		ContactsViewCache viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_contact_list, null);
			viewCache = new ContactsViewCache(rowView);
			rowView.setTag(viewCache);
		}
		else {
			viewCache = (ContactsViewCache)rowView.getTag();
		}
		try{
			Contacts contact = getItem(position);
			if (contact != null){
				// 加载图片
				ImageView imageView = viewCache.getImageView();
				if (contact.getPhoto() == null) {
					imageView.setImageResource(R.drawable.contact_photo);
				}
				else {
					imageView.setImageBitmap(contact.getPhoto());
				}
				// 设置显示名称
				TextView titleView = viewCache.getTitleView();

				titleView.setText(contact.getBuddyName().trim());

				// 设置显示号码
				TextView textView = viewCache.getTextView();
				textView.setText(contact.getPhoneNo());
			}

		}catch (Exception e){

		}
		return rowView;
	}
}
