package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        //Initialize Authentication/Database Client
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance().getReference().child("users").child(authUser.getUid());

        // UI references
        friendName = findViewById(R.id.profile_friendname);
        addButton = findViewById(R.id.add_button);
        removeButton = findViewById(R.id.remove_button);

        getData();

        findViewById(R.id.add_button).setOnClickListener(
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
                            try {
                                ArrayList<String> friends = user.getFriends();
                                friends.add(name);
                                user.setFriends(friends);
                            } catch (NullPointerException e) {
                                ArrayList<String> friends = new ArrayList<>();
                                friends.add(name);
                                user.setFriends(friends);
                            }

                            addedFriend = true;
                            // would be false if username not in the DB -> not handling that rn LOL
                            UserInformation newUser = new UserInformation(user.getFirstName(),
                                    user.getLastName(), user.getBirthdate(), authUser.getEmail(),
                                    user.getAllergens(), user.getFriends());
                            dataBase.setValue(newUser.toMap());

                            if (!addedFriend) {
                                Toast toast = Toast.makeText(AddFriendsActivity.this,
                                        "Unable to add friend",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(AddFriendsActivity.this,
                                        "Added friend!",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            startActivity(new Intent(AddFriendsActivity.this,
                                    FriendsActivity.class));
                        }
                    }
                }
        );
        findViewById(R.id.remove_button).setOnClickListener(
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
                            try {
                                ArrayList<String> friends = user.getFriends();
                                friends.removeAll(Collections.singleton(name));
                                user.setFriends(friends);
                            } catch (NullPointerException e) {
                                // nothing
                            }

                            // would be false if username not in the DB -> not handling that rn LOL
                            UserInformation newUser = new UserInformation(user.getFirstName(),
                                    user.getLastName(), user.getBirthdate(), authUser.getEmail(),
                                    user.getAllergens(), user.getFriends());
                            dataBase.setValue(newUser.toMap());

                            Toast toast = Toast.makeText(AddFriendsActivity.this,
                                    "Removed friend!",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            startActivity(new Intent(AddFriendsActivity.this,
                                    FriendsActivity.class));
                        }
                    }
                }
        );

    }
    private void getData() {
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserInformation.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
