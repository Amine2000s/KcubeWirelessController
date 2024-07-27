package com.example.kcubewirelesscontroller;



import static com.example.kcubewirelesscontroller.MainActivity.BLUETOOTH_MAC_ADRESS;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.Marker;


public class GpsModeControlActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    BluetoothDevice hc05 = null;
    BluetoothSocket btSocket = null;
    OutputStream outputStream = null;

    InputStream inputStream = null;
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private FusedLocationProviderClient fusedLocationClient;

    FusedLocationProviderClient fusedLocationProviderClient ;
    Button setLocationBtn,showRobotBtn,startNavBtn;
    TextView longitudeView;
    TextView latitudeView;

    LocationManager locationManager;
     MapView myMap;

    IMapController mapController ;

    GeoPoint userLocationPoint , robotLocationPoint  ;

    Marker userMarker,robotMarker ;

    private List<Marker> nodeMarkers = new ArrayList<>(); // List to store markers


    Road road ;
    Polyline navigationOverlay;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

    /////////////////////////////////




        //////////////////////////////////////
        /*SupportMapFragment supportMapFragment  =(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapView);
        supportMapFragment.getMapAsync(this);
        */
        setContentView(R.layout.activity_gps_mode);

        latitudeView = findViewById(R.id.lat_);
        longitudeView = findViewById(R.id.long2_);

        setLocationBtn = findViewById(R.id.get_location_btn);
        showRobotBtn = findViewById(R.id.showRobotLocationBTn);
        startNavBtn = findViewById(R.id.startNavBtn);


        myMap = findViewById(R.id.mapView);
        myMap.setTileSource(TileSourceFactory.MAPNIK);
        myMap.setMultiTouchControls(true);
        myMap.setBuiltInZoomControls(true);
        mapController = myMap.getController();
        mapController.setZoom(15.0);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Runtime permissions


        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        System.out.println(btAdapter.getBondedDevices());
        //set the mac adress of your hc05
        hc05 = btAdapter.getRemoteDevice(BLUETOOTH_MAC_ADRESS);
        System.out.println(hc05.getName());
        connect();
        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userLocationPoint==null){
                    getLocation(v);
                    setLocationBtn.setText("Delete your location");
                }else{

                    myMap.getOverlays().remove(userMarker);
                    myMap.postInvalidate();
                    System.out.println("delete with succes");
                    Toast.makeText(GpsModeControlActivity.this, " User Location Deleted successfully" , Toast.LENGTH_SHORT).show();

                    setLocationBtn.setText("Set your location");
                    userLocationPoint=null;

                }


            }
        });

        showRobotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(robotLocationPoint==null){
                    try {
                        pointToRobotLocation();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    showRobotBtn.setText("Delete robot in map");
                    //Toast.makeText(GpsModeControlActivity.this, " Robot Location Loaded successfully" , Toast.LENGTH_SHORT).show();

                    myMap.postInvalidate();

                }else{
                    myMap.getOverlays().remove(robotMarker);

                    myMap.postInvalidate();

                    showRobotBtn.setText("Show robot in map");
                    robotLocationPoint=null;
                }
            }
        });
        startNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userLocationPoint == null && robotLocationPoint ==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GpsModeControlActivity.this);
                    builder.setTitle("User and Robot Location are Not Set").setMessage("Please Set User and Robot  Location before attempting to start navigation ").show();
                }else if(userLocationPoint==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GpsModeControlActivity.this);
                    builder.setTitle("User location Not Set").setMessage("Please Set User Location before attempting to start navigation").show();
                }else if (robotLocationPoint==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GpsModeControlActivity.this);
                    builder.setTitle("Robot location Not Set").setMessage("Robot Set User Location before attempting to start navigation ").show();
                }else{

                    if(navigationOverlay==null){
                        drawRoute();
                        startNavBtn.setText("Cancel navigation");

                    }else{
                        myMap.getOverlays().remove(navigationOverlay);
                        for (Marker marker : nodeMarkers) {
                            myMap.getOverlays().remove(marker);
                        }
                        myMap.invalidate(); // Refresh the map
                        nodeMarkers.clear();
                        myMap.postInvalidate();
                        navigationOverlay=null;

                        startNavBtn.setText("Start navigation");

                    }
                }


            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        myMap.onResume(); // needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Configuration.getInstance().save(this, prefs);
        myMap.onPause(); // needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    public void getLocation(View view) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return ;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            latitudeView.setText(String.valueOf(latitude));
                            longitudeView.setText(String.valueOf(longitude));

                            // Convert latitude and longitude to address
                            Geocoder geocoder = new Geocoder(GpsModeControlActivity.this);
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String addressLine = address.getAddressLine(0); // Get the first address line
                                    pointToUserLocation(latitude,longitude);
                                    Toast.makeText(GpsModeControlActivity.this, "User Location loaded successfully" , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GpsModeControlActivity.this, "There is no address for this location", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(GpsModeControlActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(GpsModeControlActivity.this, "Location feature is not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void pointToUserLocation(double latitude, double longitude) {
         userLocationPoint = new GeoPoint(latitude, longitude);
        GeoPoint endPoint = new GeoPoint(34.844442, 5.746465);

        myMap.getController().setZoom(18.0); // Set the desired zoom level
        myMap.getController().setCenter(userLocationPoint);

        userMarker = new Marker(myMap);
        userMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.persone_pin_foreground));
        userMarker.setPosition(userLocationPoint);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //startMarker.setOnMarkerClickListener((marker, mapView) -> false);

        myMap.getOverlays().add(userMarker);

    //   drawRoute(startPoint,endPoint);

        //myMap.getOverlays().add(line);

        myMap.invalidate();
    }

    private void drawRoute() {
        RoadManager roadManager = new OSRMRoadManager(this, "MyUserAgent/1.0");

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(userLocationPoint);
        waypoints.add(robotLocationPoint);

        new Thread(() -> {
             road = roadManager.getRoad(waypoints);
            runOnUiThread(() -> {
                if (road.mStatus == Road.STATUS_OK) {
                   navigationOverlay = RoadManager.buildRoadOverlay(road);
                    myMap.getOverlays().add(navigationOverlay);
                    myMap.invalidate();
                } else {
                    // Handle the error
                }
            });
            Drawable nodeIcon = getResources().getDrawable(org.osmdroid.bonuspack.R.drawable.marker_cluster);
            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(myMap);
                nodeMarker.setPosition(node.mLocation);
                nodeMarker.setIcon(nodeIcon);
                nodeMarker.setTitle("Step "+i);
                myMap.getOverlays().add(nodeMarker);

                nodeMarker.setSnippet(node.mInstructions);

                nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
                nodeMarkers.add(nodeMarker);

               // Drawable icon = getResources().getDrawable(org.osmdroid.bonuspack.R.drawable.);
                //nodeMarker.setImage(icon);
            }

        }).start();
    }

    private void startNavigation(double latitude, double longitude) {
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        myMap.getController().setZoom(25.0); // Set the desired zoom level
        myMap.getController().setCenter(startPoint);

        // Add a marker to the map
        Marker startMarker = new Marker(myMap);


        //Drawable drawable ;
        startMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.persone_pin_foreground));
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMap.getOverlays().add(startMarker);




    }

    public void pointToRobotLocation() throws IOException {



        outputStream.write("18\n".getBytes());
        byte[] buffer = new byte[256];
        int length = inputStream.read(buffer);
        String text = new String(buffer, 0, length);

        System.out.println("####################################");
        System.out.println(text);
        System.out.println("####################################");
        String[] parts = text.split(",");

        if ( parts[0] != null && parts[0] != null ){
        double lat = Double.valueOf(parts[0]); // 004
        //double lon = Double.valueOf(parts[1]); // 034556

         /*
        if (parts.length >= 2 && parts[0] != null && parts[1] != null) {
            double lat = Double.valueOf(parts[0]); // Latitude
            double lon = Double.valueOf(parts[1]); // Longitude
         */



        Toast.makeText(this, lat+"/", Toast.LENGTH_SHORT).show();

        robotLocationPoint = new GeoPoint(lat, 0);
        myMap.getController().setZoom(18.0); // Set the desired zoom level
        myMap.getController().setCenter(robotLocationPoint);

        // Add a marker to the map
         robotMarker = new Marker(myMap);

        robotMarker.setIcon(ContextCompat.getDrawable(this, R.mipmap.robot_pin_foreground));
        robotMarker.setPosition(robotLocationPoint);
        robotMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMap.getOverlays().add(robotMarker);
        myMap.postInvalidate();//

        }
    }
