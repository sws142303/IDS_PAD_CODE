package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.juphoon.lemon.ui.MtcDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*配置-对讲通话(下拉菜单样式)*/
public class SettingIntercomsFragment {

	// 全局变量定义
	private SharedPreferences prefs;
	// 父窗体
	private Activity mActivity;
	// 控件定义
	private TextView tv_highpriorityvl = null;
	private TextView tv_samepriorityvl = null;
	private TextView tv_lowpriorityvl = null;
	private TextView tv_defaultgroupvl = null;
	private TextView tv_ringtonevl = null;
	// 数据定义
    private List<OptionInfo> mHighPriority = new ArrayList<OptionInfo>();
    private List<OptionInfo> mSamePriority = new ArrayList<OptionInfo>();
    private List<OptionInfo> mLowPriority = new ArrayList<OptionInfo>();
    private List<OptionInfo> mGroupInfo;
    private List<OptionInfo> mRingtone;
    private String defaultGroupNumber = "";
	// 设置数据适配器
	private OptionAdapter highAdapter = null;
	private OptionAdapter sameAdapter = null;
	private OptionAdapter lowAdapter = null;
	private OptionAdapter groupAdapter = null;
	private OptionAdapter ringtoneAdapter = null;
	// 弹出窗口
	private PopupWindow popupWindow;
	private View view;
	private ListView lv_optionslist;
	
	public SettingIntercomsFragment(Activity activity){
    	mActivity = activity;
    }
    
    // 加载数据供外部调用
	public boolean loadIntercomView(){
		// 全局变量定义
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		new AsyncTaskLocal().execute();
		//new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
		return true;
	}
	// 异步初始化数据信息
    class AsyncTaskLocal extends AsyncTask<Object, Object, Object>{
    	@Override     
        protected void onPreExecute() {
        	super.onPreExecute();
    	}
    	
		@Override
		protected Object doInBackground(Object... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setViewByLocal();
		}
    }
    
