package com.threeguys.scrummy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_ACTIVITY_TAG = LoginActivity.class.getSimpleName();

    private EditText mEmailTextField;
    private EditText mPasswordTextField;

    private Button mLoginButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailTextField = findViewById(R.id._emailTextField);
        mPasswordTextField = findViewById(R.id._passwordTextField);

        mLoginButton = findViewById(R.id._loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    LoginActivity.this.startActivity(
                            new Intent(LoginActivity.this, MainActivity.class)
                    );
                }
            }
        };

        if(getSupportActionBar() == null) {
            Log.d(LOGIN_ACTIVITY_TAG, "Uh oh, the action bar is null!");
        } else {
            getSupportActionBar().setTitle(getString(R.string.action_sign_in_short));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id._helpMenuItem:
                startActivity(new Intent(LoginActivity.this, HelpActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    protected void onClickLogin() {
//        setContentView(R.layout.activity_login);
//        mEmailTextField = findViewById(R.id._emailTextField);
//        mPasswordTextField = findViewById(R.id._passwordTextField);
//
//        mLoginButton = findViewById(R.id._loginButton);
//
//        mLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startSignIn();
//            }
//        });
//    }
//
//    protected void onClickRegister() {
//        setContentView(R.layout.activity_register);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        toggleUiVisibility(true);
        if(!isConnected()) {
            Log.i(LOGIN_ACTIVITY_TAG, "No internet connection");
        }

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void startSignIn() {

        final String email = mEmailTextField.getText().toString();
        final String password = mPasswordTextField.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordTextField.setError(getString(R.string.error_field_required));
            focusView = mPasswordTextField;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordTextField.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordTextField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailTextField.setError(getString(R.string.error_field_required));
            focusView = mEmailTextField;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailTextField.setError(getString(R.string.error_invalid_email));
            focusView = mEmailTextField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            toggleUiVisibility(false);
            findViewById(R.id._loadProgress).setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        LoginActivity.this.registerNewUser(email, password);
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return (email.contains("@") && email.contains("."));
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }

    private void registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    findViewById(R.id._loadProgress).setVisibility(View.GONE);
                    if(!isConnected()) {
                        Log.i(LOGIN_ACTIVITY_TAG, "No internet connection");
                    } else {
                        toggleUiVisibility(true);
                        mPasswordTextField.setError(getString(R.string.error_incorrect_password));
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Account created with email" +
                                    mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Toggles all UI elements of LoginActivity screen
     * @param isVisible true sets to VISIBLE, false sets to GONE
     */
    private void toggleUiVisibility(boolean isVisible) {
        if(isVisible) {
            findViewById(R.id._emailTextField).setVisibility(View.VISIBLE);
            findViewById(R.id._passwordTextField).setVisibility(View.VISIBLE);
            findViewById(R.id._loginButton).setVisibility(View.VISIBLE);
            findViewById(R.id._registerInfoTextView).setVisibility(View.VISIBLE);
            findViewById(R.id._scrummyIconImageView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id._emailTextField).setVisibility(View.GONE);
            findViewById(R.id._passwordTextField).setVisibility(View.GONE);
            findViewById(R.id._loginButton).setVisibility(View.GONE);
            findViewById(R.id._registerInfoTextView).setVisibility(View.GONE);
            findViewById(R.id._scrummyIconImageView).setVisibility(View.GONE);
        }
    }


    /**
     * Checks to see if the device is connected to the internet via wifi or mobile data
     * @return true if connected, false if not connected
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            showConnectionDialog();
            return false;
        }
    }

    /**
     * Displays a dialog reporting to the user that they must be connected to the internet
     */
    private void showConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_no_internet)
                .setCancelable(false)
                .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
