package com.skt.flashbase.gis.test.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplachActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Thread.sleep(2500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}
