package com.example.myapp.application;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import com.example.myapp.LoginActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://3.34.126.10:8080";

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
                    .baseUrl(BASE_URL)
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

}

