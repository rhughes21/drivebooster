package com.personal.drivebooster;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    DatabaseReference dbEditRef;
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

    public void deleteBooking(String bookingKey){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Booking").child(bookingKey);
        dbEditRef.removeValue();
    }
}
