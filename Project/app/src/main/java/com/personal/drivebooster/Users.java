package com.personal.drivebooster;

public class Users {

    public String name;
    public String email;
    public String password;
    public String userType;
    public String instructorName;

    public Users(String name, String email, String password, String userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public Users(String instructorName){

        this.instructorName = instructorName;
    }



}
