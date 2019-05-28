package com.azkj.pad.utility;

import android.annotation.SuppressLint;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@SuppressLint("SimpleDateFormat")
public class FormatController {
	/**
	 * 返回系统当前时间
	 * @return
	 */
	public static String getStringDateNow(){
		Date nowDate=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString=format.format(nowDate);
		return dateString;
	}
	
	/**
	 * 保存文件名称
	 * @return
	 */
	public static String getNewFileNameByDate(){
		Date nowDate=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString=format.format(nowDate);
		return dateString;
	}
	
	/**
	 * 格式化GIS时间
	 * @return
	 */
	public static String formatPosDate(String posDate){
		String dateString="";
		try {
			SimpleDateFormat parseFormat=new SimpleDateFormat("yyyy-MM-dd");
			
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
			dateString=format.format(parseFormat.parse(posDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateString;
	}
	/**
	 * 格式化GIS时间 yuezs add
	 * @return yyyyMMdd
	 */
	public static String formatPosDates(String posDate){
		String dateString="";
		try {
			SimpleDateFormat parseFormat=new SimpleDateFormat("yyyy-MM-dd");
			
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
			dateString=format.format(parseFormat.parse(posDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateString;
	}
	/**
	 * 格式化GIS时间 yuezs add
	 * @return HHmmss
	 */
	public static String formatPosTime(String posDate){
		String dateString="";
		try {
			SimpleDateFormat parseFormat=new SimpleDateFormat("HH:mm");
			
			SimpleDateFormat format=new SimpleDateFormat("HHmmss");
			dateString=format.format(parseFormat.parse(posDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateString;
	}
	/**
	 * GPRMC时间格式(秒)
	 * @return
	 */
	public static String getGPRMCDateFormatSec(){
		Date nowDate=new Date();
		SimpleDateFormat format=new SimpleDateFormat("hhmmss.sss");
		String dateString=format.format(nowDate);
		return dateString;
	}
	
	/**
	 * GPRMC时间格式(天)
	 * @return
	 */
	public static String getGPRMCDateFormatDay(){
		Date nowDate=new Date();
		SimpleDateFormat format=new SimpleDateFormat("ddMMyy");
		String dateString=format.format(nowDate);
		return dateString;
	}
	
	/**
	 * 获取当前时间(java.sql.Date)
	 * @return
	 */
	public static java.sql.Date getSqlDate(){
		return longStringToSqlDate(getStringDateNow());
	}
	
	public static Date getDateNow(){
		Date now;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			now= format.parse(format.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			now=new Date();
		}
		return now;
	}
	/**
	 * 返回长时间格式(yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return
	 */
	public static String getDateLongFormat(Date date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString=format.format(date);
		return dateString;
	}
	/**
	 * 返回短时间格式(yyyy-MM-dd)
	 * @param date
	 * @return
	 */
	public static String getDateShortFormat(Date date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String dateString=format.format(date);
		return dateString;
	}
	/**
	 * 返回月时间格式(MM月dd日 HH:mm)
	 * @param date
	 * @return
	 */
	public static String getDateMonthFormat(Date date){
		SimpleDateFormat format=new SimpleDateFormat("MM月dd日 HH:mm");
		String dateString=format.format(date);
		return dateString;
	}
	/**
	 * 字符串转换成Date类型
	 * @param str
	 * @return
	 */
	public static Date stringToLongDate(String str){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try {
			date=format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	public static Date stringToShortDate(String str){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date date=null;
		try {
			date=format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * 字符串转换成SqlDate类型
	 * @param str
	 * @return
	 */
	public static java.sql.Date longStringToSqlDate(String str){
		java.sql.Date sqlDate=new java.sql.Date(stringToLongDate(str).getTime());
		return sqlDate;
	}
	public static java.sql.Date shortStringToSqlDate(String str){
		java.sql.Date sqlDate=new java.sql.Date(stringToShortDate(str).getTime());
		return sqlDate;
	}
	
	
	/**
	 * 秒转换成时分秒格式
	 * @param i
	 * @return
	 */
	public static String secToTime(int i) {
        String retStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (i <= 0)
            return "00分00秒";
        else {
            minute = i / 60;
            if (minute < 60) {
                second = i % 60;
                retStr = unitFormat(minute) + "分" + unitFormat(second)+"秒";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99时59分9秒";
                minute = minute % 60;
                second = i - hour * 3600 - minute * 60;
                retStr = unitFormat(hour) + "时" + unitFormat(minute) + "分"
                        + unitFormat(second)+"秒";
            }
        }
        return retStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = Integer.toString(i);
        return retStr;
    }
    
    /*
     * 计算两个时间之差（秒）
     */
    public static int getSecondByDate(Date begin,Date end) {
    	if(begin==null||end==null)
    		return 0;
    	if(begin.getTime()>end.getTime())
    		return 0;
		long second=(end.getTime()-begin.getTime())/1000;
		return (int)second;
	}
	
    /**
     * 从$GPRMC字符串中获取时间
     */
    public static String getUTCTimeFromGPRMC(String gprmc) {
		if (!gprmc.startsWith("$GPRMC"))
			return "";
		String[] arr = gprmc.split(",");
		String time = arr[1];
		String date = arr[9];

		String hour = time.substring(0, 2);
		String minute = time.substring(2, 4);
		String second = time.substring(4,6);

		String day = date.substring(0, 2);
		String month = date.substring(2, 4);
		String year = date.substring(4);

		return "20" + year + "-" + month + "-" + day + " " + hour + ":"
				+ minute + ":" + second;
	}
    
    public static String getUTCTimeFromStr(String time,String date){
    	String hour = time.substring(0, 2);
		String minute = time.substring(2, 4);
		String second = time.substring(4,6);

		String day = date.substring(0, 2);
		String month = date.substring(2, 4);
		String year = date.substring(4);

		return "20" + year + "-" + month + "-" + day + " " + hour + ":"
				+ minute + ":" + second;
    }
	
	
	
	
	
	
}
