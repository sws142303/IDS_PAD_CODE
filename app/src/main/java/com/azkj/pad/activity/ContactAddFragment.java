package com.azkj.pad.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.LocalContact;
import com.azkj.pad.model.MemberInfo;
import com.azkj.pad.service.GroupManager;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;

import java.util.regex.Pattern;

public class ContactAddFragment extends Fragment {
	private TextView titleLayout;
	//private EditText txtSurname;
	private EditText txtName;
	private EditText txtCompany;
	private EditText txtIpPhone;
	private EditText txtMobilePhone;
	private EditText txtOfficePhone;
	private EditText txtHousePhone;
	private EditText txtEmail;
	
	private Button btn_save;
	private Button btn_cancel;
	private View view;
	private String oldName;
	private String editNo;//编辑联系人名称
	private String editNum;//编辑联系人号码
	private boolean systemContent = false;
	private Long rowId;
	private LocalContact contact;

	public String getEditNo() {
		return editNo;
	}

	public void setEditNo(String editNo) {
		this.editNo = editNo;
	}
	
	public String getEditNum() {
		return editNum;
	}

	public void setEditNum(String editNum) {
		this.editNum = editNum;
	}

	public boolean isSystemContent() {
		return systemContent;
	}

	public void setSystemContent(boolean systemContent) {
		this.systemContent = systemContent;
	}
	private InputFilter filter=new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if(source.equals(" ")||source.toString().contentEquals("\n"))return "";
			else return null;
		}
	};



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("##########ContactAddFrg onCreateView############", "#######onCreateView###");
		Log.e("====Sws测试联系人页面","进入添加页面");
		view = inflater.inflate(R.layout.fragment_contact_add, container,false);
		titleLayout=(TextView)view.findViewById(R.id.titleLayout);
		//txtSurname = (EditText) view.findViewById(R.id.editSurname);
		txtName = (EditText) view.findViewById(R.id.editname);
		txtName.setFilters(new InputFilter[]{filter});

		if (txtName.getText().toString().trim().length() > 0){
			txtName.setText(null);
		}
		txtCompany = (EditText) view.findViewById(R.id.editCompany);
		txtIpPhone = (EditText) view.findViewById(R.id.editIpPhone);
		txtMobilePhone = (EditText) view.findViewById(R.id.editMobilePhone);
		txtOfficePhone= (EditText) view.findViewById(R.id.editOfficePhone);
		txtHousePhone= (EditText) view.findViewById(R.id.editHousePhone);
		txtEmail= (EditText) view.findViewById(R.id.editEmail);
		
		initContactData();
		
		btn_save = (Button) view.findViewById(R.id.btnContactAdd);
		btn_cancel = (Button) view.findViewById(R.id.btnContactCancel);
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("====Sws测试联系人页面","确认监听中  此时etName的内容为："+txtName.getText().toString());
				//添加手机联系人
				if (txtName.getText().toString().length()==0) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.contact_name), Toast.LENGTH_SHORT).show();
					return;
				}

				String regexString="[\u4e00-\u9fa5a-zA-Z0-9]*";
				if(!Pattern.matches(regexString, txtName.getText().toString())){
					Toast.makeText(getActivity(), "联系人名称格式不正确", Toast.LENGTH_SHORT).show();
					return;
				}
				//至少填写一项
				if (txtIpPhone.getText().toString().length()==0&&txtOfficePhone.getText().toString().length()==0&& txtMobilePhone.getText().toString().length()==0&&txtHousePhone.getText().toString().length()==0) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.contact_phone),Toast.LENGTH_SHORT).show();
					return;
				}

				if (txtMobilePhone.getText().toString().length()!=0&&!CommonMethod.isMobileNO(txtMobilePhone.getText().toString())) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.contact_mobilephone),Toast.LENGTH_SHORT).show();
					return;
				}
				if(txtEmail.getText().toString().length()!=0&&!CommonMethod.checkEmail(txtEmail.getText().toString())){
					Toast.makeText(getActivity(), getActivity().getString(R.string.contact_email),Toast.LENGTH_SHORT).show();
					return;
				}
				contact = new LocalContact();
				//contact.setFirstName(txtSurname.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtName.getText().toString());
				contact.setLastName(txtName.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtCompany.getText().toString());
				contact.setCompany(txtCompany.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtIpPhone.getText().toString());
				contact.setIpPhone(txtIpPhone.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtMobilePhone.getText().toString());
				contact.setMobilePhone(txtMobilePhone.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtHousePhone.getText().toString());
				contact.setHomePhone(txtHousePhone.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtOfficePhone.getText().toString());
				contact.setOfficePhone(txtOfficePhone.getText().toString());
				Log.e("====Sws测试联系人页面","确认监听中  将内容封装到LocalContact中："+txtEmail.getText().toString());
				contact.setEmail(txtEmail.getText().toString());
				//				/*if(oldName==null){
