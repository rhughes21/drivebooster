package com.personal.drivebooster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.personal.drivebooster.R;
import com.personal.drivebooster.TimeSlot;

import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewholder> {


    Context context;
    List<TimeSlot> timeSlotList;

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList){
        this.context = context;
        this.timeSlotList = timeSlotList;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.time_slot_layout, viewGroup, false);
        return new MyViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder myViewHolder, int i) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewholder extends RecyclerView.ViewHolder{
        TextView textTimeSlot;
        public MyViewholder(@NonNull View itemView){
            super(itemView);
            textTimeSlot = (TextView)itemView.findViewById(R.id.recycler_booking_time);

        }
    }
}
