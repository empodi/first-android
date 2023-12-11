package com.example.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.myapp.dto.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends BaseActivity {

    private Socket mSocket;
    private String roomId;
    private List<Message> messages = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ExpandableListView expandableListViewChat;

    private EditText editTextMessage;
    private String currentUser;
    public static final String TAG = "mydev";

    private String getUserIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null); // null is the default value if userId not found
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        showUserIdInAppBar();

        currentUser = getUserIdFromPreferences();

        expandableListViewChat = findViewById(R.id.expandableListViewChat);

        editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);

        roomId = getIntent().getStringExtra("roomId"); // Get roomId from Intent
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
//        MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        ChatExpandableListAdapter expandableListAdapter = new ChatExpandableListAdapter(this, messages);
        expandableListViewChat.setAdapter(expandableListAdapter);


        try {
            mSocket = IO.socket("http://3.34.126.10:8080"); // Replace with your server URL and port
            mSocket.connect();

            mSocket.emit("joinRoom", new JSONObject().put("roomId", roomId));

            mSocket.on("messageReceived", args -> {
                JSONObject data = (JSONObject) args[0];
                // Extract message from data and add to messages list
                String messageContent, sender, roomId;
                try {
                    messageContent = data.getString("message");
                    sender = data.getString("sender");
                    roomId = data.getString("roomId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // Determine if the message is sent or received

                Log.d(TAG, "Message Received = " + data.toString());

                boolean isSentByCurrentUser = sender.equals(currentUser);

                Message message = new Message(messageContent, sender, isSentByCurrentUser);

//                Message message = new Message(messageContent, );
                runOnUiThread(() -> {
                    messages.add(message);
                    expandableListAdapter.notifyDataSetChanged();
                });
            });

            buttonSend.setOnClickListener(v -> {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    try {
                        mSocket.emit("messageSent",
                                new JSONObject()
                                        .put("roomId", roomId)
                                        .put("message", message)
                                        .put("sender", currentUser));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    editTextMessage.setText("");
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("messageReceived");
    }
}
