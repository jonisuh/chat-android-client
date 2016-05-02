package com.example.joni.basicchatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Joni on 30.4.2016.
 */
public class MessageCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public MessageCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to, int flags) {
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

        SharedPreferences pref = context.getSharedPreferences("BasicChatAppCredentials", 0);
        int id = pref.getInt("id", -1);
        int senderid = cursor.getInt(2);
        Log.d("MessageAdapter", senderid + " " + id+" "+cursor.getInt(0)+" "+cursor.getString(4));

        if(senderid == id){
            Log.d("MessageAdapter"," view customized");
            //view.setBackgroundColor(0xFF1C1F92);

            LinearLayout bottomtext = (LinearLayout) view.findViewById(R.id.bottomtextholder);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;

            bottomtext.setLayoutParams(params);

            TextView content = (TextView) view.findViewById(R.id.message_content);
            content.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            LinearLayout background = (LinearLayout) view.findViewById(R.id.messagelayoutitem);
            background.setBackgroundResource(R.drawable.background);

            LinearLayout spacer = (LinearLayout) view.findViewById(R.id.messagelayoutspacer);
            spacer.setVisibility(View.VISIBLE);

            TextView name = (TextView)view.findViewById(R.id.message_uname);
            name.setText("Me");
        }else{
            LinearLayout bottomtext = (LinearLayout) view.findViewById(R.id.bottomtextholder);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;

            bottomtext.setLayoutParams(params);

            TextView content = (TextView) view.findViewById(R.id.message_content);
            content.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            LinearLayout background = (LinearLayout) view.findViewById(R.id.messagelayoutitem);
            background.setBackgroundResource(R.drawable.background_message);

            LinearLayout spacer = (LinearLayout) view.findViewById(R.id.messagelayoutspacer);
            spacer.setVisibility(View.GONE);
        };



    }
}
