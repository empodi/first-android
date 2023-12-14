package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID_KEY = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, "");
        String userId = sharedPreferences.getString(USER_ID_KEY, "");

        Button buttonGoToLogin = findViewById(R.id.buttonGoToLogin);
        Button buttonGoToSignUp = findViewById(R.id.buttonGoToSignUp);

        if (!token.isEmpty() && !userId.isEmpty()) {
            // User is logged in, hide the Sign Up button
            buttonGoToSignUp.setVisibility(View.GONE);
        } else {
            // User is not logged in, show the Sign Up button
            buttonGoToSignUp.setVisibility(View.VISIBLE);
        }

        buttonGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        buttonGoToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
