package com.example.infinity.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.infinity.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class Update {

    FirebaseDatabase db;
    Context context;

    public Update() {
    }

    public Update(Context context) {
        this.context = context;
        this.db = FirebaseDatabase.getInstance();
    }

    public void checkForUpdate(){
        db.getReference("Must").child("update").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String up = dataSnapshot.getValue().toString(); // No when not must else mandatory version name

                if(up.equals("No"))
                    return;

                String version ="";
                try {
                    version = BuildConfig.VERSION_NAME;
                }catch (Exception e){}

                if(up.equals(version))
                    return;

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setTitle("Update required!");
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.getReference("About").child("updateLink").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                String url = dataSnapshot.getValue().toString();
                                Uri uri = Uri.parse(url);
                                context.startActivity(new Intent(Intent.ACTION_VIEW,uri));
                                ((Activity)context).finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Error! Try again",Toast.LENGTH_SHORT).show();
                                ((Activity)context).finish();
                            }
                        });
                    }
                }).setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Activity)context).finish();
                    }
                });
                builder.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Check for updates!",Toast.LENGTH_SHORT).show();
            }
        });
        return ;
    }
}
