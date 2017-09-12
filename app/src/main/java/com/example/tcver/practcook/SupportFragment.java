package com.example.tcver.practcook;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SupportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Support");

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        nav_view.getMenu().getItem(3).setChecked(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false);
    }


}