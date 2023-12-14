package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = "mydev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up your Toolbar here
        Toolbar toolbar = findViewById(R.id.toolbar); // Assuming you have a toolbar in your layout
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            showUserIdInAppBar();
        } else {
            Log.e(TAG, "Toolbar not found");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            // Navigate to HistoryActivity
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // Perform logout
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void performLogout() {
        clearUserIdFromPreferences();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.remove("password");
        editor.apply();
    }

}
