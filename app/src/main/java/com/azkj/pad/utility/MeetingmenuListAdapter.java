package com.azkj.pad.utility;
import java.util.ArrayList;

import com.azkj.pad.activity.MainActivity;
import com.azkj.pad.activity.R;
import com.azkj.pad.model.DecoderInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeetingmenuListAdapter extends BaseAdapter{
	    Context context;
	    ArrayList<DecoderInfo> list;
	    private LayoutInflater inflater;
	    public MeetingmenuListAdapter(Context context,ArrayList<DecoderInfo> list){
	        this.context = context;
	        this.list = list;
	        inflater = LayoutInflater.from(context);
	        
	    }
	    
	    @Override
	    public int getCount() {
	        return list.size();
	    }

	    @Override
	    public DecoderInfo getItem(int position) {
	        return list.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	        Holder holder;
	        if(convertView==null){
	            holder = new Holder();
	            convertView = inflater.inflate(R.layout.fragment_meeting_item, null);
	            holder.name = (TextView) convertView.findViewById(R.id.item_name);
	            holder.itemlayout = (LinearLayout) convertView.findViewById(R.id.itemlayout);
	            convertView.setTag(holder);
	        }else{
	            holder = (Holder) convertView.getTag();
	        }
	        
	        holder.name.setText(list.get(position).getShowname());
	        
	        holder.itemlayout.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	
	                Toast.makeText(context, list.get(position).getShowname(), Toast.LENGTH_LONG).show();
	                MainActivity activity=(MainActivity)context;
	                String idString=list.get(position).getId();
	                String showNameString=list.get(position).getShowname();
	                String ipString=list.get(position).getVideoip();
	                String portString=list.get(position).getVideoport();
	                String userString=list.get(position).getUser();
	                String passString=list.get(position).getPassword();
	                int thetype=list.get(position).getThetype();
	                //activity.selectedDecoder(idString,showNameString,ipString,portString,userString,passString,thetype);
	            }
	        });
	        return convertView;
	    }

	    protected class Holder{
	        TextView name;
	        LinearLayout itemlayout;
	    }
}
