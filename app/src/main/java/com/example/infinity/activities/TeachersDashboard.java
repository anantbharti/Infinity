package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.TeaTestAdapter;
import com.example.infinity.utilities.Statics;
import com.example.infinity.models.Test;
import com.example.infinity.utilities.Update;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TeachersDashboard extends AppCompatActivity {

    RecyclerView recyclerView;
    Button addTest,proceed,cancel;
    TeaTestAdapter adapter;
    FirebaseFirestore database;
    ProgressDialog progressDialog;
    EditText testName,subject,duration;
    LinearLayout newTestDetails;
    String testCode;
    FirebaseAuth auth;
    FirebaseDatabase db;
    List<Test> testList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_dashboard);
        setUpViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        database = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        setRecyclerView();
        Statics.DELETED_TESTS.clear();
        Update update = new Update(TeachersDashboard.this);
        update.checkForUpdate();

        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                addTest.setVisibility(View.GONE);
                newTestDetails.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                addTest.setVisibility(View.VISIBLE);
                newTestDetails.setVisibility(View.GONE);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTestName,testSubject,testDuration;
                newTestName= testName.getText().toString().trim();
                testSubject = subject.getText().toString().trim();
                testDuration = duration.getText().toString().trim();

                if(newTestName.isEmpty()||testSubject.isEmpty()||testDuration.isEmpty()){
                    Toast.makeText(TeachersDashboard.this,"Empty field!",Toast.LENGTH_SHORT).show();
                }
                else{
                    proceedNewTest(newTestName,testSubject,Integer.parseInt(testDuration));
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.my_profile:{
                startActivity(new Intent(TeachersDashboard.this,Profile.class));
                break;
            }
            case R.id.log_out:{
                auth.signOut();
                Toast.makeText(TeachersDashboard.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeachersDashboard.this,MainActivity.class));
                finish();
                break;
            }
            case R.id.help:{
                db.getReference("Help").child("T").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String help = dataSnapshot.getValue().toString();
                        AlertDialog.Builder builder=new AlertDialog.Builder(TeachersDashboard.this);
                        builder.setCancelable(true);
                        builder.setTitle("Help");
                        builder.setMessage(help);
                        builder.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeachersDashboard.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
            case R.id.share_app:{
                db.getReference("ShareApp").child("text").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String share = dataSnapshot.getValue().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, share );
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Share"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeachersDashboard.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            }
            case R.id.menu_refresh:{
                Statics.DELETED_TESTS.clear();
                Toast.makeText(TeachersDashboard.this,"Refreshing...",Toast.LENGTH_SHORT).show();
                setRecyclerView();
                break;
            }
            case R.id.sort:{
                if(testList.isEmpty()){
                    Toast.makeText(TeachersDashboard.this,"Error... Try refreshing!",Toast.LENGTH_SHORT).show();
                }
                else{
                    final String[] sortType={"Date created","Test name"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeachersDashboard.this);
                    builder.setTitle("Sort by");
                    builder.setSingleChoiceItems(sortType, 0, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==1){
                                Collections.sort(testList,Comparator.comparing(obj->obj.getName()));
                            }else {
                                Collections.sort(testList,Comparator.comparing(obj->obj.getDate(),Collections.reverseOrder()));
                            }
                            adapter = new TeaTestAdapter(TeachersDashboard.this,testList);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                    builder.setPositiveButton("Ok", null);
                    builder.setCancelable(true);
                    builder.show();
                }
            }
            case R.id.about_us:{
                startActivity(new Intent(TeachersDashboard.this,About.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database.collection(Statics.TESTS_COLLECTION)
                .whereEqualTo("preparedById",Statics.CUR_USER.getAuthId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                testList = queryDocumentSnapshots.toObjects(Test.class);
                Collections.sort(testList,Comparator.comparing(obj->obj.getDate(),Collections.reverseOrder()));
                adapter = new TeaTestAdapter(TeachersDashboard.this,testList);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeachersDashboard.this,e.getMessage()+" Try refreshing",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void proceedNewTest(String newTestName, String testSubject, int testDuration) {
        progressDialog.show();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        testCode = ""+((int) (Math.random() * (99999999 - 10000000)) + 10000000);

        Test test = new Test(newTestName,testSubject,testDuration,Statics.CUR_USER.getAuthId(),date,testCode);

        database.collection(Statics.TESTS_COLLECTION).document(testCode).set(test).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    HashMap<String, Integer> map = new HashMap<String, Integer>();
                    map.put(Statics.QUESTION_COUNT,0);
                    map.put(Statics.STUDENT_COUNT,0);
                    database.collection(Statics.COUNTERS).document(testCode).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TeachersDashboard.this,"Test added",Toast.LENGTH_SHORT).show();
                                recyclerView.setVisibility(View.VISIBLE);
                                addTest.setVisibility(View.VISIBLE);
                                newTestDetails.setVisibility(View.GONE);
                                Intent intent = new Intent(TeachersDashboard.this,TeacherTest.class);
                                intent.putExtra(Statics.TEST_CODE,testCode);
                                startActivity(intent);
                            }else{
                                Toast.makeText(TeachersDashboard.this,"Error!",Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
//                    Toast.makeText(TeachersDashboard.this,"Test added",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(TeachersDashboard.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to exit ?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TeachersDashboard.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setUpViews(){
        addTest = findViewById(R.id.add_test);
        recyclerView = findViewById(R.id.teachers_test_list);
        newTestDetails = findViewById(R.id.new_test_details);
        testName = findViewById(R.id.new_test_name);
        subject = findViewById(R.id.new_test_subject);
        duration = findViewById(R.id.new_test_duration);
        proceed = findViewById(R.id.new_test_proceed);
        newTestDetails.setVisibility(View.GONE);
        cancel = findViewById(R.id.new_test_cancel);
    }
}