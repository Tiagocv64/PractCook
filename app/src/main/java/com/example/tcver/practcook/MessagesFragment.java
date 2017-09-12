package com.example.tcver.practcook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;

public class MessagesFragment extends Fragment{
        private FragmentTabHost mTabHost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Messages");

        // Tornar BottomBar visivel e selecionar o item certo
        final BottomBar bottomBar = (BottomBar) rootView.findViewById(R.id.navigation_bottom);
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.selectTabAtPosition(3);

        // Deselecionar todos os items da barra da esquerda
        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        int size = nav_view.getMenu().size();
        for (int i = 0; i < size; i++) {
            nav_view.getMenu().getItem(i).setChecked(false);
        }

        View view = (View) inflater.inflate(R.layout.fragment_messages, container, false);

        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("FriendsTab").setIndicator("Friends", null),
                FriendsMessagesFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("MessagesTab").setIndicator("Messages", null),
                MessagesMessagesFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("GroupsTab").setIndicator("Groups", null),
                GroupsMessagesFragment.class, null);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
