package com.skt.flashbase.gis.test.roomDB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.skt.flashbase.gis.test.roomDB.dataModel.PlaceRepository;

import java.util.List;

public class PlaceViewModel extends AndroidViewModel {

    private PlaceRepository mPlaceRepository;
    private LiveData<List<Place>> mAllPlace;

    private LiveData<List<Place>> mAllTourPlace;
    private LiveData<List<Place>> mAllFoodtruckPlace;
    private LiveData<Place> mPlaceInfo;

    public PlaceViewModel(Application application) {
        super(application);
        mPlaceRepository = new PlaceRepository(application);
        mAllPlace = mPlaceRepository.getAllPlace();
        mAllTourPlace = mPlaceRepository.getAllTour();
        mAllFoodtruckPlace = mPlaceRepository.getAllFoodtruck();

    }

    public LiveData<List<Place>> getAllPlace() {
        return mAllPlace;
    }

    public LiveData<List<Place>> getAllTourPlace() {
        return mAllTourPlace;
    }

    public LiveData<List<Place>> getAllFoodtruckPlace() {
        return mAllFoodtruckPlace;
    }

    public LiveData<Place> getPlaceInfo(int idx) {
        mPlaceInfo = mPlaceRepository.getPlaceInfo(idx);
        return mPlaceInfo;
    }

    public void insert(Place place) {
        mPlaceRepository.insert(place);
    }
}
