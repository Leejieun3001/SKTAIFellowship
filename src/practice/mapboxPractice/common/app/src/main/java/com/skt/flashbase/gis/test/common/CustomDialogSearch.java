package com.skt.flashbase.gis.test.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.skt.flashbase.gis.test.R;

//jieun
// custom Dialog 생성
public class CustomDialogSearch extends Dialog implements View.OnClickListener {
    private Button searchOKBtn;
    private Button searchNoBtn;
    private CheckBox search10CheckBox;
    private CheckBox search20CheckBox;
    private CheckBox search30CheckBox;
    private CheckBox search40CheckBox;
    private CheckBox search50CheckBox;
    private CheckBox search60CheckBox;
    private CheckBox searchWomanCheckBox;
    private CheckBox searchManCheckBox;
    private Context context;

    private CustomDialogSearchListener customDialogSearchListener;

    public CustomDialogSearch(Context context) {
        super(context);
        this.context = context;
    }

    //set interface (클릭 이벤트 전달)
    interface CustomDialogSearchListener {
        void onPositiveClicked(String result);

        void onNegativeClicked();
    }

    //init listener (값을 전달하기 위해 선언)
    public void setDialogListener(CustomDialogSearchListener customDialogSearchListener) {
        this.customDialogSearchListener = customDialogSearchListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다이얼 로그 밖의 화면은 흐리게
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.custom_dialog_search);
        //init
        searchOKBtn = (Button) findViewById(R.id.search_ok_btn);
        searchNoBtn = (Button) findViewById(R.id.search_cancel_btn);

        searchWomanCheckBox = (CheckBox) findViewById(R.id.search_woman_checkbox);
        searchManCheckBox = (CheckBox) findViewById(R.id.search_man_checkbox);
        search10CheckBox = (CheckBox) findViewById(R.id.search_10_checkbox);
        search20CheckBox = (CheckBox) findViewById(R.id.search_20_checkbox);
        search30CheckBox = (CheckBox) findViewById(R.id.search_30_checkbox);
        search40CheckBox = (CheckBox) findViewById(R.id.search_40_checkbox);
        search50CheckBox = (CheckBox) findViewById(R.id.search_50_checkbox);
        search60CheckBox = (CheckBox) findViewById(R.id.search_60_checkbox);

        //add button Listener
        searchOKBtn.setOnClickListener(this);
        searchNoBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_ok_btn:
                //각각 check box 값 저장
                String result = "";
                if (searchWomanCheckBox.isChecked()) result += "여성 ";
                if (searchManCheckBox.isChecked()) result += "남성 ";
                if (search10CheckBox.isChecked()) result += "10대 ";
                if (search20CheckBox.isChecked()) result += "20대 ";
                if (search30CheckBox.isChecked()) result += "30대 ";
                if (search40CheckBox.isChecked()) result += "40대 ";
                if (search50CheckBox.isChecked()) result += "50대 ";
                if (search60CheckBox.isChecked()) result += "60대 ";

                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                customDialogSearchListener.onPositiveClicked(result);
                dismiss();
                break;
            case R.id.search_cancel_btn:
                customDialogSearchListener.onNegativeClicked();
                cancel();
                break;
        }
    }
}

