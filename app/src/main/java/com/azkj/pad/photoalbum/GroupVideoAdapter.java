package com.azkj.pad.photoalbum;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.azkj.pad.activity.R;
import com.azkj.pad.photoalbum.MyImageView.OnMeasureListener;
import com.bumptech.glide.Glide;

import java.util.List;

public class GroupVideoAdapter extends BaseAdapter{
	private List<VideoBean> list;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private GridView mGridView;
	protected LayoutInflater mInflater;
	private Context context;

	@Override
	public int getCount() {
		return list==null||list.isEmpty() ? 0 :list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	public GroupVideoAdapter(Context context, List<VideoBean> list, GridView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		VideoBean mImageBean = list.get(position);
		String path = mImageBean.getTopVideoPath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.show_voide_grid_group_item, null);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_images);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_titles);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_counts);
			
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.show_photo_friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getVideoCounts()));
		//给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(R.id.group_images,path);
		
		
//		//利用NativeImageLoader类加载本地图片
//		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
//
//			@Override
//			public void onImageLoader(Bitmap bitmap, String path) {
//				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
//				if(bitmap != null && mImageView != null){
//					mImageView.setImageBitmap(bitmap);
//				}
//			}
//		});

//		MediaMetadataRetriever media = new MediaMetadataRetriever();
//		media.setDataSource(path);
//		Bitmap bitmap = media.getFrameAtTime();
//
//		if(bitmap != null){
//			viewHolder.mImageView.setImageBitmap(bitmap);
//		}else{
//			viewHolder.mImageView.setImageResource(R.drawable.show_photo_friends_sends_pictures_no);
//		}
		Glide.with(context).load(path).into(viewHolder.mImageView);
		return convertView;
	}

	
	public static class ViewHolder{
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}
}
