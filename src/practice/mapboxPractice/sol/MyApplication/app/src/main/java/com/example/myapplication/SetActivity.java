package com.example.myapplication;

import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class SetActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    String selectedLng;
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        resultView = (TextView)findViewById(R.id.result_text);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sf = getSharedPreferences("Language",MODE_PRIVATE);
        //text라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String currentLng = sf.getString("Lng","English");
        resultView.setText(currentLng);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Activity가 종료되기 전에 저장한다.
        //SharedPreferences를 sFile이름, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("Language",MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Lng", selectedLng); // key, value를 이용하여 저장하는 형태
        //최종 커밋
        editor.commit();
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                selectedLng = (String)((RadioButton)findViewById(checkedId)).getText();
                Toast.makeText(SetActivity.this, "Selected Language : " + selectedLng, Toast.LENGTH_SHORT).show();
                resultView.setText(selectedLng);
        }
    };
}