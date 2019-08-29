package com.skt.flashbase.gis.Detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;


import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import com.skt.flashbase.gis.R;
import com.skt.flashbase.gis.roomDB.Place;
import com.skt.flashbase.gis.roomDB.PlaceViewModel;

import org.apache.commons.lang3.ObjectUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;


import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

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
    //seungeun


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ddfdf","dfdfddfdff");
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

        Button btn_analysis_info_detail = (Button)findViewById(R.id.analysis_info_detail);

        create_chart();
        create_pie_chart();

        btn_analysis_info_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AnalysisInfoDetail.class);
                startActivity(intent);
            }
        });

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
//        String clientSecret = getString(R.string.naver_access_token);//애플리케이션 클라이언트 시크릿값";

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
//                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
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


                        LinearLayout linear_categoty = findViewById(R.id.place_category);
                        LinearLayout linear_address = findViewById(R.id.place_address);
                        LinearLayout linear_link = findViewById(R.id.place_link);
                        LinearLayout linear_description = findViewById(R.id.place_description);
                        LinearLayout linear_phonenum = findViewById(R.id.place_phonenum);

                        if(category == null){
                            linear_categoty.setVisibility(View.GONE);
                        } else{DetailInfoCategoryTextView.setText(category);}

                        if(address == null){
                            linear_address.setVisibility(View.GONE);
                        } else{DetailInfoAddressTextView.setText(address);}

                        if(link == null){
                            linear_link.setVisibility(View.GONE);
                        } else{DetailInfoLinkTextView.setText(link);}

                        if(description == null){
                            linear_description.setVisibility(View.GONE);
                        } else{DetailInfoDescriptionTextView.setText(description);}

                        if(phone == null){
                            linear_phonenum.setVisibility(View.GONE);
                        } else{DetailInfoPhoneTextView.setText(phone);}



                    }
                });
            }

        }.start();
    }

    public void create_chart(){
        //pie_chart();
        bar_chart();
        radar_chart();
        line_Chart();

    }


