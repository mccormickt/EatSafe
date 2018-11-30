package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddFriendsActivity extends AppCompatActivity {

    // UI references.
    private EditText friendName;
    private Button addButton;
    private Button removeButton;

    // Data References
    private FirebaseAuth auth;
    private DatabaseReference dataBase;
    private FirebaseUser authUser;
    private UserInformation user;

    // flag
    private boolean addedFriend = false;
    private boolean removedFriend = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        //Initialize Authentication/Database Client
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance().getReference().child("users");

        // UI references
        friendName = findViewById(R.id.profile_friendname);
        addButton = findViewById(R.id.add_button);
        removeButton = findViewById(R.id.remove_button);

        dataBase.child(authUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserInformation.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = friendName.getText().toString().toLowerCase();
                        if (name.equals("")) {
                            Toast toast = Toast.makeText(AddFriendsActivity.this,
                                    "Please enter a username",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            ArrayList<String> friends = user.getFriends();
                            if (!friends.contains(name)) {
                                if (friends != null) {
                                    friends.add(name);
                                    user.setFriends(friends);
                                } else {
                                    friends = new ArrayList<>();
                                    friends.add(name);
                                    user.setFriends(friends);
                                }

                                dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userRef : dataSnapshot.getChildren()) {
                                            UserInformation queryUser = userRef.getValue(UserInformation.class);
                                            Log.wtf("AddFriendsActivity", "REMOVING: " + queryUser.getEmail());
                                            if (queryUser.getEmail().equals(name)) {
                                                // would be false if username not in the DB
                                                UserInformation newUser = new UserInformation(user.getFirstName(),
                                                        user.getLastName(), user.getBirthdate(), authUser.getEmail(),
                                                        user.getAllergens(), user.getFriends());
                                                dataBase.child(authUser.getUid()).setValue(newUser.toMap());

                                                Toast toast = Toast.makeText(AddFriendsActivity.this,
                                                        "Added friend!",
                                                        Toast.LENGTH_SHORT);
                                                toast.show();
                                                startActivity(new Intent(AddFriendsActivity.this,
                                                        FriendsActivity.class));
                                            }
                                        }
                                        Toast toast = Toast.makeText(AddFriendsActivity.this,
                                                "Unable to add friend",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }
                }
        );

        removeButton.setOnClickListener(
                new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = friendName.getText().toString();
                            if (name.equals("")) {
                                Toast toast = Toast.makeText(AddFriendsActivity.this,
                                        "Please enter a username",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                ArrayList<String> friends = user.getFriends();
                                if (friends.contains(name)) {
                                    if (friends != null) {
                                        friends.remove(name);
                                        user.setFriends(friends);
                                    }

                                    dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot userRef : dataSnapshot.getChildren()) {
                                                UserInformation queryUser = userRef.getValue(UserInformation.class);
                                                Log.wtf("AddFriendsActivity", "REMOVING: " + queryUser.getEmail());
                                                if (queryUser.getEmail().equals(name)) {
                                                    // would be false if username not in the DB
                                                    UserInformation newUser = new UserInformation(user.getFirstName(),
                                                            user.getLastName(), user.getBirthdate(), authUser.getEmail(),
                                                            user.getAllergens(), user.getFriends());

                                                    dataBase.child(authUser.getUid()).setValue(newUser.toMap());
                                                    Toast toast = Toast.makeText(AddFriendsActivity.this,
                                                            "Removed friend!",
                                                            Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    startActivity(new Intent(AddFriendsActivity.this,
                                                            FriendsActivity.class));
                                                }
                                            }
                                            Toast toast = Toast.makeText(AddFriendsActivity.this,
                                                    "Unable to remove friend",
                                                    Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                }
        );
    }
}

