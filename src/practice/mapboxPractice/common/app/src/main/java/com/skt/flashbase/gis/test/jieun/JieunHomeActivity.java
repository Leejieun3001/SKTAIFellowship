package com.skt.flashbase.gis.test.jieun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skt.flashbase.gis.test.MainActivity;
import com.skt.flashbase.gis.test.R;

public class JieunHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jieun_home);
        onClickEvent();
    }
    void onClickEvent() {
        Button mainGoCustomCurrentBtn = (Button) findViewById(R.id.jieunHome__goCustomCurrent_btn);
        mainGoCustomCurrentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JieunHomeActivity.this, CustomCurrentLoc.class);
                startActivity(intent);

            }
        });

        Button mainGoDynamicBtn = (Button) findViewById(R.id.jieunHome_goDynamicBuildMap_btn);
        mainGoDynamicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JieunHomeActivity.this, DynamicallyBuildAMapView.class);
                startActivity(intent);

            }
        });


    }

}
