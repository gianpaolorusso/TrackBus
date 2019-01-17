package com.example.gianpaolorusso.trackbus;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SOS_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_);
        Button polizia =findViewById(R.id.polizia);
        Button sos = findViewById(R.id.sos);
        Button carabinieri = findViewById(R.id.carabinieri);
        this.setTitle("SOS");

        polizia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:113"));
                startActivity(i);

            }
        });

       carabinieri.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
               startActivity(i);

           }
       });

       sos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:118"));
               startActivity(i);

           }
       });
    }
}
