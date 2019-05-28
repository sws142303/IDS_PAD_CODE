package com.azkj.pad.utility;

import com.azkj.pad.activity.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsViewCache {
	private View baseView;
	private ImageView imageView;
	private TextView titleView;
	private TextView textView;
	private ImageView addorcutView;

	public ContactsViewCache(View baseView) {
		this.baseView = baseView;
	}

	public ImageView getImageView() {
		if (imageView == null) {
			imageView = (ImageView) baseView.findViewById(R.id.iv_image);
		}
		return imageView;
	}

	public TextView getTitleView() {
		if (titleView == null) {
			titleView = (TextView) baseView.findViewById(R.id.tv_title);
			titleView.setTypeface(CommonMethod.getTypeface(baseView.getContext()));
		}
		return titleView;
	}

	public TextView getTextView() {
		if (textView == null) {
			textView = (TextView) baseView.findViewById(R.id.tv_phoneNo);
			textView.setTypeface(CommonMethod.getTypeface(baseView.getContext()));
		}
		return textView;
	}

	public ImageView getAddorcutView() {
		if (addorcutView == null) {
			addorcutView = (ImageView) baseView.findViewById(R.id.iv_addorcut);
		}
		return addorcutView;
	}
}
