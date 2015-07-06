package com.knowmiles.www.driverapp;

import android.content.Context;
import android.content.SharedPreferences;


public class UserLocalStore {
    public static final String SP_NAME = "kmDriverDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("name",user.name);
        spEditor.putString("mobile",user.mobile);
        spEditor.putString("cabInfo",user.cabInfo);
        spEditor.putString("servProv",user.servProv);
        spEditor.putString("passwrd",user.passwrd);
        spEditor.commit();
    }

    public User getLoggedInUser() {
        String name = userLocalDatabase.getString("name", "");
        String mobile = userLocalDatabase.getString("mobile","");
        String cabInfo = userLocalDatabase.getString("cabInfo","");
        String servProv = userLocalDatabase.getString("servProv","");
        String passwrd = userLocalDatabase.getString("passwrd","");

        User storedUser = new User(name, mobile, cabInfo, servProv, passwrd);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn() {
        if(userLocalDatabase.getBoolean("loggedIn",false)) {
            return true;
        }
        else {
            return false;
        }
    }
}
