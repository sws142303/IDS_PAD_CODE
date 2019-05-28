package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.SipUser;
import com.azkj.pad.photoalbum.ShowPhotoActivity;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.ui.MtcDelegate;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import cn.sword.SDK.MediaEngine;

public class MessageNewFragment extends Fragment {
	// 发送文本
	private RelativeLayout txtNewRelativeLayout;
	private LinearLayout message_new_foot, recorderLinearLayout;

	EditText txtMessageBoday;
	public TextView txtMessageNewContacts;
	ImageButton btnMessageSend;
	ImageButton btnMessageAddContact;
	ImageButton btnMessageImage;
	ImageButton btnMessageVoice;
	ImageButton btnMessageVideo;
	ImageButton btnMessageLocation;
	// 聊天对象
	private String userNo;

	private String szFile;
	private PTT_3G_PadApplication ptt_3g_PadApplication;

	// 发送图片
	private PopupWindow popupWindow;
	private TextView btnPhoto;
	private TextView btnAlbum;
	// 发送语音
	private Button btnRecoderHidden, btnRecoderBegin;
	private MediaRecorder mediaRecorder;
	private boolean isRecording = false;
	private boolean moveState = false;
	private static final int MIN_INTERVAL_TIME = 2000;// 2s
	private long startTime;
	private float buttonDownY;
	private String photoFileName;
	private String videoFilePath;
    private StringBuffer sb = new StringBuffer();
	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	private String msgBody;

	public String getMsgBody() {
		return msgBody;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
   //全局变量存储
	private SharedPreferences prefs;
	private SipUser sipUser;

	private String filenameforupload = "";
	private String fileidforupload = "";
	private String filepathforupload = "";


	@SuppressLint("CutPasteId") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		ptt_3g_PadApplication = (PTT_3G_PadApplication) getActivity()
				.getApplication();
		prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
		sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);

		View view = inflater.inflate(R.layout.fragment_message_new, container,
				false);

		txtNewRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.txtNewRelativeLayout);
		message_new_foot = (LinearLayout) view
				.findViewById(R.id.message_new_foot);
		recorderLinearLayout = (LinearLayout) view
				.findViewById(R.id.recorderLinearLayout);

		txtMessageBoday = (EditText) view.findViewById(R.id.txtMessageBoday);
		txtMessageNewContacts = (TextView) view
				.findViewById(R.id.txtMessageNewContacts);
		btnMessageSend = (ImageButton) view.findViewById(R.id.btnMessageSend);
		btnMessageAddContact = (ImageButton) view
				.findViewById(R.id.btnMessageAddContact);
		btnMessageImage = (ImageButton) view.findViewById(R.id.btnMessageImage);
		btnMessageVoice = (ImageButton) view.findViewById(R.id.btnMessageVoice);
		btnMessageVideo = (ImageButton) view.findViewById(R.id.btnMessageVideo);
		btnMessageLocation = (ImageButton) view.findViewById(R.id.btnMessageLocation);
		btnRecoderHidden = (Button) view.findViewById(R.id.btnRecoderHidden);
		btnRecoderBegin = (Button) view.findViewById(R.id.btnRecoderBegin);
		
		txtMessageBoday.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(getActivity(),
							getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (txtMessageBoday.getText().toString().endsWith("7MdHbLPugd")) {
					//语音
					String localFilePath =  txtMessageBoday.getText().toString().replace("7MdHbLPugd", "");
					ptt_3g_PadApplication.setFileType(2);
					ptt_3g_PadApplication.setLocalFilePath(localFilePath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
					String name = CommonMethod.getFileNameByPath(localFilePath, true);
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());

					///*String uri = MtcUri.Mtc_UriFormatX(
					//		GlobalConstant.PTT_MSG_SERVER_ID, false);*/
					//String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
					//		+ sipUser.getUsername() + "\r\ndst:"
					//		+ getUserNo() + "\r\nfileid:" + UUID.randomUUID()
					//		+ "\r\nfilename:" + name + "\r\nfiletype:3\r\n";
//
					///*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
					//		GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);*/
					//MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVoice);
					txtMessageBoday.setText("");
					txtMessageNewContacts.setText("");
				} else if (txtMessageBoday.getText().toString().endsWith("HURsIlso26")) {
					//图片
					String localFilePath =  txtMessageBoday.getText().toString().replace("HURsIlso26", "");
					ptt_3g_PadApplication.setFileType(1);
					ptt_3g_PadApplication.setLocalFilePath(localFilePath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
					String name=CommonMethod.getFileNameByPath(localFilePath,true);
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());
					///*String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);*/
					//String msgBodyString="req:upload\r\nsid:\r\nsrc:"+sipUser.getUsername()+"\r\ndst:"+ptt_3g_PadApplication.getOutUserNo()+"\r\nfileid:"+UUID.randomUUID()+"\r\nfilename:"+name+"\r\nfiletype:2\r\n";
					///*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);*/
					//MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
					txtMessageBoday.setText("");
					txtMessageNewContacts.setText("");
				} else if (txtMessageBoday.getText().toString().endsWith("0QRcoiHawK")) {
					//视频
					String localFilePath =  txtMessageBoday.getText().toString().replace("0QRcoiHawK", "");
					ptt_3g_PadApplication.setFileType(3);
					ptt_3g_PadApplication.setLocalFilePath(localFilePath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
					String name = CommonMethod.getFileNameByPath(localFilePath,true);
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());
					///*String uri = MtcUri.Mtc_UriFormatX(
					//		GlobalConstant.PTT_MSG_SERVER_ID, false);*/
					//String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
					//		+ sipUser.getUsername() + "\r\ndst:"
					//		+ getUserNo() + "\r\nfileid:" + UUID.randomUUID()
					//		+ "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
//
					///*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
					//		GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,
					//		msgBodyString);*/
					//MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVideo);
					txtMessageBoday.setText("");
					txtMessageNewContacts.setText("");
				} else if (txtMessageBoday.getText().toString().endsWith("5HW02YqqT3")) {
					//文件
					String localFilePath = txtMessageBoday.getText().toString().replace("5HW02YqqT3", "");
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());
					ptt_3g_PadApplication.setFileType(8);
					ptt_3g_PadApplication.setLocalFilePath(localFilePath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
					String name = CommonMethod.getFileNameByPath(localFilePath, true);
//					//String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
//					String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:8\r\n";
//					//MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
					txtMessageBoday.setText("");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
//				if (txtMessageNewContacts.getText().length() <= 0) {
//					Toast.makeText(getActivity(),
//							getString(R.string.title_message_send_empty),
//							Toast.LENGTH_SHORT).show();
//					return;
//				}
//				String myNo = sipUser.getUsername();
//				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
//					Toast.makeText(getActivity(),
//							getString(R.string.title_message_send_my),
//							Toast.LENGTH_SHORT).show();
//					return;
//
//				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 读取全局变量
		if (ptt_3g_PadApplication.getContactList() != null
				&& ptt_3g_PadApplication.getContactList().size() > 0) {
			setUserNo(CommonMethod.listToString(
					ptt_3g_PadApplication.getContactList(), ","));

            txtMessageNewContacts.setText(getUserNo());
			ptt_3g_PadApplication.setOutUserNo(getUserNo());
			ptt_3g_PadApplication.setAddContact(false);

			if (getMsgBody() != null && getMsgBody().length() > 0) {
				txtMessageBoday.setText(getMsgBody());
			}
		}

		btnMessageSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!ptt_3g_PadApplication.isNetConnection()){
					Toast.makeText(getActivity(),
							getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (txtMessageNewContacts.getText().toString().replace(" ", "").length() <= 0) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}

				String[] arr = txtMessageNewContacts.getText().toString().replace(" ", "").split(",");
				boolean isGo = true;
				for (int i = 0; i < arr.length; i++) {
					
					if(!CommonMethod.isNumeric(arr[i].trim()) && !arr[i].trim().equals("admin")) {
						isGo = false;
						break;
					}
				}
				if(!isGo){
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_blank),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				String myNo = sipUser.getUsername();
				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_my),
							Toast.LENGTH_SHORT).show();
					return;
				}

                String userno = txtMessageNewContacts.getText().toString();
                if (userno.indexOf(",") != -1){
                    String[] split = userno.split(",");
                    for (int i = 0; i < split.length; i++){
                        if (split[i].length() > 10){
                            Toast.makeText(getActivity(), "第"+(++i)+"个号码有误,请重新输入", Toast.LENGTH_SHORT).show();
                            return;
                        }

						if (split[i].equals(sipUser.getUsername())){
							Toast.makeText(getActivity(), "不能给自己发送信息", Toast.LENGTH_SHORT).show();
								return;
						}
                    }
                }else {
                    if (userno.length() > 10){
                        Toast.makeText(getActivity(), "号码有误,请重新输入", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

				// 旧方式发送短消息
				/*
				 * String[]
				 * selContact=txtMessageNewContacts.getText().toString()
				 * .split(","); //获取联系人，循环发送 for (String no : selContact) {
				 * if(no.length()<=0||no.equals(myNo)) break; String uri = null;
				 * if (no.contains("@")) { if (no.contains("sip:")) { uri = no;
				 * } else { uri = "sip:" + no; } } else { uri =
				 * MtcUri.Mtc_UriFormatX(no, false); }
				 * 
				 * MtcCli.Mtc_CliSendUserMsg(null, uri,
				 * MtcCliConstants.MTC_CLI_UMSG_PAGE,"text/plain",
				 * txtMessageBoday.getText().toString()); }
				 */

				// 使用新的方式发送短消息
				/*String uri = MtcUri.Mtc_UriFormatX(
						GlobalConstant.PTT_MSG_SERVER_ID, false);*/
				String msgBodyString = "req:send_msg\r\nsid:\r\nbody:"
						+ txtMessageBoday.getText().toString()
						+ "\r\nreceiver:"
						+ txtMessageNewContacts.getText().toString() + "\r\n";

				/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
						GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);*/
				MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
				ptt_3g_PadApplication.setMsgUserNo(txtMessageNewContacts
						.getText().toString());// 发送成功，消息接受使用
				ptt_3g_PadApplication.setMsgBody(txtMessageBoday.getText()
						.toString());

                setUserNo(txtMessageNewContacts.getText().toString());

				ptt_3g_PadApplication.setOutUserNo(getUserNo());
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				MessageNewFragment messageNewFragment = (MessageNewFragment) fragmentManager
						.findFragmentByTag("fragmentnew");
				MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager
						.findFragmentByTag("fragmentshow");

				// 跳转到显示列表页
				if (messageShowFragment == null) {
					messageShowFragment = new MessageShowFragment();
				}

				if (!messageShowFragment.isAdded()) {
					fragmentTransaction
							.hide(messageNewFragment)
							.add(R.id.messageRelativeLayout,
									messageShowFragment, "fragmentshow")
							.commit();
					
				} else {
					fragmentTransaction.hide(messageNewFragment)
							.show(messageShowFragment).commit();
				}

				setMsgBody(txtMessageBoday.getText().toString());
				txtMessageBoday.setText("");
				txtMessageNewContacts.setText("");

			}
		});
		btnMessageAddContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtMessageNewContacts.setText("");
				ptt_3g_PadApplication.getContactList().clear();
				ptt_3g_PadApplication.setAddContact(true);
				// 选择联系人
				Intent intent = new Intent(
						GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
				getActivity().sendBroadcast(intent);
			}
		});
		btnMessageImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtMessageNewContacts.getText().length() <= 0) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}
				String myNo = sipUser.getUsername();
				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_my),
							Toast.LENGTH_SHORT).show();
					return;
				}

				String userno = txtMessageNewContacts.getText().toString();
				if (userno.indexOf(",") != -1){
					String[] split = userno.split(",");
					for (int i = 0; i < split.length; i++){
						if (split[i].length() > 10){
							Toast.makeText(getActivity(), "第"+(++i)+"个号码有误,请重新输入", Toast.LENGTH_SHORT).show();
							return;
						}

						if (split[i].equals(sipUser.getUsername())){
							Toast.makeText(getActivity(), "不能给自己发送信息", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}else {
					if (userno.length() > 10){
						Toast.makeText(getActivity(), "号码有误,请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
				}

				boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
				if (aBoolean){
					ToastUtils.showToast(getActivity(),"当前有文件正在上传");
					return;
				}


				showWindow();
			}
		});
		btnMessageVoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtMessageNewContacts.getText().length() <= 0) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}
				String myNo = sipUser.getUsername();
				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_my),
							Toast.LENGTH_SHORT).show();
					return;
				}

				String userno = txtMessageNewContacts.getText().toString();
				if (userno.indexOf(",") != -1){
					String[] split = userno.split(",");
					for (int i = 0; i < split.length; i++){
						if (split[i].length() > 10){
							Toast.makeText(getActivity(), "第"+(++i)+"个号码有误,请重新输入", Toast.LENGTH_SHORT).show();
							return;
						}

						if (split[i].equals(sipUser.getUsername())){
							Toast.makeText(getActivity(), "不能给自己发送信息", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}else {
					if (userno.length() > 10){
						Toast.makeText(getActivity(), "号码有误,请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
				}

				boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
				if (aBoolean){
					ToastUtils.showToast(getActivity(),"当前有文件正在上传");
					return;
				}

				message_new_foot.setVisibility(View.GONE);
				txtNewRelativeLayout.setVisibility(View.GONE);
				recorderLinearLayout.setVisibility(View.VISIBLE);
			}
		});
		btnMessageVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtMessageNewContacts.getText().length() <= 0) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}
				String myNo = sipUser.getUsername();
				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_my),
							Toast.LENGTH_SHORT).show();
					return;
				}

				String userno = txtMessageNewContacts.getText().toString();
				if (userno.indexOf(",") != -1){
					String[] split = userno.split(",");
					for (int i = 0; i < split.length; i++){
						if (split[i].length() > 10){
							Toast.makeText(getActivity(), "第"+(++i)+"个号码有误,请重新输入", Toast.LENGTH_SHORT).show();
							return;
						}

						if (split[i].equals(sipUser.getUsername())){
							Toast.makeText(getActivity(), "不能给自己发送信息", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}else {
					if (userno.length() > 10){
						Toast.makeText(getActivity(), "号码有误,请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
				}



//				Intent intent = new Intent();
//				intent.setClass(getActivity(), RecoderActivity.class);
//				getActivity().startActivityForResult(intent,
//						GlobalConstant.RESULT_MESSAGE_VIDEO);


				boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
				if (aBoolean){
					ToastUtils.showToast(getActivity(),"当前有文件正在上传");
					return;
				}

				showVideoWindow();

			}
		});
		btnMessageLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtMessageNewContacts.getText().length() <= 0) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}
				String myNo = sipUser.getUsername();
				if (txtMessageNewContacts.getText().toString().equals(myNo)) {
					Toast.makeText(getActivity(),
							getString(R.string.title_message_send_my),
							Toast.LENGTH_SHORT).show();
					return;
				}
				String userno = txtMessageNewContacts.getText().toString();
				if (userno.indexOf(",") != -1){
					String[] split = userno.split(",");
					for (int i = 0; i < split.length; i++){
						if (split[i].length() > 10){
							Toast.makeText(getActivity(), "第"+(++i)+"个号码有误,请重新输入", Toast.LENGTH_SHORT).show();
							return;
						}

						if (split[i].equals(sipUser.getUsername())){
							Toast.makeText(getActivity(), "不能给自己发送信息", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}else {
					if (userno.length() > 10){
						Toast.makeText(getActivity(), "号码有误,请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
				}

				boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
				if (aBoolean){
					ToastUtils.showToast(getActivity(),"当前有文件正在上传");
					return;
				}

				Intent intent = new Intent();
				intent.setClass(getActivity(), MyLocationActivity.class);
				getActivity().startActivityForResult(intent,
						GlobalConstant.RESULT_MESSAGE_LOCATION);
			}
		});

		btnRecoderHidden.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				recorderLinearLayout.setVisibility(View.GONE);
				message_new_foot.setVisibility(View.VISIBLE);
				txtNewRelativeLayout.setVisibility(View.VISIBLE);
			}
		});

		/*
		 * btnRecoderBegin.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if(!isRecording){
		 * beginRecorder(); }else{ endRecorder(); } } });
		 */
		btnRecoderBegin.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					btnRecoderBegin.setBackgroundResource(R.drawable.voice_end);
					buttonDownY = event.getY();
					beginRecorder();
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					btnRecoderBegin
							.setBackgroundResource(R.drawable.voice_begin);
					if (!moveState) {
						endRecorder();
					} else {
						if (szFile.length() > 0) {
							File errorFile = new File(szFile);
							if (errorFile != null && errorFile.exists()) {
								errorFile.delete();
							}
						}
						moveState = false;
						mediaRecorder.stop();
						mediaRecorder.release();
						mediaRecorder = null;
						isRecording = false;
					}

					break;
				case MotionEvent.ACTION_MOVE:
					float moveY = event.getY();
					if (moveY - buttonDownY < -50) {
						moveState = true;
					}
					if (moveY - buttonDownY > -20) {
						moveState = false;
					}
					break;
				default:
					break;
				}

				return true;
			}
		});
		return view;
	}

	/*
    重新组装发送短信联系人的字符串，
    目的：去掉重复的联系人
*/
	private String getContactsStrings(String contactsString)
	{
		String [] temp = null;
		String recontactsString = "";
		Log.e("=========================处理前====================",contactsString);
		if (contactsString.equals(""))
			return "";
		if (!contactsString.contains(","))
			return contactsString;
		temp = contactsString.split(",");
		Set<String> set = new HashSet<>();
		for(int i=0;i<temp.length;i++){
			set.add(temp[i]);
		}
		String myNo = MtcDelegate.getLoginedUser();
		String[] arrayResult = (String[]) set.toArray(new String[set.size()]);
		for (String s: arrayResult){
			// 不能发送给自己
			if (!s.equals(myNo)){
				recontactsString = recontactsString + s +",";
			}
		}

		if (recontactsString.endsWith(",")){
			recontactsString.substring(0, recontactsString.length() - 1);
		}
		Log.e("=========================处理后====================",recontactsString);
		return recontactsString.toString();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(ptt_3g_PadApplication.isNetConnection() == false)
		{
			Toast.makeText(getActivity(),
					getString(R.string.info_network_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (popupWindow != null)
			popupWindow.dismiss();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == GlobalConstant.RESULT_MESSAGE_PHOTO) {    //拍照
				// 发送文件
				ptt_3g_PadApplication.setFileType(1);
				ptt_3g_PadApplication.setLocalFilePath(photoFileName);
				szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
				String name = CommonMethod.getFileNameByPath(
						photoFileName, true);
				setUserNo(txtMessageNewContacts.getText()
						.toString());
				ptt_3g_PadApplication.setOutUserNo(getUserNo());
//				String uri = sipUser.getUsername();
//				String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
//						+ sipUser.getUsername() + "\r\ndst:"
//						+ getUserNo() + "\r\nfileid:"
//						+ UUID.randomUUID() + "\r\nfilename:"
//						+ name + "\r\nfiletype:2\r\n";
//				MtcDelegate.log("新消息页面拍照申请上传msgBodyString:"
//						+ msgBodyString);
				/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
						GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,
						msgBodyString);*/
			//	MediaEngine.GetInstance().ME_SendMsg(uri, GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
				//TODO:申请上传文件
				applyUpload(getUserNo(), name, GlobalConstant.FileTypePic);

			} else if (requestCode == GlobalConstant.RESULT_MESSAGE_VIDEO) {     //视频


				String localPath = "";
				if (Build.MODEL.equals("3280")){
					//飞鸟10寸Pad  调用系统录像机录像
					localPath = videoFilePath;
				}else {
					localPath = data.getStringExtra("localpath");
				}

				if (localPath.length() > 0) {
					// 发送文件
					ptt_3g_PadApplication.setFileType(3);
					ptt_3g_PadApplication.setLocalFilePath(localPath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
					String name = CommonMethod.getFileNameByPath(localPath,
							true);
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());
					/*String uri = MtcUri.Mtc_UriFormatX(
							GlobalConstant.PTT_MSG_SERVER_ID, false);*/
					String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
							+ sipUser.getUsername() + "\r\ndst:"
							+ getUserNo() + "\r\nfileid:" + UUID.randomUUID()
							+ "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
					MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
					/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
							GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,
							msgBodyString);*/
					// MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					//TODO:申请上传文件
					applyUpload(getUserNo(), name, GlobalConstant.FileTypeVideo);
					txtMessageNewContacts.setText("");
				}
			} else if (requestCode == GlobalConstant.RESULT_MESSAGE_LOCATION) {
				String localPath = data.getStringExtra("localpath");
				if (localPath.length() > 0) {
					// 发送文件
					ptt_3g_PadApplication.setFileType(1);
					ptt_3g_PadApplication.setLocalFilePath(localPath);
					szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
					String name = CommonMethod.getFileNameByPath(localPath,
							true);
					setUserNo(txtMessageNewContacts.getText().toString());
					ptt_3g_PadApplication.setOutUserNo(getUserNo());
					/*String uri = MtcUri.Mtc_UriFormatX(
							GlobalConstant.PTT_MSG_SERVER_ID, false);*/
//					String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
//							+ sipUser.getUsername() + "\r\ndst:"
//							+ getUserNo() + "\r\nfileid:" + UUID.randomUUID()
//							+ "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
//					MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
					/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
							GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE,
							msgBodyString);*/
					// MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
					//TODO:申请上传文件
					applyUpload(getUserNo(), name, GlobalConstant.FileTypePic);
					txtMessageNewContacts.setText("");
				}
			}
