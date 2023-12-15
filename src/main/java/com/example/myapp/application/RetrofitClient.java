package com.example.myapp.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapp.MainActivity;
import com.example.myapp.api.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://13.125.231.234:8080";
    public static final String TAG = "mydev";
    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String TOKEN_KEY = "authToken";

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        String authToken = sharedPreferences.getString(TOKEN_KEY, "");

                        if (!authToken.isEmpty()) {
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Authorization", "Bearer " + authToken)
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }

                        return chain.proceed(original);
                    }
                })
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);

                    if (response.code() == 401 || response.code() == 403) { // Unauthorized
                        clearAuthData(context);
                        redirectToMain(context);
                    }

                    return response;
                })
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    private static void redirectToMain(Context context) {
        clearAuthData(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private static void clearAuthData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}
