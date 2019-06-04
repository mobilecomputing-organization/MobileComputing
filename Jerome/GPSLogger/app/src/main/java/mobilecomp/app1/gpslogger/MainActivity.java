package mobilecomp.app1.gpslogger;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

enum ServiceStates{
    STARTED,
    STOPPED
}


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private Intent locService;
    private static final String TAG = "MainActivity";
    private ServiceStates ServiceState = ServiceStates.STOPPED;
    private IServiceLocation serviceLocationProxy = null;

    private TextView averageSpeedTextView;
    private TextView distanceTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle for text views
        averageSpeedTextView = findViewById(R.id.SpeedText);
        distanceTextView = findViewById(R.id.DistanceText);
        latitudeTextView = findViewById(R.id.LatitudeText);
        longitudeTextView = findViewById(R.id.LongitutdeText);

        // Handle for Buttons
        Button startServiceButton = findViewById(R.id.StartButton);
        Button stopServiceButton = findViewById(R.id.StopButton);
        Button updateValueButton = findViewById(R.id.UpdateButton);
        Button exitButton = findViewById(R.id.ExitButton);

        // Create new intent, to start and stop the service.
        locService = new Intent(this, ServiceLocation.class);

        /* Definition for On click listener for startServiceButton button */
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if service already active. Only one service to run at a given point of time.
                if (ServiceState == ServiceStates.STOPPED) {

                    // Start Service explicitly
                    bindService(locService, MainActivity.this , BIND_AUTO_CREATE);
                    ServiceState = ServiceState.STARTED;
                } else {
                    Log.i(TAG,
                            " Ignoring a trigger to start a service already in STARTED state");
                }
            }
        });

        /* Definition for On click listener for stopServiceButton button */
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if service already active. Only one service to run at a given point of time.
                if (ServiceState == ServiceStates.STARTED) {

                    // Stop Service explicitly
                    unbindService(MainActivity.this);
                    ServiceState = ServiceState.STOPPED;
                }
                else
                {
                    Log.i(TAG,
                            " Ignoring a trigger to stop a service already in STOPPED state");
                }
            }
        });

        /* Definition for On click listener for updateValueButton button */
        updateValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if service already active. Only one service to run at a given point of time.
                if (ServiceState == ServiceStates.STARTED) {
                    try {
                        latitudeTextView.setText(String.format("%.6f", serviceLocationProxy.rpc_getLatitude()));
                        longitudeTextView.setText(String.format("%.6f", serviceLocationProxy.rpc_getLongitude()));
                        averageSpeedTextView.setText(String.format("%.4f", serviceLocationProxy.rpc_getAverageSpeed())+" m/s");
                        distanceTextView.setText(String.format("%.4f", serviceLocationProxy.rpc_getDistance())+" m");
                    }
                    catch (RemoteException ex)
                    {
                        Log.i(TAG, " RemoteException thrown.");
                        // Do nothing
                    }
                }
                else
                {
                    Log.i(TAG, " Ignoring a trigger to Update, service not STARTED.");
                    Toast.makeText(v.getContext(),
                            " Ignoring a trigger to Update, service not STARTED. ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        /* Definition for On click listener for exitButton button */
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Exit");
                builder.setMessage("Do you want to exit ??");
                builder.setPositiveButton("Yes. Exit now!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, " App exiting ");
//                        finishAffinity();
//                        System.exit(0);
                        finish();
                    }
                });
                builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName CompName, IBinder ibinder) {
        serviceLocationProxy = IServiceLocation.Stub.asInterface(ibinder);
        Log.i(TAG," onServiceConnected called");

    }

    @Override
    public void onServiceDisconnected(ComponentName CompName) {

        serviceLocationProxy = null;
        Log.i(TAG," onServiceDisconnected called");
    }
}
