package com.personal.drivebooster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class DummyFrag extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.dummy_frag, container, false);
        return view;
    }
}
