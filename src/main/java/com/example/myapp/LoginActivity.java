package com.example.myapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.LoginRequest;

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

    private void performLogin(String username, String password) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<Void> call = apiService.loginUser(new LoginRequest(username, password));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.d(TAG, "login success");
                } else {
                    // Handle unsuccessful response, such as authentication failure
                    Log.d(TAG, "login fail");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Handle error (like network error)
            }
        });
    }
}

