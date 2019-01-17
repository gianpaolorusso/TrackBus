package com.example.gianpaolorusso.trackbus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private BusClass bus = null;
    private SupportMapFragment map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        TextView  textView=findViewById(R.id.nota);
        bus = (BusClass) getIntent().getSerializableExtra("bus");
        String agenzia = getIntent().getStringExtra("agenzia");
        TextView title=(TextView)LayoutInflater.from(this).inflate(R.layout.title_acitvity,null);

       ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT,Gravity.CENTER);
       title.setText(agenzia+" "+bus.getN());
       getSupportActionBar().setCustomView(title,params);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        textView.setText(bus.getNota());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

           Location loc = new Location(Context.LOCATION_SERVICE);
           loc.setLatitude(bus.getLatitudine());
           loc.setLongitude(bus.getLongitudine());
           MarkerOptions markerOptions = new MarkerOptions();
           LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
           markerOptions.position(latLng);
           markerOptions.title(String.valueOf(bus.getN()));
           googleMap.addMarker(markerOptions);
           googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
       }
       else
       {
           Toast.makeText(getBaseContext(),"Abilitare la posizione",Toast.LENGTH_SHORT).show();
           Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
           startActivity(settingsIntent);
       }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        map.getMapAsync(this);
    }

}
