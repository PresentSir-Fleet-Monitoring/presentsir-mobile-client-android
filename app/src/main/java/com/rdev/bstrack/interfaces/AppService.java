package com.rdev.bstrack.interfaces;

import com.rdev.bstrack.modals.Buses;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AppService {
    @GET("ps/api/public/buses")
    Call<Buses> getAllBuses();
}
