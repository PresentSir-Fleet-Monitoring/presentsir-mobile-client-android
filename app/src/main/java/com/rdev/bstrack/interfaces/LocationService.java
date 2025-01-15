package com.rdev.bstrack.interfaces;

import com.rdev.bstrack.modals.LoginResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LocationService {

    @POST("ps/api/auth/login")
    Call<LoginResponse> sendLocation();
    @POST("ps/api/auth/login")
    Call<LoginResponse> getBusLocation();


}
