package com.example.gianpaolorusso.trackbus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.awt.font.NumericShaper;
import java.security.PrivilegedAction;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String numeroBus = null;
    private String agenzia = null;
    private SupportMapFragment mappa = null;
    private Button btnStart = null;
    private RelativeLayout mapLayout = null;
    private Button btnStop = null;
    private TrackService trackService=null;
    private Intent i=null;
    private  Intent received=null;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        mappa=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        btnStart = findViewById(R.id.btnSart);
        received=getIntent();
        agenzia=getIntent().getStringExtra("agenzia");
        numeroBus= getIntent().getStringExtra("bus");
        this.setTitle("TRACCIA BUS "+ numeroBus);
        mapLayout=findViewById(R.id.mapLayout);
        btnStop = findViewById(R.id.btnStop);
        trackService=new TrackService();
        i = new Intent(TrackActivity.this,TrackService.class);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                if (ActivityCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    AlertDialog.Builder builder=new AlertDialog.Builder(TrackActivity.this);
                    builder.setTitle("Abilitare GPS e rete dati");
                    builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.create().show();
                    return;
                }
                else{

                    Intent i = new Intent(TrackActivity.this,TrackService.class);
                    i.putExtra("agenzia",agenzia);
                    i.putExtra("bus",numeroBus);
                    trackService=new TrackService(numeroBus,agenzia,TrackActivity.this);
                    LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    trackService.setLocationManager(locationManager);
                    trackService.onCreate();
                }

            }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                Intent i = new Intent(TrackActivity.this,TrackService.class);
                stopService(i);

            }
        });
    }
    private void logOutBus()
    {
        FirebaseDatabase database=FirebaseDatabase.getInstance();

        if(!numeroBus.equals("") && !numeroBus.equals("Nessun bus disponibile")) {
            String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + agenzia + "/BUS" +numeroBus;
            DatabaseReference ref = database.getReferenceFromUrl(url);
            ref.child("LOG").setValue("NO");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        logOutBus();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        logOutBus();
    }

    @Override
    public void finish() {
        super.finish();
        logOutBus();
    }

    @Override
    public void finishAffinity() {
        super.finishAffinity();
        logOutBus();
    }

    @Override
    public void onBackPressed() {
        Intent i =new Intent(TrackActivity.this,MenagementActivity.class);
        i.putExtra("agenzia",agenzia);
        String numeroBus=this.getTitle().subSequence(12,this.getTitle().length()).toString();
        i.putExtra("bus",numeroBus);
        setResult(1,i);
       finish();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


    }
}
