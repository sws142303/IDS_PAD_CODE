package com.azkj.pad.utility;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azkj.pad.activity.PTT_3G_PadApplication;
import com.azkj.pad.activity.R;
import com.azkj.sws.library.MultilevelTreeLibrary.Node;
import com.azkj.sws.library.MultilevelTreeLibrary.OnTreeNodeClickListener;
import com.azkj.sws.library.MultilevelTreeLibrary.TreeListViewAdapter;
import com.baizhi.app.MarqueeTextView;

import java.util.List;



/**
 * Created by zhangke on 2017-1-14.
 */
public class SimpleTreeAdapter extends TreeListViewAdapter
{



    public SimpleTreeAdapter(ListView mTree, Context context, List<Node> datas, int defaultExpandLevel, int iconExpand, int iconNoExpand) {
        super(mTree, context, datas, defaultExpandLevel, iconExpand, iconNoExpand);
    }

    public SimpleTreeAdapter(ListView mTree, Context context, List<Node> datas,
                             int defaultExpandLevel) {
        super(mTree, context, datas, defaultExpandLevel);
    }

    @Override
    public View getConvertView(final Node node , int position, View convertView, ViewGroup parent)
    {

       final ViewHolder viewHolder ;
        //if (!hashMap.containsKey(node.getId())) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.label = (MarqueeTextView) convertView
                    .findViewById(R.id.id_treenode_label);
            viewHolder.label2 = (TextView) convertView
                    .findViewById(R.id.id_treenode_label2);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.relative_Item = (RelativeLayout) convertView.findViewById(R.id.relative_Item);
           // hashMap.put(String.valueOf(node.getId()),convertView);
            convertView.setTag(viewHolder);

        } else {
            //convertView = hashMap.get(node.getId());

            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.isMember()) {
            viewHolder.icon.setVisibility(View.VISIBLE);
            Integer memberState = -1;
            if (PTT_3G_PadApplication.memberStatehashMap.containsKey(node.getId())){
                memberState = PTT_3G_PadApplication.memberStatehashMap.get(node.getId()).getStatus();
            }

            if (memberState == GlobalConstant.GROUP_MEMBER_EMPTY
                    || node.getIsLogin() == GlobalConstant.GROUP_MEMBER_INIT) {
                viewHolder.icon.setImageResource(R.drawable.head_offline);
                viewHolder.label2.setVisibility(View.VISIBLE);
                viewHolder.label2.setText("离线");
                viewHolder.label.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
                viewHolder.label2.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
            }else {
                viewHolder.icon.setImageResource(R.drawable.head_online);
                viewHolder.label2.setVisibility(View.VISIBLE);
                viewHolder.label2.setText("在线");
                viewHolder.label.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
                viewHolder.label2.setTextColor(convertView.getResources().getColor(R.color.intercom_list_online));
            }
            viewHolder.label.setText(node.getName() + "(" + node.getId() + ")");
        } else {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
            viewHolder.label.setText(node.getName());
            viewHolder.label2.setVisibility(View.GONE);
            viewHolder.label.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
            viewHolder.label2.setTextColor(convertView.getResources().getColor(R.color.intercom_list_offline));
        }



        return convertView;
    }

    private final class ViewHolder
    {
        ImageView icon;

        MarqueeTextView label;

        TextView label2;

        RelativeLayout relative_Item;
    }

    @Override
    public void setOnTreeNodeClickListener(OnTreeNodeClickListener onTreeNodeClickListener) {
        super.setOnTreeNodeClickListener(onTreeNodeClickListener);

    }
}
