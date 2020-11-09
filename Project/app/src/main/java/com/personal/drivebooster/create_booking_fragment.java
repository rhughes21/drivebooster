package com.personal.drivebooster;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class create_booking_fragment extends Fragment  {
        View view;
        DayScrollDatePicker dayPicker;
        TextView datePickedText;
        ListView timeListView;
        String dateString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_booking_fragment, container, false);
        initViews();
        getDateValue(dateString);
        // Inflate the layout for this fragment
        return view;
    }

    private void initViews(){
        dayPicker = view.findViewById(R.id.horizontal_calendar);
        datePickedText = view.findViewById(R.id.value_text_view);
        timeListView = view.findViewById(R.id.time_list_view);
        setUpPicker();
        setUpListView();
    }

    private void setUpPicker(){
        dayPicker.setStartDate(2, 11, 2020);
        dayPicker.setEndDate(10,3,2021);
    }

    private String getDateValue(String dStr){
        dayPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
                    dateString = dateFormat.format(date);
                    datePickedText.setText(dateString);
                }
            }
        });
        return dateString;
    }

    public List<String> setUpTimes(){
        ArrayList<String> times = new ArrayList<String>();
        for(int i=9; i < 20; i++){
            String time = i+ ":00";
            times.add(time);
        }
        return times;
    }

    public void setUpListView(){
        setUpTimes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, setUpTimes());
        timeListView.setAdapter(adapter);

    }
}