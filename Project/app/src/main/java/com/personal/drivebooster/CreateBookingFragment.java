package com.personal.drivebooster;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateBookingFragment extends Fragment  {
        View view;
        FirebaseUser currentUser;
        String userId;
        DatabaseReference dbUserRef,databaseBookingRef, databaseCreateBookingRef, databaseScheduleRef;
        DayScrollDatePicker dayPicker;
        TextView datePickedText, timePickedText, noTimeAvailableView;
        ListView timeListView;
        String dateString, timeString, instructorName, dateDay, instructorId;
        Button createBookingButton;
        boolean canBookLesson, canUseTime;
        Date currentDate;
        int day, month, year;
        final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
        final List<String> instructorScheduleFromFirebase = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        view = inflater.inflate(R.layout.fragment_create_booking_fragment, container, false);

        currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        day = cal.get(Calendar.DATE);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
        initViews();
        getDateValue(dateString);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        getBookings();
        getInstructorIdFromFirebase();
        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeString = parent.getItemAtPosition(position).toString();
                timePickedText.setText(timeString);
            }
        });

        createBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBooking();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void initViews(){
        dayPicker = view.findViewById(R.id.horizontal_calendar);
        datePickedText = view.findViewById(R.id.value_text_view);
        timeListView = view.findViewById(R.id.time_list_view);
        timePickedText = view.findViewById(R.id.time_value_text_view);
        createBookingButton = view.findViewById(R.id.create_booking_button);
        noTimeAvailableView = view.findViewById(R.id.no_times_available);
        setUpPicker();

    }

    //method to set the start and end date shown on the calendar
    private void setUpPicker(){
        dayPicker.setStartDate(10, month, year);
        dayPicker.setEndDate(day,month + 6,year);
    }

    //method to get the date chosen by user
    private String getDateValue(String dStr){
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {

                if(date != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    dateString = dateFormat.format(date);
                    dateDay = sdf.format(date);
                    datePickedText.setText(dateDay);
                    instructorScheduleFromFirebase.clear();
                    getInstructorSchedule();
                }
            }
        });
        return dateString;
    }

    //method to set up the ListView using the times array
    public void setUpListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, instructorScheduleFromFirebase);
        timeListView.setAdapter(adapter);
    }

    //getter and setter for instructor name from firebase
    public String getInstName(){
        return instructorName;
    }

    public void setInstName(String instructorName){
        this.instructorName = instructorName;
    }

    public String getInstId(){
        return instructorId;
    }

    public void setInstId(String instructorId){
        this.instructorId = instructorId;
    }



    //method used to create a booking and push to firebase. Also include checks for bookings already in the database.
    public void createBooking(){

        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");

        databaseCreateBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot ds = snapshot.child("instructorName");
                DataSnapshot userNameFb = snapshot.child("name");
                DataSnapshot userAddressFb = snapshot.child("fullAddress");
                String userName = userNameFb.getValue(String.class);
                String userAddress = userAddressFb.getValue(String.class);
                setInstName(ds.getValue(String.class));
                canBookLesson = true;
                for(int i=0; i < bookingsFromFirebase.size(); i++){
                    if(bookingsFromFirebase.get(i).bookingDate.equals(dateString) && bookingsFromFirebase.get(i).pupilId.equals(userId)){
                        Toast.makeText(getContext(), "You have a booking already on that day", Toast.LENGTH_SHORT).show();
                        canBookLesson = false;
                    }else if (bookingsFromFirebase.get(i).bookingDate.equals(dateString) && bookingsFromFirebase.get(i).bookingTime.equals(timeString)
                            && bookingsFromFirebase.get(i).instructorName.equals(getInstName())){
                        Toast.makeText(getContext(), "Your instructor already has a booking at that time", Toast.LENGTH_SHORT).show();
                        canBookLesson = false;
                    }
                }

                if(canBookLesson){
                    Bookings bookingObj = new Bookings(userId, userName, getInstName(), timeString, dateString, userAddress, dateDay);

                    if(dateString == null || timeString == null || dateDay == null){
                        Toast.makeText(getContext(), "Make sure you have chosen a date and time", Toast.LENGTH_SHORT).show();
                    }else if(getInstName().equals("not chosen")){
                        Toast.makeText(getContext(), "Please choose an instructor on the home screen before booking", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseCreateBookingRef.push().setValue(bookingObj)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Booking complete", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "could  not complete booking", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }


    //method to get all current bookings from firebase and add these to an Object array
    public void getBookings(){
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
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
    }

    public void getInstructorIdFromFirebase(){
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setInstId(snapshot.child("instructorId").getValue(String.class));
                setInstName(snapshot.child("instructorName").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getInstructorSchedule(){
        databaseScheduleRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(getInstId()).child("Times").child(dateDay).child("times");
        databaseScheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        String t = child.getValue(String.class);
                        canUseTime = true;
                        for(int i = 0; i< bookingsFromFirebase.size(); i++){
                            if (bookingsFromFirebase.get(i).bookingDate.equals(dateString)) {
                                if (bookingsFromFirebase.get(i).instructorName.equals(getInstName()) && bookingsFromFirebase.get(i).bookingTime.equals(t)) {
                                    Log.i("INSTRUCTOR HAS THE TIME", t);
                                    canUseTime = false;
                                    break;
                                } else {
                                    canUseTime = true;
                                    break;
                                }
                            }
                        }
                        if(canUseTime){
                            instructorScheduleFromFirebase.add(t);
                        }
                    }

                    timeListView.setVisibility(View.VISIBLE);
                    noTimeAvailableView.setVisibility(View.GONE);
                    setUpListView();
                }else{
                    noTimeAvailableView.setVisibility(View.VISIBLE);
                    timeListView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}