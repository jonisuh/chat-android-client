package com.example.joni.basicchatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Joni on 4.4.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "chatDB";
    static final int DATABASE_VERSION = 5;
    static final String TABLE_USERS  = UsersTable.TABLE_USERS;
    static final String TABLE_GROUPS  = GroupsTable.TABLE_GROUPS;
    static final String TABLE_MESSAGES  = MessagesTable.TABLE_MESSAGES;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DBHelper", "DBHelper()");
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UsersTable.DATABASE_CREATE);
        db.execSQL(GroupsTable.DATABASE_CREATE);
        db.execSQL(MessagesTable.DATABASE_CREATE);
        /*
        ContentValues values = new ContentValues();

        values.put(UsersTable.COLUMN_NAME, "testusername");

        db.insert(TABLE_USERS, null, values);
        values.clear();

       values = new ContentValues();

        values.put(GroupsTable.COLUMN_NAME, "testgroupname");

        db.insert(TABLE_GROUPS, null, values);
        values.clear();
        */
        Log.d("DBHelper", "onCreate");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

}