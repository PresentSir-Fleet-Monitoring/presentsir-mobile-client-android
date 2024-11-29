package com.rdev.bstrack.interfaces;

import com.rdev.bstrack.modals.LoginResponse;
import com.rdev.bstrack.modals.RegisterStepOne;
import com.rdev.bstrack.modals.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("ps/api/auth/login")
    Call<LoginResponse> login(
        @Query("username") String username,
        @Query("password") String password
    );
    @POST("ps/api/auth/register")
    Call<Void> registerUser(@Body RequestBody requestBody);

}
