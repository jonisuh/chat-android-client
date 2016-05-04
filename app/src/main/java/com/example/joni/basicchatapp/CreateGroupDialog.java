package com.example.joni.basicchatapp;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joni.basicchatapp.services.CreateGroupService;
import com.example.joni.basicchatapp.services.LoadGroupsService;

import java.util.ArrayList;

public class CreateGroupDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter mAdapter;
    private String[] PROJECTION = new String[] {UsersTable.COLUMN_ID, UsersTable.COLUMN_NAME};
    private String SELECTION;
    private Uri CONTENT_URI = ChatProvider.USERS_CONTENT_URI;
    private ArrayList<Integer> selectedUsers;
    private  TextView groupnameinput;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int myID = getActivity().getSharedPreferences(LoginManager.PREF_NAME, 0).getInt(LoginManager.KEY_ID,-1);
        SELECTION = UsersTable.COLUMN_ID+"!="+myID;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialog = li.inflate(R.layout.new_group_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(dialog);

        selectedUsers = new ArrayList<>();

        ListView lv = (ListView) dialog.findViewById(R.id.listView2);
        groupnameinput = (TextView) dialog.findViewById(R.id.groupnameinput);
        String[] groupfromColumns = {UsersTable.COLUMN_NAME};
        int[] grouptoViews = {R.id.user_fieldcontent};

        mAdapter = new MenuCursorAdapter(getActivity(), R.layout.listitem_user,
                null, groupfromColumns, grouptoViews, 0);
        lv.setAdapter(mAdapter);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String groupname = groupnameinput.getText().toString();
                                if(groupname != null && !groupname.equals("") && !groupname.equals(" ")){

                                    int[] members = new int[selectedUsers.size()];
                                    int index = 0;
                                    for(Integer i : selectedUsers){
                                        Log.d("CreateGroupDialog",""+ i);
                                        members[index] = i;
                                        index++;
                                    }
                                    Intent createGroupIntent = new Intent(getActivity(), CreateGroupService.class);

                                    createGroupIntent.putExtra("groupname",groupname);
                                    createGroupIntent.putExtra("members",members);

                                    getActivity().startService(createGroupIntent);

                                }else{
                                    Toast t = Toast.makeText(getActivity(), "Group name is empty!", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                                    t.show();
                                }
                            }
                        });

        lv.setItemsCanFocus(false);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int userID = (int) id;

                if(selectedUsers.contains(userID)){
                    selectedUsers.remove(selectedUsers.indexOf(userID));
                    view.setBackgroundColor(0);
                }else{
                    selectedUsers.add(userID);
                    view.setBackgroundColor(0xFFB4BDE8);
                }

            }
        });

        return alertDialogBuilder.create();
        }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
            return null;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        mAdapter.swapCursor(c);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}