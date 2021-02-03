package com.personal.drivebooster;

public class Instructors {

    public String name;
    public String email;
    public String password;
    public String userType;
    public String latitude;
    public String longitude;

    //constructor for creating an instructor and pushing to firebase when registering
    public Instructors(String name, String email, String password, String userType, String latitude, String longitude) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Instructors() {
    }
}
