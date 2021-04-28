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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.os.BundleKt.bundleOf;

public class InstructorHomeFragment extends Fragment implements CustomBookingsAdapter.onBookingListener, PreviousBookingAdapter.onPreviousBookingListener {

    View view;
    TextView upcomingBookingsHeader, previousBookingHeader, noUpcomingBookings, noPreviousBookings;
    RecyclerView upcomingBookingsRecycler, previousBookingRecycler;
    String instructorName;
    CustomBookingsAdapter upcomingBookingsAdapter;
    PreviousBookingAdapter previousBookingAdapter;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    final List<Bookings> previousBookingsFromFirebase = new ArrayList<Bookings>();
    DatabaseReference databaseBookingRef, instructorRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.instructor_home_fragment, container, false);
        bookingsFromFirebase.clear();
        previousBookingsFromFirebase.clear();
        getMyName();
        getBookings();
        getPreviousBookings();
        upcomingBookingsHeader = view.findViewById(R.id.instructor_bookings_header);
        upcomingBookingsRecycler = view.findViewById(R.id.instructor_bookings_recycler);
        previousBookingHeader = view.findViewById(R.id.instructor_previous_bookings_header);
        previousBookingRecycler = view.findViewById(R.id.instructor_previous_bookings_recycler);
        noPreviousBookings = view.findViewById(R.id.instructor_no_previous_bookings);
        noUpcomingBookings = view.findViewById(R.id.instructor_no_upcoming_bookings);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerPrevious = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        upcomingBookingsRecycler.setLayoutManager(linearLayoutManager);
        upcomingBookingsAdapter = new CustomBookingsAdapter(bookingsFromFirebase, this);
        upcomingBookingsRecycler.setAdapter(upcomingBookingsAdapter);

        previousBookingRecycler.setLayoutManager(linearLayoutManagerPrevious);
        previousBookingAdapter = new PreviousBookingAdapter(previousBookingsFromFirebase, this);
        previousBookingRecycler.setAdapter(previousBookingAdapter);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.VISIBLE);


        return view;
    }

    //retrieve upcoming bookings from firebase
    public void getBookings() {
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if (bookings.instructorName.equals(getInstName())) {
                            bookingsFromFirebase.add(bookings);
                            upcomingBookingsAdapter.notifyDataSetChanged();
                            noUpcomingBookings.setVisibility(View.GONE);
                            upcomingBookingsRecycler.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //retrieve previous bookings from firebase
    public void getPreviousBookings() {
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if (bookings.instructorName.equals(getInstName())) {
                            previousBookingsFromFirebase.add(bookings);
                            previousBookingAdapter.notifyDataSetChanged();
                            noPreviousBookings.setVisibility(View.GONE);
                            previousBookingRecycler.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //retrieve instructor name from firebase
    public void getMyName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        instructorRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(userId);
        instructorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.equals(null)) {
                    setInstName(snapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //getters and setters for instructor name
    public String getInstName() {
        return instructorName;
    }

    public void setInstName(String instructorName) {
        this.instructorName = instructorName;
    }


    //navigate to booking info fragment and pass booking information
    @Override
    public void onBookingClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("bookingDate", bookingsFromFirebase.get(position).bookingDate);
        bundle.putString("bookingTime", bookingsFromFirebase.get(position).bookingTime);
        bundle.putString("userAddress", bookingsFromFirebase.get(position).userAddress);
        bundle.putString("userName", bookingsFromFirebase.get(position).userName);
        bundle.putString("phoneNo", bookingsFromFirebase.get(position).phoneNumber);
        bundle.putString("pupilId", bookingsFromFirebase.get(position).pupilId);
        Navigation.findNavController(view).navigate(R.id.action_instructor_home_nav_fragment_to_instructorBookingInfoFragment, bundle);

    }

    //navigate to previous booking fragment and pass previous booking information
    @Override
    public void onPreviousBookingClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("bookingDate", previousBookingsFromFirebase.get(position).bookingDate);
        bundle.putString("bookingTime", previousBookingsFromFirebase.get(position).bookingTime);
        bundle.putString("userAddress", previousBookingsFromFirebase.get(position).userAddress);
        bundle.putString("userName", previousBookingsFromFirebase.get(position).userName);
        Navigation.findNavController(view).navigate(R.id.action_instructor_home_nav_fragment_to_instructorPreviousBookingFragment, bundle);

    }
}
