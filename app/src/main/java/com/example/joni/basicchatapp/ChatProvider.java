package com.example.joni.basicchatapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ChatProvider extends ContentProvider {
    private SQLiteDatabase thisDB;
    private DBHelper helper;

    public static final String AUTHORITY = "com.example.joni.basicchatapp.ChatProvider";
    public static final String PROVIDER_NAME = "com.example.joni.basicchatapp.ChatProvider";
    public static final Uri GROUPS_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/groups");
    public static final Uri USERS_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/users");
    public static final Uri MESSAGES_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/messages");

    private static final int GROUPS = 1;
    private static final int GROUP_ID = 2;
    private static final int USERS = 3;
    private static final int USER_ID = 4;
    private static final int MESSAGES = 5;
    private static final int MESSAGE_ID = 6;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "groups", GROUPS);
        uriMatcher.addURI(PROVIDER_NAME, "groups/#", GROUP_ID);
        uriMatcher.addURI(PROVIDER_NAME, "users", USERS);
        uriMatcher.addURI(PROVIDER_NAME, "users/#", USER_ID);
        uriMatcher.addURI(PROVIDER_NAME, "messages", MESSAGES);
        uriMatcher.addURI(PROVIDER_NAME, "messages/#", MESSAGE_ID);
    }

    public boolean onCreate() {
        Log.d("provider", " in provider onCreate");
        Context c = getContext();
        helper = new DBHelper(c);
        thisDB = helper.getReadableDatabase();
        if (thisDB == null) {
            Log.d("provider", "null");
            return false;
        }else {
            Log.d("provider", "full");
            return true;
        }
    }
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        int rowsDeleted = 0;
        String id;
        switch (uriType) {
            case USERS:
                rowsDeleted = thisDB.delete(UsersTable.TABLE_USERS, selection,selectionArgs);
                break;
            case USER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = thisDB.delete(UsersTable.TABLE_USERS,UsersTable.COLUMN_ID + "=" + id,null);
                } else {
                    rowsDeleted = thisDB.delete(UsersTable.TABLE_USERS,UsersTable.COLUMN_ID + "=" + id+ " and " + selection, selectionArgs);
                }
                break;
            case GROUPS:
                rowsDeleted = thisDB.delete(GroupsTable.TABLE_GROUPS, selection,
                        selectionArgs);
                break;
            case GROUP_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = thisDB.delete(GroupsTable.TABLE_GROUPS,
                            GroupsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = thisDB.delete(GroupsTable.TABLE_GROUPS,
                            GroupsTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case MESSAGES:
                rowsDeleted = thisDB.delete(MessagesTable.TABLE_MESSAGES, selection,
                        selectionArgs);
                break;
            case MESSAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = thisDB.delete(MessagesTable.TABLE_MESSAGES,
                            GroupsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = thisDB.delete(MessagesTable.TABLE_MESSAGES,
                            GroupsTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;

    }



    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USERS:
                return "vnd.android.cursor.dir/vnd.example.joni.basicchatapp ";
            case USER_ID:
                return "vnd.android.cursor.item/vnd.example.joni.basicchatapp ";
            case GROUPS:
                return "vnd.android.cursor.dir/vnd.example.joni.basicchatapp ";
            case GROUP_ID:
                return "vnd.android.cursor.item/vnd.example.joni.basicchatapp ";
            case MESSAGES:
                return "vnd.android.cursor.dir/vnd.example.joni.basicchatapp ";
            case MESSAGE_ID:
                return "vnd.android.cursor.item/vnd.example.joni.basicchatapp ";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        Uri _uri = null;

        long id = 0;
        switch (uriType) {
            case USERS:
                id = thisDB.insertWithOnConflict(DBHelper.TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                _uri = ContentUris.withAppendedId(USERS_CONTENT_URI, id);
                break;
            case GROUPS:
                id = thisDB.insertWithOnConflict(DBHelper.TABLE_GROUPS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                _uri = ContentUris.withAppendedId(GROUPS_CONTENT_URI, id);
                break;
            case MESSAGES:
                id = thisDB.insertWithOnConflict(DBHelper.TABLE_MESSAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                _uri = ContentUris.withAppendedId(MESSAGES_CONTENT_URI, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return _uri;
    }


    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        int uriType = uriMatcher.match(uri);

        switch (uriType) {
            case USERS:
                sqlBuilder.setTables(UsersTable.TABLE_USERS);
                break;
            case USER_ID:
                sqlBuilder.setTables(UsersTable.TABLE_USERS);
                sqlBuilder.appendWhere(UsersTable.COLUMN_ID + " = " + uri.getPathSegments().get(1));
                break;
            case GROUPS:
                sqlBuilder.setTables(GroupsTable.TABLE_GROUPS);
                break;
            case GROUP_ID:
                sqlBuilder.setTables(GroupsTable.TABLE_GROUPS);
                sqlBuilder.appendWhere(GroupsTable.COLUMN_ID + " = " + uri.getPathSegments().get(1));
                break;
            case MESSAGES:
                sqlBuilder.setTables(MessagesTable.TABLE_MESSAGES);
                break;
            case MESSAGE_ID:
                sqlBuilder.setTables(MessagesTable.TABLE_MESSAGES);
                sqlBuilder.appendWhere(MessagesTable.COLUMN_ID + " = " + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cur = sqlBuilder.query(thisDB, projection, selection, selectionArgs, null, null, sortOrder);
        cur.setNotificationUri(getContext().getContentResolver(), uri);

        return cur;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}