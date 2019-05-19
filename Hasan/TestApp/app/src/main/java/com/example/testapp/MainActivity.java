package com.example.testapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private BluetoothDevice MyDevice;
    // private ScanCallback scanCallback;
    // private BluetoothGattCallback GattCallback;
    private BluetoothGatt mygatt = null;
    private Handler mHandler;

    private static final String TAG = "MyApp";
    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 10000;// 10 seconds
    private String appname;
    private boolean IsScanSuccess = false;
    private UUID fanServiceuuid = UUID.fromString("00000001-0000-0000-FDFD-FDFDFDFDFDFD");
    private UUID Fan_Intensity=  UUID.fromString("10000001-0000-0000-FDFD-FDFDFDFDFDFD");
    public boolean bReadTemprature = false;
    public String strHumidityVal = "N/A";
    public String strTempVal = "N/A";

    private UUID WeatherServiceUUID = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    private UUID Temperature_Measurement =  UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")  ;
    private UUID Humidity_Measurement = UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")  ;

    BluetoothGattCharacteristic Temp_characteristic;
    BluetoothGattCharacteristic Humidity_characteristic;

    private Button LeftButton;
    private Button RightButton;
    private Button DownButton;
    public TextView DispText;
    private EditText FanIntensity;
    private TextView FanIntensityText;

    private enum DisplayStates{
        INIT,
        SCANNING_WEATHER,
        SCANNING_FAN,
        DEVICE_FOUND,
        READ_ALL,
        SCANNING,
        WEATHER,
        //    TEMPERATURE_READ,
        FAN_Write
    }
    private enum ButtonStates {
        INIT,
        CONNECT,
        DISCONNECT,
        SCAN_WEATHER,
        SCAN_FAN,
        WRITE_FAN,
        READ_WEATHER,
        READ_TEMPRATURE,
        READ_HUMIDITY,
        NOTIFY_WEATHER,
    }

    private ButtonStates LeftButtonState = ButtonStates.SCAN_WEATHER;
    private ButtonStates RightButtonState = ButtonStates.SCAN_FAN;
    private ButtonStates DownButtonState = ButtonStates.CONNECT;
//    private DisplayStates CurrentState = DisplayStates.INIT;

    //=====================================//
    // SOME LOCAL FUNCTIONS
//=====================================//
    public void StartScanning(boolean IsWeatherSelected){
        DownButton.setVisibility(View.INVISIBLE);
        appname = null;
        DispText.setText("");
        LeScanner = bluetoothAdapter.getBluetoothLeScanner();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (IsScanSuccess == false) {
                    DispGUI(DisplayStates.INIT);
                    DispText.setText("Could not find device");
                    LeScanner.stopScan(scanCallback);
                } else {
                    IsScanSuccess = false; // Reset Scan Success
                }
            }
        }, SCAN_PERIOD);

        LeScanner.startScan(scanCallback);

        if (IsWeatherSelected) {
            Log.i(TAG,"APPNAME SET");
            appname = "IPVSWeather";
            DispGUI(DisplayStates.SCANNING_WEATHER);

        } else {
            appname = "IPVS-LIGHT"; //TODO//THIS IS DOUBLE
            DispGUI(DisplayStates.SCANNING_FAN);

        }
    }

    public void DispGUI(DisplayStates CurrentState) {
        switch (CurrentState) {
            case INIT:
                LeftButtonState = ButtonStates.SCAN_WEATHER;
                RightButtonState = ButtonStates.SCAN_FAN;
                DownButtonState = ButtonStates.CONNECT;

                LeftButton.setVisibility(View.VISIBLE);
                RightButton.setVisibility(View.VISIBLE);
                FanIntensity.setVisibility(View.INVISIBLE);
                FanIntensityText.setVisibility(View.INVISIBLE);

                DispText.setText("");
                DownButton.setText("Connect");
                LeftButton.setText("SCAN TEMP");
                RightButton.setText("SCAN FAN");
                RightButton.setClickable(true);
                LeftButton.setClickable(true);
                break;
            case SCANNING_FAN:
                LeftButton.setClickable(false);
                RightButton.setClickable(false);
                RightButton.setText("Scanning...");
                break;
            case SCANNING_WEATHER:
                LeftButton.setClickable(false);
                RightButton.setClickable(false);
                LeftButton.setText("Scanning...");
                break;
            case DEVICE_FOUND:
                // Reset the Buttons
                LeftButton.setText("SCAN TEMP");
                RightButton.setText("SCAN FAN");

                RightButton.setClickable(true);
                LeftButton.setClickable(true);
                // Make Connect visible
                DownButton.setVisibility(View.VISIBLE);
                DispText.setText("Connected!!\n Please click Continue to proceed");

                break;
            case FAN_Write:
                LeftButtonState = ButtonStates.WRITE_FAN;
                DownButtonState = ButtonStates.DISCONNECT;

                LeftButton.setText("Write");
                RightButton.setVisibility(View.INVISIBLE);
                FanIntensity.setVisibility(View.VISIBLE);
                FanIntensityText.setVisibility(View.VISIBLE);
                DownButton.setText("Disconnect");

                break;
            case READ_ALL:
                LeftButtonState = ButtonStates.READ_TEMPRATURE;
                RightButtonState = ButtonStates.READ_HUMIDITY;
                DownButtonState = ButtonStates.DISCONNECT;

                LeftButton.setText("T_Update");
                RightButton.setText("H_Update");
                RightButton.setVisibility(View.VISIBLE);
                LeftButton.setVisibility(View.VISIBLE);
                DownButton.setText("Disconnect");
                break;
            case WEATHER:
                LeftButtonState = ButtonStates.READ_WEATHER;
                RightButtonState = ButtonStates.NOTIFY_WEATHER;
                DownButtonState = ButtonStates.DISCONNECT;

                LeftButton.setText("Read");
                RightButton.setText("Notify");
                LeftButton.setVisibility(View.VISIBLE);
                RightButton.setVisibility(View.VISIBLE);
                DownButton.setText("Disconnect");

                break;
        }
    }

    public void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

