package com.parking.parkingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Account extends AppCompatActivity {

    @BindView(R.id.CameraBtn)
    Button CameraBtn;
    @BindView(R.id.iv)
    ImageView imageView;
    @BindView(R.id.change_password_button) Button ChangePasswordBtn;
    @BindView(R.id.new_password)
    EditText newPasswordtxt;
    @BindView(R.id.current_password) EditText currentPasswordtxt;
    @BindView(R.id.password_form) View mPasswordFormView;
    @BindView(R.id.password_progress) View mProgressView;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    UserLocalStore userLocalStore;
    public static User user;
    String currentPassword;
    String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.bind(this);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir.isDirectory()) {
            String[] children = storageDir.list();
            for (String aChildren : children) {
                File imgFile = new File(storageDir, aChildren);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }

        CameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = Utility.checkPermissionWriteExternalStorage(Account.this);
                if (result)
                    selectImage();
            }
        });

        ChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean result = Utility.checkPermissionCamera(Account.this);
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    boolean result = Utility.checkPermissionReadExternalStorage(Account.this);
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == SELECT_FILE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    saveImage(bitmap);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (data != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);
                saveImage(thumbnail);
            }
        }
    }

    public void saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp;
            File f = new File(storageDir, imageFileName + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            if (storageDir.isDirectory()) {
                String[] children = storageDir.list();
                for (String aChildren : children) {
                    if (!aChildren.equals(imageFileName + ".jpg"))
                        new File(storageDir, aChildren).delete();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attemptChangePassword() {
        // Reset errors.
        newPasswordtxt.setError(null);
        currentPasswordtxt.setError(null);

        // Store values at the time of the login attempt.
        currentPassword = currentPasswordtxt.getText().toString();
        newPassword = newPasswordtxt.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordtxt.setError(getString(R.string.error_field_required));
            focusView = currentPasswordtxt;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordtxt.setError(getString(R.string.error_field_required));
            focusView = newPasswordtxt;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            ChangeUserPassword task = new ChangeUserPassword();
            task.execute();
        }
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

        mPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    public class ChangeUserPassword extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri("http://web-meisternj.com/Parking%20App/ChangePassword.php");
            p.setParam("email", user.email);
            p.setParam("oldPassword", currentPassword);
            p.setParam("newPassword", newPassword);

            return HttpManager.getData(p);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success\n")) {
                showProgress(false);
                currentPasswordtxt.getText().clear();
                newPasswordtxt.getText().clear();
                Snackbar.make(mPasswordFormView, "Password Updated", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
            else
            {
                showProgress(false);
                Snackbar.make(mPasswordFormView, "Error Updating Password", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        }
    }
}