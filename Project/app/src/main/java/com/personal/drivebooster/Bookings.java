package com.personal.drivebooster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Bookings {


    public String pupilId;
    public String instructorName;
    public String bookingTime;
    public String bookingDate;
    public String userName;
    public String userAddress;
    public String dateDay;
    public String lessonReview;
    public String phoneNumber;
    DatabaseReference dbEditRef, getBookingsRef;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    //Booking constructor, takes pupilid, instructors name, booking time and date
    public Bookings(String pupilId, String userName, String instructorName, String bookingTime, String bookingDate, String userAddress, String dateDay, String lessonReview, String phoneNumber) {
        this.pupilId = pupilId;
        this.userName = userName;
        this.instructorName = instructorName;
        this.bookingTime = bookingTime;
        this.bookingDate = bookingDate;
        this.userAddress = userAddress;
        this.dateDay = dateDay;
        this.lessonReview = lessonReview;
        this.phoneNumber = phoneNumber;
    }

    //empty constructor for creating object array of bookings from firebase
    public Bookings(){
    }

    //method to delete bookings from the database
    public void deleteBooking(String bookingKey){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Booking").child(bookingKey);
        dbEditRef.removeValue();
    }

    //method to return a bookings object from database
    public List<Bookings> returnBookingsFromFirebase(){
        getBookingsRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        getBookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        bookingsFromFirebase.add(bookings);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return bookingsFromFirebase;
    }
}
