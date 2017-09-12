package com.example.tcver.practcook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;

public class LibraryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Library");

        final BottomBar bottomBar = (BottomBar) rootView.findViewById(R.id.navigation_bottom);
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.selectTabAtPosition(2);

        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        int size = nav_view.getMenu().size();
        for (int i = 0; i < size; i++) {
            nav_view.getMenu().getItem(i).setChecked(false);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

}
