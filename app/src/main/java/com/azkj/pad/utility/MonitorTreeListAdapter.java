package com.azkj.pad.utility;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azkj.pad.model.MonitorInfo;
import com.azkj.pad.activity.R;

/*树形结构适配器*/
public class MonitorTreeListAdapter extends BaseAdapter {
	private Context context = null;
	private List<MonitorInfo> roots;
	private List<MonitorInfo> nodeList = new ArrayList<MonitorInfo>();//所有的节点
	private List<MonitorInfo> nodeListToShow = new ArrayList<MonitorInfo>();//要展现的节点
	private LayoutInflater inflater = null;

	public MonitorTreeListAdapter(Context con, List<MonitorInfo> roots)
	{
		this.context = con;
		this.inflater = (LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 建立节点列表
		for (MonitorInfo monitorinfo : roots){
			establishNodeList(monitorinfo);
		}
		this.roots = roots;
		// 设置节点显示
		setNodeListToShow();
	}
	
	// 建立节点列表
	public void establishNodeList(MonitorInfo node)
	{
		nodeList.add(node);
		// 叶子节点则返回
		if(node.isLeaf()){
			return;
		}
		List<MonitorInfo> children = node.getChildren();
		// 递归创建子节点
		for(int i=0; i<children.size(); i++) {
			establishNodeList(children.get(i));
		}
	}
	
	// 设置显示
	public void setNodeListToShow()
	{
		this.nodeListToShow.clear();
		for (MonitorInfo monitorinfo : roots){
			establishNodeListToShow(monitorinfo);
		}
	}

	// 构造要展示在listview的nodeListToShow
	public void establishNodeListToShow(MonitorInfo node)
	{
		this.nodeListToShow.add(node);
		// 不可展开、不是叶子阶段、且有子节点
		if((node.isExpanded())
			&&(!node.isLeaf())
			&&(node.getChildren() != null)
			&&(node.getChildren().size() > 0)) {
			List<MonitorInfo> children = node.getChildren();
			for(int i=0; i<children.size(); i++) {
				establishNodeListToShow(children.get(i));
			}
		}
	}

	// 根据oid得到某一个Node,并更改其状态(设置展示或折叠)
	public void changeNodeExpandOrFold(int position)
	{
		String oid = this.nodeListToShow.get(position).getNumber();
		for(int i=0; i<this.nodeList.size(); i++)
		{
			if(nodeList.get(i).getNumber().equals(oid))
			{
				boolean flag = nodeList.get(i).isExpanded();
				nodeList.get(i).setExpanded(!flag);
			}
		}
	}

	// listItem被点击的响应事件
	public MonitorInfo OnListItemClick(int position)
	{
		MonitorInfo node = this.nodeListToShow.get(position);
		if(node.isLeaf()) {
			/*Toast.makeText(this.context, "该节点为子节点", Toast.LENGTH_SHORT).show();*/
			return node;
		}
		else {
			this.changeNodeExpandOrFold(position);
			this.setNodeListToShow();
			this.notifyDataSetChanged();
			return null;
		}
	}
	
	public int getCount() {
		return nodeListToShow.size();
	}

	public Object getItem(int arg0) {
		return nodeListToShow.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View view, ViewGroup parent) {
		Holder holder = null;
		if(view != null) {
			holder = (Holder)view.getTag();
		}
		else {
			holder = new Holder();
			view = this.inflater.inflate(R.layout.layout_monitor_treeitem, null);
			holder.tv_name = (TextView)view.findViewById(R.id.tv_name);
			holder.iv_nodeImage = (ImageView)view.findViewById(R.id.iv_nodeImage);
			holder.iv_expandedImage = (ImageView)view.findViewById(R.id.iv_expandedImage);
			holder.tv_status = (TextView)view.findViewById(R.id.tv_status);
			view.setTag(holder);
		}

		// 绘制一个item
		MonitorInfo node = this.nodeListToShow.get(position);
		// 设置图标
		if (node.isIsuser()){
			System.out.println("lizhiwei 用户" + node.getName());
			holder.iv_nodeImage.setVisibility(View.VISIBLE);
			// 设置状态
			switch(node.getType()){
				case GlobalConstant.MONITOR_TYPE_CAMERA:
					holder.iv_nodeImage.setBackgroundResource(R.drawable.monitor_camera_online);
					break;
				case GlobalConstant.MONITOR_TYPE_PHONE:
					holder.iv_nodeImage.setBackgroundResource(R.drawable.monitor_phone_online);
					break;
				default:
					holder.iv_nodeImage.setBackgroundResource(R.drawable.monitor_default_online);
					break;
			}
			holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
			holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
			if (node.isInmonitoring()){
				holder.tv_status.setText("监控中");
			}
			else {
				holder.tv_status.setText("在线");
			}
		}
		else {
			holder.iv_nodeImage.setVisibility(View.GONE);
			holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.tv_status.setText("");
		}

		//设置名称
		holder.tv_name.setTypeface(CommonMethod.getTypeface(this.context));
		if  ((node.getName() != null)
				&& (node.getName().trim().length() > 0)){
			holder.tv_name.setText(node.getName());
		}
		else {
			holder.tv_name.setText(node.getNumber());
		}

		//设置展开折叠图标
		if(!node.isIsuser())
		{
			int expandIcon = node.getExpandOrFoldIcon();
			if(expandIcon == -1){
				holder.iv_expandedImage.setVisibility(View.INVISIBLE);
			}
			else {
				holder.iv_expandedImage.setImageResource(expandIcon);
				holder.iv_expandedImage.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			holder.iv_expandedImage.setVisibility(View.INVISIBLE);
		}
		/*// 设置状态
		if (node.getType() == GlobalConstant.MONITOR_USERTYPE_USER){
			switch(node.getStatus()){
				case GlobalConstant.MONITOR_MEMBER_OUTGOING:
					// 4为外呼中
					holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setText("外呼中");
					break;
				case GlobalConstant.MONITOR_MEMBER_TAKING:
					// 7为通话中
					holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setText("在线");
					break;
				case GlobalConstant.MONITOR_MEMBER_RELEASE:
					// 11为释放
					holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.intercom_list_online));
					holder.tv_status.setText("释放");
					break;
				default:
					// 0为不在线
					holder.tv_name.setTextColor(this.context.getResources().getColor(R.color.intercom_list_offline));
					holder.tv_status.setTextColor(this.context.getResources().getColor(R.color.intercom_list_offline));
					holder.tv_status.setText("离线");
					break;
			}
		}*/
		view.setPadding(node.getLevel()*15, 10, 10, 10);
		return view;
	}

	public class Holder
	{
		ImageView iv_nodeImage;
		TextView tv_name;
		TextView tv_status;
		ImageView iv_expandedImage;
	}

}
