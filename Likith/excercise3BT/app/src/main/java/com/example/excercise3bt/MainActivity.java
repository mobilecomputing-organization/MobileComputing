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
    private final int SCAN_PERIOD = 30000;// 500 seconds //TODO CHANGE

    private String appname = "F6:B6:2A:79:7B:5D";
    private TextView beaconText;
    private TextView UIDText;
    private TextView URLText;
    private TextView TLMText;
    private TextView DistText;

    private Button StartButton;

//    private int RcvdRssi;
    private ScanRecord scanRecord;
    private byte[] RecvdMsg;
    private int Rssi;
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
        DistText = findViewById(R.id.Dist);

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
                StartButton.setText("Connect");
            }
        }, SCAN_PERIOD);

        LeScanner.startScan(scanCallback);
        StartButton.setText("Scanning...");
    }

    public void parseMsg(byte[] BuffData)
    {
        String print_Str = "";
        byte[] UsefulData = {0};
        String Data_str = "";
        double Dist;
        if (BuffData[5] == -86 && BuffData[6] == -2) // Packet is of EddyStone
        {
            switch(BuffData[11]) {
                case UID_PACKET:
                    UsefulData = Arrays.copyOfRange(BuffData, 13, 23);
                    Data_str = bytesToHex(UsefulData);
                    UIDText.setText(Data_str);
                    Dist = CalcDist(Rssi,BuffData[12]);
                    DistText.setText(Double.toString(Dist));
                    break;
                case URL_PACKET:
                    switch (BuffData[13]) {
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
                    UsefulData = Arrays.copyOfRange(BuffData, 14, BuffData.length);
                    try {
                        Data_str = new String(UsefulData, "UTF-8");
                    } catch (Exception UnsupportedEncodingException)
                    {
                        // Do Nothing
                    }

                    print_Str = print_Str + Data_str;
                    Dist = CalcDist(Rssi,BuffData[12]);
                    DistText.setText(Double.toString(Dist));
                    URLText.setText(print_Str);
                    break;
                case TLM_PACKET:
                    byte[] Voltage = Arrays.copyOfRange(BuffData, 13, 15);
                    byte[] Temprature = Arrays.copyOfRange(BuffData, 15, 17);
                    int TestVolt = ((Voltage[0] & 0xFFFF) << 8) | (Voltage[1] & 0xFFFF);

                    Data_str = ((double)TestVolt/1000) + "mV\n" + Temprature[0] + "." + Temprature[1] + " C";
                    TLMText.setText(Data_str);
                    break;
            }
        }
    }

    private String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public double CalcDist(int Rssi,byte Ref_TxPower)
    {
        double Dist;
     //   Log.i(TAG,Byte.toString(Ref_TxPower) + "\nRssi:" + Rssi );
        double Temp = ((double)(Ref_TxPower - 60) - (double)Rssi)/((double)20);
        Log.i(TAG,Double.toString(Temp));
        Dist = Math.pow(10,Temp);
        return Dist;
    }

    //-------------------------------------//
    // Device scan callback.
    //-------------------------------------//
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getScanRecord() != null) {

                if (result.getDevice().getAddress() != null) {
                    if (result.getDevice().getAddress().equals(appname)) {
                        //check or mask is needed to check for the kind of advertisement
                        // UID, URL, TLM

                        /*
                        data to be displayed
                        ◦ Beacon ID
                        ◦ URL
                        ◦ Voltage
                        ◦ Temperature
                        ◦ Distance to beacon in meter (optimize estimation/calibration)*/
                        Rssi = result.getRssi();
                        scanRecord = result.getScanRecord();//advertisement data
                        RecvdMsg = scanRecord.getBytes();
                        parseMsg(RecvdMsg);
                    }
                }
            }
        }
    };
}