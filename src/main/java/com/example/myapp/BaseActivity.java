package com.example.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "mydev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up your Toolbar here
        Toolbar toolbar = findViewById(R.id.toolbar); // Assuming you have a toolbar in your layout
        setSupportActionBar(toolbar);
    }

    protected void showUserIdInAppBar() {
        Toolbar toolbar = findViewById(R.id.toolbar); // Make sure this ID matches the Toolbar's ID in your layout
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            String userId = getUserIdFromPreferences();
            getSupportActionBar().setTitle("Hello, " + userId);
        }
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("userId", "No User ID"); // Default text if userId not found
    }
}
