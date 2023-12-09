package com.example.myapp.api;

import com.example.myapp.dto.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/login")
    Call<Void> loginUser(@Body LoginRequest loginRequest);
}
