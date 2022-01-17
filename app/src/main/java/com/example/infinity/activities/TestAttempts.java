package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.StuResultAdapter;
import com.example.infinity.models.Result;
import com.example.infinity.models.Statics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TestAttempts extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView testName,testSub,noOfStu;
    FirebaseFirestore database;
    String testCode;
    Button refresh,sort;
    StuResultAdapter adapter;
    List<Result> results;
    int sortByScore = 1;
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

        showCount();
        setRecyclerView();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestAttempts.this,"Refreshing...",Toast.LENGTH_SHORT).show();
                showCount();
                setRecyclerView();
            }
        });
    }

    private void showCount() {
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

        sort.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(!results.isEmpty())
                {
                    if(sortByScore%2==1){
                        sort.setText("Sort by names");
                        Collections.sort(results, Comparator.comparing(obj->obj.getScore()));
                    } else{
                        sort.setText("Sort by scores");
                        Collections.sort(results, Comparator.comparing(obj->obj.getStudentName()));
                    }
                    sortByScore++;
                    adapter = new StuResultAdapter(results,TestAttempts.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database.collection(Statics.RESULT_COLLECTION)
                .whereEqualTo(Statics.TEST_CODE,testCode).limit(200)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                results = queryDocumentSnapshots.toObjects(Result.class);
                Collections.sort(results, Comparator.comparing(obj->obj.getStudentName()));
                adapter = new StuResultAdapter(results,TestAttempts.this);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                yt.setText(e.getMessage());
                Log.e(TAG, e.getLocalizedMessage());
                Toast.makeText(TestAttempts.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpViews() {
        recyclerView = findViewById(R.id.attempted_students_list);
        testName = findViewById(R.id.tat_test_name);
        testSub = findViewById(R.id.tat_sub);
        noOfStu = findViewById(R.id.tat_no_of_stu);
        refresh = findViewById(R.id.ta_refresh);
        sort = findViewById(R.id.sort_attempts);
    }

}