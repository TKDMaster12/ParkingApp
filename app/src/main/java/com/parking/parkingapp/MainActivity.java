package com.parking.parkingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    UserLocalStore userLocalStore;
    User user;

    @BindView(R.id.help_button) Button HelpBtn;
    @BindView(R.id.parking_lot_button) Button parkingBtn;
    @BindView(R.id.find_car_button) Button findCarBtn;
    @BindView(R.id.username) TextView userName;
    @BindView(R.id.logout_button) Button logoutBtn;
    @BindView(R.id.profileImage) ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir.isDirectory()) {
            String[] children = storageDir.list();
            for (String aChildren : children) {
                File imgFile = new File(storageDir, aChildren);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileImage.setImageBitmap(myBitmap);
            }
        }

        HelpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        parkingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Parking_Lot.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        findCarBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Find_Car.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        userName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Account.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (!authenticate())
            startActivity(new Intent(MainActivity.this, Login.class));
        else
            getUserName();
    }

    private boolean authenticate()
    {
        return userLocalStore.getUserLoggedIn();
    }

    private void getUserName()
    {
        user = userLocalStore.getLoggedInUser();
        userName.setText(user.username);
    }

    public void showAlertDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle("Log Out");
        dialogBuilder.setMessage("Are you sure you want to Log Out?");
        dialogBuilder.setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                finish();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent, 0);
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", null);
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    @Override
    public void onBackPressed() {

    }
}