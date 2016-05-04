package com.example.joni.basicchatapp;

/**
 * Created by Joni on 27.4.2016.
 */
public class UsersTable {
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "username";
    public static final String COLUMN_FIRSTNAME = "fname";
    public static final String COLUMN_LASTNAME = "lname";
    public static final String COLUMN_DEPARTMENT = "department";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_EMAIL = "email";

    public static final String DATABASE_CREATE = "create table if not exists "
            +TABLE_USERS
            +" ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text,"
            + COLUMN_FIRSTNAME + " text,"
            + COLUMN_LASTNAME + " text,"
            + COLUMN_DEPARTMENT + " text,"
            + COLUMN_TITLE + " text,"
            + COLUMN_EMAIL + " text"
            +");";

}
