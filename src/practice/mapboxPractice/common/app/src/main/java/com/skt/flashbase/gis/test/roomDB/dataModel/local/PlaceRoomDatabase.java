package com.skt.flashbase.gis.test.roomDB.dataModel.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.skt.flashbase.gis.test.roomDB.Place;


//데이터 베이스 정의 , entities = 사용될 엔티티

// 데이터 변경시 migration 사용
@Database(entities = {Place.class}, version = 1)
public abstract class PlaceRoomDatabase extends RoomDatabase {
//데이터 베이스와 연결되는 DAO


    //DAO는 abstract로 'getter'를 제공

    public abstract PlaceDAO placeDAO();

    //singleton pattern, room database는 한개만 존재
    private static PlaceRoomDatabase INSTANCE;

    public static PlaceRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //synchronized : 동시에 2개의 Instance 가 생성되는 것방지
            synchronized (PlaceRoomDatabase.class) {
                if (INSTANCE == null) {
                    //데이터 베이스 생성
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PlaceRoomDatabase.class, "place_database").build();
                }
            }
        }
        return INSTANCE;
    }

}

