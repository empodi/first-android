package com.example.myapp;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

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

public class RssActivity extends BaseActivity {
    // Your RSS parsing and ListView setup goes here
    private static final String TAG = "mydev";
    ListView listView;
    MyAdapter adapter;
    List<HaniItem> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);
        showUserIdInAppBar();

        listView = findViewById(R.id.result);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        new MyAsyncTask().execute("https://www.hani.co.kr/rss/");

        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Toast.makeText(this, list.get(position)+"clicked", Toast.LENGTH_SHORT).show();

            HaniItem selectedItem = list.get(position);

            Intent detailIntent = new Intent(RssActivity.this, RssDetailActivity.class);
            detailIntent.putExtra("rssItem", selectedItem);
            startActivity(detailIntent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HaniItem selectedItem = list.get(position);
            Intent intent = new Intent(RssActivity.this, ChatActivity.class);
            intent.putExtra("roomId", String.valueOf(selectedItem.getId()));
            startActivity(intent);
            Log.d(TAG, selectedItem.toString());
            return true;
        });

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
            Log.d(TAG, "onPostExecute: "+list.size());
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
                            } else if (name.equalsIgnoreCase("description")) {
                                item.setDescription(parser.nextText());
                            } else if (name.equalsIgnoreCase("pubDate")) {
                                item.setPubDate(new Date(parser.nextText()));
                            } else if("dc".equalsIgnoreCase(parser.getPrefix())){   // namespace
                                if (name.equalsIgnoreCase("subject")) {
                                    item.setSubject(parser.nextText());
                                }else if (name.equalsIgnoreCase("category")) {
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
