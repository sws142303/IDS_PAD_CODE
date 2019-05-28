package com.azkj.pad.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azkj.pad.model.MessageRecords;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.photoalbum.ShowPhotoActivity;
import com.azkj.pad.utility.AsyncImageLoader;
import com.azkj.pad.utility.ButtonUtils;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.DensityUtil;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.LogUtils;
import com.azkj.pad.utility.MediaScanner;
import com.azkj.pad.utility.RoundProgressBarBean;
import com.azkj.pad.utility.RoundProgressBarWidthNumber;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.ToastUtils;
import com.azkj.sws.library.PullToRefreshBase;
import com.azkj.sws.library.PullToRefreshListView;
import com.juphoon.lemon.ui.MtcDelegate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cn.sword.SDK.MediaEngine;

import static com.azkj.pad.utility.GlobalConstant.MESSAGE_AUDIO_READ_YES;

/********
 * yuezs 隐藏 ice相关的
 *********/
/*import IceInternal.Timer;
import IceInternal.TimerTask;*/
@SuppressWarnings("ResourceType")
public class MessageShowFragment extends Fragment {
    private PullToRefreshBase.Mode CurrentMode;
    private RelativeLayout message_edit_layout;
    private LinearLayout message_new_foot, recorderLinearLayout;
    private PTT_3G_PadApplication ptt_3g_PadApplication;
    private TextView txtMessageUserNo;
    private String szFile;
    //用于下一条播放
    private HashMap<Integer, View> hashMap = new HashMap<>();
    //聊天记录
    private MessageListFragment messageListFragment;
    private PullToRefreshListView showMessageList;
   // private List<MessageRecords> messageRecordsList;
    private LinkedList<MessageRecords> messageRecordsList;
    private ChatSimpleAdapter chatSimpleAdapter;
    private MessageNewFragment messageNewFragment;
    //聊天对象
    private String myNo;
    private String userNo;
    private ImageButton btnMessageImage;
    private ImageButton btnMessageVoice;
    private ImageButton btnMessageVideo;
    private ImageButton btnMessageLocation;
    //下一条短信的索引
    private int nextPosition = -1;
    //发送消息
    private EditText messageBody;
    private ImageButton messagebtnsend;
    @SuppressWarnings("unused")
    private String msgBody;
    // 点击显示菜单
    public PopupWindow popupWindow;
    private TextView btnPhoto;
    private TextView btnAlbum;

    private Button btnRecoderHidden, btnRecoderBegin;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private boolean moveState = false;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;//语音是否播放
    private String playName = "";//当前播放语音名称
    private MessageRecords clickMessageRecords;
    private static final int MIN_INTERVAL_TIME = 2000;// 2s
    private long startTime;
    private float buttonDownY;
    private boolean hiddenFlag = true;//多选联系人，是否全部显示标签

    private MessageForwardBroadcastReceiver messageForwardBroadcastReceiver;
    private int forwardMsgType = 0;//转发消息类型（0文本1图片2语音3视频）
    private String forwardBody = "";//转发消息内容
    private String photoFileName;
    private String videoFilePath;
    private MediaScanner myMediaScanner;
    //返回按钮
    private ImageButton btnBack;
    //语音短信播放动画
    private ImageButton imgGameWord;//语音短信背景图容器
    private AnimationDrawable animDown = new AnimationDrawable();
    private Integer inoutInteger;//记录语音短信是接收还是发送
    private SharedPreferences prefs;
    private SipUser sipUser;

    private ReceiverForwardMsg receiverForwardMsg;
    private AudioManager audioManager;
    private ViewHolderAudioIn viewHolderAudioIn;

    //上传进度相关
    private ProgressReceiver progressReceiver;
    private HashMap<String, RoundProgressBarWidthNumber> roundProgressBarWidthNumberHashMap = new HashMap<>();
    private HashMap<String, RoundProgressBarBean> roundProgressBarBeanHashMap = new HashMap<>();
    private String fileName = null;
    private String progress = null;
    private static final int FILE_PROGRESS_START = 0;
    private static final int FILE_PROGRESS = 1;
    private static final int FILE_PROGRESS_STOPs = 2;
    private Dialog bottomDialog;
    private TextView dialogTxtMessageUserNo;


    //分页加载相关
    private int pageSize = 10;
    private int pageNum = 1;
    private LinkedList<MessageRecords> linkedListMessageList = new LinkedList<>();

    private StopMediaPlayerReceiver stopMediaPlayerReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pageNum = 1;
        pageSize = 10;

        ptt_3g_PadApplication = (PTT_3G_PadApplication) getActivity().getApplication();

        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        View view = inflater.inflate(R.layout.fragment_message_show, container, false);
        message_edit_layout = (RelativeLayout) view.findViewById(R.id.message_edit_layout);
        message_new_foot = (LinearLayout) view.findViewById(R.id.message_new_foot);
        recorderLinearLayout = (LinearLayout) view.findViewById(R.id.recorderLinearLayout);

        txtMessageUserNo = (TextView) view.findViewById(R.id.txtMessageUserNo);
        btnMessageImage = (ImageButton) view.findViewById(R.id.btnMessageImage);
        btnMessageVoice = (ImageButton) view.findViewById(R.id.btnMessageVoice);
        btnMessageVideo = (ImageButton) view.findViewById(R.id.btnMessageVideo);
        btnMessageLocation = (ImageButton) view.findViewById(R.id.btnMessageLocation);
        btnRecoderHidden = (Button) view.findViewById(R.id.btnRecoderHidden);
        btnRecoderBegin = (Button) view.findViewById(R.id.btnRecoderBegin);
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        //myNo=MtcDelegate.getLoginedUser();
        myNo = sipUser.getUsername();
        //接受聊天对象no
        messageListFragment = (MessageListFragment) getFragmentManager().findFragmentByTag("fragmentlist");

        if (messageListFragment != null) {
            userNo = messageListFragment.getCallUserNo();
            ptt_3g_PadApplication.setOutUserNo(userNo);
            txtMessageUserNo.setText(userNo);
        }
        messageNewFragment = (MessageNewFragment) getFragmentManager().findFragmentByTag("fragmentnew");
        if (messageNewFragment != null) {
            userNo = messageNewFragment.getUserNo();
            ptt_3g_PadApplication.setOutUserNo(userNo);
            msgBody = messageNewFragment.getMsgBody();
            txtMessageUserNo.setText(userNo);
        }

        ptt_3g_PadApplication.setExistenceMessageShow(true);//设置当前存在MessageShow界面
        ptt_3g_PadApplication.setMessageShowBuddyNo(userNo);//设置对方号码
        Log.e("=====********======","userNo : " + userNo);

