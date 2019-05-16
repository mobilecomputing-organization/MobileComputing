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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1234; // just to pass as an argument
    private BluetoothLeScanner LeScanner;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private UUID myuuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    private BluetoothDevice bledevice;
    private ScanCallback scanCallback;
    private ScanFilter scanfilter;
    private ScanSettings scanSettings;
    private Handler mHandler;
    private int SCAN_PERIOD = 10000;// 10 seconds
    private  boolean enable = true;
    private Context activityContext;
    private static final String TAG = "MyApp";
    private BluetoothDevice mydevice;
    //private String appname = "IPVS-LIGHT";
    private String appname =null;
    public View myv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //GUI variables
        final Button ScanTemp = (Button) findViewById(R.id.ScanTemperature);
        final Button ScanLight = (Button) findViewById(R.id.ScanLight);
        final Button ConnectBtn = (Button) findViewById(R.id.Connect);
        final TextView ScanRes = (TextView) findViewById(R.id.ScanRes);
        ConnectBtn.setVisibility(View.INVISIBLE);

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
                ScanRes.setText(ScanRes.getText()+ "new ScanRes =  ");
                Log.i(TAG,"Called");
                //ScanRes.setText(result.getDevice().getName());
                if (result.getScanRecord()!= null)
                {
                    Log.i(TAG,"inside If");
                  //  Log.i(TAG,result.getDevice().getName());
                    ScanRes.setText(ScanRes.getText()+ "(ScanRec not null +  ");
                        if(result.getScanRecord().getDeviceName()!= null){
                            ScanRes.setText(ScanRes.getText()+ " Device name not null +  "
                                    + result.getScanRecord().getDeviceName());
                            if (result.getScanRecord().getDeviceName().equals(appname))
                            {
                                LeScanner.stopScan(scanCallback);
                                ScanRes.setText(ScanRes.getText()+ "name matched +  ");
                                ScanTemp.setText("SCAN APP1");
                                ScanLight.setText("SCAN APP2");
                                mydevice = result.getDevice();
                                ScanRes.setText(ScanRes.getText()+"Dev Add is " + mydevice.getAddress());
                                ConnectBtn.setVisibility(View.VISIBLE);
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
                }


            }
        };

        mHandler = new Handler();

        // Button used to scan for the devices
        ScanTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectBtn.setVisibility(View.INVISIBLE);
                myv = v;
                appname = "IPVSWeather";

                ScanRes.setText("");
                enable = true;
                LeScanner = bluetoothAdapter.getBluetoothLeScanner();
                if (enable)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //mScanning = false;

                            ScanTemp.setText("SCAN APP1");
                            ScanLight.setText("SCAN APP2");
                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);

                    //mScanning = true;
                    ScanTemp.setText("Scanning...");
                    LeScanner.startScan(scanCallback);
                } else
                {
                    //mScanning = false;
                    LeScanner.stopScan(scanCallback);
                }
                //LeScanner.startScan(scanfilter,scanSettings,scanCallback);
            }
        });

        ScanLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectBtn.setVisibility(View.INVISIBLE);
                myv = v;
                appname = "IPVS-LIGHT";

                ScanRes.setText("");
                enable = true;
                LeScanner = bluetoothAdapter.getBluetoothLeScanner();
                if (enable)
                {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //mScanning = false;
                            ScanTemp.setText("SCAN App1");
                            ScanLight.setText("SCAN App2");
                            LeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_PERIOD);

                    //mScanning = true;
                    ScanLight.setText("Scanning...");
                    LeScanner.startScan(scanCallback);
                } else
                {
                    //mScanning = false;
                    LeScanner.stopScan(scanCallback);
                }
                //LeScanner.startScan(scanfilter,scanSettings,scanCallback);
            }
        });
        ConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appname == "IPVS-LIGHT")
                {
                    Intent intent = new Intent(v.getContext(),BtleServiceActivity.class);
                    intent.putExtra("Device",mydevice);
                    startActivity(intent);
                }
                else if (appname == "IPVSWeather")
                {
                    Intent intent = new Intent(v.getContext(),BtleServiceActivityfan.class);
                    intent.putExtra("Device",mydevice);
                    startActivity(intent);
                }
            }
        });



    }
}
