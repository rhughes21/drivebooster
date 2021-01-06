package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PupilDetailsFragment extends Fragment {

    View view;
    TextView userNameView, userEmailView, yourDetailsView, userInstructorTextView;
    DatabaseReference  dbUserRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pupil_my_details_fragment, container, false);
        yourDetailsView = view.findViewById(R.id.your_details_view);
        userNameView = view.findViewById(R.id.user_name);
        userEmailView = view.findViewById(R.id.user_email);
        userInstructorTextView = view.findViewById(R.id.user_instructor);
        getUserDetails();
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
}
