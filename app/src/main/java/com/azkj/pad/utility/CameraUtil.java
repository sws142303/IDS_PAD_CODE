package com.azkj.pad.utility;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

public class CameraUtil {
    private static CameraUtil cameraUtil;
    //320 240
    private int camWidth = 320;
    private int camHeight = 240;
    private Camera.Parameters parameters;
    private Camera mCamera;
    public static synchronized  CameraUtil getInstance(){
        if(cameraUtil==null) cameraUtil=new CameraUtil();
        return cameraUtil;
    }
    private int FindFrontCamera(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number


        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }
    private int FindBackCamera(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number


        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }


    /**
     *
     * @param holder
     * @param tag 1前置 2后置
     */
    public void surfaceCreated(SurfaceHolder.Callback callback,SurfaceHolder holder,int tag) {
        CloseCamera(holder,callback);
        int CammeraIndex=-1;
        Camera camera=null;
        if(tag==1)
            CammeraIndex=FindFrontCamera();
        else if(tag==2){
            CammeraIndex=FindBackCamera();
        }
       
        if(CammeraIndex!=-1) {
           mCamera=  Camera.open(CammeraIndex);
        }
        //return camera;
    }
    public void CloseCamera(SurfaceHolder holder,SurfaceHolder.Callback callback){
        if(mCamera!=null) {
            if(holder!=null&&callback!=null)
            holder.removeCallback(callback);
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.lock();
            mCamera.release();
            mCamera=null;
        }
    }
    /**
     * camera的预览
     */
    public void initCamera(Context context,Camera.PreviewCallback cb) {
        if(mCamera==null) return;
        /**设置了一堆属性并给camera设置这些属性*/
        parameters = mCamera.getParameters();
        parameters.setFlashMode("off"); // 无闪光灯
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFormat(ImageFormat.YV12);
        parameters.setPictureSize(camWidth, camHeight);
        parameters.setPreviewSize(camWidth, camHeight);
        //这两个属性 如果这两个属性设置的和真实手机的不一样时，就会报错
        mCamera.setParameters(parameters);
        setOrientation(mCamera,context);


        byte[] buf = new byte[camWidth * camHeight * 3 / 2];
        mCamera.addCallbackBuffer(buf);
        mCamera.setPreviewCallback(cb);
    }
    private void setOrientation(Camera mCamera, Context context) {
        // 横竖屏镜头自动调整
        if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait"); //
            parameters.set("rotation", 90); // 镜头角度转90度（默认摄像头是横拍）
            mCamera.setDisplayOrientation(90); // 在2.2以上可以使用
        } else {// 如果是横屏
            parameters.set("orientation", "landscape"); //
            mCamera.setDisplayOrientation(0); // 在2.2以上可以使用
        }
    }
    //当我们的程序开始运行，即使我们没有开始录制视频，我们的surFaceView中也要显示当前摄像头显示的内容
    public void doChange(SurfaceHolder holder,Activity context) {
        if(mCamera==null) return;
        try {
            mCamera.setPreviewDisplay(holder);
            //设置surfaceView旋转的角度，系统默认的录制是横向的画面，把这句话注释掉运行你就会发现这行代码的作用
            mCamera.setDisplayOrientation(getDegree(context));
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getDegree(Activity context) {
        //获取当前屏幕旋转的角度
        int rotating = context.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        //根据手机旋转的角度，来设置surfaceView的显示的角度
        switch (rotating) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }
}
