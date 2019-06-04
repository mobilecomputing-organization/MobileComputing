package mobilecomp.app1.gps_logger;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast toast = Toast.makeText(this,"Please enable Location",Toast.LENGTH_LONG);
            toast.show();
        }


    }
}
