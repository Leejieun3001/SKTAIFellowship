package com.skt.flashbase.gis.test;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skt.flashbase.gis.test.common.HomeActivity;
import com.skt.flashbase.gis.test.jieun.JieunHomeActivity;
import com.skt.flashbase.gis.test.sol.SolHomeActivity;
import com.skt.flashbase.gis.test.sqLite.DBHelper;

public class MainActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickListener();

    }

    void clickListener() {
        Button mainCommonBtn = (Button) findViewById(R.id.main_common_btn);
        mainCommonBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        Button mainJieunBtn = (Button) findViewById(R.id.main_jieun_btn);
        mainJieunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JieunHomeActivity.class);
                startActivity(intent);
            }
        });

        Button mainSolBtn = (Button) findViewById(R.id.main_sol_btn);
        mainSolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SolHomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
