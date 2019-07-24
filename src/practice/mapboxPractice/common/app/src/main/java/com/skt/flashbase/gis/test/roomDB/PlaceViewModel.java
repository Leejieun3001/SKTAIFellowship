package com.skt.flashbase.gis.test.roomDB;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.skt.flashbase.gis.test.roomDB.dataModel.PlaceRepository;

import java.util.List;

public class PlaceViewModel extends AndroidViewModel {

    private PlaceRepository mPlaceRepository;
    private LiveData<List<Place>> mAllPlace;

    public PlaceViewModel(Application application) {
        super(application);
        mPlaceRepository = new PlaceRepository(application);
        mAllPlace = mPlaceRepository.getAllPlace();
    }

   public LiveData<List<Place>> getAllPlace() {
        return mAllPlace;
    }

    public void insert(Place place) {
        mPlaceRepository.insert(place);
    }
}
