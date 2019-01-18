package com.example.gianpaolorusso.trackbus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackService extends Service {

    String bus = null;
    Context cx=null;
    LocationManager locationManager = null;
    LocationProvider locationProvider = null;

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }


    String agenzia = null;

    public TrackService() {
    }


    public TrackService(String bus, String agenzia,Context cx) {

        this.agenzia = agenzia;
        this.bus = bus;
        this.cx = cx;
    }



    @Override
    public void onCreate() {
        String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + this.agenzia + "/BUS" + this.bus;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl(url);
        ref.child("LOG").setValue("SI");


        LocationProvider provider = locationManager.getProvider(locationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this.cx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.cx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
                locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + agenzia + "/BUS" + bus+"/COORDINATE";
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReferenceFromUrl(url);
                    ref.child("LONGITUDINE").setValue(location.getLongitude());
                    ref.child("LATITUDINE").setValue(location.getLatitude());

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logoutBus();
    }


    @Override
    public IBinder onBind(Intent intent) {
        this.bus = intent.getStringExtra("bus");
        this.agenzia = intent.getStringExtra("agenzia");
        this.onCreate();
        return null;
    }

    @Override
    public ComponentName startService(Intent service) {
        this.bus = service.getStringExtra("bus");
        this.agenzia = service.getStringExtra("agenzia");
        this.onCreate();
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        logoutBus();
        locationManager=null;
        this.onDestroy();
        return  true;
    }

    private void logoutBus()
    {
        String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + agenzia + "/BUS" + bus;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl(url);
        ref.child("LOG").setValue("NO");
    }
}
