package com.skt.flashbase.gis.test.roomDB.dataModel.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.skt.flashbase.gis.test.roomDB.Place;

import java.util.List;

@Dao
public interface PlaceDAO {
    @Insert
    void insert(Place place);

    @Query("DELETE FROM placeInfo")
    void deleteAll();

    @Query("SELECT * from placeInfo ORDER BY id ASC")
    LiveData<List<Place>> getAllPlaces();



    @Query("SELECT * from placeInfo Where category = 1  ORDER BY id ASC ")
    LiveData<List<Place>> getAllTourPlaces();

    @Query("SELECT * from placeInfo Where category = 2 ORDER BY id ASC")
    LiveData<List<Place>> getAllFoddTrucks();
}
