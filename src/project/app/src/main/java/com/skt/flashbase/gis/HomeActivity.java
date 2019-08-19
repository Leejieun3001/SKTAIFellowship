package com.skt.flashbase.gis;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.opencsv.CSVReader;
import com.skt.flashbase.gis.Detail.MarkerDetailInfoActivity;
import com.skt.flashbase.gis.roomDB.Place;
import com.skt.flashbase.gis.roomDB.PlaceViewModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    //Sol
    int storedValue = 50;
    //jieun
    private static final String SOURCE_ID_Foodtruck = "Foodtruck";
    private static final String ICON_ID_Foodtruck = "Foodtruck";
    private static final String LAYER_ID_Foodtruck = "Foodtruck";
    private static final String SOURCE_ID_Tour = "Tour";
    private static final String ICON_ID_Tour = "Tour";
    private static final String LAYER_ID_Tour = "Tour";
    private MapView mapView;
    SQLiteDatabase db;
    private PlaceViewModel mPlaceViewModel;
    private List<Place> pinPlaceTour = new ArrayList<>();
    private List<Place> pinPlaceFoodTruck = new ArrayList<>();
    private List<Place> pinPlaceAll = new ArrayList<>();
    // current location
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;
    private MapboxMap mapboxMap;
    //Back 키 두번 클릭 여부 확인
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    //jieun

    //seungeun
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //mapView init setting
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        //--jieun--//
        //Model Provider 생성 RoomDB
        mPlaceViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", true);
        //SharedPreferences 처음 실행하는 경우에만 sqLite에 저장
        if (isFirst) {
            CSVtoSqLite();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
        setCustomDialogSearh();
        setPlaceData();


    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        //--jieun--//
        this.mapboxMap = mapboxMap;

        //marker 생성 (foodTuck)
        List<Feature> FoodTruckPlaceList = new ArrayList<>();
        for (int i = 0; i < pinPlaceFoodTruck.size(); i++) {
            Double longitude = pinPlaceFoodTruck.get(i).getPLongitude();
            Double latitude = pinPlaceFoodTruck.get(i).getPLatitude();
            int index = pinPlaceFoodTruck.get(i).getPidx();
            String name = pinPlaceFoodTruck.get(i).getPName();
            FoodTruckPlaceList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));
            FoodTruckPlaceList.get(i).addStringProperty("idx", String.valueOf(index));
            FoodTruckPlaceList.get(i).addStringProperty("name", name);



        }
        //marker 생성 (Tour)
        List<Feature> tourPlaceList = new ArrayList<>();
        for (int i = 0; i < pinPlaceTour.size(); i++) {
            Double longitude = pinPlaceTour.get(i).getPLongitude();
            Double latitude = pinPlaceTour.get(i).getPLatitude();
            int index = pinPlaceTour.get(i).getPidx();
            String name = pinPlaceTour.get(i).getPName();
            tourPlaceList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));
            tourPlaceList.get(i).addStringProperty("idx", String.valueOf(index));
            tourPlaceList.get(i).addStringProperty("name", name);
        }

        //jieun - mapbox on fling & on move events
        mapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
            @Override
            public void onMoveBegin(MoveGestureDetector detector) {
                // user started moving the map
            }

            @Override
            public void onMove(MoveGestureDetector detector) {
                // user is moving the map
            }

            @Override
            public void onMoveEnd(MoveGestureDetector detector) {
                // user stopped moving the map
                int viewportWidth = mapView.getWidth();
                int viewportHeight = mapView.getHeight();
                Toast.makeText(HomeActivity.this, "onMoveEnd", Toast.LENGTH_SHORT).show();
            }
        });
        mapboxMap.addOnFlingListener(new MapboxMap.OnFlingListener() {
            @Override
            public void onFling() {
                Toast.makeText(HomeActivity.this, "onFling", Toast.LENGTH_SHORT).show();
            }
        });

        // 그냥 클릭시 다음 화면으로 넘어감
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
                List<Feature> Tourfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Tour);
                if (!Tourfeatures.isEmpty()) {
                    String idx = Tourfeatures.get(0).getStringProperty("idx");
                    Intent intent = new Intent(getApplicationContext(), MarkerDetailInfoActivity.class);
                    intent.putExtra("idx", idx);
                    startActivity(intent);
                    return true;
                }
                List<Feature> FoodTruckfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Foodtruck);
                if (!FoodTruckfeatures.isEmpty()) {
                    String idx = FoodTruckfeatures.get(0).getStringProperty("idx");
                    Intent intent = new Intent(getApplicationContext(), MarkerDetailInfoActivity.class);
                    intent.putExtra("idx", idx);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                // foodtruck marker style 지정
                style.addImageAsync(ICON_ID_Foodtruck, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_truck_pin_custom)));
                Source FoodTruck = new GeoJsonSource(SOURCE_ID_Foodtruck,
                        FeatureCollection.fromFeatures(FoodTruckPlaceList));
                style.addSource(FoodTruck);
                SymbolLayer FoodTruckLayer = new SymbolLayer(LAYER_ID_Foodtruck, SOURCE_ID_Foodtruck)
                        .withProperties(PropertyFactory.iconImage(ICON_ID_Foodtruck), PropertyFactory.visibility(Property.NONE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));
                style.addLayer(FoodTruckLayer);
                // tour marker style 지정
                style.addImageAsync(ICON_ID_Tour, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_tour_pin_custom)));
                Source Tour = new GeoJsonSource(SOURCE_ID_Tour,
                        FeatureCollection.fromFeatures(tourPlaceList));
                style.addSource(Tour);
                SymbolLayer TourLayer = new SymbolLayer(LAYER_ID_Tour, SOURCE_ID_Tour)
                        .withProperties(PropertyFactory.iconImage(ICON_ID_Tour), PropertyFactory.visibility(Property.NONE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));
                style.addLayer(TourLayer);

                //floating btn event
                FloatingActionButton homeTourFab = findViewById(R.id.home_landmark_fab);
                homeTourFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayerVisible(LAYER_ID_Tour, style);
                    }
                });
                FloatingActionButton homeFoodtruckFab = findViewById(R.id.home_food_fab);
                homeFoodtruckFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayerVisible(LAYER_ID_Foodtruck, style);
                    }
                });
            }
        });
    }

    //--jieun --//
    // marker visibility change 함수
    private void setLayerVisible(String layerId, @NonNull Style loadedMapStyle) {
        Layer layer = loadedMapStyle.getLayer(layerId);
        if (layer == null) {
            return;
        }
        if (VISIBLE.equals(layer.getVisibility().getValue())) {
            layer.setProperties(
                    PropertyFactory.visibility(Property.NONE)
            );
        } else {
            layer.setProperties(
                    PropertyFactory.visibility(VISIBLE)
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //--jieun--//
    //csv 데이터 저장
    void CSVtoSqLite() {
        try {
            // 카테고리 1, 관광지
            CSVReader read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.korea_landmark_standard_data), "EUC-KR"));
            String[] record = null;
            //CSV 파일을 읽으면서 동시에 SqLite에 저장
            while ((record = read.readNext()) != null) {
                // Log.i("CSV 파일 읽기", "이름: " + record[0] + ", 위도: " + record[4] + ", 경도: " + record[5]);
                if (!record[4].equals("위도")) {
                    //saHelper 이용
                    //room db 이용, 카테고리 1 =관광지
                    Place place = new Place(0, 1, record[0], Double.parseDouble(record[4]), Double.parseDouble(record[5]));
                    mPlaceViewModel.insert(place);
                }
            }

            //카테고리 2, 푸드트럭
            read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodtruck_permission_area), "EUC-KR"));
            record = null;
            //CSV 파일을 읽으면서 동시에 SqLite에 저장
            while ((record = read.readNext()) != null) {
                // Log.i("CSV 파일 읽기", "이름: " + record[0] + ", 위도: " + record[6] + ", 경도: " + record[7]);
                if (!record[6].equals("위도")) {
                    //     dbHelper.insertLandmark(record[0], Double.parseDouble(record[6]), Double.parseDouble(record[7]));
                    //room db 이용 / 카테고리 2 = 푸드트럭
                    if (!record[0].isEmpty() && !record[6].isEmpty() && !record[7].isEmpty()) {
                        Place place = new Place(0, 2, record[0], Double.parseDouble(record[6]), Double.parseDouble(record[7]));
                        mPlaceViewModel.insert(place);
                    }
                }
            }
        } catch (IOException ex) {
            // handle exception
        } finally {
        }
    }

    //jieun (current user location)
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        //check permission
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // 사용자 정의 핀 생성
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .foregroundDrawable(R.drawable.map_default_map_marker)
                    .build();
            // 컴포넌트의 인스턴트 가져오기
            locationComponent = mapboxMap.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions
                    = LocationComponentActivationOptions.builder(this, loadedMapStyle).locationComponentOptions(customLocationComponentOptions).build();
            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
