package com.knowmiles.www.driverapp;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;


public class HomePage extends AppCompatActivity implements View.OnClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    Toolbar toolbar;
    TextView txtVLogout;

    TextView dName, cabInfo, servProv;

    UserLocalStore userLocalStore;

    GoogleMap mMap;
    MapView mMapView;

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 16;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_appbar);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Changing Toolbar Title
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

        //Maps
        if(servicesOK()) {
            mMapView = (MapView) findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);

            if(initMap()) {
                Toast.makeText(getApplicationContext(),"Ready to Map",Toast.LENGTH_SHORT).show();

//                double iitLat = 28.5457419;
//                double iitLng = 77.1896732;
//                gotoLocation(iitLat,iitLng,DEFAULT_ZOOM);
                mGoogleApiClient = new GoogleApiClient.Builder(this, this, this)
                        .addApi(LocationServices.API)
                        .build();

            } else {
                Toast.makeText(getApplicationContext(),"Map Not Available",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"Work without Map or Upgrade Modbile Phone",Toast.LENGTH_SHORT).show();
        }
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
                break;
            }
        }
    }

    public void buttonClickedHome(View v) {
        switch (v.getId()) {
            case (R.id.btn_my_location): {
                Location currentlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if(currentlocation == null) {
                    Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
                } else {
                    gotoLocation(currentlocation.getLatitude(),currentlocation.getLongitude(),DEFAULT_ZOOM);
                }
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        if(authenticate()) {
            fillInDetails();
        }
        else {
            startActivity(new Intent(HomePage.this, LoginActivity.class));
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
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

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS) {
            return  true;
        }
        else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Can't Connect to Google Play Services",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Maps Over ride Methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    //Other Methods to access Map
    private boolean initMap() {
        if(mMap == null) {
            mMap = mMapView.getMap();
        }
        return (mMap != null);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(update);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // this callback will be invoked when all specified services are connected
        Location currentlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(currentlocation == null) {
            Toast.makeText(this, "Location Not Available, try turning on GPS", Toast.LENGTH_SHORT).show();
        } else {
            gotoLocation(currentlocation.getLatitude(),currentlocation.getLongitude(),DEFAULT_ZOOM);
        }

        //Recurring Location request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(5000)
        .setInterval(20000);
//                .setSmallestDisplacement(75.0F)
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // this callback will be invoked when the client is disconnected
        // it might happen e.g. when Google Play service crashes
        // when this happens, all requests are canceled,
        // and you must wait for it to be connected again
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // this callback will be invoked when the connection attempt fails

        if (connectionResult.hasResolution()) {
            // Google Play services can fix the issue
            // e.g. the user needs to enable it, updates to latest version
            // or the user needs to grant permissions to it
            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (Exception e) {
                // it happens if the resolution intent has been canceled,
                // or is no longer able to execute the request
            }
        } else {
            // Google Play services has no idea how to fix the issue
        }
    }

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {
        // this callback is invoked when location updates
        String msg = "Location: " + location.getLatitude() + ", " + location.getLongitude();
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
