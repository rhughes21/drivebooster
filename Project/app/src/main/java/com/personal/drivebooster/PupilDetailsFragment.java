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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PupilDetailsFragment extends Fragment {

    View view;
    EditText userNameView;
    TextView userInstructorTextView, userEmailView;
    DatabaseReference  dbUserRef;
    Button logoutButton, updateDetailsButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        view = inflater.inflate(R.layout.pupil_my_details_fragment, container, false);
        userNameView = view.findViewById(R.id.user_name);
        userEmailView = view.findViewById(R.id.user_email);
        userInstructorTextView = view.findViewById(R.id.user_instructor);
        logoutButton = view.findViewById(R.id.logout_button);
        updateDetailsButton = view.findViewById(R.id.update_user_details_button);
        getUserDetails();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                System.exit(0);
            }
        });

        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog();
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
                userInstructorTextView.setText(snapshot.child("instructorName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateUserDetails(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUserRef.child(userId).child("name").setValue(userNameView.getText().toString());
    }

    public void updateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure you want to update your name?");
        builder.setMessage("updating details will return you to the home screen as the database has updated your information");
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateUserDetails();
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

}
