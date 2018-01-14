package com.example.zoe.midyear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login;
    private String email, password;
    private EditText getEmail, getPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginForReal);
        login.setOnClickListener(this);
        getEmail = findViewById(R.id.getCurrentEmail);
        getPassword = findViewById(R.id.getCurrentPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(Login.this, Profile.class));
        }

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v){
        email = getEmail.getText().toString();
        password = getPassword.getText().toString();

        if(email.length()>0&&password.length()>0){

            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        finish();
                        startActivity(new Intent(Login.this, Profile.class));
                    }
                }
            });

            progressDialog.dismiss();
        }
    }
}
