package com.skt.flashbase.gis.roomDB.dataModel.local;




import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.skt.flashbase.gis.roomDB.Place;

@Database(entities = {Place.class}, version = 1)
public abstract class PlaceRoomDatabase extends RoomDatabase {

    public abstract PlaceDAO placeDAO();

    //singleton pattern
    private static PlaceRoomDatabase INSTANCE;

    public static PlaceRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //synchronized
            synchronized (PlaceRoomDatabase.class) {
                if (INSTANCE == null) {
                    //create DB
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PlaceRoomDatabase.class, "place_database").build();
                }
            }
        }
        return INSTANCE;
    }

}

