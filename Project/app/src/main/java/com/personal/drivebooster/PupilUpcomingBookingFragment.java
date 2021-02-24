package com.personal.drivebooster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PupilUpcomingBookingFragment extends Fragment {

    View view;
    FirebaseUser currentUser;
    String userId, instructorId, dateString, dateDay, bookingKey, timeString;
    ListView timeListView;
    boolean canUseTime;
    TextView bookingDay, bookingDate, bookingTime, userAddress, instructorName, noTimeAvailableView;
    Button editBookingButton, cancelEdit, submitEditButton, deleteButton;
    Date currentDate;
    Calendar calendar;
    DayScrollDatePicker dayPicker;
    int day, month, year;
    DatabaseReference databaseBookingRef, databaseScheduleRef, dbUserRef, dbKeyRef, dbEditRef;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    final List<String> instructorScheduleFromFirebase = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.pupil_edit_booking_fragment, container, false);
        initViews();
        bookingDay.setText(getArguments().getString("dateDay"));
        bookingDate.setText(getArguments().getString("bookingDate"));
        bookingTime.setText(getArguments().getString("bookingTime"));
        userAddress.setText(getArguments().getString("userAddress"));
        instructorName.setText(getArguments().getString("instructorName"));
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        getBookings();
        retrieveBookingKeyFromFirebase();
        getInstructorIdFromFirebase();

        editBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPicker();
                showEditViews();
            }
        });
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefaultButtons();
            }
        });

        timeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeString = parent.getItemAtPosition(position).toString();
            }
        });

        submitEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBooking();
                showDefaultButtons();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog();
            }
        });
        return view;
    }

    public void initViews(){
        bookingDate = view.findViewById(R.id.pupil_booking_info_date);
        bookingDay = view.findViewById(R.id.pupil_booking_info_date_day);
        bookingTime = view.findViewById(R.id.pupil_booking_info_time);
        userAddress = view.findViewById(R.id.pupil_address);
        instructorName = view.findViewById(R.id.instructor_name);
        editBookingButton = view.findViewById(R.id.edit_booking_button);
        cancelEdit = view.findViewById(R.id.cancel_edit);
        noTimeAvailableView = view.findViewById(R.id.booking_info_no_times_available);
        submitEditButton = view.findViewById(R.id.submit_edit);
        timeListView = view.findViewById(R.id.info_time_list_view);
        dayPicker = view.findViewById(R.id.info_horizontal_calendar);
        deleteButton = view.findViewById(R.id.delete_booking_button);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);
    }

    //method to set the start and end date shown on the calendar
    private void setUpPicker(){
        dayPicker.setStartDate(10, month, year);
        dayPicker.setEndDate(day,month + 6,year);
    }

    public void updateBooking(){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Booking").child(getBookingKey());
        dbEditRef.child("bookingDate").setValue(dateString);
        dbEditRef.child("bookingTime").setValue(timeString);
        dbEditRef.child("dateDay").setValue(dateDay)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Booking updated", Toast.LENGTH_SHORT).show();
                    }
                });
        bookingDate.setText(dateString);
        bookingTime.setText(timeString);
        bookingDay.setText(dateDay);
    }
    private String getDateValue(String dStr){
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {

                if(date != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    dateString = dateFormat.format(date);
                    dateDay = sdf.format(date);
                    instructorScheduleFromFirebase.clear();
                    getInstructorSchedule();
                }
            }
        });
        return dateString;
    }
    public void showEditViews(){

        currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
        try {
            final Date bDate = dateFormat.parse(bookingDate.getText().toString());
                    if(bDate.equals(currentDate)) {
                        noTimeAvailableView.setVisibility(View.VISIBLE);
                    }else if(bDate.after(currentDate)){
                        dayPicker.setVisibility(View.VISIBLE);
                        getDateValue(dateString);
                        editBookingButton.setVisibility(View.GONE);
                        timeListView.setVisibility(View.VISIBLE);
                        cancelEdit.setVisibility(View.VISIBLE);
                        submitEditButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                    }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


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
    public void setUpListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, instructorScheduleFromFirebase);
        timeListView.setAdapter(adapter);
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
                                if (bookingsFromFirebase.get(i).instructorName.equals(instructorName.getText().toString()) && bookingsFromFirebase.get(i).bookingTime.equals(t)) {
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

    public String getInstId(){
        return instructorId;
    }

    public void setInstId(String instructorId){
        this.instructorId = instructorId;
    }


    public void getInstructorIdFromFirebase(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setInstId(snapshot.child("instructorId").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public String getBookingKey(){
        return bookingKey;
    }

    public void setBookingKey(String bookingKey){
        this.bookingKey = bookingKey;
    }
    public void retrieveBookingKeyFromFirebase(){
        dbKeyRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        Query keyQuery = dbKeyRef.orderByChild("userAddress").equalTo(userAddress.getText().toString());
        final String date = bookingDate.getText().toString();
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    String dateSnapshot = childSnapshot.child("bookingDate").getValue(String.class);
                    if(dateSnapshot.equals(date)){
                        setBookingKey(childSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void deleteBooking(){
        dbEditRef = FirebaseDatabase.getInstance().getReference().child("Booking").child(getBookingKey());
        dbEditRef.removeValue();
    }

    public void deleteDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.confirm_deletion));
            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteBooking();
                        }
                    });
            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog updateAlert = builder.create();
            updateAlert.show();
            updateAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.negative_alert_button));
            updateAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.positive_alert_button));
    }

    public void showDefaultButtons(){
        dayPicker.setVisibility(View.GONE);
        timeListView.setVisibility(View.GONE);
        cancelEdit.setVisibility(View.GONE);
        submitEditButton.setVisibility(View.GONE);
        editBookingButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }


}
