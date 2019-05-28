package com.azkj.pad.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.azkj.pad.activity.R;
import com.azkj.pad.model.CallingRecords;
import com.azkj.pad.model.Contacts;
import com.azkj.pad.model.MeetingMembers;
import com.azkj.pad.model.MeetingRecords;
import com.azkj.pad.model.MessageRecords;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/*本地数据库操作*/
public class SQLiteHelper {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA); 
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	// 数据库信息
	private static final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory() + "/ptt";
	private static final String DATABASE_NAME = "ptt.db"; 
	private static final int DATABASE_VERSION =4;
	
	// 数据库创建语句
	private static final String CONTACTS_CREATE = "CREATE TABLE Contacts(" +
			" UserNO STRING NOT NULL," +
			" BuddyNO VARCHAR(16) NOT NULL," +
			" BuddyName VARCHAR(128) DEFAULT ''," +
			" GroupNO VARCHAR(16) DEFAULT ''," +
			" PhoneNO VARCHAR(16) DEFAULT ''," +
			" Remark VARCHAR(512) DEFAULT ''," +
			" CreateDate VARCHAR(16) NOT NULL," +
			" UpdateDate VARCHAR(16) NOT NULL," +
			" PRIMARY KEY(UserNO, BuddyNO));";
	private static final String CALLINGRECORDS_CREATE = "CREATE TABLE CallingRecords(" +
			" ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
			" UserNO STRING NOT NULL," +
			" BuddyNO VARCHAR(16) NOT NULL," +
			" StartDate VARCHAR(16) NOT NULL," +
			" AnswerDate VARCHAR(16)," +
			" StopDate VARCHAR(16)," +
			" Duration INTEGER DEFAULT 0," +
			" Time VARCHAR(32)," +
			" InOutFlg INTEGER DEFAULT 0," +
			" CallState INTEGER DEFAULT 0," + "SessId INTEGER NOT NULL);";
	private static final String MESSAGERECORDS_CREATE = "CREATE TABLE MessageRecords(" +
			" ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
			" UserNO STRING NOT NULL," +
			" BuddyNO VARCHAR(16) NOT NULL," +
			" Content TEXT DEFAULT ''," +
			" ContentType INTEGER DEFAULT 0," +
			" LocalFileUri VARCHAR(128) ," +
			" ServerFileUri VARCHAR(128) ," +
			" Length INTEGER DEFAULT 0," +
			" InOutFlg INTEGER DEFAULT 0," +
			" SendDate VARCHAR(16)," +
			" SendState INTEGER DEFAULT 0," +
			" ReceiveDate VARCHAR(16)," +
			" ReceiveState INTEGER DEFAULT 0," +
			" Layout INTEGER DEFAULT 0," +
			" ProgressState INTEGER DEFAULT 1"+
			");";
	private static final String MEETINGRECORDS_CREATE = "CREATE TABLE MeetingRecords(" +
			" ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
			" UserNO STRING NOT NULL," +
			" SID VARCHAR(32) NOT NULL," +
			" StartDate VARCHAR(16) NOT NULL," +
			" ConferenceNO VARCHAR(16) NOT NULL," +
			" ConferenceNM VARCHAR(128) NOT NULL," +
			" IsMonitor INTEGER DEFAULT 0," +
			" ConferenceType INTEGER DEFAULT 0," +
			" MediaType INTEGER DEFAULT 0," +
			" CID VARCHAR(32)," +
			" StopDate VARCHAR(16)," +
			" Duration INTEGER DEFAULT 0," +
			" Time VARCHAR(32));";
	private static final String MEETINGMEMBERS_CREATE = "CREATE TABLE MeetingMembers(" +
			" ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
			" MeetingID INTEGER DEFAULT 0," +
			" BuddyNO VARCHAR(16) NOT NULL," +
			" StartDate VARCHAR(16) NOT NULL," +
			" AnswerType INTEGER DEFAULT 0," +
			" MemberType INTEGER DEFAULT 0," +
			" StopDate VARCHAR(16));";
	// 数据库表名
	private static final String CONTACTS_TABLENAME = "Contacts";//通讯录
	private static final String CALLINGRECORDS_TABLENAME = "CallingRecords";//通话记录
	private static final String MESSAGERECORDS_TABLENAME = "MessageRecords";//消息记录
	private static final String MEETINGRECORDS_TABLENAME = "MeetingRecords";//视频会议
	private static final String MEETINGMEMBERS_TABLENAME = "MeetingMembers";//会议成员
	
	// 数据库表字段
	private static final String USERNO_COLUMNNAME = "UserNO";//登录号码
	private static final String BUDDYNO_COLUMNNAME = "BuddyNO";//好友号码
	private static final String BUDDYNAME_COLUMNNAME = "BuddyName";//好友姓名
	private static final String GROUPNO_COLUMNNAME = "GroupNO";//组别
	private static final String PHONENO_COLUMNNAME = "PhoneNO";//本地号码
	private static final String REMARK_COLUMNNAME = "Remark";//备注
	private static final String CREATEDATE_COLUMNNAME = "CreateDate";//添加时间
	private static final String UPDATEDATE_COLUMNNAME = "UpdateDate";//修改时间
	
	private static final String ID_COLUMNNAME = "ID";//编号
	private static final String STARTDATE_COLUMNNAME = "StartDate";//开始时间
	private static final String ANSWERDATE_COLUMNNAME = "AnswerDate";//通话开始
	private static final String STOPDATE_COLUMNNAME = "StopDate";//结束时间
	private static final String DURATION_COLUMNNAME = "Duration";//通话时长(秒)
	private static final String TIME_COLUMNNAME = "Time";//通话时长(显示)
	private static final String INOUTFLG_COLUMNNAME = "InOutFlg";//呼入呼出区分
	private static final String CALLSTATE_COLUMNNAME = "CallState";//呼叫状态
	private static final String SESSID_COLUMNNAME = "SessId";//呼叫标识
	
	private static final String CONTENT_COLUMNNAME = "Content";//消息内容
	private static final String CONTENTTYPE_COLUMNNAME = "ContentType";//消息类型
	private static final String LOCALFILEURI_COLUMNNAME = "LocalFileUri";//本地文件地址
	private static final String SERVERFILEURI_COLUMNNAME = "ServerFileUri";//服务器文件地址
	private static final String LENGTH_COLUMNNAME = "Length";//消息长度
	private static final String LAYOUT_COLUMNNAME = "Layout";//布局类型
	private static final String SENDDATE_COLUMNNAME = "SendDate";//发送时间
	private static final String SENDSTATE_COLUMNNAME = "SendState";//发出状态
	private static final String RECEIVEDATE_COLUMNNAME = "ReceiveDate";//接收时间
	private static final String RECEIVESTATE_COLUMNNAME = "ReceiveState";//阅读状态
	private static final String PROGRESSSTATE_COLUMNNAME = "ProgressState";//进度是否上传完成

	private static final String SID_COLUMNNAME = "SID";//消息标识
	private static final String CONFERENCENO_COLUMNNAME = "ConferenceNO";//会议编号
	private static final String CONFERENCENM_COLUMNNAME = "ConferenceNM";//会议名称
	private static final String ISMONITOR_COLUMNNAME = "IsMonitor";//会是否为监控
	private static final String CONFERENCETYPE_COLUMNNAME = "ConferenceType";//会议类型
	private static final String MEDIATYPE_COLUMNNAME = "MediaType";//通话类型
	private static final String CID_COLUMNNAME = "CID";//调度机里的会议唯一标识
	
	private static final String MEETINGID_COLUMNNAME = "MeetingID";//会议编号
	private static final String ANSWERTYPE_COLUMNNAME = "AnswerType";//应答模式
	private static final String MEMBERTYPE_COLUMNNAME = "MemberType";//成员类型
	
	// 数据库操作帮助类
	public static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context, String path) {
				super(context, path, null, DATABASE_VERSION);
		}
		
		public DatabaseHelper(Context context)
		{
			super(context,"ptt.db", null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(CONTACTS_CREATE);
			db.execSQL(CALLINGRECORDS_CREATE);
			db.execSQL(MESSAGERECORDS_CREATE);
			db.execSQL(MEETINGRECORDS_CREATE);
			db.execSQL(MEETINGMEMBERS_CREATE);
			Log.w("#######数据库创建#######", "#######数据库创建#######");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS Contacts");
			db.execSQL("DROP TABLE IF EXISTS CallingRecords");
			db.execSQL("DROP TABLE IF EXISTS MessageRecords");
			db.execSQL("DROP TABLE IF EXISTS MeetingRecords");
			db.execSQL("DROP TABLE IF EXISTS MeetingMembers");
			onCreate(db);
			Log.w("#######数据库升级#######", "#######数据库升级#######");
		}
	}
	
	// 构造函数
	public SQLiteHelper(Context ctx){
		this.mCtx = ctx;
	}
	
	// 打开数据库
	public SQLiteHelper open() throws SQLException {
		if (this.mCtx == null){
			return null;
		}
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		/*if (Boolean.parseBoolean(this.mCtx.getString(com.zzy.ptt.activity.R.string.sp_debug_on))){
			// 调试模式在存储卡中生成数据库文件
			String databasename = DATABASE_PATH;
			//有存储卡时保存于存储卡中
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				File file = new File(databasename);
				if(!file.exists())
				{
					file.mkdir();
				}
				databasename = DATABASE_PATH + "/" + DATABASE_NAME;
			}
			mDbHelper = new DatabaseHelper(mCtx, databasename);
			mDb = mDbHelper.getWritableDatabase();
		}
		else {
			// 非调试模式将raw中文件拷贝到存储卡中
			//有存储卡时保存于存储卡中
			String databasefile = this.mCtx.getFilesDir().getPath() + "/";
			File file = new File(databasefile);
			if(!file.exists())
			{
				file.mkdir();
			}
			databasefile = this.mCtx.getFilesDir().getPath() + "/" + DATABASE_NAME;
			file = new File(databasefile);
			if(!file.exists())
			{
				InputStream is = null;
				FileOutputStream os = null;
				try {
				//打开静态数据库文件的输入流
					is = this.mCtx.getResources().openRawResource(com.zzy.ptt.activity.R.raw.ptt);
					//打开目标数据库文件的输出流 
					os = this.mCtx.openFileOutput(DATABASE_NAME, Context.MODE_APPEND+Context.MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					int count = 0;
					//将静态数据库文件拷贝到目的地
					while ((count = is.read(buffer)) > 0) {
						os.write(buffer, 0, count);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null){
							os.flush();
							os.close();
						}
						if (is != null){
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (file.exists()){
				mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
			}
		}*/
		return this;
	}
	
	// 打开数据库
	public SQLiteHelper open(String userno) throws SQLException {
		if (Boolean.parseBoolean(this.mCtx.getString(R.string.sp_debug_on))){
			String databasename = DATABASE_PATH;
			/*有存储卡时保存于存储卡中*/
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				File file = new File(databasename);
				if(!file.exists())
				{
					file.mkdir();
				}
				databasename = DATABASE_PATH+"/"+DATABASE_NAME;
			}
			mDbHelper = new DatabaseHelper(mCtx, databasename);
			mDb = mDbHelper.getWritableDatabase();
		}
		else {
			/*有存储卡时保存于存储卡中*/
			String databasefile = this.mCtx.getFilesDir().getPath() + "/";
			File file = new File(databasefile);
			if(!file.exists())
			{
				file.mkdir();
			}
			databasefile = this.mCtx.getFilesDir().getPath() + "/" + DATABASE_NAME;
			file = new File(databasefile);
			if(!file.exists())
			{
				InputStream is = null;
				FileOutputStream os = null;
				try {
					/*打开静态数据库文件的输入流*/
					is = this.mCtx.getResources().openRawResource(R.raw.ptt);
					/*打开目标数据库文件的输出流 */
					os = this.mCtx.openFileOutput(DATABASE_NAME, Context.MODE_APPEND+Context.MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					int count = 0;
					/*将静态数据库文件拷贝到目的地*/
					while ((count = is.read(buffer)) > 0) {
						os.write(buffer, 0, count);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (os != null){
							os.flush();
							os.close();
						}
						if (is != null){
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (file.exists()){
				mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
			}
		}
		// 依据配置文件移除超范围通话记录数据
		int minid = 0;
		int maxcount = Integer.parseInt(this.mCtx.getString(R.string.default_callingrecord_count));
		if (maxcount > 0){
			Cursor cursor = mDb.rawQuery(
					"select min("+ID_COLUMNNAME+") from " + CALLINGRECORDS_TABLENAME
					+ " where " + USERNO_COLUMNNAME + " = ?"
					+ " order by " + ID_COLUMNNAME + " desc"
					+ " Limit " + String.valueOf(maxcount) + ";",
					new String[]{userno});
			if (cursor != null) {
				if (cursor.moveToFirst()){
					if (cursor.getInt(0) > 1)
					{
						minid = cursor.getInt(0);
					}
				}
				cursor.close();
			}
			if (minid > 0){
				mDb.delete(CALLINGRECORDS_TABLENAME, ID_COLUMNNAME + "<" + minid, null);
			}
		}
		// 依据配置文件移除超范围消息记录数据
		minid = 0;
		maxcount = Integer.parseInt(this.mCtx.getString(R.string.default_callingrecord_count));
		if (maxcount > 0){
			Cursor cursor = mDb.rawQuery(
					"select min("+ID_COLUMNNAME+") from " + MESSAGERECORDS_TABLENAME
					+ " where " + USERNO_COLUMNNAME + " = ?"
					+ " order by " + ID_COLUMNNAME + " desc"
					+ " Limit " + String.valueOf(maxcount) + ";",
					new String[]{userno});
			if (cursor != null) {
				if (cursor.moveToFirst()){
					if (cursor.getInt(0) > 1)
					{
						minid = cursor.getInt(0);
					}
				}
				cursor.close();
			}
			if (minid > 0){
				mDb.delete(MESSAGERECORDS_TABLENAME, ID_COLUMNNAME + "<" + minid, null);
			}
		}
		return this;
	}
	// 关闭数据库
	public void closeclose() {

		if(mDb != null){
			mDb.close();
		}
	}
	// 关闭数据库
	public void closeclose(String userno) {

		// 依据配置文件移除超范围通话记录数据
		int minid = 0;
		int maxcount = Integer.parseInt(this.mCtx.getString(R.string.default_callingrecord_count));
		if (maxcount > 0){
			Cursor cursor = mDb.rawQuery(
					"select min("+ID_COLUMNNAME+") from " + CALLINGRECORDS_TABLENAME
					+ " where " + USERNO_COLUMNNAME + " = ?"
					+ " order by " + ID_COLUMNNAME + " desc"
					+ " Limit " + String.valueOf(maxcount) + ";",
					new String[]{userno});
			if (cursor != null) {
				if (cursor.moveToFirst()){
					if (cursor.getInt(0) > 1)
					{
						minid = cursor.getInt(0);
					}
				}
				cursor.close();
			}
			if (minid > 0){
				mDb.delete(CALLINGRECORDS_TABLENAME, ID_COLUMNNAME + "<" + minid, null);
			}
		}
		// 依据配置文件移除超范围消息记录数据
		minid = 0;
		maxcount = Integer.parseInt(this.mCtx.getString(R.string.default_callingrecord_count));
		if (maxcount > 0){
			Cursor cursor = mDb.rawQuery(
					"select min("+ID_COLUMNNAME+") from " + MESSAGERECORDS_TABLENAME
					+ " where " + USERNO_COLUMNNAME + " = ?"
					+ " order by " + ID_COLUMNNAME + " desc"
					+ " Limit " + String.valueOf(maxcount) + ";",
					new String[]{userno});
			if (cursor != null) {
				if (cursor.moveToFirst()){
					if (cursor.getInt(0) > 1)
					{
						minid = cursor.getInt(0);
					}
				}
				cursor.close();
			}
			if (minid > 0){
				mDb.delete(MESSAGERECORDS_TABLENAME, ID_COLUMNNAME + "<" + minid, null);
			}
		}
		// 关闭数据库
		if (Boolean.parseBoolean(this.mCtx.getString(R.string.sp_debug_on))){
			mDbHelper.close();
		}
		else {
			mDb.close();
		}
	}
	
	/*创建一个联系人(已确认)*/
	public long createContacts(Contacts contact) {
		long result = 0;
		// 判断联系人是否存在
		Cursor cursor = mDb.rawQuery(
				"select count(*) from " + CONTACTS_TABLENAME + " where " + USERNO_COLUMNNAME + " = ? and " + BUDDYNO_COLUMNNAME + " = ?;",
				new String[]{contact.getUserNo(), contact.getBuddyNo()});
		if (cursor != null) {
			if (cursor.moveToFirst()){
				if (cursor.getInt(0) > 1)
				{
					result = -1;
				}
			}
			cursor.close();
		}
		if (result < 0){
			return result;
		}
		// 不存在则创建新的联系人
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, contact.getUserNo());
		initialValues.put(BUDDYNO_COLUMNNAME, contact.getBuddyNo());
		initialValues.put(BUDDYNAME_COLUMNNAME, contact.getBuddyName());
		initialValues.put(GROUPNO_COLUMNNAME, contact.getGroupNo());
		initialValues.put(PHONENO_COLUMNNAME, contact.getPhoneNo());
		initialValues.put(REMARK_COLUMNNAME, contact.getRemark());
		initialValues.put(CREATEDATE_COLUMNNAME, sdf.format(contact.getCreateDate()));
		initialValues.put(UPDATEDATE_COLUMNNAME, sdf.format(contact.getUpdateDate()));
		return mDb.insert(CONTACTS_TABLENAME, null, initialValues);
	}
	
	/*取得通讯录数量(已确认)*/
	public HashMap<String, Integer> getContactsCount() throws SQLException {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		Cursor cursor = mDb.rawQuery(
		"select " + GROUPNO_COLUMNNAME + ", count(*) from " + CONTACTS_TABLENAME + " group by " + GROUPNO_COLUMNNAME + ";",
		new String[]{});
		if (cursor != null) {
			if (cursor.moveToFirst()){
				result.put(cursor.getString(0), cursor.getInt(1));
			}
			cursor.close();
		}
		return result;
	}
	
	/*取得通讯录数据,组传空值取全部(已确认)*/
	public List<Contacts> getContactsByUserNO(String userno, String groupno) throws SQLException {
		List<Contacts> result = new ArrayList<Contacts>();
		Cursor cursor = mDb.query(true, CONTACTS_TABLENAME, new String[] { USERNO_COLUMNNAME, BUDDYNO_COLUMNNAME
				, BUDDYNAME_COLUMNNAME, GROUPNO_COLUMNNAME, PHONENO_COLUMNNAME, REMARK_COLUMNNAME, CREATEDATE_COLUMNNAME, UPDATEDATE_COLUMNNAME}
				, USERNO_COLUMNNAME+" = " + userno
				+ ((groupno==null||groupno.length()<=0)?"":" and " + GROUPNO_COLUMNNAME + "=" + groupno)
				, null, null,
				null, BUDDYNO_COLUMNNAME + " asc ", null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Contacts contact = new Contacts();
					contact.setUserNo(cursor.getString(0));
					contact.setBuddyNo(cursor.getString(1));
					contact.setBuddyName(cursor.getString(2));
					contact.setGroupNo(cursor.getString(3));
					if ((cursor.getString(4) == null)
						|| (cursor.getString(4).trim().length() <= 0)){
						contact.setPhoneNo(cursor.getString(1));
					}
					else {
						contact.setPhoneNo(cursor.getString(4));
					}
					contact.setRemark(cursor.getString(5));
					try {
						contact.setCreateDate(sdf.parse(cursor.getString(6)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					try {
						contact.setUpdateDate(sdf.parse(cursor.getString(7)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					result.add(contact);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}
	//获取联系人
	public Contacts getContactsByBudyNO(String budyNo,String groupno) throws SQLException {
		Contacts contact = new Contacts();
		Cursor cursor = mDb.query(true, CONTACTS_TABLENAME, new String[] { USERNO_COLUMNNAME, BUDDYNO_COLUMNNAME
				, BUDDYNAME_COLUMNNAME, GROUPNO_COLUMNNAME, PHONENO_COLUMNNAME, REMARK_COLUMNNAME, CREATEDATE_COLUMNNAME, UPDATEDATE_COLUMNNAME}
				, BUDDYNO_COLUMNNAME+" = " + budyNo
				+ ((groupno==null||groupno.length()<=0)?"":" and " + GROUPNO_COLUMNNAME + "=" + groupno)
				, null, null,
				null, BUDDYNO_COLUMNNAME + " asc ", null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				contact.setUserNo(cursor.getString(0));
				contact.setBuddyNo(cursor.getString(1));
				contact.setBuddyName(cursor.getString(2));
				contact.setGroupNo(cursor.getString(3));
				if ((cursor.getString(4) == null)
					|| (cursor.getString(4).trim().length() <= 0)){
					contact.setPhoneNo(cursor.getString(1));
				}
				else {
					contact.setPhoneNo(cursor.getString(4));
				}
				contact.setRemark(cursor.getString(5));
				try {
					contact.setCreateDate(sdf.parse(cursor.getString(6)));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				try {
					contact.setUpdateDate(sdf.parse(cursor.getString(7)));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
	        }
			cursor.close();
		}
		return contact;
	}
	
	/*创建一个通话记录(已确认)*/
	public long createCallingRecords(CallingRecords callingrecord){
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, callingrecord.getUserNo());
		initialValues.put(BUDDYNO_COLUMNNAME, callingrecord.getBuddyNo());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(callingrecord.getStartDate()));
		initialValues.put(ANSWERDATE_COLUMNNAME, sdf.format(callingrecord.getAnswerDate()));
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(callingrecord.getStopDate()));
		initialValues.put(DURATION_COLUMNNAME, callingrecord.getDuration());
		initialValues.put(TIME_COLUMNNAME, callingrecord.getTime());
		initialValues.put(INOUTFLG_COLUMNNAME, callingrecord.getInOutFlg());
		initialValues.put(CALLSTATE_COLUMNNAME, callingrecord.getCallState());
		initialValues.put(SESSID_COLUMNNAME, callingrecord.getSessId());
		return mDb.insert(CALLINGRECORDS_TABLENAME, null, initialValues);
	}

	/*更新一个通话记录*/
	public boolean updateCallingRecords(CallingRecords callingrecord) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, callingrecord.getUserNo());
		initialValues.put(BUDDYNO_COLUMNNAME, callingrecord.getBuddyNo());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(callingrecord.getStartDate()));
		initialValues.put(ANSWERDATE_COLUMNNAME, sdf.format(callingrecord.getAnswerDate()));
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(callingrecord.getStopDate()));
		initialValues.put(DURATION_COLUMNNAME, callingrecord.getDuration());
		initialValues.put(TIME_COLUMNNAME, callingrecord.getTime());
		initialValues.put(INOUTFLG_COLUMNNAME, callingrecord.getInOutFlg());
		initialValues.put(CALLSTATE_COLUMNNAME, callingrecord.getCallState());
		initialValues.put(SESSID_COLUMNNAME, callingrecord.getSessId());
		return mDb.update(CALLINGRECORDS_TABLENAME, initialValues,
				ID_COLUMNNAME + "=" + callingrecord.getId(), null) > 0;
	}
	
	//删除指定id通话记录
	public int deleteCallingRecords(String callingrecordid){
		int delete = mDb.delete(CALLINGRECORDS_TABLENAME, ID_COLUMNNAME + "=" + callingrecordid, null);

		return delete;
	}
	
	/*取得通话记录数据(已确认)*/
	public List<CallingRecords> getCallingRecordsByUserNO(String userno, Integer pagenum, Integer pagesize) throws SQLException {
		List<CallingRecords> result = new ArrayList<CallingRecords>();
		String sql= "select " + ID_COLUMNNAME + ","
				+ USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + STARTDATE_COLUMNNAME + "," + ANSWERDATE_COLUMNNAME + "," + STOPDATE_COLUMNNAME
				+ "," + DURATION_COLUMNNAME + "," + TIME_COLUMNNAME + "," + INOUTFLG_COLUMNNAME + "," + CALLSTATE_COLUMNNAME + "," + SESSID_COLUMNNAME 
				+ " from " + CALLINGRECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = " + userno
				+ " order by " + ID_COLUMNNAME + " desc Limit "+String.valueOf(pagenum*pagesize)
				+ ", "+String.valueOf(pagesize);
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					CallingRecords callingrecord = new CallingRecords();
					callingrecord.setId(cursor.getInt(0));
					callingrecord.setUserNo(cursor.getString(1));
					callingrecord.setBuddyNo(cursor.getString(2));
					try {
						callingrecord.setStartDate(sdf.parse(cursor.getString(3)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					try {
						callingrecord.setAnswerDate(sdf.parse(cursor.getString(4)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					try {
						callingrecord.setStopDate(sdf.parse(cursor.getString(5)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					callingrecord.setDuration(cursor.getInt(6));
					callingrecord.setTime(cursor.getString(7));
					callingrecord.setInOutFlg(cursor.getInt(8));
					callingrecord.setCallState(cursor.getInt(9));
					callingrecord.setSessId(cursor.getInt(10));
					result.add(callingrecord);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}
	
	public List<CallingRecords> getCallingRecordsByUserNOAndSessId(String userno,int sessId) throws SQLException
	{
		List<CallingRecords> result = new ArrayList<CallingRecords>();
		String sql = "select " + ID_COLUMNNAME + "," + USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + STARTDATE_COLUMNNAME + "," + ANSWERDATE_COLUMNNAME + "," + STOPDATE_COLUMNNAME + "," + DURATION_COLUMNNAME + "," + TIME_COLUMNNAME + "," + INOUTFLG_COLUMNNAME + "," + CALLSTATE_COLUMNNAME + "," + SESSID_COLUMNNAME + " from " + CALLINGRECORDS_TABLENAME + " where " + USERNO_COLUMNNAME + " = " + userno + " and " + SESSID_COLUMNNAME + " = " + sessId + " order by " + ID_COLUMNNAME + " desc";
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				while (!cursor.isAfterLast())
				{
					CallingRecords callingrecord = new CallingRecords();
					callingrecord.setId(cursor.getInt(0));
					callingrecord.setUserNo(cursor.getString(1));
					callingrecord.setBuddyNo(cursor.getString(2));
					try
					{
						callingrecord.setStartDate(sdf.parse(cursor.getString(3)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					try
					{
						callingrecord.setAnswerDate(sdf.parse(cursor.getString(4)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					try
					{
						callingrecord.setStopDate(sdf.parse(cursor.getString(5)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
					callingrecord.setDuration(cursor.getInt(6));
					callingrecord.setTime(cursor.getString(7));
					callingrecord.setInOutFlg(cursor.getInt(8));
					callingrecord.setCallState(cursor.getInt(9));
					callingrecord.setSessId(cursor.getInt(10));
					result.add(callingrecord);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return result;
	}
	
	/*创建一个消息记录(已确认)*/
	public long createMessageRecords(MessageRecords messagerecord){
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, messagerecord.getUserNo());
		initialValues.put(BUDDYNO_COLUMNNAME, messagerecord.getBuddyNo());
		initialValues.put(CONTENT_COLUMNNAME, messagerecord.getContent());
		initialValues.put(CONTENTTYPE_COLUMNNAME, messagerecord.getContentType());
		initialValues.put(LOCALFILEURI_COLUMNNAME, messagerecord.getLocalFileUri());
		initialValues.put(SERVERFILEURI_COLUMNNAME, messagerecord.getServerFileUri());
		initialValues.put(LENGTH_COLUMNNAME, messagerecord.getLength());
		initialValues.put(INOUTFLG_COLUMNNAME, messagerecord.getInOutFlg());
		initialValues.put(SENDDATE_COLUMNNAME, sdf.format(messagerecord.getSendDate()));
		initialValues.put(SENDSTATE_COLUMNNAME, messagerecord.getSendState());
		initialValues.put(RECEIVEDATE_COLUMNNAME, sdf.format(messagerecord.getReceiveDate()));
		initialValues.put(RECEIVESTATE_COLUMNNAME, messagerecord.getReceiveState());
		initialValues.put(LAYOUT_COLUMNNAME, messagerecord.getLayout());
		initialValues.put(PROGRESSSTATE_COLUMNNAME,messagerecord.getProgressState());
		return mDb.insert(MESSAGERECORDS_TABLENAME, null, initialValues);
	}

	public long upDateMessageRecordsProgressState(String fileServicePath,int progressState){

		ContentValues contentValues = new ContentValues();
		contentValues.put(PROGRESSSTATE_COLUMNNAME,progressState);
		int update = mDb.update(MESSAGERECORDS_TABLENAME, contentValues, SERVERFILEURI_COLUMNNAME + "=?", new String[]{fileServicePath});
		return update;
	}


	/*取得消息记录数据,组传空值取全部(已确认)*/
	public ArrayList<MessageRecords> getMessageRecordsByUserNO(String userno, String buddyno, Integer pagenum, Integer pagesize) throws SQLException {
		ArrayList<MessageRecords> result = new ArrayList<MessageRecords>();
		String sql= "select " + ID_COLUMNNAME + ","
				+ USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + CONTENT_COLUMNNAME +"," + CONTENTTYPE_COLUMNNAME + ","+ LOCALFILEURI_COLUMNNAME+","+SERVERFILEURI_COLUMNNAME + "," + LENGTH_COLUMNNAME + "," + INOUTFLG_COLUMNNAME
				+ "," + SENDDATE_COLUMNNAME + "," + SENDSTATE_COLUMNNAME + "," + RECEIVEDATE_COLUMNNAME + "," + RECEIVESTATE_COLUMNNAME+","+LAYOUT_COLUMNNAME +"," + PROGRESSSTATE_COLUMNNAME
				+ " from " + MESSAGERECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = '" + userno + "' and " + BUDDYNO_COLUMNNAME + " = '" + buddyno + "'"
				+ " order by " + ID_COLUMNNAME 
				+ " desc Limit "+String.valueOf(pagenum*pagesize)
				+ ","+String.valueOf(pagesize);
		if (mDb == null){
			return new ArrayList<MessageRecords>();
		}
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					MessageRecords messagerecord = new MessageRecords();
					messagerecord.setId(cursor.getInt(0));
					messagerecord.setUserNo(cursor.getString(1));
					messagerecord.setBuddyNo(cursor.getString(2));
					messagerecord.setContent(cursor.getString(3));
					messagerecord.setContentType(cursor.getInt(4));
					Log.e("sql查询类型", "cursor.getString(1):"+cursor.getString(2)+":"+cursor.getString(3)+",类型："+cursor.getInt(4)+"");
					messagerecord.setLocalFileUri(cursor.getString(5));
					messagerecord.setServerFileUri(cursor.getString(6));
					messagerecord.setLength(cursor.getInt(7));
					messagerecord.setInOutFlg(cursor.getInt(8));
					try {
						messagerecord.setSendDate(sdf.parse(cursor.getString(9)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setSendState(cursor.getInt(10));
					try {
						messagerecord.setReceiveDate(sdf.parse(cursor.getString(11)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setReceiveState(cursor.getInt(12));
					messagerecord.setLayout(cursor.getInt(13));
					messagerecord.setProgressState(cursor.getInt(14));
					result.add(messagerecord);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}

	public List<MessageRecords> getMyMessageRecordsByUserNO(String userno, Integer pagenum, Integer pagesize) throws SQLException {
		List<MessageRecords> result = new ArrayList<MessageRecords>();
		String sql= "select " + ID_COLUMNNAME + ","
				+ USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + CONTENT_COLUMNNAME +"," + CONTENTTYPE_COLUMNNAME + ","+ LOCALFILEURI_COLUMNNAME+","+SERVERFILEURI_COLUMNNAME + "," + LENGTH_COLUMNNAME + "," + INOUTFLG_COLUMNNAME
				+ "," + SENDDATE_COLUMNNAME + "," + SENDSTATE_COLUMNNAME + "," + RECEIVEDATE_COLUMNNAME + "," + RECEIVESTATE_COLUMNNAME+","+LAYOUT_COLUMNNAME
				+ " from " + MESSAGERECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = '" + userno + "'" 
				+ " order by " + ID_COLUMNNAME + " desc "
				+ " Limit "+String.valueOf(pagenum*pagesize)
				+ ","+String.valueOf(pagesize);
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					MessageRecords messagerecord = new MessageRecords();
					messagerecord.setId(cursor.getInt(0));
					messagerecord.setUserNo(cursor.getString(1));
					messagerecord.setBuddyNo(cursor.getString(2));
					messagerecord.setContent(cursor.getString(3));
					messagerecord.setContentType(cursor.getInt(4));
					messagerecord.setLocalFileUri(cursor.getString(5));
					messagerecord.setServerFileUri(cursor.getString(6));
					messagerecord.setLength(cursor.getInt(7));
					messagerecord.setInOutFlg(cursor.getInt(8));
					try {
						messagerecord.setSendDate(sdf.parse(cursor.getString(9)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setSendState(cursor.getInt(10));
					try {
						messagerecord.setReceiveDate(sdf.parse(cursor.getString(11)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setReceiveState(cursor.getInt(12));
					messagerecord.setLayout(cursor.getInt(13));
					result.add(messagerecord);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}
	/*取得用户最后一条消息*/
	public List<MessageRecords> getMessageRecordsLastByUserNO(String userno) throws SQLException {
		List<MessageRecords> result = new ArrayList<MessageRecords>();
		String sql= "select " + ID_COLUMNNAME + ","
				+ USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + CONTENT_COLUMNNAME +"," + CONTENTTYPE_COLUMNNAME + ","+ LOCALFILEURI_COLUMNNAME+","+SERVERFILEURI_COLUMNNAME + "," + LENGTH_COLUMNNAME + "," + INOUTFLG_COLUMNNAME
				+ "," + SENDDATE_COLUMNNAME + "," + SENDSTATE_COLUMNNAME + "," + RECEIVEDATE_COLUMNNAME + "," + RECEIVESTATE_COLUMNNAME+","+LAYOUT_COLUMNNAME
				+ " from " + MESSAGERECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = '" + userno + "'"
				+ " and " + ID_COLUMNNAME + " in ("
				+ " Select MAX(" + ID_COLUMNNAME + ")"
				+ " From " + MESSAGERECORDS_TABLENAME
				+ " GROUP BY " + BUDDYNO_COLUMNNAME + ")"
				+ " order by " + SENDDATE_COLUMNNAME + " desc ";
		
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					MessageRecords messagerecord = new MessageRecords();
					messagerecord.setId(cursor.getInt(0));
					messagerecord.setUserNo(cursor.getString(1));
					messagerecord.setBuddyNo(cursor.getString(2));
					messagerecord.setContent(cursor.getString(3));
					messagerecord.setContentType(cursor.getInt(4));
					messagerecord.setLocalFileUri(cursor.getString(5));
					messagerecord.setServerFileUri(cursor.getString(6));
					messagerecord.setLength(cursor.getInt(7));
					messagerecord.setInOutFlg(cursor.getInt(8));
					try {
						messagerecord.setSendDate(sdf.parse(cursor.getString(9)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setSendState(cursor.getInt(10));
					try {
						messagerecord.setReceiveDate(sdf.parse(cursor.getString(11)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					messagerecord.setReceiveState(cursor.getInt(12));
					messagerecord.setLayout(cursor.getInt(13));
					result.add(messagerecord);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}
	//删除指定用户的所有记录
	public void deleteMessageRecordByUserNo(String userNo,String budyNo){
		mDb.delete(MESSAGERECORDS_TABLENAME, USERNO_COLUMNNAME + "='" + userNo+"' and "+BUDDYNO_COLUMNNAME+"= '" + budyNo + "'", null);
	}
	//删除指定id消息
	public void deleteMessageRecord(String messageRecordId){
		mDb.delete(MESSAGERECORDS_TABLENAME, ID_COLUMNNAME + "=" + messageRecordId, null);
	}
	//删除指定servicefilepath消息
	public void deleteMessageRecordFilePath(String serverfileurl){
		mDb.delete(MESSAGERECORDS_TABLENAME, SERVERFILEURI_COLUMNNAME + "=" + serverfileurl, null);
	}
	//查询指定联系人未读信息数
	public int getMessageReadNoCountByUserNo(String userNo,String budyNo){
		String sql= "select count(*) from " + MESSAGERECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = '" + userNo + "'"
				+" AND " + BUDDYNO_COLUMNNAME + " = '" + budyNo + "'"
				+" AND " + RECEIVESTATE_COLUMNNAME + "=" + GlobalConstant.MESSAGE_READ_NO;
		Cursor cursor = mDb.rawQuery(sql, null);
		int count=0;
		if (cursor != null&&cursor.moveToNext()) {
			count=cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	//批量更新未读信息
	public boolean updateMessageRecord(String userNo,String budyNo){
		ContentValues initialValues = new ContentValues();
		initialValues.put(RECEIVESTATE_COLUMNNAME, GlobalConstant.MESSAGE_READ_YES);
		
		return mDb.update(MESSAGERECORDS_TABLENAME, initialValues,USERNO_COLUMNNAME+"='"+userNo+"' and "+ BUDDYNO_COLUMNNAME+"='"+budyNo+"' and "+ RECEIVESTATE_COLUMNNAME + "=" + GlobalConstant.MESSAGE_READ_NO, null) > 0;
	}
	/*创建一个会议记录(已确认)*/
	public long createMeetingRecords(MeetingRecords meetingrecord){
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, meetingrecord.getUserNo());
		initialValues.put(SID_COLUMNNAME, meetingrecord.getsID());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingrecord.getStartDate()));
		initialValues.put(CONFERENCENO_COLUMNNAME, meetingrecord.getConferenceNo());
		initialValues.put(CONFERENCENM_COLUMNNAME, meetingrecord.getConferenceNm());
		initialValues.put(ISMONITOR_COLUMNNAME, meetingrecord.getIsMonitor());
		initialValues.put(CONFERENCETYPE_COLUMNNAME, meetingrecord.getConferenceType());
		initialValues.put(MEDIATYPE_COLUMNNAME, meetingrecord.getMediaType());
		initialValues.put(CID_COLUMNNAME, meetingrecord.getcID());
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingrecord.getStopDate()));
		initialValues.put(DURATION_COLUMNNAME, meetingrecord.getDuration());
		initialValues.put(TIME_COLUMNNAME, meetingrecord.getTime());
		long result = mDb.insert(MEETINGRECORDS_TABLENAME, null, initialValues);
		if ((meetingrecord.getMeetingMembers() != null)
			&& (meetingrecord.getMeetingMembers().size() > 0)){
			for(int i=0; i<meetingrecord.getMeetingMembers().size(); i++){
				MeetingMembers meetingmember = meetingrecord.getMeetingMembers().get(i);
				initialValues = new ContentValues();
				initialValues.put(MEETINGID_COLUMNNAME, result);
				initialValues.put(BUDDYNO_COLUMNNAME, meetingmember.getBuddyNo());
				initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingmember.getStartDate()));
				initialValues.put(ANSWERTYPE_COLUMNNAME, meetingmember.getAnswerType());
				initialValues.put(MEMBERTYPE_COLUMNNAME, meetingmember.getMemberType());
				initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingmember.getStartDate()));
				mDb.insert(MEETINGMEMBERS_TABLENAME, null, initialValues);
			}
		}
		return result;
	}
	/*更新一个通话记录*/
	public boolean updateMeetingRecords(MeetingRecords meetingrecord){
		boolean result = false;
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERNO_COLUMNNAME, meetingrecord.getUserNo());
		initialValues.put(SID_COLUMNNAME, meetingrecord.getsID());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingrecord.getStartDate()));
		initialValues.put(CONFERENCENO_COLUMNNAME, meetingrecord.getConferenceNo());
		initialValues.put(CONFERENCENM_COLUMNNAME, meetingrecord.getConferenceNm());
		initialValues.put(ISMONITOR_COLUMNNAME, meetingrecord.getIsMonitor());
		initialValues.put(CONFERENCETYPE_COLUMNNAME, meetingrecord.getConferenceType());
		initialValues.put(MEDIATYPE_COLUMNNAME, meetingrecord.getMediaType());
		initialValues.put(CID_COLUMNNAME, meetingrecord.getcID());
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingrecord.getStopDate()));
		initialValues.put(DURATION_COLUMNNAME, meetingrecord.getDuration());
		initialValues.put(TIME_COLUMNNAME, meetingrecord.getTime());
		result = mDb.update(MEETINGRECORDS_TABLENAME, initialValues,
				ID_COLUMNNAME + "=" + meetingrecord.getId(), null) > 0;
		if ((meetingrecord.getMeetingMembers() != null)
			&& (meetingrecord.getMeetingMembers().size() > 0)){
			for(int i=0; i<meetingrecord.getMeetingMembers().size(); i++){
				MeetingMembers meetingmember = meetingrecord.getMeetingMembers().get(i);
				initialValues = new ContentValues();
				initialValues.put(MEETINGID_COLUMNNAME, meetingmember.getMeetingId());
				initialValues.put(BUDDYNO_COLUMNNAME, meetingmember.getBuddyNo());
				initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingmember.getStartDate()));
				initialValues.put(ANSWERTYPE_COLUMNNAME, meetingmember.getAnswerType());
				initialValues.put(MEMBERTYPE_COLUMNNAME, meetingmember.getMemberType());
				initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingmember.getStopDate()));
				result = mDb.update(MEETINGMEMBERS_TABLENAME, initialValues,
						ID_COLUMNNAME + "=" + meetingmember.getId(), null) > 0;
			}
		}
		return result;
	}
	/*取得一个会议记录(已确认)*/
	public MeetingRecords getMeetingRecordsByID(Integer id) throws SQLException {
		// 取得会议记录
		MeetingRecords meetingrecord = null;
		String sql= "select " + ID_COLUMNNAME + ","
				+ USERNO_COLUMNNAME + "," + SID_COLUMNNAME + "," + STARTDATE_COLUMNNAME + "," + CONFERENCENO_COLUMNNAME + "," + CONFERENCENM_COLUMNNAME
				+ "," + ISMONITOR_COLUMNNAME + "," + CONFERENCETYPE_COLUMNNAME + "," + MEDIATYPE_COLUMNNAME + "," + CID_COLUMNNAME + "," + STOPDATE_COLUMNNAME + "," + DURATION_COLUMNNAME + "," + TIME_COLUMNNAME
				+ " from " + MEETINGRECORDS_TABLENAME
				+ " where " + ID_COLUMNNAME + " = " + id;
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				meetingrecord = new MeetingRecords();
				meetingrecord.setId(cursor.getInt(0));
				meetingrecord.setUserNo(cursor.getString(1));
				meetingrecord.setsID(cursor.getString(2));
				try {
					meetingrecord.setStartDate(sdf.parse(cursor.getString(3)));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				meetingrecord.setConferenceNo(cursor.getString(4));
				meetingrecord.setConferenceNm(cursor.getString(5));
				meetingrecord.setIsMonitor(cursor.getInt(6));
				meetingrecord.setConferenceType(cursor.getInt(7));
				meetingrecord.setMediaType(cursor.getInt(8));
				meetingrecord.setcID(cursor.getString(9));
				try {
					meetingrecord.setStopDate(sdf.parse(cursor.getString(10)));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				meetingrecord.setDuration(cursor.getInt(11));
				meetingrecord.setTime(cursor.getString(12));
	        }
			cursor.close();
		}
		// 取得会议成员
		sql= "select " + ID_COLUMNNAME + "," + MEETINGID_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + STARTDATE_COLUMNNAME
				+ "," + ANSWERTYPE_COLUMNNAME + "," + MEMBERTYPE_COLUMNNAME
				+ "," + STOPDATE_COLUMNNAME
				+ " from " + MEETINGMEMBERS_TABLENAME
				+ " where " + MEETINGID_COLUMNNAME + " = " + id;
		cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
			meetingrecord.setMeetingMembers(new ArrayList<MeetingMembers>());
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					MeetingMembers meetingmember = new MeetingMembers();
					meetingmember.setId(cursor.getInt(0));
					meetingmember.setMeetingId(cursor.getInt(1));
					meetingmember.setBuddyNo(cursor.getString(2));
					try {
						meetingmember.setStartDate(sdf.parse(cursor.getString(3)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					meetingmember.setAnswerType(cursor.getInt(4));
					meetingmember.setMemberType(cursor.getInt(5));
					try {
						meetingmember.setStopDate(sdf.parse(cursor.getString(6)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					meetingrecord.getMeetingMembers().add(meetingmember);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return meetingrecord;
	}
	/*创建一个会议成员(已确认)*/
	public long createMeetingMembers(MeetingMembers meetingmember){
		ContentValues initialValues = new ContentValues();
		initialValues.put(MEETINGID_COLUMNNAME, meetingmember.getMeetingId());
		initialValues.put(BUDDYNO_COLUMNNAME, meetingmember.getBuddyNo());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingmember.getStartDate()));
		initialValues.put(ANSWERTYPE_COLUMNNAME, meetingmember.getAnswerType());
		initialValues.put(MEMBERTYPE_COLUMNNAME, meetingmember.getMemberType());
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingmember.getStopDate()));
		long result = mDb.insert(MEETINGMEMBERS_TABLENAME, null, initialValues);
		return result;
	}
	/*更新一个会议成员*/
	public boolean updateMeetingMembers(MeetingMembers meetingmember){
		ContentValues initialValues = new ContentValues();
		initialValues.put(MEETINGID_COLUMNNAME, meetingmember.getMeetingId());
		initialValues.put(BUDDYNO_COLUMNNAME, meetingmember.getBuddyNo());
		initialValues.put(STARTDATE_COLUMNNAME, sdf.format(meetingmember.getStartDate()));
		initialValues.put(ANSWERTYPE_COLUMNNAME, meetingmember.getAnswerType());
		initialValues.put(MEMBERTYPE_COLUMNNAME, meetingmember.getMemberType());
		initialValues.put(STOPDATE_COLUMNNAME, sdf.format(meetingmember.getStopDate()));
		return mDb.update(MEETINGMEMBERS_TABLENAME, initialValues,
				ID_COLUMNNAME + "=" + meetingmember.getId(), null) > 0;
	}
	/*取得通话记录数据,包含会议记录(已确认)*/
	public List<CallingRecords> getCallingAndMeetingRecordsByUserNO(String userno, Integer pagenum, Integer pagesize) throws SQLException {
		List<CallingRecords> result = new ArrayList<CallingRecords>();
		String sql= "select * from ("
				+ " Select " + ID_COLUMNNAME + "," + USERNO_COLUMNNAME + "," + BUDDYNO_COLUMNNAME + "," + STARTDATE_COLUMNNAME
				+ "," + ANSWERDATE_COLUMNNAME + ", '' " + CONFERENCENO_COLUMNNAME + ", '' " + CONFERENCENM_COLUMNNAME + ", 0 " + ISMONITOR_COLUMNNAME + ", 0 " + CONFERENCETYPE_COLUMNNAME + ", 0 " + MEDIATYPE_COLUMNNAME + ", '' " + CID_COLUMNNAME
				+ "," + STOPDATE_COLUMNNAME
				+ "," + DURATION_COLUMNNAME + "," + TIME_COLUMNNAME + "," + INOUTFLG_COLUMNNAME + "," + CALLSTATE_COLUMNNAME + "," + SESSID_COLUMNNAME 
				+ " from " + CALLINGRECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = " + userno
				+ " UNION ALL"
				+ " Select "+ ID_COLUMNNAME + "," + USERNO_COLUMNNAME + "," + SID_COLUMNNAME + "," + STARTDATE_COLUMNNAME
				+ "," + STARTDATE_COLUMNNAME + "," + CONFERENCENO_COLUMNNAME + "," + CONFERENCENM_COLUMNNAME + "," + ISMONITOR_COLUMNNAME + "," + CONFERENCETYPE_COLUMNNAME + "," + MEDIATYPE_COLUMNNAME + "," + CID_COLUMNNAME
				+ "," + STOPDATE_COLUMNNAME + "," + DURATION_COLUMNNAME + "," + TIME_COLUMNNAME + ", 0, 0"
				+ " from " + MEETINGRECORDS_TABLENAME
				+ " where " + USERNO_COLUMNNAME + " = " + userno
				+ " and " + ISMONITOR_COLUMNNAME + " = 0"
				+ ") as A"
				+ " order by " + STARTDATE_COLUMNNAME + " desc Limit "+String.valueOf(pagenum*pagesize)
				+ ", "+String.valueOf(pagesize);
		Cursor cursor = mDb.rawQuery(sql, null);
		if (cursor != null) {
	        if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					CallingRecords callingrecord = new CallingRecords();
					callingrecord.setId(cursor.getInt(0));
					callingrecord.setUserNo(cursor.getString(1));
					callingrecord.setBuddyNo(cursor.getString(2));
					try {
						callingrecord.setStartDate(sdf.parse(cursor.getString(3)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					
					try {
						callingrecord.setAnswerDate(sdf.parse(cursor.getString(4)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					
					callingrecord.setConferenceNo(cursor.getString(5));
					callingrecord.setConferenceNm(cursor.getString(6));
					callingrecord.setIsMonitor(cursor.getInt(7));
					callingrecord.setConferenceType(cursor.getInt(8));
					callingrecord.setMediaType(cursor.getInt(9));
					callingrecord.setcID(cursor.getString(10));
					
					
					try {
						callingrecord.setStopDate(sdf.parse(cursor.getString(11)));
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					callingrecord.setDuration(cursor.getInt(12));
					callingrecord.setTime(cursor.getString(13));
					callingrecord.setInOutFlg(cursor.getInt(14));
					callingrecord.setCallState(cursor.getInt(15));
					callingrecord.setSessId(cursor.getInt(16));
					result.add(callingrecord);
					cursor.moveToNext();
				}
	        }
			cursor.close();
		}
		return result;
	}
	// 删除指定id会议记录
	public void deleteMeetingRecords(String meetingrecordid){
		mDb.delete(MEETINGMEMBERS_TABLENAME, MEETINGID_COLUMNNAME + "=" + meetingrecordid, null);
		mDb.delete(MEETINGRECORDS_TABLENAME, ID_COLUMNNAME + "=" + meetingrecordid, null);
	}
}
