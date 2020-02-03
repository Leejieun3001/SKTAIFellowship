package com.skt.flashbase.gis.roomDB.dataModel.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.skt.flashbase.gis.roomDB.Place;

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
    LiveData<List<Place>> getAllFoodTrucks();

    @Query("SELECT * from placeInfo Where category = 3 ORDER BY id ASC")
    LiveData<List<Place>> getAllFishingPlaces();
    @Query("SELECT * from placeInfo Where id = :pidx")
    LiveData<Place> getPlaceInfo(int pidx);
}
