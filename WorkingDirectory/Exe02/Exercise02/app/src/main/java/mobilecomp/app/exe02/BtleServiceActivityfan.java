package mobilecomp.app.exe02;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BtleServiceActivityfan extends AppCompatActivity {

    private BluetoothDevice myappdevice;
    private BluetoothGattCallback GattCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btle_service_activityfan);

        
    }
}
