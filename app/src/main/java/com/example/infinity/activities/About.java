package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.BuildConfig;
import com.example.infinity.R;
import com.example.infinity.adapter.StuResultAdapter;
import com.example.infinity.models.AboutUs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class About extends AppCompatActivity {

    FirebaseDatabase db;
    AboutUs aboutUs;
    TextView aboutText;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        update = findViewById(R.id.check_for_update);
        aboutText = findViewById(R.id.about_us_text);
        update.setVisibility(View.GONE);
        db = FirebaseDatabase.getInstance();
        aboutUs = new AboutUs();


        db.getReference("About").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot!=null) {
//                    aboutText.setText(snapshot.getValue().toString());
                    aboutUs = snapshot.getValue(AboutUs.class);
                    if(aboutUs!=null){
                        aboutText.setText(aboutUs.getText());
                        String version ="";
                        try {
                            version = BuildConfig.VERSION_NAME;
                        }catch (Exception e){
                        }
                        if(!aboutUs.getVersion().equals(version)){
                            update.setVisibility(View.VISIBLE);
//                            aboutText.setText(version);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                aboutUs.setText("Unable to load data!");
                Toast.makeText(About.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!aboutUs.getUpdateLink().isEmpty()){
                    Uri uri = Uri.parse(aboutUs.getUpdateLink());
                    startActivity(new Intent(Intent.ACTION_VIEW,uri));
                }else{
                    Toast.makeText(About.this,"Already latest version",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}