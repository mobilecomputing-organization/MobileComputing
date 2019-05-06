package com.example.task3_1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorVal extends Service {

    private SensorValImpl impl;
    GetData mSensorData = null;

    private class SensorValImpl extends ISensorVal.Stub {

        @Override
        public float[] GetSensorVal() {
            return mSensorData.Data;//sensor_event.values[0];
            //    return 0;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorData = new GetData(this);
        impl = new SensorValImpl();
    }


    @Override
    public IBinder onBind(Intent intent){
        mSensorData.register();
        return impl;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mSensorData.register();
        super.onDestroy();
    }
}