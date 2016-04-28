package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.joni.basicchatapp.services.LoadGroupsService;
import com.example.joni.basicchatapp.services.LoadUsersService;

public class MenuActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String logvar = "MainActivity: ";

    public final static String SERVER_URI = "http://10.112.219.210:8080/ProjectV1/API/";
    //public final static String SERVER_URI = "http://192.168.0.102:8080/ProjectV1/API/";

    private SimpleCursorAdapter myAdapter;
    private ListView lv;
    private String[] PROJECTION = new String[] {GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
    private String SELECTION = "";
    private Uri CONTENT_URI = ChatProvider.GROUPS_CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        lv = (ListView)findViewById(R.id.listView);

        TextView userid = (TextView) findViewById(R.id.textView2);
        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        int id = pref.getInt("id",-1);
        Log.d("menuactivity",""+id);
        userid.setText("" + id);

        Intent loadusersintent = new Intent(this, LoadUsersService.class);
        startService(loadusersintent);

        Intent loadgroupsintent = new Intent(this, LoadGroupsService.class);
        startService(loadgroupsintent);

        /*
        LoginManager manager = new LoginManager(this);
        manager.logoutUser();
        */
        initListView();

        getLoaderManager().initLoader(0, null, this);

        Button groupsbutton = (Button) findViewById(R.id.button);
        groupsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROJECTION = new String[] {GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
                CONTENT_URI = ChatProvider.GROUPS_CONTENT_URI;
                changeToGroupAdapter();
                getLoaderManager().restartLoader(0, null, MenuActivity.this);
            }
        });
        Button usersbutton = (Button) findViewById(R.id.button2);
        usersbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROJECTION = new String[] {UsersTable.COLUMN_ID, UsersTable.COLUMN_NAME};
                CONTENT_URI = ChatProvider.USERS_CONTENT_URI;
                changeToUserAdapter();
                getLoaderManager().restartLoader(0, null, MenuActivity.this);
            }
        });

    }
    //CursorLoader methods
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("provider", " loader created");
        return new CursorLoader(this, CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        myAdapter.swapCursor(c);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter.swapCursor(null);
    }


    //Methods for alternating user and group data in the same listview
    private void changeToUserAdapter(){
        String[] userfromColumns = { UsersTable.COLUMN_NAME};
        int[] usertoViews = {R.id.user_fieldcontent};

        myAdapter.setViewResource(R.layout.listitem_user);
        myAdapter.changeCursorAndColumns(null, userfromColumns, usertoViews);
    }

    private void changeToGroupAdapter(){
        String[] groupfromColumns = {GroupsTable.COLUMN_NAME};
        int[] grouptoViews = {R.id.fieldcontent};

        myAdapter.setViewResource(R.layout.listitem);
        myAdapter.changeCursorAndColumns(null, groupfromColumns, grouptoViews);

    }
    private void initListView(){
        String[] groupfromColumns = {GroupsTable.COLUMN_NAME};
        int[] grouptoViews = {R.id.fieldcontent};

        myAdapter = new SimpleCursorAdapter(this, R.layout.listitem,
                null, groupfromColumns, grouptoViews, 0);
        lv.setAdapter(myAdapter);

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
