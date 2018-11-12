package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    // UI references.
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText birthdateField;
    private RadioButton dairy;
    private RadioButton fish;
    private RadioButton peanuts;
    private RadioButton shellfish;
    private RadioButton soy;
    private RadioButton treenuts;
    private RadioButton gluten;
    private Button saveButton;

    // Data References
    private FirebaseAuth auth;
    private DatabaseReference dataBase;
    private FirebaseUser authUser;
    private UserInformation user;
    private Map<String, Boolean> userAllergies;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialize Authentication/Database Client
        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance().getReference().child("users").child(authUser.getUid());

        // UI references
        firstNameField = findViewById(R.id.profile_firstname);
        lastNameField = findViewById(R.id.profile_lastname);
        birthdateField = findViewById(R.id.profile_bday);
        dairy = findViewById(R.id.dairy);
        fish = findViewById(R.id.fish);
        peanuts = findViewById(R.id.peanuts);
        shellfish = findViewById(R.id.shellfish);
        soy = findViewById(R.id.soy);
        treenuts = findViewById(R.id.treenuts);
        gluten = findViewById(R.id.gluten);
        saveButton = findViewById(R.id.save_profile_button);

        // Set values of fields
        getData();

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                String firstname = firstNameField.getText().toString();
                String lastname = lastNameField.getText().toString();
                String birthday = birthdateField.getText().toString();

                Map<String, Boolean> newMap = new HashMap<>();
                newMap.put("dairy", dairy.isChecked());
                newMap.put("fish", fish.isChecked());
                newMap.put("peanuts", peanuts.isChecked());
                newMap.put("shellfish", shellfish.isChecked());
                newMap.put("soy", soy.isChecked());
                newMap.put("treenuts", treenuts.isChecked());
                newMap.put("gluten", gluten.isChecked());

                user = new UserInformation(firstname, lastname, birthday,
                        authUser.getEmail(), newMap);

                dataBase.setValue(user.toMap());
                startActivity(new Intent(ProfileActivity.this,  HomeActivity.class));
            }
        });

    }
    private void getData() {
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(UserInformation.class);
                Map<String, Boolean> allergies = user.getAllergens();

                firstNameField.setText(user.getFirstName());
                lastNameField.setText(user.getLastName());
                birthdateField.setText(user.getBirthdate());
                dairy.setChecked(allergies.get("dairy"));
                fish.setChecked(allergies.get("fish"));
                peanuts.setChecked(allergies.get("peanuts"));
                shellfish.setChecked(allergies.get("shellfish"));
                soy.setChecked(allergies.get("soy"));
                treenuts.setChecked(allergies.get("treenuts"));
                gluten.setChecked(allergies.get("gluten"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
