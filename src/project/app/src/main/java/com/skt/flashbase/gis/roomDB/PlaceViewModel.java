package com.skt.flashbase.gis.roomDB;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.skt.flashbase.gis.roomDB.dataModel.PlaceRepository;

import java.util.List;

public class PlaceViewModel extends AndroidViewModel {

    private PlaceRepository mPlaceRepository;
    private LiveData<List<Place>> mAllPlace;

    private LiveData<List<Place>> mAllTourPlace;
    private LiveData<List<Place>> mAllFoodtruckPlace;
    private LiveData<List<Place>> mAllFishingPlace;
    private LiveData<Place> mPlaceInfo;

    public PlaceViewModel(Application application) {
        super(application);
        mPlaceRepository = new PlaceRepository(application);
        mAllPlace = mPlaceRepository.getAllPlace();
        mAllTourPlace = mPlaceRepository.getAllTour();
        mAllFoodtruckPlace = mPlaceRepository.getAllFoodtruck();
        mAllFishingPlace = mPlaceRepository.getAllFishing();

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

    public LiveData<List<Place>> getAllFishingPlace() { return mAllFishingPlace; }

    public LiveData<Place> getPlaceInfo(int idx) {
        mPlaceInfo = mPlaceRepository.getPlaceInfo(idx);
        return mPlaceInfo;
    }

    public void insert(Place place) {
        mPlaceRepository.insert(place);
    }
}
