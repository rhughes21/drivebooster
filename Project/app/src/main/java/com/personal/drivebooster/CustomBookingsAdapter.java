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
    private onBookingListener onBookingListener;
    int rowindex;
    int sizeOfView;

    public CustomBookingsAdapter(List<Bookings> bookingsFromFirebase, onBookingListener onBookingListener){
        this.bookingsFromFirebase = bookingsFromFirebase;
        this.onBookingListener = onBookingListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recycler_item, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();

        return new viewHolder(view, onBookingListener);
    }

    //booking item click listener
    public interface onBookingListener{
        void onBookingClick(int position);
    }
    //method to set the text for the views
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

    //method to setup the viewholder
    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView bookingDate, bookingTime, bookingInstructor, bookingPupilName;
        ConstraintLayout bookingConstraintLayout;
        onBookingListener onBookingListener;
        public viewHolder(@NonNull View itemView, onBookingListener onBookingListener) {
            super(itemView);
            bookingConstraintLayout = itemView.findViewById(R.id.booking_constraint_layout);
            bookingDate = itemView.findViewById(R.id.recycler_booking_date);
            bookingTime = itemView.findViewById(R.id.recycler_booking_time);
            bookingInstructor = itemView.findViewById(R.id.recycler_booking_instructor);
            bookingPupilName = itemView.findViewById(R.id.recycler_booking_pupil_name);
            this.onBookingListener = onBookingListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookingListener.onBookingClick(getAdapterPosition());
            rowindex = getAdapterPosition();
            notifyDataSetChanged();

        }
    }
}
