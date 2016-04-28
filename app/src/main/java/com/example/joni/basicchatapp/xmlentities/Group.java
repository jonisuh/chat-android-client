package com.example.joni.basicchatapp.xmlentities;

/**
 * Created by Joni on 28.4.2016.
 */
public class Group {
    private int id;
    private String groupname;

    public Group(){

    }

    public Group(int id, String groupname){
        this.id = id;
        this.groupname = groupname;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getGroupname(){
        return groupname;
    }
    public void setGroupname(String groupname){
        this.groupname = groupname;
    }
}
