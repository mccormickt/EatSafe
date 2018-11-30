package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    // UI references.
    private TextView friendsBody;

    // Data References
    private FirebaseAuth auth;
    private DatabaseReference dataBase;
    private FirebaseUser authUser;
    private UserInformation user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Initialize Authentication/Database Client
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance().getReference().child("users").child(authUser.getUid());

        // UI references
        friendsBody = findViewById(R.id.friends_body);

        // Set values of fields
        getData();

        findViewById(R.id.addFriends_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(FriendsActivity.this,
                                AddFriendsActivity.class));
                    }
                }
        );

    }
    private void getData() {
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserInformation.class);
                String text = "";
                //user.setFriends(new ArrayList<String>());
                try {
                    ArrayList<String> friends = user.getFriends();
                    if (friends.size() == 0) {
                        text = "No friends to display";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (String friend : friends) {
                            System.out.println(friend);
                            String str = friend + "\n";
                            sb.append(str);
                        }
                        text = sb.toString();
                    }
                } catch (NullPointerException e) {
                    text = "No friends to display";
                }
                friendsBody.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
