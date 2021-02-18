package com.personal.drivebooster;

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

public class PreviousBookingAdapter extends RecyclerView.Adapter{

    List<Bookings> bookingsFromFirebase;
    String currentUserId;
    FirebaseUser currentUser;
    private PreviousBookingAdapter.onPreviousBookingListener onPreviousBookingListener;
    int rowindex;
    int sizeOfView;

    public PreviousBookingAdapter(List<Bookings> bookingsFromFirebase, PreviousBookingAdapter.onPreviousBookingListener onPreviousBookingListener){
        this.bookingsFromFirebase = bookingsFromFirebase;
        this.onPreviousBookingListener = onPreviousBookingListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookings_recycler_item, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();

        return new PreviousBookingAdapter.viewHolder(view, onPreviousBookingListener);
    }

    public interface onPreviousBookingListener{
        void onPreviousBookingClick(int position);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PreviousBookingAdapter.viewHolder myViewHolder=(PreviousBookingAdapter.viewHolder)holder;

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

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView bookingDate, bookingTime, bookingInstructor, bookingPupilName;
        ConstraintLayout bookingConstraintLayout;
        PreviousBookingAdapter.onPreviousBookingListener onPreviousBookingListener;
        public viewHolder(@NonNull View itemView, PreviousBookingAdapter.onPreviousBookingListener onPreviousBookingListener) {
            super(itemView);
            bookingConstraintLayout = itemView.findViewById(R.id.booking_constraint_layout);
            bookingDate = itemView.findViewById(R.id.recycler_booking_date);
            bookingTime = itemView.findViewById(R.id.recycler_booking_time);
            bookingInstructor = itemView.findViewById(R.id.recycler_booking_instructor);
            bookingPupilName = itemView.findViewById(R.id.recycler_booking_pupil_name);
            this.onPreviousBookingListener = onPreviousBookingListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPreviousBookingListener.onPreviousBookingClick(getAdapterPosition());
            rowindex = getAdapterPosition();
            notifyDataSetChanged();

        }
    }
}
