package com.example.gianpaolorusso.trackbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MenagementActivity extends AppCompatActivity {

    private Button btnTrack=null;
    private Button btnSos=null;
    private Button btnNote=null;
    private BusClass bus=null;
    private String numberBus=null;
    private boolean logBus = false;
    private Spinner  spinnerBus=null;
    private BusClass busClass=null;
    private Button btnLog = null;
    private RelativeLayout layoutLog=null;
    private TextView labelSpinner = null;
    private RelativeLayout commandLayout=null;
    private MenuItem menuItem =null;
    private String agenzia=null;
    private FirebaseDatabase database=null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        agenzia=getIntent().getStringExtra("agenzia");
        this.setTitle(agenzia);
        spinnerBus=findViewById(R.id.listaBus);
        btnNote=findViewById(R.id.Comunicazioni);
        labelSpinner=findViewById(R.id.labelSpinner);
        btnSos=findViewById(R.id.SOS);
        btnLog=findViewById(R.id.btnLog);
        btnTrack=findViewById(R.id.track);
        commandLayout= findViewById(R.id.commandLayout);
        layoutLog=findViewById(R.id.layoutLog);
        commandLayout.setVisibility(View.GONE);

        database=FirebaseDatabase.getInstance();
        populateBus();
            btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerBus.getSelectedItem().toString().equals("Nessun bus disponibile"))
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(MenagementActivity.this);
                    builder.setTitle("Sloggare prima un bus per renderlo disponibile");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                   AlertDialog alertDialog=builder.create();
                   alertDialog.show();
                }
                if(spinnerBus.getSelectedItem().toString().equals(""))
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(MenagementActivity.this);
                    builder.setTitle("Seleziona un bus dalla lista");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
                else
                {
                    menuItem.setVisible(true);
                    layoutLog.setVisibility(View.GONE);
                    commandLayout.setVisibility(View.VISIBLE);
                    labelSpinner.setText("BUS "+numberBus);
                    menuItem.setVisible(true);
                    String url= "https://trackbus-bd0ec.firebaseio.com/Agenzie/"+agenzia+"/BUS"+numberBus;
                    DatabaseReference ref=database.getReferenceFromUrl(url);
                    ref.child("LOG").setValue("SI");


                }
            }
        });

        spinnerBus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberBus=spinnerBus.getItemAtPosition(position).toString();
                labelSpinner.setText("BUS "+numberBus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=MenagementActivity.this.getTitle().toString();
                Intent i = new Intent(MenagementActivity.this,TrackActivity.class);
                i.putExtra("bus",numberBus);
                i.putExtra("agenzia",agenzia);
                startActivityForResult(i,1);
            }
        });

        btnSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenagementActivity.this,SOS_Activity.class);
                startActivityForResult(i,1);


            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenagementActivity.this,NoteActivity.class);
                i.putExtra("agenzia",agenzia);
                i.putExtra("bus",spinnerBus.getSelectedItem().toString());
                startActivityForResult(i,1);
            }
        });

    }
    private void populateBus()
    {
        final ArrayList<String> bus=new ArrayList<>();
        bus.add("");
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String url="https://trackbus-bd0ec.firebaseio.com/Agenzie/"+agenzia+".json";
        StringRequest stringRequest= new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
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
                            String log=jsonObject1.getString("LOG");
                            if(log.equals("NO")) {
                                bus.add(jsonObject1.getString("N°"));
                            }
                        }
                    }
                    if(bus.size()==0)
                    {
                        bus.add("Nessun bus disponibile");
                    }

                    ArrayAdapter arrayAdapter=new ArrayAdapter(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,bus);

                    spinnerBus.setAdapter(arrayAdapter);


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

    private void logOutUser()
    {
        logOutBus();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("nomeAgenzia","nessuna").commit();
        sharedPreferences.edit().putString("passwordAgenzia","nessuna").commit();
        Intent i = new Intent(this,LogInActivity.class );
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,i,PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }

    private void logOutBus()
    {
        layoutLog.setVisibility(View.VISIBLE);
        commandLayout.setVisibility(View.GONE);
        labelSpinner.setText("BUS");
        menuItem.setVisible(false);

        if(!numberBus.equals("") && !numberBus.equals("Nessun bus disponibile")) {
            String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + agenzia + "/BUS" + numberBus;
            DatabaseReference ref = database.getReferenceFromUrl(url);
            ref.child("LOG").setValue("NO");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.layout_logout_menu,menu);
        menuItem=menu.getItem(0);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("LOGOUT BUS"))
        {
            logOutBus();
        }
        if(item.getTitle().equals("LOGOUT USER"))
        {
            logOutUser();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateBus();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Fai logout per uscire",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOutBus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        populateBus();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==1 && requestCode==1) {
            agenzia=data.getStringExtra("agenzia");
            numberBus=data.getStringExtra("bus");
            layoutLog.setVisibility(View.GONE);
            commandLayout.setVisibility(View.VISIBLE);
            labelSpinner.setText("BUS " + numberBus);
            menuItem.setVisible(true);
            String url= "https://trackbus-bd0ec.firebaseio.com/Agenzie/"+agenzia+"/BUS"+numberBus+"/";
            DatabaseReference ref=database.getReferenceFromUrl(url);
            ref.child("LOG").setValue("SI");
        }
    }
    private String getNumberToTitle(String title)
    {
        String number="";
        for(int index=title.length(); index==0; index--)
        {
            if(title.indexOf(index)=='S'|| title.indexOf(index)==' ' )
            {
                break;
            }
            number=number+title.indexOf(index);
        }
        return number;
    }
}
