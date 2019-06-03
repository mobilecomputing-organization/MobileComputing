package com.example.excercise3bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
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

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private Handler handler;

    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 20000;// 10 seconds

    private String appname = ""; //TODO FILL THE APP NAME //
    private TextView beaconText;
    private TextView UIDText;
    private TextView URLText;
    private TextView TLMText;

    private Button StartButton;

//    private int RcvdRssi;
    private ScanRecord scanRecord;
    private byte[] RecvdMsg;
    private static final byte UID_PACKET = 0;
    private static final byte URL_PACKET = 16;
    private static final byte TLM_PACKET = 32;
    private static final byte HTTP_WWW_FRAME = 0;
    private static final byte HTTPS_WWW_FRAME = 1;
    private static final byte HTTP_OHNE_WWW_FRAME = 2;
    private static final byte HTTPS_0HNE_WWW_FRAME = 3;

    //    private int RcvdTxPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartButton = findViewById(R.id.StartButton);
        beaconText = findViewById(R.id.beaconText);
        UIDText = findViewById(R.id.UID);
        URLText = findViewById(R.id.URL);
        TLMText = findViewById(R.id.TLM);
        handler = new Handler();

        beaconText.setText("UID:\n\nURL:\n\nVoltage:\nTemp\n");
        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        /* Ensures Bluetooth is available on the device and it is enabled. If not,
           displays a dialog requesting user permission to enable Bluetooth.*/
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        StartButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startScanning();
            }
        }
        );
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

    public void parseMsg(byte[] BuffData)
    {
        String print_Str = "";
        byte[] UsefulData = {0};
        String Data_str = "";
        // TODO Change 0x56 to FF AND AE
        Log.i(TAG, BuffData[2] + "AND" + BuffData[3]);
        if (BuffData[2] == -86 && BuffData[3] == -2) // Packet is of EddyStone
        {
            switch(BuffData[8]) {
                case UID_PACKET:
                    Log.i(TAG, "A WASP");
                    //TODO Display as HEX instead of INTS
                    UsefulData = Arrays.copyOfRange(BuffData, 10, 20);
                    Data_str = UsefulData.toString();
                    UIDText.setText(Data_str);
                    break;
                case URL_PACKET:
                    switch (BuffData[10]) {
                        case HTTP_WWW_FRAME:
                            print_Str = "http://www.";
                            break;
                        case HTTPS_WWW_FRAME:
                            print_Str = "https://www.";
                            break;
                        case HTTP_OHNE_WWW_FRAME:
                            print_Str = "http://";
                            break;
                        case HTTPS_0HNE_WWW_FRAME:
                            print_Str = "https://";
                            break;
                    }
                    UsefulData = Arrays.copyOfRange(BuffData, 11, BuffData.length);
                    try {
                        Data_str = new String(UsefulData, "UTF-8");
                    } catch (Exception UnsupportedEncodingException)
                    {
                        // Do Nothing
                    }

                    print_Str = print_Str + Data_str;
                    URLText.setText(print_Str);
                    break;
                case TLM_PACKET:
                    // TODO Parse This Packet
                    Log.i(TAG, "A Spider");
                    printScanRecord (BuffData);
                    byte[] Voltage = Arrays.copyOfRange(BuffData, 10, 11);
                    byte[] Temprature = Arrays.copyOfRange(BuffData, 12, 13);
                    Data_str = ByteArrayToString(Voltage) + "\n" + ByteArrayToString(Temprature);
                    TLMText.setText(Data_str);
                    break;
            }

        }
    }

    public static String ByteArrayToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }

    public void printScanRecord (byte[] scanRecord) {
        // Simply print all raw bytes
    //    String decodedRecord = new String(scanRecord,"UTF-8");
        Log.i(TAG,"decoded String : " + ByteArrayToString(scanRecord));
    }

    //-------------------------------------//
    // Device scan callback.
    //-------------------------------------//
    private ScanCallback scanCallback = new ScanCallback() {
        //startScan(List<ScanFilter> filters, ScanSettings settings, PendingIntent callbackIntent) if needed
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            Log.i(TAG, "scanCallback ...");

            if (result.getScanRecord() != null) {
                //Log.i(TAG, result.getScanRecord().toString());
                Log.i(TAG, "FLY on the wall..");
                //simulator result
                // ScanRecord [mAdvertiseFlags=26, mServiceUuids=null, mManufacturerSpecificData={76=[16, 5, 3, 24, -59, -88, -81]}, mServiceData={}, mTxPowerLevel=-2147483648, mDeviceName=null]
                if (result.getDevice().getAddress() != null) {
                    Log.i(TAG, "Woh an INSECT!");
                    Log.i(TAG, result.getDevice().getAddress());
                    //    Log.i(TAG, result.getScanRecord().getDeviceName());
                //    if (result.getScanRecord().getDeviceName().equals(appname)) {
                    // TODO ADD IF-STATEMENT OF MAC ADDRESS

                        //check or mask is needed to check for the kind of advertisement
                        // UID, URL, TLM

                        /*
                        data to be displayed
                        ◦ Beacon ID
                        ◦ URL
                        ◦ Voltage
                        ◦ Temperature
                        ◦ Distance to beacon in meter (optimize estimation/calibration)*/
                    //    RcvdRssi = result.getRssi();//Returns the received signal strength in dBm.
                        scanRecord = result.getScanRecord();//advertisement data
                        RecvdMsg = scanRecord.getBytes();
                        parseMsg(RecvdMsg);
                    //    printScanRecord(RecvdMsg);
                    //    RcvdTxPower = result.getTxPower();//Returns the transmit power in dBm.

                    //    beaconText.setText("RSSI: " + RcvdRssi + "\nTx Power: " + RcvdTxPower);
                //    }
                }
            }
        }
    };
}