package com.azkj.pad.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.AutoAnswerBena;
import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.LocalContact;
import com.azkj.pad.model.PosMarkerInfo;
import com.azkj.pad.model.SipServer;
import com.azkj.pad.model.SipUser;
import com.juphoon.lemon.ui.MtcDelegate;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sword.SDK.MediaEngine;

/*公共方法处理*/
@SuppressLint("SimpleDateFormat")
public class CommonMethod {
	// 联系人取得数组
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	// 联系人显示名称
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	// 电话号码
	private static final int PHONES_NUMBER_INDEX = 1;
	// 头像ID
	@SuppressWarnings("unused")
	private static final int PHONES_PHOTO_ID_INDEX = 2;
	// 联系人的ID
	@SuppressWarnings("unused")
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	private static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			DEFAULT_TIME_FORMAT);
	private static String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	private static SimpleDateFormat shortDateFormatter = new SimpleDateFormat(
			SHORT_DATE_FORMAT);
	private static String SHORT_CN_DATE_FORMAT = "yyyy年MM月dd日";
	private static SimpleDateFormat shortCNDateFormatter = new SimpleDateFormat(
			SHORT_CN_DATE_FORMAT);
	private static String LONG_CN_TIME_FORMAT = "yyyy年MM月dd日 HH时mm分";
	private static SimpleDateFormat longCNDatetimeFormatter = new SimpleDateFormat(
			LONG_CN_TIME_FORMAT);
	private static String SHORT_MM_dd="MM-dd";
	private static SimpleDateFormat shortMMdd=new SimpleDateFormat(SHORT_MM_dd);
	private static SharedPreferences prefs;
	private static Typeface typeface;
	private static CommonMethod instance = new CommonMethod();

	//通过callId得到对应的ME_IdsPara对象
	public static HashMap<Integer,MediaEngine.ME_IdsPara> hashMap = new HashMap<>();

	//对讲组中成员的信息 key为成员number
	public static HashMap<String,MediaEngine.ME_UserInfo> prefixMap = new HashMap<>();
	//存储通讯录中本地联系人信息
	public static HashMap<String,Contacts> prefixLocalMap = new HashMap<>();

	public static CommonMethod getInstance() {
		return instance;
	}

	private CommonMethod() {
	}

	/* 将dip转为像素 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/* 将像素转为dip */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/* 取得字体 */
	public static Typeface getTypeface(Context context) {
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					context.getString(R.string.typeface));
		}
		return typeface;
	}

	/* 取得当前日期 */
	public static String getCurrDate(Calendar calendar) {
		return dateFormatter.format(calendar.getTime());
	}

	/* 取得当前日期 */
	public static String getCurrDate() {
		return dateFormatter.format(Calendar.getInstance().getTime());
	}

	/* 取得当前短日期 */
	public static String getCurrShortDate(Calendar calendar) {
		return shortDateFormatter.format(calendar.getTime());
	}

	/* 取得当前短日期 */
	public static String getCurrShortDate() {
		return shortDateFormatter.format(Calendar.getInstance().getTime());
	}

	/* 取得当前短中文日期 */
	public static String getCurrShortCNDate(Calendar calendar) {
		return shortCNDateFormatter.format(calendar.getTime());
	}

	/* 取得当前短中文日期 */
	public static String getCurrShortCNDate() {
		return shortCNDateFormatter.format(Calendar.getInstance().getTime());
	}

	/* 取得当前长中文日期 */
	public static String getCurrLongCNDate(Calendar calendar) {
		return longCNDatetimeFormatter.format(calendar.getTime());
	}

	/* 取得当前短中文日期 */
	public static String getCurrLongCNDate() {
		return longCNDatetimeFormatter.format(Calendar.getInstance().getTime());
	}
    /*取得当前月和日*/
	public static String getCurrShortMMdd(Calendar calendar){
		return shortMMdd.format(calendar.getTime());
	}
	/* 取得当前时间 */
	/*
	 * public static String getCurrTime(java.util.Calendar valueType){ if
	 * (valueType == Calendar.HOUR_OF_DAY){
	 * 
	 * } return shortCNDateFormatter.format(Calendar.getInstance().getTime()); }
	 */

	/* 初始化全局存储 */
	private static void initPrefs(Context context) {
		if (!prefs.getBoolean(context.getString(R.string.sp_init_flag), false)) {
			PreferenceManager.setDefaultValues(context, R.xml.default_settings,
					false);
			Editor editor = prefs.edit();
			editor.putBoolean(context.getString(R.string.sp_init_flag), true);
			editor.commit();
		}
	}

	/* 取得全局存储值(字符串型) */
	public String getPrefsStringValue(Context context, String key) {
		String result = "";
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		initPrefs(context);
		result = prefs.getString(key, "");
		return result;
	}

	/* 取得全局存储值(整形) */
	public Integer getPrefsIntValue(Context context, String key) {
		Integer result = 0;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		initPrefs(context);
		result = prefs.getInt(key, 0);
		return result;
	}

	/* 取得全局存储值(布尔型) */
	public Boolean getPrefsBoolValue(Context context, String key) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		initPrefs(context);
		return prefs.getBoolean(key, false);
	}

	/* 取得全局存储值(服务器信息) */
	public SipServer getSipServerFromPrefs(SharedPreferences prefs) {
		SipServer sipServer = null;
		if (prefs == null) {
			return null;
		}
		sipServer = new SipServer();
		sipServer.setServerIp(prefs.getString(GlobalConstant.SP_SERVERIP, ""));
//		sipServer.setAccreditServerip(prefs.getString(GlobalConstant.SP_ACCRDITSERVERIP, ""));
		sipServer.setPort(prefs.getString(GlobalConstant.SP_PORT, "5060"));
		return sipServer;
	}

	/* 取得全局存储值(登录用户信息) */
	public SipUser getSipUserFromPrefs(SharedPreferences prefs) {
		SipUser sipUser = null;
		if (prefs == null) {
			return null;
		}
		sipUser = new SipUser();
		sipUser.setUsername(prefs.getString(GlobalConstant.SP_USERNAME, ""));
		sipUser.setPasswd(prefs.getString(GlobalConstant.SP_PASSWORD, ""));
		sipUser.setSavepass(prefs.getBoolean(GlobalConstant.SP_SAVEPASS, false));
		return sipUser;
	}

	// 检查IP输入是否正确
	public boolean checkIP(String ipAddr) {
		if (isStrBlank(ipAddr)) {
			return false;
		}
		String regular = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
		Pattern pattern = Pattern.compile(regular);
		Matcher mat = pattern.matcher(ipAddr);
		return mat.matches();
	}
	
	//检查邮箱地址
	public static boolean checkEmail(String email){
	   String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	   Pattern regex = Pattern.compile(check);
	   Matcher matcher = regex.matcher(email);
	   if(matcher.matches())
		   return true;
	   return false;
	 }
	
	//检查数字
	 public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	//判断字符串是否包含空格
	public static boolean checkBlank(String str){
		Pattern pattern = Pattern.compile("[\\s]+");        
		Matcher matcher = pattern.matcher(str);        
		boolean flag = false;        
		while (matcher.find()) {            
			flag = true;        
		}        
		return flag;
		
	}

	// 判断字符串长度是否为0
	public boolean isStrBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	// 检查号码是否是手机联系人
	public static boolean checkConcactExist(Context context, String number) {
		boolean isExist;
		Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		Cursor phonesCursor = context.getContentResolver().query(phoneUri,
				null,
				ContactsContract.Contacts.HAS_PHONE_NUMBER + "=" + number,
				null, null);
		if (phonesCursor.getCount() >= 1) {
			isExist = true;
		} else {
			isExist = false;
		}
		phonesCursor.close();
		return isExist;
	}

	// 根据联系人名称查找id
	public static String getContactIdByName(Context context, String name) {
		String id = "0";
		Cursor cursor = context
				.getContentResolver()
				.query(ContactsContract.Contacts.CONTENT_URI,
						new String[] { ContactsContract.Contacts._ID },
						ContactsContract.Contacts.DISPLAY_NAME
								+ "='" + name + "'", null, null);
		if (cursor.moveToNext()) {
			id = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
		}
		cursor.close();
		return id;
	}

	public static String getUpdateContactIdByName(Context context, String name) {
		String id = "0";
		/*Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },

				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { name }, null);*/
		Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },

				ContactsContract.Contacts.DISPLAY_NAME + "='"+name+"'",
				null, null);

		if (cursor.moveToFirst()) {
			do {
				id = cursor.getString(cursor
						.getColumnIndex(Data.RAW_CONTACT_ID));
			} while (cursor.moveToNext());
			cursor.close();
		}
		return id;
	}

	// 添加手机联系人
	public static void addLocalContact(Context context, LocalContact contact) {
		// 添加本地联系人


		Contacts contacts = new Contacts();
		contacts.setBuddyNo(contact.getIpPhone());
		contacts.setBuddyName(contact.getLastName());
		CommonMethod.prefixLocalMap.put(contacts.getBuddyNo(),contacts);


		ContentValues values = new ContentValues();
		Uri rawContactUri = context.getContentResolver().insert(
				RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// 姓名
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.DISPLAY_NAME, contact.getLastName());
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// 公司
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
		values.put(Organization.COMPANY, contact.getCompany());
		values.put(Organization.TYPE, Phone.TYPE_WORK);
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// IP电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getIpPhone());
		values.put(Phone.TYPE, Phone.TYPE_FAX_WORK); // IP电话存放到传真字段中
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// 移动电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getMobilePhone());
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// 办公电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getOfficePhone());
		values.put(Phone.TYPE, Phone.TYPE_WORK);
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// 住宅电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getHomePhone());
		values.put(Phone.TYPE, Phone.TYPE_HOME);
		context.getContentResolver().insert(Data.CONTENT_URI,
				values);

		// 邮箱
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		values.put(Email.ADDRESS, contact.getEmail());
		values.put(Email.TYPE, Email.TYPE_MOBILE);

		context.getContentResolver().insert(Data.CONTENT_URI,
				values);
	}

	// 更新本地联系人
	public static void updateLocalContact(Context context,
			LocalContact contact, long rowId) {

		Uri uri = Data.CONTENT_URI;
		MtcDelegate.log("uri:" + uri.toString());
		ContentValues values = new ContentValues();

		// 姓名

		if (contact.getLastName().length() > 0) {
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rowId);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			values.put(StructuredName.DISPLAY_NAME, contact.getLastName());
			context.getContentResolver()
					.update(uri,
							values,
							Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE
									+ "=?",
							new String[] { rowId + "",
									StructuredName.CONTENT_ITEM_TYPE });

		}

		// 公司
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE);
		values.put(Organization.COMPANY, contact.getCompany());
		values.put(Organization.TYPE, Phone.TYPE_WORK);
		context.getContentResolver().update(uri, values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
				new String[] { rowId + "", Organization.CONTENT_ITEM_TYPE });

		// IP电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getIpPhone());
		values.put(Phone.TYPE, Phone.TYPE_FAX_WORK); // IP电话存放到传真字段中
		context.getContentResolver().update(
				uri,
				values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=? and "
						+ Phone.TYPE + "=?",
				new String[] { rowId + "", Phone.CONTENT_ITEM_TYPE,
						Phone.TYPE_FAX_WORK + "" });

		// 移动电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getMobilePhone());
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
		context.getContentResolver().update(
				uri,
				values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=? and "
						+ Phone.TYPE + "=?",
				new String[] { rowId + "", Phone.CONTENT_ITEM_TYPE,
						Phone.TYPE_MOBILE + "" });

		// 办公电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getOfficePhone());
		values.put(Phone.TYPE, Phone.TYPE_WORK);
		context.getContentResolver().update(
				uri,
				values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=? and "
						+ Phone.TYPE + "=?",
				new String[] { rowId + "", Phone.CONTENT_ITEM_TYPE,
						Phone.TYPE_WORK + "" });

		// 住宅电话
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, contact.getHomePhone());
		values.put(Phone.TYPE, Phone.TYPE_HOME);
		context.getContentResolver().update(
				uri,
				values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=? and "
						+ Phone.TYPE + "=?",
				new String[] { rowId + "", Phone.CONTENT_ITEM_TYPE,
						Phone.TYPE_HOME + "" });

		// 邮箱
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rowId);
		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		values.put(Email.ADDRESS, contact.getEmail());
		values.put(Email.TYPE, Email.TYPE_MOBILE);
		context.getContentResolver().update(uri, values,
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE + "=?",
				new String[] { rowId + "", Email.CONTENT_ITEM_TYPE });

	}

	// 获取指定名称联系人
	public static LocalContact getContactByName(Context context, String name) {
		LocalContact contact = new LocalContact();
		String id = getContactIdByName(context, name);
		Cursor nameCur = context
				.getContentResolver()
				.query(Data.CONTENT_URI,
						new String[] { Data.CONTACT_ID,
								StructuredName.DISPLAY_NAME,
								StructuredName.FAMILY_NAME,
								StructuredName.GIVEN_NAME,
								StructuredName.MIDDLE_NAME },
						Data.CONTACT_ID
								+ "=?"
								+ " AND "
								+ Data.MIMETYPE
								+ "='"
								+ StructuredName.CONTENT_ITEM_TYPE
								+ "'", new String[] { id }, null);
		if (nameCur.moveToFirst()) {
			contact.setLastName(nameCur.getString(nameCur
					.getColumnIndex(StructuredName.DISPLAY_NAME)));
		}
		nameCur.close();
		Cursor phoneCursor = null;

		phoneCursor = context.getContentResolver().query(
				Phone.CONTENT_URI, null,
				Phone.CONTACT_ID + " = " + id,
				null, null);

		// 取得电话号码(可能存在多个号码)
		while (phoneCursor.moveToNext()) {
			String number = phoneCursor
					.getString(phoneCursor
							.getColumnIndex(Phone.NUMBER));
			String phoneNumberType = phoneCursor
					.getString(phoneCursor
							.getColumnIndex(Phone.TYPE));
			switch (Integer.parseInt(phoneNumberType)) {
			case 1:
				contact.setHomePhone(number);
				break;
			case 2:
				contact.setMobilePhone(number);
				break;
			case 3:
				contact.setOfficePhone(number);
				break;
			case 4:
				contact.setIpPhone(number);
				break;
			default:
				break;
			}
		}

		phoneCursor.close();

		Cursor emailCur = context
				.getContentResolver()
				.query(Email.CONTENT_URI,
						new String[] {
								Email.DATA,
								Email.TYPE },
						Data.CONTACT_ID + "='" + id + "'",
						null, null);

		while (emailCur.moveToNext()) {
			contact.setEmail(emailCur.getString(emailCur
					.getColumnIndex(Email.DATA)));
		}
		emailCur.close();

		Cursor comCur = context.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data._ID, Organization.COMPANY,
						Organization.TITLE },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Organization.CONTENT_ITEM_TYPE + "'",
				new String[] { id }, null);

		while (comCur.moveToNext()) {
			contact.setCompany(comCur.getString(comCur
					.getColumnIndex(Organization.COMPANY)));
		}

		comCur.close();

		return contact;
	}

	// 获取指定手机联系人名称
	@SuppressWarnings("unused")
	public static String getDisplayNameByNo(Context context, String number) {
		Cursor cursor=null;
		String name="";
		try{
		String[] projection = { PhoneLookup.DISPLAY_NAME,
				Phone.NUMBER };
		// 将自己添加到 msPeers 中
		 cursor = context.getContentResolver().query(
				Phone.CONTENT_URI,
				projection, // Which columns to return.
				Phone.NUMBER + " = '" + number
						+ "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.
		if (cursor == null) {
			name= "";
		}
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);

			// 取得联系人名字
			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			 name = cursor.getString(nameFieldColumnIndex);
			 break;
			//return name;
		}
		
		}catch(Exception ex){
			Log.e("CommonMethod.getDisplayNameByNo", ex.getMessage());
		}
		finally{
			cursor.close();	
		}
		return name;
	}

	// 删除系统联系人
	public static void delContact(Context context, String name,String buddyNo) {

		if (CommonMethod.prefixLocalMap.containsKey(buddyNo)){
			CommonMethod.prefixLocalMap.remove(buddyNo);
		}

		Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },

				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { name }, null);

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		if (cursor.moveToFirst()) {
			do {
				long Id = cursor.getLong(cursor
						.getColumnIndex(Data.RAW_CONTACT_ID));

				/*ops.add(ContentProviderOperation
						.newDelete(
								ContentUris.withAppendedId(
										RawContacts.CONTENT_URI, Id)).build());*/
				ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                        .withSelection(RawContacts._ID + "=?", new String[]{String.valueOf(Id)})
                        .build());
				try {
					context.getContentResolver().applyBatch(
							ContactsContract.AUTHORITY, ops);
				} catch (Exception e) {
				}
			} while (cursor.moveToNext());
			cursor.close();
		}
	}

	// 判断是否为手机号码
	public static boolean isMobileNO(String mobiles) {
		/*Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");*/
		Pattern p = Pattern
				.compile("^((1[358][0-9])|(14[57])|(17[0678]))\\d{8}$");
		
		Matcher m = p.matcher(mobiles);
		return m.matches();

	}

	// 数组toString
	public static String listToString(List<String> stringList, String split) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(split);
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();
	}

	// 截取字符串
	public static String subString(String subject, int size, String end) {
		subject = TextUtils.htmlEncode(subject);
		if (subject.length() > size) {
			subject = subject.substring(0, size) + end;
		}
		return subject;
	}

	/**
	 * Gets the corresponding path to a file from the given content:// URI
	 * 
	 * @param selectedVideoUri
	 *            The content:// URI to find the file path from
	 * @param contentResolver
	 *            The content resolver to use to perform the query.
	 * @return the file path as a string
	 */
	public static String getFilePathFromContentUri(Uri selectedVideoUri,
			ContentResolver contentResolver) {
		String filePath;
		String[] filePathColumn = { MediaColumns.DATA };

		Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn,
				null, null, null);
		// 也可用下面的方法拿到cursor
		// Cursor cursor = this.context.managedQuery(selectedVideoUri,
		// filePathColumn, null, null, null);

		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}

	// 获取文件名称
	public static String getFileNameByPath(String pathandname, boolean ext) {

		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (ext) {
			return pathandname.substring(start + 1, pathandname.length());
		} else {
			if (start != -1 && end != -1) {
				return pathandname.substring(start + 1, end);
			}
		}
		return null;
	}

	// sd卡根目录
	public static String getSDCardRootPath() {
		return Environment.getExternalStorageDirectory().getParent();
	}

	/**
	 * 得到amr的时长
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static int getAmrDuration(File file) {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (duration <= 0)
			return 0;
		return (int) duration / 1000;
	}

	// 取得本机IP地址
	public static String getIP() {
		String IP = null;
		StringBuilder IPStringBuilder = new StringBuilder();
		try {
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaceEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaceEnumeration
						.nextElement();
				Enumeration<InetAddress> inetAddressEnumeration = networkInterface
						.getInetAddresses();
				while (inetAddressEnumeration.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnumeration
							.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						IPStringBuilder.append(inetAddress.getHostAddress()
								.toString() + "\n");
					}
				}
			}
		} catch (SocketException ex) {

		}

		IP = IPStringBuilder.toString();
		return IP;
	}

	// 取得本机IP地址
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	//获取IP
	public static String getIPAddress(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (Ice.SocketException e) {
					e.printStackTrace();
				} catch (java.net.SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
				return ipAddress;
			}
		}
		return null;
	}

	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				(ip >> 24 & 0xFF);
	}



	// 取得手机通讯录联系人
	public static ArrayList<Contacts> getPhoneContacts(Context context) {
		Cursor phoneCursor=null;
		Cursor cursor=null;
		final ArrayList<Contacts> result = new ArrayList<Contacts>();
		try{
		 cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		int contactIdIndex = 0;
		int nameIndex = 0;

		if (cursor.getCount() > 0) {
			contactIdIndex = cursor
					.getColumnIndex(ContactsContract.Contacts._ID);
			nameIndex = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		}
		while (cursor.moveToNext()) {
			
			
			String contactId = cursor.getString(contactIdIndex);
			String contactName = cursor.getString(nameIndex);
			Contacts contact = new Contacts();

			contact.setBuddyName(contactName);
			
			
			/*
			 * 查找该联系人的phone信息
			 */
			
			 phoneCursor = context.getContentResolver().query(
					Phone.CONTENT_URI,
					null,
					Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			// 取得电话号码(可能存在多个号码)
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(Phone.NUMBER));
				String phoneNumberType = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(Phone.TYPE));

				
				switch (Integer.parseInt(phoneNumberType)) {
				case 1:
					//家庭
					if (phoneNumber != null && phoneNumber.length() > 0) {
						contact.setBuddyNo(phoneNumber);
						contact.setPhoneNo(phoneNumber);
					}
					break;
				case 2:
					// 手机
					if (phoneNumber != null && phoneNumber.length() > 0) {
						contact.setBuddyNo(phoneNumber);
						contact.setPhoneNo(phoneNumber);
					}
					break;
				case 3:
					//工作
					if (phoneNumber != null && phoneNumber.length() > 0) {
						contact.setBuddyNo(phoneNumber);
						contact.setPhoneNo(phoneNumber);
					}
					break;
				case 4:
					//传真
					if (phoneNumber != null && phoneNumber.length() > 0) {
						contact.setBuddyNo(phoneNumber);
						contact.setPhoneNo(phoneNumber);
					}
					break;
				default:
					break;
				}
			}
			contact.setPhoto(null);
			if (contact.getPhoneNo() != null) {
				contact.getPhoneNo().replaceAll(" ", "");
			}
			result.add(contact);
			CommonMethod.prefixLocalMap.put(contact.getBuddyNo(),contact);
			phoneCursor.close();
		}
		cursor.close();
		/*
		 * ContentResolver resolver = context.getContentResolver(); // 获取手机联系人
		 * Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
		 * PHONES_PROJECTION, null, null, null); if (phoneCursor != null) {
		 * while (phoneCursor.moveToNext()) { // 得到手机号码 String phoneNumber =
		 * phoneCursor.getString(PHONES_NUMBER_INDEX); if (phoneNumber == null)
		 * { phoneNumber = ""; } phoneNumber = phoneNumber.replaceAll("\\s*",
		 * ""); // 当手机号码为空的或者为空字段 跳过当前循环 if (TextUtils.isEmpty(phoneNumber)) {
		 * continue; } // 得到联系人名称 String contactName = phoneCursor
		 * .getString(PHONES_DISPLAY_NAME_INDEX); // 得到联系人ID Long contactid =
		 * phoneCursor.getLong(PHONES_CONTACT_ID_INDEX); // 得到联系人头像ID Long
		 * photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX); //
		 * 得到联系人头像Bitamp Bitmap contactPhoto = null; // photoid 大于0 表示联系人有头像
		 * 如果没有给此人设置头像则给他一个默认 if (photoid > 0) { Uri uri =
		 * ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI,
		 * contactid); InputStream input = ContactsContract.Contacts
		 * .openContactPhotoInputStream(resolver, uri); contactPhoto =
		 * BitmapFactory.decodeStream(input); } else { contactPhoto =
		 * BitmapFactory.decodeResource( context.getResources(),
		 * R.drawable.contact_photo); } Contacts contact = new Contacts();
		 * contact.setBuddyNo(phoneNumber); contact.setBuddyName(contactName);
		 * contact.setPhoneNo(phoneNumber); contact.setPhoto(contactPhoto);
		 * result.add(contact); } phoneCursor.close(); }
		 */
		}
        catch(Exception ex){
        	Log.e("查询联系人", ex.getMessage());
        }finally{
        	if(phoneCursor!=null)
        	phoneCursor.close();
        	if(cursor!=null)
        	cursor.close();
        }
		return result;
	}

	// 取得SIM卡联系人
	public static ArrayList<Contacts> getSIMContacts(Context context) {
		ArrayList<Contacts> result = new ArrayList<Contacts>();
		ContentResolver resolver = context.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像
				Contacts contact = new Contacts();
				contact.setBuddyNo(phoneNumber);
				contact.setBuddyName(contactName);
				contact.setPhoneNo(phoneNumber);
				result.add(contact);
			}
			phoneCursor.close();
		}
		return result;
	}

	/**
	 * 计算百分比
	 * 
	 * @param y
	 * @param z
	 * @return
	 */
	public static String getPercentage(int y, int z) {
		String baifenbi = "";// 接受百分比的值
		double baiy = y * 1.0;
		double baiz = z * 1.0;
		double fen = baiy / baiz;
		// NumberFormat nf = NumberFormat.getPercentInstance(); 注释掉的也是一种方法
		// nf.setMinimumFractionDigits( 2 ); 保留到小数点后几位
		DecimalFormat df1 = new DecimalFormat("##%"); // ##.00%
														// 百分比格式，后面不足2位的用0补齐
		// baifenbi=nf.format(fen);
		baifenbi = df1.format(fen);
		System.out.println(baifenbi);
		return baifenbi;
	}

	private static final double PI = 3.14159265;
	@SuppressWarnings("unused")
	private static final double EARTH_RADIUS = 6378137;
	@SuppressWarnings("unused")
	private static final double RAD = Math.PI / 180.0;

	// @see
	// http://snipperize.todayclose.com/snippet/php/SQL-Query-to-Find-All-Retailers-Within-a-Given-Radius-of-a-Latitude-and-Longitude--65095/
	// The circumference of the earth is 24,901 miles.
	// 24,901/360 = 69.17 miles / degree
	/**
	 * @param raidus
	 *            单位米 return minLat,minLng,maxLat,maxLng
	 */
	public static double[] getAround(double lat, double lon, int raidus) {

		Double latitude = lat;
		Double longitude = lon;

		Double degree = (24901 * 1609) / 360.0;
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		Double minLat = latitude - radiusLat;
		Double maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		Double minLng = longitude - radiusLng;
		Double maxLng = longitude + radiusLng;
		// System.out.println(&quot;[&quot;+minLat+&quot;,&quot;+minLng+&quot;,&quot;+maxLat+&quot;,&quot;+maxLng+&quot;]&quot;);
		return new double[] { minLat, minLng, maxLat, maxLng };
	}

	/**
	 * 获取短消息文件保存路径
	 * 文件类型,2:图片,3：音频 ,4：视频,8:文件
	 * @param type
	 * @return
	 */
	public static String getMessageFileDownPath(int type) {
		String sdCardPath = "";
		String downPath = sdCardPath;
		switch (type) {
		case 2:
			downPath = Environment.getExternalStorageDirectory()
			+ "/ptt_3g_pad/file/images/";
			break;
		case 3:
			downPath = Environment.getExternalStorageDirectory()
			+ "/ptt_3g_pad/file/audio/";
			break;
		case 4:
			downPath = Environment.getExternalStorageDirectory()
			+ "/ptt_3g_pad/file/video/";
			break;
		case 5://位置截图
			downPath = Environment.getExternalStorageDirectory()
					+ "/ptt_3g_pad/file/video/";
			break;
		case 8:
			downPath = Environment.getExternalStorageDirectory()
			+ "/ptt_3g_pad/file/folder/";
			break;

		default:
			break;
		}
//		if (type == 2) {
//			downPath = Environment.getExternalStorageDirectory()
//					+ "/ptt_3g_pad/file/images/";
//		} else if (type == 3) {
//			downPath = Environment.getExternalStorageDirectory()
//					+ "/ptt_3g_pad/file/audio/";
//		} else if (type == 4) {
//			downPath = Environment.getExternalStorageDirectory()
//					+ "/ptt_3g_pad/file/video/";
//		}
		File file = new File(downPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return downPath;
	}

	// 去除list重复数据
	public static List<PosMarkerInfo> removeDuplicates(List<PosMarkerInfo> l) {
		java.util.Set<PosMarkerInfo> s = new java.util.TreeSet<PosMarkerInfo>(
				new java.util.Comparator<PosMarkerInfo>() {
					@Override
					public int compare(PosMarkerInfo o1, PosMarkerInfo o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
		s.addAll(l);
		List<PosMarkerInfo> res = new ArrayList<PosMarkerInfo>(s);
		return res;
	}

	// 返回键屏蔽
	public static boolean hanndleChildBackButtonPress(final Activity activity,
			int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return false;
		}
	}
	public static void setCameraDisplayOrientation(Activity activity,
	         int cameraId, Camera camera) {
	     Camera.CameraInfo info =
	             new Camera.CameraInfo();
	     Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }


	private AutoAnswerBena autoAnswerBena = new AutoAnswerBena();

	public  AutoAnswerBena getAutoAnswerBena() {
		return autoAnswerBena;
	}

	public void setAutoAnswerBena(AutoAnswerBena autoAnswerBena) {
		this.autoAnswerBena = autoAnswerBena;
	}
}
