package com.azkj.pad.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class AsyncImageLoader
{
	 private final int REFRESH_IMAGE = 1;
	    private Map<String, SoftReference<Bitmap>> mImageCache;

	    public AsyncImageLoader(Context context)
	    {
		mImageCache = new HashMap<String, SoftReference<Bitmap>>();
	    }

	    public Bitmap getBitmap(String imageUrl)
	    {
		Bitmap result = null;

		if (mImageCache.containsKey(imageUrl))
		{
		    result = mImageCache.get(imageUrl).get();
		}
		return result;
	    }

	    public void loadPicImage(final String imageUrl,
								 final ImageCallback imageCallback, final ImageView v, final int type, final int viewWidth)
	    {
		final Handler handler = new Handler()
		{
		    @Override
		    public void handleMessage(Message msg)
		    {
			super.handleMessage(msg);
			switch (msg.what)
			{
			case REFRESH_IMAGE:
			    imageCallback.imageCallback((Bitmap) msg.obj, imageUrl, v);
			    break;
			default:
			    break;
			}
		    }
		};

		new Thread(new Runnable()
		{

		    @Override
		    public void run()
		    {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			if (type == 2)
			    bitmap = createPicLocalBitmap(imageUrl,viewWidth);
			else if(type==4){
			    bitmap= createVideoThumbnail(imageUrl);
			}
			mImageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
			Message msg = handler.obtainMessage(REFRESH_IMAGE, bitmap);
			handler.sendMessage(msg);

		    }
		}).start();
	    }

	    public interface ImageCallback
	    {
		public void imageCallback(Bitmap bitmap, String url, ImageView v);
	    }
	    
	    
	    
	    public Bitmap createPicLocalBitmap(String filePath, int viewWidth)
	    {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		opts.inSampleSize = computeScale(opts, viewWidth);
		opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
		opts.inJustDecodeBounds = false;

		try
		{
		    fis = new FileInputStream(filePath);
		    bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (Exception e)
		{
		} finally
		{
		    try
		    {
			fis.close();
		    } 
		    catch (IOException e)
		    {
		    }
		    catch (Exception e2) {
				// TODO: handle exception
			}
		}
		if (bitmap != null)
		{
		    return rotaingImageView(readPictureDegree(filePath), bitmap);
		}
		return bitmap;
	    }

	    private int readPictureDegree(String path)
	    {
		int degree = 0;
		try
		{
		    ExifInterface exifInterface = new ExifInterface(path);
		    int orientation = exifInterface.getAttributeInt(
			    ExifInterface.TAG_ORIENTATION,
			    ExifInterface.ORIENTATION_NORMAL);
		    switch (orientation)
		    {
		    case ExifInterface.ORIENTATION_ROTATE_90:
			degree = 90;
			break;
		    case ExifInterface.ORIENTATION_ROTATE_180:
			degree = 180;
			break;
		    case ExifInterface.ORIENTATION_ROTATE_270:
			degree = 270;
			break;
		    }
		} catch (IOException e)
		{
		    e.printStackTrace();
		}
		return degree;
	    }
	    
	    /*
	     * 旋转图片
	     * 
	     * @param angle
	     * 
	     * @param bitmap
	     * 
	     * @return Bitmap
	     */
	    private Bitmap rotaingImageView(int angle, Bitmap bitmap)
	    {
	    	// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
			bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	    }

	    private int computeScale(BitmapFactory.Options options, int viewWidth)
	    {
		int inSampleSize = 1;
		if (viewWidth == 0)
		{
		    return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHigth = options.outHeight;
		if (bitmapHigth > viewWidth || bitmapWidth > viewWidth)
		{
		    int widthScale = Math
			    .round((float) bitmapWidth / (float) viewWidth);
		    int heightScale = Math.round((float) bitmapHigth
			    / (float) viewWidth);
		    inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	    }

	    
	    public Bitmap createVideoThumbnail(String filePath)
	    {
		Bitmap bitmap = null;
		// filePath=filePath.replace(".avi", ".mp4");
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try
		{
		    retriever.setDataSource(filePath);
		    bitmap = retriever.getFrameAtTime(0,
			    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		} catch (IllegalArgumentException ex)
		{
		    // Assume this is a corrupt video file
		} catch (RuntimeException ex)
		{
		    System.out.println(ex.getMessage());
		    // Assume this is a corrupt video file.
		} finally
		{
		    try
		    {
			retriever.release();
		    } catch (RuntimeException ex)
		    {
			// Ignore failures while cleaning up.
		    }
		}
		
		return bitmap;
	    }
	    
}
