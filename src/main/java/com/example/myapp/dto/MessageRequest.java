package com.example.myapp.dto;

public class MessageRequest {
    private String roomId;
    private String content;
    private String sender;

    // Constructor
    public MessageRequest(String roomId, String content, String sender) {
        this.roomId = roomId;
        this.content = content;
        this.sender = sender;
    }
}

