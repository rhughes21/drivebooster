package com.personal.drivebooster;

import android.location.Location;

public class Users {

    public String name;
    public String email;
    public String password;
    public String userType;
    public String instructorName;
    public String latitude;
    public String longitude;

    //constructor for creating a new user and storing in firebase when registering
    public Users(String name, String email, String password, String userType, String instructorName, String latitude, String longitude) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.instructorName = instructorName;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
