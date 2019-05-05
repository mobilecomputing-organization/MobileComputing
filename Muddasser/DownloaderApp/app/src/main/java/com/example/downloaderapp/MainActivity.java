package com.example.downloaderapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private static final String TAG = MainActivity.class.getSimpleName();
    private String url;
    private TextInputLayout editTextUrl;
    private IDownloadFile DownloadFileProxy = null;
    private int DownloadID;
    private TextView EndText;
    ProgressBar myprogressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //editTextUrl = findViewById(R.id.editTextUrl);
        EndText = findViewById(R.id.LastCall);
        Button downloadButton = findViewById(R.id.buttonDownload);
        myprogressBar = findViewById(R.id.myprogressBar);
        myprogressBar.setMax(100);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the URL entered
                //url = "https://newevolutiondesigns.com/images/freebies/galaxy-wallpaper-1.jpg";
                //url = "https://speed.hetzner.de/100MB.bin";
                url = "http://ipv4.download.thinkbroadband.com/20MB.zip";

                // Call the service with URL
                if (DownloadFileProxy != null) {
                    try {
                        DownloadID = DownloadFileProxy.downloadFile(url);
                        EndText.setText("Please wait the File is Downloading");
                    } catch (RemoteException ex) {
                        DownloadID = -1;
                    }
                }
            }

        });
        Intent i = new Intent(this, DownloadFileService.class);
        bindService(i, this, BIND_AUTO_CREATE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("com.example.downloaderapp"))
                {
                   EndText.setText("File downloaded in the local path");
                    }
                else if (action.equals("ProgressBC")){
                    Bundle b = intent.getExtras();

                    //EndText.setText(intent.getExtras().toString());
                    myprogressBar.setProgress(b.getInt("data"));
                }
                }
        };

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.example.downloaderapp");
        registerReceiver(receiver, intentFilter1);
       IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("ProgressBC");
        registerReceiver(receiver, intentFilter2);
    }

    @Override
    public void onServiceConnected(ComponentName CompName, IBinder ibinder) {
        DownloadFileProxy = IDownloadFile.Stub.asInterface(ibinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName CompName) {
        DownloadFileProxy = null;
    }

}

