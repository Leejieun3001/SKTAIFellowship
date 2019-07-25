package com.skt.flashbase.gis.test.common;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.ImageSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.opencsv.CSVReader;
import com.skt.flashbase.gis.test.R;
import com.skt.flashbase.gis.test.roomDB.Place;
import com.skt.flashbase.gis.test.roomDB.PlaceViewModel;
import com.skt.flashbase.gis.test.sqLite.DBHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //Model Provider 생성
        mPlaceViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        //SQLite 방식
//        db = openOrCreateDatabase("location.db", Context.MODE_PRIVATE, null);
//        String sql = "select * from landmark ; ";
//        Cursor results = db.rawQuery(sql, null);
//        results.moveToFirst();
//        while (!results.isAfterLast()) {
//            Double longitude = results.getDouble(3);
//            Double latitude = results.getDouble(2);
//            symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                    Point.fromLngLat(longitude, latitude)));
//            results.moveToNext();
//        }
//        results.close();


        List<Feature> FoodTruckPlaceList = new ArrayList<>();

        for (int i = 0; i < pinPlaceFoodTruck.size(); i++) {
            Double longitude = pinPlaceFoodTruck.get(i).getPLongitude();
            Double latitude = pinPlaceFoodTruck.get(i).getPLatitude();
            FoodTruckPlaceList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));

        }
        List<Feature> tourPlaceList = new ArrayList<>();

        for (int i = 0; i < pinPlaceTour.size(); i++) {
            Double longitude = pinPlaceTour.get(i).getPLongitude();
            Double latitude = pinPlaceTour.get(i).getPLatitude();
            tourPlaceList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));
        }

        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                style.addImageAsync(ICON_ID_Foodtruck, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.pin_foodtruck_tmp)));
                Source FoodTruck = new GeoJsonSource(SOURCE_ID_Foodtruck,
                        FeatureCollection.fromFeatures(FoodTruckPlaceList));
                style.addSource(FoodTruck);
                SymbolLayer FoodTruckLayer = new SymbolLayer(LAYER_ID_Foodtruck, SOURCE_ID_Foodtruck)
                        .withProperties(PropertyFactory.iconImage(ICON_ID_Foodtruck), PropertyFactory.visibility(VISIBLE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));
                style.addLayer(FoodTruckLayer);

                style.addImageAsync(ICON_ID_Tour, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.pin_tour_tmp)));
                Source Tour = new GeoJsonSource(SOURCE_ID_Tour,
                        FeatureCollection.fromFeatures(tourPlaceList));
                style.addSource(Tour);
                SymbolLayer TourLayer = new SymbolLayer(LAYER_ID_Tour, SOURCE_ID_Tour)
                        .withProperties(PropertyFactory.iconImage(ICON_ID_Tour), PropertyFactory.visibility(VISIBLE), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));
                style.addLayer(TourLayer);


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
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", true);
        //SharedPreferences 처음 실행하는 경우에만 sqLite에 저장
        if (isFirst) {
            CSVtoSqLite();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }

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

    void CSVtoSqLite() {
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "location.db", null, 1);
        try {
            // 카테고리 1, 관광지
            CSVReader read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.korea_landmark_standard_data), "EUC-KR"));
            String[] record = null;
            //CSV 파일을 읽으면서 동시에 SqLite에 저장
            while ((record = read.readNext()) != null) {
                // Log.i("CSV 파일 읽기", "이름: " + record[0] + ", 위도: " + record[4] + ", 경도: " + record[5]);
                if (!record[4].equals("위도")) {
                    //saHelper 이용
                    dbHelper.insertLandmark(record[0], Double.parseDouble(record[4]), Double.parseDouble(record[5]));
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
}