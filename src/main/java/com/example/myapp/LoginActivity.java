package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.userRequest;

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

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            performLogin(username, password);
        });
    }

    private void saveUserIdToPreferences(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }


    private void performLogin(String username, String password) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<Void> call = apiService.loginUser(new userRequest(username, password));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.d(TAG, "login success: " + username);
                    // Inside LoginActivity, when login is successful
                    saveUserIdToPreferences(username);
                    Intent intent = new Intent(LoginActivity.this, RssActivity.class);
                    startActivity(intent);
                    finish(); // To remove LoginActivity from the back stack
                } else {
                    // Handle unsuccessful response, such as authentication failure
                    Log.d(TAG, "login fail: " + username);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Handle error (like network error)
            }
        });
    }
}

