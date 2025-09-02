package com.rdev.bstrack.service;

import android.util.Log;

import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.interfaces.ApiService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppConfig {

    private static final String TAG = "AppConfig";

    public void loadConstants() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        apiService.getAppConfig().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // Use safe get with defaults
                        Constants.setAppConfigUrl(jsonObject.optString("APP_CONFIG_URL", Constants.APP_CONFIG_URL));
                        Constants.setServerUrl(jsonObject.optString("SERVER_URL", Constants.SERVER_URL));
                        Constants.setWebsocketUrl(jsonObject.optString("WEBSOCKET_URL", Constants.WEBSOCKET_URL));
                        Constants.setOnesignalAppId(jsonObject.optString("ONESIGNAL_APP_ID", Constants.ONESIGNAL_APP_ID));
                        Constants.setMapMyIndiaApiKey(jsonObject.optString("MAP_MY_INDIA_API_KEY", Constants.MAP_MY_INDIA_API_KEY));
                        Constants.setMapMyIndiaClientId(jsonObject.optString("MAP_MY_INDIA_CLIENT_ID", Constants.MAP_MY_INDIA_CLIENT_ID));
                        Constants.setMapMyIndiaClientSecret(jsonObject.optString("MAP_MY_INDIA_CLIENT_SECRET", Constants.MAP_MY_INDIA_CLIENT_SECRET));
                        Constants.setReminderMeter(jsonObject.optInt("REMINDER_METER", Constants.REMINDER_METER));

                        // UI configs
                        Constants.setBuyMeCoffeeButtonVisible(jsonObject.optString("buyMeCoffeeButtonVisible", Constants.buyMeCoffeeButtonVisible));
                        Constants.setDeveloperName(jsonObject.optString("developerName", Constants.developerName));

                        // Role-based share location visibility
                        Constants.setShareLocationButtonVisibleToEveryone(jsonObject.optString("shareLocationButtonVisibleToEveryone", Constants.shareLocationButtonVisibleToEveryone));
                        Constants.setShareLocationButtonVisibleToUser(jsonObject.optString("shareLocationButtonVisibleToUser", Constants.shareLocationButtonVisibleToUser));
                        Constants.setShareLocationButtonVisibleToDriver(jsonObject.optString("shareLocationButtonVisibleToDriver", Constants.shareLocationButtonVisibleToDriver));
                        Constants.setShareLocationButtonVisibleToAdmin(jsonObject.optString("shareLocationButtonVisibleToAdmin", Constants.shareLocationButtonVisibleToAdmin));

                        // Role-based change bus visibility
                        Constants.setChangeBusButtonVisibleToEveryone(jsonObject.optString("changeBusButtonVisibleToEveryone", Constants.changeBusButtonVisibleToEveryone));
                        Constants.setChangeBusButtonVisibleToUser(jsonObject.optString("changeBusButtonVisibleToUser", Constants.changeBusButtonVisibleToUser));
                        Constants.setChangeBusButtonVisibleToDriver(jsonObject.optString("changeBusButtonVisibleToDriver", Constants.changeBusButtonVisibleToDriver));
                        Constants.setChangeBusButtonVisibleToAdmin(jsonObject.optString("changeBusButtonVisibleToAdmin", Constants.changeBusButtonVisibleToAdmin));

                        Log.d(TAG, "Constants updated successfully!");

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response body", e);
                    }
                } else {
                    Log.e(TAG, "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage());
            }
        });
    }
}
