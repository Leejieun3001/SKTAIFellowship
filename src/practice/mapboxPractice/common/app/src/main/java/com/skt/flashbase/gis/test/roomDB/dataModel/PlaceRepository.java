package com.skt.flashbase.gis.test.roomDB.dataModel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;

import com.skt.flashbase.gis.test.roomDB.Place;
import com.skt.flashbase.gis.test.roomDB.dataModel.local.PlaceDAO;
import com.skt.flashbase.gis.test.roomDB.dataModel.local.PlaceRoomDatabase;

import java.util.List;

public class PlaceRepository {
    private PlaceDAO mPlaceDAO;
    private LiveData<List<Place>> mAllPlaces;
    private LiveData<List<Place>> mAllTourPlaces;
    private LiveData<List<Place>> mAllFoodtruckPlaces;


    public PlaceRepository(Application application) {
        PlaceRoomDatabase db = PlaceRoomDatabase.getDatabase(application);
        //RoomDatabase에 있는 Dao를 가져온다.
        mPlaceDAO = db.placeDAO();
        //Dao의 쿼리를 이용하여 저장되어있는 모든 word를 가져온다.
        mAllPlaces = mPlaceDAO.getAllPlaces();
        mAllTourPlaces = mPlaceDAO.getAllTourPlaces();
        mAllFoodtruckPlaces = mPlaceDAO.getAllFoddTrucks();
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
    //place 추가하는 함수
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
