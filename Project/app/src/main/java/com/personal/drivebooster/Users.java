package com.personal.drivebooster;

import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Users {

    public String name;
    public String email;
    public String password;
    public String userType;
    public String instructorName;
    public String instructorId;
    public Double latitude;
    public Double longitude;
    public String fullAddress;
    public String phoneNumber;
    DatabaseReference dbEditRef, dbUserRef;
    //constructor for creating a new user and storing in firebase when registering
    public Users(String name, String email, String password, String userType, String instructorName, String instructorId, Double latitude, Double longitude, String fullAddress, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.instructorName = instructorName;
        this.instructorId = instructorId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress= fullAddress;
        this.phoneNumber = phoneNumber;
    }

    public Users(){}

    public void updateNameAndAddress(String uuid, String name, String address, Double latitude, Double longitude){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uuid);
        dbEditRef.child("name").setValue(name);
        dbEditRef.child("fullAddress").setValue(address);
        dbEditRef.child("latitude").setValue(latitude);
        dbEditRef.child("longitude").setValue(longitude);
    }

    public void chooseInstructor(Button chooseInstructorButton, RecyclerView instructorRecycler, boolean hasPickedInstructor, String instId, String instructorName){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUserRef.child(userId).child("instructorName").setValue(instructorName);
        dbUserRef.child(userId).child("instructorId").setValue(instId);
        chooseInstructorButton.setVisibility(View.GONE);
        instructorRecycler.setVisibility(View.GONE);
        hasPickedInstructor = true;
    }

}
