package com.skt.flashbase.gis.Detail;

import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.skt.flashbase.gis.Bubble.BubbleSeekBar;
import com.skt.flashbase.gis.HomeActivity;
import com.skt.flashbase.gis.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnalysisInfoDetail extends AppCompatActivity {

    private int storedValue =3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_info_detail);
        createSeekBar();
        line_chart();
        create_bar_chart();

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
                    color = ContextCompat.getColor(AnalysisInfoDetail.this, R.color.mapboxRed);
                } else {
                    color = ContextCompat.getColor(AnalysisInfoDetail.this, R.color.mapboxTeal);
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
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, R.layout.spinner_item, str);

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

                            line_chart();
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

                            line_chart();
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

                            line_chart();
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
    public void line_chart(){
        LineChart lineChart = findViewById(R.id.real_time_linechart);
        List<Entry> lineEntries = getDataSet();
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setLineWidth(1);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setCircleRadius(1);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.DKGRAY);

        LineData lineData = new LineData(lineDataSet);
        lineChart.getDescription().setText("");
        lineChart.getDescription().setTextSize(12);
        //lineChart.setDrawMarkers(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(false);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
        lineChart.setData(lineData);
    }
    private List<Entry> getDataSet() {
        List<Entry> lineEntries = new ArrayList<Entry>();

        for(int i=0; i < 23; ){
            int x = (int)(Math.random()*20)+1;
            lineEntries.add(new Entry(i, x));
            i=i+2;
        }

        return lineEntries;
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

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(10f);
        xl.setCenterAxisLabels(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
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
}