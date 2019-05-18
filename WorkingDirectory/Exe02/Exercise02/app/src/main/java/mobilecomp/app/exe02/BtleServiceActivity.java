package mobilecomp.app.exe02;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.Format;
import java.util.List;
import java.util.UUID;

public class BtleServiceActivity extends AppCompatActivity {

    private ScrollView resultscroll;
    private BluetoothDevice myappdevice;
    private BluetoothGattCallback GattCallback;
    private BluetoothGattCharacteristic charatersitic;
    private UUID myuuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    final long MSB = 0x0000000000001000L;
    final long LSB = 0x800000805f9b34fbL;
    long value = 0x2A1C & 0xFFFFFFFF;
    //UUID Temperature_Measurement = new UUID(MSB | (value << 32), LSB);
    UUID Temperature_Measurement=  UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")  ;
    private Context activityContext;
    private BluetoothGatt mygatt = null;
    private static final String TAG = "BtleServiceActivity";
    private byte aByte[];
    private TextView Temperaturedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle_service);

        Temperaturedata = (TextView) findViewById(R.id.Tempdata);
        final Button ReadBtn = (Button) findViewById(R.id.Read);
        Intent intent=getIntent();
        Bundle bund = intent.getExtras();
        myappdevice = (BluetoothDevice) bund.get("Device");

        GattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.i(TAG, " onConnectionStateChange: " + newState );
                if (newState == BluetoothProfile.STATE_CONNECTED){
                    Log.i(TAG, " onConnectionStateChange: " + status );
                    //Temperaturedata.setText(Temperaturedata.getText()+ "state_connected wait for service discovery \n");
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status){

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //Temperaturedata.setText(Temperaturedata.getText()+ "service discovered  "+ status +"\n");
                    Log.i(TAG, "onServicesDiscovered received1: " + status );
                    if (gatt.getService(myuuid) != null)
                    {
                        Log.i(TAG, "onServicesDiscovered no null before: " + gatt.getService(myuuid) );

                        BluetoothGattCharacteristic characteristic =  gatt.getService(myuuid).getCharacteristic(Temperature_Measurement);
                        Log.i(TAG, "onServicesDiscovered no null after: " + characteristic.getUuid() );
                        //Temperaturedata.setText(Temperaturedata.getText() + characteristic.getUuid().toString() + " \n " );
                        gatt.setCharacteristicNotification(characteristic, true);
                        gatt.readCharacteristic(characteristic);

                    }
                } else {
                    Log.i(TAG, "onServicesDiscovered received2: " + status );
                }



                //BluetoothGattService sercharatersitic =  gatt.getService(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));


                //Temperaturedata.setText(Temperaturedata.getText()+ "character   " + characteristic.getService().getUuid().toString());

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                //Temperaturedata.setText(Temperaturedata.getText()+ "\n readchar   " + characteristic.getValue());
                Log.i(TAG, "onCharacteristicRead outside IF : " + status + characteristic.getUuid());
                if(characteristic.getUuid().equals(Temperature_Measurement)){
                    Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getUuid());
                    Log.i(TAG, "onCharacteristicRead true : "  + characteristic.getValue());
                    aByte = characteristic.getValue();
                    Log.i(TAG, "onCharacteristicRead true : "  + aByte[0]);
                    Log.i(TAG, "onCharacteristicRead true : "  + (float)aByte[1]);
                    Log.i(TAG, "onCharacteristicRead true : "  + aByte[2]);
                    Log.i(TAG, "onCharacteristicRead true : "  + aByte[3]);
                    Log.i(TAG, "onCharacteristicRead true : "  + aByte[4]);

                    gatt.disconnect();
                    //gatt.close();
                }
                else
                    Log.i(TAG, "onCharacteristicRead flase : " + status + characteristic.getUuid());


            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                //Temperaturedata.setText(Temperaturedata.getText()+ "get value   " + characteristic.getValue());
            }
        };



        ReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Temperaturedata.setText("Connecting ..\n" );
                Temperaturedata.setText(Temperaturedata.getText()+ myappdevice.getName() + " \n "  );
                mygatt= myappdevice.connectGatt(v.getContext(),false,GattCallback);
            }
        });


    }
}
