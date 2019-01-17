package com.example.gianpaolorusso.trackbus;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static android.net.NetworkInfo.*;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog=null;
    private String urlAgenzie="https://trackbus-bd0ec.firebaseio.com/";
    private Spinner spinnerAgenzie=null;
    private Spinner spinnerBus=null;
    private BusClass busClass=null;
    private ArrayList<BusClass> lista=null;
    private Button btnTrack=null;
    private ArrayList<String>listaAgenzie=null;
    private Context cx=null;
    private  NetworkInfo networkInfo=null;
    private ConnectivityManager connectivityManager=null;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView labelAgenzia = findViewById(R.id.labelAgenzia);
        TextView labelBus = findViewById(R.id.labelNbus);
        spinnerAgenzie= findViewById(R.id.agenzie);
        spinnerBus = findViewById(R.id.nBus);
        btnTrack=findViewById(R.id.btnTrack);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Caricamento Agenzie");
        progressDialog.show();
        cx=this;
        networkState();


        spinnerAgenzie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateBus(spinnerAgenzie.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAgenzie.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lista.size()>0)
                {
                    for(BusClass el :lista)
                    {
                     if(String.valueOf(el.getN()).equals(spinnerBus.getSelectedItem()))
                     {
                         Intent i=new Intent(MainActivity.this,MapActivity.class);
                         i.putExtra("bus",el);
                         i.putExtra("agenzia",spinnerAgenzie.getSelectedItem().toString());
                         try {
                             PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),1,i,PendingIntent.FLAG_UPDATE_CURRENT);
                             pendingIntent.send();
                         } catch (PendingIntent.CanceledException e) {
                             e.printStackTrace();
                         }
                     }
                    }
                }
            }
        });
    }

    private void populateSpinnergenzie()
    {
        final ArrayList<String> agenzie=new ArrayList<>();

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, urlAgenzie+".json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject jsonObject2=jsonObject.getJSONObject("Agenzie");
                    Iterator<String> iterator=jsonObject2.keys();
                    while(iterator.hasNext())
                    {
                        String key=iterator.next().toUpperCase();
                        agenzie.add(key);
                    }
                    listaAgenzie=agenzie;
                    ArrayAdapter arrayAdapter=new ArrayAdapter(cx,R.layout.support_simple_spinner_dropdown_item,agenzie);
                    arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinnerAgenzie.setAdapter(arrayAdapter);

                    progressDialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Errore di connessione",Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(stringRequest);
    }

    private void populateBus(final String agenzia)
    {   progressDialog.setTitle("Caricamento Bus");
        progressDialog.show();
        final ArrayList<String>bus=new ArrayList<>();
        lista=new ArrayList<>();
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, urlAgenzie+"Agenzie/"+agenzia+".json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Iterator<String> iterator=jsonObject.keys();
                    while (iterator.hasNext())
                    {   String key=iterator.next();
                        if(!key.equals("PASSWORD"))
                        {

                        String ag =jsonObject.getString(key);
                        JSONObject jsonObject1=new JSONObject(ag);
                        JSONObject coordinate=jsonObject1.getJSONObject("COORDINATE");
                        busClass=new BusClass((Double.valueOf(coordinate.getString("LONGITUDINE"))),(Double.valueOf(coordinate.getString("LATITUDINE"))),Integer.valueOf(jsonObject1.getString("N°")),key,jsonObject1.getString("COMUNICAZIONE"));
                        bus.add(String.valueOf(busClass.getN()));
                        lista.add(busClass);}
                    }

                    ArrayAdapter arrayAdapter=new ArrayAdapter(cx,R.layout.support_simple_spinner_dropdown_item,bus);

                    spinnerBus.setAdapter(arrayAdapter);

                    progressDialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);

    }
    private void goLogIn()  {
        Intent i = new Intent(this,LogInActivity.class);
        i.putStringArrayListExtra("agenzie",listaAgenzie);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,i,PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.layout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        goLogIn();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        networkState();

    }
    private void networkState()
    {
        connectivityManager=(ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected())
        {
            populateSpinnergenzie();
        }
        else {
            Toast.makeText(this,"In attesa di connessione",Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
                    @Override
                    public void onNetworkActive() {
                        populateSpinnergenzie();
                    }
                });
            }
        }
    }
}
