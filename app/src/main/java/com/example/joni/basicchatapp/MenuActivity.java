package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joni.basicchatapp.services.ChatWebSocketService;
import com.example.joni.basicchatapp.services.LoadGroupsService;
import com.example.joni.basicchatapp.services.LoadUsersService;

public class MenuActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String logvar = "MainActivity: ";

    public final static String SERVER_URI = "http://10.112.201.215:8080/ProjectV1/API/";
    //public final static String SERVER_URI = "http://192.168.0.102:8080/ProjectV1/API/";
    //public final static String WS_URI = "ws://192.168.0.102:8080/ProjectV1/chatendpoint";
    public final static String WS_URI = "ws://10.112.201.215:8080/ProjectV1/chatendpoint";

    private SimpleCursorAdapter myAdapter;
    private TextView usernamefield;
    private ListView lv;
    private String[] PROJECTION = new String[] {GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
    private String SELECTION = "";
    private Uri CONTENT_URI = ChatProvider.GROUPS_CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        lv = (ListView)findViewById(R.id.listView);

        usernamefield = (TextView) findViewById(R.id.textView2);
        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        int id = pref.getInt("id",-1);
        Log.d("menuactivity", "" + id);
        usernamefield.setText("" + id);

        NameQueryHandler handler =  new NameQueryHandler(getContentResolver());
        handler.execute();

        initListView();

        getLoaderManager().initLoader(0, null, this);

        Button groupsbutton = (Button) findViewById(R.id.button);
        groupsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PROJECTION = new String[]{GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
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

        Button logoutbutton = (Button) findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager manager = new LoginManager(MenuActivity.this);
                manager.logoutUser();

                Intent backtologin = new Intent(MenuActivity.this, Login.class);
                startActivity(backtologin);
            }
        });



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Log.d("MenuActivity",""+R.id.user_fieldcontent);
                Log.d("MenuActivity",""+R.layout.listitem_user);
                Log.d("MenuActivity",""+R.id.userlistitem);
                Log.d("MenuActivity",""+view.getId()); */
                // When clicked, show a toast with the TextView text
                TextView userview = (TextView) view.findViewById(R.id.user_fieldcontent);
                TextView groupview = (TextView) view.findViewById(R.id.fieldcontent);

                String selected;
                if(view.getId() == R.id.grouplistitem){
                    selected = groupview.getText().toString();
                    Intent openchatintent = new Intent(MenuActivity.this, ChatScreenActivity.class);
                    Log.d("provider", ""+id);
                    openchatintent.putExtra("groupID",(int) id);
                    startActivity(openchatintent);
                }else{
                    selected = userview.getText().toString();
                }

            }
        });

    }
    //CursorLoader methods
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

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

        myAdapter = new MenuCursorAdapter(this, R.layout.listitem,
                null, groupfromColumns, grouptoViews, 0);
        lv.setAdapter(myAdapter);

    }

    private class NameQueryHandler extends AsyncQueryHandler {
        public NameQueryHandler(ContentResolver c){
            super(c);
        }
        public void execute() {
            String [] nameselection = {UsersTable.COLUMN_NAME};
            SharedPreferences pref = MenuActivity.this.getSharedPreferences("BasicChatAppCredentials", 0);
            int id = pref.getInt("id",-1);
            startQuery(0,null,ChatProvider.USERS_CONTENT_URI,nameselection,UsersTable.COLUMN_ID+"="+id,null,null);
        }

        public void onQueryComplete(int t, Object command, Cursor c) {
            if(c != null && c.getCount() > 0) {
                Log.d("MenuActivity", "" + c.getColumnCount());
                c.moveToNext();
                usernamefield.setText(c.getString(0));
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent loadusersintent = new Intent(this, LoadUsersService.class);
        startService(loadusersintent);

        Intent loadgroupsintent = new Intent(this, LoadGroupsService.class);
        startService(loadgroupsintent);

        Intent wsintent = new Intent(this, ChatWebSocketService.class);

        startService(wsintent);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
