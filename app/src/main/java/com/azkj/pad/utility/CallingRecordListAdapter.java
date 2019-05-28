package com.azkj.pad.utility;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.CallingRecords;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.service.GroupManager;

import java.util.List;

/*通话记录列表适配器*/
public class CallingRecordListAdapter extends ArrayAdapter<CallingRecords> {
	public CallingRecordListAdapter(Activity activity, List<CallingRecords> callingrecords, ListView listViews) {
		super(activity, 0, callingrecords);
	}
	public static CallingRecordViewHolder callingRecordViewHolder=null;
	public Boolean isckVISIBLE=false;
	public Boolean ischecked=false;
	public Boolean getchecked(){
		
		if(isckVISIBLE && callingRecordViewHolder.ckbBox.isChecked()){
			return true;
		}
		return false;
	}
	private int positionPublic;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		positionPublic=position;
		// 填充视图
		View rowView = convertView;
		
		if (rowView == null) {
			callingRecordViewHolder=new CallingRecordViewHolder();
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.layout_calling_item, null);
			// 取得控件
			callingRecordViewHolder.ckbBox=(CheckBox)rowView.findViewById(R.id.ckbox);
			
//			callingRecordViewHolder.ckbBox.setVisibility(View.VISIBLE);
//			callingRecordViewHolder.ckbBox.setChecked(true);
			callingRecordViewHolder.calling_userimg = (ImageView)rowView.findViewById(R.id.calling_userimg);
			callingRecordViewHolder.calling_username = (TextView)rowView.findViewById(R.id.calling_username);
			callingRecordViewHolder.calling_time = (TextView)rowView.findViewById(R.id.calling_time);
			callingRecordViewHolder.calling_timespan = (TextView)rowView.findViewById(R.id.calling_timespan);
			rowView.setTag(callingRecordViewHolder);
			
		}else{
			callingRecordViewHolder=(CallingRecordViewHolder)rowView.getTag();
		}
		CallingRecords callingrecord = getItem(position);
		callingRecordViewHolder.ckbBox.setTag(callingrecord);
		// 设置字体
		callingRecordViewHolder.calling_username.setTypeface(CommonMethod.getTypeface(activity));
		callingRecordViewHolder.calling_time.setTypeface(CommonMethod.getTypeface(activity));
		callingRecordViewHolder.calling_timespan.setTypeface(CommonMethod.getTypeface(activity));
		if(isckVISIBLE){
		    callingRecordViewHolder.ckbBox.setVisibility(View.VISIBLE);
		  //  RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) callingRecordViewHolder.calling_userimg.getLayoutParams();
	        //lp.setMargins(70, 0, 0, 0);
			//callingRecordViewHolder.calling_userimg.setLayoutParams(lp);
		}else{
			callingRecordViewHolder.ckbBox.setVisibility(View.GONE);
			//RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) callingRecordViewHolder.calling_userimg.getLayoutParams();
	        //lp.setMargins(20, 0, 0, 0);
			//callingRecordViewHolder.calling_userimg.setLayoutParams(lp);
		}
		if(ischecked){
			callingRecordViewHolder.ckbBox.setChecked(true);
			callingrecord.setIscheck(true);
			
		}else{
			callingRecordViewHolder.ckbBox.setChecked(false);
			callingrecord.setIscheck(false);
//			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) callingRecordViewHolder.calling_userimg.getLayoutParams();
//	        lp.setMargins(20, 0, 0, 0);
//			callingRecordViewHolder.calling_userimg.setLayoutParams(lp);
		}
		// 设置数值
		if (CommonMethod.checkConcactExist(getContext(), callingrecord.getBuddyNo())) {
			// 本地联系人
			if (callingrecord.getInOutFlg() == 0) {
				// 呼出
				callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_sim_out));
			} else {
				// 呼入
				if(callingrecord.getCallState()==0){
					callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_sim_in));
				}else{
					callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_sim_in_answer));
				}
				
			}
			callingrecord.setBuddyNm(CommonMethod.getDisplayNameByNo(getContext(), callingrecord.getBuddyNo()));
		}
		else {
			// 系统联系人
			if (callingrecord.getInOutFlg() == 0) {
				// 呼出
				callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_out));
			}
			else {
				// 呼入
				if(callingrecord.getCallState()==0){
					callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_in));
				}else{
					callingRecordViewHolder.calling_userimg.setImageDrawable(activity.getResources().getDrawable(R.drawable.calling_in_answer));
				}
				
			}
			if ((callingrecord.getConferenceNo() == null)
					|| (callingrecord.getConferenceNo().length() <= 0)){
				MemberInfo memberinfo = GroupManager.getInstance().getMemberInfo(callingrecord.getBuddyNo());
				if (memberinfo != null){
					callingrecord.setBuddyNm(memberinfo.getName());
				}
			}
			/*map.put("username", CommonMethod.subString(callingrecord.getBuddyNo(), 5, "."));*/
		}
		if ((callingrecord.getConferenceNm() == null)
				|| (callingrecord.getConferenceNm().length() <= 0)){
			// 如果不是会议
			if ((callingrecord.getBuddyNm() != null)
				&& (callingrecord.getBuddyNm().length() > 0)){
				//yuezs修改，4改成12
				callingRecordViewHolder.calling_username.setText(CommonMethod.subString(callingrecord.getBuddyNm(), 12, ""));

			}
			else {
				callingRecordViewHolder.calling_username.setText(CommonMethod.subString(callingrecord.getBuddyNo(), 12, ""));
			}
		}
		else {
			// 如果是会议
			callingRecordViewHolder.calling_username.setText(callingrecord.getConferenceNm().substring("yyyy年MM月dd日".length()));
		}
		callingRecordViewHolder.calling_time.setText(FormatController.getDateMonthFormat(callingrecord.getAnswerDate()));
		callingRecordViewHolder.calling_timespan.setText(FormatController.secToTime(callingrecord.getDuration()));
		callingRecordViewHolder.ckbBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				//Log.e("选择", "第一行");
				CheckBox cbBox=(CheckBox)buttonView;
				CallingRecords callingrecord = (CallingRecords)cbBox.getTag();//getItem(posid);
				//Log.e("选中序号", callingrecord.getId()+","+callingrecord.getBuddyNo());
				callingrecord.setIscheck(isChecked);
			}
		});
		
		return rowView;
	}
	
	private static class CallingRecordViewHolder{
	    CheckBox ckbBox;
		ImageView calling_userimg;
		TextView calling_username;
		TextView calling_time;
		TextView calling_timespan;
	}
}
