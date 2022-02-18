package com.example.infinity.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinity.R;
import com.example.infinity.activities.StudentDashboard;
import com.example.infinity.activities.TeachersDashboard;
import com.example.infinity.utilities.Statics;
import com.example.infinity.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class LogIn extends Fragment {

    private TextView forgotPwd,signUp;
    private EditText loginId,loginPwd;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private CheckBox remMe;
    private FirebaseAuth auth;
    private DocumentReference dRef;
    private FirebaseFirestore database;

    public LogIn(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_log_in, container, false);
        setUpViews(view);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(getContext());

        sharedPreferences= requireActivity().getSharedPreferences("Credentials",MODE_PRIVATE);
        loginId.setText(sharedPreferences.getString("username",""));
        loginPwd.setText(sharedPreferences.getString("password",""));
        if(Objects.equals(sharedPreferences.getString("remMe", ""), "1"))
            remMe.setChecked(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginUsername=loginId.getText().toString().trim();
                String loginPassword=loginPwd.getText().toString().trim();

                if(loginUsername.isEmpty()){
                    Toast.makeText(getContext(),"Enter username!",Toast.LENGTH_SHORT).show();
                }
                else if(loginPassword.isEmpty()){
                    Toast.makeText(getContext(),"Enter password!",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(remMe.isChecked()){
                        editor.putString("username", loginUsername);
                        editor.putString("password", loginPassword);
                        editor.putString("remMe","1");
                    }
                    editor.apply();
                    startLogin(loginUsername,loginPassword);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp signUp = new SignUp();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_activity,signUp);
                ft.commit();
            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = loginId.getText().toString().trim();
                if(!mail.isEmpty()){
                    new AlertDialog.Builder(getContext())
                            .setMessage("Password reset link will be sent to your email !")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    progressDialog.setMessage("Sending email...");
                                    progressDialog.show();
                                    auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(),"Password reset mail sent",Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getContext(),"Error!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }else{
                    Toast.makeText(getContext(),"Enter email id!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void startLogin(String loginUsername, String loginPassword) {

        auth.signInWithEmailAndPassword(loginUsername,loginPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                dRef = database.collection(Statics.USERS_COLLECTION).document(auth.getUid());
                dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot==null){
                            Toast.makeText(getContext(),"No such user exists!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Statics.CUR_USER = documentSnapshot.toObject(User.class);
                            getActivity().finish();
                            if(Statics.CUR_USER.getUserType().equals(Statics.STUDENT)){
//                                Toast.makeText(getContext(),"Student",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), StudentDashboard.class));
                            }else {
//                                Toast.makeText(getContext(),"Teacher",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), TeachersDashboard.class));
                            }
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void setUpViews(View view) {
        forgotPwd = view.findViewById(R.id.forgot_pwd_btn);
        signUp = view.findViewById(R.id.sign_up_btn);
        loginId=view.findViewById(R.id.login_id);
        loginPwd=view.findViewById(R.id.login_pwd);
        loginBtn=view.findViewById(R.id.login_btn);
        remMe=view.findViewById(R.id.rem_me);
    }
}