        messageForwardBroadcastReceiver = new MessageForwardBroadcastReceiver();
        IntentFilter messageForwardIntentFilter = new IntentFilter();
        messageForwardIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGFORWARD);
        messageForwardIntentFilter.addAction(GlobalConstant.ACTION_MEDIASCANNER_FILE_DATA_SIZE);
        getActivity().registerReceiver(messageForwardBroadcastReceiver, messageForwardIntentFilter);


        receiverForwardMsg = new ReceiverForwardMsg();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.azkj.pad.forwardMsg");
        getActivity().registerReceiver(receiverForwardMsg, intentFilter);


        progressReceiver = new ProgressReceiver();
        IntentFilter receiverProgressFilter = new IntentFilter();
        receiverProgressFilter.addAction(GlobalConstant.FILE_PROGRESS_START);
        receiverProgressFilter.addAction(GlobalConstant.FILE_PROGRESS);
        receiverProgressFilter.addAction(GlobalConstant.FILE_PROGRESS_STOP);
        receiverProgressFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGSENDFILEERROR);
        getActivity().registerReceiver(progressReceiver, receiverProgressFilter);
        Log.e("=====dddd", "广播  registerReceiver");


        stopMediaPlayerReceiver = new StopMediaPlayerReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(GlobalConstant.StopMediaPlayerReceiver);
        getActivity().registerReceiver(stopMediaPlayerReceiver, intentFilter1);



        myMediaScanner = new MediaScanner(this.getActivity().getApplicationContext());

        txtMessageUserNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog(txtMessageUserNo.getText().toString().trim());
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent switchIntent = new Intent(
                        GlobalConstant.ACTION_MESSAGE_SWITCHFRAGMENT);
                getActivity().sendBroadcast(switchIntent);

                //如果当前正在播放语音 则停止
                stopPlaying();
            }
        });

        showMessageList = (PullToRefreshListView) view.findViewById(R.id.showMessageList);

        initMessageData();   //展示聊天纪录

        messageBody = (EditText) view.findViewById(R.id.messageBody);

        messageBody.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Log.e("MessageListFragment.setOnItemClickListener", "滑动1");
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(getActivity(),
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (messageBody.getText().toString().endsWith("7MdHbLPugd")) {
                    //语音
                    String localFilePath = messageBody.getText().toString().replace("7MdHbLPugd", "");
                    ptt_3g_PadApplication.setFileType(2);
                    ptt_3g_PadApplication.setLocalFilePath(localFilePath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
                    String name = CommonMethod.getFileNameByPath(localFilePath, true);
//                    /*String uri = MtcUri.Mtc_UriFormatX(
//                            GlobalConstant.PTT_MSG_SERVER_ID, false);*/
//                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:"
//                            + sipUser.getUsername() + "\r\ndst:"
//                            + userNo + "\r\nfileid:" + UUID.randomUUID()
//                            + "\r\nfilename:" + name + "\r\nfiletype:3\r\n";
//
//					/*MtcCli.Mtc_CliSendUserMsg(null, uri, 1,
//							GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);*/
////                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    messageBody.setText("");
                    applyUpload(ptt_3g_PadApplication.getMessageShowBuddyNo(), name, GlobalConstant.FileTypeVoice);
                } else if (messageBody.getText().toString().endsWith("HURsIlso26")) {
                    //图片
                    String localFilePath = messageBody.getText().toString().replace("HURsIlso26", "");
                    ptt_3g_PadApplication.setFileType(1);
                    ptt_3g_PadApplication.setLocalFilePath(localFilePath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();// 上传赋值
                    String name = CommonMethod.getFileNameByPath(localFilePath, true);
//                    //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
//                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
//                    //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
////                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    messageBody.setText("");
                    applyUpload(ptt_3g_PadApplication.getMessageShowBuddyNo(), name, GlobalConstant.FileTypePic);
                } else if (messageBody.getText().toString().endsWith("0QRcoiHawK")) {
                    //视频
                    String localFilePath = messageBody.getText().toString().replace("0QRcoiHawK", "");
                    ptt_3g_PadApplication.setOutUserNo(userNo);
                    ptt_3g_PadApplication.setFileType(3);
                    ptt_3g_PadApplication.setLocalFilePath(localFilePath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                    String name = CommonMethod.getFileNameByPath(localFilePath, true);
                    //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                   // String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
                    //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    messageBody.setText("");
                    applyUpload(ptt_3g_PadApplication.getMessageShowBuddyNo(), name, GlobalConstant.FileTypeVideo);
                } else if (messageBody.getText().toString().endsWith("5HW02YqqT3")) {
                    //文件
                    String localFilePath = messageBody.getText().toString().replace("5HW02YqqT3", "");
                    ptt_3g_PadApplication.setOutUserNo(userNo);
                    ptt_3g_PadApplication.setFileType(8);
                    ptt_3g_PadApplication.setLocalFilePath(localFilePath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                    String name = CommonMethod.getFileNameByPath(localFilePath, true);
                    //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:8\r\n";
                    //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    messageBody.setText("");
                    applyUpload(ptt_3g_PadApplication.getMessageShowBuddyNo(), name, GlobalConstant.FileTypePic);
                }

            }

        });

        messagebtnsend = (ImageButton) view.findViewById(R.id.messagebtnsend);

        messagebtnsend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ptt_3g_PadApplication.isNetConnection()) {
                    ToastUtils.showToast(getActivity(),
                            getString(R.string.info_network_unavailable));
                    return;
                }
                if (messageBody.getText().length() <= 0) {
                    ToastUtils.showToast(getActivity(), "请输入短信内容");
                    return;
                }

                initSendMsg(true, messageBody.getText().toString(), userNo);

                msgBody = messageBody.getText().toString();
            }
        });

        //发送图片
        btnMessageImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
                if (aBoolean){
                    ToastUtils.showToast(getActivity(),"当前有文件正在上传");
                    return;
                }

                showImgWindow();
            }
        });
        //录音
        btnMessageVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);
                if (aBoolean){
                    ToastUtils.showToast(getActivity(),"当前有文件正在上传");
                    return;
                }
                message_new_foot.setVisibility(View.GONE);
                message_edit_layout.setVisibility(View.GONE);
                recorderLinearLayout.setVisibility(View.VISIBLE);

            }
        });
        //视频
        btnMessageVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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

                boolean aBoolean = prefs.getBoolean(GlobalConstant.isFileUpLoad, false);

                if (aBoolean){
                    ToastUtils.showToast(getActivity(),"当前有文件正在上传");
                    return;
                }

                //一秒内多次点击视为无效点击
                if (ButtonUtils.isFastDoubleClick(R.id.btnMessageLocation,1000)){
                    ToastUtils.showToast(getActivity(),"无效点击");
                    return;
                }


                Intent intent = new Intent();
                intent.setClass(getActivity(), MyLocationActivity.class);
                getActivity().startActivityForResult(intent, GlobalConstant.SHOWRESULT_MESSAGE_LOCATION);

            }
        });
        btnRecoderHidden.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderLinearLayout.setVisibility(View.GONE);
                message_new_foot.setVisibility(View.VISIBLE);
                message_edit_layout.setVisibility(View.VISIBLE);
            }
        });


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
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        btnRecoderBegin.setBackgroundResource(R.drawable.voice_begin);
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

    //显示上传进度条
    private void showProgressDialog(String no)
    {

        if(bottomDialog != null) {
            bottomDialog.dismiss();
            bottomDialog = null;
        }

        if(bottomDialog == null) {
            bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_content_message_name, null);
            dialogTxtMessageUserNo = (TextView)contentView.findViewById(R.id.dialogTxtMessageUserNo);
            bottomDialog.setContentView(contentView);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(getActivity(), 16f);
            params.topMargin = DensityUtil.dp2px(getActivity(), 8f);
            contentView.setLayoutParams(params);
            dialogTxtMessageUserNo.setText(no);
            bottomDialog.setCanceledOnTouchOutside(true);
            bottomDialog.getWindow().setGravity(Gravity.TOP);
            bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            bottomDialog.show();
        }
    }


    //发送文本信息
    private void initSendMsg(boolean state, String msgBody, String userNo) {

        //使用新的方式发送短消息
        String msgBodyString = "req:send_msg\r\nsid:\r\nbody:" + msgBody + "\r\nreceiver:" + userNo + "\r\n";
        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);

        ptt_3g_PadApplication.setMsgUserNo(userNo);//发送成功，消息接受使用

        ptt_3g_PadApplication.setMsgBody(msgBody);

        if (state) {
            messageBody.setText("");
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //initMessageData();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("===Sws测试文件", "541   onResume:");
        //聊天列表展示
        //new AsyncInitMessageData().execute();
        new AsyncInitMessageData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);

        ptt_3g_PadApplication.setExistenceMessageShow(true);//设置当前存在MessageShow界面
        ptt_3g_PadApplication.setMessageShowBuddyNo(userNo);//设置对方号码
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("===Sws测试文件saddasdweff", "546   onActivityResult:");

        if (popupWindow != null)
            popupWindow.dismiss();
        if (ptt_3g_PadApplication.isNetConnection() == false) {
            ToastUtils.showToast(getActivity(),
                    getString(R.string.info_network_unavailable));
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GlobalConstant.SHOWRESULT_MESSAGE_PHOTO) {

                //发送文件
                ptt_3g_PadApplication.setFileType(1);
                ptt_3g_PadApplication.setLocalFilePath(photoFileName);
                szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                Log.e("====Sws测试文件上传进度条", "messageshowFragment   759   szFile:" + szFile);
                String name = CommonMethod.getFileNameByPath(photoFileName, true);
                Log.e("===Sws测试文件", "552   name:" + name);
                //String dst=CommonMethod.listToString(selContact, ",");
                //setUserNo(dst);//对方号码
                //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
                MtcDelegate.log("对讲页面拍照申请上传msgBodyString:" + msgBodyString);
                //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
            } else if (requestCode == GlobalConstant.SHOWRESULT_MESSAGE_VIDEO) {

                String localPath = "";
                if (Build.MODEL.equals("3280")){
                    //飞鸟10寸Pad  调用系统录像机录像
                    localPath = videoFilePath;
                }else {
                    localPath = data.getStringExtra("localpath");
                }
                if (localPath.length() > 0) {
                    //发送文件
                    ptt_3g_PadApplication.setOutUserNo(userNo);
                    ptt_3g_PadApplication.setFileType(3);
                    ptt_3g_PadApplication.setLocalFilePath(localPath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值

                    String name = CommonMethod.getFileNameByPath(localPath, true);
                    Log.e("===Sws测试文件", "570   name:" + name);
                    //String dst=CommonMethod.listToString(selContact, ",");
                    //setUserNo(dst);//对方号码
                    //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
                    MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
                    //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVideo);
                }
            } else if (requestCode == GlobalConstant.SHOWRESULT_MESSAGE_LOCATION) {

                String localPath = data.getStringExtra("localpath");
                if (localPath.length() > 0) {
                    //发送文件
                    ptt_3g_PadApplication.setOutUserNo(userNo);
                    ptt_3g_PadApplication.setFileType(1);
                    ptt_3g_PadApplication.setLocalFilePath(localPath);
                    szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                    String name = CommonMethod.getFileNameByPath(localPath, true);
                    Log.e("===Sws测试文件", "588   name:" + name);
                    //String dst=CommonMethod.listToString(selContact, ",");
                    //setUserNo(dst);//对方号码
                    //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
//                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
//                    MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
                    //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
 //                   MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFileName() {
        String newFilePath = CommonMethod.getMessageFileDownPath(2);
        File photoFile = new File(newFilePath);
        if (!photoFile.exists()) {
            photoFile.mkdirs();
        }

        newFilePath = newFilePath + FormatController.getNewFileNameByDate() + ".jpg";

        return newFilePath;

    }

    //点击视频
    private void showVideoWindow() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_photo, null);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btnPhoto);
        btnPhoto.setText("录制");
        TextView btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);
        btnAlbum.setText("从本地选择");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog imageDialog = builder.setView(view).create();
        imageDialog.show();

        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
                  if (Build.MODEL.equals("3280")){
                      Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                      videoFilePath= CommonMethod.getMessageFileDownPath(4)+ FormatController.getNewFileNameByDate()+".mp4";
                      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(videoFilePath)));
                      getActivity().startActivityForResult(intent, GlobalConstant.SHOWRESULT_MESSAGE_VIDEO);
                      return;
                  }

                Intent intent = new Intent();
                intent.setClass(getActivity(), RecoderActivity.class);
                getActivity().startActivityForResult(intent, GlobalConstant.SHOWRESULT_MESSAGE_VIDEO);
            }
        });

        btnAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
                ptt_3g_PadApplication.setFileType(3);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ShowVideoActivity.class);
                getActivity().startActivity(intent);

            }
        });
    }

    // 发送图片
    private void showImgWindow() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_photo, null);
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
                photoFileName = getFileName();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoFileName)));
                getActivity().startActivityForResult(intent, GlobalConstant.SHOWRESULT_MESSAGE_PHOTO);
            }
        });

        btnAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
                ptt_3g_PadApplication.setFileType(1);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ShowPhotoActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }

    //开始录音
    private void beginRecorder() {

        // TODO 先判断是否存在对讲  如果有直接挂断
        //TODO  判断是否存在单呼或者会议  如果存在 提示是否挂断

        if (audioManager == null){
            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        }

        //打开麦克风
        try {
            if (audioManager.isMicrophoneMute()) {

                audioManager.setMicrophoneMute(false);
                Log.e("集体测试","设置麦克风 messageshow 734  取消静音");
            }
            //释放audiotrack
            MediaEngine.GetInstance().ME_CloseSndDev();
            Thread.sleep(500);

            startTime = System.currentTimeMillis();

            File audioFileDir = new File(CommonMethod.getMessageFileDownPath(3));
            if (!audioFileDir.exists()) {
                audioFileDir.mkdirs();
            }
            String audioFilePath = CommonMethod.getMessageFileDownPath(3) + FormatController.getNewFileNameByDate() + ".amr";
            ptt_3g_PadApplication.setFileType(2);
            ptt_3g_PadApplication.setLocalFilePath(audioFilePath);
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            }
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setAudioEncodingBitRate(16);
            // 设置录制音频的输出格式
            //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置录制音频文件输出文件路径
            mediaRecorder.setOutputFile(audioFilePath);

            mediaRecorder.setOnErrorListener(new OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                   if (mediaRecorder != null){
                       // 发生错误，停止录制
                       mediaRecorder.setOnErrorListener(null);
                       mediaRecorder.stop();
                       mediaRecorder.release();
                       mediaRecorder = null;
                       isRecording = false;
                       ToastUtils.showToast(getActivity(), "录音发生错误");
                   }
                }
            });

            // 准备、开始
            mediaRecorder.prepare();

            // Thread.sleep(200);
            mediaRecorder.start();
            szFile = ptt_3g_PadApplication.getLocalFilePath();

            isRecording = true;
            ToastUtils.showToast(getActivity(), "开始录音");
        } catch (Exception e) {
            MtcDelegate.log("录音异常:" + e.getMessage());
        }
    }

    //结束录音
    private void endRecorder() {
        if (isRecording) {
            long intervalTime = System.currentTimeMillis() - startTime;
            if (intervalTime < MIN_INTERVAL_TIME) {
                ToastUtils.showToast(getActivity(), "时间太短，录音失败");
                File errorFile = new File(szFile);
                if (errorFile.exists()) {
                    errorFile.delete();
                }
            } else {
                ToastUtils.showToast(getActivity(), "结束录音");
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(getActivity(),
                            getString(R.string.info_network_unavailable));
                    return;
                }
                //申请上传
                String name = CommonMethod.getFileNameByPath(szFile, true);
                ptt_3g_PadApplication.setOutUserNo(userNo);
                //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + userNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:3\r\n";
                MtcDelegate.log("申请上传msgBodyString:" + msgBodyString);
                //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVoice);
            }

            if (mediaRecorder != null) {
                //停止并释放资源
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
            isRecording = false;

            //恢复布局
            recorderLinearLayout.setVisibility(View.GONE);
            message_new_foot.setVisibility(View.VISIBLE);
            message_edit_layout.setVisibility(View.VISIBLE);

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try{
                    chatSimpleAdapter = new ChatSimpleAdapter(getActivity(), messageRecordsList);
                    showMessageList.setAdapter(chatSimpleAdapter);
                    chatSimpleAdapter.notifyDataSetChanged();
                    showMessageList.onRefreshComplete();
                    showMessageList.getRefreshableView().setSelection(messageRecordsList.size() - 1);
                }catch (Exception e){
                    LogUtils.writeTxtToFile("Error   815:" + e.getMessage(),"/sdcard/Error/","error.txt");
                }

                Log.e("测试奔溃ListView","=========818");

            }else if (msg.what == 2){
                try{
                    Log.e("分页加载短消息","进入handler  messageRecordsList.size : " + messageRecordsList.size() + "\r\n" + "pageNum : " + pageNum);
                    chatSimpleAdapter = new ChatSimpleAdapter(getActivity(),messageRecordsList);
                    showMessageList.setAdapter(chatSimpleAdapter);
                    chatSimpleAdapter.notifyDataSetChanged();
                    showMessageList.onRefreshComplete();
                    if (pageNum == 0) {
                        showMessageList.getRefreshableView().setSelection(messageRecordsList.size());
                    } else if (pageNum >= 1) {
                        showMessageList.getRefreshableView().setSelection(messageRecordsList.size() - pageNum * pageSize + 1);
                    }
                    pageNum++;
                    Log.e("分页加载短消息","pageNum++ ： " + pageNum);
                }catch (Exception e){
                    LogUtils.writeTxtToFile("Error   815:" + e.getMessage(),"/sdcard/Error/","error.txt");
                }
                Log.e("测试奔溃ListView","=========835");

            }else if (msg.what == 3){







            }
        }

        ;
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
            sqLiteHelper.open();
            //更新所有未读信息为已读
            if (sqLiteHelper.getMessageReadNoCountByUserNo(myNo, userNo) > 0) {
                sqLiteHelper.updateMessageRecord(myNo, userNo);
            }

            List<MessageRecords> messageRecordsByUserNO = sqLiteHelper.getMessageRecordsByUserNO(myNo, userNo, 0, pageSize);
            if (messageRecordsByUserNO.size() > 0){
                List<MessageRecords> list = new ArrayList<>();
                for (int i = 0; i < messageRecordsByUserNO.size(); i++){
                    list.add(messageRecordsByUserNO.get(i));
                }
                messageRecordsList.addAll(list);
            }

            //messageRecordsList = sqLiteHelper.getMessageRecordsByUserNO(myNo, userNo, 0, pageSize);
            sqLiteHelper.closeclose();
            mHandler.sendEmptyMessage(1);
        }
    };

    private void initMessageData() {

        if (myNo == null || userNo == null)
            return;

        if (messageRecordsList == null) {
            messageRecordsList = new LinkedList<>();
        } else {
            messageRecordsList.clear();
            Log.e("======PageNum","clear 946");
//            if (chatSimpleAdapter != null){
//                chatSimpleAdapter.notifyDataSetChanged();
//            }
        }


        try {
            //聊天列表展示
            new AsyncInitMessageData().execute();
            //new AsyncInitMessageData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
            Log.e("测试奔溃ListView","===========882");
        }catch (Exception e){
            LogUtils.writeTxtToFile("Error   871:" + e.getMessage(),"/sdcard/Error/","error.txt");
        }


        //new Thread(runnable).start();

        showMessageList.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                try{
                    if (position < 1){
                        return;
                    }

                    Log.e("MessageShowFragment====869===", "MessageShowFragment消息列表点击事件");
                    //将读取状态保存到本地
                    SharedPreferences ssp = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
                    //存入数据
                    Editor editors = ssp.edit();
                    editors.putString("position", String.valueOf(position - 1));
                    editors.apply();
                    Log.e("当前Item的position", "position：" + position);

                    Log.e("当前Item的position", "点击短信内容");
                    MessageRecords mr = messageRecordsList.get(position - 1);
                    Log.e("Type", mr.getContentType() + "");
                    if (mr.getContentType() == GlobalConstant.MESSAGE_TEXT) {

                    } else if (mr.getContentType() == GlobalConstant.MESSAGE_IMG) {
//					Intent showImageIntent=new Intent(getActivity(),MessageImageShowActivity.class);
//					showImageIntent.putExtra("src", mr.getLocalFileUri());
//					getActivity().startActivity(showImageIntent);
                        getImageFileIntent(mr.getLocalFileUri());
                    } else if (mr.getContentType() == GlobalConstant.MESSAGE_AUDIO) {

                        if (imgGameWord != null && inoutInteger != null) {
                            //判断短信是接收还是发送
                            if (inoutInteger == GlobalConstant.LAYOUT_AUDIO_IN) {
                                imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
                            } else {
                                imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
                            }
                        }

                        //语音接收
                        if (viewHolderAudioIn == null) {
                            viewHolderAudioIn = new ViewHolderAudioIn();
                        }
                        //语音发送
                        ViewHolderAudioOut viewHolderAudioOut = new ViewHolderAudioOut();
                        //语音接收
                        if (mr.getLayout() == GlobalConstant.LAYOUT_AUDIO_IN) {

                            viewHolderAudioIn.reddian_in = (ImageView) view.findViewById(R.id.reddian_in);
                            viewHolderAudioIn.reddian_in.setVisibility(View.GONE);
                            viewHolderAudioIn.chatAudioInText = (ImageButton) view.findViewById(R.id.chatAudioInText);
                            imgGameWord = viewHolderAudioIn.chatAudioInText;
                            //将读取状态保存到本地
                            SharedPreferences sp = getActivity().getSharedPreferences(GlobalConstant.SHARED_PREFERENCE, 0);
                            //存入数据
                            Editor editor = sp.edit();
                            editor.putInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + mr.getId(), MESSAGE_AUDIO_READ_YES);
                            editor.commit();

                        } else {
                            viewHolderAudioOut.chatAudioOutText = (ImageButton) view.findViewById(R.id.chatAudioOutText);
                            imgGameWord = viewHolderAudioOut.chatAudioOutText;
                        }
                        Log.e("MessageShowFragment====924===", "mr.getLocalFileUri():" + mr.getLocalFileUri() + ", mr.getLayout():" + mr.getLayout());
                        startPlaying(mr.getLocalFileUri(), mr.getLayout(), position - 1);


                    } else if (mr.getContentType() == GlobalConstant.MESSAGE_VIDEO) {
                        Intent showVideoIntent = new Intent(getActivity(), PlayViewActivity.class);
                        showVideoIntent.putExtra("src", mr.getLocalFileUri());
                        getActivity().startActivity(showVideoIntent);

                    } else if (mr.getContentType() == GlobalConstant.MESSAGE_FILE) {
                        String abUri = mr.getLocalFileUri();
                        if (!"".equals(abUri)) {
                            openFile(abUri);
                        }
                    }
                }catch (Exception e){

                }

            }

        });
        showMessageList.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                if (arg2 < 1){
                    return false;
                }

                Log.e("showMessageList.setOnItemLongClickListener", "长按短信内容");
                clickMessageRecords = messageRecordsList.get(arg2 -1);
                showMessageShowMenuWindow(arg1);
                return true;
            }
        });

        showMessageList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;

                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), flags);
                CurrentMode = refreshView.getCurrentMode();
                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                //进行分页加载
                //new AsyncMessageDatePageNum().execute();
                new AsyncMessageDatePageNum().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
            }
        });

    }

    //YUEZS ADD 20150907  调用系统工具，打开文件
    private void openFile(String localpath) {
        String fileMimeType = discriminateFileType(localpath);
        if ("".equals(fileMimeType)) {
            ToastUtils.showToast(getActivity(), "系统不能识别该文件");
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(localpath));
        intent.setDataAndType(uri, fileMimeType);
        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showToast(getActivity(), "没有应用程序能打开该文件");
        }
    }

    //YUEZS ADD 20150907  调用系统工具，打开文件
    private String discriminateFileType(String pathString) {
        String fileMimeType = "";
        if (pathString.endsWith(".doc")) {
            fileMimeType = "application/vnd.ms-word";
        } else if (pathString.endsWith(".docx")) {
            fileMimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (pathString.endsWith(".xls")) {
            fileMimeType = "application/vnd.ms-excel";
        } else if (pathString.endsWith(".xlsx")) {
            fileMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (pathString.endsWith(".ppt")) {
            fileMimeType = "application/vnd.ms-powerpoint";

        } else if (pathString.endsWith(".pptx")) {
            fileMimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (pathString.endsWith(".txt")) {
            fileMimeType = "text/plain";
        } else if (pathString.endsWith(".html") || pathString.endsWith(".htm")) {
            fileMimeType = "text/html";
        } else if (pathString.endsWith(".mht")) {
            fileMimeType = "message/rfc822";
        } else if (pathString.endsWith(".chm")) {
            fileMimeType = "application/vnd.olivephone-chm";
        } else if (pathString.endsWith(".pdf")) {
            fileMimeType = " application/pdf";
        } else if (pathString.endsWith(".apk")) {
            fileMimeType = "application/vnd.android.package-archive";
        } else {
            fileMimeType = "*/*";
        }
        return fileMimeType;
    }

    //读取图片 YUEZS ADD 20150910
    private void getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        getActivity().startActivity(intent);
    }

    //加载短消息
    @SuppressWarnings("unused")
    private class AsyncInitMessageData extends AsyncTask<Object, Object, List<MessageRecords>> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected List<MessageRecords> doInBackground(Object... arg0) {

            pageNum = 1;


            LinkedList<MessageRecords> messageRecordses = new LinkedList<>();
            try{
                SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
                sqLiteHelper.open();
                //更新所有未读信息为已读
                if (sqLiteHelper.getMessageReadNoCountByUserNo(myNo, userNo) > 0) {
                    sqLiteHelper.updateMessageRecord(myNo, userNo);
                }

                //查询聊天纪录，返回一个集合
                //messageRecordsList = sqLiteHelper.getMessageRecordsByUserNO(myNo, userNo, 0, pageSize);
                ArrayList<MessageRecords> messageRecordsByUserNO = sqLiteHelper.getMessageRecordsByUserNO(myNo, userNo, 0, pageSize);
                ArrayList<MessageRecords> messageRecordsByUserNOClone = (ArrayList<MessageRecords>) messageRecordsByUserNO.clone();

                if (messageRecordsList.size() > 0){
                    messageRecordsList.clear();
                    Log.e("======PageNum","clear 1172");
                }
                if (messageRecordsByUserNO.size() > 0) {
                    for (MessageRecords mRecords : messageRecordsByUserNOClone) {
                        messageRecordsList.addFirst(mRecords);
                        messageRecordses.addFirst(mRecords);
                    }
                }
                // chatSimpleAdapter = new ChatSimpleAdapter(getActivity(), messageRecordsList);
                sqLiteHelper.closeclose();
            }catch (Exception e){
                LogUtils.writeTxtToFile("Error   1108:" + e.getMessage(),"/sdcard/Error/","error.txt");

            }
            return messageRecordses;
        }

        @Override
        protected void onPostExecute(List<MessageRecords> result) {
            super.onPostExecute(result);

            LinkedList<MessageRecords> messageRecordses = (LinkedList<MessageRecords>) result;

            try{
                chatSimpleAdapter = new ChatSimpleAdapter(getActivity(), messageRecordses);
                showMessageList.setAdapter(chatSimpleAdapter);
                chatSimpleAdapter.notifyDataSetChanged();
                showMessageList.onRefreshComplete();
                showMessageList.getRefreshableView().setSelection(messageRecordses.size() - 1);
            }catch (Exception e){
                LogUtils.writeTxtToFile("Error   1100:" + e.getMessage(),"/sdcard/Error/","error.txt");
            }
            Log.e("测试奔溃ListView","=========1121");


        }
    }

    //分页加载短消息
    private class AsyncMessageDatePageNum extends AsyncTask<Object,List<MessageRecords>,Object>{

        @Override
        protected List<MessageRecords> doInBackground(Object... objects) {

            Log.e("分页加载短消息","进入AsyncTask  pageNum : " + pageNum);
            if (pageNum > 0) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {

                }
            }

            SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
            sqLiteHelper.open();
            ArrayList<MessageRecords> list = sqLiteHelper.getMessageRecordsByUserNO(myNo, userNo, pageNum, pageSize);
            ArrayList<MessageRecords> cloneList = (ArrayList<MessageRecords>) list.clone();

            Log.e("分页加载短消息","开始添加数据  messageRecordsList.size : " + messageRecordsList.size());
            if (cloneList.size() > 0) {
                if (pageNum == 0) {
                    List<MessageRecords> mList = new ArrayList<>();
                    for (int i = cloneList.size() - 1; i >= 0; i--) {
                        mList.add(cloneList.get(i));
                    }

                    //mHandler.sendEmptyMessage(3);
                    if (messageRecordsList.size() > 0){

                        ;
                        Log.e("======PageNum","clear 1237");
                    }

                    messageRecordsList.addAll(mList);
                } else {
                    for (MessageRecords mRecords : cloneList) {
                        messageRecordsList.addFirst(mRecords);
                    }
                }
            }
            sqLiteHelper.closeclose();
            Log.e("分页加载短消息","添加完成数据  messageRecordsList.size : " + messageRecordsList.size());
            mHandler.sendEmptyMessage(2);
            Log.e("测试奔溃ListView","=========1164");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

//            showMessageList.setAdapter(chatSimpleAdapter);
//            chatSimpleAdapter.notifyDataSetChanged();
//            showMessageList.onRefreshComplete();
//            if (pageNum == 0) {
//                showMessageList.getRefreshableView().setSelection(messageRecordsList.size());
//            } else if (pageNum >= 1) {
//                showMessageList.getRefreshableView().setSelection(messageRecordsList.size() - pageNum * pageSize + 1);
//            }
//            pageNum++;
        }
    }


    @Override
    public void onDestroy()
    {
        getActivity().unregisterReceiver(messageForwardBroadcastReceiver);
        getActivity().unregisterReceiver(receiverForwardMsg);
        getActivity().unregisterReceiver(progressReceiver);
        getActivity().unregisterReceiver(stopMediaPlayerReceiver);
        Log.e("=====dddd", "取消注册");

        stopPlaying();

        ptt_3g_PadApplication.setExistenceMessageShow(false);//设置当前存在MessageShow界面
        ptt_3g_PadApplication.setMessageShowBuddyNo("-1");//设置对方号码

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        audioManager = null;
    }

    private void showMessageShowMenuWindow(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.layout_message_show_menu, null);

        popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popupWindow.setOutsideTouchable(true);
        // 保存anchor在屏幕中的位置
        int[] location = new int[2];
        // 读取位置anchor座标
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] + 40, location[1] - 50);

        TextView btnMessageCopy = (TextView) view.findViewById(R.id.btnMessageCopy);
        TextView btnMessageForward = (TextView) view.findViewById(R.id.btnMessageForward);
        TextView btnMessageDelete = (TextView) view.findViewById(R.id.btnMessageDelete);
        TextView btnMessageClear = (TextView) view.findViewById(R.id.btnMessageClear);

        btnMessageCopy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_TEXT_IN || clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_TEXT_OUT) {
                    //文本复制
                    cmb.setPrimaryClip(ClipData.newPlainText(null, clickMessageRecords.getContent()));
                } else if (clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_AUDIO_IN || clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_AUDIO_OUT) {
                    //文件复制
                    cmb.setPrimaryClip(ClipData.newPlainText(null, clickMessageRecords.getLocalFileUri() + "7MdHbLPugd"));
                } else if (clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_IMG_IN || clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_IMG_OUT) {
                    //文件复制
                    cmb.setPrimaryClip(ClipData.newPlainText(null, clickMessageRecords.getLocalFileUri() + "HURsIlso26"));
                } else if (clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_VIDEO_IN || clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_VIDEO_OUT) {
                    //文件复制
                    cmb.setPrimaryClip(ClipData.newPlainText(null, clickMessageRecords.getLocalFileUri() + "0QRcoiHawK"));
                } else if (clickMessageRecords.getLayout() == GlobalConstant.LAYOUT_FILE_IN) {
                    //文件YUEZS ADD 20150909
                    cmb.setPrimaryClip(ClipData.newPlainText(null, clickMessageRecords.getLocalFileUri() + "7MdHbLPugd"));
                }
                ToastUtils.showToast(getActivity(), "已经复制到剪贴板");
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        btnMessageForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                //文本
                if (clickMessageRecords.getContentType() == 0) {
                    ptt_3g_PadApplication.getContactList().clear();
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setMessageForward(true);
                    //选择联系人
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
                    getActivity().sendBroadcast(intent);

                    forwardMsgType = 0;
                    forwardBody = clickMessageRecords.getContent();

                } else if (clickMessageRecords.getContentType() == 2) {
                    //图片
                    ptt_3g_PadApplication.getContactList().clear();
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setMessageForward(true);
                    //选择联系人
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
                    getActivity().sendBroadcast(intent);

                    forwardMsgType = 2;
                    forwardBody = clickMessageRecords.getLocalFileUri();
                } else if (clickMessageRecords.getContentType() == 3) {
                    //音频
                    ptt_3g_PadApplication.getContactList().clear();
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setMessageForward(true);
                    //选择联系人
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
                    getActivity().sendBroadcast(intent);

                    forwardMsgType = 3;
                    forwardBody = clickMessageRecords.getLocalFileUri();
                } else if (clickMessageRecords.getContentType() == 4) {
                    //视频
                    ptt_3g_PadApplication.getContactList().clear();
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setMessageForward(true);
                    //选择联系人
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
                    getActivity().sendBroadcast(intent);

                    forwardMsgType = 4;
                    forwardBody = clickMessageRecords.getLocalFileUri();
                } else if (clickMessageRecords.getContentType() == 8) {
                    //文件
                    ptt_3g_PadApplication.getContactList().clear();
                    ptt_3g_PadApplication.setAddContact(true);
                    ptt_3g_PadApplication.setMessageForward(true);
                    //选择联系人
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGADDCONTACT);
                    getActivity().sendBroadcast(intent);

                    forwardMsgType = 8;
                    forwardBody = clickMessageRecords.getContent();
                }
            }
        });
        btnMessageDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
                sqLiteHelper.open();
                sqLiteHelper.deleteMessageRecord(clickMessageRecords.getId().toString());
                sqLiteHelper.closeclose();

                initMessageData();

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }

            }
        });
        btnMessageClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
                sqLiteHelper.open();
                sqLiteHelper.deleteMessageRecordByUserNo(clickMessageRecords.getUserNo(), clickMessageRecords.getBuddyNo());
                sqLiteHelper.closeclose();

                initMessageData();

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

    }

    //转发消息广播
    public class MessageForwardBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ptt_3g_PadApplication.isNetConnection() == false) {
                ToastUtils.showToast(getActivity(),
                        getString(R.string.info_network_unavailable));
                return;
            }

            String messageAction = intent.getAction();
            if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGFORWARD) {
                String forwardUserNo = CommonMethod.listToString(ptt_3g_PadApplication.getContactList(), ",");
                if (!"".equals(forwardUserNo)) {
//					Log.e("收信人", "收信人不能为空");
//					Toast.makeText(getActivity(), "收信人不能为空", Toast.LENGTH_SHORT).show();
//					return;

                    if (forwardMsgType == 0) {
					/*String[] selContact=forwardUserNo.split(",");
					for (String no : selContact) {
						if(no.length()<=0)
							break;
						String uri = null;
						if (no.contains("@")) {
							if (no.contains("sip:")) {
								uri = no;
							} else {
								uri = "sip:" + no;
							}
						} else {
							uri = MtcUri.Mtc_UriFormatX(no, false);
						}
						Log.e("消息转发uri地址", "消息转发uri地址" + uri);
						MtcCli.Mtc_CliSendUserMsg(null, uri, MtcCliConstants.MTC_CLI_UMSG_PAGE,"text/plain", forwardBody);
					}*/


                        ptt_3g_PadApplication.setMsgUserNo(forwardUserNo);//发送成功，消息接受使用
                        ptt_3g_PadApplication.setMsgBody(forwardBody);
                        Log.e("消息转发", forwardBody + ",成员：" + forwardUserNo);

                        Intent sendMsg = new Intent("com.azkj.pad.forwardMsg");
                        sendMsg.putExtra("forwarBody", forwardBody);
                        sendMsg.putExtra("forwardUserNo", forwardUserNo);
                        getActivity().sendBroadcast(sendMsg);


                        //String uri = MtcUri.Mtc_UriFormatX(forwardUserNo, false);
                        //MtcCli.Mtc_CliSendUserMsg(null, uri, MtcCliConstants.MTC_CLI_UMSG_PAGE,"text/plain", forwardBody);
                        //MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, forwardBody);
					/*SQLiteHelper sqLiteHelper=new SQLiteHelper(getActivity());
					sqLiteHelper.open();
					MessageRecords newMessageRecords=new MessageRecords();
					newMessageRecords.setUserNo(MtcDelegate.getLoginedUser());
					newMessageRecords.setBuddyNo(forwardUserNo);
					newMessageRecords.setContent(forwardBody);
					newMessageRecords.setContentType(GlobalConstant.MESSAGE_TEXT);
					newMessageRecords.setLocalFileUri("");
					newMessageRecords.setServerFileUri("");
					newMessageRecords.setLength(forwardBody.length());
					newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_OUT);
					Date nowDate = new Date();
					newMessageRecords.setSendDate(nowDate);
					newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_OK);
					newMessageRecords.setReceiveDate(nowDate);
					newMessageRecords.setReceiveState(GlobalConstant.MESSAGE_READ_YES);
					newMessageRecords.setLayout(GlobalConstant.LAYOUT_TEXT_OUT);

					sqLiteHelper.createMessageRecords(newMessageRecords);
					sqLiteHelper.closeclose();*/
                        msgBody = forwardBody;
                        ToastUtils.showToast(getActivity(), getString(R.string.title_message_send_success));
                    } else if (forwardMsgType == 2) {

                        //图片
                        ptt_3g_PadApplication.setOutUserNo(forwardUserNo);
                        ptt_3g_PadApplication.setFileType(1);
                        ptt_3g_PadApplication.setLocalFilePath(forwardBody);
                        szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值

                        String name = CommonMethod.getFileNameByPath(forwardBody, true);
                        //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                        String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + forwardUserNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
                        //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                        applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
                    } else if (forwardMsgType == 3) {
                        //语音
                        ptt_3g_PadApplication.setOutUserNo(forwardUserNo);
                        ptt_3g_PadApplication.setFileType(2);
                        ptt_3g_PadApplication.setLocalFilePath(forwardBody);
                        szFile = ptt_3g_PadApplication.getLocalFilePath();
                        String name = CommonMethod.getFileNameByPath(forwardBody, true);

                        //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                        String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + forwardUserNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:3\r\n";
                        //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                        applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVoice);
                    } else if (forwardMsgType == 4) {
                        //视频
                        ptt_3g_PadApplication.setOutUserNo(forwardUserNo);
                        ptt_3g_PadApplication.setFileType(3);
                        ptt_3g_PadApplication.setLocalFilePath(forwardBody);
                        szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                        String name = CommonMethod.getFileNameByPath(forwardBody, true);
                        //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                        String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + forwardUserNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:4\r\n";

                        //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                        applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVideo);
                    } else if (forwardMsgType == 8) {
                        // 文件类型暂时没有此功能
                        Log.e("文件类型", forwardBody);
                        ptt_3g_PadApplication.setOutUserNo(forwardUserNo);
                        ptt_3g_PadApplication.setFileType(4);
                        ptt_3g_PadApplication.setLocalFilePath(forwardBody);
                        szFile = ptt_3g_PadApplication.getLocalFilePath();//上传赋值
                        String name = CommonMethod.getFileNameByPath(forwardBody, true);

                        //String uri=MtcUri.Mtc_UriFormatX(GlobalConstant.PTT_MSG_SERVER_ID, false);
                        String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + forwardUserNo + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:8\r\n";
                        //MtcCli.Mtc_CliSendUserMsg(null, uri, 1,GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
//                        applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVoice);
                    }
                    //清空
                    forwardMsgType = 0;
                    forwardBody = "";
                    ptt_3g_PadApplication.setMessageForward(false);
                    ptt_3g_PadApplication.setAddContact(false);
                } else if (messageAction == GlobalConstant.ACTION_MEDIASCANNER_FILE_DATA_SIZE) {
//                    chatSimpleAdapter = new ChatSimpleAdapter(getActivity(), messageRecordsList);
//                    showMessageList.setAdapter(chatSimpleAdapter);
//
//                    showMessageList.getRefreshableView().setSelection(messageRecordsList.size() - 1);
//                    chatSimpleAdapter.notifyDataSetChanged();
//                    showMessageList.onRefreshComplete();
                    mHandler.sendEmptyMessage(1);
                    Log.e("测试奔溃ListView","=========1485");

                } else {
                    //返回短信列表
                    // 点击左侧短信导航，返回短信列表
                    Intent switchIntent = new Intent(
                            GlobalConstant.ACTION_MESSAGE_SWITCHFRAGMENT);
                    getActivity().sendBroadcast(switchIntent);
                }
            }
        }
    }

    //短信
    private void startPlaying(String fileName, Integer INOUTFLG, int position) {

        if (INOUTFLG == GlobalConstant.LAYOUT_AUDIO_IN) {
            //语音接收
            if (viewHolderAudioIn == null) {
                viewHolderAudioIn = new ViewHolderAudioIn();
            }
            View view = hashMap.get(position);
            if (view != null) {
                imgGameWord = (ImageButton) view.findViewById(R.id.chatAudioInText);
                viewHolderAudioIn.reddian_in = (ImageView) view.findViewById(R.id.reddian_in);
                viewHolderAudioIn.reddian_in.setVisibility(View.GONE);
            } else {
                return;
            }
        }

        Log.e("MessageShowFragment====1400===", " startPlaying中    fileName:" + fileName + ", INOUTFLG:" + INOUTFLG + ",playName:" + playName);
//        //fileName 为下载文件的本地路径
//        //INOUTFLG 为布局类型
//
//		/*//释放audiotrack
//		MediaEngine.GetInstance().ME_CloseSndDev();
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}*/
        try {
            inoutInteger = INOUTFLG;
            if (isPlaying && mediaPlayer != null) {
                if (playName.equals(fileName)) {
                    //如果正在播放，点击的是正在播放语音则停止
                    animDown.stop();
                    if (INOUTFLG == GlobalConstant.LAYOUT_AUDIO_IN) {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
                    } else {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
                    }
                    stopPlaying();
                    return;
                } else {
                    animDown.stop();
                    if (INOUTFLG == GlobalConstant.LAYOUT_AUDIO_IN) {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
                    } else {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
                    }
                    //正在播放，点击的不是正在播放语音，停止当前播放，播放后点击语音
                    stopPlaying();
                }
            }

            if (INOUTFLG == GlobalConstant.LAYOUT_AUDIO_IN) {
                imgGameWord.setBackgroundResource(R.anim.message_audio_inxml);
            } else {
                imgGameWord.setBackgroundResource(R.anim.message_audio_outxml);
            }
            animDown = (AnimationDrawable) imgGameWord.getBackground();
            animDown.start();
            //getActivity().setContentView(R.anim.message_audio_inxml);

            //播放视频，默认音量为0，设置音量大小

            if (audioManager == null){
                audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            }
            Log.e("扬声器状态", audioManager.isSpeakerphoneOn() + "");
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                Log.e("集体测试","测试扬声器 MessageShowFragment    1624   true");
            }

            //int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int currVolume = ptt_3g_PadApplication.getCurrentVolume();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);

            Log.e("当前播放音频模式", audioManager.getMode() + "");
            audioManager.setMode(ptt_3g_PadApplication.getGlobalAudioManagerMode());

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            playName = fileName;
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animDown.stop();
                    if (inoutInteger == GlobalConstant.LAYOUT_AUDIO_IN) {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
                    } else {
                        imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
                    }
                    stopPlaying();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //得到当前播放的position

                    SharedPreferences sp = null;

                    try{
                        sp = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);

                    }catch (Exception e){

                        Log.e("抛异常",e.getMessage());
                    }

                    if (sp == null) {
                        return;
                    }

                    String position = sp.getString("position", null);
                    Log.e("测试播放语音", "Main======1427======   position:" + position);
                    if (position != null) {
                        int positions = Integer.valueOf(position) + 1;
                        if (positions < messageRecordsList.size()) {
                            MessageRecords msgRecoder = messageRecordsList.get(positions);
                            if (msgRecoder.getContentType() != GlobalConstant.MESSAGE_AUDIO) {
                                return;
                            }
                            if (msgRecoder.getLayout() == GlobalConstant.LAYOUT_AUDIO_OUT) {
                                return;
                            }
                            int state = sp.getInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + msgRecoder.getId(), GlobalConstant.MESSAGE_AUDIO_READ_NO);
                            Log.e("测试播放语音", "Main======1432======   mr:" + msgRecoder.getReceiveState());
                            if (state == GlobalConstant.MESSAGE_AUDIO_READ_NO) {
                                Editor edit = sp.edit();
                                edit.putInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + msgRecoder.getId(), MESSAGE_AUDIO_READ_YES);
                                edit.apply();
                                //将读取状态保存到本地
                                SharedPreferences ssp = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
                                //存入数据
                                Editor editors = ssp.edit();
                                editors.putString("position", String.valueOf(positions));
                                editors.apply();
                                Log.e("当前Item的position", "position：" + positions);

                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(GlobalConstant.SHARED_PREFERENCE, 0);
                                sharedPreferences.edit().putInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + msgRecoder.getId(), GlobalConstant.MESSAGE_AUDIO_READ_YES).apply();

                                startPlaying(msgRecoder.getLocalFileUri(), msgRecoder.getLayout(), positions);
                            } else {
                                //下一条数据已读
                                return;
                            }
                        } else {
                            //表示当前为最后一条数据
                            return;
                        }
                    } else {
                        //position为空
                        return;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying() {

        Log.e("=====1524", "===========释放");
        if (mediaPlayer != null) {
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;

        }
    }

    private class ChatSimpleAdapter extends BaseAdapter {
        private AsyncImageLoader mAsyncImageLoader;
        private Context context;
        private List<MessageRecords> messages;
        public ChatSimpleAdapter(Context context, List<MessageRecords> messages) {
            this.context = context;
            this.messages = messages;
            mAsyncImageLoader = new AsyncImageLoader(context);
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
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //将读取状态保存到本地
            SharedPreferences ssp = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
            //存入数据
            Editor editors = ssp.edit();
            editors.putString("position", String.valueOf(position));
            editors.apply();
            Log.e("当前Item的position", "position：" + position);


            MessageRecords messageRecords = messages.get(position);
            Log.e("getView", "getView-start--" + messageRecords.getContentType() + ", name: " + messageRecords.getBuddyNo());
            Log.e("===Sws测试文件", "messageRecoders.getId:" + messageRecords.getId());

            int progressState = messageRecords.getProgressState();
//           if (roundProgressBarBeanHashMap.containsKey(messageRecords.getContent())){
//               progressState = roundProgressBarBeanHashMap.get(messageRecords.getContent()).isProgressState();
//               Log.e("===Sws测试Progress==","适配器中   1602行     isProgressState：" + progressState);
//           }

            int type = messageRecords.getLayout();
            ViewHolderTextIn viewHolderTextIn = null;
            ViewHolderTextout viewHolderTextout = null;
            ViewHolderImageIn viewHolderImageIn = null;
            ViewHolderImageOut viewHolderImageOut = null;
            ViewHolderAudioIn viewHolderAudioIn = null;
            ViewHolderAudioOut viewHolderAudioOut = null;
            ViewHolderVideoIn viewHolderVideoIn = null;
            ViewHolderVideoOut viewHolderVideoOut = null;
            //ViewHolderFileIN viewHolderFilein=null;
            if (convertView == null) {
                switch (type) {
                    case GlobalConstant.LAYOUT_TEXT_IN:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_text_in, null);
                        viewHolderTextIn = new ViewHolderTextIn();
                        messageTextInInit(convertView, messageRecords, viewHolderTextIn);
                        convertView.setTag(viewHolderTextIn);
                        break;
                    case GlobalConstant.LAYOUT_TEXT_OUT:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_text_out, null);
                        viewHolderTextout = new ViewHolderTextout();
                        messageTextOutInit(convertView, messageRecords, viewHolderTextout);
                        convertView.setTag(viewHolderTextout);
                        break;
                    case GlobalConstant.LAYOUT_IMG_IN:
                        //Log.e("getView", "IMG_IN-start");
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_img_in, null);
                        viewHolderImageIn = new ViewHolderImageIn();
                        messageImgInInit(convertView, messageRecords, viewHolderImageIn);
                        convertView.setTag(viewHolderImageIn);
                        //Log.e("getView", "IMG_IN-end");
                        break;
                    case GlobalConstant.LAYOUT_IMG_OUT:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_img_out, null);
                        viewHolderImageOut = new ViewHolderImageOut();
                        messageImgOutInit(convertView, messageRecords, viewHolderImageOut, progressState);
                        convertView.setTag(viewHolderImageOut);
                        break;
                    case GlobalConstant.LAYOUT_AUDIO_IN:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_audio_in, null);
                        viewHolderAudioIn = new ViewHolderAudioIn();
                        messageAudioInInit(convertView, messageRecords, viewHolderAudioIn);
                        convertView.setTag(viewHolderAudioIn);
                        break;
                    case GlobalConstant.LAYOUT_AUDIO_OUT:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_audio_out, null);
                        viewHolderAudioOut = new ViewHolderAudioOut();
                        messageAudioOutInit(convertView, messageRecords, viewHolderAudioOut, progressState);
                        convertView.setTag(viewHolderAudioOut);
                        break;
                    case GlobalConstant.LAYOUT_VIDEO_IN:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_video_in, null);
                        viewHolderVideoIn = new ViewHolderVideoIn();
                        messageVideoInInit(convertView, messageRecords, viewHolderVideoIn);
                        convertView.setTag(viewHolderVideoIn);
                        break;
                    case GlobalConstant.LAYOUT_VIDEO_OUT:
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_video_out, null);
                        viewHolderVideoOut = new ViewHolderVideoOut();
                        messageVideoOutInit(convertView, messageRecords, viewHolderVideoOut, progressState);
                        convertView.setTag(viewHolderVideoOut);
                        break;
