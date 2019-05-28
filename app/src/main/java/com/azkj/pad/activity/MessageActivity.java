package com.azkj.pad.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.azkj.chw.coreprogress.helper.ProgressHelper;
import com.azkj.chw.coreprogress.listener.ProgressListener;
import com.azkj.chw.coreprogress.listener.impl.UIProgressListener;
import com.azkj.pad.model.MessageRecords;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.azkj.pad.service.PTTService;
import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.GlobalConstant;
import com.azkj.pad.utility.SQLiteHelper;
import com.azkj.pad.utility.SetVolumeUtils;
import com.azkj.pad.utility.ToastUtils;
import com.juphoon.lemon.ui.MtcDelegate;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import cn.sword.SDK.MediaEngine;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends Activity {
    SwitchFragmentReceiver switchFragmentReceiver;
    PTT_3G_PadApplication ptt_3g_PadApplication;
    private UploadReceive uploadReceive;
    private MessageSendBroadcastReceiver messageSendBroadcastReceiver;
    private SharedPreferences prefs;
    private SipUser sipUser;
    private SipServer sipServer;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1){
                case 1:
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    messageNewFragment = (MessageNewFragment) fragmentManager.findFragmentByTag("fragmentnew");
                    messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                    if (messageNewFragment != null) {
                        fragmentTransaction.hide(messageNewFragment);
                    }
                    if (messageShowFragment == null) {
                        messageShowFragment = new MessageShowFragment();
                    }

                    if (!messageShowFragment.isAdded()) {
                        fragmentTransaction.add(R.id.messageRelativeLayout, messageShowFragment, "fragmentshow").commitAllowingStateLoss();
                    } else {
                        fragmentTransaction.show(messageShowFragment).commitAllowingStateLoss();
                        messageShowFragment.refreshMessageShow();
                    }
                    Log.e("TEST离线短信","handler刷新界面 ");
                    break;
            }

        }
    };
    private MessageNewFragment messageNewFragment;
    private MessageShowFragment messageShowFragment;
    private FragmentTransaction fragmentTransaction;

    private SetVolumeUtils setVolumeUtils = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ptt_3g_PadApplication = (PTT_3G_PadApplication) getApplication();

        prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        sipUser = CommonMethod.getInstance().getSipUserFromPrefs(prefs);
        sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        messageSendBroadcastReceiver = new MessageSendBroadcastReceiver();
        IntentFilter messageIntentFilter = new IntentFilter();
        messageIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGSENDOK);
        messageIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGSENDFAILED);
        registerReceiver(messageSendBroadcastReceiver, messageIntentFilter);


        uploadReceive = new UploadReceive();
        IntentFilter uploadIntentFilter = new IntentFilter();
        uploadIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADYES);
        uploadIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADNO);
        uploadIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_FILEUPLOADYES);
        uploadIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_CHOOSEPHOTO);
        registerReceiver(uploadReceive, uploadIntentFilter);

        if (ptt_3g_PadApplication.getOutUserNo() == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
            if (messageListFragment == null) {
                messageListFragment = new MessageListFragment();
            }
            if (!messageListFragment.isAdded()) {
                fragmentTransaction.add(R.id.messageRelativeLayout, messageListFragment, "fragmentlist").commitAllowingStateLoss();
            } else {
                fragmentTransaction.show(messageListFragment).commitAllowingStateLoss();
            }
        }

        switchFragmentReceiver = new SwitchFragmentReceiver();
        IntentFilter switchIntentFilter = new IntentFilter();
        switchIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_SWITCHFRAGMENT);
        switchIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_SWITCHMESSAGENEW);
        switchIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGINCOMMING);
        switchIntentFilter.addAction(GlobalConstant.ACTION_MESSAGE_MSGSENDFILEOK);
        switchIntentFilter.addAction(GlobalConstant.ACTION_CONTACT_HIDDEN);
        switchIntentFilter.addAction(GlobalConstant.ACTION_MEDIASCANNER_FILE_DATA_SIZE);
        registerReceiver(switchFragmentReceiver, switchIntentFilter);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setVolumeUtils = new SetVolumeUtils(this,audioManager);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("@@@@@@@@MessageAc91 onResume@@@@@@@@@@", "@@@@@@@@MessageAc91 onResume@@@@@@@@");
    }

    @Override
    public void onBackPressed() {

    }

    //键位监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("文胜的百宝箱","进入onkeydown   1");
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
            Log.e("文胜的百宝箱","进入onkeydown    down");
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
        {
            Log.e("文胜的百宝箱","进入onkeydown up");
            return true;
        }else if (CommonMethod.hanndleChildBackButtonPress(this, keyCode, event)) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("文胜的百宝箱","进入onkeyup");
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
            setVolumeUtils.showPopupWindow(false,getWindow().getDecorView());
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
        {
            setVolumeUtils.showPopupWindow(true,getWindow().getDecorView());
            return true;
        } else return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();

        ptt_3g_PadApplication.setExistenceMessageShow(false);//设置当前存在MessageShow界面
        ptt_3g_PadApplication.setMessageShowBuddyNo("-1");//设置对方号码

        //发送广播 通知短信详情界面停止当前正在播放的语音
        sendBroadcast(new Intent(GlobalConstant.StopMediaPlayerReceiver));

    }


    //使用拍照录制定位等功能时，路径的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MtcDelegate.log("requestCode:" + requestCode);
        if (requestCode == GlobalConstant.RESULT_MESSAGE_PHOTO || requestCode == GlobalConstant.RESULT_MESSAGE_IMAGE || requestCode == GlobalConstant.RESULT_MESSAGE_VIDEO || requestCode == GlobalConstant.RESULT_MESSAGE_LOCATION) {
            MessageNewFragment messageNewFragment = (MessageNewFragment) getFragmentManager().findFragmentByTag("fragmentnew");
            if (messageNewFragment != null) {
                MtcDelegate.log("新消息页面onActivityResult");
                Log.e("zzzttt", "messageActivity 新消息页面onActivityResult ");
                messageNewFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == GlobalConstant.SHOWRESULT_MESSAGE_PHOTO || requestCode == GlobalConstant.SHOWRESULT_MESSAGE_IMAGE || requestCode == GlobalConstant.SHOWRESULT_MESSAGE_VIDEO || requestCode == GlobalConstant.SHOWRESULT_MESSAGE_LOCATION) {
            MessageShowFragment messageShowFragment = (MessageShowFragment) getFragmentManager().findFragmentByTag("fragmentshow");
            MessageNewFragment messageNewFragment = (MessageNewFragment) getFragmentManager().findFragmentByTag("fragmentnew");
            if (messageShowFragment != null) {
                Log.e("zzzttt", "messageActivity 老消息页面onActivityResult ");
                messageShowFragment.onActivityResult(requestCode, resultCode, data);
            }
            if (messageNewFragment != null) {
                MtcDelegate.log("对讲页面onActivityResult");
                messageNewFragment.onActivityResult(requestCode, resultCode, data);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 登出接收器
    public class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            finish();
        }
    }

    //点击导航，切换到短信列表
    private class SwitchFragmentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionIntent = intent.getAction();
            if (actionIntent == GlobalConstant.ACTION_MESSAGE_SWITCHFRAGMENT) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
                MessageNewFragment messageNewFragment = (MessageNewFragment) fragmentManager.findFragmentByTag("fragmentnew");
                MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                if (messageNewFragment != null) {
                    fragmentTransaction.remove(messageNewFragment);
                }
                if (messageShowFragment != null) {
                    fragmentTransaction.remove(messageShowFragment);
                }

                if (messageListFragment == null) {
                    messageListFragment = new MessageListFragment();
                    fragmentTransaction.add(R.id.messageRelativeLayout, messageListFragment, "fragmentlist").commitAllowingStateLoss();
                    //刷新短信列表，修改已读状态
                    messageListFragment.refreshMessageList();
                } else {
                    fragmentTransaction.show(messageListFragment).commitAllowingStateLoss();

                    //刷新短信列表，修改已读状态
                    messageListFragment.refreshMessageList();
                }
            } else if (actionIntent == GlobalConstant.ACTION_MESSAGE_SWITCHMESSAGENEW) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
                MessageNewFragment messageNewFragment = (MessageNewFragment) fragmentManager.findFragmentByTag("fragmentnew");
                MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                if (messageListFragment != null) {
                    fragmentTransaction.hide(messageListFragment);
                }
                if (messageShowFragment != null) {
                    fragmentTransaction.remove(messageShowFragment);
                }
                if (messageNewFragment != null) {
                    fragmentTransaction.remove(messageNewFragment);
                }
                messageNewFragment = new MessageNewFragment();
                fragmentTransaction.add(R.id.messageRelativeLayout, messageNewFragment, "fragmentnew").commit();

                ptt_3g_PadApplication.setAddContact(false);
            } else if (actionIntent == GlobalConstant.ACTION_MESSAGE_MSGINCOMMING) {
                Log.e("Short message file transmission", "MessageActivity=====223===  接收到Main界面发送的广播");
                FragmentManager fragmentManager = getFragmentManager();
                MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
                if (messageListFragment != null) {
                    messageListFragment.refreshMessageList();
                }
                MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                if (messageShowFragment != null) {
                    messageShowFragment.refreshMessageShow();
                }

            } else if (actionIntent == GlobalConstant.ACTION_MESSAGE_MSGSENDFILEOK) {
                Log.e("=======MessageActivity====235=", "接收到GlobalConstant.ACTION_MESSAGE_MSGSENDFILEOK上传成功广播");
                if (ptt_3g_PadApplication.isNetConnection() == false) {
                    ToastUtils.showToast(MessageActivity.this,
                            getString(R.string.info_network_unavailable));
                    return;
                }

/*                String msgBody = "ind:u_upload\r\nemployeeid:" + sipUser.getUsername() + "\r\nfileid:" + ptt_3g_PadApplication.getFileId() + "\r\ntype:2\r\n";
                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);
                Log.e("TEST离线短信","上传完成并通知服务器      msgBody:" + msgBody);*/

                //清空全局信息
                ptt_3g_PadApplication.setFileId("");
                ptt_3g_PadApplication.setFileType(0);
                ptt_3g_PadApplication.setLocalFilePath("");
                ptt_3g_PadApplication.setServerFilePath("");
                //ptt_3g_PadApplication.setOutUserNo("");
                ptt_3g_PadApplication.getContactList().clear();


            } else if (actionIntent == GlobalConstant.ACTION_CONTACT_HIDDEN) {
                FragmentManager fragmentManager = getFragmentManager();
                MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
                MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                if (messageListFragment != null && messageListFragment.popupWindow != null) {
                    messageListFragment.popupWindow.dismiss();
                }
                if (messageShowFragment != null && messageShowFragment.popupWindow != null) {
                    messageShowFragment.popupWindow.dismiss();
                }
            } else if (actionIntent == GlobalConstant.ACTION_MEDIASCANNER_FILE_DATA_SIZE) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MessageNewFragment messageNewFragment = (MessageNewFragment) fragmentManager.findFragmentByTag("fragmentnew");
                MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
                if (messageNewFragment != null) {
                    fragmentTransaction.hide(messageNewFragment);
                }
                if (messageShowFragment == null) {
                    messageShowFragment = new MessageShowFragment();
                }

                if (!messageShowFragment.isAdded()) {
                    fragmentTransaction.add(R.id.messageRelativeLayout, messageShowFragment, "fragmentshow").commitAllowingStateLoss();
                } else {
                    fragmentTransaction.show(messageShowFragment).commitAllowingStateLoss();
                    messageShowFragment.refreshMessageShow();
                }
            }
        }

    }

    //开始上传文件接收器
    private class UploadReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ptt_3g_PadApplication.isNetConnection() == false) {
                ToastUtils.showToast(MessageActivity.this,
                        getString(R.string.info_network_unavailable));
                return;
            }
            String actionIntent = intent.getAction();
            if (actionIntent == GlobalConstant.ACTION_MESSAGE_APPLYUPLOADYES) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,true);
                edit.commit();
                String filepath = intent.getStringExtra("filepath");
                String fileid = intent.getStringExtra("fileid");
                String filename = intent.getStringExtra("filename");
                ptt_3g_PadApplication.setServerFilePath(filepath);
                ptt_3g_PadApplication.setFileId(fileid);
                Log.e("测试fileId", "filepath:" + filepath);
                Log.e("测试fileId", "fileid:" + fileid);
                Log.e("测试fileId", "filename:" + filename);
                Log.e("申请上传-MessageActi", "申请上传成功，开始上传文件:" + (ptt_3g_PadApplication.getLocalFilePath() + ";" + filepath));
                //上传文件的时候，路径改成 本地路径+服务器返回的filepath
                //开始上传之前，通知服务器上传中（新增加）1-上传中；2-上传成功：3-上传失败；4-下载成功；5-下载失败

