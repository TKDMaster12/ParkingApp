package com.parking.parkingapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

public class ServerRequest {
    public static final String SERVER_ADDRESS = "http://localhost:8080/";

    public ServerRequest(Context context) {
    }

    public void fetchUserDataInBackground(User user, GetUserCallBack CallBack) {
        new FetchUserDataAsyncTask(user, CallBack).execute();
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

                if (jObject.length() != 0)
                {
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
}