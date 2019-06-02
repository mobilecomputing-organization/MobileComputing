package com.example.excercise3gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ServiceLocation extends Service {
    private static final String FILE_NAME = "sample.gpx";
    private static final String TAG = "ServiceLocation";

    private ServiceLocationImpl locationService_Ibinder;
    private Location location;
    Location previousLocation = null;
    float minDistance = 10;
    long interval = 1;
    double totalDistance = 0;
    double averageSpeed = 0;
    private LocationManager locationManager;
    private LocationListener listener;

    private class ServiceLocationImpl extends IServiceLocation.Stub {

        public double rpc_getLatitude() {
            return (location.getLatitude());
        }

        public double rpc_getLongitude() {
            return (location.getLongitude());
        }

        public double rpc_getDistance() {
            return (totalDistance);
        }

        public double rpc_getAverageSpeed() {
            return (averageSpeed);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationService_Ibinder = new ServiceLocationImpl();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location dynloc) {
                Log.i(TAG, " onLocationChanged: "+dynloc.toString());
                if(previousLocation == null)
                    previousLocation = dynloc;
                else
                    previousLocation = location;
                location = dynloc;
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

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,   // use GPS device
                    interval,                       // hint for notification interval
                    minDistance,                    // hint for minimum position distance
                    listener);                      // callback receiver
        }
        catch (SecurityException e){
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return locationService_Ibinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        locationManager.removeUpdates(listener);
        // Write file to memory
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Toast.makeText(this, "Invoke LocationService. LocationService starting...", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(listener);
        super.onDestroy();
        Toast.makeText(this, "Background LocationService stopped.", Toast.LENGTH_LONG).show();
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
            lat.appendChild(document.createTextNode(Double.toString(location.getLatitude())));
            wpt.appendChild(lat);

            // lon element
            Element lon = document.createElement("lon");
            lon.appendChild(document.createTextNode(Double.toString(location.getLatitude())));
            wpt.appendChild(lon);

            // ele element
            Element ele = document.createElement("ele");
            ele.appendChild(document.createTextNode(Double.toString(location.getAltitude())));
            wpt.appendChild(ele);

            // time elements
            Element time = document.createElement("time");
            time.appendChild(document.createTextNode(Double.toString(location.getTime())));
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