//			else if (requestCode == GlobalConstant.SHOWRESULT_MESSAGE_VIDEO){   //录制视频
//
//				String localPath = data.getStringExtra("localpath");
//				if (localPath.length() > 0) {
//
//					setUserNo(txtMessageNewContacts.getText().toString());
//					//发送文件
//					ptt_3g_PadApplication.setOutUserNo(getUserNo());
//					ptt_3g_PadApplication.setFileType(3);
//					ptt_3g_PadApplication.setLocalFilePath(localPath);
//					szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
//
//					String name = CommonMethod.getFileNameByPath(localPath, true);
//					Log.e("===Sws测试文件", "570   name:" + name);
//					//String dst=CommonMethod.listToString(selContact, ",");
//					//setUserNo(dst);//对方号码
//					//String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
//					String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + getUserNo() + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
//					MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
//					//MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//					MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//				}
//			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private String getFileName()
	{
		String newFilePath = CommonMethod.getMessageFileDownPath(2);
		File photoFile = new File(newFilePath);
		if(!photoFile.exists()){
			photoFile.mkdirs();
		}
		newFilePath=newFilePath+ FormatController.getNewFileNameByDate()+ ".jpg";
		return newFilePath;
	}

	// 显示操作菜单窗口
	private void showWindow()
	{
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_photo, null);
		btnPhoto = (TextView) view.findViewById(R.id.btnPhoto);
		btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final AlertDialog imageDialog = builder.setView(view).create();
		imageDialog.show();

		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageDialog.dismiss();

				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				photoFileName=getFileName();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoFileName)));
				getActivity().startActivityForResult(intent,
						GlobalConstant.RESULT_MESSAGE_PHOTO);
			}
		});
		btnAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageDialog.dismiss();

				ptt_3g_PadApplication.setFileType(1);
				setUserNo(txtMessageNewContacts.getText().toString());
				ptt_3g_PadApplication.setOutUserNo(getUserNo());

				Intent intent = new Intent();
				intent.setClass(getActivity(), ShowPhotoActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}

	// 开始录音
	private void beginRecorder()
	{

		try {
			startTime = System.currentTimeMillis();
			File audioFileDir = new File(CommonMethod.getMessageFileDownPath(3));
			if (!audioFileDir.exists()) {
				audioFileDir.mkdirs();
			}
			String audioFilePath = CommonMethod.getMessageFileDownPath(3)
					+ FormatController.getNewFileNameByDate() + ".amr";
			ptt_3g_PadApplication.setFileType(2);
			ptt_3g_PadApplication.setLocalFilePath(audioFilePath);
			mediaRecorder = new MediaRecorder();
			// 设置音频录入源
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setAudioSamplingRate(8000);
			mediaRecorder.setAudioEncodingBitRate(16);
			// 设置录制音频的输出格式
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			// 设置音频的编码格式
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// 设置录制音频文件输出文件路径
			mediaRecorder.setOutputFile(audioFilePath);
			mediaRecorder.setOnErrorListener(new OnErrorListener() {
				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					// 发生错误，停止录制
					mediaRecorder.setOnErrorListener(null);
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					isRecording = false;
					Toast.makeText(getActivity(), "录音发生错误", Toast.LENGTH_SHORT)
							.show();
				}
			});
			// 准备、开始
			mediaRecorder.prepare();
			mediaRecorder.start();
			szFile = ptt_3g_PadApplication.getLocalFilePath();
			isRecording = true;
			Toast.makeText(getActivity(), "开始录音", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			MtcDelegate.log("录音异常:" + e.getMessage());
		}
	}

	// 结束录音
	private void endRecorder()
	{
		if (isRecording) {
			long intervalTime = System.currentTimeMillis() - startTime;
			if (intervalTime < MIN_INTERVAL_TIME) {
				Toast.makeText(getActivity(), "时间太短，录音失败", Toast.LENGTH_SHORT)
						.show();
				File errorFile = new File(szFile);
				if (errorFile.exists()) {
					errorFile.delete();
				}
			} else {
				Toast.makeText(getActivity(), "结束录音", Toast.LENGTH_SHORT)
						.show();
				if(ptt_3g_PadApplication.isNetConnection() == false)
				{
					Toast.makeText(getActivity(),
							getString(R.string.info_network_unavailable),
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 申请上传
				String name = CommonMethod.getFileNameByPath(szFile, true);
				setUserNo(txtMessageNewContacts.getText().toString());
				ptt_3g_PadApplication.setOutUserNo(getUserNo());

				/*String uri = MtcUri.Mtc_UriFormatX(
						GlobalConstant.PTT_MSG_SERVER_ID, false);*/
				String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
						+ sipUser.getUsername() + "\r\ndst:"
						+ getUserNo() + "\r\nfileid:" + UUID.randomUUID()
						+ "\r\nfilename:" + name + "\r\nfiletype:3\r\n";
				MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
				/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
						GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);*/
//				MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
				txtMessageNewContacts.setText("");

				applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVoice);
			}

			// 停止并释放资源
			mediaRecorder.setOnErrorListener(null);
			mediaRecorder.stop();
			mediaRecorder.release();
			
			mediaRecorder = null;
			isRecording = false;
			//MediaEngine.GetInstance().ME_CloseSndDev();

			// 恢复布局
			recorderLinearLayout.setVisibility(View.GONE);
			message_new_foot.setVisibility(View.VISIBLE);
			txtNewRelativeLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaRecorder != null) {
			if (isRecording) {
				mediaRecorder.stop();
			}

			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	private void showVideoWindow()
	{
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_photo, null);
		btnPhoto = (TextView) view.findViewById(R.id.btnPhoto);
		btnPhoto.setText("录制");
		btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);
		btnAlbum.setText("从本地选择");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final AlertDialog imageDialog = builder.setView(view).create();
		imageDialog.show();

		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageDialog.dismiss();
				if (Build.MODEL.equals("3280")) {
					Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
					videoFilePath = CommonMethod.getMessageFileDownPath(4) + FormatController.getNewFileNameByDate() + ".mp4";
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(videoFilePath)));
					getActivity().startActivityForResult(intent, GlobalConstant.RESULT_MESSAGE_VIDEO);
					return;
				}

				Intent intent=new Intent();
				intent.setClass(getActivity(), RecoderActivity.class);
				//getActivity().startActivityForResult(intent,GlobalConstant.SHOWRESULT_MESSAGE_VIDEO);
				getActivity().startActivityForResult(intent,GlobalConstant.RESULT_MESSAGE_VIDEO);
			}
		});

		btnAlbum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageDialog.dismiss();

				ptt_3g_PadApplication.setFileType(3);
				setUserNo(txtMessageNewContacts.getText().toString());
				ptt_3g_PadApplication.setOutUserNo(getUserNo());


				Intent intent = new Intent();
				intent.setClass(getActivity(), ShowVideoActivity.class);
				getActivity().startActivity(intent);

				//Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				//intent.setType("image/*");//图片列表
				//getActivity().startActivityForResult(intent,GlobalConstant.SHOWRESULT_MESSAGE_IMAGE);
			}
		});
	}

	private void applyUpload(String userNo, String pathname, final int type)
	{
		String[] contactStr = null;
		if (userNo.contains(",")){
			contactStr = userNo.split(",");
		}else {
			contactStr = new String[1];
			contactStr[0] =userNo;
		}

		for (String str:contactStr){
			Log.e("zzt","图片接收人："+str);
		}

		txtMessageNewContacts.setText("");
		//初始化
		filenameforupload = "";
		fileidforupload = "";
		filepathforupload = "";

		//TODO:申请上传文件
		boolean isRequestApplyUpload = MediaEngine.GetInstance().ME_ApplyUpload(pathname, type, contactStr, new MediaEngine.ME_ApplyUpload_callback(){
			@Override
			public void onCallBack(String filename, String fileid, String filepath) {
				Log.e("zzt","回调：filename:"+filename+", fileid:"+fileid+", filepath:"+filepath);
				filenameforupload = filename;
				fileidforupload = fileid;
				filepathforupload = filepath;

				if (!filepathforupload.equals("")){
					//TODO:设置文件正在上传
					boolean setUploading = MediaEngine.GetInstance().ME_SetFileUploading(fileidforupload);
					if (setUploading){
						Log.e("zzt", "设置文件正在上传----成功");
						Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADYES);
						intent.putExtra("filepath", filepathforupload);
						intent.putExtra("fileid", fileidforupload);
						intent.putExtra("filename", filenameforupload);
						getActivity().sendBroadcast(intent);
					}else {
						Log.e("zzt", "设置文件正在上传-----失败");
						SharedPreferences.Editor edit = prefs.edit();
						edit.putBoolean(GlobalConstant.isFileUpLoad,false);
						edit.commit();
					}
				}
			}
		});

		if (!isRequestApplyUpload){
			Toast.makeText(getActivity(), "申请上传失败， 请检查网络",Toast.LENGTH_SHORT).show();
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean(GlobalConstant.isFileUpLoad,false);
			edit.commit();
		}
	}
}
