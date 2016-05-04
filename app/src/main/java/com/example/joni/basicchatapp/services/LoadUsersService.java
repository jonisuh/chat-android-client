package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.UsersTable;
import com.example.joni.basicchatapp.xmlentities.LoginCredentials;
import com.example.joni.basicchatapp.xmlentities.User;
import com.example.joni.basicchatapp.xmlparsers.CredentialsParser;
import com.example.joni.basicchatapp.xmlparsers.UserParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Joni on 28.4.2016.
 */
public class LoadUsersService extends IntentService {

    public LoadUsersService() {
        super("LoadUsersService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("LoadUsersService", "started");
            try {
                InputStream input = loadUsers();

                UserParser parser =  new UserParser();
                ArrayList<User> users = parser.parse(input);

                String[] PROJECTION = new String[]{UsersTable.COLUMN_ID, UsersTable.COLUMN_NAME, UsersTable.COLUMN_FIRSTNAME,UsersTable.COLUMN_LASTNAME,UsersTable.COLUMN_EMAIL,UsersTable.COLUMN_TITLE,UsersTable.COLUMN_DEPARTMENT};
                String SELECTION = "";
                Cursor c = getContentResolver().query(ChatProvider.USERS_CONTENT_URI, PROJECTION, SELECTION, null, null);

                boolean newUsers = false;
                ArrayList<User> currentusers = new ArrayList<>();

                if (c.getCount() != 0){
                    while (c.moveToNext()) {
                        currentusers.add(new User(c.getInt(0), c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6)));
                    }
                    c.close();
                    for(User u : currentusers){
                        if(users.contains(u)){
                            users.remove(u);
                        }else{
                            Uri uri = Uri.parse(ChatProvider.USERS_CONTENT_URI + "/"+ u.getId());
                            getContentResolver().delete(uri,null,null);
                        }
                    }
                }

                for(User u : users){

                    ContentValues values = new ContentValues();
                    values.put(UsersTable.COLUMN_ID, u.getId());
                    values.put(UsersTable.COLUMN_NAME, u.getUsername());
                    values.put(UsersTable.COLUMN_FIRSTNAME, u.getFirstname());
                    values.put(UsersTable.COLUMN_LASTNAME, u.getLastname());
                    values.put(UsersTable.COLUMN_DEPARTMENT, u.getDepartment());
                    values.put(UsersTable.COLUMN_TITLE, u.getTitle());
                    values.put(UsersTable.COLUMN_EMAIL, u.getEmail());
                    getContentResolver().insert(ChatProvider.USERS_CONTENT_URI, values);
                }

            } catch (IOException e) {
                e.printStackTrace();
                broadcastToast("Network not connected.");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                broadcastToast("Error while receiving data from server...");
            }


        }
        Log.d("LoadGroupsService", "ended");
    }

   private InputStream loadUsers() throws IOException {
       URL url = new URL(MenuActivity.SERVER_URI+"Users/");
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();

       String basicAuth = this.getSharedPreferences(LoginManager.PREF_NAME, 0).getString(LoginManager.KEY_CRED,"");

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