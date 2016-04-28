package com.example.joni.basicchatapp.xmlentities;

/**
 * Created by Joni on 28.4.2016.
 */
public class User {
    private int id;
    private String username;

    public User(){

    }

    public User(int id, String username){
        this.id = id;
        this.username = username;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
}
