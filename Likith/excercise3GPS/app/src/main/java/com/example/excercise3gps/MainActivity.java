package com.example.excercise3gps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        float minDistance = 10;
        long interval = 0;
        Location location; // location
        double latitude; // latitude
        double longitude; // longitude
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

/*        if (locationManager == null) {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
        }*/
/*        LocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, // use GPS device
                interval, // hint for notification interval
                minDistance, // hint for minimum position distance
                this); // callback receiver*/




/*        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }*/
        //LocationManager.removeUpdates(intent);

    }
}
