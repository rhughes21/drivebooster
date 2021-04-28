package com.personal.drivebooster;

import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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
    DatabaseReference dbEditRef, dbUserRef, dbEditDetailsInBooking, dbEditDetailsInPreviousBooking;
    Query updateDetailsInBooking, updateDetailsInPreviousBooking;
    FirebaseAuth auth;

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
        this.fullAddress = fullAddress;
        this.phoneNumber = phoneNumber;
    }

    //empty constructor for calling methods
    public Users() {
    }

    //method to update name and address in the database
    public void updateNameAndAddress(final String name, final String address, Double latitude, Double longitude) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbEditRef.child("name").setValue(name);
        dbEditRef.child("fullAddress").setValue(address);
        dbEditRef.child("latitude").setValue(latitude);
        dbEditRef.child("longitude").setValue(longitude);

        dbEditDetailsInBooking = FirebaseDatabase.getInstance().getReference().child("Booking");
        updateDetailsInBooking = dbEditDetailsInBooking.orderByChild("pupilId").equalTo(userId);
        updateDetailsInBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        Log.i("USERKEY", Objects.requireNonNull(userSnap.getKey()));
                        dbEditDetailsInBooking.child(userSnap.getKey()).child("userName").setValue(name);
                        dbEditDetailsInBooking.child(userSnap.getKey()).child("userAddress").setValue(address);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        updatePupilNameInPreviousBookings(userId, name);
    }

    //method to update the instructor field in the user entry when they choose a different instructor
    public void chooseInstructor(Button chooseInstructorButton, RecyclerView instructorRecycler, boolean hasPickedInstructor, String instId, String instructorName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUserRef.child(userId).child("instructorName").setValue(instructorName);
        dbUserRef.child(userId).child("instructorId").setValue(instId);
        chooseInstructorButton.setVisibility(View.GONE);
        instructorRecycler.setVisibility(View.GONE);
        hasPickedInstructor = true;
    }

    //method to update name in previous bookings
    public void updatePupilNameInPreviousBookings(String pupilId, final String pupilName) {
        dbEditDetailsInPreviousBooking = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        updateDetailsInPreviousBooking = dbEditDetailsInPreviousBooking.orderByChild("pupilId").equalTo(pupilId);
        updateDetailsInPreviousBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        Log.i("USERKEY", userSnap.getKey());
                        dbEditDetailsInPreviousBooking.child(userSnap.getKey()).child("userName").setValue(pupilName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}
