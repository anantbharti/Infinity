package com.example.infinity.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinity.R;
import com.example.infinity.models.Question;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Queue;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TestAnalysisAdapter extends RecyclerView.Adapter< TestAnalysisAdapter.TestAnalysisViewHolder> {

    DocumentSnapshot documentSnapshot;
    List<Question> questionList;

    public TestAnalysisAdapter(DocumentSnapshot documentSnapshot, List<Question> questionList) {
        this.documentSnapshot = documentSnapshot;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public TestAnalysisAdapter.TestAnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.analysis_questions_list_item,parent,false);
        return new TestAnalysisAdapter.TestAnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAnalysisAdapter.TestAnalysisViewHolder holder, int position) {

        Question question = questionList.get(position);

        holder.des.setText(question.getQuesNo()+": "+question.getStatement());
        holder.a.setText("(a) "+question.getOptionA());
        holder.b.setText("(b) "+question.getOptionB());
        holder.c.setText("(c) "+question.getOptionC());
        holder.d.setText("(d) "+question.getOptionD());
        holder.cor.setText("Correct option: "+question.getCorrectOption());
        if(question.getImageURL()!=null){
            Glide.with(holder.img.getContext()).load(question.getImageURL()).into(holder.img);
            // to zoom image
        }

        try {
            String response  = documentSnapshot.getString(question.getQuesCode());
            if(response!=null){
                holder.res.setText("Response: "+response);
                if(response.equals(question.getCorrectOption())){
                    holder.res.setTextColor(Color.parseColor("#007505"));
                }else{
                    holder.res.setTextColor(Color.parseColor("FF8E0A00"));
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class TestAnalysisViewHolder extends RecyclerView.ViewHolder {
        TextView des,a,b,c,d,cor,res;
        ImageView img;
        public TestAnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            des = itemView.findViewById(R.id.aql_ques_des);
            a = itemView.findViewById(R.id.aql_opt_a);
            b = itemView.findViewById(R.id.aql_opt_b);
            c = itemView.findViewById(R.id.aql_opt_c);
            d = itemView.findViewById(R.id.aql_opt_d);
            img = itemView.findViewById(R.id.aql_ques_img);
            cor = itemView.findViewById(R.id.aql_cor_opt);
            res = itemView.findViewById(R.id.aql_res_opt);

            PhotoViewAttacher attacher;
            attacher = new PhotoViewAttacher(img);
            attacher.update();
        }
    }
}
