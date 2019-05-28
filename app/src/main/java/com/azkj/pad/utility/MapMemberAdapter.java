package com.azkj.pad.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.PosMarkerInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ANGELCOMM on 2017/12/21.
 */

public class MapMemberAdapter extends BaseAdapter {
    private HashMap<Integer, Boolean> checkedMap = new HashMap<>();
    private Context context;
    private List<PosMarkerInfo> list;

    public MapMemberAdapter(Context context, List<PosMarkerInfo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_mapmemberlistitem,null);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_mapmemberItem);
            viewHolder.mCheckBox.setTag(list.get(position));
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mCheckBox.setText(list.get(position).getName());
        viewHolder.mCheckBox.setTag(position);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                checkedMap.put(position, isChecked);
                notifyDataSetChanged();
            }
        });
        if (checkedMap.containsKey(position)
                && checkedMap.get(position)) {
            //如果Map中存在此消息，并且已选中
            viewHolder.mCheckBox.setChecked(true);
        } else {
            viewHolder.mCheckBox.setChecked(false);
        }

        return convertView;
    }

    class ViewHolder{
        CheckBox mCheckBox;
    }
}
