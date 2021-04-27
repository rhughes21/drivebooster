package com.personal.drivebooster;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InstructorDetailsFragment extends Fragment {

    View view;
    TextView userNameView, userEmailView, addressView;
    Button editDetailsButton, logoutButton, setUpTimesButton;
    DatabaseReference dbUserRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.instructor_my_details_fragment, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.VISIBLE);
        userNameView = view.findViewById(R.id.instructor_name);
        userEmailView = view.findViewById(R.id.instructor_email);
        addressView = view.findViewById(R.id.instructor_address);
        editDetailsButton = view.findViewById(R.id.update_instructor_details_button);
        logoutButton = view.findViewById(R.id.instructor_logout_button);
        setUpTimesButton = view.findViewById(R.id.instructor_set_times_button);
        getUserDetails();
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
        setUpTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTimesButtonClick();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return view;
    }


    //retrieve instructor details from firebase
    public void getUserDetails(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(userId);

        dbUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNameView.setText(snapshot.child("name").getValue(String.class));
                userEmailView.setText(snapshot.child("email").getValue(String.class));
                addressView.setText(snapshot.child("fullAddress").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //navigate to update details fragment
    public void updateUserDetails(){
        Navigation.findNavController(view).navigate(R.id.action_instructorDetailsFragment_to_instructorUpdateDetailsFragment);
    }
    //navigate to set instructor times fragment
    public void setUpTimesButtonClick(){
        Navigation.findNavController(view).navigate(R.id.action_instructorDetailsFragment_to_setInstructorTimesFragment);

    }
}
