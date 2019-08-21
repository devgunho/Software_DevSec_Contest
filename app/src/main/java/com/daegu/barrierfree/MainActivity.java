package com.daegu.barrierfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Symbol;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnSymbolClickListener {

    private MapFragment mapFragment;
    private FragmentManager fm;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap = null;
    private ArrayList<BarrierDO> list = new ArrayList<>();
    private static Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

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
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

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
    }

    private List<BFDataSample> bfSamples = new ArrayList<>();

    private void readBarrierFreeData() {
        InputStream is = getResources().openRawResource(R.raw.seoul_barrier_free);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                //Log.d("MyActivity","Line : "+line);
                // Split
                String[] tokens = line.split("\",\"");

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
                sample.setLatitude(tokens[28]);
                sample.setLongitude(tokens[27]);
                bfSamples.add(sample);

                Log.d("MyActivity", "Just Created : " + sample);

//                doBarrierParsing(sample.toString().replaceAll("BFDataSample", "").replaceAll("=", ":"));

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error Reading Data File on Line" + line, e);
            e.printStackTrace();
        }

    }

    private void doBarrierParsing(final String json) {
        BarrierDO barrier = gson.fromJson(json, BarrierDO.class);

        Log.i("tqtq", "" + barrier);

        list.add(barrier);
    }
}
