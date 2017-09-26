package com.example.tcver.practcook;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            Intent intentMain = new Intent(this, DrawerActivity.class);
            startActivity(intentMain);
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .setLogo(R.drawable.pract_cook_image)
                            .setTheme(R.style.MainThemeFire)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    //Result of Login's Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                checkIfNewUser(user);

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }
        }
    }

    // Verifica se o ID do user existe na DB. Devolve true se a entrada não existir na DB
    public void checkIfNewUser(final FirebaseUser user){
        final String uID = user.getUid();

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(uID)) {
                    Intent intentMain = new Intent(LoginActivity.this, DrawerActivity.class);
                    startActivity(intentMain);
                    finish();
                    return;
                }
                else {
                    Log.d("DEBUG", "NEW USER!");
                    DatabaseClasses.User newUser = new DatabaseClasses.User("", user.getEmail(), "", null, uID, 0, user.getDisplayName(), null, System.currentTimeMillis(), "");
                    rootRef.child(uID).setValue(newUser);
                    Intent intentMain = new Intent(LoginActivity.this, DrawerActivity.class);
                    startActivity(intentMain);
                    finish();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        rootRef.addListenerForSingleValueEvent(postListener);

    }

    protected void showSnackbar(int messageSnackbar){
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.layout_login_activity) , getResources().getString(messageSnackbar), Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}
