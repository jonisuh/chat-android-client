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

    Context _context;

    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "BasicChatAppCredentials";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "id";

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
