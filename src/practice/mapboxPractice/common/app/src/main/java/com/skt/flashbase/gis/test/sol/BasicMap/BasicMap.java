package com.skt.flashbase.gis.test.sol.BasicMap;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.skt.flashbase.gis.test.R;

public class BasicMap extends AppCompatActivity {
    SeekBar seekBar;
    TextView status;
    int storedValue = 0;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_basic_map);

        seekBar = (SeekBar)findViewById(R.id.seekBar1);
        status = (TextView)findViewById(R.id.status);
        seekBar.setProgress(storedValue);
        status.setText("real time");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float padding= seekBar.getPaddingLeft() + seekBar.getPaddingRight();
                float sPos = seekBar.getLeft() + seekBar.getPaddingLeft();
                float xPos = (seekBar.getWidth()-padding) * (seekBar.getProgress()+50)/ (seekBar.getMax()+50) + sPos - (status.getWidth() / 2);

                status.setX(xPos);

                if(progress<0) {
                    status.setText(Math.abs(progress) + "min ago");
                    if (xPos < -450) {
                        status.setX(-450);
                    }
                }else if(progress>0 && progress<=50) {
                    status.setText(progress + "min later");
                    if (xPos > 450) {
                        status.setX(450);
                    }
                }else if (progress == 0) {
                    status.setText("real time");
                }

                // 탐색 시간 전달하는 코드 추가
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
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
