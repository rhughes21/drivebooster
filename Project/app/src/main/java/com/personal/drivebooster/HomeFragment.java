package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.dom.DOMLocator;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener, CustomInstructorAdapter.onInstructorNameListener, CustomBookingsAdapter.onBookingListener {

    View view;
    RecyclerView bookingsRecycler, manoeuvreRecycler, instructorRecycler, previousBookingsRecycler;
    FirebaseAuth auth;
    Button chooseInstructorButton;
    TextView noInstructorsText, myBookingsText, previousBookingsText;
    DatabaseReference databaseRef, dbUserRef,databaseBookingRef, previousBookingsReference;
    Query databaseQuery;
    Boolean hasPickedInstructor = false;
    boolean instructorAvailable;
    double lessThanMyLng, moreThanMyLng;
    String instructorName, instructorId;
    Double instLng, myLng;
    CustomBookingsAdapter customBookingsAdapter, customPreviousBookingAdapter;
    Date currentDate;
    Calendar calendar;

    ManoeuvresAdapter manoeuvresAdapter;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    List<Manoeuvres> manoeuvres = new ArrayList<Manoeuvres>();
    YouTubePlayerView manoeuvreYouTubePlayerView;
    Lifecycle lifecycle = getLifecycle();

    CustomInstructorAdapter customInstructorAdapter;
    final List<Instructors> instructorsFromFirebase = new ArrayList<Instructors>();

    final List<Bookings> previousBookingsFromFirebase = new ArrayList<Bookings>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.home_fragment, container, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        updatePreviousBookings();
        addManoeuvres();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        currentDate = calendar.getTime();
        instructorsFromFirebase.clear();
        bookingsFromFirebase.clear();
        previousBookingsFromFirebase.clear();
        checkInstructorChosen();
        getBookings();
        getInstructorsFromFirebase();
        myBookingsText = view.findViewById(R.id.my_bookings_header);
        bookingsRecycler = view.findViewById(R.id.my_bookings_recycler);
        //previousBookingsRecycler = view.findViewById(R.id.previous_bookings_recycler);
        //previousBookingsText = view.findViewById(R.id.previous_bookings_header);
        instructorRecycler = view.findViewById(R.id.my_instructors_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //LinearLayoutManager previousLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerTwo = new LinearLayoutManager(getContext());
        bookingsRecycler.setLayoutManager(linearLayoutManager);
        instructorRecycler.setLayoutManager(linearLayoutManagerTwo);


        manoeuvreRecycler = view.findViewById(R.id.manoeuvres_recycler);
        LinearLayoutManager linearLayoutManagerManoeuvres = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        manoeuvreRecycler.setLayoutManager(linearLayoutManagerManoeuvres);

        customInstructorAdapter = new CustomInstructorAdapter(instructorsFromFirebase, this);

        instructorRecycler.setAdapter(customInstructorAdapter);
        customBookingsAdapter = new CustomBookingsAdapter(bookingsFromFirebase, this);
        //customPreviousBookingAdapter = new CustomBookingsAdapter(previousBookingsFromFirebase);
        //previousBookingsRecycler.setLayoutManager(previousLinearLayoutManager);
        bookingsRecycler.setAdapter(customBookingsAdapter);
        //previousBookingsRecycler.setAdapter(customPreviousBookingAdapter);
        manoeuvresAdapter = new ManoeuvresAdapter(manoeuvres, lifecycle);
        manoeuvreRecycler.setAdapter(manoeuvresAdapter);

        chooseInstructorButton = view.findViewById(R.id.instructor_choice_button);
        auth = FirebaseAuth.getInstance();
        noInstructorsText = view.findViewById(R.id.no_instructors_available);

        chooseInstructorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInstructor();
            }
        });


        return view;
    }

    public void getMyLongitudeDifference(){
        double x = getLng();

        lessThanMyLng = x - 1;
        moreThanMyLng = x + 1;
    }
    //getter and setter for instructor name
    public String getInstName(){
        return instructorName;
    }

    public void setInstName(String instructorName){
        this.instructorName = instructorName;
    }

    public String getInstId(){
        return instructorId;
    }

    public void setInstId(String instructorId){
        this.instructorId = instructorId;
    }

    public boolean getInstructorAvailable(){
        return instructorAvailable;
    }
    public void setInstructorAvailable(boolean instructorAvailable){
        this.instructorAvailable = instructorAvailable;
    }
    public Double getLng(){
        return myLng;
    }

    public void setMyLng(Double myLng){
        this.myLng = myLng;
    }

    //method to set whether or not the choose instructor button and spinner should be shown
    public void setChooseInstructorVisibility(){
        if(getInstName().equals("not chosen") && getInstructorAvailable()){
            chooseInstructorButton.setVisibility(View.VISIBLE);
            instructorRecycler.setVisibility(View.VISIBLE);
            myBookingsText.setVisibility(View.INVISIBLE);
            bookingsRecycler.setVisibility(View.INVISIBLE);
        }else if(!getInstName().equals("not chosen") && getInstructorAvailable()){
            noInstructorsText.setVisibility(View.GONE);
            chooseInstructorButton.setVisibility(View.GONE);
            instructorRecycler.setVisibility(View.GONE);
            myBookingsText.setVisibility(View.VISIBLE);
            bookingsRecycler.setVisibility(View.VISIBLE);
        }else{
            chooseInstructorButton.setVisibility(View.GONE);
            instructorRecycler.setVisibility(View.GONE);
            myBookingsText.setVisibility(View.VISIBLE);
            bookingsRecycler.setVisibility(View.VISIBLE);
        }
    }

    //onItemSelected method for the instructor spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        instructorName = item;

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //method to get if the user has picked an instructor or not
    public void checkInstructorChosen(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.equals(null)) {
                    setInstName(snapshot.child("instructorName").getValue(String.class));
                    setMyLng(snapshot.child("longitude").getValue(Double.class));
                    setChooseInstructorVisibility();
                }else if(snapshot.equals(null)){
                    Toast.makeText(getContext(), "You are an instructor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //method called when users tap the choose instructor button, changes the value of the instructor name in the database for that user.
    public void chooseInstructor(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUserRef.child(userId).child("instructorName").setValue(instructorName);
        dbUserRef.child(userId).child("instructorId").setValue(getInstId());
        chooseInstructorButton.setVisibility(View.GONE);
        instructorRecycler.setVisibility(View.GONE);
        hasPickedInstructor = true;
        Toast.makeText(getContext(), "Instructor chosen", Toast.LENGTH_SHORT).show();
    }

    public void getBookings(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if(bookings.pupilId.equals(userId) ) {
                            bookingsFromFirebase.add(bookings);
                            customBookingsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*public void getPreviousBookings(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookings = child.getValue(Bookings.class);
                        if(bookings.pupilId.equals(userId) ) {
                                previousBookingsFromFirebase.add(bookings);

                            customPreviousBookingAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    public void updatePreviousBookings(){
        databaseBookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        databaseBookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Bookings bookingsP = child.getValue(Bookings.class);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
                        try {
                            Date bDate = dateFormat.parse(bookingsP.bookingDate);
                            if(currentDate.after(bDate)) {
                                Query removeOutdatedBookings = databaseBookingRef.orderByChild("bookingDate").equalTo(bookingsP.bookingDate);
                                removeOutdatedBookings.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot bookingSnap: snapshot.getChildren()){
                                            bookingSnap.getRef().removeValue();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                previousBookingsReference = FirebaseDatabase.getInstance().getReference().child("PreviousBookings");
                                previousBookingsReference.push().setValue(bookingsP)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(getContext(), "Previous bookings updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Couldn't update previous bookings", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                break;
                            }else{
                                //do nothing
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void addManoeuvres() {
        Manoeuvres parallelPark = new Manoeuvres("FetKmuscFY8", "Parallel Parking");
        Manoeuvres bayPark = new Manoeuvres("NuDPbDkknRM", "Bay Parking");
        Manoeuvres reverseAroundCorner = new Manoeuvres("ABUeMYQHEoQ", "Reverse Around Corner");
        Manoeuvres threePointTurn = new Manoeuvres("FB9huB-DelU", "3-Point Turn");

        manoeuvres.add(parallelPark);
        manoeuvres.add(bayPark);
        manoeuvres.add(reverseAroundCorner);
        manoeuvres.add(threePointTurn);
    }

    public void getInstructorsFromFirebase(){
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    noInstructorsText.setVisibility(View.GONE);
                    setInstructorAvailable(true);
                    checkInstructorChosen();
                    getMyLongitudeDifference();
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child: children){
                        Instructors instructors = child.getValue(Instructors.class);
                        instLng = instructors.longitude;
                        if(instLng > lessThanMyLng && instLng < moreThanMyLng){
                            instructorsFromFirebase.add(instructors);
                            customInstructorAdapter.notifyDataSetChanged();
                        }
                    }
                }else if(!snapshot.hasChildren()){
                    setInstructorAvailable(false);
                    noInstructorsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onInstructorNameClick(int position) {
        setInstName(instructorsFromFirebase.get(position).name);

        databaseQuery = FirebaseDatabase.getInstance().getReference().child("Instructors").orderByChild("name").equalTo(getInstName());
        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    setInstId(childSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBookingClick(int position) {

    }
}
