package com.skt.flashbase.gis.Detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.skt.flashbase.gis.HomeActivity;
import com.skt.flashbase.gis.R;
import com.skt.flashbase.gis.roomDB.Place;
import com.skt.flashbase.gis.roomDB.PlaceViewModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MarkerDetailInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private PlaceViewModel mPlaceViewModel;
    private TextView detailInfoTextView;
    //jieun
    //map pin
    private String idx;
    private double longtitude;
    private double latitude;
    private String name;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    //naver search api
    private TextView DetailInfoCategoryTextView;
    private TextView DetailInfoLinkTextView;
    private TextView DetailInfoDescriptionTextView;
    private TextView DetailInfoPhoneTextView;
    private TextView DetailInfoAddressTextView;
    //jieun

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_marker_detail_info);
        mapView = findViewById(R.id.detailInfo_mapView_mapView);
        mPlaceViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        Intent intent = getIntent();
        idx = intent.getExtras().getString("idx");
        detailInfoTextView = (TextView) findViewById(R.id.detailInfo_name_textView);
        setPlaceData();

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(longtitude, latitude)));

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longtitude))
                .zoom(13)
                .bearing(180)
                .tilt(30)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 4000);

        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                style.addImageAsync(ICON_ID, BitmapUtils.getBitmapFromDrawable(
                        getResources().getDrawable(R.drawable.map_default_map_marker)));
                Source source = new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList));
                style.addSource(source);
                SymbolLayer layer = new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(PropertyFactory.iconImage(ICON_ID), iconAllowOverlap(true), iconOffset(new Float[]{0f, -9f}));
                style.addLayer(layer);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        detailInfoTextView.setText(name);
        Intent intent = getIntent();
        idx = intent.getExtras().getString("idx");
        mPlaceViewModel.getPlaceInfo(Integer.parseInt(idx)).observe(this, new Observer<Place>() {
            @Override
            public void onChanged(@Nullable Place place) {
                name = place.getPName();
                longtitude = place.getPLongitude();
                latitude = place.getPLatitude();
                TextView detailInfoTextView;
                detailInfoTextView = findViewById(R.id.detailInfo_name_textView);
                detailInfoTextView.setText(name);
                searchNaverLocationAPI(name);
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

    public void setPlaceData() {
        mPlaceViewModel.getPlaceInfo(Integer.parseInt(idx)).observe(this, new Observer<Place>() {
            @Override
            public void onChanged(@Nullable Place place) {
                name = place.getPName();
                longtitude = place.getPLongitude();
                latitude = place.getPLatitude();
                detailInfoTextView.setText(name);
            }
        });
    }

    public void searchNaverLocationAPI(String location) {

        String clientId = "vMpT6HoKgZdoqmxsEeYo";//애플리케이션 클라이언트 아이디값";
        String clientSecret = getString(R.string.naver_access_token);//애플리케이션 클라이언트 시크릿값";

        new Thread() {
             String category = "";
             String link = "";
             String description = "";
             String phone = "";
             String address = "";
            @Override
            public void run() {
                try {
                    String text = URLEncoder.encode(location, "UTF-8");
                    String apiURL = "https://openapi.naver.com/v1/search/local.xml?query=" + text + "&display=1"; // json 결과
                    //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    if (responseCode == 200) { // 정상 호출
                        xpp.setInput(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    } else {  // 에러 발생
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String tag;
                    xpp.next();
                    int eventType = xpp.getEventType();


                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        switch (eventType) {

                            case XmlPullParser.START_DOCUMENT:
                                break;


                            case XmlPullParser.START_TAG:

                                tag = xpp.getName();    //테그 이름 얻어오기


                                if (tag.equals("item")) ;// 첫번째 검색결과

                                else if (tag.equals("link")) {
                                    xpp.next();
                                    link = xpp.getText();

                                } else if (tag.equals("category")) {
                                    xpp.next();
                                    category = xpp.getText();
                                } else if (tag.equals("description")) {
                                    xpp.next();

                                    description = xpp.getText(); //description 요소의 TEXT 읽어와서 문자열버퍼에 추가

                                } else if (tag.equals("telephone")) {

                                    xpp.next();

                                    phone = xpp.getText(); //telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가

                                } else if (tag.equals("address")) {

                                    xpp.next();

                                    address = xpp.getText();

                                }

                                break;

                            case XmlPullParser.TEXT:

                                break;

                            case XmlPullParser.END_TAG:

                                break;

                        }
                        eventType = xpp.next();

                    }


                } catch (Exception e) {
                    System.out.println(e);
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        DetailInfoCategoryTextView = (TextView) findViewById(R.id.detailInfo_category_textView);
                        DetailInfoLinkTextView = (TextView) findViewById(R.id.detailInfo_link_textView);
                        DetailInfoDescriptionTextView = (TextView) findViewById(R.id.detailInfo_description_textView);
                        DetailInfoPhoneTextView = (TextView) findViewById(R.id.detailInfo_phone_textView);
                        DetailInfoAddressTextView = (TextView) findViewById(R.id.detailInfo_address_textView);


                        DetailInfoCategoryTextView.setText(category);
                        DetailInfoLinkTextView.setText(link);
                        DetailInfoDescriptionTextView.setText(description);
                        DetailInfoPhoneTextView.setText(phone);
                        DetailInfoAddressTextView.setText(address);

                    }
                });
            }

        }.start();
    }


}
