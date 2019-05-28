package com.azkj.pad.photoalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.azkj.pad.activity.R;
import com.azkj.pad.activity.ShowVideoActivity;

import java.util.List;

public class ShowVideosActivity extends Activity {
    private GridView mGridView;
    private List<String> list;
    public  List<String> strlist;
    private ChildVideoAdapter adapter;
    public static ShowVideosActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video2);
        Toast.makeText(ShowVideosActivity.this, "不显示大于50M的视频", Toast.LENGTH_SHORT).show();
        instance=this;
        mGridView = (GridView) findViewById(R.id.video_grid2);
        list = getIntent().getStringArrayListExtra("data");
        //从showphotoactivity中得到图片的list
        adapter = new ChildVideoAdapter(this, list, mGridView);
        mGridView.setAdapter(adapter);
    }
    //写函数完成返回图片的path
    public void getimagepath()
    {
        strlist=adapter.getpath();
        for(int i=0;i<strlist.size();i++)
            System.out.println(strlist.get(i));
    }
    @Override
    public void onBackPressed() {
        //Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        intent.setClass(ShowVideosActivity.this, ShowVideoActivity.class);
        startActivity(intent);
        ShowVideosActivity.this.finish();
        //Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
        //if()
        super.onBackPressed();
    }



}
