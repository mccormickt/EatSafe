package edu.gatech.cc.eatsafe;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The constant VALID_EMAIL_ADDRESS_REGEX.
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    // Database References
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        //Initialize Authentication Client
        mAuth = FirebaseAuth.getInstance();

        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(
                            final TextView textView,
                            final int id, final KeyEvent keyEvent) {
                        if ((id == R.id.sign_in) || (id == EditorInfo.IME_NULL)) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

        final Button mEmailSignInButton = findViewById(R.id.sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                attemptLogin();
            }
        });

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

        Button forgotPasswordButton = findViewById(R.id.forgot_password);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Forgotten Password");

                Editable emailText = mEmailView.getText();
                String emailString = emailText.toString();

                mAuth.sendPasswordResetEmail(emailString)
                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(
                                    @NonNull final Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast pHolder = Toast.makeText(
                                            LoginActivity.this,
                                            "Email sent.",
                                            Toast.LENGTH_SHORT);
                                    pHolder.show();
                                }
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialog,
                                    final int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //IMPORTANT MESSAGE
        //THIS CODE IS ONLY USED TO TEST API USAGE
        /*GetNutrition tst = new GetNutrition();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            String outpt = tst.getFoodByFoodGroup(0100);
            Log.d("test", outpt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //END API TEST
        */

        // Store values at the time of the login attempt.
        Editable pHolder = mEmailView.getText();
        String email = pHolder.toString();

        pHolder = mPasswordView.getText();
        String password = pHolder.toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
        }

        // perform the user login attempt.
        mAuth.signInWithEmailAndPassword(email, password) .addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(
                            @NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast pHolder = Toast.makeText(LoginActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT);
                            pHolder.show();
                            cancelLogin();
                        }
                    }
                });
    }

    // Cancels the login
    private void cancelLogin() {
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }

    // Checks email against a valid email regex
    private boolean isEmailValid(final String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private boolean isPasswordValid(final String password) {
        return password.length() > 4;
    }

}


