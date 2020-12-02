package com.personal.drivebooster;

public class Users {

    public String name;
    public String email;
    public String password;
    public String userType;
    public String instructorName;

    //constructor for creating a new user and storing in firebase when registering
    public Users(String name, String email, String password, String userType,String instructorName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.instructorName = instructorName;
    }


}
