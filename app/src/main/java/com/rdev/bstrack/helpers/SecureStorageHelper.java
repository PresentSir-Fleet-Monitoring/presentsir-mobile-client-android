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

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("login_response", new Gson().toJson(response));
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
            return responseJson != null ? new Gson().fromJson(responseJson, LoginResponse.class) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to check if the login response is empty
    public static boolean isLoginResponseEmpty(Context context) {
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
            return responseJson == null || responseJson.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Return true as a safe fallback
        }
    }

    // Clear all stored data in SecureStorageHelper
    public static void clearAllData(Context context) {
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

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clear all stored data
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