//					rowId=Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(),contact.getLastName()));
//				}else{
//					rowId=Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(),oldName));
//				}
//
//				MtcDelegate.log("rowId:"+rowId);
//				if(rowId==0){
//					CommonMethod.addLocalContact(getActivity(), contact);
//				}else{
//					//CommonMethod.updateLocalContact(getActivity(), contact,rowId);
//					Toast.makeText(getActivity(), "已存在同名联系人，请修改后重试！", Toast.LENGTH_SHORT).show();
//					return;
//				}*/
				Log.e("====Sws测试联系人页面","确认监听中  判断oldName是否为空    oldName："+oldName);


				new AddContactAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);



			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canelAddContact();
			}
		});
		return view;
	}
	
	//初始化联系人数据
	private void initContactData(){
		/*if(getEditNo()==null)
			return;*/
		if(isSystemContent() == false){
			LocalContact contact=CommonMethod.getContactByName(getActivity(),getEditNo());
			if(contact!=null && contact.getLastName()!=null){
				//txtSurname.setText(contact.getFirstName());
				txtName.setText(contact.getLastName());
				txtName.setEnabled(false);
				oldName=contact.getLastName();
				txtCompany.setText(contact.getCompany());
				txtIpPhone.setText(contact.getIpPhone());
				txtOfficePhone.setText(contact.getOfficePhone());
				txtHousePhone.setText(contact.getHomePhone());
				txtMobilePhone.setText(contact.getMobilePhone()!=null?contact.getMobilePhone().replace(" ", ""):"");
				txtEmail.setText(contact.getEmail());
			}else{
				txtName.setText(getEditNo());
				txtIpPhone.setText(getEditNum());
			}
		}
		else{
			Long rowId=Long.valueOf(CommonMethod.getContactIdByName(getActivity(), getEditNo()));
			if(rowId>0){
				titleLayout.setText(getString(R.string.title_contact_edit));
			}
			MemberInfo memberinfo = GroupManager.getInstance().getMemberInfoByName(getEditNo());
			String systemNum = null;
			if (memberinfo != null)
			{
				systemNum = memberinfo.getNumber();
			}
			if(null == systemNum){
				return;
			}				
			txtName.setText(getEditNo());
			//oldName=getEditNo();				
			txtIpPhone.setText(systemNum);
			/*txtName.setText(getEditNo());
			oldName=getEditNo();
			txtIpPhone.setText(getEditNo());*/
		}
	}
	
	private void canelAddContact(){
		FragmentManager fragmentManager=getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");

		if(contactAddFragment!=null){
			fragmentTransaction.remove(contactAddFragment).commitAllowingStateLoss();
		}
		// 发送联系人添加、修改、删除完成通告页面广播
		Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGEOVER);
		view.getContext().sendBroadcast(callIntent);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	class AddContactAsync extends AsyncTask<Object,Object,Object>{

		@Override
		protected Object doInBackground(Object... params) {
			if(oldName==null || "".equals(oldName)){
				rowId = Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(), contact.getLastName()));
				Log.e("##########ContactAddFrg 133############", "#######ContactAddAc### rowId:" + rowId);
				Log.e("====Sws测试联系人页面","确认监听中     156判断rowId           rowId："+ rowId);
				if(rowId ==0){
					CommonMethod.addLocalContact(getActivity(), contact);
				}else{
					//CommonMethod.updateLocalContact(ContactAddActivity.this, contact,rowId);
					return "Error";
				}
			}else{
				rowId = Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(),oldName));
				Log.e("====Sws测试联系人页面","确认监听中     166判断rowId           rowId："+ rowId);
				Log.e("##########ContactAddFrg 143############", "#######ContactAddAc### rowId:" + rowId);
				if(rowId ==0){
					rowId = Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(),oldName));
					if (rowId == 0){
						CommonMethod.addLocalContact(getActivity(), contact);
					}
				}else{
					if(oldName.equals(contact.getLastName())){
						CommonMethod.updateLocalContact(getActivity(), contact, rowId);
					}else{
						long newRowId=Long.valueOf(CommonMethod.getUpdateContactIdByName(getActivity(), contact.getLastName()));
						Log.e("====Sws测试联系人页面","确认监听中     newRowId           newRowId："+newRowId);
						if(newRowId == 0){
							//CommonMethod.delContact(getActivity(), oldName);
							//CommonMethod.addLocalContact(getActivity(), contact);
							CommonMethod.updateLocalContact(getActivity(), contact, rowId);
						}else{

							return "Error";
						}
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			String content = (String) o;
			if (content != null){

				if (content.equals("Error")){
					ToastUtils.showToast(getActivity(), "已存在同名联系人，请修改后重试！");
				}

			}else {
				// 发送 联系人添加、修改、删除广播
				Intent callIntent = new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
				view.getContext().sendBroadcast(callIntent);
				Log.e("====Sws测试联系人页面","确认监听中     GlobalConstant.ACTION_CONTACT_CHANGE广播发送 ");

				canelAddContact();

				Toast.makeText(getActivity(), getActivity().getString(R.string.contact_success),Toast.LENGTH_SHORT).show();
			}


		}
	}

}
