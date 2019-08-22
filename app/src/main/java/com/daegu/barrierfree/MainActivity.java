package com.daegu.barrierfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Symbol;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnSymbolClickListener, NaverMap.OnLocationChangeListener, TabLayout.OnTabSelectedListener {

    private MapFragment mapFragment = null;
    private FragmentManager fm = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private TabLayout main_bottom = null;
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

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);

        main_bottom = (TabLayout)findViewById(R.id.main_bottom);

        main_bottom.addTab(main_bottom.newTab().setText("전체"));
        main_bottom.addTab(main_bottom.newTab().setText("공공"));
        main_bottom.addTab(main_bottom.newTab().setText("금융"));
        main_bottom.addTab(main_bottom.newTab().setText("검색"));
        main_bottom.addTab(main_bottom.newTab().setText("즐겨찾기"));

        main_bottom.getTabAt(0).setIcon(R.drawable.basic);
        main_bottom.getTabAt(1).setIcon(R.drawable.gov);
        main_bottom.getTabAt(2).setIcon(R.drawable.money);
        main_bottom.getTabAt(3).setIcon(R.drawable.lenz);
        main_bottom.getTabAt(4).setIcon(R.drawable.fill_star);


        main_bottom.addOnTabSelectedListener(this);

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
                sample.setDetail(tokens[31]);
                sample.setAccess(tokens[32]);
                sample.setEle(tokens[33]);
                sample.setToilet(tokens[34]);
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

        final InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAnchor(new PointF(0, 1));
        infoWindow.setOffsetX(getResources().getDimensionPixelSize(R.dimen.custom_info_window_offset_x));
        infoWindow.setOffsetY(getResources().getDimensionPixelSize(R.dimen.custom_info_window_offset_y));
        infoWindow.setAdapter(new InfoWindowAdapter(getApplicationContext()));
        infoWindow.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {

                return true;
            }
        });


        final Marker marker = new Marker();
        marker.setPosition(new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude())));
        marker.setAnchor(new PointF(1, 1));
        marker.setIconPerspectiveEnabled(true); //자동 원근법
        marker.setCaptionText(data.getBusinessName()); //가게명
        marker.setSubCaptionText(data.getCategory()); //업종
        marker.setCaptionColor(Color.rgb(0,0,0));
        marker.setHideCollidedSymbols(true);    //심볼 충돌제거
        marker.setTag(data);
        marker.setMap(naverMap);

        if(data.getAccess().equals("1")) {
            //초록색 갈수있음
            marker.setIcon(OverlayImage.fromResource(R.drawable.greenzone));
        } else if(data.getAccess().equals("2")) {
            //빨간색 갈수없음
            marker.setIcon(OverlayImage.fromResource(R.drawable.redzone));
        }

        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                infoWindow.open(marker);
                return true;
            }
        });

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

            final InfoWindow infoWIndow = new InfoWindow();

            for(int i=0; i<list.size(); i++) {
                BarrierDO data = list.get(i);

            }
        }
    };

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();

        switch(pos) {
            case 0:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setBasicMarker();
                break;
            case 1:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setGovMarker();
                break;
            case 2:
                releaseBasicMarker();
                releaseGovMarker();
                releaseMoneyMarker();
                setMoneyMarker();
                break;
            case 3:
                SearchDialog dialog = new SearchDialog(MainActivity.this, callBack);
                dialog.showDialog(list, location);
                break;
            case 4:
                //커스텀리스트뷰 구현
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class InfoWindowAdapter extends InfoWindow.ViewAdapter {
        @NonNull
        private final Context context;
        private View rootView;
        private TextView tvMarkerName;
        private TextView tvMarkerAddr;
        private TextView tvMarkerTime;
        private ImageView ivMarkerFavorite;
        private ImageView ivMarkerIcon;
        private ImageView ivMarkerEle;
        private Button btnMarkerGo;

        private InfoWindowAdapter(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(@NonNull InfoWindow infoWindow) {
            if (rootView == null) {
                rootView = View.inflate(context, R.layout.view_custom_info_window, null);
                tvMarkerName = rootView.findViewById(R.id.tvMarkerName);
                tvMarkerAddr = rootView.findViewById(R.id.tvMarkerAddr);
                tvMarkerTime = rootView.findViewById(R.id.tvMarkerTime);
                ivMarkerFavorite = rootView.findViewById(R.id.ivMarkerFavorite);
                ivMarkerIcon = rootView.findViewById(R.id.ivMarkerIcon);
                ivMarkerEle = rootView.findViewById(R.id.ivMarkerEle);
                btnMarkerGo = rootView.findViewById(R.id.btnMarkerGo);
            }

            if (infoWindow.getMarker() != null) {
                BarrierDO data = (BarrierDO) infoWindow.getMarker().getTag();
                tvMarkerName.setText(data.getBusinessName());
                tvMarkerAddr.setText("주소: " + data.getAddress());
                tvMarkerTime.setText("영업: " + data.getOpTime());
                ivMarkerFavorite.setImageResource(R.drawable.empty_star);
                ivMarkerFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ivMarkerFavorite.setImageResource(R.drawable.fill_star);
                        Toast.makeText(context, "즐겨찾기 추가완료", Toast.LENGTH_SHORT).show();
                    }
                });

                btnMarkerGo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 도착지 dialog창 나오기
                        Log.i("qtqt", "wqopejq");
                        LatLng g = new LatLng(location.getLatitude(), location.getLongitude());
                        Utmk loc = Utmk.valueOf(g);

                        String key = getText(R.string.near_station).toString();

                        HttpConnection connection = HttpConnection.getInstance();

                        connection.requestStation(key, loc.x, loc.y, stationCallback);
                    }
                });

                //btnMarkerGo.performClick();
            } else {
//                icon.setImageResource(R.drawable.ic_my_location_black_24dp);
//                text.setText(context.getString(
//                        R.string.format_coord, infoWindow.getPosition().latitude, infoWindow.getPosition().longitude));
            }

            return rootView;
        }
    }

    Callback stationCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String body = response.body().string().toString();

            Log.i("tqfjadk", ""+body);
        }
    };
}
