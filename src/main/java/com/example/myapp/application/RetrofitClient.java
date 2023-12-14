package com.example.myapp.application;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.myapp.LoginActivity;
import com.example.myapp.api.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static final String TAG = "mydev";

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

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
                            Log.d(TAG, "❗️ Not Logged In");
                            redirectToLogin(context);
                        } else {
                            Log.d(TAG, "Interceptor = " + response.code());
                        }

                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static void redirectToLogin(Context context) {
        // Ensure the context is an Activity context
        if (context instanceof Activity) {
            TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(new Intent(context, LoginActivity.class))
                    .startActivities();

            ((Activity) context).finish();
            Log.d(TAG, "Redirect to Login");
        } else {
            Log.e(TAG, "Provided context is not an Activity context");
        }

        // Optionally, clear any stored session data here
    }


    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}

