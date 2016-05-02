package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.MessagesTable;
import com.example.joni.basicchatapp.UsersTable;
import com.example.joni.basicchatapp.xmlentities.Message;
import com.example.joni.basicchatapp.xmlentities.User;
import com.example.joni.basicchatapp.xmlparsers.MessageParser;
import com.example.joni.basicchatapp.xmlparsers.UserParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Joni on 1.5.2016.
 */
public class PostMessageService extends IntentService {

    ChatWebSocketService mService;
    boolean mBound = false;

    public PostMessageService() {
        super("PostMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("PostMessageService", "started");

            int id = intent.getIntExtra("groupID",-1);
            String message = intent.getStringExtra("message");

            try {
                Intent wsintent = new Intent(this, ChatWebSocketService.class);
                bindService(wsintent, mConnection, 0);

                InputStream in = postMessage(id,message);

                if(in != null){
                    MessageParser parser = new MessageParser();
                    ArrayList<Message> messages = parser.parse(in);

                    Message m = messages.get(0);
                    ContentValues values = new ContentValues();
                    values.put(MessagesTable.COLUMN_ID, m.getMessageID());
                    values.put(MessagesTable.COLUMN_USER_ID, m.getUserID());
                    values.put(MessagesTable.COLUMN_GROUP_ID, m.getGroupID());
                    values.put(MessagesTable.COLUMN_MESSAGE, m.getMessage());
                    values.put(MessagesTable.COLUMN_TIMESTAMP, Message.formatTimeStamp(m.getTimestamp()));
                    values.put(MessagesTable.COLUMN_NAME, m.getUsername());
                    getContentResolver().insert(ChatProvider.MESSAGES_CONTENT_URI, values);
                    Log.d("PostMessageService", "Message " + m.getMessage() + " added");
                    mService.sendMessage("" + id);

                }else{
                    broadcastToast("Error while sending message");
                }

                unbindService(mConnection);
            } catch (IOException e) {
                broadcastToast("Error while sending message");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                broadcastToast("Error while sending message");
                e.printStackTrace();
            }
            Log.d("PostMessageService", "ended");
        }
    }

    private InputStream postMessage(int groupID, String message) throws IOException {
        SharedPreferences prefs = this.getSharedPreferences(LoginManager.PREF_NAME, 0);
        int id = prefs.getInt(LoginManager.KEY_ID, -1);
        String basicAuth = prefs.getString(LoginManager.KEY_CRED, "");

        URL url = new URL(MenuActivity.SERVER_URI + "Groups/"+groupID+"/users/"+id+"/messages/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        HashMap<String,String> params = new HashMap();
        params.put("message",message);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostParams(params));

        writer.flush();
        writer.close();
        os.close();
        InputStream in = conn.getInputStream();
        Log.d("PostMessageService",conn.getResponseCode()+" "+conn.getResponseMessage());
        if(conn.getResponseCode() == 201){
           return in;
        }else{
            return null;
        }
    }

    private String getPostParams(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private void broadcastToast(String message) {
        Intent intent = new Intent(this, LoginReceiver.class);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ChatWebSocketService.ChatBinder binder = (ChatWebSocketService.ChatBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}