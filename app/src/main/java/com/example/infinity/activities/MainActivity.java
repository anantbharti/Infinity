package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.fragments.LogIn;
import com.example.infinity.utilities.Statics;
import com.example.infinity.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private DocumentReference documentReference;
    private FirebaseUser firebaseUser;
    ProgressBar progressBar;
    ImageView inf;
    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        database = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.welcome_progress_bar);
        inf = findViewById(R.id.inf_logo);
        welcome = findViewById(R.id.welcome_txt);

        FirebaseMessaging.getInstance().subscribeToTopic("Notification");

        proceed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to exit ?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void proceed(){
        if(firebaseUser==null){
            startLogInFragment();
        }
        else{
//            progressDialog.show();
            documentReference = database.collection(Statics.USERS_COLLECTION).document(auth.getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        progressDialog.dismiss();
                    if (documentSnapshot == null) {
                        startLogInFragment();
                    } else {
                        Statics.CUR_USER=documentSnapshot.toObject(User.class);
                        startUserDashboard();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
                    startLogInFragment();
                }
            });
        }
    }

    private void startUserDashboard(){
        if (Statics.CUR_USER.getUserType().equals(Statics.STUDENT)) {
//                            Toast.makeText(MainActivity.this,"Student",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, StudentDashboard.class));
        } else {
//                            Toast.makeText(MainActivity.this,"Teacher",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, TeachersDashboard.class));
        }
        finish();
    }

    private void startLogInFragment(){
        welcome.setVisibility(View.GONE);
        inf.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.main_activity,new LogIn());
        ft.commit();
    }
}