package com.example.downloaderapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadFileService extends Service{

    private DownloadFileServiceImpl impl;
    private AsyncDownloadFile DownloadAsync;

    private class DownloadFileServiceImpl extends IDownloadFile.Stub {

        @Override
        public int downloadFile(String Str) {
            DownloadAsync.execute(Str);
            return 0;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        DownloadAsync = new AsyncDownloadFile();
        impl = new DownloadFileServiceImpl();
    }

    @Override
    public IBinder onBind(Intent intent){
        return impl;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
