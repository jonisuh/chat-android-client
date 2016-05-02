package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joni.basicchatapp.services.ChatWebSocketService;
import com.example.joni.basicchatapp.services.LoadGroupMessagesService;
import com.example.joni.basicchatapp.services.LoadUsersService;
import com.example.joni.basicchatapp.services.PostMessageService;
import com.example.joni.basicchatapp.xmlentities.Group;
import com.example.joni.basicchatapp.xmlentities.Message;

public class ChatScreenActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private MessageCursorAdapter messageadapter;
    private TextView usernamefield;
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

        Intent i = getIntent();
        groupID = i.getIntExtra("groupID", -1);
        TextView test = (TextView) findViewById(R.id.textView3);
        test.setText(""+groupID);

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

                if(inputmessage.length() <= 500){
                    if(inputmessage.length() > 0){
                        Intent postmessageintent = new Intent(ChatScreenActivity.this, PostMessageService.class);
                        postmessageintent.putExtra("groupID",groupID);
                        postmessageintent.putExtra("message", inputmessage);
                        startService(postmessageintent);

                        inputfield.setText("");

                        View view = getCurrentFocus();
                        if (view != null) {
                            view.clearFocus();
                            InputMethodManager imm = (InputMethodManager)getSystemService(ChatScreenActivity.this.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }else{
                        t = Toast.makeText(ChatScreenActivity.this,"Message is empty.",Toast.LENGTH_SHORT);
                        t.show();
                    }
                }else{
                    t = Toast.makeText(ChatScreenActivity.this,"Message too long, maximum character limit is 500.",Toast.LENGTH_SHORT);
                    t.show();
                }
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

        Intent loadmessagesintent = new Intent(this, LoadGroupMessagesService.class);
        loadmessagesintent.putExtra("groupID",groupID);
        startService(loadmessagesintent);

        activityVisible = true;

    }
    public static boolean isVisible(){
        return activityVisible;
    }
    public static int getGroupID(){
        return groupID;
    }
}
