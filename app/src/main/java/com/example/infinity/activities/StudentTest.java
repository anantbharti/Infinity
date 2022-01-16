package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.StudentQuestionsAdapter;
import com.example.infinity.adapter.StudentResultAdapter;
import com.example.infinity.adapter.TeacherQuestionsAdapter;
import com.example.infinity.models.Question;
import com.example.infinity.models.Result;
import com.example.infinity.models.Statics;
import com.example.infinity.models.Test;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StudentTest extends AppCompatActivity {

    Test test;
    FirebaseFirestore database;
    TextView testName,testSubject,timeRem;
    Button submitBtn;
    RecyclerView recyclerView;
    StudentQuestionsAdapter questionsAdapter;
    HashMap<String,String> responses,correctOptions;
    int score=0;
    Result result;
    int minimizeCount=0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_test);
        test = (Test) getIntent().getSerializableExtra("Test");
        database = FirebaseFirestore.getInstance();
        setUpViews();
        getSupportActionBar().hide();
        setRecyclerView();
        responses = new HashMap<String, String>();
        correctOptions = new HashMap<String, String>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait... Don't exit!");
        progressDialog.setCancelable(false);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(StudentTest.this)
                        .setMessage("Do you want to submit ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                endTest();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        new CountDownTimer((test.getDuration())*60*1000+15000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeRem.setText((millisUntilFinished / 1000)/60+"m "+(millisUntilFinished/1000)%60+"s");
            }
            public void onFinish() {
                endTest();
            }
        }.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure ?\nYour test will be submitted !")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            endTest();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void endTest() {
        progressDialog.show();
        responses = questionsAdapter.getResponses();
        correctOptions = questionsAdapter.getCorrectOptions();
        for (HashMap.Entry<String, String> element : correctOptions.entrySet()) {
            if(responses.containsKey(element.getKey())){
                if(responses.get(element.getKey()).equals(element.getValue()))
                    score++;
            }
        }
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        result = new Result(test.getTestCode(),Statics.CUR_USER.getAuthId(),Statics.CUR_USER.getName(),Statics.CUR_USER.getRollNo(),date,test.getName(),score,minimizeCount);
        database.collection(Statics.RESULT_COLLECTION).document(test.getTestCode()+Statics.CUR_USER.getAuthId())
                .set(result).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    database.collection(Statics.RESPONSE_COLLECTION)
                            .document(test.getTestCode()+Statics.CUR_USER.getAuthId())
                            .set(responses).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                updateCounter();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(StudentTest.this,"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(StudentTest.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCounter() {
        database.collection(Statics.COUNTERS).document(test.getTestCode()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    long quesCnt = documentSnapshot.getLong(Statics.STUDENT_COUNT);
                    database.collection(Statics.COUNTERS).document(test.getTestCode())
                            .update(Statics.STUDENT_COUNT,quesCnt+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(StudentTest.this,"Test submitted successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(StudentTest.this,StudentDashboard.class));
                                finish();
                            }else{
                                Toast.makeText(StudentTest.this,"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(StudentTest.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(StudentTest.this,"Error!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStop(){
        super.onStop();
        minimizeCount++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(StudentTest.this,"Warning!...Don't minimize app",Toast.LENGTH_SHORT).show();
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Query query = database.collection(Statics.QUESTIONS_COLLECTION)
                .whereEqualTo(Statics.TEST_CODE,test.getTestCode())
                .orderBy("quesNo");
        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query,Question.class)
                .build();
        questionsAdapter = new StudentQuestionsAdapter(options,StudentTest.this);
        recyclerView.setAdapter(questionsAdapter);
        questionsAdapter.startListening();

    }

    private void setUpViews() {
        testName = findViewById(R.id.student_test_name);
        testName.setText(test.getName());
        testSubject = findViewById(R.id.stu_test_subject);
        testSubject.setText(test.getSubject());
        timeRem = findViewById(R.id.test_time_rem);
        timeRem.setText(test.getDuration()+"mins rem");
        submitBtn = findViewById(R.id.submit_btn);
        recyclerView= findViewById(R.id.st_ques_list);
    }
}