package com.parking.parkingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Recover_Information extends AppCompatActivity {

    // UI references.
    @BindView(R.id.email_password) AutoCompleteTextView mEmailPasswordView;
    @BindView(R.id.email_username) AutoCompleteTextView mEmailUsernameView;
    @BindView(R.id.send_password_button) Button SendPasswordButton;
    @BindView(R.id.send_username_button) Button SendUsernameButton;
    @BindView(R.id.recover_information_form) View mRecoveryFormView;
    @BindView(R.id.recovery_progress) View mProgressView;
    @BindView(R.id.messageLayout) View messageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_information);

        ButterKnife.bind(this);
        mEmailPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSendPassword();
                    return true;
                }
                return false;
            }
        });
        mEmailUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSendUsername();
                    return true;
                }
                return false;
            }
        });
        SendPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendPassword();
            }
        });
        SendUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSendUsername();
            }
        });
    }

    private void attemptSendPassword() {
        // Reset errors.
        mEmailPasswordView.setError(null);

        // Store values at the time of the recovery attempt.
        String email = mEmailPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailPasswordView.setError(getString(R.string.error_field_required));
            focusView = mEmailPasswordView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailPasswordView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt recover and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user recovery attempt.
            showProgress(true);
            User user = new User(email);
            sendPassword(user);
        }
    }

    private void attemptSendUsername() {
        // Reset errors.
        mEmailUsernameView.setError(null);

        // Store values at the time of the recovery attempt.
        String email = mEmailUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailUsernameView.setError(getString(R.string.error_field_required));
            focusView = mEmailUsernameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt recover and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user recovery attempt.
            showProgress(true);
            User user = new User(email);
            sendUsername(user);
        }
    }


    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

        mRecoveryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRecoveryFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecoveryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void sendPassword(User user) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.SendEmailPasswordInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                showProgress(false);
                if (returnedUser == null) {
                    mRecoveryFormView.setVisibility(View.GONE);
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
                    mEmailPasswordView.setError(getString(R.string.error_invalid_email));
                    mEmailPasswordView.requestFocus();
                }
            }
        });
    }

    private void sendUsername(User user) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.SendEmailUserNameInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                showProgress(false);
                if (returnedUser == null) {
                    mRecoveryFormView.setVisibility(View.GONE);
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
                    mEmailUsernameView.setError(getString(R.string.error_invalid_email));
                    mEmailUsernameView.requestFocus();
                }
            }
        });
    }
}