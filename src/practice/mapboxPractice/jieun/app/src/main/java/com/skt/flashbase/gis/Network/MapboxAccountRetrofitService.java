package com.skt.flashbase.gis.Network;

import com.skt.flashbase.gis.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapboxAccountRetrofitService {

    @GET("User/{username}")
    Call<UserResponse> getUserAccount(@Path("username") String userName, @Query("access_token") String accessToken);

}
