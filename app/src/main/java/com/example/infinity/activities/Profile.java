package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.models.Statics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    LinearLayout details,edit;
    TextView tvName,tvDes,tvRoll,tvSchool,tvEmail;
    EditText edName,edSub,edStd,edRoll,edSchool;
    Button update,cancel,save;
    FirebaseAuth auth;
    FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        setUpViews();
        showDetails();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edName.getText().toString();
                String school = edSchool.getText().toString();
                String des,roll;
                if(Statics.CUR_USER.getUserType().equals(Statics.STUDENT)) {
                    des = edStd.getText().toString();
                    roll = edRoll.getText().toString();
                }
                else {
                    des = edSub.getText().toString();
                    roll = "0";
                }
                if(name.isEmpty()||school.isEmpty()||des.isEmpty()||roll.isEmpty()){
                    Toast.makeText(Profile.this,"Empty field!",Toast.LENGTH_SHORT).show();
                }else{
                    Statics.CUR_USER.setName(name);
                    Statics.CUR_USER.setDes(des);
                    Statics.CUR_USER.setSchool(school);
                    Statics.CUR_USER.setRollNo(Integer.parseInt(roll));
                    updateDetails();
                }
            }
        });
    }

    private void updateDetails(){
        ProgressDialog progressDialog = new ProgressDialog(Profile.this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        database.collection(Statics.USERS_COLLECTION).document(Statics.CUR_USER.getAuthId())
                .set(Statics.CUR_USER).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                showDetails();
                edit.setVisibility(View.GONE);
                details.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    Toast.makeText(Profile.this,"Profile updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Profile.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDetails() {
        edName.setText(Statics.CUR_USER.getName());
        edSchool.setText(Statics.CUR_USER.getSchool());
        tvName.setText(Statics.CUR_USER.getName());
        tvSchool.setText(Statics.CUR_USER.getSchool());
        tvEmail.setText("Email id: "+Statics.CUR_USER.getEmail());

        if(Statics.CUR_USER.getUserType().equals(Statics.STUDENT)){
            tvDes.setText("Class: "+Statics.CUR_USER.getDes());
            tvRoll.setText("Roll no: "+Statics.CUR_USER.getRollNo());
            edRoll.setText(Statics.CUR_USER.getRollNo()+"");
            edStd.setText(Statics.CUR_USER.getDes());
            edSub.setVisibility(View.GONE);
        }
        else{
            tvDes.setText("Subject: "+Statics.CUR_USER.getDes());
            edSub.setText(Statics.CUR_USER.getDes());
            tvRoll.setVisibility(View.GONE);
            edStd.setVisibility(View.GONE);
            edRoll.setVisibility(View.GONE);
        }
    }

    private void setUpViews() {
        details = findViewById(R.id.profile_details_ll);
        edit = findViewById(R.id.profile_edit_ll);
        tvName = findViewById(R.id.profile_details_name);
        tvDes = findViewById(R.id.profile_details_des);
        tvRoll = findViewById(R.id.profile_details_roll);
        tvSchool = findViewById(R.id.profile_details_school);
        tvEmail = findViewById(R.id.profile_details_email);
        edName = findViewById(R.id.profile_edit_name);
        edSub = findViewById(R.id.profile_edit_subject);
        edStd = findViewById(R.id.profile_edit_class);
        edRoll = findViewById(R.id.profile_edit_roll);
        edSchool = findViewById(R.id.profile_edit_school);
        update = findViewById(R.id.profile_details_update);
        cancel = findViewById(R.id.profile_edit_cancel);
        save = findViewById(R.id.prfile_edit_save);
        edit.setVisibility(View.GONE);
    }

}