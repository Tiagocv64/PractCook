package com.example.tcver.practcook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;


public class FriendsMessagesFragment extends Fragment {
    public static final ArrayList<Friends> friendsList = new ArrayList<>();
    public static final ArrayList<String> friendsHashList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = (View) inflater.inflate(R.layout.fragment_friends_messages, container, false);

        if(friendsList.isEmpty()) {
            view.findViewById(R.id.progress_bar_messages_friends).setVisibility(View.VISIBLE);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());


        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user).child("friends");


        final RecyclerAdapter adapter = new RecyclerAdapter(friendsList);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(mLayoutManager);

        final ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                friendsList.clear();

                for (String id : friendsHashList) {

                    DataSnapshot user = dataSnapshot.child(id);

                    friendsList.add(new Friends(user.child("name").getValue().toString(), user.child("foto").getValue().toString(), user.child("online").getValue().toString()));

                }

                mRecyclerView.setAdapter(new RecyclerAdapter(friendsList));
                view.findViewById(R.id.progress_bar_messages_friends).setVisibility(View.GONE);;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                friendsList.clear();
                friendsHashList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (Integer.parseInt(ds.getValue().toString()) == FriendsFragment.FRIENDS_TRUE) {
                        friendsHashList.add(ds.getKey());
                    }
                }

                DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                refUsers.addListenerForSingleValueEvent(postListener2);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.addListenerForSingleValueEvent(postListener);
        return view;
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<FriendsHolder> {
        private ArrayList<Friends> mFriends;

        public RecyclerAdapter(ArrayList<Friends> friends) {
            mFriends = friends;
        }

        @Override
        public FriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_friends_recycler_view, parent, false);
            return new FriendsHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(FriendsHolder holder, int position) {

            holder.changeInfo(position);
        }

        @Override
        public int getItemCount() {
            return friendsList.size();
        }
    }

    //1
    public static class FriendsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //2
        private ImageView mFriendImage;
        private TextView mFriendName;
        private TextView mFriendOnline;
        private FirebaseStorage storage = FirebaseStorage.getInstance();

        //3
        private static final String FRIENDS_KEY = "FRIEND";

        //4
        public FriendsHolder(View v) {
            super(v);

            mFriendImage = (ImageView) v.findViewById(R.id.messages_friends_foto);
            mFriendName = (TextView) v.findViewById(R.id.messages_friends_name);
            mFriendOnline = (TextView) v.findViewById(R.id.messages_friends_online);
            v.setOnClickListener(this);
        }

        public void changeInfo(int position){
            //TODO: get image from DB
            mFriendName.setText(FriendsMessagesFragment.friendsList.get(position).name);
            long timeMillis = Long.parseLong(friendsList.get(position).online);
            long difference = System.currentTimeMillis() - timeMillis;
            String text = "Last Online: ";
            if (difference < 0L){
                text += "0 seconds ago";
            } else if (difference < 60000L){
                text += difference/1000 + " seconds ago";
            } else if (difference < 3600000L){
                text += difference/60000 + " minutes ago";
            } else if (difference < 86400000L){
                text += difference/3600000 + " hours ago";
            } else if (difference < 31557600000L){
                text += difference/86400000L + " days ago";
            } else {
                text += difference/31557600000L + " years ago";
            }
            mFriendOnline.setText(text);

            if (!friendsList.get(position).photo.equals("")) {
                StorageReference httpsReference = storage.getReferenceFromUrl(friendsList.get(position).photo);
                Glide.with(mFriendImage.getContext())
                        .using(new FirebaseImageLoader())
                        .load(httpsReference)
                        .into(mFriendImage);
            }
        }

        //5
        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }
    }

    public class Friends{
        private String name;
        private String photo;
        private String online;

        public Friends(String name1, String photo1, String online1){
            name = name1;
            photo = photo1;
            online = online1;
        }

    }

}
