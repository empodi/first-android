package com.example.myapp.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    private String content;
    private String sender;
    private boolean isSentByCurrentUser; // Or use userId to determine sender

    private Long time;

    public boolean isSentByCurrentUser() {
        return this.isSentByCurrentUser;
    }

    // Constructor, getters, and setters

    public Message(String content, String sender, boolean isSentByCurrentUser) {
        this.content = content;
        this.sender = sender;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.time = System.currentTimeMillis();
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

    public void setSentByCurrentUser(boolean sentByCurrentUser) {
        isSentByCurrentUser = sentByCurrentUser;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(new Date(this.time));
    }
}

