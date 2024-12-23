package com.rdev.bstrack.service;

import android.util.Log;

import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.interfaces.ApiService;
import com.rdev.bstrack.modals.Buses;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppConfig {

    private List<Buses> buses;
    private Constants constants;

    public void loadConstants() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        // Make a call
        Call<ResponseBody> call = apiService.getAppConfig();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Convert ResponseBody to a String
                        String responseBody = response.body().string();

                        // Parse the response body as JSON
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // Update constants from the JSON response
                        if (jsonObject.has("SERVER_URL")) {
//                            Constants.setServerUrl(jsonObject.getString("SERVER_URL"));
                            Log.d("from Constant SERVER_URL :", Constants.getServerUrl());
                        }
                        if (jsonObject.has("DEVELOPER_NAME")) {
                            Constants.setDeveloperName(jsonObject.getString("DEVELOPER_NAME"));
                        }
                        if (jsonObject.has("WEBSOCKET_URL")) {
                            Constants.setWebsocketUrl(jsonObject.getString("WEBSOCKET_URL"));
                        }
                        if (jsonObject.has("ONESIGNAL_APP_ID")) {
                            Constants.setOnesignalAppId(jsonObject.getString("ONESIGNAL_APP_ID"));
                        }
                        if (jsonObject.has("MAP_MY_INDIA_API_KEY")) {
                            Constants.setMapMyIndiaApiKey(jsonObject.getString("MAP_MY_INDIA_API_KEY"));
                        }
                        if (jsonObject.has("MAP_MY_INDIA_CLIENT_ID")) {
                            Constants.setMapMyIndiaClientId(jsonObject.getString("MAP_MY_INDIA_CLIENT_ID"));
                        }
                        if (jsonObject.has("MAP_MY_INDIA_CLIENT_SERCRET")) {
                            Constants.setMapMyIndiaClientSercret(jsonObject.getString("MAP_MY_INDIA_CLIENT_SERCRET"));
                        }
                        if (jsonObject.has("REMINDER_METER")) {
                            Constants.setReminderMeter(jsonObject.getInt("REMINDER_METER"));
                        }
                        if (jsonObject.has("BUY_ME_COFFEE_BUTTON_VISIBLE")) {
                            Constants.setBuyMeCoffeeButtonVisible(jsonObject.getString("BUY_ME_COFFEE_BUTTON_VISIBLE"));
                        }

                        Log.d("API Response", "Constants updated successfully!");
                    } catch (Exception e) {
                        Log.e("API Error", "Error parsing response body", e);
                    }
                } else {
                    Log.e("API Error", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Failure", "Request failed: " + t.getMessage());
            }
        });
    }
}
