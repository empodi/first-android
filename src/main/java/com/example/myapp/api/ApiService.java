package com.example.myapp.api;

import com.example.myapp.UserIdCheckResponse;
import com.example.myapp.dto.HaniItem;
import com.example.myapp.dto.Message;
import com.example.myapp.dto.MessageRequest;
import com.example.myapp.dto.userRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("user/login")
    Call<Void> loginUser(@Body userRequest userRequest);

    @POST("user/signup")
    Call<Void> signUpUser(@Body userRequest userRequest);

    @POST("/hani/update") // Replace with your actual endpoint
    Call<Void> postHaniItems(@Body List<HaniItem> haniItems);

    @GET("/hani/all")
    Call<List<HaniItem>> getAllHaniData();

    @GET("/hani/user")
    Call<List<HaniItem>> getHaniItemsByUser(@Query("userId") String userId);

    @GET("/user/check")
    Call<UserIdCheckResponse> checkUserId(@Query("userId") String userId);

    @GET("/chat/all/")
    Call<List<Message>> getAllChat(@Query("haniRoomId") String haniRoomId);

    @POST("/chat/post")
    Call<Void> postMessage(@Body MessageRequest messageRequest);
}
