package com.skt.flashbase.gis.test.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.opencsv.CSVReader;
import com.skt.flashbase.gis.test.R;
import com.skt.flashbase.gis.test.sqLite.DBHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapView mapView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", true);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_home);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        //처음 실행하는 경우에만 sqLite에 저장
        if (isFirst) {
            readCSV();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
        readSQLite();
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        db = openOrCreateDatabase("location.db", Context.MODE_PRIVATE, null);

        String sql = "select * from landmark ; ";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

        while (!results.isAfterLast()) {
            Double longitude = results.getDouble(3);
            Double latitude = results.getDouble(2);
            symbolLayerIconFeatureList.add(Feature.fromGeometry(
                    Point.fromLngLat(longitude, latitude)));
            results.moveToNext();
        }
        results.close();

        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        HomeActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(PropertyFactory.iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconOffset(new Float[]{0f, -9f}))
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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

    void readSQLite() {
// Database 생성 및 열기
    }

    void readCSV() {
        String UTF8 = "utf8";
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "location.db", null, 1);
        try {
            CSVReader read = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.korea_landmark_standard_data), "EUC-KR"));
            String[] record = null;

            while ((record = read.readNext()) != null) {
                Log.i("CSV 파일 읽기", "이름: " + record[0] + ", 위도: " + record[4] + ", 경도: " + record[5]);
                if (!record[4].equals("위도")) {
                    dbHelper.insertLandmark(record[0], Double.parseDouble(record[4]), Double.parseDouble(record[5]));
                }
            }
        } catch (IOException ex) {
            // handle exception
        } finally {

        }
    }
}