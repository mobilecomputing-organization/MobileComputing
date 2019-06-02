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

    Location location;
    Location previousLocation = null;
    float minDistance = 10;
    long interval = 1;
    double totalDistance = 0;
    double averageSpeed = 0;
    private LocationManager locationManager;
    private LocationListener listener;
    private TextView AverageSpeedTextView;
    private TextView distanceTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize ui
        AverageSpeedTextView = findViewById(R.id.AverageSpeedTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                Log.i(TAG, " onLocationChanged: "+loc.toString());
                if(previousLocation == null)
                    previousLocation = loc;
                else
                    previousLocation = location;
                location = loc;
                calculateDistance();
                calculateAverageSpeed();
                convertGPX();
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
            Document document;
            Element root;
            //if(fileExist) {
                document = documentBuilder.newDocument();
                root = document.createElement("gpx");
                document.appendChild(root);
            //}
/*            try {
                Document document = documentBuilder.parse("server.xml");
            } catch (Io e){}*/
            root = document.getDocumentElement();
            // root element


            // wpt element
            Element wpt = document.createElement("wpt");

            root.appendChild(wpt);


            // lat element
            Element lat = document.createElement("lat");
            lat.appendChild(document.createTextNode(Double.toString(location.getLatitude())));
            wpt.appendChild(lat);

            // lon element
            Element lon = document.createElement("lon");
            lon.appendChild(document.createTextNode(Double.toString(location.getLongitude())));
            wpt.appendChild(lon);

            // ele element
            Element ele = document.createElement("ele");
            ele.appendChild(document.createTextNode(Double.toString(location.getAltitude())));
            wpt.appendChild(ele);

            // time elements
            Element time = document.createElement("time");
            time.appendChild(document.createTextNode(new Date(location.getTime()).toString()));
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

            System.out.println(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    File.separator + FILE_NAME+"Done creating XML File");

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
        if(location!= null) {
            AverageSpeedTextView.setText(Double.toString(averageSpeed));
            distanceTextView.setText(Double.toString(totalDistance));
            latitudeTextView.setText(Double.toString(location.getLatitude()));
            longitudeTextView.setText(Double.toString(location.getLongitude()));
        }
    }
    public void exit(View v){
        Log.i(TAG, " exit: ");
        finishAffinity();
        System.exit(0);
    }

    //Todo: copy and check the gpx file
    //private double getDistance(double lat1, double lon1, double lat2, double lon2) {
    private void calculateDistance() {
        double theta = previousLocation.getLongitude() - location.getLongitude();
        double distance = Math.sin(deg2rad(previousLocation.getLatitude()))
                * Math.sin(deg2rad(location.getLatitude()))
                + Math.cos(deg2rad(previousLocation.getLatitude()))
                * Math.cos(deg2rad(location.getLatitude()))
                * Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60 * 1.1515;
        totalDistance = totalDistance + distance;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private void calculateAverageSpeed() {
        if(averageSpeed == 0) averageSpeed = location.getSpeed();
        averageSpeed = (averageSpeed + location.getSpeed())*0.5;
    }
}