///=================================///
    // ON CREATE STUFF //
///=================================///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LeftButton = findViewById(R.id.LeftButton);
        RightButton = findViewById(R.id.RightButton);
        DownButton = findViewById(R.id.DownButton);
        DispText = findViewById(R.id.Disp);
        FanIntensity = findViewById(R.id.FanIntensity);
        FanIntensityText = findViewById(R.id.FanIntensityText);

        LeftButton.setOnClickListener(this);
        RightButton.setOnClickListener(this);
        DownButton.setOnClickListener(this);

        DownButton.setVisibility(View.INVISIBLE);
        FanIntensity.setVisibility(View.INVISIBLE);
        FanIntensityText.setVisibility(View.INVISIBLE);

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
    }

    ///=================================///
    // ON CLICK STUFF //
///=================================///
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LeftButton:
                switch (LeftButtonState) {
                    case SCAN_WEATHER:
                        StartScanning(true);
                        break;
                    case WRITE_FAN:
                        mygatt= MyDevice.connectGatt(MainActivity.this,false,GattCallback);
                        break;
                    case READ_WEATHER:
                        //mygatt= MyDevice.connectGatt(MainActivity.this,false,GattCallback);
                        DispGUI(DisplayStates.READ_ALL);
                        break;
                    case READ_TEMPRATURE:
                        bReadTemprature = true;
                        mygatt= MyDevice.connectGatt(MainActivity.this,false,GattCallback);
                        //mygatt.readCharacteristic(Temp_characteristic);
                        break;
                }
                break;
            case R.id.RightButton:
                //TODO check if correct
                switch (RightButtonState) {
                    case SCAN_FAN:
                        StartScanning(false);
                        break;
                    case READ_HUMIDITY:
                        bReadTemprature = false;
                        mygatt= MyDevice.connectGatt(MainActivity.this,false,GattCallback);
                        //mygatt.readCharacteristic(Temp_characteristic);
                        break;
                }
                break;
            case R.id.DownButton:
                switch (DownButtonState) {
                    case CONNECT:
                        if (appname == "IPVS-LIGHT") {
                            DispGUI(DisplayStates.FAN_Write);
                        } else {
                            DispGUI(DisplayStates.WEATHER);
                        }
                        break;
                    case DISCONNECT:
                        //mygatt.disconnect();
                        DispGUI(DisplayStates.INIT);
                        break;
                }
                break;
        }
    }
///=================================///
    // THE END //
