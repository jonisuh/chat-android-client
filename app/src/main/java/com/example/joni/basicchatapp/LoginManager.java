package com.example.joni.basicchatapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.joni.basicchatapp.xmlentities.LoginCredentials;

/**
 * Created by Joni on 27.4.2016.
 */
public class LoginManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "BasicChatAppCredentials";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";

    // Email address (make variable public to access from outside)
    public static final String KEY_CRED = "credentials";

    // Constructor
    public LoginManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(LoginCredentials cred){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_ID, cred.getId());
        editor.putString(KEY_CRED, cred.getAuthcredentials());
        editor.commit();
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }
}
