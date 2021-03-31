package com.personal.drivebooster;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class HomeFragment extends Fragment implements  CustomBookingsAdapter.onBookingListener {

    View view;
    RecyclerView bookingsRecycler, manoeuvreRecycler;
    FirebaseAuth auth;
    Button previousBookingsButton;
    TextView noInstructorsText, myBookingsText, noBookingsText;
    DatabaseReference  dbUserRef,databaseBookingRef, previousBookingsReference, instructorsAvailableRef;
    boolean instructorAvailable;
    String instructorName;
    CustomBookingsAdapter customBookingsAdapter;
    Date currentDate;
    Calendar calendar;
    ManoeuvresAdapter manoeuvresAdapter;
    final List<Bookings> bookingsFromFirebase = new ArrayList<Bookings>();
    List<Manoeuvres> manoeuvres = new ArrayList<Manoeuvres>();
    YouTubePlayerView manoeuvreYouTubePlayerView;
    Lifecycle lifecycle = getLifecycle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.home_fragment, container, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < ((FragmentManager) fm).getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        checkInstructorChosen();
        updatePreviousBookings();
        addManoeuvres();

        noBookingsText = view.findViewById(R.id.no_upcoming_bookings);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        currentDate = calendar.getTime();
        bookingsFromFirebase.clear();

        getBookings();
        myBookingsText = view.findViewById(R.id.my_bookings_header);
        bookingsRecycler = view.findViewById(R.id.my_bookings_recycler);
        previousBookingsButton = view.findViewById(R.id.previous_bookings_button);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        bookingsRecycler.setLayoutManager(linearLayoutManager);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.VISIBLE);
        manoeuvreRecycler = view.findViewById(R.id.manoeuvres_recycler);
        LinearLayoutManager linearLayoutManagerManoeuvres = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        manoeuvreRecycler.setLayoutManager(linearLayoutManagerManoeuvres);
        customBookingsAdapter = new CustomBookingsAdapter(bookingsFromFirebase, this);
        bookingsRecycler.setAdapter(customBookingsAdapter);
        manoeuvresAdapter = new ManoeuvresAdapter(manoeuvres, lifecycle);
        manoeuvreRecycler.setAdapter(manoeuvresAdapter);
        auth = FirebaseAuth.getInstance();
        noInstructorsText = view.findViewById(R.id.no_instructors_available);


        previousBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_pupilPreviousBookingsFragment);

            }
        });

        return view;
    }


    public boolean isInstructorAvailable() {
        return instructorAvailable;
    }

    public void setInstructorAvailable(boolean instructorAvailable) {
        this.instructorAvailable = instructorAvailable;
    }

    public void instructorsAvailable(){

        instructorsAvailableRef = FirebaseDatabase.getInstance().getReference().child("Instructors");
        instructorsAvailableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    setInstructorAvailable(true);
                }else{
                    setInstructorAvailable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //method to get if the user has picked an instructor or not
    public void checkInstructorChosen(){
        instructorsAvailable();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("instructorName").getValue().equals("not chosen") && isInstructorAvailable()){
                    showInstructorDialog();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void showInstructorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_instructor_to_continue));
        builder.setNeutralButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_chooseInstructorFragment);
                    }
                });
        AlertDialog updateAlert = builder.create();
        updateAlert.show();
        updateAlert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.positive_alert_button));
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
                            noBookingsText.setVisibility(View.GONE);
                            bookingsRecycler.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

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


    @Override
    public void onBookingClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("bookingDate", bookingsFromFirebase.get(position).bookingDate);
        bundle.putString("bookingTime", bookingsFromFirebase.get(position).bookingTime);
        bundle.putString("userAddress", bookingsFromFirebase.get(position).userAddress);
        bundle.putString("dateDay", bookingsFromFirebase.get(position).dateDay);
        bundle.putString("instructorName", bookingsFromFirebase.get(position).instructorName);
        Navigation.findNavController(view).navigate(R.id.action_homeFragment2_to_pupilUpcomingBookingFragment, bundle);

    }
}
