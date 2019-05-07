package com.example.task3_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GetData implements SensorEventListener {
    private SensorManager sensor_manager;
    private Sensor gyroSensor;
    float Data[] = {0,0,0};

    public GetData(Context context){
        sensor_manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //gyroSensor = sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroSensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onAccuracyChanged(Sensor event, int arg1){
        // TODO

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Data[0] = event.values[0];
        Data[1] = event.values[1];
        Data[2] = event.values[2];
    }

    public void register(){
        sensor_manager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensor_manager.unregisterListener(this);
    }
}