///=================================///

    //-------------------------------------//
    // Device scan callback.
    //-------------------------------------//
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            //    Log.i(TAG, "Called");

            if (result.getScanRecord() != null) {

                //    Log.i(TAG, "inside If");

                if (result.getScanRecord().getDeviceName() != null) {

                    if (result.getScanRecord().getDeviceName().equals(appname)) {

                        // Stop the Scan becasue the wanted device is found

                        LeScanner.stopScan(scanCallback);
                        DispGUI(DisplayStates.DEVICE_FOUND);
                        IsScanSuccess = true;

                        //Store the found device in a local Blt Device
                        MyDevice = result.getDevice();

                    } else {
                        //    DispText.setText("Could not find device");
                    }
                }
            }
        }
    };

    //----------------------//
    //GATT CALLBACK
    //----------------------//

    private BluetoothGattCallback GattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, " onConnectionStateChange: " + newState );
            if (newState == BluetoothProfile.STATE_CONNECTED){
                Log.i(TAG, " onConnectionStateChange: " + status );
                //DispText.setText("GATT!!");
                setText(DispText,"Server Connected \n");
                //ScanRes.setText(ScanRes.getText()+ "state_connected wait for service discovery \n");
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){

            if (status == BluetoothGatt.GATT_SUCCESS) {
                    //ScanRes.setText(ScanRes.getText()+ "service discovered  "+ status +"\n");
                Log.i(TAG, "onServicesDiscovered received1: " + status );
                setText(DispText,"");

                if (gatt.getService(WeatherServiceUUID) != null )
                {

                    if (bReadTemprature) {
                        Temp_characteristic =  gatt.getService(WeatherServiceUUID).getCharacteristic(Temperature_Measurement);
                        mygatt.readCharacteristic(Temp_characteristic);

                    } else {
                        Humidity_characteristic =  gatt.getService(WeatherServiceUUID).getCharacteristic(Humidity_Measurement);
                        mygatt.readCharacteristic(Humidity_characteristic);
                    }


                    //                        if(isNotify == true)
//                        {
//                            gatt.setCharacteristicNotification(characteristic1, true);
//                            //gatt.setCharacteristicNotification(characteristic2, true);
//                        }

                }
                else if (gatt.getService(fanServiceuuid) != null){
                    BluetoothGattCharacteristic characteristic =  gatt.getService(fanServiceuuid).getCharacteristic(Fan_Intensity);
                  //  characteristic.setValue(0,BluetoothGattCharacteristic.FORMAT_UINT16,0);
                    Log.i(TAG, "FanIntensity test : " + FanIntensity.getText());
                    Log.i(TAG, "FanIntensity test : " + Integer.parseInt(FanIntensity.getText().toString()));

                    characteristic.setValue(Integer.parseInt(FanIntensity.getText().toString()),BluetoothGattCharacteristic.FORMAT_UINT16,0);

                    gatt.writeCharacteristic(characteristic);
                }


            }
//                else {
//                    Log.i(TAG, "onServicesDiscovered received2: " + status );
//                }
//                //BluetoothGattService sercharatersitic =  gatt.getService(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
//                //Temperaturedata.setText(Temperaturedata.getText()+ "character   " + characteristic.getService().getUuid().toString());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                super.onCharacteristicRead(gatt, characteristic, status);
//                //ScanRes.setText(ScanRes.getText()+ "\n readchar   " + characteristic.getValue());
//                Log.i(TAG, "onCharacteristicRead outside IF : " + status + characteristic.getUuid());
//                if(characteristic.getUuid().equals(Temperature_Measurement)){
//                    Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getUuid());
//                    Log.i(TAG, "onCharacteristicRead true sint : "  + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16,1));
//                }
//
//                if(isNotify != true) {
//                    gatt.disconnect();
//                    ScanTemp.setClickable(true); // to disable button
//                    ScanLight.setClickable(true); // to disable button
//                    ConnectBtn.setClickable(true); // to disable button
//                }
//                else {
//                    // do nothing
//                }

            if (bReadTemprature)
            {
                int temperature_data = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16,1);
                strTempVal = String.valueOf ((float)(temperature_data/100));
                setText(DispText, strTempVal + "°C "+strHumidityVal + "% \n");
            }else
            {
                float percentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0)*(float)(0.01);
                strHumidityVal = Float.toString(percentage);;
                setText(DispText, strTempVal + "°C "+strHumidityVal + "% \n");
            }




            mygatt.disconnect();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //    super.onCharacteristicWrite(gatt, characteristic, status);

            mygatt.disconnect();
        }

        //    @Override
        //    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        //  gatt.readCharacteristic(characteristic);
        //ScanRes.setText(ScanRes.getText()+ "get value   " + characteristic.getValue());
        //}

    };



}
