package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.MessagesTable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Joni on 28.4.2016.
 */
public class LeaveGroupService extends IntentService {

    public LeaveGroupService() {
        super("LeaveGroupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("LeaveGroupService", "started");
            try {
            int groupID = intent.getIntExtra("groupID", -1);
                if(groupID != -1){
                    boolean leavestatus = leaveGroup(groupID);

                    if(leavestatus) {
                        Uri uri = Uri.parse(ChatProvider.GROUPS_CONTENT_URI + "/" + groupID);
                        getContentResolver().delete(uri, null, null);

                        getContentResolver().delete(ChatProvider.MESSAGES_CONTENT_URI, MessagesTable.COLUMN_GROUP_ID + "=" + groupID, null);
                    }else{
                        broadcastToast("Error while leaving group.");
                    }
                 }



            } catch (IOException e) {
                e.printStackTrace();
                broadcastToast("Network not connected.");
            }


        }
        Log.d("LeaveGroupService", "ended");
    }

    private boolean leaveGroup(int groupID) throws IOException {
        URL url = new URL(MenuActivity.SERVER_URI+"Groups/"+groupID+"/leave/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String basicAuth = this.getSharedPreferences(LoginManager.PREF_NAME, 0).getString(LoginManager.KEY_CRED,"");

        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("DELETE");
        // Starts the query
        conn.connect();
        //Log.d("LeaveGroupService", conn.getResponseCode()+" "+conn.getResponseMessage());
        if(conn.getResponseCode() == 200){
            return true;
        }else {
            return false;
        }
    }

    private void broadcastToast(String message) {
        Intent intent = new Intent(this, LoginReceiver.class);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
}