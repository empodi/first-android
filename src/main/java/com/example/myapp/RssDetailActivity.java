package com.example.myapp;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.myapp.dto.HaniItem;

public class RssDetailActivity extends BaseActivity{

    private WebView webViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_detail);
        webViewDetail = findViewById(R.id.webViewDetail);

        TextView textViewDetailTitle = findViewById(R.id.textViewDetailTitle);

        // Retrieve the RSS item details passed from RssActivity
        HaniItem item = (HaniItem) getIntent().getSerializableExtra("rssItem");
        if (item != null && item.getLink() != null) {
            webViewDetail.loadUrl(item.getLink());
            finish();
        }
    }
}


