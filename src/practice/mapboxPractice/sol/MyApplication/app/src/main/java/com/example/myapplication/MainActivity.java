package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.AnimatedIconMovement.AnimatedIconMovement;
import com.example.myapplication.BasicMap.BasicMap;
import com.example.myapplication.CircleLayerClusters.CircleLayerClusters;
import com.example.myapplication.CreateHotspots.CreateHotspots;
import com.example.myapplication.DataTimeLapse.DataTimeLapse;
import com.example.myapplication.DeviceLocation.LocationCamera;
import com.example.myapplication.VariableLabelPlacement.VariableLabelPlacementActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button basicMapBtn = (Button) findViewById(R.id.basic_map_btn) ;
        Button IconMoveBtn = (Button) findViewById(R.id.Icon_move_btn) ;
        Button VaryLabelBtn = (Button) findViewById(R.id.variable_label_btn) ;
        Button CircleLayerBtn = (Button) findViewById(R.id.circle_layer_btn) ;
        Button CreateHotspotsBtn = (Button) findViewById(R.id.create_hotspots_btn) ;
        Button DataTimeLapseBtn = (Button) findViewById(R.id.data_time_lapse_btn) ;
        Button LocationCamBtn = (Button) findViewById(R.id.location_cam_btn) ;
        Button SettingBtn = (Button) findViewById(R.id.setting_btn) ;
        // 저장된 값을 불러오기 위해 같은 네임파일을 찾음.

        basicMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, BasicMap.class);
                startActivity(intent);
            }
        });
        VaryLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, VariableLabelPlacementActivity.class);
                startActivity(intent);
            }
        });
        IconMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AnimatedIconMovement.class);
                startActivity(intent);
            }
        });
        CircleLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CircleLayerClusters.class);
                startActivity(intent);
            }
        });
        CreateHotspotsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CreateHotspots.class);
                startActivity(intent);
            }
        });
        DataTimeLapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, DataTimeLapse.class);
                startActivity(intent);
            }
        });
        LocationCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, LocationCamera.class);
                startActivity(intent);
            }
        });
        SettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SetActivity.class);
                startActivity(intent);
            }
        });
    }
}