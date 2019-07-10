package com.skt.flashbase.gis.test.sqLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Landmark (idx INTEGER PRIMARY KEY  AUTOINCREMENT ,name CHAR(500), lat REAL, lng REAL, category CHAR(300) );";
        db.execSQL(sql);
        Log.d("TABLE", "테이블 생성 완료");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //이미 테이블이 있다면 drop!
        db.execSQL("DROP TABLE Landmark");
        onCreate(db);
    }
}
