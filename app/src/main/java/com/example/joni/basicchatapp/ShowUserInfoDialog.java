package com.example.joni.basicchatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

/**
 * Created by Joni on 3.5.2016.
 */
public class ShowUserInfoDialog extends DialogFragment{
    private int userID;

    private View dialogview;
    private TextView u_info_username;
    private TextView u_info_fname;
    private TextView u_info_lname;
    private TextView u_info_department;
    private TextView u_info_title;
    private TextView u_info_email;

    static ShowUserInfoDialog newInstance(int userID) {
        ShowUserInfoDialog dialog = new ShowUserInfoDialog();

        Bundle args = new Bundle();
        args.putInt("userID", userID);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getArguments().getInt("userID");

        LayoutInflater li = LayoutInflater.from(getActivity());
        dialogview = li.inflate(R.layout.user_info_dialog, null);

        u_info_username = (TextView) dialogview.findViewById(R.id.u_info_username);
        u_info_fname = (TextView) dialogview.findViewById(R.id.u_info_fname);
        u_info_lname = (TextView) dialogview.findViewById(R.id.u_info_lname);
        u_info_department = (TextView) dialogview.findViewById(R.id.u_info_department);
        u_info_title = (TextView) dialogview.findViewById(R.id.u_info_title);
        u_info_email = (TextView) dialogview.findViewById(R.id.u_info_email);

        UserInfoQueryHandler handler =  new UserInfoQueryHandler(getActivity().getContentResolver());
        handler.execute();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(dialogview);
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Start conversation",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int[] members = new int[1];
                                members[0] = userID;
                                Intent createGroupIntent = new Intent(getActivity(), CreateGroupService.class);

                                TextView mynameview = (TextView) getActivity().findViewById(R.id.textView2);
                                String myname = mynameview.getText().toString();

                                String username = u_info_username.getText().toString();

                                createGroupIntent.putExtra("groupname",myname+", "+username);
                                createGroupIntent.putExtra("members",members);

                                getActivity().startService(createGroupIntent);
                            }
                        });


        return alertDialogBuilder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }
    private class UserInfoQueryHandler extends AsyncQueryHandler {
        public UserInfoQueryHandler(ContentResolver c){
            super(c);
        }
        public void execute() {
            String[] PROJECTION = new String[]{UsersTable.COLUMN_ID, UsersTable.COLUMN_NAME, UsersTable.COLUMN_FIRSTNAME,UsersTable.COLUMN_LASTNAME,UsersTable.COLUMN_EMAIL,UsersTable.COLUMN_TITLE,UsersTable.COLUMN_DEPARTMENT};
            startQuery(0,null,ChatProvider.USERS_CONTENT_URI,PROJECTION,UsersTable.COLUMN_ID+"="+userID,null,null);
        }

        public void onQueryComplete(int t, Object command, Cursor c) {
            if(c != null && c.getCount() > 0) {
                Log.d("MenuActivity", "" + c.getColumnCount());
                c.moveToNext();
                u_info_username.setText(c.getString(1));
                u_info_fname.setText(c.getString(2));
                u_info_lname.setText(c.getString(3));
                u_info_department.setText(c.getString(6));
                u_info_title.setText(c.getString(5));
                u_info_email.setText(c.getString(4));
                c.close();
            }
        }
    }
}