package com.azkj.pad.utility;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azkj.pad.activity.R;

import java.util.List;

/**
 * Created by ANGELCOMM on 2018/9/19.
 */

public class SwipRecyclerViewAdapter extends RecyclerView.Adapter<SwipRecyclerViewAdapter.ViewHolder> {
    private List<String> datas;
    private Context mContext;
    private LayoutInflater mLiLayoutInflater;

    public SwipRecyclerViewAdapter(List<String> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
        this.mLiLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public SwipRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLiLayoutInflater.inflate(R.layout.item_linear,parent, false));
    }

    @Override
    public void onBindViewHolder(SwipRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tv_voiceContent.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_voiceContent;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_voiceContent = (TextView) itemView.findViewById(R.id.tv_voiceContent);

        }
    }
}
