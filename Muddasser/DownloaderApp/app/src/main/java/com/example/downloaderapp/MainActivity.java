package com.example.downloaderapp;

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
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements ServiceConnection{
    private static final String TAG = MainActivity.class.getSimpleName();
    private String url;
    private TextInputLayout editTextUrl;
    private IDownloadFile DownloadFileProxy = null;
    private int DownloadID;
    private TextView EndText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //editTextUrl = findViewById(R.id.editTextUrl);
        EndText = findViewById(R.id.LastCall);
        Button downloadButton = findViewById(R.id.buttonDownload);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the URL entered
                url = "https://newevolutiondesigns.com/images/freebies/galaxy-wallpaper-1.jpg";

                // Call the service with URL
                if (DownloadFileProxy != null) {
                    try {
                        DownloadID = DownloadFileProxy.downloadFile(url);
                        EndText.setText("DONE DONE DONE");
                    } catch (RemoteException ex) {
                        DownloadID = -1;
                    }
                }
            }

        });
        Intent i = new Intent(this, DownloadFileService.class);
        bindService(i, this, BIND_AUTO_CREATE);
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

