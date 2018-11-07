package com.parking.parkingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;

    @BindView(R.id.help_button) Button HelpBtn;
    @BindView(R.id.parking_lot_button) Button parkingBtn;
    @BindView(R.id.find_car_button) Button findCarBtn;

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
    }
}