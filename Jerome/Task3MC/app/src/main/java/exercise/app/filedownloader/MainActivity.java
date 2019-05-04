package exercise.app.filedownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    DownloadManager dm;
    long queueid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
                {
                    DownloadManager.Query req_query = new DownloadManager.Query();
                    req_query.setFilterById(queueid);

                    Cursor c = dm.query(req_query);

                    if(c.moveToFirst())
                    {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if(DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex))
                        {
                            TextView resultstat = (TextView)findViewById(R.id.textView);
                            resultstat.setText("File downloaded in the local path " + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
                        }
                    }


                }

            }
        };

        registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



        Button Download = (Button)findViewById(R.id.DownloadButton);

        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://newevolutiondesigns.com/images/freebies/galaxy-wallpaper-1.jpg"));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"MyDown");

                queueid = dm.enqueue(request);


            }
        });

    }
}
