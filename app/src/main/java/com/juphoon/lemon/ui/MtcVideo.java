package com.juphoon.lemon.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import com.juphoon.lemon.MtcCall;
import com.juphoon.lemon.MtcMediaConstants;
import com.juphoon.lemon.MtcNumber;
import com.juphoon.lemon.ST_MTC_RECT;

public class MtcVideo {
    
    public static int sMode = MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT;
    
    public static void startLocal(int dwSessId, View local) {
        MtcNumber numWidth = new MtcNumber();
        MtcNumber numHeight = new MtcNumber();
        MtcCall.Mtc_SessGetVideoLocalSize(dwSessId, numWidth, numHeight);
        MtcCall.Mtc_SessPreviewSetArea(-1, null, (short)0, (short)0, numWidth.getValue(), numHeight.getValue());
        MtcCall.Mtc_SessPreviewShow(-1, true);
    }
    
    public static void startRemote(int dwSessId, int localOrientation) {
        MtcCall.Mtc_SessRotateLocal(dwSessId, localOrientation);
        MtcCall.Mtc_SessCameraAttach(dwSessId);
        MtcCall.Mtc_SessVideoStart(dwSessId);
    }
    
    public static void videoSize(int dwSessId, int dwWidth, int dwHeight, int localOrientation, 
            int screenWidth, int screenHeight, View remote) {
        MtcDelegate.log("videoSize " + screenWidth + ", " +screenHeight);
        Point remoteSize = calcSize(dwWidth, dwHeight, localOrientation, screenWidth, screenHeight);
        setViewSize(remote, remoteSize);
        if (remote.getVisibility() != View.VISIBLE) {
            MtcCall.Mtc_SessRenderReset(dwSessId);
            MtcCall.Mtc_SessRenderAdd(dwSessId, remote, (short) 0, (short) 0, remoteSize.x, remoteSize.y);
            MtcCall.Mtc_SessRenderBuild(dwSessId);
            MtcCall.Mtc_SessRotateRemote(dwSessId, localOrientation);
            remote.setVisibility(View.VISIBLE);
        } else {
            alphaAnimation(remote);
        }
    }
    
    public static void orientationChanged(int dwSessId, int remoteWidth, int remoteHeight,
            int localOrientation, int screenWidth, int screenHeight, View remote, View local) {
        if (local.getVisibility() == View.VISIBLE) {
            MtcCall.Mtc_SessRotateLocal(dwSessId, localOrientation);
        }
        if (remote.getVisibility() == View.VISIBLE) {
            MtcCall.Mtc_SessRotateRemote(dwSessId, localOrientation);
            Point remoteSize = calcSize(remoteWidth, remoteHeight, localOrientation, screenWidth, screenHeight);
            setViewSize(remote, remoteSize);
            alphaAnimation(remote);
        }
    }

    public static Point calcLocalSize(int localWidth, int localHeight,
    		int screenWidth, int screenHeight) {
        return calcSize(localWidth, localHeight, MtcMediaConstants.EN_MTC_ORIENTATION_PORTRAIT, 
                screenWidth, screenHeight);
    }
    
