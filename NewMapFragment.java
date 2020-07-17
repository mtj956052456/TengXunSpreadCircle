package com.tcmygy.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tcmygy.R;
import com.tcmygy.interf.PermissionCallBack;
import com.tcmygy.util.PermissionUtil;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.Circle;
import com.tencent.tencentmap.mapsdk.maps.model.CircleOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 孟腾蛟
 * @Description
 * @date 2020/7/17
 */
public class NewMapFragment extends Fragment {

    private MapView mMapView;
    private Button btnLocation;
    private TencentMap tencentMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void initView(View view) {
        mMapView = view.findViewById(R.id.mapview);
        btnLocation = view.findViewById(R.id.btnLocation);
        tencentMap = mMapView.getMap();
        locationManager = TencentLocationManager.getInstance(getContext());
        //移除倾斜手势监听
        tencentMap.getUiSettings().setTiltGesturesEnabled(false);
        //移除缩放手势监听
        tencentMap.getUiSettings().setRotateGesturesEnabled(false);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    reset();
                    btnLocation.setText("启动");
                } else {
                    startLocation();//开启定位
                    btnLocation.setText("暂停");
                }
            }
        });
    }

    private LocationSource.OnLocationChangedListener locationChangedListener;
    private TencentLocationManager locationManager;

    private void startLocation() {
        PermissionUtil.checkPermission(getContext(), new PermissionCallBack() {
            @Override
            public void success() {
                locationManager.requestSingleFreshLocation(null, mTencentLocationListener, Looper.getMainLooper());
            }
        }, Permission.Group.LOCATION);
    }

    private Marker mCustomMarker;
    private LatLng mLatLng;
    private TencentLocationListener mTencentLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
            if (i == TencentLocation.ERROR_OK) {
                mLatLng = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
                tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 18));
                BitmapDescriptor custom = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);
                mCustomMarker = tencentMap.addMarker(new MarkerOptions(mLatLng)
                        .icon(custom)
                        .flat(true)
                        .level(2));
                startTimer();
            }
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {

        }
    };
    private Timer timer;
    private TimerTask timerTask;
    private int count = 0;
    private double radius = 8;
    private int circleCount = 5;

    private ArrayList<Circle> circles = new ArrayList<>();

    //初始化
    private void reset() {
        cancelTimer();
        clearCircle();
        count = 0;
        if (mCustomMarker != null)
            mCustomMarker.remove();
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    count++;
                    int color = Color.argb(255 / count, 255, 255, 255);
                    Circle circle = tencentMap.addCircle(new CircleOptions().
                            center(mLatLng).
                            radius(radius * count).
                            fillColor(color).
                            strokeWidth(0).
                            strokeColor(0x00000000).
                            clickable(false).
                            level(1));
                    circles.add(circle);
                    if (circleCount == count) {
                        clearCircle();
                        count = 0;
                    }
                }
            };
        }
        timer.schedule(timerTask, 0, 211);
    }

    //清除圆
    private void clearCircle() {
        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).remove();
        }
        circles.clear();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }

    @Override
    public void onStart() {
        mMapView.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mMapView.onStop();
        super.onStop();
    }

}
