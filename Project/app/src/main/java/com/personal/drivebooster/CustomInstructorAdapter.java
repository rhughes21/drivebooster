package com.personal.drivebooster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomInstructorAdapter extends RecyclerView.Adapter {

    List<Instructors> instructorsFromFirebase;
    private onInstructorNameListener onInstructorNameListener;
    int rowindex;

    int sizeOfView;

    public CustomInstructorAdapter(List<Instructors> instructorsFromFirebase, onInstructorNameListener onInstructorNameListener) {
        this.instructorsFromFirebase = instructorsFromFirebase;
        this.onInstructorNameListener = onInstructorNameListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instructors_recycler_item, parent, false);
        return new viewHolder(view, onInstructorNameListener);
    }

    //method for setting the text in items
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder myViewHolder = (viewHolder) holder;

        if (!instructorsFromFirebase.get(position).latitude.equals("")) {
            myViewHolder.instructorName.setText(instructorsFromFirebase.get(position).name);
        } else {
            Log.d("DIDN'T WORK", "BLAH BLAH");
        }
        if (rowindex == position) {
            myViewHolder.instructorCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccentLight));
        } else {
            myViewHolder.instructorCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }
    }

    //instructor name click listener
    public interface onInstructorNameListener {
        void onInstructorNameClick(int position);
    }

    @Override
    public int getItemCount() {
        return instructorsFromFirebase.size();
    }

    //setting up the view holder
    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView instructorName;
        CardView instructorCardView;
        onInstructorNameListener onInstructorNameListener;

        public viewHolder(@NonNull View itemView, onInstructorNameListener onInstructorNameListener) {
            super(itemView);
            instructorName = itemView.findViewById(R.id.recycler_instructor_name);
            instructorCardView = itemView.findViewById(R.id.instructor_cardview);
            this.onInstructorNameListener = onInstructorNameListener;
            itemView.setOnClickListener(this);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View v) {

            onInstructorNameListener.onInstructorNameClick(getAdapterPosition());
            rowindex = getAdapterPosition();
            notifyDataSetChanged();

        }
    }
}
