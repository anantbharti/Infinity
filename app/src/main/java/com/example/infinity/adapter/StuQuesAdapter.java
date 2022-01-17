package com.example.infinity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinity.R;
import com.example.infinity.models.Question;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import java.util.HashMap;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class StuQuesAdapter extends RecyclerView.Adapter<StuQuesAdapter.StuQuesViewHolder> {

    List<Question> quesList;
    Context context;
    HashMap<String,String> responses;

    public StuQuesAdapter(List<Question> quesList, Context context) {
        this.quesList = quesList;
        this.context = context;
        this.responses = new HashMap<String, String>();
    }

    public HashMap<String, String> getResponses() {
        return responses;
    }

    @NonNull
    @Override
    public StuQuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_questions_list_item,parent,false);
        return new StuQuesAdapter.StuQuesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StuQuesViewHolder holder, int position) {
        Question question = quesList.get(position);

        holder.des.setText(question.getQuesNo()+": "+question.getStatement());
        holder.optA.setText("(a) "+question.getOptionA());
        holder.optB.setText("(b) "+question.getOptionB());
        holder.optC.setText("(c) "+question.getOptionC());
        holder.optD.setText("(d) "+question.getOptionD());


        if(question.getImageURL()!=null){
            Glide.with(holder.quesImg.getContext()).load(question.getImageURL()).into(holder.quesImg);
        }

        holder.radA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responses.put(question.getQuesCode(),"a");
            }
        });
        holder.radB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responses.put(question.getQuesCode(),"b");
            }
        });
        holder.radC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responses.put(question.getQuesCode(),"c");
            }
        });
        holder.radD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responses.put(question.getQuesCode(),"d");
            }
        });

    }

    @Override
    public int getItemCount() {
        return quesList.size();
    }

    public class StuQuesViewHolder extends RecyclerView.ViewHolder {
        TextView des,optA,optB,optC,optD;
        ImageView quesImg;
        RadioButton radA,radB,radC,radD;
        public StuQuesViewHolder(@NonNull View itemView) {
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
