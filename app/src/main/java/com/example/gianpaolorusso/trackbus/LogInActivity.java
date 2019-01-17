package com.example.gianpaolorusso.trackbus;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;

public class LogInActivity extends AppCompatActivity {
    private String password=null;
    private Button access=null;
    private  Spinner spinnerAgenzie=null;
    private CheckBox ricorda=null;
    private SharedPreferences sharedPreferences=null;
    private  SharedPreferences.Editor editor=null;
    private  String preferenceAgenzia=null;
    private String getPreferencePassword = null;
    private ProgressDialog progressDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        final EditText editPassword=findViewById(R.id.password);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        preferenceAgenzia = sharedPreferences.getString("nomeAgenzia","nessuna");
        getPreferencePassword=sharedPreferences.getString("passwordAgenzia","nessuna");
        spinnerAgenzie=findViewById(R.id.spinnerAgenzie);
        ricorda=findViewById(R.id.ricorda);
        access=findViewById(R.id.accesso);
        editor = sharedPreferences.edit();
        this.setTitle("ACCEDI");
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Caricamento Agenzie");
        progressDialog.show();
        populateSpinner();
        access.setOnClickListener(new View.OnClickListener() {

            ProgressDialog progressDialog = new ProgressDialog(getBaseContext());

            @Override
            public void onClick(View v) {
                if (!editPassword.getText().toString().equals("")) {
                    if (ricorda.isChecked()) {
                        editor.putString("nomeAgenzia",spinnerAgenzie.getSelectedItem().toString()).apply();
                        editor.putString("passwordAgenzia",editPassword.getText().toString()).apply();

                    }

                        String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" + spinnerAgenzie.getSelectedItem().toString() + ".json";
                        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);
                                    password = jsonObject.getString("PASSWORD");
                                    if (editPassword.getText().toString().equals(password)) {
                                        Intent i = new Intent(LogInActivity.this, MenagementActivity.class);
                                        i.putExtra("agenzia", spinnerAgenzie.getSelectedItem().toString());
                                        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                        try {
                                            pendingIntent.send();
                                        } catch (PendingIntent.CanceledException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Password non corretta", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getBaseContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
                            }
                        });
                        requestQueue.add(stringRequest);

                } else {
                    Toast.makeText(getBaseContext(),"Inserire la password",Toast.LENGTH_SHORT).show();

                }
            }

        });

        if(!preferenceAgenzia.equals("nessuna"))
        {
            String url = "https://trackbus-bd0ec.firebaseio.com/Agenzie/" +preferenceAgenzia+ ".json";
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        password = jsonObject.getString("PASSWORD");
                        if (getPreferencePassword.equals(password)) {
                            Intent i = new Intent(LogInActivity.this, MenagementActivity.class);
                            i.putExtra("agenzia",preferenceAgenzia);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Password non corretta", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getBaseContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(stringRequest);

        }

    }
    public  void populateSpinner()
    {
        final ArrayList<String>agenzie=new ArrayList<>();
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://trackbus-bd0ec.firebaseio.com/.json", new Response.Listener<String>() {
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
                    ArrayAdapter arrayAdapter=new ArrayAdapter(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,agenzie);
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
}
