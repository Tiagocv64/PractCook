package com.example.tcver.practcook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.R.attr.fragment;

public class FriendsFragment extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final ArrayList<String[]> friendsList = new ArrayList<>();
    public static final ArrayList<String[]> friendsHashList = new ArrayList<>();
    public static final int FRIENDS_TRUE = 0;
    public static final int FRIENDS_REQUEST_RECEIVED = 1;
    public static final int FRIENDS_REQUEST_SENT = 2;
    public static final int FRIENDS_REJECTED = 3;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Friends");

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Selecionar o item correto na barra da esquerda
        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        nav_view.getMenu().getItem(1).setChecked(true);

        //TODO

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friends);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());

        final FriendsListAdapter adapter = new FriendsListAdapter(friendsList);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(mLayoutManager);


        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refUserFriends = FirebaseDatabase.getInstance().getReference().child("Users").child(user).child("friends");

        refUserFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                friendsList.clear();
                friendsHashList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (!(Integer.parseInt(ds.getValue().toString()) == FriendsFragment.FRIENDS_REJECTED)) {
                        String inf[] = {ds.getKey() , ds.getValue().toString()};
                        friendsHashList.add(inf);
                    }
                }

                if(friendsHashList.isEmpty()){
                    TextView textViewNoFriends = (TextView) view.findViewById(R.id.text_view_no_friends);
                    textViewNoFriends.setVisibility(View.VISIBLE);
                }


                DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        friendsList.clear();

                        for (String[] userInf : friendsHashList) {

                            DataSnapshot user = dataSnapshot.child(userInf[0]);

                            String friendInf[] = {user.child("name").getValue().toString(), user.child("bio").getValue().toString(),
                                    user.child("foto").getValue().toString(), userInf[1] , userInf[0] };
                            friendsList.add(friendInf);

                        }
                        mRecyclerView.setAdapter(new FriendsListAdapter(friendsList));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //TODO
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_friends_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
                        .title("Search For Friends")
                        .customView(R.layout.dialog_add_friends, true)
                        .positiveText("Done")
                        .build();

                final EditText edit_txt = (EditText) dialog.getCustomView().findViewById(R.id.friends_search_bar);

                edit_txt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //here is your code
                        String queryText = edit_txt.getText().toString().toLowerCase();
                        getList(queryText, dialog.getCustomView());

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

                    }
                });

                /**
                 edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                // CODE HERE
                String queryText = edit_txt.getText().toString();
                getList(queryText, dialog.getCustomView());
                return true;
                }
                return false;
                }
                });**/

                dialog.show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void getList(final String queryText, final View dialog) {

        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        final RecyclerView people = (RecyclerView) dialog.findViewById(R.id.search_results);
        people.setLayoutManager(new LinearLayoutManager(getContext()));


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String[]> peopleList = new ArrayList<String[]>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("name").getValue().toString().toLowerCase().contains(queryText) && !(ds.child("id").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {

                        String[] data = new String[4];
                        data[0] = ds.child("name").getValue().toString();
                        data[1] = ds.child("bio").getValue().toString();
                        data[2] = ds.child("id").getValue().toString();
                        data[3] = ds.child("foto").getValue().toString();

                        peopleList.add(data);

                    }
                }

                people.setAdapter(new PeopleAdapter(peopleList));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nada
            }
        };
        rootRef.addListenerForSingleValueEvent(postListener);
    }

    public void sendFriendRequest(String targetID){
        // TODO
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(targetID).setValue(FRIENDS_REQUEST_SENT);
        myRef.child(targetID).child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FRIENDS_REQUEST_RECEIVED);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }

    public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

        private ArrayList<String[]> peopleList;

        public PeopleAdapter(ArrayList<String[]> peopleList) {
            this.peopleList = peopleList;
        }

        @Override
        public int getItemCount() {
            return peopleList.size();
        }

        @Override
        public void onBindViewHolder(final PeopleViewHolder peopleViewHolder, int i) {
            final String[] data = peopleList.get(i);
            peopleViewHolder.mNameField.setText(data[0]);
            peopleViewHolder.mBioField.setText(data[1]);

            if (!data[3].equals("")) {
                StorageReference httpsReference = storage.getReferenceFromUrl(data[3]);
                Glide.with(peopleViewHolder.mImageFriend.getContext())
                        .using(new FirebaseImageLoader())
                        .load(httpsReference)
                        .into(peopleViewHolder.mImageFriend);
            }

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {

                    if(ds.hasChild("friends")) {

                        if (ds.child("friends").hasChild(data[2])) {

                            if (Integer.parseInt(ds.child("friends").child(data[2]).getValue().toString()) == 0) {
                                peopleViewHolder.mFriendStatus.setVisibility(View.VISIBLE);
                                peopleViewHolder.mFriendStatus.setText("FRIEND");
                            } else {
                                peopleViewHolder.mFriendStatus.setVisibility(View.VISIBLE);
                                peopleViewHolder.mFriendStatus.setText("Pending");
                            }
                        }

                        else {
                            peopleViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                            peopleViewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    if ( ((CheckBox)v).isChecked() ) {
                                        ((CheckBox)v).setVisibility(View.INVISIBLE);
                                        peopleViewHolder.mFriendStatus.setText("Pending");
                                        peopleViewHolder.mFriendStatus.setVisibility(View.VISIBLE);
                                        sendFriendRequest(data[2]);
                                    }
                                }
                            });
                        }
                    }

                    else {
                        peopleViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                        peopleViewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if ( ((CheckBox)v).isChecked() ) {
                                    ((CheckBox)v).setVisibility(View.INVISIBLE);
                                    peopleViewHolder.mFriendStatus.setText("Pending");
                                    peopleViewHolder.mFriendStatus.setVisibility(View.VISIBLE);
                                    sendFriendRequest(data[2]);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Nada
                }
            };
            rootRef.addListenerForSingleValueEvent(postListener);
        }

        @Override
        public PeopleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.populate_people, viewGroup, false);

            return new PeopleViewHolder(itemView);
        }

        public class PeopleViewHolder extends RecyclerView.ViewHolder {
            protected TextView mNameField;
            protected TextView mBioField;
            protected CheckBox mCheckBox;
            protected TextView mFriendStatus;
            protected ImageView mImageFriend;

            public PeopleViewHolder(View v) {
                super(v);
                mNameField = (TextView) v.findViewById(R.id.personName);
                mBioField = (TextView) v.findViewById(R.id.personBio);
                mCheckBox = (CheckBox) v.findViewById(R.id.search_friends_checkBox);
                mFriendStatus = (TextView) v.findViewById(R.id.personFriendStatus);
                mImageFriend = (ImageView) v.findViewById(R.id.personFoto);

            }
        }
    }


    //TODO

    public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder> {

        private ArrayList<String[]> friendsList;

        public FriendsListAdapter(ArrayList<String[]> friendsList) {
            this.friendsList = friendsList;
        }

        @Override
        public int getItemCount() {
            return friendsList.size();
        }

        @Override
        public void onBindViewHolder(final FriendsListViewHolder friendsListViewHolder, int position) {
            final String[] data = friendsList.get(position);
            friendsListViewHolder.mNameField.setText(data[0]);
            friendsListViewHolder.mBioField.setText(data[1]);
            if (!data[2].equals("")) {
                StorageReference httpsReference = storage.getReferenceFromUrl(data[2]);
                Glide.with(friendsListViewHolder.mImageView.getContext())
                        .using(new FirebaseImageLoader())
                        .load(httpsReference)
                        .into(friendsListViewHolder.mImageView);
            }
            switch (Integer.parseInt(data[3])){
                case 0: friendsListViewHolder.mButtonFriendStatus.setText("friends");
                        friendsListViewHolder.mButtonFriendStatus.setEnabled(false);
                    break;
                case 1: friendsListViewHolder.mButtonFriendStatus.setText("Accept");
                        friendsListViewHolder.mButtonFriendStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                friendsListViewHolder.mButtonFriendStatus.setText("friends");
                                friendsListViewHolder.mButtonFriendStatus.setEnabled(false);
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child(data[4]).setValue(FRIENDS_TRUE);
                                myRef.child(data[4]).child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FRIENDS_TRUE);
                            }
                        });
                    break;
                case 2: friendsListViewHolder.mButtonFriendStatus.setText("Sent");
                        friendsListViewHolder.mButtonFriendStatus.setEnabled(false);
                    break;
                case 3: friendsListViewHolder.mButtonFriendStatus.setText("Rejected");
                        friendsListViewHolder.mButtonFriendStatus.setEnabled(false);
                    break;
            }
        }

        @Override
        public FriendsListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.friends_recycler_view, viewGroup, false);

            return new FriendsListViewHolder(itemView);
        }

        public class FriendsListViewHolder extends RecyclerView.ViewHolder {
            protected TextView mNameField;
            protected TextView mBioField;
            protected ImageView mImageView;
            protected Button mButtonFriendStatus;

            public FriendsListViewHolder(View v) {
                super(v);
                mNameField = (TextView) v.findViewById(R.id.friends_name);
                mBioField = (TextView) v.findViewById(R.id.friends_bio);
                mImageView = (ImageView) v.findViewById(R.id.friends_foto);
                mButtonFriendStatus = (Button) v.findViewById(R.id.friends_button_status);

            }
        }
    }
}


