package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joni.basicchatapp.services.ChatWebSocketService;
import com.example.joni.basicchatapp.services.LeaveGroupService;
import com.example.joni.basicchatapp.services.LoadGroupMessagesService;
import com.example.joni.basicchatapp.services.LoadGroupsService;
import com.example.joni.basicchatapp.services.LoadUsersService;
import com.example.joni.basicchatapp.services.PostMessageService;
import com.example.joni.basicchatapp.xmlentities.Group;
import com.example.joni.basicchatapp.xmlentities.Message;

public class ChatScreenActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private MessageCursorAdapter messageadapter;
    private TextView usernamefield;
    private TextView groupnamefield;
    private ListView lv;
    private String[] PROJECTION = new String[] {MessagesTable.COLUMN_ID,MessagesTable.COLUMN_GROUP_ID,MessagesTable.COLUMN_USER_ID,MessagesTable.COLUMN_NAME, MessagesTable.COLUMN_MESSAGE, MessagesTable.COLUMN_TIMESTAMP};
    private String SELECTION = "";
    private Uri CONTENT_URI = ChatProvider.MESSAGES_CONTENT_URI;

    private static boolean activityVisible;
    private static int groupID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        usernamefield = (TextView) findViewById(R.id.textView2);
        groupnamefield = (TextView) findViewById(R.id.groupnamefield);

        Intent i = getIntent();
        groupID = i.getIntExtra("groupID", -1);

        ChatNameQueryHandler handler =  new ChatNameQueryHandler(getContentResolver());
        handler.execute("username");
        handler.execute("groupname");

        lv = (ListView) findViewById(R.id.messageview);

        String[] messagefromcolumns = {MessagesTable.COLUMN_MESSAGE, MessagesTable.COLUMN_NAME, MessagesTable.COLUMN_TIMESTAMP};
        int[] messagetoviews = {R.id.message_content, R.id.message_uname, R.id.message_timestamp};

        messageadapter = new MessageCursorAdapter(this, R.layout.listitem_message,
                null, messagefromcolumns, messagetoviews, 0);
        SELECTION = MessagesTable.COLUMN_GROUP_ID+"="+groupID;
        Log.d("ChatScreenActivity", SELECTION);

        lv.setAdapter(messageadapter);
        getLoaderManager().initLoader(1, null, this);


        Button sendbutton = (Button) findViewById(R.id.sendbutton);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputfield = (EditText) findViewById(R.id.messageinput);
                String inputmessage = inputfield.getText().toString();
                Toast t;

                if (inputmessage.length() <= 500) {
                    if (inputmessage.length() > 0) {
                        Intent postmessageintent = new Intent(ChatScreenActivity.this, PostMessageService.class);
                        postmessageintent.putExtra("groupID", groupID);
                        postmessageintent.putExtra("message", inputmessage);
                        startService(postmessageintent);

                        inputfield.setText("");


                    } else {
                        t = Toast.makeText(ChatScreenActivity.this, "Message is empty.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                } else {
                    t = Toast.makeText(ChatScreenActivity.this, "Message too long, maximum character limit is 500.", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        Button menubutton = (Button) findViewById(R.id.chatmenubutton);
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(ChatScreenActivity.this, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_logout:
                                LoginManager manager = new LoginManager(ChatScreenActivity.this);
                                manager.logoutUser();

                                Intent closewebsocket = new Intent(ChatScreenActivity.this, ChatWebSocketService.class);
                                stopService(closewebsocket);

                                Intent backtologin = new Intent(ChatScreenActivity.this, Login.class);
                                startActivity(backtologin);

                                ChatScreenActivity.this.finish();
                                MenuActivity.getMenuactivity().finish();
                                return true;
                            case R.id.action_refresh:
                                Intent loadmessages = new Intent(ChatScreenActivity.this, LoadGroupMessagesService.class);
                                loadmessages.putExtra("groupID",groupID);
                                startService(loadmessages);

                                return true;
                            case R.id.action_leave_group:
                                LayoutInflater li = getLayoutInflater();
                                View dialogview = li.inflate(R.layout.leave_group_dialog, null);

                                new AlertDialog.Builder(ChatScreenActivity.this)
                                        .setView(dialogview)
                                        .setCancelable(false)
                                        .setNegativeButton("No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                })
                                        .setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        Intent leavegroupintent = new Intent(ChatScreenActivity.this, LeaveGroupService.class);
                                                        leavegroupintent.putExtra("groupID", groupID);
                                                        startService(leavegroupintent);
                                                        ChatScreenActivity.this.finish();
                                                    }
                                                }).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.chatscreen_menu, menu.getMenu());
                menu.show();
            }
        });

   }
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        messageadapter.swapCursor(c);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        messageadapter.swapCursor(null);
    }



    @Override
    protected void onPause() {
        super.onPause();
        activityVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        int myID = pref.getInt("id", -1);

        if(myID == -1){
            Intent closewebsocket = new Intent(this, ChatWebSocketService.class);
            stopService(closewebsocket);

            Intent backtologin = new Intent(this, Login.class);
            startActivity(backtologin);
            finish();
            if(MenuActivity.getMenuactivity() != null){
                MenuActivity.getMenuactivity().finish();
            }
        }else {

            NotificationSender.getInstance().cancelNotification(groupID,this);

            Intent loadmessagesintent = new Intent(this, LoadGroupMessagesService.class);
            loadmessagesintent.putExtra("groupID", groupID);
            loadmessagesintent.putExtra("messageID", 0);
            startService(loadmessagesintent);

            activityVisible = true;
        }

    }
    public static boolean isVisible(){
        return activityVisible;
    }
    public static int getGroupID(){
        return groupID;
    }

    private class ChatNameQueryHandler extends AsyncQueryHandler {
        public ChatNameQueryHandler(ContentResolver c){
            super(c);
        }
        public void execute(String action) {
            if(action.equals("username")) {

                String[] nameselection = {UsersTable.COLUMN_NAME};
                SharedPreferences pref = ChatScreenActivity.this.getSharedPreferences("BasicChatAppCredentials", 0);
                int id = pref.getInt("id", -1);
                startQuery(0, action, ChatProvider.USERS_CONTENT_URI, nameselection, UsersTable.COLUMN_ID + "=" + id, null, null);

            }else if(action.equals("groupname")){

                String[] groupnameselection = {GroupsTable.COLUMN_NAME};
                startQuery(1, action, ChatProvider.GROUPS_CONTENT_URI, groupnameselection, GroupsTable.COLUMN_ID + "=" + groupID, null, null);

            }
        }

        public void onQueryComplete(int t, Object command, Cursor c) {
            if(c != null && c.getCount() > 0) {
                c.moveToNext();
                if (command.equals("username")) {
                    Log.d("ChatScreenActivity", "" + c.getColumnCount());
                    usernamefield.setText(c.getString(0));
                } else if (command.equals("groupname")){
                    Log.d("ChatScreenActivity", "" + c.getColumnCount());
                    groupnamefield.setText(c.getString(0));
                }
                c.close();
            }
        }
    }
}
