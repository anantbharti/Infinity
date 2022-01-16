package com.example.infinity.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinity.R;
import com.example.infinity.models.Question;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;

public class StudentQuestionsAdapter extends FirestoreRecyclerAdapter<Question,StudentQuestionsAdapter.StudentQuestionsViewHolder> {

    Context context;
    HashMap<String,String> responses;
    HashMap<String,String> correctOptions;
    public StudentQuestionsAdapter(@NonNull FirestoreRecyclerOptions<Question> options,Context context) {
        super(options);
        this.context = context;
        this.responses = new HashMap<String, String>();
        this.correctOptions = new HashMap<String, String>();
    }

    public HashMap<String, String> getResponses() {
        return responses;
    }

    public HashMap<String, String> getCorrectOptions() {
        return correctOptions;
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentQuestionsAdapter.StudentQuestionsViewHolder holder, int position, @NonNull Question question) {
        holder.des.setText(question.getQuesNo()+": "+question.getStatement());
        holder.optA.setText("(a) "+question.getOptionA());
        holder.optB.setText("(b) "+question.getOptionB());
        holder.optC.setText("(c) "+question.getOptionC());
        holder.optD.setText("(d) "+question.getOptionD());
        String quesCode = question.getQuesCode();
        String corOpt = question.getCorrectOption();

        try {
            correctOptions.put(quesCode,corOpt);
        }catch (Exception e){

        }

//        holder.des.setText(quesCode+corOpt+" not null");
        if(question.getImageURL()!=null){
            Glide.with(holder.quesImg.getContext()).load(question.getImageURL()).into(holder.quesImg);
            // to zoom image
        }

        holder.radA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"a",Toast.LENGTH_SHORT).show();
                responses.put(question.getQuesCode(),"a");
            }
        });
        holder.radB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"b",Toast.LENGTH_SHORT).show();
                responses.put(question.getQuesCode(),"b");
            }
        });
        holder.radC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"c",Toast.LENGTH_SHORT).show();
                responses.put(question.getQuesCode(),"c");
            }
        });
        holder.radD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"d",Toast.LENGTH_SHORT).show();
                responses.put(question.getQuesCode(),"d");
            }
        });
    }

    @NonNull
    @Override
    public StudentQuestionsAdapter.StudentQuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_questions_list_item,parent,false);
        return new StudentQuestionsAdapter.StudentQuestionsViewHolder(view);
    }

    public class StudentQuestionsViewHolder extends RecyclerView.ViewHolder{
        TextView des,optA,optB,optC,optD;
        ImageView quesImg;
        RadioButton radA,radB,radC,radD;
        public StudentQuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            des = itemView.findViewById(R.id.sql_ques_des);
            optA = itemView.findViewById(R.id.sql_opt_a);
            optB = itemView.findViewById(R.id.sql_opt_b);
            optC = itemView.findViewById(R.id.sql_opt_c);
            optD = itemView.findViewById(R.id.sql_opt_d);
            quesImg = itemView.findViewById(R.id.sql_ques_img);
            radA = itemView.findViewById(R.id.sql_rad_btn_a);
            radB = itemView.findViewById(R.id.sql_rad_btn_b);
            radC = itemView.findViewById(R.id.sql_rad_btn_c);
            radD = itemView.findViewById(R.id.sql_rad_btn_d);
            PhotoViewAttacher attacher;
            attacher = new PhotoViewAttacher(quesImg);
            attacher.update();
        }
    }
}
