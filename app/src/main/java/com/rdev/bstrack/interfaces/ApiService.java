package com.rdev.bstrack.interfaces;

import com.rdev.bstrack.modals.Buses;
import com.rdev.bstrack.modals.RequestBody;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("ps/api/public/buses")
    Call<List<Buses>> getAllBuses();

    @POST("ps/api/auth/update")
    Call<Void> updateUser(@Body RequestBody requestBody);
    @GET("app-config.json")
    Call<ResponseBody> getAppConfig();

}
