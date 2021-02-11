package com.personal.drivebooster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class InstructorHomeFragment extends Fragment implements View.OnClickListener{

    View view;
    Button logOutButton, addTimesButton;
    TextView upcomingBookingsHeader, previousBookingHeader;
    RecyclerView upcomingBookingsRecycler, previousBookingRecycler;
    String instructorName;
    CustomBookingsAdapter upcomingBookingsAdapter, previousBookingAdapter;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    final List<Bookings> previousBookingsFromFirebase = new ArrayList<Bookings>();
    DatabaseReference databaseBookingRef, instructorRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.instructor_home_fragment, container, false);
        getMyName();
        getBookings();
        getPreviousBookings();
        addTimesButton = view.findViewById(R.id.show_times_fragment);
        logOutButton = view.findViewById(R.id.instructor_logout_button);
        upcomingBookingsHeader = view.findViewById(R.id.instructor_bookings_header);
        upcomingBookingsRecycler = view.findViewById(R.id.instructor_bookings_recycler);
        previousBookingHeader = view.findViewById(R.id.instructor_previous_bookings_header);
        previousBookingRecycler = view.findViewById(R.id.instructor_previous_bookings_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerPrevious = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        upcomingBookingsRecycler.setLayoutManager(linearLayoutManager);
        upcomingBookingsAdapter = new CustomBookingsAdapter(bookingsFromFirebase);
        upcomingBookingsRecycler.setAdapter(upcomingBookingsAdapter);

        previousBookingRecycler.setLayoutManager(linearLayoutManagerPrevious);
        previousBookingAdapter = new CustomBookingsAdapter(previousBookingsFromFirebase);
        previousBookingRecycler.setAdapter(previousBookingAdapter);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                System.exit(0);
            }
        });

        addTimesButton.setOnClickListener(this);


        return view;
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
                        if(bookings.instructorName.equals(getInstName()) ) {
                            bookingsFromFirebase.add(bookings);
                            upcomingBookingsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPreviousBookings(){
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if(bookings.instructorName.equals(getInstName()) ) {
                            previousBookingsFromFirebase.add(bookings);
                            previousBookingAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyName(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        instructorRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(userId);
        instructorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.equals(null)){
                    setInstName(snapshot.child("name").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getInstName(){
        return instructorName;
    }

    public void setInstName(String instructorName){
        this.instructorName = instructorName;
    }
    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_instructor_home_nav_fragment_to_setInstructorTimesFragment);
    }
}
