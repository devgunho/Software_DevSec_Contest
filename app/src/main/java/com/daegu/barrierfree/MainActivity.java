package com.daegu.barrierfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Symbol;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnSymbolClickListener, NaverMap.OnLocationChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private MapFragment mapFragment = null;
    private FragmentManager fm = null;
    private BottomNavigationView main_bottom = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap = null;
    private ArrayList<BarrierDO> list = new ArrayList<>(); // basic
    private ArrayList<BarrierDO> govList = new ArrayList<>(); // gov
    private ArrayList<BarrierDO> moneyList = new ArrayList<>(); // money
    private ArrayList<Marker> listMarker = new ArrayList<>(); // basic marker
    private ArrayList<Marker> govMarker = new ArrayList<>(); // gov marker
    private ArrayList<Marker> moneyMarker = new ArrayList<>(); // money marker
    private static Gson gson = new Gson();
    private Location location = null;
    private LocationManager manager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_bottom = (BottomNavigationView)findViewById(R.id.main_bottom);

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        main_bottom.setOnNavigationItemSelectedListener(this);

        TedPermission.with(this)
                  .setPermissionListener(permissionlistener)
                .setRationaleMessage("위치 정보를 활용하기 위해서는 권한이 필요합니다")
                .setDeniedMessage("거부하셨습니다.\n추후에 [설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        new Thread(new Runnable() {
            @Override
            public void run() {
                readBarrierFreeData();

                setBasicMarker();
            }
        }).start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            fm = getSupportFragmentManager();
            mapFragment = (MapFragment) fm.findFragmentById(R.id.main_frame);
            if (mapFragment == null) {
                mapFragment = MapFragment.newInstance();
                fm.beginTransaction().add(R.id.main_frame, mapFragment).commit();
            }

            mapFragment.getMapAsync(MainActivity.this);

            if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //GPS 설정화면으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        this.naverMap.addOnLocationChangeListener(this);

        setDefaultSettings();
    }

    @Override
    public boolean onSymbolClick(@NonNull Symbol symbol) {
        return false;
    }

    private void setDefaultSettings() {
        UiSettings settings = naverMap.getUiSettings();
        settings.setZoomControlEnabled(false);
        settings.setLocationButtonEnabled(true);

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
        naverMap.setMapType(NaverMap.MapType.Basic);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);
        naverMap.setIndoorEnabled(true);
        //naverMap.setMinZoom(17.5);
    }

    private List<BFDataSample> bfSamples = new ArrayList<>();

    private void readBarrierFreeData() {
        InputStream is = getResources().openRawResource(R.raw.seoul_barrier_free);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";

        try {
            //caption 버리기위함
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                //Log.d("MyActivity","Line : "+line);
                // Split
                String[] tokens = line.split(",");

                // Read Data
                BFDataSample sample = new BFDataSample();
                //sample.setNum(tokens[0]);
                sample.setBusinessName(tokens[1]);
                sample.setTel(tokens[2]);
                sample.setFax(tokens[3]);
                sample.setAddress(tokens[4]);
                sample.setOpTime(tokens[5]);
                sample.setClosedDay(tokens[6]);
                sample.setBasicInfo(tokens[7]);
                sample.setCategory(tokens[26]);
                sample.setLatitude(tokens[28]);
                sample.setLongitude(tokens[27]);
                bfSamples.add(sample);

                Log.d("MyActivity", "Just Created : " + sample);

                doBarrierParsing(sample.toString().replaceAll("BFDataSample", ""));

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++) {
                    BarrierDO barrier = list.get(i);
                    if(barrier.getCategory().equals("공공")) {
                        govList.add(barrier);
                    } else if(barrier.getCategory().equals("금융")) {
                        moneyList.add(barrier);
                    }
                }
            }
        }).start();

    }

    private void doBarrierParsing(final String json) {
        BarrierDO barrier = gson.fromJson(json, BarrierDO.class);

        list.add(barrier);
    }

    // 처음부터 실행되야하니까 마커 생성후 바로 붙임
    private void setMarker(BarrierDO data, char flag) {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude())));
        marker.setAnchor(new PointF(1, 1));
        marker.setIconPerspectiveEnabled(true); //자동 원근법
        marker.setCaptionText(data.getBusinessName()); //가게명
        marker.setSubCaptionText(data.getCategory()); //업종
        marker.setCaptionColor(Color.CYAN);
        marker.setHideCollidedSymbols(true);    //심볼 충돌제거
        marker.setMap(naverMap);

        switch(flag) {
            case 'B':
                listMarker.add(marker);
                break;
            case 'G':
                govMarker.add(marker);
                break;
            case 'M':
                moneyMarker.add(marker);
                break;
            default:
                break;
        }
    }

    private void setBasicMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++) {
                    setMarker(list.get(i), 'B');
                }
            }
        });
    }

    private void setGovMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<govList.size(); i++) {
                    setMarker(govList.get(i), 'G');
                }
            }
        });
    }

    private void setMoneyMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<moneyList.size(); i++) {
                    setMarker(moneyList.get(i), 'M');
                }
            }
        });
    }


    @Override
    public void onLocationChange(@NonNull Location location) {
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(location.getLatitude(), location.getLongitude()))
                .animate(CameraAnimation.Fly, 1000);
        naverMap.moveCamera(cameraUpdate);
        this.location = location;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.bottom_basic:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setBasicMarker();
                break;
            case R.id.bottom_gov:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setGovMarker();
                break;
            case R.id.bottom_money:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setMoneyMarker();
                break;
            case R.id.bottom_search:
                SearchDialog dialog = new SearchDialog(MainActivity.this, callBack);
                dialog.showDialog(list, location);
                break;
            case R.id.bottom_favorite:
                //커스텀리스트뷰 구현
                break;
        }
        return true;
    }

    private void releaseBasicMarker() {
        for(int i=0; i<listMarker.size(); i++) {
            listMarker.get(i).setMap(null);
        }
    }

    private void releaseGovMarker() {
        for(int i=0; i<govMarker.size(); i++) {
            govMarker.get(i).setMap(null);
        }
    }

    private void releaseMoneyMarker() {
        for(int i=0; i<moneyMarker.size(); i++) {
            moneyMarker.get(i).setMap(null);
        }
    }

    private ICallBack callBack = new ICallBack() {
        @Override
        public void goSelect(double lat, double lon) {
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(lat, lon))
                    .animate(CameraAnimation.Fly, 1000);
            naverMap.moveCamera(cameraUpdate);
            naverMap.setLocationTrackingMode(LocationTrackingMode.None);
        }
    };
}
