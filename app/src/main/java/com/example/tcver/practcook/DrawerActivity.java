package com.example.tcver.practcook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(R.id.userName);
        navigationView.getHeaderView(R.id.userEmail);

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.navigation_bottom);

        bottomBar.setOnTabSelectListener(
                new OnTabSelectListener() {
                    @Override
                    public void onTabSelected(@IdRes int tabId) {
                        switch (tabId) {
                            case R.id.nav_home:
                                goFragment("Home");
                                break;
                            case R.id.nav_search:
                                goFragment("Search");
                                break;
                            case R.id.nav_library:
                                goFragment("Library");
                                break;
                            case R.id.nav_messages:
                                goFragment("Messages");
                                break;
                        }
                    }
                });

        // Buscar a View dentro do hamburger menu
        View hView = navigationView.getHeaderView(0);

        TextView userName = (TextView) hView.findViewById(R.id.userName);
        TextView userEmail = (TextView) hView.findViewById(R.id.userEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            HomeFragment firstFragment = new HomeFragment();

            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.navigation_bottom);
        bottomBar.setVisibility(View.INVISIBLE);

        if (id == R.id.profile) {
            goFragment("Profile");
        } else if (id == R.id.friends) {
            goFragment("Friends");
        } else if (id == R.id.settings) {
            goFragment("Settings");
        } else if (id == R.id.support) {
            goFragment("Support");
        } else if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(DrawerActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void goFragment(String fragmentName){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragmentName) {
            case "Home":
                HomeFragment newFragment = new HomeFragment();
                transaction.replace(R.id.fragment_container, newFragment);
                break;

            case "Search":
                // tive que mudar o nome do fragment pq dava erro...
                SearchFragment newFragment2 = new SearchFragment();
                transaction.replace(R.id.fragment_container, newFragment2);
                break;

            case "Library":
                LibraryFragment newFragment3 = new LibraryFragment();
                transaction.replace(R.id.fragment_container, newFragment3);
                break;

            case "Messages":
                MessagesFragment newFragment4 = new MessagesFragment();
                transaction.replace(R.id.fragment_container, newFragment4);
                break;

            case "Profile":
                ProfileFragment newFragment5 = new ProfileFragment();
                transaction.replace(R.id.fragment_container, newFragment5);
                break;

            case "Friends":
                FriendsFragment newFragment6 = new FriendsFragment();
                transaction.replace(R.id.fragment_container, newFragment6);
                break;

            case "Settings":
                SettingsFragment newFragment7 = new SettingsFragment();
                transaction.replace(R.id.fragment_container, newFragment7);
                break;

            case "Support":
                SupportFragment newFragment8 = new SupportFragment();
                transaction.replace(R.id.fragment_container, newFragment8);
                break;
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }
}
