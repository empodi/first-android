package com.example.myapp.dto;

public class Message {
    private String content;
    private String sender;
    private boolean isSentByCurrentUser; // Or use userId to determine sender

    public boolean isSentByCurrentUser() {
        return this.isSentByCurrentUser;
    }

    // Constructor, getters, and setters


    public Message(String content, String sender, boolean isSentByCurrentUser) {
        this.content = content;
        this.sender = sender;
        this.isSentByCurrentUser = isSentByCurrentUser;
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
}

