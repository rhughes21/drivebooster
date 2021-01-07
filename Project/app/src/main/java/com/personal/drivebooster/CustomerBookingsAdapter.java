package com.personal.drivebooster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerBookingsAdapter extends RecyclerView.Adapter {

    List<Bookings> bookingsFromFirebase;

    public CustomerBookingsAdapter(List<Bookings> bookingsFromFirebase){
        this.bookingsFromFirebase = bookingsFromFirebase;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recycler_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder myViewHolder=(viewHolder)holder;
        myViewHolder.bookingDate.setText((CharSequence) bookingsFromFirebase.get(position).bookingDate);
        myViewHolder.bookingTime.setText(((CharSequence) bookingsFromFirebase.get(position).bookingTime));
    }

    @Override
    public int getItemCount() {
        return bookingsFromFirebase.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView bookingDate, bookingTime;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.recycler_booking_date);
            bookingTime = itemView.findViewById(R.id.recycler_booking_time);
        }
    }
}
