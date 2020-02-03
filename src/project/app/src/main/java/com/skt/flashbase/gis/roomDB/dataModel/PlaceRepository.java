package com.skt.flashbase.gis.roomDB.dataModel;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.skt.flashbase.gis.roomDB.Place;
import com.skt.flashbase.gis.roomDB.dataModel.local.PlaceDAO;
import com.skt.flashbase.gis.roomDB.dataModel.local.PlaceRoomDatabase;

import java.util.List;

public class PlaceRepository {
    private PlaceDAO mPlaceDAO;
    private LiveData<List<Place>> mAllPlaces;
    private LiveData<List<Place>> mAllTourPlaces;
    private LiveData<List<Place>> mAllFoodtruckPlaces;
    private LiveData<List<Place>> mAllFishingPlaces;

    private LiveData<Place> mPlaceInfo;


    public PlaceRepository(Application application) {
        PlaceRoomDatabase db = PlaceRoomDatabase.getDatabase(application);
        mPlaceDAO = db.placeDAO();
        mAllPlaces = mPlaceDAO.getAllPlaces();
        mAllTourPlaces = mPlaceDAO.getAllTourPlaces();
        mAllFoodtruckPlaces = mPlaceDAO.getAllFoodTrucks();
        mAllFishingPlaces = mPlaceDAO.getAllFishingPlaces();

    }

    public LiveData<List<Place>> getAllPlace() {
        return mAllPlaces;
    }
    public LiveData<List<Place>> getAllTour() {
        return mAllTourPlaces;
    }
    public LiveData<List<Place>> getAllFoodtruck() {
        return mAllFoodtruckPlaces;
    }
    public LiveData<List<Place>> getAllFishing() {
        return mAllFishingPlaces;
    }

    public LiveData<Place> getPlaceInfo(int pidx) {
        mPlaceInfo = mPlaceDAO.getPlaceInfo(pidx);
        return mPlaceInfo;
    }


    public void insert(Place place) {
        new insertAsyncTaske(mPlaceDAO).execute(place);
    }

    private static class insertAsyncTaske extends AsyncTask<Place, Void, Void> {
        private PlaceDAO mAsyncTaskDao;


        insertAsyncTaske(PlaceDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Place... places) {
            mAsyncTaskDao.insert(places[0]);
            return null;
        }

    }
}