// Add the location icon click listener
            locationComponent.addOnLocationClickListener(this);
// Add the camera tracking listener. Fires if the map camera is manually moved.
            locationComponent.addOnCameraTrackingChangedListener(this);
            findViewById(R.id.home_user_fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(16f);
                        Toast.makeText(HomeActivity.this, getString(R.string.tracking_enabled),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.tracking_already_enabled),
                                Toast.LENGTH_SHORT).show();
                    }
                }

            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        //권한 확인
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            //컴포넌트의 인스턴스 설정 (옵션)
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            //모드 설정
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;

    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {

    }

    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    // -- jieun --// custom Dialog
    public void setCustomDialogSearh() {
        Button homeSearchBtn;
        homeSearchBtn = (Button) findViewById(R.id.home_search_btn);
        homeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogSearch customDialogSearch = new CustomDialogSearch(HomeActivity.this);
                customDialogSearch.setDialogListener(new CustomDialogSearch.CustomDialogSearchListener() {
                    @Override
                    public void onPositiveClicked(String result) {
                        if (result.equals("")) {
                            Toast.makeText(getApplicationContext(), "선택사항 없음", Toast.LENGTH_SHORT).show();
                            customDialogSearch.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "선택 : " + result, Toast.LENGTH_SHORT).show();
                            customDialogSearch.dismiss();
                        }
                    }

                    @Override
                    public void onNegativeClicked() {
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                        customDialogSearch.dismiss();
                    }
                });
                customDialogSearch.show();
            }
        });
    }

    // -- jieun --// - BackKey 두번 출려
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            this.backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 가기 키를 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // -- jieun --//  데이터 추가 함수
    public void setPlaceData() {
        //roomdb 방식, 관광지 데이터 조회
        mPlaceViewModel.getAllTourPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceTour.add(i, places.get(i));
                }
            }
        });
        //roomdb 방식, 푸드트럭 데이터 조회
        mPlaceViewModel.getAllFoodtruckPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceFoodTruck.add(i, places.get(i));
                }
            }
        });

    }
}
