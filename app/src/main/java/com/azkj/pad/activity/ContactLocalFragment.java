package com.azkj.pad.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.ContactsListAdapter;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ContactLocalFragment {
	// 父窗体
	private Activity mActivity;
    // 联系人图片
    private ArrayList<Contacts> mContacts = new ArrayList<Contacts>();
	// 列表控件
    private ListView mContactListView;
    // 列表适配器
    private ContactsListAdapter adapter;
    public PopupWindow popupWindow;// 点击显示菜单
    private ImageButton btnContactAudio;
    private ImageButton btnContactEdit;
    private ImageButton btnContactDelete;
    private Contacts selcContact;
    private LinearLayout ll_tab, ll_oper;
	private ViewPager mPager;
	private Timer timer;
    private PTT_3G_PadApplication ptt_3g_PadApplication;
	private SipUser sipUser;
	private SharedPreferences prefs;
    public ContactLocalFragment(Activity activity,PTT_3G_PadApplication ptt_3g_PadApplication){
    	mActivity = activity;
    	this.ptt_3g_PadApplication=ptt_3g_PadApplication;

		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
    }
    
    // 加载数据供外部调用
	public boolean loadLocalListView(List<Contacts> contacts){
		// 添加本地联系人
		// 取得当前tab页是否显示
		boolean currTabVisible = CommonMethod.getInstance().getPrefsBoolValue(ptt_3g_PadApplication,
				GlobalConstant.SP_SHOWORHIDDEN);
		if (!currTabVisible){
			return false;
		}
		mContacts.clear();
		mContacts.addAll(contacts);
		//new AsyncTaskLocal().execute();
		new AsyncTaskLocal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
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
			/*getPhoneContacts();*/
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			setListViewByLocal(mContacts);
		}
    }

    // 设置列表显示本地联系人
    private void setListViewByLocal(List<Contacts> contacts) {
		timer = new Timer();
    	ll_tab = (LinearLayout)mActivity.findViewById(R.id.ll_tab);
		ll_oper = (LinearLayout)mActivity.findViewById(R.id.ll_oper);
		mPager = (ViewPager) mActivity.findViewById(R.id.vPager);
		mContactListView = (ListView)mActivity.findViewById(R.id.contact_local_listview);	
        adapter = new ContactsListAdapter(mActivity, contacts, mContactListView);
        if (mContactListView == null){
        	return;
        }
        mContactListView.setAdapter(adapter);
  
        // 选中联系人操作
        OnItemClickListener mContactOnItemClickListener = new OnItemClickListener() {



			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selcContact=mContacts.get(arg2);

				if(ptt_3g_PadApplication.isAddContact()){
					if (selcContact.getPhoneNo().equals(sipUser.getUsername())){
						ToastUtils.showToast(mActivity,"发送联系人不能为自己");
						return;
					}

					if(!ptt_3g_PadApplication.getContactList().contains(selcContact.getPhoneNo())){
						arg1.setBackgroundResource(R.color.contact_user_color);
						ptt_3g_PadApplication.getContactList().add(selcContact.getPhoneNo());
					}else{
						arg1.setBackgroundResource(R.color.meeting_keyboard_bg);
						ptt_3g_PadApplication.getContactList().remove(selcContact.getPhoneNo());
					}
				}else{
						showWindow(arg1);
				}
			}
		};
		mContactListView.setOnItemClickListener(mContactOnItemClickListener);
	}
    
    // 刷新联系人列表
 	public void refreshContactsList(List<Contacts> contacts){
 		// 添加系统用户
 		mContacts.clear();
 		mContacts.addAll(contacts);
 		if (mContactListView == null){
        	return;
        }
        mContactListView.setAdapter(adapter);
 	}
	
	// 显示操作菜单窗口
	private void showWindow(View v) {
		LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
		View view = layoutInflater.inflate(R.layout.layout_contact_local_menu,null);
		
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
		// popupWindow.setOutsideTouchable(true);
		// 保存anchor在屏幕中的位置
		int[] location = new int[2];
		// 读取位置anchor座标
		v.getLocationOnScreen(location);
		if (mActivity.getResources().getDisplayMetrics().heightPixels == 1200 || mActivity.getResources().getDisplayMetrics().widthPixels == 1920){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1] - ((v.getHeight()*2)-(v.getHeight()/2)));
		}else if (mActivity.getResources().getDisplayMetrics().heightPixels == 800 || mActivity.getResources().getDisplayMetrics().widthPixels == 1280){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1] - ((v.getHeight()*3)- v.getHeight()));
		}else if (mActivity.getResources().getDisplayMetrics().heightPixels == 720 || mActivity.getResources().getDisplayMetrics().widthPixels == 1024){
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1] - ((v.getHeight()*2)- v.getHeight()));
		}else {
			popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,location[1] - ((v.getHeight()*3)- v.getHeight()));
		}


		btnContactAudio = (ImageButton) view.findViewById(R.id.btnContactAudio);
		btnContactEdit = (ImageButton) view.findViewById(R.id.btnContactEdit);
		btnContactDelete = (ImageButton) view.findViewById(R.id.btnContactDelete);

		btnContactAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(GlobalConstant.CALLHASVIDEO,GlobalConstant.CALLTYPE_VOICE);
				edit.apply();
				Log.e("*******Sws*****","voice");


				Intent voiceIntent=new Intent(GlobalConstant.ACTION_CALLING_OTHERCALLVOICE);
				voiceIntent.putExtra("callUserNo", selcContact.getPhoneNo());
				mActivity.sendBroadcast(voiceIntent);
				
				removePopupWindow();
			}
		});
		btnContactEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager=mActivity.getFragmentManager();
				FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
				ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");
				if(contactAddFragment==null){
					contactAddFragment=new ContactAddFragment();
				}
				contactAddFragment.setEditNo(selcContact.getBuddyName());//编辑联系人
				if(!contactAddFragment.isAdded()){
					fragmentTransaction.add(R.id.ll_contact,contactAddFragment, "contactadd").commit();
				}
				ll_tab.setVisibility(View.GONE);
				mPager.setVisibility(View.GONE);
				ll_oper.setVisibility(View.GONE);
				
				removePopupWindow();
			}
		});
		btnContactDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonMethod.delContact(mActivity, selcContact.getBuddyName(),selcContact.getBuddyNo());
				
				Intent contactChangeIntent=new Intent(GlobalConstant.ACTION_CONTACT_CHANGE);
				mActivity.sendBroadcast(contactChangeIntent);
				
				Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
				removePopupWindow();
			}
		});

	}
	
	private void removePopupWindow(){
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}
}
