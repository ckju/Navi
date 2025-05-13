package com.amap.track.activity.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.track.demo.R;
import com.amap.track.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends Activity implements AMap.OnMarkerClickListener {

    private AMap aMap;
    private MapView mapView;
    private Marker mainMarker;
    private MarkerOptions markerOption;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption continuousOption;
    private AMapLocationClientOption onceOption;

    private boolean firstLocated = false;
    private ProgressDialog progressDialog;
    private AMapLocation latestLocation;
    private LatLng latestLatLng;
    private final List<Marker> savedMarkers = new ArrayList<>();

    private Button btnShowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // “显示位置信息” 按钮
        btnShowInfo = findViewById(R.id.btn_show_location_info);
        btnShowInfo.setOnClickListener(v -> {
            if (latestLocation != null && latestLatLng != null) {
                showDetailDialog(latestLocation, latestLatLng);
            } else {
                new AlertDialog.Builder(LocationActivity.this)
                        .setMessage("位置信息尚未获取，请稍后。")
                        .setPositiveButton("我知道了", null)
                        .show();
            }
        });

        // 地图初始化
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();

        // 定位初始化
        try {
            locationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        continuousOption = new AMapLocationClientOption()
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setInterval(2000)
                .setNeedAddress(true)
                .setMockEnable(true)
                .setLocationCacheEnable(false);
        onceOption = new AMapLocationClientOption()
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setOnceLocation(true)
                .setOnceLocationLatest(true)
                .setNeedAddress(true)
                .setMockEnable(true)
                .setLocationCacheEnable(false);

        locationClient.setLocationOption(continuousOption);
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            markerOption = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .draggable(true);
            aMap.setOnMarkerClickListener(this);
        }
    }

    /** 定位回调 */
    private final AMapLocationListener locationListener = loc -> {
        if (loc == null || loc.getErrorCode() != 0) {
            // 定位失败时关闭进度框并提示
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                new AlertDialog.Builder(this)
                        .setMessage("定位失败，请重试。")
                        .setPositiveButton("我知道了", null)
                        .show();
                locationClient.setLocationOption(continuousOption);
            }
            return;
        }

        latestLocation = loc;
        latestLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());

        // 首次定位：添加蓝色主标记并移动视角
        if (!firstLocated) {
            firstLocated = true;
            mainMarker = aMap.addMarker(markerOption.position(latestLatLng));
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestLatLng, 16f));
            return;
        }

        // 单次定位回调：刷新中
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

            // 检查逆地理信息完整性
            boolean hasAddr = loc.getCountry() != null
                    && loc.getProvince() != null
                    && loc.getCity() != null
                    && loc.getDistrict() != null
                    && loc.getAddress() != null;

            // 跳动主标记到新位置
            bounceTo(latestLatLng);
            aMap.animateCamera(CameraUpdateFactory.newLatLng(latestLatLng));

            if (!hasAddr) {
                new AlertDialog.Builder(this)
                        .setMessage("地址信息获取失败。")
                        .setPositiveButton("我知道了", null)
                        .show();
            }
            // 详情对话框保留“显示位置信息”按钮触发

            // 恢复持续定位
            locationClient.setLocationOption(continuousOption);
        } else {
            // 普通持续更新：移动主标记
            if (mainMarker != null) {
                mainMarker.setPosition(latestLatLng);
            }
        }
    };

    /** 点击任意 Marker */
    @Override
    public boolean onMarkerClick(Marker marker) {
        // 点击主标记：刷新定位
        if (marker.equals(mainMarker)) {
            progressDialog = ProgressDialog.show(
                    this,
                    "刷新定位",
                    "正在获取最新位置和地址…",
                    true,
                    false
            );
            locationClient.setLocationOption(onceOption);
            locationClient.startLocation();
            return true;
        }
        // 点击红色已保存标记：询问是否删除
        if (savedMarkers.contains(marker)) {
            new AlertDialog.Builder(this)
                    .setMessage("删除该标记？")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface d, int w) {
                            marker.remove();
                            savedMarkers.remove(marker);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        }
        return false;
    }

    /** 弹跳动画 */
    private void bounceTo(LatLng target) {
        Projection proj = aMap.getProjection();
        Point pt = proj.toScreenLocation(target);
        pt.offset(0, -200);
        LatLng start = proj.fromScreenLocation(pt);
        mainMarker.setPosition(start);

        final long startTime = SystemClock.uptimeMillis();
        final Handler handler = new Handler();
        final Interpolator interp = new BounceInterpolator();
        final long duration = 600;

        handler.post(new Runnable() {
            @Override public void run() {
                float t = interp.getInterpolation(
                        (SystemClock.uptimeMillis() - startTime) / (float) duration);
                double lat = t * target.latitude  + (1 - t) * start.latitude;
                double lng = t * target.longitude + (1 - t) * start.longitude;
                mainMarker.setPosition(new LatLng(lat, lng));
                if (t < 1f) handler.postDelayed(this, 16);
            }
        });
    }

    /** 弹出详情对话框，含“标记当前位置”按钮 */
    private void showDetailDialog(AMapLocation loc, LatLng ll) {
        StringBuilder sb = new StringBuilder();
        sb.append("定位成功\n")
                .append("类型: ").append(loc.getLocationType()).append("\n")
                .append("经度: ").append(loc.getLongitude()).append("\n")
                .append("纬度: ").append(loc.getLatitude()).append("\n")
                .append("精度: ").append(loc.getAccuracy()).append(" m\n")
                .append("提供者: ").append(loc.getProvider()).append("\n")
                .append("国家: ").append(loc.getCountry()).append("\n")
                .append("省份: ").append(loc.getProvince()).append("\n")
                .append("城市: ").append(loc.getCity()).append("\n")
                .append("区县: ").append(loc.getDistrict()).append("\n")
                .append("地址: ").append(loc.getAddress()).append("\n")
                .append("时间: ").append(Utils.formatUTC(loc.getTime(), "yyyy-MM-dd HH:mm:ss"));

        new AlertDialog.Builder(this)
                .setTitle("更新位置详情")
                .setMessage(sb.toString())
                .setPositiveButton("标记当前位置", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        Marker red = aMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .draggable(false));
                        savedMarkers.add(red);
                    }
                })
                .setNegativeButton("关闭", null)
                .show();
    }

    // MapView 生命周期转发
    @Override protected void onResume()    { super.onResume(); mapView.onResume(); }
    @Override protected void onPause()     { super.onPause();  mapView.onPause(); locationClient.stopLocation(); }
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState);
    }
    @Override protected void onDestroy()   { super.onDestroy(); mapView.onDestroy(); locationClient.onDestroy(); }
}
