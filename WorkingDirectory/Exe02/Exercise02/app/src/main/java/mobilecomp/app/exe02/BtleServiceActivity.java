package mobilecomp.app.exe02;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class BtleServiceActivity extends AppCompatActivity {

    private ScrollView resultscroll;
    private BluetoothDevice myappdevice;
    private BluetoothGattCallback GattCallback;
    //private UUID myuuid = UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD");
    private UUID myuuid = UUID.fromString("00000001-0000-0000-FDFD-FDFDFDFDFDFD");
    final long MSB = 0x0000000000001000L;
    final long LSB = 0x800000805f9b34fbL;
    long value = 0x2A1C & 0xFFFFFFFF;
    UUID Temperature_Measurement = new UUID(MSB | (value << 32), LSB);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle_service);

        final TextView Temperaturedata = (TextView) findViewById(R.id.Tempdata);
        final Button ReadBtn = (Button) findViewById(R.id.Read);
        Intent intent=getIntent();
        Bundle bund = intent.getExtras();
        myappdevice = (BluetoothDevice) bund.get("Device");



        ReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Temperaturedata.setText(myappdevice.getName() + "  " +myappdevice.getAddress() );
                myappdevice.connectGatt(v.getContext(),true,GattCallback);

                /*ParcelUuid uuids[] = myappdevice.getUuids();
                        if(uuids!= null){

                            for (ParcelUuid uuid : uuids)
                            {
                                UUID resuuid = uuid.getUuid();
                                Temperaturedata.setText(Temperaturedata.getText()+ resuuid.toString() + " ");
                            }
                        }
                        else
                        {
                            Temperaturedata.setText(Temperaturedata.getText()+ " no uuid");
                        }*/


            }
        });

        GattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED){
                    Temperaturedata.setText(Temperaturedata.getText()+ "state_connected");
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status){
                Temperaturedata.setText(Temperaturedata.getText()+ "service discovered");

                BluetoothGattCharacteristic characteristic =  gatt.getService(myuuid).getCharacteristic(Temperature_Measurement);
               gatt.setCharacteristicNotification(characteristic, true);

                Temperaturedata.setText(Temperaturedata.getText()+ "character   " + characteristic.getService().getUuid().toString());
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Temperaturedata.setText(Temperaturedata.getText()+ "readchar   " + characteristic.getValue());
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                Temperaturedata.setText(Temperaturedata.getText()+ "get value   " + characteristic.getValue());
            }
        };
    }
}
