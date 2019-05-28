package com.azkj.pad.utility;

import com.azkj.pad.service.MessageReceiver;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

public class MediaScanner
{

    private MediaScannerConnection mediaScanConn = null;

    private MusicSannerClient client = null;

    private String filePath = null;

    private String fileType = null;

    private String[] filePaths = null;

    private Context context;

    private String notificationnum = null;

    /**
     * 然后调用MediaScanner.scanFile("/sdcard/2.mp3");
     * */
    public MediaScanner(Context context)
    {
	this.context = context;
	// 创建MusicSannerClient
	if (client == null)
	{
	    client = new MusicSannerClient();
	}

	if (mediaScanConn == null)
	{

	    mediaScanConn = new MediaScannerConnection(context, client);
	}
    }

    class MusicSannerClient implements
	    MediaScannerConnection.MediaScannerConnectionClient
    {

	
	public void onMediaScannerConnected()
	{
	    if (filePath != null)
	    {

		mediaScanConn.scanFile(filePath, fileType);
	    }

	    if (filePaths != null)
	    {

		for (String file : filePaths)
		{

		    mediaScanConn.scanFile(file, fileType);
		}
	    }
	}

	public void onScanCompleted(String path, Uri uri)
	{
	    // TODO Auto-generated method stub1419233666991.amr
	    mediaScanConn.disconnect();
	    
	    Log.e("OnScanCompleted", "OnScanCompleted="+path);
	    
	    if(null !=uri)
	    {
		freshList();
	    }
	    
	    filePath = null;
	    fileType = null;
	    filePaths = null; 
	    
	    
	    
	}

	@Override
	protected void finalize() throws Throwable
	{
	    super.finalize();
	}
    }

    /**
     * 扫描文件标签信息
     * 
     * @param filePath
     *            文件路径 eg:/sdcard/MediaPlayer/dahai.mp3
     * @param fileType
     *            文件类型 eg: audio/mp3 media/* application/ogg
     * */
    public void scanFile(String filepath, String fileType,
	    String notificationnum)
    {

	this.filePath = filepath;

	this.notificationnum = notificationnum;

	this.fileType = fileType;
	// 连接之后调用MusicSannerClient的onMediaScannerConnected()方法
	mediaScanConn.connect();
	
	Log.e("scanFile", "scanFile filepath="+filepath);
	Log.e("scanFile", "scanFile fileType="+fileType);
	Log.e("scanFile", "scanFile notificationnum="+notificationnum);
	
	
    }

    /**
     * @param filePaths
     *            文件路径
     * @param fileType
     *            文件类型
     * 
     *            此函数在没有使用
     * */
    public void scanFile(String[] filePaths, String fileType)
    {

	this.filePaths = filePaths;

	this.fileType = fileType;

	mediaScanConn.connect();

    }

    private void freshList()
    {
	Intent intent = new Intent();
	intent.setAction(GlobalConstant.ACTION_MEDIASCANNER_FILE_DATA_SIZE);
	context.sendBroadcast(intent);
    }
    /*private void recordNotification(String msgname)
    {
	MessageNotification.NEWESGNUM++;
	new MessageNotification(context, R.drawable.unread_msg, "音频", msgname,
		1);
    }*/
}

