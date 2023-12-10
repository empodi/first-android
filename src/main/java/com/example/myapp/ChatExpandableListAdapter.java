package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapp.dto.Message;

import java.util.List;

public class ChatExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Message> listDataHeader; // header titles

    // Constructor
    public ChatExpandableListAdapter(Context context, List<Message> listDataHeader) {
        this.context = context;
        this.listDataHeader = listDataHeader;
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

        // Check if the previous message exists and has the same sender
        if (groupPosition > 0) {
            Message previousMessage = (Message) getGroup(groupPosition - 1);
            if (currentMessage.getSender().equals(previousMessage.getSender())) {
                // Hide sender's name if the same as previous message
                senderName.setVisibility(View.GONE);
            } else {
                // Show sender's name if different
                senderName.setVisibility(View.VISIBLE);
                senderName.setText("Sender: " + currentMessage.getSender());
            }
        } else {
            // Always show the sender's name for the first message
            senderName.setVisibility(View.VISIBLE);
            senderName.setText("Sender: " + currentMessage.getSender());
        }

        listGroupHeader.setText(currentMessage.getContent());
        listGroupHeader.setText(currentMessage.getContent());

        RelativeLayout.LayoutParams senderParams = (RelativeLayout.LayoutParams) senderName.getLayoutParams();
        RelativeLayout.LayoutParams messageParams = (RelativeLayout.LayoutParams) listGroupHeader.getLayoutParams();

        if (currentMessage.isSentByCurrentUser()) {
            senderParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            messageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            senderParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            messageParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        } else {
            senderParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            messageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            senderParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            messageParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        }

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

