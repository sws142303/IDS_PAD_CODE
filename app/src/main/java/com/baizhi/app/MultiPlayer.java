package com.baizhi.app;

/**
 * Created by ANGELCOMM on 2017/12/1.
 */

public class MultiPlayer {

    public static final int kStopped = 0;
    public static final int kPaused = 1;
    public static final int kPlaying = 2;


    static {

//        System.loadLibrary("avutil-52");
//        System.loadLibrary("swscale-2");
//        System.loadLibrary("avcodec-55");
//        System.loadLibrary("avformat-55");
//        System.loadLibrary("swresample-0");
//        System.loadLibrary("avfilter-4");
//        System.loadLibrary("SDL2");
//        System.loadLibrary("common");
//        System.loadLibrary("rtpclient");
//        System.loadLibrary("rtspclient");
//        System.loadLibrary("LitePlayer");
//        System.loadLibrary("MultiPlayer");
    }

    /**
     * ???????????,????????????????
     * @return
     */
    public native boolean startup();

    /**
     * ????????, ???????????????
     */
    public native void cleanup();

    /**
     * ??????????
     * @param obj ???Surface
     * @return 0 ??????, ????????????
     */
    public native int create(Object obj);

    /**
     * ?????????
     */
    public native void destroy();

    /**
     * ?ж??????????
     * @return true ??????????
     */
    public native boolean isCreated();

    /**
     * ???????
     * @param url
     * @return 0 ??????
     */
    public native int open(String url);

    /**
     * ????????
     */
    public native void close();

    /**
     * ?ж??????????
     * @return true ????????
     */
    public native boolean isOpen();

    /**
     * ????
     * @return 0 ??????
     */
    public native int play();

    /**
     * ???
     * @return 0 ??????
     */
    public native int pause();

    /**
     * ??????
     */
    public native void stop();

    /**
     * ?????????
     * @return ??????
     */
    public native int getState();

    /**
     * ?????,???????л?
     * @return 0 ??????
     */
    public native int togglePlay();


    public int	m_handle = 0;


}
