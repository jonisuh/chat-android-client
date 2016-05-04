package com.example.joni.basicchatapp.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import com.example.joni.basicchatapp.ChatProvider;
import com.example.joni.basicchatapp.LoginManager;
import com.example.joni.basicchatapp.MenuActivity;
import com.example.joni.basicchatapp.MessagesTable;
import com.example.joni.basicchatapp.R;
import com.example.joni.basicchatapp.xmlentities.Message;
import com.example.joni.basicchatapp.xmlparsers.MessageParser;

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

/**
 * Created by Joni on 4.5.2016.
 */
public class RegisterUserService extends IntentService {

    public RegisterUserService() {
        super("RegisterUserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("RegisterUserService", "started");

            String[] registervalues = intent.getStringArrayExtra("registervalues");
            if (registervalues != null) {
                try {

                    boolean registersuccess = registerUser(registervalues);
                    if(registersuccess){
                        broadcastToast("Registration succesful");
                    }else {
                        broadcastToast("Error while registering");
                    }

                } catch (IOException e) {
                    broadcastToast("Error while registering");
                    e.printStackTrace();
                }
                Log.d("RegisterUserService", "ended");
            }
        }
    }

    private boolean registerUser(String[] registervalues) throws IOException {

        URL url = new URL(MenuActivity.SERVER_URI + "Users/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        HashMap<String, String> params = new HashMap();
        params.put("name", registervalues[0]);
        params.put("password", registervalues[1]);
        params.put("firstname", registervalues[2]);
        params.put("lastname", registervalues[3]);
        params.put("department", registervalues[4]);
        params.put("title", registervalues[5]);
        params.put("email", registervalues[6]);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostParams(params));

        writer.flush();
        writer.close();
        os.close();
        InputStream in = conn.getInputStream();

        Log.d("PostMessageService", conn.getResponseCode() + " " + conn.getResponseMessage());
        if (conn.getResponseCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    private String getPostParams(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
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
}