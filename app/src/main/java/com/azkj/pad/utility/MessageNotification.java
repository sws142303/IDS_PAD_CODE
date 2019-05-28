package com.azkj.pad.utility;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.azkj.pad.activity.MainActivity;
import com.azkj.pad.activity.R;

@SuppressLint("NewApi") public class MessageNotification
{
	public static int NEWESGNUM = 0;
	public static int SETCUTTENTTAB=-1;
	private NotificationManager notificationManager;




	public MessageNotification()
    {

    }



	public MessageNotification(Context context, int drawable, String nameString)
    {
		showMessageNotification(context,drawable,nameString);
    }
	
    private void showMessageNotificationS(Context context, int drawable,
	    String nameString)
    {
		notificationManager = (NotificationManager) context
			.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification(drawable, nameString + "发送的"
//			+ "信息", System.currentTimeMillis());
//		notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
//		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
//		notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击一次后自动消除
//		notification.defaults = Notification.DEFAULT_SOUND;// 声音
		CharSequence contentTitle = "新信息"; // 通知栏标题
     	CharSequence contentText = NEWESGNUM + "条未读信息"; // 通知栏内容
//		Intent notificationIntent = new Intent();
//		notificationIntent.setClass(context, MainActivity.class);
//		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		//notificationIntent.setAction(GlobalConstant.ACTION_CHANGEACTIVITY_MESSAGE);
//		//PendingIntent contentItent = PendingIntent.getBroadcast(context, 0,notificationIntent, 0);
//		notificationIntent.putExtra("isNewMessage", true);
//		PendingIntent contentItentAc = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context, contentTitle, contentText,
//			contentItentAc);
//		notificationManager.notify(2, notification);
		Notification.Builder n = new Notification.Builder(context);
		n.setSmallIcon(drawable); //设置图标
		n.setLargeIcon(
				BitmapFactory.decodeResource(context.getResources(),drawable));//落下后显示的图标
		n.setTicker(context.getString(R.string.app_name));
		n.setContentTitle(contentTitle); //设置标题
		n.setContentText(contentText); //消息内容
		n.setWhen(System.currentTimeMillis()); //发送时间
		n.setDefaults(Notification.FLAG_NO_CLEAR); //设置默认的提示音，振动方式，灯光
		n.setAutoCancel(true);//打开程序后图标消失
		Intent intent = new Intent();
		PendingIntent pendingIntent =PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setContentIntent(pendingIntent);
		Notification notification1 = n.build();
		//notification1.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		//notification1.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification1.flags |= Notification.FLAG_AUTO_CANCEL; // 点击一次后自动消除
		//notification1.defaults = Notification.DEFAULT_SOUND;// 声音
		notificationManager.notify(2, notification1); // 通过通知管理器发送通知
    }

	private void showMessageNotification(Context context, int drawable,
	    String nameString)
    {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
    		/*Notification notification = new Notification(drawable, nameString + "发送的"
    			+ "信息", System.currentTimeMillis());*/
		Notification.Builder n = new Notification.Builder(context);
		n.setSmallIcon(drawable); //设置图标
		n.setContentTitle("新信息");
/*		n.setContentText(nameString + "发送的"+ "信息"); //消息内容
		n.setContentInfo(NEWESGNUM + "条未读信息");*/
		n.setContentText(NEWESGNUM + "条未读信息"); //消息内容
		n.setWhen(System.currentTimeMillis()); //发送时间
		//n.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
		n.setAutoCancel(false);//打开程序后图标消失
		Notification notification = n.build();
		//notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		//notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击一次后自动消除
		//notification.defaults = Notification.DEFAULT_SOUND;// 声音
		Intent notificationIntent = new Intent();
		notificationIntent.setClass(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//notificationIntent.setAction(GlobalConstant.ACTION_CHANGEACTIVITY_MESSAGE);
		//PendingIntent contentItent = PendingIntent.getBroadcast(context, 0,notificationIntent, 0);
		notificationIntent.putExtra("isNewMessage", true);
		PendingIntent contentItentAc = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//notification.setLatestEventInfo(context, contentTitle, contentText,contentItentAc);
		notification.contentIntent = contentItentAc;
		notificationManager.notify(2, notification);
    }

	
}	

