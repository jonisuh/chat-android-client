package com.example.joni.basicchatapp.xmlentities;

/**
 * Created by Joni on 28.4.2016.
 */
public class User{
    private int id;
    private String username;
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String department;

    public User(){

    }
    public User(int id, String username){
        this.id = id;
        this.username = username;
    }
    public User(int id, String username, String firstname, String lastname, String email, String title, String department){
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.title = title;
        this.department = department;
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
    public String getFirstname(){
        return firstname;
    }
    public String getLastname(){
        return lastname;
    }
    public String getTitle(){
        return title;
    }
    public String getDepartment(){
        return department;
    }
    public String getEmail(){
        return email;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!User.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final User other = (User) obj;
        if(this.id != other.id){
            return false;
        }
        if(!this.username.equals(other.username)){
            return false;
        }
        if(!this.firstname.equals(other.firstname)){
            return false;
        }
        if(!this.lastname.equals(other.lastname)){
            return false;
        }
        if(!this.department.equals(other.department)){
            return false;
        }
        if(!this.title.equals(other.title)){
            return false;
        }
        if(!this.email.equals(other.email)){
            return false;
        }
        return true;
    }
}
