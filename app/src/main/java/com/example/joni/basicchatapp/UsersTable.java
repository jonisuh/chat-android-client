package com.example.joni.basicchatapp;

/**
 * Created by Joni on 27.4.2016.
 */
public class UsersTable {
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "username";


    public static final String DATABASE_CREATE = "create table if not exists "
            +TABLE_USERS
            +" ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text"
            +");";

}