//				case GlobalConstant.LAYOUT_FILE_IN:
//					Log.e("getView", "文件:"+messageRecords.getContentType());
//					convertView=LayoutInflater.from(context).inflate(R.layout.layout_message_file_in, null);
//					viewHolderFilein=new ViewHolderFileIN();
//					messageFileIninit(convertView,messageRecords,viewHolderFilein);
//					convertView.setTag(viewHolderFilein);
//					break;
                    default:
                        break;
                }
            } else {        //Log.e("getView.else", "进入else");
                switch (type) {
                    case GlobalConstant.LAYOUT_TEXT_IN:
                        viewHolderTextIn = (ViewHolderTextIn) convertView.getTag();
                        messageTextInInit(convertView, messageRecords, viewHolderTextIn);
                        break;
                    case GlobalConstant.LAYOUT_TEXT_OUT:
                        viewHolderTextout = (ViewHolderTextout) convertView.getTag();
                        messageTextOutInit(convertView, messageRecords, viewHolderTextout);
                        break;
                    case GlobalConstant.LAYOUT_IMG_IN:
                        viewHolderImageIn = (ViewHolderImageIn) convertView.getTag();
                        messageImgInInit(convertView, messageRecords, viewHolderImageIn);
                        break;
                    case GlobalConstant.LAYOUT_IMG_OUT:
                        viewHolderImageOut = (ViewHolderImageOut) convertView.getTag();
                        messageImgOutInit(convertView, messageRecords, viewHolderImageOut, progressState);
                        break;
                    case GlobalConstant.LAYOUT_AUDIO_IN:
                        viewHolderAudioIn = (ViewHolderAudioIn) convertView.getTag();
                        messageAudioInInit(convertView, messageRecords, viewHolderAudioIn);
                        break;
                    case GlobalConstant.LAYOUT_AUDIO_OUT:
                        viewHolderAudioOut = (ViewHolderAudioOut) convertView.getTag();
                        messageAudioOutInit(convertView, messageRecords, viewHolderAudioOut, progressState);
                        break;
                    case GlobalConstant.LAYOUT_VIDEO_IN:
                        viewHolderVideoIn = (ViewHolderVideoIn) convertView.getTag();
                        messageVideoInInit(convertView, messageRecords, viewHolderVideoIn);
                        break;
                    case GlobalConstant.LAYOUT_VIDEO_OUT:
                        viewHolderVideoOut = (ViewHolderVideoOut) convertView.getTag();
                        messageVideoOutInit(convertView, messageRecords, viewHolderVideoOut, progressState);
                        break;
//                case GlobalConstant.LAYOUT_FILE_IN:
//                	viewHolderFilein=(ViewHolderFileIN)convertView.getTag();
//					messageFileIninit(convertView,messageRecords,viewHolderFilein);
//					break;
                    default:
                        break;
                }
            }

            if (hashMap.containsKey(position)) {
                hashMap.remove(position);
                hashMap.put(position, convertView);
            } else {
                hashMap.put(position, convertView);
            }

            if (roundProgressBarWidthNumberHashMap.containsKey(messageRecords.getServerFileUri())) {
                roundProgressBarWidthNumberHashMap.remove(messageRecords.getServerFileUri());
                if (type == GlobalConstant.LAYOUT_IMG_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderImageOut.imgRoundProgressBar);

                } else if (type == GlobalConstant.LAYOUT_AUDIO_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderAudioOut.audioRoundProgressBar);
                } else if (type == GlobalConstant.LAYOUT_VIDEO_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderVideoOut.videoRoundProgressBar);
                }
            } else {
                if (type == GlobalConstant.LAYOUT_IMG_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderImageOut.imgRoundProgressBar);
                } else if (type == GlobalConstant.LAYOUT_AUDIO_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderAudioOut.audioRoundProgressBar);
                } else if (type == GlobalConstant.LAYOUT_VIDEO_OUT) {
                    roundProgressBarWidthNumberHashMap.put(messageRecords.getServerFileUri(), viewHolderVideoOut.videoRoundProgressBar);
                }
            }
            Log.e("===Sws测试文件", "1742     roundProgressBarWidthNumberHashMap.size:" + roundProgressBarWidthNumberHashMap.size());
            Log.e("getView", "getView-end");
            return convertView;
        }

        //文件接收初始化 YUEZS ADD 20150908
        private void messageFileIninit(View view, MessageRecords mr, ViewHolderFileIN viewHolderFilein) {

            Log.e("MessageShowFragment", "文件初始化：" + mr.getContent());
            viewHolderFilein.chatFileInUserImage = (ImageView) view.findViewById(R.id.chatFileInUserImage);
            viewHolderFilein.chatFileInText = (TextView) view.findViewById(R.id.chatFileInText);
            viewHolderFilein.chatFileInDate = (TextView) view.findViewById(R.id.chatFileInDate);

            viewHolderFilein.chatFileInUserImage.setBackgroundResource(R.drawable.head);
            viewHolderFilein.chatFileInText.setText(mr.getContent());
            viewHolderFilein.chatFileInDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
        }

        //文本接收初始化
        private void messageTextInInit(View view, MessageRecords mr, ViewHolderTextIn viewHolderTextIn) {
            viewHolderTextIn.chatTextInUserImage = (ImageView) view.findViewById(R.id.chatTextInUserImage);
            viewHolderTextIn.chatTextInText = (TextView) view.findViewById(R.id.chatTextInText);
            viewHolderTextIn.chatTextInDate = (TextView) view.findViewById(R.id.chatTextInDate);

            viewHolderTextIn.chatTextInUserImage.setBackgroundResource(R.drawable.head);
            viewHolderTextIn.chatTextInText.setText(mr.getContent());
            viewHolderTextIn.chatTextInDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
        }

        //文本发送初始化
        private void messageTextOutInit(View view, MessageRecords mr, ViewHolderTextout viewHolderTextout) {
            viewHolderTextout.chatTextOutText = (TextView) view.findViewById(R.id.chatTextOutText);
            viewHolderTextout.imgMessageFail = (ImageView) view.findViewById(R.id.imgMessageFail);
            viewHolderTextout.chatTextOutDate = (TextView) view.findViewById(R.id.chatTextOutDate);
            viewHolderTextout.chatTextOutText.setText(mr.getContent());
            if (mr.getSendState() == GlobalConstant.MESSAGE_SEND_FAILED) {
                viewHolderTextout.imgMessageFail.setVisibility(View.VISIBLE);
            } else {
                viewHolderTextout.imgMessageFail.setVisibility(View.GONE);
            }
            viewHolderTextout.chatTextOutDate.setText(FormatController.getDateMonthFormat(mr.getSendDate()));
        }

        //图片接受初始化
        private void messageImgInInit(View view, MessageRecords mr, ViewHolderImageIn viewHolderImageIn) {
            viewHolderImageIn.chatImgInUserImage = (ImageView) view.findViewById(R.id.chatImgInUserImage);
            viewHolderImageIn.chatImgInText = (ImageView) view.findViewById(R.id.chatImgInText);
            viewHolderImageIn.chatImgInDate = (TextView) view.findViewById(R.id.chatImgInDate);

            viewHolderImageIn.chatImgInUserImage.setBackgroundResource(R.drawable.head);

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(mr.getLocalFileUri(), options);
//            options.inSampleSize = computeSampleSize(options, -1, 128 * 128);//计算缩放比例
//            options.inPreferredConfig = Config.RGB_565;
//            options.inPurgeable = true;//让系统能及时回收内存
//            options.inInputShareable = true;
//            options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(mr.getLocalFileUri(), options);
//
//            viewHolderImageIn.chatImgInText.setImageBitmap(bitmap);
            glideLoadingImg(viewHolderImageIn.chatImgInText,mr.getLocalFileUri());
            viewHolderImageIn.chatImgInDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
            Log.e("MessageShowFragment", "FormatController.getDateMonthFormat(mr.getReceiveDate()):" + FormatController.getDateMonthFormat(mr.getReceiveDate()));
        }

        //图片发送初始化
        private void messageImgOutInit(View view, MessageRecords mr, ViewHolderImageOut viewHolderImageOut, int progressState) {
            viewHolderImageOut.imgMessageFail = (ImageView) view.findViewById(R.id.rrrrrrr);
            viewHolderImageOut.imgRoundProgressBar = (RoundProgressBarWidthNumber) view.findViewById(R.id.imgRoundProgressBar);
            setProgressShowOrHide(viewHolderImageOut.imgRoundProgressBar,progressState);
            viewHolderImageOut.chatImageOutDate = (TextView) view.findViewById(R.id.chatImageOutDate);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(mr.getLocalFileUri(), options);
//            options.inSampleSize = computeSampleSize(options, -1, 128 * 128);//计算缩放比例;
//            options.inPreferredConfig = Config.RGB_565;
//            options.inPurgeable = true;//让系统能及时回收内存
//            options.inInputShareable = true;
//            options.inJustDecodeBounds = false;
//            Bitmap bitmap = BitmapFactory.decodeFile(mr.getLocalFileUri(), options);
//            viewHolderImageOut.imgMessageFail.setImageBitmap(bitmap);
            glideLoadingImg(viewHolderImageOut.imgMessageFail,mr.getLocalFileUri());

			/*if(mr.getSendState()==GlobalConstant.MESSAGE_SEND_FAILED){
				viewHolderImageOut.imgMessageFail.setVisibility(View.VISIBLE);
			}else{
				viewHolderImageOut.imgMessageFail.setVisibility(View.GONE);
			}*/
            viewHolderImageOut.chatImageOutDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));



        }

        public int computeSampleSize(BitmapFactory.Options options,
                                     int minSideLength, int maxNumOfPixels) {
            int initialSize = computeInitialSampleSize(options, minSideLength,
                    maxNumOfPixels);
            int roundedSize;
            if (initialSize <= 8) {
                roundedSize = 1;
                while (roundedSize < initialSize) {
                    roundedSize <<= 1;
                }
            } else {
                roundedSize = (initialSize + 7) / 8 * 8;
            }
            return roundedSize;
        }

        private int computeInitialSampleSize(BitmapFactory.Options options,
                                             int minSideLength, int maxNumOfPixels) {
            double w = options.outWidth;
            double h = options.outHeight;
            int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                    .sqrt(w * h / maxNumOfPixels));
            int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                    Math.floor(w / minSideLength), Math.floor(h / minSideLength));
            if (upperBound < lowerBound) {
                // return the larger one when there is no overlapping zone.
                return lowerBound;
            }
            if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
                return 1;
            } else if (minSideLength == -1) {
                return lowerBound;
            } else {
                return upperBound;
            }
        }

        //语音接收初始化
        public void messageAudioInInit(View view, MessageRecords mr, ViewHolderAudioIn viewHolderAudioIn) {

            viewHolderAudioIn.chatAudioInUserImage = (ImageView) view.findViewById(R.id.chatAudioInUserImage);
            viewHolderAudioIn.chatAudioInText = (ImageButton) view.findViewById(R.id.chatAudioInText);
            viewHolderAudioIn.chatAudioInTime = (TextView) view.findViewById(R.id.chatAudioInTime);
            viewHolderAudioIn.chatAudioInDate = (TextView) view.findViewById(R.id.chatAudioInDate);
            viewHolderAudioIn.chatAudioInUserImage.setBackgroundResource(R.drawable.head);
            viewHolderAudioIn.chatAudioInText.setBackgroundResource(R.drawable.message_audio_in);
            viewHolderAudioIn.reddian_in = (ImageView) view.findViewById(R.id.reddian_in);
            viewHolderAudioIn.chatAudioInText.setTag(R.id.message_audio_in_Records, mr);
            viewHolderAudioIn.chatAudioInText.setTag(R.id.message_audio_in_Holder, viewHolderAudioIn);
            viewHolderAudioIn.chatAudioInText.setTag(R.id.message_audio_in_View, view);
//            viewHolderAudioIn.chatAudioInText.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    if (imgGameWord != null && inoutInteger != null) {
//                        if (inoutInteger == GlobalConstant.LAYOUT_AUDIO_IN) {
//                            imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
//                        } else {
//                            imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
//                        }
//                    }
//
//                    MessageRecords mr = (MessageRecords) v.getTag(R.id.message_audio_in_Records);
//                    ViewHolderAudioIn viewHolderAudioIn = (ViewHolderAudioIn) v.getTag(R.id.message_audio_in_Holder);
//                    View view = (View) v.getTag(R.id.message_audio_in_View);
//                    if (mr.getLayout() == GlobalConstant.LAYOUT_AUDIO_IN) {
//                        viewHolderAudioIn.reddian_in = (ImageView) view.findViewById(R.id.reddian_in);
//                        viewHolderAudioIn.reddian_in.setVisibility(View.GONE);
//                        viewHolderAudioIn.chatAudioInText = (ImageButton) view.findViewById(R.id.chatAudioInText);
//                        imgGameWord = viewHolderAudioIn.chatAudioInText;
//                        //将读取状态保存到本地
//                        SharedPreferences sp = getActivity().getSharedPreferences(GlobalConstant.SHARED_PREFERENCE, 0);
//                        //存入数据
//                        Editor editor = sp.edit();
//                        editor.putInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + mr.getId(), MESSAGE_AUDIO_READ_YES);
//                        editor.commit();
//
//                    }
//
//                    //startPlaying(mr.getLocalFileUri(),mr.getLayout());
//                    Log.e(mr.getLocalFileUri() + "", mr.getLayout() + "");
//                }
//            });
            SharedPreferences sp = getActivity().getSharedPreferences(GlobalConstant.SHARED_PREFERENCE, 0);
            int rvalue = sp.getInt(GlobalConstant.MESSAGE_AUDIO_READ_KEY + mr.getId(), 0);
            if (rvalue == MESSAGE_AUDIO_READ_YES) {
                viewHolderAudioIn.reddian_in.setVisibility(View.GONE);
            } else {
                viewHolderAudioIn.reddian_in.setVisibility(View.VISIBLE);
            }
            File file = new File(mr.getLocalFileUri());

            Log.e("LocalFileUri", "LocalFileUri= " + mr.getLocalFileUri());
            Log.e("LocalFileUri", "LocalFileUriSubString= " + mr.getLocalFileUri().substring(
                    mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length()));
            Log.e("LocalFileUri", "LocalFileUri time= " + getMP3Time(mr.getLocalFileUri().substring(
                    mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length())));


            if (file.exists()) {
                //viewHolderAudioIn.chatAudioInTime.setText(CommonMethod.getAmrDuration(file)+"''");
				/*viewHolderAudioIn.chatAudioInTime.setText(getMP3Time(mr.getLocalFileUri().substring(
						mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length())));*/

                int recordTime = getMP3Time(mr.getLocalFileUri().substring(
                        mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length()));


                if (recordTime == -1) {
                    viewHolderAudioIn.chatAudioInTime.setText(0 + "\"");
                    myMediaScanner.scanFile(mr.getLocalFileUri(), "audio/amr", null);
                } else {
                    viewHolderAudioIn.chatAudioInTime.setText(recordTime / 1000 + "\"");
                }

            }
            viewHolderAudioIn.chatAudioInDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));

        }

        //语音发送初始化
        private void messageAudioOutInit(View view, MessageRecords mr, ViewHolderAudioOut viewHolderAudioOut, int progressState) {
            viewHolderAudioOut.chatAudioOutText = (ImageButton) view.findViewById(R.id.chatAudioOutText);
            viewHolderAudioOut.audioRoundProgressBar = (RoundProgressBarWidthNumber) view.findViewById(R.id.audioRoundProgressBar);
            setProgressShowOrHide(viewHolderAudioOut.audioRoundProgressBar,progressState);
            viewHolderAudioOut.chatAudioOutTime = (TextView) view.findViewById(R.id.chatAudioOutTime);
            viewHolderAudioOut.imgMessageFail = (ImageView) view.findViewById(R.id.imgMessageFail);
            viewHolderAudioOut.chatAudioOutText.setBackgroundResource(R.drawable.message_audio_out);
            viewHolderAudioOut.chatAudioOutDate = (TextView) view.findViewById(R.id.chatAudioOutDate);
            viewHolderAudioOut.chatAudioOutText = (ImageButton) view.findViewById(R.id.chatAudioOutText);
            viewHolderAudioOut.chatAudioOutText.setTag(R.id.message_audio_out_Records, mr);
            viewHolderAudioOut.chatAudioOutText.setTag(R.id.message_audio_out_Holder, viewHolderAudioOut);
//            viewHolderAudioOut.chatAudioOutText.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    if (imgGameWord != null && inoutInteger != null) {
//                        if (inoutInteger == GlobalConstant.LAYOUT_AUDIO_IN) {
//                            imgGameWord.setBackgroundResource(R.drawable.message_audio_in);
//                        } else {
//                            imgGameWord.setBackgroundResource(R.drawable.message_audio_out);
//                        }
//                    }
//                    MessageRecords mr = (MessageRecords) v.getTag(R.id.message_audio_out_Records);
//                    ViewHolderAudioOut viewHolderAudioOut = (ViewHolderAudioOut) v.getTag(R.id.message_audio_out_Holder);
//                    if (mr.getLayout() == GlobalConstant.LAYOUT_AUDIO_OUT) {
//                        imgGameWord = viewHolderAudioOut.chatAudioOutText;
//
//                        //startPlaying(mr.getLocalFileUri(),mr.getLayout());
//                    }
//                }
//            });

            File file = new File(mr.getLocalFileUri());

            Log.e("LocalFileUri*****", "LocalFileUri= " + mr.getLocalFileUri());
            Log.e("LocalFileUri*****", "LocalFileUriSubString= " + mr.getLocalFileUri().substring(
                    mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length()));
            Log.e("LocalFileUri*****", "LocalFileUri time *****= " + getMP3Time(mr.getLocalFileUri().substring(
                    mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length())));

            if (file.exists()) {
                //viewHolderAudioOut.chatAudioOutTime.setText(CommonMethod.getAmrDuration(file)+"''");
				/*viewHolderAudioOut.chatAudioOutTime.setText(getMP3Time(mr.getLocalFileUri().substring(
						mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length())));*/


                int recordTime = getMP3Time(mr.getLocalFileUri().substring(
                        mr.getLocalFileUri().lastIndexOf("/") + 1, mr.getLocalFileUri().length()));


                if (recordTime == -1) {
                    viewHolderAudioOut.chatAudioOutTime.setText(0 + "\"");
                    myMediaScanner.scanFile(mr.getLocalFileUri(), "audio/amr", null);
                } else {
                    viewHolderAudioOut.chatAudioOutTime.setText(recordTime / 1000 + "\"");
                }
            }

            if (mr.getSendState() == GlobalConstant.MESSAGE_SEND_FAILED) {
                viewHolderAudioOut.imgMessageFail.setVisibility(View.VISIBLE);
            } else {
                viewHolderAudioOut.imgMessageFail.setVisibility(View.GONE);
            }
            viewHolderAudioOut.chatAudioOutDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));

        }

        //视频接受初始化
        private void messageVideoInInit(View view, MessageRecords mr, ViewHolderVideoIn viewHolderVideoIn) {
            viewHolderVideoIn.chatVideoInUserImage = (ImageView) view.findViewById(R.id.chatVideoInUserImage);
            viewHolderVideoIn.chatVideoInText = (ImageView) view.findViewById(R.id.chatVideoInText);
            viewHolderVideoIn.chatVideoInDate = (TextView) view.findViewById(R.id.chatVideoInDate);
            viewHolderVideoIn.chatVideoDCImage = (ImageView) view.findViewById(R.id.list_say_imageview_dc);
            viewHolderVideoIn.chatVideoDCImage.setVisibility(View.VISIBLE);
            viewHolderVideoIn.chatVideoInUserImage.setBackgroundResource(R.drawable.head);
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mr.getLocalFileUri(), Thumbnails.MICRO_KIND);
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 60, 60, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//            viewHolderVideoIn.chatVideoInText.setImageBitmap(bitmap);
            loadVideoImg(viewHolderVideoIn.chatVideoInText,mr.getLocalFileUri());
            //viewHolderVideoIn.chatVideoInText.setBackgroundResource(R.drawable.msg_video_bg);
            viewHolderVideoIn.chatVideoInDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
