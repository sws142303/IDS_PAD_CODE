package com.azkj.pad.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.azkj.pad.utility.CommonMethod;
import com.azkj.pad.utility.FormatController;
import com.azkj.pad.utility.GlobalConstant;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MyLocationActivity extends Activity implements MKOfflineMapListener {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
	private boolean isFirstLoc = true;// 是否首次定位
	private MKOfflineMap mOffline = null;
	
	private Button btnSendLocation,btnCancelLocation;
	private String imageFilePath;
	private LinearLayout lLayoutMap;
	private ArrayList<MKOLUpdateElement> localMapList = null;
	private SharedPreferences spfs = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylocation);
		btnSendLocation=(Button)findViewById(R.id.btnSendLocation);
		btnCancelLocation=(Button)findViewById(R.id.btnCancelLocation);
		lLayoutMap = (LinearLayout)findViewById(R.id.ll_map);
		//mMapView = (MapView) findViewById(R.id.bmapView);
		spfs = PreferenceManager.getDefaultSharedPreferences(PTT_3G_PadApplication.sContext);
		
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		int num = mOffline.importOfflineData();	    
		// 获取已下过的离线地图信息
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) 
		{
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		if(localMapList != null && localMapList.size() > 0)
		{
			lLayoutMap.removeAllViews();
			int offlineMapCityId = spfs.getInt(GlobalConstant.SP_OFFLINEMAP_CITYID, -1);
			if(offlineMapCityId == -1)
			{
				MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
				mMapView = new MapView(this,new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));
				lLayoutMap.addView(mMapView);
			}
			else
			{
				MKOLUpdateElement mk = null;
				for(MKOLUpdateElement mkolUpdateElement : localMapList)
				{
					if(mkolUpdateElement.cityID == offlineMapCityId)
					{
						mk = mkolUpdateElement;
					}
				}
				if(mk == null)
				{
					MKOLUpdateElement mkolUpdateElement = localMapList.get(0);
					mMapView = new MapView(this,new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mkolUpdateElement.geoPt).build()));			
					lLayoutMap.addView(mMapView);
					int cityID = mkolUpdateElement.cityID;
				}
				else
				{
					mMapView = new MapView(this,new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mk.geoPt).build()));			
					lLayoutMap.addView(mMapView);
					int cityID = mk.cityID;
				}
			}
		}
		else
		{
			mMapView = (MapView) findViewById(R.id.bmapView);
			mMapView.invalidate();			
		}
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
		/*百度地图部分开始*/
		//使用默认定位图标
		//mCurrentMode = LocationMode.NORMAL;//普通
		//mCurrentMode = LocationMode.FOLLOWING;//跟随
		//mCurrentMode = LocationMode.COMPASS;//罗盘
		//mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
		// MyLocationConfiguration(mCurrentMode, true, null));
		
		//自定义定位图标
		mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		btnSendLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBaiduMap != null)
				{
				mBaiduMap.snapshot(callback);//截屏回调
				Toast.makeText(MyLocationActivity.this, "正在截取屏幕图片...",Toast.LENGTH_SHORT).show();
				btnSendLocation.setEnabled(false);
				}
			}
		});
		btnCancelLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() {
		if(mMapView != null)
		{
		mMapView.onPause();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(mMapView != null)
		{
		mMapView.onResume();
		}
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		if(mLocClient!=null)
		{
			// 退出时销毁定位
			mLocClient.stop();
		}
		if(mBaiduMap != null)
		{
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
		}
		if(mMapView != null)
		{
			mMapView.onDestroy();
			mMapView = null;
		}
		super.onDestroy();
	}
	
	SnapshotReadyCallback callback = new SnapshotReadyCallback() {  
	    /** 
	    * 地图截屏回调接口 
	    * @param snapshot 截屏返回的 bitmap 数据 
	    */  
	    public void onSnapshotReady(Bitmap snapshot){  

	    	File imageFileDir=new File(CommonMethod.getMessageFileDownPath(4));
			if(!imageFileDir.exists()){
				imageFileDir.mkdirs();
			}
	    	
			imageFilePath=CommonMethod.getMessageFileDownPath(4)+FormatController.getNewFileNameByDate()+".jpg";

			File file = new File(imageFilePath);
			FileOutputStream out;
			try
			{
			    out = new FileOutputStream(file);
			    if (snapshot.compress(Bitmap.CompressFormat.PNG, 100, out))
			    {
					out.flush();
					out.close();
			    }
			    
			    Toast.makeText(MyLocationActivity.this,"屏幕截图成功，图片存在: " + file.toString(),Toast.LENGTH_SHORT).show();
			   
			    Intent intent=new Intent();
				intent.putExtra("localpath",imageFilePath);
				setResult(RESULT_OK, intent);

			    finish();

			} catch (FileNotFoundException e)
			{
			    e.printStackTrace();
			} catch (IOException e)
			{
			    e.printStackTrace();
			}
	    }
	};
	
	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			if(location.getLatitude() == 4.9E-324 || location.getLongitude() == 4.9E-324)
			{
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		@SuppressWarnings("unused")
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	@Override
	public void onGetOfflineMapState(int arg0, int arg1)
	{
		int type = arg0;
		int state = arg1;
		switch (type)
		{
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			// 离线地图下载更新事件类型
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			Toast.makeText(this, String.format("%s : %d%%", update.cityName,
					update.ratio), Toast.LENGTH_SHORT).show();
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			break;
		}
		
	}

}
