package com.personal.drivebooster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class InstructorBookingInfoFragment extends Fragment implements OnMapReadyCallback {

    View view;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;
    TextView bookingInfoDate, bookingInfoTime;
    Button remindButton, deleteButton;
    DatabaseReference dbKeyRef, dbUserRef;
    String bookingDate, addressBooking, userName,bookingKey, phoneNo, pupilEmail, pupilId, message, pupilPhoneNumber;
    GoogleMap mMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.booking_info_fragment, container, false);
        bookingInfoDate = view.findViewById(R.id.booking_info_date);
        bookingInfoTime = view.findViewById(R.id.booking_info_time);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.info_map);
        addressBooking = getArguments().getString("userAddress");
        bookingDate = getArguments().getString("bookingDate");
        userName = getArguments().getString("userName");
        pupilId = getArguments().getString("pupilId");
        pupilPhoneNumber = getArguments().getString("phoneNo");
        getEmailFromFirebase();
        bookingInfoDate.setText(bookingDate);
        bookingInfoTime.setText(getArguments().getString("bookingTime"));
        retrieveBookingKeyFromFirebase();
        deleteButton = view.findViewById(R.id.instructor_delete_booking_button);
        remindButton = view.findViewById(R.id.instructor_remind_booking_button);
        remindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pupilPhoneNumber.equals("Unavailable")){
                    sendReminderSMSMessage();
                } else{
                    sendBookingReminderEmail();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog();
            }
        });

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);
        mapFragment.getMapAsync(this);
        return view;
    }

    //checks if the map is ready, display location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng address = getLocationFromAddress(getContext(), addressBooking);
        mMap.addMarker(new MarkerOptions().position(address).title(userName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address,16));

    }

    //gets the lat lng from the pupils address
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }

    //getters and setters for the booking key
    public String getBookingKey(){
        return bookingKey;
    }

    public void setBookingKey(String bookingKey){
        this.bookingKey = bookingKey;
    }

    //retrieves booking key from database
    public void retrieveBookingKeyFromFirebase(){
        dbKeyRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        Query keyQuery = dbKeyRef.orderByChild("userAddress").equalTo(addressBooking);
        final String date = bookingInfoDate.getText().toString();
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    String dateSnapshot = childSnapshot.child("bookingDate").getValue(String.class);
                    if(dateSnapshot.equals(date)){
                        setBookingKey(childSnapshot.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //dialog asking if user wants to delete the booking
    public void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.confirm_deletion));
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bookings b = new Bookings();
                        b.deleteBooking(getBookingKey());
                        if(!pupilPhoneNumber.equals("Unavailable")){
                            sendBookingDeletedSMSMessage();
                        }else {
                            sendBookingDeletedEmail();
                        }

                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog updateAlert = builder.create();
        updateAlert.show();
        updateAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.negative_alert_button));
        updateAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.positive_alert_button));
    }

    //method to show system email app if no phone number exists
    @SuppressLint("LongLogTag")
    public void sendBookingDeletedEmail(){
        Log.i("Send email", "");
        String[] TO = {getPupilEmail()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Booking on " + bookingDate + " was deleted");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Let " + userName + " know why you had to delete the booking");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            getActivity().finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //method to show system email app for remind pupils
    @SuppressLint("LongLogTag")
    public void sendBookingReminderEmail(){
        Log.i("Send email", "");
        String[] TO = {getPupilEmail()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Booking reminder");
        emailIntent.putExtra(Intent.EXTRA_TEXT,  userName + " this is a reminder for your booking on " + bookingDate + " at " + bookingInfoTime.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            getActivity().finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //getters and setters for pupil email
    public String getPupilEmail() {
        return pupilEmail;
    }

    public void setPupilEmail(String pupilEmail) {
        this.pupilEmail = pupilEmail;
    }

    public void getEmailFromFirebase(){
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(pupilId);
        dbUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setPupilEmail(snapshot.child("email").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //method to send sms when a booking is deleted
    protected void sendBookingDeletedSMSMessage() {
        message = "Booking on " + bookingInfoDate.getText().toString() + " has been deleted";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(pupilPhoneNumber, null, message, null, null);
        Log.i("Pupil Phone", pupilPhoneNumber);
        Toast.makeText(getActivity(), "SMS sent ", Toast.LENGTH_SHORT).show();
    }

    //method to send booking reminder sms
    protected void sendReminderSMSMessage() {
        message = "This is a reminder for your lesson on " + bookingInfoDate.getText().toString() + " at " + bookingInfoTime.getText().toString();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(pupilPhoneNumber, null, message, null, null);
        Toast.makeText(getActivity(), "SMS reminder sent ", Toast.LENGTH_SHORT).show();
    }


}
