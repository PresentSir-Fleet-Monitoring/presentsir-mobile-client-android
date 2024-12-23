package com.rdev.bstrack.helpers;

import android.util.Log;

import com.rdev.bstrack.constants.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//    private static final String BASE_URL = "https://presentsir.up.railway.app/";
//    private static final String BASE_URL = "https://a3fb-2401-4900-1b98-f4a5-11d0-3f85-c50c-4a0.ngrok-free.app/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        Log.d("API CLIENT ", Constants.getServerUrl());

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(Constants.getServerUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }


    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.getAppConfigUrl()) // Use the github gits from Constants
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
