package com.knowmiles.www.driverapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class ServerRequests {
    ProgressDialog progressDialog;
    public static final String SERVER_ADDRESS = "http://knowmiles.hostingsiteforfree.com/driver/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallback).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Sending the File to connect to
            ServerConnection serverConnection = new ServerConnection(SERVER_ADDRESS + "register.php");

            //Inserting data to send
            serverConnection.putPair("name", user.name);
            serverConnection.putPair("mobile", user.mobile);
            serverConnection.putPair("cab_info", user.cabInfo);
            serverConnection.putPair("serv_prov", user.servProv);
            serverConnection.putPair("passwrd", user.passwrd);

            //Executing the server connection
            String returnedString = serverConnection.execute();

            Log.i("gsk", "The values received in the store part are as follows:");
            Log.i("gsk", returnedString);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            userCallback.done(null);

            super.onPostExecute(aVoid);
        }
    }


    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ServerConnection serverConnection = new ServerConnection(SERVER_ADDRESS + "fetchUserData.php");
            serverConnection.putPair("mobile", user.mobile);
            serverConnection.putPair("passwrd", user.passwrd);

            User returnedUser = null;
            try {
                String returnedString = serverConnection.execute();

                Log.i("gsk","The values received in the retrieve part are as follows:");
                Log.i("gsk",returnedString);

                //JSON Reading
                JSONObject jObject = new JSONObject(returnedString);

                if(jObject.length()==0)
                    returnedUser = null;
                else {
                    String name = jObject.getString("name");
                    String mobile = jObject.getString("mobile");
                    String cab_info = jObject.getString("cab_info");
                    String serv_prov = jObject.getString("serv_prov");
                    String passwrd = jObject.getString("passwrd");

                    returnedUser = new User(name, mobile, cab_info, serv_prov, passwrd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);

            super.onPostExecute(returnedUser);
        }
    }
}
