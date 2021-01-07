package com.personal.drivebooster;

public class Bookings {


    public String pupilId;
    public String instructorName;
    public String bookingTime;
    public String bookingDate;

    //Booking constructor, takes pupilid, instructors name, booking time and date
    public Bookings(String pupilId, String instructorName, String bookingTime, String bookingDate) {
        this.pupilId = pupilId;
        this.instructorName = instructorName;
        this.bookingTime = bookingTime;
        this.bookingDate = bookingDate;
    }

    //empty constructor for creating object array of bookings from firebase
    public Bookings(){
    }

}
