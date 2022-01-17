package com.example.infinity.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.infinity.R;
import com.example.infinity.models.Question;
import com.example.infinity.models.Statics;
import com.example.infinity.models.Test;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import uk.co.senab.photoview.PhotoViewAttacher;

public class EditQuestion extends AppCompatActivity {

    String testCode;
    EditText quesNo,quesStatement,optionA,optionB,optionC,optionD;
    ImageView editImgBtn,quesImg;
    RadioGroup optionsRadGrp;
    RadioButton selectedRadioBtn,radBtnA,radBtnB,radBtnC,radBtnD;
    Button saveBtn;
    Uri imageUri;
    int qNo;
    String qStatement,optA,optB,optC,optD,corOpt,imgURL,qCode;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore database;
    ProgressDialog progressDialog;
    String editable;
//    Test test;
    Question question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        setUpViews();

//        test = (Test) getIntent().getSerializableExtra("Test");
        question = (Question) getIntent().getSerializableExtra("Question");
        testCode = getIntent().getExtras().getString(Statics.TEST_CODE);
        editable = getIntent().getExtras().getString("editable");
        if(!editable.equals("0")) {
            saveBtn.setVisibility(View.GONE);
            editImgBtn.setVisibility(View.GONE);
        }

        if(question!=null){
            quesStatement.setText(question.getStatement());
            quesNo.setText(""+question.getQuesNo());
            optionA.setText(question.getOptionA());
            optionB.setText(question.getOptionB());
            optionC.setText(question.getOptionC());
            optionD.setText(question.getOptionD());

            if(question.getImageURL()!=null){
                Glide.with(quesImg.getContext()).load(question.getImageURL()).into(quesImg);
                // to zoom image
            }
            String CO = question.getCorrectOption();
            if(CO.equals("a"))
                radBtnA.setChecked(true);
            else if(CO.equals("b"))
                radBtnB.setChecked(true);
            else if(CO.equals("c"))
                radBtnC.setChecked(true);
            else
                radBtnD.setChecked(true);
        }

        // to zoom image
        PhotoViewAttacher attacher;
        attacher = new PhotoViewAttacher(quesImg);
        attacher.update();

        progressDialog=new ProgressDialog(EditQuestion.this);
        progressDialog.setCancelable(false);


        editImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(EditQuestion.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(300)			//Final image size will be less than 300 KB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validFields()){
                    if(question==null)
                    qCode = ""+((int) (Math.random() * (99999999 - 10000000)) + 10000000);
                    else
                        qCode = question.getQuesCode();
                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
                    uploadImage();
//                    Toast.makeText(EditQuestion.this,"Valid",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage() {
//        Toast.makeText(EditQuestion.this,"uploading",Toast.LENGTH_SHORT).show();
        if(imageUri==null){
            uploadQuestion();
            return;
        }
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference().child(testCode).child(qCode);
        UploadTask uploadTask=storageReference.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditQuestion.this,"Error in uploading image!",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgURL = uri.toString();
                        uploadQuestion();
//                        Toast.makeText(EditQuestion.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditQuestion.this,"Error in uploading image!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploadQuestion() {
        database=FirebaseFirestore.getInstance();
        Question newQues=new Question(qNo,qStatement,optA,optB,optC,optD,corOpt,imgURL,qCode,testCode);

        database.collection(Statics.QUESTIONS_COLLECTION).document(testCode+qCode)
                .set(newQues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateQuestionCounter();
//                    Toast.makeText(EditQuestion.this,"Question saved successfully",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(EditQuestion.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateQuestionCounter() {
        database.collection(Statics.COUNTERS).document(testCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null) {
                    long quesCnt = documentSnapshot.getLong(Statics.QUESTION_COUNT);
                    database.collection(Statics.COUNTERS).document(testCode)
                            .update(Statics.QUESTION_COUNT,quesCnt+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(EditQuestion.this,"Question saved successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(EditQuestion.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(EditQuestion.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditQuestion.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validFields() {

        if(quesNo.getText().toString().isEmpty()){
            Toast.makeText(EditQuestion.this,"Enter question no!",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            qNo = Integer.parseInt(quesNo.getText().toString());
        }

        qStatement=quesStatement.getText().toString().trim();
        if(qStatement.isEmpty()){
            Toast.makeText(EditQuestion.this,"Enter question statement!",Toast.LENGTH_SHORT).show();
            return false;
        }

        optA=optionA.getText().toString().trim();
        optB=optionB.getText().toString().trim();
        optC=optionC.getText().toString().trim();
        optD=optionD.getText().toString().trim();

        if(optA.isEmpty()||optB.isEmpty()||optC.isEmpty()||optD.isEmpty()){
            Toast.makeText(EditQuestion.this,"Enter all options!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!radBtnA.isChecked() && !radBtnB.isChecked() && !radBtnC.isChecked() && !radBtnD.isChecked()){
            Toast.makeText(EditQuestion.this,"Select correct option!",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            int addAnsRadId = optionsRadGrp.getCheckedRadioButtonId();
            selectedRadioBtn = findViewById(addAnsRadId);
            corOpt = selectedRadioBtn.getText().toString().trim();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
        imageUri=data.getData();
        quesImg.setImageURI(imageUri);
//        }
    }

    private void setUpViews() {
        quesNo=findViewById(R.id.edit_ques_no);
        quesStatement=findViewById(R.id.edit_ques_statement);
        optionA=findViewById(R.id.edit_option_a);
        optionB=findViewById(R.id.edit_option_b);
        optionC=findViewById(R.id.edit_option_c);
        optionD=findViewById(R.id.edit_option_d);
        optionsRadGrp=findViewById(R.id.correct_option_radio_group);
        quesImg = findViewById(R.id.edit_ques_img);
        editImgBtn= findViewById(R.id.edit_img_btn);
        saveBtn= findViewById(R.id.save_ques_btn);
        radBtnA=findViewById(R.id.rad_btn_a);
        radBtnB=findViewById(R.id.rad_btn_b);
        radBtnC=findViewById(R.id.rad_btn_c);
        radBtnD=findViewById(R.id.rad_btn_d);
    }
}