package com.parking.parkingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

    @Override
    public void onBackPressed() {

    }
}