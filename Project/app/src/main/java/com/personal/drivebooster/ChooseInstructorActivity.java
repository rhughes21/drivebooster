package com.personal.drivebooster;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseInstructorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    FirebaseAuth auth;
    DatabaseReference databaseRef, dbUserRef;
    Spinner instructorChoiceSpinner;
    String instructorName;

    @Override
    protected void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);
        setContentView(R.layout.choose_instructor_activity);
        getInstructors();
        setUpSpinner();
    }

    public ArrayList<String> getInstructors(){
        final ArrayList<String> instructorArray = new ArrayList<String>();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ds.child("name").getValue(String.class);
                    instructorArray.add(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return instructorArray;
    }

    public void setUpSpinner(){

        instructorChoiceSpinner = findViewById(R.id.choose_instructor_spinner);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getInstructors());

        instructorChoiceSpinner.setAdapter(spinnerArrayAdapter);

        instructorChoiceSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        instructorName = item;


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
