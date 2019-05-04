package com.example.task3_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GetData implements SensorEventListener {
    private SensorManager sensor_manager;
    private Sensor gyroSensor;
    float Data;

    public GetData(Context context){
        sensor_manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensor_manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onAccuracyChanged(Sensor event, int arg1){
        // TODO

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Data = event.values[0];
    }

    public void register(){
        sensor_manager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensor_manager.unregisterListener(this);
    }
}
