package com.parking.parkingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Registration extends AppCompatActivity {

    @BindView(R.id.first_name)
    EditText _firstNameText;
    @BindView(R.id.last_name)
    EditText _lastNameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.username)
    EditText _usernameText;
    @BindView(R.id.btn_SignUp)
    Button _signUpButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    @BindView(R.id.registration_form)
    View mRegistrationFormView;
    @BindView(R.id.registration_progress)
    View mProgressView;
    @BindView(R.id.messageLayout)
    View messageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        _signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    protected void signUp() {
        boolean cancel = false;
        View focusView = null;

        _signUpButton.setEnabled(false);
        _firstNameText.setError(null);
        _lastNameText.setError(null);
        _usernameText.setError(null);
        _emailText.setError(null);
        _passwordText.setError(null);

        //store value at the time of the registration attempt
        String fname = _firstNameText.getText().toString();
        String lname = _lastNameText.getText().toString();
        String username = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _firstNameText.setError("at least 3 characters");
            focusView = _firstNameText;
            cancel = true;
        }

        if (lname.isEmpty() || lname.length() < 3) {
            _lastNameText.setError("at least 3 characters");
            focusView = _lastNameText;
            cancel = true;
        }

        // Check for a valid email address.
        if (email.isEmpty()) {
            _emailText.setError(getString(R.string.error_field_required));
            focusView = _emailText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            _emailText.setError(getString(R.string.error_invalid_email));
            focusView = _emailText;
            cancel = true;
        }

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            focusView = _usernameText;
            cancel = true;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 characters");
            focusView = _passwordText;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
            _signUpButton.setEnabled(true);
        } else {
            showProgress(true);
            User user = new User(fname, email, username, password);
            registerUser(user);
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void registerUser(User user) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                showProgress(false);
                if (returnedUser == null) {
                    mRegistrationFormView.setVisibility(View.GONE);
                    messageLayout.setVisibility(View.VISIBLE);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 3 seconds
                            finish();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivityForResult(intent, 0);
                        }
                    }, 3000);
                } else {
                    _usernameText.setError(getString(R.string.error_incorrect_password));
                    _usernameText.requestFocus();
                    _signUpButton.setEnabled(true);
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