      private void setViewByLocal() {
    	// 取得控件
    	TextView tv_highpriority = (TextView)mActivity.findViewById(R.id.tv_highpriority);
    	tv_highpriorityvl = (TextView)mActivity.findViewById(R.id.tv_highpriorityvl);
    	TextView tv_samepriority = (TextView)mActivity.findViewById(R.id.tv_samepriority);
    	tv_samepriorityvl = (TextView)mActivity.findViewById(R.id.tv_samepriorityvl);
    	TextView tv_lowpriority = (TextView)mActivity.findViewById(R.id.tv_lowpriority);
    	tv_lowpriorityvl = (TextView)mActivity.findViewById(R.id.tv_lowpriorityvl);
    	TextView tv_defaultgroup = (TextView)mActivity.findViewById(R.id.tv_defaultgroup);
    	tv_defaultgroupvl = (TextView)mActivity.findViewById(R.id.tv_defaultgroupvl);
    	TextView tv_ringtone = (TextView)mActivity.findViewById(R.id.tv_defaultringtone);
    	tv_ringtonevl = (TextView)mActivity.findViewById(R.id.tv_defaultringtonevl);
    	// 设置字体
    	tv_highpriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_highpriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_samepriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_samepriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_lowpriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_lowpriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_defaultgroup.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_defaultgroupvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_ringtone.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	tv_ringtonevl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	
		// 取得数据
    	String[] mIntercomPriorityArray = mActivity.getResources().getStringArray(R.array.intercom_priority);
    	mHighPriority = new ArrayList<OptionInfo>();
    	mSamePriority = new ArrayList<OptionInfo>();
    	mLowPriority = new ArrayList<OptionInfo>();
        for (int i = 0; i < mIntercomPriorityArray.length; i++){
        	Log.e("mIntercomPriorityArray"+i, mIntercomPriorityArray[i]);
        	switch (i) {
			case 0:
				mHighPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
				mSamePriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
				break;
            case 1:
            	mHighPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
            	mSamePriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
            	mLowPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
				break;
            case 2:
            	mSamePriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
            	mLowPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
	            break;

			default:
				break;
			}
        	
//        	mHighPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
//        	mSamePriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
//        	mLowPriority.add(new OptionInfo(mIntercomPriorityArray[i], mIntercomPriorityArray[i]));
        }
        List<GroupInfo> groupinfos = GroupManager.getInstance().getGroupData();
        mGroupInfo = new ArrayList<OptionInfo>();
        if ((groupinfos != null)
        	&& (groupinfos.size() > 0)){
            defaultGroupNumber = groupinfos.get(0).getNumber();
            for (GroupInfo groupinfo : groupinfos){
            	mGroupInfo.add(new OptionInfo(groupinfo.getNumber(), groupinfo.getName()));
            	if (groupinfo.getLevel() > groupinfos.get(0).getLevel()){
        			defaultGroupNumber = groupinfo.getNumber();
            	}
            }
        }
        mRingtone = listAllRings(mActivity.getResources(), "call_");
    	
    	// 取得默认数据
    	String highvalue = prefs.getString(GlobalConstant.SP_INTERCOM_HIGHPRIORITY, mIntercomPriorityArray[1]);
    	String samevalue = prefs.getString(GlobalConstant.SP_INTERCOM_SAMEPRIORITY, mIntercomPriorityArray[1]);
    	String lowvalue = prefs.getString(GlobalConstant.SP_INTERCOM_LOWPRIORITY, mIntercomPriorityArray[1]);
    	String defaultgroup = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, defaultGroupNumber);
    	String ringtone = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, "call_harp.mp3");
    	
    	// 设置默认数据
    	if ((highvalue != null)
    		&& (highvalue.length() > 0)){
    		tv_highpriorityvl.setText(highvalue);
    		if ((mHighPriority != null)
	        	&& (mHighPriority.size() > 0)){
    			for (OptionInfo optioninfo : mHighPriority){
    				if (optioninfo.getNumber().equals(highvalue)){
    					optioninfo.setSelected(true);
    				}
    			}
	        }
    	}
    	if ((samevalue != null)
    		&& (samevalue.length() > 0)){
    		tv_samepriorityvl.setText(samevalue);
    		if ((mSamePriority != null)
	        	&& (mSamePriority.size() > 0)){
    			for (OptionInfo optioninfo : mSamePriority){
    				if (optioninfo.getNumber().equals(samevalue)){
    					optioninfo.setSelected(true);
    				}
    			}
	        }
    	}
    	if ((lowvalue != null)
    		&& (lowvalue.length() > 0)){
    		tv_lowpriorityvl.setText(lowvalue);
    		if ((mLowPriority != null)
	        	&& (mLowPriority.size() > 0)){
    			for (OptionInfo optioninfo : mLowPriority){
    				if (optioninfo.getNumber().equals(lowvalue)){
    					optioninfo.setSelected(true);
    				}
    			}
	        }
    	}
    	if ((defaultgroup == null)
    		|| (defaultgroup.length() <= 0)){
    		defaultgroup = defaultGroupNumber;
    	}
    	OptionInfo currOptionInfo = null;
		if ((mGroupInfo != null)
        	&& (mGroupInfo.size() > 0)){
			for (OptionInfo optioninfo : mGroupInfo){
				if (optioninfo.getNumber().equals(defaultgroup)){
					currOptionInfo = optioninfo;
					optioninfo.setSelected(true);
				}
				else {
					optioninfo.setSelected(false);
				}
			}
        }
		if (currOptionInfo != null){
			if (currOptionInfo.getName().equals(currOptionInfo.getNumber())){
				tv_defaultgroupvl.setText(currOptionInfo.getName());
			}
			else {
				tv_defaultgroupvl.setText(currOptionInfo.getName() + "("+currOptionInfo.getNumber()+")");
			}
		}
    	if ((ringtone != null)
    		&& (ringtone.length() > 0)){
    		tv_ringtonevl.setText(ringtone);
    		if ((mRingtone != null)
	        	&& (mRingtone.size() > 0)){
    			for (OptionInfo optioninfo : mRingtone){
    				if (optioninfo.getNumber().equals(ringtone)){
    					optioninfo.setSelected(true);
    				}
    			}
	        }
    	}
		
        // 设置适配器
        highAdapter = new OptionAdapter(mActivity, mHighPriority);
    	sameAdapter = new OptionAdapter(mActivity, mSamePriority);
    	lowAdapter = new OptionAdapter(mActivity, mLowPriority);
    	groupAdapter = new OptionAdapter(mActivity, mGroupInfo);
    	ringtoneAdapter = new OptionAdapter(mActivity, mRingtone);
        
        // 事件侦听
    	tv_highpriorityvl.setOnClickListener(new BtnOnClickListener());
    	tv_samepriorityvl.setOnClickListener(new BtnOnClickListener());
		tv_lowpriorityvl.setOnClickListener(new BtnOnClickListener());
		tv_defaultgroupvl.setOnClickListener(new BtnOnClickListener());
		tv_ringtonevl.setOnClickListener(new BtnOnClickListener());
    }
    //监听
	private class BtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
		
			showWindow(view);
		}
		
	}
    
    // 显示弹出框
	private void showWindow(final View parent) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater)mActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.layout_setting_options_list, null);
			
			lv_optionslist = (ListView) view.findViewById(R.id.lv_optionslist);
			// 创建一个PopuWidow对象
			popupWindow = new PopupWindow(view, parent.getWidth(),LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		if (parent == tv_highpriorityvl){
			
			lv_optionslist.setAdapter(highAdapter);
			popupWindow.setHeight(80 * mHighPriority.size() + 4);
		}
		else if (parent == tv_samepriorityvl){
			lv_optionslist.setAdapter(sameAdapter);
			popupWindow.setHeight(80 * mSamePriority.size() + 4);
		}
		else if (parent == tv_lowpriorityvl){
			lv_optionslist.setAdapter(lowAdapter);
			popupWindow.setHeight(80 * mLowPriority.size() + 4);
		}
		else if (parent == tv_defaultgroupvl){
			lv_optionslist.setAdapter(groupAdapter);
			popupWindow.setHeight(80 * mGroupInfo.size() + 4);
		}
		else if (parent == tv_ringtonevl){
			lv_optionslist.setAdapter(ringtoneAdapter);
			popupWindow.setHeight(80 * mRingtone.size() + 4);
		}
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		popupWindow.showAsDropDown(parent);

		//配置界面--对讲通话  中的选项监听
		lv_optionslist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (parent == tv_highpriorityvl){
					OptionInfo currOptioninfo = mHighPriority.get(position); 			
					tv_highpriorityvl.setText(currOptioninfo.getNumber());
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.SP_INTERCOM_HIGHPRIORITY, currOptioninfo.getNumber());
					//Toast.makeText(mActivity, " currOptioninfo.getName():" + currOptioninfo.getName() + "currOptioninfo.getNumber():"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					editor.commit();
					
					for (OptionInfo optioninfo : mHighPriority){
						if (currOptioninfo.getNumber().equals(optioninfo.getNumber())){
							optioninfo.setSelected(true);
						}
						else {
							optioninfo.setSelected(false);
						}
					}
		    		/*Toast.makeText(mActivity, mActivity.getString(R.string.title_setting_intercom_msg), Toast.LENGTH_SHORT).show();*/
				}
				else if (parent == tv_samepriorityvl){
					OptionInfo currOptioninfo = mSamePriority.get(position); 
					/*Toast.makeText(mActivity, "你点击的是:" + currOptioninfo.getName(), Toast.LENGTH_SHORT).show();*/ 
					 
					tv_samepriorityvl.setText(currOptioninfo.getNumber());
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.SP_INTERCOM_SAMEPRIORITY, currOptioninfo.getNumber());
					//Toast.makeText(mActivity, " currOptioninfo.getName():" + currOptioninfo.getName() + "currOptioninfo.getNumber():"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					editor.commit();
					
					for (OptionInfo optioninfo : mSamePriority){
						if (currOptioninfo.getNumber().equals(optioninfo.getNumber())){
							optioninfo.setSelected(true);
						}
						else {
							optioninfo.setSelected(false);
						}
					}
		    		/*Toast.makeText(mActivity, mActivity.getString(R.string.title_setting_intercom_msg), Toast.LENGTH_SHORT).show();*/
				}
				else if (parent == tv_lowpriorityvl){
					OptionInfo currOptioninfo = mLowPriority.get(position); 
					/*Toast.makeText(mActivity, "你点击的是:" + currOptioninfo.getName(), Toast.LENGTH_SHORT).show();*/ 

					tv_lowpriorityvl.setText(currOptioninfo.getNumber());
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.SP_INTERCOM_LOWPRIORITY, currOptioninfo.getNumber());
					//Toast.makeText(mActivity, " currOptioninfo.getName():" + currOptioninfo.getName() + "currOptioninfo.getNumber():"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					editor.commit();
					
					for (OptionInfo optioninfo : mLowPriority){
						if (currOptioninfo.getNumber().equals(optioninfo.getNumber())){
							optioninfo.setSelected(true);
						}
						else {
							optioninfo.setSelected(false);
						}
					}
		    		/*Toast.makeText(mActivity, mActivity.getString(R.string.title_setting_intercom_msg), Toast.LENGTH_SHORT).show();*/
				}
				else if (parent == tv_defaultgroupvl){
					OptionInfo currOptioninfo = mGroupInfo.get(position); 
					/*Toast.makeText(mActivity, "你点击的是:" + currOptioninfo.getName(), Toast.LENGTH_SHORT).show(); */
					
					if (currOptioninfo.getName().equals(currOptioninfo.getNumber())){
						tv_defaultgroupvl.setText(currOptioninfo.getName());
					}
					else {
						tv_defaultgroupvl.setText(currOptioninfo.getName() + "("+currOptioninfo.getNumber()+")");
					}
					/*tv_defaultgroupvl.setText(currOptioninfo.getNumber());*/
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, currOptioninfo.getNumber());
					//Toast.makeText(mActivity, " currOptioninfo.getName():" + currOptioninfo.getName() + "currOptioninfo.getNumber():"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					editor.commit();
					
					for (OptionInfo optioninfo : mGroupInfo){
						if (currOptioninfo.getNumber().equals(optioninfo.getNumber())){
							optioninfo.setSelected(true);
						}
						else {
							optioninfo.setSelected(false);
						}
					}
		    		//Toast.makeText(mActivity, mActivity.getString(R.string.title_setting_intercom_msg), Toast.LENGTH_SHORT).show();
				}
				else if (parent == tv_ringtonevl){
					OptionInfo currOptioninfo = mRingtone.get(position); 
					/*Toast.makeText(mActivity, "你点击的是:" + currOptioninfo.getName()+";"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show(); */

					tv_ringtonevl.setText(currOptioninfo.getNumber());
					Editor editor = prefs.edit();
					editor.putString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, currOptioninfo.getNumber());
					//Toast.makeText(mActivity, currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					//Toast.makeText(mActivity, " currOptioninfo.getName():" + currOptioninfo.getName() + "currOptioninfo.getNumber():"+currOptioninfo.getNumber(), Toast.LENGTH_SHORT).show();
					editor.commit();
					
					for (OptionInfo optioninfo : mRingtone){
						if (currOptioninfo.getNumber().equals(optioninfo.getNumber())){
							optioninfo.setSelected(true);
						}
						else {
							optioninfo.setSelected(false);
						}
					}
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}});
				
		    // 10秒无操作自动关闭
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// 发送自动拒绝广播
					if ((popupWindow != null)
						&& (popupWindow.isShowing())){
						MtcDelegate.log("lizhiwei 正在对讲中，10秒无操作自动关闭");
						popupWindow.dismiss();
						popupWindow = null;
					}
				}
			}, 10000);
	}
	
	// 隐藏弹出窗口
	public void HiddenPopupWindow(){
		if ((popupWindow != null)
			&& (popupWindow.isShowing())){
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

    // 取得所有铃声
	private List<OptionInfo> listAllRings(Resources res, String start) {
		List<OptionInfo> lstRings = new ArrayList<OptionInfo>();
		AssetManager assetManager = res.getAssets();
		try {
			String[] files = assetManager.list("");
			if (files == null || files.length == 0) {
				return lstRings;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].startsWith(start)) {
					lstRings.add(new OptionInfo(files[i], files[i]));
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lstRings;
	}
	
	// 选项信息类
	public class OptionInfo {
		private String number;
		private String name;
		private boolean selected = false;
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		public OptionInfo(){
			
		}
		
		public OptionInfo(String number, String name){
			this.number = number;
			this.name = name;
		}
		
		public OptionInfo(String number, String name, boolean selected){
			this.number = number;
			this.name = name;
			this.selected = selected;
		}
	}
	
	// 选项适配器
	public class OptionAdapter extends BaseAdapter {

		private Context context;

		private List<OptionInfo> list;

		public OptionAdapter(Context context, List<OptionInfo> list) {

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
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_setting_options_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.groupItem = (TextView) convertView.findViewById(R.id.groupItem);
				holder.itemlayout = (LinearLayout) convertView.findViewById(R.id.itemlayout);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.groupItem.setTextColor(Color.BLACK);
			if (list.get(position).getName().equals(list.get(position).getNumber())){
				holder.groupItem.setText(list.get(position).getName());
			}
			else {
				holder.groupItem.setText(list.get(position).getName() + "("+list.get(position).getNumber()+")");
			}
			
			if (list.get(position).isSelected()){
				holder.groupItem.setBackgroundColor(context.getResources().getColor(R.color.setting_content_bg));
				holder.groupItem.setTextColor(context.getResources().getColor(R.color.contact_title_selected));
			}
			else {
				holder.groupItem.setBackgroundColor(Color.WHITE);
				holder.groupItem.setTextColor(Color.BLACK);
			}
			/*holder.groupItem.getBackground().setAlpha(120);
			holder.itemlayout.getBackground().setAlpha(120);*/
			
			return convertView;
		}

		class ViewHolder {
			LinearLayout itemlayout;
			TextView groupItem;
		}
	}
}