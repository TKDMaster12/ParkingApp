package com.parking.parkingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.math.BigInteger;
import java.security.SecureRandom;
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
    private static SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot);

        ButterKnife.bind(this);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        checkParkingSpot(user);
    }

    public static String nextSessionId() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }

    protected void updateDisplay()
    {
        adapter = new CustomAdapter(parkingLotList, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ParkingLotModel parkingLotModel= parkingLotList.get(position);
                if (parkingLotModel.getStatus().equals("AVAILABLE")) {

                    //TODO: insert code in database and update other table
                    String newCode = nextSessionId();

                    Intent intent = new Intent(getApplicationContext(), Generated_Code.class);
                    intent.putExtra("CODE_KEY", newCode);
                    intent.putExtra("CODE_USER", user.username);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Snackbar.make(view, parkingLotModel.getName()+"\n Is Full!", Snackbar.LENGTH_LONG)
                             .setAction("No action", null).show();
                }
            }
        });
    }

    public class FetchParkingLotData extends AsyncTask<Void, Void, List<ParkingLotModel>>
    {
        @Override
        protected List<ParkingLotModel> doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri("http://web-meisternj.com/Parking%20App/retreveParkingLots.php");

            String content = HttpManager.getData(p);
            parkingLotList = JSON_Parser.parseFeed(content);

            return parkingLotList;
        }

        @Override
        protected void onPostExecute(List<ParkingLotModel> result) {
            updateDisplay();
        }
    }

    private void checkParkingSpot(User userSpot) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.checkUserParkingSpotInBackground(userSpot, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser != null) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), Generated_Code.class);
                    intent.putExtra("CODE_KEY", returnedUser.name);
                    intent.putExtra("CODE_USER", user.username);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    FetchParkingLotData task = new FetchParkingLotData();
                    task.execute();
                }
            }
        });
    }
}