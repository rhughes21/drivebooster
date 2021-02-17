package com.personal.drivebooster;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class InstructorBookingInfoFragment extends Fragment implements OnMapReadyCallback {

    View view;
    TextView bookingInfoDate, bookingInfoTime;
    String bookingDate, addressBooking, userName;
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
        bookingInfoDate.setText(bookingDate);
        bookingInfoTime.setText(getArguments().getString("bookingTime"));
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.INVISIBLE);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng address = getLocationFromAddress(getContext(), addressBooking);
        mMap.addMarker(new MarkerOptions().position(address).title(userName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address,16));

    }

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
}
