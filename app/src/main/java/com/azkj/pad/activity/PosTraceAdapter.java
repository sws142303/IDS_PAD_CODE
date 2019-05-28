package com.azkj.pad.activity;

import java.util.List;

import com.azkj.pad.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class PosTraceAdapter extends BaseAdapter {
	private List<String> list;
	private Context context;
	public PosTraceAdapter(Context context,List<String> list){
		this.context=context;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_pos_trace_item, null);
		}
		Button btnDelete=(Button)convertView.findViewById(R.id.btnDelete);
		TextView txtAddUserNo=(TextView)convertView.findViewById(R.id.txtAddUserNo);
		txtAddUserNo.setText(list.get(position));
		final int index=position;
		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.remove(index);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

}
