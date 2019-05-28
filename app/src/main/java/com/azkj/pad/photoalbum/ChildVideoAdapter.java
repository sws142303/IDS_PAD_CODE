package com.azkj.pad.photoalbum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.azkj.pad.activity.R;
import com.azkj.pad.activity.ShowVideoActivity;
import com.azkj.pad.photoalbum.MyImageView.OnMeasureListener;
import com.azkj.pad.utility.GlobalConstant;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ChildVideoAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	//hashmap包括两个值，一个Integer和一个 Boolean
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;
	private Context context;

	public ChildVideoAdapter(Context context, List<String> list, GridView mGridView) {
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
		this.list = filterVideo(list);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return list.isEmpty() ? 0 : list.size();
	}
	//得到图片
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.show_photo_grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			
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
		viewHolder.mImageView.setTag(R.id.child_image,path);
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//如果是未选中的CheckBox,则添加动画
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
				    Intent intent = new Intent(GlobalConstant.ACTION_MESSAGE_CHOOSEPHOTO);
					intent.putExtra("swsVideo", list.get(position));
					ShowVideosActivity.instance.sendBroadcast(intent);
					ShowVideosActivity.instance.finish();
				    ShowVideoActivity.instance.finish();
				}
				mSelectMap.put(position, isChecked);
			}
		});
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
		//利用NativeImageLoader类加载本地图片
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


//		if(bitmap != null){
//			viewHolder.mImageView.setImageBitmap(bitmap);
//		}else{
//			viewHolder.mImageView.setImageResource(R.drawable.show_photo_friends_sends_pictures_no);
//		}
		Glide.with(context).load(path).into(viewHolder.mImageView);
		return convertView;
	}
	
	
	
	/**
	 * 获取选中的Item的position
	 * @return
	 */
	public List<Integer> getSelectItems(){
		List<Integer> intlist = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				intlist.add(entry.getKey());
			}
		}
		
		return intlist;
	}
	
	//获取选中项的多选path
	public List<String> getpath()
	{
	    List<String> strlist=new ArrayList<String>();
	    for(Iterator<Map.Entry<Integer, Boolean>> it=mSelectMap.entrySet().iterator();it.hasNext();)
	    {
		Map.Entry<Integer, Boolean> entry = it.next();
		if(entry.getValue()){
			strlist.add(list.get(entry.getKey()));
		}
	    }
	    return strlist;
	}
	
	//返回path
	void getselectpash(String path)
	{
		/*Intent intent=new Intent();
		//前一个是当前的，后一个是现在的
		intent.setClass(chatroom.this,homepage.class);
		startActivity(intent);//启动Activity
		mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
		chatroom.this.finish();//关闭当前Activity*/
	}
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

	/**10M=10485760 b,大于50M的不显示
	 * 过滤视频文件
	 * @param videoInfos
	 * @return
	 */
	private List<String> filterVideo(List<String> videoInfos){
		List<String> newVideos=new ArrayList<String>();
		for(String videoInfo:videoInfos){
			File f=new File(videoInfo);
			if(f.exists()&&f.isFile()&&f.length()<=52428800){
				newVideos.add(videoInfo);
				Log.i("TGA","文件大小"+f.length());
			}else {
				Log.i("TGA","文件太小或者不存在");
			}
		}
		return newVideos;
	}
}
