package com.example.joni.basicchatapp;

/**
 * Created by Joni on 27.4.2016.
 */
public class GroupsTable {
    public static final String TABLE_GROUPS = "groups";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "groupname";


    public static final String DATABASE_CREATE = "create table if not exists "
            +TABLE_GROUPS
            +" ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text"
            +");";

}
