package com.example.mapbox_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button btn_main = (Button)findViewById(R.id.btn_goto_main);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_change = (Button)findViewById(R.id.btn_goto_change);

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),LanguageSwitchActivity.class);
                startActivity(intent2);
            }
        });

        Button btn_pulsing = (Button)findViewById(R.id.btn_goto_pulsing_layer_opacity);

        btn_pulsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getApplicationContext(), PulsingLayerOpacityColorActivity.class);
                startActivity(intent3);
            }
        });

        Button btn_toggle = (Button)findViewById(R.id.btn_goto_toggle_collision_detection);

        btn_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(getApplicationContext(), SymbolCollisionDetectionActivity.class);
                startActivity(intent4);
            }
        });

        Button btn_symbol = (Button)findViewById(R.id.btn_goto_symbolLayer_info_window);

        btn_symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent(getApplicationContext(),InfoWindowSymbolLayerActivity.class);
                startActivity(intent5);
            }
        });

        Button btn_zoom = (Button)findViewById(R.id.btn_goto_zoom_icon_switch);

        btn_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent6 = new Intent(getApplicationContext(),SymbolSwitchOnZoomActivity.class);
                startActivity(intent6);
            }
        });

        Button btn_camera = (Button)findViewById(R.id.btn_goto_location_camera);

        btn_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent7 = new Intent(getApplicationContext(),LocationComponentCameraOptionsActivity.class);
                startActivity(intent7);
            }
        });
        Button btn_foodtruck = (Button)findViewById(R.id.btn_goto_foodtruck);

        btn_foodtruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent8 = new Intent(getApplicationContext(),BasicSymbolLayerActivity.class);
                startActivity(intent8);
            }
        });

    }
}
