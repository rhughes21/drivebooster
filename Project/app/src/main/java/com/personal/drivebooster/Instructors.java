package com.personal.drivebooster;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Instructors {

    public String name;
    public String email;
    public String password;
    public String userType;
    public Double latitude;
    public Double longitude;
    public String fullAddress;
    DatabaseReference dbEditRef, dbUpdateNameRef, dbEditBookingNameRef, dbEditPreviousBooking;
    Query updateNameInUserTables, updateNameInBookings, updateNameInPrevious;

    //constructor for creating an instructor and pushing to firebase when registering
    public Instructors(String name, String email, String password, String userType, Double latitude, Double longitude, String fullAddress) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress = fullAddress;
    }

    public Instructors() {
    }

    public void updateNameAndAddress(String uuid, final String name, String address, Double latitude, Double longitude){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(uuid);
        dbEditRef.child("name").setValue(name);
        dbEditRef.child("fullAddress").setValue(address);
        dbEditRef.child("latitude").setValue(latitude);
        dbEditRef.child("longitude").setValue(longitude);

        dbUpdateNameRef = FirebaseDatabase.getInstance().getReference().child("Users");
        updateNameInUserTables = dbUpdateNameRef.orderByChild("instructorId").equalTo(uuid);
        updateNameInUserTables.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot userSnap: snapshot.getChildren()){
                        Log.i("USERKEY", userSnap.getKey());
                        dbUpdateNameRef.child(userSnap.getKey()).child("instructorName").setValue(name);
                        updateInstructorNameInBooking(userSnap.getKey(), name);
                        updateInstructorNameInPreviousBooking(userSnap.getKey(), name);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void updateInstructorNameInBooking(String pupilId, final String instructorName){
        dbEditBookingNameRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        updateNameInBookings = dbEditBookingNameRef.orderByChild("pupilId").equalTo(pupilId);
        updateNameInBookings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot userSnap: snapshot.getChildren()){
                        Log.i("USERKEY", userSnap.getKey());
                        dbEditBookingNameRef.child(userSnap.getKey()).child("instructorName").setValue(instructorName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateInstructorNameInPreviousBooking(String pupilId, final String instructorName){
        dbEditPreviousBooking = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        updateNameInPrevious = dbEditPreviousBooking.orderByChild("pupilId").equalTo(pupilId);
        updateNameInPrevious.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot userSnap: snapshot.getChildren()){
                        Log.i("USERKEY", userSnap.getKey());
                        dbEditPreviousBooking.child(userSnap.getKey()).child("instructorName").setValue(instructorName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
