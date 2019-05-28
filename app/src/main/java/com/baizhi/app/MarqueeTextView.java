package com.baizhi.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ANGELCOMM on 2017/12/12.
 */

public class MarqueeTextView extends TextView {


    private OnMarqueeCompleteListener marqueeCompleteListener;

    private long mTime = 0;
    private long longs = 0;


    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public boolean isFocused(){
        return true;
    }


    // 开始监听
    public void setOnMarqueeCompleteListener(OnMarqueeCompleteListener marqueeCompleteListener) {
        this.marqueeCompleteListener = marqueeCompleteListener;
        // 避免一些机子反应慢，postDelayed一下
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mTime = -1;
                longs = -1;
            }
        }, 3000);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mTime == -1) {
                mTime = System.currentTimeMillis();
            } else if (longs == -1) {
                long time = System.currentTimeMillis();
                longs = time - mTime;
                mTime = time;
            } else if (longs != 0) {
                long time = System.currentTimeMillis();
                long thisLong = time - mTime;
                mTime = time;
                if (thisLong > 10 * longs) {
                    if (null != marqueeCompleteListener) {
                        marqueeCompleteListener.onMarqueeComplete();
                    }
                }
            }
        }

        public interface OnMarqueeCompleteListener {
            void onMarqueeComplete();
        }
}
