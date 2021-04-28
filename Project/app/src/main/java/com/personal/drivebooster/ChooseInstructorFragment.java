package com.personal.drivebooster;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseInstructorFragment extends Fragment implements CustomInstructorAdapter.onInstructorNameListener {
    View view;
    RecyclerView instructorRecycler;
    TextView noInstructorsText;
    boolean instructorAvailable, hasPickedInstructor;
    double myLng, lessThanMyLng, moreThanMyLng, instLng, myLat;
    String instructorName, instructorId;
    Button chooseInstructorButton;
    DatabaseReference databaseRef, dbUserRef;
    Query databaseQuery;
    Location pupilLocation, instructorLocation;
    CustomInstructorAdapter customInstructorAdapter;
    final List<Instructors> instructorsFromFirebase = new ArrayList<Instructors>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_instructor_fragment, container, false);
        instructorsFromFirebase.clear();
        checkInstructorChosen();
        getInstructorsFromFirebase();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.GONE);
        pupilLocation = new Location("Pupils Location");
        instructorLocation = new Location("Instructors Location");

        instructorRecycler = view.findViewById(R.id.my_instructors_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        instructorRecycler.setLayoutManager(linearLayoutManager);
        noInstructorsText = view.findViewById(R.id.no_instructors_available);
        chooseInstructorButton = view.findViewById(R.id.instructor_choice_button);
        customInstructorAdapter = new CustomInstructorAdapter(instructorsFromFirebase, this);
        instructorRecycler.setAdapter(customInstructorAdapter);

        chooseInstructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInstructor();
            }
        });
        return view;
    }

    //onClick for instructor names
    @Override
    public void onInstructorNameClick(int position) {
        setInstName(instructorsFromFirebase.get(position).name);

        databaseQuery = FirebaseDatabase.getInstance().getReference().child("Instructors").orderByChild("name").equalTo(getInstName());
        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    setInstId(childSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //getters and setters for instructor names
    public String getInstName() {
        return instructorName;
    }

    public void setInstName(String instructorName) {
        this.instructorName = instructorName;
    }

    //method to return instructors from database
    public void getInstructorsFromFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    noInstructorsText.setVisibility(View.GONE);
                    setInstructorAvailable(true);
                    checkInstructorChosen();
                    //getMyLongitudeDifference();
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Instructors instructors = child.getValue(Instructors.class);
                        instructorLocation.setLatitude(instructors.latitude);
                        instructorLocation.setLongitude(instructors.longitude);
                        if (pupilLocation.distanceTo(instructorLocation) < 10000 && !getInstName().equals(instructors.name)) {
                            instructorsFromFirebase.add(instructors);
                            customInstructorAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (!snapshot.hasChildren()) {
                    setInstructorAvailable(false);
                    noInstructorsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyLongitudeDifference() {
        double x = getLng();

        lessThanMyLng = x - 1;
        moreThanMyLng = x + 1;

    }

    //getters and setters for latitude and longitude
    public Double getLng() {
        return myLng;
    }

    public void setMyLng(Double myLng) {
        this.myLng = myLng;
    }

    public Double getLat() {
        return myLat;
    }

    public void setMyLat(Double myLat) {
        this.myLat = myLat;
    }

    //method to check if pupil has chosen an instructor
    public void checkInstructorChosen() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.equals(null)) {
                    setInstName(snapshot.child("instructorName").getValue(String.class));
                    setMyLng(snapshot.child("longitude").getValue(Double.class));
                    setMyLat(snapshot.child("latitude").getValue(Double.class));
                    pupilLocation.setLongitude(getLng());
                    pupilLocation.setLatitude(getLat());
                    setChooseInstructorVisibility();
                } else if (snapshot.equals(null)) {
                    Toast.makeText(getContext(), "You are an instructor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //getters and setters for instructors available
    public boolean getInstructorAvailable() {
        return instructorAvailable;
    }

    public void setInstructorAvailable(boolean instructorAvailable) {
        this.instructorAvailable = instructorAvailable;
    }

    //method to choose instructor and update this in the database
    public void chooseInstructor() {
        Users pickInsUserObj = new Users();
        pickInsUserObj.chooseInstructor(chooseInstructorButton, instructorRecycler, hasPickedInstructor, getInstId(), getInstName());
    }

    //getters and setters for instructorId
    public String getInstId() {
        return instructorId;
    }

    public void setInstId(String instructorId) {
        this.instructorId = instructorId;
    }

    //method to set visibility of views on this screen
    public void setChooseInstructorVisibility() {
        if (getInstName().equals("not chosen") && getInstructorAvailable()) {
            chooseInstructorButton.setVisibility(View.VISIBLE);
            instructorRecycler.setVisibility(View.VISIBLE);
        } else if (getInstructorAvailable()) {
            chooseInstructorButton.setVisibility(View.VISIBLE);
            instructorRecycler.setVisibility(View.VISIBLE);
        } else if (!getInstructorAvailable()) {
            noInstructorsText.setVisibility(View.VISIBLE);
            chooseInstructorButton.setVisibility(View.GONE);
            instructorRecycler.setVisibility(View.GONE);
        }
    }
}
