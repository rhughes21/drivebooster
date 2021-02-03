package com.personal.drivebooster.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.personal.drivebooster.Bookings;
import com.personal.drivebooster.CustomBookingsAdapter;
import com.personal.drivebooster.R;
import com.personal.drivebooster.Users;

import java.util.List;

public class CustomPupilsAdapter extends RecyclerView.Adapter {

    List<Users> usersFromFirebase;

    public CustomPupilsAdapter(List<Users> usersFromFirebase){
        this.usersFromFirebase = usersFromFirebase;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_pupils_recycler_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        viewHolder myViewHolder=(viewHolder)holder;

        if(usersFromFirebase.get(position).name != null){
            myViewHolder.pupilName.setText(usersFromFirebase.get(position).name);
        }
    }

    @Override
    public int getItemCount() {
        return usersFromFirebase.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView pupilName;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            pupilName = itemView.findViewById(R.id.recycler_pupil_name);

        }
    }
}
