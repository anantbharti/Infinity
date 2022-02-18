package com.example.infinity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinity.R;
import com.example.infinity.activities.TestAnalysis;
import com.example.infinity.models.Result;
import com.example.infinity.utilities.Statics;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StuResultAdapter extends RecyclerView.Adapter<StuResultAdapter.StuResultViewHolder> {

    List<Result> resultList;
    Context context;
    FirebaseFirestore database;

    public StuResultAdapter(List<Result> resultList, Context context) {
        this.resultList = resultList;
        this.context = context;
        this.database = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public StuResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_result_list_item,parent,false);
        return new StuResultAdapter.StuResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StuResultViewHolder holder, int position) {

        Result result = resultList.get(position);

        holder.testName.setText(result.getTestName());
        holder.stuName.setText(result.getStudentName());
        holder.stuRoll.setText("Roll no: "+result.getStudentRoll());
        holder.minCnt.setText("Screen minimized: "+result.getMinimizeCount()+" times");
        holder.date.setText("On "+result.getDate());
        holder.score.setText("Score: "+result.getScore());
        if(Statics.CUR_USER.getUserType().equals(Statics.TEACHER)){
            holder.testName.setVisibility(View.GONE);
        }
        Intent intent = new Intent(context, TestAnalysis.class);

        holder.analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("result",result);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class StuResultViewHolder extends RecyclerView.ViewHolder {
        TextView testName,score,stuName,stuRoll,minCnt,date;
        Button analyse;
        public StuResultViewHolder(@NonNull View itemView) {
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
