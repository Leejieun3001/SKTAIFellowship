package com.skt.flashbase.gis.test.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.skt.flashbase.gis.test.R;

import java.util.ArrayList;
import java.util.List;


/**
 * The most basic example of adding a map to an activity.
 */
public class ChartExample extends AppCompatActivity {

    private MapView mapView;
    public LineChart lineChart;
    public RadarChart radarChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_chart_example);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                    }
                });
            }
        });
        create_bar_chart();
        createChart();
        create_radar_chart();

        //markerview activity는 마커 자체에 보여지는 내용에 대한 뷰
        MyMarkerView marker = new MyMarkerView(this, R.layout.activity_my_marker_view);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);
    }

    public void create_bar_chart(){

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

    void createChart() {
        lineChart = findViewById(R.id.line_chart);
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
    void create_radar_chart(){

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

        radarChart = findViewById(R.id.radarChart);
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
