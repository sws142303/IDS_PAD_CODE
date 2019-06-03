package com.azkj.pad.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azkj.pad.model.MessageRecords;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageListFragment extends Fragment {
	private MessageNewFragment messageNewFragment;
	private MessageShowFragment messageShowFragment;

	private ListView messageList;
	private String userNo;
	private List<MessageRecords> messageRecordsList;
	private MessageSimpleAdapter messageSimpleAdapter;

	public PopupWindow popupWindow;// 点击显示菜单
	private Button btnNewMessage;
	/************批量删除start**************/
    private ImageButton imgbtn;
    private RelativeLayout topPlan;
    private LinearLayout message_dellayout;
    private CheckBox checkAll;
    private Button btndelete;
    private Button Cancel;
    private TextView listNum;
    /**************批量删除end**************/
	private TextView btnMessageVoice;
	private TextView btnMessageVideo;
	private TextView btnMessageEdit;
	private TextView btnMessageReply;
	private TextView btnMessageDelete;
	private String menuCallNo;
	private SharedPreferences prefs;
	private SipUser sipUser;
	@SuppressWarnings("unused")
	private String menuCallId;
	public String getCallUserNo() {
		return callUserNo;
	}

	public void setCallUserNo(String callUserNo) {
		this.callUserNo = callUserNo;
	}

	private String callUserNo;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onResume() {
		super.onResume();

		try{
			if (messageSimpleAdapter != null){
				if(messageSimpleAdapter.getCount()>0){
					topPlan.setVisibility(View.VISIBLE);
					message_dellayout.setVisibility(View.GONE);
					MessageSimpleAdapter messagesimpleadapters=null;
					messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
					messagesimpleadapters.isvisible=false;
					messageList.setAdapter(messagesimpleadapters);
					checkAll.setChecked(false);
				}

			}
		}catch (Exception e){

		}





	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message_list, container,
				false);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		 sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
		
		Log.e("短信", "进入短信");
		btnNewMessage = (Button) view.findViewById(R.id.btnNewMessage);

		//userNo = MtcDelegate.getLoginedUser();
		userNo = sipUser.getUsername();
		messageList = (ListView) view.findViewById(R.id.messagelist);
		imgbtn=(ImageButton) view.findViewById(R.id.messagedelbutton);
		topPlan=(RelativeLayout)view.findViewById(R.id.topPlan);
		message_dellayout=(LinearLayout)view.findViewById(R.id.messagedellayout);
        checkAll=(CheckBox)view.findViewById(R.id.checkAll);
        btndelete=(Button)view.findViewById(R.id.btndelete);
        Cancel=(Button)view.findViewById(R.id.Cancel);
        listNum=(TextView)view.findViewById(R.id.listNum);
		initMessageData();
        
		btnNewMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				MessageListFragment messageListFragment = (MessageListFragment) fragmentManager
						.findFragmentByTag("fragmentlist");
//				if (messageNewFragment == null) {
//					messageNewFragment = new MessageNewFragment();
//				}
				messageNewFragment = new MessageNewFragment();

				if (!messageNewFragment.isAdded()) {
					fragmentTransaction
							.hide(messageListFragment)    
							.add(R.id.messageRelativeLayout,
									messageNewFragment, "fragmentnew").commitAllowingStateLoss();
				} else {
					fragmentTransaction.hide(messageListFragment)
							.show(messageNewFragment).commitAllowingStateLoss();
				}
			}
		});
		return view;
	}

	private void initMessageData() {
		if(userNo==null){
			return;
		}
		if (messageRecordsList == null) {
			messageRecordsList = new ArrayList<MessageRecords>();
		}

		//new AsyncInitMessageData().execute();
		new AsyncInitMessageData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);

        imgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(messageSimpleAdapter.getCount()>0){
				topPlan.setVisibility(View.GONE);
				message_dellayout.setVisibility(View.VISIBLE);	
				MessageSimpleAdapter messagesimpleadapters=null;
				messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
				messagesimpleadapters.isvisible=true;
				messageList.setAdapter(messagesimpleadapters);
				}else{
					Log.e("集合数量", "小于等于0");
				}
			}
		});
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//全选
					for (int i = 0; i < messageList.getAdapter().getCount(); i++) {
						MessageRecords messageRecords=(MessageRecords)messageList.getAdapter().getItem(i);
						messageRecords.setIschecked(true);
					}
					MessageSimpleAdapter messagesimpleadapters=null;
					messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
					messagesimpleadapters.isChecked=true;
					messageList.setAdapter(messagesimpleadapters);
				}else{
					//取消全选
					for (int i = 0; i < messageList.getAdapter().getCount(); i++) {
						MessageRecords messageRecords=(MessageRecords)messageList.getAdapter().getItem(i);
						messageRecords.setIschecked(false);
					}
					MessageSimpleAdapter messagesimpleadapters=null;
					messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
					messagesimpleadapters.isChecked=false;
					messageList.setAdapter(messagesimpleadapters);
				}
			}
		});
        btndelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
				sqLiteHelper.open();
                for (int i = 0; i < messageList.getAdapter().getCount(); i++) {
                	MessageRecords messageRecords=(MessageRecords)messageList.getAdapter().getItem(i);
                	Log.e("ischecked:", messageRecords.getIschecked()+"");
                	if(messageRecords.getIschecked()){
				      sqLiteHelper.deleteMessageRecordByUserNo(userNo,messageRecords.getBuddyNo());
                	}
                }
                sqLiteHelper.closeclose();
                topPlan.setVisibility(View.VISIBLE);
				message_dellayout.setVisibility(View.GONE);	
				MessageSimpleAdapter messagesimpleadapters=null;
				messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
				messagesimpleadapters.isvisible=false;
				messageList.setAdapter(messagesimpleadapters);
				checkAll.setChecked(false);
				messageRecordsList.clear();
				initMessageData();
                
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
        Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(messageSimpleAdapter.getCount()>0){
				topPlan.setVisibility(View.VISIBLE);
				message_dellayout.setVisibility(View.GONE);	
				MessageSimpleAdapter messagesimpleadapters=null;
				messagesimpleadapters=(MessageSimpleAdapter)messageList.getAdapter();
				messagesimpleadapters.isvisible=false;
				messageList.setAdapter(messagesimpleadapters);
				checkAll.setChecked(false);
				}
			}
		});
		messageList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MessageRecords mr = messageRecordsList.get(position);
				//得到新消息ID
				menuCallId=mr.getId().toString();
				//得到新消息号码
				menuCallNo=mr.getBuddyNo();
				
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				MessageListFragment messageListFragment = (MessageListFragment) fragmentManager
						.findFragmentByTag("fragmentlist");
				if (messageShowFragment == null) {
					messageShowFragment = new MessageShowFragment();
				}
				messageListFragment.setCallUserNo(menuCallNo);
				// fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
				if (!messageShowFragment.isAdded()) {
					fragmentTransaction.hide(messageListFragment).add(R.id.messageRelativeLayout,messageShowFragment, "fragmentshow").commit();
				} 
			}
		});
		messageList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				MessageRecords mr = messageRecordsList.get(arg2);
				menuCallId=mr.getId().toString();
				menuCallNo=mr.getBuddyNo();
				showWindow(arg1);
				
				return true;
			}
		});
	}
	
	private class AsyncInitMessageData extends AsyncTask<Object, Object, Object>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected Object doInBackground(Object... arg0) {
			SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
			sqLiteHelper.open();
			messageRecordsList = sqLiteHelper.getMessageRecordsLastByUserNO(userNo);
			sqLiteHelper.closeclose();
			Log.e("============","消息列表长度"+messageRecordsList.size());
			messageSimpleAdapter = new MessageSimpleAdapter(getActivity(),messageRecordsList);
			
			return null;
		}
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			messageList.setAdapter(messageSimpleAdapter);
			messageSimpleAdapter.notifyDataSetChanged();
			listNum.setText("共["+messageSimpleAdapter.getCount()+"]条");
		}
	}

	private class MessageSimpleAdapter extends BaseAdapter {
		private Context context;
		private List<MessageRecords> messages;
		private boolean isvisible=false;
		private boolean isChecked=false;
		public MessageSimpleAdapter(Context context,
				List<MessageRecords> messages) {
			this.context = context;
			this.messages = messages;
		}

		@SuppressWarnings("unused")
		public List<MessageRecords> getMessages() {
			return messages;
		}

		@Override
		public int getCount() {
			return messages.size();
		}

		@Override
		public Object getItem(int arg0) {
			return messages.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageRecords messageRecords = messages.get(position);
			int type = messageRecords.getContentType();
			
			if (convertView == null) {
				switch (type) {
				case GlobalConstant.MESSAGE_TEXT:
					convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_item_text, null);
					messageTextInit(convertView, messageRecords);
					break;
				case GlobalConstant.MESSAGE_IMG:
					convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_item_img, null);
					messageImgInit(convertView, messageRecords);
					break;
				case GlobalConstant.MESSAGE_AUDIO:
					convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_item_audio, null);
					messageAudioInit(convertView, messageRecords);
					break;
				case GlobalConstant.MESSAGE_VIDEO:
					convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_item_video, null);
					messageVideoInit(convertView, messageRecords);
					break;
				case GlobalConstant.MESSAGE_FILE:
					convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_item_file, null);
					messageFileInit(convertView, messageRecords);
					break;
				default:
					break;
				}
			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			MessageRecords mRecords = messages.get(position);
			return mRecords.getLayout();
		}

		@Override
		public int getViewTypeCount() {
			return 8;
		}
		private void messageTextInit(View view,MessageRecords mr){
			ViewHolderText viewHolderText=new ViewHolderText();
			viewHolderText.txtTextMessageCount=(TextView)view.findViewById(R.id.txtTextMessageCount);
			viewHolderText.txtTextMessageUserName=(TextView)view.findViewById(R.id.txtTextMessageUserName);
			viewHolderText.txtTextMessageDate=(TextView)view.findViewById(R.id.txtTextMessageDate);
			viewHolderText.txtTextMessageText=(TextView)view.findViewById(R.id.txtTextMessageText);
			viewHolderText.txtcBox=(CheckBox)view.findViewById(R.id.txtckbox);
			if(isvisible){
				viewHolderText.txtcBox.setVisibility(View.VISIBLE);
			}else{
				viewHolderText.txtcBox.setVisibility(View.GONE);
			}
			if(isChecked){
			 viewHolderText.txtcBox.setChecked(true);
			    mr.setIschecked(true);
			}else{
				viewHolderText.txtcBox.setChecked(false);
				mr.setIschecked(false);
			}
			
			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
				viewHolderText.txtTextMessageCount.setVisibility(View.VISIBLE);
				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
				sqLiteHelper.closeclose();
				viewHolderText.txtTextMessageCount.setText(readNoCount+"");//未读
				
				
			}else{
				viewHolderText.txtTextMessageCount.setVisibility(View.GONE);
			}
			viewHolderText.txtTextMessageUserName.setText(mr.getBuddyNo());
			viewHolderText.txtTextMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
			viewHolderText.txtTextMessageText.setText(mr.getContent());
			viewHolderText.txtcBox.setTag(mr);
			viewHolderText.txtcBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					MessageRecords mrs=(MessageRecords)buttonView.getTag();
					mrs.setIschecked(isChecked);
				}
			});
		}
		private void messageImgInit(View view,MessageRecords mr){
			ViewHolderImg viewHolderImg=new ViewHolderImg();
			viewHolderImg.txtImgMessageCount=(TextView)view.findViewById(R.id.txtImgMessageCount);
			viewHolderImg.txtImgMessageUserName=(TextView)view.findViewById(R.id.txtImgMessageUserName);
			viewHolderImg.txtImgMessageDate=(TextView)view.findViewById(R.id.txtImgMessageDate);
			viewHolderImg.imgcBox=(CheckBox)view.findViewById(R.id.imgckbox);
			if(isvisible){
				viewHolderImg.imgcBox.setVisibility(View.VISIBLE);
			}else{
				viewHolderImg.imgcBox.setVisibility(View.GONE);
			}
			if(isChecked){
				viewHolderImg.imgcBox.setChecked(true);
			    mr.setIschecked(true);
			}else{
				viewHolderImg.imgcBox.setChecked(false);
				mr.setIschecked(false);
			}
			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
				viewHolderImg.txtImgMessageCount.setVisibility(View.VISIBLE);
				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
				sqLiteHelper.closeclose();
				viewHolderImg.txtImgMessageCount.setText(readNoCount+"");//临时未读
			}else{
				viewHolderImg.txtImgMessageCount.setVisibility(View.GONE);
			}
			viewHolderImg.txtImgMessageUserName.setText(mr.getBuddyNo());
			viewHolderImg.txtImgMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
			viewHolderImg.imgcBox.setTag(mr);
			viewHolderImg.imgcBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					MessageRecords mrs=(MessageRecords)buttonView.getTag();
					mrs.setIschecked(isChecked);
				}
			});
		}
		private void messageAudioInit(View view,MessageRecords mr){
			ViewHolderAudio viewHolderAudio=new ViewHolderAudio();
			viewHolderAudio.txtAudioMessageCount=(TextView)view.findViewById(R.id.txtAudioMessageCount);
			viewHolderAudio.txtAudioMessageUserName=(TextView)view.findViewById(R.id.txtAudioMessageUserName);
			viewHolderAudio.txtAudioMessageDate=(TextView)view.findViewById(R.id.txtAudioMessageDate);
			viewHolderAudio.AudiocBox=(CheckBox)view.findViewById(R.id.Audiockbox);
			if(isvisible){
				viewHolderAudio.AudiocBox.setVisibility(View.VISIBLE);
			}else{
				viewHolderAudio.AudiocBox.setVisibility(View.GONE);
			}
			if(isChecked){
				viewHolderAudio.AudiocBox.setChecked(true);
			    mr.setIschecked(true);
			}else{
				viewHolderAudio.AudiocBox.setChecked(false);
				mr.setIschecked(false);
			}
			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
				viewHolderAudio.txtAudioMessageCount.setVisibility(View.VISIBLE);
				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
				sqLiteHelper.closeclose();
				viewHolderAudio.txtAudioMessageCount.setText(readNoCount+"");//临时未读
			}else{
				viewHolderAudio.txtAudioMessageCount.setVisibility(View.GONE);
			}
			viewHolderAudio.txtAudioMessageUserName.setText(mr.getBuddyNo());
			viewHolderAudio.txtAudioMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
			viewHolderAudio.AudiocBox.setTag(mr);
			viewHolderAudio.AudiocBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					MessageRecords mrs=(MessageRecords)buttonView.getTag();
					mrs.setIschecked(isChecked);
				}
			});
		}
		private void messageVideoInit(View view,MessageRecords mr){
			ViewHolderVideo viewHolderVideo=new ViewHolderVideo();
			viewHolderVideo.txtVideoMessageCount=(TextView)view.findViewById(R.id.txtVideoMessageCount);
			viewHolderVideo.txtVideoMessageUserName=(TextView)view.findViewById(R.id.txtVideoMessageUserName);
			viewHolderVideo.txtVideoMessageDate=(TextView)view.findViewById(R.id.txtVideoMessageDate);
			viewHolderVideo.VideocBox=(CheckBox)view.findViewById(R.id.Videockbox);
			if(isvisible){
				viewHolderVideo.VideocBox.setVisibility(View.VISIBLE);
			}else{
				viewHolderVideo.VideocBox.setVisibility(View.GONE);
			}
			if(isChecked){
				viewHolderVideo.VideocBox.setChecked(true);
			    mr.setIschecked(true);
			}else{
				viewHolderVideo.VideocBox.setChecked(false);
				mr.setIschecked(false);
			}
			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
				viewHolderVideo.txtVideoMessageCount.setVisibility(View.VISIBLE);
				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
				sqLiteHelper.closeclose();
				viewHolderVideo.txtVideoMessageCount.setText(readNoCount+"");//临时未读
			}else{
				viewHolderVideo.txtVideoMessageCount.setVisibility(View.GONE);
			}
			viewHolderVideo.txtVideoMessageUserName.setText(mr.getBuddyNo());
			viewHolderVideo.txtVideoMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
			viewHolderVideo.VideocBox.setTag(mr);
			viewHolderVideo.VideocBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					MessageRecords mrs=(MessageRecords)buttonView.getTag();
					mrs.setIschecked(isChecked);
				}
			});
		}
		//YUEZS ADD 2015-09-08
		private void messageFileInit(View view,MessageRecords mr){
			ViewHolderFile viewHolderfile=new ViewHolderFile();
			viewHolderfile.txtFileMessageCount=(TextView)view.findViewById(R.id.txtFileMessageCount);
			viewHolderfile.txtFileMessageUserName=(TextView)view.findViewById(R.id.txtFileMessageUserName);
			viewHolderfile.txtFileMessageDate=(TextView)view.findViewById(R.id.txtFileMessageDate);
			viewHolderfile.txtFileMessageText=(TextView)view.findViewById(R.id.txtFileMessageText);
			viewHolderfile.FilecBox=(CheckBox)view.findViewById(R.id.Fileckbox);
			if(isvisible){
				viewHolderfile.FilecBox.setVisibility(View.VISIBLE);
			}else{
				viewHolderfile.FilecBox.setVisibility(View.GONE);
			}
			if(isChecked){
				viewHolderfile.FilecBox.setChecked(true);
			    mr.setIschecked(true);
			}else{
				viewHolderfile.FilecBox.setChecked(false);
				mr.setIschecked(false);
			}
			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
				viewHolderfile.txtFileMessageCount.setVisibility(View.VISIBLE);
				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
				sqLiteHelper.closeclose();
				viewHolderfile.txtFileMessageCount.setText(readNoCount+"");//未读
				
				
			}else{
				viewHolderfile.txtFileMessageCount.setVisibility(View.GONE);
			}
			viewHolderfile.txtFileMessageUserName.setText(mr.getBuddyNo());
			viewHolderfile.txtFileMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
			viewHolderfile.txtFileMessageText.setText(mr.getContent());
			viewHolderfile.FilecBox.setTag(mr);
			viewHolderfile.FilecBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					MessageRecords mrs=(MessageRecords)buttonView.getTag();
					mrs.setIschecked(isChecked);
				}
			});
		}
