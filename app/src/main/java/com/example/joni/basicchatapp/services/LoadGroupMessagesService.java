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
import com.example.joni.basicchatapp.UsersTable;
import com.example.joni.basicchatapp.xmlentities.Group;
import com.example.joni.basicchatapp.xmlentities.Message;
import com.example.joni.basicchatapp.xmlentities.User;
import com.example.joni.basicchatapp.xmlparsers.GroupParser;
import com.example.joni.basicchatapp.xmlparsers.MessageParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Joni on 30.4.2016.
 */
public class LoadGroupMessagesService extends IntentService {
    public LoadGroupMessagesService() {
        super("LoadGroupMessagesService");
    }
    private int groupID;
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("LoadMessagesService", "started");

            groupID = intent.getIntExtra("groupID",-1);
            int intentmsgID= intent.getIntExtra("messageID",-1);

            if(groupID != -1) {
                try {
                    int newestmsgID = -1;

                    if(intentmsgID != -1){
                        newestmsgID = intentmsgID;
                    }else {

                        String[] PROJECTION = new String[]{MessagesTable.COLUMN_ID};
                        String SELECTION = MessagesTable.COLUMN_GROUP_ID + "=" + groupID;
                        Cursor c = getContentResolver().query(ChatProvider.MESSAGES_CONTENT_URI, PROJECTION, SELECTION, null, null);

                        if (c != null && c.getCount() > 0) {
                            int largest = 0;

                            while (c.moveToNext()) {
                                int cursormsgid = c.getInt(0);
                                if (cursormsgid > largest) {
                                    largest = cursormsgid;
                                }
                            }
                            newestmsgID = largest;
                            Log.d("LoadMessagesService", "largest was " + largest);
                        }
                        c.close();
                    }

                    InputStream messagestream = loadMessages(newestmsgID);

                    MessageParser parser = new MessageParser();
                    ArrayList<Message> messages = parser.parse(messagestream);

                    for (Message m : messages) {
                        Log.d("LoadMessagesService","received from server "+ m.getMessageID() + "|" + m.getUserID() + "|" + m.getGroupID() + "   " + m.getMessage());
                        ContentValues values = new ContentValues();
                        values.put(MessagesTable.COLUMN_ID, m.getMessageID());
                        values.put(MessagesTable.COLUMN_USER_ID, m.getUserID());
                        values.put(MessagesTable.COLUMN_GROUP_ID, m.getGroupID());
                        values.put(MessagesTable.COLUMN_MESSAGE, m.getMessage());
                        values.put(MessagesTable.COLUMN_TIMESTAMP, Message.formatTimeStamp(m.getTimestamp()));
                        values.put(MessagesTable.COLUMN_NAME, m.getUsername());
                        getContentResolver().insert(ChatProvider.MESSAGES_CONTENT_URI, values);
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

        }
    }


    private InputStream loadMessages(int messageID) throws IOException {
        SharedPreferences pref = this.getSharedPreferences(LoginManager.PREF_NAME, 0);

        URL url = new URL(MenuActivity.SERVER_URI+"Groups/"+groupID+"/messages/"+messageID+"/");
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
