package com.personal.drivebooster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class PupilDetailsFragment extends Fragment {

    View view;
    TextView userNameView;
    TextView userInstructorTextView, userEmailView, addressView;
    DatabaseReference  dbUserRef;
    Button logoutButton, updateDetailsButton, changeInstructorButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.VISIBLE);
        view = inflater.inflate(R.layout.pupil_my_details_fragment, container, false);
        userNameView = view.findViewById(R.id.user_name);
        userEmailView = view.findViewById(R.id.user_email);
        userInstructorTextView = view.findViewById(R.id.user_instructor);
        addressView = view.findViewById(R.id.user_address);
        logoutButton = view.findViewById(R.id.logout_button);
        changeInstructorButton = view.findViewById(R.id.change_instructor_button);
        updateDetailsButton = view.findViewById(R.id.update_user_details_button);
        getUserDetails();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        changeInstructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInstructor();
            }
        });
        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
        return view;
    }

    public void getUserDetails(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        dbUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNameView.setText(snapshot.child("name").getValue(String.class));
                userEmailView.setText(snapshot.child("email").getValue(String.class));
                addressView.setText(snapshot.child("fullAddress").getValue(String.class));
                userInstructorTextView.setText(snapshot.child("instructorName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void changeInstructor(){
        Navigation.findNavController(view).navigate(R.id.action_navigation_my_details_to_chooseInstructorFragment);
    }
    public void updateUserDetails(){
        Navigation.findNavController(view).navigate(R.id.action_navigation_my_details_to_editDetailsFragment);

    }

}
