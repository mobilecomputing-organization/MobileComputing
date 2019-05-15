package mobilecomp.app.exe02;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private BluetoothLeScanner LeScanner;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private UUID uuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    private BluetoothDevice bledevice;
    private ScanCallback scanCallback;
    private ScanFilter scanfilter;
    private ScanSettings scanSettings;
    private Handler mHandler;
    private int SCAN_PERIOD = 10000;
    private  boolean enable = true;
    private Context activityContext;
    private static final String TAG = "MyApp";
    private BluetoothDevice mydevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GUI variables
        Button ScanButton = (Button) findViewById(R.id.ScanButton);
        final TextView ScanRes = (TextView) findViewById(R.id.ScanRes);

        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //LeScanner = bluetoothAdapter.getBluetoothLeScanner();
        //final ScanCallback callback;

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Device scan callback.
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {



                //ScanRes.setText("calback called ");
                Log.i(TAG,"Called");


                //Toast toast = Toast.makeText(activityContext,"Callback called", Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                //toast.show();

                ScanRes.setText(result.getDevice().getName());
                if (result.getDevice().getName() != null)
                {
                    Log.i(TAG,"inside If");
                    Log.i(TAG,result.getDevice().getName());
                    if (result.getDevice().getUuids()!= null){
                        if(result.getDevice().getUuids().equals(uuid))
                        mydevice = result.getDevice();

                        Log.i(TAG,result.getDevice().getName());
                        ScanRes.setText("connected");
                    }



                    //mydevice = result.getDevice();
                    //enable = false;

                }


            }
        };

        mHandler = new Handler();

        // Button used to scan for the devices
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanRes.setText("Hi !!");
                enable = true;
                LeScanner = bluetoothAdapter.getBluetoothLeScanner();
                if (enable)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //mScanning = false;

                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);

                    //mScanning = true;
                    LeScanner.startScan(scanCallback);
                } else
                {
                    //mScanning = false;
                    LeScanner.stopScan(scanCallback);
                }
                //LeScanner.startScan(scanfilter,scanSettings,scanCallback);
            }
        });






    }
}
