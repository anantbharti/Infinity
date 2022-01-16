package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.StudentResultAdapter;
import com.example.infinity.models.Result;
import com.example.infinity.models.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TestAttempts extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView testName,testSub,noOfStu;
    StudentResultAdapter studentResultAdapter;
    FirebaseFirestore database;
    String testCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_attempts);
        setUpViews();
        getSupportActionBar().hide();
        testCode = getIntent().getExtras().getString(Statics.TEST_CODE);

        testName.setText(getIntent().getExtras().getString("testName"));
        testSub.setText("Subject: "+getIntent().getExtras().getString("subject"));
        database = FirebaseFirestore.getInstance();

        database.collection(Statics.COUNTERS).document(testCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String str = documentSnapshot.get(Statics.STUDENT_COUNT)+" students attempted";
                noOfStu.setText(str);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TestAttempts.this,"Error!",Toast.LENGTH_SHORT).show();
            }
        });
        setRecyclerView();

    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = database.collection(Statics.RESULT_COLLECTION)
                .whereEqualTo(Statics.TEST_CODE,testCode)
                .orderBy("date")
                .orderBy("testName");
        FirestoreRecyclerOptions<Result> options = new FirestoreRecyclerOptions.Builder<Result>()
                .setQuery(query,Result.class)
                .build();
        studentResultAdapter = new StudentResultAdapter(options,this);
        recyclerView.setAdapter(studentResultAdapter);
        studentResultAdapter.startListening();
    }
    private void setUpViews() {
        recyclerView = findViewById(R.id.attempted_students_list);
        testName = findViewById(R.id.tat_test_name);
        testSub = findViewById(R.id.tat_sub);
        noOfStu = findViewById(R.id.tat_no_of_stu);
    }

}