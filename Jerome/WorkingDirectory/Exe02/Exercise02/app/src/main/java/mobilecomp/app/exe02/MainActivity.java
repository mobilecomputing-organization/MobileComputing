package mobilecomp.app.exe02;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{// todo implements View.onClicklistenere

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private BluetoothDevice MyDevice;
    private ScanCallback scanCallback;
    private Handler mHandler;
    private static final String TAG = "MyApp";
    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 10000;// 10 seconds
    private String appname = null;


// mainly for GATTcallback

    private BluetoothDevice myappdevice;
    private BluetoothGattCallback GattCallback;
    private BluetoothGattCharacteristic charatersitic;
    private UUID myuuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    private UUID Temperature_Measurement=  UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")  ;
    private UUID Humidity_Measurement = UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")  ;
    private UUID fanServiceuuid = UUID.fromString("00000001-0000-0000-FDFD-FDFDFDFDFDFD");
    private UUID Fan_Intensity=  UUID.fromString("10000001-0000-0000-FDFD-FDFDFDFDFDFD")  ;
    private BluetoothGatt mygatt = null;
    private byte aByte[];
    private Boolean isNotify = false;

    //final long MSB = 0x0000000000001000L;
    // final long LSB = 0x800000805f9b34fbL;
    //long value = 0x2A1C & 0xFFFFFFFF;
    //UUID Temperature_Measurement = new UUID(MSB | (value << 32), LSB);


    private enum currentstate{
        SCANNING,
        TEMPERATURE_READ,
        FAN_Write
    }
    private currentstate mystate;

    private Button ScanTemp;
    private Button ScanLight;
    private Button ConnectBtn;
    private TextView ScanRes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mystate = currentstate.SCANNING;

        //GUI variables
        // TODO// Check whether to declare variables up
        ScanTemp = findViewById(R.id.ScanTemperature);
        ScanLight = findViewById(R.id.ScanLight);
        ConnectBtn = findViewById(R.id.Connect);
        ScanRes = findViewById(R.id.ScanRes);

        ScanTemp.setOnClickListener(this);
        ScanLight.setOnClickListener(this);
        ConnectBtn.setOnClickListener(this);

        //TODO// Change the name
        ConnectBtn.setVisibility(View.INVISIBLE);

        mHandler = new Handler();

        // ///////
        // Initializes Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // //////////

        GattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.i(TAG, " onConnectionStateChange: " + newState );
                if (newState == BluetoothProfile.STATE_CONNECTED){
                    Log.i(TAG, " onConnectionStateChange: " + status );
                    //ScanRes.setText(ScanRes.getText()+ "state_connected wait for service discovery \n");
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status){

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //ScanRes.setText(ScanRes.getText()+ "service discovered  "+ status +"\n");
                    Log.i(TAG, "onServicesDiscovered received1: " + status );

                    if (gatt.getService(myuuid) != null )
                    {
                        Log.i(TAG, "onServicesDiscovered no null before: " + gatt.getService(myuuid) );


                        BluetoothGattCharacteristic characteristic1 =  gatt.getService(myuuid).getCharacteristic(Temperature_Measurement);
                        //BluetoothGattCharacteristic characteristic2 =  gatt.getService(myuuid).getCharacteristic(Humidity_Measurement);


                        Log.i(TAG, "onServicesDiscovered no null after: " + characteristic1.getUuid() );
                        //Log.i(TAG, "onServicesDiscovered no null after: " + characteristic2.getUuid() );
                        //ScanRes.setText(ScanRes.getText() + characteristic.getUuid().toString() + " \n " );
                        if(isNotify == true)
                        {
                            gatt.setCharacteristicNotification(characteristic1, true);
                            //gatt.setCharacteristicNotification(characteristic2, true);
                        }

                            gatt.readCharacteristic(characteristic1);
                            //gatt.readCharacteristic(characteristic2);

                    }
                    else if (gatt.getService(fanServiceuuid) != null){

                        Log.i(TAG, "onServicesDiscovered no null before: " + gatt.getService(fanServiceuuid) );


                        BluetoothGattCharacteristic characteristic =  gatt.getService(fanServiceuuid).getCharacteristic(Fan_Intensity);

                        Log.i(TAG, "onServicesDiscovered no null after: " + characteristic.getUuid() );

                        //ScanRes.setText(ScanRes.getText() + characteristic.getUuid().toString() + " \n " );
                        characteristic.setValue(65000,BluetoothGattCharacteristic.FORMAT_UINT16,0);

                        gatt.writeCharacteristic(characteristic);
                    }
                }
                else {
                    Log.i(TAG, "onServicesDiscovered received2: " + status );
                }
                //BluetoothGattService sercharatersitic =  gatt.getService(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
                //Temperaturedata.setText(Temperaturedata.getText()+ "character   " + characteristic.getService().getUuid().toString());
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                //ScanRes.setText(ScanRes.getText()+ "\n readchar   " + characteristic.getValue());
                Log.i(TAG, "onCharacteristicRead outside IF : " + status + characteristic.getUuid());
                if(characteristic.getUuid().equals(Temperature_Measurement)){
                   Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getUuid());
                   Log.i(TAG, "onCharacteristicRead true sint : "  + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16,1));
               }
               /*if (characteristic.getUuid().equals(Humidity_Measurement))
                {
                    Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getUuid());
                    Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getValue());
                    float percentage = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0)*(float)(0.01);
                    Log.i(TAG, "onCharacteristicRead true percentage: "  + percentage);
                }
                else
                    Log.i(TAG, "onCharacteristicRead false : " + status + characteristic.getUuid());
*/
                if(isNotify != true) {
                    gatt.disconnect();
                    ScanTemp.setClickable(true); // to disable button
                    ScanLight.setClickable(true); // to disable button
                    ConnectBtn.setClickable(true); // to disable button
                }
                else {
                    // do nothing
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                gatt.readCharacteristic(characteristic);
                //ScanRes.setText(ScanRes.getText()+ "get value   " + characteristic.getValue());
            }
        };
        // Device scan callback.
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                Log.i(TAG,"Called");
                //ScanRes.setText(result.getDevice().getName());
                if (result.getScanRecord()!= null)
                {
                    ScanRes.setText(ScanRes.getText()+ "new Scan Record found  \n");
                    Log.i(TAG,"inside If");
                    if(result.getScanRecord().getDeviceName()!= null){
                        ScanRes.setText(ScanRes.getText()+ " Device name is   "
                                + result.getScanRecord().getDeviceName()+"\n");
                        if (result.getScanRecord().getDeviceName().equals(appname))
                        {
                            // Stop the Scan becasue the wanted device is found
                            LeScanner.stopScan(scanCallback);
                            // Reset the Scan button text
                            ScanTemp.setText("SCAN TEMP");
                            ScanLight.setText("SCAN FAN");
                            ScanLight.setClickable(true);
                            ScanTemp.setClickable(true);
                            //Store the found device in a local Blt Device
                            MyDevice = result.getDevice();
                            // Update the Logging window
                            ScanRes.setText(ScanRes.getText()+ "name matched and  ");
                            ScanRes.setText(ScanRes.getText()+"Dev Add is " + MyDevice.getAddress() + "\n");
                            // Make connect button visible
                            ConnectBtn.setVisibility(View.VISIBLE);
                            ScanRes.setText(ScanRes.getText()+"Dev Add is " + "Please click Continue to proceed" + "\n");
                        }
                        else{
                            ScanRes.setText(ScanRes.getText()+ "matched Failed Continue Scan  ");
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ScanTemperature:
                if(mystate == currentstate.SCANNING){
                    // To make button invisible again if you want to scan again
                    ConnectBtn.setVisibility(View.INVISIBLE);

                    appname = "IPVSWeather";// todo with mac adress > F6:B6:2A:79:7B:5D
                    //appname = "Jerome";
                    // clear the Logging Window
                    ScanRes.setText("");

                    LeScanner = bluetoothAdapter.getBluetoothLeScanner();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ScanTemp.setText("SCAN TEMP");
                            ScanLight.setText("SCAN FAN");
                            ScanLight.setClickable(true);
                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);
                    ScanTemp.setText("Scanning...");
                    ScanLight.setClickable(false);
                    LeScanner.startScan(scanCallback);
                }
                else if(mystate == currentstate.TEMPERATURE_READ){

                    isNotify = false;

                    ScanTemp.setClickable(false); // to disable button
                    // ScanTemp.setTextColor(Color.WHITE);
                    ScanLight.setClickable(false); // to disable button
                    // ScanLight.setTextColor(Color.WHITE);
                    ConnectBtn.setClickable(false); // to disable button
                    // ConnectBtn.setTextColor(Color.WHITE);

                    mygatt= MyDevice.connectGatt(v.getContext(),false,GattCallback);
                }
                else if (mystate == currentstate.FAN_Write){
                    mygatt= MyDevice.connectGatt(v.getContext(),false,GattCallback);
                }
                break;

            case R.id.ScanLight: // also notify button
                if(mystate == currentstate.SCANNING) {
                    ConnectBtn.setVisibility(View.INVISIBLE);
                    appname = "IPVS-LIGHT";  //todo with mac adress > F8:20:74:F7:2B:82

                    ScanRes.setText("");
                    // TODO// LOOK AT LATER
                    LeScanner = bluetoothAdapter.getBluetoothLeScanner();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //mScanning = false;
                            ScanTemp.setText("SCAN TEMP");
                            ScanLight.setText("SCAN FAN");
                            ScanTemp.setClickable(true);
                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);

                    ScanLight.setText("Scanning...");
                    ScanTemp.setClickable(false);
                    LeScanner.startScan(scanCallback);
                }
                else if (mystate == currentstate.TEMPERATURE_READ)
                {
                    isNotify = true;
                    ScanTemp.setClickable(false); // Disable clicking
                    ScanLight.setClickable(false);
                    mygatt= MyDevice.connectGatt(v.getContext(),false,GattCallback);
                }
                else if (mystate == currentstate.FAN_Write)
                {
                    //todo the main code for connecting fan server
                    mygatt= MyDevice.connectGatt(v.getContext(),false,GattCallback);
                }
                break;
            case R.id.Connect:  // disconnect button

                if (mystate == currentstate.SCANNING) {

                    Intent intent;
                    if (appname == "IPVS-LIGHT")// todo if mac adrees then take care
                    {
                        //intent = new Intent(v.getContext(),BtleServiceActivityfan.class);
                        mystate = currentstate.FAN_Write;
                    } else // (appname == "IPVSWeather") //TODO// THIS MIGHT CAUSE ISSUES
                    {
                        //intent = new Intent(v.getContext(),BtleServiceActivity.class);
                        mystate = currentstate.TEMPERATURE_READ;
                    }
                    //intent.putExtra("Device",MyDevice);
                    //startActivity(intent);
                    GATTFunction();
                }
                else if (mystate == currentstate.TEMPERATURE_READ || mystate == currentstate.FAN_Write)
                {
                    if (mygatt!=null){
                        mygatt.disconnect();
                        mygatt=null;
                        mystate = currentstate.SCANNING;
                        GATTFunction();
                    }
                }
                break;
        }
    }

    private void GATTFunction(){
        switch (mystate) {
            case SCANNING:
                ScanTemp.setText("SCAN TEMP");
                ScanLight.setText("SCAN FAN");
                ConnectBtn.setText("Connect");
                ScanTemp.setClickable(true);
                ScanLight.setClickable(true);
                ConnectBtn.setVisibility(View.INVISIBLE);

                // to check later
                break;
            case TEMPERATURE_READ:

                ScanTemp.setText("Read");
                ScanLight.setText("Notify");
                ConnectBtn.setText("Disconnect");
                break;
            case FAN_Write:

                ScanTemp.setText("Write");
                //ScanLight.setText("SCAN FAN");
                ScanLight.setVisibility(View.INVISIBLE);
                ConnectBtn.setText("Disconnect");

                break;
            default:
                break;
        }
    }

/*    private void CloseConnection(BluetoothGatt gatt){




    }*/

}