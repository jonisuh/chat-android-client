package com.example.joni.basicchatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.joni.basicchatapp.services.LoginService;

public class Login extends Activity {
    private EditText usernamefield;
    private EditText passwordfield;
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

    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences pref = this.getSharedPreferences("BasicChatAppCredentials", 0);
        boolean b = pref.getBoolean("IsLoggedIn",false);
        if(b){
            Intent menuintent = new Intent(this, MenuActivity.class);
            startActivity(menuintent);
        }
    }
}
