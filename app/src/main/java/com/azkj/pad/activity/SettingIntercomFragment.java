package com.azkj.pad.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.GroupInfo;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/*配置-对讲通话(弹出菜单样式)*/      //这个报废了
public class SettingIntercomFragment {

	// 全局变量定义
	private SharedPreferences prefs;
	// 父窗体
	private Activity mActivity;
	// 控件定义
	private Spinner sp_highpriorityvl = null;
	private Spinner sp_samepriorityvl = null;
	private Spinner sp_lowpriorityvl = null;
	private Spinner sp_defaultgroupvl = null;
	private Spinner sp_ringtonevl = null;
	// 数据定义
    private String[] mIntercomPriorityArray;
    private List<GroupInfo> mGroupInfo;
    private List<String> mRingtone;
    private int defaultGroupIndex = 0;
    private String defaultGroupNumber = "";
	// 设置数据适配器
	private ArrayAdapter<String> highAdapter = null;
	private ArrayAdapter<String> sameAdapter = null;
	private ArrayAdapter<String> lowAdapter = null;
	private ArrayAdapter<GroupInfo> groupAdapter = null;
	private ArrayAdapter<String> ringtoneAdapter = null;
	
	public SettingIntercomFragment(Activity activity){
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
    	sp_highpriorityvl = (Spinner)mActivity.findViewById(R.id.sp_highpriorityvl);
    	TextView tv_samepriority = (TextView)mActivity.findViewById(R.id.tv_samepriority);
    	sp_samepriorityvl = (Spinner)mActivity.findViewById(R.id.sp_samepriorityvl);
    	TextView tv_lowpriority = (TextView)mActivity.findViewById(R.id.tv_lowpriority);
    	sp_lowpriorityvl = (Spinner)mActivity.findViewById(R.id.sp_lowpriorityvl);
    	TextView tv_defaultgroup = (TextView)mActivity.findViewById(R.id.tv_defaultgroup);
    	sp_defaultgroupvl = (Spinner)mActivity.findViewById(R.id.sp_defaultgroupvl);
    	TextView tv_ringtone = (TextView)mActivity.findViewById(R.id.tv_ringtone);
    	sp_ringtonevl = (Spinner)mActivity.findViewById(R.id.sp_ringtonevl);
    	// 设置字体
    	tv_highpriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	/*sp_highpriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));*/
    	tv_samepriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	/*sp_samepriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));*/
    	tv_lowpriority.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	/*sp_lowpriorityvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));*/
    	tv_defaultgroup.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	/*sp_defaultgroupvl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));*/
    	tv_ringtone.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));
    	/*sp_ringtonevl.setTypeface(CommonMethod.getTypeface(mActivity.getBaseContext()));*/
    	
		// 取得数据
        mIntercomPriorityArray = mActivity.getResources().getStringArray(R.array.intercom_priority);
        mGroupInfo = GroupManager.getInstance().getGroupData();
        if ((mGroupInfo != null)
        	&& (mGroupInfo.size() > 0)){
        	for (int i = 0; i < mGroupInfo.size(); i++){
        		if (mGroupInfo.get(i).getLevel() > mGroupInfo.get(0).getLevel()){
        			defaultGroupIndex = i;
        			defaultGroupNumber = mGroupInfo.get(i).getNumber();
        		}
        	}
        }
        mRingtone = listAllRings(mActivity.getResources(), "call_");
        // 设置适配器
        highAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mIntercomPriorityArray){
        	@Override 
        	public View getDropDownView(int position, View convertView, ViewGroup parent) {
				LayoutInflater _LayoutInflater = LayoutInflater.from(mActivity);
				View view = _LayoutInflater.inflate(R.layout.layout_setting_option_item, null);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.setting_content_bg));
                TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
                ImageView iv_selected = (ImageView)view.findViewById(R.id.iv_selected);
                
                tv_name.setText(mIntercomPriorityArray[position]);
            	tv_name.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
            	tv_name.setTypeface(CommonMethod.getTypeface(mActivity));
                
                if (sp_highpriorityvl.getSelectedItemPosition() == position) {
                	iv_selected.setImageResource(R.drawable.btn_radio_on);
                }
                else {
                	iv_selected.setImageResource(R.drawable.btn_radio_off);
                }

                return view;
        	}
        };
        highAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        sameAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mIntercomPriorityArray){
        	@Override 
        	public View getDropDownView(int position, View convertView, ViewGroup parent) {
				LayoutInflater _LayoutInflater = LayoutInflater.from(mActivity);
				View view = _LayoutInflater.inflate(R.layout.layout_setting_option_item, null);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.setting_content_bg));
                TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
                ImageView iv_selected = (ImageView)view.findViewById(R.id.iv_selected);
                
                tv_name.setText(mIntercomPriorityArray[position]);
            	tv_name.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
            	tv_name.setTypeface(CommonMethod.getTypeface(mActivity));
                
                if (sp_samepriorityvl.getSelectedItemPosition() == position) {
                	iv_selected.setImageResource(R.drawable.btn_radio_on);
                }
                else {
                	iv_selected.setImageResource(R.drawable.btn_radio_off);
                }

                return view;
        	}
        };
        sameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        lowAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mIntercomPriorityArray){
        	@Override 
        	public View getDropDownView(int position, View convertView, ViewGroup parent) {
				LayoutInflater _LayoutInflater = LayoutInflater.from(mActivity);
				View view = _LayoutInflater.inflate(R.layout.layout_setting_option_item, null);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.setting_content_bg));
                TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
                ImageView iv_selected = (ImageView)view.findViewById(R.id.iv_selected);
                
                tv_name.setText(mIntercomPriorityArray[position]);
            	tv_name.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
            	tv_name.setTypeface(CommonMethod.getTypeface(mActivity));
                
                if (sp_lowpriorityvl.getSelectedItemPosition() == position) {
                	iv_selected.setImageResource(R.drawable.btn_radio_on);
                }
                else {
                	iv_selected.setImageResource(R.drawable.btn_radio_off);
                }

                return view;
        	}
        };
        lowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        groupAdapter = new ArrayAdapter<GroupInfo>(mActivity, android.R.layout.simple_spinner_item, mGroupInfo){
        	@Override 
        	public View getDropDownView(int position, View convertView, ViewGroup parent) {
				LayoutInflater _LayoutInflater = LayoutInflater.from(mActivity);
				View view = _LayoutInflater.inflate(R.layout.layout_setting_option_item, null);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.setting_content_bg));
				
                TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
                ImageView iv_selected = (ImageView)view.findViewById(R.id.iv_selected);
                
                if (mGroupInfo.get(position).getName().equals(mGroupInfo.get(position).getNumber())){
                    tv_name.setText(mGroupInfo.get(position).getName());
                }
                else {
                    tv_name.setText(mGroupInfo.get(position).getName() + "(" +mGroupInfo.get(position).getNumber()+ ")");
                }
            	tv_name.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
            	tv_name.setTypeface(CommonMethod.getTypeface(mActivity));
                
                if (sp_defaultgroupvl.getSelectedItemPosition() == position) {
                	iv_selected.setImageResource(R.drawable.btn_radio_on);
                }
                else {
                	iv_selected.setImageResource(R.drawable.btn_radio_off);
                }

                return view;
        	}
        };
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        ringtoneAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mRingtone){
        	@Override 
        	public View getDropDownView(int position, View convertView, ViewGroup parent) {
				LayoutInflater _LayoutInflater = LayoutInflater.from(mActivity);
				View view = _LayoutInflater.inflate(R.layout.layout_setting_option_item, null);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.setting_content_bg));
                TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
                ImageView iv_selected = (ImageView)view.findViewById(R.id.iv_selected);
                
                tv_name.setText(mRingtone.get(position));
            	tv_name.setTextColor(mActivity.getResources().getColor(R.color.contact_title_selected));
            	tv_name.setTypeface(CommonMethod.getTypeface(mActivity));
                
                if (sp_ringtonevl.getSelectedItemPosition() == position) {
                	iv_selected.setImageResource(R.drawable.btn_radio_on);
                }
                else {
                	iv_selected.setImageResource(R.drawable.btn_radio_off);
                }

                return view;
        	}
        };
        ringtoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	// 绑定数据
    	sp_highpriorityvl.setAdapter(highAdapter);
    	sp_samepriorityvl.setAdapter(sameAdapter);
    	sp_lowpriorityvl.setAdapter(lowAdapter);
    	sp_defaultgroupvl.setAdapter(groupAdapter);
    	sp_ringtonevl.setAdapter(ringtoneAdapter);

    	// 设置侦听
    	sp_highpriorityvl.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String str = parent.getItemAtPosition(position).toString(); 
				Toast.makeText(mActivity, "你点击的是:" + str, Toast.LENGTH_SHORT).show(); 

				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_INTERCOM_HIGHPRIORITY, parent.getItemAtPosition(position).toString());
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
    	sp_samepriorityvl.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String str = parent.getItemAtPosition(position).toString(); 
				Toast.makeText(mActivity, "你点击的是:" + str, Toast.LENGTH_SHORT).show(); 

				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_INTERCOM_SAMEPRIORITY, parent.getItemAtPosition(position).toString());
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
    	sp_lowpriorityvl.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String str = parent.getItemAtPosition(position).toString(); 
				Toast.makeText(mActivity, "你点击的是:" + str, Toast.LENGTH_SHORT).show(); 

				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_INTERCOM_LOWPRIORITY, parent.getItemAtPosition(position).toString());
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
    	sp_defaultgroupvl.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				GroupInfo groupinfo = (GroupInfo)parent.getSelectedItem();
				Toast.makeText(mActivity, "你点击的是:" + groupinfo.getNumber(), Toast.LENGTH_SHORT).show(); 
				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, groupinfo.getNumber());
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
    	sp_ringtonevl.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String str = parent.getItemAtPosition(position).toString(); 
				Toast.makeText(mActivity, "你点击的是:" + str, Toast.LENGTH_SHORT).show(); 

				Editor editor = prefs.edit();
				editor.putString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, str);
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}});
    	
    	// 取得默认数据
    	String highvalue = prefs.getString(GlobalConstant.SP_INTERCOM_HIGHPRIORITY, mIntercomPriorityArray[1]);
    	String samevalue = prefs.getString(GlobalConstant.SP_INTERCOM_SAMEPRIORITY, mIntercomPriorityArray[1]);
    	String lowvalue = prefs.getString(GlobalConstant.SP_INTERCOM_LOWPRIORITY, mIntercomPriorityArray[1]);
    	String defaultgroup = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTGROUP, defaultGroupNumber);
    	String ringtone = prefs.getString(GlobalConstant.SP_INTERCOM_DEFAULTRINGTONE, "");
    	if ((defaultgroup != null)
    		&& (defaultgroup.length() > 0)){
    		if ((mGroupInfo != null)
	        	&& (mGroupInfo.size() > 0)){
	        	for (int i = 0; i < mGroupInfo.size(); i++){
	        		if (mGroupInfo.get(i).getNumber().equals(mGroupInfo.get(0).getNumber())){
	        			defaultGroupIndex = i;
	        		}
	        	}
	        }
    	}
    	// 设置默认数据
    	sp_highpriorityvl.setSelection(getPriorityValueIndex(highvalue, 0));
    	sp_samepriorityvl.setSelection(getPriorityValueIndex(samevalue, 1));
    	sp_lowpriorityvl.setSelection(getPriorityValueIndex(lowvalue, 2));
    	sp_defaultgroupvl.setSelection(defaultGroupIndex);
    	sp_ringtonevl.setSelection(mRingtone.indexOf(ringtone) < 0?0:mRingtone.indexOf(ringtone));
    }
    
    // 得到优先级选择索引
    private int getPriorityValueIndex(String value, int defaultvalue){
    	int result = defaultvalue;
    	for (int i = 0; i < mIntercomPriorityArray.length; i++){
    		if (mIntercomPriorityArray[i].equals(value)){
    			result = i;
    			break;
    		}
    	}
    	return result;
    }

    // 取得所有铃声
	private List<String> listAllRings(Resources res, String start) {
		List<String> lstRings = new ArrayList<String>();
		AssetManager assetManager = res.getAssets();
		try {
			String[] files = assetManager.list("");
			if (files == null || files.length == 0) {
				return lstRings;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].startsWith(start)) {
					lstRings.add(files[i]);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lstRings;
	}
}
