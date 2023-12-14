package com.example.myapp;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapp.dto.Message;

import java.util.List;

public class ChatExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;

    private String currentUser;
    private List<Message> listDataHeader; // header titles

    // Constructor
    public ChatExpandableListAdapter(Context context, List<Message> listDataHeader, String currentUser) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.currentUser = currentUser;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // Return the number of children for each group (message)
        // For simple messages, it can be 0 or 1
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // Return child data for each message
        // Modify as per your requirement
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }

        Message currentMessage = (Message) getGroup(groupPosition);
        TextView senderName = convertView.findViewById(R.id.senderName);
        TextView listGroupHeader = convertView.findViewById(R.id.groupHeader);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        LinearLayout messageLayout = convertView.findViewById(R.id.messageLayout);

        RelativeLayout.LayoutParams senderLayoutParams = (RelativeLayout.LayoutParams) senderName.getLayoutParams();
        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) messageLayout.getLayoutParams();

        // Check if the next message exists and has the same time and sender
        if (groupPosition < getGroupCount() - 1) {
            Message nextMessage = (Message) getGroup(groupPosition + 1);
            if (currentMessage.getFormattedTime().equals(nextMessage.getFormattedTime())
                    && currentMessage.getSender().equals(nextMessage.getSender())) {
                // Hide time if the next message has the same time and sender
                textViewTime.setVisibility(View.GONE);
            } else {
                // Show time otherwise
                textViewTime.setVisibility(View.VISIBLE);
            }
        } else {
            // Show time for the last message
            textViewTime.setVisibility(View.VISIBLE);
        }

        // Check if the previous message exists and has the same sender
        if (groupPosition > 0) {
            Message previousMessage = (Message) getGroup(groupPosition - 1);
            if (currentMessage.getSender().equals(previousMessage.getSender())) {
                // Hide sender's name if the same as previous message
                senderName.setVisibility(View.GONE);
            } else {
                // Show sender's name if different
                senderName.setVisibility(View.VISIBLE);
                senderName.setText(currentMessage.getSender());
            }
        } else {
            // Always show the sender's name for the first message
            senderName.setVisibility(View.VISIBLE);
            senderName.setText(currentMessage.getSender());
        }

        // Adjust alignment based on the sender
        if (currentMessage.isSentByCurrentUser(this.currentUser)) {
            // For messages sent by the current user, align to the right
            messageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            senderLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            senderLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            messageLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            senderName.setVisibility(View.GONE);

            messageLayout.setGravity(Gravity.END);
            messageLayout.removeView(textViewTime);
            messageLayout.addView(textViewTime, 0);
            listGroupHeader.setBackgroundResource(R.drawable.message_background_current_user);
        } else {
            // For received messages, align to the left
            messageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            senderLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            senderLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            messageLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);

            messageLayout.setGravity(Gravity.START);
            messageLayout.removeView(listGroupHeader);
            messageLayout.addView(listGroupHeader, 0); /// Message first, then time
            listGroupHeader.setBackgroundResource(R.drawable.message_background_other_user);
        }

        listGroupHeader.setText(currentMessage.getContent());
        listGroupHeader.setText(currentMessage.getContent());
        textViewTime.setText(currentMessage.getFormattedTime());

        RelativeLayout.LayoutParams senderParams = (RelativeLayout.LayoutParams) senderName.getLayoutParams();
        LinearLayout.LayoutParams messageParams = (LinearLayout.LayoutParams) listGroupHeader.getLayoutParams();

        senderName.setLayoutParams(senderParams);
        listGroupHeader.setLayoutParams(messageParams);

        return convertView;
    }




    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
        // Check if the existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.list_item, null);
//        }

//         Get the message object for the current child
//        Message message = (Message) getChild(groupPosition, childPosition);
//
        // Find the TextView in the list_item.xml layout with the ID listItem
//        TextView listItem = convertView.findViewById(R.id.listItem);
//        listItem.setText("Sender: " + message.getSender());
//        listItem.setBackground(context.getResources().getDrawable(R.drawable.message_background));

//        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

