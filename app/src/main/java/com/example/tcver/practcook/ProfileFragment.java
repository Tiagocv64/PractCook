package com.example.tcver.practcook;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileFragment extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        // Selecionar o item correto na barra da esquerda
        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        nav_view.getMenu().getItem(0).setChecked(true);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView textViewProfile = (TextView)view.findViewById(R.id.text_profile);
        final CircularImageView imageViewProfile = (CircularImageView)view.findViewById(R.id.profile_image);
        final ImageView imageViewProfileBackground = (ImageView)view.findViewById(R.id.image_profile_background);

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refUserFriends = FirebaseDatabase.getInstance().getReference().child("Users").child(user);


        refUserFriends.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("foto").getValue().toString().equals("")){
                    StorageReference httpsReference = storage.getReferenceFromUrl(dataSnapshot.child("foto").getValue().toString());
                    Glide.with(view.getContext())
                            .using(new FirebaseImageLoader())
                            .load(httpsReference)
                            .into(imageViewProfile);
                }
                if (!dataSnapshot.child("fotoComida").getValue().toString().equals("")){
                    StorageReference httpsReference1 = storage.getReferenceFromUrl(dataSnapshot.child("fotoComida").getValue().toString());
                    Glide.with(view.getContext())
                            .using(new FirebaseImageLoader())
                            .load(httpsReference1)
                            .into(imageViewProfileBackground);
                }
                textViewProfile.setText(dataSnapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }


}

