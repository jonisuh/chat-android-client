package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.GroupsTable;
import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.MessagesTable;
import com.example.joni.basicchatapp.xmlentities.Group;
import com.example.joni.basicchatapp.xmlentities.Message;
import com.example.joni.basicchatapp.xmlparsers.GroupParser;

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
 * Created by Joni on 3.5.2016.
 */
public class CreateGroupService extends IntentService {
    public CreateGroupService() {
        super("CreateGroupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("CreateGroupService", "started");
            try {
                int groupStarterID = this.getSharedPreferences(LoginManager.PREF_NAME, 0).getInt(LoginManager.KEY_ID, -1);
                String groupname = intent.getStringExtra("groupname");
                int[] memberIDs = intent.getIntArrayExtra("members");

                int groupID = createGroup(groupStarterID, groupname);
                if(groupID != -1 ){
                    Log.d("CreateGroupService", "group created");
                    for(int i : memberIDs){
                        boolean success = addMember(i,groupID);
                        if(success){
                            Log.d("CreateGroupService", "member added");
                        }
                    }

                        ContentValues values = new ContentValues();
                        values.put(GroupsTable.COLUMN_ID, groupID);
                        values.put(GroupsTable.COLUMN_NAME, groupname);
                        getContentResolver().insert(ChatProvider.GROUPS_CONTENT_URI, values);

                }


            } catch (IOException e) {
                e.printStackTrace();
                broadcastToast("Network not connected");
                Log.d("LoadGroupsService", "Network not connected");
            }

        }
        Log.d("CreateGroupService", "ended");
    }

    private int createGroup(int starterID, String groupname) throws IOException{
        SharedPreferences prefs = this.getSharedPreferences(LoginManager.PREF_NAME, 0);
        String basicAuth = prefs.getString(LoginManager.KEY_CRED, "");

        URL url = new URL(MenuActivity.SERVER_URI + "Groups/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        HashMap<String,String> params = new HashMap();
        params.put("groupname",groupname);
        params.put("groupstarterID", ""+starterID);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostParams(params));

        writer.flush();
        writer.close();
        os.close();
        InputStream in = conn.getInputStream();
        Log.d("CreateGroupService",conn.getResponseCode()+" "+conn.getResponseMessage());

        if(conn.getResponseCode() == 201){
            return Integer.parseInt(convertStreamToString(in));
        }else{
            return -1;
        }
    }
    private boolean addMember(int memberID, int groupID) throws IOException{
        SharedPreferences prefs = this.getSharedPreferences(LoginManager.PREF_NAME, 0);
        String basicAuth = prefs.getString(LoginManager.KEY_CRED, "");

        URL url = new URL(MenuActivity.SERVER_URI + "Groups/"+groupID+"/users/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        HashMap<String,String> params = new HashMap();
        params.put("userID",""+memberID);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostParams(params));

        writer.flush();
        writer.close();
        os.close();
        InputStream in = conn.getInputStream();
        Log.d("CreateGroupService",conn.getResponseCode()+" "+conn.getResponseMessage());

        if(conn.getResponseCode() == 200){
            return true;
        }else{
            return false;
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

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void broadcastToast(String message) {
        Intent intent = new Intent(this, LoginReceiver.class);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
}