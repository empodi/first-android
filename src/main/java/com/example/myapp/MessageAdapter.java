package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapp.dto.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);
        assert message != null;
        if (message.isSentByCurrentUser()) { // You need a method to check if the message was sent by the current user
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Two types of views, one for sent messages, one for received
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == VIEW_TYPE_SENT) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_sent, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_received, parent, false);
            }
        }

        Message message = getItem(position);
        TextView textView = convertView.findViewById(R.id.textMessage);
        assert message != null;
        textView.setText(message.getContent());

        return convertView;
    }
}

