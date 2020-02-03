package com.skt.flashbase.gis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

//jieun
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
    private TextView searchchoseWholeBtn;
    private TextView searchcancelWholeBtn;
    private SharedPreferences pref = getContext().getSharedPreferences("ChoseSex_Age", Activity.MODE_PRIVATE);

    private Context context;

    private CustomDialogSearchListener customDialogSearchListener;

    public CustomDialogSearch(Context context) {
        super(context);
        this.context = context;
    }

    //set interface
    interface CustomDialogSearchListener {
        void onPositiveClicked(String result);

        void onNegativeClicked();
    }

    //init listener
    public void setDialogListener(CustomDialogSearchListener customDialogSearchListener) {
        this.customDialogSearchListener = customDialogSearchListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        searchchoseWholeBtn = (TextView) findViewById(R.id.search_choseWhole_textView);
        searchcancelWholeBtn = (TextView) findViewById(R.id.search_cancelWhole_textView);
        boolean Man10 = pref.getBoolean("Man10", false);
        searchMan10CheckBox.setChecked(Man10);
        boolean Man20 = pref.getBoolean("Man20", false);
        searchMan20CheckBox.setChecked(Man20);
        boolean Man30 = pref.getBoolean("Man30", false);
        searchMan30CheckBox.setChecked(Man30);
        boolean Man40 = pref.getBoolean("Man40", false);
        searchMan40CheckBox.setChecked(Man40);
        boolean Man50 = pref.getBoolean("Man50", false);
        searchMan50CheckBox.setChecked(Man50);
        boolean Man60 = pref.getBoolean("Man60", false);
        searchMan60CheckBox.setChecked(Man60);

        boolean Woman10 = pref.getBoolean("Woman10", false);
        searchWoman10CheckBox.setChecked(Woman10);
        boolean Woman20 = pref.getBoolean("Woman20", false);
        searchWoman20CheckBox.setChecked(Woman20);
        boolean Woman30 = pref.getBoolean("Woman30", false);
        searchWoman30CheckBox.setChecked(Woman30);
        boolean Woman40 = pref.getBoolean("Woman40", false);
        searchWoman40CheckBox.setChecked(Woman40);
        boolean Woman50 = pref.getBoolean("Woman50", false);
        searchWoman50CheckBox.setChecked(Woman50);
        boolean Woman60 = pref.getBoolean("Woman60", false);
        searchWoman60CheckBox.setChecked(Woman60);


        //add button Listener
        searchOKBtn.setOnClickListener(this);
        searchNoBtn.setOnClickListener(this);
        searchchoseWholeBtn.setOnClickListener(this);
        searchcancelWholeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_ok_btn:
                String result = "";
                SharedPreferences.Editor editor = pref.edit();

                if (searchWoman10CheckBox.isChecked()) {
                    result += "10대 여성 ";
                    editor.putBoolean("Woman10", true);
                } else {
                    editor.putBoolean("Woman10", false);
                }
                if (searchWoman20CheckBox.isChecked()) {
                    result += "20대 여성 ";
                    editor.putBoolean("Woman20", true);
                } else {
                    editor.putBoolean("Woman20", false);
                }
                if (searchWoman30CheckBox.isChecked()) {
                    result += "30대 여성 ";
                    editor.putBoolean("Woman30", true);
                } else {
                    editor.putBoolean("Woman30", false);
                }
                if (searchWoman40CheckBox.isChecked()) {
                    result += "40대 여성 ";
                    editor.putBoolean("Woman40", true);
                } else {
                    editor.putBoolean("Woman40", false);
                }
                if (searchWoman50CheckBox.isChecked()) {
                    result += "50대 여성 ";
                    editor.putBoolean("Woman50", true);
                } else {
                    editor.putBoolean("Woman50", false);
                }
                if (searchWoman60CheckBox.isChecked()) {
                    result += "60대 여성 ";
                    editor.putBoolean("Woman60", true);
                } else {
                    editor.putBoolean("Woman60", false);
                }
                if (searchMan10CheckBox.isChecked()) {
                    result += "10대 남성 ";
                    editor.putBoolean("Man10", true);
                } else {
                    editor.putBoolean("Man10", false);
                }
                if (searchMan20CheckBox.isChecked()) {
                    result += "20대 남성 ";
                    editor.putBoolean("Man20", true);
                } else {
                    editor.putBoolean("Man20", false);
                }
                if (searchMan30CheckBox.isChecked()) {
                    result += "30대 남성 ";
                    editor.putBoolean("Man30", true);
                } else {
                    editor.putBoolean("Man30", false);
                }
                if (searchMan40CheckBox.isChecked()) {
                    result += "40대 남성 ";
                    editor.putBoolean("Man40", true);
                } else {
                    editor.putBoolean("Man40", false);
                }
                if (searchMan50CheckBox.isChecked()) {
                    result += "50대 남성 ";
                    editor.putBoolean("Man50", true);
                } else {
                    editor.putBoolean("Man50", false);
                }
                if (searchMan60CheckBox.isChecked()) {
                    result += "60대 남성 ";
                    editor.putBoolean("Man60", true);
                } else {
                    editor.putBoolean("Man60", false);
                }
                editor.commit();

                customDialogSearchListener.onPositiveClicked(result);
                dismiss();
                break;
            case R.id.search_cancel_btn:
                customDialogSearchListener.onNegativeClicked();
                cancel();
                break;
            case R.id.search_choseWhole_textView:
                searchWoman10CheckBox.setChecked(true);
                searchWoman20CheckBox.setChecked(true);
                searchWoman30CheckBox.setChecked(true);
                searchWoman40CheckBox.setChecked(true);
                searchWoman50CheckBox.setChecked(true);
                searchWoman60CheckBox.setChecked(true);
                searchMan10CheckBox.setChecked(true);
                searchMan20CheckBox.setChecked(true);
                searchMan30CheckBox.setChecked(true);
                searchMan40CheckBox.setChecked(true);
                searchMan50CheckBox.setChecked(true);
                searchMan60CheckBox.setChecked(true);
                storeData();
                break;
            case R.id.search_cancelWhole_textView:
                searchWoman10CheckBox.setChecked(false);
                searchWoman20CheckBox.setChecked(false);
                searchWoman30CheckBox.setChecked(false);
                searchWoman40CheckBox.setChecked(false);
                searchWoman50CheckBox.setChecked(false);
                searchWoman60CheckBox.setChecked(false);
                searchMan10CheckBox.setChecked(false);
                searchMan20CheckBox.setChecked(false);
                searchMan30CheckBox.setChecked(false);
                searchMan40CheckBox.setChecked(false);
                searchMan50CheckBox.setChecked(false);
                searchMan60CheckBox.setChecked(false);
                storeData();
                break;

        }
    }

    public void storeData() {
        SharedPreferences.Editor editor = pref.edit();
        String result = "";
        if (searchWoman10CheckBox.isChecked()) {
            result += "10대 여성 ";
            editor.putBoolean("Woman10", true);
        } else {
            editor.putBoolean("Woman10", false);
        }
        if (searchWoman20CheckBox.isChecked()) {
            result += "20대 여성 ";
            editor.putBoolean("Woman20", true);
        } else {
            editor.putBoolean("Woman20", false);
        }
        if (searchWoman30CheckBox.isChecked()) {
            result += "30대 여성 ";
            editor.putBoolean("Woman30", true);
        } else {
            editor.putBoolean("Woman30", false);
        }
        if (searchWoman40CheckBox.isChecked()) {
            result += "40대 여성 ";
            editor.putBoolean("Woman40", true);
        } else {
            editor.putBoolean("Woman40", false);
        }

        if (searchWoman50CheckBox.isChecked()) {
            result += "50대 여성 ";
            editor.putBoolean("Woman50", true);
        } else {
            editor.putBoolean("Woman50", false);
        }
        if (searchWoman60CheckBox.isChecked()) {
            result += "60대 여성 ";
            editor.putBoolean("Woman60", true);
        } else {
            editor.putBoolean("Woman60", false);
        }
        if (searchMan10CheckBox.isChecked()) {
            result += "10대 남성 ";
            editor.putBoolean("Man10", true);
        } else {
            editor.putBoolean("Man10", false);
        }
        if (searchMan20CheckBox.isChecked()) {
            result += "20대 남성 ";
            editor.putBoolean("Man20", true);
        } else {
            editor.putBoolean("Man20", false);

        }
        if (searchMan30CheckBox.isChecked()) {
            result += "30대 남성 ";
            editor.putBoolean("Man30", true);
        } else {
            editor.putBoolean("Man30", false);

        }
        if (searchMan40CheckBox.isChecked()) {
            result += "40대 남성 ";
            editor.putBoolean("Man40", true);
        } else {
            editor.putBoolean("Man40", false);

        }
        if (searchMan50CheckBox.isChecked()) {
            result += "50대 남성 ";
            editor.putBoolean("Man50", true);
        } else {
            editor.putBoolean("Man50", false);

        }
        if (searchMan60CheckBox.isChecked()) {
            result += "60대 남성 ";
            editor.putBoolean("Man60", true);
        } else {
            editor.putBoolean("Man60", false);

        }
        editor.commit();

    }
}

