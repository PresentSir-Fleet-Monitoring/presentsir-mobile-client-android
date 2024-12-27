package com.rdev.bstrack.helpers;

import android.util.Log;

import com.rdev.bstrack.constants.Constants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//    private static final String BASE_URL = "https://presentsir.up.railway.app/";
//    private static final String BASE_URL = "https://a3fb-2401-4900-1b98-f4a5-11d0-3f85-c50c-4a0.ngrok-free.app/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        HttpUrl originalUrl = originalRequest.url();

                        HttpUrl newUrl = originalUrl
                                .newBuilder()
                                .scheme("https") // Change scheme if necessary
                                .host(Constants.getServerUrl()) // Set the new host
                                .build();

                        Request newRequest = originalRequest
                                .newBuilder()
                                .url(newUrl)
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://4712-106-210-237-147.ngrok-free.app") // Provide an initial base URL (can be dummy)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

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
