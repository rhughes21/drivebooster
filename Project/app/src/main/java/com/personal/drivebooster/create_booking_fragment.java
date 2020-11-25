package com.personal.drivebooster;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.ValueEventListener;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class create_booking_fragment extends Fragment  {
        View view;
        FirebaseUser currentUser;
        String userId, bookingPupilId, existingBookingDate;
        DatabaseReference dbUserRef,databaseBookingRef, databaseCreateBookingRef;
        DayScrollDatePicker dayPicker;
        TextView datePickedText, timePickedText;
        ListView timeListView;
        String dateString, timeString, instructorName;
        Button createBookingButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_booking_fragment, container, false);
        initViews();
        getDateValue(dateString);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

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
        setUpPicker();
        setUpListView();

    }

    private void setUpPicker(){
        dayPicker.setStartDate(2, 11, 2020);
        dayPicker.setEndDate(10,3,2021);
    }

    private String getDateValue(String dStr){
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
                    dateString = dateFormat.format(date);
                    datePickedText.setText(dateString);
                }
            }
        });
        return dateString;
    }

    public List<String> setUpTimes(){
        ArrayList<String> times = new ArrayList<String>();
        for(int i=9; i < 20; i++){
            String time = i+ ":00";
            times.add(time);
        }
        return times;
    }

    public void setUpListView(){
        setUpTimes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, setUpTimes());
        timeListView.setAdapter(adapter);
    }

    public String getInstName(){
        return instructorName;
    }

    public void setInstName(String instructorName){
        this.instructorName = instructorName;
    }

    public String getExistingBookingDate(){
        return existingBookingDate;
    }

    public void setExistingBookingDate(String existingBookingDate){
        this.existingBookingDate = existingBookingDate;
    }

    public String getBookingPupilId(){
        return bookingPupilId;
    }

    public void setBookingPupilId(String bookingPupilId){
        this.bookingPupilId = bookingPupilId;
    }
    public void createBooking(){

        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");

        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataSnapshot ds = snapshot.child("instructorName");
                setInstName(ds.getValue(String.class));
                checkDatabaseForUserBooking();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public void checkDatabaseForBookingDate(){
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseCreateBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    setExistingBookingDate(ds.child("bookingDate").getValue(String.class));

                    if (getExistingBookingDate().equals(dateString)){
                        Toast.makeText(getContext(), "You already have an upcoming booking", Toast.LENGTH_LONG).show();
                    }else{
                        Bookings bookingObj = new Bookings(userId, getInstName(), timeString, dateString);

                        databaseCreateBookingRef.push().setValue(bookingObj)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Booking complete", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
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

    public void checkDatabaseForUserBooking(){
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                    setBookingPupilId(ds.child("pupilId").getValue(String.class));

                if(getBookingPupilId().equals(userId)){
                    checkDatabaseForBookingDate();
                }else{
                    createBooking();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}