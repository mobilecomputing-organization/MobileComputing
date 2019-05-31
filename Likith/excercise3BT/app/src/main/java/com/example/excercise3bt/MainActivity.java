package com.example.excercise3bt;

import android.bluetooth.BluetoothAdapter;
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
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private Handler handler;
    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 10000;// 10 seconds
    private String appname;
    private TextView beaconText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconText = findViewById(R.id.beaconText);
        handler = new Handler();

        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /* Ensures Bluetooth is available on the device and it is enabled. If not,
           displays a dialog requesting user permission to enable Bluetooth.*/
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        startScanning();
    }

    private void startScanning() {
        LeScanner = bluetoothAdapter.getBluetoothLeScanner();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LeScanner.stopScan(scanCallback);
            }
        }, SCAN_PERIOD);

        LeScanner.startScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        //startScan(List<ScanFilter> filters, ScanSettings settings, PendingIntent callbackIntent) if needed
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            Log.i(TAG, "scanCallback ...");

            if (result.getScanRecord() != null) {
                Log.i(TAG, result.getScanRecord().toString());
                //simulator result
                // ScanRecord [mAdvertiseFlags=26, mServiceUuids=null, mManufacturerSpecificData={76=[16, 5, 3, 24, -59, -88, -81]}, mServiceData={}, mTxPowerLevel=-2147483648, mDeviceName=null]
                if (result.getScanRecord().getDeviceName() != null) {
                    Log.i(TAG, result.getScanRecord().getDeviceName());
                    if (result.getScanRecord().getDeviceName().equals(appname)) {
                        LeScanner.stopScan(scanCallback);

                        //check or mask is needed to check for the kind of advertisement
                        // UID, URL, TLM


                        /*
                        data to be displayed
                        ◦ Beacon ID
                        ◦ URL
                        ◦ Voltage
                        ◦ Temperature
                        ◦ Distance to beacon in meter (optimize estimation/calibration)*/
                        result.getRssi();//Returns the received signal strength in dBm.
                        result.getScanRecord();//advertisement data
                        result.getTxPower();//Returns the transmit power in dBm.

                        beaconText.setText("result");
                    }
                }
            }
        }
    };
}