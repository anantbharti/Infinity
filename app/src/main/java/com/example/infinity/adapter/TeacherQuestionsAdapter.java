package com.example.infinity.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinity.R;
import com.example.infinity.activities.EditQuestion;
import com.example.infinity.activities.TeacherTest;
import com.example.infinity.models.Question;
import com.example.infinity.utilities.Statics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherQuestionsAdapter extends FirestoreRecyclerAdapter<Question, TeacherQuestionsAdapter.TeacherQuestionViewHolder> {

    FirebaseFirestore database;
    Context context;
    String editable;
    public TeacherQuestionsAdapter(@NonNull FirestoreRecyclerOptions<Question> options, Context context,String editable) {
        super(options);
        this.context = context;
        this.database = FirebaseFirestore.getInstance();
        this.editable = editable;
    }

    @Override
    protected void onBindViewHolder(@NonNull TeacherQuestionsAdapter.TeacherQuestionViewHolder holder, int position, @NonNull Question question) {

        holder.quesNo.setText(question.getQuesNo()+": ");
        holder.quesDes.setText(question.getStatement());

        if(!editable.equals("0"))
            holder.del.setVisibility(View.GONE);
        holder.quesDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditQuestion.class);
                intent.putExtra("Question",question);
                intent.putExtra("editable",editable);
                intent.putExtra(Statics.TEST_CODE,question.getTestCode());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                database.collection(Statics.QUESTIONS_COLLECTION).document(question.getTestCode()+question.getQuesCode())
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            updateQuestionCounter(question.getTestCode());
                                        }else{
                                            Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @NonNull
    @Override
    public TeacherQuestionsAdapter.TeacherQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_questions_list_item,parent,false);
        return new TeacherQuestionViewHolder(view);
    }

    public class TeacherQuestionViewHolder extends RecyclerView.ViewHolder{
        TextView quesNo,quesDes;
        Button del;
        public TeacherQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            quesDes=itemView.findViewById(R.id.tq_li_ques_des);
            quesNo=itemView.findViewById(R.id.tq_li_ques_no);
            del=itemView.findViewById(R.id.tq_li_delete);
        }
    }

    private void updateQuestionCounter(String testCode) {
        database.collection(Statics.COUNTERS).document(testCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    long quesCnt = documentSnapshot.getLong(Statics.QUESTION_COUNT);
                    database.collection(Statics.COUNTERS).document(testCode)
                            .update(Statics.QUESTION_COUNT,quesCnt-1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context,"Question deleted successfully",Toast.LENGTH_SHORT).show();
                                ((Activity)context).finish();
                                Intent intent = new Intent(context, TeacherTest.class);
                                intent.putExtra(Statics.TEST_CODE,testCode);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                context.getApplicationContext().startActivity(intent);
                            }else{
                                Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
