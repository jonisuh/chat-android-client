package com.example.joni.basicchatapp.xmlentities;

import android.util.Log;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Group.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Group other = (Group) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!this.groupname.equals(other.groupname)) {
            return false;
        }
        return true;
    }
}