//    public void line_chart(){
//        LineChart lineChart = findViewById(R.id.line_chart);
//        List<Entry> lineEntries = getDataSet();
//        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
//        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        lineDataSet.setHighlightEnabled(true);
//        lineDataSet.setLineWidth(2);
//        lineDataSet.setColor(Color.RED);
//        lineDataSet.setCircleColor(Color.YELLOW);
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleHoleRadius(3);
//        lineDataSet.setDrawHighlightIndicators(true);
//        lineDataSet.setHighLightColor(Color.RED);
//        lineDataSet.setValueTextSize(12);
//        lineDataSet.setValueTextColor(Color.DKGRAY);
//
//        LineData lineData = new LineData(lineDataSet);
//        lineChart.getDescription().setText("");
//        lineChart.getDescription().setTextSize(12);
//        lineChart.setDrawMarkers(true);
//        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
//        lineChart.animateY(1000);
//        lineChart.getXAxis().setGranularityEnabled(true);
//        lineChart.getXAxis().setGranularity(1.0f);
//        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
//        lineChart.setData(lineData);
//    }
//    private List<Entry> getDataSet() {
//        List<Entry> lineEntries = new ArrayList<Entry>();
//        lineEntries.add(new Entry(0, 1));
//        lineEntries.add(new Entry(1, 2));
//        lineEntries.add(new Entry(2, 3));
//        lineEntries.add(new Entry(3, 4));
//        lineEntries.add(new Entry(4, 2));
//        lineEntries.add(new Entry(5, 3));
//        lineEntries.add(new Entry(6, 1));
//        lineEntries.add(new Entry(7, 5));
//        lineEntries.add(new Entry(8, 7));
//        lineEntries.add(new Entry(9, 6));
//        lineEntries.add(new Entry(10, 4));
//        lineEntries.add(new Entry(11, 5));
//        return lineEntries;
//    }
//    public void hbar_chart(){
//
//        LineChart lineChart = findViewById(R.id.bar_chart);
//        List<Entry> entries = new ArrayList<>();
//
//        entries.add(new Entry(1, 1));
//        entries.add(new Entry(2, 2));
//        entries.add(new Entry(3, 0));
//        entries.add(new Entry(4, 4));
//        entries.add(new Entry(5, 3));
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "시간대별 유동인구");
//        lineDataSet.setLineWidth(1);
//        lineDataSet.setCircleRadius(4);
//        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setCircleColorHole(Color.BLUE);
//        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setDrawCircleHole(true);
//        lineDataSet.setDrawCircles(true);
//        lineDataSet.setDrawHorizontalHighlightIndicator(false);
//        lineDataSet.setDrawHighlightIndicators(false);
//        lineDataSet.setDrawValues(false);
//
//        LineData lineData = new LineData(lineDataSet);
//        lineChart.setData(lineData);
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextColor(Color.BLACK);
//        xAxis.enableGridDashedLine(8, 24, 0);
//
//        YAxis yLAxis = lineChart.getAxisLeft();
//        yLAxis.setTextColor(Color.BLACK);
//
//        YAxis yRAxis = lineChart.getAxisRight();
//        yRAxis.setDrawLabels(false);
//        yRAxis.setDrawAxisLine(false);
//        yRAxis.setDrawGridLines(false);
//
//        Description description = new Description();
//        description.setText("");
//
//        lineChart.setDoubleTapToZoomEnabled(false);
//        lineChart.setDrawGridBackground(false);
//        lineChart.setDescription(description);
//        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
//        lineChart.invalidate();
//    }
//    void radar_chart(){
//
//        ArrayList<RadarEntry> entries = new ArrayList<>();
//        entries.add(new RadarEntry(0f, 0.21f));
//        entries.add(new RadarEntry(1f, 0.12f));
//        entries.add(new RadarEntry(2f, 0.20f));
//        entries.add(new RadarEntry(3f, 0.52f));
//        entries.add(new RadarEntry(4f, 0.29f));
//        entries.add(new RadarEntry(5f, 0.62f));
//
//        ArrayList<RadarEntry> entries2 = new ArrayList<>();
//        entries.add(new RadarEntry(0f, 0.44f));
//        entries.add(new RadarEntry(1f, 0.32f));
//        entries.add(new RadarEntry(2f, 0.24f));
//        entries.add(new RadarEntry(3f, 0.18f));
//        entries.add(new RadarEntry(4f, 0.22f));
//        entries.add(new RadarEntry(5f, 0.65f));
//
//        RadarChart radarChart = findViewById(R.id.radar_chart);
//        RadarDataSet radarDataSet = new RadarDataSet(entries, "woman");
//        radarDataSet.setColors(Color.parseColor("#822B30"));
//        radarDataSet.setDrawFilled(true);
//
//        RadarDataSet radarDataSet2 = new RadarDataSet(entries2, "man");
//        radarDataSet.setColors(Color.parseColor("#264973"));
//        radarDataSet2.setDrawFilled(true);
//
//
//        ArrayList<IRadarDataSet> sets = new ArrayList<>();
//        sets.add(radarDataSet);
//        sets.add(radarDataSet2);
//
//        RadarData radarData = new RadarData(sets);
//
//        XAxis xAxis = radarChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//
//        final String[] ages = new String[]{"10대", "20대", "30대", "40대", "50대", "60대"};
//        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(ages);
//        xAxis.setGranularity(2f);
//        xAxis.setValueFormatter(formatter);
//        radarChart.setData(radarData);
//
//        radarChart.animateXY(5000, 5000);
//        radarChart.invalidate();
//    }
//
////    public void pie_chart() {
////        PieChart pieChart = (PieChart)findViewById(R.id.pie_chart);
////        PieDataSet pieDataSet = new PieDataSet(getData(),"Inducesmile");
////        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
////        PieData pieData = new PieData(pieDataSet);
////        //pieChart.setCenterText("사람이 많아요");
////        //pieChart.setCenterTextColor(Color.parseColor("#264973"));
////
////        pieChart.setData(pieData);
////        pieChart.animateXY(5000, 5000);
////        pieChart.invalidate();
////    }
//    private ArrayList getData(){
//        ArrayList<PieEntry> entries = new ArrayList<>();
//        entries.add(new PieEntry(945f, "Ayo"));
//        entries.add(new PieEntry(1030f, "Adekola"));
//        entries.add(new PieEntry(1143f, "Henry"));
//        entries.add(new PieEntry(1250f, "Mark"));
//        return entries;
//    }

    public void bar_chart(){

        HorizontalBarChart barChart = findViewById(R.id.bar_chart);

//        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
//        Description description = new Description();
//        description.setText("");
//        barChart.setDescription(description);
//        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(true);
//        barChart.setDrawGridBackground(false);
//
//        XAxis xl = barChart.getXAxis();
//        xl.setGranularity(10f);
//        xl.setCenterAxisLabels(true);
//
//        YAxis leftAxis = barChart.getAxisLeft();
//        leftAxis.setDrawGridLines(false);
//        leftAxis.setSpaceTop(30f);
        barChart.getAxisRight().setEnabled(false);

//data
        float groupSpace = 4.6f;
        float barSpace = 0.8f;
        float barWidth = 2f;

        int startYear = 10;
        int endYear = 70;

        List<BarEntry> man_List = new ArrayList<BarEntry>();
        List<BarEntry> woman_List = new ArrayList<BarEntry>();

        int n = 300;
        int n2 = 143;

        //BarEntry 첫번째 인자는 x축 두번째 인자는 y축
        for (int i = startYear; i <= endYear; i+=10) {
            man_List.add(new BarEntry(i, n));
            n+= 10;
        }
        for (int i = startYear; i <= endYear; i+=10) {
            woman_List.add(new BarEntry(i, n2));
            n2+=30;
        }

        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);

            set1.setValues(man_List);
            set2.setValues(woman_List);

            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(man_List, "man");
            set1.setColors(Color.parseColor("#264973"));

            set2 = new BarDataSet(woman_List, " woman");
            set2.setColors(Color.parseColor("#822B30"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }
        barChart.getBarData().setBarWidth(barWidth);
        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();
    }

    void line_Chart() {
        LineChart lineChart = findViewById(R.id.line_chart);
        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 3));

        LineDataSet lineDataSet = new LineDataSet(entries, "시간대별 유동인구");
        lineDataSet.setLineWidth(1);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
    }
    void radar_chart(){

        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(0f, 0.21f));
        entries.add(new RadarEntry(1f, 0.12f));
        entries.add(new RadarEntry(2f, 0.20f));
        entries.add(new RadarEntry(3f, 0.52f));
        entries.add(new RadarEntry(4f, 0.29f));
        entries.add(new RadarEntry(5f, 0.62f));

        ArrayList<RadarEntry> entries2 = new ArrayList<>();
        entries.add(new RadarEntry(0f, 0.44f));
        entries.add(new RadarEntry(1f, 0.32f));
        entries.add(new RadarEntry(2f, 0.24f));
        entries.add(new RadarEntry(3f, 0.18f));
        entries.add(new RadarEntry(4f, 0.22f));
        entries.add(new RadarEntry(5f, 0.65f));

        RadarChart radarChart = findViewById(R.id.radar_chart);
        RadarDataSet radarDataSet = new RadarDataSet(entries, "woman");
        radarDataSet.setColors(Color.parseColor("#822B30"));
        radarDataSet.setDrawFilled(true);

        RadarDataSet radarDataSet2 = new RadarDataSet(entries2, "man");
        radarDataSet.setColors(Color.parseColor("#264973"));
        radarDataSet2.setDrawFilled(true);


        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(radarDataSet);
        sets.add(radarDataSet2);

        RadarData radarData = new RadarData(sets);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        final String[] ages = new String[]{"10대", "20대", "30대", "40대", "50대", "60대"};
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(ages);
        xAxis.setGranularity(2f);
        xAxis.setValueFormatter(formatter);
        radarChart.setData(radarData);

        radarChart.animateXY(5000, 5000);
        radarChart.invalidate();
    }
    public void create_pie_chart() {
        PieChartView pieChartview;
        pieChartview = findViewById(R.id.pie_chart);

        List pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.parseColor("#a3c9c7")).setLabel("20대 : 15%"));
        pieData.add(new SliceValue(25, Color.parseColor("#cb7575")).setLabel("30대 : 25%"));
        pieData.add(new SliceValue(10, Color.parseColor("#ef9e9f")).setLabel("10대 : 10%"));
        pieData.add(new SliceValue(60, Color.parseColor("#8283a7")).setLabel("40대 : 10%"));
        pieData.add(new SliceValue(10, Color.parseColor("#589167")).setLabel("50대 : 35%"));
        pieData.add(new SliceValue(60, Color.parseColor("#ebce95")).setLabel("그 외 : 5%"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(12);

        //원 안에 텍스트 넣을 수 있는 코드
        pieChartData.setHasCenterCircle(true).setCenterText1("사람이 많아요!!").setCenterText2("약 5000명 ").setCenterText2Color(Color.parseColor("#0097A7"))
                .setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartview.setPieChartData(pieChartData);

    }

}
