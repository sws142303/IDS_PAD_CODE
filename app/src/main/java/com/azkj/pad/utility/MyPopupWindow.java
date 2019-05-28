package com.azkj.pad.utility;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow {
	private View view;
	public MyPopupWindow(Context context,int resId){
		LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view=layoutInflater.inflate(resId, null);
		caculateView(view);
		setContentView(view);
		float screnWidth=context.getResources().getDisplayMetrics().widthPixels;
		int height=view.getMeasuredHeight();
		setWidth((int)(screnWidth*4/5));
		setHeight(height);
		setOutsideTouchable(true);
		setFocusable(true);
		ColorDrawable colorDrawable=new ColorDrawable(00000000);
		setBackgroundDrawable(colorDrawable);
	}
	
	public View getView() {
		return view;
	}
	
	private void caculateView(View view){
		int w= MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int h= MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
	}
}
