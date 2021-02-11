package com.personal.drivebooster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CustomBookingsAdapter extends RecyclerView.Adapter {

    List<Bookings> bookingsFromFirebase;
    String currentUserId;
    FirebaseUser currentUser;
    int sizeOfView;

    public CustomBookingsAdapter(List<Bookings> bookingsFromFirebase){
        this.bookingsFromFirebase = bookingsFromFirebase;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recycler_item, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder myViewHolder=(viewHolder)holder;

            sizeOfView = sizeOfView +1;
            myViewHolder.bookingDate.setText((CharSequence) bookingsFromFirebase.get(position).bookingDate);
            myViewHolder.bookingTime.setText(((CharSequence) bookingsFromFirebase.get(position).bookingTime));
            myViewHolder.bookingInstructor.setText(((CharSequence) bookingsFromFirebase.get(position).instructorName));
            myViewHolder.bookingPupilName.setText(bookingsFromFirebase.get(position).userName);


    }

    @Override
    public int getItemCount() {
        return bookingsFromFirebase.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView bookingDate, bookingTime, bookingInstructor, bookingPupilName;
        ConstraintLayout bookingConstraintLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            bookingConstraintLayout = itemView.findViewById(R.id.booking_constraint_layout);
            bookingDate = itemView.findViewById(R.id.recycler_booking_date);
            bookingTime = itemView.findViewById(R.id.recycler_booking_time);
            bookingInstructor = itemView.findViewById(R.id.recycler_booking_instructor);
            bookingPupilName = itemView.findViewById(R.id.recycler_booking_pupil_name);

        }
    }
}
