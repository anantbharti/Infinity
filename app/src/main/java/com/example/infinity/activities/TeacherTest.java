package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.TeacherQuestionsAdapter;
import com.example.infinity.fragments.LogIn;
import com.example.infinity.models.Question;
import com.example.infinity.models.Statics;
import com.example.infinity.models.Test;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TeacherTest extends AppCompatActivity {

    public String testCode;
    RecyclerView recyclerView;
    private TextView testName,testSubject,testDuration,tvTestCode,testDate;
    Button addQues,seeWhoAttempted,editTest,deleteTest,copyCode,editTestCancel,editTestSave;
    FirebaseFirestore database;
    DocumentReference documentReference;
    Test test;
    CardView editTestCv;
    EditText editTestName,editTestSubject,editTestDuration;
    TeacherQuestionsAdapter questionsAdapter;
    ProgressDialog progressDialog;
    String editable = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_test);
        setUpViews();
        getSupportActionBar().hide();
        testCode=getIntent().getExtras().getString(Statics.TEST_CODE);
        database = FirebaseFirestore.getInstance();

        documentReference = database.collection(Statics.TESTS_COLLECTION).document(testCode);
        progressDialog  = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    test = documentSnapshot.toObject(Test.class);
                    showTestDetails();
                }
                else{
                    Toast.makeText(TeacherTest.this,"Error in loading data!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherTest.this,"Error in loading data!",Toast.LENGTH_SHORT).show();
            }
        });

        database.collection(Statics.COUNTERS).document(testCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                editable = String.valueOf(documentSnapshot.getLong(Statics.STUDENT_COUNT));
                setRecyclerView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setRecyclerView();
                Toast.makeText(TeacherTest.this,"Something went wrong!",Toast.LENGTH_SHORT);
            }
        });

        addQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editable.equals("0")) {
                    Intent intent = new Intent(TeacherTest.this, EditQuestion.class);
                    intent.putExtra(Statics.TEST_CODE, testCode);
                    intent.putExtra("Test", test);
                    intent.putExtra("editable",editable);
                    Question q = null;
                    intent.putExtra("Question", q);
                    startActivity(intent);
                }else{
                    Toast.makeText(TeacherTest.this,"Test attempted! Can't add question",Toast.LENGTH_SHORT).show();
                }
            }
        });
        seeWhoAttempted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherTest.this,TestAttempts.class);
                intent.putExtra(Statics.TEST_CODE,testCode);
                intent.putExtra("subject",test.getSubject());
                intent.putExtra("testName",test.getName());
                startActivity(intent);
            }
        });

        copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText(Statics.TEST_CODE,testCode);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(TeacherTest.this,"Test code copied",Toast.LENGTH_SHORT).show();
            }
        });

        deleteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTestFun();
            }
        });

        editTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editable.equals("0")) {
                    recyclerView.setVisibility(View.GONE);
                    addQues.setVisibility(View.GONE);
                    editTestCv.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(TeacherTest.this,"Test attempted! Can't be edited",Toast.LENGTH_SHORT).show();
                }
            }
        });

        editTestCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                addQues.setVisibility(View.VISIBLE);
                editTestCv.setVisibility(View.GONE);
            }
        });

        editTestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etName = editTestName.getText().toString();
                String etSub = editTestSubject.getText().toString();
                String etDur = editTestDuration.getText().toString();
                if(etDur.isEmpty()||etName.isEmpty()||etSub.isEmpty()){
                    Toast.makeText(TeacherTest.this,"Empty field!",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Saving...");
                    progressDialog.show();
                    int dur = Integer.parseInt(etDur);
                    test.setDuration(dur);
                    test.setName(etName);
                    test.setSubject(etSub);
                    database.collection(Statics.TESTS_COLLECTION).document(testCode).set(test).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(TeacherTest.this,"Saved successfully",Toast.LENGTH_SHORT).show();
                                recyclerView.setVisibility(View.VISIBLE);
                                addQues.setVisibility(View.VISIBLE);
                                editTestCv.setVisibility(View.GONE);
                                showTestDetails();
                            }else{
                                Toast.makeText(TeacherTest.this,"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    private void deleteTestFun() {
        new AlertDialog.Builder(TeacherTest.this)
                .setMessage("Do you want to delete the test ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.setMessage("Deleting...");
                        progressDialog.show();
                        database.collection(Statics.TESTS_COLLECTION).document(testCode).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(TeacherTest.this,"Test deleted successfully",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(TeacherTest.this,"Error!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    private void setRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Query query = database.collection(Statics.QUESTIONS_COLLECTION)
                .whereEqualTo(Statics.TEST_CODE,testCode)
                .orderBy("quesNo");
        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query,Question.class)
                .build();
        questionsAdapter = new TeacherQuestionsAdapter(options,TeacherTest.this,editable);
        recyclerView.setAdapter(questionsAdapter);
        questionsAdapter.startListening();
    }

    private void showTestDetails() {
        tvTestCode.setText("Test code: "+testCode);
        testName.setText(test.getName());
        testDuration.setText("Duration: "+test.getDuration()+" minutes");
        testSubject.setText("Subject: "+test.getSubject());
        testDate.setText("Added on: "+test.getDate());
        editTestName.setText(test.getName());
        editTestSubject.setText(test.getSubject());
        editTestDuration.setText(""+test.getDuration());
    }

    private void setUpViews() {
        recyclerView = findViewById(R.id.teacher_questions_list);
        addQues = findViewById(R.id.add_question);
        testName = findViewById(R.id.ttd_test_name);
        testSubject = findViewById(R.id.ttd_test_subject);
        tvTestCode =findViewById(R.id.ttd_test_code);
        testDate = findViewById(R.id.ttd_test_date);
        testDuration = findViewById(R.id.ttd_test_duration);
        seeWhoAttempted = findViewById(R.id.see_students_attempted);
        editTest = findViewById(R.id.tt_edit_btn);
        deleteTest = findViewById(R.id.tt_delete_btn);
        copyCode = findViewById(R.id.ttd_code_copy);
        editTestCancel = findViewById(R.id.edit_test_cancel);
        editTestSave = findViewById(R.id.edit_test_save);
        editTestName = findViewById(R.id.edit_test_name);
        editTestCv = findViewById(R.id.tt_edit_cv);
        editTestSubject = findViewById(R.id.edit_test_subject);
        editTestDuration = findViewById(R.id.edit_test_duration);
        editTestCv.setVisibility(View.GONE);
    }

}