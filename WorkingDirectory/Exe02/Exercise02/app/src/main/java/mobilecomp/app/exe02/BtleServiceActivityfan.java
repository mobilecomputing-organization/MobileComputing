package mobilecomp.app.exe02;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class BtleServiceActivityfan extends AppCompatActivity {

    private Intent MyIntent;
    private Bundle MyBundle;
    private BluetoothDevice BLEFanDevice;
    private BluetoothGatt FanGATT;
    private BluetoothGattCallback FanGattCallback;
    private BluetoothGattCharacteristic FanGattCharacterstics;
    private Button FanButton;
    private TextView TestData;
    private UUID FanUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle_service_activityfan);

        FanButton = findViewById(R.id.FanButton);
        TestData = findViewById(R.id.FanText);

        MyIntent = getIntent();
        MyBundle = MyIntent.getExtras();
        BLEFanDevice = (BluetoothDevice) MyBundle.get("Device");

        FanGATT = BLEFanDevice.connectGatt(this,true,FanGattCallback);

        FanUUID = new UUID(0xFDFDFDFD, 0xFDFDFDFD);
        FanGattCharacterstics = new BluetoothGattCharacteristic(FanUUID, 1, 1);

        FanGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                TestData.setText(" callback called  ");
            }

     //       @Override
   //         public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

 //           }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            //    processData(characteristic.getValue());
            }
        };

        FanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FanGattCharacterstics.setValue(65535, 0, 0);
                FanGATT.writeCharacteristic(FanGattCharacterstics);
            }
        });
    }
}
