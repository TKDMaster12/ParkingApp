package com.parking.parkingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Parking_Lot extends AppCompatActivity {

    CustomAdapter adapter;
    @BindView(R.id.list) ListView listView;
    private static final int REQUEST_CODE = 0;
    UserLocalStore userLocalStore;
    public static User user;
    List<ParkingLotModel> parkingLotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot);

        ButterKnife.bind(this);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        MyTask task = new MyTask();
        task.execute();
    }

    protected void updateDisplay()
    {
        adapter = new CustomAdapter(parkingLotList, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ParkingLotModel parkingLotModel= parkingLotList.get(position);

                Intent intent = new Intent(getApplicationContext(), Generated_Code.class);
                intent.putExtra("CODE_KEY", parkingLotModel.getName());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    public class MyTask extends AsyncTask<Void, Void, List<ParkingLotModel>>
    {
        @Override
        protected List<ParkingLotModel> doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri("http://web-meisternj.com/Parking%20App/retreveParkingLots.php");
            p.setParam("email", user.email);
            p.setParam("username", user.username);

            String content = HttpManager.getData(p);
            parkingLotList = JSON_Parser.parseFeed(content);

            return parkingLotList;
        }

        @Override
        protected void onPostExecute(List<ParkingLotModel> result) {
            updateDisplay();
        }
    }
}