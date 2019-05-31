package com.example.excercise3gps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    float minDistance = 10;
    long interval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

/*        if (locationManager == null) {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
        }*/

        LocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, // use GPS device
                interval, // hint for notification interval
                minDistance, // hint for minimum position distance
                listener); // callback receiver


        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //LocationManager.removeUpdates(intent);

    }



}
