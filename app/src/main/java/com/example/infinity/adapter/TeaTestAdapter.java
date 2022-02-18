package com.example.infinity.adapter;

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
import com.example.infinity.activities.TeacherTest;
import com.example.infinity.utilities.Statics;
import com.example.infinity.models.Test;

import java.util.List;

public class TeaTestAdapter extends RecyclerView.Adapter<TeaTestAdapter.TeaTestViewHolder> {

    Context context;
    List<Test> testList;

    public TeaTestAdapter(Context context, List<Test> testList) {
        this.context = context;
        this.testList = testList;
    }

    @NonNull
    @Override
    public TeaTestAdapter.TeaTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teachers_test_list_item,parent,false);
        return new TeaTestAdapter.TeaTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeaTestAdapter.TeaTestViewHolder holder, int position) {

        Test test = testList.get(position);

        holder.name.setText(test.getName());
        holder.subject.setText(test.getSubject());
        holder.date.setText("Added on: "+test.getDate());
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Statics.DELETED_TESTS.contains(test.getTestCode())){
                    Toast.makeText(context,"This test was deleted!",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(context, TeacherTest.class);
                    intent.putExtra(Statics.TEST_CODE,test.getTestCode());
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        });
        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("You can't edit test after anyone attempts the test. Do you want to share?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT, "Attend my test on Infinity app.\n*Test code:* "+test.getTestCode()+"\n_All the best_");
                                intent.setType("text/plain");
                                context.startActivity(Intent.createChooser(intent, "Share"));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class TeaTestViewHolder extends RecyclerView.ViewHolder {
        TextView name,subject,date;
        Button open,invite;
        public TeaTestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tt_li_name);
            subject = itemView.findViewById(R.id.tt_li_subject);
            date = itemView.findViewById(R.id.tt_li_date);
            open = itemView.findViewById(R.id.tt_li_open_btn);
            invite = itemView.findViewById(R.id.tt_li_invite_btn);
        }
    }
}