/*                String msgBody = "ind:u_upload\r\nemployeeid:" + sipUser.getUsername() + "\r\nfileid:" + fileid + "\r\ntype:1\r\n";
                MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBody);*/


                //放在onStart中可能会出现数据还没有写完但是文件已经上传完了
                try {
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(MessageActivity.this);
                    sqLiteHelper.open();
                    MessageRecords newMessageRecords = new MessageRecords();
                    newMessageRecords.setUserNo(sipUser.getUsername());
                    newMessageRecords.setBuddyNo(ptt_3g_PadApplication.getOutUserNo());
                    newMessageRecords.setContent(filename);
                    newMessageRecords.setLocalFileUri(ptt_3g_PadApplication.getLocalFilePath());//服务器路径
                    newMessageRecords.setServerFileUri(ptt_3g_PadApplication.getServerFilePath());//服务器路径
                    newMessageRecords.setLength(0);
//                                newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_IN);
                    newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_OUT);
                    Date nowDate = new Date();
                    newMessageRecords.setSendDate(nowDate);
                    newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_OK);
                    newMessageRecords.setReceiveDate(nowDate);
                    newMessageRecords.setReceiveState(GlobalConstant.MESSAGE_READ_YES);
                    newMessageRecords.setProgressState(GlobalConstant.MESSAGE_PROGRESS_NoCOMPLETE);
                    Log.e("MessageActivity.getFileType()", ptt_3g_PadApplication.getFileType() + "");
                    if (ptt_3g_PadApplication.getFileType() == 1) {
                        newMessageRecords.setContentType(GlobalConstant.MESSAGE_IMG);
                        newMessageRecords.setLayout(GlobalConstant.LAYOUT_IMG_OUT);
                    } else if (ptt_3g_PadApplication.getFileType() == 2) {
                        newMessageRecords.setContentType(GlobalConstant.MESSAGE_AUDIO);
                        newMessageRecords.setLayout(GlobalConstant.LAYOUT_AUDIO_OUT);
                    } else if (ptt_3g_PadApplication.getFileType() == 3) {
                        newMessageRecords.setContentType(GlobalConstant.MESSAGE_VIDEO);
                        newMessageRecords.setLayout(GlobalConstant.LAYOUT_VIDEO_OUT);
                    } else if (ptt_3g_PadApplication.getFileType() == 8) {
                        newMessageRecords.setContentType(GlobalConstant.MESSAGE_FILE);
                        newMessageRecords.setLayout(GlobalConstant.LAYOUT_TEXT_OUT);
                    }
                    long messageRecords = sqLiteHelper.createMessageRecords(newMessageRecords);
                    sqLiteHelper.closeclose();
                    Log.e("TEST离线短信","记录已存入数据库 ");
                } catch (Exception e) {
                    MtcDelegate.log("短消息发送文件写入数据库异常：" + e.getMessage());
                    Log.e("TEST离线短信","记录存入数据库异常  568  Error:" + e.getMessage());
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Message obtain = Message.obtain();
                obtain.arg1 = 1;
                handler.sendMessage(obtain);


                //  1.文件名    2.本地地址    3.filepath
                okHttpUploadLocalFile(filename, ptt_3g_PadApplication.getLocalFilePath(), filepath);

            } else if (actionIntent == GlobalConstant.ACTION_MESSAGE_APPLYUPLOADNO) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,false);
                edit.commit();



            } else if (actionIntent == GlobalConstant.ACTION_MESSAGE_CHOOSEPHOTO) {//从相册选择图片或视频
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,true);
                edit.commit();
                ToastUtils.showToast(context, "正在上传");
                String szFile = intent.getStringExtra("szfile");
                String swsVideo = intent.getStringExtra("swsVideo");
                Log.e("======MessageActivity=====402==", "swsVideo:" + swsVideo);
                Log.e("======MessageActivity=====403==", "szFile:" + szFile);
                if (szFile != null) {
                    ptt_3g_PadApplication.setFileType(1);
                    ptt_3g_PadApplication.setLocalFilePath(szFile);
                    String name = CommonMethod.getFileNameByPath(szFile, true);
                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + ptt_3g_PadApplication.getOutUserNo() + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:2\r\n";
//                    MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypePic);
                } else if (swsVideo != null) {
                    ptt_3g_PadApplication.setFileType(3);
                    ptt_3g_PadApplication.setLocalFilePath(swsVideo);
                    String name = CommonMethod.getFileNameByPath(swsVideo, true);
                    Log.e("======MessageActivity=====416==", "name:" + name);
                    String msgBodyString = "req:upload\r\nsid:\r\nsrc:" + sipUser.getUsername() + "\r\ndst:" + ptt_3g_PadApplication.getOutUserNo() + "\r\nfileid:" + UUID.randomUUID() + "\r\nfilename:" + name + "\r\nfiletype:4\r\n";
                    Log.e("======MessageActivity=====416==", "申请上传msgBodyString:" + msgBodyString);
 //                   MediaEngine.GetInstance().ME_SendMsg(sipUser.getUsername(), GlobalConstant.PTT_MSG_SYSTEM_BODY_TYPE, msgBodyString);
                    applyUpload(ptt_3g_PadApplication.getOutUserNo(), name, GlobalConstant.FileTypeVideo);
                }
            }
        }
    }

    //文本消息发送接收器
    public class MessageSendBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String messageAction = intent.getAction();
            Log.e("发送短信", messageAction);
            SQLiteHelper sqLiteHelper = new SQLiteHelper(MessageActivity.this);
            sqLiteHelper.open();
            MessageRecords newMessageRecords = new MessageRecords();
            newMessageRecords.setUserNo(sipUser.getUsername());
            Log.e("msgUserNo:", ptt_3g_PadApplication.getMsgUserNo());
            newMessageRecords.setBuddyNo(ptt_3g_PadApplication.getMsgUserNo());
            newMessageRecords.setContent(ptt_3g_PadApplication.getMsgBody());
            newMessageRecords.setContentType(GlobalConstant.MESSAGE_TEXT);
            newMessageRecords.setLocalFileUri("");
            newMessageRecords.setServerFileUri("");
            newMessageRecords.setLength(ptt_3g_PadApplication.getMsgBody().length());
            newMessageRecords.setInOutFlg(GlobalConstant.MESSAGE_OUT);
            Date nowDate = new Date();
            newMessageRecords.setSendDate(nowDate);
            if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGSENDOK) {
                newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_OK);
                ToastUtils.showToast(MessageActivity.this, getString(R.string.title_message_send_success));
            } else if (messageAction == GlobalConstant.ACTION_MESSAGE_MSGSENDFAILED) {
                newMessageRecords.setSendState(GlobalConstant.MESSAGE_SEND_FAILED);
                ToastUtils.showToast(MessageActivity.this, getString(R.string.title_message_send_faild));
            }

            newMessageRecords.setReceiveDate(nowDate);
            newMessageRecords.setReceiveState(GlobalConstant.MESSAGE_READ_YES);
            newMessageRecords.setLayout(GlobalConstant.LAYOUT_TEXT_OUT);

            sqLiteHelper.createMessageRecords(newMessageRecords);
            sqLiteHelper.closeclose();

            //清空全局变量
            ptt_3g_PadApplication.setMsgUserNo("");
            ptt_3g_PadApplication.setMsgBody("");
            ptt_3g_PadApplication.getContactList().clear();

            //发送文本记录数据库，发送文件成功广播无操作
            FragmentManager fragmentManager = getFragmentManager();
            MessageListFragment messageListFragment = (MessageListFragment) fragmentManager.findFragmentByTag("fragmentlist");
            if (messageListFragment != null) {
                messageListFragment.refreshMessageList();
            }
            MessageShowFragment messageShowFragment = (MessageShowFragment) fragmentManager.findFragmentByTag("fragmentshow");
            if (messageShowFragment != null) {
                messageShowFragment.refreshMessageShow();
            }

        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(messageSendBroadcastReceiver);
        unregisterReceiver(uploadReceive);
        unregisterReceiver(switchFragmentReceiver);
        super.onDestroy();
    }

    // 提示是否退出
    @SuppressWarnings("unused")
    private void askIfExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_main_exitinfo));
        builder.setMessage(getString(R.string.title_main_ifexit));
        builder.setPositiveButton(getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new SettingExitFragment(MessageActivity.this).exit(); //之前的退出操作
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.btn_cancel),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void okHttpUploadLocalFile(final String finame, final String filepath, final String serverpath) {



        //此文件必须在手机上存在，实际情况下请自行修改，这个目录下的文件只是在我手机中存在。
        File file = new File(filepath);
        Log.e("测试fileId","contentLength :" + ((double) file.length()/ 1024) + "kb" + "\r\n" + ((double) file.length()/ 1024 / 1024) + "M");
        //这个是非ui线程回调，不可直接操作UI
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void onProgress(String filename, long bytesWrite, long contentLength, boolean done) {

            }
        };

        //这个是ui线程回调，可直接操作UI
        final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
            @Override
            public void onUIProgress(String filename, long bytesWrite, long contentLength, boolean done) {
                long progress = bytesWrite * 100 / contentLength;
                Intent intentProgress = new Intent(GlobalConstant.FILE_PROGRESS);
                intentProgress.putExtra("progress", String.valueOf(progress));
                intentProgress.putExtra("progressState", done);
                intentProgress.putExtra("fileName", filename);
                intentProgress.putExtra("fileServicePath", serverpath);
                Log.e("*******------********","onUIProgress    fileServicePath:" + serverpath + "\r\n" + "progress :" + progress);

                switch (String.valueOf(progress)) {
                    case "5":
                        sendBroadcast(intentProgress);
                        break;
                    case "15":
                        sendBroadcast(intentProgress);
                        break;
                    case "35":
                        sendBroadcast(intentProgress);
                        break;
                    case "55":
                        sendBroadcast(intentProgress);
                        break;
                    case "75":
                        sendBroadcast(intentProgress);
                        break;
                    case "95":
                        sendBroadcast(intentProgress);
                        break;
                    case "100":
                        sendBroadcast(intentProgress);
                        break;
                }
            }

            @Override
            public void onUIStart(long bytesWrite, long contentLength, boolean done) {
                super.onUIStart(bytesWrite, contentLength, done);




                Intent intentStart = new Intent(GlobalConstant.FILE_PROGRESS_START);
                intentStart.putExtra("fileName", finame);
                intentStart.putExtra("done", done);
                intentStart.putExtra("fileServicePath", serverpath);
                sendBroadcast(intentStart);

            }

            @Override
            public void onUIFinish(String filename, long bytesWrite, long contentLength, final boolean done) {
                super.onUIFinish(filename, bytesWrite, contentLength, done);
                Log.e("okHttpUploadLocalFile", "onUIFinish");


            }
        };



        //构造上传请求，类似web表单
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("fileName", finame)
                .addFormDataPart("serverPath", serverpath)
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .build();

        Log.e("测试fileId: ", "filepath: " + serverpath + ", name: " + file.getName() + "fileID : " + ptt_3g_PadApplication.getFileId());
        //得到配置界面的端口号
        SipServer sipServer = CommonMethod.getInstance().getSipServerFromPrefs(prefs);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ptt_3g_PadApplication);
        String port = prefs.getString("settingOtherport", "80");
        String url = "http://" + sipServer.getServerIp() + ":" + port + "/UpFileServlet";

        Log.e("测试fileId","文件上传地址url：" + url);
        Log.e("测试fileId","文件本地地址url：" + filepath);
        Log.e("测试fileId","文件名：" + finame);

        //进行包装，使其支持进度回调
        final Request request = new Request.Builder()
                .url(url)
                .post(ProgressHelper.addProgressRequestListener(finame, requestBody, uiProgressRequestListener))
                .build();

        //开始请求
        PTTService.okHttpClient.newCall(request).enqueue(new Callback() {
            //成功
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                // TODO Auto-generated method stub
                String Result = arg1.body().string();
                Log.e("测试fileId", "[postBeanExecute]Call===" + arg1.code() + "Response===" + Result);
                if (arg1.code() == 200 && Result != null && !Result.equals("failure")) {


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //TODO:设置文件上传完成
                    boolean setUploadFinish = MediaEngine.GetInstance().ME_SetFileUploaded(ptt_3g_PadApplication.getFileId());
                    if (setUploadFinish){
                        Log.e("测试fileId", "设置文件上传完成， 文件标识为："+ptt_3g_PadApplication.getFileId());
                    }else {
                        Log.e("测试fileId", "设置文件上传完成-----失败");
                    }


                    // 上传完成
                    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGSENDFILEOK);
                    intent.putExtra("strFileName", Result);
                    sendBroadcast(intent);
                    Log.e("测试fileId","文件上传成功");




                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    SQLiteHelper sqLiteHelper = new SQLiteHelper(MessageActivity.this);
                    sqLiteHelper.open();
                    long l = sqLiteHelper.upDateMessageRecordsProgressState(serverpath, GlobalConstant.MESSAGE_PROGRESS_COMPLETE);
                    sqLiteHelper.closeclose();
                    Log.e("TEST离线ssssssssssss短信","onUIFinish   文件上传完成   serverpath：" + serverpath);
                    Log.e("TEST离线ssssssssssss短信","onUIFinish   文件上传完成   数据库更新返回：" + l);


                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intentStop = new Intent(GlobalConstant.FILE_PROGRESS_STOP);
                    intentStop.putExtra("fileServicePath", serverpath);
                    sendBroadcast(intentStop);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(GlobalConstant.isFileUpLoad,false);
                    edit.commit();
                }
            }

            //失败
            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
                Log.e("TEST离线短信","文件上传失败   664  Error:" + arg1.getMessage());
                Log.e("*************Sws***", "上传失败    arg1:" + arg1.getMessage());
                Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_MSGSENDFILEERROR);
                intent.putExtra("serverPath", serverpath);
                sendBroadcast(intent);

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(GlobalConstant.isFileUpLoad,false);
                edit.commit();
            }
        });
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
                Log.e("测试fileId","回调：filename:"+filename+", fileid:"+fileid+", filepath:"+filepath);
                filenameforupload = filename;
                fileidforupload = fileid;
                filepathforupload = filepath;

                if (!filepathforupload.equals("")){
                    //TODO:设置文件正在上传
                    boolean setUploading = MediaEngine.GetInstance().ME_SetFileUploading(fileidforupload);
                    if (setUploading){
                        Log.e("测试fileId", "设置文件正在上传----成功 fileid : " + fileidforupload);
                        Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_APPLYUPLOADYES);
                        intent.putExtra("filepath", filepathforupload);
                        intent.putExtra("fileid", fileidforupload);
                        intent.putExtra("filename", filenameforupload);
                        sendBroadcast(intent);
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
            Toast.makeText(MessageActivity.this, "申请上传失败， 请检查网络",Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(GlobalConstant.isFileUpLoad,false);
            edit.commit();
        }
    }


    //TODO HTTPClient 文件上传


}
