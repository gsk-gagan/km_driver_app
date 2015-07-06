package com.knowmiles.www.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class HomePage extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtVLogout;

    TextView dName, cabInfo, servProv;

    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_appbar);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        changeToolbarTitle("My Toolbar");

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        //Finding the logout button
        txtVLogout = (TextView) findViewById(R.id.txt_v_logout);
        txtVLogout.setOnClickListener(this);

        //Finding the tags of basic info of user
        dName = (TextView) findViewById(R.id.txt_v_driver_name);
        cabInfo = (TextView) findViewById(R.id.txt_v_cab_info);
        servProv = (TextView) findViewById(R.id.txt_v_cab_provider);

        //Local Storage
        userLocalStore = new UserLocalStore(this);
    }

    //Change the toolbar title
    private void changeToolbarTitle(String newTitle) {
        getSupportActionBar().setTitle(newTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.txt_v_logout): {
                Toast.makeText(getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                //Move back to login Page
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticate()) {
            fillInDetails();
        }
        else {
            startActivity(new Intent(HomePage.this, LoginActivity.class));
        }
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    private void fillInDetails() {
        User driver = userLocalStore.getLoggedInUser();

        dName.setText(driver.name);
        cabInfo.setText(driver.cabInfo);
        servProv.setText(driver.servProv);
    }
}
