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
import com.example.joni.basicchatapp.UsersTable;
import com.example.joni.basicchatapp.xmlentities.Group;
import com.example.joni.basicchatapp.xmlentities.User;
import com.example.joni.basicchatapp.xmlparsers.GroupParser;
import com.example.joni.basicchatapp.xmlparsers.UserParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.InterruptibleChannel;
import java.util.ArrayList;

/**
 * Created by Joni on 28.4.2016.
 */
public class LoadGroupsService extends IntentService {
    public LoadGroupsService() {
        super("LoadUsersService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("LoadGroupsService", "started");
            try {
                InputStream inputgroups = loadGroups();
                InputStream inputadmined = loadAdmined();

                GroupParser parser =  new GroupParser();
                ArrayList<Group> groups = parser.parse(inputgroups);
                ArrayList<Group> adminedgroups = parser.parse(inputadmined);

                String[] PROJECTION = new String[]{GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME };
                String SELECTION = "";
                Cursor c = getContentResolver().query(ChatProvider.GROUPS_CONTENT_URI, PROJECTION, SELECTION, null, null);

                boolean newGroups = false;
                ArrayList<Group> currentgroups = new ArrayList<>();
                Log.d("LoadGroupsService",""+groups.size());
                for(Group g : groups){
                    Log.d("LoadGroupsService","in group"+ +g.getId());
                }
                if (c.getCount() != 0) {
                    while (c.moveToNext()) {
                        currentgroups.add(new Group(c.getInt(0), c.getString(1)));
                        Log.d("LoadGroupsService", "added group to current groups");
                    }

                    for(Group g : currentgroups){
                        if(groups.contains(g)){
                            Log.d("LoadGroupsService", g.getId()+" removed from new groups");
                            groups.remove(g);
                        }else{
                            Uri uri = Uri.parse(ChatProvider.GROUPS_CONTENT_URI + "/"+ g.getId());
                            getContentResolver().delete(uri,null,null);
                            Log.d("LoadGroupsService", "deleted " + g.getId());
                        }
                    }
                }

                for(Group g : groups){

                    ContentValues values = new ContentValues();
                    values.put(GroupsTable.COLUMN_ID, g.getId());
                    values.put(GroupsTable.COLUMN_NAME, g.getGroupname());
                    getContentResolver().insert(ChatProvider.GROUPS_CONTENT_URI, values);
                    Log.d("LoadGroupsService", "inserted "+g.getId());
                }

                for(Group g : adminedgroups){
                    Log.d("LoadGroupsService",""+g.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
                broadcastToast("Network not connected");
                Log.d("LoadGroupsService", "Network not connected");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                broadcastToast("Error while receiving data from server...");
            }


        }
        Log.d("LoadGroupsService", "ended");
    }

    private InputStream loadAdmined() throws IOException{
        SharedPreferences pref = this.getSharedPreferences(LoginManager.PREF_NAME, 0);
        int myID = pref.getInt(LoginManager.KEY_ID, -1);

        URL url = new URL(MenuActivity.SERVER_URI+"Users/"+myID+"/groups/adminedgroups/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String basicAuth = pref.getString(LoginManager.KEY_CRED, "");

        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Content-Type", "application/xml");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private InputStream loadGroups() throws IOException {
        SharedPreferences pref = this.getSharedPreferences(LoginManager.PREF_NAME, 0);
        int myID = pref.getInt(LoginManager.KEY_ID, -1);

        URL url = new URL(MenuActivity.SERVER_URI+"Users/"+myID+"/groups/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String basicAuth = pref.getString(LoginManager.KEY_CRED,"");

        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Content-Type", "application/xml");

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private void broadcastToast(String message) {
        Intent intent = new Intent(this, LoginReceiver.class);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
}
