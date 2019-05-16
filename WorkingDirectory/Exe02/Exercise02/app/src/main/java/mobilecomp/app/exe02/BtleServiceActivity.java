package mobilecomp.app.exe02;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
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



                ParcelUuid uuids[] = myappdevice.getUuids();
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
                        }


            }
        });

        GattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Temperaturedata.setText(Temperaturedata.getText()+ " callback called  ");
            }
        };
    }
}
