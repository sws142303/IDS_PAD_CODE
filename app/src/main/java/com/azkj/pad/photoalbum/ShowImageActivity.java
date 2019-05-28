package com.azkj.pad.photoalbum;

import java.util.List;

import com.azkj.pad.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;


public class ShowImageActivity extends Activity {
	private GridView mGridView;
	private List<String> list;
	public  List<String> strlist;
	private ChildAdapter adapter;
	public static ShowImageActivity instance = null; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);
		instance=this;
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		//从showphotoactivity中得到图片的list
		adapter = new ChildAdapter(this, list, mGridView);
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
	    Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
	       Intent intent=new Intent();
	       intent.setClass(ShowImageActivity.this, ShowPhotoActivity.class);
	       startActivity(intent);
	       ShowImageActivity.this.finish();
		Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
		//if()
		super.onBackPressed();
	}
	
	

	
}