//		private void messageFileInit(View view,MessageRecords mr){
//			ViewHolderFile viewHolderImg=new ViewHolderFile();
//			viewHolderImg.txtImgMessageCount=(TextView)view.findViewById(R.id.txtImgMessageCount);
//			viewHolderImg.txtImgMessageUserName=(TextView)view.findViewById(R.id.txtImgMessageUserName);
//			viewHolderImg.txtImgMessageDate=(TextView)view.findViewById(R.id.txtImgMessageDate);
//			if(mr.getReceiveState()==GlobalConstant.MESSAGE_READ_NO){
//				viewHolderImg.txtImgMessageCount.setVisibility(View.VISIBLE);
//				SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
//				sqLiteHelper.open();
//				int readNoCount=sqLiteHelper.getMessageReadNoCountByUserNo(mr.getUserNo(), mr.getBuddyNo());
//				sqLiteHelper.closeclose();
//				viewHolderImg.txtImgMessageCount.setText(readNoCount+"");//临时未读
//			}else{
//				viewHolderImg.txtImgMessageCount.setVisibility(View.GONE);
//			}
//			//viewHolderImg.txtImgMessageUserName.setText(mr.getBuddyNo());
//			viewHolderImg.txtImgMessageUserName.setText(mr.getBuddyNo());
//			viewHolderImg.txtImgMessageDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
//		}
	}
	
	private class ViewHolderText{
		TextView txtTextMessageCount;
		TextView txtTextMessageUserName;
		TextView txtTextMessageDate;
		TextView txtTextMessageText;
		CheckBox txtcBox;
	}
	private class ViewHolderImg{
		TextView txtImgMessageCount;
		TextView txtImgMessageUserName;
		TextView txtImgMessageDate;
		CheckBox imgcBox;
	}
	private class ViewHolderAudio{
		TextView txtAudioMessageCount;
		TextView txtAudioMessageUserName;
		TextView txtAudioMessageDate;
		CheckBox AudiocBox;
	}
	private class ViewHolderVideo{
		TextView txtVideoMessageCount;
		TextView txtVideoMessageUserName;
		TextView txtVideoMessageDate;
		CheckBox VideocBox;
	}
	private class ViewHolderFile{
		TextView txtFileMessageCount;
		TextView txtFileMessageUserName;
		TextView txtFileMessageDate;
		TextView txtFileMessageText;
		CheckBox FilecBox;
	}
