package com.personal.drivebooster;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomInstructorAdapter extends RecyclerView.Adapter {

    List<Instructors> instructorsFromFirebase;
    private onInstructorNameListener onInstructorNameListener;
    int rowindex;

    int sizeOfView;
    public CustomInstructorAdapter(List<Instructors> instructorsFromFirebase, onInstructorNameListener onInstructorNameListener){
        this.instructorsFromFirebase = instructorsFromFirebase;
        this.onInstructorNameListener = onInstructorNameListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instructors_recycler_item, parent, false);
        return new viewHolder(view, onInstructorNameListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolder myViewHolder = (viewHolder)holder;

        if(!instructorsFromFirebase.get(position).latitude.equals("")){
            myViewHolder.instructorName.setText(instructorsFromFirebase.get(position).name);
        }else{
            Log.d("DIDN'T WORK", "BLAH BLAH");
        }
        if (rowindex == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#00FF00"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public interface onInstructorNameListener{
        void onInstructorNameClick(int position);
    }

    @Override
    public int getItemCount() {
        return instructorsFromFirebase.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView instructorName;
        onInstructorNameListener onInstructorNameListener;
        public viewHolder(@NonNull View itemView, onInstructorNameListener onInstructorNameListener) {
            super(itemView);
            instructorName = itemView.findViewById(R.id.recycler_instructor_name);
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