//			final String fileName=mr.getLocalFileUri();
//			//yuezs add 2015-12-28///////////////////
//			viewHolderVideoIn.chatVideoInText.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(Intent.ACTION_VIEW);
//		            //intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + "/"+v.getContext().toString()), "video/mp4");
//					intent.setDataAndType(Uri.parse(fileName), "video/mp4");
//		            startActivity(intent);
//				}
//			});

            /////////////////////////////////////////////
        }

        //视频发送初始化
        private void messageVideoOutInit(View view, MessageRecords mr, ViewHolderVideoOut viewHolderVideoOut, int progressState) {
            viewHolderVideoOut.chatVideoOutText = (ImageView) view.findViewById(R.id.chatVideoOutText);
            viewHolderVideoOut.videoRoundProgressBar = (RoundProgressBarWidthNumber) view.findViewById(R.id.videoRoundProgressBar);
            setProgressShowOrHide(viewHolderVideoOut.videoRoundProgressBar,progressState);
            viewHolderVideoOut.imgMessageFail = (ImageView) view.findViewById(R.id.imgMessageFail);
            viewHolderVideoOut.chatVideoOutDate = (TextView) view.findViewById(R.id.chatVideoOutDate);
            viewHolderVideoOut.chatVideoOutDCImage = (ImageView) view.findViewById(R.id.list_say_imageview_dc);
            viewHolderVideoOut.chatVideoOutDCImage.setVisibility(View.VISIBLE);
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mr.getLocalFileUri(), Thumbnails.MICRO_KIND);
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 60, 60, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//            viewHolderVideoOut.chatVideoOutText.setImageBitmap(bitmap);
            loadVideoImg(viewHolderVideoOut.chatVideoOutText,mr.getLocalFileUri());
            //viewHolderVideoOut.chatVideoOutText.setBackgroundResource(R.drawable.msg_video_bg);
            if (mr.getSendState() == GlobalConstant.MESSAGE_SEND_FAILED) {
                viewHolderVideoOut.imgMessageFail.setVisibility(View.VISIBLE);
            } else {
                viewHolderVideoOut.imgMessageFail.setVisibility(View.GONE);
            }
            viewHolderVideoOut.chatVideoOutDate.setText(FormatController.getDateMonthFormat(mr.getReceiveDate()));
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= messages.size()) {
                return 0;
            }
            MessageRecords mRecords = messages.get(position);
            return mRecords.getLayout();
        }

        @Override
        public int getViewTypeCount() {
            return 8;
        }

        //设置文件显示隐藏
        private void setProgressShowOrHide(RoundProgressBarWidthNumber roundProgressBarWidthNumber,int progressState){

            switch (progressState){
                case GlobalConstant.MESSAGE_PROGRESS_COMPLETE:
                    roundProgressBarWidthNumber.setVisibility(View.GONE);
                    break;
                case GlobalConstant.MESSAGE_PROGRESS_NoCOMPLETE:
                    roundProgressBarWidthNumber.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private void loadVideoImg(ImageView imageView,String path){

            Bitmap bitmap = mAsyncImageLoader.getBitmap(path);
            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            } else
            {
                imageView.setImageResource(R.drawable.ptt_image_laoding);
                mAsyncImageLoader.loadPicImage(path, new AsyncImageLoader.ImageCallback()
                {
                    @Override
                    public void imageCallback(Bitmap bitmap, String url,
                                              ImageView v)
                    {
                        if (bitmap != null)
                        {
                            v.setImageBitmap(bitmap);
                        } else
                        {
                            v.setImageResource(R.drawable.pic_failure);
                        }
                    }
                }, imageView, 4, 1);
            }

        }

        //加载图片
        private void glideLoadingImg(ImageView imageView,String path){
            //Log.e("测试Glide","path:" + path);
//            RequestListener requestListener = new RequestListener() {
//                @Override
//                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
//                    //Log.e("测试Glide","Exception:" + e.getMessage());
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    return false;
//                }
//            };
//            Glide.with(getActivity()).load(path).listener(requestListener).into(imageView);

            Bitmap bitmap = mAsyncImageLoader.getBitmap(path);
            if (bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            } else
            {
                imageView.setImageResource(R.drawable.ptt_image_laoding);
                mAsyncImageLoader.loadPicImage(path, new AsyncImageLoader.ImageCallback()
                {
                    @Override
                    public void imageCallback(Bitmap bitmap, String url,
                                              ImageView v)
                    {
                        if (bitmap != null)
                        {
                            v.setImageBitmap(bitmap);
                        } else
                        {
                            v.setImageResource(R.drawable.pic_failure);
                        }
                    }
                }, imageView, 2, 100);
            }



        }

    }

    //文本接收
    private class ViewHolderTextIn {
        ImageView chatTextInUserImage;
        TextView chatTextInText;
        TextView chatTextInDate;
    }

    //文本发送
    private class ViewHolderTextout {
        TextView chatTextOutText;
        ImageView imgMessageFail;
        TextView chatTextOutDate;
    }

    //语音接收
    private class ViewHolderAudioIn {
        ImageView chatAudioInUserImage;
        ImageButton chatAudioInText;
        TextView chatAudioInTime;
        TextView chatAudioInDate;
        ImageView reddian_in;
    }

    //语音发送
    private class ViewHolderAudioOut {
        RoundProgressBarWidthNumber audioRoundProgressBar;
        ImageButton chatAudioOutText;
        TextView chatAudioOutTime;
        ImageView imgMessageFail;
        TextView chatAudioOutDate;
    }

    //图片接收
    private class ViewHolderImageIn {
        ImageView chatImgInUserImage;
        ImageView chatImgInText;
        TextView chatImgInDate;
    }

    //图片发送
    private class ViewHolderImageOut {
        RoundProgressBarWidthNumber imgRoundProgressBar;
        ImageView imgMessageFail;
        TextView chatImageOutDate;
    }

    //视频接收
    private class ViewHolderVideoIn {
        ImageView chatVideoInUserImage;
        ImageView chatVideoInText;
        TextView chatVideoInDate;
        ImageView chatVideoDCImage;
    }

    //视频发送
    private class ViewHolderVideoOut {
        RoundProgressBarWidthNumber videoRoundProgressBar;
        ImageView chatVideoOutText;
        ImageView imgMessageFail;
        TextView chatVideoOutDate;
        ImageView chatVideoOutDCImage;
    }

    //文件接收
    private class ViewHolderFileIN {
        ImageView chatFileInUserImage;
        TextView chatFileInText;
        TextView chatFileInDate;
    }

    public void refreshMessageShow() {
        //适当延时 确保文件下载并写入本地完成 在进行界面展示
        try {
            Thread.sleep(900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initMessageData();
    }

    /**
     * @param
     * @return 音频文件的秒
     * @author dc 得到音频文件的时间长度
     */
    private int getMP3Time(String address) {
        String[] projection =
                {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};
        Cursor cursor = null;
        long duration = -1;
        try {
            cursor = this.getActivity().getApplicationContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,// 需要查询的列
                    MediaStore.Audio.Media.DISPLAY_NAME + " = '" + address
                            + "'", null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor.moveToNext()) {
                duration = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION));
                Log.e("duration", "duration=" + duration);
            }
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null)
                cursor.close();
        }
        int durationInt = (int) duration;
        Log.e("durationInt", "durationInt=" + durationInt);
        return duration - durationInt >= 0.5 ? durationInt + 1 : durationInt;

    }

    class ReceiverForwardMsg extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String forwarBody = intent.getStringExtra("forwarBody");
            String forwardUserNo = intent.getStringExtra("forwardUserNo");
            initSendMsg(false, forwarBody, forwardUserNo);
        }
    }

    //用于更新Progress
    class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String fileServicePath = intent.getStringExtra("fileServicePath");

            if (fileServicePath == null){
                return;
            }

            if (roundProgressBarWidthNumberHashMap.size() <= 0){
                return;
            }

            if (!roundProgressBarWidthNumberHashMap.containsKey(fileServicePath)){
                return;
            }


            if (intent.getAction() == GlobalConstant.FILE_PROGRESS) {         //文件上传进度

                String progresss = intent.getStringExtra("progress");

                RoundProgressBarWidthNumber roundProgressBarWidthNumber = roundProgressBarWidthNumberHashMap.get(fileServicePath);
                roundProgressBarWidthNumber.setProgress(Integer.valueOf(progresss));

            } else if (intent.getAction() == GlobalConstant.FILE_PROGRESS_START) {      //文件开始上传


                String fileNames = intent.getStringExtra("fileName");
                fileName = fileNames;

                RoundProgressBarWidthNumber roundProgressBarWidthNumber = roundProgressBarWidthNumberHashMap.get(fileServicePath);
                if (roundProgressBarWidthNumber.getVisibility() != View.VISIBLE){
                    roundProgressBarWidthNumber.setVisibility(View.VISIBLE);
                }
                roundProgressBarWidthNumber.setMax(100);


            } else if (intent.getAction() == GlobalConstant.FILE_PROGRESS_STOP) {         //文件上传完成

                ToastUtils.showToast(getActivity(), getString(R.string.title_message_send_success));
                RoundProgressBarWidthNumber roundProgressBarWidthNumber = roundProgressBarWidthNumberHashMap.get(fileServicePath);
                if (roundProgressBarWidthNumber.getVisibility() != View.GONE){
                    roundProgressBarWidthNumber.setVisibility(View.GONE);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e("捕获异常","2416    Error:" + e.getMessage());
                    LogUtils.writeTxtToFile("Error  2416:" + e.getMessage(),"/sdcard/Error/","error.txt");
                    e.printStackTrace();
                }
                if (messageRecordsList != null){
                    if (messageRecordsList.size() > 0){
                        messageRecordsList.clear();
                        Log.e("======PageNum","clear 2549");
                        if (chatSimpleAdapter != null){
                            chatSimpleAdapter.notifyDataSetChanged();
                        }
                    }
                }

                //聊天列表展示
                //new AsyncInitMessageData().execute();
                new AsyncInitMessageData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
                Log.e("测试奔溃ListView","===========2443");

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,false);
                edit.commit();
            } else if (intent.getAction() == GlobalConstant.ACTION_MESSAGE_MSGSENDFILEERROR){

                String serverpath = intent.getStringExtra("serverPath");
                SQLiteHelper sqLiteHelper = new SQLiteHelper(getActivity());
                sqLiteHelper.open();
                sqLiteHelper.deleteMessageRecordFilePath(serverpath);
                sqLiteHelper.closeclose();

                if (messageRecordsList != null){
                    if (messageRecordsList.size() > 0){
                        messageRecordsList.clear();
                        Log.e("======PageNum","clear 2573");
                        if (chatSimpleAdapter != null){
                            chatSimpleAdapter.notifyDataSetChanged();
                        }
                    }
                }


                //聊天列表展示
                try{
                   // new AsyncInitMessageData().execute();
                    new AsyncInitMessageData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
                    Log.e("测试奔溃ListView","===========2462");
                }catch (Exception e){
                    Log.e("捕获异常","2434    Error:" + e.getMessage());
                    LogUtils.writeTxtToFile("Error   2434:" + e.getMessage(),"/sdcard/Error/","error.txt");
                }

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,false);
                edit.commit();
            }
        }
    }


    private String filenameforupload = "";
    private String fileidforupload = "";
    private String filepathforupload = "";

    private void applyUpload(String userNo, String pathname, final int type){

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


    //该广播用于切换界面检测当前是否存在正在播放的语音 如果存在的话则暂停播放
    public class StopMediaPlayerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("UntilYou","StopMediaPlayerReceiver");
            if (isPlaying) {
                stopPlaying();
                Log.e("UntilYou","stopPlaying");
            }
        }
    }

}
