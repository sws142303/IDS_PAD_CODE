package com.azkj.pad.activity;

import com.azkj.pad.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MessageImageShowActivity extends Activity {
	private ImageView showImage;
	private LinearLayout showLinearLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messageimageshow);
		
		showImage=(ImageView)findViewById(R.id.showImage);
		showLinearLayout=(LinearLayout)findViewById(R.id.showLinearLayout);
		
		Intent intent=getIntent();
		String src=intent.getStringExtra("src");
		if(src.length()>0){
			try {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inJustDecodeBounds=true;
				options.inSampleSize=computeSampleSize(options,-1,(int)0.5*1024*1024);//计算缩放比例;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable=true;//让系统能及时回收内存
				options.inInputShareable=true;
				options.inJustDecodeBounds=false;
				Bitmap bitmap=BitmapFactory.decodeFile(src,options);
				showImage.setImageBitmap(bitmap);
			} catch (OutOfMemoryError e) {
				Toast.makeText(MessageImageShowActivity.this, "打开图片失败", Toast.LENGTH_SHORT).show();
			}
			
		}
		
		showLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	public int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
