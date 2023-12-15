package com.example.myapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.HaniItem;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RssActivity extends BaseActivity {
    // Your RSS parsing and ListView setup goes here
    private static final String TAG = "mydev";
    ListView listView;
    MyAdapter adapter;
    List<HaniItem> list = new ArrayList<>();

    NfcAdapter nfcAdapter;

    PendingIntent pIntent;

    IntentFilter[] filters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showUserIdInAppBar();

        listView = findViewById(R.id.result);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        new MyAsyncTask().execute("https://www.hani.co.kr/rss/");

        listView.setOnItemClickListener((parent, view, position, id) -> {
            HaniItem selectedItem = list.get(position);
            Intent detailIntent = new Intent(RssActivity.this, RssDetailActivity.class);
            detailIntent.putExtra("rssItem", selectedItem); // Ensure selectedItem is Parcelable or Serializable
            startActivity(detailIntent);
        });


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HaniItem selectedItem = list.get(position);
            Intent intent = new Intent(RssActivity.this, ChatActivity.class);
            intent.putExtra("roomId", String.valueOf(selectedItem.getId()));
            intent.putExtra("roomTitle", String.valueOf(selectedItem.getTitle()));
            startActivity(intent);
            Log.d(TAG, selectedItem.toString());
            return true;
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device.", Toast.LENGTH_LONG).show();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
        }
        if (nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is Enabled.", Toast.LENGTH_LONG).show();
        }

        Intent i = new Intent(this, RssActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_MUTABLE);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        filters = new IntentFilter[] {filter,};
    }

    class MyAsyncTask extends AsyncTask<String, String, List<HaniItem>> {

        @Override
        protected List<HaniItem> doInBackground(String... arg) {
            try {
                Log.d(TAG, "connection start....");
                InputStream input = new URL(arg[0]).openConnection().getInputStream();
                Log.d(TAG, "connection ok....");

                parsing(new BufferedReader(new InputStreamReader(input)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }

        //data 조회 완료.
        protected void onPostExecute(List<HaniItem> result) {
            Log.d(TAG, "START onPostExecute: "+list.size());
            postHaniItems(list);
            fetchHani();
            Log.d(TAG, "AFTER onPostExecute: "+list.size());
            adapter.notifyDataSetChanged();
        }

        XmlPullParser parser = Xml.newPullParser();
        private void parsing(Reader reader) throws Exception {
            parser.setInput(reader);
            Log.d(TAG, "parsing: " + parser.getNamespace());
            int eventType = parser.getEventType();
            HaniItem item = null;
            long id = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item")) {
                            item = new HaniItem();
                            item.setId(++id);
                        } else if (item != null) {
                            if (name.equalsIgnoreCase("title")) {
                                item.setTitle(parser.nextText());
                            } else if (name.equalsIgnoreCase("link")) {
                                item.setLink(parser.nextText());
                            } else if (name.equalsIgnoreCase("pubDate")) {
                                item.setPubDate(new Date(parser.nextText()));
                            } else if("dc".equalsIgnoreCase(parser.getPrefix())){   // namespace
                                if (name.equalsIgnoreCase("category")) {
                                    item.setCategory(parser.nextText());
                                }
                            }
//                            else if (name.equalsIgnoreCase("subject")) { // namespace를 무시해도 이슈없음.
//                                item.subject = parser.nextText();
//                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("item") && item != null && item.getLink() != null) {
                            String link = item.getLink();
                            String[] tok = link.split("/");
                            String lastTok = tok[tok.length - 1];
                            String hId = lastTok.split("\\.")[0];
                            item.setId(Long.parseLong(hId));
                            Log.d(TAG, item.toString());
                            list.add(item);
                        }
                        break;
                }
                eventType = parser.next();
            }
        }
    }

    private void postHaniItems(List<HaniItem> haniItems) {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<Void> call = apiService.postHaniItems(haniItems);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.d(TAG, "hani update post successful");
                } else {
                    // Handle failure
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle error (network issue, etc.)
            }
        });
    }

    private void fetchHani() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<HaniItem>> call = apiService.getAllHaniData();
        call.enqueue(new Callback<List<HaniItem>>() {
            @Override
            public void onResponse(Call<List<HaniItem>> call, Response<List<HaniItem>> response) {
                if (response.isSuccessful()) {
                    List<HaniItem> dataList = response.body();
                    Log.d(TAG, "GET req Hani Items. dataList" + dataList.size());
                    list.addAll(dataList);
                    adapter.notifyDataSetChanged();
                    // Handle the received data
                } else {
                    // Handle request failure (e.g., response error)
                }
            }

            @Override
            public void onFailure(Call<List<HaniItem>> call, Throwable t) {

            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(RssActivity.this).inflate(R.layout.rss_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.textViewItemTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HaniItem item = list.get(position);
            holder.title.setText(item.getTitle());

            Log.d(TAG, "from getView. list size = " + list.size());

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();

//        Log.d(TAG, "New Intent action : " + action);

        performLogout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pIntent, filters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
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
        editor.remove("authToken");
        editor.apply();
    }
}
