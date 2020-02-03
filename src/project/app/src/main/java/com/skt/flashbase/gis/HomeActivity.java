package com.skt.flashbase.gis;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.opencsv.CSVReader;
import com.skt.flashbase.gis.Bubble.BubbleSeekBar;
import com.skt.flashbase.gis.Detail.AnalysisInfoDetail;
import com.skt.flashbase.gis.Detail.MarkerDetailInfoActivity;
import com.skt.flashbase.gis.roomDB.Place;
import com.skt.flashbase.gis.roomDB.PlaceViewModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.step;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    //Sol
    int storedValue = 3;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    //jieun
    private static final String SOURCE_ID_Foodtruck = "Foodtruck";
    private static final String ICON_ID_Foodtruck = "Foodtruck";
    private static final String ICON_ID_Foodtruck_min = "Foodtruck_min";
    private static final String ICON_ID_Foodtruck_max = "Foodtruck_max";
    private static final String LAYER_ID_Foodtruck = "Foodtruck";

    private static final String SOURCE_ID_Tour = "Tour";
    private static final String ICON_ID_Tour = "Tour";
    private static final String ICON_ID_Tour_min = "Tour_min";
    private static final String ICON_ID_Tour_max = "Tour_max";
    private static final String LAYER_ID_Tour = "Tour";


    private static final String SOURCE_ID_Fishing = "Fishing";
    private static final String ICON_ID_Fishing = "Fishing";
    private static final String ICON_ID_Fishing_min = "Fishing_min";
    private static final String ICON_ID_Fishing_max = "Fishing_max";
    private static final String LAYER_ID_Fishing = "Fishing";


    private MapView mapView;

    private PlaceViewModel mPlaceViewModel;
    private List<Place> pinPlaceTour = new ArrayList<>();
    private List<Place> pinPlaceFoodTruck = new ArrayList<>();
    private List<Place> pinPlaceFishing = new ArrayList<>();
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;
    private MapboxMap mapboxMap;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    //jieun

    //seungeun
    private LineChart lineChart;
    private LinearLayout llBottomSheet;


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
        //Create Model Provider ( RoomDB )
        mPlaceViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", true);
        if (isFirst) {
            CSVtoSqLite();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }

        setCustomDialogSearh();
        setPlaceData();
        setAboutFlashBase();

        create_bottomsheet();
        createSeekBar();

        TextView btn_chart_ex = (TextView) findViewById(R.id.txt_detail_info);

        btn_chart_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalysisInfoDetail.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        //--jieun--//
        this.mapboxMap = mapboxMap;


        //create marker  (foodTuck)
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
        //create marker  (Tour)
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

        //create marker 생성 (Fishing)
        List<Feature> fishingPlaceList = new ArrayList<>();
        for (int i = 0; i < pinPlaceFishing.size(); i++) {
            Double longitude = pinPlaceFishing.get(i).getPLongitude();
            Double latitude = pinPlaceFishing.get(i).getPLatitude();
            int index = pinPlaceFishing.get(i).getPidx();
            String name = pinPlaceFishing.get(i).getPName();
            fishingPlaceList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));
            fishingPlaceList.get(i).addStringProperty("idx", String.valueOf(index));
            fishingPlaceList.get(i).addStringProperty("name", name);
        }


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
                List<Feature> Fishingfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Fishing);
                if (!Fishingfeatures.isEmpty()) {
                    String idx = Fishingfeatures.get(0).getStringProperty("idx");
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

                initSearchFab();

                addUserLocations();

                // Add the symbol layer icon
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        HomeActivity.this.getResources(), R.drawable.blue_marker_view));

                setupLayer(style);
                enableLocationComponent(style);
                // foodtruck marker style
                style.addImageAsync(ICON_ID_Foodtruck, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_truck_pin_custom)));
                style.addImageAsync(ICON_ID_Foodtruck_min, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_truck_pin_custom_min)));
                style.addImageAsync(ICON_ID_Foodtruck_max, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_truck_pin_custom_max)));
                Source FoodTruck = new GeoJsonSource(SOURCE_ID_Foodtruck,
                        FeatureCollection.fromFeatures(FoodTruckPlaceList));
                style.addSource(FoodTruck);
                SymbolLayer FoodTruckLayer = new SymbolLayer(LAYER_ID_Foodtruck, SOURCE_ID_Foodtruck)
                        .withProperties(iconImage(ICON_ID_Foodtruck), PropertyFactory.visibility(Property.NONE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));

                FoodTruckLayer.setProperties(iconImage(step(zoom(), literal(ICON_ID_Foodtruck_min), stop(11, ICON_ID_Foodtruck),
                        stop(13, ICON_ID_Foodtruck_max))),
                        iconIgnorePlacement(true),
                        iconAllowOverlap(true));

                style.addLayer(FoodTruckLayer);


                // tour marker style
                style.addImageAsync(ICON_ID_Tour_min, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_tour_pin_custom_min)));
                style.addImageAsync(ICON_ID_Tour, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_tour_pin_custom)));

                style.addImageAsync(ICON_ID_Tour_max, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_tour_pin_custom_max)));
                Source Tour = new GeoJsonSource(SOURCE_ID_Tour,
                        FeatureCollection.fromFeatures(tourPlaceList));
                style.addSource(Tour);

                SymbolLayer TourLayer = new SymbolLayer(LAYER_ID_Tour, SOURCE_ID_Tour)
                        .withProperties(iconImage(ICON_ID_Tour), PropertyFactory.visibility(Property.NONE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));

                TourLayer.setProperties(iconImage(step(zoom(), literal(ICON_ID_Tour_min), stop(11, ICON_ID_Tour),
                        stop(13, ICON_ID_Tour_max))),
                        iconIgnorePlacement(true),
                        iconAllowOverlap(true));

                style.addLayer(TourLayer);

               // fishing marker style
                style.addImageAsync(ICON_ID_Fishing_min, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_fishing_pin_custom_min)));
                style.addImageAsync(ICON_ID_Fishing, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_fishing_pin_custom)));
                style.addImageAsync(ICON_ID_Fishing_max, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.ic_fishing_pin_custom_max)));
                Source Fishing = new GeoJsonSource(SOURCE_ID_Fishing,
                        FeatureCollection.fromFeatures(fishingPlaceList));
                style.addSource(Fishing);
                SymbolLayer FishingLayer = new SymbolLayer(LAYER_ID_Fishing, SOURCE_ID_Fishing)
                        .withProperties(iconImage(ICON_ID_Fishing_min), PropertyFactory.visibility(Property.NONE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));

                FishingLayer.setProperties(iconImage(step(zoom(), literal(ICON_ID_Fishing_min), stop(11, ICON_ID_Fishing),
                        stop(13, ICON_ID_Fishing_max))),
                        iconIgnorePlacement(true),
                        iconAllowOverlap(true));
                style.addLayer(FishingLayer);


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

                FloatingActionButton homeFishingFab = findViewById(R.id.home_fising_fab);
                homeFishingFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayerVisible(LAYER_ID_Fishing, style);
                    }
                });
                FloatingActionButton homeaddFab = findViewById(R.id.home_add_fab);
                homeaddFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                        startActivity(intent);

                    }
                });

            }
        });
    }

    //--jieun --//
    // marker visibility change
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

    void CSVtoSqLite() {
        try {
            // 카테고리 (1) 관광지
            CSVReader read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.korea_landmark_standard_data), "EUC-KR"));
            String[] record = null;
            while ((record = read.readNext()) != null) {
                if (!record[4].equals("위도")) {
                    //room db : 카테고리 1 =관광지
                    Place place = new Place(0, 1, record[0], Double.parseDouble(record[4]), Double.parseDouble(record[5]));
                    mPlaceViewModel.insert(place);
                }
            }
            //카테고리 (2) 푸드트럭
            read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodtruck_permission_area), "EUC-KR"));
            record = null;
            while ((record = read.readNext()) != null) {
                if (!record[6].equals("위도")) {
                    //room db 이용 : 카테고리 2 = 푸드트럭
                    if (!record[0].isEmpty() && !record[6].isEmpty() && !record[7].isEmpty()) {
                        Place place = new Place(0, 2, record[0], Double.parseDouble(record[6]), Double.parseDouble(record[7]));
                        mPlaceViewModel.insert(place);
                    }
                }
            }
            //카테고리 (3) 낚시터
            read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.fishing_area), "utf-8"));
            record = null;
            while ((record = read.readNext()) != null) {
                Log.i("CSV 파일 읽기", "이름: " + record[0] + ", 위도: " + record[4] + ", 경도: " + record[5]);
                if (!record[4].equals("위도")) {
                    //room db 이용 : 카테고리 3 = 낚시 터
                    if (!record[0].isEmpty() && !record[4].isEmpty() && !record[5].isEmpty()) {
                        Place place = new Place(0, 3, record[0], Double.parseDouble(record[4]), Double.parseDouble(record[5]));
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
            // custom pin
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .foregroundDrawable(R.drawable.ic_current_location_pin)
                    .build();
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
        //check permission
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
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
        TextView homeSearchTextView;
        homeSearchTextView = (TextView) findViewById(R.id.home_search_textView);
        homeSearchTextView.setOnClickListener(new View.OnClickListener() {
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

    // -- jieun --// Back key
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

    // -- jieun --//  get data from RoomDB
    public void setPlaceData() {
        //landmark
        mPlaceViewModel.getAllTourPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceTour.add(i, places.get(i));
                }
            }
        });
        //Food truck
        mPlaceViewModel.getAllFoodtruckPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceFoodTruck.add(i, places.get(i));
                }
            }
        });
        //Fishing
        mPlaceViewModel.getAllFishingPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceFishing.add(i, places.get(i));
                }
            }
        });

    }

    // --jieun--// connect flash base homepage
    public void setAboutFlashBase() {
        TextView homeAboutFlachBaseBtn = (TextView) findViewById(R.id.home_aboutFlashBase_textView);
        homeAboutFlachBaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutFlashBaseActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // -- seungeun --// bottomsheet
    void create_bottomsheet() {

        llBottomSheet = findViewById(R.id.bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });

        create_pie_chart();
    }

    //--seung eun--//
    public void create_pie_chart() {
        TextView person = (TextView) findViewById(R.id.person);

        PieChartView pieChartview;
        pieChartview = findViewById(R.id.pie_chart);

        int[] array1 = new int[6];
        String[] array2 = new String[6];


        List pieData = new ArrayList<>();

        for (int a = 0; a < array1.length; a++) {
            array1[a] = (int) (Math.random() * 1000) + 1;
        }
        int sum = array1[0] + array1[1] + array1[2] + array1[3] + array1[4] + array1[5];
        String p = Integer.toString(sum) + "명";
        person.setText(p);

        for (int a = 0; a < array1.length; a++) {
            array2[a] = Integer.toString(array1[a] * 100 / sum + 1);
            array2[a] += "%";
        }


        pieData.add(new SliceValue(array1[1], Color.parseColor("#a3c9c7")).setLabel(array2[1]));
        pieData.add(new SliceValue(array1[2], Color.parseColor("#cb7575")).setLabel(array2[2])); // 30
        pieData.add(new SliceValue(array1[3], Color.parseColor("#ef9e9f")).setLabel(array2[3])); // 10
        pieData.add(new SliceValue(array1[4], Color.parseColor("#8283a7")).setLabel(array2[4])); // 40
        pieData.add(new SliceValue(array1[5], Color.parseColor("#589167")).setLabel(array2[5])); // 35
        pieData.add(new SliceValue(array1[0], Color.parseColor("#ebce95")).setLabel(array2[0])); // 그외

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(12);

        //원 안에 텍스트 넣을 수 있는 코드
        pieChartData.setHasCenterCircle(true).setCenterText1("").setCenterText2("").setCenterText2Color(Color.parseColor("#0097A7"))
                .setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartview.setPieChartData(pieChartData);

    }

    public void createSeekBar() {
        // sol Bubble Seek Bar
        final BubbleSeekBar bubbleSeekBar3 = findViewById(R.id.demo_4_seek_bar_3);

        bubbleSeekBar3.getConfigBuilder()
                .min(0)
                .max(6)
                .sectionCount(6)
                .seekBySection()
                .autoAdjustSectionMark()
                .showThumbText()
                .build();

        bubbleSeekBar3.setProgress(storedValue);

        SimpleDateFormat sdf_hour = new SimpleDateFormat("hh:mm");
        SimpleDateFormat sdf_day = new SimpleDateFormat("MM/dd");
        SimpleDateFormat sdf_month = new SimpleDateFormat("yy/MM");
        Calendar cal = Calendar.getInstance();

        bubbleSeekBar3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                cal.add(Calendar.DAY_OF_MONTH, -3);
                array.put(0, sdf_day.format(cal.getTime()));
                for (int i = 1; i < 7; i++) {
                    cal.add(Calendar.DAY_OF_MONTH, +1);
                    array.put(i, sdf_day.format(cal.getTime()));
                }
                array.put(3, "NOW");

                return array;
            }
        });

        bubbleSeekBar3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                int color;
                if (progress <= 3) {
                    color = ContextCompat.getColor(HomeActivity.this, R.color.mapboxRed);
                } else {
                    color = ContextCompat.getColor(HomeActivity.this, R.color.mapboxTeal);
                }
                bubbleSeekBar.setSecondTrackColor(color);
                bubbleSeekBar.setThumbColor(color);
                bubbleSeekBar.setBubbleColor(color);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        // sol's spinner
        final Spinner spinner = findViewById(R.id.spinner_field);

        String[] str = getResources().getStringArray(R.array.question);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, str);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //  Toast.makeText(HomeActivity.this, (String) sAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                if (spinner.getSelectedItemPosition() == 0) {
                    bubbleSeekBar3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
                        @NonNull
                        @Override
                        public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                            array.clear();
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.HOUR, -3);
                            array.put(0, sdf_hour.format(cal.getTime()));
                            for (int i = 1; i < 7; i++) {
                                cal.add(Calendar.HOUR, +1);
                                array.put(i, sdf_hour.format(cal.getTime()));
                            }
                            array.put(3, "NOW");

                            return array;
                        }
                    });
                } else if (spinner.getSelectedItemPosition() == 1) {
                    bubbleSeekBar3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
                        @NonNull
                        @Override
                        public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                            array.clear();
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DAY_OF_MONTH, -3);
                            array.put(0, sdf_day.format(cal.getTime()));
                            for (int i = 1; i < 7; i++) {
                                cal.add(Calendar.DAY_OF_MONTH, +1);
                                array.put(i, sdf_day.format(cal.getTime()));
                            }
                            array.put(3, "NOW");

                            return array;
                        }
                    });
                } else if (spinner.getSelectedItemPosition() == 2) {
                    bubbleSeekBar3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
                        @NonNull
                        @Override
                        public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                            array.clear();
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, -3);
                            array.put(0, sdf_month.format(cal.getTime()));
                            for (int i = 1; i < 7; i++) {
                                cal.add(Calendar.MONTH, +1);
                                array.put(i, sdf_month.format(cal.getTime()));
                            }
                            array.put(3, "NOW");

                            return array;
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(ForSearch);
        findViewById(R.id.fab_location_search_btn).setOnClickListener(ForSearch);
    }

    private View.OnClickListener ForSearch = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .addInjectedFeature(home)
                            .addInjectedFeature(work)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(HomeActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        }
    };

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
    }
}