package com.personal.drivebooster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SetInstructorTimesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    View view;
    TextView chosenTimesList;
    FirebaseAuth auth;
    TimePicker timePicker;
    Button addTime, submitTimes;
    String lastTimeChosen, dayType;
    Spinner daySpinner;
    DatabaseReference databaseTimesReference, databaseCurrentTimesReference;
    List<String> myTimes = new ArrayList<>();
    List<String> chosenTimes = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.set_up_times_fragment, container, false);
        timePicker = view.findViewById(R.id.instructor_time_picker);
        addTime = view.findViewById(R.id.pick_time_button);
        chosenTimesList = view.findViewById(R.id.chosen_times_list);
        submitTimes = view.findViewById(R.id.submit_times_button);
        daySpinner = view.findViewById(R.id.day_type_spinner);
        auth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(spinnerAdapter);
        daySpinner.setOnItemSelectedListener(this);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                lastTimeChosen = String.format("%02d:%02d", timePicker.getHour() , timePicker.getMinute());

            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTimes.add(lastTimeChosen);
                chosenTimes.add(lastTimeChosen);
                chosenTimesList.setText("Times selected " + chosenTimes.toString());
                Toast.makeText(getContext(), "Time chosen  " + lastTimeChosen, Toast.LENGTH_SHORT).show();
            }
        });

        submitTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInstructorTimes(myTimes);
                myTimes.clear();
            }
        });

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);
        return view;
    }

    //onItemSelected for day chosen from drop down
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dayType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //push new times to the database
    public void setInstructorTimes(List<String> times){
        times = myTimes;
        String instructorUUid = auth.getUid();

        databaseTimesReference = FirebaseDatabase.getInstance().getReference().child("Instructors").child(instructorUUid).child("Times");
        TimeSlot times_obj = new TimeSlot(times);

        databaseTimesReference.child(dayType).setValue(times_obj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Times stored", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Times could not be saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
