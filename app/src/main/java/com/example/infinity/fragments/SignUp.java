package com.example.infinity.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.models.Statics;
import com.example.infinity.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends Fragment {

    private Spinner stdSpinner,rollSpinner;
    private EditText regName,regEmail,regSchool,regSubject,regPwd;
//    private RadioGroup userTypeRadGrp;
    private RadioButton stuRadBtn,teaRadBtn;
    private Button registerBtn;
    private TextView signInBtn;
    private LinearLayout stuDes;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private String name,email,school,userType,des,pwd;
    private int rollNo;
    private ProgressDialog progressDialog;
    public SignUp() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setUpViews(view);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(getContext());

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();
                    startRegistration();
                }
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn logIn = new LogIn();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_activity,logIn);
                ft.commit();
            }
        });
        stuRadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stuDes.setVisibility(View.VISIBLE);
                regSubject.setVisibility(View.GONE);
            }
        });
        teaRadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stuDes.setVisibility(View.GONE);
                regSubject.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void startRegistration() {
        auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(name,email,school,auth.getUid(),des,userType,rollNo);
                    database.collection(Statics.USERS_COLLECTION).document(auth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Registration successful",Toast.LENGTH_LONG).show();
                            auth.signOut();
                            progressDialog.dismiss();
                            LogIn logIn = new LogIn();
                            assert getFragmentManager() != null;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.main_activity,logIn);
                            ft.commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Error!",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(),"Registration failed!",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private boolean isValid() {
        name = regName.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(getContext(),"Enter name!",Toast.LENGTH_SHORT).show();
            return  false;
        }
        email = regEmail.getText().toString().trim();
        if(email.isEmpty()){
            Toast.makeText(getContext(),"Enter email!",Toast.LENGTH_SHORT).show();
            return  false;
        }
        school = regSchool.getText().toString().trim();
        if(school.isEmpty()){
            Toast.makeText(getContext(),"Enter school/institute!",Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(!teaRadBtn.isChecked()&&!stuRadBtn.isChecked()){
            Toast.makeText(getContext(),"Select user type!",Toast.LENGTH_SHORT).show();
            return  false;
        }
        else{
            if(teaRadBtn.isChecked())
                userType = Statics.TEACHER;
            else
                userType = Statics.STUDENT;
        }

        if(userType.equals(Statics.STUDENT)){
            des = stdSpinner.getSelectedItem().toString();
            if(des.equals("Class/Std")){
                Toast.makeText(getContext(),"Enter class!",Toast.LENGTH_SHORT).show();
                return  false;
            }
            String roll = rollSpinner.getSelectedItem().toString();
            if(roll.equals("Roll no")){
                Toast.makeText(getContext(),"Enter roll no!",Toast.LENGTH_SHORT).show();
                return  false;
            }
            rollNo = Integer.parseInt(roll);
        }
        else{
            des = regSubject.getText().toString().trim();
            rollNo = 0;
            if(des.isEmpty()){
                Toast.makeText(getContext(),"Enter subject!",Toast.LENGTH_SHORT).show();
                return  false;
            }
        }

        pwd = regPwd.getText().toString().trim();
        if(pwd.isEmpty()){
            Toast.makeText(getContext(),"Enter password!",Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
    }

    private void setUpViews(View view) {
        rollSpinner = view.findViewById(R.id.roll_no);
        stdSpinner = view.findViewById(R.id.select_class);
        regName = view.findViewById(R.id.reg_name);
        regEmail = view.findViewById(R.id.reg_email);
        regSchool = view.findViewById(R.id.reg_school);
        regSubject = view.findViewById(R.id.reg_subject);
        regPwd = view.findViewById(R.id.reg_password);
        registerBtn = view.findViewById(R.id.btn_register);
        signInBtn = view.findViewById(R.id.sign_in_btn);
//        userTypeRadGrp = view.findViewById(R.id.user_type_rad_grp);
        teaRadBtn = view.findViewById(R.id.reg_tea_rad_btn);
        stuRadBtn = view.findViewById(R.id.reg_stu_rad_btn);
        stuDes = view.findViewById(R.id.stu_des);
        regSubject.setVisibility(View.GONE);
        stuDes.setVisibility(View.GONE);
        setSpinner();
    }

    private void setSpinner(){
        List<String> list = new ArrayList<>();
        list.add(0,"Roll no");
        for(int i=1;i<=200;i++){
            list.add(""+i);
        }
        ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rollSpinner.setAdapter(adapter);

        List<String> stdList = new ArrayList<>();
        stdList.add(0,"Class/Std");
        for(int i=1;i<=12;i++){
            stdList.add(""+i);
        }

        ArrayAdapter<String> stdAdapter;
        stdAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,stdList);
        stdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stdSpinner.setAdapter(stdAdapter);
    }
}