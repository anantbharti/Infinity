package com.example.infinity.adapter;

import android.content.Context;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import uk.co.senab.photoview.PhotoViewAttacher;

public class AnalysisAdapter extends FirestoreRecyclerAdapter<Question,AnalysisAdapter.AnalysisViewHolder> {

    DocumentSnapshot documentSnapshot;

    public AnalysisAdapter(@NonNull FirestoreRecyclerOptions<Question> options,DocumentSnapshot documentSnapshot) {
        super(options);
        this.documentSnapshot = documentSnapshot;
    }

    @Override
    protected void onBindViewHolder(@NonNull AnalysisAdapter.AnalysisViewHolder holder, int position, @NonNull Question question) {
        holder.des.setText(question.getQuesNo()+": "+question.getStatement());
        holder.a.setText("(a) "+question.getOptionA());
        holder.b.setText("(b) "+question.getOptionB());
        holder.c.setText("(c) "+question.getOptionA());
        holder.d.setText("(d) "+question.getOptionA());
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

    @NonNull
    @Override
    public AnalysisAdapter.AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.analysis_questions_list_item,parent,false);
        return new AnalysisAdapter.AnalysisViewHolder(view);
    }

    public class AnalysisViewHolder extends RecyclerView.ViewHolder{
        TextView des,a,b,c,d,cor,res;
        ImageView img;
        public AnalysisViewHolder(@NonNull View itemView) {
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