//	private class ViewHolderFile{
//		TextView txtImgMessageCount;
//		TextView txtImgMessageUserName;
//		TextView txtImgMessageDate;
//	}
	// 显示操作菜单窗口
	private void showWindow(View v) {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View view = layoutInflater.inflate(R.layout.layout_message_item_menu,
				null);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setOutsideTouchable(true);
		// 保存anchor在屏幕中的位置
		int[] location = new int[2];
		// 读取位置anchor座标
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40,
				location[1] - 30);

		btnMessageVoice = (TextView) view.findViewById(R.id.btnMessageVoice);
		btnMessageVideo = (TextView) view.findViewById(R.id.btnMessageVideo);
		btnMessageEdit = (TextView) view.findViewById(R.id.btnMessageEdit);
		btnMessageReply = (TextView) view.findViewById(R.id.btnMessageReply);
		btnMessageDelete = (TextView) view.findViewById(R.id.btnMessageDelete);

		btnMessageVoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(GlobalConstant.CALLHASVIDEO,GlobalConstant.CALLTYPE_VOICE);
				edit.apply();
				Log.e("*******Sws*****","voice 718");

				//通过广播拨号
				Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_OTHERCALLVOICE);
				callIntent.putExtra("callUserNo",menuCallNo);
				getActivity().sendBroadcast(callIntent);
				
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
				
			}
		});
		btnMessageVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(GlobalConstant.CALLHASVIDEO,GlobalConstant.CALLTYPE_VIDEO);
				edit.apply();
				Log.e("*******Sws*****","video 738");

				Intent callIntent=new Intent(GlobalConstant.ACTION_CALLING_OTHERCALLVIDEO);
				callIntent.putExtra("callUserNo", menuCallNo);
				getActivity().sendBroadcast(callIntent);
				
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		btnMessageEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager=getFragmentManager();
				FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
				ContactAddFragment contactAddFragment=(ContactAddFragment)fragmentManager.findFragmentByTag("contactadd");
				if(contactAddFragment==null){
					contactAddFragment=new ContactAddFragment();
				}
				
				contactAddFragment.setEditNo(menuCallNo);//编辑联系人
				if(!contactAddFragment.isAdded()){
					fragmentTransaction.add(R.id.messageRelativeLayout,contactAddFragment, "contactadd").commit();
				}
				
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		btnMessageReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				MessageListFragment messageListFragment = (MessageListFragment) fragmentManager
						.findFragmentByTag("fragmentlist");
				if (messageShowFragment == null) {
					messageShowFragment = new MessageShowFragment();
				}
				messageListFragment.setCallUserNo(menuCallNo);
				// fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
				if (!messageShowFragment.isAdded()) {
					fragmentTransaction.hide(messageListFragment).add(R.id.messageRelativeLayout,messageShowFragment, "fragmentshow").commit();
				} 
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		btnMessageDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
				sqLiteHelper.open();
				sqLiteHelper.deleteMessageRecordByUserNo(userNo,menuCallNo);
				sqLiteHelper.closeclose();

				messageRecordsList.clear();
				initMessageData();

				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});

	}
	
	public void refreshMessageList(){
		Log.e("Short message file transmission", "MessageListFragment=====799=== MessageActivity成功调用本界面的refreshMessageList()方法");
		if(messageRecordsList!=null){
			messageRecordsList.clear();
		}
		initMessageData();
	}
}
