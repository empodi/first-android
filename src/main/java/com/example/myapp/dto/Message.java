package com.example.myapp.dto;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    private String content;
    private String sender;
    private boolean isSentByCurrentUser; // Or use userId to determine sender

    private Date time;

    public boolean isSentByCurrentUser(String currentUser) {
        if (this.sender == null || currentUser == null) return false;
        return this.sender.equals(currentUser);
    }

    // Constructor, getters, and setters

    public Message(String content, String sender, boolean isSentByCurrentUser) {
        this.content = content;
        this.sender = sender;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.time = new Date();
    }

    public Message(String content, String sender, Date time) {
        this.content = content;
        this.sender = sender;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(this.time);
    }
}

