package com.skt.flashbase.gis.Currentlocation;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.skt.flashbase.gis.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class CustomCurrentLoc extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private Marker featureMarker;
    private   MapboxMap mapboxMap ;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get mapbox instance
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_custom_current_loc);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    /*
    OnMapReadyCallback: Called when the map is ready to be used
      - 지도가 사용될 준비가 되었을때 콜백
      - 이 인터페이스의 인스턴스가 객체 MapFragment또는 MapView객체 에 설정 되면
       onMapReady()메소드는 맵의 사용 준비가 끝났을 때에 트리거되어 null의 인스턴스를 제공
      - 보통 지도에 대한 초기 셋팅 해줌
     */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        /* 11. Marker symbol layer 연습
         *  지도에 pin 을 mark 하는 간단한 예제
         */
        //com.mapbox.geojson의 Feautre 이용해 생성
        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        //skt 분당사옥 위도,경도
        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(127.115753, 37.380808)));
        // 수내역 위도,경도
        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(127.114312, 37.378395)));

        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                        //symbol layer 이미지 아이콘 추가
                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                                CustomCurrentLoc.this.getResources(), R.drawable.pin))
                        //icon 에 GeoJson 추가
                        .withSource(new GeoJsonSource(SOURCE_ID,
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                        //아이콘의 중간 부분이 아니라 고정되어있는 아이콘 아이콘이 좌표에 고정됩니다.
                        //좌표 점. 오프셋이 반드시 필요한 것은 아니며 이미지에 따라 다릅니다.
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(PropertyFactory.iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, -9f}))
                        ),

                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap.addOnMapClickListener(CustomCurrentLoc.this);
                        Toast.makeText(CustomCurrentLoc.this,
                                getString(R.string.click_on_map_instruction), Toast.LENGTH_SHORT).show();

                        //16. 현재위치 가져오기
                        enableLocationComponent(style);
                    }
                });
    }

    /*
     OnMapClickListener

     */
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

        if (features.size() > 0) {
            Feature feature = features.get(0);
            String property;
            StringBuilder stringBuilder = new StringBuilder();
            if (feature.properties() != null) {
                for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                    stringBuilder.append(String.format("%s - %s", entry.getKey(), entry.getValue()));
                    stringBuilder.append(System.getProperty("line.separator"));
                }

                featureMarker = mapboxMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(getString(R.string.query_feature_marker_title))
                        .snippet(stringBuilder.toString())
                );

            } else {
                property = getString(R.string.query_feature_marker_snippet);
                featureMarker = mapboxMap.addMarker(new MarkerOptions()
                        .position(point)
                        .snippet(property)
                );
            }
        } else {
            featureMarker = mapboxMap.addMarker(new MarkerOptions()
                    .position(point)
                    .snippet(getString(R.string.query_feature_marker_snippet))
            );
        }
        mapboxMap.selectMarker(featureMarker);

        return true;
    }

    @SuppressWarnings({"MossingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        //check if permissions are enabled and if nor request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.RED)
                    .foregroundDrawable(R.drawable.here)
                    .build();
            //Get and instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            //Activation with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            //Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            //set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            //set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            //Add the location icon click listener
            locationComponent.addOnLocationClickListener(this);

            findViewById(R.id.back_to_camera_tracking_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(16f);
                        //  Toast.makeText(LocationComponentOptionsActivity.this, getString(R.string.tracking_enabled),
                        //        Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(LocationComponentOptionsActivity.this, getString(R.string.tracking_already_enabled),
                        //         Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.user_location_permission_explanation),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
// Empty on purpose
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}