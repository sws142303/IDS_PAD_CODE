package com.azkj.pad.model;

import java.util.ArrayList;
import java.util.List;

import com.azkj.pad.activity.R;

import android.content.Context;

public class CallResolvingPower {

	private List<ResolvingPower> list;
	private Context context;
	public CallResolvingPower(Context context){
		
		list = new ArrayList<ResolvingPower>();
		this.context = context;
		
	}
	
	public  List<ResolvingPower> getResolvingPower(){
		String[] resplvingPower = context.getResources().getStringArray(R.array.call_Resolvingpower);	
		for (int i = 0; i < resplvingPower.length; i++) {
			ResolvingPower reso = new ResolvingPower();
			String str = resplvingPower[i];
			String strs = str.substring(str.indexOf("("),str.indexOf(")"));
			String strt = strs.substring(1, strs.length());
			String height = strt.substring(0, strt.indexOf("*"));
			String widths = strt.substring(strt.indexOf("*"),strt.length());
			String width = widths.substring(1, widths.length());
			reso.setHeight(height);
			reso.setWidth(width);
			list.add(reso);
		}
		
		return list;		
	}
	
}
