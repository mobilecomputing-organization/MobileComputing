package com.example.testapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private BluetoothDevice MyDevice;
    private ScanCallback scanCallback;
    private Handler mHandler;
    private static final String TAG = "MyApp";
    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 10000;// 10 seconds
    private String appname = null;
    private boolean IsScanSuccess = false;

    private Button ScanWeatherButton;
    private Button ScanFanButton;
    private Button ConnectButtton;
    private TextView DispText;

//=====================================//
    // SOME LOCAL FUNCTIONS
//=====================================//
    public void StartScanning(boolean IsWeatherSelected){
        ConnectButtton.setVisibility(View.INVISIBLE);
        DispText.setText("");
        LeScanner = bluetoothAdapter.getBluetoothLeScanner();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IsScanSuccess == false) {
                    ScanWeatherButton.setText("SCAN TEMP");
                    ScanFanButton.setText("SCAN FAN");
                    ScanFanButton.setClickable(true);
                    ScanWeatherButton.setClickable(true);

                    LeScanner.stopScan(scanCallback);
                } else {
                    IsScanSuccess = false; // Reset Scan Success
                }
            }
        }, SCAN_PERIOD);

        ScanWeatherButton.setClickable(false);
        ScanFanButton.setClickable(false);
        LeScanner.startScan(scanCallback);

        if (IsWeatherSelected) {
            Log.i(TAG,"APPNAME SET");
            appname = "IPVSWeather";
            ScanWeatherButton.setText("Scanning...");
        } else {
            appname = "IPVS-LIGHT"; //TODO//THIS IS DOUBLE
            ScanFanButton.setText("Scanning...");
        }
    }

///=================================///
    // ON CREATE STUFF //
///=================================///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScanWeatherButton = findViewById(R.id.ScanTemperature);
        ScanFanButton = findViewById(R.id.ScanLight);
        ConnectButtton = findViewById(R.id.Connect);
        DispText = findViewById(R.id.Disp);

        ScanWeatherButton.setOnClickListener(this);
        ScanFanButton.setOnClickListener(this);
        ConnectButtton.setOnClickListener(this);

        ConnectButtton.setVisibility(View.INVISIBLE);

        mHandler = new Handler();

        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //-------------------------------------//
        // Device scan callback.
        //-------------------------------------//
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                Log.i(TAG, "Called");

                if (result.getScanRecord() != null) {

                    Log.i(TAG, "inside If");

                    if (result.getScanRecord().getDeviceName() != null) {

                        if (result.getScanRecord().getDeviceName().equals(appname)) {

                            // Stop the Scan becasue the wanted device is found
                            //LeScanner.stopScan(scanCallback);

                            LeScanner.stopScan(scanCallback);
                            ScanWeatherButton.setText("SCAN TEMP");
                            ScanFanButton.setText("SCAN FAN");
                            ScanFanButton.setClickable(true);
                            ScanWeatherButton.setClickable(true);
                            IsScanSuccess = true;

                            //Store the found device in a local Blt Device
                            MyDevice = result.getDevice();

                            // Make connect button visible
                            ConnectButtton.setVisibility(View.VISIBLE);
                            DispText.setText("Connected!!\n Please click Continue to proceed");

                        } else {
                            DispText.setText("Could not find device");
                        }
                    }
                }
            }
            };
        }

///=================================///
    // ON CLICK STUFF //
///=================================///
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ScanTemperature:

                appname = "IPVSWeather";
                StartScanning(true);

                break;
            case R.id.ScanLight:

                appname = "IPVS-LIGHT";  //todo with mac adress > F8:20:74:F7:2B:82
                StartScanning(false);

                break;
            case R.id.Connect:
                break;
        }
    }
///=================================///
    // THE END //
///=================================///
}
