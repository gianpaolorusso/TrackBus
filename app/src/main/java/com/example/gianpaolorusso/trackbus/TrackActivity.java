package com.example.gianpaolorusso.trackbus;

import android.content.BroadcastReceiver;
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

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String numeroBus = null;
    private String agenzia = null;
    private TrackService trackService = null;
    private SupportMapFragment mappa = null;
    private Button btnStart = null;
    private RelativeLayout mapLayout = null;
    private Button btnStop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        mappa=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        btnStart = findViewById(R.id.btnSart);
        agenzia=getIntent().getStringExtra("agenzia");
        numeroBus= getIntent().getStringExtra("bus");
        this.setTitle("TRACCIA BUS "+ numeroBus);
        trackService=new TrackService(numeroBus,agenzia);
        mapLayout=findViewById(R.id.mapLayout);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.GONE);
                mapLayout.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
            }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(View.VISIBLE);
                mapLayout.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);

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
    public void onMapReady(GoogleMap googleMap) {


    }
}
