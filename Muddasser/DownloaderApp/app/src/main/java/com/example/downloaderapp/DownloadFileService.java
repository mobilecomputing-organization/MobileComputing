package com.example.downloaderapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
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
//        intent = new Intent();
//        intent.setAction("com.example.broadcast.MY_NOTIFICATION");
//        intent.putExtra("data","Notice me senpai!");
//        sendBroadcast(intent);
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

        //private ProgressDialog progressDialog;
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
        /*this.progressDialog = new ProgressDialog(MainActivity.this);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();*/
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
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                //fileName = "DownloadedFile";

                //Append timestamp to file name

                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                //folder = Environment.getDataDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator;
                folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;

                //File dir = Environment.getDataDirectory();
                //Log.d(TAG, dir.toString());

                //Create DownloaderApp folder if it does not exist
//            File directory = new File(folder);
//
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }

                // Output stream to write file
                OutputStream output = new FileOutputStream( folder + fileName );

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

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

            // uncomment this line if you want to send data
            progressBC.putExtra("data",progress[0]);

            sendBroadcast(progressBC);
            Log.d(TAG, "Progress: called ");

            // setting progress percentage
            // progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            // this.progressDialog.dismiss();
            sendMyBroadCast();


            // Display File path after downloading
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        private void sendMyBroadCast()
        {
            try
            {
                Intent broadCastIntent = new Intent();
                broadCastIntent.setAction("com.example.downloaderapp");

                // uncomment this line if you want to send data
//            broadCastIntent.putExtra("data", "abc");

                sendBroadcast(broadCastIntent);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }


}
