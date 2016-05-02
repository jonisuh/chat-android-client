package com.example.joni.basicchatapp;

/**
 * Created by Joni on 30.4.2016.
 */
public class MessagesTable {
        public static final String TABLE_MESSAGES = "messages";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_USER_ID = "userid";
        public static final String COLUMN_GROUP_ID = "groupid";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME = "username";


        public static final String DATABASE_CREATE = "create table if not exists "
                +TABLE_MESSAGES
                +" ("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_USER_ID + " integer, "
                + COLUMN_GROUP_ID + " integer, "
                + COLUMN_MESSAGE + " text, "
                + COLUMN_TIMESTAMP + " text, "
                + COLUMN_NAME + " text"
                +");";

}
