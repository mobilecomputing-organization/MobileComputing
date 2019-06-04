package mobilecomp.app1.gpslogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ServiceLocation extends Service {
    public ServiceLocation() {
    }

    private static final String FILE_NAME = "sample.gpx";
    private static final String TAG = "ServiceLocation";
    private ServiceLocationImpl locationService_Ibinder;
    private Location location;
    Location previousLocation = null;
    float minDistance = 0.01f;
    long interval = 1000;
    double totalDistance = 0;
    double averageSpeed = 0;
    private LocationManager locationManager;
    private LocationListener listener;
    private long StartTime = 0;
    private Document document;
    private DocumentBuilder documentBuilder;
    private Transformer transformer;
    private File file;


    private class ServiceLocationImpl extends IServiceLocation.Stub {

        public double rpc_getLatitude() {
            if (location!= null )
                return (location.getLatitude());
            else
                return 0.0;
        }

        public double rpc_getLongitude() {

            if (location!= null )
                return (location.getLongitude());
            else
                return 0.0;
        }

        public double rpc_getDistance() { return (totalDistance);  }

        public double rpc_getAverageSpeed() { return calculateAverageSpeed();}

    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationService_Ibinder = new ServiceLocationImpl();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // create a new gpx file with required headers tags
        Createnewfile();


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location dynloc) {
                if (StartTime == 0)
                {
                    StartTime = dynloc.getTime();
                }
                Log.i(TAG, " onLocationChanged: "+dynloc.toString());
                if(previousLocation == null)
                    previousLocation = dynloc;
                else
                    previousLocation = location;
                location = dynloc;
                calculateDistance();
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
            Log.i(TAG,"requestLocationUpdates success");
        }
        catch (SecurityException e){
            Log.i(TAG,"Exception caught in requestLocationUpdates");
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

    private void Createnewfile()
    {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            // root element
            Element root = document.createElement("gpx");
            document.appendChild(root);
            Element trk = document.createElement("trk");
            root.appendChild(trk);
            Element trkseg = document.createElement("trkseg");
            trk.appendChild(trkseg);
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);

            file =  new File(Environment.getExternalStorageDirectory() + File.separator + FILE_NAME);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSource, streamResult);

            System.out.println(Environment.getExternalStorageDirectory() +
                    File.separator + FILE_NAME+"Done creating XML File");
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
    private void convertGPX(){
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            // root element
            document= documentBuilder.parse(file);

            Node root = document.getElementsByTagName("trkseg").item(0);

            // wpt element
            Element trkpt = document.createElement("trkpt");
            trkpt.setAttribute("lat",Double.toString(location.getLatitude()));
            trkpt.setAttribute("lon",Double.toString(location.getLongitude()));
            root.appendChild(trkpt);

            // ele element
            Element ele = document.createElement("ele");
            ele.appendChild(document.createTextNode(Double.toString(location.getAltitude())));
            trkpt.appendChild(ele);

            // time elements
            Element time = document.createElement("time");
            //set time
            Date date = new Date(location.getTime());
            time.setTextContent(format.format(date));
            trkpt.appendChild(time);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSource, streamResult);

        } catch (SAXException | IOException | TransformerException ex){
            ex.printStackTrace();
        }
    }

    private void calculateDistance() {
        float[] result = {0};
        Location.distanceBetween(previousLocation.getLatitude(),previousLocation.getLongitude(),location.getLatitude(),location.getLongitude(),result);
        Log.i(TAG,"result 1" +Float.toString(result[0]));
        totalDistance = totalDistance + result[0];
    }

    private double calculateAverageSpeed() {
        if (location!=null)
        {
            Log.i(TAG,Long.toString(location.getTime()-StartTime));
            return totalDistance/((location.getTime()-StartTime)/1000);
        }
        else
            return 0.0;
    }
}
