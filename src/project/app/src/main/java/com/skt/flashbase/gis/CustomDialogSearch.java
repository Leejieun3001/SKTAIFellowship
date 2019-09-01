package com.skt.flashbase.gis;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import static android.content.Context.MODE_PRIVATE;

//jieun
// custom Dialog 생성
public class CustomDialogSearch extends Dialog implements View.OnClickListener {
    private Button searchOKBtn;
    private Button searchNoBtn;
    private CheckBox searchMan10CheckBox;
    private CheckBox searchMan20CheckBox;
    private CheckBox searchMan30CheckBox;
    private CheckBox searchMan40CheckBox;
    private CheckBox searchMan50CheckBox;
    private CheckBox searchMan60CheckBox;
    private CheckBox searchWoman10CheckBox;
    private CheckBox searchWoman20CheckBox;
    private CheckBox searchWoman30CheckBox;
    private CheckBox searchWoman40CheckBox;
    private CheckBox searchWoman50CheckBox;
    private CheckBox searchWoman60CheckBox;


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

        searchMan10CheckBox = (CheckBox) findViewById(R.id.search_man_10_checkbox);
        searchMan20CheckBox = (CheckBox) findViewById(R.id.search_man_20_checkbox);
        searchMan30CheckBox = (CheckBox) findViewById(R.id.search_man_30_checkbox);
        searchMan40CheckBox = (CheckBox) findViewById(R.id.search_man_40_checkbox);
        searchMan50CheckBox = (CheckBox) findViewById(R.id.search_man_50_checkbox);
        searchMan60CheckBox = (CheckBox) findViewById(R.id.search_man_60_checkbox);

        searchWoman10CheckBox = (CheckBox) findViewById(R.id.search_woman_10_checkbox);
        searchWoman20CheckBox = (CheckBox) findViewById(R.id.search_woman_20_checkbox);
        searchWoman30CheckBox = (CheckBox) findViewById(R.id.search_woman_30_checkbox);
        searchWoman40CheckBox = (CheckBox) findViewById(R.id.search_woman_40_checkbox);
        searchWoman50CheckBox = (CheckBox) findViewById(R.id.search_woman_50_checkbox);
        searchWoman60CheckBox = (CheckBox) findViewById(R.id.search_woman_60_checkbox);



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

                if (searchWoman10CheckBox.isChecked()) result += "10대 여성 ";
                if (searchWoman20CheckBox.isChecked()) result += "20대 여성 ";
                if (searchWoman30CheckBox.isChecked()) result += "30대 여성 ";
                if (searchWoman40CheckBox.isChecked()) result += "40대 여성 ";
                if (searchWoman50CheckBox.isChecked()) result += "50대 여성 ";
                if (searchWoman60CheckBox.isChecked()) result += "60대 여성 ";
                if (searchMan10CheckBox.isChecked()) result += "10대 남성 ";
                if (searchMan20CheckBox.isChecked()) result += "20대 남성 ";
                if (searchMan30CheckBox.isChecked()) result += "30대 남성 ";
                if (searchMan40CheckBox.isChecked()) result += "40대 남성 ";
                if (searchMan50CheckBox.isChecked()) result += "50대 남성 ";
                if (searchMan60CheckBox.isChecked()) result += "60대 남성 ";

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

