package com.skt.flashbase.gis.test.sol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skt.flashbase.gis.test.R;
import com.skt.flashbase.gis.test.sol.AnimatedIconMovement.AnimatedIconMovement;
import com.skt.flashbase.gis.test.sol.BasicMap.BasicMap;
import com.skt.flashbase.gis.test.sol.CircleLayerClusters.CircleLayerClusters;
import com.skt.flashbase.gis.test.sol.CreateHotspots.CreateHotspots;
import com.skt.flashbase.gis.test.sol.DataTimeLapse.DataTimeLapse;
import com.skt.flashbase.gis.test.sol.DeviceLocation.LocationCamera;
import com.skt.flashbase.gis.test.sol.VariableLabelPlacement.VariableLabelPlacement;

public class SolHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sol_home);
        Button basicMapBtn = (Button) findViewById(R.id.basic_map_btn);
        Button IconMoveBtn = (Button) findViewById(R.id.Icon_move_btn) ;
        Button VaryLabelBtn = (Button) findViewById(R.id.variable_label_btn) ;
        Button CircleLayerBtn = (Button) findViewById(R.id.circle_layer_btn) ;
        Button CreateHotspotsBtn = (Button) findViewById(R.id.create_hotspots_btn) ;
        Button DataTimeLapseBtn = (Button) findViewById(R.id.data_time_lapse_btn) ;
        Button LocationCamBtn = (Button) findViewById(R.id.location_cam_btn) ;
        Button SettingBtn = (Button) findViewById(R.id.setting_btn) ;

        basicMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SolHomeActivity.this, BasicMap.class);
                startActivity(intent);
            }
        });
        VaryLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, VariableLabelPlacement.class);
                startActivity(intent);
            }
        });
        IconMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, AnimatedIconMovement.class);
                startActivity(intent);
            }
        });
        CircleLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, CircleLayerClusters.class);
                startActivity(intent);
            }
        });
        CreateHotspotsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, CreateHotspots.class);
                startActivity(intent);
            }
        });
        DataTimeLapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, DataTimeLapse.class);
                startActivity(intent);
            }
        });
        LocationCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, LocationCamera.class);
                startActivity(intent);
            }
        });
        SettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SolHomeActivity.this, SetActivity.class);
                startActivity(intent);
            }
        });
    }
}