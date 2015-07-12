package com.knowmiles.www.driverapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;


public class ServerRequests {
    ProgressDialog progressDialog;
    public static final String SERVER_ADDRESS = "http://knowmiles.hostingsiteforfree.com/driver/";
    int maxRetries = 15;

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

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;
        String result;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
            result = "una";
        }

        @Override
        protected User doInBackground(Void... params) {
            //Sending the File to connect to
            ServerConnection serverConnection = new ServerConnection(SERVER_ADDRESS + "register.php");

            //Inserting data to send
            serverConnection.putPair("name", user.name);
            serverConnection.putPair("mobile", user.mobile);
            serverConnection.putPair("cab_info", user.cabInfo);
            serverConnection.putPair("serv_prov", user.servProv);
            serverConnection.putPair("passwrd", user.passwrd);

            try{
                //Executing the server connection
                String returnedString = serverConnection.execute();

                Log.i("gsk", "The values received in the store part are as follows:");
                Log.i("gsk", returnedString);

                //To use the first 5 letters returned from the server
                //  alr - Another user with same mobile number already exists
                //  don - User created
                //  una - Unable to connect to the server - This will be the default value to be presnet in the string
                String opt = returnedString.substring(0,5);
                Log.i("gsk",opt);
                if(opt.equals("*?alr"))
                    result = "alr";
                if(opt.equals("*?don"))
                    result = "don";
            } catch (Exception e) {
                e.printStackTrace();
            }


            User returnedUser = new User(result,result,result,result,result);

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
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
                int retryTimes = 0;
                String returnedString = "";
                while(retryTimes < maxRetries && returnedString.equals(""))
                {
                    returnedString = serverConnection.execute();
                    retryTimes += 1;
                    Log.i("gsk","No. of times tried " + retryTimes);
                }

                Log.i("gsk","The values received in the retrieve part are as follows:");
                Log.i("gsk",returnedString);

                if(returnedString.substring(0,11).equals("Not_Present")) {
                    returnedUser = new User("not_present", "not_present", "not_present", "not_present", "not_present");
                }
                else {
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
