package com.azkj.sws.library.logger;

import com.azkj.pad.activity.PTT_3G_PadApplication;
import com.juphoon.lemon.ui.MtcUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 功能: 打印输出log
 * Created by Administrator on 2017/8/25.
 */

public class MyLogger {

    /**
     * 作用: 打印日志文件
     * @param className
     * @param logMessage
     */
    public static void startLog1(String className, Object... logMessage) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = sdf.format(new Date()) + ".txt";
        String path = MtcUtils.getDataDir(PTT_3G_PadApplication.sContext) + "/" + "PTT_3G_Pad" + "/log";
        Logger.file(LogTag.mk(className), fileName, new File(path), null,logMessage);
    }

    /**
     * 作用: 打印日志文件
     * @param className
     * @param
     * @param logMessage
     */
    public static void startLog(String className, String logMessage){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = sdf.format(new Date()) + ".txt";
        String path = MtcUtils.getDataDir(PTT_3G_PadApplication.sContext) + "/" + "PTT_3G_Pads" + "/log/";
        File targetFile = new File(path + fileName +".txt");
        if(!targetFile.exists()){
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 以指定文件创建RandomAccessFile对象
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(targetFile, "rw");
            // 将文件记录指针移动到最后
            raf.seek(targetFile.length());
            // 输出文件内容
            raf.write(logMessage.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
