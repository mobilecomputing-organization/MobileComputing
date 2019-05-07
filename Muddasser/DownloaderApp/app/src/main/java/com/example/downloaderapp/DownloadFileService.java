package com.example.downloaderapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public class AsyncDownloadFile extends AsyncTask<String, Integer, String> {

        private String fileName;
        private String folder;
        private boolean isDownloaded;
        private final String TAG = AsyncDownloadFile.class.getSimpleName();

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // Get file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                // Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                // Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;

                // Output stream to write file
                OutputStream output = new FileOutputStream( folder + fileName );

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    // After this onProgressUpdate will be called
                    double d_percent = (((total * 100) / lengthOfFile));
                    if((d_percent > 0) && (((int)d_percent) % 1) == 0)
                    {
                        int arr_temp[] = {(int)d_percent, (int)total, lengthOfFile};

                        // Publish the progress
                        publishProgress(arr_temp[0], arr_temp[1], arr_temp[2]);
                        Log.d(TAG,  "Progress: " + d_percent + "%.");
                    }

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(Integer... progress) {

            Intent progressBC = new Intent();
            progressBC.setAction("ProgressBC");

            // Update progress info
            progressBC.putExtra("percent",progress[0]);
            progressBC.putExtra("curr_dwn",progress[1]);
            progressBC.putExtra("file_size",progress[2]);

            sendBroadcast(progressBC);
            Log.d(TAG, "Progress: called ");
        }

        @Override
        protected void onPostExecute(String message) {
            sendMyBroadCast();

            // Display File path after downloading
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        /* Send Broadcast*/
        private void sendMyBroadCast()
        {
            try
            {
                Intent broadCastIntent = new Intent();
                broadCastIntent.setAction("com.example.downloaderapp.DOWNLOAD_NOTIFICATION");
                sendBroadcast(broadCastIntent);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }


}
