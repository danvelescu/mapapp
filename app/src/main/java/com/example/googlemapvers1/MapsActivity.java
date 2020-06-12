package com.example.googlemapvers1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.IDNA;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String CHANNEL_ID = "User";
    LatLng testPoint;
    private int PROXIMITY_RADIUS = 5000;
    String TAG = "Main Activity";
    private int DEFAULT_ZOOM = 1;
    private LatLng mDefaultLocation = new LatLng(45 ,24);
    private Location mLastKnownLocation;
     private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION  = 100;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
   private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPrmission();



        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        mMap = googleMap;
        testPoint = new LatLng(45.7526686,24.1127237);//Q437+3X Сибиу, Румыния
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);


        MarkerOptions markerO = new MarkerOptions().position(testPoint).title("Poarta de lemn Maramureş").snippet("Poarta monumentală transferată în anul 1974");



        InfoData info = new InfoData();
        info.setImage("aula_0");
        info.setInfoString("Example first example");
        CostumeInfoWindowMap customInfoWindow = new CostumeInfoWindowMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);
        Marker m = mMap.addMarker(markerO);
        m.setTag(info);
        m.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(testPoint));


        updateLocationUI();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void chekNear(){

            float[] results = new float[1];
            Location.distanceBetween(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),45.7526686,24.1127237,results);

         Log.println(Log.WARN,"alert",results.toString());
         if(results[0]<10000){
             Toast.makeText(this,"Esti Aproape",Toast.LENGTH_LONG).show();
         }


    }

    private void openDialogPage(){


    }

    private void getLocationPrmission(){

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);


            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPrmission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            chekNear();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                  new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }
}
