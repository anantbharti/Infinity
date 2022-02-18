package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.StuResultAdapter;
import com.example.infinity.models.Result;
import com.example.infinity.utilities.Statics;
import com.example.infinity.models.Test;
import com.example.infinity.utilities.Update;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView recyclerView;
//    StudentResultAdapter studentResultAdapter;
    FirebaseFirestore database;
    Button enter,start,cancel;
    EditText test_code_ed;
    TextView testName,testSubject,testDuration,testInstructions,yt;
    CardView cardView,cardViewIns;
    RelativeLayout relativeLayout;
    Test test;
    ProgressDialog progressDialog;
    FirebaseDatabase  db;
    List<Result> results;
    StuResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        setUpViews();
        auth= FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        setRecyclerView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        db = FirebaseDatabase.getInstance();

        Update update = new Update(StudentDashboard.this);
        update.checkForUpdate();


        db.getReference("Instructions").child("text").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                try{
                    String ins = dataSnapshot.getValue().toString();
                    testInstructions.setText(ins);
                }catch (Exception e){}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentDashboard.this,"Error in loading instructions!",Toast.LENGTH_SHORT).show();
            }
        });


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testCode = test_code_ed.getText().toString().trim();
                if(testCode.isEmpty()){
                    Toast.makeText(StudentDashboard.this,"Enter test code!",Toast.LENGTH_SHORT).show();
                }else{
                    validate(testCode);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView.setVisibility(View.GONE);
                cardViewIns.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                database.collection(Statics.ATTEMPTS_COLLECTION)
                        .document(test.getTestCode()+Statics.CUR_USER.getAuthId())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       String attempted =  documentSnapshot.getString(Statics.ATTEMPTS_COLLECTION);
                        progressDialog.dismiss();
                        if(attempted == null){
                            Intent intent = new Intent(StudentDashboard.this,StudentTest.class);
                            intent.putExtra("Test",test);
                            startActivity(intent);
                            finish();
                       }
                       else{
                           cardView.setVisibility(View.GONE);
                           cardViewIns.setVisibility(View.GONE);
                           relativeLayout.setVisibility(View.VISIBLE);
                           Toast.makeText(StudentDashboard.this,"Test already attempted!",Toast.LENGTH_SHORT).show();
                       }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(StudentDashboard.this,"Error...Try again!",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void validate(String testCode) {
        database.collection(Statics.TESTS_COLLECTION).document(testCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                test = documentSnapshot.toObject(Test.class);
                if(test==null){
                    Toast.makeText(StudentDashboard.this,"Invalid code!",Toast.LENGTH_SHORT).show();
                }
                else{
                    showTestDetails();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentDashboard.this,"Error!",Toast.LENGTH_SHORT).show();
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
                            StudentDashboard.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showTestDetails() {
        relativeLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        cardViewIns.setVisibility(View.VISIBLE);
        testName.setText(test.getName());
        testDuration.setText("Time: "+test.getDuration()+" mins");
        testSubject.setText(test.getSubject());
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database.collection(Statics.RESULT_COLLECTION)
                .whereEqualTo("studentId",Statics.CUR_USER.getAuthId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                results = queryDocumentSnapshots.toObjects(Result.class);
                Collections.sort(results,Comparator.comparing(obj->obj.getDate(),Collections.reverseOrder()));
                adapter = new StuResultAdapter(results,StudentDashboard.this);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentDashboard.this,e.getLocalizedMessage()+" Try refreshing",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpViews() {
        recyclerView = findViewById(R.id.student_result_list);
        testName = findViewById(R.id.sd_test_name);
        testSubject = findViewById(R.id.sd_test_subject);
        testDuration = findViewById(R.id.sd_test_duration);
        enter = findViewById(R.id.enter_test);
        start = findViewById(R.id.start_test);
        test_code_ed = findViewById(R.id.enter_test_code);
        relativeLayout = findViewById(R.id.rl_result);
        cardView = findViewById(R.id.sd_td_cv);
        cancel = findViewById(R.id.cancel_test);
        cardView.setVisibility(View.GONE);
        cardViewIns = findViewById(R.id.sd_instructions_cv);
        testInstructions = findViewById(R.id.tv_ins);
        yt = findViewById(R.id.stu_dashboard_text);
        cardViewIns.setVisibility(View.GONE);

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
                startActivity(new Intent(StudentDashboard.this,Profile.class));
                break;
            }
            case R.id.log_out:{
                auth.signOut();
                Toast.makeText(StudentDashboard.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentDashboard.this,MainActivity.class));
                finish();
                break;
            }
            case R.id.help:{
                db.getReference("Help").child("S").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String help = dataSnapshot.getValue().toString();
                        AlertDialog.Builder builder=new AlertDialog.Builder(StudentDashboard.this);
                        builder.setCancelable(true);
                        builder.setTitle("Help");
                        builder.setMessage(help);
                        builder.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentDashboard.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentDashboard.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            }
            case R.id.menu_refresh:{
                Toast.makeText(StudentDashboard.this,"Refreshing...",Toast.LENGTH_SHORT).show();
                setRecyclerView();
                break;
            }
            case R.id.sort:{
                if(results.isEmpty()){
                    Toast.makeText(StudentDashboard.this,"Error... Try refreshing!",Toast.LENGTH_SHORT).show();
                }
                else{
                    final String[] sortType={"Date attempted","Test name"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboard.this);
                    builder.setTitle("Sort by");
                    builder.setSingleChoiceItems(sortType, 0, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==1){
                                Collections.sort(results,Comparator.comparing(obj->obj.getTestName()));
                            }else {
                                Collections.sort(results,Comparator.comparing(obj->obj.getDate(),Collections.reverseOrder()));
                            }
                            adapter = new StuResultAdapter(results,StudentDashboard.this);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                    builder.setPositiveButton("Done", null);
                    builder.setCancelable(true);
                    builder.show();
                }
                break;
            }
            case R.id.about_us:{
                startActivity(new Intent(StudentDashboard.this,About.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}