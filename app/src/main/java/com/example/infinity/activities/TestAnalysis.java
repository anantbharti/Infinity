package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.adapter.AnalysisAdapter;
import com.example.infinity.adapter.StudentQuestionsAdapter;
import com.example.infinity.models.Question;
import com.example.infinity.models.Result;
import com.example.infinity.models.Statics;
import com.example.infinity.models.Test;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TestAnalysis extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore database;
    AnalysisAdapter analysisAdapter;
    Result result;
    String qCnt;
    TextView testName,score,stuName,stuRoll,minCnt,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_analysis);
        getSupportActionBar().hide();
        result = (Result) getIntent().getSerializableExtra("result");
        qCnt = getIntent().getExtras().getString("quesCnt");
        setUpViews();
        database = FirebaseFirestore.getInstance();

        database.collection(Statics.RESPONSE_COLLECTION).document(result.getTestCode()+result.getStudentId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setRecyclerView(documentSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TestAnalysis.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpViews() {
        recyclerView = findViewById(R.id.analysis_question_list);
        date = findViewById(R.id.ta_test_date);
        testName=findViewById(R.id.ta_test_name);
        score=findViewById(R.id.ta_score);
        stuName = findViewById(R.id.ta_stu_name);
        stuRoll = findViewById(R.id.ta_stu_roll);
        minCnt = findViewById(R.id.ta_sc_min_cnt);

        date.setText("On "+result.getDate());
        testName.setText(result.getTestName());
        if(qCnt.equals(""))
        score.setText("Score: "+result.getScore());
        else
            score.setText("Score: "+result.getScore()+"/"+qCnt);
        stuName.setText(result.getStudentName());
        stuRoll.setText("Roll no: "+result.getStudentRoll());
        minCnt.setText("Screen minimized: "+result.getMinimizeCount()+" times");
    }

    private void setRecyclerView(DocumentSnapshot documentSnapshot) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Query query = database.collection(Statics.QUESTIONS_COLLECTION)
                .whereEqualTo(Statics.TEST_CODE,result.getTestCode())
                .orderBy("quesNo");
        FirestoreRecyclerOptions<Question> options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(query,Question.class)
                .build();
        analysisAdapter = new AnalysisAdapter(options,documentSnapshot);
        recyclerView.setAdapter(analysisAdapter);
        analysisAdapter.startListening();
    }
}