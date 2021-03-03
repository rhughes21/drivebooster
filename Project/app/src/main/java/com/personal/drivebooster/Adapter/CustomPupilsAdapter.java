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
    private onNameListener onNameListener;
    int rowindex;

    public CustomPupilsAdapter(List<Users> usersFromFirebase, onNameListener onNameListener){
        this.usersFromFirebase = usersFromFirebase;
        this.onNameListener = onNameListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_pupils_recycler_item, parent, false);

        return new viewHolder(view, onNameListener);
    }

    public interface onNameListener{
        void onNameClick(int position);
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

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView pupilName;
        onNameListener onNameListener;
        public viewHolder(@NonNull View itemView, onNameListener onNameListener) {
            super(itemView);
            pupilName = itemView.findViewById(R.id.recycler_pupil_name);
            this.onNameListener = onNameListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNameListener.onNameClick(getAdapterPosition());
            rowindex = getAdapterPosition();
            notifyDataSetChanged();
        }
    }
}
