package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.azkj.pad.photoalbum.GroupVideoAdapter;
import com.azkj.pad.photoalbum.ShowVideosActivity;
import com.azkj.pad.photoalbum.VideoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShowVideoActivity extends Activity {

    private GridView grid_video;
    private Object videoURLMethod;
    private List<VideoBean> list = new ArrayList<VideoBean>();
    private List<HashMap<String, String>> listImage;
    public static ShowVideoActivity instance = null;
    private HashMap<String,List<String>> hashMap = new HashMap<String,List<String>>();
    private final static int SCAN_OK = 1;
    private GroupVideoAdapter adapter;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:

                    adapter = new GroupVideoAdapter(ShowVideoActivity.this, list = subGroupOfImage(hashMap), grid_video);
                    grid_video.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        instance = this;
        grid_video = (GridView) findViewById(R.id.grid_video);
       // new getURLAsync().execute();
        new getURLAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,0);
        grid_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                List<String> childList = hashMap.get(list.get(position).getFolderName());
                Intent mIntent = new Intent(ShowVideoActivity.this, ShowVideosActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
                startActivity(mIntent);
                ShowVideoActivity.this.finish();
            }
        });

    }


    public void getContentProvider(Uri uri, String[] projection, String orderBy) {
        // TODO Auto-generated method stub
        listImage = new ArrayList<HashMap<String, String>>();
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, orderBy);
        if (null == cursor) {
            return ;
        }
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            for(int i=0;i<projection.length;i++){
                map.put(projection[i], cursor.getString(i));
                System.out.println(projection[i]+":"+cursor.getString(i));
            }
            listImage.add(map);
        }
        //通知Handler扫描图片完成
        mHandler.sendEmptyMessage(SCAN_OK);
        Log.e("ShowVideo=====返回来的视频地址",""+listImage.toString());
    }




    class getURLAsync extends AsyncTask<Object,Object,Object>{

        @Override
        protected Object doInBackground(Object... objects) {
            String[] projection = {MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATA};
                String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                getContentProvider(uri, projection, orderBy);
            return null;
        }

        @Override
        protected void onPostExecute(Object contentProvider) {
            super.onPostExecute(contentProvider);

            if (listImage != null || listImage.size() >0){
                ArrayList<String> listVideoPath = new ArrayList<>();
                for (int i = 0; i < listImage.size(); i++){
                    HashMap<String, String> stringStringHashMap = listImage.get(i);
                    String display_name = stringStringHashMap.get("_display_name");
                    Log.e("获取数据","display_name:"+display_name);
                    String data = stringStringHashMap.get("_data");
                    Log.e("获取数据","data:"+data);
                    String id = stringStringHashMap.get("_id");
                    Log.e("获取数据","id:"+id);
                    listVideoPath.add(data);
                    //获取该图片的父路径名
                    String parentName = new File(data).getParentFile().getName();

                    if (!hashMap.containsKey(parentName)){
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(data);
                        hashMap.put(parentName, chileList);
                    }else {
                        hashMap.get(parentName).add(data);
                    }
                }
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }
    }
    private List<VideoBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
        if(mGruopMap.size() == 0){
            return null;
        }
        List<VideoBean> list = new ArrayList<VideoBean>();
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            VideoBean mImageBean = new VideoBean();
            String key = entry.getKey();
            mImageBean.setFolderName(key);
            mImageBean.setVideoCounts(listImage.size());
            mImageBean.setTopVideoPath(listImage.get(0).get("_data"));//获取该组的第一张图片
            list.add(mImageBean);
            Log.e("14144444444444444","key:"+key+",长度："+listImage.size()+",路径："+listImage.get(0).get("_data"));
        }
        return list;
    }


}
