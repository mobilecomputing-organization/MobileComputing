package com.example.task3_1;

import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    TextView SensorTextX,SensorTextY,SensorTextZ;
    TextView TimerText;
    private float[] SensorValue = {0,0,0};
    CountDownTimer Timer;
    private ISensorVal sensorvalproxy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorTextX = findViewById(R.id.SensorValX);
        SensorTextY = findViewById(R.id.SensorValY);
        SensorTextZ = findViewById(R.id.SensorValZ);

        TimerText = findViewById(R.id.Timer);
        //SensorText.setText(String.valueOf(SensorValue));
        SensorTextX.setText("0.0");
        SensorTextY.setText("0.0");
        SensorTextZ.setText("0.0");

        RunTimer();

        Intent i = new Intent(this, SensorVal.class);
        bindService(i, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName CompName, IBinder ibinder) {
        sensorvalproxy = ISensorVal.Stub.asInterface(ibinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName CompName) {
        sensorvalproxy = null;
    }

    public void RunTimer() {
        Timer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                TimerText.setText("remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (sensorvalproxy != null) {
                    try {
                        SensorValue = sensorvalproxy.GetSensorVal();
                    } catch (RemoteException ex) {
                        SensorValue[0] = -1;
                    }
                }
                //SensorText.setText("Val:" + SensorValue);
                SensorTextX.setText( Float.toString(SensorValue[0]));
                SensorTextY.setText( Float.toString(SensorValue[1]));
                SensorTextZ.setText( Float.toString(SensorValue[2]));

                Timer.start();
            }
        }.start();
    }
}

