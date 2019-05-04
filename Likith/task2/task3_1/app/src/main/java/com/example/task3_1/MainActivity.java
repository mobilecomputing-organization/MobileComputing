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

    TextView SensorText;
    TextView TimerText;
    private float SensorValue = 0;
    CountDownTimer Timer;
    private ISensorVal sensorvalproxy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorText = findViewById(R.id.SensorVal);
        TimerText = findViewById(R.id.Timer);
        SensorText.setText(String.valueOf(SensorValue));

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
                        SensorValue = -1;
                    }
                }
                SensorText.setText("Val:" + SensorValue);
                Timer.start();
            }
        }.start();
    }
}