/*
    public void getLocation(View view) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return ;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            longitudeView.setText(longitude+"");
                            latitudeView.setText(latitude+"");

                            // Convert latitude and longitude to address
                            Geocoder geocoder = new Geocoder(GpsModeControlActivity.this);
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String addressLine = address.getAddressLine(0); // Get the first address line
                                    // textView_location.setText(addressLine);
                                    // Show the address in a toast
                                   /* String tempadress = address.toString();
                                    location_textview.setText(addressLine);*/
                                  /*  Toast.makeText(GpsModeControlActivity.this, "تم تحميل الموقع الجغرافي بنجاح" , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GpsModeControlActivity.this, "لا يوجد عنوان لهاذا الموقع", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(GpsModeControlActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(GpsModeControlActivity.this, "خاصية تحديد الموقع غير متوفرة", Toast.LENGTH_SHORT).show();
                        }



                    }

                });


    }

*/


    private void addMarker(GeoPoint p) {
        Marker marker = new Marker(myMap);
        marker.setPosition(p);
        marker.setTitle("Marker at (" + p.getLatitude() + ", " + p.getLongitude() + ")");
        myMap.getOverlays().add(marker);
        myMap.invalidate(); // Refresh the map view
    }



    private void removeMarkerIfPresent(GeoPoint p) {
        for (int i = 0; i < myMap.getOverlays().size(); i++) {
            if (myMap.getOverlays().get(i) instanceof Marker) {
                Marker marker = (Marker) myMap.getOverlays().get(i);
                if (marker.getPosition().distanceToAsDouble(p) < 250) { // Threshold distance
                    myMap.getOverlays().remove(i);
                    myMap.invalidate(); // Refresh the map view
                    break;
                }
            }
        }
    }


    private boolean markerExists(GeoPoint p){
        for (int i = 0; i < myMap.getOverlays().size(); i++) {
            if (myMap.getOverlays().get(i) instanceof Marker) {
                Marker marker = (Marker) myMap.getOverlays().get(i);
                if (marker.getPosition().distanceToAsDouble(p) < 50) { // Threshold distance
                    return true;
                }
            }
        }
        return false;
    }


    public void connect() {
        int counter = 0;
        do {
            try {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(btSocket);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                btSocket.connect();
                System.out.println(btSocket.isConnected());
                outputStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());
                Toast.makeText(GpsModeControlActivity.this, "Connceted Successfully", Toast.LENGTH_SHORT).show();
            }catch (IOException e ){
                e.printStackTrace();
            }
            counter++;

        }while(!btSocket.isConnected() && counter < 3);
    }

    public void get_robot_location() throws IOException {

        outputStream.write("18\n".getBytes());
        byte[] buffer = new byte[256];
        int length = inputStream.read(buffer);
        String text = new String(buffer, 0, length);

        //set
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }
}



