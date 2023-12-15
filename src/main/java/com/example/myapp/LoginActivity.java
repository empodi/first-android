package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.userRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    public static final String TAG = "mydev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isTokenAvailable()) {
            navigateToRssActivity();
            return;
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            performLogin(username, password);
        });
    }

    private void performLogin(String username, String password) {

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(new userRequest(username, password));
        call.enqueue(new Callback<ResponseBody>() { // Change Void to ResponseBody or your token response class
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        // Assuming the response is a JSON object with a field named "token"
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String token = jsonObject.getString("token");
                        saveInfo(token, username);
                        navigateToRssActivity();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Handle error
            }
        });
    }

    private void saveInfo(String token, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", username);
        editor.putString("authToken", token);
        editor.apply();
    }

    private boolean isTokenAvailable() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.contains("authToken");
    }

    private void navigateToRssActivity() {
        Intent intent = new Intent(LoginActivity.this, RssActivity.class);
        startActivity(intent);
        finish();
    }
}

