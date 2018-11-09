package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static edu.gatech.cc.eatsafe.LoginActivity.VALID_EMAIL_ADDRESS_REGEX;

public class RegistrationActivity extends AppCompatActivity {

    //Tag for debug logging
    private static final String TAG = "RegisterUser";

    // View references
    private EditText mEmailView;
    private EditText mPasswordView;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set up views
        mEmailView = findViewById(R.id.user_email_string);

        mPasswordView = findViewById(R.id.password_string);
        mPasswordView.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    final TextView textView,
                    final int id,
                    final KeyEvent keyEvent) {
                if ((id == R.id.sign_in) || (id == EditorInfo.IME_NULL)) {
                    registerUser();
                    return true;
                }
                return false;
            }
        });

        Button registrationButton = findViewById(R.id.registration_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                registerUser();
            }
        });
    }

    /**
     * Creates a user in firebase with the given email and password. Since
     * this is a basic app, passwords are not hashed.
     * This is not acceptable in a full size app, but shortcuts are shortcuts.
     *
     * WARNING TO ANYONE REVIEWING THIS CODE. DO NOT USE REAL PASSWORDS.
     * THEY ARE STORED IN PLAIN TEXT. DO NOT USE REAL PASSWORDS.
     */
    private void registerUser() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Grab the values from the form
        Editable pHolder = mEmailView.getText();
        String email = pHolder.toString();

        pHolder = mPasswordView.getText();
        String password = pHolder.toString();

        // Local vars
        View focusView = null;
        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (!cancel) {
            // Create user authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(
                                @NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast pHolder = Toast.makeText(RegistrationActivity.this,
                                        "Registration Complete!",
                                        Toast.LENGTH_SHORT);
                                pHolder.show();

                                // Add user to database
                                FirebaseUser user = mAuth.getCurrentUser();
                                DatabaseReference reference = mDatabase.child("users").child(user.getUid()).push();
                                reference.setValue(new UserInformation("", "", null,
                                        user.getEmail(), new ArrayList<String>()));

                                startActivity(
                                        new Intent(RegistrationActivity.this, HomeActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast pHolder = Toast.makeText(RegistrationActivity.this,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT);
                                pHolder.show();
                            }
                        }
                    });


        } else {
            focusView.requestFocus();
        }
    }

    // Checks email against a valid email regex
    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
