package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.personal.drivebooster.Adapter.CustomPupilsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyPupilsFragment extends Fragment implements CustomPupilsAdapter.onNameListener{
    View view;
    final List<Users> usersFromFirebase = new ArrayList<Users>();
    CustomPupilsAdapter customPupilAdapter;
    DatabaseReference databaseRef, userRef;
    RecyclerView pupilRecycler;
    String myName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.instructors_my_pupils_fragment, container, false);
        pupilRecycler = view.findViewById(R.id.my_pupils_recycler);
        usersFromFirebase.clear();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.VISIBLE);
        getMyName();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        pupilRecycler.setLayoutManager(linearLayoutManager);
        customPupilAdapter = new CustomPupilsAdapter(usersFromFirebase,this);
        customPupilAdapter.notifyDataSetChanged();
        pupilRecycler.setAdapter(customPupilAdapter);
        return view;
    }

    public void getMyPupils() {
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    for (DataSnapshot child : children) {
                        Users users = child.getValue(Users.class);
                        if(users.instructorName.equals(getName())) {
                            usersFromFirebase.add(users);
                            customPupilAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMyName(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Instructors").child(userId);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    setMyName(snapshot.child("name").getValue(String.class));
                    getMyPupils();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getName(){
        return myName;
    }

    public void setMyName(String myName){
        this.myName = myName;
    }

    @Override
    public void onNameClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("pupilName", usersFromFirebase.get(position).name);
        bundle.putString("pupilEmail", usersFromFirebase.get(position).email);
        bundle.putString("pupilAddress", usersFromFirebase.get(position).fullAddress);
        bundle.putDouble("latitude", usersFromFirebase.get(position).latitude);
        bundle.putDouble("longitude", usersFromFirebase.get(position).longitude);
        Navigation.findNavController(view).navigate(R.id.action_instructor_my_pupils_to_instructorPupilDetailsFragment, bundle);

    }
}
