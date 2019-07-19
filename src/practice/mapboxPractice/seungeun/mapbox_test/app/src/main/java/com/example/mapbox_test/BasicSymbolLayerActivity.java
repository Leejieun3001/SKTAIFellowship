package com.example.mapbox_test;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

/**
 * Display {@link SymbolLayer} icons on the map.
 *
 */
// 안드로이드 하위버전의 단말기들을 고려해서 AppCompatActivity를 상속받는 것이 좋다.
// 맵박스가 준비되었음을 알려주는 OnMapReadyCallBack 인터페이스를 구현해
// 안드로이드가 자동으로 OnMapReady() 메소드를 호출해준다.
// OnMapReady() 메소드 안에 맵박스 관련 코드를 작성해주면 된다.
public class BasicSymbolLayerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_basic_symbol_layer);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //이렇게 하면 맵박스 실행시 안드로이드가 onMapReady()메소드를 호출해준다.
        mapView.getMapAsync(this);

    }

    //맵박스가 준비되었을 때 호출되는 메소드
    //인자로 넘어온 맵박스를 활용해서 기능을 사용함.
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        //핀을 꽃아주기 위해 위도, 경도를 저장할 리스트 배열 초기화
        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

        try {
            //
            List<List<String>> ret = new ArrayList<List<String>>();

            //raw 폴더에 저장된 csv 형태의 foodtruck 데이터를 읽어 is에 저장
            //한글이 깨지지 않도록 euc-kr 인코딩 방식을 지정
            InputStreamReader is = new InputStreamReader(getResources()
                    .openRawResource(R.raw.foodtruck), "euc-kr");
            //foodtruck 데이터를 버퍼에 담아 한꺼번에 사용
            BufferedReader reader = new BufferedReader(is);

            // 각 데이터 한 줄을 분리해주기 위해 사용
            String line = "";


            // 버퍼에 담긴 데이터를 한 줄 씩 읽어서 line 변수에 저장
            // 모든 데이터를 다 읽을 때 까지 반복
            while ((line = reader.readLine()) != null) {

                List<String> tmpList = new ArrayList<String>();

                // csv 파일 저장시 ','를 사용해 분리하도록 지정
                String array[] = line.split(",");
                //하나의 장소에 대한 모든 데이터(쉼표로 분리된)를 tmpList에 저장
                tmpList = Arrays.asList(array);

                // tmpList에 String 형태로 저장된 위도,경도값을 double 형으로 바꾸어 저장
                Double longtitude = Double.valueOf(tmpList.get(6));
                Double latitude = Double.parseDouble(tmpList.get(7));

                //핀을 꽃아주기 위해 symbolLayerIconFeatureList 리스트에 위도, 경도 값 추가
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(longtitude,latitude))
                );
                //ret.add(tmpList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                // Add the SymbolLayer icon image to the map style
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        BasicSymbolLayerActivity.this.getResources(), R.drawable.blue_marker))

                // Adding a GeoJson source for the SymbolLayer icons.
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                // the coordinate point. This is offset is not always needed and is dependent on the image
                // that you use for the SymbolLayer icon.
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(PropertyFactory.iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconOffset(new Float[]{0f, -9f}))
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


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
}