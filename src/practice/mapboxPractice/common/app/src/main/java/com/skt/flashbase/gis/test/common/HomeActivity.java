package com.skt.flashbase.gis.test.common;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.charts.LineChart;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.opencsv.CSVReader;
import com.skt.flashbase.gis.test.Bubble.BubbleSeekBar;
import com.skt.flashbase.gis.test.R;
import com.skt.flashbase.gis.test.roomDB.Place;
import com.skt.flashbase.gis.test.roomDB.PlaceViewModel;
import com.skt.flashbase.gis.test.sqLite.DBHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

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

        PlaceAutocompleteFragment autocompleteFragment;

        if (savedInstanceState == null) {
            autocompleteFragment = PlaceAutocompleteFragment.newInstance(getString(R.string.mapbox_access_token));

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.mapSearchBar, autocompleteFragment, PlaceAutocompleteFragment.TAG);
            transaction.commit();
        } else {
            autocompleteFragment = (PlaceAutocompleteFragment)getSupportFragmentManager().findFragmentByTag(PlaceAutocompleteFragment.TAG);
        }
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(CarmenFeature carmenFeature) {
                Toast.makeText(HomeActivity.this,
                        carmenFeature.text(), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }
        });

        //--jieun--//
        //Model Provider 생성 RoomDB
        mPlaceViewModel = ViewModelProviders.of(this).get(PlaceViewModel.class);
        // 카테고리 검색 다이얼로그 생성
        setCustomDialogSearh();

        //--seungeun--//
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        //bottomsheetbebavior은 스트링 이름, 값은 클래스 명으로 지정. 해당 클래스가 로드되어 수행되는 구조
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
        //createChart();

        create_pie_chart();
//        //markerview activity는 마커 자체에 보여지는 내용에 대한 뷰
//        MyMarkerView marker = new MyMarkerView(this, R.layout.activity_my_marker_view);
//        marker.setChartView(lineChart);
//        lineChart.setMarker(marker);

        // sol BubbleSeekBar
        bottomSheetBehavior.getPeekHeight();

        final BubbleSeekBar bubbleSeekBar3 = findViewById(R.id.demo_4_seek_bar_3);

        bubbleSeekBar3.setProgress(storedValue);

        SimpleDateFormat sdf_hour = new SimpleDateFormat("hh:mm");
        SimpleDateFormat sdf_day = new SimpleDateFormat("MM/dd");
        SimpleDateFormat sdf_month = new SimpleDateFormat("yy/MM");
        Calendar cal = Calendar.getInstance();

        bubbleSeekBar3.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                cal.add(Calendar.DAY_OF_MONTH, -3);
                array.put(0, sdf_day.format(cal.getTime()));
                for (int i = 1; i < 7; i++) {
                    cal.add(Calendar.DAY_OF_MONTH, +1);
                    array.put(i, sdf_day.format(cal.getTime()));
                }
                return array;
            }
        });

        bubbleSeekBar3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                // 탐색 시간 전달하는 코드 추가
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        // sol's spinner
        Spinner spinner = (Spinner) findViewById(R.id.txt_question_type);
        SpinnerAdapter sAdapter = ArrayAdapter.createFromResource(this, R.array.question, android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(sAdapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(HomeActivity.this, (String) sAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                if(spinner.getSelectedItemPosition() == 0) {
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
                            return array;
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        TextView btn_chart_ex = (TextView) findViewById(R.id.btn_chart_example);

        btn_chart_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChartExample.class);
                startActivity(intent);
            }
        });

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
        // 길게 누를때 - 화면 넘어감
        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
            @Override
            public boolean onMapLongClick(@NonNull LatLng point) {
                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
                List<Feature> Tourfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Tour);
                if (!Tourfeatures.isEmpty()) {
                    String name = Tourfeatures.get(0).getStringProperty("name");

                    Toast.makeText(HomeActivity.this, name + "입니다.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                List<Feature> FoodTruckfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Foodtruck);
                if (!FoodTruckfeatures.isEmpty()) {
                    String name = FoodTruckfeatures.get(0).getStringProperty("name");
                    Toast.makeText(HomeActivity.this, name + "입니다.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
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
                    Intent intent = new Intent(getApplicationContext(), DetailInfoActivity.class);

                    intent.putExtra("idx", idx);
                    startActivity(intent);
                    Toast.makeText(HomeActivity.this, "index 는 : " + idx + "입니다.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                List<Feature> FoodTruckfeatures = mapboxMap.queryRenderedFeatures(rectF, LAYER_ID_Foodtruck);
                if (!FoodTruckfeatures.isEmpty()) {
                    String idx = FoodTruckfeatures.get(0).getStringProperty("idx");
                    Intent intent = new Intent(getApplicationContext(), DetailInfoActivity.class);

                    Toast.makeText(HomeActivity.this, "index 는 : " + idx + "입니다.",
                            Toast.LENGTH_SHORT).show();
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
        //--jieun--//
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
        mPlaceViewModel.getAllPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                for (int i = 0; i < places.size(); i++) {
                    pinPlaceAll.add(i, places.get(i));
                }

            }
        });
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


    //--seung eun--//



    public void create_pie_chart(){
        PieChartView pieChartview;
        pieChartview= findViewById(R.id.pie_chart);

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
        pieChartData.setHasCenterCircle(true).setCenterText1("사람이 많아요!!").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));

        pieChartview.setPieChartData(pieChartData);

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
                    .foregroundDrawable(R.drawable.red_marker)
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
}
