package com.skt.flashbase.gis.Currentlocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.mapbox.mapboxsdk.snapshotter.MapSnapshot;
import com.mapbox.mapboxsdk.snapshotter.MapSnapshotter;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.skt.flashbase.gis.R;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class CustomCurrentLoc extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private Marker featureMarker;
    private MapboxMap mapboxMap;

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    //location picker
    private static final String TAG = "CustomCurrentLoc";
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private Button selectLocationButton;
    private ImageView hoveringMarker;
    //snap shot
    private MapSnapshotter mapSnapshotter;
    private Button cameraFab;
    private boolean hasStartedSnapshotGeneration;
    private Style style;
    //customized location icon
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get mapbox instance
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_custom_current_loc);

        cameraFab = findViewById(R.id.share_screenshot_button);
        hasStartedSnapshotGeneration = false;

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        CustomCurrentLoc.this.style = style;
                        cameraFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!hasStartedSnapshotGeneration) {
                                    hasStartedSnapshotGeneration = true;
                                    Toast.makeText(CustomCurrentLoc.this, "스크린 샷 중..", Toast.LENGTH_LONG).show();
                                    startSnapShot(
                                            mapboxMap.getProjection().getVisibleRegion().latLngBounds,
                                            mapView.getMeasuredHeight(),
                                            mapView.getMeasuredWidth());
                                }
                            }
                        });
                    }
                });
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
        });
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
        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(127.115753, 37.380808)));  //skt 분당사옥 위도,경도
        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(127.114312, 37.378395)));  // 수내역 위도,경도

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
                                .withProperties(iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, -9f}))
                        ),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        mapboxMap.addOnMapClickListener(CustomCurrentLoc.this);
                        Toast.makeText(CustomCurrentLoc.this,
                                getString(R.string.click_on_map_instruction), Toast.LENGTH_SHORT).show();

                        //16. 현재위치 가져오기
                        enableLocationComponent(style);

                        //13. location picker
                        enableLocationPlugin(style);
                        // Toast instructing user to tap on the mapboxMap
                        Toast.makeText(
                                CustomCurrentLoc.this,
                                getString(R.string.move_map_instruction), Toast.LENGTH_SHORT).show();

// When user is still picking a location, we hover a marker above the mapboxMap in the center.
// This is done by using an image view with the default marker found in the SDK. You can
// swap out for your own marker image, just make sure it matches up with the dropped marker.
                        hoveringMarker = new ImageView(CustomCurrentLoc.this);
                        hoveringMarker.setImageResource(R.drawable.click);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                        hoveringMarker.setLayoutParams(params);
                        mapView.addView(hoveringMarker);

// Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                        initDroppedMarker(style);

// Button for user to drop marker or to pick marker back up.
                        selectLocationButton = findViewById(R.id.select_location_button);
                        selectLocationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (hoveringMarker.getVisibility() == VISIBLE) {

// Use the map target's coordinates to make a reverse geocoding search
                                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

// Hide the hovering red hovering ImageView marker
                                    hoveringMarker.setVisibility(View.INVISIBLE);

// Transform the appearance of the button to become the cancel button
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(CustomCurrentLoc.this, R.color.colorAccent));
                                    selectLocationButton.setText(getString(R.string.location_picker_select_location_button_cancel));

// Show the SymbolLayer icon to represent the selected map location
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                                        if (source != null) {
                                            source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                        }
                                        style.getLayer(DROPPED_MARKER_LAYER_ID).setProperties(visibility(Property.VISIBLE));
                                    }

// Use the map camera target's coordinates to make a reverse geocoding search
                                    reverseGeocode(style, Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                                } else {

// Switch the button appearance back to select a location.
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(CustomCurrentLoc.this, R.color.colorPrimary));
                                    selectLocationButton.setText(getString(R.string.location_picker_select_location_button_select));

// Show the red hovering ImageView marker
                                    hoveringMarker.setVisibility(VISIBLE);

// Hide the selected location SymbolLayer
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        style.getLayer(DROPPED_MARKER_LAYER_ID).setProperties(visibility(NONE));
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
// Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.here));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
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


    @SuppressWarnings({"MissingPermission"})
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

    /*
     * PermissionsListener
     * - 사용자 권한 설정 관리자
     * */
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

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param style style
     * @param point The location to use for the search
     */
    private void reverseGeocode(@NonNull final Style style, final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    List<CarmenFeature> results = response.body().features();
                    if (results.size() > 0) {
                        CarmenFeature feature = results.get(0);

                        // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                        if (style.isFullyLoaded() && style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                            Toast.makeText(CustomCurrentLoc.this,
                                    String.format(getString(R.string.location_picker_place_name_result),
                                            feature.placeName()), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CustomCurrentLoc.this,
                                getString(R.string.location_picker_dropped_marker_snippet_no_results), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    //Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            //Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
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


    /**
     * Creates bitmap from given parameters, and creates a notification with that bitmap
     *
     * @param latLngBounds of map
     * @param height       of map
     * @param width        of map
     */
    private void startSnapShot(LatLngBounds latLngBounds, int height, int width) {
        if (!style.isFullyLoaded()) {
            return;
        }
        if (mapSnapshotter == null) {
            MapSnapshotter.Options options =
                    new MapSnapshotter.Options(width, height).withRegion(latLngBounds).withStyle(style.getUrl());

            mapSnapshotter = new MapSnapshotter(CustomCurrentLoc.this, options);
        } else {
            mapSnapshotter.setSize(width, height);
            mapSnapshotter.setRegion(latLngBounds);
            mapSnapshotter.setRegion(latLngBounds);
        }

        mapSnapshotter.start(new MapSnapshotter.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(MapSnapshot snapshot) {
                Bitmap bitmapOfMapSnapshotImage = snapshot.getBitmap();
                Uri bmpUri = getLocalBitmapUri(bitmapOfMapSnapshotImage);
                Intent shareIntent = new Intent();
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/png");
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share map image"));

                hasStartedSnapshotGeneration = false;
            }
        });
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            try {
                out.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return bmpUri;
    }
}
