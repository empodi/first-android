package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.HaniItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends BaseActivity {
    private static final String TAG = "mydev";
    ListView listView;
    MyAdapter adapter;
    List<HaniItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history); // Use your actual layout file for HistoryActivity

        Toolbar toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        listView = findViewById(R.id.history_rss); // Replace with your ListView ID
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        fetchHaniItems();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HaniItem selectedItem = list.get(position);
            Intent intent = new Intent(HistoryActivity.this, ChatActivity.class);
            intent.putExtra("roomId", String.valueOf(selectedItem.getId()));
            intent.putExtra("roomTitle", String.valueOf(selectedItem.getTitle()));
            startActivity(intent);
            Log.d(TAG, selectedItem.toString());
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back button action
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchHaniItems() {
        String userId = getUserIdFromPreferences();
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<HaniItem>> call = apiService.getHaniItemsByUser(userId);

        call.enqueue(new Callback<List<HaniItem>>() {
            @Override
            public void onResponse(Call<List<HaniItem>> call, Response<List<HaniItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    for (HaniItem h : list) {
                        Log.d(TAG, h.toString());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<HaniItem>> call, Throwable t) {
                Log.e(TAG, "Network error while fetching data", t);
            }
        });
    }

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.rss_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.textViewItemTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HaniItem item = list.get(position);
            holder.title.setText(item.getTitle());

            return convertView;
        }

        class ViewHolder {
            TextView title;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).getId();
        }
    }
}
