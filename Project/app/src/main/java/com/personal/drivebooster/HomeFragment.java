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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    Button chooseInstructorButton;
    TextView noInstructorsText;
    DatabaseReference databaseRef, dbUserRef;
    Spinner instructorChoiceSpinner;
    Boolean hasPickedInstructor = false;
    boolean instructorAvailable;
    String instructorName;
    ArrayList<String> instructorArray = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.home_fragment, container, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        chooseInstructorButton = view.findViewById(R.id.instructor_choice_button);
        auth = FirebaseAuth.getInstance();
        instructorChoiceSpinner = view.findViewById(R.id.choose_instructor_spinner);
        noInstructorsText = view.findViewById(R.id.no_instructors_available);
        getInstructors();
        instructorChoiceSpinner.setOnItemSelectedListener(this);

        chooseInstructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInstructor();
            }
        });
        return view;
    }

    //getter and setter for instructor name
    public String getInstName(){
        return instructorName;
    }

    public void setInstName(String instructorName){
        this.instructorName = instructorName;
    }

    public boolean getInstructorAvailable(){
        return instructorAvailable;
    }
    public void setInstructorAvailable(boolean instructorAvailable){
        this.instructorAvailable = instructorAvailable;
    }

    //method to retrieve all instructor names from firebase. Checks if instructors are available for selection.
    public void getInstructors(){
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, instructorArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorChoiceSpinner.setAdapter(spinnerArrayAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    noInstructorsText.setVisibility(View.GONE);
                    setInstructorAvailable(true);
                    checkInstructorChosen();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String name = ds.child("name").getValue(String.class);
                        instructorArray.add(name);
                        spinnerArrayAdapter.notifyDataSetChanged();
                    }
                }else if(!snapshot.hasChildren()){
                    setInstructorAvailable(false);
                    noInstructorsText.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //method to set whether or not the choose instructor button and spinner should be shown
    public void setChooseInstructorVisibility(){
        if(getInstName().equals("not chosen") && getInstructorAvailable()){
            chooseInstructorButton.setVisibility(View.VISIBLE);
            instructorChoiceSpinner.setVisibility(View.VISIBLE);
        }else if(!getInstName().equals("not chosen") && getInstructorAvailable()){
            noInstructorsText.setVisibility(View.GONE);
            chooseInstructorButton.setVisibility(View.GONE);
            instructorChoiceSpinner.setVisibility(View.GONE);
        }else{
            chooseInstructorButton.setVisibility(View.GONE);
            instructorChoiceSpinner.setVisibility(View.GONE);
        }
    }

    //onItemSelected method for the instructor spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        instructorName = item;

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //method to get if the user has picked an instructor or not
    public void checkInstructorChosen(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.equals(null)) {
                    setInstName(snapshot.child("instructorName").getValue(String.class));
                    setChooseInstructorVisibility();
                }else if(snapshot.equals(null)){
                    Toast.makeText(getContext(), "You are an instructor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //method called when users tap the choose instructor button, changes the value of the instructor name in the database for that user.
    public void chooseInstructor(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUserRef.child(userId).child("instructorName").setValue(instructorName);

        chooseInstructorButton.setVisibility(View.GONE);
        instructorChoiceSpinner.setVisibility(View.GONE);
        hasPickedInstructor = true;
        Toast.makeText(getContext(), "Instructor chosen", Toast.LENGTH_SHORT).show();
    }
}
