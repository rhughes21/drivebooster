package com.personal.drivebooster;

public class Instructors {

    public String name;
    public String email;
    public String password;
    public String userType;

    //constructor for creating an instructor and pushing to firebase when registering
    public Instructors(String name, String email, String password, String userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

}
