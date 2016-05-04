package com.example.joni.basicchatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.joni.basicchatapp.services.LeaveGroupService;
import com.example.joni.basicchatapp.services.LoadUsersService;
import com.example.joni.basicchatapp.services.LoginService;
import com.example.joni.basicchatapp.services.RegisterUserService;

public class Login extends Activity {
    private EditText usernamefield;
    private EditText passwordfield;
    private View dialogview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernamefield = (EditText) findViewById(R.id.inputName);
        passwordfield = (EditText) findViewById(R.id.inputPassword);

        Button loginbutton = (Button) findViewById(R.id.loginbutton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refreshusers = new Intent(Login.this, LoadUsersService.class);
                startService(refreshusers);

                String username = usernamefield.getText().toString();
                String password = passwordfield.getText().toString();
                Log.d("loginactivity", "login started with " + username + " " + password);

                Intent intent = new Intent(Login.this, LoginService.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startService(intent);

                usernamefield.setText("");
                passwordfield.setText("");
            }
        });

        Button registerbutton = (Button) findViewById(R.id.registerbutton);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = getLayoutInflater();
                dialogview = li.inflate(R.layout.register_dialog, null);

                new AlertDialog.Builder(Login.this)
                        .setView(dialogview)
                        .setCancelable(false)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Register user",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String[] registervalues = new String[7];

                                        registervalues[0] = ((EditText) dialogview.findViewById((R.id.register_username))).getText().toString();
                                        registervalues[1] = ((EditText) dialogview.findViewById((R.id.register_password))).getText().toString();
                                        registervalues[2] = ((EditText) dialogview.findViewById((R.id.register_fname))).getText().toString();
                                        registervalues[3] = ((EditText) dialogview.findViewById((R.id.register_lname))).getText().toString();
                                        registervalues[4] = ((EditText) dialogview.findViewById((R.id.register_department))).getText().toString();
                                        registervalues[5] = ((EditText) dialogview.findViewById((R.id.register_title))).getText().toString();
                                        registervalues[6] = ((EditText) dialogview.findViewById((R.id.register_email))).getText().toString();

                                        boolean emptyval = false;
                                        for( String s : registervalues){
                                            if(s == null || s.isEmpty()  || s.equals("") || s.equals (" ")){
                                                emptyval = true;
                                            }
                                        }

                                        if(!emptyval){
                                            Intent registerintent = new Intent(Login.this, RegisterUserService.class);
                                            registerintent.putExtra("registervalues", registervalues);
                                            startService(registerintent);
                                            usernamefield.setText(registervalues[0]);
                                            passwordfield.setText(registervalues[1]);
                                        }

                                    }
                                }).show();

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        boolean b = pref.getBoolean("IsLoggedIn",false);
        if(b){
            Intent menuintent = new Intent(this, MenuActivity.class);
            menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(menuintent);
        }
    }
}
