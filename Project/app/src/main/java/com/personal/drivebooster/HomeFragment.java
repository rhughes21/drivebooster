package com.personal.drivebooster;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    View view;
    FirebaseAuth auth;
    Button logoutButton;
    DatabaseReference databaseRef, dbUserRef;
    Spinner instructorChoiceSpinner;
    String instructorName;
    ArrayList<String> instructorArray = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.home_fragment, container, false);
        logoutButton = view.findViewById(R.id.logout_button);
        getInstructors();
        instructorChoiceSpinner = view.findViewById(R.id.choose_instructor_spinner);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, instructorArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorChoiceSpinner.setAdapter(spinnerArrayAdapter);
        instructorChoiceSpinner.setOnItemSelectedListener(this);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return view;
    }

    public void getInstructors(){
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String uId = (String) ds.getKey();
                    String name = ds.child("name").getValue(String.class);
                    instructorArray.add(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        instructorName = item;
        Toast.makeText(getContext(), "Instructor chosen", Toast.LENGTH_SHORT).show();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Users userObj = new Users(instructorName);
        FirebaseUser firebaseUser = auth.getCurrentUser();

        dbUserRef.child(firebaseUser.getUid()).setValue(userObj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Instructor chosen", Toast.LENGTH_SHORT).show();

                            instructorChoiceSpinner.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(getContext(), "Could not choose instructor", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
