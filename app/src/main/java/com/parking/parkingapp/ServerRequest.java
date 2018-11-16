package com.parking.parkingapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.List;

public class ServerRequest {
    public static final String SERVER_ADDRESS = "http://web-meisternj.com/Parking%20App/";

    public ServerRequest(Context context) {
    }

    public void fetchUserDataInBackground(User user, GetUserCallBack CallBack) {
        new FetchUserDataAsyncTask(user, CallBack).execute();
    }

    public void SendEmailDataInBackground(User user, GetUserCallBack CallBack) {
        new SendEmailDataAsyncTask(user, CallBack).execute();
    }

    public void storeUserDataInBackground(User user, GetUserCallBack userCallBack) {
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    public void checkUserParkingSpotInBackground(User user, GetUserCallBack userCallBack) {
        new CheckUserParkingSpotAsyncTask(user, userCallBack).execute();
    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallBack;

        public FetchUserDataAsyncTask(User user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri(SERVER_ADDRESS + "login.php");
            p.setParam("username", user.username);
            p.setParam("password", user.password);

            String content = HttpManager.getData(p);

            User returnedUser = null;
            try {
                JSONObject jObject = new JSONObject(content);

                if (jObject.length() != 0) {
                    String name = jObject.getString("name");
                    String email = jObject.getString("email");
                    returnedUser = new User(name, email, user.username, user.password);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            userCallBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class SendEmailDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallBack;

        public SendEmailDataAsyncTask(User user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri(SERVER_ADDRESS + "forgetPassword.php");
            p.setParam("email", user.email);

            User returnedUser = null;
            String content = HttpManager.getData(p);
            try {
                JSONObject jObject = new JSONObject(content);
                if (jObject.length() != 0) {
                    String name = jObject.getString("name");
                    returnedUser = new User(name, user.email, user.username, user.password);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            userCallBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, User>
    {
        User user;
        GetUserCallBack userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallBack userCallBack){
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri(SERVER_ADDRESS + "registration.php");
            p.setParam("name", user.name);
            p.setParam("email", user.email);
            p.setParam("username", user.username);
            p.setParam("password", user.password);

            String content = HttpManager.getData(p);
            User returnedUser = null;
            try{
                JSONObject jObject = new JSONObject(content);

                if (jObject.length() != 0)
                {
                    String name = jObject.getString("name");
                    returnedUser = new User(name, user.email, user.username, user.password);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            userCallBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class CheckUserParkingSpotAsyncTask extends AsyncTask<Void, Void, User>
    {
        User user;
        GetUserCallBack userCallBack;

        public CheckUserParkingSpotAsyncTask(User user, GetUserCallBack userCallBack){
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            RequestPackage p = new RequestPackage();
            p.setMethod("POST");
            p.setUri(SERVER_ADDRESS + "CheckUserParkingSpot.php");
            p.setParam("email", user.email);
            p.setParam("username", user.username);

            String content = HttpManager.getData(p);
            User returnedUser = null;
            try{
                JSONObject jObject = new JSONObject(content);

                if (jObject.length() != 0)
                {
                    String name = jObject.getString("name");
                    returnedUser = new User(name, user.email, user.username, user.password);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            userCallBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }
}