package com.personal.drivebooster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SetInstructorTimesFragment extends Fragment{

    View view;
    TimePicker timePicker;
    Button addTime;
    String lastTimeChosen;
    List<String> myTimes = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.set_up_times_fragment, container, false);
        timePicker = view.findViewById(R.id.instructor_time_picker);
        addTime = view.findViewById(R.id.pick_time_button);


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
                Toast.makeText(getContext(), "Time chosen  " + lastTimeChosen, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
