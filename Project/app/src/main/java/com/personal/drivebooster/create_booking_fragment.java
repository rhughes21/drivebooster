package com.personal.drivebooster;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.util.Date;

public class create_booking_fragment extends Fragment  {
        View view;
        DayScrollDatePicker dayPicker;
        TextView datePickedText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_booking_fragment, container, false);
        initViews();
        getValue();
        // Inflate the layout for this fragment
        return view;
    }

    private void initViews(){
        dayPicker = view.findViewById(R.id.horizontal_calendar);
        datePickedText = view.findViewById(R.id.value_text_view);
        setUpPicker();
    }

    private void setUpPicker(){
        dayPicker.setStartDate(2, 11, 2020);
        dayPicker.setEndDate(10,3,2021);
    }

    private void getValue(){
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    datePickedText.setText(date.toString());
                }
            }
        });
    }
}