package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.userRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextUserId;
    private EditText editTextPassword;
    private Button buttonCheckId;
    private Button buttonSignUp;
    private boolean isIdChecked = false; // Flag to track ID check status

    public static final String TAG = "mydev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextUserId = findViewById(R.id.editTextUserId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCheckId = findViewById(R.id.buttonCheckId);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        editTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isIdChecked = false;
                Log.d(TAG, "text change. isChecked = " + isIdChecked);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        buttonCheckId.setOnClickListener(view -> {
            checkDuplicateUserId(editTextUserId.getText().toString());
        });

        buttonSignUp.setOnClickListener(view -> {

            if (!validateInput(editTextUserId.getText().toString(), editTextPassword.getText().toString())) {
                return;
            }
            if (isIdChecked) {
                // Proceed with sign up
                ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
                Call<Void> call = apiService.signUpUser(new userRequest(editTextUserId.getText().toString(), editTextPassword.getText().toString()));

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Finish SignUpActivity so user can't go back
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Network error or other issues, show a toast
                        Toast.makeText(SignUpActivity.this, "Sign up error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Inform the user to check the ID first
                Toast.makeText(SignUpActivity.this, "Please check the ID for duplicates first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String userId, String password) {
        Log.d(TAG, "Check Valid Input id = " + userId + "  pwd = " + password);
        if (userId.isEmpty() || userId.length() < 4 || userId.length() > 12) {
            editTextUserId.setError("User ID must be 4-12 characters long");
            editTextUserId.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            editTextPassword.setError("Password must be 4-12 characters long");
            editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void checkDuplicateUserId(String userId) {
        ApiService apiService = RetrofitClient.getApiService(this); // Assuming RetrofitClient is correctly set up
        apiService.checkUserId(userId).enqueue(new Callback<UserIdCheckResponse>() {
            @Override
            public void onResponse(Call<UserIdCheckResponse> call, Response<UserIdCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isDuplicate = response.body().isDuplicate();
                    if (!isDuplicate) {
                        isIdChecked = true;
                        Log.d(TAG, "duplicate checked. isIdChecked = " + isIdChecked);
                        Toast.makeText(SignUpActivity.this, "ID is available.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "ID already exists.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle response error (e.g., 404 or 500 status code)
                }
            }
            @Override
            public void onFailure(Call<UserIdCheckResponse> call, Throwable t) {
                // Handle network error
                Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}


