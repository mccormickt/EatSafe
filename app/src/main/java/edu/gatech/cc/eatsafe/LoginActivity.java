package edu.gatech.cc.eatsafe;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity
        extends AppCompatActivity
        implements LoaderCallbacks<Cursor> {

    /**
     * The constant VALID_EMAIL_ADDRESS_REGEX.
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[] {
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        final Button mEmailSignInButton =
                (Button) findViewById(R.id.sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(final View view) {
                attemptLogin();
            }
        });


        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Toast toast = Toast.makeText(LoginActivity.this,
                        "Coming Soon!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

//        Button forgotPasswordButton =
//                (Button) findViewById(R.id.forgot_password);
//
//        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                //noinspection ChainedMethodCall method breaks if not chained
//                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//                alertDialog.setTitle("Forgotten Password");
//
//                Editable emailText = mEmailView.getText();
//                String emailString = emailText.toString();
//
//                //noinspection ChainedMethodCall Needed for onclick
//                mAuth.sendPasswordResetEmail(emailString)
//                        .addOnCompleteListener(
//                                new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(
//                                            @NonNull final Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast pHolder = Toast.makeText(
//                                                    LoginActivity.this,
//                                                    "Email sent.",
//                                                    Toast.LENGTH_SHORT);
//                                            pHolder.show();
//                                        }
//                                    }
//                                });

//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(
//                                    final DialogInterface dialog,
//                                    final int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();
//            }
//        });


//        mLoginFormView = findViewById(R.id.login_form);
//        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        //noinspection ChainedMethodCall Isn't chained
        getLoaderManager().initLoader(0, null, this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean mayRequestContacts() {
        if (checkSelfPermission(READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar pHolder = Snackbar.make(mEmailView,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE);
            pHolder.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(final View v) {
                    requestPermissions(
                            new String[]{READ_CONTACTS},
                            REQUEST_READ_CONTACTS);
                }
            });
        } else {
            requestPermissions(new String[]{READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        //IMPORTANT MESSAGE
        //IMPORTANT
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

        // Local variables
        boolean cancel = false;
        View focusView = null;

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

        // Cancellation parts
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress();
            if (Arrays.asList(DUMMY_CREDENTIALS).contains(email + ":" + password)) {
                Toast toast = Toast.makeText(LoginActivity.this,
                        "Login Successful!",
                        Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(LoginActivity.this,
                        HomeActivity.class));
            } else {
                Toast toast = Toast.makeText(LoginActivity.this,
                        "Login Failed.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }


            //noinspection ChainedMethodCall Needed for onclick
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this,
//                            new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(
//                                        @NonNull final Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        // Sign in success,
//                                        // update UI with the
//                                        // signed-in user's information
//                                        startActivity(new Intent(LoginActivity.this,
//                                                LogoutScreen.class));
//                                    } else {
//                                        // If sign in fails,
//                                        // display a message to the user.
//                                        Toast pHolder = Toast.makeText(LoginActivity.this,
//                                                "Authentication failed.",
//                                                Toast.LENGTH_SHORT);
//                                        pHolder.show();
////                                numberOfTries++;
////                                sendToBan();
//                                        cancelLogin();
//                                    }
//                                }
//                            });
        }
    }

    // Checks email against a valid email regex
    private boolean isEmailValid(final CharSequence email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private boolean isPasswordValid(final CharSequence password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        //noinspection ChainedMethodCall Needs chain
        int shortAnimTime = getResources().
                getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(View.GONE);
        //noinspection ChainedMethodCall Needs chaining on the animate statement
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mLoginFormView.setVisibility(View.GONE);
            }
        });

        mProgressView.setVisibility(View.VISIBLE);
        //noinspection ChainedMethodCall Needs chaining on the animate statement
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(View.VISIBLE);
            }
        });

//        Button cancelButton = (Button) findViewById(R.id.cancel_button);
//        cancelButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                cancelLogin();
//            }
//        });

    }

    private void cancelLogin() {
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }


    /**
     * The onCreateLoader to get the profile of the user
     * @param i an integer i
     * @param bundle the final Bundle
     * @return the Loader coming from the Cursor
     */
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE
                        + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email
                        .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /**
     * The onLoadFinished loads the data after the user logs in
     * @param cursorLoader loads all the data
     * @param cursor the cursor to load the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    /**
     * The onLoaderReset method
     * @param cursorLoader loads all the data
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(final List<String>
                                                 emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView
        // what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        emailAddressCollection);

//        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        /**
         * The Projection.
         */
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        /**
         * The constant ADDRESS.
         */
        int ADDRESS = 0;
        /**
         * The constant IS_PRIMARY.
         */
        int IS_PRIMARY = 1;
    }
}

