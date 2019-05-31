package com.example.excercise3gps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "sample.gpx";

    double latitude;
    double longitude;
    double distance = 0;
    double averageSpeed = 0;
    float minDistance = 10;
    long interval = 1;
    private LocationManager locationManager;
    private LocationListener listener;
    Button StartServiceButton ;
    Button exitButton;
    Button beaconText;
    Button updateValueButton;
    TextView AverageSpeedTextView;
    TextView distanceTextView;
    TextView latitudeTextView;
    TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize ui
        StartServiceButton = findViewById(R.id.startServiceButton);
        exitButton = findViewById(R.id.stopServiceButton);
        beaconText = findViewById(R.id.exitButton);
        updateValueButton = findViewById(R.id.updateValueButton);
        AverageSpeedTextView = findViewById(R.id.AverageSpeedTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, " onLocationChanged: "+location.toString());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                save(location.toString());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.i(TAG, " onStatusChanged: "+s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.i(TAG, " onProviderEnabled: " +s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i(TAG, " onProviderDisabled: "+s);
            }
        };
    }
    void save(String str){

        //External directory path to save file
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator ;
        OutputStream output = null;
        try {
            output = new FileOutputStream( folder + FILE_NAME );
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        try {
            if(output != null) output.write(str.getBytes());

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(output != null) output.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void convertGPX(){
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("gpx");
            document.appendChild(root);

            // wpt element
            Element wpt = document.createElement("wpt");

            root.appendChild(wpt);


            // lat element
            Element lat = document.createElement("lat");
            lat.appendChild(document.createTextNode("James"));
            wpt.appendChild(lat);

            // lon element
            Element lon = document.createElement("lon");
            lon.appendChild(document.createTextNode("Harley"));
            wpt.appendChild(lon);

            // ele element
            Element ele = document.createElement("ele");
            ele.appendChild(document.createTextNode(""));
            wpt.appendChild(ele);

            // time elements
            Element time = document.createElement("time");
            time.appendChild(document.createTextNode("0:0:0"));
            wpt.appendChild(time);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    File.separator + FILE_NAME));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging
            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void startService(View v){
        Log.i(TAG, " startService: ");
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // use GPS device
                    interval, // hint for notification interval
                    minDistance, // hint for minimum position distance
                    listener); // callback receiver
        } catch (SecurityException e){
        }
    }
    public void stopService(View v){
        Log.i(TAG, " stopService: ");
        locationManager.removeUpdates(listener);
    }
    public void updateValue(View v){
        Log.i(TAG, " updateValue: ");
        AverageSpeedTextView.setText(Double.toString(getAverageSpeed()));
        distanceTextView.setText(Double.toString(getDistance()));
        latitudeTextView.setText(Double.toString(latitude));
        longitudeTextView.setText(Double.toString(longitude));
    }
    public void exit(View v){
        Log.i(TAG, " exit: ");
        finishAffinity();
        System.exit(0);
    }

    public double getAverageSpeed(){return  averageSpeed;}//Todo
    public double getDistance(){return  distance;}//Todo
    //Todo: copy and check the gpx file
}
