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

public class AppConfig {

    private List<Buses> buses;

    public void loadConstants() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        // Make a call
        Call<ResponseBody> call = apiService.getAppConfig();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inside onResponse, after jsonObject is parsed
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);

                        if (jsonObject.has("APP_CONFIG_URL"))
                            Constants.setAppConfigUrl(jsonObject.getString("APP_CONFIG_URL"));

                        if (jsonObject.has("SERVER_URL"))
                            Constants.setServerUrl(jsonObject.getString("SERVER_URL"));

                        if (jsonObject.has("WEBSOCKET_URL"))
                            Constants.setWebsocketUrl(jsonObject.getString("WEBSOCKET_URL"));

                        if (jsonObject.has("ONESIGNAL_APP_ID"))
                            Constants.setOnesignalAppId(jsonObject.getString("ONESIGNAL_APP_ID"));

                        if (jsonObject.has("MAP_MY_INDIA_API_KEY"))
                            Constants.setMapMyIndiaApiKey(jsonObject.getString("MAP_MY_INDIA_API_KEY"));

                        if (jsonObject.has("MAP_MY_INDIA_CLIENT_ID"))
                            Constants.setMapMyIndiaClientId(jsonObject.getString("MAP_MY_INDIA_CLIENT_ID"));

                        if (jsonObject.has("MAP_MY_INDIA_CLIENT_SECRET"))
                            Constants.setMapMyIndiaClientSecret(jsonObject.getString("MAP_MY_INDIA_CLIENT_SECRET"));

                        if (jsonObject.has("REMINDER_METER"))
                            Constants.setReminderMeter(jsonObject.getInt("REMINDER_METER"));

                        if (jsonObject.has("buyMeCoffeeButtonVisible"))
                            Constants.setBuyMeCoffeeButtonVisible(jsonObject.getString("buyMeCoffeeButtonVisible"));

                        if (jsonObject.has("developerName"))
                            Constants.setDeveloperName(jsonObject.getString("developerName"));

                        // Role-based share location visibility
                        if (jsonObject.has("shareLocationButtonVisibleToEveryone"))
                            Constants.setShareLocationButtonVisibleToEveryone(jsonObject.getString("shareLocationButtonVisibleToEveryone"));

                        if (jsonObject.has("shareLocationButtonVisibleToUser"))
                            Constants.setShareLocationButtonVisibleToUser(jsonObject.getString("shareLocationButtonVisibleToUser"));

                        if (jsonObject.has("shareLocationButtonVisibleToDriver"))
                            Constants.setShareLocationButtonVisibleToDriver(jsonObject.getString("shareLocationButtonVisibleToDriver"));

                        if (jsonObject.has("shareLocationButtonVisibleToAdmin"))
                            Constants.setShareLocationButtonVisibleToAdmin(jsonObject.getString("shareLocationButtonVisibleToAdmin"));

                        // Role-based change bus visibility
                        if (jsonObject.has("changeBusButtonVisibleToEveryone"))
                            Constants.setChangeBusButtonVisibleToEveryone(jsonObject.getString("changeBusButtonVisibleToEveryone"));

                        if (jsonObject.has("changeBusButtonVisibleToUser"))
                            Constants.setChangeBusButtonVisibleToUser(jsonObject.getString("changeBusButtonVisibleToUser"));

                        if (jsonObject.has("changeBusButtonVisibleToDriver"))
                            Constants.setChangeBusButtonVisibleToDriver(jsonObject.getString("changeBusButtonVisibleToDriver"));

                        if (jsonObject.has("changeBusButtonVisibleToAdmin"))
                            Constants.setChangeBusButtonVisibleToAdmin(jsonObject.getString("changeBusButtonVisibleToAdmin"));

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
