package com.example.tcver.practcook;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Friends");

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Selecionar o item correto na barra da esquerda
        NavigationView nav_view = (NavigationView) rootView.findViewById(R.id.nav_view);
        nav_view.getMenu().getItem(1).setChecked(true);

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
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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

    public void getList(String queryText, View dialog) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = databaseReference.orderByChild("lowername")
                .startAt(queryText)
                .endAt(queryText + "\uf8ff");

        RecyclerView people = (RecyclerView) dialog.findViewById(R.id.search_results);
        people.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<DatabaseClasses.User, UserHolder>(DatabaseClasses.User.class, R.layout.populate_people, UserHolder.class, query) {
            @Override
            public void populateViewHolder(UserHolder holder, DatabaseClasses.User user, int position) {
                holder.setName(user.getName(), user.getID());
                holder.setBio(user.getBio());

                // TODO: FOTO!!!
                // holder.setFoto(user.getFoto());

            }
        };

        people.setAdapter(mAdapter);
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mBioField;

        public UserHolder(View itemView) {
            super(itemView);
            mNameField = (TextView) itemView.findViewById(R.id.personName);
            mBioField = (TextView) itemView.findViewById(R.id.personBio);
        }

        public void setName(String name, String uID) {
            mNameField.setText(name);
        }

        public void setBio(String message) {
            mBioField.setText(message);
        }
    }

}