    public static Point calcSize(int dwWidth, int dwHeight, int localOrientation, 
            int screenWidth, int screenHeight) {
        if (localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_RIGHT 
                || localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_LEFT) {
            int tmp = screenWidth;
            screenWidth = screenHeight;
            screenHeight = tmp;
        }

        Point size = new Point();
        
        int screenWidthXHeight = screenWidth * dwHeight;
        int screenHeightXWidth = screenHeight * dwWidth;
        if (screenWidthXHeight == 0 || screenHeightXWidth == 0) {
            return size;
        }
        
        int mode = sMode;
        if (mode != MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT) {
            if (dwWidth > dwHeight) {
                if (localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_PORTRAIT 
                        || localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_PORTRAIT_UPSIDEDOWN)
                    mode = MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT;
                
            } else {
                if (localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_RIGHT 
                        || localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_LEFT)
                    mode = MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT;
            }
        }
        
        if (mode == MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT) {
            if (screenWidthXHeight < screenHeightXWidth) {
                size.x = screenWidth;
                size.y = screenWidthXHeight / dwWidth;
            } else {
                size.x = screenHeightXWidth / dwHeight;
                size.y = screenHeight;
            }
        } else {
            if (screenWidthXHeight > screenHeightXWidth) {
                size.x = screenWidth;
                size.y = screenWidthXHeight / dwWidth;
            } else {
                size.x = screenHeightXWidth / dwHeight;
                size.y = screenHeight;
            }
        }
       
        if (localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_RIGHT 
                || localOrientation == MtcMediaConstants.EN_MTC_ORIENTATION_LANDSCAPE_LEFT) {
            int tmp = size.x;
            size.x = size.y;
            size.y = tmp;
        }
        return size;
    }
    
    public static ST_MTC_RECT calcLocalRect(int localWidth, int localHeight,
    		int screenWidth, int screenHeight) {
        int height = (int) Math.sqrt(screenWidth * screenHeight * localHeight / localWidth / 24);
        if (Build.MODEL.equals("GT-I9000")) {
            int remainder = height % 16;
            if (remainder <= 8) {
                height -= remainder;
            } else {
                height += 16 - remainder;
            }
        }
        int width = height * localWidth / localHeight;
        
        ST_MTC_RECT localRect = new ST_MTC_RECT();
        localRect.setIX(10);
        localRect.setIY(10);
        localRect.setIWidth(width);
        localRect.setIHeight(height);
        return localRect;
    }
    
    public static ST_MTC_RECT getViewRect(View v) {
        ST_MTC_RECT rect = new ST_MTC_RECT();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
        rect.setIX(params.leftMargin);
        rect.setIY(params.topMargin);
        rect.setIWidth(params.width);
        rect.setIHeight(params.height);
        return rect;
    }

    public static void setViewRect(View v, ST_MTC_RECT rect) {
        FrameLayout.LayoutParams rlp = new FrameLayout.LayoutParams(rect.getIWidth(), rect.getIHeight());
        rlp.leftMargin = rect.getIX();
        rlp.topMargin = rect.getIY();

        v.setLayoutParams(rlp);
    }

    public static void setViewSize(View v, Point size) {
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(size.x, size.y);
        flp.gravity = Gravity.CENTER;
        v.setLayoutParams(flp);
    }
    
    private static void alphaAnimation(final View view) {
        final View alphaView = new View(view.getContext());
        alphaView.setBackgroundColor(Color.BLACK);
        final ViewGroup parent = (ViewGroup) view.getParent();
        parent.addView(alphaView, 1);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        alpha.setDuration(1000);
        alpha.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // removeView directly will cause i9100 crash
                parent.post(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(alphaView);
                    }
                });
            }
        });
        alphaView.startAnimation(alpha);
    }
    
    public static class OnTouchMoveListener implements OnTouchListener {

        int offsetX = 0;
        int offsetY = 0;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final int action = event.getAction();
            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();
            ViewGroup.LayoutParams p = v.getLayoutParams();
            if (!(p instanceof FrameLayout.LayoutParams))
                return false;
            FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) p;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    offsetX = rlp.leftMargin - x;
                    offsetY = rlp.topMargin - y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int iX = (int) (offsetX + x);
                    final int iY = (int) (offsetY + y);
                    v.layout(iX, iY, iX + rlp.width, iY + rlp.height);
                    break;
                case MotionEvent.ACTION_UP:
                    final int iX2 = offsetX + x;
                    final int iY2 = offsetY + y;
                    rlp.leftMargin = iX2;
                    rlp.topMargin = iY2;
                    v.setLayoutParams(rlp);
                    offsetX = 0;
                    offsetY = 0;
                    break;
            }
            return true;
        }
    }
    
}
