package com.example.smartchatters.Model;

import com.example.smartchatters.View.ChatActivity;

import java.util.HashMap;

public class ChatActivityModel {

    private HashMap<String, ChatActivity.MessageAdapter> messageAdapters;
    private HashMap<String,Integer> indexes;
    private int profilePicsIndex;

    public ChatActivityModel(){
        messageAdapters=new HashMap<>();
        indexes=new HashMap<>();
        profilePicsIndex=0;
    }

    public void setMessageAdapter(String topicName,ChatActivity.MessageAdapter messageAdapter){
        messageAdapters.put(topicName,messageAdapter);
    }

    public ChatActivity.MessageAdapter getMessageAdapter(String topicName){
        return messageAdapters.get(topicName);
    }

    public void setIndex(String topicName,int index){
        indexes.put(topicName,index);
    }

    public int getIndex(String topicName){
        return indexes.get(topicName);
    }

    public int getProfilePicsIndex(){
        return profilePicsIndex;
    }

    public void setProfilePicsIndex(int index){ profilePicsIndex=index; }

}
