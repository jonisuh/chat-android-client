package com.example.joni.basicchatapp.xmlentities;

/**
 * Created by Joni on 27.4.2016.
 */
public class LoginCredentials {
    private int id;
    private String authcredentials;

    public LoginCredentials(){

    }

    public LoginCredentials(int id, String authcredentials){
        this.id = id;
        this.authcredentials = authcredentials;
    }

    public int getId(){
       return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getAuthcredentials(){
        return authcredentials;
    }
    public void setAuthcredentials(String authcredentials){
        this.authcredentials = authcredentials;
    }
}
