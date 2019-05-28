package com.azkj.pad.photoalbum;

/**
 * Created by ANGELCOMM on 2017/12/19.
 */

public class VideoBean {
    private String topVideoPath;
    private String folderName;
    private int videoCounts;

    /**
     * 文件夹的第一个视频路径
     */
    public String getTopVideoPath() {
        return topVideoPath;
    }

    public void setTopVideoPath(String topVideoPath) {
        this.topVideoPath = topVideoPath;
    }

    /**
     * 文件夹名
     */
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 文件夹中的视频数量
     */
    public int getVideoCounts() {
        return videoCounts;
    }

    public void setVideoCounts(int videoCounts) {
        this.videoCounts = videoCounts;
    }
}
