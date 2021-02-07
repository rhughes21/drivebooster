package com.personal.drivebooster;

public class Bookings {


    public String pupilId;
    public String instructorName;
    public String bookingTime;
    public String bookingDate;
    public String userName;
    public String userAddress;
    public String dateDay;

    //Booking constructor, takes pupilid, instructors name, booking time and date
    public Bookings(String pupilId, String userName, String instructorName, String bookingTime, String bookingDate, String userAddress, String dateDay) {
        this.pupilId = pupilId;
        this.userName = userName;
        this.instructorName = instructorName;
        this.bookingTime = bookingTime;
        this.bookingDate = bookingDate;
        this.userAddress = userAddress;
        this.dateDay = dateDay;
    }

    //empty constructor for creating object array of bookings from firebase
    public Bookings(){
    }

}
