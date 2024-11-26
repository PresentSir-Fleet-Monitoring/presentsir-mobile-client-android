package com.rdev.bstrack.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.google.gson.Gson;
import com.rdev.bstrack.modals.LoginResponse;

public class SecureStorageHelper {

    private static final String PREF_NAME = "secure_prefs";

    // Save the LoginResponse object securely in EncryptedSharedPreferences
    public static void saveLoginResponse(Context context, LoginResponse response) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Convert the response object to JSON
            Gson gson = new Gson();
            String responseJson = gson.toJson(response);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("login_response", responseJson);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retrieve the LoginResponse object from EncryptedSharedPreferences
    public static LoginResponse getLoginResponse(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String responseJson = sharedPreferences.getString("login_response", null);

            if (responseJson != null) {
                Gson gson = new Gson();
                return gson.fromJson(responseJson, LoginResponse.class);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
