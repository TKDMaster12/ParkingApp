package com.parking.parkingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
    @BindView(R.id.parking_lot_form) View mParkingFormView;
    @BindView(R.id.parking_progress) View mProgressView;

    private static final int REQUEST_CODE = 0;
    UserLocalStore userLocalStore;
    public static User user;
    List<ParkingLotModel> parkingLotList;
    private static SecureRandom SECURE_RANDOM = new SecureRandom();
    String newCode;
    String selectedParkingLotName;
    String selectedParkingLotAmount;
    String selectedParkingLotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot);

        ButterKnife.bind(this);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        checkParkingSpot(user);
        showProgress(true);
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

                ParkingLotModel parkingLotModel = parkingLotList.get(position);
                if (parkingLotModel.getStatus().equals("AVAILABLE")) {
                    showProgress(true);
                    newCode = nextSessionId();
                    selectedParkingLotName = parkingLotModel.getName();
                    selectedParkingLotAmount = parkingLotModel.getAmountLeft();
                    selectedParkingLotId = parkingLotModel.getId();

                    InsertParkingLotData task = new InsertParkingLotData();
                    task.execute();
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
            showProgress(false);
        }
    }

    public class InsertParkingLotData extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri("http://web-meisternj.com/Parking%20App/insertParkingSpot.php");
            p.setParam("email", user.email);
            p.setParam("code", newCode);
            p.setParam("ParkingLotName", selectedParkingLotName);
            p.setParam("ParkingLotAmount", selectedParkingLotAmount);
            p.setParam("ParkingLotId", selectedParkingLotId);

            return HttpManager.getData(p);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("successsuccess\n")) {
                finish();
                Intent intent = new Intent(getApplicationContext(), Generated_Code.class);
                intent.putExtra("CODE_KEY", newCode);
                intent.putExtra("CODE_USER", user.username);
                startActivityForResult(intent, REQUEST_CODE);
            }
            else
            {
                showProgress(false);
                //error
            }
        }
    }

    private void checkParkingSpot(User userSpot) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.checkUserParkingSpotInBackground(userSpot, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser != null) {
                    showProgress(false);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mParkingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mParkingFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mParkingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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