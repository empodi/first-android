package com.example.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.example.myapp.api.ApiService;
import com.example.myapp.application.RetrofitClient;
import com.example.myapp.dto.Message;
import com.example.myapp.dto.MessageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private Socket mSocket;
    private String roomId;
    private List<Message> messages = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ExpandableListView expandableListViewChat;

    private ChatExpandableListAdapter chatExpandableListAdapter;

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
        chatExpandableListAdapter = new ChatExpandableListAdapter(this, messages, currentUser);
        expandableListViewChat.setAdapter(chatExpandableListAdapter);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the room title passed from the previous activity
        String roomTitle = getIntent().getStringExtra("roomTitle");
        if (roomTitle != null) {
            getSupportActionBar().setTitle(roomTitle);
        }

        fetchAllChatMessages();

        try {
            mSocket = IO.socket("http://13.125.231.234:8080");
//            mSocket = IO.socket("http://10.0.2.2:8080");
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
                    chatExpandableListAdapter.notifyDataSetChanged();
                    expandableListViewChat.setSelection(chatExpandableListAdapter.getGroupCount() - 1);
//                    updateMessagesAndScroll(messages);
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
                        postMessage(roomId, message, currentUser);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Do not inflate the menu for ChatActivity
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar's buttons
        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchAllChatMessages() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getAllChat(roomId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> chatMessages = response.body();
                    messages.clear();
                    messages.addAll(chatMessages);
                    chatExpandableListAdapter.notifyDataSetChanged();
                    expandableListViewChat.setSelection(chatExpandableListAdapter.getGroupCount() - 1);
                } else {
                    // Handle the case where the response is not successful
                    Log.d(TAG, "Failed to fetch chat messages: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                // Handle network failure
                Log.e(TAG, "Network error while fetching chat messages", t);
            }
        });
    }


    private void postMessage(String roomId, String message, String currentUser) {
        MessageRequest messageRequest = new MessageRequest(roomId, message, currentUser);

        // Make the POST request
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.postMessage(messageRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "message Sent" + response.message());
                // Handle successful response
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("messageReceived");
    }
}
