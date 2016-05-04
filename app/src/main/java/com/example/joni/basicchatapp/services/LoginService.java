package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.xmlparsers.CredentialsParser;
import com.example.joni.basicchatapp.xmlentities.LoginCredentials;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginService extends IntentService {

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("intentservice", "service started");
            broadcastToast("Authenticating...");
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            Log.d("intentservice", "received "+username+" "+password);

            InputStream input = null;
            try {
                input = sendLogin(username, password);
                if( input != null){
                    CredentialsParser parser = new CredentialsParser();
                    LoginCredentials credentials = parser.parse(input);

                    LoginManager manager = new LoginManager(this);
                    manager.createLoginSession(credentials);

                    Log.d("intentservice", "Logged in, userid: " + credentials.getId() + " authcred: " + credentials.getAuthcredentials());

                    SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
                    boolean b = pref.getBoolean("IsLoggedIn",false);
                    Log.d("intentservice", "" + b);
                    if(b){
                        Intent menuintent = new Intent(this, MenuActivity.class);
                        menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(menuintent);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("intentservice", "connection failed");
                broadcastToast("Network connection failed.");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

        }
    }

    private InputStream sendLogin(String username, String password) throws IOException {
        URL myURL = new URL(MenuActivity.SERVER_URI+"Users/login/");
        HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();

        String userCredentials = username+":"+password;
        String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), 0));

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(10000 /* milliseconds */);

        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Content-Type", "application/xml");

        conn.setDoOutput(true);

       /* DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(null);
        wr.flush();
        wr.close(); */

        int responsecode = conn.getResponseCode();
        if(responsecode == 200){
            return conn.getInputStream();
        }else{
            broadcastToast("Invalid login.");
            Log.d("intentservice","invalid login");
            return null;
        }
    }
    private void broadcastToast(String message){
        Intent intent = new Intent(this, LoginReceiver.class);
        intent.putExtra("message",message);
        sendBroadcast(intent);
    }

}
