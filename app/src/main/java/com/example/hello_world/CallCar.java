package com.example.hello_world;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CallCar extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private Button callCarButton;
    public LocationClient mLocationClient;
    private boolean isFirstLocate = true;
    double Latitude,Longitude;
    short i = -1;//点一次呼叫小车，加1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.call_car);
        mapView = (MapView)findViewById(R.id.bmapView);
        callCarButton =(Button)findViewById(R.id.callCarButtonId);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置定位坐标系（百度经纬度坐标系 ：bd09ll）,很重要，能让定位更准
        option.setScanSpan(1000);//设施扫描间隔时间
        mLocationClient.setLocOption(option);//将参数添加进客户端
        mLocationClient.start();
        callCarButton.setOnClickListener(new ButtonListener4());
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        //MyLocationData.Builder类用来封装当前位置
        MyLocationData.Builder locationBulider = new MyLocationData.Builder();
        locationBulider.latitude(location.getLatitude());
        locationBulider.longitude(location.getLongitude());
        MyLocationData locationData = locationBulider.build();
        baiduMap.setMyLocationData(locationData);
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
    }

    class ButtonListener4 implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            try {
                i++;
                SendLocation sendLocation = new SendLocation();
                sendLocation.onCreate(Latitude,Longitude,i);
                //成功发送位置消息，则打开对话框显示等待小车到达
                if (sendLocation.Send){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CallCar.this);
                    dialog.setTitle("Dialog");
                    dialog.setMessage("已呼叫小车，等待小车到达");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {


        public void onReceiveLocation(BDLocation location) {

                navigateTo(location);

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }


    protected void onDestory(){
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
