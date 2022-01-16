package com.example.infinity.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinity.R;
import com.example.infinity.activities.TeacherTest;
import com.example.infinity.activities.TestAnalysis;
import com.example.infinity.models.Result;
import com.example.infinity.models.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentResultAdapter extends FirestoreRecyclerAdapter<Result,StudentResultAdapter.StudentResultViewHolder> {

    long quesCnt;
    Context context;
    FirebaseFirestore database;
    public StudentResultAdapter(@NonNull FirestoreRecyclerOptions<Result> options,Context context) {
        super(options);
        this.context = context;
        database = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentResultAdapter.StudentResultViewHolder holder, int position, @NonNull Result result) {
        holder.testName.setText(result.getTestName());
        holder.stuName.setText(result.getStudentName());
        holder.stuRoll.setText("Roll no: "+result.getStudentRoll());
        holder.minCnt.setText("Screen minimized: "+result.getMinimizeCount()+" times");
        holder.date.setText("On "+result.getDate());
        Intent intent = new Intent(context, TestAnalysis.class);

        long quesCnt;
        database.collection(Statics.COUNTERS).document(result.getTestCode()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long quesCnt = documentSnapshot.getLong(Statics.QUESTION_COUNT);
                holder.score.setText("Score: "+result.getScore()+"/"+quesCnt);
                intent.putExtra("quesCnt",""+quesCnt);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                intent.putExtra("quesCnt","");
                holder.score.setText("Score: "+result.getScore());
            }
        });

        holder.analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("result",result);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
    }



    @NonNull
    @Override
    public StudentResultAdapter.StudentResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_result_list_item,parent,false);
        return new StudentResultAdapter.StudentResultViewHolder(view);
    }

    public class StudentResultViewHolder extends RecyclerView.ViewHolder{
        TextView testName,score,stuName,stuRoll,minCnt,date;
        Button analyse;
        public StudentResultViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.sr_li_test_date);
            testName=itemView.findViewById(R.id.sr_li_test_name);
            score=itemView.findViewById(R.id.sr_li_score);
            stuName = itemView.findViewById(R.id.sr_li_stu_name);
            stuRoll = itemView.findViewById(R.id.sr_li_stu_roll);
            minCnt = itemView.findViewById(R.id.sr_li_sc_min_cnt);
            analyse = itemView.findViewById(R.id.sr_li_analyse);

        }
    }
}
