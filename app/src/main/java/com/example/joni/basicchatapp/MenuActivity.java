package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.DialogFragment;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joni.basicchatapp.services.ChatWebSocketService;
import com.example.joni.basicchatapp.services.LoadGroupsService;
import com.example.joni.basicchatapp.services.LoadUsersService;

public class MenuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String logvar = "MainActivity: ";

    public final static String SERVER_URI = "http://192.168.43.132:8080/ProjectV1/API/";
    public final static String WS_URI = "ws://192.168.43.132:8080/ProjectV1/chatendpoint";

    //public final static String SERVER_URI = "http://10.112.201.215:8080/ProjectV1/API/";
    //public final static String SERVER_URI = "http://192.168.0.102:8080/ProjectV1/API/";
    //public final static String WS_URI = "ws://192.168.0.102:8080/ProjectV1/chatendpoint";
    //public final static String WS_URI = "ws://10.112.201.215:8080/ProjectV1/chatendpoint";

    private SimpleCursorAdapter myAdapter;
    private TextView usernamefield;
    private ListView lv;
    private String[] PROJECTION = new String[] {GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
    private String SELECTION = "";
    private Uri CONTENT_URI = ChatProvider.GROUPS_CONTENT_URI;

    private static Activity menuactivity;

    private Button usersbutton;
    private Button groupsbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menuactivity = this;
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

        groupsbutton = (Button) findViewById(R.id.button);
        groupsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.swapCursor(null);
                PROJECTION = new String[]{GroupsTable.COLUMN_ID, GroupsTable.COLUMN_NAME};
                CONTENT_URI = ChatProvider.GROUPS_CONTENT_URI;
                changeToGroupAdapter();
                getLoaderManager().restartLoader(0, null, MenuActivity.this);
                groupsbutton.setBackgroundResource(R.drawable.selected_button);
                usersbutton.setBackgroundResource(R.drawable.blue_button);
            }
        });
        groupsbutton.setBackgroundResource(R.drawable.selected_button);

        usersbutton = (Button) findViewById(R.id.button2);
        usersbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.swapCursor(null);
                PROJECTION = new String[] {UsersTable.COLUMN_ID, UsersTable.COLUMN_NAME};
                CONTENT_URI = ChatProvider.USERS_CONTENT_URI;
                changeToUserAdapter();
                getLoaderManager().restartLoader(0, null, MenuActivity.this);
                usersbutton.setBackgroundResource(R.drawable.selected_button);
                groupsbutton.setBackgroundResource(R.drawable.blue_button);
            }
        });


        Button menubutton = (Button) findViewById(R.id.menubutton);
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(MenuActivity.this, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_logout:
                                LoginManager manager = new LoginManager(MenuActivity.this);
                                manager.logoutUser();

                                Intent closewebsocket = new Intent(MenuActivity.this, ChatWebSocketService.class);
                                stopService(closewebsocket);

                                Intent backtologin = new Intent(MenuActivity.this, Login.class);
                                startActivity(backtologin);
                                MenuActivity.this.finish();
                                return true;
                            case R.id.action_refresh:
                                Intent loadusersintent = new Intent(MenuActivity.this, LoadUsersService.class);
                                startService(loadusersintent);

                                Intent loadgroupsintent = new Intent(MenuActivity.this, LoadGroupsService.class);
                                startService(loadgroupsintent);

                                Intent wsintent = new Intent(MenuActivity.this, ChatWebSocketService.class);

                                startService(wsintent);
                                return true;
                            case R.id.action_new_group:
                                DialogFragment dialog = new CreateGroupDialog();
                                dialog.show(getFragmentManager(), "CreateDialog");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.menu_main, menu.getMenu());
                menu.show();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView userview = (TextView) view.findViewById(R.id.user_fieldcontent);
                TextView groupview = (TextView) view.findViewById(R.id.fieldcontent);

                String selected;
                if(view.getId() == R.id.grouplistitem){
                    selected = groupview.getText().toString();
                    Intent openchatintent = new Intent(MenuActivity.this, ChatScreenActivity.class);
                    openchatintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    openchatintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    openchatintent.putExtra("groupID",(int) id);
                    startActivity(openchatintent);
                }else{
                    selected = userview.getText().toString();
                    DialogFragment dialog = ShowUserInfoDialog.newInstance((int) id);
                    dialog.show(getFragmentManager(), "UserInfoDialog");
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
                c.close();
            }
        }
    }
    public static Activity getMenuactivity(){
        return menuactivity;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        int myID = pref.getInt("id", -1);

        if(myID == -1){
            Intent closewebsocket = new Intent(MenuActivity.this, ChatWebSocketService.class);
            stopService(closewebsocket);

            Intent backtologin = new Intent(this, Login.class);;
            startActivity(backtologin);
            finish();
        }else {
            Intent loadusersintent = new Intent(this, LoadUsersService.class);
            startService(loadusersintent);

            Intent loadgroupsintent = new Intent(this, LoadGroupsService.class);
            startService(loadgroupsintent);

            Intent wsintent = new Intent(this, ChatWebSocketService.class);

            startService(wsintent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
