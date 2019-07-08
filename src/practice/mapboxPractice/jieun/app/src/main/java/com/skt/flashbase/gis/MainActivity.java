package com.skt.flashbase.gis;

import android.content.Intent;
import android.support.design.shape.CutCornerTreatment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skt.flashbase.gis.Currentlocation.CustomCurrentLoc;
import com.skt.flashbase.gis.DynamicBuildMap.DynamicallyBuildAMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onClickEvent();
    }

    void onClickEvent() {
        Button mainGoCustomCurrentBtn = (Button) findViewById(R.id.main_goCustomCurrent_btn);
        mainGoCustomCurrentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomCurrentLoc.class);
                startActivity(intent);

            }
        });

        Button mainGoDynamicBtn = (Button) findViewById(R.id.main_goDynamicBuildMap_btn);
        mainGoDynamicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DynamicallyBuildAMapView.class);
                startActivity(intent);

            }
        });


    }

}
