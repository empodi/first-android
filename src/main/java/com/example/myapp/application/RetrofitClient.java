package com.example.myapp.application;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import com.example.myapp.LoginActivity;
import com.example.myapp.api.ApiService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://13.125.231.234:8080";
    private static final String LOCAL_URL = "http://10.0.2.2:8080";

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .cookieJar(new MyCookieJar())
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);

                        // shared Preference 추가

                        if (response.code() == 401) { // 401 Unauthorized
                            // Session has expired
                            redirectToLogin(context);
                        }

                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(LOCAL_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static void redirectToLogin(Context context) {
        // Create an intent to your LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // Optionally, you can also clear any stored session data here
    }

    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }

    public static ApiService updateHaniService(Context context) {
        return getClient(context).create(ApiService.class);
    }

    public static ApiService getAllHaniItem(Context context) {
        return getClient(context).create(ApiService.class);
    }

    public static ApiService checkDuplicateId(Context context) {
        return getClient(context).create(ApiService.class);
    }
}

