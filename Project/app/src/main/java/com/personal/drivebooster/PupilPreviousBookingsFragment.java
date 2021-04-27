package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PupilPreviousBookingsFragment extends Fragment implements PreviousBookingAdapter.onPreviousBookingListener{

    View view;
    TextView review, reviewHeader;
    final List<Bookings> previousBookingsFromFirebase = new ArrayList<Bookings>();
    RecyclerView previousBookingsRecycler;
    PreviousBookingAdapter previousBookingAdapter;
    DatabaseReference databaseBookingRef, dbReviewRef;
    String bookingKey;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.pupil_previous_bookings_fragment, container, false);

        getPreviousBookings();
        previousBookingsRecycler = view.findViewById(R.id.previous_bookings_recycler);
        LinearLayoutManager previousLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        previousBookingAdapter = new PreviousBookingAdapter(previousBookingsFromFirebase, this);
        previousBookingsRecycler.setLayoutManager(previousLinearLayoutManager);
        previousBookingsRecycler.setAdapter(previousBookingAdapter);
        reviewHeader = view.findViewById(R.id.review_header);
        review = view.findViewById(R.id.review_text);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);

        return view;
    }

    //retrieve previous bookings from database
    public void getPreviousBookings(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if(bookings.pupilId.equals(userId) ) {
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

    //onClick for a previous booking item, shows booking review
    @Override
    public void onPreviousBookingClick(int position) {
        dbReviewRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        Query keyQuery = dbReviewRef.orderByChild("userAddress").equalTo(previousBookingsFromFirebase.get(position).userAddress);
        final String date = previousBookingsFromFirebase.get(position).bookingDate;
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    String dateSnapshot = childSnapshot.child("bookingDate").getValue(String.class);
                    String reviewSnap = childSnapshot.child("lessonReview").getValue(String.class);
                    if(dateSnapshot.equals(date)){
                        setBookingKey(childSnapshot.getKey());
                        review.setText(reviewSnap + "\n" + getString(R.string.remember_to_book));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //getter and setter for bookinh key
    public String getBookingKey(){
        return bookingKey;
    }

    public void setBookingKey(String bookingKey){
        this.bookingKey = bookingKey;
    }

}
