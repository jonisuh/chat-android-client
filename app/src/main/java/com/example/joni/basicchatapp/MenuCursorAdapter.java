package com.example.joni.basicchatapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Joni on 30.4.2016.
 */
public class MenuCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public MenuCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to, int flags) {
        super(context,layout,c,from,to,flags);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.cr = c;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        return super.getView(position, convertview, arg2);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

    }
}
