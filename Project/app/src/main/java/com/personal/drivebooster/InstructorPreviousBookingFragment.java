package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Database;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InstructorPreviousBookingFragment extends Fragment {

    View view;
    String bookingKey;
    TextView bookingInfoDate, bookingInfoTime, pupilName, pupilAddress;
    EditText composeReview;
    DatabaseReference dbReviewRef;
    Button submitReview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.instructor_previous_booking_fragment, container, false);

        bookingInfoDate = view.findViewById(R.id.previous_booking_info_date);
        bookingInfoTime = view.findViewById(R.id.previous_booking_info_time);
        pupilName = view.findViewById(R.id.previous_booking_info_name);
        pupilAddress = view.findViewById(R.id.previous_booking_info_address);
        composeReview = view.findViewById(R.id.leave_review_edit_text);
        submitReview = view.findViewById(R.id.submit_review_button);

        pupilName.setText(getArguments().getString("userName"));
        bookingInfoDate.setText(getArguments().getString("bookingDate"));
        bookingInfoTime.setText(getArguments().getString("bookingTime"));
        pupilAddress.setText(getArguments().getString("userAddress"));
        retrieveBookingKeyFromFirebase();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLessonReview();
            }
        });
        return view;
    }

    //getter and setter for booking key
    public String getBookingKey(){
        return bookingKey;
    }

    public void setBookingKey(String bookingKey){
        this.bookingKey = bookingKey;
    }

    //retrieve booking key from database
    public void retrieveBookingKeyFromFirebase(){
        dbReviewRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        Query keyQuery = dbReviewRef.orderByChild("userAddress").equalTo(pupilAddress.getText().toString());
        final String date = bookingInfoDate.getText().toString();
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

    //update the lesson review in the database
    public void submitLessonReview(){
        dbReviewRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings").child(getBookingKey());
        String lessonReview = composeReview.getText().toString();
        dbReviewRef.child("lessonReview").setValue(lessonReview)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Review Submitted", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
