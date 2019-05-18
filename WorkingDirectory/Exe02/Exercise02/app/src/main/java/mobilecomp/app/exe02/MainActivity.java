package mobilecomp.app.exe02;

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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{// todo implements View.onClicklistenere

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner LeScanner;
    private BluetoothDevice MyDevice;
    private ScanCallback scanCallback;
    private Handler mHandler;
    private MainActivity ma;

    //private UUID myuuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    //private BluetoothDevice bledevice;
    //private ScanFilter scanfilter;
    //private ScanSettings scanSettings;
    //private Context activityContext;

    private static final String TAG = "MyApp";
    private final int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private final int SCAN_PERIOD = 10000;// 10 seconds
//    private final boolean enable = true;

    private String appname = null;
    public View myv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GUI variables
        // TODO// Check whether to declare variables up
        final Button ScanTemp = (Button) findViewById(R.id.ScanTemperature);
        final Button ScanLight = (Button) findViewById(R.id.ScanLight);
        final Button ConnectBtn = (Button) findViewById(R.id.Connect);
        final TextView ScanRes = (TextView) findViewById(R.id.ScanRes);

        //TODO// Change the name
        ConnectBtn.setVisibility(View.INVISIBLE);

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

        // Button used to scan for the devices
        ScanTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        LeScanner.stopScan(scanCallback);
                    }
                }, SCAN_PERIOD);
                ScanTemp.setText("Scanning...");
                LeScanner.startScan(scanCallback);
            }
        });

        ScanLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);

                    ScanLight.setText("Scanning...");
                    LeScanner.startScan(scanCallback);
            }
        });

        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (appname == "IPVS-LIGHT")// todo if mac adrees then take care
                {
                    intent = new Intent(v.getContext(),BtleServiceActivityfan.class);
                }
                else // (appname == "IPVSWeather") //TODO// THIS MIGHT CAUSE ISSUES
                {
                    intent = new Intent(v.getContext(),BtleServiceActivity.class);
                }
                intent.putExtra("Device",MyDevice);
                startActivity(intent);

            }
        });
    }
}
//mydevice = result.getDevice();
                        /*List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
                        if(uuids!= null){
                            ScanRes.setText(ScanRes.getText()+ "uuids not null :  ");
                            for (ParcelUuid uuid : uuids)
                            {
                                UUID resuuid = uuid.getUuid();
                                ScanRes.setText(ScanRes.getText()+ resuuid.toString() + ") ");
                            }
                        }*/

                        /*if(result.getScanRecord().getServiceUuids() != null)
                        ScanRes.setText(ScanRes.getText()+ "UUID  "+result.getScanRecord().getServiceUuids());
                        else
                            ScanRes.setText(ScanRes.getText()+ result.getDevice().toString());*/
//mydevice = result.getDevice();
//enable = false;