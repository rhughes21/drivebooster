package com.personal.drivebooster;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class InstructorPupilDetailsFragment extends Fragment {

    View view;
    TextView pupilName, pupilAddress, pupilEmail, pupilDistance;
    double pupilLat, pupilLong, myLng, myLat, distance;
    DatabaseReference dbRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.instructor_pupil_details_fragment, container, false);
        initViews();


        return view;
    }

    public void initViews(){
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.GONE);
        pupilName = view.findViewById(R.id.pupil_details_name);
        pupilEmail = view.findViewById(R.id.pupil_details_email);
        pupilAddress = view.findViewById(R.id.pupil_details_address);
        pupilDistance = view.findViewById(R.id.pupil_details_distance);

        pupilName.setText(getArguments().getString("pupilName"));
        pupilEmail.setText(getArguments().getString("pupilEmail"));
        pupilAddress.setText(getArguments().getString("pupilAddress"));
        pupilLat = getArguments().getDouble("latitude");
        pupilLong = getArguments().getDouble("longitude");

        getInstructorLatLng();
    }


    public void calculateDistanceToUser(double lat1, double long1, double lat2,double  long2){

        double longDifference = long1 - long2;


        double distance = Math.sin(degreeToRadian(lat1))
                * Math.sin(degreeToRadian(lat2))
                + Math.cos(degreeToRadian(lat1))
                * Math.cos(degreeToRadian(lat2))
                * Math.cos(degreeToRadian(longDifference));
        distance = Math.acos(distance);
        distance = radianToDegree(distance);
        distance =distance * 60 * 1.1515;

        pupilDistance.setText(String.format(Locale.UK,"%2f Miles", distance));
    }

    private double radianToDegree(double distance) {
        return (distance * 180.0/ Math.PI);
    }

    private double degreeToRadian(double lat1) {
        return (lat1 * Math.PI/180);
    }

    public Double getLng(){
        return myLng;
    }

    public void setMyLng(Double myLng){
        this.myLng = myLng;
    }

    public Double getLat(){
        return myLat;
    }

    public void setMyLat(Double myLat){
        this.myLat = myLat;
    }

    public void getInstructorLatLng(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setMyLat(snapshot.child("latitude").getValue(Double.class));
                setMyLng(snapshot.child("longitude").getValue(Double.class));
                calculateDistanceToUser(pupilLat, getLng(), getLat(), pupilLong);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
