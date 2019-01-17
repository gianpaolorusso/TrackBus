package com.example.gianpaolorusso.trackbus;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackService extends Service {

    String bus=null;
    String agenzia=null;

    public TrackService(){};
    public TrackService (String bus,String agenzia){

        this.agenzia=agenzia;
        this.bus=bus;
    }

    public TrackService TrackService(String bus,String agenzia)
    {
        if(this != null)
        {
            return this;
        }
        else
        {
            return new TrackService(bus,agenzia);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + agenzia + "/BUS" + bus;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReferenceFromUrl(url);
        ref.child("LOG").setValue("NO");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public ComponentName startService(Intent service) {

        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        this.onDestroy();
        return super.stopService(name);
    }
}
