package com.example.joni.basicchatapp.xmlentities;

/**
 * Created by Joni on 28.4.2016.
 */
public class Message {
    private int messageID;
    private int userID;
    private int groupID;
    private String message;
    private String timestamp;
    private String username;

    public Message() {

    }

    public Message(int userID, int groupID, int messageID, String username, String message, String timestamp) {
        this.userID = userID;
        this.groupID = groupID;
        this.messageID = messageID;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
    }

    public void setMessageID(int id) {
        this.messageID = id;
    }
    public int getMessageID() {
        return messageID;
    }

    public void setUserID(int id) {
        this.userID = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setGroupID(int id) {
        this.groupID = id;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
